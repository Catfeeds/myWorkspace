package com.hunliji.marrybiz.fragment.revenue;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonviewlibrary.views.fragments.BaseGetSmsCodeFragment;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.entities.HljHttpStatus;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.api.revenue.RevenueApi;
import com.hunliji.marrybiz.model.MerchantUser;
import com.hunliji.marrybiz.model.revenue.Bank;
import com.hunliji.marrybiz.util.Session;
import com.hunliji.marrybiz.view.revenue.BondAccountActivity;

import rx.Observable;


/**
 * Created by hua_rong on 2017/8/18 0018
 * 提现 修改绑定银行卡/支付宝信息 验证
 */

public class BondBankGetSmsCodeFragment extends BaseGetSmsCodeFragment {


    private HljHttpSubscriber checkSubscriber;
    private HljHttpSubscriber submitSubscriber;
    private static final String BANK = "bank";
    private Bank bankBody;
    private ProgressBar progressBar;
    private BondAccountActivity activity;
    private String contactMobile;

    public static BondBankGetSmsCodeFragment newInstance(Bank bankBody) {
        Bundle args = new Bundle();
        BondBankGetSmsCodeFragment fragment = new BondBankGetSmsCodeFragment();
        args.putParcelable(BANK, bankBody);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (BondAccountActivity) getActivity();
        progressBar = activity.getProgressBar();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MerchantUser merchantUser = Session.getInstance()
                .getCurrentUser();
        contactMobile = merchantUser.getContactMobile();
        Bundle bundle = getArguments();
        if (bundle != null) {
            bankBody = bundle.getParcelable(BANK);
        }
    }

    @Override
    protected String getAlertTitle() {
        return getString(R.string.label_safety_check);
    }

    @Override
    protected String getAlertMsg() {
        if (TextUtils.isEmpty(contactMobile) || contactMobile.length() < 7) {
            return "请输入手机收到的验证码";
        }
        return getString(R.string.label_enter_verification_code_received_by_phone,
                contactMobile.substring(7));
    }

    @Override
    protected void onGetSmsCode() {
        if (timer != null) {
            timer.start();
        }
        Observable<HljHttpResult<JsonObject>> observable = RevenueApi.getCertifyCode(contactMobile);
        checkSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<HljHttpResult<JsonObject>>() {
                    @Override
                    public void onNext(HljHttpResult<JsonObject> hljHttpResult) {
                        try {
                            HljHttpStatus status = hljHttpResult.getStatus();
                            if (status != null) {
                                if (status.getRetCode() != 0) {
                                    if (status.getRetCode() == 503) {
                                        //重复提交 取消倒计时
                                        if (timer != null) {
                                            timer.onFinish();
                                            timer.cancel();
                                        }
                                    }
                                    Toast.makeText(getContext(),
                                            status.getMsg(),
                                            Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }
                            JsonObject jsonObject = hljHttpResult.getData();
                            if (jsonObject != null) {
                                String msg = jsonObject.get("msg")
                                        .toString();
                                if (Constants.DEBUG && !TextUtils.isEmpty(msg)) {
                                    Log.d("bond_bank", "验证码：" + msg);
                                }
                            } else {
                                certifyFail();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setProgressBar(progressBar)
                .build();
        observable.subscribe(checkSubscriber);
    }

    private void certifyFail() {
        if (timer != null) {
            timer.cancel();
        }
        btnGetSmsCode.setEnabled(true);
        btnGetSmsCode.setText(getString(R.string.label_resend_code));
        Toast.makeText(getContext(), getString(R.string.label_code_sent_fail), Toast.LENGTH_SHORT)
                .show();
    }


    @Override
    protected void onConfirm() {
        if (activity != null) {
            activity.hideKeyboard(null);
        }
        checkSmsCodeObb();
    }


    private void checkSmsCodeObb() {
        String smsCode = etSmsCode.getText()
                .toString();
        if (TextUtils.isEmpty(smsCode)) {
            Toast.makeText(getContext(),
                    getString(R.string.label_protect_financial_security_verify_registered_phone),
                    Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        bankBody.setCode(smsCode);
        CommonUtil.unSubscribeSubs(submitSubscriber);
        Observable observable = RevenueApi.postAppBindBank(bankBody);
        submitSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener() {
                    @Override
                    public void onNext(Object o) {
                        Toast.makeText(getContext(),
                                getString(R.string.label_submit_success),
                                Toast.LENGTH_SHORT)
                                .show();
                        dismiss();
                        if (activity != null) {
                            activity.finish();
                        }
                    }
                })
                .setProgressBar(progressBar)
                .build();
        observable.subscribe(submitSubscriber);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        CommonUtil.unSubscribeSubs(checkSubscriber, submitSubscriber);
    }

}
