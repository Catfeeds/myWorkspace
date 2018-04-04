package com.hunliji.hljpaymentlibrary.views.activities.xiaoxi_installment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.suncloud.hljweblibrary.HljWeb;
import com.google.gson.JsonElement;
import com.hunliji.hljcommonlibrary.models.Bank;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearLayout;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljpaymentlibrary.R;
import com.hunliji.hljpaymentlibrary.R2;
import com.hunliji.hljpaymentlibrary.adapters.xiaoxi_installment.XiaoxiInstallmentBankListAdapter;
import com.hunliji.hljpaymentlibrary.api.xiaoxi_installment.XiaoxiInstallmentApi;
import com.hunliji.hljpaymentlibrary.models.PayRxEvent;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.AuthItem;
import com.hunliji.hljpaymentlibrary.utils.xiaoxi_installment.XiaoxiInstallmentAuthorization;
import com.hunliji.hljpaymentlibrary.views.fragments.xiaoxi_installment
        .BankCardVerificationFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * 小犀分期-绑定还款银行卡
 * Created by chen_bin on 2017/8/10 0010.
 */
public class AddXiaoxiInstallmentBankCardActivity extends HljBaseActivity {

    @BindView(R2.id.tv_bank_name)
    TextView tvBankName;
    @BindView(R2.id.bank_layout)
    LinearLayout bankLayout;
    @BindView(R2.id.et_bank_card_no)
    EditText etBankCardNo;
    @BindView(R2.id.et_mobile)
    EditText etMobile;
    @BindView(R2.id.tv_agreement)
    TextView tvAgreement;
    @BindView(R2.id.agreement_layout)
    CheckableLinearLayout agreementLayout;
    @BindView(R2.id.content_layout)
    LinearLayout contentLayout;
    @BindView(R2.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;

    private Dialog banksDialog;

    private List<Bank> banks;
    private String realName;
    private String idCardNo;
    private String bankCode;
    private boolean isAuto;

    private Subscription rxBusEventSub;
    private HljHttpSubscriber getRealNameSub;
    private HljHttpSubscriber getBanksSub;

    public final static String ARG_IS_AUTO = "is_auto";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSwipeBackEnable(false);
        setContentView(R.layout.activity_add_xiaoxi_installment_bank_card___pay);
        ButterKnife.bind(this);
        initValues();
        initViews();
        getRealName();
        getBanks(false);
        registerRxBusEvent();
    }

    private void initValues() {
        banks = new ArrayList<>();
        isAuto = getIntent().getBooleanExtra(ARG_IS_AUTO, false);
    }

    private void initViews() {
        emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
        emptyView.setOnEmptyClickListener(new HljEmptyView.OnEmptyClickListener() {
            @Override
            public void onEmptyClickListener() {
                getRealName();
            }
        });
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                getRealName();
            }
        });
        SpannableStringBuilder builder = new SpannableStringBuilder(tvAgreement.getText());
        builder.setSpan(new AgreementClickableSpan(XiaoxiInstallmentAgreementActivity
                        .TYPE_AUTHENTICATION_WITHHOLD),
                7,
                15,
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        builder.setSpan(new AgreementClickableSpan(XiaoxiInstallmentAgreementActivity
                        .TYPE_BEIJING_BANK_DEPOSITORY_SERVICE),
                15,
                tvAgreement.length(),
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        tvAgreement.setText(builder);
        tvAgreement.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void getRealName() {
        if (getRealNameSub == null || getRealNameSub.isUnsubscribed()) {
            getRealNameSub = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<JsonElement>() {
                        @Override
                        public void onNext(JsonElement jsonElement) {
                            realName = CommonUtil.getAsString(jsonElement, "real_name");
                            idCardNo = CommonUtil.getAsString(jsonElement, "id_card");
                        }
                    })
                    .setEmptyView(emptyView)
                    .setContentView(contentLayout)
                    .setProgressBar(progressBar)
                    .build();
            XiaoxiInstallmentApi.getRealNameObb()
                    .subscribe(getRealNameSub);
        }
    }

    private void getBanks(final boolean isShowBanksDialog) {
        if (!CommonUtil.isCollectionEmpty(banks)) {
            return;
        }
        if (getBanksSub == null || getBanksSub.isUnsubscribed()) {
            getBanksSub = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<List<Bank>>() {
                        @Override
                        public void onNext(List<Bank> bankList) {
                            banks.clear();
                            banks.addAll(bankList);
                            if (isShowBanksDialog) {
                                showBanksDialog();
                            }
                        }
                    })
                    .setProgressDialog(isShowBanksDialog ? DialogUtil.createProgressDialog(this)
                            : null)
                    .build();
            XiaoxiInstallmentApi.getBanksObb()
                    .subscribe(getBanksSub);
        }
    }

    //注册RxEventBus监听
    private void registerRxBusEvent() {
        if (rxBusEventSub == null || rxBusEventSub.isUnsubscribed()) {
            rxBusEventSub = RxBus.getDefault()
                    .toObservable(PayRxEvent.class)
                    .subscribe(new RxBusSubscriber<PayRxEvent>() {
                        @Override
                        protected void onEvent(PayRxEvent payRxEvent) {
                            switch (payRxEvent.getType()) {
                                case BIND_BANK_CARD_SUCCESS:
                                    XiaoxiInstallmentAuthorization.getInstance()
                                            .onCurrentItemAuthorized(
                                                    AddXiaoxiInstallmentBankCardActivity.this,
                                                    AuthItem.CODE_BANK_CARD,
                                                    isAuto);
                                    break;
                            }
                        }
                    });
        }
    }

    @OnClick(R2.id.bank_layout)
    void onSelectBank() {
        if (CommonUtil.isCollectionEmpty(banks)) {
            getBanks(true);
        } else {
            showBanksDialog();
        }
    }

    private void showBanksDialog() {
        if (banksDialog != null && banksDialog.isShowing()) {
            return;
        }
        if (banksDialog == null) {
            banksDialog = new Dialog(this, R.style.BubbleDialogTheme);
            banksDialog.setContentView(R.layout.dialog_xiaoxi_installment_banks___pay);
            banksDialog.findViewById(R.id.btn_close)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            banksDialog.dismiss();
                        }
                    });
            ListView listView = banksDialog.findViewById(R.id.bank_list);
            listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            listView.setAdapter(new XiaoxiInstallmentBankListAdapter(banks));
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Bank bank = (Bank) parent.getAdapter()
                            .getItem(position);
                    if (bank != null) {
                        banksDialog.dismiss();
                        bankCode = bank.getCode();
                        tvBankName.setText(bank.getName());
                    }
                }
            });
            Window win = banksDialog.getWindow();
            if (win != null) {
                WindowManager.LayoutParams params = win.getAttributes();
                Point point = CommonUtil.getDeviceSize(this);
                params.width = point.x;
                params.height = point.y / 2;
                win.setGravity(Gravity.BOTTOM);
                win.setWindowAnimations(R.style.dialog_anim_rise_style);
            }
        }
        banksDialog.show();
    }

    @OnClick(R2.id.tv_bank_card_limit)
    void onBankCardLimit() {
        HljWeb.startWebView(this, XiaoxiInstallmentApi.XIAOXI_INSTALLMENT_BANK_CARD_LIMIT_URL);
    }

    @OnClick(R2.id.btn_submit)
    void onSubmit() {
        if (tvBankName.length() == 0) {
            ToastUtil.showToast(this, null, R.string.hint_select_deposit_bank___pay);
            return;
        }
        if (etBankCardNo.length() == 0) {
            ToastUtil.showToast(this, null, R.string.hint_enter_bank_card_no___pay);
            return;
        }
        if (etBankCardNo.length() < 13) {
            ToastUtil.showToast(this, null, R.string.hint_enter_bank_card_no_error___pay);
            return;
        }
        if (etMobile.length() == 0) {
            ToastUtil.showToast(this, null, R.string.hint_enter_bank_reserved_mobile___pay);
            return;
        }
        if (!CommonUtil.isMobileNO(etMobile.getText()
                .toString())) {
            ToastUtil.showToast(this, null, R.string.hint_enter_bank_reserved_mobile_error___pay);
            return;
        }
        if (!agreementLayout.isChecked()) {
            ToastUtil.showToast(this, null, R.string.hint_check_relevant_agreement___pay);
            return;
        }
        String backCardNo = etBankCardNo.getText()
                .toString();
        String mobile = etMobile.getText()
                .toString();
        BankCardVerificationFragment fragment = BankCardVerificationFragment.newInstance(realName,
                idCardNo,
                bankCode,
                backCardNo,
                mobile);
        fragment.show(getSupportFragmentManager(), "BankCardVerificationFragment");
    }

    private class AgreementClickableSpan extends ClickableSpan {
        private int agreementType;

        private AgreementClickableSpan(int agreementType) {
            this.agreementType = agreementType;
        }

        @Override
        public void onClick(View widget) {
            Intent intent = new Intent(AddXiaoxiInstallmentBankCardActivity.this,
                    XiaoxiInstallmentAgreementActivity.class);
            intent.putExtra(XiaoxiInstallmentAgreementActivity.ARG_TYPE, agreementType);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(ContextCompat.getColor(AddXiaoxiInstallmentBankCardActivity.this,
                    R.color.colorLink));
            ds.setUnderlineText(false);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isAuto) {
            RxBus.getDefault()
                    .post(new PayRxEvent(PayRxEvent.RxEventType.AUTHORIZE_CANCEL, null));
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(getRealNameSub, getBanksSub, rxBusEventSub);
    }
}