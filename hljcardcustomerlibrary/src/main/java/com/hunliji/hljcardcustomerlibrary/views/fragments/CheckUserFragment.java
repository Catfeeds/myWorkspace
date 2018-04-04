package com.hunliji.hljcardcustomerlibrary.views.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hunliji.hljcardcustomerlibrary.R;
import com.hunliji.hljcardcustomerlibrary.views.activities.BindUserPhoneActivity;
import com.hunliji.hljcardcustomerlibrary.views.activities.WithdrawCardListActivity;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.CertifyCodeMsg;
import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
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
public class CheckUserFragment extends BaseGetSmsCodeFragment {
    private User user;
    private HljHttpSubscriber checkSubscriber;
    private HljHttpSubscriber certifySubscriber;
    public static final int CERTIFY_CODE = 503;//提示"稍后再提交验证码"的retCode
    public static final String WALLET_MSG = "walletMsg";//提现验证码

    public static CheckUserFragment newInstance() {
        Bundle args = new Bundle();
        CheckUserFragment fragment = new CheckUserFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = UserSession.getInstance()
                .getUser(getContext());
    }

    @Override
    public void initView() {
        super.initView();
        tvAlertTitle.setTextSize(14);
        tvAlertTitle.setTextColor(ContextCompat.getColor(getContext(), R.color.colorBlack2));
        tvAlertTitle.getPaint()
                .setFakeBoldText(false);
        tvAlertTitle.setLineSpacing(CommonUtil.dp2px(getContext(), 4), 1);
        tvAlertMsg.setTextColor(ContextCompat.getColor(getContext(), R.color.colorBlack2));
        tvAlertMsg.setTextSize(18);
        tvAlertMsg.getPaint()
                .setFakeBoldText(true);
        tvAlertMsg.setLineSpacing(0, 1);
        tvAlertTip.setVisibility(View.VISIBLE);
        tvAlertTip.setText("手机号已更换？");
        tvAlertTip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), BindUserPhoneActivity.class);
                startActivity(intent);
                dismiss();
            }
        });
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) tvAlertMsg.getLayoutParams();
        lp.gravity = Gravity.CENTER;
        lp.topMargin = CommonUtil.dp2px(getContext(), 18);
        lp.bottomMargin = CommonUtil.dp2px(getContext(), 18);
        tvAlertMsg.setLayoutParams(lp);
    }

    @Override
    protected String getAlertTitle() {
        return getString(R.string.label_withdraw_cash_check_user);
    }

    @Override
    protected String getAlertMsg() {
        return user != null ? user.getPhone() : null;
    }

    @Override
    protected void onGetSmsCode() {
        certify(WALLET_MSG);
    }

    @Override
    protected void onConfirm() {
        hideKeyboard();
        preCheckSmsCodeObb();
    }

    private void certify(String flag) {
        String phone = user.getPhone();
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.showToast(getActivity(),  "请输入手机号码",0);
            return;
        }
        if (timer != null) {
            timer.start();
        }
        Observable<HljHttpResult<CertifyCodeMsg>> observable = CommonApi.getPostCertifyCode(flag,phone);
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
                                    Toast.makeText(getContext(), codeMsg.getMsg(), Toast.LENGTH_SHORT)
                                            .show();
                                }
                            } else {
                                onCertifyError(btnGetSmsCode,
                                        "验证码发送失败，请稍后重试");
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
        final String smsCode = etSmsCode.getText()
                .toString();
        if (TextUtils.isEmpty(smsCode)) {
            ToastUtil.showToast(getActivity(), "请输入验证码", 0);
            return;
        }
        CommonUtil.unSubscribeSubs(checkSubscriber);
        checkSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener() {
                    @Override
                    public void onNext(Object data) {
                        dismiss();
                        Intent intent = new Intent();
                        intent.putExtra("smsCode", smsCode);
                        intent.setClass(getContext(), WithdrawCardListActivity.class);
                        startActivity(intent);
                    }
                })
                .build();
        CommonApi.preCheckSmsCodeObb(smsCode)
                .subscribe(checkSubscriber);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        hideKeyboard();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CommonUtil.unSubscribeSubs(checkSubscriber,certifySubscriber);
    }
}
