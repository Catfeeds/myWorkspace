package com.hunliji.cardmaster.fragments.login;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hunliji.cardmaster.Constants;
import com.hunliji.cardmaster.R;
import com.hunliji.cardmaster.api.login.LoginApi;
import com.hunliji.cardmaster.models.login.CertifyPostBody;
import com.hunliji.cardmaster.models.login.LoginResult;
import com.hunliji.cardmaster.models.login.ThirdRegisterPostBody;
import com.hunliji.hljcommonlibrary.models.CertifyCodeMsg;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.entities.HljHttpStatus;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnCompletedListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljsharelibrary.models.ThirdLoginParameter;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by wangtao on 2017/7/14.
 */

public class ThirdLoginBindFragment extends RefreshFragment {

    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.btn_sms_certify)
    ImageButton btnSmsCertify;
    @BindView(R.id.tv_count_down)
    TextView tvCountDown;
    @BindView(R.id.btn_resend)
    Button btnResend;
    @BindView(R.id.phone_line)
    View phoneLine;
    @BindView(R.id.et_certify)
    EditText etCertify;
    @BindView(R.id.btn_voice_certify)
    Button btnVoiceCertify;
    @BindView(R.id.certify_edit_layout)
    RelativeLayout certifyEditLayout;
    Unbinder unbinder;

    private Subscription certifySubscription;
    private Subscription timeDownSubscription;
    private Subscription loginSubscription;
    private Subscription immSubscription;
    private InputMethodManager imm;

    private ThirdLoginParameter parameter;

    public static ThirdLoginBindFragment newInstance(ThirdLoginParameter parameter) {
        Bundle args = new Bundle();
        args.putParcelable("parameter", parameter);
        ThirdLoginBindFragment fragment = new ThirdLoginBindFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imm = ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE));
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_thrid_login_bind, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null) {
            parameter = getArguments().getParcelable("parameter");
        }
    }

    private String testRegex = "#\\w+##";
    private String testRegex2 = "[^#]+\\w+";

    @OnClick({R.id.btn_sms_certify, R.id.btn_resend, R.id.btn_voice_certify})
    public void onGetCertify(View view) {
        String phone = etPhone.getText()
                .toString()
                .trim();
        if (TextUtils.isEmpty(phone)) {
            phoneLine.setBackgroundResource(R.color.colorPrimary);
            etPhone.requestFocus();
            imm.showSoftInput(etPhone, InputMethodManager.SHOW_IMPLICIT);
            return;
        }
        if (phone.matches(testRegex)) {
            certifyEditLayout.setVisibility(View.VISIBLE);
            etCertify.requestFocus();
            return;
        }
        if (!CommonUtil.isMobileNO(phone)) {
            phoneLine.setBackgroundResource(R.color.colorPrimary);
            ToastUtil.showToast(getContext(), null, R.string.hint_new_number_error);
            return;
        }
        phoneLine.setBackgroundResource(R.color.colorLine);
        startTimeDown();
        CommonUtil.unSubscribeSubs(certifySubscription);
        Subscriber subscriber = HljHttpSubscriber.buildSubscriber(getContext())
                .setProgressDialog(DialogUtil.createProgressDialog(getContext()))
                .setOnNextListener(new SubscriberOnNextListener<HljHttpResult<CertifyCodeMsg>>() {
                    @Override
                    public void onNext(HljHttpResult<CertifyCodeMsg> result) {
                        HljHttpStatus status = result.getStatus();
                        if (status != null && status.getRetCode() != 0) {
                            if (status.getRetCode() != LoginApi.RESULT_CODE_CERTIFY_BUSY) {
                                CommonUtil.unSubscribeSubs(timeDownSubscription);
                            }
                            ToastUtil.showToast(getContext(),
                                    status.getMsg(),
                                    R.string.hint_send_code_error);
                        } else {
                            certifyEditLayout.setVisibility(View.VISIBLE);
                            etCertify.requestFocus();
                        }
                        if (Constants.DEBUG) {
                            CertifyCodeMsg codeMsg = result.getData();
                            if (codeMsg != null && !TextUtils.isEmpty(codeMsg.getMsg())) {
                                ToastUtil.showToast(getContext(), codeMsg.getMsg(), 0);
                            }
                        }
                    }
                })
                .setOnCompletedListener(new SubscriberOnCompletedListener() {
                    @Override
                    public void onCompleted() {
                        if (certifyEditLayout.getVisibility() == View.VISIBLE) {
                            CommonUtil.unSubscribeSubs(immSubscription);
                            immSubscription = Observable.interval(100, TimeUnit.MILLISECONDS)
                                    .first()
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Action1<Long>() {
                                        @Override
                                        public void call(Long aLong) {
                                            imm.showSoftInput(etCertify,
                                                    InputMethodManager.SHOW_IMPLICIT);
                                        }
                                    });
                        }
                    }
                })
                .setOnErrorListener(new SubscriberOnErrorListener() {
                    @Override
                    public void onError(Object o) {
                        CommonUtil.unSubscribeSubs(timeDownSubscription);
                    }
                })
                .build();
        if (view.getId() == R.id.btn_voice_certify) {
            ToastUtil.showToast(getContext(), null, R.string.hint_voice_certify);
            certifySubscription = LoginApi.getCertifyCode(new CertifyPostBody(phone,
                    CertifyPostBody.FLAG_LOGIN_CERTIFY_VOICE))
                    .subscribe(subscriber);
        } else {
            certifySubscription = LoginApi.getCertifyCode(new CertifyPostBody(phone,
                    CertifyPostBody.FLAG_LOGIN_CERTIFY_MSG))
                    .subscribe(subscriber);
        }
    }

    @OnTextChanged(R.id.et_certify)
    public void onCertifyEdit(CharSequence text) {
        String code = text.toString()
                .trim();
        if (code.length() == 4) {
            String phone = etPhone.getText()
                    .toString()
                    .trim();
            if (phone.matches(testRegex)) {
                Matcher matcher = Pattern.compile(testRegex2)
                        .matcher(phone);
                if (matcher.find()) {
                    phone = matcher.group(0);
                }
            }
            if (CommonUtil.isMobileNO(phone)) {
                onCertifyCheck();
            }
        }
    }

    @SuppressWarnings("unchecked")
    @OnClick(R.id.btn_certify_check)
    public void onCertifyCheck() {
        String phone = etPhone.getText()
                .toString()
                .trim();
        if (phone.matches(testRegex)) {
            Matcher matcher = Pattern.compile(testRegex2)
                    .matcher(phone);
            if (matcher.find()) {
                phone = matcher.group(0);
            }
        }
        if (!CommonUtil.isMobileNO(phone)) {
            ToastUtil.showToast(getContext(), null, R.string.hint_new_number_error);
            return;
        }
        String code = etCertify.getText()
                .toString()
                .trim();
        if (TextUtils.isEmpty(code)) {
            ToastUtil.showToast(getContext(), null, R.string.label_certify_hint);
            return;
        }
        if (parameter == null) {
            return;
        }
        if (loginSubscription != null && !loginSubscription.isUnsubscribed()) {
            return;
        }
        UserSession.getInstance()
                .logout(getContext());
        loginSubscription = LoginApi.thirdRegister(new ThirdRegisterPostBody(phone,
                code,
                parameter))
                .subscribe(HljHttpSubscriber.buildSubscriber(getContext())
                        .setProgressDialog(DialogUtil.createProgressDialog(getContext()))
                        .setOnNextListener(new SubscriberOnNextListener<LoginResult>() {
                            @Override
                            public void onNext(LoginResult loginResult) {
                                if (getParentFragment() != null && getParentFragment() instanceof
                                        PhoneLoginFragment.ChildLoginCallback) {
                                    ((PhoneLoginFragment.ChildLoginCallback) getParentFragment()).onComplete(
                                            loginResult);
                                }
                            }
                        })
                        .build());

    }

    private void startTimeDown() {
        CommonUtil.unSubscribeSubs(timeDownSubscription);
        timeDownSubscription = Observable.interval(0, 1, TimeUnit.SECONDS)
                .take(31)
                .doOnUnsubscribe(new Action0() {
                    @Override
                    public void call() {
                        if (unbinder == null) {
                            return;
                        }
                        btnResend.setVisibility(View.VISIBLE);
                        btnVoiceCertify.setVisibility(View.VISIBLE);
                        tvCountDown.setVisibility(View.GONE);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Long>() {

                    @Override
                    public void onStart() {
                        btnSmsCertify.setVisibility(View.GONE);
                        btnResend.setVisibility(View.GONE);
                        btnVoiceCertify.setVisibility(View.GONE);
                        tvCountDown.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Long aLong) {
                        tvCountDown.setText(getContext().getString(R.string.label_second,
                                30 - aLong));
                    }
                });
    }

    @Override
    public void refresh(Object... params) {

    }

    @Override
    public void onDestroyView() {
        CommonUtil.unSubscribeSubs(certifySubscription,
                timeDownSubscription,
                loginSubscription,
                immSubscription);
        if (unbinder != null) {
            unbinder.unbind();
            unbinder = null;
        }
        super.onDestroyView();
    }
}
