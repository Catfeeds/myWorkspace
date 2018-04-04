package com.hunliji.hljpaymentlibrary.views.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.suncloud.hljweblibrary.HljWeb;
import com.hunliji.hljcommonlibrary.models.BankCard;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.ClearableEditText;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljpaymentlibrary.R;
import com.hunliji.hljpaymentlibrary.R2;
import com.hunliji.hljpaymentlibrary.api.PaymentApi;
import com.hunliji.hljpaymentlibrary.models.LLPayer;
import com.hunliji.hljpaymentlibrary.models.PayRxEvent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;


public class AddBankCardActivity extends HljBaseActivity {

    @BindView(R2.id.btn_next_step)
    Button btnNextStep;
    @BindView(R2.id.agreement_layout)
    LinearLayout agreementLayout;
    @BindView(R2.id.et_card_id)
    ClearableEditText etCardId;
    private String cardId;
    private boolean isDelete = false;
    private int preLength;
    private LLPayer llPayer;

    private Subscription paySubscriber;
    private Subscription cardSubscriber;

    public final String LLPAY_AGREEMENT_URL = "p/wedding/Public/wap/activity/paycol.html";

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            btnNextStep.setEnabled(s.length() > 0);
            if (s.length() < preLength) {
                isDelete = true;
            } else {
                isDelete = false;
            }
            preLength = s.length();

            if (!isDelete) {
                etCardId.removeTextChangedListener(textWatcher);
                etCardId.setText(addBlank(removeBlank(s.toString())));
                etCardId.setSelection(etCardId.getText()
                        .length());
                etCardId.addTextChangedListener(textWatcher);
            }
        }
    };

    private Dialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bank_card___pay);
        ButterKnife.bind(this);

        llPayer = getIntent().getParcelableExtra("llPayer");

        etCardId.addTextChangedListener(textWatcher);
        paySubscriber = RxBus.getDefault()
                .toObservable(PayRxEvent.class)
                .subscribe(new RxBusSubscriber<PayRxEvent>() {
                    @Override
                    protected void onEvent(PayRxEvent payRxEvent) {
                        if (payRxEvent.getType() == PayRxEvent.RxEventType.PAY_SUCCESS) {
                            onBackPressed();
                        }
                    }
                });
    }

    private String addBlank(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            if (i != 0 && i % 4 == 0) {
                sb.append("  ");
            }
            sb.append(s.charAt(i));
        }

        return sb.toString();
    }

    private String removeBlank(String s) {
        return s.replaceAll(" ", "");
    }

    @SuppressWarnings("unchecked")
    @OnClick(R2.id.btn_next_step)
    void onNextStep() {
        // 查询卡bin信息,成功之后跳转下一步
        if (progressDialog == null) {
            progressDialog = DialogUtil.createProgressDialog(this);
        }
        progressDialog.show();
        cardId = removeBlank(etCardId.getText()
                .toString());
        cardSubscriber = PaymentApi.getCardInfo(cardId)
                .subscribe(HljHttpSubscriber.buildSubscriber(this)
                        .setProgressDialog(progressDialog)
                        .setOnNextListener(new SubscriberOnNextListener<BankCard>() {
                            @Override
                            public void onNext(BankCard bankCard) {
                                if (bankCard != null && !TextUtils.isEmpty(bankCard.getBankName()
                                )) {
                                    bankCard.setCardId(cardId);
                                    Intent intent = new Intent(AddBankCardActivity.this,
                                            LLPayIdentificationActivity.class);
                                    intent.putExtra("llPayer", llPayer);
                                    intent.putExtra("bankCard", bankCard);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.slide_in_right,
                                            R.anim.activity_anim_default);
                                } else {
                                    ToastUtil.showToast(AddBankCardActivity.this,
                                            null,
                                            R.string.msg_card_bin_fail___pay);
                                }
                            }
                        })
                        .build());
    }

    @OnClick(R2.id.support_cards_layout)
    void onSupportCards() {
        Intent intent = new Intent(this, SupportCardListActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    @OnClick(R2.id.agreement_layout)
    void onAgreementLayout() {
        HljWeb.startWebView(this, HljHttp.getHOST() + LLPAY_AGREEMENT_URL);
    }

    @Override
    protected void onFinish() {
        if (!paySubscriber.isUnsubscribed()) {
            paySubscriber.unsubscribe();
        }
        if (cardSubscriber != null && !cardSubscriber.isUnsubscribed()) {
            cardSubscriber.unsubscribe();
        }
        super.onFinish();
    }
}
