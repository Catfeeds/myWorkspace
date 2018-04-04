package com.hunliji.marrybiz.view;

import android.app.Dialog;
import android.graphics.Point;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import com.hunliji.marrybiz.util.Util;
import com.hunliji.marrybiz.widget.ClearableEditText;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FindPswActivity extends HljBaseActivity {

    @BindView(R.id.et_phone)
    ClearableEditText etPhone;
    @BindView(R.id.et_valid_code)
    ClearableEditText etValidCode;
    @BindView(R.id.btn_valid)
    Button btnValid;
    @BindView(R.id.btn_send_valid_code)
    Button btnSendValidCode;
    @BindView(R.id.content_layout1)
    LinearLayout contentLayout1;
    @BindView(R.id.et_password)
    ClearableEditText etPassword;
    @BindView(R.id.et_re_password)
    ClearableEditText etRePassword;
    @BindView(R.id.btn_submit)
    Button btnSubmit;
    @BindView(R.id.content_layout2)
    LinearLayout contentLayout2;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    private int step = 1; // 1:获取验证码,2,提交验证码,3:提交新密码
    private String phoneNumber;
    private String validCode;
    private Dialog dialog;
    private TimeDown timeDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_psw);
        ButterKnife.bind(this);

        setTextWatcher();

        phoneNumber = getIntent().getStringExtra("phone_number");
        if (!JSONUtil.isEmpty(phoneNumber)) {
            etPhone.setText(phoneNumber);
        }
        contentLayout1.setVisibility(View.VISIBLE);
        contentLayout2.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        if (step == 2) {
            contentLayout1.setVisibility(View.VISIBLE);
            contentLayout2.setVisibility(View.GONE);
            validCode = "";
            if (timeDown != null) {
                timeDown.cancel();
            }
            btnSendValidCode.setEnabled(true);
            btnSendValidCode.setText(R.string.label_get_valid_code);
            step = 1;
        } else {
            super.onBackPressed();
        }
    }

    private void setTextWatcher() {
        TextWatcher textWatcher1 = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                btnValid.setEnabled(etPhone.length() > 0 && etValidCode.length() > 0);
            }
        };
        TextWatcher textWatcher2 = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                btnSubmit.setEnabled(etPassword.length() > 0 && etRePassword.length() > 0);
            }
        };
        etPhone.addTextChangedListener(textWatcher1);
        etValidCode.addTextChangedListener(textWatcher1);
        etPassword.addTextChangedListener(textWatcher2);
        etRePassword.addTextChangedListener(textWatcher2);
    }

    private void goNextStep() {
        if (timeDown != null) {
            timeDown.cancel();
        }
        btnSendValidCode.setEnabled(true);

        step = 2;
        phoneNumber = etPhone.getText()
                .toString();
        validCode = etValidCode.getText()
                .toString();
        contentLayout2.setVisibility(View.VISIBLE);
        contentLayout1.setVisibility(View.GONE);
        etValidCode.setText("");
    }

    @OnClick(R.id.btn_valid)
    void onValidate() {
        if (etPhone.length() == 0 || !Util.isMobileNO(etPhone.getText()
                .toString())) {
            Toast.makeText(this, R.string.msg_invalid_phone, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (etValidCode.length() == 0) {
            Toast.makeText(this, R.string.msg_empty_valid_code, Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("step", 2);
            jsonObject.put("Phone",
                    etPhone.getText()
                            .toString());
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
                            goNextStep();
                        } else {
                            Toast.makeText(FindPswActivity.this,
                                    returnStatus.getErrorMsg(),
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }
                    } else {
                        Toast.makeText(FindPswActivity.this,
                                R.string.msg_fail_to_valid_phone,
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                }

                @Override
                public void onRequestFailed(Object obj) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(FindPswActivity.this,
                            R.string.msg_fail_to_valid_phone,
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }).execute(Constants.getAbsUrl(Constants.HttpPath.POST_FOR_FORGET_PSW),
                    jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.btn_submit)
    void onSumbit() {
        if (etPassword.length() == 0 || etRePassword.length() == 0) {
            Toast.makeText(this, R.string.msg_empty_password, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (!etRePassword.getText()
                .toString()
                .equals(etPassword.getText()
                        .toString())) {
            Toast.makeText(this, R.string.msg_wrong_re_password, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (etPassword.length() > Constants.MAX_PSW_LENGTH || etPassword.length() < Constants
                .MIN_PSW_LENGTH) {
            Toast.makeText(FindPswActivity.this, R.string.msg_invalid_password, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("step", 3);
            jsonObject.put("Phone", phoneNumber);
            jsonObject.put("SmsCode", validCode);
            jsonObject.put("Password",
                    etPassword.getText()
                            .toString());
            progressBar.setVisibility(View.VISIBLE);
            new NewHttpPostTask(this, new OnHttpRequestListener() {
                @Override
                public void onRequestCompleted(Object obj) {
                    progressBar.setVisibility(View.GONE);
                    JSONObject resultObj = (JSONObject) obj;
                    if (resultObj != null) {
                        ReturnStatus returnStatus = new ReturnStatus(resultObj.optJSONObject(
                                "status"));
                        if (returnStatus.getRetCode() == 0) {
                            // 设置成功
                            showAlertDialog();
                        } else {
                            Toast.makeText(FindPswActivity.this,
                                    returnStatus.getErrorMsg(),
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }
                    } else {
                        Toast.makeText(FindPswActivity.this,
                                R.string.msg_fail_to_reset_password,
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                }

                @Override
                public void onRequestFailed(Object obj) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(FindPswActivity.this,
                            R.string.msg_fail_to_reset_password,
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }).execute(Constants.getAbsUrl(Constants.HttpPath.POST_FOR_FORGET_PSW),
                    jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showAlertDialog() {
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        dialog = new Dialog(FindPswActivity.this, R.style.BubbleDialogTheme);
        dialog.setCancelable(false);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_msg_single_button, null);
        TextView titleView = (TextView) dialogView.findViewById(R.id.dialog_msg_title);
        TextView contentView = (TextView) dialogView.findViewById(R.id.dialog_msg_content);
        titleView.setText(R.string.msg_succeed_reset_password);
        contentView.setText(R.string.hint_reset_password);
        Button confirmView = (Button) dialogView.findViewById(R.id.dialog_msg_confirm);
        confirmView.setText(R.string.label_ok);
        confirmView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                finish();
            }
        });

        dialog.setContentView(dialogView);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        Point point = JSONUtil.getDeviceSize(FindPswActivity.this);
        params.width = Math.round(point.x * 5 / 7);
        params.height = Math.round(params.width * 256 / 380);
        window.setAttributes(params);
        dialog.show();
    }

    @OnClick(R.id.btn_send_valid_code)
    void onSendValidCode() {
        if (etPhone.length() == 0 || !Util.isMobileNO(etPhone.getText()
                .toString())) {
            Toast.makeText(this, R.string.msg_invalid_phone, Toast.LENGTH_SHORT)
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
                            Toast.makeText(FindPswActivity.this, str, Toast.LENGTH_SHORT)
                                    .show();
                            btnSendValidCode.setEnabled(false);
                            timeDown = new TimeDown(60 * 1000, 1000);
                            timeDown.start();
                        } else {
                            Toast.makeText(FindPswActivity.this,
                                    returnStatus.getErrorMsg(),
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }
                    } else {
                        Toast.makeText(FindPswActivity.this,
                                R.string.msg_fail_to_send_sms_code,
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                }

                @Override
                public void onRequestFailed(Object obj) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(FindPswActivity.this,
                            R.string.msg_fail_to_send_sms_code,
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }).execute(Constants.getAbsUrl(Constants.HttpPath.POST_FOR_FORGET_PSW),
                    jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

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
