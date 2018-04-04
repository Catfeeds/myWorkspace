package me.suncloud.marrymemo.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.suncloud.hljweblibrary.views.activities.HljWebViewActivity;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.ReturnStatus;
import me.suncloud.marrymemo.model.UserBindBankCard;
import me.suncloud.marrymemo.task.StatusHttpPostTask;
import me.suncloud.marrymemo.task.StatusRequestListener;
import me.suncloud.marrymemo.util.LLPayer;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.widget.ClearableEditText;

public class FindPayPasswordActivity extends HljBaseActivity {

    @BindView(R.id.et_card_holder)
    ClearableEditText etCardHolder;
    @BindView(R.id.et_id_card_number)
    ClearableEditText etIdCardNumber;
    @BindView(R.id.btn_next_step)
    Button btnNextStep;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.et_card_id)
    ClearableEditText etCardId;
    private LLPayer llPayer;
    private UserBindBankCard userBindBankCard;
    private boolean isDelete = false;
    private int preLength;
    private String cardId;
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
            progressBar.setVisibility(View.GONE);
            String retStr = (String) msg.obj;
            switch (msg.what) {
                case Constants.PayResultStatus.WHAT_SUCCESS:
                    Toast.makeText(FindPayPasswordActivity.this, retStr, Toast.LENGTH_SHORT)
                            .show();
                    llPayer.goFindPayPasswordActivity(FindPayPasswordActivity.this);
                    break;
                case Constants.PayResultStatus.WHAT_FAIL:
                    Toast.makeText(FindPayPasswordActivity.this, retStr, Toast.LENGTH_SHORT)
                            .show();
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pay_password);
        ButterKnife.bind(this);
        llPayer = (LLPayer) getIntent().getSerializableExtra("payer");
        userBindBankCard = (UserBindBankCard) getIntent().getSerializableExtra("user_bind_card");

        if (userBindBankCard != null) {
            etCardId.setHint(userBindBankCard.getBankName() + "(" + userBindBankCard.getAccount()
                    + ")");
            etCardHolder.setHint(userBindBankCard.getShortName() + "(请输入完整姓名)");
        }

        etIdCardNumber.addTextChangedListener(textWatcher);
        etCardHolder.addTextChangedListener(textWatcher);
        etCardId.addTextChangedListener(textWatcher2);
    }

    @OnClick(R.id.btn_next_step)
    void onNextStep() {
        if (etCardId.length() == 0) {
            Toast.makeText(this, R.string.msg_empty_card_id, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
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

        cardId = removeBlank(etCardId.getText()
                .toString());
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("bank_id", userBindBankCard.getId());
            jsonObject.put("kind", 2);
            jsonObject.put("user_name",
                    etCardHolder.getText()
                            .toString());
            jsonObject.put("user_bankcard", cardId);
            jsonObject.put("user_idcard",
                    etIdCardNumber.getText()
                            .toString());

            progressBar.setVisibility(View.VISIBLE);
            new StatusHttpPostTask(this, new StatusRequestListener() {
                @Override
                public void onRequestCompleted(Object object, ReturnStatus returnStatus) {
                    progressBar.setVisibility(View.GONE);
                    if (returnStatus.getRetCode() == 0) {
                        JSONObject dataObject = (JSONObject) object;
                        String payParams = dataObject.optString("pay_params");
                        double fee = dataObject.optDouble("fee", 0);
                        llPayer.securePay(FindPayPasswordActivity.this, payParams, mHandler, null);
                    } else {
                        Toast.makeText(FindPayPasswordActivity.this,
                                returnStatus.getErrorMsg(),
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                }

                @Override
                public void onRequestFailed(ReturnStatus returnStatus, boolean network) {
                    progressBar.setVisibility(View.GONE);
                    Util.postFailToast(FindPayPasswordActivity.this,
                            returnStatus,
                            R.string.msg_fail_to_get_pay_info,
                            network);
                }
            }).execute(Constants.getAbsUrl(Constants.HttpPath.LLPAY_PAY_A_PENY),
                    jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private String addBlank(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            if (i != 0 && i % 4 == 0) {
                sb.append(getString(R.string.label_blank));
            }
            sb.append(s.charAt(i));
        }

        return sb.toString();
    }

    private String removeBlank(String s) {
        return s.replaceAll(getString(R.string.label_blank2), "");
    }

    @OnClick(R.id.agreement_layout)
    void onAgreementLayout() {
        Intent intent = new Intent(this, HljWebViewActivity.class);
        intent.putExtra("path", Constants.getAbsUrl(Constants.HttpPath.LLPAY_AGREEMENT_URL));
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }
}
