package me.suncloud.marrymemo.view;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.ReturnStatus;
import me.suncloud.marrymemo.task.NewHttpPostTask;
import me.suncloud.marrymemo.task.OnHttpRequestListener;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.LLPayer;
import me.suncloud.marrymemo.widget.SafeKeyboardView;
import me.suncloud.marrymemo.widget.SafePasswordEditText;

/**
 * 设置支付密码界面
 * 有三种使用方式,首次设置,或者找回支付密码,或者修改(重置)支付密码
 */
public class SetPayPasswordActivity extends HljBaseActivity {

    @BindView(R.id.safe_kb)
    SafeKeyboardView safeKb;
    //    StringBuilder sb;
    @BindView(R.id.tv_hint_1)
    TextView tvHint1;
    @BindView(R.id.et_password_1)
    SafePasswordEditText etPassword1;
    @BindView(R.id.tv_hint_2)
    TextView tvHint2;
    @BindView(R.id.et_password_2)
    SafePasswordEditText etPassword2;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.view_flipper)
    ViewFlipper viewFlipper;
    @BindView(R.id.btn_finish)
    Button btnFinish;
    private int step = 1;
    private String firstPassword;
    private String secondPassword;
    private String extraPara;
    private Dialog dialog;
    private LLPayer llPayer;
    private int type = 0; // 0:设置支付密码, 1:找回支付密码, 2:修改(重置)支付密码
    private String postUrl;
    private String oldPswMd5;
    private String cardHolder;
    private String idNumber;
    private Dialog errorDlg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_pay_password);
        ButterKnife.bind(this);

        llPayer = (LLPayer) getIntent().getSerializableExtra("payer");
        extraPara = getIntent().getStringExtra("extra_para");
        type = getIntent().getIntExtra("type", 0);
        cardHolder = getIntent().getStringExtra("card_holder");
        idNumber = getIntent().getStringExtra("id_number");
        postUrl = Constants.getAbsUrl(Constants.HttpPath.LLPAY_SET_PAY_PASSWORD);
        if (type == LLPayer.TYPE_FIND_PASSWORD) {
            // 找回支付密码
            setTitle(R.string.title_activity_find_pay_password);
            tvHint1.setText(R.string.label_reset_pay_password);
            tvHint2.setText(R.string.label_reset_pay_password2);
            postUrl = Constants.getAbsUrl(Constants.HttpPath.LLPAY_FIND_PAY_PASSWORD);
        } else if (type == LLPayer.TYPE_RESET_PASSWORD) {
            // 修改支付密码
            setTitle(R.string.label_change_pay_password);
            tvHint1.setText(R.string.label_reset_pay_password);
            tvHint2.setText(R.string.label_reset_pay_password2);
            postUrl = Constants.getAbsUrl(Constants.HttpPath.LLPAY_RESET_PAY_PASSWORD);
            oldPswMd5 = getIntent().getStringExtra("old_psw_md5");
        }

        hideBackButton();
        setOkText(R.string.label_cancel);
        setSwipeBackEnable(false);

        setFirstEditListener();
    }

    private void setFirstEditListener() {
        safeKb.setOnInputListener(etPassword1);
        etPassword1.setOnSafeEditTextListener(new SafePasswordEditText.onSafeEditTextListener() {
            @Override
            public void onInputComplete(String string) {
                // 如果是修改密码的话,第一次输入的密码需要与原密码对比
                if (type == LLPayer.TYPE_RESET_PASSWORD && TextUtils.equals(JSONUtil.getMD5(string),
                        oldPswMd5)) {
                    // 原密码与新密码相同,需要提示
                    showSamePasswordAlert();
                } else {
                    showNextPage();
                    firstPassword = string;

                    step = 2;
                }
            }

            @Override
            public void onEditTextEmpty() {

            }

            @Override
            public void onTextChange(String string, boolean complete) {

            }
        });
    }

    private void setSecondEditListener() {
        safeKb.setOnInputListener(etPassword2);
        etPassword2.setOnSafeEditTextListener(new SafePasswordEditText.onSafeEditTextListener() {
            @Override
            public void onInputComplete(String string) {
                secondPassword = string;
                btnFinish.setEnabled(true);
            }

            @Override
            public void onEditTextEmpty() {

            }

            @Override
            public void onTextChange(String string, boolean complete) {

            }
        });
    }

    private void showSamePasswordAlert() {
        errorDlg = new Dialog(this, R.style.BubbleDialogTheme);
        View v = getLayoutInflater().inflate(R.layout.dialog_confirm, null);
        TextView msgAlertTv = (TextView) v.findViewById(R.id.tv_alert_msg);
        Button confirmBtn = (Button) v.findViewById(R.id.btn_confirm);
        Button cancelBtn = (Button) v.findViewById(R.id.btn_cancel);
        msgAlertTv.setText(R.string.msg_same_as_old_password);
        cancelBtn.setText(R.string.label_quit_reset);
        confirmBtn.setText(R.string.label_enter_again);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 放弃修改
                errorDlg.dismiss();
                finish();
            }
        });
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 重新输入
                errorDlg.dismiss();
                etPassword1.clearEditText();
            }
        });

        errorDlg.setContentView(v);
        Window window = errorDlg.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        Point point = JSONUtil.getDeviceSize(this);
        params.width = Math.round(point.x * 27 / 32);
        window.setAttributes(params);
        errorDlg.show();
    }

    private void showNextPage() {
        viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_right));
        viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_out_left));
        viewFlipper.showNext();
        etPassword2.clearEditText();
        setSecondEditListener();
        btnFinish.setEnabled(false);
    }

    private void showFirstPage() {
        viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_left));
        viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_out_right));
        viewFlipper.showPrevious();
        setFirstEditListener();
        etPassword1.clearEditText();
        step = 1;
        firstPassword = secondPassword = "";
        btnFinish.setEnabled(false);
    }

    @OnClick(R.id.btn_finish)
    void onFinishSetPsw() {
        if (!TextUtils.equals(firstPassword, secondPassword)) {
            Toast.makeText(this, R.string.msg_different_two_psw, Toast.LENGTH_SHORT)
                    .show();
            // 返回第一次重新输入
            showFirstPage();
        } else {
            // 开始提交设置密码
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("password", JSONUtil.getMD5(secondPassword));
                if (type == LLPayer.TYPE_RESET_PASSWORD) {
                    jsonObject.put("old_password", oldPswMd5);
                } else if (type == LLPayer.TYPE_FIND_PASSWORD) {
                    JSONObject extraData = new JSONObject(extraPara);
                    jsonObject.put("res_data", extraData);
                } else {
                    jsonObject.put("fullname", cardHolder);
                    jsonObject.put("id_number", idNumber);
                }
                progressBar.setVisibility(View.VISIBLE);
                new NewHttpPostTask(this, new OnHttpRequestListener() {
                    @Override
                    public void onRequestCompleted(Object obj) {
                        progressBar.setVisibility(View.GONE);
                        JSONObject resultObject = (JSONObject) obj;
                        if (resultObject != null && !resultObject.isNull("status")) {
                            ReturnStatus returnStatus = new ReturnStatus(resultObject.optJSONObject(
                                    "status"));
                            if (returnStatus.getRetCode() == 0) {
                                // 成功设置支付密码
                                Toast.makeText(SetPayPasswordActivity.this,
                                        R.string.msg_success_to_set_pay_password,
                                        Toast.LENGTH_SHORT)
                                        .show();

                                if (type == LLPayer.TYPE_FIND_PASSWORD || type == LLPayer
                                        .TYPE_RESET_PASSWORD) {
                                    // 找回/重置 密码后
                                    llPayer.backBeforeResetPassword(SetPayPasswordActivity.this);
                                } else {
                                    // 首次设置密码后
                                    Intent intent = getIntent();
                                    intent.putExtra("pswmd5", JSONUtil.getMD5(secondPassword));
                                    setResult(RESULT_OK, intent);
                                    finish();
                                }
                            } else {
                                Toast.makeText(SetPayPasswordActivity.this,
                                        returnStatus.getErrorMsg(),
                                        Toast.LENGTH_SHORT)
                                        .show();
                            }
                        } else {
                            Toast.makeText(SetPayPasswordActivity.this,
                                    R.string.msg_fail_set_pay_password,
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }

                    @Override
                    public void onRequestFailed(Object obj) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(SetPayPasswordActivity.this,
                                R.string.msg_fail_set_pay_password,
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                }).execute(postUrl, jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    public void onOkButtonClick() {
        // 退出设置,显示提示弹窗
        if (dialog != null && dialog.isShowing()) {
            return;
        }

        if (dialog == null) {
            dialog = new Dialog(this, R.style.BubbleDialogTheme);
            View v = getLayoutInflater().inflate(R.layout.dialog_confirm, null);
            TextView msgAlertTv = (TextView) v.findViewById(R.id.tv_alert_msg);
            Button confirmBtn = (Button) v.findViewById(R.id.btn_confirm);
            Button cancelBtn = (Button) v.findViewById(R.id.btn_cancel);
            if (type == 0) {
                msgAlertTv.setText(R.string.msg_cancel_set_pay_password);
                cancelBtn.setText(R.string.label_cancel_set_pay_password);
                confirmBtn.setText(R.string.label_continue_set_pay_password);
            } else {
                msgAlertTv.setText(R.string.msg_cancel_set_pay_password2);
                cancelBtn.setText(R.string.label_yes);
                confirmBtn.setText(R.string.label_no);
            }

            confirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 继续设置
                    dialog.cancel();
                }
            });

            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 返回
                    if (type == 1) {
                        llPayer.backBeforeResetPassword(SetPayPasswordActivity.this);
                    } else {
                        finish();
                    }
                }
            });
            dialog.setContentView(v);
            Window window = dialog.getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            Point point = JSONUtil.getDeviceSize(this);
            params.width = Math.round(point.x * 27 / 32);
            window.setAttributes(params);
        }

        dialog.show();
        super.onOkButtonClick();
    }
}
