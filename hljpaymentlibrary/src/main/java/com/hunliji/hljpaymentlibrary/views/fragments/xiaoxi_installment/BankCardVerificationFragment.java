package com.hunliji.hljpaymentlibrary.views.fragments.xiaoxi_installment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.JsonElement;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonviewlibrary.views.fragments.BaseGetSmsCodeFragment;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljpaymentlibrary.R;
import com.hunliji.hljpaymentlibrary.api.xiaoxi_installment.XiaoxiInstallmentApi;
import com.hunliji.hljpaymentlibrary.models.PayRxEvent;

/**
 * 签约绑卡验证
 * Created by chen_bin on 2017/8/21 0021.
 */
public class BankCardVerificationFragment extends BaseGetSmsCodeFragment {

    private String realName;
    private String idCardNo;
    private String bankCode;
    private String bankCardNo;
    private String mobile;
    private String smsSerialNo;
    private String orderNo;
    private int interval;

    private HljHttpSubscriber bindBankCardSub;
    private HljHttpSubscriber verifySub;

    public static final String ARG_REAL_NAME = "real_name";
    public static final String ARG_ID_CARD_NO = "id_card_no";
    public static final String ARG_BANK_CODE = "bank_code";
    public static final String ARG_BANK_CARD_NO = "bank_card_no";
    public static final String ARG_MOBILE = "mobile";

    public static BankCardVerificationFragment newInstance(
            String realName, String idCardNo, String bankCode, String bankCardNo, String mobile) {
        BankCardVerificationFragment fragment = new BankCardVerificationFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_REAL_NAME, realName);
        bundle.putString(ARG_ID_CARD_NO, idCardNo);
        bundle.putString(ARG_BANK_CODE, bankCode);
        bundle.putString(ARG_BANK_CARD_NO, bankCardNo);
        bundle.putString(ARG_MOBILE, mobile);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            realName = getArguments().getString(ARG_REAL_NAME);
            idCardNo = getArguments().getString(ARG_ID_CARD_NO);
            bankCode = getArguments().getString(ARG_BANK_CODE);
            bankCardNo = getArguments().getString(ARG_BANK_CARD_NO);
            mobile = getArguments().getString(ARG_MOBILE);
        }
    }

    @Override
    protected String getAlertTitle() {
        return null;
    }

    @Override
    protected String getAlertMsg() {
        String msg = mobile;
        if (!TextUtils.isEmpty(msg) && msg.length() > 4) {
            msg = msg.substring(msg.length() - 4, msg.length());
        }
        return getString(R.string.msg_bank_card_verification___pay, msg);
    }

    @Override
    protected void onGetSmsCode() {
        CommonUtil.unSubscribeSubs(bindBankCardSub);
        bindBankCardSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<JsonElement>() {
                    @Override
                    public void onNext(JsonElement jsonElement) {
                        smsSerialNo = CommonUtil.getAsString(jsonElement, "smsSerialNo");
                        orderNo = CommonUtil.getAsString(jsonElement, "orderNo");
                        interval = CommonUtil.getAsInt(jsonElement, "interval");
                        if (interval > 0) {
                            changeMillisInFuture(interval * 1000);
                        }
                        if (timer != null) {
                            timer.start();
                        }
                    }
                })
                .setProgressDialog(DialogUtil.createProgressDialog(getContext()))
                .build();
        XiaoxiInstallmentApi.bindBankCardObb(getContext(),
                realName,
                idCardNo,
                bankCode,
                bankCardNo,
                mobile)
                .subscribe(bindBankCardSub);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onConfirm() {
        if (etSmsCode.length() == 0) {
            ToastUtil.showToast(getContext(), null, R.string.hint_enter_sms_code___pay);
            return;
        }
        String smsCode = etSmsCode.getText()
                .toString();
        CommonUtil.unSubscribeSubs(verifySub);
        verifySub = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener() {
                    @Override
                    public void onNext(Object o) {
                        RxBus.getDefault()
                                .post(new PayRxEvent(PayRxEvent.RxEventType.BIND_BANK_CARD_SUCCESS,
                                        null));
                        dismiss();
                    }
                })
                .setDataNullable(true)
                .setProgressDialog(DialogUtil.createProgressDialog(getContext()))
                .build();
        XiaoxiInstallmentApi.verifyBankCardObb(getContext(),
                realName,
                idCardNo,
                bankCode,
                bankCardNo,
                mobile,
                smsSerialNo,
                orderNo,
                smsCode)
                .subscribe(verifySub);
    }
}