package me.suncloud.marrymemo.view;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.suncloud.hljweblibrary.views.activities.HljWebViewActivity;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.ReturnStatus;
import me.suncloud.marrymemo.util.DialogUtil;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.LLPayer;
import me.suncloud.marrymemo.widget.ClearableEditText;


public class EnterBankCardIDActivity extends HljBaseActivity {

    @BindView(R.id.btn_next_step)
    Button btnNextStep;
    @BindView(R.id.agreement_layout)
    LinearLayout agreementLayout;
    @BindView(R.id.et_card_id)
    ClearableEditText etCardId;
    private String cardId;
    private boolean isDelete = false;
    private int preLength;
    private LLPayer llPayer;
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            btnNextStep.setEnabled(s.length() > 0);
            if (s.length() < preLength) {
                isDelete = true;
            } else {
                isDelete = false;
            }
            preLength = s.length();

            if (!isDelete) {
                etCardId.removeTextChangedListener(textWatcher);
                etCardId.setText(addBlank(removeBlank(s.toString())));
                etCardId.setSelection(etCardId.getText()
                        .length());
                etCardId.addTextChangedListener(textWatcher);
            }
        }
    };

    private Dialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_bank_card_id);
        ButterKnife.bind(this);

        llPayer = (LLPayer) getIntent().getSerializableExtra("payer");

        etCardId.addTextChangedListener(textWatcher);
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

    @OnClick(R.id.btn_next_step)
    void onNextStep() {
        // 查询卡bin信息,成功之后跳转下一步
        if (progressDialog == null) {
            progressDialog = DialogUtil.createProgressDialog(this);
        }
        progressDialog.show();
        cardId = removeBlank(etCardId.getText()
                .toString());
        new GetBankCardBinTask().execute(Constants.getAbsUrl(String.format(Constants.HttpPath
                        .BANK_CARD_BIN,
                cardId)));
    }

    private class GetBankCardBinTask extends AsyncTask<String, Object, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            String url = params[0];
            try {
                String json = JSONUtil.getStringFromUrl(url);
                if (JSONUtil.isEmpty(json)) {
                    return null;
                }

                return new JSONObject(json);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            if (jsonObject != null) {
                ReturnStatus returnStatus = new ReturnStatus(jsonObject.optJSONObject("status"));
                if (returnStatus.getRetCode() == 0 && jsonObject.optJSONObject("data") != null) {
                    // 获取成功
                    JSONObject dataObj = jsonObject.optJSONObject("data");
                    String bankCode = dataObj.optString("bank_code");
                    String bankName = dataObj.optString("bank_name");
                    String bankLogoPath = dataObj.optString("logo");
                    if (!JSONUtil.isEmpty(bankName)) {
                        llPayer.goLLPayIdentification(EnterBankCardIDActivity.this,
                                bankName,
                                bankCode,
                                cardId,
                                bankLogoPath);
                    } else {
                        Toast.makeText(EnterBankCardIDActivity.this,
                                R.string.msg_fail_to_get_card_bin,
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                } else {
                    Toast.makeText(EnterBankCardIDActivity.this,
                            R.string.msg_fail_to_get_card_bin,
                            Toast.LENGTH_SHORT)
                            .show();
                }
            } else {
                Toast.makeText(EnterBankCardIDActivity.this,
                        R.string.msg_fail_to_get_card_bin,
                        Toast.LENGTH_SHORT)
                        .show();
            }
            super.onPostExecute(jsonObject);
        }
    }

    @OnClick(R.id.support_cards_layout)
    void onSupportCards() {
        Intent intent = new Intent(this, SupportBankCardsListActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    @OnClick(R.id.agreement_layout)
    void onAgreementLayout() {
        Intent intent = new Intent(this, HljWebViewActivity.class);
        intent.putExtra("path", Constants.getAbsUrl(Constants.HttpPath.LLPAY_AGREEMENT_URL));
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }
}
