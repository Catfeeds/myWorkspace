package me.suncloud.marrymemo.view;

import android.app.Dialog;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import me.suncloud.marrymemo.util.DialogUtil;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.LLPayer;
import me.suncloud.marrymemo.widget.SafeKeyboardView;
import me.suncloud.marrymemo.widget.SafePasswordEditText;

/**
 * 验证支付密码,然后进行支付或者重新设置支付密码
 */
public class VerificationPayPasswordActivity extends HljBaseActivity implements
        SafePasswordEditText.onSafeEditTextListener {

    @BindView(R.id.safe_kb)
    SafeKeyboardView safeKb;
    @BindView(R.id.tv_pay_money)
    TextView tvPayMoney;
    @BindView(R.id.et_password_1)
    SafePasswordEditText etPassword1;
    private LLPayer llPayer;
    private String password;
    private Dialog errorDlg;
    private int type = 0; // 0:支付订单,验证密码后直接支付; 1: 修改密码,验证密码后跳转重置密码; 2:添加新的银行卡
    private Dialog progressDialog;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            String retStr = (String) msg.obj;
            switch (msg.what) {
                case Constants.PayResultStatus.WHAT_SUCCESS:
                    Toast.makeText(VerificationPayPasswordActivity.this, retStr, Toast.LENGTH_SHORT)
                            .show();
                    llPayer.goAfterPayActivity(VerificationPayPasswordActivity.this);
                    finish();
                    break;
                case Constants.PayResultStatus.WHAT_FAIL:
                    Toast.makeText(VerificationPayPasswordActivity.this, retStr, Toast.LENGTH_SHORT)
                            .show();
                    finish();
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_by_password);
        ButterKnife.bind(this);
        llPayer = (LLPayer) getIntent().getSerializableExtra("payer");
        type = getIntent().getIntExtra("type", 0);

        safeKb.setOnInputListener(etPassword1);
        etPassword1.setOnSafeEditTextListener(this);

        if (type == LLPayer.TYPE_VERIFY_RESET_PASSWORD) {
            setTitle(R.string.label_change_pay_password);
            tvPayMoney.setVisibility(View.GONE);
            findViewById(R.id.tv_forget_password).setVisibility(View.GONE);
        } else {
            tvPayMoney.setVisibility(View.VISIBLE);
            tvPayMoney.setText(Html.fromHtml(getString(R.string.label_llpay_money,
                    llPayer.feeStr)));
        }
    }

    @Override
    public void onInputComplete(String string) {
        password = string;
        onConfirmPayment();
    }

    private void onConfirmPayment() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("password", JSONUtil.getMD5(password));
            if (progressDialog == null) {
                progressDialog = DialogUtil.createProgressDialog(this);
            }
            progressDialog.show();
            new NewHttpPostTask(this, new OnHttpRequestListener() {
                @Override
                public void onRequestCompleted(Object obj) {
                    JSONObject resultObj = (JSONObject) obj;
                    if (resultObj != null && !resultObj.isNull("status")) {
                        ReturnStatus returnStatus = new ReturnStatus(resultObj.optJSONObject(
                                "status"));
                        if (returnStatus.getRetCode() == 0) {
                            // 请求成功
                            if (type == LLPayer.TYPE_VERIFY_RESET_PASSWORD) {
                                // 重新设置支付密码
                                llPayer.goResetPayPasswordActivity
                                        (VerificationPayPasswordActivity.this,
                                        LLPayer.TYPE_RESET_PASSWORD,
                                        JSONUtil.getMD5(password));
                            } else if (type == LLPayer.TYPE_VERIFY_BIND_NEW_CARD) {
                                // 使用新卡支付,并绑定到现在的账户
                                llPayer.goEnterBankCardId(VerificationPayPasswordActivity.this);
                                finish();
                            } else {
                                // 直接开始使用绑定的卡支付
                                onPay(JSONUtil.getMD5(password));
                            }
                        } else {
                            // 请求失败
                            // 显示不同的错误信息
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                            }
                            showErrorDialog(returnStatus);
                        }
                    } else {
                        // 请求失败
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        Toast.makeText(VerificationPayPasswordActivity.this,
                                R.string.msg_fail_to_verification_password,
                                Toast.LENGTH_SHORT)
                                .show();
                    }

                }

                @Override
                public void onRequestFailed(Object obj) {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(VerificationPayPasswordActivity.this,
                            R.string.msg_fail_to_verification_password,
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }).execute(Constants.getAbsUrl(Constants.HttpPath.LLPAY_CHECK_PASSWORD),
                    jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showErrorDialog(ReturnStatus returnStatus) {
        if (errorDlg != null && errorDlg.isShowing()) {
            return;
        }
        errorDlg = new Dialog(this, R.style.BubbleDialogTheme);
        View v = getLayoutInflater().inflate(R.layout.dialog_confirm, null);
        TextView msgAlertTv = (TextView) v.findViewById(R.id.tv_alert_msg);
        Button confirmBtn = (Button) v.findViewById(R.id.btn_confirm);
        Button cancelBtn = (Button) v.findViewById(R.id.btn_cancel);
        msgAlertTv.setText(returnStatus.getErrorMsg());
        if (returnStatus.getRetCode() == 1001) {
            // 支付密码错误已达到5次
            cancelBtn.setText(R.string.label_cancel);
            confirmBtn.setText(R.string.label_forget_password);
            confirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 去找回密码
                    errorDlg.cancel();
                    llPayer.findPayPassword(VerificationPayPasswordActivity.this);
                }
            });

            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 取消支付
                    errorDlg.cancel();
                    VerificationPayPasswordActivity.this.finish();
                }
            });
        } else if (returnStatus.getRetCode() == 1002) {
            // 支付密码错误
            cancelBtn.setText(R.string.label_enter_again);
            confirmBtn.setText(R.string.label_find_psw);
            confirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 去找回密码
                    errorDlg.cancel();
                    llPayer.findPayPassword(VerificationPayPasswordActivity.this);
                }
            });

            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 重新输入
                    errorDlg.cancel();
                    etPassword1.clearEditText();
                }
            });
        } else {
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 出现其他未知错误类型,直接返回
                    errorDlg.cancel();
                    VerificationPayPasswordActivity.this.finish();
                }
            });
            confirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 出现其他未知错误类型,直接返回
                    errorDlg.cancel();
                    llPayer.findPayPassword(VerificationPayPasswordActivity.this);
                }
            });
        }

        errorDlg.setContentView(v);
        Window window = errorDlg.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        Point point = JSONUtil.getDeviceSize(this);
        params.width = Math.round(point.x * 27 / 32);
        window.setAttributes(params);
        errorDlg.show();
    }

    private void onPay(String password) {
        try {
            final JSONObject jsonObject = new JSONObject(llPayer.jsonString);
            jsonObject.put("agent", "llpay");
            jsonObject.put("bank_id", String.valueOf(llPayer.bindCardId));
            jsonObject.put("pay_password", password);
            new NewHttpPostTask(this, new OnHttpRequestListener() {
                @Override
                public void onRequestCompleted(Object obj) {
                    JSONObject resultObj = (JSONObject) obj;
                    if (resultObj != null && !resultObj.isNull("status")) {
                        ReturnStatus returnStatus = new ReturnStatus(resultObj.optJSONObject(
                                "status"));
                        if (returnStatus.getRetCode() == 0) {
                            // 请求成功
                            JSONObject dataObject = resultObj.optJSONObject("data");
                            if (dataObject != null) {
                                String freeOrderLink = JSONUtil.getString(dataObject,
                                        "free_order_link");
                                llPayer.setFreeOrderLink(freeOrderLink);
                                String payParams = dataObject.optString("pay_params");
                                JSONObject shareObject = dataObject.optJSONObject("share");
                                double fee = dataObject.optDouble("fee", 0);
                                if (fee <= 0) {
                                    // 零元支付
                                    llPayer.zeroPay(VerificationPayPasswordActivity.this,
                                            mHandler,
                                            shareObject,
                                            jsonObject);
                                } else {
                                    llPayer.securePay(VerificationPayPasswordActivity.this,
                                            payParams,
                                            mHandler,
                                            shareObject);
                                }
                            } else {
                                // 请求失败
                                if (progressDialog != null) {
                                    progressDialog.dismiss();
                                }
                                Toast.makeText(VerificationPayPasswordActivity.this,
                                        R.string.msg_fail_to_get_pay_info,
                                        Toast.LENGTH_SHORT)
                                        .show();
                            }
                        } else {
                            // 请求失败
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                            }
                            Toast.makeText(VerificationPayPasswordActivity.this,
                                    returnStatus.getErrorMsg(),
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }

                    } else {
                        // 请求失败
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        Toast.makeText(VerificationPayPasswordActivity.this,
                                R.string.msg_fail_to_get_pay_info,
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                }

                @Override
                public void onRequestFailed(Object obj) {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(VerificationPayPasswordActivity.this,
                            R.string.msg_fail_to_get_pay_info,
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }

            ).execute(llPayer.payUrl, jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEditTextEmpty() {

    }

    @Override
    public void onTextChange(String string, boolean complete) {
    }

    @OnClick(R.id.tv_forget_password)
    void onForgetPassword() {
        llPayer.findPayPassword(this);
    }

    @Override
    protected void onPause() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        super.onPause();
    }
}
