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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljkefulibrary.moudles.Support;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.ContactsAdapter;
import me.suncloud.marrymemo.model.OfflineOrder;
import me.suncloud.marrymemo.task.NewHttpPostTask;
import me.suncloud.marrymemo.task.OnHttpRequestListener;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.CustomerSupportUtil;
import com.hunliji.hljcommonlibrary.utils.SystemNotificationUtil;
import me.suncloud.marrymemo.util.Util;

public class AfterPayOfflineActivity extends HljBaseActivity {

    private String orderNum;
    private OfflineOrder offlineOrder;
    private View progressBar;

    private TextView merchantNameTv;
    private TextView totalPriceTv;
    private TextView consumePriceTv;
    private TextView redPacketAmountTv;
    private View contactPhoneLayout;
    private TextView contactPhoneTv;
    private TextView orderNumTv;
    private TextView orderTimeTv;
    private SimpleDateFormat simpleDateFormat;
    private Dialog contactDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_pay_offline);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        setOkText(R.string.label_finish);

        progressBar = findViewById(R.id.progressBar);
        merchantNameTv = (TextView) findViewById(R.id.tv_merchant_name);
        totalPriceTv = (TextView) findViewById(R.id.tv_total_price);
        consumePriceTv = (TextView) findViewById(R.id.tv_consume_money);
        redPacketAmountTv = (TextView) findViewById(R.id.tv_red_packet_amount);
        contactPhoneLayout = findViewById(R.id.contact_phone_layout);
        contactPhoneTv = (TextView) findViewById(R.id.tv_contact_phone);
        orderNumTv = (TextView) findViewById(R.id.tv_order_num);
        orderTimeTv = (TextView) findViewById(R.id.tv_order_time);

        simpleDateFormat = new SimpleDateFormat(getString(R.string.format_date_type10));

        orderNum = getIntent().getStringExtra("order_no");

        if (!JSONUtil.isEmpty(orderNum)) {
            progressBar.setVisibility(View.VISIBLE);
            getOfflineOrder();
        }
        initNotifyDialog();
    }

    private void initNotifyDialog() {
        Dialog dialog = SystemNotificationUtil.getNotificationOpenDlgOfPrefName(this,
                HljCommon.SharedPreferencesNames.PREF_NOTICE_OPEN_DLG_PAY,
                "付款成功",
                "立即开启消息通知，及时掌握订单状态和物流信息哦~",
                R.drawable.icon_dlg_appointment);
        if (dialog != null) {
            dialog.show();
        }
    }

    @Override
    public void onOkButtonClick() {
        Intent intent = new Intent(this, PayOfflineOrderListActivity.class);
        intent.putExtra("back_to_list", true);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.activity_anim_default);
        finish();
    }

    @Override
    public void onBackPressed() {
        onOkButtonClick();
    }

    private void getOfflineOrder() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("order_no", orderNum);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new NewHttpPostTask(this, new OnHttpRequestListener() {
            @Override
            public void onRequestCompleted(Object obj) {
                progressBar.setVisibility(View.GONE);
                if (obj != null) {
                    JSONObject resultObject = (JSONObject) obj;
                    if (resultObject != null && resultObject.optJSONObject("status") != null &&
                            resultObject.optJSONObject("status")
                                    .optInt("RetCode", -1) == 0) {
                        offlineOrder = new OfflineOrder(resultObject.optJSONObject("data"));
                        setOrderDetail();
                    } else {
                        Toast.makeText(AfterPayOfflineActivity.this,
                                getString(R.string.msg_fail_to_retrive_data),
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                } else {
                    Toast.makeText(AfterPayOfflineActivity.this,
                            getString(R.string.msg_fail_to_retrive_data),
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onRequestFailed(Object obj) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AfterPayOfflineActivity.this,
                        getString(R.string.msg_fail_to_retrive_data),
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }).execute(Constants.getAbsUrl(Constants.HttpPath.GET_OFFLINE_ORDER),
                jsonObject.toString());
    }


    private void setOrderDetail() {
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
        }
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

    public void onContact(View view) {
        CustomerSupportUtil.goToSupport(this, Support.SUPPORT_KIND_DEFAULT_ROBOT);
    }

}
