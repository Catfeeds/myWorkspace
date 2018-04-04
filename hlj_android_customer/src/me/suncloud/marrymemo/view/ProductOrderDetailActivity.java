package me.suncloud.marrymemo.view;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.hunliji.hljcommonlibrary.models.product.ShopProduct;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.NumberFormatUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljpaymentlibrary.PayConfig;
import com.hunliji.hljpaymentlibrary.models.PayRxEvent;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.ContactsAdapter;
import me.suncloud.marrymemo.api.orders.OrderApi;
import me.suncloud.marrymemo.model.orders.ProductOrder;
import me.suncloud.marrymemo.model.orders.ProductOrderRedPacketState;
import me.suncloud.marrymemo.model.orders.ProductSubOrder;
import me.suncloud.marrymemo.task.AsyncBitmapDrawable;
import me.suncloud.marrymemo.task.ImageLoadTask;
import me.suncloud.marrymemo.util.DataConfig;
import me.suncloud.marrymemo.util.DialogUtil;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.ScaleMode;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.view.chat.WSCustomerChatActivity;
import me.suncloud.marrymemo.view.comment.CommentProductOrderActivity;
import me.suncloud.marrymemo.view.product.AfterPayProductOrderActivity;
import me.suncloud.marrymemo.view.product.ShopProductDetailActivity;
import rx.Subscriber;

public class ProductOrderDetailActivity extends HljBaseActivity implements
        PullToRefreshScrollView.OnRefreshListener {

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
    @BindView(R.id.tv_alert_shipping_info)
    TextView tvAlertShippingInfo;
    @BindView(R.id.tv_alert_shipping_time)
    TextView tvAlertShippingTime;
    @BindView(R.id.tv_alert_closed)
    TextView tvAlertClosed;
    @BindView(R.id.order_alert_layout)
    LinearLayout orderAlertLayout;
    @BindView(R.id.tv_buyer_name)
    TextView tvBuyerName;
    @BindView(R.id.tv_buyer_phone)
    TextView tvBuyerPhone;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.shipping_address_layout)
    LinearLayout shippingAddressLayout;
    @BindView(R.id.tv_merchant_name)
    TextView tvMerchantName;
    @BindView(R.id.items_layout)
    LinearLayout itemsLayout;
    @BindView(R.id.products_layout)
    LinearLayout productsLayout;
    @BindView(R.id.actions_layout)
    LinearLayout actionsLayout;
    @BindView(R.id.tv_total_product_price)
    TextView tvTotalProductPrice;
    @BindView(R.id.tv_shipping_fee)
    TextView tvShippingFee;
    @BindView(R.id.tv_total_price)
    TextView tvTotalPrice;
    @BindView(R.id.tv_order_no)
    TextView tvOrderNo;
    @BindView(R.id.tv_order_time)
    TextView tvOrderTime;
    @BindView(R.id.ordering_info_layout)
    LinearLayout orderingInfoLayout;
    @BindView(R.id.scroll_view)
    PullToRefreshScrollView scrollView;
    @BindView(R.id.content_layout)
    View contentLayout;
    @BindView(R.id.progressBar)
    View progressBar;
    @BindView(R.id.btn_chat)
    Button btnChat;
    @BindView(R.id.btn_call)
    Button btnCall;
    @BindView(R.id.tv_red_packet_amount)
    TextView tvRedPacketAmount;
    @BindView(R.id.red_packet_layout)
    LinearLayout redPacketLayout;
    @BindView(R.id.tv_auto_confirm_hint)
    TextView tvAutoConfirmHint;
    @BindView(R.id.alert_arrow)
    ImageView alertArrow;
    @BindView(R.id.merchant_layout)
    View merchantLayout;
    @BindView(R.id.tv_order_status)
    TextView tvOrderStatus;
    @BindView(R.id.tv_memo)
    TextView tvMemo;
    @BindView(R.id.order_memo_layout)
    LinearLayout orderMemoLayout;
    @BindView(R.id.tv_coupon_amount)
    TextView tvCouponAmount;
    @BindView(R.id.coupon_layout)
    LinearLayout couponLayout;

    private String productIds;
    private long orderId;
    private ProductOrder order;
    private ExpireTimeCountDown countDownTimer;
    private Dialog cancelOrderDlg;
    private Dialog confirmShippingDlg;
    private Dialog contactDialog;
    private HljHttpSubscriber initSub;
    private HljHttpSubscriber confirmSub;
    private HljHttpSubscriber cancelSub;
    private Subscriber<PayRxEvent> paySubscriber;
    private HljHttpSubscriber checkSub;
    private Dialog checkDlg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_order_detail);
        ButterKnife.bind(this);

        initValues();
        initViews();
        initLoad(false);
    }

    private void initValues() {
        orderId = getIntent().getLongExtra("id", 0);
    }

    private void initViews() {
        setOkText(R.string.label_cancel_order);
        scrollView.setOnRefreshListener(this);
    }

    /**
     * 是否是下拉刷新
     *
     * @param isPTR
     */
    private void initLoad(boolean isPTR) {
        initSub = HljHttpSubscriber.buildSubscriber(this)
                .setProgressBar(isPTR ? null : progressBar)
                .setContentView(isPTR ? null : contentLayout)
                .setOnNextListener(new SubscriberOnNextListener<ProductOrder>() {
                    @Override
                    public void onNext(ProductOrder o) {
                        ProductOrderDetailActivity.this.order = o;
                        scrollView.onRefreshComplete();
                        setOrderDetails();
                    }
                })
                .build();
        OrderApi.getProductOrderDetail(orderId)
                .subscribe(initSub);
    }


    private void setOrderDetails() {
        if (order == null) {
            bottomLayout.setVisibility(View.GONE);
            contentLayout.setVisibility(View.GONE);
        } else {
            bottomLayout.setVisibility(View.VISIBLE);
            contentLayout.setVisibility(View.VISIBLE);

            setAlertInfo();
            setShippingAddressInfo();
            setProductsInfo();
            setPricesInfo();
            setOrderingInfo();
            setBottomInfo();
        }
    }

    /**
     * 显示顶部的订单提示消息
     */
    private void setAlertInfo() {
        hideOkText();
        switch (order.getStatus()) {
            case 10:
                // 订单已提交,待付款
                orderAlertLayout.setVisibility(View.VISIBLE);
                tvAlertClosed.setVisibility(View.GONE);
                tvAlertShippingInfo.setVisibility(View.GONE);
                tvAlertShippingTime.setVisibility(View.GONE);
                alertArrow.setVisibility(View.GONE);

                tvAlertExpiredTime.setText(Html.fromHtml(getString(R.string
                                .label_order_expired_time,
                        order.getExpireTime()
                                .toString("yyyy-MM-dd HH:mm:ss"))));
                // 启动倒计时
                DateTime now = new DateTime();
                if (order.getExpireTime()
                        .isAfter(now)) {
                    // 还未超时
                    tvAlertExpiredTime.setVisibility(View.VISIBLE);
                    countDownTimer = new ExpireTimeCountDown(order.getExpireTime()
                            .getMillis() - now.getMillis(), 1000);
                    countDownTimer.start();

                    // 顺便显示取消选项
                    showOkText();
                } else {
                    // 已经超时
                    // 禁止付款操作
                    bottomLayout.setVisibility(View.GONE);
                    // 显示关闭提示
                    tvAlertClosed.setVisibility(View.VISIBLE);
                    tvAlertExpiredTime.setVisibility(View.GONE);
                }
                break;
            case 90:
                // 已确认服务,交易成功,即:已收货
            case 92:
                // 已评论
            case 89:
                // 商家已发货
                if (order.getExpressId() > 0) {
                    if (order.getExpressInfo() != null) {
                        orderAlertLayout.setVisibility(View.VISIBLE);
                        tvAlertClosed.setVisibility(View.GONE);
                        tvAlertExpiredTime.setVisibility(View.GONE);
                        tvAlertShippingTime.setVisibility(View.VISIBLE);
                        tvAlertShippingInfo.setVisibility(View.VISIBLE);

                        if (order.getExpressInfo()
                                .getShippingStatusList() != null && order.getExpressInfo()
                                .getShippingStatusList()
                                .size() > 0) {
                            // 有物流信息
                            tvAlertShippingInfo.setText(order.getExpressInfo()
                                    .getShippingStatusList()
                                    .get(0)
                                    .getStatus());
                            tvAlertShippingTime.setText(order.getExpressInfo()
                                    .getShippingStatusList()
                                    .get(0)
                                    .getTime()
                                    .toString("yyyy-MM-dd HH:mm:ss"));
                            alertArrow.setVisibility(View.VISIBLE);
                        } else {
                            tvAlertShippingInfo.setText(R.string.label_no_shipping_status);
                            tvAlertShippingTime.setVisibility(View.GONE);
                            alertArrow.setVisibility(View.GONE);
                        }
                    } else {
                        // 没有快递信息,显示暂无提示
                        orderAlertLayout.setVisibility(View.VISIBLE);
                        tvAlertShippingInfo.setVisibility(View.GONE);
                        tvAlertShippingTime.setVisibility(View.GONE);
                        tvAlertExpiredTime.setVisibility(View.VISIBLE);
                        tvAlertClosed.setVisibility(View.GONE);
                        alertArrow.setVisibility(View.GONE);
                        tvAlertExpiredTime.setText(R.string.label_no_current_shipping_info);
                    }
                } else {
                    // 无需物流
                    orderAlertLayout.setVisibility(View.VISIBLE);
                    tvAlertShippingInfo.setVisibility(View.GONE);
                    tvAlertShippingTime.setVisibility(View.GONE);
                    tvAlertExpiredTime.setVisibility(View.VISIBLE);
                    tvAlertClosed.setVisibility(View.GONE);
                    alertArrow.setVisibility(View.GONE);
                    tvAlertExpiredTime.setText(R.string.label_need_no_shipping);
                }
                break;
            case 91:
                // 交易关闭
                if (order.getReason() == 1 || order.getReason() == 2) {
                    orderAlertLayout.setVisibility(View.VISIBLE);
                    tvAlertShippingInfo.setVisibility(View.GONE);
                    tvAlertShippingTime.setVisibility(View.GONE);
                    tvAlertExpiredTime.setVisibility(View.GONE);
                    tvAlertClosed.setVisibility(View.VISIBLE);
                    tvAlertClosed.setText(order.getClosedReason());
                    alertArrow.setVisibility(View.GONE);
                } else {
                    orderAlertLayout.setVisibility(View.GONE);
                }
                break;
            case 93:
                // 系统自动关闭交易
                orderAlertLayout.setVisibility(View.VISIBLE);
                tvAlertShippingInfo.setVisibility(View.GONE);
                tvAlertShippingTime.setVisibility(View.GONE);
                tvAlertExpiredTime.setVisibility(View.GONE);
                tvAlertClosed.setVisibility(View.VISIBLE);
                alertArrow.setVisibility(View.GONE);
                tvAlertClosed.setText(R.string.label_order_auto_close);
                break;
            default:
                orderAlertLayout.setVisibility(View.GONE);
                break;
        }
    }

    @OnClick(R.id.order_alert_layout)
    void goShippingStatus() {
        if (order == null || order.getExpressInfo() == null || order.getExpressInfo()
                .getShippingStatusList()
                .size() == 0) {
            return;
        }
        Intent intent = new Intent(this, ShippingStatusActivity.class);
        intent.putExtra("order_id", order.getId());
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    /**
     * 显示收货地址信息
     */
    private void setShippingAddressInfo() {
        if (order.getShippingAddress() != null) {
            shippingAddressLayout.setVisibility(View.VISIBLE);
            tvBuyerName.setText(order.getShippingAddress()
                    .getBuyerName());
            tvBuyerPhone.setText(order.getShippingAddress()
                    .getMobile());
            tvAddress.setText(order.getShippingAddress()
                    .toString());
        } else {
            shippingAddressLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 显示商家信息和商品
     */
    private void setProductsInfo() {
        tvMerchantName.setText(order.getMerchant()
                .getName());
        tvOrderStatus.setText(order.getStatusStr());
        merchantLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductOrderDetailActivity.this,
                        ProductMerchantActivity.class);
                intent.putExtra("id",
                        order.getMerchant()
                                .getId());
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
            }
        });

        itemsLayout.removeAllViews();
        for (final ProductSubOrder subOrder : order.getSubOrders()) {
            View itemView = getLayoutInflater().inflate(R.layout.product_order_item, null);
            TextView tvProductTitle = (TextView) itemView.findViewById(R.id.tv_title);
            ImageView imgCover = (ImageView) itemView.findViewById(R.id.img_cover);
            TextView tvSkuInfo = (TextView) itemView.findViewById(R.id.tv_sku_info);
            TextView tvPrice = (TextView) itemView.findViewById(R.id.tv_price);
            TextView tvQuantity = (TextView) itemView.findViewById(R.id.tv_quantity);
            TextView tvActivityOverHint = (TextView) itemView.findViewById(R.id.tv_activity_over);
            TextView tvRefundInfo = (TextView) itemView.findViewById(R.id.tv_refund_info);
            Button btnRefundStatus = (Button) itemView.findViewById(R.id.btn_refund_status);
            LinearLayout refundInfoLayout = (LinearLayout) itemView.findViewById(R.id.layout3);

            tvProductTitle.setText(subOrder.getProduct()
                    .getTitle());

            String url = JSONUtil.getImagePath2(subOrder.getProduct()
                    .getCoverPath(), imgCover.getLayoutParams().width);
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

            // 退款信息
            boolean hasRefundInfo;
            if (subOrder.getRefundStatus() == 1) {
                // 退款申请中
                tvRefundInfo.setVisibility(View.VISIBLE);
                hasRefundInfo = true;
                tvRefundInfo.setText(getString(R.string.label_refund_processing,
                        NumberFormatUtil.formatDouble2StringWithTwoFloat(subOrder.getRefundInfo()
                                .getPayMoney())));
            } else if (subOrder.getRefundStatus() == 2) {
                // 退款成功
                hasRefundInfo = true;
                tvRefundInfo.setVisibility(View.VISIBLE);
                tvRefundInfo.setText(getString(R.string.label_refunded_money,
                        NumberFormatUtil.formatDouble2StringWithTwoFloat(subOrder.getRefundInfo()
                                .getPayMoney())));
            } else {
                tvRefundInfo.setVisibility(View.INVISIBLE);
                hasRefundInfo = false;
            }

            // 新版退款流程退款按钮
            boolean hasRefundStatus = true;
            btnRefundStatus.setVisibility(View.VISIBLE);
            if (subOrder.getRefundStatus() == 0) {
                // 没有申请退款
                if (order.getStatus() == 88) {
                    btnRefundStatus.setText(R.string.label_refund);
                } else if (order.getStatus() == 89) {
                    // 待收货
                    btnRefundStatus.setText(R.string.label_refund_or_return);
                } else {
                    btnRefundStatus.setVisibility(View.GONE);
                    hasRefundStatus = false;
                }
            } else if (subOrder.getRefundStatus() == 4) {
                // 等待处理退款
                btnRefundStatus.setText(R.string.label_refund_detail);
            } else if (subOrder.getRefundStatus() == 5) {
                // 等待处理退货
                btnRefundStatus.setText(R.string.label_refund_or_return_detail);
            } else if (subOrder.getRefundStatus() == 6) {
                // 商家拒绝退款/退货
                btnRefundStatus.setText(R.string.label_refund_declined);
            } else if (subOrder.getRefundStatus() == 7) {
                // 商家拒绝退货
                btnRefundStatus.setText(R.string.label_return_declined);
            } else if (subOrder.getRefundStatus() == 8) {
                // 等待退货
                btnRefundStatus.setText(R.string.label_wait_return);
            } else if (subOrder.getRefundStatus() == 9) {
                // 等待商家确认
                btnRefundStatus.setText(R.string.label_merchant_confirming);
            } else if (subOrder.getRefundStatus() == 10) {
                // 商家未收到退货
                btnRefundStatus.setText(R.string.label_merchant_not_received);
            } else if (subOrder.getRefundStatus() == 11) {
                // 退款/退货完成
                if (subOrder.getRefundInfo()
                        .getType() == 2) {
                    btnRefundStatus.setText(R.string.label_return_complete);
                } else {
                    btnRefundStatus.setText(R.string.label_refund_complete);
                }
            } else if (subOrder.getRefundStatus() == 12) {
                // 退款/退货取消
                if (subOrder.getRefundInfo()
                        .getType() == 2) {
                    // 待发货状态下的退货取消
                    btnRefundStatus.setText(R.string.label_return_canceled);
                } else {
                    btnRefundStatus.setText(R.string.label_refund_canceled);
                }
            } else if (subOrder.getRefundStatus() == 13) {
                if (subOrder.getRefundInfo()
                        .getType() == 2) {
                    btnRefundStatus.setText(R.string.label_return_closed2);
                } else {
                    btnRefundStatus.setText(R.string.label_refund_closed);
                }
            } else {
                btnRefundStatus.setVisibility(View.GONE);
            }

            refundInfoLayout.setVisibility((hasRefundInfo || hasRefundStatus) ? View.VISIBLE :
                    View.GONE);


            if (order.getStatus() == 10) {
                if (subOrder.getActivityStatus() == 2) {
                    tvActivityOverHint.setVisibility(View.VISIBLE);
                } else {
                    tvActivityOverHint.setVisibility(View.GONE);
                }
            } else {
                tvActivityOverHint.setVisibility(View.GONE);
            }

            tvSkuInfo.setText(getString(R.string.label_sku2,
                    subOrder.getSku()
                            .getName()));
            tvPrice.setText(getString(R.string.label_price,
                    NumberFormatUtil.formatDouble2StringWithTwoFloat(subOrder.getActualMoney() /
                            subOrder.getQuantity())));
            tvQuantity.setText("x" + String.valueOf(subOrder.getQuantity()));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ProductOrderDetailActivity.this,
                            ShopProductDetailActivity.class);
                    intent.putExtra("id",
                            subOrder.getProduct()
                                    .getId());
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                }
            });

            btnRefundStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent;
                    if (subOrder.getRefundStatus() > 3) {
                        // 已经进入退款流程,进入退款详情
                        intent = new Intent(ProductOrderDetailActivity.this,
                                ProductRefundDetailActivity.class);
                        intent.putExtra("from_detail", true);
                        intent.putExtra("order", order);
                        intent.putExtra("id", subOrder.getId());
                    } else {
                        if (order.getStatus() == 88) {
                            // 待发货
                            intent = new Intent(ProductOrderDetailActivity.this,
                                    ProductRefundApplyActivity.class);
                            intent.putExtra("order", order);
                            intent.putExtra("id", subOrder.getId());
                        } else if (order.getStatus() == 89) {
                            // 已发货
                            intent = new Intent(ProductOrderDetailActivity.this,
                                    SelectRefundTypeActivity.class);
                            intent.putExtra("order", order);
                            intent.putExtra("id", subOrder.getId());
                        } else {
                            intent = new Intent(ProductOrderDetailActivity.this,
                                    ProductRefundDetailActivity.class);
                            intent.putExtra("from_detail", true);
                            intent.putExtra("order", order);
                            intent.putExtra("id", subOrder.getId());
                        }
                    }

                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                }
            });

            itemsLayout.addView(itemView);
        }
    }

    private void setPricesInfo() {
        orderMemoLayout.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(order.getRemark())) {
            orderMemoLayout.setVisibility(View.VISIBLE);
            tvMemo.setText(order.getRemark());
        } else {
            orderMemoLayout.setVisibility(View.GONE);
        }
        tvTotalProductPrice.setText(getString(R.string.label_price,
                NumberFormatUtil.formatDouble2StringWithTwoFloat(order.getActualMoney())));
        tvShippingFee.setText("+" + getString(R.string.label_price,
                NumberFormatUtil.formatDouble2StringWithTwoFloat(order.getShippingFee())));
        tvTotalPrice.setText(getString(R.string.label_price,
                NumberFormatUtil.formatDouble2StringWithTwoFloat(order.getActualMoney() + order
                        .getShippingFee() - order.getRedPacketMoney() - order.getAidMoney())));
        tvFinalTotalPrice.setText(NumberFormatUtil.formatDouble2StringWithTwoFloat(order
                .getActualMoney() + order.getShippingFee() - order.getRedPacketMoney() - order
                .getAidMoney()));
        if (JSONUtil.isEmpty(order.getRedPacketNo())) {
            redPacketLayout.setVisibility(View.GONE);
        } else {
            redPacketLayout.setVisibility(View.VISIBLE);
            tvRedPacketAmount.setText(getString(R.string.label_price6,
                    NumberFormatUtil.formatDouble2StringWithTwoFloat(order.getRedPacketMoney())));
        }
        if (TextUtils.isEmpty(order.getUserCouponId())) {
            couponLayout.setVisibility(View.GONE);
        } else {
            couponLayout.setVisibility(View.VISIBLE);
            tvCouponAmount.setText(getString(R.string.label_price6,
                    NumberFormatUtil.formatDouble2StringWithTwoFloat(order.getAidMoney())));
        }
        btnOrderAction.setEnabled(true);
    }

    private void setOrderingInfo() {
        tvOrderNo.setText(order.getOrderNo());
        tvOrderTime.setText(order.getCreatedAt()
                .toString("yyyy-MM-dd HH:mm:ss"));

        // 自动收货提示
        // 若有一个商品处于退款状态，则自动确认收货时间隐藏
        if (order.getStatus() == 89 && !order.isRefunding() && order.getExpireTime() != null) {
            tvAutoConfirmHint.setVisibility(View.VISIBLE);
            DateTime now = new DateTime();
            String str = "";
            if (order.getExpireTime()
                    .isAfter(now)) {
                // 还没有自动确认,计算剩余时间
                long leftMs = order.getExpireTime()
                        .getMillis() - now.getMillis();
                SimpleDateFormat simpleDateFormat;
                if (leftMs > 24 * 60 * 60 * 1000) {
                    // 超过一天
                    leftMs -= 24 * 60 * 60 * 1000;
                    simpleDateFormat = new SimpleDateFormat(getString(R.string.format_day),
                            Locale.getDefault());
                } else if (leftMs > 60 * 60 * 1000) {
                    simpleDateFormat = new SimpleDateFormat(getString(R.string.format_hour_minute),
                            Locale.getDefault());
                } else {
                    simpleDateFormat = new SimpleDateFormat(getString(R.string.format_minute),
                            Locale.getDefault());
                }

                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                str = simpleDateFormat.format(leftMs);
            }
            tvAutoConfirmHint.setText(getString(R.string.label_auto_confirm_time_alert, str));
        } else {
            tvAutoConfirmHint.setVisibility(View.GONE);
        }
    }

    private void setBottomInfo() {
        switch (order.getStatus()) {
            case 10:
                // 去付款
                bottomLayout.setVisibility(View.VISIBLE);
                btnOrderAction.setText(R.string.label_go_pay);
                break;
            case 89:
                // 待收货
                bottomLayout.setVisibility(View.VISIBLE);
                btnOrderAction.setText(R.string.label_confirm_receive);
                break;
            case 90:
                // 去评价
                bottomLayout.setVisibility(View.VISIBLE);
                btnOrderAction.setText(R.string.label_comment_immediately);
                break;
            default:
                bottomLayout.setVisibility(View.GONE);
                break;
        }
    }

    @OnClick(R.id.btn_order_action)
    void onOrderAction() {
        switch (order.getStatus()) {
            case 10:
                // 去付款
                onPay();
                break;
            case 89:
                // 确认收货
                if (order.isRefunding()) {
                    Toast.makeText(this, R.string.msg_cannot_confirming, Toast.LENGTH_SHORT)
                            .show();
                } else {
                    confirmShipping();
                }
                break;
            case 90:
                // 去评价
                if (order == null || order.getSubOrders() == null || order.getSubOrders()
                        .isEmpty()) {
                    return;
                }
                Intent intent2 = new Intent(this, CommentProductOrderActivity.class);
                intent2.putExtra(CommentProductOrderActivity.ARG_PRODUCT_ORDER, order);
                startActivity(intent2);
                overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                break;
            default:
                break;
        }
    }

    private void onPay() {
        // 如果使用红包，付款前先确认红包是否满足原红包的满减条件
        if (!TextUtils.isEmpty(order.getRedPacketNo()) && order.getRedPacketMoney() > 0) {
            checkSub = HljHttpSubscriber.buildSubscriber(this)
                    .setProgressDialog(DialogUtil.createProgressDialog(ProductOrderDetailActivity
                            .this))
                    .setOnNextListener(new SubscriberOnNextListener<ProductOrderRedPacketState>() {
                        @Override
                        public void onNext(ProductOrderRedPacketState productOrderRedPacketState) {
                            if (productOrderRedPacketState.getRedPacketUseless() > 0) {
                                // 红包无效，弹窗提示
                                showRedPacketUselessDlg();
                            } else {
                                payOrder(order.getActualMoney() + order.getShippingFee() - order
                                        .getRedPacketMoney() - order.getAidMoney());
                            }
                        }
                    })
                    .build();
            OrderApi.checkProductOrderRedPacket(order.getId())
                    .subscribe(checkSub);
        } else {
            payOrder(order.getActualMoney() + order.getShippingFee() - order.getAidMoney());
        }
    }

    private void showRedPacketUselessDlg() {
        checkDlg = DialogUtil.createDoubleButtonDialog(checkDlg,
                this,
                "订单金额不满足红包满减条件，\n是否继续支付？",
                "",
                "",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        checkDlg.cancel();
                        // 确认不使用红包继续付款
                        payOrder(order.getActualMoney() + order.getShippingFee());
                    }
                });
        checkDlg.show();
    }

    private void payOrder(double money) {
        if (paySubscriber == null) {
            paySubscriber = new RxBusSubscriber<PayRxEvent>() {
                @Override
                protected void onEvent(PayRxEvent rxEvent) {
                    Intent intent;
                    switch (rxEvent.getType()) {
                        case PAY_SUCCESS:
                            // 支付成功，跳转成功页面
                            intent = new Intent(ProductOrderDetailActivity.this,
                                    AfterPayProductOrderActivity.class);
                            intent.putExtra("order_type",
                                    Constants.OrderType.WEDDING_PRODUCT_ORDER);
                            intent.putExtra("product_ids", productIds);
                            initLoad(false);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(0, 0);
                            break;
                        case PAY_CANCEL:
                            break;
                    }
                }
            };
        }
        JSONObject jsonObject = new JSONObject();
        StringBuilder sb = new StringBuilder();
        try {
            jsonObject.put("order_ids", String.valueOf(order.getId()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (!CommonUtil.isCollectionEmpty(order.getSubOrders())) {
            for (ProductSubOrder subOrder : order.getSubOrders()) {
                ShopProduct product = subOrder.getProduct();
                if (product != null) {
                    sb.append(product.getId())
                            .append(",");
                }

            }
            if (!TextUtils.isEmpty(sb) && sb.lastIndexOf(",") > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
        }
        productIds = sb.toString();
        PayConfig.Builder builder = new PayConfig.Builder(this);
        DataConfig dataConfig = Session.getInstance()
                .getDataConfig(this);
        builder.payAgents(dataConfig != null ? dataConfig.getPayTypes() : null,
                DataConfig.getWalletPayAgents());
        builder.params(jsonObject)
                .path(Constants.getAbsUrl(Constants.HttpPath.PRODUCT_ORDER_PAYMENT))
                .price(money > 0 ? money : 0)
                .subscriber(paySubscriber)
                .build()
                .pay();
    }

    @OnClick(R.id.btn_chat)
    void onChatMerchant() {
        Intent intent = new Intent(this, WSCustomerChatActivity.class);
        intent.putExtra("user",
                order.getMerchant()
                        .toUser(1));
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    @OnClick(R.id.btn_call)
    void onCallMerchant() {
        if (order.getMerchant()
                .getContactPhone() != null && !order.getMerchant()
                .getContactPhone()
                .isEmpty()) {
            callUp();
        } else {
            // 提示没有商家的联系电话
            Toast.makeText(ProductOrderDetailActivity.this,
                    getString(R.string.msg_no_merchant_number),
                    Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void callUp() {
        if (order.getMerchant()
                .getContactPhone()
                .size() == 1) {
            String phone = order.getMerchant()
                    .getContactPhone()
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
                    order.getMerchant()
                            .getContactPhone());
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

    /**
     * 取消订单
     */
    @Override
    public void onOkButtonClick() {
        if (order == null || order.getStatus() != 10 || (cancelOrderDlg != null && cancelOrderDlg
                .isShowing())) {
            return;
        }
        cancelSub = HljHttpSubscriber.buildSubscriber(this)
                .setProgressDialog(DialogUtil.createProgressDialog(this))
                .setOnNextListener(new SubscriberOnNextListener<ProductOrder>() {
                    @Override
                    public void onNext(ProductOrder o) {
                        // 支付成功,发送刷新订单列表的消息
                        ProductOrderDetailActivity.this.order = o;
                        setOrderDetails();
                    }
                })
                .build();
        cancelOrderDlg = DialogUtil.createDoubleButtonDialog(cancelOrderDlg,
                this,
                getString(R.string.msg_cancel_order),
                getString(R.string.label_cancel_order),
                getString(R.string.label_wrong_action),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cancelOrderDlg.cancel();
                        OrderApi.cancelProductOrder(order.getId())
                                .subscribe(cancelSub);
                    }
                });
        cancelOrderDlg.show();

        super.onOkButtonClick();
    }

    private void confirmShipping() {
        confirmSub = HljHttpSubscriber.buildSubscriber(this)
                .setProgressDialog(DialogUtil.createProgressDialog(this))
                .setOnNextListener(new SubscriberOnNextListener<ProductOrder>() {
                    @Override
                    public void onNext(ProductOrder o) {
                        ProductOrderDetailActivity.this.order = o;
                        setOrderDetails();
                        // 跳转到交易成功
                        Intent intent = new Intent(ProductOrderDetailActivity.this,
                                AfterConfirmReceiveActivity.class);
                        intent.putExtra("productOrder", order);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_from_bottom, 0);
                    }
                })
                .build();

        confirmShippingDlg = DialogUtil.createDoubleButtonDialog(confirmShippingDlg,
                this,
                getString(R.string.msg_confirm_shipping),
                getString(R.string.label_confirm_receive),
                getString(R.string.label_wrong_action),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        OrderApi.confirmProductOrderShipping(order.getId())
                                .subscribe(confirmSub);
                    }
                });
        confirmShippingDlg.show();
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        initLoad(true);
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
            // 距离结束时间为15分钟的时候开始显示倒计时
            if (millisUntilFinished < 1000 * 60 * 15) {
                int minutes = (int) (millisUntilFinished / (1000 * 60));
                int seconds = (int) ((millisUntilFinished - minutes * 1000 * 60) / 1000);
                tvAlertExpiredTime.setText(Html.fromHtml(getString(R.string
                                .label_order_expired_count_down_time,
                        minutes,
                        seconds)));
            }

        }

        @Override
        public void onFinish() {
            // 到达过期时间,客户端修改订单状态
            order.autoCancelOrder();
            setOrderDetails();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setOrderDetails();
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
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(initSub, checkSub, paySubscriber, confirmSub, cancelSub);
    }
}
