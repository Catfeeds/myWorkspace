package me.suncloud.marrymemo.view;

import android.app.Dialog;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljkefulibrary.moudles.Support;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.CSORefundInfo;
import me.suncloud.marrymemo.model.CustomSetmealOrder;
import me.suncloud.marrymemo.model.MessageEvent;
import me.suncloud.marrymemo.model.ReturnStatus;
import me.suncloud.marrymemo.task.StatusHttpPutTask;
import me.suncloud.marrymemo.task.StatusRequestListener;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.CustomerSupportUtil;
import me.suncloud.marrymemo.util.Util;

public class CustomRefundOrderActivity extends HljBaseActivity {

    @BindView(R.id.btn_action)
    Button btnAction;
    @BindView(R.id.bottom_layout)
    LinearLayout bottomLayout;
    @BindView(R.id.tv_refund_status_title)
    TextView tvRefundStatusTitle;
    @BindView(R.id.tv_refund_status_desc)
    TextView tvRefundStatusDesc;
    @BindView(R.id.tv_refund_status_desc2)
    TextView tvRefundStatusDesc2;
    @BindView(R.id.tv_refund_status_desc3)
    TextView tvRefundStatusDesc3;
    @BindView(R.id.tv_refund_merchant_name)
    TextView tvRefundMerchantName;
    @BindView(R.id.tv_refund_status)
    TextView tvRefundStatus;
    @BindView(R.id.tv_refund_reason)
    TextView tvRefundReason;
    @BindView(R.id.tv_refund_explain)
    TextView tvRefundExplain;
    @BindView(R.id.refund_desc_layout)
    LinearLayout refundDescLayout;
    @BindView(R.id.tv_refund_num)
    TextView tvRefundNum;
    @BindView(R.id.tv_refund_time)
    TextView tvRefundTime;
    @BindView(R.id.refund_infos_layout)
    LinearLayout refundInfosLayout;
    @BindView(R.id.btn_contact_service)
    Button btnContactService;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.content)
    LinearLayout content;
    private long id;
    private CSORefundInfo csoRefundInfo;
    private CustomSetmealOrder order;
    private SimpleDateFormat dateFormat;
    private Dialog confirmDlg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_refund_order);
        ButterKnife.bind(this);

        dateFormat = new SimpleDateFormat(getString(R.string.format_date_type10));

        id = getIntent().getLongExtra("id", 0);

        progressBar.setVisibility(View.VISIBLE);
        new GetOrderRefundDetailTask().executeOnExecutor(Constants.INFOTHEADPOOL);
    }

    private void setRefundDetail() {
        if (csoRefundInfo != null) {
            refundInfosLayout.setVisibility(View.VISIBLE);
            bottomLayout.setVisibility(View.GONE);
            if (csoRefundInfo.getStatus() == 20) {
                // 退款审核中
                tvRefundStatusTitle.setText(csoRefundInfo.getStatusStr());
                tvRefundStatusDesc.setVisibility(View.VISIBLE);
                tvRefundStatusDesc2.setVisibility(View.GONE);
                tvRefundStatusDesc3.setVisibility(View.GONE);
                // 退款审核中,显示撤销按钮
                bottomLayout.setVisibility(View.VISIBLE);
            } else if (csoRefundInfo.getStatus() == 21) {
                // 已取消退款申请
                tvRefundStatusTitle.setText(csoRefundInfo.getStatusStr());
                tvRefundStatusDesc.setVisibility(View.GONE);
                tvRefundStatusDesc2.setVisibility(View.GONE);
                tvRefundStatusDesc3.setVisibility(View.GONE);
            } else if (csoRefundInfo.getStatus() == 23) {
                // 退款被拒绝
                tvRefundStatusTitle.setText(csoRefundInfo.getStatusStr());
                tvRefundStatusDesc.setVisibility(View.GONE);
                tvRefundStatusDesc2.setVisibility(View.VISIBLE);
                tvRefundStatusDesc3.setVisibility(View.VISIBLE);
                tvRefundStatusDesc2.setText(getString(R.string.label_refund_refused_reason,
                        csoRefundInfo.getRefuseReason()));
                if (csoRefundInfo.getUpdatedAt() != null) {
                    tvRefundStatusDesc3.setText(getString(R.string.label_refund_refused_time,
                            dateFormat.format(csoRefundInfo.getUpdatedAt()
                                    .toDate())));
                }
            } else if (csoRefundInfo.getStatus() == 24) {
                // 退款成功
                tvRefundStatusTitle.setText(csoRefundInfo.getStatusStr());
                tvRefundStatusDesc.setVisibility(View.GONE);
                tvRefundStatusDesc2.setVisibility(View.VISIBLE);
                tvRefundStatusDesc3.setVisibility(View.VISIBLE);
                tvRefundStatusDesc2.setText(getString(R.string.label_refunded_money3,
                        Util.roundDownDouble2StringPositive(csoRefundInfo.getPayMoney())));
                if (csoRefundInfo.getUpdatedAt() != null) {
                    tvRefundStatusDesc3.setText(getString(R.string.label_refund_date,
                            dateFormat.format(csoRefundInfo.getUpdatedAt()
                                    .toDate())));
                }
            }

            // 退款申请信息
            tvRefundMerchantName.setText(order.getCustomSetmeal()
                    .getMerchant()
                    .getName());
            tvRefundStatus.setText(csoRefundInfo.getStatusStr());
            tvRefundReason.setText(csoRefundInfo.getReasonName());
            if (!JSONUtil.isEmpty(csoRefundInfo.getDesc())) {
                refundDescLayout.setVisibility(View.VISIBLE);
                tvRefundExplain.setText(csoRefundInfo.getDesc());
            } else {
                refundDescLayout.setVisibility(View.GONE);
            }
            tvRefundNum.setText(csoRefundInfo.getOrderNo());
            if (csoRefundInfo.getCreatedAt() != null) {
                tvRefundTime.setText(dateFormat.format(csoRefundInfo.getCreatedAt()
                        .toDate()));
            }
        }
    }

    @OnClick(R.id.btn_action)
    void onCancelRefund() {
        if (csoRefundInfo == null || (confirmDlg != null && confirmDlg.isShowing())) {
            return;
        }

        confirmDlg = new Dialog(this, R.style.BubbleDialogTheme);
        View v = getLayoutInflater().inflate(R.layout.dialog_confirm, null);
        TextView msgAlertTv = (TextView) v.findViewById(R.id.tv_alert_msg);
        final Button confirmBtn = (Button) v.findViewById(R.id.btn_confirm);
        Button cancelBtn = (Button) v.findViewById(R.id.btn_cancel);
        msgAlertTv.setText(R.string.msg_confirm_withdraw_refund_apply);
        confirmBtn.setText(R.string.label_confirm_withdraw);
        cancelBtn.setText(R.string.label_wrong_action);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDlg.dismiss();
                progressBar.setVisibility(View.VISIBLE);
                postCancelRefund();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDlg.dismiss();
            }
        });
        confirmDlg.setContentView(v);
        Window window = confirmDlg.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        Point point = JSONUtil.getDeviceSize(this);
        params.width = Math.round(point.x * 27 / 32);
        window.setAttributes(params);
        confirmDlg.show();
    }

    @OnClick(R.id.btn_contact_service)
    void onContactService() {
        CustomerSupportUtil.goToSupport(this, Support.SUPPORT_KIND_DEFAULT_ROBOT);
    }

    private void postCancelRefund() {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("refund_id", csoRefundInfo.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new StatusHttpPutTask(this, new StatusRequestListener() {
            @Override
            public void onRequestCompleted(Object object, ReturnStatus returnStatus) {
                JSONObject orderObject = (JSONObject) object;
                if (orderObject != null) {
                    order = new CustomSetmealOrder(orderObject);
                    // 取消退款申请,发送刷新订单列表的消息
                    EventBus.getDefault()
                            .post(new MessageEvent(MessageEvent.EventType
                                    .CUSTOM_SETMEAL_ORDER_REFRESH_WITH_OBJECT,
                                    order));
                    // 刷新退款单列表
                    EventBus.getDefault()
                            .post(new MessageEvent(MessageEvent.EventType.CUSTOM_REFUND_ORDER_FLAG,
                                    null));

                    //重新请求数据,刷新当前页面
                    new GetOrderRefundDetailTask().executeOnExecutor(Constants.INFOTHEADPOOL);
                }
            }

            @Override
            public void onRequestFailed(ReturnStatus returnStatus, boolean network) {
                progressBar.setVisibility(View.GONE);
                Util.postFailToast(CustomRefundOrderActivity.this,
                        returnStatus,
                        R.string.msg_fail_to_cancel_refund,
                        network);
            }
        }).execute(Constants.getAbsUrl(Constants.HttpPath.CUSTOM_SETMEAL_ORDER_CANCEL_REFUND),
                jsonObject.toString());
    }


    private class GetOrderRefundDetailTask extends AsyncTask<String, Object, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            String url = Constants.getAbsUrl(String.format(Constants.HttpPath
                            .CUSTOM_SETMEAL_ORDER_REFUND_DETAIL,
                    id));
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
            progressBar.setVisibility(View.GONE);

            if (jsonObject != null) {
                ReturnStatus returnStatus = new ReturnStatus(jsonObject.optJSONObject("status"));
                if (returnStatus.getRetCode() == 0) {
                    JSONObject dataObj = jsonObject.optJSONObject("data");
                    if (dataObj != null) {
                        order = new CustomSetmealOrder(dataObj.optJSONObject("order"));
                        csoRefundInfo = new CSORefundInfo(dataObj);
                    }
                }

                setRefundDetail();
            }

            super.onPostExecute(jsonObject);
        }
    }
}
