package me.suncloud.marrymemo.view;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.ImageView;
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
import me.suncloud.marrymemo.task.AsyncBitmapDrawable;
import me.suncloud.marrymemo.task.ImageLoadTask;
import me.suncloud.marrymemo.task.NewHttpPostTask;
import me.suncloud.marrymemo.task.OnHttpRequestListener;
import me.suncloud.marrymemo.util.DialogUtil;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.LLPayer;
import me.suncloud.marrymemo.util.ScaleMode;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.widget.ClearableEditText;

public class LLPayIdentificationActivity extends HljBaseActivity {

    @BindView(R.id.tv_bank_name)
    TextView tvBankName;
    @BindView(R.id.tv_card_id)
    TextView tvCardId;
    @BindView(R.id.btn_next_step)
    Button btnNextStep;
    @BindView(R.id.et_card_holder)
    ClearableEditText etCardHolder;
    @BindView(R.id.et_id_card_number)
    ClearableEditText etIdCardNumber;
    @BindView(R.id.img_bank_logo)
    ImageView imgBankLogo;

    @BindView(R.id.tv_hint)
    TextView tvHint;
    private String bankCode;
    private String bankName;
    private String bankCardId;
    private LLPayer llPayer;
    private String bankLogoPath;
    private Dialog progressDialog;

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            btnNextStep.setEnabled(tvBankName.length() > 0 && tvCardId.length() > 0 &&
                    etCardHolder.length() > 0 && etIdCardNumber.length() > 0);
        }
    };
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            String retStr = (String) msg.obj;
            switch (msg.what) {
                case Constants.PayResultStatus.WHAT_SUCCESS:
                    Toast.makeText(LLPayIdentificationActivity.this, retStr, Toast.LENGTH_SHORT)
                            .show();
                    llPayer.goAfterPayActivity(LLPayIdentificationActivity.this);
                    finish();
                    break;
                case Constants.PayResultStatus.WHAT_FAIL:
                    Toast.makeText(LLPayIdentificationActivity.this, retStr, Toast.LENGTH_SHORT)
                            .show();
                    // 失败之后应该重新设置密码
                    pswmd5 = "";
                    btnNextStep.setEnabled(true);
                    break;
            }
            return false;
        }
    });
    private String pswmd5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_llpay_identification);
        ButterKnife.bind(this);
        tvCardId.addTextChangedListener(textWatcher);
        tvBankName.addTextChangedListener(textWatcher);
        etCardHolder.addTextChangedListener(textWatcher);
        etIdCardNumber.addTextChangedListener(textWatcher);

        bankCode = getIntent().getStringExtra("bank_code");
        bankName = getIntent().getStringExtra("bank_name");
        bankCardId = getIntent().getStringExtra("bank_card_id");
        bankLogoPath = getIntent().getStringExtra("bank_logo_path");

        llPayer = (LLPayer) getIntent().getSerializableExtra("payer");
        if (llPayer != null) {
            tvHint.setText(Html.fromHtml(getString(R.string.label_llpay_fee, llPayer.feeStr)));
        }

        tvBankName.setText(bankName);
        tvCardId.setText(bankCardId);
        String logoPath = JSONUtil.getImagePath(bankLogoPath, imgBankLogo.getLayoutParams().width);
        if (!JSONUtil.isEmpty(logoPath)) {
            imgBankLogo.setTag(logoPath);
            ImageLoadTask task = new ImageLoadTask(imgBankLogo, null, 0);
            task.loadImage(logoPath,
                    imgBankLogo.getLayoutParams().width,
                    ScaleMode.WIDTH,
                    new AsyncBitmapDrawable(getResources(), R.mipmap.icon_empty_image, task));
        } else {
            imgBankLogo.setImageBitmap(null);
        }
    }

    @OnClick(R.id.btn_next_step)
    void onNextStep() {
        if (etCardHolder.length() == 0) {
            Toast.makeText(this, R.string.msg_empty_card_holder, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (etIdCardNumber.length() == 0) {
            Toast.makeText(this, R.string.msg_empty_id_card, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (!Util.validIdStr(etIdCardNumber.getText()
                .toString())) {
            Toast.makeText(this, R.string.msg_invalid_id_number, Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        // 如果没有支付密码则先设置支付密码,否则直接支付(添加新卡的支付)
        if (llPayer.isBindNewCard) {
            // 支付
            pay();
        } else if (!TextUtils.isEmpty(pswmd5)) {
            // 已经设置过密码了
            pay();
        } else {
            // 先去设置密码
            llPayer.goSetPayPasswordActivity(this,
                    etCardHolder.getText()
                            .toString(),
                    etIdCardNumber.getText()
                            .toString());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == -1 && data != null) {
            switch (requestCode) {
                case Constants.RequestCode.SET_PAY_PASSWORD:
                    if (data != null) {
                        pswmd5 = data.getStringExtra("pswmd5");
                        if (!TextUtils.isEmpty(pswmd5)) {
                            // 成功设置了密码之后开始支付
                            pay();
                        }
                    }
                    break;
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void pay() {
        btnNextStep.setEnabled(false);
        try {
            final JSONObject jsonObject = new JSONObject(llPayer.jsonString);
            // 添加另外的两个必要参数user_name,user_idcard,和一个可选参数user_bankcard
            jsonObject.put("agent", "llpay");
            jsonObject.put("bank_name", bankName);
            jsonObject.put("user_name",
                    etCardHolder.getText()
                            .toString());
            jsonObject.put("user_idcard",
                    etIdCardNumber.getText()
                            .toString());
            jsonObject.put("user_bankcard", bankCardId);
            jsonObject.put("kind", 2);
            jsonObject.put("bank_code", bankCode);

            if (progressDialog == null) {
                progressDialog = DialogUtil.createProgressDialog(this);
            }
            progressDialog.show();
            // 请求支付信息
            new NewHttpPostTask(this, new OnHttpRequestListener() {
                @Override
                public void onRequestCompleted(Object obj) {
                    JSONObject resultObj = (JSONObject) obj;
                    if (resultObj != null && !resultObj.isNull("status")) {
                        ReturnStatus returnStatus = new ReturnStatus(resultObj.optJSONObject(
                                "status"));
                        if (returnStatus.getRetCode() == 0) {
                            // 请求成功
                            // 有的支付请求会给订单绑定红包
                            llPayer.afterBindRedPacket();
                            JSONObject dataObject = resultObj.optJSONObject("data");
                            if (dataObject != null) {
                                String freeOrderLink = JSONUtil.getString(dataObject,
                                        "free_order_link");
                                llPayer.setFreeOrderLink(freeOrderLink);
                                JSONObject shareObject = dataObject.optJSONObject("share");
                                String payParams = dataObject.optString("pay_params");
                                double fee = dataObject.optDouble("fee", 0);
                                if (fee <= 0) {
                                    // 零元支付,直接完成
                                    llPayer.zeroPay(LLPayIdentificationActivity.this,
                                            mHandler,
                                            shareObject,
                                            jsonObject);
                                } else {
                                    llPayer.securePay(LLPayIdentificationActivity.this,
                                            payParams,
                                            mHandler,
                                            shareObject);
                                }
                            } else {
                                // 请求失败
                                if (progressDialog != null) {
                                    progressDialog.dismiss();
                                }
                                Toast.makeText(LLPayIdentificationActivity.this,
                                        returnStatus.getErrorMsg(),
                                        Toast.LENGTH_SHORT)
                                        .show();
                            }
                        } else {
                            // 请求失败
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                            }
                            Toast.makeText(LLPayIdentificationActivity.this,
                                    returnStatus.getErrorMsg(),
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }
                    } else {
                        // 请求失败
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        Toast.makeText(LLPayIdentificationActivity.this,
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
                    Toast.makeText(LLPayIdentificationActivity.this,
                            R.string.msg_fail_to_get_pay_info,
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }).execute(llPayer.payUrl, jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        super.onPause();
    }
}
