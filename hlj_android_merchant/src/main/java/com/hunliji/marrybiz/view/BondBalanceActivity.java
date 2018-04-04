package com.hunliji.marrybiz.view;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.suncloud.hljweblibrary.views.activities.HljWebViewActivity;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.MerchantUser;
import com.hunliji.marrybiz.model.ReturnStatus;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.Session;
import com.hunliji.marrybiz.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

// TODO: 2018/2/1 mo_yu
@Deprecated
public class BondBalanceActivity extends HljBaseActivity {

    @BindView(R.id.tv_alert_msg)
    TextView tvAlertMsg;
    @BindView(R.id.alert_layout)
    LinearLayout alertLayout;
    @BindView(R.id.tv_balance1)
    TextView tvBalance1;
    @BindView(R.id.tv_balance2)
    TextView tvBalance2;
    @BindView(R.id.btn_pay)
    Button btnPay;
    @BindView(R.id.bond_fee_detail_layout)
    LinearLayout bondFeeDetailLayout;
    @BindView(R.id.read_bond_fee_layout)
    LinearLayout readBondFeeLayout;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    private boolean isLoading;
    private ReturnStatus returnStatus;
    private double bondBalance;
    private boolean bondEnough;
    private MerchantUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bond_fee_balance);
        ButterKnife.bind(this);
        user = Session.getInstance()
                .getCurrentUser(this);

        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        new GetBondFeeBalanceTask().execute();
        super.onResume();
    }

    private void setViewValue() {
        tvBalance1.setText(Util.getIntegerPartFromDouble(bondBalance));
        tvBalance2.setText(Util.getFloatPartFromDouble(bondBalance));

        if (user.isBondPaid() && !user.isBondSign()) {
            // 余额不足超过限定期限,保证金权限过期
            alertLayout.setVisibility(View.VISIBLE);
            tvAlertMsg.setText(getString(R.string.msg_bond_fee_short_expire,
                    user.getBondMerchantExpireDays()));
        } else if (user.isBondPaid() && !bondEnough) {
            // 金额不足
            alertLayout.setVisibility(View.VISIBLE);
            tvAlertMsg.setText(R.string.msg_bond_fee_short);
        } else {
            alertLayout.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.bond_fee_detail_layout)
    void onBondFeeDetails() {
        if (returnStatus == null || returnStatus.getRetCode() != 0) {
            return;
        }

        Intent intent = new Intent(this, BondBalanceListActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    @OnClick(R.id.read_bond_fee_layout)
    void onReadBondFeePlan() {
        if (returnStatus == null || returnStatus.getRetCode() != 0) {
            return;
        }

        // 了解消费者保障计划页面
        Intent intent = new Intent(this, HljWebViewActivity.class);
        String url = Constants.getAbsUrl(Constants.HttpPath.BOND_PAY_WEB);
        if (user.isBondPaid()) {
            url = url + "#joined";
        }
        intent.putExtra("path", url);
        intent.putExtra("title", getString(R.string.label_bond_plan));
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    @OnClick(R.id.btn_pay)
    void onPay() {
        if (returnStatus == null || returnStatus.getRetCode() != 0) {
            return;
        }

        if (!user.isBondPaid()) {
            // 用户没有加入保证金计划,跳转到消费者保障计划页面
            Intent intent = new Intent(this, HljWebViewActivity.class);
            intent.putExtra("path", Constants.getAbsUrl(Constants.HttpPath.BOND_PAY_WEB));
            intent.putExtra("title", getString(R.string.label_bond_plan));
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        } else if (user.isBondPaid() && user.isBondSign() && bondEnough) {
            // 保证金充足无需缴纳
            Toast.makeText(this, R.string.msg_bond_enough, Toast.LENGTH_SHORT)
                    .show();
        } else {
            Intent intent = new Intent(this, BondPayActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

    private class GetBondFeeBalanceTask extends AsyncTask<String, Integer, JSONObject> {

        public GetBondFeeBalanceTask() {
            isLoading = true;
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            String url = Constants.getAbsUrl(Constants.HttpPath.GET_WITHDRAW_STATICS);
            try {
                String json = JSONUtil.getStringFromUrl(BondBalanceActivity.this, url);
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
            isLoading = false;
            progressBar.setVisibility(View.GONE);
            if (jsonObject != null) {
                returnStatus = new ReturnStatus(jsonObject.optJSONObject("status"));
                if (returnStatus.getRetCode() == 0) {
                    JSONObject dataObj = jsonObject.optJSONObject("data");
                    bondBalance = dataObj.optDouble("bond_fee", 0);
                    bondEnough = dataObj.optInt("bond_enough", 0) > 0;

                    setViewValue();
                } else {
                    Toast.makeText(BondBalanceActivity.this,
                            returnStatus.getErrorMsg(),
                            Toast.LENGTH_SHORT)
                            .show();
                }
            } else {
                Toast.makeText(BondBalanceActivity.this,
                        R.string.msg_fail_to_get_withdraw_statistics,
                        Toast.LENGTH_SHORT)
                        .show();
            }

            super.onPostExecute(jsonObject);
        }
    }

}
