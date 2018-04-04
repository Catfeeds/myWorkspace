package com.hunliji.hljpaymentlibrary.views.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
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
import com.hunliji.hljpaymentlibrary.HljPay;
import com.hunliji.hljpaymentlibrary.R;
import com.hunliji.hljpaymentlibrary.R2;
import com.hunliji.hljpaymentlibrary.api.PaymentApi;
import com.hunliji.hljpaymentlibrary.models.LLPayer;
import com.hunliji.hljpaymentlibrary.models.PayRxEvent;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * 验证支付密码,然后进行支付或者重新设置支付密码
 */
public class VerificationPasswordActivity extends HljBaseActivity implements SafePasswordEditText
        .onSafeEditTextListener {

    @BindView(R2.id.safe_kb)
    SafeKeyboardView safeKb;
    @BindView(R2.id.tv_pay_money)
    TextView tvPayMoney;
    @BindView(R2.id.et_password_1)
    SafePasswordEditText etPassword1;
    private LLPayer llPayer;
    private String password;
    private Dialog errorDlg;
    private int type = 0; // 0:支付订单,验证密码后直接支付; 1: 修改密码,验证密码后跳转重置密码; 2:添加新的银行卡
    private Dialog progressDialog;

    public final int TYPE_VERIFY_PAY_PASSWORD = 0;
    public final int TYPE_VERIFY_RESET_PASSWORD = 1;
    public final int TYPE_VERIFY_BIND_NEW_CARD = 2;


    private Subscription paySubscriber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_password___pay);
        ButterKnife.bind(this);
        llPayer = getIntent().getParcelableExtra("llPayer");

        safeKb.setOnInputListener(etPassword1);
        etPassword1.setOnSafeEditTextListener(this);

        if (llPayer == null) {
            type = TYPE_VERIFY_RESET_PASSWORD;
        } else if (llPayer.getBindCardId() == 0) {
            type = TYPE_VERIFY_BIND_NEW_CARD;
        } else {
            type = TYPE_VERIFY_PAY_PASSWORD;
        }

        if (type == TYPE_VERIFY_RESET_PASSWORD) {
            setTitle(R.string.title_activity_reset_password___pay);
            tvPayMoney.setVisibility(View.GONE);
            findViewById(R.id.tv_forget_password).setVisibility(View.GONE);
        } else {
            tvPayMoney.setVisibility(View.VISIBLE);
            tvPayMoney.setText(CommonUtil.fromHtml(this,getString(R.string.html_fmt_llpay_money___pay),
                    llPayer.getPriceStr()));
        }
        paySubscriber = RxBus.getDefault()
                .toObservable(PayRxEvent.class)
                .subscribe(new RxBusSubscriber<PayRxEvent>() {
                    @Override
                    protected void onEvent(PayRxEvent payRxEvent) {
                        if (payRxEvent.getType() == PayRxEvent.RxEventType.PAY_SUCCESS ||
                                payRxEvent.getType() == PayRxEvent.RxEventType.PAY_FAIL) {
                            onBackPressed();
                        }
                    }
                });
    }

    @Override
    public void onInputComplete(String string) {
        password = CommonUtil.getMD5(string);
        onConfirmPayment();
    }

    @SuppressWarnings("unchecked")
    private void onConfirmPayment() {
        if (progressDialog == null) {
            progressDialog = DialogUtil.createProgressDialog(this);
        }
        PaymentApi.postCheckPassword(password)
                .subscribe(HljHttpSubscriber.
                        buildSubscriber(this)
                        .setProgressDialog(progressDialog)
                        .setOnNextListener(new SubscriberOnNextListener<HljHttpResult<Object>>() {
                            @Override
                            public void onNext(HljHttpResult<Object> result) {
                                HljHttpStatus status = result.getStatus();
                                if (status != null) {
                                    if (result.getStatus()
                                            .getRetCode() != 0) {
                                        showErrorDialog(result.getStatus());
                                    } else {
                                        if (type == TYPE_VERIFY_RESET_PASSWORD) {
                                            // 重新设置支付密码
                                            Intent intent = new Intent
                                                    (VerificationPasswordActivity.this,
                                                    SetPasswordActivity.class);
                                            intent.putExtra("type",
                                                    SetPasswordActivity.TYPE_RESET_PASSWORD);
                                            intent.putExtra("old_psw_md5", password);
                                            startActivity(intent);
                                            finish();
                                            overridePendingTransition(R.anim.slide_in_from_bottom,
                                                    R.anim.activity_anim_default);
                                        } else if (type == TYPE_VERIFY_BIND_NEW_CARD) {
                                            // 使用新卡支付,并绑定到现在的账户
                                            Intent intent = new Intent
                                                    (VerificationPasswordActivity.this,
                                                    AddBankCardActivity.class);
                                            intent.putExtra("llPayer", llPayer);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            // 直接开始使用绑定的卡支付
                                            onPay();

                                        }
                                    }
                                } else {
                                    ToastUtil.showToast(VerificationPasswordActivity.this,
                                            null,
                                            R.string.msg_verification_password_fail___pay);
                                }
                            }
                        })
                        .build());
    }

    private void showErrorDialog(HljHttpStatus returnStatus) {
        if (errorDlg != null && errorDlg.isShowing()) {
            return;
        }
        String confirmStr = null, cancelStr = null;
        View.OnClickListener cancelListener;
        View.OnClickListener confirmListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 去找回密码
                errorDlg.cancel();
                onForgetPassword();
            }
        };
        if (returnStatus.getRetCode() == 1001) {
            confirmStr = getString(R.string.label_forget_password___pay);
            cancelStr = getString(R.string.label_cancel___cm);
            cancelListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 取消支付
                    errorDlg.cancel();
                    onBackPressed();
                }
            };
        } else if (returnStatus.getRetCode() == 1002) {
            confirmStr = getString(R.string.label_find_password___pay);
            cancelStr = getString(R.string.label_input_again___pay);
            cancelListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 重新输入
                    errorDlg.cancel();
                    etPassword1.clearEditText();
                }
            };
        } else {
            cancelListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 出现其他未知错误类型,直接返回
                    errorDlg.cancel();
                    onBackPressed();
                }
            };
        }
        errorDlg = DialogUtil.createDoubleButtonDialog(this,
                returnStatus.getMsg(),
                confirmStr,
                cancelStr,
                confirmListener,
                cancelListener);
        errorDlg.show();
    }

    private void onPay() {
        JSONObject payParams = null;
        try {
            payParams = new JSONObject(llPayer.getPayParams());
            payParams.put("bank_id", String.valueOf(llPayer.getBindCardId()));
            payParams.put("pay_password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new HljPay.Builder(this).params(payParams)
                .path(llPayer.getPayPath())
                .price(llPayer.getPrice())
                .build()
                .onPayLLPay();
    }

    @Override
    public void onEditTextEmpty() {

    }

    @Override
    public void onTextChange(String string, boolean complete) {
    }

    @OnClick(R2.id.tv_forget_password)
    void onForgetPassword() {
        Intent intent = new Intent(this, BindCardListActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    @Override
    protected void onPause() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onFinish() {
        if (!paySubscriber.isUnsubscribed()) {
            paySubscriber.unsubscribe();
        }
        super.onFinish();
    }
}
