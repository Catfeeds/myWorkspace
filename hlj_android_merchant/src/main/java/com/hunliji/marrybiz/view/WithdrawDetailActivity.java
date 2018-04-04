package com.hunliji.marrybiz.view;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.Withdraw;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WithdrawDetailActivity extends HljBaseActivity {

    @BindView(R.id.tv_withdraw_1)
    TextView withdrawMoney1;
    @BindView(R.id.tv_withdraw_2)
    TextView withdrawMoney2;
    @BindView(R.id.tv_withdraw_total)
    TextView withdrawPendingTv;
    @BindView(R.id.tv_paid_refund)
    TextView refundTv;
    @BindView(R.id.tv_compensation)
    TextView compensationTv;
    @BindView(R.id.tv_return_redpacket)
    TextView returnRedPacketTv;
    @BindView(R.id.tv_withdraw_time)
    TextView withdrawTimeTv;
    @BindView(R.id.tv_status)
    TextView withdrawStatusTv;
    @BindView(R.id.progressBar)
    View progressBar;

    private Withdraw withdraw;
    private long withdrawId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_detail);
        ButterKnife.bind(this);

        withdrawId = getIntent().getLongExtra("id", 0);

        progressBar.setVisibility(View.VISIBLE);
        new GetWithdrawDetailInfoTask().execute(String.format(Constants.getAbsUrl(Constants
                .HttpPath.GET_WITHDRAW_DETAIL),
                withdrawId));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_anim_default, R.anim.slide_out_right);
    }

    @OnClick(R.id.withdraw_total_layout)
    void goWithdrawSubsList() {
        Intent intent = new Intent(this, SubWithdrawListActivity.class);
        intent.putExtra("withdraw", withdraw);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    private void setInfo() {
        if (withdraw != null) {
            withdrawMoney1.setText(Util.getIntegerPartFromDouble(withdraw.getTotalWithdrawMoney()));
            withdrawMoney2.setText(Util.getFloatPartFromDouble(withdraw.getTotalWithdrawMoney()));
            withdrawPendingTv.setText(Util.formatDouble2String(withdraw.getPendingMoney()));
            refundTv.setText(Util.formatDouble2String(0 - withdraw.getRefundMoney()));
            compensationTv.setText(Util.formatDouble2String(0 - withdraw.getCompensationMoney()));
            returnRedPacketTv.setText(Util.formatDouble2String(0 - withdraw
                    .getReturnRedPacketMoney()));
            withdrawTimeTv.setText(withdraw.getCreatedAt()
                    .toString(getString(R.string.format_date_type11)));
            withdrawStatusTv.setText(withdraw.getStatusStr());
        }
    }

    private class GetWithdrawDetailInfoTask extends AsyncTask<String, Integer, JSONObject> {


        @Override
        protected JSONObject doInBackground(String... params) {
            String url = params[0];
            try {
                String json = JSONUtil.getStringFromUrl(WithdrawDetailActivity.this, url);
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
                if (jsonObject.optJSONObject("status") != null && jsonObject.optJSONObject("status")
                        .optInt("RetCode", -1) == 0) {
                    withdraw = new Withdraw(jsonObject.optJSONObject("data"));
                    setInfo();
                } else {
                    Toast.makeText(WithdrawDetailActivity.this,
                            getString(R.string.msg_error_server),
                            Toast.LENGTH_SHORT)
                            .show();
                }
            } else {
                Toast.makeText(WithdrawDetailActivity.this,
                        getString(R.string.msg_error_server),
                        Toast.LENGTH_SHORT)
                        .show();
            }
            super.onPostExecute(jsonObject);
        }
    }
}
