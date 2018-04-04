package com.hunliji.hljpaymentlibrary.views.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;

import com.example.suncloud.hljweblibrary.HljWeb;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.hunliji.hljcommonlibrary.models.BankCard;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.ClearableEditText;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljpaymentlibrary.HljPay;
import com.hunliji.hljpaymentlibrary.R;
import com.hunliji.hljpaymentlibrary.R2;
import com.hunliji.hljpaymentlibrary.api.PaymentApi;
import com.hunliji.hljpaymentlibrary.models.LLPaySecurePayer;
import com.hunliji.hljpaymentlibrary.models.PayResult;
import com.hunliji.hljpaymentlibrary.models.PayRxEvent;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

public class FindPasswordActivity extends HljBaseActivity {

    @BindView(R2.id.et_card_holder)
    ClearableEditText etCardHolder;
    @BindView(R2.id.et_id_card_number)
    ClearableEditText etIdCardNumber;
    @BindView(R2.id.btn_next_step)
    Button btnNextStep;
    @BindView(R2.id.et_card_id)
    ClearableEditText etCardId;
    private BankCard userBindBankCard;
    private Dialog progressDialog;
    private Subscription restSubscriber;
    private Subscription paySubscriber;

    public final String LLPAY_AGREEMENT_URL = "p/wedding/Public/wap/activity/paycol.html";
    public final String LLPAY_PAY_A_PENY = "p/wedding/index.php/Home/APIUserSecurity/pay";

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            btnNextStep.setEnabled(etCardId.length() > 0 && etIdCardNumber.length() > 0 &&
                    etCardHolder.length() > 0 && etIdCardNumber.length() > 0);
        }
    };

    private TextWatcher textWatcher2 = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            etCardId.removeTextChangedListener(textWatcher2);
            etCardId.setText(addBlank(removeBlank(s.toString())));
            etCardId.setSelection(etCardId.getText()
                    .length());
            etCardId.addTextChangedListener(textWatcher2);
            btnNextStep.setEnabled(etCardId.length() > 0 && etIdCardNumber.length() > 0 &&
                    etCardHolder.length() > 0 && etIdCardNumber.length() > 0);
        }
    };

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case HljPay.PayResultCode.LL_PAY:
                    try {
                        String retStr = (String) msg.obj;
                        JSONObject contentObj = new JSONObject(retStr);
                        String retCode = contentObj.optString("ret_code");
                        String retMsg = contentObj.optString("ret_msg");
                        String resultPay = contentObj.optString("result_pay");
                        if ("0000".equals(retCode) && "SUCCESS".equals(resultPay)) {
                            ToastUtil.showToast(FindPasswordActivity.this,
                                    retMsg,
                                    R.string.msg_pay_success___pay);
                            Intent intent = new Intent(FindPasswordActivity.this,
                                    SetPasswordActivity.class);
                            intent.putExtra("type", SetPasswordActivity.TYPE_FIND_PASSWORD);
                            intent.putExtra("extra_para", retStr);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_from_bottom,
                                    R.anim.activity_anim_default);
                            RxBus.getDefault()
                                    .post(new PayRxEvent(PayRxEvent.RxEventType.RESET_PASSWORD,
                                            null));
                        } else {
                            ToastUtil.showToast(FindPasswordActivity.this,
                                    retMsg,
                                    R.string.msg_pay_fail___pay);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password___pay);
        ButterKnife.bind(this);
        userBindBankCard = getIntent().getParcelableExtra("user_bind_card");

        if (userBindBankCard != null) {
            etCardId.setHint(userBindBankCard.getBankName() + "(" + userBindBankCard.getAccount()
                    + ")");
            etCardHolder.setHint(userBindBankCard.getShortName() + "(请输入完整姓名)");
        }

        etIdCardNumber.addTextChangedListener(textWatcher);
        etCardHolder.addTextChangedListener(textWatcher);
        etCardId.addTextChangedListener(textWatcher2);
        restSubscriber = RxBus.getDefault()
                .toObservable(PayRxEvent.class)
                .subscribe(new RxBusSubscriber<PayRxEvent>() {
                    @Override
                    protected void onEvent(PayRxEvent payRxEvent) {
                        if (payRxEvent.getType() == PayRxEvent.RxEventType.RESET_PASSWORD) {
                            finish();
                        }
                    }
                });
    }

    @OnClick(R2.id.btn_next_step)
    void onNextStep() {
        if (etCardId.length() == 0) {
            ToastUtil.showToast(this, null, R.string.msg_card_id_empty___pay);
            return;
        }
        if (etCardHolder.length() == 0) {
            ToastUtil.showToast(this, null, R.string.msg_card_holder_empty___pay);
            return;
        }
        if (etIdCardNumber.length() == 0) {
            ToastUtil.showToast(this, null, R.string.msg_id_card_empty___pay);
            return;
        }
        if (!CommonUtil.validIdStr(etIdCardNumber.getText()
                .toString())) {
            ToastUtil.showToast(this, null, R.string.msg_id_card_err___pay);
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("bank_id", userBindBankCard.getId());
            jsonObject.put("kind", 2);
            jsonObject.put("user_name",
                    etCardHolder.getText()
                            .toString());
            jsonObject.put("user_bankcard",
                    removeBlank(etCardId.getText()
                            .toString()));
            jsonObject.put("user_idcard",
                    etIdCardNumber.getText()
                            .toString());
            if (progressDialog == null) {
                progressDialog = DialogUtil.createProgressDialog(this);
            }

            paySubscriber = PaymentApi.getPayParams(LLPAY_PAY_A_PENY, jsonObject)
                    .subscribe(HljHttpSubscriber.
                            buildSubscriber(this)
                            .setProgressDialog(progressDialog)
                            .setOnNextListener(new SubscriberOnNextListener<JsonElement>() {
                                @Override
                                public void onNext(JsonElement jsonElement) {
                                    try {
                                        PayResult payResult = new Gson().fromJson(jsonElement,
                                                PayResult.class);
                                        if (payResult != null) {
                                            final String orderInfo = payResult.getPayParams();
                                            if (!TextUtils.isEmpty(orderInfo)) {
                                                new LLPaySecurePayer().pay(orderInfo,
                                                        mHandler,
                                                        HljPay.PayResultCode.LL_PAY,
                                                        FindPasswordActivity.this,
                                                        false);
                                                return;

                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    ToastUtil.showToast(FindPasswordActivity.this,
                                            null,
                                            R.string.msg_pay_params_fail___pay);
                                }
                            })
                            .build());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private String addBlank(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            if (i != 0 && i % 4 == 0) {
                sb.append("  ");
            }
            sb.append(s.charAt(i));
        }

        return sb.toString();
    }

    private String removeBlank(String s) {
        return s.replaceAll(" ", "");
    }

    @OnClick(R2.id.agreement_layout)
    void onAgreementLayout() {
        HljWeb.startWebView(this, HljHttp.getHOST() + LLPAY_AGREEMENT_URL);
    }

    @Override
    protected void onPause() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        super.onPause();
    }

    @Override
    protected void onFinish() {
        if (restSubscriber != null && !restSubscriber.isUnsubscribed()) {
            restSubscriber.unsubscribe();
        }
        if (paySubscriber != null && !paySubscriber.isUnsubscribed()) {
            paySubscriber.unsubscribe();
        }
        super.onFinish();
    }
}
