package com.hunliji.hljcardcustomerlibrary.views.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.hunliji.hljcardcustomerlibrary.R;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.CertifyCodeMsg;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonviewlibrary.views.fragments.BaseGetSmsCodeFragment;
import com.hunliji.hljhttplibrary.api.CommonApi;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.entities.HljHttpStatus;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import rx.Observable;

/**
 * Created by mo_yu on 2017/2/9.验证身份弹窗
 */
public class CheckSecurityVerificationFragment extends BaseGetSmsCodeFragment {
    public static final String ARG_PHONE = "phone";

    private HljHttpSubscriber modifySubscriber;
    private HljHttpSubscriber certifySubscriber;
    public static final int CERTIFY_CODE = 503;//提示"稍后再提交验证码"的retCode
    public static final String SET_PHONE = "setPhone";//新手机号进行安全验证
    private String phone;

    public static CheckSecurityVerificationFragment newInstance(String phone) {
        Bundle args = new Bundle();
        CheckSecurityVerificationFragment fragment = new CheckSecurityVerificationFragment();
        args.putString(ARG_PHONE, phone);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            phone = getArguments().getString(ARG_PHONE);
        }
    }

    @Override
    public void initView() {
        super.initView();
        if (getContext() == null) {
            return;
        }
        tvAlertTip.setVisibility(View.VISIBLE);
        tvAlertTip.setTextColor(ContextCompat.getColor(getContext(), R.color.colorBlack2));
        tvAlertTip.setTextSize(17);
        tvAlertTip.setText(String.valueOf(phone));
    }

    @Override
    protected String getAlertTitle() {
        return "安全验证";
    }

    @Override
    protected String getAlertMsg() {
        return "我们将对您的新手机号进行安全验证";
    }

    @Override
    protected void onGetSmsCode() {
        certify(SET_PHONE);
    }

    @Override
    protected void onConfirm() {
        hideKeyboard();
        preCheckSmsCodeObb();
    }

    private void certify(String flag) {
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.showToast(getActivity(), "请输入手机号码", 0);
            return;
        }
        if (timer != null) {
            timer.start();
        }
        Observable<HljHttpResult<CertifyCodeMsg>> observable = CommonApi.getPostCertifyCode(flag,
                phone);
        certifySubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<HljHttpResult<CertifyCodeMsg>>() {
                    @Override
                    public void onNext(HljHttpResult<CertifyCodeMsg> result) {
                        if (result != null) {
                            HljHttpStatus status = result.getStatus();
                            if (status != null) {
                                if (status.getRetCode() != 0) {
                                    if (status.getRetCode() == CERTIFY_CODE) {
                                        //重复提交 取消倒计时
                                        if (timer != null) {
                                            timer.onFinish();
                                            timer.cancel();
                                        }
                                    }
                                    String msg = status.getMsg();
                                    Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }
                            CertifyCodeMsg codeMsg = result.getData();
                            if (codeMsg != null) {
                                if (HljCommon.debug && !TextUtils.isEmpty(codeMsg.getMsg())) {
                                    Toast.makeText(getContext(),
                                            codeMsg.getMsg(),
                                            Toast.LENGTH_SHORT)
                                            .show();
                                }
                            } else {
                                onCertifyError(btnGetSmsCode, "验证码发送失败，请稍后重试");
                            }
                        }
                    }
                })
                .build();
        observable.subscribe(certifySubscriber);

    }

    private void onCertifyError(Button btnSmsCertify, String msg) {
        if (timer != null) {
            timer.cancel();
        }
        btnSmsCertify.setEnabled(true);
        btnSmsCertify.setText(R.string.label_regain___cv);
        Toast.makeText(btnSmsCertify.getContext(), msg, Toast.LENGTH_SHORT)
                .show();
    }


    public void hideKeyboard() {
        if (getActivity().getCurrentFocus() != null) {
            ((InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(
                    getActivity().getCurrentFocus()
                            .getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void preCheckSmsCodeObb() {
        if (getContext() == null) {
            return;
        }
        final String smsCode = etSmsCode.getText()
                .toString();
        if (TextUtils.isEmpty(smsCode)) {
            ToastUtil.showToast(getActivity(), "请输入验证码", 0);
            return;
        }
        CommonUtil.unSubscribeSubs(modifySubscriber);
        modifySubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener() {
                    @Override
                    public void onNext(Object data) {
                        dismiss();
                        Dialog dialog = DialogUtil.createSingleButtonDialog(getContext(),
                                "提示",
                                getContext().getString(R.string.msg_new_phone_login_in, phone),
                                "重新登陆",
                                new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        ARouter.getInstance()
                                                .build(RouterPath.IntentPath.Customer.LoginActivityPath.LOGIN)
                                                .withFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK)
                                                .withBoolean(RouterPath.IntentPath.Customer.Login.ARG_IS_RESET, true)
                                                .withTransition(R.anim.slide_in_up,
                                                        R.anim.activity_anim_default)
                                                .navigation(v.getContext());
                                        dismiss();
                                    }
                                });
                        dialog.setCancelable(false);
                        dialog.show();
                    }
                })
                .setDataNullable(true)
                .setProgressDialog(DialogUtil.createProgressDialog(getContext()))
                .build();
        CommonApi.modifyBindPhoneObb(phone, smsCode)
                .subscribe(modifySubscriber);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        hideKeyboard();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CommonUtil.unSubscribeSubs(modifySubscriber, certifySubscriber);
    }
}
