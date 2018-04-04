package com.hunliji.marrybiz.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.ReturnStatus;
import com.hunliji.marrybiz.task.NewHttpPostTask;
import com.hunliji.marrybiz.task.OnHttpRequestListener;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.Session;
import com.hunliji.marrybiz.widget.ClearableEditText;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingLoginPhoneActivity extends HljBaseActivity {

    @BindView(R.id.tv_current_account)
    TextView tvCurrentAccount;
    @BindView(R.id.et_phone)
    ClearableEditText etPhone;
    @BindView(R.id.btn_next_step)
    Button btnNextStep;
    @BindView(R.id.content_layout1)
    LinearLayout contentLayout1;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.et_valid_code)
    ClearableEditText etValidCode;
    @BindView(R.id.btn_send_valid_code)
    Button btnSendValidCode;
    @BindView(R.id.btn_submit)
    Button btnSubmit;
    @BindView(R.id.content_layout2)
    LinearLayout contentLayout2;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    private String phoneNumber;
    private String currentAccountName;
    private TimeDown timeDown;
    private int step = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_login_phone);
        ButterKnife.bind(this);

        setTextWatcher();
        currentAccountName = getIntent().getStringExtra("current_email");

        tvCurrentAccount.setText(getString(R.string.label_current_account, currentAccountName));
    }

    @Override
    public void onBackPressed() {
        if (step == 2) {
            contentLayout1.setVisibility(View.VISIBLE);
            contentLayout2.setVisibility(View.GONE);
            phoneNumber = "";
            if (timeDown != null) {
                timeDown.cancel();
            }
            step = 1;
            setTitle(R.string.title_activity_setting_login_phone);
        } else {
            super.onBackPressed();
        }
    }

    private void goNextStep() {
        // 已发送验证码,开始倒计时,并且禁用按钮
        btnSendValidCode.setEnabled(false);
        timeDown = new TimeDown(60 * 1000, 1000);
        timeDown.start();

        step = 2;
        phoneNumber = etPhone.getText()
                .toString();
        tvPhone.setText(phoneNumber);
        setTitle(R.string.label_enter_valid_code);
        contentLayout1.setVisibility(View.GONE);
        contentLayout2.setVisibility(View.VISIBLE);
        etValidCode.setText("");
    }

    @OnClick(R.id.btn_next_step)
    void onNextStep() {
        if (etPhone.length() == 0) {
            Toast.makeText(this, R.string.msg_empty_phone, Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("step", 1);
            jsonObject.put("Phone",
                    etPhone.getText()
                            .toString());
            progressBar.setVisibility(View.VISIBLE);
            new NewHttpPostTask(this, new OnHttpRequestListener() {
                @Override
                public void onRequestCompleted(Object obj) {
                    JSONObject resultObj = (JSONObject) obj;
                    progressBar.setVisibility(View.GONE);
                    if (resultObj != null) {
                        ReturnStatus returnStatus = new ReturnStatus(resultObj.optJSONObject(
                                "status"));
                        if (returnStatus.getRetCode() == 0) {
                            String str = Constants.DEBUG ? resultObj.optString("data") :
                                    getString(R.string.msg_succeed_send_code);
                            Toast.makeText(SettingLoginPhoneActivity.this, str, Toast.LENGTH_SHORT)
                                    .show();
                            goNextStep();
                        } else {
                            Toast.makeText(SettingLoginPhoneActivity.this,
                                    returnStatus.getErrorMsg(),
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }
                    } else {
                        Toast.makeText(SettingLoginPhoneActivity.this,
                                R.string.msg_fail_to_send_sms_code,
                                Toast.LENGTH_SHORT)
                                .show();
                    }

                }

                @Override
                public void onRequestFailed(Object obj) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SettingLoginPhoneActivity.this,
                            R.string.msg_fail_to_send_sms_code,
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }).execute(Constants.getAbsUrl(Constants.HttpPath.POST_SETTING_PHONE),
                    jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.btn_send_valid_code)
    void onResendValidCode() {
        if (JSONUtil.isEmpty(phoneNumber)) {
            Toast.makeText(this, R.string.msg_empty_phone, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Phone", phoneNumber);
            jsonObject.put("flag", "setPhone");

            progressBar.setVisibility(View.VISIBLE);
            new NewHttpPostTask(this, new OnHttpRequestListener() {
                @Override
                public void onRequestCompleted(Object obj) {
                    JSONObject resultObj = (JSONObject) obj;
                    progressBar.setVisibility(View.GONE);
                    if (resultObj != null) {
                        ReturnStatus returnStatus = new ReturnStatus(resultObj.optJSONObject(
                                "status"));
                        if (returnStatus.getRetCode() == 0) {
                            String str = Constants.DEBUG ? resultObj.optString("data") :
                                    getString(R.string.msg_succeed_send_code);
                            Toast.makeText(SettingLoginPhoneActivity.this, str, Toast.LENGTH_SHORT)
                                    .show();
                            // 已发送验证码,开始倒计时,并且禁用按钮
                            btnSendValidCode.setEnabled(false);
                            timeDown = new TimeDown(60 * 1000, 1000);
                            timeDown.start();
                        } else {
                            Toast.makeText(SettingLoginPhoneActivity.this,
                                    returnStatus.getErrorMsg(),
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }
                    } else {
                        Toast.makeText(SettingLoginPhoneActivity.this,
                                R.string.msg_fail_to_send_sms_code,
                                Toast.LENGTH_SHORT)
                                .show();
                    }

                }

                @Override
                public void onRequestFailed(Object obj) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SettingLoginPhoneActivity.this,
                            R.string.msg_fail_to_send_sms_code,
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }).execute(Constants.getAbsUrl(Constants.HttpPath.SEND_SMS_CODE_AGIN),
                    jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.btn_submit)
    void onSubmit() {
        if (JSONUtil.isEmpty(etValidCode.getText()
                .toString())) {
            Toast.makeText(this, R.string.msg_empty_valid_code, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (JSONUtil.isEmpty(phoneNumber)) {
            Toast.makeText(this, R.string.msg_empty_phone, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("step", 2);
            jsonObject.put("Phone", phoneNumber);
            jsonObject.put("SmsCode",
                    etValidCode.getText()
                            .toString());
            progressBar.setVisibility(View.VISIBLE);
            new NewHttpPostTask(this, new OnHttpRequestListener() {
                @Override
                public void onRequestCompleted(Object obj) {
                    JSONObject resultObj = (JSONObject) obj;
                    progressBar.setVisibility(View.GONE);
                    if (resultObj != null) {
                        ReturnStatus returnStatus = new ReturnStatus(resultObj.optJSONObject(
                                "status"));
                        if (returnStatus.getRetCode() == 0) {
                            // 成功设置手机号码
                            Session.getInstance()
                                    .editPhone(SettingLoginPhoneActivity.this, phoneNumber);
                            Intent intent = new Intent(SettingLoginPhoneActivity.this,
                                    HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("is_login_done", true);
                            startActivity(intent);
                            overridePendingTransition(R.anim.activity_anim_default,
                                    R.anim.activity_anim_default);
                            finish();
                        } else {
                            Toast.makeText(SettingLoginPhoneActivity.this,
                                    returnStatus.getErrorMsg(),
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }
                    } else {
                        Toast.makeText(SettingLoginPhoneActivity.this,
                                R.string.msg_fail_to_set_phone,
                                Toast.LENGTH_SHORT)
                                .show();
                    }

                }

                @Override
                public void onRequestFailed(Object obj) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SettingLoginPhoneActivity.this,
                            R.string.msg_fail_to_set_phone,
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }).execute(Constants.getAbsUrl(Constants.HttpPath.POST_SETTING_PHONE),
                    jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setTextWatcher() {
        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                btnNextStep.setEnabled(etPhone.length() > 0);
            }
        });
        etValidCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                btnSubmit.setEnabled(etValidCode.length() > 0);
            }
        });
    }

    @Override
    protected void onFinish() {
        if (timeDown != null) {
            timeDown.cancel();
        }
        super.onFinish();
    }

    public class TimeDown extends CountDownTimer {

        public TimeDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            btnSendValidCode.setEnabled(true);
            btnSendValidCode.setText(R.string.label_resend_valid_code);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            int secUntilFinished = (int) (millisUntilFinished / 1000);
            btnSendValidCode.setText(getString(R.string.label_resend_valid_code3,
                    secUntilFinished > 9 ? secUntilFinished : ("0" + secUntilFinished)));
        }
    }

}
