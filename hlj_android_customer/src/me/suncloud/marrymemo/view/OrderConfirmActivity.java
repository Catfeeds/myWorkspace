package me.suncloud.marrymemo.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljtrackerlibrary.HljTracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.RedPacket;
import me.suncloud.marrymemo.model.ReturnStatus;
import me.suncloud.marrymemo.model.Work;
import me.suncloud.marrymemo.model.orders.ServeCustomerInfo;
import me.suncloud.marrymemo.task.AsyncBitmapDrawable;
import me.suncloud.marrymemo.task.ImageLoadTask;
import me.suncloud.marrymemo.task.NewHttpPostTask;
import me.suncloud.marrymemo.task.OnHttpRequestListener;
import me.suncloud.marrymemo.task.StatusHttpPostTask;
import me.suncloud.marrymemo.task.StatusRequestListener;
import me.suncloud.marrymemo.util.DialogUtil;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.ScaleMode;
import me.suncloud.marrymemo.util.ServeCustomerInfoUtil;
import me.suncloud.marrymemo.util.Util;

public class OrderConfirmActivity extends HljBaseNoBarActivity {

    private ViewGroup contentLayout;
    private View bottomLayout;
    private TextView serveTimeTv;
    private TextView serveCustomerTv;
    private TextView phoneTv;
    private TextView totalActualPriceTv;
    private int coverWidth;
    private int coverHeight;
    private ArrayList<RedPacket> redPackets;
    private Dialog dialog;
    private TextView redPacketsCount;
    private double allTotalPrice;
    private double allMoney;
    private String orderStr;
    private JSONArray giftArray;
    private View progressBar;
    private boolean needWeddingTime;
    private View serveTimeLayout;
    private Dialog progressDialog;
    private Dialog confirmDialog;
    private String merchantCity;

    private Handler handler = new Handler();
    private Runnable submitRunnable = new Runnable() {
        @Override
        public void run() {
            if (confirmDialog != null && confirmDialog.isShowing()) {
                confirmDialog.dismiss();
                submitOrder();
            }
        }
    };
    private ServeCustomerInfo customerInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        redPackets = new ArrayList<>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_order_confirm);
        setDefaultStatusBarPadding();
        Point point = JSONUtil.getDeviceSize(this);
        coverWidth = Math.round(point.x * 100 / 320);
        coverHeight = Math.round(coverWidth * 212 / 338);

        orderStr = getIntent().getStringExtra("orderStr");
        contentLayout = (ViewGroup) findViewById(R.id.content_layout);
        bottomLayout = findViewById(R.id.bottom_layout);
        serveTimeTv = (TextView) findViewById(R.id.tv_serve_time);
        serveTimeLayout = findViewById(R.id.serve_time_layout);
        serveCustomerTv = (TextView) findViewById(R.id.tv_serve_customer);
        phoneTv = (TextView) findViewById(R.id.tv_phone);
        totalActualPriceTv = (TextView) findViewById(R.id.tv_total_actual_price);
        redPacketsCount = (TextView) findViewById(R.id.tv_red_packets);
        progressBar = findViewById(R.id.progressBar);

        progressBar.setVisibility(View.VISIBLE);
        postWorksOrder(orderStr);
        new NewHttpPostTask(this, new OnHttpRequestListener() {
            @Override
            public void onRequestCompleted(Object obj) {
                JSONObject jsonObject = (JSONObject) obj;
                ReturnStatus returnStatus = new ReturnStatus(jsonObject.optJSONObject("status"));
                if (returnStatus.getRetCode() == 0 && !jsonObject.isNull("data") && jsonObject
                        .optJSONObject(
                        "data") != null) {
                    JSONArray jsonArray = jsonObject.optJSONObject("data")
                            .optJSONArray("list");
                    if (jsonArray != null && jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            RedPacket redPacket = new RedPacket(jsonArray.optJSONObject(i));
                            redPackets.add(redPacket);
                        }
                        if (redPackets.size() > 0) {
                            findViewById(R.id.red_packet_layout).setVisibility(View.VISIBLE);
                            redPacketsCount.setText(getString(R.string.hint_red_packet_count,
                                    redPackets.size()));
                        } else {
                            findViewById(R.id.red_packet_layout).setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onRequestFailed(Object obj) {

            }
        }).executeOnExecutor(Constants.LISTTHEADPOOL,
                Constants.getAbsUrl(Constants.HttpPath.GET_RED_PACKETS_URL),
                orderStr);
    }

    public void onBackPressed(View view) {
        onBackPressed();
    }

    public void editInfo(View view) {
        Intent intent = new Intent(this, OrderInfoEditActivity.class);
        intent.putExtra("info", customerInfo);
        intent.putExtra("is_need_wedding_time", needWeddingTime);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    private void postWorksOrder(String orderStr) {
        new NewHttpPostTask(this, new OnHttpRequestListener() {
            @Override
            public void onRequestCompleted(Object obj) {
                progressBar.setVisibility(View.GONE);
                JSONObject jsonObject = (JSONObject) obj;
                if (jsonObject != null) {
                    if (jsonObject.optInt("RetCode", -1) == 0) {
                        showOrders(jsonObject);
                    } else {
                        String msg = jsonObject.optString("msg");
                        Toast.makeText(OrderConfirmActivity.this, msg, Toast.LENGTH_SHORT)
                                .show();
                        OrderConfirmActivity.super.onBackPressed();
                    }
                } else {
                    Toast.makeText(OrderConfirmActivity.this,
                            getString(R.string.msg_order_submit_error),
                            Toast.LENGTH_SHORT)
                            .show();
                    OrderConfirmActivity.super.onBackPressed();
                }
            }

            @Override
            public void onRequestFailed(Object obj) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(OrderConfirmActivity.this,
                        getString(R.string.msg_order_submit_error),
                        Toast.LENGTH_SHORT)
                        .show();
                OrderConfirmActivity.super.onBackPressed();
            }
        }).execute(Constants.getAbsUrl(Constants.HttpPath.FIRST_POST_ORDER), orderStr);
    }

    private void showOrders(JSONObject jsonObject) {
        contentLayout.setVisibility(View.VISIBLE);
        bottomLayout.setVisibility(View.VISIBLE);
        LayoutInflater inflater = LayoutInflater.from(this);
        JSONArray packagesArray = jsonObject.optJSONArray("rule_items");

        needWeddingTime = jsonObject.optInt("need_wedding_time", 1) > 0;
        setCustomerInfoView();
        int workItemCount = 0; // 用来计数套餐数量，用于标记套餐item的分割线

        Work work = null;
        double depositMoney = 0;
        double payAllSavedMoney = 0;
        if (packagesArray != null && packagesArray.length() > 0) {
            // 所有的活动，这里添加的是属于活动的套餐
            for (int i = 0; i < packagesArray.length(); i++) {
                // 单独处理每个活动
                JSONObject packageObject = packagesArray.optJSONObject(i);
                JSONObject infoObject = packageObject.optJSONObject("ruleinfo");
                JSONArray worksArray = packageObject.optJSONArray("items");
                JSONArray gArray = packageObject.optJSONArray("gift"); // 赠送套餐
                depositMoney += packageObject.optDouble("deposit_money", 0);
                payAllSavedMoney += packageObject.optDouble("pay_all_money", 0);

                View packageView;
                // 超过一个活动套餐就像是活动标题
                if (worksArray != null && worksArray.length() > 0) {
                    packageView = inflatePackageView(inflater,
                            JSONUtil.getString(infoObject, "name"));
                } else {
                    packageView = inflatePackageView(inflater, JSONUtil.getString(infoObject, ""));
                }

                ViewGroup worksViewGroup = (ViewGroup) packageView.findViewById(R.id.works_layout);
                if (worksArray != null && worksArray.length() > 0) {
                    // 活动中的套餐
                    for (int j = 0; j < worksArray.length(); j++) {
                        if (work == null) {
                            work = new Work(worksArray.optJSONObject(j));
                        }
                        View workItemView = inflateWorkItemView(inflater,
                                worksArray.optJSONObject(j),
                                false);
                        if (workItemCount == 0) {
                            workItemView.findViewById(R.id.top_dash_line)
                                    .setVisibility(View.GONE);
                        }
                        worksViewGroup.addView(workItemView);
                        workItemCount++;
                    }
                }

                if (gArray != null && gArray.length() > 0) {
                    // 赠送的套餐
                    giftArray = new JSONArray();
                    for (int j = 0; j < gArray.length(); j++) {
                        giftArray.put(gArray.optJSONObject(j)
                                .optJSONObject("prdinfo"));

                        View workItemView = inflateWorkItemView(inflater,
                                gArray.optJSONObject(j),
                                true);
                        if (workItemCount == 0) {
                            workItemView.findViewById(R.id.top_dash_line)
                                    .setVisibility(View.GONE);
                        }
                        worksViewGroup.addView(workItemView);
                        workItemCount++;
                    }
                }

                worksViewGroup.addView(inflatePricesView(inflater, packageObject, true));

                contentLayout.addView(packageView);
            }
        }

        JSONArray otherWorksArray = jsonObject.optJSONArray("others");
        if (otherWorksArray != null && otherWorksArray.length() > 0) {
            // 不属于活动的套餐
            for (int i = 0; i < otherWorksArray.length(); i++) {
                JSONObject tempJsonObject = otherWorksArray.optJSONObject(i);
                depositMoney += tempJsonObject.optDouble("deposit_money", 0);
                payAllSavedMoney += tempJsonObject.optDouble("pay_all_money", 0);
                View packageView = inflatePackageView(inflater, "");
                ViewGroup worksViewGroup = (ViewGroup) packageView.findViewById(R.id.works_layout);

                JSONObject workObject = tempJsonObject.optJSONObject("item");
                if (workObject != null) {
                    if (work == null) {
                        work = new Work(workObject);
                    }
                    View workItemView = inflateWorkItemView(inflater, workObject, false);
                    if (workItemCount == 0) {
                        workItemView.findViewById(R.id.top_dash_line)
                                .setVisibility(View.GONE);
                    }
                    worksViewGroup.addView(workItemView);
                    workItemCount++;
                }

                worksViewGroup.addView(inflatePricesView(inflater, tempJsonObject, false));

                contentLayout.addView(packageView);
            }
        }
        if (depositMoney >= allMoney) {
            depositMoney = 0;
        }

        if (work != null && work.getVersion() > 0 && ((!work.isInstallment() && depositMoney > 0)
                || !JSONUtil.isEmpty(
                work.getOrderGift()) || !JSONUtil.isEmpty(work.getPayAllGift()) ||
                payAllSavedMoney > 0 || (work.getMerchant() != null && !JSONUtil.isEmpty(
                work.getMerchant()
                        .getCostEffective())))) {
            View view = View.inflate(this, R.layout.work_privilege_layout, null);
            TextView earnest = (TextView) view.findViewById(R.id.earnest);
            if (depositMoney > 0 && !work.isInstallment()) {
                earnest.setVisibility(View.VISIBLE);
                earnest.setText("• " + getString(R.string.label_earnest,
                        Util.formatDouble2String(depositMoney)));
            } else {
                earnest.setVisibility(View.GONE);
            }
            TextView gift = (TextView) view.findViewById(R.id.gift);
            if (!JSONUtil.isEmpty(work.getOrderGift())) {
                gift.setVisibility(View.VISIBLE);
                gift.setText("• " + getString(R.string.label_gift, work.getOrderGift()));
            } else {
                gift.setVisibility(View.GONE);
            }
            TextView payAll = (TextView) view.findViewById(R.id.pay_all);
            if (payAllSavedMoney > 0) {
                payAll.setVisibility(View.VISIBLE);
                payAll.setText("• " + getString(R.string.label_pay_all_percent,
                        Util.formatDouble2String(payAllSavedMoney)));
            } else if (!JSONUtil.isEmpty(work.getPayAllGift())) {
                payAll.setVisibility(View.VISIBLE);
                payAll.setText("• " + getString(R.string.label_pay_all_gift, work.getPayAllGift()));
            } else {
                payAll.setVisibility(View.GONE);
            }
            if (work.getMerchant() != null && !JSONUtil.isEmpty(work.getMerchant()
                    .getCostEffective())) {
                view.findViewById(R.id.merchant_privilege)
                        .setVisibility(View.VISIBLE);
            } else {
                view.findViewById(R.id.merchant_privilege)
                        .setVisibility(View.GONE);
            }
            contentLayout.addView(view);
        }

        totalActualPriceTv.setText(Util.formatDouble2String(allTotalPrice));
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void onSubmit(View view) {
        if (TextUtils.isEmpty(customerInfo.getCustomerName())) {
            Toast.makeText(this, getString(R.string.msg_name_empty2), Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (TextUtils.isEmpty(customerInfo.getCustomerPhone())) {
            Toast.makeText(this, getString(R.string.msg_phone_empty2), Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (needWeddingTime) {
            if (customerInfo.getServeTime() == null) {
                Toast.makeText(this, getString(R.string.msg_time_empty2), Toast.LENGTH_SHORT)
                        .show();
                return;
            } else {
                if (customerInfo.getServeTime()
                        .isBeforeNow()) {
                    // 当前填写的这个时间不能使用
                    Toast.makeText(OrderConfirmActivity.this,
                            getString(R.string.msg_wrong_time),
                            Toast.LENGTH_SHORT)
                            .show();
                    return;
                }
            }
        }
        if (confirmDialog != null && confirmDialog.isShowing()) {
            return;
        }
        if (confirmDialog == null) {
            confirmDialog = new Dialog(this, R.style.BubbleDialogTheme);
            confirmDialog.setContentView(R.layout.dialog_order_confirm);
            confirmDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    handler.postDelayed(submitRunnable, 5000);
                }
            });
            confirmDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    handler.removeCallbacks(submitRunnable);
                }
            });

            confirmDialog.findViewById(R.id.btn_confirm)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            confirmDialog.dismiss();
                            submitOrder();
                        }
                    });
            confirmDialog.findViewById(R.id.btn_cancel)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            confirmDialog.dismiss();
                            editInfo(null);
                        }
                    });
            confirmDialog.findViewById(R.id.merchant_city_layout)
                    .setVisibility(View.VISIBLE);
            ((TextView) confirmDialog.findViewById(R.id.tv_city)).setText(merchantCity);
        }
        if (needWeddingTime) {
            confirmDialog.findViewById(R.id.serve_time_layout)
                    .setVisibility(View.VISIBLE);
            ((TextView) confirmDialog.findViewById(R.id.tv_serve_time)).setText(customerInfo
                    .getServeTime()
                    .toString("yyyy-MM-dd"));
        } else {
            confirmDialog.findViewById(R.id.serve_time_layout)
                    .setVisibility(View.GONE);
        }
        ((TextView) confirmDialog.findViewById(R.id.tv_serve_customer)).setText(customerInfo
                .getCustomerName());
        ((TextView) confirmDialog.findViewById(R.id.tv_phone)).setText(customerInfo
                .getCustomerPhone());

        confirmDialog.show();
    }

    private void submitOrder() {
        JSONObject orderObject = null;
        try {
            orderObject = new JSONObject(orderStr);
            JSONArray array = orderObject.optJSONArray("prds");
            new HljTracker.Builder(this).screen("v2_submit_order")
                    .action("submit_order")
                    .additional(array != null ? array.toString() : null)
                    .build()
                    .add();
            if (giftArray != null) {
                orderObject.put("gift", giftArray);
            }
            orderObject.put("buy_name", customerInfo.getCustomerName());
            orderObject.put("buy_phone", customerInfo.getCustomerPhone());
            if (needWeddingTime) {
                orderObject.put("wedding_time",
                        customerInfo.getServeTime()
                                .toString("yyyy-MM-dd"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (orderObject == null) {
            return;
        }
        if (progressDialog == null) {
            progressDialog = DialogUtil.createProgressDialog(this);
        }
        progressDialog.show();
        new StatusHttpPostTask(this, new StatusRequestListener() {
            @Override
            public void onRequestCompleted(Object object, ReturnStatus returnStatus) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                if (object != null && object instanceof JSONObject) {
                    JSONObject dataObject = (JSONObject) object;
                    String orderNum = JSONUtil.getString(dataObject, "order_no");
                    long orderId = dataObject.optLong("order_id", 0);
                    Intent intent = new Intent(OrderConfirmActivity.this,
                            OrderPaymentActivity.class);
                    intent.putExtra("order_id", orderId);
                    intent.putExtra("prds", orderStr);
                    intent.putExtra("order_num", orderNum);
                    intent.putExtra("back_to_list", true);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                    finish();
                }

            }

            @Override
            public void onRequestFailed(ReturnStatus returnStatus, boolean network) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                Util.postFailToast(OrderConfirmActivity.this,
                        returnStatus,
                        R.string.msg_order_submit_error,
                        network);

            }
        }).execute(Constants.getAbsUrl(Constants.HttpPath.POST_SUBMIT_ORDER),
                orderObject.toString());
    }


    private View inflatePackageView(LayoutInflater inflater, String packageName) {
        View packageView = inflater.inflate(R.layout.package_work_orders_layout,
                contentLayout,
                false);
        if (!JSONUtil.isEmpty(packageName)) {
            TextView textView = (TextView) packageView.findViewById(R.id.tv_package_title);
            textView.setText(packageName);
        } else {
            packageView.findViewById(R.id.works_package_head)
                    .setVisibility(View.GONE);
        }

        return packageView;
    }

    private View inflateWorkItemView(
            LayoutInflater inflater, JSONObject jsonObject, boolean isGift) {
        View workView = inflater.inflate(R.layout.work_order_item, null, false);
        workView.findViewById(R.id.img_installment)
                .setVisibility(JSONUtil.getBoolean(jsonObject,
                        "is_installment") ? View.VISIBLE : View.GONE);
        ImageView cover = (ImageView) workView.findViewById(R.id.img_cover);
        TextView titleTv = (TextView) workView.findViewById(R.id.tv_title);
        TextView merchantNameTv = (TextView) workView.findViewById(R.id.tv_merchant_name);
        TextView priceTv = (TextView) workView.findViewById(R.id.tv_work_price);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) cover
                .getLayoutParams();
        params.width = coverWidth;
        params.height = coverHeight;

        String url = JSONUtil.getString(jsonObject, "cover_path");
        String title = JSONUtil.getString(jsonObject, "title");
        double price = jsonObject.optDouble("actual_price", 0);
        double salePrice = jsonObject.optDouble("sale_price", 0);
        if (salePrice > 0) {
            priceTv.setText(getString(R.string.label_price,
                    Util.formatDouble2String(salePrice) + " "));
        } else {
            priceTv.setText(getString(R.string.label_price, Util.formatDouble2String(price) + " "));
        }
        if (isGift) {
            priceTv.setText(getString(R.string.label_price, "0"));
        }
        try {
            String merchantName = JSONUtil.getString(jsonObject.getJSONObject("merchant"), "name");
            if (JSONUtil.isEmpty(merchantCity)) {
                merchantCity = JSONUtil.getString(jsonObject.getJSONObject("merchant"), "city");
            }
            merchantNameTv.setText(merchantName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        url = JSONUtil.getImagePath(url, coverWidth);
        if (!JSONUtil.isEmpty(url)) {
            ImageLoadTask task = new ImageLoadTask(cover);
            cover.setTag(url);
            task.loadImage(url,
                    coverWidth,
                    ScaleMode.WIDTH,
                    new AsyncBitmapDrawable(getResources(), R.mipmap.icon_empty_image, task));
        }

        titleTv.setText(title);

        return workView;
    }

    private View inflatePricesView(
            LayoutInflater inflater, JSONObject jsonObject, boolean hasDiscount) {
        View view = inflater.inflate(R.layout.orders_price_layout, null, false);
        if (hasDiscount) {
            float discountPrice = (float) jsonObject.optDouble("aidmoney", 0);
            String aidString = JSONUtil.getString(jsonObject.optJSONObject("ruleinfo"),
                    "description");

            view.findViewById(R.id.discount_layout)
                    .setVisibility(View.VISIBLE);
            TextView discountPriceTv = (TextView) view.findViewById(R.id.tv_discount);
            if (discountPrice > 0) {
                discountPriceTv.setText(getString(R.string.label_price,
                        Util.formatDouble2String(discountPrice) + ""));
            } else if (!JSONUtil.isEmpty(aidString)) {
                discountPriceTv.setText(aidString);
            } else {
                view.findViewById(R.id.discount_layout)
                        .setVisibility(View.GONE);
            }
        } else {
            view.findViewById(R.id.discount_layout)
                    .setVisibility(View.GONE);
        }
        double totalPrice = jsonObject.optDouble("allmoney", 0) - jsonObject.optDouble("aidmoney",
                0);
        TextView totalPriceTv = (TextView) view.findViewById(R.id.tv_total);
        totalPriceTv.setText(getString(R.string.label_price,
                Util.formatDouble2String(totalPrice) + ""));

        allTotalPrice += totalPrice;
        allMoney += jsonObject.optDouble("allmoney", 0);

        return view;
    }

    public void showRedPackets(View view) {
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        new HljTracker.Builder(this).eventableType("AvailableRedPacket")
                .screen("v2_submit_order")
                .action("hit")
                .build()
                .add();
        if (dialog == null) {
            dialog = new Dialog(this, R.style.BubbleDialogTheme);
            View v = getLayoutInflater().inflate(R.layout.dialog_red_packets, null);
            v.findViewById(R.id.btn_close)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

            ListView listView = (ListView) v.findViewById(R.id.list);
            PacketsListAdapter packetsListAdapter = new PacketsListAdapter(redPackets,
                    OrderConfirmActivity.this);
            listView.setAdapter(packetsListAdapter);

            dialog.setContentView(v);

            Window win = dialog.getWindow();
            WindowManager.LayoutParams params = win.getAttributes();
            Point point = JSONUtil.getDeviceSize(this);
            params.width = point.x;
            params.height = point.y / 2;
            win.setGravity(Gravity.BOTTOM);
            win.setWindowAnimations(R.style.dialog_anim_rise_style);
        }

        dialog.show();
    }

    private class PacketsListAdapter extends BaseAdapter {

        private ArrayList<RedPacket> mData;
        private Context mContext;

        public PacketsListAdapter(ArrayList<RedPacket> data, Context context) {
            this.mData = data;
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return mData == null ? 0 : mData.size();
        }

        @Override
        public Object getItem(int position) {
            return mData == null ? null : mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mData == null ? -1 : mData.get(position)
                    .getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext)
                        .inflate(R.layout.red_packet_item_view2, parent, false);
                ViewHolder holder = new ViewHolder();
                holder.textView = (TextView) convertView.findViewById(R.id.tv_amount);
                convertView.setTag(holder);
            }
            RedPacket redPacket = mData.get(position);
            if (redPacket != null) {
                ViewHolder holder = (ViewHolder) convertView.getTag();
                if (holder != null) {
                    holder.textView.setText(getString(R.string.label_red_packet_item,
                            Util.formatDouble2String(redPacket.getAmount()),
                            redPacket.getRedPacketName()));
                }
            }

            return convertView;
        }

        private class ViewHolder {
            TextView textView;
        }
    }

    private void setCustomerInfoView() {
        customerInfo = ServeCustomerInfoUtil.readServeCustomerInfo(this);

        if (!TextUtils.isEmpty(customerInfo.getCustomerName())) {
            serveCustomerTv.setText(customerInfo.getCustomerName());
        }
        if (!TextUtils.isEmpty(customerInfo.getCustomerPhone())) {
            phoneTv.setText(customerInfo.getCustomerPhone());
        }
        if (needWeddingTime) {
            serveTimeLayout.setVisibility(View.VISIBLE);
            if (customerInfo.getServeTime() != null) {
                serveTimeTv.setText(customerInfo.getServeTime()
                        .toString("yyyy-MM-dd"));
            }
        } else {
            serveTimeLayout.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        setCustomerInfoView();
        super.onResume();
    }
}
