package me.suncloud.marrymemo.view;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hunliji.hljcommonlibrary.models.merchant.MerchantUser;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljkefulibrary.moudles.Support;

import org.json.JSONException;
import org.json.JSONObject;

import de.greenrobot.event.EventBus;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.MessageEvent;
import me.suncloud.marrymemo.model.NewOrder;
import me.suncloud.marrymemo.model.ReturnStatus;
import me.suncloud.marrymemo.model.Work;
import me.suncloud.marrymemo.task.NewHttpPostTask;
import me.suncloud.marrymemo.task.OnHttpRequestListener;
import me.suncloud.marrymemo.util.CustomerSupportUtil;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.TimeUtil;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.chat.WSCustomerChatActivity;

public class OrderRefundDetailActivity extends HljBaseActivity {

    private TextView refundStatusTv;
    private TextView refundAlertTv;
    private TextView merchantNameTv;
    private TextView refundStatusTv2;
    private TextView refundReasonTv;
    private TextView refundExplainTv;
    private TextView refundNoTv;
    private TextView refundApplyTimeTv;
    private Button refundActionBtn;
    private TextView timeTv;
    private NewOrder order;
    private View progressBar;
    private Dialog cancelRefundDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        order = (NewOrder) getIntent().getSerializableExtra("order");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_refund_detail);

        progressBar = findViewById(R.id.progressBar);
        timeTv = (TextView) findViewById(R.id.tv_time);
        refundNoTv = (TextView) findViewById(R.id.tv_refund_num);
        refundAlertTv = (TextView) findViewById(R.id.tv_refund_hint);
        refundReasonTv = (TextView) findViewById(R.id.tv_refund_reason);
        refundStatusTv = (TextView) findViewById(R.id.tv_refund_status);
        refundActionBtn = (Button) findViewById(R.id.btn_refund_action);
        merchantNameTv = (TextView) findViewById(R.id.tv_merchant_name);
        refundApplyTimeTv = (TextView) findViewById(R.id.tv_refund_time);
        refundStatusTv2 = (TextView) findViewById(R.id.tv_refund_status2);
        refundExplainTv = (TextView) findViewById(R.id.tv_refund_explain);

        refundStatusTv.setText(order.getStatusStr());
        refundStatusTv2.setText(order.getStatusStr());
        merchantNameTv.setText(order.getMerchantName());
        refundReasonTv.setText(order.getRefundReason());
        if (!JSONUtil.isEmpty(order.getRefundDesc())) {
            findViewById(R.id.refund_explain_layout).setVisibility(View.VISIBLE);
            refundExplainTv.setText(order.getRefundDesc());
        }
        refundNoTv.setText(order.getRefundOrderNum());
        refundApplyTimeTv.setText(TimeUtil.getTimeString(order.getCreatedAt(), this));
        if (order.getStatus() == 24) {
            refundAlertTv.setVisibility(View.VISIBLE);
            timeTv.setVisibility(View.VISIBLE);
            refundAlertTv.setText(getString(R.string.label_refund_price2,
                    Util.formatDouble2String(order.getRefundPrice())));
            timeTv.setText(getString(R.string.label_refund_time2,
                    TimeUtil.getTimeString2(order.getUpdatedAt(), this)));
        } else if (!JSONUtil.isEmpty(order.getRefuseReason())) {
            refundAlertTv.setVisibility(View.VISIBLE);
            timeTv.setVisibility(View.VISIBLE);
            refundAlertTv.setText(getString(R.string.label_refund_refused_reason,
                    order.getRefuseReason()));
            timeTv.setText(getString(R.string.label_refund_refused_time,
                    TimeUtil.getTimeString(order.getUpdatedAt(), this)));
        } else if (order.getActions() != null && !order.getActions()
                .isEmpty()) {
            refundAlertTv.setVisibility(View.VISIBLE);
            refundActionBtn.setVisibility(View.VISIBLE);
            refundAlertTv.setText(R.string.hint_order_refund);
            refundActionBtn.setText(order.getActions()
                    .get(0)
                    .getAction());
        }
    }

    public void onBackPressed(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void onContact(View view) {
        CustomerSupportUtil.goToSupport(this,
                order.isLvpai() ? Support.SUPPORT_KIND_TRAVEL : Support.SUPPORT_KIND_DEFAULT_ROBOT);
    }

    public void onMerchantChat(View view) {
        Intent intent = new Intent(this, WSCustomerChatActivity.class);
        MerchantUser user = new MerchantUser();
        user.setNick(order.getMerchantName());
        user.setId(order.getMerchantUserId());
        user.setAvatar(order.getMerchantLogoPath());
        user.setMerchantId(order.getMerchantId());

        Work work = new Work(new JSONObject());
        work.setId(order.getPrdId());
        work.setCoverPath(order.getCoverPath());
        work.setTitle(order.getTitle());

        intent.putExtra("user", user);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    public void onRefundAction(View view) {
        if (order == null || JSONUtil.isEmpty(order.getOrderNum()) || (cancelRefundDialog != null
                && cancelRefundDialog.isShowing())) {
            return;
        }
        if (cancelRefundDialog == null) {
            cancelRefundDialog = new Dialog(this, R.style.BubbleDialogTheme);
            cancelRefundDialog.setContentView(R.layout.dialog_confirm);
            TextView msgAlertTv = (TextView) cancelRefundDialog.findViewById(R.id.tv_alert_msg);
            Button confirmBtn = (Button) cancelRefundDialog.findViewById(R.id.btn_confirm);
            Button cancelBtn = (Button) cancelRefundDialog.findViewById(R.id.btn_cancel);
            msgAlertTv.setText(R.string.msg_cancel_refund);
            confirmBtn.setText(R.string.label_cancel_refund);
            cancelBtn.setText(R.string.label_wrong_action);
            confirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancelRefundDialog.dismiss();
                    cancelRefund();
                }
            });

            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancelRefundDialog.dismiss();
                }
            });
            Window window = cancelRefundDialog.getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            Point point = JSONUtil.getDeviceSize(this);
            params.width = Math.round(point.x * 27 / 32);
            window.setAttributes(params);
        }
        cancelRefundDialog.show();
    }

    private void cancelRefund() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("order_no", order.getOrderNum());
            progressBar.setVisibility(View.VISIBLE);
            new NewHttpPostTask(this, new OnHttpRequestListener() {
                @Override
                public void onRequestCompleted(Object obj) {
                    JSONObject jsonObject = (JSONObject) obj;
                    progressBar.setVisibility(View.GONE);
                    if (jsonObject != null && !jsonObject.isNull("status")) {
                        ReturnStatus returnStatus = new ReturnStatus(jsonObject.optJSONObject(
                                "status"));
                        if (returnStatus.getRetCode() == 0) {
                            EventBus.getDefault()
                                    .post(new MessageEvent(MessageEvent.EventType
                                            .SERVICE_ORDER_REFRESH_FLAG,
                                            null));

                            order = new NewOrder(jsonObject.optJSONObject("data"));
                            Intent intent = getIntent();
                            intent.putExtra("order", order);
                            setResult(RESULT_OK, intent);
                            refundStatusTv.setText(order.getStatusStr());
                            refundStatusTv2.setText(order.getStatusStr());
                            refundActionBtn.setVisibility(View.GONE);
                            refundAlertTv.setVisibility(View.GONE);
                            timeTv.setVisibility(View.GONE);
                            if (order.getRefundPrice() > 0) {
                                refundAlertTv.setText(getString(R.string.label_refund_price2,
                                        order.getRefundPrice()));
                                timeTv.setVisibility(View.VISIBLE);
                                timeTv.setText(getString(R.string.label_refund_time2,
                                        TimeUtil.getTimeString(order.getUpdatedAt(),
                                                OrderRefundDetailActivity.this)));
                            } else if (!JSONUtil.isEmpty(order.getRefuseReason())) {
                                refundAlertTv.setText(getString(R.string
                                                .label_refund_refused_reason,
                                        order.getRefuseReason()));
                                timeTv.setVisibility(View.VISIBLE);
                                timeTv.setText(getString(R.string.label_refund_refused_time,
                                        TimeUtil.getTimeString(order.getUpdatedAt(),
                                                OrderRefundDetailActivity.this)));
                            } else if (order.getActions() != null && !order.getActions()
                                    .isEmpty()) {
                                refundAlertTv.setText(R.string.hint_order_refund);
                                refundActionBtn.setVisibility(View.VISIBLE);
                                refundActionBtn.setText(order.getActions()
                                        .get(0)
                                        .getAction());
                            }
                            return;
                        }
                    }
                    Toast.makeText(OrderRefundDetailActivity.this,
                            R.string.msg_fail_to_cancel_refund,
                            Toast.LENGTH_SHORT)
                            .show();
                }

                @Override
                public void onRequestFailed(Object obj) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(OrderRefundDetailActivity.this,
                            R.string.msg_fail_to_cancel_refund,
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }).execute(Constants.getAbsUrl(Constants.HttpPath.POST_CANCEL_REFUND_APPLICATION2),
                    jsonObject.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
