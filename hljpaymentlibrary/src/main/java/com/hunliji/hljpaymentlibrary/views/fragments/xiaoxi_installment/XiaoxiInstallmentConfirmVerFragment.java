package com.hunliji.hljpaymentlibrary.views.fragments.xiaoxi_installment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonviewlibrary.views.fragments.BaseGetSmsCodeFragment;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljpaymentlibrary.R;
import com.hunliji.hljpaymentlibrary.api.xiaoxi_installment.XiaoxiInstallmentApi;

/**
 * Created by luohanlin on 2017/9/12.
 */

public class XiaoxiInstallmentConfirmVerFragment extends BaseGetSmsCodeFragment {

    public static final String ARG_PHONE_LAST_NUM = "phone_last_num";
    public static final String ARG_SMS_SERIAL_NO = "sms_serial_no";
    public static final String ARG_ORDER_ID = "order_id";

    private String phoneLastNum;
    private String smsSerialNo;
    private String assetOrderId;
    private HljHttpSubscriber confirmSub;
    private HljHttpSubscriber resendSub;

    private OnConfirmSuccessListener onConfirmSuccessListener;

    public static XiaoxiInstallmentConfirmVerFragment newInstance(
            String phoneLast, String smsSerialNo, String orderId) {
        Bundle args = new Bundle();
        XiaoxiInstallmentConfirmVerFragment fragment = new XiaoxiInstallmentConfirmVerFragment();
        args.putString(ARG_PHONE_LAST_NUM, phoneLast);
        args.putString(ARG_SMS_SERIAL_NO, smsSerialNo);
        args.putString(ARG_ORDER_ID, orderId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        if (getArguments() != null) {
            phoneLastNum = getArguments().getString(ARG_PHONE_LAST_NUM);
            assetOrderId = getArguments().getString(ARG_ORDER_ID);
            smsSerialNo = getArguments().getString(ARG_SMS_SERIAL_NO);
        }
        initViews();

        return rootView;
    }

    public void setOnConfirmSuccessListener(OnConfirmSuccessListener onConfirmSuccessListener) {
        this.onConfirmSuccessListener = onConfirmSuccessListener;
    }

    private void initViews() {
        // 显示弹出框的时候已经由支付接口发出了验证码的请求，直接等待验证码和输入
        if (timer != null) {
            timer.start();
        }
    }

    @Override
    protected String getAlertTitle() {
        return "安全校验";
    }

    @Override
    protected String getAlertMsg() {
        String msg = phoneLastNum;
        if (!TextUtils.isEmpty(msg) && msg.length() > 4) {
            msg = msg.substring(msg.length() - 4, msg.length());
        }
        return getString(R.string.msg_bank_card_verification___pay, msg);
    }

    @Override
    protected void onGetSmsCode() {
        // 更新订单短信验证码
        CommonUtil.unSubscribeSubs(resendSub);
        resendSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setProgressDialog(DialogUtil.createProgressDialog(getContext()))
                .setOnNextListener(new SubscriberOnNextListener() {
                    @Override
                    public void onNext(Object o) {
                        if (timer != null) {
                            timer.start();
                        }
                    }
                })
                .build();
        XiaoxiInstallmentApi.resendSms(getContext(), assetOrderId, smsSerialNo)
                .subscribe(resendSub);
    }

    @Override
    protected void onConfirm() {
        if (TextUtils.isEmpty(etSmsCode.getText())) {
            ToastUtil.showToast(getContext(), null, R.string.hint_enter_sms_code___pay);
            return;
        }
        String verifyCode = etSmsCode.getText()
                .toString();
        CommonUtil.unSubscribeSubs(confirmSub);
        confirmSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener() {
                    @Override
                    public void onNext(Object o) {
                        // 支付成功
                        dismiss();
                        if (onConfirmSuccessListener != null) {
                            onConfirmSuccessListener.onConfirmSuccess();
                        }
                    }
                })
                .setProgressDialog(DialogUtil.createProgressDialog(getContext()))
                .setDataNullable(true)
                .build();
        XiaoxiInstallmentApi.confirmInstallment(getContext(), smsSerialNo, verifyCode, assetOrderId)
                .subscribe(confirmSub);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CommonUtil.unSubscribeSubs(confirmSub, resendSub);
    }

    public interface OnConfirmSuccessListener {
        void onConfirmSuccess();
    }
}
