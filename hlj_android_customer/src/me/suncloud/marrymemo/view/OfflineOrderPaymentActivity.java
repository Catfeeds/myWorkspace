package me.suncloud.marrymemo.view;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.unionpay.UPPayAssistEx;
import com.unionpay.uppay.PayActivity;

import org.json.JSONException;
import org.json.JSONObject;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.task.NewHttpPostTask;
import me.suncloud.marrymemo.task.OnHttpRequestListener;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.Result;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.widget.CheckableLinearLayoutGroup;

public class OfflineOrderPaymentActivity extends HljBaseNoBarActivity {

    private static final int RQF_PAY = 1;
    private CheckableLinearLayoutGroup payAgentMenu;
    private double realPrice;
    private String redPacketNum;
    private String orderNum;
    private TextView totalPriceTv;
    private View progressBar;
    private long orderId;
    private boolean backToList;

    Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case RQF_PAY:
                    Result resultObj = new Result((String) msg.obj);
                    String resultStatus = resultObj.resultStatus;

                    if (progressBar != null) {
                        progressBar.setVisibility(View.GONE);
                    }
                    if (TextUtils.equals(resultStatus, "9000") || TextUtils.equals(resultStatus,
                            "8000")) {
                        Intent intent = new Intent(OfflineOrderPaymentActivity.this,
                                AfterPayOfflineActivity.class);
                        intent.putExtra("order_no", orderNum);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                        finish();
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 500);
                    } else {
                        Toast.makeText(OfflineOrderPaymentActivity.this,
                                R.string.pay_result3,
                                Toast.LENGTH_SHORT)
                                .show();

                    }
                    break;
                default:
                    break;
            }
        }
    };
    private Dialog confirmDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_order_payment);
        setDefaultStatusBarPadding();

        setSwipeBackEnable(false);
        progressBar = findViewById(R.id.progressBar);
        payAgentMenu = (CheckableLinearLayoutGroup) findViewById(R.id.pay_agent_menu);
        totalPriceTv = (TextView) findViewById(R.id.tv_total_actual_price);
        orderId = getIntent().getLongExtra("orderId", 0);
        orderNum = getIntent().getStringExtra("order_no");
        realPrice = getIntent().getDoubleExtra("real_price", 0);
        redPacketNum = getIntent().getStringExtra("red_packet_no");
        backToList = getIntent().getBooleanExtra("back_to_list", false);

        totalPriceTv.setText(Util.formatDouble2String(realPrice));
    }

    public void onBackPressed(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (backToList) {
            Intent intent = new Intent(this, PayOfflineOrderListActivity.class);
            intent.putExtra("back_to_list", true);
            startActivity(intent);
            overridePendingTransition(0, R.anim.slide_out_right);
            finish();
        } else {
            super.onBackPressed();
        }
    }

    //    public void confirmBack() {
    //        if (confirmDialog != null && confirmDialog.isShowing()) {
    //            return;
    //        }
    //        if (confirmDialog == null) {
    //            confirmDialog = new Dialog(this, R.style.bubble_dialog);
    //            View v = getLayoutInflater().inflate(R.layout.dialog_confirm, null);
    //            TextView msgAlertTv = (TextView) v.findViewById(R.id.tv_alert_msg);
    //            Button confirmBtn = (Button) v.findViewById(R.id.btn_confirm);
    //            Button cancelBtn = (Button) v.findViewById(R.id.btn_cancel);
    //            msgAlertTv.setText(R.string.msg_confirm_exit_payment);
    //            confirmBtn.setText(R.string.label_confirm_exit);
    //            cancelBtn.setText(R.string.label_wrong_action);
    //            confirmBtn.setOnClickListener(new View.OnClickListener() {
    //                @Override
    //                public void onClick(View v) {
    //                    confirmDialog.dismiss();
    //                    OfflineOrderPaymentActivity.super.onBackPressed();
    //                }
    //            });
    //
    //            cancelBtn.setOnClickListener(new View.OnClickListener() {
    //                @Override
    //                public void onClick(View v) {
    //                    confirmDialog.dismiss();
    //                }
    //            });
    //            confirmDialog.setContentView(v);
    //            Window window = confirmDialog.getWindow();
    //            WindowManager.LayoutParams params = window.getAttributes();
    //            Point point = JSONUtil.getDeviceSize(this);
    //            params.width = Math.round(point.x * 27 / 32);
    //            window.setAttributes(params);
    //        }
    //        confirmDialog.show();
    //    }

    public void onPay(View view) {
        JSONObject jsonObject = new JSONObject();
        String payAgent = "";
        switch (payAgentMenu.getCheckedRadioButtonId()) {
            case R.id.alipay:
                payAgent = "alipay";
                break;
            case R.id.union_pay:
                payAgent = "unionpay";
                break;
            //            case R.id.credit_card_pay:
            //                payAgent = "umpay";
            //                break;

        }
        try {
            jsonObject.put("order_no", orderNum);
            jsonObject.put("pay_type", payAgent);
            if (!JSONUtil.isEmpty(redPacketNum)) {
                jsonObject.put("red_package_no", redPacketNum);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String finalPayAgent = payAgent;
        new NewHttpPostTask(this, new OnHttpRequestListener() {
            @Override
            public void onRequestCompleted(Object obj) {
                if (obj != null) {
                    JSONObject resultObject = (JSONObject) obj;

                    // 判断返回结果中有没有pay_params参数，如果有的话说明需要正常支付，如果没有的话可能是零元支付的情况
                    // 判断需付金额是否大于零，如果大于则正常支付，如果不大于则跳过支付
                    if (!resultObject.isNull("data") && !resultObject.optJSONObject("data")
                            .isNull("pay_params")) {
                        JSONObject dataObject = resultObject.optJSONObject("data");
                        Log.e("result paying order", resultObject.toString());

                        final String orderInfo = JSONUtil.getString(dataObject, "pay_params");
                        if (finalPayAgent.equals("alipay")) {
                            // 支付宝支付
                            new Thread() {
                                public void run() {
                                    PayTask alipay = new PayTask(OfflineOrderPaymentActivity.this);
                                    String result = alipay.pay(orderInfo);
                                    Message msg = new Message();
                                    msg.what = RQF_PAY;
                                    msg.obj = result;
                                    mHandler.sendMessage(msg);
                                }
                            }.start();
                        } else if (finalPayAgent.equals("unionpay")) {
                            // 银联支付
                            JSONObject json = null;
                            try {
                                json = new JSONObject(dataObject.optString("pay_params"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (json != null && !JSONUtil.isEmpty(json.optString("tn", null))) {
                                UPPayAssistEx.startPayByJAR(OfflineOrderPaymentActivity.this,
                                        PayActivity.class,
                                        null,
                                        null,
                                        json.optString("tn"),
                                        "00");
                            } else {
                                Toast.makeText(OfflineOrderPaymentActivity.this,
                                        R.string.hint_ordor_pay_err,
                                        Toast.LENGTH_SHORT)
                                        .show();
                            }
                        } else if (finalPayAgent.equals("umpay")) {
                            // 信用卡支付
                            Intent intent = new Intent(OfflineOrderPaymentActivity.this,
                                    UMPayActivity.class);
                            intent.putExtra("url", orderInfo);
                            startActivityForResult(intent, Constants.RequestCode.UMPAY);
                            overridePendingTransition(R.anim.slide_in_right,
                                    R.anim.activity_anim_default);
                        }
                    } else if (!resultObject.isNull("data") && !resultObject.optJSONObject("data")
                            .isNull("fee") && resultObject.optJSONObject("data")
                            .optInt("fee", 0) <= 0) {
                        // 零元支付
                        Intent intent = new Intent(OfflineOrderPaymentActivity.this,
                                AfterPayOfflineActivity.class);
                        intent.putExtra("order_no", orderNum);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                        finish();
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 500);
                    } else {
                        Toast.makeText(OfflineOrderPaymentActivity.this,
                                R.string.msg_fail_to_pay_order,
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                } else {
                    Toast.makeText(OfflineOrderPaymentActivity.this,
                            R.string.msg_fail_to_pay_order,
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onRequestFailed(Object obj) {
                Toast.makeText(OfflineOrderPaymentActivity.this,
                        R.string.msg_fail_to_pay_order,
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }).execute(Constants.getAbsUrl(Constants.HttpPath.POST_PAY_OFFLINE), jsonObject.toString());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {

            if (resultCode == RESULT_OK) {
                switch (requestCode) {
                    case Constants.RequestCode.UMPAY:
                        // 信用卡支付成功
                        boolean isSuccess = data.getBooleanExtra("success", false);
                        if (isSuccess) {
                            progressBar.setVisibility(View.GONE);
                            setResult(RESULT_OK);
                            Intent intent = new Intent(OfflineOrderPaymentActivity.this,
                                    AfterPayOfflineActivity.class);
                            intent.putExtra("order_no", orderNum);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right,
                                    R.anim.activity_anim_default);
                            finish();
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    finish();
                                }
                            }, 500);
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(this,
                                    getString(R.string.msg_fail_to_umpay),
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }
                        break;
                }
            }

            // 银联支付返回的信息判断
            String str = data.getExtras()
                    .getString("pay_result");
            if (!JSONUtil.isEmpty(str)) {
                if (str.equalsIgnoreCase("success")) {
                    setResult(RESULT_OK);
                    Intent intent = new Intent(OfflineOrderPaymentActivity.this,
                            AfterPayOfflineActivity.class);
                    intent.putExtra("order_no", orderNum);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                    finish();
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 500);
                } else if (str.equalsIgnoreCase("fail")) {
                    Toast.makeText(OfflineOrderPaymentActivity.this,
                            R.string.pay_result3,
                            Toast.LENGTH_SHORT)
                            .show();
                } else if (str.equalsIgnoreCase("cancel")) {
                    Toast.makeText(OfflineOrderPaymentActivity.this,
                            R.string.pay_result4,
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
