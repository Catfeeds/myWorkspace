package com.hunliji.marrybiz.view;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.suncloud.hljweblibrary.views.activities.HljWebViewActivity;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.MerchantUser;
import com.hunliji.marrybiz.task.OnHttpRequestListener;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.LinkUtil;
import com.hunliji.marrybiz.util.Session;
import com.hunliji.marrybiz.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RevenueActivity extends HljBaseActivity {

    @BindView(R.id.tv_withdrawable_1)
    TextView withdrawableTv1;
    @BindView(R.id.tv_withdrawable_2)
    TextView withdrawableTv2;
    @BindView(R.id.tv_unwithdraw)
    TextView unwithdrawTv;
    @BindView(R.id.tv_refund)
    TextView refundTv;
    @BindView(R.id.tv_compensation)
    TextView compensationTv;
    @BindView(R.id.bottom_layout)
    View bottomLayout;
    @BindView(R.id.btn_withdraw)
    Button withdrawButton;
    @BindView(R.id.progressBar)
    View progressBar;
    @BindView(R.id.tv_withdrawed_money_1)
    TextView tvWithdrawedMoney1;
    @BindView(R.id.tv_withdrawed_money_2)
    TextView tvWithdrawedMoney2;
    @BindView(R.id.tv_return_redpacket)
    TextView tvReturnRedpacket;
    @BindView(R.id.tv_total_income)
    TextView tvTotalIncome;

    private double incomeSummary;
    private double withdrawed;
    private double unWithdraw;
    private double withdrawable;
    private double refund;
    private double compensation;
    private double returnRedPacket;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revenue);
        ButterKnife.bind(this);

        setOkText(R.string.label_nomal_question);

        progressBar.setVisibility(View.VISIBLE);
        MerchantUser user = Session.getInstance()
                .getCurrentUser(this);
        withdrawButton.setEnabled(user.getExamine() == 1 && user.getCertifyStatus() == 3);
        new GetRevenueStaticsTask().execute();
    }

    private void setNumbers() {
        withdrawableTv1.setText(Util.getIntegerPartFromDouble(withdrawable));
        withdrawableTv2.setText(Util.getFloatPartFromDouble(withdrawable));

        tvWithdrawedMoney1.setText(Util.getIntegerPartFromDouble(withdrawed));
        tvWithdrawedMoney2.setText(Util.getFloatPartFromDouble(withdrawed));

        tvTotalIncome.setText(Util.formatDouble2String(incomeSummary));

        unwithdrawTv.setText(Util.formatDouble2String(unWithdraw));
        tvReturnRedpacket.setText(Util.formatDouble2String(returnRedPacket));

        compensationTv.setText(Util.formatDouble2String(compensation));
        refundTv.setText(Util.formatDouble2String(refund));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_anim_default, R.anim.slide_out_right);
    }

    @Override
    public void onOkButtonClick() {
        super.onOkButtonClick();
        progressBar.setVisibility(View.VISIBLE);
        LinkUtil.getInstance(this)
                .getLink(Constants.LinkNames.MERCHANT_HELP, new OnHttpRequestListener() {
                    @Override
                    public void onRequestCompleted(Object obj) {
                        progressBar.setVisibility(View.GONE);
                        String url = (String) obj;
                        if (!JSONUtil.isEmpty(url)) {
                            gotoWebView(1, url);
                        }
                    }

                    @Override
                    public void onRequestFailed(Object obj) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    @OnClick(R.id.btn_withdraw)
    void onWithdraw() {
        Intent intent;
        // 没有交保证金的话先获取非保证金商家宣传页的url,进入常规提现页,显示特殊提示
        MerchantUser user = Session.getInstance()
                .getCurrentUser(this);
        if (user != null && user.getId() > 0 && user.isBondPaid() && !user.isBondSign()) {
            // 已申请,但未通过审核
            if (dialog != null && dialog.isShowing()) {
                return;
            }
            dialog = new Dialog(this, R.style.BubbleDialogTheme);
            dialog.setCancelable(false);
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_msg_single_button, null);
            TextView titleView = (TextView) dialogView.findViewById(R.id.dialog_msg_title);
            TextView contentView = (TextView) dialogView.findViewById(R.id.dialog_msg_content);
            titleView.setVisibility(View.GONE);
            contentView.setText(R.string.hint_bond_sign2);
            Button confirmView = (Button) dialogView.findViewById(R.id.dialog_msg_confirm);
            confirmView.setText(R.string.label_i_know);
            confirmView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });

            dialog.setContentView(dialogView);
            Window window = dialog.getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            Point point = JSONUtil.getDeviceSize(this);
            params.width = Math.round(point.x * 5 / 7);
            params.height = Math.round(params.width * 256 / 380);
            window.setAttributes(params);
            dialog.show();
        } else if (user != null && user.getId() > 0 && user.isBondSign()) {
            intent = new Intent(this, WithdrawableListActivity.class);
            intent.putExtra("refund_money", refund);
            intent.putExtra("compensation_money", compensation);
            startActivityForResult(intent, Constants.RequestCode.WITHDRAW);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        } else {
            intent = new Intent(this, WithdrawableListActivity.class);
            StringBuilder url = new StringBuilder(Constants.getAbsUrl(Constants.HttpPath
                    .BOND_PAY_WEB));
            if (user != null && user.isBondPaid()) {
                url.append("#joined");
            }
            intent.putExtra("url", url.toString());
            intent.putExtra("title", getString(R.string.label_bond_plan));
            startActivityForResult(intent, Constants.RequestCode.WITHDRAW);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

    @OnClick(R.id.income_layout)
    void goIncomeList() {
        Intent intent = new Intent(this, IncomeListActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    @OnClick(R.id.withdrawed_layout)
    void goWithdrawed() {
        Intent intent = new Intent(this, WithdrawRecordListActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    @OnClick(R.id.refund_layout)
    void goRefund() {
        Intent intent = new Intent(this, RefundCompensationListActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    @OnClick(R.id.compensation_layout)
    void goCompensation() {
        Intent intent = new Intent(this, RefundCompensationListActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    @OnClick(R.id.return_redpacket_layout)
    void goReturnRedPackets() {
        Intent intent = new Intent(this, ReturnRedPacketListActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.RequestCode.WITHDRAW:
                    if (data != null && data.getBooleanExtra("withdrawed", false)) {
                        // 刷新
                        progressBar.setVisibility(View.VISIBLE);
                        new GetRevenueStaticsTask().execute();
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void gotoWebView(int type, String url) {
        Intent intent = new Intent(this, HljWebViewActivity.class);
        intent.putExtra("path", url);
        intent.putExtra("title", getString(R.string.label_faq));
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    private class GetRevenueStaticsTask extends AsyncTask<String, Integer, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            String url = Constants.getAbsUrl(Constants.HttpPath.GET_REVENUE_STATICS);
            try {
                String json = JSONUtil.getStringFromUrl(RevenueActivity.this, url);
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
            // 更新用户信息
            new GetMerchantInfoTask().executeOnExecutor(Constants.INFOTHEADPOOL,
                    Constants.getAbsUrl(Constants.HttpPath.POST_MERCHANTS_INFO));
            super.onPostExecute(jsonObject);
            if (jsonObject != null) {
                if (jsonObject.optJSONObject("status") != null && jsonObject.optJSONObject("status")
                        .optInt("RetCode", -1) == 0) {
                    JSONObject dataObject = jsonObject.optJSONObject("data");
                    if (dataObject != null) {
                        incomeSummary = dataObject.optDouble("income_summary", 0);
                        withdrawed = dataObject.optDouble("withdraw_yes", 0);
                        withdrawable = dataObject.optDouble("withdraw_ability", 0);
                        unWithdraw = dataObject.optDouble("withdraw_no", 0);
                        refund = dataObject.optDouble("need_refund", 0);
                        compensation = dataObject.optDouble("need_indemnity", 0);
                        returnRedPacket = dataObject.optDouble("need_redpacket", 0);

                        setNumbers();
                    } else {
                        Toast.makeText(RevenueActivity.this,
                                getString(R.string.msg_error_server),
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                } else {
                    Toast.makeText(RevenueActivity.this,
                            getString(R.string.msg_error_server),
                            Toast.LENGTH_SHORT)
                            .show();
                }
            } else {
                Toast.makeText(RevenueActivity.this,
                        getString(R.string.msg_error_server),
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
    //    private class GetLinksTask extends AsyncTask<String, Integer, JSONObject> {
    //
    //        private int type;
    //
    //        public GetLinksTask(int type) {
    //            this.type = type;
    //        }
    //
    //        @Override
    //        protected JSONObject doInBackground(String... params) {
    //            String url = Constants.getAbsUrl(Constants.HttpPath.GET_LINKS_URL);
    //            try {
    //                String json = JSONUtil.getStringFromUrl(RevenueActivity.this, url);
    //                if (JSONUtil.isEmpty(json)) {
    //                    return null;
    //                }
    //
    //                return new JSONObject(json);
    //            } catch (IOException e) {
    //                e.printStackTrace();
    //            } catch (JSONException e) {
    //                e.printStackTrace();
    //            }
    //            return null;
    //        }
    //
    //        @Override
    //        protected void onPostExecute(JSONObject jsonObject) {
    //            progressBar.setVisibility(View.GONE);
    //            if (jsonObject != null) {
    //                if (jsonObject.optJSONObject("status") != null && jsonObject.optJSONObject
    //                        ("status").optInt("RetCode", -1) == 0) {
    //                    JSONObject dataObject = jsonObject.optJSONObject("data");
    //                    if (type == 1) {
    //                        String url = JSONUtil.getString(dataObject, "merchant_help");
    //                        gotoWebView(type, url);
    //                    } else if (type == 2) {
    //                        String url = JSONUtil.getString(dataObject, "bond_instro");
    //                        // 到常规提现页,但传入特殊参数,以显示特殊提示
    //                        Intent intent = new Intent(RevenueActivity.this,
    // WithdrawableListActivity
    //                                .class);
    //                        intent.putExtra("url", url);
    //                        startActivity(intent);
    //                        overridePendingTransition(R.anim.slide_in_right, R.anim
    //                                .activity_anim_default);
    //                    }
    //                }
    //            }
    //            super.onPostExecute(jsonObject);
    //        }
    //    }

    private class GetMerchantInfoTask extends AsyncTask<String, Integer, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            String url = params[0];
            try {
                String json = JSONUtil.getStringFromUrl(RevenueActivity.this, url);
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
                int statusCode = jsonObject.optJSONObject("status")
                        .optInt("RetCode");
                if (statusCode == 0) {
                    JSONObject merchantJSONObject = jsonObject.optJSONObject("data");
                    Session.getInstance()
                            .setCurrentUser(RevenueActivity.this, merchantJSONObject);
                }
            }
            super.onPostExecute(jsonObject);
        }
    }

}
