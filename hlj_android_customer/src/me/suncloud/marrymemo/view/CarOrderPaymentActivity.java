package me.suncloud.marrymemo.view;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljpaymentlibrary.PayConfig;
import com.hunliji.hljpaymentlibrary.models.PayRxEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.PacketsListAdapter;
import me.suncloud.marrymemo.model.MessageEvent;
import me.suncloud.marrymemo.model.RedPacket;
import me.suncloud.marrymemo.model.ReturnStatus;
import me.suncloud.marrymemo.task.StatusHttpPostTask;
import me.suncloud.marrymemo.task.StatusRequestListener;
import me.suncloud.marrymemo.util.DataConfig;
import me.suncloud.marrymemo.util.DialogUtil;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.widget.CheckableLinearLayoutButton;
import me.suncloud.marrymemo.widget.CheckableLinearLayoutGroup;
import rx.Subscriber;


public class CarOrderPaymentActivity extends HljBaseActivity implements
        CheckableLinearLayoutGroup.OnCheckedChangeListener {

    @BindView(R.id.line_layout)
    View lineLayout;
    @BindView(R.id.tv_total_actual_price)
    TextView tvTotalActualPrice;
    @BindView(R.id.tv_paid_money)
    TextView tvPaidMoney;
    @BindView(R.id.btn_pay)
    Button btnPay;
    @BindView(R.id.bottom_layout)
    LinearLayout bottomLayout;
    @BindView(R.id.tv_pay_all_saved)
    CheckedTextView tvPayAllSaved;
    @BindView(R.id.pay_all)
    CheckableLinearLayoutButton payAll;
    @BindView(R.id.tv_deposit_need)
    CheckedTextView tvDepositNeed;
    @BindView(R.id.pay_deposit)
    CheckableLinearLayoutButton payDeposit;
    @BindView(R.id.disable_deposit_layout)
    TextView disableDepositLayout;
    @BindView(R.id.order_pay_type)
    CheckableLinearLayoutGroup orderPayType;
    @BindView(R.id.tv_saved_amount)
    TextView tvSavedAmount;
    @BindView(R.id.red_packet_layout)
    LinearLayout redPacketLayout;
    @BindView(R.id.content_layout)
    LinearLayout contentLayout;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.arrow)
    ImageView arrow;
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
    @BindView(R.id.tv_available_count)
    TextView tvAvailableCount;

    private Long id;
    private double totalPrice;
    private String extraJSONString;
    private boolean backToList;
    private Dialog dialog;
    private ArrayList<RedPacket> redPackets;
    private RedPacket selectedRedPacket;
    private RedPacket pendingRedPacket;
    private Dialog confirmDialog;
    // 已绑定的红包信息,没有的话说明没有使用过红包
    private String bindRedPacketNo;
    private boolean isPayRest;
    private double paidMoney;
    private double payAllSavedMoney;
    private double depositPercent;
    private Subscriber<PayRxEvent> paySubscriber;
    private Subscriber<RxEvent> rxSubscriber;

    private double realPay;
    private PacketsListAdapter packetsListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_order_payment);
        ButterKnife.bind(this);

        id = getIntent().getLongExtra("id", 0);
        totalPrice = getIntent().getDoubleExtra("total_price", 0);
        extraJSONString = getIntent().getStringExtra("extra_json_string");
        backToList = getIntent().getBooleanExtra("back_to_list", false);
        bindRedPacketNo = getIntent().getStringExtra("red_packet_no");
        double bindRedPacketMoney = getIntent().getDoubleExtra("red_packet_money", 0);
        isPayRest = getIntent().getBooleanExtra("is_pay_rest", false);
        payAllSavedMoney = getIntent().getDoubleExtra("pay_all_saved_money", 0);
        depositPercent = getIntent().getDoubleExtra("deposit_percent", 0);

        if (id > 0) {
            redPacketLayout.setVisibility(View.VISIBLE);
            contentLayout.setVisibility(View.VISIBLE);
            bottomLayout.setVisibility(View.VISIBLE);
        } else {
            redPacketLayout.setVisibility(View.GONE);
            contentLayout.setVisibility(View.GONE);
            bottomLayout.setVisibility(View.GONE);
        }

        // 如果没有绑定红包,则正常流程显示红包选择等流程
        if (JSONUtil.isEmpty(bindRedPacketNo)) {
            progressBar.setVisibility(View.VISIBLE);
            redPackets = new ArrayList<>();
            getRedPacketList();
            redPacketLayout.setClickable(true);
            arrow.setVisibility(View.VISIBLE);
        } else {
            // 如果绑定了红包,则只显示已绑定的红包金额,不能再次替换红包
            contentLayout.setVisibility(View.VISIBLE);
            bottomLayout.setVisibility(View.VISIBLE);
            redPacketLayout.setVisibility(View.VISIBLE);
            selectedRedPacket = new RedPacket(null);
            selectedRedPacket.setTicketNo(bindRedPacketNo);
            selectedRedPacket.setId(0);
            selectedRedPacket.setAmount(bindRedPacketMoney);

            arrow.setVisibility(View.GONE);
            tvSavedAmount.setText(Html.fromHtml(getString(R.string.label_saved_money,
                    Util.formatDouble2String(selectedRedPacket.getAmount()))));
            redPacketLayout.setClickable(false);
        }

        setOrderPayTypeView();
        setRedPacketView();
        setPrice();
    }

    @Override
    public void onBackPressed() {
        confirmBack();
    }

    public void confirmBack() {
        if (confirmDialog != null && confirmDialog.isShowing()) {
            return;
        }
        if (confirmDialog == null) {
            confirmDialog = new Dialog(this, R.style.BubbleDialogTheme);
            View v = getLayoutInflater().inflate(R.layout.dialog_confirm, null);
            TextView msgAlertTv = (TextView) v.findViewById(R.id.tv_alert_msg);
            Button confirmBtn = (Button) v.findViewById(R.id.btn_confirm);
            Button cancelBtn = (Button) v.findViewById(R.id.btn_cancel);
            msgAlertTv.setText(R.string.msg_confirm_exit_payment);
            confirmBtn.setText(R.string.label_give_up);
            cancelBtn.setText(R.string.label_wrong_action);
            confirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    confirmDialog.dismiss();
                    // 回到订单列表
                    if (backToList) {
                        Intent intent = new Intent(CarOrderPaymentActivity.this,
                                MyOrderListActivity.class);
                        intent.putExtra(RouterPath.IntentPath.Customer.MyOrder.ARG_BACK_MAIN, true);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_left,
                                R.anim.activity_anim_default);
                        finish();
                    } else {
                        CarOrderPaymentActivity.super.onBackPressed();
                    }
                }
            });

            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    confirmDialog.dismiss();
                }
            });
            confirmDialog.setContentView(v);
            Window window = confirmDialog.getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            Point point = JSONUtil.getDeviceSize(this);
            params.width = Math.round(point.x * 27 / 32);
            window.setAttributes(params);
        }
        confirmDialog.show();
    }

    private void setOrderPayTypeView() {
        // 如果是在付余款,隐藏选择付款选项
        if (isPayRest) {
            orderPayType.setVisibility(View.GONE);
            paidMoney = getIntent().getDoubleExtra("paid_money", 0);
            setTitle(R.string.label_pay_rest_money3);
        } else {
            orderPayType.setVisibility(View.VISIBLE);
            orderPayType.setOnCheckedChangeListener(this);
            setTitle(R.string.title_activity_car_order_payment);

            // 显示支付全款优惠和定金金额
            tvPayAllSaved.setText(getString(R.string.label_discount_amount,
                    CommonUtil.formatDouble2String(payAllSavedMoney)));
            String depositStr = getString(R.string.label_price2,
                    CommonUtil.formatDouble2String(((double) Math.round(totalPrice *
                            depositPercent * 100)) / 100));
            tvDepositNeed.setText(getString(R.string.label_deposit_need2, depositStr));
        }
    }

    private void setPrice() {
        realPay = 0;

        if (isPayRest) {
            findViewById(R.id.price_layout).setVisibility(View.GONE);
            // 如果是支付余款的话,需要显示已付金额
            tvPaidMoney.setVisibility(View.VISIBLE);
            tvPaidMoney.setText(getString(R.string.label_paid_money2,
                    Util.formatDouble2String(paidMoney)));

            realPay = totalPrice - paidMoney;

            if (selectedRedPacket != null && selectedRedPacket.getId() != -1 && !JSONUtil.isEmpty(
                    selectedRedPacket.getTicketNo())) {
                realPay -= selectedRedPacket.getAmount();
            }
        } else {
            // 不是支付余款,显示实付金额
            tvTotalPrice.setText(getString(R.string.label_price,
                    Util.formatDouble2String(totalPrice)));
            tvPaidMoney.setVisibility(View.GONE);

            double redPacketMoney = 0;
            if (selectedRedPacket != null && selectedRedPacket.getId() != -1 && !JSONUtil.isEmpty(
                    selectedRedPacket.getTicketNo())) {
                redPacketMoney = selectedRedPacket.getAmount();
            }

            if (redPacketMoney > 0) {
                redPacketPriceLayout.setVisibility(View.VISIBLE);
                tvRedPacketPrice.setText(getString(R.string.label_price6,
                        Util.formatDouble2String(selectedRedPacket.getAmount())));
            } else {
                redPacketPriceLayout.setVisibility(View.GONE);
            }

            // 根据全额支付还是付定金的选择计算显示实付金额
            if (orderPayType.getCheckedRadioButtonId() == R.id.pay_all) {
                // 全额支付
                // 实付金额 = 全额 - 全额优惠 - 红包金额
                realPay = totalPrice - payAllSavedMoney - redPacketMoney;

                earnestPayLayout.setVisibility(View.GONE);
                if (payAllSavedMoney > 0) {
                    payAllSavedPriceLayout.setVisibility(View.VISIBLE);
                    tvPayAllSavedPrice.setText(getString(R.string.label_price6,
                            Util.formatDouble2String(payAllSavedMoney)));
                } else {
                    payAllSavedPriceLayout.setVisibility(View.GONE);
                }
                payAllPriceLayout.setVisibility(View.VISIBLE);
                tvPayAllPrice.setText(getString(R.string.label_price,
                        Util.formatDouble2String(realPay)));
                residualPriceLayout.setVisibility(View.GONE);
                tvEarnestPayRedPacketHint.setVisibility(View.GONE);

            } else if (orderPayType.getCheckedRadioButtonId() == R.id.pay_deposit) {
                // 定金支付
                // 定金 = 全额 * depositPercent
                realPay = ((double) Math.round(totalPrice * depositPercent * 100)) / 100;

                earnestPayLayout.setVisibility(View.VISIBLE);
                tvEarnestPayPrice.setText(getString(R.string.label_price6,
                        Util.formatDouble2String(realPay)));
                payAllSavedPriceLayout.setVisibility(View.GONE);
                payAllPriceLayout.setVisibility(View.GONE);
                residualPriceLayout.setVisibility(View.VISIBLE);
                tvResidualPrice.setText(getString(R.string.label_price,
                        Util.formatDouble2String(Math.max(0,
                                totalPrice - realPay - redPacketMoney))));
                tvEarnestPayRedPacketHint.setVisibility(redPacketMoney > 0 ? View.VISIBLE : View
                        .GONE);
            }
        }
        if (realPay < 0) {
            realPay = 0;
        }
        tvTotalActualPrice.setText(CommonUtil.formatDouble2String(realPay));
    }

    private void setRedPacketView() {
        if (!JSONUtil.isEmpty(bindRedPacketNo)) {
            return;
        }
        if (!isPayRest && !redPackets.isEmpty()) {
            tvAvailableCount.setText(getString(R.string.label_available_packet_count,
                    redPackets.size() - 1));
            tvAvailableCount.setVisibility(View.VISIBLE);
            //不是余额支付且红包不为空,则正常流程显示红包选择等流程
            redPacketLayout.setVisibility(View.VISIBLE);
            redPacketLayout.setClickable(true);
            arrow.setVisibility(View.VISIBLE);

            tvSavedAmount.setTextColor(getResources().getColor(R.color.colorBlack2));
            if (selectedRedPacket == null || selectedRedPacket.getId() == -1) {
                // 选择了不使用红包
                tvSavedAmount.setText(R.string.label_use_not_red_enve2);
            } else {
                tvSavedAmount.setText(Html.fromHtml(getString(R.string.label_saved_money,
                        Util.formatDouble2String(selectedRedPacket.getAmount()))));
            }

            arrow.setVisibility(View.VISIBLE);
        } else {
            redPacketLayout.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.red_packet_layout)
    void onSelectedRedPacket() {
        if (redPackets.isEmpty()) {
            return;
        }

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
                            tvSavedAmount.setText(R.string.label_use_not_red_enve2);
                        } else {
                            // 使用红包
                            tvSavedAmount.setText(Html.fromHtml(getString(R.string
                                            .label_saved_money,
                                    Util.formatDouble2String(selectedRedPacket.getAmount()))));
                        }

                        setPrice();
                    }
                },
                redPackets.indexOf(selectedRedPacket));
        dialog.show();
    }


    @OnClick(R.id.btn_pay)
    void submit() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id);
            if (selectedRedPacket != null && selectedRedPacket.getId() > 0 && JSONUtil.isEmpty(
                    bindRedPacketNo)) {
                jsonObject.put("rpid", selectedRedPacket.getTicketNo());
            }
            if (!isPayRest) {
                int payAll = 1;
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
        if (paySubscriber == null) {
            paySubscriber = initSubscriber();
        }
        if (rxSubscriber == null) {
            rxSubscriber = new RxBusSubscriber<RxEvent>() {
                @Override
                protected void onEvent(RxEvent rxEvent) {
                    switch (rxEvent.getType()) {
                        case ORDER_BIND:
                            if (selectedRedPacket != null && selectedRedPacket.getId() > 0) {
                                redPacketLayout.setClickable(false);
                                arrow.setVisibility(View.GONE);
                                // 请求返回后,绑定的红包,订单金额计算有改变,需要发送列表刷新的消息
                                EventBus.getDefault()
                                        .post(new MessageEvent(MessageEvent.EventType
                                                .CAR_ORDER_REFRESH_FLAG,
                                                null));
                            }
                            break;
                    }
                }
            };
            RxBus.getDefault()
                    .toObservable(RxEvent.class)
                    .subscribe(rxSubscriber);
        }

        ArrayList<String> payTypes = null;
        if (Session.getInstance()
                .getDataConfig(this) != null) {
            payTypes = Session.getInstance()
                    .getDataConfig(this)
                    .getPayTypes();
        }
        new PayConfig.Builder(this).params(jsonObject)
                .path(isPayRest ? Constants.HttpPath.CAR_PRODUCT_PAY_REST : Constants.HttpPath
                        .CAR_PROUDCT_PAY)
                .price(realPay)
                .subscriber(paySubscriber)
                .payAgents(payTypes, DataConfig.getPayAgents())
                .build()
                .pay();
    }

    private void getRedPacketList() {
        if (!JSONUtil.isEmpty(extraJSONString)) {
            new StatusHttpPostTask(this, new StatusRequestListener() {
                @Override
                public void onRequestCompleted(Object object, ReturnStatus returnStatus) {
                    progressBar.setVisibility(View.GONE);
                    JSONObject resultObject = (JSONObject) object;
                    if (resultObject != null) {
                        JSONArray jsonArray = resultObject.optJSONArray("list");
                        if (jsonArray != null && jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                RedPacket redPacket = new RedPacket(jsonArray.optJSONObject(i));
                                redPackets.add(redPacket);
                            }
                        }
                    }

                    // 设置红包显示
                    setRedPacketsInfo();
                }

                @Override
                public void onRequestFailed(ReturnStatus returnStatus, boolean network) {
                    progressBar.setVisibility(View.GONE);
                    setRedPacketsInfo();
                }
            }).execute(Constants.getAbsUrl(Constants.HttpPath.GET_CAR_RED_PACKT_LIST),
                    extraJSONString);
        } else {
            setRedPacketsInfo();
        }
    }

    private void setRedPacketsInfo() {
        contentLayout.setVisibility(View.VISIBLE);
        bottomLayout.setVisibility(View.VISIBLE);
        if (redPackets.size() > 0) {
            // 列表中"不使用红包"的占位项
            redPackets.add(new RedPacket(-1));

            if (selectedRedPacket == null) {
                selectedRedPacket = redPackets.get(0);
            }
        }
        if (packetsListAdapter != null) {
            packetsListAdapter.notifyDataSetChanged();
        }
        setRedPacketView();
        setPrice();
    }

    @Override
    public void onCheckedChanged(CheckableLinearLayoutGroup group, int checkedId) {
        setRedPacketView();
        setPrice();
    }

    @Override
    protected void onFinish() {
        CommonUtil.unSubscribeSubs(paySubscriber, rxSubscriber);
        super.onFinish();
    }

    private Subscriber<PayRxEvent> initSubscriber() {
        return new RxBusSubscriber<PayRxEvent>() {
            @Override
            protected void onEvent(PayRxEvent rxEvent) {
                switch (rxEvent.getType()) {
                    case PAY_SUCCESS:
                        // 支付成功,发送刷新订单列表的消息
                        EventBus.getDefault()
                                .post(new MessageEvent(MessageEvent.EventType
                                        .CAR_ORDER_REFRESH_FLAG,
                                        null));
                        Intent intent = new Intent(CarOrderPaymentActivity.this,
                                AfterPayActivity.class);
                        intent.putExtra(AfterPayActivity.ARG_ORDER_TYPE,
                                Constants.OrderType.WEDDING_CAR_ORDER);
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
                        overridePendingTransition(0, R.anim.activity_anim_default);
                        break;

                }
            }
        };
    }
}
