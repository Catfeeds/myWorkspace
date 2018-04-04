package me.suncloud.marrymemo.view;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.unionpay.UPPayAssistEx;
import com.unionpay.uppay.PayActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.PacketsListAdapter;
import me.suncloud.marrymemo.model.CustomSetmealOrder;
import me.suncloud.marrymemo.model.MessageEvent;
import me.suncloud.marrymemo.model.RedPacket;
import me.suncloud.marrymemo.model.ReturnStatus;
import me.suncloud.marrymemo.model.UserBindBankCard;
import me.suncloud.marrymemo.task.AsyncBitmapDrawable;
import me.suncloud.marrymemo.task.ImageLoadTask;
import me.suncloud.marrymemo.task.StatusHttpPostTask;
import me.suncloud.marrymemo.task.StatusRequestListener;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.LLPayer;
import me.suncloud.marrymemo.util.Result;
import me.suncloud.marrymemo.util.ScaleMode;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.widget.CheckableLinearLayoutButton;
import me.suncloud.marrymemo.widget.CheckableLinearLayoutGroup;

public class CustomSetmealOrderPaymentActivity extends HljBaseActivity implements View
        .OnClickListener, CheckableLinearLayoutGroup.OnCheckedChangeListener {

    @BindView(R.id.tv_total_actual_price)
    TextView tvTotalActualPrice;
    @BindView(R.id.tv_paid_money)
    TextView tvPaidMoney;
    @BindView(R.id.btn_pay)
    Button btnPay;
    @BindView(R.id.btn_order_action2)
    Button btnOrderAction2;
    @BindView(R.id.bottom_layout)
    LinearLayout bottomLayout;
    @BindView(R.id.line_layout)
    View lineLayout;
    @BindView(R.id.tv_label2)
    TextView tvLabel2;
    @BindView(R.id.tv_order_name)
    TextView tvOrderName;
    @BindView(R.id.tv_label4)
    TextView tvLabel4;
    @BindView(R.id.tv_price)
    TextView tvPrice;
    @BindView(R.id.tv_need_pay_rest)
    TextView tvNeedPayRest;
    @BindView(R.id.need_pay_rest_layout)
    LinearLayout needPayRestLayout;
    @BindView(R.id.tv_label1)
    TextView tvLabel1;
    @BindView(R.id.tv_serve_time)
    TextView tvServeTime;
    @BindView(R.id.wedding_time_layout)
    LinearLayout weddingTimeLayout;
    @BindView(R.id.tv_pay_all_saved)
    CheckedTextView tvPayAllSaved;
    @BindView(R.id.pay_all)
    CheckableLinearLayoutButton payAll;
    @BindView(R.id.tv_deposit_need)
    CheckedTextView tvDepositNeed;
    @BindView(R.id.pay_deposit)
    CheckableLinearLayoutButton payDeposit;
    @BindView(R.id.disable_deposit_layout)
    LinearLayout disableDepositLayout;
    @BindView(R.id.order_pay_type)
    CheckableLinearLayoutGroup orderPayType;
    @BindView(R.id.payment_select_layout)
    LinearLayout paymentSelectLayout;
    @BindView(R.id.tv_saved_amount)
    TextView tvSavedAmount;
    @BindView(R.id.arrow)
    ImageView arrow;
    @BindView(R.id.red_packet_layout)
    LinearLayout redPacketLayout;
    @BindView(R.id.et_pay_money)
    EditText etPayMoney;
    @BindView(R.id.add_new_card_layout)
    LinearLayout addNewCardLayout;
    @BindView(R.id.select_other_payment)
    RelativeLayout selectOtherPayment;
    @BindView(R.id.quick_pay)
    CheckableLinearLayoutButton quickPay;
    @BindView(R.id.limit_hint)
    Button limitHint;
    @BindView(R.id.union_pay)
    CheckableLinearLayoutButton unionPay;
    @BindView(R.id.alipay)
    CheckableLinearLayoutButton alipay;
    @BindView(R.id.pay_menu)
    CheckableLinearLayoutGroup payMenu;
    @BindView(R.id.pay_way_layout)
    RelativeLayout payWayLayout;
    @BindView(R.id.content_layout)
    LinearLayout contentLayout;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.order_action_layout)
    LinearLayout orderActionLayout;
    @BindView(R.id.pay_rest_layout)
    LinearLayout payRestLayout;
    @BindView(R.id.tv_available_count)
    TextView tvAvailableRedpacketCount;
    private CustomSetmealOrder order;
    private SimpleDateFormat simpleDateFormat;
    private String bindRedPacketNo;
    private double bindRedPacketMoney;
    private boolean isPayRest;
    private ArrayList<RedPacket> redPackets;
    private Dialog dialog;
    private Dialog confirmDialog;
    private ArrayList<UserBindBankCard> bindBankCards;
    private Dialog selectDialog;
    private double realPay;
    private RedPacket selectedRedPacket;
    private boolean isPayDeposit;
    private JSONObject shareObject;
    private static final int RQF_PAY = 1;

    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case RQF_PAY:
                    Result resultObj = new Result((String) msg.obj);
                    String resultStatus = resultObj.resultStatus;

                    if (progressBar != null) {
                        progressBar.setVisibility(View.GONE);
                    }
                    if (TextUtils.equals(resultStatus, "9000") || TextUtils.equals(resultStatus,
                            "8000")) {
                        // 支付成功,发送刷新订单列表的消息
                        EventBus.getDefault()
                                .post(new MessageEvent(MessageEvent.EventType
                                        .CUSTOM_SETMEAL_ORDER_REFRESH_FLAG,
                                        null));
                        Intent intent = new Intent(CustomSetmealOrderPaymentActivity.this,
                                AfterPayCustomSetmealOrderActivity.class);
                        if (shareObject != null) {
                            intent.putExtra("share_json", shareObject.toString());
                        }
                        JSONArray jsonArray = new JSONArray();
                        jsonArray.put(order.getId()
                                .intValue());
                        intent.putExtra("ids", jsonArray.toString());
                        intent.putExtra("is_custom_order", true);
                        intent.putExtra("is_deposit", !isPayRest && isPayDeposit);
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
                        Toast.makeText(CustomSetmealOrderPaymentActivity.this,
                                R.string.pay_result3,
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                    break;
                case Constants.PayResultStatus.WHAT_SUCCESS:
                    // 支付成功,发送刷新订单列表的消息
                    EventBus.getDefault()
                            .post(new MessageEvent(MessageEvent.EventType
                                    .CUSTOM_SETMEAL_ORDER_REFRESH_FLAG,
                                    null));
                    break;
                case Constants.PayResultStatus.WHAT_FAIL:
                    break;
                default:
                    break;
            }
            return false;
        }
    });
    private boolean backToList;
    private double editPayMoney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_setmeal_order_payment);
        ButterKnife.bind(this);
        setSwipeBackEnable(false);
        simpleDateFormat = new SimpleDateFormat(getString(R.string.format_date_type8));

        order = (CustomSetmealOrder) getIntent().getSerializableExtra("order");
        isPayRest = getIntent().getBooleanExtra("is_pay_rest", false);
        backToList = getIntent().getBooleanExtra("back_to_list", false);

        if (order != null) {
            setOrderInfo();
            redPacketLayout.setVisibility(View.VISIBLE);
            payMenu.setVisibility(View.VISIBLE);
            contentLayout.setVisibility(View.VISIBLE);
            bottomLayout.setVisibility(View.VISIBLE);
        } else {
            redPacketLayout.setVisibility(View.GONE);
            payMenu.setVisibility(View.GONE);
            contentLayout.setVisibility(View.GONE);
            bottomLayout.setVisibility(View.GONE);
        }

        // 如果没有绑定红包,则正常流程显示红包选择等流程
        if (JSONUtil.isEmpty(order.getRedPacketNo())) {
            progressBar.setVisibility(View.VISIBLE);
            redPackets = new ArrayList<>();
            getRedPacketList();
            redPacketLayout.setClickable(true);
            arrow.setVisibility(View.VISIBLE);
        } else {
            bindRedPacketNo = order.getRedPacketNo();
            bindRedPacketMoney = order.getRedPacketMoney();

            // 如果绑定了红包,则只显示已绑定的红包金额,不能再次替换红包
            selectedRedPacket = new RedPacket(null);
            selectedRedPacket.setTicketNo(bindRedPacketNo);
            selectedRedPacket.setId(0);
            selectedRedPacket.setAmount(bindRedPacketMoney);

            arrow.setVisibility(View.GONE);
            tvSavedAmount.setText(Html.fromHtml(getString(R.string.label_saved_money,
                    Util.formatDouble2String(selectedRedPacket.getAmount()))));
            redPacketLayout.setClickable(false);
        }

        progressBar.setVisibility(View.VISIBLE);
        bindBankCards = new ArrayList<>();
        new GetBankCardList().executeOnExecutor(Constants.LISTTHEADPOOL);

        orderPayType.setOnCheckedChangeListener(this);
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
                        Intent intent = new Intent(CustomSetmealOrderPaymentActivity.this,
                                MyOrderListActivity.class);
                        intent.putExtra(RouterPath.IntentPath.Customer.MyOrder.ARG_BACK_MAIN, true);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_left,
                                R.anim.activity_anim_default);
                        finish();
                    } else {
                        CustomSetmealOrderPaymentActivity.super.onBackPressed();
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

    private void setOrderInfo() {
        tvOrderName.setText(order.getCustomSetmeal()
                .getTitle());
        tvPrice.setText(getString(R.string.label_price,
                Util.roundDownDouble2StringPositive(order.getActualPrice())));
        tvServeTime.setText(simpleDateFormat.format(order.getWeddingTime()
                .toDate()));
        tvDepositNeed.setText(getString(R.string.label_deposit_need4,
                Util.roundDownDouble2StringPositive(order.getEarnestMoney())));
        if (isPayRest) {
            // 支付余款
            needPayRestLayout.setVisibility(View.VISIBLE);
            tvNeedPayRest.setText(getString(R.string.label_price,
                    Util.roundDownDouble2StringPositive(order.getActualPrice() - order
                            .getRedPacketMoney() - order.getPaidMoney())));
        } else {
            needPayRestLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 设置介个显示,并且根据定金支付还是继续支付显示不同的界面
     */
    private void setPrice() {
        realPay = 0;
        tvPaidMoney.setVisibility(View.GONE);

        if (isPayRest) {
            // 如果是支付余款的话,显示另一种提交按钮,并且去掉支付类型选择
            orderActionLayout.setVisibility(View.GONE);
            btnOrderAction2.setVisibility(View.VISIBLE);
            paymentSelectLayout.setVisibility(View.GONE);
            payRestLayout.setVisibility(View.VISIBLE);

            setTitle(R.string.label_continue_pay);

            if (editPayMoney > 0) {
                etPayMoney.setText(Util.roundDownDouble2StringPositive(editPayMoney));
            } else {
                etPayMoney.setText(Util.roundDownDouble2StringPositive(order.getActualPrice() -
                        order.getRedPacketMoney() - order.getPaidMoney()));
            }

        } else {
            // 不是支付余款,显示传统的支付按钮界面
            orderActionLayout.setVisibility(View.VISIBLE);
            btnOrderAction2.setVisibility(View.GONE);
            paymentSelectLayout.setVisibility(View.VISIBLE);
            payRestLayout.setVisibility(View.GONE);

            // 判断是否可以使用定金支付方式
            if (order.isEarnest()) {
                payDeposit.setVisibility(View.VISIBLE);
                disableDepositLayout.setVisibility(View.GONE);
            } else {
                payDeposit.setVisibility(View.GONE);
                disableDepositLayout.setVisibility(View.VISIBLE);
            }

            // 定金支付可以使用红包

            // 根据全额支付还是付定金的选择计算显示实付金额
            if (orderPayType.getCheckedRadioButtonId() == R.id.pay_all) {
                // 全额支付
                realPay = order.getActualPrice();
                // 全额支付和定金支付都可以使用红包,
                // 但是全额支付的时候付款金额要减去红包金额,而付定金的时候,付款的金额不能减去红包金额
                if (selectedRedPacket != null && selectedRedPacket.getId() != -1 && !JSONUtil
                        .isEmpty(
                        selectedRedPacket.getTicketNo())) {
                    realPay -= selectedRedPacket.getAmount();
                }
            } else if (orderPayType.getCheckedRadioButtonId() == R.id.pay_deposit) {
                // 定金支付
                realPay = order.getEarnestMoney();
                // 付定金是使用红包的话,提示红包的使用方式
                if (selectedRedPacket != null && selectedRedPacket.getId() >= 0) {
                    tvPaidMoney.setVisibility(View.VISIBLE);
                    tvPaidMoney.setText(R.string.label_custom_order_red_packet);
                }
            }
        }

        tvTotalActualPrice.setText(Util.roundUpDouble2StringPositive(realPay));
    }

    @OnClick(R.id.btn_pay)
    void onPayFirstTime() {
        onPay("", false);
    }

    @OnClick(R.id.btn_order_action2)
    void onPayRest() {
        onPay("", false);
    }

    private void onPay(String payAgent, boolean isAddNewCard) {
        boolean isBindCardPay = false;
        if (TextUtils.isEmpty(payAgent)) {
            switch (payMenu.getCheckedRadioButtonId()) {
                case R.id.alipay:
                    payAgent = "alipay";
                    break;
                case R.id.union_pay:
                    payAgent = "unionpay";
                    break;
                case R.id.quick_pay:
                    payAgent = "llpay";
                    break;
                default:
                    isBindCardPay = true;
                    payAgent = "llpay";
                    break;
            }
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", order.getId());
            jsonObject.put("agent", payAgent);
            if (selectedRedPacket != null && selectedRedPacket.getId() != 0 && JSONUtil.isEmpty(
                    bindRedPacketNo)) {
                jsonObject.put("rpid", selectedRedPacket.getTicketNo());
            }

            if (!isPayRest) {
                // 非支付余款的时候才需要判断是否是全款支付
                int isPayAll = 1;
                switch (orderPayType.getCheckedRadioButtonId()) {
                    case R.id.pay_all:
                        isPayAll = 1;
                        isPayDeposit = false;
                        break;
                    case R.id.pay_deposit:
                        isPayAll = 0;
                        isPayDeposit = true;
                        break;
                }

                jsonObject.put("payall", isPayAll);
            } else {
                jsonObject.put("payall", 0);
                // 付余款的时候默认就是分笔支付
                if (etPayMoney.length() <= 0) {
                    // 没有输入金额
                    Toast.makeText(this, R.string.msg_empty_pay_money, Toast.LENGTH_SHORT)
                            .show();
                    return;
                } else {
                    editPayMoney = Double.valueOf(etPayMoney.getText()
                            .toString());
                    if (editPayMoney < 1 || editPayMoney > order.getActualPrice() - order
                            .getRedPacketMoney() - order.getPaidMoney()) {
                        // 输入金额小于1或者大于余款
                        Toast.makeText(this, R.string.msg_invalid_pay_money, Toast.LENGTH_SHORT)
                                .show();
                        return;
                    }

                    // 设置支付余款参数
                    jsonObject.put("paysplit", 1);
                    jsonObject.put("split_money", editPayMoney);
                    realPay = editPayMoney;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String postUrl = Constants.getAbsUrl(Constants.HttpPath.CUSTOM_SETMEAL_ORDER_PAY);

        if (TextUtils.equals(payAgent, "llpay")) {
            onLLPay(postUrl, jsonObject, isBindCardPay, isAddNewCard);
        } else {
            onAliUnionPay(payAgent, postUrl, jsonObject);
        }
    }

    private void onAliUnionPay(final String finalPayAgent, String postUrl, JSONObject jsonObject) {
        progressBar.setVisibility(View.VISIBLE);
        new StatusHttpPostTask(this, new StatusRequestListener() {
            @Override
            public void onRequestCompleted(Object object, ReturnStatus returnStatus) {
                progressBar.setVisibility(View.GONE);
                // 请求返回来的时候,已经给订单绑定了红包,这个时候便不能再选择红包,并且下回提交的时候不能再次提交红包number
                if (selectedRedPacket != null && selectedRedPacket.getId() != -1) {
                    bindRedPacketMoney = selectedRedPacket.getAmount();
                    bindRedPacketNo = selectedRedPacket.getTicketNo();
                    arrow.setVisibility(View.GONE);
                    tvSavedAmount.setText(Html.fromHtml(getString(R.string.label_saved_money,
                            Util.formatDouble2String(selectedRedPacket.getAmount()))));
                    redPacketLayout.setClickable(false);
                    setPrice();
                }
                // 请求返回后,绑定的红包,订单金额计算有改变,需要发送列表刷新的消息
                EventBus.getDefault()
                        .post(new MessageEvent(MessageEvent.EventType
                                .CUSTOM_SETMEAL_ORDER_REFRESH_FLAG,
                                null));

                JSONObject resultObject = (JSONObject) object;
                if (resultObject != null && !resultObject.isNull("pay_params")) {
                    shareObject = resultObject.optJSONObject("share");

                    final String orderInfo = JSONUtil.getString(resultObject, "pay_params");
                    if (finalPayAgent.equals("alipay")) {
                        // 支付宝支付
                        new Thread() {
                            public void run() {
                                PayTask alipay = new PayTask(CustomSetmealOrderPaymentActivity
                                        .this);
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
                            json = new JSONObject(resultObject.optString("pay_params"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (json != null && !JSONUtil.isEmpty(json.optString("tn", null))) {
                            UPPayAssistEx.startPayByJAR(CustomSetmealOrderPaymentActivity.this,
                                    PayActivity.class,
                                    null,
                                    null,
                                    json.optString("tn"),
                                    "00");
                        } else {
                            Toast.makeText(CustomSetmealOrderPaymentActivity.this,
                                    R.string.hint_ordor_pay_err,
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                } else if (resultObject != null && !resultObject.isNull("fee") && resultObject
                        .optInt(
                        "fee",
                        0) <= 0) {
                    // 零元支付,直接成功
                    // 支付成功,发送刷新订单列表的消息
                    shareObject = resultObject.optJSONObject("share");
                    EventBus.getDefault()
                            .post(new MessageEvent(MessageEvent.EventType
                                    .CUSTOM_SETMEAL_ORDER_REFRESH_FLAG,
                                    null));
                    Intent intent = new Intent(CustomSetmealOrderPaymentActivity.this,
                            AfterPayCustomSetmealOrderActivity.class);
                    JSONArray jsonArray = new JSONArray();
                    jsonArray.put(order.getId()
                            .intValue());
                    intent.putExtra("ids", jsonArray.toString());
                    intent.putExtra("is_custom_order", true);
                    intent.putExtra("is_deposit", !isPayRest && isPayDeposit);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                    finish();
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 500);
                } else {
                    Toast.makeText(CustomSetmealOrderPaymentActivity.this,
                            R.string.msg_fail_to_pay_order,
                            Toast.LENGTH_SHORT)
                            .show();
                }

            }

            @Override
            public void onRequestFailed(ReturnStatus returnStatus, boolean network) {
                progressBar.setVisibility(View.GONE);
                Util.postFailToast(CustomSetmealOrderPaymentActivity.this,
                        returnStatus,
                        R.string.msg_fail_to_pay_order,
                        network);

            }
        }).execute(postUrl, jsonObject.toString());
    }

    private void onLLPay(
            String postUrl, JSONObject jsonObject, boolean isBindCardPay, boolean isAddNewCard) {
        Map<String, Object> nextData = new HashMap<>();
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(order.getId()
                .intValue());
        nextData.put("ids", jsonArray.toString());
        nextData.put("is_car_order", true);
        nextData.put("is_deposit", !isPayRest && isPayDeposit);
        if (isBindCardPay) {
            if (bindBankCards != null && !bindBankCards.isEmpty()) {
                int bindCardId = payMenu.getCheckedRadioButtonId();
                UserBindBankCard userBindCard = null;
                for (int i = 0; i < bindBankCards.size(); i++) {
                    UserBindBankCard card = bindBankCards.get(i);
                    if (card.getId()
                            .intValue() == bindCardId) {
                        userBindCard = card;
                    }
                }
                if (userBindCard != null) {
                    LLPayer llPayer = new LLPayer(Util.roundUpDouble2StringPositive(realPay),
                            postUrl,
                            jsonObject.toString(),
                            AfterPayCustomSetmealOrderActivity.class,
                            nextData,
                            mHandler,
                            isAddNewCard);
                    llPayer.llPay(this, userBindCard.getId());
                }
            }
        } else {
            LLPayer llPayer = new LLPayer(Util.roundUpDouble2StringPositive(realPay),
                    postUrl,
                    jsonObject.toString(),
                    AfterPayCustomSetmealOrderActivity.class,
                    nextData,
                    mHandler,
                    isAddNewCard);
            llPayer.llPay(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        progressBar.setVisibility(View.GONE);
        if (data != null) {
            // 银联支付返回的信息判断
            String str = data.getExtras()
                    .getString("pay_result");
            if (!JSONUtil.isEmpty(str)) {
                if (str.equalsIgnoreCase("success")) {
                    setResult(RESULT_OK);
                    // 支付成功,发送刷新订单列表的消息
                    EventBus.getDefault()
                            .post(new MessageEvent(MessageEvent.EventType
                                    .CUSTOM_SETMEAL_ORDER_REFRESH_FLAG,
                                    null));
                    Intent intent = new Intent(CustomSetmealOrderPaymentActivity.this,
                            AfterPayCustomSetmealOrderActivity.class);
                    JSONArray jsonArray = new JSONArray();
                    jsonArray.put(order.getId()
                            .intValue());
                    intent.putExtra("ids", jsonArray.toString());
                    intent.putExtra("is_custom_order", true);
                    intent.putExtra("is_deposit", !isPayRest && isPayDeposit);
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
                    Toast.makeText(CustomSetmealOrderPaymentActivity.this,
                            R.string.pay_result3,
                            Toast.LENGTH_SHORT)
                            .show();
                } else if (str.equalsIgnoreCase("cancel")) {
                    Toast.makeText(CustomSetmealOrderPaymentActivity.this,
                            R.string.pay_result4,
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getRedPacketList() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", order.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
        }).execute(Constants.getAbsUrl(Constants.HttpPath.CUSTOM_SETMEAL_REDPACKET),
                jsonObject.toString());
    }

    private void setRedPacketsInfo() {
        if (redPackets.size() > 0) {
            redPackets.add(new RedPacket(-1));

            if (selectedRedPacket == null) {
                selectedRedPacket = redPackets.get(0);
            }
        }

        setRedPacketView();
        setPrice();
    }

    /**
     * 设置红包选择界面
     */
    private void setRedPacketView() {
        redPacketLayout.setVisibility(View.VISIBLE);
        if (isPayRest) {
            // 支付余额的时候不可以使用红包,也不显示
            redPacketLayout.setVisibility(View.GONE);
        } else {
            redPacketLayout.setVisibility(View.VISIBLE);

            // 定金支付或者全款支付的时候都可以使用红包
            // 如果没有绑定红包,则正常流程显示红包选择等流程
            if (JSONUtil.isEmpty(bindRedPacketNo)) {
                if (redPackets.size() > 0) {
                    redPacketLayout.setClickable(true);
                    arrow.setVisibility(View.VISIBLE);
                    tvAvailableRedpacketCount.setText(getString(R.string
                                    .label_available_packet_count,
                            redPackets.size() - 1));
                    tvAvailableRedpacketCount.setVisibility(View.VISIBLE);

                    tvSavedAmount.setTextColor(getResources().getColor(R.color.colorBlack2));
                    if (selectedRedPacket.getId() == -1) {
                        // 选择了不使用红包
                        tvSavedAmount.setText(R.string.label_use_not_red_enve2);
                    } else {
                        tvSavedAmount.setText(Html.fromHtml(getString(R.string.label_saved_money,
                                Util.formatDouble2String(selectedRedPacket.getAmount()))));
                    }

                    arrow.setVisibility(View.VISIBLE);
                } else {
                    // 没有可用红包,直接不显示
                    redPacketLayout.setVisibility(View.GONE);
                }
            } else {
                // 如果绑定了红包,则只显示已绑定的红包金额,不能再次替换红包
                selectedRedPacket = new RedPacket(null);
                selectedRedPacket.setTicketNo(bindRedPacketNo);
                selectedRedPacket.setId(0);
                selectedRedPacket.setAmount(bindRedPacketMoney);

                arrow.setVisibility(View.GONE);
                tvSavedAmount.setText(Html.fromHtml(getString(R.string.label_saved_money,
                        Util.formatDouble2String(selectedRedPacket.getAmount()))));
                redPacketLayout.setClickable(false);
            }
        }
    }

    @OnClick(R.id.red_packet_layout)
    void onSelectedRedPacket() {
        if (redPackets.isEmpty()) {
            return;
        }

        showRedPackets();
    }

    public void showRedPackets() {
        if (dialog != null && dialog.isShowing()) {
            return;
        }
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

            final ListView listView = (ListView) v.findViewById(R.id.list);
            PacketsListAdapter packetsListAdapter = new PacketsListAdapter(redPackets,
                    CustomSetmealOrderPaymentActivity.this);
            listView.setAdapter(packetsListAdapter);
            listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    listView.setItemChecked(position, true);
                    selectedRedPacket = redPackets.get(position);
                    if (selectedRedPacket.getId() < 0) {
                        // 不使用红包
                        tvSavedAmount.setText(R.string.label_use_not_red_enve2);
                    } else {
                        // 使用红包
                        tvSavedAmount.setText(Html.fromHtml(getString(R.string.label_saved_money,
                                Util.formatDouble2String(selectedRedPacket.getAmount()))));
                    }

                    setPrice();
                    dialog.dismiss();
                }
            });

            // 默认选中第一个红包
            listView.setItemChecked(0, true);

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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.select_other_payment:
                // 选择其他支付方式
                onSelectOtherPayment();
                break;
            case R.id.add_new_card_layout:
                // 增加新的银行卡,调用llpay的支付方式,不过在付完款之后不需要进行设置支付密码
                onPay("llpay", true);
                break;
        }
    }

    private void onSelectOtherPayment() {
        if (selectDialog != null && selectDialog.isShowing()) {
            return;
        }
        if (selectDialog == null) {
            selectDialog = new Dialog(this, R.style.BubbleDialogTheme);
            View v = getLayoutInflater().inflate(R.layout.dialog_msg_single_button, null);
            v.findViewById(R.id.extend_layout)
                    .setVisibility(View.GONE);
            TextView msgAlertTv = (TextView) v.findViewById(R.id.tv_alert_msg);
            Button confirmBtn = (Button) v.findViewById(R.id.btn_confirm);
            msgAlertTv.setText(R.string.msg_select_other_payments);
            confirmBtn.setText(R.string.label_confirm);

            confirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectDialog.dismiss();
                    // 显示其他支付方式
                    findViewById(R.id.select_other_payment).setVisibility(View.GONE);
                    CheckableLinearLayoutButton unionPayBtn = (CheckableLinearLayoutButton)
                            findViewById(
                            R.id.union_pay);
                    unionPayBtn.setVisibility(View.VISIBLE);
                    //                    unionPayBtn.setChecked(true);
                    findViewById(R.id.quick_pay).setVisibility(View.GONE);
                    findViewById(R.id.alipay).setVisibility(View.VISIBLE);
                }
            });

            selectDialog.setCancelable(false);
            selectDialog.setContentView(v);
            Window window = selectDialog.getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            Point point = JSONUtil.getDeviceSize(CustomSetmealOrderPaymentActivity.this);
            params.width = Math.round(point.x * 27 / 32);
            window.setAttributes(params);
        }
        selectDialog.show();
    }

    @Override
    public void onCheckedChanged(CheckableLinearLayoutGroup group, int checkedId) {
        setRedPacketView();
        setPrice();
    }

    private class GetBankCardList extends AsyncTask<String, Object, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            String url = Constants.getAbsUrl(Constants.HttpPath.GET_USER_BIND_BANK_CARD_LIST);
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
                if (returnStatus != null && returnStatus.getRetCode() == 0) {
                    bindBankCards.clear();
                    contentLayout.setVisibility(View.VISIBLE);
                    bottomLayout.setVisibility(View.VISIBLE);
                    // 获取用户支付信息成功
                    JSONArray jsonArray = jsonObject.optJSONArray("data");
                    if (jsonArray != null && jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            UserBindBankCard card = new UserBindBankCard(jsonArray.optJSONObject
                                    (i));
                            bindBankCards.add(card);
                        }
                    }

                    setPaymentMenu();
                } else {
                    // 失败的话就无法进行下一步操作
                    contentLayout.setVisibility(View.GONE);
                    bottomLayout.setVisibility(View.GONE);
                    Toast.makeText(CustomSetmealOrderPaymentActivity.this,
                            R.string.msg_fail_to_get_bank_list,
                            Toast.LENGTH_SHORT)
                            .show();
                }
            } else {
                // 失败的话就无法进行下一步操作
                contentLayout.setVisibility(View.GONE);
                bottomLayout.setVisibility(View.GONE);
                Toast.makeText(CustomSetmealOrderPaymentActivity.this,
                        R.string.msg_fail_to_get_bank_list,
                        Toast.LENGTH_SHORT)
                        .show();
            }
            super.onPostExecute(jsonObject);
        }
    }

    /**
     * 根据用户是否绑定了银行卡改变付款选择项目
     */
    private void setPaymentMenu() {
        payMenu.setVisibility(View.VISIBLE);
        if (bindBankCards.isEmpty()) {
            // 没有绑定银行卡
            findViewById(R.id.add_new_card_layout).setVisibility(View.GONE);
            findViewById(R.id.select_other_payment).setVisibility(View.GONE);
            findViewById(R.id.quick_pay).setVisibility(View.VISIBLE);
            findViewById(R.id.alipay).setVisibility(View.VISIBLE);
            findViewById(R.id.union_pay).setVisibility(View.VISIBLE);
            // 默认选中快捷支付
            CheckableLinearLayoutButton btnQuickPay = (CheckableLinearLayoutButton) findViewById
                    (R.id.quick_pay);
            btnQuickPay.setChecked(true);
        } else {
            // 已经绑定了银行卡
            findViewById(R.id.add_new_card_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.select_other_payment).setVisibility(View.VISIBLE);
            findViewById(R.id.add_new_card_layout).setOnClickListener(this);
            findViewById(R.id.select_other_payment).setOnClickListener(this);
            findViewById(R.id.quick_pay).setVisibility(View.GONE);
            findViewById(R.id.alipay).setVisibility(View.GONE);
            findViewById(R.id.union_pay).setVisibility(View.GONE);

            // 添加银行卡列表
            for (int i = 0; i < bindBankCards.size(); i++) {
                UserBindBankCard card = bindBankCards.get(i);
                CheckableLinearLayoutButton view = (CheckableLinearLayoutButton)
                        getLayoutInflater().inflate(
                        R.layout.select_bank_card_item,
                        null);
                view.setClickable(true);
                ImageView imgLogo = (ImageView) view.findViewById(R.id.img_bank_logo);
                TextView tvBankName = (TextView) view.findViewById(R.id.tv_bank_name);
                TextView tvBankCardId = (TextView) view.findViewById(R.id.tv_bank_card_id);
                String logoPath = JSONUtil.getImagePath(card.getLogoPath(),
                        imgLogo.getLayoutParams().width);
                if (!JSONUtil.isEmpty(logoPath)) {
                    imgLogo.setTag(logoPath);
                    ImageLoadTask task = new ImageLoadTask(imgLogo, null, 0);
                    task.loadImage(logoPath,
                            imgLogo.getLayoutParams().width,
                            ScaleMode.WIDTH,
                            new AsyncBitmapDrawable(getResources(),
                                    R.mipmap.icon_empty_image,
                                    task));
                } else {
                    imgLogo.setImageBitmap(null);
                }
                tvBankName.setText(card.getBankName());
                tvBankCardId.setText("**  " + card.getAccount());

                view.setId(card.getId()
                        .intValue());

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout
                        .LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                if (i == 0) {
                    view.setChecked(true);
                }

                payMenu.addView(view, i, params);
            }
        }
    }
}
