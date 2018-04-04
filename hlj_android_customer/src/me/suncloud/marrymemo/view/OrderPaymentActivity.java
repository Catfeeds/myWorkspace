package me.suncloud.marrymemo.view;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljpaymentlibrary.PayConfig;
import com.hunliji.hljpaymentlibrary.models.PayRxEvent;
import com.hunliji.hljtrackerlibrary.HljTracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.PacketsListAdapter;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.model.MessageEvent;
import me.suncloud.marrymemo.model.NewOrder;
import me.suncloud.marrymemo.model.NewOrderPacket;
import me.suncloud.marrymemo.model.RedPacket;
import me.suncloud.marrymemo.model.ReturnStatus;
import me.suncloud.marrymemo.model.orders.Installment;
import me.suncloud.marrymemo.task.NewHttpPostTask;
import me.suncloud.marrymemo.task.OnHttpRequestListener;
import me.suncloud.marrymemo.util.DataConfig;
import me.suncloud.marrymemo.util.DialogUtil;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.widget.CheckableLinearLayoutButton;
import me.suncloud.marrymemo.widget.CheckableLinearLayoutGroup;
import rx.Subscriber;

public class OrderPaymentActivity extends HljBaseActivity implements CheckableLinearLayoutGroup
        .OnCheckedChangeListener {

    @BindView(R.id.tv_total_price)
    TextView tvTotalPrice;
    @BindView(R.id.tv_earnest_pay_price)
    TextView tvEarnestPayPrice;
    @BindView(R.id.earnest_pay_layout)
    LinearLayout earnestPayLayout;
    @BindView(R.id.tv_red_packet_price)
    TextView tvRedPacketPrice;
    @BindView(R.id.red_packet_price_layout)
    LinearLayout redPacketPriceLayout;
    @BindView(R.id.tv_pay_all_saved_price)
    TextView tvPayAllSavedPrice;
    @BindView(R.id.pay_all_saved_price_layout)
    LinearLayout payAllSavedPriceLayout;
    @BindView(R.id.tv_pay_all_price)
    TextView tvPayAllPrice;
    @BindView(R.id.pay_all_price_layout)
    LinearLayout payAllPriceLayout;
    @BindView(R.id.tv_residual_price)
    TextView tvResidualPrice;
    @BindView(R.id.residual_price_layout)
    LinearLayout residualPriceLayout;
    @BindView(R.id.tv_earnest_pay_red_packet_hint)
    TextView tvEarnestPayRedPacketHint;
    @BindView(R.id.icon_arrow)
    ImageView iconArrow;
    @BindView(R.id.tv_stage_num)
    TextView tvStageNum;
    @BindView(R.id.installment_layout)
    RelativeLayout installmentLayout;

    private Dialog dialog;
    private ArrayList<RedPacket> redPackets;
    private RedPacket selectedRedPacket;
    private RedPacket pendingRedPacket;
    private TextView redPacketAmount;
    private View contentLayout;
    private View bottomLayout;
    private View progressBar;
    private LinearLayout orderTitlesLayout;
    private TextView serveTimeTv;
    private TextView priceTv;
    private TextView needPayMoneyTv;
    private TextView tvAvailableRedpacketCount;
    private SimpleDateFormat simpleDateFormat;
    private long orderId;
    private String orderNum;
    private double redPacketMoney;
    private boolean isPayRest;
    private TextView paidMoneyTv;
    private CheckableLinearLayoutGroup orderPayType;
    private CheckableLinearLayoutButton payDepositBtn;
    private TextView payAllSavedTv;
    private TextView depositMoneyTv;
    private TextView tvDisable;
    private View disableDepositLayout;
    private NewOrderPacket orderPacket;
    private boolean backToList;
    private double finalPayAmount;
    private boolean onRefreshing = false;

    private Dialog confirmDialog;
    private City myCity;
    private PacketsListAdapter packetsListAdapter;
    private Installment installment;
    private Subscriber<PayRxEvent> paySubscriber;
    private Subscriber<RxEvent> rxSubscriber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_payment);
        setOkButton(R.mipmap.icon_refresh_primary_44_44);
        ButterKnife.bind(this);
        setSwipeBackEnable(false);

        orderId = getIntent().getLongExtra("order_id", 0);
        orderNum = getIntent().getStringExtra("order_num");
        isPayRest = getIntent().getBooleanExtra("is_pay_rest", false);
        backToList = getIntent().getBooleanExtra("back_to_list", false);
        myCity = Session.getInstance()
                .getMyCity(this);

        redPacketAmount = (TextView) findViewById(R.id.tv_saved_amount);
        contentLayout = findViewById(R.id.content_layout);
        simpleDateFormat = new SimpleDateFormat(getString(R.string.format_date_type8),
                Locale.getDefault());
        progressBar = findViewById(R.id.progressBar);
        bottomLayout = findViewById(R.id.bottom_layout);
        tvAvailableRedpacketCount = (TextView) findViewById(R.id.tv_available_count);
        orderTitlesLayout = (LinearLayout) findViewById(R.id.orders_layout);
        serveTimeTv = (TextView) findViewById(R.id.tv_serve_time);
        priceTv = (TextView) findViewById(R.id.tv_price);
        needPayMoneyTv = (TextView) findViewById(R.id.tv_total_actual_price);
        orderPayType = (CheckableLinearLayoutGroup) findViewById(R.id.order_pay_type);
        payDepositBtn = (CheckableLinearLayoutButton) findViewById(R.id.pay_deposit);
        payAllSavedTv = (TextView) findViewById(R.id.tv_pay_all_saved);
        depositMoneyTv = (TextView) findViewById(R.id.tv_deposit_need);
        paidMoneyTv = (TextView) findViewById(R.id.tv_paid_money);
        tvDisable = (TextView) findViewById(R.id.tv_disable_reason);
        orderPayType.setOnCheckedChangeListener(this);
        disableDepositLayout = findViewById(R.id.disable_deposit_layout);

        // 如果是在付余款的话，隐藏选择付款选项
        if (isPayRest) {
            orderPayType.setVisibility(View.GONE);
        } else {
            orderPayType.setVisibility(View.VISIBLE);
        }

        if (!JSONUtil.isEmpty(orderNum)) {
            redPackets = new ArrayList<>();
            progressBar.setVisibility(View.VISIBLE);
            postRefreshOrder();
            new GetPaymentPreInfoTask().executeOnExecutor(Constants.INFOTHEADPOOL);
        } else {
            Toast.makeText(OrderPaymentActivity.this,
                    R.string.msg_wrong_order_info,
                    Toast.LENGTH_SHORT)
                    .show();
        }

    }

    @Override
    public void onBackPressed() {
        if (isPayRest) {
            if (backToList) {
                Intent intent = new Intent(OrderPaymentActivity.this, MyOrderListActivity.class);
                intent.putExtra(RouterPath.IntentPath.Customer.MyOrder.ARG_BACK_MAIN, true);
                intent.putExtra(RouterPath.IntentPath.Customer.MyOrder.ARG_SELECT_TAB,
                        RouterPath.IntentPath.Customer.MyOrder.Tab.SERVICE_ORDER);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.activity_anim_default);
                finish();
            } else {
                OrderPaymentActivity.super.onBackPressed();
            }
        } else {
            confirmBack();
        }
    }

    public void confirmBack() {
        if (confirmDialog != null && confirmDialog.isShowing()) {
            return;
        }
        if (confirmDialog == null) {
            confirmDialog = DialogUtil.createDoubleButtonDialog(confirmDialog,
                    this,
                    getString(R.string.msg_confirm_exit_payment),
                    getString(R.string.label_give_up),
                    getString(R.string.label_wrong_action),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            confirmDialog.dismiss();
                            if (orderPacket != null) {
                                new HljTracker.Builder(OrderPaymentActivity.this).eventableId(
                                        orderPacket.getId())
                                        .eventableType("OrderV2")
                                        .screen("v2_pay")
                                        .action("hit_cancel_pay")
                                        .build()
                                        .add();
                            }
                            // 回到订单列表
                            if (backToList) {
                                Intent intent = new Intent(OrderPaymentActivity.this,
                                        MyOrderListActivity.class);
                                intent.putExtra(RouterPath.IntentPath.Customer.MyOrder
                                                .ARG_BACK_MAIN,
                                        true);
                                intent.putExtra(RouterPath.IntentPath.Customer.MyOrder
                                                .ARG_SELECT_TAB,
                                        RouterPath.IntentPath.Customer.MyOrder.Tab.SERVICE_ORDER);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_left,
                                        R.anim.activity_anim_default);
                                finish();
                            } else {
                                OrderPaymentActivity.super.onBackPressed();
                            }
                        }
                    });
        }
        confirmDialog.show();
    }

    private void setPayTypeMenu() {
        if (orderPacket == null || isPayRest) {
            return;
        }
        if (installment != null) {
            orderPayType.setVisibility(View.GONE);
            installmentLayout.setVisibility(View.VISIBLE);
            tvStageNum.setText(getString(R.string.format_stage_num,
                    installment.getDetails()
                            .get(0)
                            .getStageNum()));
            return;
        }
        orderPayType.setVisibility(View.VISIBLE);
        String gift = null;
        if (orderPacket.getRuleOrders() != null && !orderPacket.getRuleOrders()
                .isEmpty()) {
            gift = orderPacket.getRuleOrders()
                    .get(0)
                    .getPayAllGift();
        }
        if (JSONUtil.isEmpty(gift) && orderPacket.getOtherOrders() != null && !orderPacket
                .getOtherOrders()
                .isEmpty()) {
            gift = orderPacket.getOtherOrders()
                    .get(0)
                    .getPayAllGift();
        }
        if (orderPacket.getPayAllSavedMoney() > 0) {
            payAllSavedTv.setText(getString(R.string.label_discount_amount2,
                    Util.formatDouble2String(orderPacket.getPayAllSavedMoney())));
        } else if (!JSONUtil.isEmpty(gift)) {
            payAllSavedTv.setText(getString(R.string.label_pay_all_gift2, gift));
        } else {
            payAllSavedTv.setText("");
        }
        if (orderPacket.isAllowEarnest()) {
            disableDepositLayout.setVisibility(View.GONE);
            payDepositBtn.setVisibility(View.VISIBLE);
            depositMoneyTv.setText(getString(R.string.label_deposit_need3,
                    Util.formatDouble2String(orderPacket.getDepositMoney())));
        } else {
            // 不支持定金支付
            disableDepositLayout.setVisibility(View.VISIBLE);
            payDepositBtn.setVisibility(View.GONE);
            tvDisable.setText(orderPacket.getEarnestDisableReason());
            disableDepositLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Util.showToast(OrderPaymentActivity.this,
                            null,
                            orderPacket.getDisableType() == 2 ? R.string.msg_disable_deposit_1 :
                                    R.string.msg_disable_deposit_2);
                }
            });
        }

    }

    private void setPriceLayout(double finalPayAmount) {
        if (orderPacket == null || isPayRest || installment != null) {
            findViewById(R.id.price_layout).setVisibility(View.GONE);
            return;
        }
        findViewById(R.id.price_layout).setVisibility(View.VISIBLE);
        redPacketMoney = selectedRedPacket == null ? 0 : selectedRedPacket.getAmount();
        tvTotalPrice.setText(getString(R.string.label_price,
                Util.formatDouble2String(orderPacket.getAllMoney() - orderPacket.getAidMoney())));
        if (redPacketMoney > 0) {
            redPacketPriceLayout.setVisibility(View.VISIBLE);
            tvRedPacketPrice.setText(getString(R.string.label_price6,
                    Util.formatDouble2String(redPacketMoney)));
        } else {
            redPacketPriceLayout.setVisibility(View.GONE);
        }
        switch (orderPayType.getCheckedRadioButtonId()) {
            case R.id.pay_all:
                earnestPayLayout.setVisibility(View.GONE);
                if (orderPacket.getPayAllSavedMoney() > 0) {
                    payAllSavedPriceLayout.setVisibility(View.VISIBLE);
                    tvPayAllSavedPrice.setText(getString(R.string.label_price6,
                            Util.formatDouble2String(orderPacket.getPayAllSavedMoney())));
                } else {
                    payAllSavedPriceLayout.setVisibility(View.GONE);
                }
                payAllPriceLayout.setVisibility(View.VISIBLE);
                tvPayAllPrice.setText(getString(R.string.label_price,
                        Util.formatDouble2String(finalPayAmount)));
                residualPriceLayout.setVisibility(View.GONE);
                tvEarnestPayRedPacketHint.setVisibility(View.GONE);
                break;
            case R.id.pay_deposit:
                earnestPayLayout.setVisibility(View.VISIBLE);
                tvEarnestPayPrice.setText(getString(R.string.label_price6,
                        Util.formatDouble2String(finalPayAmount)));
                payAllSavedPriceLayout.setVisibility(View.GONE);
                payAllPriceLayout.setVisibility(View.GONE);
                residualPriceLayout.setVisibility(View.VISIBLE);
                tvResidualPrice.setText(getString(R.string.label_price,
                        Util.formatDouble2String(Math.max(0,
                                orderPacket.getAllMoney() - orderPacket.getAidMoney() -
                                        finalPayAmount - redPacketMoney))));
                tvEarnestPayRedPacketHint.setVisibility(redPacketMoney > 0 ? View.VISIBLE : View
                        .GONE);
                break;
        }
    }

    private void refreshPrices() {
        if (orderPacket == null) {
            return;
        }
        redPacketMoney = selectedRedPacket == null ? 0 : selectedRedPacket.getAmount();
        if (redPacketMoney > 0) {
            // 使用红包
            redPacketAmount.setTextColor(ContextCompat.getColor(OrderPaymentActivity.this,
                    R.color.colorBlack2));
            redPacketAmount.setText(Html.fromHtml(getString(R.string.label_saved_money,
                    Util.formatDouble2String(redPacketMoney))));
        } else if (findViewById(R.id.red_packet_layout).getVisibility() == View.VISIBLE) {
            // 不使用红包
            redPacketAmount.setTextColor(ContextCompat.getColor(OrderPaymentActivity.this,
                    R.color.colorBlack2));
            redPacketAmount.setText(R.string.label_use_not_red_enve2);
        }

        if (isPayRest) {
            // 如果是支付余款的话，需要显示已付金额
            paidMoneyTv.setVisibility(View.VISIBLE);
            paidMoneyTv.setText(getString(R.string.label_paid_money2,
                    Util.formatDouble2String(orderPacket.getPaidMoney())));

            double needPayMoney = orderPacket.getAllMoney() - orderPacket.getAidMoney() -
                    redPacketMoney - orderPacket.getPaidMoney();
            finalPayAmount = needPayMoney > 0 ? needPayMoney : 0;
            needPayMoneyTv.setText(Util.formatDouble2String(finalPayAmount));
        } else {
            switch (orderPayType.getCheckedRadioButtonId()) {
                case R.id.pay_all:
                    // 只有在选择全额支付下才能使用红包
                    double needPayMoney = orderPacket.getAllMoney() - orderPacket
                            .getPayAllSavedMoney() - redPacketMoney - orderPacket.getAidMoney();
                    finalPayAmount = needPayMoney > 0 ? needPayMoney : 0;
                    break;
                case R.id.pay_deposit:
                    // 定金支付情况下，不能使用红包，但可以绑定红包
                    needPayMoney = orderPacket.getDepositMoney();
                    finalPayAmount = needPayMoney > 0 ? needPayMoney : 0;
                    break;
            }
            needPayMoneyTv.setText(Util.formatDouble2String(finalPayAmount));
            paidMoneyTv.setVisibility(View.GONE);

            setPriceLayout(finalPayAmount);
        }
    }

    @Override
    public void onCheckedChanged(CheckableLinearLayoutGroup group, int checkedId) {
        refreshPrices();
    }

    @Override
    public void onOkButtonClick() {
        if (!onRefreshing) {
            bottomLayout.setVisibility(View.GONE);
            contentLayout.setVisibility(View.GONE);
            redPacketMoney = 0;
            redPackets = new ArrayList<>();
            progressBar.setVisibility(View.VISIBLE);
            postRefreshOrder();
            new GetPaymentPreInfoTask().executeOnExecutor(Constants.INFOTHEADPOOL);
        }
        super.onOkButtonClick();
    }

    private void postRefreshOrder() {
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
                    contentLayout.setVisibility(View.VISIBLE);
                    bottomLayout.setVisibility(View.VISIBLE);
                    JSONObject jsonObject = (JSONObject) obj;
                    orderPacket = new NewOrderPacket(jsonObject.optJSONObject("data"));

                    // 显示所有套餐的title
                    ArrayList<NewOrder> allOrders = new ArrayList<>();
                    allOrders.addAll(orderPacket.getRuleOrders());
                    allOrders.addAll(orderPacket.getOtherOrders());
                    if (allOrders.size() > 0) {
                        installment = null;
                        orderTitlesLayout.removeAllViews();
                        for (NewOrder order : allOrders) {
                            if (order.isInstallment() && order.getInstallment() != null) {
                                installment = order.getInstallment();
                            }
                            TextView title = new TextView(OrderPaymentActivity.this);
                            title.setSingleLine(true);
                            title.setText(order.getTitle());
                            title.setTextColor(ContextCompat.getColor(OrderPaymentActivity.this,
                                    R.color.colorBlack2));
                            title.setTextSize(14);
                            title.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
                            orderTitlesLayout.addView(title);
                        }

                    }
                    // 显示服务时间
                    if (orderPacket.getWeddingTime() != null) {
                        findViewById(R.id.wedding_time_layout).setVisibility(View.VISIBLE);
                        serveTimeTv.setText(simpleDateFormat.format(orderPacket.getWeddingTime()));
                    } else {
                        findViewById(R.id.wedding_time_layout).setVisibility(View.GONE);
                    }
                    priceTv.setText(getString(R.string.label_price,
                            Util.formatDouble2String(orderPacket.getAllMoney() - orderPacket
                                    .getAidMoney())));
                    setPayTypeMenu();
                    setRedPacketLayout();
                    refreshPrices();
                }
            }

            @Override
            public void onRequestFailed(Object obj) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(OrderPaymentActivity.this,
                        R.string.msg_fail_to_referesh_order_info,
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }).execute(Constants.getAbsUrl(Constants.HttpPath.POST_FOR_ORDER_DETAIL),
                jsonObject.toString());
    }


    private class GetPaymentPreInfoTask extends AsyncTask<String, Object, JSONObject> {

        public GetPaymentPreInfoTask() {
            onRefreshing = true;
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            String url = String.format(Constants.getAbsUrl(Constants.HttpPath
                            .GET_ORDER_PAYMENT_PRE_INFO),
                    orderId,
                    myCity.getId());
            try {
                String json = JSONUtil.getStringFromUrl(url);
                if (JSONUtil.isEmpty(json)) {
                    return null;
                }

                return new JSONObject(json);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (jsonObject != null) {
                ReturnStatus returnStatus = new ReturnStatus(jsonObject.optJSONObject("status"));
                if (returnStatus.getRetCode() == 0 && !jsonObject.isNull("data")) {
                    JSONObject dataObj = jsonObject.optJSONObject("data");

                    // 再处理红包数据
                    JSONArray redPacketArr = dataObj.optJSONArray("redPacket");
                    if (redPacketArr != null && redPacketArr.length() > 0) {
                        for (int i = 0; i < redPacketArr.length(); i++) {
                            RedPacket redPacket = new RedPacket(redPacketArr.optJSONObject(i));
                            if (JSONUtil.isEmpty(redPacket.getTicketNo())) {
                                continue;
                            }
                            redPackets.add(redPacket);
                        }
                        if (redPackets.size() > 0) {
                            // 不使用红包的列表占位实体
                            redPackets.add(new RedPacket(-1));
                            tvAvailableRedpacketCount.setText(getString(R.string
                                            .label_available_packet_count,
                                    redPackets.size() - 1));
                            // 默认选中第一个
                            if (selectedRedPacket == null) {
                                selectedRedPacket = redPackets.get(0);
                            }
                        }
                        if (packetsListAdapter != null) {
                            packetsListAdapter.notifyDataSetChanged();
                        }
                        if (orderPacket != null) {
                            progressBar.setVisibility(View.GONE);
                            contentLayout.setVisibility(View.VISIBLE);
                            bottomLayout.setVisibility(View.VISIBLE);
                            setRedPacketLayout();
                            refreshPrices();
                        }
                    }
                }
            }
            onRefreshing = false;
            super.onPostExecute(jsonObject);
        }
    }

    private void setRedPacketLayout() {
        if (orderPacket == null) {
            return;
        }
        if (redPackets.isEmpty() && JSONUtil.isEmpty(orderPacket.getRedPacketNo())) {
            findViewById(R.id.red_packet_layout).setVisibility(View.GONE);
        } else if (!JSONUtil.isEmpty(orderPacket.getRedPacketNo())) {
            findViewById(R.id.red_packet_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.red_packet_layout).setClickable(false);
            tvAvailableRedpacketCount.setVisibility(View.GONE);
            selectedRedPacket = new RedPacket(new JSONObject());
            selectedRedPacket.setTicketNo(orderPacket.getRedPacketNo());
            selectedRedPacket.setAmount(orderPacket.getRedPacketMoney());
            iconArrow.setVisibility(View.GONE);
        } else {
            findViewById(R.id.red_packet_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.red_packet_layout).setClickable(true);
            tvAvailableRedpacketCount.setVisibility(View.VISIBLE);
            iconArrow.setVisibility(View.VISIBLE);
        }
    }

    public void onPay(View view) {
        if (orderPacket == null) {
            return;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("order_no", orderNum);
            if (selectedRedPacket != null && !JSONUtil.isEmpty(selectedRedPacket.getTicketNo())) {
                jsonObject.put("red_package_no", selectedRedPacket.getTicketNo());
            }
            if (!isPayRest) {
                int payAll = 0;
                switch (orderPayType.getCheckedRadioButtonId()) {
                    case R.id.pay_all:
                        payAll = 1;
                        break;
                    case R.id.pay_deposit:
                        payAll = 0;
                        break;
                }
                jsonObject.put("payall", payAll);
            }
        } catch (JSONException ignored) {

        }
        String postUrl = Constants.HttpPath.POST_ORDER_PAY;
        if (isPayRest) {
            postUrl = Constants.HttpPath.POST_ORDER_PAY_REST;
        }
        if (paySubscriber == null) {
            paySubscriber = new RxBusSubscriber<PayRxEvent>() {
                @Override
                protected void onEvent(PayRxEvent rxEvent) {
                    switch (rxEvent.getType()) {
                        case PAY_SUCCESS:
                            EventBus.getDefault()
                                    .post(new MessageEvent(MessageEvent.EventType
                                            .SERVICE_ORDER_REFRESH_FLAG,
                                            null));
                            Intent intent = new Intent(OrderPaymentActivity.this,
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

                    }
                }
            };
        }
        if (rxSubscriber == null) {
            rxSubscriber = new RxBusSubscriber<RxEvent>() {
                @Override
                protected void onEvent(RxEvent rxEvent) {
                    switch (rxEvent.getType()) {
                        case ORDER_BIND:
                            if (selectedRedPacket != null && selectedRedPacket.getId() > 0) {
                                EventBus.getDefault()
                                        .post(new MessageEvent(MessageEvent.EventType
                                                .SERVICE_ORDER_REFRESH_FLAG,
                                                null));
                                findViewById(R.id.red_packet_layout).setClickable(false);
                                tvAvailableRedpacketCount.setVisibility(View.GONE);
                                iconArrow.setVisibility(View.GONE);
                            }
                            break;
                    }
                }
            };
            RxBus.getDefault()
                    .toObservable(RxEvent.class)
                    .subscribe(rxSubscriber);
        }
        PayConfig.Builder builder = new PayConfig.Builder(this);
        if (installment != null) {
            switch (installment.getType()) {
                default:
                    DialogUtil.createSingleButtonDialog(null,
                            this,
                            getString(R.string.msg_installment_not_support),
                            null,
                            null)
                            .show();
                    return;
            }
        } else {
            DataConfig dataConfig = Session.getInstance()
                    .getDataConfig(this);
            builder.payAgents(dataConfig != null ? dataConfig.getPayTypes() : null,
                    DataConfig.getPayAgents());
        }
        builder.params(jsonObject)
                .path(postUrl)
                .price(finalPayAmount)
                .subscriber(paySubscriber)
                .build()
                .pay();
    }


    public void showRedPackets(View view) {
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        if (packetsListAdapter == null) {
            packetsListAdapter = new PacketsListAdapter(redPackets);
        }
        dialog = DialogUtil.createRedPacketDialog(dialog,
                this,
                packetsListAdapter,
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(
                            AdapterView<?> parent, View view, int position, long id) {
                        pendingRedPacket = (RedPacket) parent.getAdapter()
                                .getItem(position);
                    }
                },
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        if (pendingRedPacket != null) {
                            selectedRedPacket = pendingRedPacket;
                        }
                        if (selectedRedPacket == null) {
                            return;
                        }
                        if (selectedRedPacket.getId() < 0) {
                            // 不使用红包
                            redPacketAmount.setTextColor(getResources().getColor(R.color
                                    .colorBlack2));
                            redPacketAmount.setText(R.string.label_use_not_red_enve2);
                        } else {
                            // 使用红包
                            redPacketAmount.setTextColor(getResources().getColor(R.color
                                    .colorBlack2));
                            redPacketAmount.setText(Html.fromHtml(getString(R.string
                                            .label_saved_money,
                                    Util.formatDouble2String(selectedRedPacket.getAmount()))));
                        }

                        // 修改需付金额
                        redPacketMoney = selectedRedPacket.getAmount();
                        refreshPrices();
                    }
                },
                redPackets.indexOf(selectedRedPacket));
        dialog.show();
    }

    @Override
    protected void onFinish() {
        CommonUtil.unSubscribeSubs(paySubscriber, rxSubscriber);
        super.onFinish();
    }
}
