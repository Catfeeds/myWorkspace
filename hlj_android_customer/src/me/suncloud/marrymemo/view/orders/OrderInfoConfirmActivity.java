package me.suncloud.marrymemo.view.orders;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljpaymentlibrary.PayConfig;
import com.hunliji.hljpaymentlibrary.models.PayRxEvent;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.orders.OrderApi;
import me.suncloud.marrymemo.model.orders.ServiceOrder;
import me.suncloud.marrymemo.model.orders.ServiceOrderSubmitBody;
import me.suncloud.marrymemo.model.orders.ServiceOrderSubmitResponse;
import me.suncloud.marrymemo.util.DataConfig;
import me.suncloud.marrymemo.util.DialogUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.view.AfterPayActivity;
import me.suncloud.marrymemo.view.MyOrderListActivity;

public class OrderInfoConfirmActivity extends HljBaseActivity {

    @BindView(R.id.progress)
    ProgressBar progress;
    @BindView(R.id.tv_confirming)
    TextView tvConfirming;
    @BindView(R.id.tv_serve_time)
    TextView tvServeTime;
    @BindView(R.id.tv_serve_customer)
    TextView tvServeCustomer;
    @BindView(R.id.tv_contact_phone)
    TextView tvContactPhone;
    @BindView(R.id.tv_merchant_city)
    TextView tvMerchantCity;
    @BindView(R.id.serve_time_layout)
    LinearLayout serveTimeLayout;
    @BindView(R.id.customer_name_layout)
    LinearLayout customerNameLayout;
    @BindView(R.id.contact_phone_layout)
    LinearLayout contactPhoneLayout;
    @BindView(R.id.merchant_city_layout)
    LinearLayout merchantCityLayout;

    private String merchantCity;
    private ServiceOrderSubmitBody submitData;
    private HljHttpSubscriber submitSub;
    private RxBusSubscriber<PayRxEvent> paySubscriber;
    private double needPayMoney;
    private Handler handler = new Handler();

    //自动提交订单
    private Runnable submitRunnable = new Runnable() {
        @Override
        public void run() {
            tvConfirming.setText(R.string.label_info_confirmed);
            postOrder();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_info_confirm);
        ButterKnife.bind(this);

        initValues();
        initViews();
    }

    private void initValues() {
        merchantCity = getIntent().getStringExtra("merchant_city");
        needPayMoney = getIntent().getDoubleExtra("need_pay_money", 0);
        submitData = getIntent().getParcelableExtra("submit_data");
    }

    private void initViews() {
        if (submitData.getWeddingTime() != null) {
            serveTimeLayout.setVisibility(View.VISIBLE);
            tvServeTime.setText(submitData.getWeddingTime()
                    .toString("yyyy.MM.dd"));
        } else {
            serveTimeLayout.setVisibility(View.GONE);
        }
        if (TextUtils.isEmpty(submitData.getBuyerName())) {
            customerNameLayout.setVisibility(View.GONE);
        } else {
            customerNameLayout.setVisibility(View.VISIBLE);
            tvServeCustomer.setText(submitData.getBuyerName());
        }
        if (TextUtils.isEmpty(submitData.getBuyerPhone())) {
            contactPhoneLayout.setVisibility(View.GONE);
        } else {
            contactPhoneLayout.setVisibility(View.VISIBLE);
            tvContactPhone.setText(submitData.getBuyerPhone());
        }
        if (TextUtils.isEmpty(merchantCity)) {
            merchantCityLayout.setVisibility(View.GONE);
        } else {
            merchantCityLayout.setVisibility(View.VISIBLE);
            tvMerchantCity.setText(merchantCity);
        }

        // 开始五秒倒计时后自动提交订单
        handler.postDelayed(submitRunnable, 5 * 1000);
    }

    private void postOrder() {
        submitSub = HljHttpSubscriber.buildSubscriber(this)
                .setProgressDialog(DialogUtil.createProgressDialog(this))
                .setOnNextListener(new SubscriberOnNextListener<ServiceOrderSubmitResponse>() {
                    @Override
                    public void onNext(ServiceOrderSubmitResponse serviceOrderSubmitResponse) {
                        // 提交成功，直接付款
                        goPayOrder(serviceOrderSubmitResponse.getOrder());
                    }
                })
                .build();
        OrderApi.submitServiceOrder(submitData)
                .subscribe(submitSub);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 暂停或退出时，移出倒计时自动提交事件
        handler.removeCallbacks(submitRunnable);
    }

    /**
     * 跳转付款
     *
     * @param serviceOrder
     */
    private void goPayOrder(ServiceOrder serviceOrder) {
        if (paySubscriber == null) {
            paySubscriber = new RxBusSubscriber<PayRxEvent>() {
                @Override
                protected void onEvent(PayRxEvent rxEvent) {
                    Intent intent;
                    switch (rxEvent.getType()) {
                        case PAY_SUCCESS:
                            // 支付成功，跳转成功页面
                            intent = new Intent(OrderInfoConfirmActivity.this,
                                    AfterPayActivity.class);
                            intent.putExtra(AfterPayActivity.ARG_ORDER_TYPE,
                                    Constants.OrderType.NOMAL_WORK_ORDER);
                            if (rxEvent.getObject() != null && rxEvent.getObject() instanceof
                                    JsonObject) {
                                JsonObject orderResult = (JsonObject) rxEvent.getObject();
                                try {
                                    if (orderResult.get("free_order_link") != null) {
                                        intent.putExtra(AfterPayActivity.ARG_PATH,
                                                orderResult.get("free_order_link")
                                                        .getAsString());
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            startActivity(intent);
                            finish();
                            overridePendingTransition(0, 0);
                            break;
                        case PAY_CANCEL:
                            // 支付失败，跳转订单列表
                            goOrderListActivity();
                            break;
                    }
                }
            };
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("order_id", serviceOrder.getId());
            // 初次付款，付定金或者付全款
            jsonObject.put("pay_type", serviceOrder.getOrderPayType());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        PayConfig.Builder builder = new PayConfig.Builder(this);
        DataConfig dataConfig = Session.getInstance()
                .getDataConfig(this);
        builder.payAgents(dataConfig != null ? dataConfig.getPayTypes() : null,
                DataConfig.getPayAgents());
        builder.params(jsonObject)
                .path(Constants.getAbsUrl(Constants.HttpPath.SERVICE_ORDER_PAYMENT))
                .price(needPayMoney)
                .subscriber(paySubscriber)
                .build()
                .pay();
    }

    private void goOrderListActivity() {
        Intent intent = new Intent(OrderInfoConfirmActivity.this, MyOrderListActivity.class);
        intent.putExtra(RouterPath.IntentPath.Customer.MyOrder.ARG_BACK_MAIN, true);
        intent.putExtra(RouterPath.IntentPath.Customer.MyOrder.ARG_SELECT_TAB,
                RouterPath.IntentPath.Customer.MyOrder.Tab.SERVICE_ORDER);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.activity_anim_default, R.anim.slide_out_to_bottom);
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(paySubscriber, submitSub);
    }
}
