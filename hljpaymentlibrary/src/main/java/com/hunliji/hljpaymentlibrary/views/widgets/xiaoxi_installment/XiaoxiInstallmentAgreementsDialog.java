package com.hunliji.hljpaymentlibrary.views.widgets.xiaoxi_installment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljpaymentlibrary.R;
import com.hunliji.hljpaymentlibrary.R2;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.XiaoxiInstallmentOrder;
import com.hunliji.hljpaymentlibrary.views.activities.xiaoxi_installment.InvestorListActivity;
import com.hunliji.hljpaymentlibrary.views.activities.xiaoxi_installment
        .XiaoxiInstallmentAgreementActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 小犀分期相关协议
 * Created by chen_bin on 2017/9/12 0012.
 */
public class XiaoxiInstallmentAgreementsDialog extends Dialog {

    private String assetOrderId;

    public XiaoxiInstallmentAgreementsDialog(@NonNull Context context) {
        super(context, R.style.BubbleDialogTheme);
        setContentView(R.layout.dialog_xiaoxi_installment_agreements);
        ButterKnife.bind(this);
        initViews();
    }

    private void initViews() {
        Window win = getWindow();
        if (win != null) {
            WindowManager.LayoutParams params = win.getAttributes();
            params.width = CommonUtil.getDeviceSize(getContext()).x;
            win.setAttributes(params);
            win.setGravity(Gravity.BOTTOM);
            win.setWindowAnimations(R.style.dialog_anim_rise_style);
        }
    }

    @OnClick({R2.id.tv_platform_service_agreement, R2.id.tv_authentication_withhold_agreement,
            R2.id.tv_loan_agreement, R2.id.tv_entrusted_payment_agreement, R2.id
            .tv_plan_management})
    void onAgreement(View v) {
        dismiss();
        Intent intent;
        if (!CommonUtil.isEmpty(assetOrderId) && v.getId() == R.id.tv_loan_agreement) {
            intent = new Intent(v.getContext(), InvestorListActivity.class);
            intent.putExtra(InvestorListActivity.ARG_ASSET_ORDER_ID, assetOrderId);
        } else {
            int type = 0;
            if (v.getId() == R.id.tv_platform_service_agreement) {
                type = XiaoxiInstallmentAgreementActivity.TYPE_PLATFORM_SERVICE;
            } else if (v.getId() == R.id.tv_loan_agreement) {
                type = XiaoxiInstallmentAgreementActivity.TYPE_LOAN;
            } else if (v.getId() == R.id.tv_authentication_withhold_agreement) {
                type = XiaoxiInstallmentAgreementActivity.TYPE_AUTHENTICATION_WITHHOLD;
            } else if (v.getId() == R.id.tv_entrusted_payment_agreement) {
                type = XiaoxiInstallmentAgreementActivity.TYPE_ENTRUSTED_PAYMENT;
            } else if (v.getId() == R.id.tv_plan_management) {
                type = XiaoxiInstallmentAgreementActivity.TYPE_PLAN_MANAGEMENT;
            }
            intent = new Intent(v.getContext(), XiaoxiInstallmentAgreementActivity.class);
            intent.putExtra(XiaoxiInstallmentAgreementActivity.ARG_TYPE, type);
            intent.putExtra(XiaoxiInstallmentAgreementActivity.ARG_ASSET_ORDER_ID, assetOrderId);
        }
        v.getContext()
                .startActivity(intent);
    }

    @OnClick(R2.id.btn_close)
    void onClose() {
        dismiss();
    }

    public Dialog setAssetOrderId(String assetOrderId) {
        this.assetOrderId = assetOrderId;
        return this;
    }
}
