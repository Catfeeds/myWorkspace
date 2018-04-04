package com.hunliji.hljpaymentlibrary.views.activities.xiaoxi_installment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearLayout;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljpaymentlibrary.R;
import com.hunliji.hljpaymentlibrary.R2;
import com.hunliji.hljpaymentlibrary.api.xiaoxi_installment.XiaoxiInstallmentApi;
import com.hunliji.hljpaymentlibrary.models.PayRxEvent;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.AuthItem;
import com.hunliji.hljpaymentlibrary.utils.xiaoxi_installment.XiaoxiInstallmentAuthorization;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 小犀分期-实名认证
 * Created by chen_bin on 2017/8/10 0010.
 */
public class RealNameAuthenticationActivity extends HljBaseActivity {

    @BindView(R2.id.et_real_name)
    EditText etRealName;
    @BindView(R2.id.et_id_card_no)
    EditText etIdCardNo;
    @BindView(R2.id.tv_agreement)
    TextView tvAgreement;
    @BindView(R2.id.agreement_layout)
    CheckableLinearLayout agreementLayout;

    private boolean isAuto;

    private HljHttpSubscriber authorizeSub;

    public final static String ARG_IS_AUTO = "is_auto";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSwipeBackEnable(false);
        setContentView(R.layout.activity_real_name_authentication___pay);
        ButterKnife.bind(this);
        initValues();
        initViews();
    }

    private void initValues() {
        isAuto = getIntent().getBooleanExtra(ARG_IS_AUTO, false);
    }

    private void initViews() {
        SpannableStringBuilder builder = new SpannableStringBuilder(tvAgreement.getText());
        builder.setSpan(new AgreementClickableSpan(XiaoxiInstallmentAgreementActivity
                        .TYPE_AUTHENTICATION_SERVICE),
                7,
                14,
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        builder.setSpan(new AgreementClickableSpan(XiaoxiInstallmentAgreementActivity
                        .TYPE_USER_REGISTER),
                14,
                tvAgreement.length(),
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        tvAgreement.setText(builder);
        tvAgreement.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @OnClick(R2.id.btn_submit)
    void onSubmit() {
        if (etRealName.length() == 0) {
            ToastUtil.showToast(this, null, R.string.hint_enter_real_name___pay);
            return;
        }
        if (etIdCardNo.length() == 0) {
            ToastUtil.showToast(this, null, R.string.hint_enter_id_card_no___pay);
            return;
        }
        if (!CommonUtil.validIdStr(etIdCardNo.getText()
                .toString())) {
            ToastUtil.showToast(this, null, R.string.hint_enter_id_card_no_error___pay);
            return;
        }
        if (!agreementLayout.isChecked()) {
            ToastUtil.showToast(this, null, R.string.hint_check_relevant_agreement___pay);
            return;
        }
        final String realName = etRealName.getText()
                .toString();
        final String idCardNo = etIdCardNo.getText()
                .toString();
        CommonUtil.unSubscribeSubs(authorizeSub);
        authorizeSub = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener() {
                    @Override
                    public void onNext(Object o) {
                        XiaoxiInstallmentAuthorization.getInstance()
                                .onCurrentItemAuthorized(RealNameAuthenticationActivity.this,
                                        AuthItem.CODE_REAL_NAME,
                                        isAuto);
                    }
                })
                .setDataNullable(true)
                .setProgressDialog(DialogUtil.createProgressDialog(this))
                .build();
        XiaoxiInstallmentApi.authorizeRealNameObb(this, realName, idCardNo)
                .subscribe(authorizeSub);
    }

    private class AgreementClickableSpan extends ClickableSpan {
        private int agreementType;

        private AgreementClickableSpan(int agreementType) {
            this.agreementType = agreementType;
        }

        @Override
        public void onClick(View widget) {
            Intent intent = new Intent(RealNameAuthenticationActivity.this,
                    XiaoxiInstallmentAgreementActivity.class);
            intent.putExtra(XiaoxiInstallmentAgreementActivity.ARG_TYPE, agreementType);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(ContextCompat.getColor(RealNameAuthenticationActivity.this,
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
        CommonUtil.unSubscribeSubs(authorizeSub);
    }
}