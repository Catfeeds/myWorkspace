package me.suncloud.marrymemo.view;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.suncloud.hljweblibrary.views.activities.HljWebViewActivity;
import com.google.gson.JsonObject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.hunliji.hljcarlibrary.views.activities.WeddingCarProductDetailActivity;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljkefulibrary.moudles.Support;
import com.hunliji.hljpaymentlibrary.PayConfig;
import com.hunliji.hljpaymentlibrary.models.PayRxEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.CarOrder;
import me.suncloud.marrymemo.model.CarSubOrder;
import me.suncloud.marrymemo.model.MessageEvent;
import me.suncloud.marrymemo.model.ReturnStatus;
import me.suncloud.marrymemo.task.AsyncBitmapDrawable;
import me.suncloud.marrymemo.task.ImageLoadTask;
import me.suncloud.marrymemo.task.StatusHttpPostTask;
import me.suncloud.marrymemo.task.StatusRequestListener;
import me.suncloud.marrymemo.util.CustomerSupportUtil;
import me.suncloud.marrymemo.util.DataConfig;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.ScaleMode;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.TimeUtil;
import me.suncloud.marrymemo.util.Util;
import rx.Subscriber;

public class CarOrderDetailActivity extends HljBaseActivity implements PullToRefreshScrollView
        .OnRefreshListener {

    @BindView(R.id.tv_pay_label)
    TextView tvPayLabel;
    @BindView(R.id.tv_final_total_price)
    TextView tvFinalTotalPrice;
    @BindView(R.id.btn_order_action)
    Button btnOrderAction;
    @BindView(R.id.bottom_layout)
    LinearLayout bottomLayout;
    @BindView(R.id.tv_alert_expired_time)
    TextView tvAlertExpiredTime;
    @BindView(R.id.tv_alert_closed)
    TextView tvAlertClosed;
    @BindView(R.id.order_alert_layout)
    LinearLayout orderAlertLayout;
    @BindView(R.id.tv_buyer_name)
    TextView tvBuyerName;
    @BindView(R.id.tv_buyer_phone)
    TextView tvBuyerPhone;
    @BindView(R.id.tv_car_use_addr)
    TextView tvCarUseAddr;
    @BindView(R.id.tv_car_use_time)
    TextView tvCarUseTime;
    @BindView(R.id.shipping_address_layout)
    LinearLayout shippingAddressLayout;
    @BindView(R.id.tv_bride_address)
    TextView tvBrideAddress;
    @BindView(R.id.tv_groom_address)
    TextView tvGroomAddress;
    @BindView(R.id.tv_hotel_address)
    TextView tvHotelAddress;
    @BindView(R.id.tv_passed_address)
    TextView tvPassedAddress;
    @BindView(R.id.tv_other_address_info)
    TextView tvOtherAddressInfo;
    @BindView(R.id.items_layout)
    LinearLayout itemsLayout;
    @BindView(R.id.products_layout)
    LinearLayout productsLayout;
    @BindView(R.id.tv_total_product_price)
    TextView tvTotalProductPrice;
    @BindView(R.id.tv_discount_price)
    TextView tvDiscountPrice;
    @BindView(R.id.discount_layout)
    LinearLayout discountLayout;
    @BindView(R.id.tv_red_packet_amount)
    TextView tvRedPacketAmount;
    @BindView(R.id.red_packet_layout)
    LinearLayout redPacketLayout;
    @BindView(R.id.tv_total_price)
    TextView tvTotalPrice;
    @BindView(R.id.tv_insurance_agreement)
    TextView tvInsuranceAgreement;
    @BindView(R.id.tv_auto_confirm_hint)
    TextView tvAutoConfirmHint;
    @BindView(R.id.tv_order_no)
    TextView tvOrderNo;
    @BindView(R.id.tv_order_time)
    TextView tvOrderTime;
    @BindView(R.id.btn_contact_service)
    Button btnContactService;
    @BindView(R.id.ordering_info_layout)
    LinearLayout orderingInfoLayout;
    @BindView(R.id.content_layout)
    LinearLayout contentLayout;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.scroll_view)
    PullToRefreshScrollView scrollView;
    @BindView(R.id.confirm_info_layout)
    LinearLayout confirmInfoLayout;
    @BindView(R.id.paid_deposit_layout)
    LinearLayout paidDepositLayout;
    @BindView(R.id.tv_paid_deposit)
    TextView tvPaidDeposit;
    @BindView(R.id.tv_total_price_label)
    TextView tvTotalPriceLabel;
    @BindView(R.id.insurance_about_layout)
    LinearLayout insuranceAboutLayout;
    @BindView(R.id.pay_all_saved_layout)
    LinearLayout payAllSavedLayout;
    @BindView(R.id.order_memos_layout)
    LinearLayout orderMemosLayout;
    @BindView(R.id.tv_pay_all_saved)
    TextView tvPayAllSaved;
    @BindView(R.id.tv_groom_address_memo)
    TextView tvGroomAddressMemo;
    @BindView(R.id.tv_bride_address_memo)
    TextView tvBrideAddressMemo;
    @BindView(R.id.tv_hotel_address_memo)
    TextView tvHotelAddressMemo;
    @BindView(R.id.tv_passed_address_memo)
    TextView tvPassedAddressMemo;
    @BindView(R.id.tv_other_address_info_memo)
    TextView tvOtherAddressInfoMemo;

    private boolean isLoad;
    private long orderId;
    private CarOrder order;
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat dateFormat2;
    private ExpireTimeCountDown countDownTimer;
    private Dialog cancelOrderDlg;
    private Dialog confirmServiceDlg;
    private static final String PINGAN_INSURANCE_ABOUT_URL = "https://home.pingan.com" + "" + ""
            + ".cn/productCommonClause.screen?productId=PR000682";
    private Subscriber<PayRxEvent> paySubscriber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_order_detail);
        ButterKnife.bind(this);
        setTitle("");

        dateFormat = new SimpleDateFormat(getString(R.string.format_date_type10),
                Locale.getDefault());
        dateFormat2 = new SimpleDateFormat(getString(R.string.format_date_type8),
                Locale.getDefault());
        tvInsuranceAgreement.setText(Html.fromHtml(getString(R.string.label_insurance_agreement)));

        scrollView.setOnRefreshListener(this);

        orderId = getIntent().getLongExtra("id", 0);
        setOkText(R.string.label_cancel_order);
        hideOkText();
        order = (CarOrder) getIntent().getSerializableExtra("order");
        if (order != null) {
            setOrderDetail();

            orderId = order.getId();
        }

        if (orderId > 0) {
            progressBar.setVisibility(View.VISIBLE);

            new GetOrderDetailTask().execute();
        }
    }

    private void setOrderDetail() {
        if (order == null) {
            bottomLayout.setVisibility(View.GONE);
            contentLayout.setVisibility(View.GONE);
        } else {
            bottomLayout.setVisibility(View.VISIBLE);
            contentLayout.setVisibility(View.VISIBLE);

            setAlertInfo();
            setCarUseInfo();
            setConfirmInfo();
            setCarItems();
            setPricesInfo();
            setOrderMemos();
            setOrderingInfo();
            setBottomInfo();
        }
    }

    private void setAlertInfo() {
        hideOkText();
        setTitle(order.getStatusStr2());
        switch (order.getStatus()) {
            case 10:
                // 等待商家接单
                showOkText();
                orderAlertLayout.setVisibility(View.VISIBLE);
                tvAlertExpiredTime.setVisibility(View.GONE);
                tvAlertClosed.setVisibility(View.VISIBLE);
                tvAlertClosed.setText(R.string.label_wait_for_accept);
                break;
            case 11:
                // 代付款
                orderAlertLayout.setVisibility(View.VISIBLE);
                tvAlertClosed.setVisibility(View.GONE);

                // 启动倒计时
                Calendar calendar = Calendar.getInstance();
                if (order.getExpireTime()
                        .after(calendar.getTime())) {
                    // 还未超时
                    tvAlertExpiredTime.setText(Html.fromHtml(getString(R.string
                                    .label_order_expired_count_down_time2,
                            TimeUtil.getSpecialTimeLiteral(this,
                                    order.getExpireTime()
                                            .getTime() - calendar.getTimeInMillis()))));

                    tvAlertExpiredTime.setVisibility(View.VISIBLE);
                    if (countDownTimer != null) {
                        countDownTimer.cancel();
                        countDownTimer = null;
                    }
                    countDownTimer = new ExpireTimeCountDown(order.getExpireTime()
                            .getTime() - calendar.getTimeInMillis(), 1000 * 60);
                    countDownTimer.start();
                } else {
                    // 重新获取订单详情
                    if (orderId > 0 && !isLoad) {
                        progressBar.setVisibility(View.VISIBLE);

                        new GetOrderDetailTask().execute();
                    }
                }
                break;
            case 91:
                // 用户取消订单
                orderAlertLayout.setVisibility(View.VISIBLE);
                tvAlertExpiredTime.setVisibility(View.GONE);
                tvAlertClosed.setVisibility(View.VISIBLE);
                tvAlertClosed.setText(R.string.label_canceld_order);
                break;
            case 93:
                // 系统自动关闭订单
                orderAlertLayout.setVisibility(View.VISIBLE);
                tvAlertExpiredTime.setVisibility(View.GONE);
                tvAlertClosed.setVisibility(View.VISIBLE);
                tvAlertClosed.setText(R.string.label_order_auto_close);
                break;
            case 15:
                // 商家拒绝接单
                orderAlertLayout.setVisibility(View.VISIBLE);
                tvAlertExpiredTime.setVisibility(View.GONE);
                tvAlertClosed.setVisibility(View.VISIBLE);
                tvAlertClosed.setText(R.string.label_declined_order);
                break;
            default:
                // 已付款
                if (order.isOfflinePay()) {
                    // 线下付款成功，显示提示
                    orderAlertLayout.setVisibility(View.VISIBLE);
                    tvAlertExpiredTime.setVisibility(View.GONE);
                    tvAlertClosed.setVisibility(View.VISIBLE);
                    tvAlertClosed.setText(R.string.label_offlie_paid_confirm);
                } else {
                    orderAlertLayout.setVisibility(View.GONE);
                }
                break;
        }
    }

    private void setCarUseInfo() {
        if (order.getCarUseDate() != null) {
            tvCarUseTime.setText(getString(R.string.label_car_use_time,
                    dateFormat.format(order.getCarUseDate())));
        }
        tvCarUseAddr.setText(getString(R.string.label_car_use_addr, order.getCarUseAddr()));
        tvBuyerName.setText(order.getBuyerName());
        tvBuyerPhone.setText(order.getBuyerPhone());
    }

    private void setConfirmInfo() {
        if (order.getStatus() == 11) {
            // 代付款状态才需要显示确认信息
            confirmInfoLayout.setVisibility(View.VISIBLE);
            tvGroomAddress.setText(JSONUtil.isEmpty(order.getMemoGroomAddress()) ? getString(R
                    .string.label_empty) : order.getMemoGroomAddress());
            tvBrideAddress.setText(JSONUtil.isEmpty(order.getMemoBrideAddress()) ? getString(R
                    .string.label_empty) : order.getMemoBrideAddress());
            tvHotelAddress.setText(JSONUtil.isEmpty(order.getMemoHotel()) ? getString(R.string
                    .label_empty) : order.getMemoHotel());
            tvPassedAddress.setText(JSONUtil.isEmpty(order.getMemoWay()) ? getString(R.string
                    .label_empty) : order.getMemoWay());
            tvOtherAddressInfo.setText(JSONUtil.isEmpty(order.getMemoExtra()) ? getString(R
                    .string.label_empty) : order.getMemoExtra());
        } else {
            confirmInfoLayout.setVisibility(View.GONE);
        }
    }

    private void setCarItems() {
        itemsLayout.removeAllViews();
        for (final CarSubOrder subOrder : order.getSubOrders()) {
            View itemView = getLayoutInflater().inflate(R.layout.car_shop_cart_item, null);
            TextView tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            ImageView imgCover = (ImageView) itemView.findViewById(R.id.img_cover);
            TextView tvSkuInfo = (TextView) itemView.findViewById(R.id.tv_sku_info);
            TextView tvPrice = (TextView) itemView.findViewById(R.id.tv_price);
            TextView tvQuantity = (TextView) itemView.findViewById(R.id.tv_quantity);

            tvTitle.setText(subOrder.getCarProduct()
                    .getTitle());
            String url = JSONUtil.getImagePath(subOrder.getCarProduct()
                    .getCover(), imgCover.getLayoutParams().width);
            if (!JSONUtil.isEmpty(url)) {
                imgCover.setTag(url);
                ImageLoadTask task = new ImageLoadTask(imgCover, null, 0);
                task.loadImage(url,
                        imgCover.getLayoutParams().width,
                        ScaleMode.WIDTH,
                        new AsyncBitmapDrawable(getResources(), R.mipmap.icon_empty_image, task));
            } else {
                imgCover.setImageBitmap(null);
            }

            tvSkuInfo.setText(getString(R.string.label_sku2,
                    subOrder.getCarSku()
                            .getSkuNames()));
            tvPrice.setText(getString(R.string.label_price,
                    CommonUtil.formatDouble2String((subOrder.getActualMoney() / subOrder
                            .getQuantity()))));
            tvQuantity.setText("x" + String.valueOf(subOrder.getQuantity()));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(CarOrderDetailActivity
                            .this, WeddingCarProductDetailActivity.class);
                    intent.putExtra(WeddingCarProductDetailActivity.ARG_ID,
                            subOrder.getCarProduct()
                                    .getId());
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                }
            });
            itemsLayout.addView(itemView);
        }
    }

    private void setPricesInfo() {
        tvTotalProductPrice.setText(getString(R.string.label_price,
                CommonUtil.formatDouble2String((order.getOriginActualMoney()))));
        if (order.getAidMoney() == 0) {
            discountLayout.setVisibility(View.GONE);
        } else {
            discountLayout.setVisibility(View.VISIBLE);
            tvDiscountPrice.setText(getString(R.string.label_price6,
                    CommonUtil.formatDouble2String(order.getAidMoney())));
        }

        payAllSavedLayout.setVisibility(View.GONE);

        // 红包金额
        if (JSONUtil.isEmpty(order.getRedPacketNo())) {
            redPacketLayout.setVisibility(View.GONE);
        } else {
            redPacketLayout.setVisibility(View.VISIBLE);
            tvRedPacketAmount.setText(getString(R.string.label_price6,
                    CommonUtil.formatDouble2String(order.getRedPacketMoney())));
        }

        // 付款状态
        if (order.getStatus() == 87 || order.getStatus() == 92 || order.getStatus() == 90) {
            if (order.isPayAll()) {
                // 已付全款
                paidDepositLayout.setVisibility(View.GONE);
                tvTotalPriceLabel.setText(R.string.label_real_pay);
                tvPayLabel.setText(R.string.label_real_pay);

                tvFinalTotalPrice.setText(Util.formatDouble2String(order.getPaidMoney()));
                tvTotalPrice.setText(getString(R.string.label_price,
                        Util.formatDouble2String(order.getPaidMoney())));

                if (order.getPayAllSavedMoney() > 0) {
                    payAllSavedLayout.setVisibility(View.VISIBLE);
                    tvPayAllSaved.setText(getString(R.string.label_price6,
                            Util.formatDouble2String(order.getPayAllSavedMoney())));
                } else {
                    payAllSavedLayout.setVisibility(View.GONE);
                }
            } else {
                // 已付定金
                paidDepositLayout.setVisibility(View.VISIBLE);
                tvTotalPriceLabel.setText(R.string.label_rest_to_pay);
                tvPayLabel.setText(R.string.label_rest_to_pay);
                tvPaidDeposit.setText(getString(R.string.label_price6,
                        Util.formatDouble2String((order.getPaidMoney()))));

                double total = order.getOriginActualMoney() - order.getAidMoney() - order
                        .getRedPacketMoney() - order.getPaidMoney();
                tvFinalTotalPrice.setText(Util.formatDouble2StringPositive(total));
                tvTotalPrice.setText(getString(R.string.label_price,
                        Util.formatDouble2StringPositive(total)));
            }
        } else {
            paidDepositLayout.setVisibility(View.GONE);
            tvTotalPriceLabel.setText(R.string.label_total_price4);
            tvPayLabel.setText(R.string.label_total_price4);

            double total = order.getOriginActualMoney() - order.getAidMoney() - order
                    .getRedPacketMoney() - order.getPaidMoney();
            tvFinalTotalPrice.setText(Util.formatDouble2StringPositive(total));
            tvTotalPrice.setText(getString(R.string.label_price,
                    Util.formatDouble2StringPositive(total)));
        }

        btnOrderAction.setEnabled(true);
    }

    private void setOrderMemos() {
        if ((order.getStatus() == 87 || order.getStatus() == 90 || order.getStatus() == 91 ||
                order.getStatus() == 92 || order.getStatus() == 93) && !JSONUtil.isEmpty(
                order.getMemoGroomAddress())) {
            orderMemosLayout.setVisibility(View.VISIBLE);
            tvGroomAddressMemo.setText(JSONUtil.isEmpty(order.getMemoGroomAddress()) ? getString
                    (R.string.label_empty) : order.getMemoGroomAddress());
            tvBrideAddressMemo.setText(JSONUtil.isEmpty(order.getMemoBrideAddress()) ? getString
                    (R.string.label_empty) : order.getMemoBrideAddress());
            tvHotelAddressMemo.setText(JSONUtil.isEmpty(order.getMemoHotel()) ? getString(R
                    .string.label_empty) : order.getMemoHotel());
            tvPassedAddressMemo.setText(JSONUtil.isEmpty(order.getMemoWay()) ? getString(R.string
                    .label_empty) : order.getMemoWay());
            tvOtherAddressInfoMemo.setText(JSONUtil.isEmpty(order.getMemoExtra()) ? getString(R
                    .string.label_empty) : order.getMemoExtra());
        } else {
            orderMemosLayout.setVisibility(View.GONE);
        }
    }

    private void setOrderingInfo() {
        tvOrderNo.setText(order.getOrderNo());
        tvOrderTime.setText(dateFormat.format(order.getCreatedAt()));
        if (order.getCarInsuranceId() > 0 && order.getStatus() > 10) {
            insuranceAboutLayout.setVisibility(View.VISIBLE);
        } else {
            insuranceAboutLayout.setVisibility(View.GONE);
        }
    }

    private void setBottomInfo() {
        switch (order.getStatus()) {
            case 11:
                bottomLayout.setVisibility(View.VISIBLE);
                btnOrderAction.setText(R.string.label_go_pay);
                break;
            case 87:
                bottomLayout.setVisibility(View.VISIBLE);
                if (order.isPayAll()) {
                    // 已付全款
                    btnOrderAction.setText(R.string.label_confirm_service);
                } else {
                    btnOrderAction.setText(R.string.label_pay_rest_money3);
                }
                break;
            case 90:
                // 交易成功,未评价
                bottomLayout.setVisibility(View.VISIBLE);
                btnOrderAction.setText(R.string.label_review2);
                break;
            default:
                bottomLayout.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * 取消订单
     */
    @Override
    public void onOkButtonClick() {
        if (order == null || order.getStatus() != 10 || (cancelOrderDlg != null && cancelOrderDlg
                .isShowing())) {
            return;
        }

        cancelOrderDlg = new Dialog(this, R.style.BubbleDialogTheme);
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
                cancelOrderDlg.dismiss();
                progressBar.setVisibility(View.VISIBLE);
                postCancelOrder();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelOrderDlg.dismiss();
            }
        });
        cancelOrderDlg.setContentView(v);
        Window window = cancelOrderDlg.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        Point point = JSONUtil.getDeviceSize(this);
        params.width = Math.round(point.x * 27 / 32);
        window.setAttributes(params);
        cancelOrderDlg.show();

        super.onOkButtonClick();
    }

    private void postCancelOrder() {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", order.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new StatusHttpPostTask(this, new StatusRequestListener() {
            @Override
            public void onRequestCompleted(Object object, ReturnStatus returnStatus) {
                progressBar.setVisibility(View.GONE);
                JSONObject orderObject = (JSONObject) object;
                if (orderObject != null) {
                    order = new CarOrder(orderObject);
                    // 支付成功,发送刷新订单列表的消息
                    EventBus.getDefault()
                            .post(new MessageEvent(MessageEvent.EventType
                                    .CAR_ORDER_REFRESH_WITH_OBJECT,
                                    order));
                    setOrderDetail();
                }
            }

            @Override
            public void onRequestFailed(ReturnStatus returnStatus, boolean network) {
                progressBar.setVisibility(View.GONE);
                Util.postFailToast(CarOrderDetailActivity.this,
                        returnStatus,
                        R.string.msg_fail_to_cancel_order,
                        network);
            }
        }).execute(Constants.getAbsUrl(Constants.HttpPath.CANCEL_CAR_ORDER), jsonObject.toString());
    }

    @OnClick(R.id.btn_order_action)
    void onOrderAction() {
        Intent intent = null;
        switch (order.getStatus()) {
            case 11:
                intent = new Intent(this, CarOrderPaymentActivity.class);
                intent.putExtra("id", order.getId());
                intent.putExtra("total_price", order.getOriginActualMoney() - order.getAidMoney());
                intent.putExtra("pay_all_saved_money", order.getPayAllSavedMoneyExpect());
                intent.putExtra("deposit_percent", order.getEarnestPercent());
                if (!JSONUtil.isEmpty(order.getRedPacketNo())) {
                    intent.putExtra("red_packet_no", order.getRedPacketNo());
                    intent.putExtra("red_packet_money", order.getRedPacketMoney());
                }
                // 拼接请求红包需要的参数
                JSONObject extraObj = new JSONObject();
                JSONArray extraArray = new JSONArray();
                try {
                    for (CarSubOrder item : order.getSubOrders()) {
                        JSONObject itemObj = new JSONObject();
                        itemObj.put("sku_id",
                                item.getCarSku()
                                        .getId());
                        itemObj.put("product_id",
                                item.getCarProduct()
                                        .getId());
                        itemObj.put("quantity", item.getQuantity());

                        extraArray.put(itemObj);
                    }

                    extraObj.put("sub_items", extraArray);
                    extraObj.put("order_id", order.getId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                intent.putExtra("extra_json_string", extraObj.toString());
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                break;
            case 87:
                if (order.isPayAll()) {
                    // 已付全款
                    btnOrderAction.setText(R.string.label_confirm_service);
                    confirmService();
                } else {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("id", order.getId());
                    } catch (JSONException ignored) {

                    }
                    if (paySubscriber == null) {
                        paySubscriber = initSubscriber();
                    }
                    ArrayList<String> payTypes = null;
                    if (Session.getInstance()
                            .getDataConfig(this) != null) {
                        payTypes = Session.getInstance()
                                .getDataConfig(this)
                                .getPayTypes();
                    }
                    double payMoney = order.getOriginActualMoney() - order.getAidMoney() - order
                            .getPaidMoney() - order.getRedPacketMoney();
                    if (payMoney < 0) {
                        payMoney = 0;
                    }
                    new PayConfig.Builder(this).params(jsonObject)
                            .path(Constants.HttpPath.CAR_PRODUCT_PAY_REST)
                            .price(payMoney)
                            .subscriber(paySubscriber)
                            .payAgents(payTypes, DataConfig.getPayAgents())
                            .build()
                            .pay();
                }
                break;
            case 90:
                // 交易成功,未评价
                Intent comment = new Intent(this, CommentCarActivity.class);
                comment.putExtra(CommentCarActivity.ARG_ORDER_ID, order.getId());
                startActivity(comment);
                this.overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                break;
            default:
                break;
        }
    }

    private void confirmService() {
        if (order == null || !order.isPayAll() || (confirmServiceDlg != null && confirmServiceDlg
                .isShowing())) {
            return;
        }

        confirmServiceDlg = new Dialog(this, R.style.BubbleDialogTheme);
        View v = getLayoutInflater().inflate(R.layout.dialog_confirm, null);
        TextView msgAlertTv = (TextView) v.findViewById(R.id.tv_alert_msg);
        final Button confirmBtn = (Button) v.findViewById(R.id.btn_confirm);
        Button cancelBtn = (Button) v.findViewById(R.id.btn_cancel);
        msgAlertTv.setText(R.string.msg_confirm_service);
        confirmBtn.setText(R.string.label_confirm_service2);
        cancelBtn.setText(R.string.label_wrong_action);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmServiceDlg.dismiss();
                progressBar.setVisibility(View.VISIBLE);
                postConfirmService();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmServiceDlg.dismiss();
            }
        });
        confirmServiceDlg.setContentView(v);
        Window window = confirmServiceDlg.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        Point point = JSONUtil.getDeviceSize(this);
        params.width = Math.round(point.x * 27 / 32);
        window.setAttributes(params);
        confirmServiceDlg.show();
    }

    private void postConfirmService() {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("order_id", order.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new StatusHttpPostTask(this, new StatusRequestListener() {
            @Override
            public void onRequestCompleted(Object object, ReturnStatus returnStatus) {
                progressBar.setVisibility(View.GONE);
                JSONObject orderObject = (JSONObject) object;
                if (orderObject != null) {
                    order = new CarOrder(orderObject);
                    // 支付成功,发送刷新订单列表的消息
                    EventBus.getDefault()
                            .post(new MessageEvent(MessageEvent.EventType
                                    .CAR_ORDER_REFRESH_WITH_OBJECT,
                                    order));
                    setOrderDetail();
                    Intent intent = new Intent(CarOrderDetailActivity.this,
                            AfterConfirmReceiveActivity.class);
                    intent.putExtra("is_car_order", true);
                    intent.putExtra("car_order", order);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_from_bottom, 0);
                }
            }

            @Override
            public void onRequestFailed(ReturnStatus returnStatus, boolean network) {
                progressBar.setVisibility(View.GONE);
                Util.postFailToast(CarOrderDetailActivity.this,
                        returnStatus,
                        R.string.msg_fail_to_confirm_service,
                        network);
            }
        }).execute(Constants.getAbsUrl(Constants.HttpPath.CONFIRM_CAR_ORDER_SERVICE),
                jsonObject.toString());
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        if (!isLoad) {
            new GetOrderDetailTask().execute();
        }
    }

    @OnClick(R.id.insurance_about_layout)
    void goSeeAboutInsurance() {
        Intent intent = new Intent(this, HljWebViewActivity.class);
        intent.putExtra("path", PINGAN_INSURANCE_ABOUT_URL);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }


    private class ExpireTimeCountDown extends CountDownTimer {
        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link
         *                          #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public ExpireTimeCountDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            // 距离结束时间为24小时的时候开始显示倒计时
            tvAlertExpiredTime.setText(Html.fromHtml(getString(R.string
                            .label_order_expired_count_down_time2,
                    TimeUtil.getSpecialTimeLiteral(CarOrderDetailActivity.this,
                            millisUntilFinished))));
        }

        @Override
        public void onFinish() {
            // 重新获取订单详情
            if (orderId > 0 && !isLoad) {
                progressBar.setVisibility(View.VISIBLE);

                new GetOrderDetailTask().execute();
            }
        }
    }

    private class GetOrderDetailTask extends AsyncTask<String, Object, JSONObject> {

        public GetOrderDetailTask() {
            isLoad = true;
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            String url = Constants.getAbsUrl(String.format(Constants.HttpPath.CAR_ORDER_DETAIL,
                    orderId));
            try {
                String json = JSONUtil.getStringFromUrl(url);
                if (JSONUtil.isEmpty(json)) {
                    return null;
                }
                Log.d("CarORderDetail", json);

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
            scrollView.onRefreshComplete();
            if (jsonObject != null) {
                ReturnStatus returnStatus = new ReturnStatus(jsonObject.optJSONObject("status"));
                if (returnStatus.getRetCode() == 0) {
                    JSONObject dataObject = jsonObject.optJSONObject("data");
                    if (dataObject != null) {
                        order = new CarOrder(dataObject);

                        EventBus.getDefault()
                                .post(new MessageEvent(MessageEvent.EventType
                                        .CAR_ORDER_REFRESH_WITH_OBJECT,
                                        order));
                        setOrderDetail();
                    }
                }
            }

            isLoad = false;
            super.onPostExecute(jsonObject);
        }
    }

    @OnClick(R.id.btn_contact_service)
    void onContact(View view) {
        CustomerSupportUtil.goToSupport(this, Support.SUPPORT_KIND_CAR, order);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setOrderDetail();
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
                        Intent intent = new Intent(CarOrderDetailActivity.this,
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
                        overridePendingTransition(0, 0);
                        onRefresh(null);
                        break;
                }
            }
        };
    }

    @Override
    protected void onFinish() {
        if (paySubscriber != null && !paySubscriber.isUnsubscribed()) {
            paySubscriber.unsubscribe();
        }
        super.onFinish();
    }
}
