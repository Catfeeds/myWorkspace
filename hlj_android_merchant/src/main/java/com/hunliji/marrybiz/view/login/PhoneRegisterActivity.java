package com.hunliji.marrybiz.view.login;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.suncloud.hljweblibrary.views.activities.HljWebViewActivity;
import com.google.gson.JsonElement;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.api.login.LoginApi;
import com.hunliji.marrybiz.util.Session;
import com.hunliji.marrybiz.util.Util;
import com.hunliji.marrybiz.view.HomeActivity;
import com.hunliji.marrybiz.widget.ClearableEditText;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PhoneRegisterActivity extends HljBaseActivity {


    @BindView(R.id.et_phone)
    ClearableEditText etPhone;
    @BindView(R.id.et_valid_code)
    ClearableEditText etValidCode;
    @BindView(R.id.btn_resend_valid_code)
    Button btnResendValidCode;
    @BindView(R.id.et_password)
    ClearableEditText etPassword;
    @BindView(R.id.et_confirm_password)
    ClearableEditText etConfirmPassword;
    @BindView(R.id.cb_agreement)
    CheckBox cbAgreement;
    @BindView(R.id.btn_next)
    Button btnNext;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    private TimeDown timeDown;
    private HljHttpSubscriber codeSubscriber;
    private HljHttpSubscriber registerSubscriber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_register);
        ButterKnife.bind(this);
        setTextWatcher();
    }


    @OnClick({R.id.root_view, R.id.content_layout1})
    void onRootView() {
        hideKeyboard(null);
    }

    @OnClick(R.id.btn_resend_valid_code)
    void onGetValidCode() {
        String phone = etPhone.getText()
                .toString();
        if (!Util.isMobileNO(phone)) {
            Toast.makeText(this, R.string.hint_new_number_error, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (codeSubscriber == null || codeSubscriber.isUnsubscribed()) {
            codeSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener() {
                        @Override
                        public void onNext(Object o) {
                            Toast.makeText(PhoneRegisterActivity.this,
                                    "验证码发送成功",
                                    Toast.LENGTH_SHORT)
                                    .show();
                            // 已发送验证码,开始倒计时,并且禁用按钮
                            btnResendValidCode.setEnabled(false);
                            timeDown = new TimeDown(60 * 1000, 1000);
                            timeDown.start();
                        }
                    })
                    .setProgressBar(progressBar)
                    .build();
            LoginApi.getValidCodeObb(phone)
                    .subscribe(codeSubscriber);
        }
    }

    @OnClick(R.id.btn_next)
    void onNext() {
        hideKeyboard(null);
        String phone = etPhone.getText()
                .toString();
        String password = etPassword.getText()
                .toString();
        String confirmPassword = etConfirmPassword.getText()
                .toString();
        String code = etValidCode.getText()
                .toString();
        if (!Util.isMobileNO(phone)) {
            Toast.makeText(this, R.string.hint_new_number_error, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (password.length() > Constants.MAX_PSW_LENGTH || password.length() < Constants
                .MIN_PSW_LENGTH) {
            Toast.makeText(this, R.string.msg_invalid_password, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, R.string.msg_invalid_confirm_password, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (registerSubscriber == null || registerSubscriber.isUnsubscribed()) {
            registerSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<JsonElement>() {
                        @Override
                        public void onNext(JsonElement jsonElement) {
                            JSONObject loginJsonObject = null;
                            try {
                                loginJsonObject = new JSONObject(jsonElement.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(PhoneRegisterActivity.this,
                                    R.string.msg_succeed_register,
                                    Toast.LENGTH_SHORT)
                                    .show();
                            Session.getInstance()
                                    .setCurrentUser(PhoneRegisterActivity.this, loginJsonObject);
                            Dialog dialog = DialogUtil.createSingleButtonDialog(
                                    PhoneRegisterActivity.this,
                                    "注册成功",
                                    "系统将自动登录，请及时完成开店流程",
                                    null,
                                    null);
                            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    RxBus.getDefault()
                                            .post(new RxEvent(RxEvent.RxEventType.REGISTER_SUCCESS,
                                                    null));
                                    Intent intent = new Intent();
                                    intent.setClass(PhoneRegisterActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            dialog.show();
                        }

                    })
                    .setProgressBar(progressBar)
                    .build();
            LoginApi.postRegisterObb(phone, code, password, password)
                    .subscribe(registerSubscriber);
        }
    }

    @OnClick(R.id.tv_agreement)
    void onAgreement() {
        hideKeyboard(null);
        Intent intent = new Intent(this, HljWebViewActivity.class);
        intent.putExtra("path", Constants.getAbsUrl(Constants.HttpPath.USER_PROTOCOL_URL));
        intent.putExtra("title", getString(R.string.label_register_agreement));
        startActivity(intent);
    }


    @OnClick(R.id.agreement_view)
    public void onAgreementView() {
        hideKeyboard(null);
        if (cbAgreement.isChecked()) {
            cbAgreement.setChecked(false);
        } else {
            cbAgreement.setChecked(true);
        }
        checkConfirmEnable();
    }

    private void checkConfirmEnable() {
        if (etPassword.length() > 0 && etValidCode.length() > 0 && etPhone.length() > 0 &&
                etConfirmPassword.length() > 0 && cbAgreement.isChecked()) {
            btnNext.setEnabled(true);
        } else {
            btnNext.setEnabled(false);
        }
    }

    private void setTextWatcher() {
        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkConfirmEnable();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        etValidCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkConfirmEnable();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkConfirmEnable();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkConfirmEnable();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public class TimeDown extends CountDownTimer {

        public TimeDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            btnResendValidCode.setEnabled(true);
            btnResendValidCode.setText(R.string.label_resend_valid_code);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            btnResendValidCode.setEnabled(false);
            int secUntilFinished = (int) (millisUntilFinished / 1000);
            btnResendValidCode.setText(String.valueOf(secUntilFinished > 9 ? secUntilFinished :
                    ("0" + secUntilFinished)));
        }
    }

    @Override
    protected void onFinish() {
        if (timeDown != null) {
            timeDown.cancel();
        }
        CommonUtil.unSubscribeSubs(codeSubscriber, registerSubscriber);
        super.onFinish();
    }


}
