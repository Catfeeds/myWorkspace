package com.hunliji.marrybiz.view;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.MerchantUser;
import com.hunliji.marrybiz.model.ReturnStatus;
import com.hunliji.marrybiz.task.NewHttpPostTask;
import com.hunliji.marrybiz.task.OnHttpRequestListener;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.Session;
import com.hunliji.marrybiz.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ApplyWithdrawActivity extends HljBaseActivity {

    @BindView(R.id.tv_account)
    TextView tvAccount;
    @BindView(R.id.tv_account_balance)
    TextView tvAccountBalance;
    @BindView(R.id.btn_apply_withdraw)
    Button btnApplyWithdraw;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.et_withdraw_amount)
    EditText etWithdrawAmount;
    private double balance;
    private double withdrawAmount;
    private String withdrawAccount;
    private Dialog hintDlg;
    private MerchantUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_withdraw);
        ButterKnife.bind(this);

        balance = getIntent().getDoubleExtra("balance", 0);
        withdrawAccount = getIntent().getStringExtra("withdraw_account");
        if (JSONUtil.isEmpty(withdrawAccount)) {
            withdrawAccount = getString(R.string.label_default_withdraw_account);
        }

        user = Session.getInstance()
                .getCurrentUser(this);

        tvAccountBalance.setText(getString(R.string.label_withdrawable_money2,
                Util.formatDouble2String(balance)));
        tvAccount.setText(withdrawAccount);

        etWithdrawAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String amountStr = etWithdrawAmount.getText()
                        .toString();
                try {
                    withdrawAmount = Double.valueOf(amountStr);
                } catch (NumberFormatException e) {
                    withdrawAmount = 0;
                    e.printStackTrace();
                } finally {
                    if (withdrawAmount > balance) {
                        tvAccountBalance.setText(R.string.msg_withdraw_amount_wrong);
                        tvAccountBalance.setTextColor(getResources().getColor(R.color
                                .colorPrimary));
                        btnApplyWithdraw.setEnabled(false);
                    } else if (withdrawAmount <= 0) {
                        tvAccountBalance.setText(getString(R.string.label_withdrawable_money2,
                                Util.formatDouble2String(balance)));
                        tvAccountBalance.setTextColor(getResources().getColor(R.color.colorBlack3));
                        btnApplyWithdraw.setEnabled(false);
                    } else {
                        tvAccountBalance.setText(getString(R.string.label_withdrawable_money2,
                                Util.formatDouble2String(balance)));
                        tvAccountBalance.setTextColor(getResources().getColor(R.color.colorBlack3));
                        btnApplyWithdraw.setEnabled(true);
                    }
                }
            }
        });
    }

    @OnClick(R.id.btn_apply_withdraw)
    void onApplyWithdraw() {
        if (user.getBankStatus() != MerchantUser.BANK_STATUS_SUCCESSED) {
            if (user.getBankStatus() == MerchantUser.BANK_STATUS_NO_APPLY) {
                hintDlg = DialogUtil.createSingleButtonDialog(this,
                        getString(R.string.msg_empty_withdraw_account),
                        getString(R.string.label_i_know),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                hintDlg.cancel();
                            }
                        });
                hintDlg.show();
            } else if (user.getBankStatus() == MerchantUser.BANK_STATUS_REVIEWING) {
                hintDlg = DialogUtil.createSingleButtonDialog(this,
                        getString(R.string.msg_withdraw_certify_2),
                        getString(R.string.label_i_know),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                hintDlg.cancel();
                            }
                        });
                hintDlg.show();
            } else if (user.getBankStatus() == MerchantUser.BANK_STATUS_FAILED) {
                hintDlg = DialogUtil.createSingleButtonDialog(this,
                        getString(R.string.msg_withdraw_certify_3),
                        getString(R.string.label_i_know),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                hintDlg.cancel();
                            }
                        });
                hintDlg.show();
            }
            return;
        }

        if (withdrawAmount < 1) {
            Toast.makeText(ApplyWithdrawActivity.this,
                    R.string.msg_withdraw_amount_wrong2,
                    Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("pending_money", withdrawAmount);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressBar.setVisibility(View.VISIBLE);
        new NewHttpPostTask(this, new OnHttpRequestListener() {
            @Override
            public void onRequestCompleted(Object obj) {
                progressBar.setVisibility(View.GONE);
                if (obj != null) {
                    JSONObject resultObject = (JSONObject) obj;
                    ReturnStatus returnStatus = new ReturnStatus(resultObject.optJSONObject
                            ("status"));

                    if (returnStatus.getRetCode() == 0) {
                        // 提现成功
                        Intent intent = new Intent(ApplyWithdrawActivity.this,
                                AfterWithdrawedActivity.class);
                        intent.putExtra("amount", withdrawAmount);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                        finish();
                    } else {
                        Toast.makeText(ApplyWithdrawActivity.this,
                                returnStatus.getErrorMsg(),
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                } else {
                    Toast.makeText(ApplyWithdrawActivity.this,
                            R.string.msg_fail_to_withdraw,
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onRequestFailed(Object obj) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ApplyWithdrawActivity.this,
                        R.string.msg_fail_to_withdraw,
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }).execute(Constants.getAbsUrl(Constants.HttpPath.APPLY_WITHDRAW), jsonObject.toString());
    }

}
