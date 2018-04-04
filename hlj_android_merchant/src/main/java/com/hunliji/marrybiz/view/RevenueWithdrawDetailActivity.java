package com.hunliji.marrybiz.view;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.ReturnStatus;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.Util;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RevenueWithdrawDetailActivity extends HljBaseActivity {

    @BindView(R.id.tv_money1)
    TextView tvMoney1;
    @BindView(R.id.tv_money2)
    TextView tvMoney2;
    @BindView(R.id.tv_withdraw_account)
    TextView tvWithdrawAccount;
    @BindView(R.id.tv_withdraw_time)
    TextView tvWithdrawTime;
    @BindView(R.id.tv_withdraw_no)
    TextView tvWithdrawNo;
    @BindView(R.id.tv_withdraw_status)
    TextView tvWithdrawStatus;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private long id;
    private double withdrawMoney;
    private String withdrawType;
    private String withdrawAccount;
    private DateTime withdrawDateTime;
    private String withdrawNo;
    private int withdrawStatus;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revenue_withdraw_detail);
        ButterKnife.bind(this);

        id = getIntent().getLongExtra("id", 0);

        progressBar.setVisibility(View.VISIBLE);
        new GetWithdrawDetailTask().execute();
    }

    private class GetWithdrawDetailTask extends AsyncTask<String, Integer, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... strings) {
            String url = String.format(Constants.getAbsUrl(Constants.HttpPath
                    .GET_REVENUE_WITHDRAW_DETAIL),
                    id);
            try {
                String json = JSONUtil.getStringFromUrl(RevenueWithdrawDetailActivity.this, url);
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
            progressBar.setVisibility(View.GONE);
            if (jsonObject != null) {
                ReturnStatus returnStatus = new ReturnStatus(jsonObject.optJSONObject("status"));
                if (returnStatus.getRetCode() == 0) {
                    JSONObject dataObj = jsonObject.optJSONObject("data");
                    withdrawMoney = dataObj.optDouble("withdraw_money", 0);
                    withdrawAccount = JSONUtil.getString(dataObj, "withdraw_account");
                    if (JSONUtil.isEmpty(withdrawAccount)) {
                        withdrawAccount = getString(R.string.label_default_withdraw_account);
                    }

                    withdrawType = JSONUtil.getString(dataObj, "withdraw_type");
                    withdrawStatus = dataObj.optInt("withdraw_status");
                    String withdrawStatusType = JSONUtil.getString(dataObj, "withdraw_status_type");
                    withdrawNo = JSONUtil.getString(dataObj, "withdraw_trade_no");
                    withdrawDateTime = JSONUtil.getDateTime(dataObj, "withdraw_date");

                    tvMoney1.setText(Util.getIntegerPartFromDouble(withdrawMoney));
                    tvMoney2.setText(Util.getFloatPartFromDouble(withdrawMoney));
                    tvWithdrawAccount.setText(withdrawAccount);
                    tvWithdrawTime.setText(withdrawDateTime.toString(getString(R.string
                            .format_date_type11)));
                    tvWithdrawNo.setText(withdrawNo);
                    tvWithdrawStatus.setText(withdrawStatusType);
                } else {
                    Toast.makeText(RevenueWithdrawDetailActivity.this,
                            returnStatus.getErrorMsg(),
                            Toast.LENGTH_SHORT)
                            .show();
                }
            } else {
                Toast.makeText(RevenueWithdrawDetailActivity.this,
                        R.string.msg_fail_to_get_withdraw_detail,
                        Toast.LENGTH_SHORT)
                        .show();
            }
            super.onPostExecute(jsonObject);
        }
    }

}
