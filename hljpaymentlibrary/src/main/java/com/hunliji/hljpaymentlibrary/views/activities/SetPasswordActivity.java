package com.hunliji.hljpaymentlibrary.views.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.SafeKeyboardView;
import com.hunliji.hljcommonlibrary.views.widgets.SafePasswordEditText;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.entities.HljHttpStatus;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljpaymentlibrary.R;
import com.hunliji.hljpaymentlibrary.R2;
import com.hunliji.hljpaymentlibrary.api.PaymentApi;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;

/**
 * 设置支付密码界面
 * 有三种使用方式,首次设置,或者找回支付密码,或者修改(重置)支付密码
 */
public class SetPasswordActivity extends HljBaseActivity {

    @BindView(R2.id.safe_kb)
    SafeKeyboardView safeKb;
    //    StringBuilder sb;
    @BindView(R2.id.tv_hint_1)
    TextView tvHint1;
    @BindView(R2.id.et_password_1)
    SafePasswordEditText etPassword1;
    @BindView(R2.id.tv_hint_2)
    TextView tvHint2;
    @BindView(R2.id.et_password_2)
    SafePasswordEditText etPassword2;
    @BindView(R2.id.view_flipper)
    ViewFlipper viewFlipper;
    @BindView(R2.id.btn_finish)
    Button btnFinish;
    private int step = 1;
    private String firstPassword;
    private String secondPassword;
    private String extraPara;
    private Dialog dialog;
    private int type = 0; // 0:设置支付密码, 1:找回支付密码, 2:修改(重置)支付密码
    private String oldPswMd5;
    private String cardHolder;
    private String idNumber;
    private Dialog errorDlg;
    private Dialog progressDialog;

    private Subscriber<HljHttpResult<Object>> passwordSubscriber;

    public static final int TYPE_FIRST_SET_PASSWORD = 0;
    public static final int TYPE_FIND_PASSWORD = 1;
    public static final int TYPE_RESET_PASSWORD = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_password___pay);
        ButterKnife.bind(this);

        extraPara = getIntent().getStringExtra("extra_para");
        type = getIntent().getIntExtra("type", 0);
        cardHolder = getIntent().getStringExtra("card_holder");
        idNumber = getIntent().getStringExtra("id_number");
        if (type == TYPE_FIND_PASSWORD) {
            // 找回支付密码
            setTitle(R.string.title_activity_find_password___pay);
            tvHint1.setText(R.string.label_input_new_password___pay);
            tvHint2.setText(R.string.label_input_new_password_again___pay);
        } else if (type == TYPE_RESET_PASSWORD) {
            // 修改支付密码
            setTitle(R.string.title_activity_reset_password___pay);
            tvHint1.setText(R.string.label_input_new_password___pay);
            tvHint2.setText(R.string.label_input_new_password_again___pay);
            oldPswMd5 = getIntent().getStringExtra("old_psw_md5");
        }

        hideBackButton();
        setOkText(R.string.label_cancel___cm);
        setSwipeBackEnable(false);

        setFirstEditListener();
    }

    private void setFirstEditListener() {
        safeKb.setOnInputListener(etPassword1);
        etPassword1.setOnSafeEditTextListener(new SafePasswordEditText.onSafeEditTextListener() {
            @Override
            public void onInputComplete(String string) {
                // 如果是修改密码的话,第一次输入的密码需要与原密码对比
                if (type == TYPE_RESET_PASSWORD && TextUtils.equals(CommonUtil.getMD5(string),
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
        if (errorDlg != null && errorDlg.isShowing()) {
            return;
        }
        errorDlg = DialogUtil.createDoubleButtonDialog(this,
                getString(R.string.msg_password_no_change__pay),
                getString(R.string.label_input_again___pay),
                getString(R.string.label_cancel_reset___pay),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 重新输入
                        errorDlg.dismiss();
                        etPassword1.clearEditText();
                    }
                },
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 放弃修改
                        errorDlg.dismiss();
                        finish();

                    }
                });
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

    @SuppressWarnings("unchecked")
    @OnClick(R2.id.btn_finish)
    void onFinishSetPsw() {
        if (!TextUtils.equals(firstPassword, secondPassword)) {
            ToastUtil.showToast(this, null, R.string.msg_different_two_psw___pay);
            // 返回第一次重新输入
            showFirstPage();
        } else {
            // 开始提交设置密码
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("password", CommonUtil.getMD5(secondPassword));
            if (type == TYPE_RESET_PASSWORD) {
                jsonObject.addProperty("old_password", oldPswMd5);
            } else if (type == TYPE_FIND_PASSWORD) {
                jsonObject.add("res_data", new JsonParser().parse(extraPara));
            } else {
                jsonObject.addProperty("fullname", cardHolder);
                jsonObject.addProperty("id_number", idNumber);
            }
            if (progressDialog == null) {
                progressDialog = DialogUtil.createProgressDialog(this);
            }
            passwordSubscriber= HljHttpSubscriber.
                    buildSubscriber(this)
                    .setProgressDialog(progressDialog).setOnNextListener(new SubscriberOnNextListener<HljHttpResult<Object>>() {

                        @Override
                        public void onNext(HljHttpResult<Object> result) {
                            HljHttpStatus status = result.getStatus();
                            if (status != null && status.getRetCode() == 0) {
                                // 成功设置支付密码
                                ToastUtil.showToast(SetPasswordActivity.this,
                                        null,
                                        R.string.msg_set_password_success___pay);
                                if (type == TYPE_FIRST_SET_PASSWORD) {
                                    // 首次设置密码后
                                    Intent intent = getIntent();
                                    intent.putExtra("pswmd5", CommonUtil.getMD5(secondPassword));
                                    setResult(RESULT_OK, intent);
                                }
                                finish();
                                return;
                            }
                            ToastUtil.showToast(SetPasswordActivity.this,
                                    status == null ? null : status.getMsg(),
                                    R.string.msg_set_password_fail___pay);
                        }
                    }).build();
            switch (type) {
                case TYPE_FIRST_SET_PASSWORD:
                    PaymentApi.postSetPassword(jsonObject)
                            .subscribe(passwordSubscriber);
                    break;
                case TYPE_FIND_PASSWORD:
                    PaymentApi.postFindPassword(jsonObject)
                            .subscribe(passwordSubscriber);
                    break;
                case TYPE_RESET_PASSWORD:
                    PaymentApi.postResetPassword(jsonObject)
                            .subscribe(passwordSubscriber);
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        onOkButtonClick();
    }

    @Override
    public void onOkButtonClick() {
        // 退出设置,显示提示弹窗
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        String msgStr;
        String cancelStr;
        String confirmStr;

        if (type == TYPE_FIRST_SET_PASSWORD) {
            msgStr = getString(R.string.msg_new_password_exit___pay);
            confirmStr = getString(R.string.label_cancel_set_password___pay);
            cancelStr = getString(R.string.label_continue_set_password___pay);
        } else {
            msgStr = getString(R.string.msg_edit_password_exit___pay);
            confirmStr = getString(R.string.label_yes___pay);
            cancelStr = getString(R.string.label_no___pay);
        }
        dialog = DialogUtil.createDoubleButtonDialog(this,
                msgStr,
                confirmStr,
                cancelStr,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 返回
                        dialog.cancel();
                        finish();
                    }
                },null);
        dialog.show();
        super.onOkButtonClick();
    }

    @Override
    protected void onFinish() {
        if (passwordSubscriber != null && !passwordSubscriber.isUnsubscribed()) {
            passwordSubscriber.unsubscribe();
        }
        super.onFinish();
    }
}
