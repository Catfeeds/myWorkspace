package me.suncloud.marrymemo.view;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljkefulibrary.moudles.Support;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.ContactsAdapter;
import me.suncloud.marrymemo.model.OfflineOrder;
import me.suncloud.marrymemo.model.OrderStatusActionsEnum;
import me.suncloud.marrymemo.task.NewHttpPostTask;
import me.suncloud.marrymemo.task.OnHttpRequestListener;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.CustomerSupportUtil;
import me.suncloud.marrymemo.util.Util;

public class OfflineOrderDetailActivity extends HljBaseNoBarActivity {

    private TextView merchantNameTv;
    private TextView totalPriceTv;
    private TextView consumePriceTv;
    private TextView redPacketAmountTv;
    private View contactPhoneLayout;
    private TextView contactPhoneTv;
    private TextView orderNumTv;
    private TextView orderTimeTv;
    private SimpleDateFormat simpleDateFormat;
    private OfflineOrder offlineOrder;
    private TextView realPriceTv;
    private Button actionBtn;
    private View progressBar;
    private Dialog contactDialog;
    private Dialog cancelOrderDialog;
    private View realPayLayout;
    private View redPacketLayout;
    private View bottomLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_order_detail);
        setDefaultStatusBarPadding();

        offlineOrder = (OfflineOrder) getIntent().getSerializableExtra("offline_order");

        progressBar = findViewById(R.id.progressBar);
        merchantNameTv = (TextView) findViewById(R.id.tv_merchant_name);
        totalPriceTv = (TextView) findViewById(R.id.tv_total_price);
        bottomLayout = findViewById(R.id.bottom_layout);
        consumePriceTv = (TextView) findViewById(R.id.tv_consume_money);
        redPacketAmountTv = (TextView) findViewById(R.id.tv_red_packet_amount);
        contactPhoneLayout = findViewById(R.id.contact_phone_layout);
        contactPhoneTv = (TextView) findViewById(R.id.tv_contact_phone);
        orderNumTv = (TextView) findViewById(R.id.tv_order_num);
        orderTimeTv = (TextView) findViewById(R.id.tv_order_time);
        realPriceTv = (TextView) findViewById(R.id.tv_real_price);
        actionBtn = (Button) findViewById(R.id.btn_order_action);
        realPayLayout = findViewById(R.id.real_pay_layout);
        redPacketLayout = findViewById(R.id.red_packet_layout);

        simpleDateFormat = new SimpleDateFormat(getString(R.string.format_date_type10));

        showOrderDetail();
    }

    private void showOrderDetail() {
        if (offlineOrder != null) {
            merchantNameTv.setText(offlineOrder.getMerchantName());
            double price = offlineOrder.getTotalPrice() - offlineOrder.getRedPacketMoney();
            totalPriceTv.setText(Util.formatDouble2String(price < 0 ? 0 : price));

            consumePriceTv.setText(Util.formatDouble2String(offlineOrder.getTotalPrice()));
            redPacketAmountTv.setText(Util.formatDouble2String(offlineOrder.getRedPacketMoney()));

            if (offlineOrder.getMerchantContactPhones()
                    .size() > 0) {
                for (int i = 0; i < offlineOrder.getMerchantContactPhones()
                        .size(); i++) {
                    Log.d(OfflineOrderDetailActivity.class.getSimpleName(),
                            offlineOrder.getMerchantContactPhones()
                                    .get(i));
                    if (!JSONUtil.isEmpty(offlineOrder.getMerchantContactPhones()
                            .get(0))) {
                        contactPhoneLayout.setVisibility(View.VISIBLE);
                        contactPhoneTv.setText(offlineOrder.getMerchantContactPhones()
                                .get(0));
                    }
                }
            } else {
                contactPhoneLayout.setVisibility(View.GONE);
            }

            orderNumTv.setText(offlineOrder.getOrderNum());
            orderTimeTv.setText(simpleDateFormat.format(offlineOrder.getCreatedAt()));

            realPriceTv.setText(Util.formatDouble2String(offlineOrder.getTotalPrice() -
                    offlineOrder.getRedPacketMoney()));

            OrderStatusActionsEnum actionsEnum = null;
            ArrayList<OrderStatusActionsEnum> actionsEnums = offlineOrder.getActionsEnums();
            boolean isCancelable = false;
            for (int i = 0; i < actionsEnums.size(); i++) {
                if (actionsEnums.get(i) == OrderStatusActionsEnum.CANCEL) {
                    isCancelable = true;
                } else {
                    actionsEnum = actionsEnums.get(i);
                }
            }
            if (isCancelable) {
                findViewById(R.id.cancel).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.cancel).setVisibility(View.GONE);
            }

            // 要有就只会有一个动作，付款操作，
            if (actionsEnum == null) {
                bottomLayout.setVisibility(View.GONE);
                actionBtn.setVisibility(View.GONE);
                // 已经付款了，所以可以显示
                realPayLayout.setVisibility(View.VISIBLE);
                if (offlineOrder.getRedPacketMoney() > 0) {
                    redPacketLayout.setVisibility(View.VISIBLE);
                } else {
                    redPacketLayout.setVisibility(View.GONE);
                }
            } else {
                bottomLayout.setVisibility(View.VISIBLE);
                redPacketLayout.setVisibility(View.GONE);
                realPayLayout.setVisibility(View.GONE);

                actionBtn.setVisibility(View.VISIBLE);
                actionBtn.setText(actionsEnum.action);
                final OrderStatusActionsEnum finalActionsEnum = actionsEnum;
                actionBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (finalActionsEnum) {
                            case ONPAY:
                                Intent intent = new Intent(OfflineOrderDetailActivity.this,
                                        PayOfflineActivity.class);
                                intent.putExtra("offline_order", offlineOrder);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_right,
                                        R.anim.activity_anim_default);
                                break;
                        }
                    }
                });
            }
        }
    }

    public void onBackPressed(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void onCall(View v) {
        if (offlineOrder.getMerchantContactPhones() == null || offlineOrder
                .getMerchantContactPhones()
                .isEmpty()) {
            Toast.makeText(this, R.string.msg_no_merchant_number, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (offlineOrder.getMerchantContactPhones()
                .size() == 1) {
            String phone = offlineOrder.getMerchantContactPhones()
                    .get(0);
            if (!JSONUtil.isEmpty(phone) && phone.trim()
                    .length() != 0) {
                try {
                    callUp(Uri.parse("tel:" + phone.trim()));
                } catch (Exception e) {
                }
            }
            return;
        }
        if (contactDialog != null && contactDialog.isShowing()) {
            return;
        }

        if (contactDialog == null) {
            contactDialog = new Dialog(this, R.style.BubbleDialogTheme);
            Point point = JSONUtil.getDeviceSize(this);
            View view = getLayoutInflater().inflate(R.layout.dialog_contact_phones, null);
            ListView listView = (ListView) view.findViewById(R.id.contact_list);
            ContactsAdapter contactsAdapter = new ContactsAdapter(this,
                    offlineOrder.getMerchantContactPhones());
            listView.setAdapter(contactsAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String phone = (String) adapterView.getAdapter()
                            .getItem(i);
                    if (!JSONUtil.isEmpty(phone) && phone.trim()
                            .length() != 0) {
                        try {
                            callUp(Uri.parse("tel:" + phone.trim()));
                        } catch (Exception e) {

                        }
                    }
                }
            });
            contactDialog.setContentView(view);
            Window win = contactDialog.getWindow();
            ViewGroup.LayoutParams params = win.getAttributes();
            params.width = Math.round(point.x * 3 / 4);
            win.setGravity(Gravity.CENTER);
        }

        contactDialog.show();
    }

    public void onCancel(View view) {
        // 取消订单
        if (offlineOrder == null && (cancelOrderDialog != null && cancelOrderDialog.isShowing())) {
            return;
        }

        cancelOrderDialog = new Dialog(this, R.style.BubbleDialogTheme);
        View v = getLayoutInflater().inflate(R.layout.dialog_confirm, null);
        TextView msgAlertTv = (TextView) v.findViewById(R.id.tv_alert_msg);
        Button confirmBtn = (Button) v.findViewById(R.id.btn_confirm);
        Button cancelBtn = (Button) v.findViewById(R.id.btn_cancel);
        msgAlertTv.setText(R.string.msg_cancel_order);
        confirmBtn.setText(R.string.label_cancel_order);
        cancelBtn.setText(R.string.label_wrong_action);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 取消订单
                cancelOrderDialog.dismiss();
                progressBar.setVisibility(View.VISIBLE);
                cancelOrder();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelOrderDialog.dismiss();
            }
        });
        cancelOrderDialog.setContentView(v);
        Window window = cancelOrderDialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        Point point = JSONUtil.getDeviceSize(this);
        params.width = Math.round(point.x * 27 / 32);
        window.setAttributes(params);
        cancelOrderDialog.show();
    }

    private void cancelOrder() {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("order_no", offlineOrder.getOrderNum());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new NewHttpPostTask(this, new OnHttpRequestListener() {
            @Override
            public void onRequestCompleted(Object obj) {
                progressBar.setVisibility(View.GONE);
                if (obj != null) {
                    JSONObject jsonObject1 = (JSONObject) obj;

                    if (jsonObject1 != null && jsonObject1.optJSONObject("status") != null) {
                        int retCode = jsonObject1.optJSONObject("status")
                                .optInt("RetCode", -1);
                        if (retCode == 0) {
                            Toast.makeText(OfflineOrderDetailActivity.this,
                                    R.string.msg_success_to_cancel_order,
                                    Toast.LENGTH_SHORT)
                                    .show();
                            // 成功取消订单，刷新订单详情
                            offlineOrder = new OfflineOrder(jsonObject1.optJSONObject("data"));
                            showOrderDetail();
                        } else {
                            String msg = JSONUtil.getString(jsonObject1.optJSONObject("status"),
                                    "msg");
                            Toast.makeText(OfflineOrderDetailActivity.this, msg, Toast.LENGTH_SHORT)
                                    .show();
                        }

                    } else {
                        Toast.makeText(OfflineOrderDetailActivity.this,
                                R.string.msg_fail_to_cancel_order,
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                } else {
                    Toast.makeText(OfflineOrderDetailActivity.this,
                            R.string.msg_fail_to_cancel_order,
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onRequestFailed(Object obj) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(OfflineOrderDetailActivity.this,
                        R.string.msg_fail_to_cancel_order,
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }).execute(Constants.getAbsUrl(Constants.HttpPath.POST_CANCEL_OFFLINE_ORDER),
                jsonObject.toString());
    }

    public void onContact(View view) {
        CustomerSupportUtil.goToSupport(this, Support.SUPPORT_KIND_DEFAULT_ROBOT);
    }
}
