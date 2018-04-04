package com.hunliji.hljpaymentlibrary.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.models.BankCard;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.ClearableEditText;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.hunliji.hljpaymentlibrary.HljPay;
import com.hunliji.hljpaymentlibrary.R;
import com.hunliji.hljpaymentlibrary.R2;
import com.hunliji.hljpaymentlibrary.models.LLPayer;
import com.hunliji.hljpaymentlibrary.models.PayRxEvent;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

public class LLPayIdentificationActivity extends HljBaseActivity {

    @BindView(R2.id.tv_bank_name)
    TextView tvBankName;
    @BindView(R2.id.tv_card_id)
    TextView tvCardId;
    @BindView(R2.id.btn_next_step)
    Button btnNextStep;
    @BindView(R2.id.et_card_holder)
    ClearableEditText etCardHolder;
    @BindView(R2.id.et_id_card_number)
    ClearableEditText etIdCardNumber;
    @BindView(R2.id.img_bank_logo)
    ImageView imgBankLogo;

    @BindView(R2.id.tv_hint)
    TextView tvHint;
    private LLPayer llPayer;
    private BankCard bankCard;

    private final int SET_PAY_PASSWORD = 1;

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            btnNextStep.setEnabled(tvBankName.length() > 0 && tvCardId.length() > 0 &&
                    etCardHolder.length() > 0 && etIdCardNumber.length() > 0);
        }
    };
    private String pswmd5;
    private Subscription paySubscriber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_llpay_identification___pay);
        ButterKnife.bind(this);
        tvCardId.addTextChangedListener(textWatcher);
        tvBankName.addTextChangedListener(textWatcher);
        etCardHolder.addTextChangedListener(textWatcher);
        etIdCardNumber.addTextChangedListener(textWatcher);

        bankCard = getIntent().getParcelableExtra("bankCard");
        llPayer = getIntent().getParcelableExtra("llPayer");
        if (llPayer != null) {
            tvHint.setText(CommonUtil.fromHtml(this,
                    getString(R.string.html_fmt_llpay_money___pay),
                    llPayer.getPriceStr()));
        }

        tvBankName.setText(bankCard.getBankName());
        tvCardId.setText(bankCard.getCardId());
        String logoPath = ImageUtil.getImagePath(bankCard.getLogoPath(),
                imgBankLogo.getLayoutParams().width);
        if (!TextUtils.isEmpty(logoPath)) {
            Glide.with(this)
                    .load(logoPath)
                    .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image)
                            .dontAnimate())
                    .into(imgBankLogo);
        } else {
            Glide.with(this)
                    .clear(imgBankLogo);
            imgBankLogo.setImageBitmap(null);
        }
        paySubscriber = RxBus.getDefault()
                .toObservable(PayRxEvent.class)
                .subscribe(new RxBusSubscriber<PayRxEvent>() {
                    @Override
                    protected void onEvent(PayRxEvent payRxEvent) {
                        if (payRxEvent.getType() == PayRxEvent.RxEventType.PAY_SUCCESS) {
                            onBackPressed();
                        } else if (payRxEvent.getType() == PayRxEvent.RxEventType.PAY_FAIL) {
                            // 失败之后应该重新设置密码
                            pswmd5 = "";
                            btnNextStep.setEnabled(true);
                        }
                    }
                });
    }

    @OnClick(R2.id.btn_next_step)
    void onNextStep() {
        if (etCardHolder.length() == 0) {
            Toast.makeText(this, R.string.msg_card_holder_empty___pay, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (etIdCardNumber.length() == 0) {
            Toast.makeText(this, R.string.msg_id_card_empty___pay, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (!CommonUtil.validIdStr(etIdCardNumber.getText()
                .toString())) {
            Toast.makeText(this, R.string.msg_id_card_err___pay, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (llPayer.isSimple()) {
            //如果连连支付是简单模式忽略密码直接支付
            pay();
        } else if (!llPayer.isFirst()) {
            // 如果没有支付密码则先设置支付密码,否则直接支付(添加新卡的支付)
            pay();
        } else if (!TextUtils.isEmpty(pswmd5)) {
            // 已经设置过密码了
            pay();
        } else {
            // 先去设置密码
            Intent intent = new Intent(this, SetPasswordActivity.class);
            intent.putExtra("card_holder",
                    etCardHolder.getText()
                            .toString());
            intent.putExtra("id_number",
                    etIdCardNumber.getText()
                            .toString());
            startActivityForResult(intent, SET_PAY_PASSWORD);
            overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.activity_anim_default);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case SET_PAY_PASSWORD:
                    pswmd5 = data.getStringExtra("pswmd5");
                    if (!TextUtils.isEmpty(pswmd5)) {
                        // 成功设置了密码之后开始支付
                        pay();
                    }
                    break;
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void pay() {
        btnNextStep.setEnabled(false);
        JSONObject payParams = null;
        try {
            payParams = new JSONObject(llPayer.getPayParams());
            payParams.put("bank_name", bankCard.getBankName());
            payParams.put("user_name",
                    etCardHolder.getText()
                            .toString());
            payParams.put("user_idcard",
                    etIdCardNumber.getText()
                            .toString());
            payParams.put("user_bankcard", bankCard.getCardId());
            payParams.put("kind", 2);
            payParams.put("bank_code", bankCard.getBankCode());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new HljPay.Builder(this).params(payParams)
                .path(llPayer.getPayPath())
                .price(llPayer.getPrice())
                .build()
                .onPayLLPay();
    }


    @Override
    protected void onFinish() {
        if (!paySubscriber.isUnsubscribed()) {
            paySubscriber.unsubscribe();
        }
        super.onFinish();
    }
}
