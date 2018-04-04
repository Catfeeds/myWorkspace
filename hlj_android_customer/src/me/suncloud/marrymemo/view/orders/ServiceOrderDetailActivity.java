package me.suncloud.marrymemo.view.orders;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.suncloud.hljweblibrary.views.activities.HljWebViewActivity;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.models.WorkRule;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.hunliji.hljimagelibrary.views.activities.PicsPageViewActivity;
import com.hunliji.hljpaymentlibrary.PayAgent;
import com.hunliji.hljpaymentlibrary.PayConfig;
import com.hunliji.hljpaymentlibrary.models.PayRxEvent;
import com.hunliji.hljpaymentlibrary.views.activities.xiaoxi_installment.MyBillListActivity;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.ContactsAdapter;
import me.suncloud.marrymemo.api.orders.OrderApi;
import me.suncloud.marrymemo.model.orders.ServeCustomerInfo;
import me.suncloud.marrymemo.model.orders.ServiceOrder;
import me.suncloud.marrymemo.model.orders.ServiceOrderIdBody;
import me.suncloud.marrymemo.model.orders.ServiceOrderNoBody;
import me.suncloud.marrymemo.model.orders.ServiceOrderSub;
import me.suncloud.marrymemo.util.DataConfig;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.ServeCustomerInfoUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.TimeUtil;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.AfterConfirmReceiveActivity;
import me.suncloud.marrymemo.view.AfterPayActivity;
import me.suncloud.marrymemo.view.OrderInfoEditActivity;
import me.suncloud.marrymemo.view.RefundApplyActivity;
import me.suncloud.marrymemo.view.WorkActivity;
import me.suncloud.marrymemo.view.chat.WSCustomerChatActivity;
import me.suncloud.marrymemo.view.comment.CommentServiceActivity;
import me.suncloud.marrymemo.view.merchant.MerchantDetailActivity;
import rx.Subscriber;

public class ServiceOrderDetailActivity extends HljBaseActivity implements
        PullToRefreshScrollView.OnRefreshListener {

    @BindView(R.id.tv_total_money_label)
    TextView tvTotalMoneyLabel;
    @BindView(R.id.tv_total_money)
    TextView tvTotalMoney;
    @BindView(R.id.btn_action)
    Button btnAction;
    @BindView(R.id.bottom_layout)
    LinearLayout bottomLayout;
    @BindView(R.id.tv_order_alert)
    TextView tvOrderAlert;
    @BindView(R.id.order_alert_layout)
    LinearLayout orderAlertLayout;
    @BindView(R.id.tv_customer_name)
    TextView tvCustomerName;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.tv_serve_time)
    TextView tvServeTime;
    @BindView(R.id.info_layout)
    LinearLayout infoLayout;
    @BindView(R.id.tv_merchant_name)
    TextView tvMerchantName;
    @BindView(R.id.tv_order_status)
    TextView tvOrderStatus;
    @BindView(R.id.img_cover)
    ImageView imgCover;
    @BindView(R.id.img_installment)
    ImageView imgInstallment;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_price)
    TextView tvPrice;
    @BindView(R.id.btn_action1)
    Button btnAction1;
    @BindView(R.id.btn_action2)
    Button btnAction2;
    @BindView(R.id.action_layout)
    LinearLayout actionLayout;
    @BindView(R.id.tv_work_price2)
    TextView tvWorkPrice2;
    @BindView(R.id.tv_pay_now_money)
    TextView tvPayNowMoney;
    @BindView(R.id.pay_now_money_layout)
    LinearLayout payNowMoneyLayout;
    @BindView(R.id.tv_pay_all_saved_money)
    TextView tvPayAllSavedMoney;
    @BindView(R.id.pay_all_saved_money_layout)
    LinearLayout payAllSavedMoneyLayout;
    @BindView(R.id.tv_coupon_money)
    TextView tvCouponMoney;
    @BindView(R.id.coupon_money_layout)
    LinearLayout couponMoneyLayout;
    @BindView(R.id.tv_red_packet_money)
    TextView tvRedPacketMoney;
    @BindView(R.id.red_packet_money_layout)
    LinearLayout redPacketMoneyLayout;
    @BindView(R.id.tv_bottom_money_label)
    TextView tvBottomMoneyLabel;
    @BindView(R.id.tv_bottom_money)
    TextView tvBottomMoney;
    @BindView(R.id.tv_order_num)
    TextView tvOrderNum;
    @BindView(R.id.tv_order_time)
    TextView tvOrderTime;
    @BindView(R.id.tv_prepay_remind)
    TextView tvPrepayRemind;
    @BindView(R.id.scroll_view)
    PullToRefreshScrollView scrollView;
    @BindView(R.id.btn_lucky_draw)
    ImageButton btnLuckyDraw;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.activity_service_order_detail)
    RelativeLayout activityServiceOrderDetail;
    @BindView(R.id.customer_message_layout)
    LinearLayout customerMessageLayout;
    @BindView(R.id.tv_message)
    TextView tvMessage;
    @BindView(R.id.pay_method_layout)
    View payMethodLayout;
    @BindView(R.id.tv_pay_method)
    TextView tvPayMethod;
    @BindView(R.id.tv_paid_deposit_money)
    TextView tvPaidDepositMoney;
    @BindView(R.id.paid_deposit_money_layout)
    LinearLayout paidDepositMoneyLayout;
    @BindView(R.id.btn_delete_order)
    Button btnDeleteOrder;
    @BindView(R.id.btn_delay_confirm)
    Button btnDelayConfirm;
    @BindView(R.id.content_layout)
    RelativeLayout contentLayout;
    @BindView(R.id.work_info_layout)
    View workInfoLayout;
    @BindView(R.id.sales_layout)
    View salesLayout;
    @BindView(R.id.tv_sales_text)
    TextView tvSalesText;
    @BindView(R.id.tv_bottom_rmb)
    TextView tvBottomRmb;
    @BindView(R.id.serve_time_layout)
    LinearLayout serveTimeLayout;
    @BindView(R.id.tv_now_money_label)
    TextView tvNowMoneyLabel;
    @BindView(R.id.tv_paid_deposit_money_label)
    TextView tvPaidDepositMoneyLabel;
    @BindView(R.id.img_intent_money)
    ImageView imgIntentMoney;
    @BindView(R.id.img_arrow)
    ImageView imgArrow;
    @BindView(R.id.tv_paid_intent_money_label)
    TextView tvPaidIntentMoneyLabel;
    @BindView(R.id.tv_paid_intent_money)
    TextView tvPaidIntentMoney;
    @BindView(R.id.paid_intent_money_layout)
    LinearLayout paidIntentMoneyLayout;
    @BindView(R.id.tv_paid_rest_money_label)
    TextView tvPaidRestMoneyLabel;
    @BindView(R.id.tv_paid_rest_money)
    TextView tvPaidRestMoney;
    @BindView(R.id.paid_rest_money_layout)
    LinearLayout paidRestMoneyLayout;
    @BindView(R.id.tv_pay_all_saved_hint)
    TextView tvPayAllSavedHint;
    @BindView(R.id.grid_protocol_images)
    GridLayout gridProtocolImages;
    @BindView(R.id.protocol_pics_layout)
    LinearLayout protocolPicsLayout;

    private long id;
    private ServiceOrder serviceOrder;
    private AlertTimer alertTimer;
    private HljHttpSubscriber cancelSub;
    private Dialog cancelDlg;
    private HljHttpSubscriber deleteSub;
    private Dialog deleteDlg;
    private HljHttpSubscriber confirmSub;
    private Dialog confirmDlg;
    private Subscriber<PayRxEvent> paySubscriber;
    private double needPay;
    private Dialog contactDialog;
    private HljHttpSubscriber detailSub;
    private Dialog cancelRefundDlg;
    private HljHttpSubscriber cancelRefundSub;
    private ConfirmAlertTimer confirmAlertTimer;
    private boolean isRefundIntentMoney = false; // 在用户付完意向金且没有付余款的情况下，如果退款则直接退还意向金
    private HljHttpSubscriber delayConfirmSub;
    private Dialog delayDlg;
    private HljHttpSubscriber updateSub;
    private boolean infoUpdated;
    private int protocolImgViewSize;
    private int protocolImgSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_order_detail);
        ButterKnife.bind(this);

        initValues();
        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (infoUpdated) {
            infoUpdated = false;
        } else {
            initLoad(false);
        }
    }

    private void initValues() {
        id = getIntent().getLongExtra("id", 0);
        protocolImgViewSize = Math.round((CommonUtil.getDeviceSize(this).x - CommonUtil.dp2px(this,
                18)) / 3);
        protocolImgSize = Math.round(protocolImgViewSize - CommonUtil.dp2px(this, 14));
    }

    private void initViews() {
        scrollView.setOnRefreshListener(this);
    }

    private void initLoad(boolean isReload) {
        if (detailSub != null && !detailSub.isUnsubscribed()) {
            detailSub.unsubscribe();
        }
        detailSub = HljHttpSubscriber.buildSubscriber(this)
                .setContentView(contentLayout)
                .setProgressBar(isReload ? null : progressBar)
                .setOnNextListener(new SubscriberOnNextListener<ServiceOrder>() {
                    @Override
                    public void onNext(ServiceOrder order) {
                        scrollView.onRefreshComplete();
                        serviceOrder = order;
                        setOrderView();
                    }
                })
                .build();
        OrderApi.getServiceOrderDetail(id)
                .subscribe(detailSub);
    }

    private void reloadDetail() {
        initLoad(true);
    }

    private void setOrderView() {
        setOkButtonView();
        setOrderAlertLayout();
        setCustomerInfo();
        setWorkInfoView();
        setCustomerMemoView();
        setPricesView();
        setProtocolImages();
        setBottomOrderInfoView();
        setBottomAction();
    }

    private void setOkButtonView() {
        if (serviceOrder.getOrderSub()
                .getStatus() == ServiceOrderSub.STATUS_WAITING_FOR_THE_PAYMENT) {
            setOkText(R.string.label_cancel_order);
            showOkText();
        } else {
            hideOkText();
        }
    }

    private void setOrderAlertLayout() {
        if (alertTimer != null) {
            alertTimer.cancel();
            alertTimer = null;
        }
        if (confirmAlertTimer != null) {
            confirmAlertTimer.cancel();
            confirmAlertTimer = null;
        }
        if (serviceOrder.getOrderSub()
                .getStatus() == ServiceOrderSub.STATUS_WAITING_FOR_THE_PAYMENT && serviceOrder
                .getOrderSub()
                .getExpireTime() != null) {
            // 待付款状态且有过期时间，则显示倒计时提示
            // 未参加活动的套餐下单后七天未付款，订单自动关闭
            orderAlertLayout.setVisibility(View.VISIBLE);
            tvOrderAlert.setText(Html.fromHtml(getString(R.string.label_order_expired_time2,
                    serviceOrder.getOrderSub()
                            .getExpireTime()
                            .toString("yyyy-MM-dd HH:mm:ss"))));
            if (serviceOrder.getRule() == null) {
                // 非活动套餐
                // 启动计时器，距离结束15分钟开始显示倒计时
                DateTime now = new DateTime(HljTimeUtils.getServerCurrentTimeMillis());
                long millisInFuture = serviceOrder.getOrderSub()
                        .getExpireTime()
                        .getMillis() - now.getMillis();
                alertTimer = new AlertTimer(millisInFuture,
                        1000,
                        R.string.label_order_expired_count_down_time);
                alertTimer.start();
            } else {
                // 活动套餐，显示倒计时
                if (serviceOrder.getOrderSub()
                        .getExpireTime()
                        .isBeforeNow()) {
                    // 已经过期了，重新请求刷新订单
                    reloadDetail();
                    return;
                } else {
                    // 没有过期，需要显示倒计时
                    DateTime now = new DateTime(HljTimeUtils.getServerCurrentTimeMillis());
                    long millisInFuture = serviceOrder.getOrderSub()
                            .getExpireTime()
                            .getMillis() - now.getMillis();
                    alertTimer = new AlertTimer(millisInFuture,
                            1000,
                            R.string.label_order_expired_count_down_time3);
                    alertTimer.start();
                }
            }
        } else if (serviceOrder.getOrderSub()
                .getStatus() == ServiceOrderSub.STATUS_MERCHANT_ACCEPT_ORDER && serviceOrder
                .getOrderSub()
                .isFinished() && serviceOrder.getOrderSub()
                .getExpireTime() != null) {
            // 商家已确认服务，倒计时自动确认服务
            String hintStr;
            if (serviceOrder.getOrderSub()
                    .getMerchant()
                    .getProperty()
                    .getId() == Merchant.PROPERTY_WEDDING_DRESS_PHOTO || serviceOrder.getOrderSub()
                    .getMerchant()
                    .getProperty()
                    .getId() == Merchant.PROPERTY_WEDDING_PHOTO || serviceOrder.getOrderSub()
                    .getMerchant()
                    .getProperty()
                    .getId() == Merchant.PROPERTY_WEDDING_SHOOTING) {
                // 如果是婚纱摄影、婚礼摄影、婚礼摄像这三种，有"交付成片字段"。否则没有
                hintStr = getString(R.string.label_order_merchant_confirm2);
            } else {
                hintStr = getString(R.string.label_order_merchant_confirm1);
            }
            String hintStr2;
            if (serviceOrder.getOrderSub()
                    .getMerchant()
                    .getProperty()
                    .getId() == Merchant.PROPERTY_WEDDING_DRESS_PHOTO) {
                // 如果是婚纱摄影的话，有延期提示字段
                hintStr2 = getString(R.string.label_order_confirm_count_down_time2);
            } else {
                hintStr2 = getString(R.string.label_order_confirm_count_down_time1);
            }
            DateTime now = new DateTime();
            long millisInFuture = serviceOrder.getOrderSub()
                    .getExpireTime()
                    .getMillis() - now.getMillis();
            confirmAlertTimer = new ConfirmAlertTimer(millisInFuture, 1000, hintStr + hintStr2);
            confirmAlertTimer.start();
            orderAlertLayout.setVisibility(View.VISIBLE);
        } else if (serviceOrder.getOrderSub()
                .getStatus() == ServiceOrderSub.STATUS_MERCHANT_REFUSE_ORDER) {
            orderAlertLayout.setVisibility(View.VISIBLE);
            tvOrderAlert.setText("商家拒绝接单\n" + serviceOrder.getOrderSub()
                    .getReason());
        } else if (serviceOrder.getOrderSub()
                .getStatus() == ServiceOrderSub.STATUS_ORDER_AUTO_CLOSED) {
            orderAlertLayout.setVisibility(View.VISIBLE);
            tvOrderAlert.setText("用户未付款，订单自动关闭");
        } else if (serviceOrder.getOrderSub()
                .getStatus() == ServiceOrderSub.STATUS_ORDER_CLOSED) {
            orderAlertLayout.setVisibility(View.VISIBLE);
            tvOrderAlert.setText("买家主动取消");
        } else {
            // 除此之外不需要显示提示
            orderAlertLayout.setVisibility(View.GONE);
        }
    }


    private void setCustomerInfo() {
        tvCustomerName.setText(serviceOrder.getBuyerName());
        tvPhone.setText(serviceOrder.getBuyerPhone());
        if (serviceOrder.getOrderSub()
                .isPayedSomeStatus() && serviceOrder.getOrderPayType() == ServiceOrder
                .ORDER_PAY_TYPE_INTENT && serviceOrder.getOrderSub()
                .getMerchant()
                .getPropertyId() != Merchant.PROPERTY_WEDDING_DRESS_PHOTO && serviceOrder
                .getOrderSub()
                .getMoneyStatus() < ServiceOrderSub.MONEY_STATUS_PAID_DEPOSIT) {
            // 是意向金支付，并且需要(非婚纱摄影)选择服务时间
            serveTimeLayout.setVisibility(View.VISIBLE);
            if (serviceOrder.getWeddingTime() == null) {
                tvServeTime.setText(R.string.hint_serve_time2);
                tvServeTime.setTextColor(ContextCompat.getColor(this, R.color.colorGray));
            } else {
                tvServeTime.setText(serviceOrder.getWeddingTime()
                        .toString("yyyy-MM-dd"));
                tvServeTime.setTextColor(ContextCompat.getColor(this, R.color.colorBlack2));
            }
            imgArrow.setVisibility(View.VISIBLE);
            infoLayout.setClickable(true);
        } else {
            if (serviceOrder.getWeddingTime() == null) {
                serveTimeLayout.setVisibility(View.GONE);
            } else {
                serveTimeLayout.setVisibility(View.VISIBLE);
                tvServeTime.setText(serviceOrder.getWeddingTime()
                        .toString("yyyy-MM-dd"));
                tvServeTime.setTextColor(ContextCompat.getColor(this, R.color.colorBlack2));
            }
            imgArrow.setVisibility(View.GONE);
            infoLayout.setClickable(false);
        }
    }

    private void setWorkInfoView() {
        tvMerchantName.setText(serviceOrder.getOrderSub()
                .getMerchant()
                .getName());
        imgIntentMoney.setVisibility(serviceOrder.getOrderPayType() == ServiceOrder
                .ORDER_PAY_TYPE_INTENT ? View.VISIBLE : View.GONE);
        imgInstallment.setVisibility(serviceOrder.isInstallment() ? View.VISIBLE : View.GONE);
        if (serviceOrder.isInstallment()) {
            imgIntentMoney.setVisibility(View.GONE);
        }
        tvMerchantName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ServiceOrderDetailActivity.this,
                        MerchantDetailActivity.class);
                intent.putExtra("id",
                        serviceOrder.getOrderSub()
                                .getMerchant()
                                .getId());
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
            }
        });
        tvOrderStatus.setText(serviceOrder.getOrderSub()
                .getStatusStr());
        Glide.with(this)
                .load(ImageUtil.getImagePath(serviceOrder.getOrderSub()
                        .getWork()
                        .getCoverPath(), CommonUtil.dp2px(this, 160)))
                .into(imgCover);
        tvTitle.setText(serviceOrder.getOrderSub()
                .getWork()
                .getTitle());
        tvPrice.setText(getString(R.string.label_price4,
                Util.formatDouble2String(serviceOrder.getOrderSub()
                        .getActualPrice())));
        if (serviceOrder.getRule() != null && !TextUtils.isEmpty(serviceOrder.getRule()
                .getName())) {
            salesLayout.setVisibility(View.VISIBLE);
            tvSalesText.setText(serviceOrder.getRule()
                    .getName());
        } else {
            salesLayout.setVisibility(View.GONE);
        }

        // 在不同的订单状态下需要不同的操作
        if (serviceOrder.getOrderSub()
                .getStatus() == ServiceOrderSub.STATUS_WAITING_FOR_ACCEPT_ORDER || serviceOrder
                .getOrderSub()
                .getStatus() == ServiceOrderSub.STATUS_MERCHANT_ACCEPT_ORDER || serviceOrder
                .getOrderSub()
                .getStatus() == ServiceOrderSub.STATUS_MERCHANT_REFUSE_ORDER) {
            // 在（等待商家接单、商家拒绝接单、商家已接单、待确认服务）这几个状态下，需要显示退款按钮
            actionLayout.setVisibility(View.VISIBLE);
            btnAction2.setText("申请退款");
            btnAction2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRefundOrder();
                }
            });
        } else if (serviceOrder.getOrderSub()
                .getStatus() == ServiceOrderSub.STATUS_REFUND_REVIEWING) {
            // 退款审核中，可以取消退款
            actionLayout.setVisibility(View.VISIBLE);
            btnAction2.setText("撤销退款申请");
            btnAction2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCancelRefundOrder();
                }
            });
        } else if (serviceOrder.getOrderSub()
                .getStatus() == ServiceOrderSub.STATUS_REFUND_SUCCEED) {
            actionLayout.setVisibility(View.GONE);
        } else {
            actionLayout.setVisibility(View.VISIBLE);
            btnAction2.setText("电话商家");
            btnAction2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onMerchantCall();
                }
            });
        }
        btnAction1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMerchantChat();
            }
        });
    }

    private void setCustomerMemoView() {
        if (!TextUtils.isEmpty(serviceOrder.getMessage())) {
            customerMessageLayout.setVisibility(View.VISIBLE);
            tvMessage.setText(serviceOrder.getMessage());
        } else {
            customerMessageLayout.setVisibility(View.GONE);
        }
    }

    private void setPricesView() {
        // 套餐价格
        tvWorkPrice2.setText(getString(R.string.label_price,
                Util.formatDouble2String(serviceOrder.getOrderSub()
                        .getActualPrice())));

        payAllSavedMoneyLayout.setVisibility(View.GONE);
        payNowMoneyLayout.setVisibility(View.GONE);
        paidDepositMoneyLayout.setVisibility(View.GONE);
        paidIntentMoneyLayout.setVisibility(View.GONE);
        paidRestMoneyLayout.setVisibility(View.GONE);
        redPacketMoneyLayout.setVisibility(View.GONE);
        couponMoneyLayout.setVisibility(View.GONE);
        tvBottomMoneyLabel.setText("需支付");

        if (serviceOrder.getOrderSub()
                .getRedPacketMoney() > 0) {
            redPacketMoneyLayout.setVisibility(View.VISIBLE);
            tvRedPacketMoney.setText("-" + getString(R.string.label_price,
                    Util.formatDouble2String(serviceOrder.getOrderSub()
                            .getRedPacketMoney())));
        }
        if (serviceOrder.getOrderSub()
                .getAidMoney() > 0) {
            couponMoneyLayout.setVisibility(View.VISIBLE);
            tvCouponMoney.setText("-" + getString(R.string.label_price,
                    Util.formatDouble2String(serviceOrder.getOrderSub()
                            .getAidMoney())));
        }

        if (serviceOrder.getOrderSub()
                .getMoneyStatus() == ServiceOrderSub.MONEY_STATUS_PAID_ALL) {
            payMethodLayout.setVisibility(View.VISIBLE);
            // 付款完成后也要显示付款方式
            if (serviceOrder.getOrderPayType() == ServiceOrder.ORDER_PAY_TYPE_INTENT) {
                tvPayMethod.setText("意向金支付");
            } else if (serviceOrder.getOrderPayType() == ServiceOrder.ORDER_PAY_TYPE_DEPOSIT) {
                // 使用定金支付方式
                tvPayMethod.setText("定金支付");
            } else {
                tvPayMethod.setText("全款支付");
            }

            // 用户已付全款，无论是以定金还是以全款的方式
            tvTotalMoneyLabel.setText("实付金额：");
            tvTotalMoney.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
            tvTotalMoney.setText(getString(R.string.label_price,
                    Util.formatDouble2String(serviceOrder.getOrderSub()
                            .getPaidMoney())));
            // 全款优惠
            if (serviceOrder.getOrderSub()
                    .getPayAllSavedMoney() > 0) {
                payAllSavedMoneyLayout.setVisibility(View.VISIBLE);
                tvPayAllSavedMoney.setText("-" + getString(R.string.label_price,
                        Util.formatDouble2String(serviceOrder.getOrderSub()
                                .getPayAllSavedMoney())));
            }
            // 意向金已付
            if (serviceOrder.getOrderSub()
                    .getIntentMoney() > 0) {
                paidIntentMoneyLayout.setVisibility(View.VISIBLE);
                tvPaidIntentMoney.setText("-" + getString(R.string.label_price,
                        Util.formatDouble2String(serviceOrder.getOrderSub()
                                .getIntentMoney())));
            }
            // 此标志位表示，是否是商家手动将已付意向金的订单置为已收尾款，这种状态下，不显示已付定金了
            boolean isFinishedIntentPay = serviceOrder.getOrderSub()
                    .getPaidMoney() == serviceOrder.getOrderSub()
                    .getIntentMoney();
            // 已付定金(实际定金付款=定金-意向金)
            if (serviceOrder.getOrderSub()
                    .getEarnestMoney() - serviceOrder.getOrderSub()
                    .getIntentMoney() > 0 && !isFinishedIntentPay) {
                paidDepositMoneyLayout.setVisibility(View.VISIBLE);
                tvPaidDepositMoney.setText("-" + getString(R.string.label_price,
                        Util.formatDouble2String(serviceOrder.getOrderSub()
                                .getEarnestMoney() - serviceOrder.getOrderSub()
                                .getIntentMoney())));
            }
        } else {
            payMethodLayout.setVisibility(View.VISIBLE);

            if (serviceOrder.getOrderPayType() == ServiceOrder.ORDER_PAY_TYPE_DEPOSIT ||
                    serviceOrder.getOrderPayType() == ServiceOrder.ORDER_PAY_TYPE_INTENT) {
                // 意向金支付和定金支付方式
                if (serviceOrder.getOrderPayType() == ServiceOrder.ORDER_PAY_TYPE_INTENT) {
                    tvPayMethod.setText("意向金支付");
                } else {
                    // 使用定金支付方式
                    tvPayMethod.setText("定金支付");
                }

                if (serviceOrder.getOrderSub()
                        .getMoneyStatus() == ServiceOrderSub.MONEY_STATUS_PAID_INTENT) {
                    // 用户已支付意向金
                    paidIntentMoneyLayout.setVisibility(View.VISIBLE);
                    tvPaidIntentMoney.setText("-" + getString(R.string.label_price,
                            Util.formatDouble2String(serviceOrder.getOrderSub()
                                    .getIntentMoney())));
                    isRefundIntentMoney = true;

                    tvTotalMoneyLabel.setText("后续需支付：");
                    needPay = serviceOrder.getOrderSub()
                            .getActualPrice() - serviceOrder.getOrderSub()
                            .getAidMoney() - serviceOrder.getOrderSub()
                            .getRedPacketMoney() - serviceOrder.getOrderSub()
                            .getPaidMoney();
                    tvTotalMoney.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
                    tvTotalMoney.setText(getString(R.string.label_price,
                            Util.formatDouble2StringPositive(needPay)));
                    // 全款优惠
                    if (serviceOrder.getOrderSub()
                            .getPayAllSavedMoney() > 0) {
                        tvPayAllSavedHint.setVisibility(View.VISIBLE);
                        tvPayAllSavedHint.setText(getString(R.string.label_pay_all_saved_hint,
                                CommonUtil.formatDouble2String(serviceOrder.getOrderSub()
                                        .getPayAllSavedMoney())));
                    } else if (!TextUtils.isEmpty(serviceOrder.getOrderSub()
                            .getWork()
                            .getPayAllGift())) {
                        tvPayAllSavedHint.setVisibility(View.VISIBLE);
                        tvPayAllSavedHint.setText(getString(R.string.label_pay_all_saved_hint2,
                                serviceOrder.getOrderSub()
                                        .getWork()
                                        .getPayAllGift()));
                    } else {
                        tvPayAllSavedHint.setVisibility(View.GONE);
                    }
                } else if (serviceOrder.getOrderSub()
                        .getMoneyStatus() == ServiceOrderSub.MONEY_STATUS_PAID_DEPOSIT) {
                    // 用户已付定金
                    if (serviceOrder.getOrderPayType() == ServiceOrder.ORDER_PAY_TYPE_INTENT &&
                            serviceOrder.getOrderSub()
                            .getIntentMoney() > 0) {
                        // 用户也付过意向金
                        paidIntentMoneyLayout.setVisibility(View.VISIBLE);
                        tvPaidIntentMoney.setText("-" + getString(R.string.label_price,
                                Util.formatDouble2String(serviceOrder.getOrderSub()
                                        .getIntentMoney())));
                        isRefundIntentMoney = true;
                    }
                    // 已付定金
                    paidDepositMoneyLayout.setVisibility(View.VISIBLE);
                    tvPaidDepositMoney.setText("-" + getString(R.string.label_price,
                            Util.formatDouble2String(serviceOrder.getOrderSub()
                                    .getEarnestMoney() - serviceOrder.getOrderSub()
                                    .getIntentMoney())));

                    // 后续需要支付
                    tvTotalMoneyLabel.setText("后续需支付：");
                    needPay = serviceOrder.getOrderSub()
                            .getActualPrice() - serviceOrder.getOrderSub()
                            .getAidMoney() - serviceOrder.getOrderSub()
                            .getRedPacketMoney() - serviceOrder.getOrderSub()
                            .getPaidMoney();
                    tvTotalMoney.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
                    tvTotalMoney.setText(getString(R.string.label_price,
                            Util.formatDouble2StringPositive(needPay)));
                } else {
                    // 本次支付定金或本次意向金支付
                    payNowMoneyLayout.setVisibility(View.VISIBLE);

                    double nextPay;

                    // 显示本次需支付
                    if (serviceOrder.getOrderPayType() == ServiceOrder.ORDER_PAY_TYPE_INTENT) {
                        tvPayMethod.setText("意向金支付");
                        tvNowMoneyLabel.setText("本次意向金支付:");
                        needPay = serviceOrder.getOrderSub()
                                .getIntentMoney();
                        tvPayNowMoney.setText("-" + getString(R.string.label_price,
                                Util.formatDouble2String(needPay)));
                        nextPay = serviceOrder.getOrderSub()
                                .getActualPrice() - serviceOrder.getOrderSub()
                                .getAidMoney() - serviceOrder.getOrderSub()
                                .getRedPacketMoney() - serviceOrder.getOrderSub()
                                .getIntentMoney();

                        // 全款优惠
                        if (serviceOrder.getOrderSub()
                                .getPayAllSavedMoney() > 0) {
                            tvPayAllSavedHint.setVisibility(View.VISIBLE);
                            tvPayAllSavedHint.setText(getString(R.string.label_pay_all_saved_hint,
                                    CommonUtil.formatDouble2String(serviceOrder.getOrderSub()
                                            .getPayAllSavedMoney())));
                        } else if (!TextUtils.isEmpty(serviceOrder.getOrderSub()
                                .getWork()
                                .getPayAllGift())) {
                            tvPayAllSavedHint.setVisibility(View.VISIBLE);
                            tvPayAllSavedHint.setText(getString(R.string.label_pay_all_saved_hint2,
                                    serviceOrder.getOrderSub()
                                            .getWork()
                                            .getPayAllGift()));
                        } else {
                            tvPayAllSavedHint.setVisibility(View.GONE);
                        }
                    } else {
                        // 使用定金支付方式
                        tvPayMethod.setText("定金支付");
                        tvNowMoneyLabel.setText("本次定金支付:");
                        needPay = serviceOrder.getOrderSub()
                                .getEarnestMoney();
                        tvPayNowMoney.setText("-" + getString(R.string.label_price,
                                Util.formatDouble2String(needPay)));
                        nextPay = serviceOrder.getOrderSub()
                                .getActualPrice() - serviceOrder.getOrderSub()
                                .getAidMoney() - serviceOrder.getOrderSub()
                                .getRedPacketMoney() - serviceOrder.getOrderSub()
                                .getEarnestMoney();
                    }
                    tvPayNowMoney.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));

                    // 下次还需支付金额
                    tvTotalMoneyLabel.setText("后续需支付：");
                    tvTotalMoney.setTextColor(ContextCompat.getColor(this, R.color.colorBlack2));
                    tvTotalMoney.setText(getString(R.string.label_price,
                            Util.formatDouble2StringPositive(nextPay)));
                }
            } else {
                // 全款支付方式
                tvPayMethod.setText("全款支付");
                // 全款优惠
                if (serviceOrder.getOrderSub()
                        .getPayAllSavedMoney() > 0) {
                    payAllSavedMoneyLayout.setVisibility(View.VISIBLE);
                    tvPayAllSavedMoney.setText("-" + getString(R.string.label_price,
                            Util.formatDouble2String(serviceOrder.getOrderSub()
                                    .getPayAllSavedMoney())));
                }
                tvTotalMoneyLabel.setText("本次全款支付：");
                // 本次全款支付
                needPay = serviceOrder.getOrderSub()
                        .getActualPrice() - serviceOrder.getOrderSub()
                        .getPayAllSavedMoney() - serviceOrder.getOrderSub()
                        .getAidMoney() - serviceOrder.getOrderSub()
                        .getRedPacketMoney();
                tvTotalMoney.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
                tvTotalMoney.setText(getString(R.string.label_price,
                        Util.formatDouble2StringPositive(needPay)));
            }
        }
        // 已付余款(实付款-意向金付款-定金付款（定金-意向金） -> 实付款-定金)
        // 如果是全款支付方式，不显示余款
        double prePaidMoney;
        if (serviceOrder.getOrderSub()
                .getEarnestMoney() == 0) {
            prePaidMoney = serviceOrder.getOrderSub()
                    .getIntentMoney();
        } else {
            prePaidMoney = serviceOrder.getOrderSub()
                    .getEarnestMoney();
        }
        double paidRestMoney = serviceOrder.getOrderSub()
                .getPaidMoney() - prePaidMoney;
        if (serviceOrder.getOrderPayType() == ServiceOrder.ORDER_PAY_TYPE_PAY_ALL ||
                paidRestMoney <= 0) {
            paidRestMoneyLayout.setVisibility(View.GONE);
        } else {
            paidRestMoneyLayout.setVisibility(View.VISIBLE);

            tvPaidRestMoney.setText("-" + getString(R.string.label_price,
                    Util.formatDouble2String(paidRestMoney)));
        }

        tvBottomMoney.setText(Util.formatDouble2StringPositive(needPay));
    }

    private void setBottomOrderInfoView() {
        tvOrderNum.setText(serviceOrder.getOrderNo());
        tvOrderTime.setText(serviceOrder.getCreatedAt()
                .toString("yyyy-MM-dd HH:mm:ss"));

        DataConfig dataConfig = Session.getInstance()
                .getDataConfig(this);
        if (dataConfig != null && !JSONUtil.isEmpty(dataConfig.getPrepayRemind(serviceOrder
                .getOrderSub()
                .getMerchant()
                .getProperty()
                .getId()))) {
            tvPrepayRemind.setVisibility(View.VISIBLE);
            tvPrepayRemind.setText(dataConfig.getPrepayRemind(serviceOrder.getOrderSub()
                    .getMerchant()
                    .getProperty()
                    .getId()));
        } else {
            tvPrepayRemind.setVisibility(View.GONE);
        }

        if (serviceOrder.getOrderSub()
                .getMoneyStatus() > 0 && !JSONUtil.isEmpty(serviceOrder.getFreeOrderLink())) {
            btnLuckyDraw.setVisibility(View.VISIBLE);
        } else {
            btnLuckyDraw.setVisibility(View.GONE);
        }
    }

    private void setProtocolImages() {
        if (serviceOrder.getOrderSub()
                .getProtocolImages() != null && !serviceOrder.getOrderSub()
                .getProtocolImages()
                .isEmpty()) {
            protocolPicsLayout.setVisibility(View.VISIBLE);
            gridProtocolImages.setVisibility(View.VISIBLE);
            for (int i = 0; i < serviceOrder.getOrderSub()
                    .getProtocolImages()
                    .size(); i++) {
                View view = gridProtocolImages.getChildAt(i);
                Photo photo = serviceOrder.getOrderSub()
                        .getProtocolImages()
                        .get(i);
                if (view == null) {
                    view = View.inflate(this, R.layout.protocol_image_item, null);
                    gridProtocolImages.addView(view, protocolImgViewSize, protocolImgViewSize);
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(ServiceOrderDetailActivity.this,
                                    PicsPageViewActivity.class);
                            intent.putExtra("photos",
                                    serviceOrder.getOrderSub()
                                            .getProtocolImages());
                            intent.putExtra("position", gridProtocolImages.indexOfChild(v));
                            startActivity(intent);
                        }
                    });
                }
                ImageViewHolder holder = (ImageViewHolder) view.getTag();
                if (holder == null) {
                    holder = new ImageViewHolder(view);
                    holder.imgCover.setBackgroundColor(ContextCompat.getColor(this,
                            R.color.colorLine));
                    view.setTag(holder);
                }
                Glide.with(this)
                        .load(ImagePath.buildPath(photo.getImagePath())
                                .width(protocolImgSize)
                                .height(protocolImgSize)
                                .cropPath())
                        .into(holder.imgCover);
            }
        } else {
            protocolPicsLayout.setVisibility(View.GONE);
        }
    }

    private void setBottomAction() {
        bottomLayout.setVisibility(View.VISIBLE);
        tvBottomMoneyLabel.setVisibility(View.VISIBLE);
        tvBottomMoney.setVisibility(View.VISIBLE);
        tvBottomRmb.setVisibility(View.VISIBLE);
        btnDeleteOrder.setVisibility(View.GONE);
        btnDelayConfirm.setVisibility(View.GONE);

        if (serviceOrder.getOrderSub()
                .getStatus() == ServiceOrderSub.STATUS_WAITING_FOR_THE_PAYMENT) {
            // 等待付款，显示去付款
            btnAction.setText("去付款");
            btnAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 虽然订单的order_pay_type与支付接口的pay_type很相似，但两者没有直接关系，不能直接使用
                    switch (serviceOrder.getOrderPayType()) {
                        case ServiceOrder.ORDER_PAY_TYPE_INTENT:
                            // 意向金支付
                            onPay(PayConfig.PAY_TYPE_INTENT);
                            break;
                        case ServiceOrder.ORDER_PAY_TYPE_DEPOSIT:
                            onPay(PayConfig.PAY_TYPE_DEPOSIT);
                            break;
                        case ServiceOrder.ORDER_PAY_TYPE_PAY_ALL:
                            onPay(PayConfig.PAY_TYPE_PAY_ALL);
                            break;
                    }
                }
            });
        } else if ((serviceOrder.getOrderSub()
                .getStatus() == ServiceOrderSub.STATUS_WAITING_FOR_ACCEPT_ORDER || serviceOrder
                .getOrderSub()
                .getStatus() == ServiceOrderSub.STATUS_MERCHANT_ACCEPT_ORDER) && (serviceOrder
                .getOrderSub()
                .getMoneyStatus() == ServiceOrderSub.MONEY_STATUS_PAID_DEPOSIT || serviceOrder
                .getOrderSub()
                .getMoneyStatus() == ServiceOrderSub.MONEY_STATUS_PAID_INTENT)) {
            // 已付款
            // 但未付完，支付余款
            btnAction.setText("去付款");
            btnAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onPayRest();
                }
            });
        } else {
            if (serviceOrder.getOrderSub()
                    .getStatus() == ServiceOrderSub.STATUS_MERCHANT_ACCEPT_ORDER && serviceOrder
                    .getOrderSub()
                    .isFinished()) {
                // 商家已确认服务，等待用户确认服务
                if (serviceOrder.getOrderSub()
                        .getMerchant()
                        .getProperty()
                        .getId() == Merchant.PROPERTY_WEDDING_DRESS_PHOTO) {
                    // 如果是婚纱摄影的话，显示延期确认按钮
                    btnDelayConfirm.setVisibility(View.VISIBLE);
                    btnDelayConfirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onDelayConfirmOrder();
                        }
                    });
                } else {
                    btnDelayConfirm.setVisibility(View.GONE);
                }
                tvBottomMoneyLabel.setVisibility(View.GONE);
                tvBottomMoney.setVisibility(View.GONE);
                tvBottomRmb.setVisibility(View.GONE);
                btnAction.setText("确认已消费");
                btnAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onConfirm();
                    }
                });
            } else if (serviceOrder.getOrderSub()
                    .getStatus() == ServiceOrderSub.STATUS_ORDER_CLOSED || serviceOrder
                    .getOrderSub()
                    .getStatus() == ServiceOrderSub.STATUS_ORDER_AUTO_CLOSED) {
                // 订单关闭，可以删除和重新下单
                tvBottomMoneyLabel.setVisibility(View.GONE);
                tvBottomMoney.setVisibility(View.GONE);
                tvBottomRmb.setVisibility(View.GONE);

                btnDeleteOrder.setVisibility(View.VISIBLE);
                btnDeleteOrder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onDelete();
                    }
                });
                btnAction.setText("重新下单");
                btnAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ServiceOrderDetailActivity.this,
                                WorkActivity.class);
                        intent.putExtra("id",
                                serviceOrder.getOrderSub()
                                        .getWork()
                                        .getId());
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    }
                });
            } else if (serviceOrder.getOrderSub()
                    .getStatus() == ServiceOrderSub.STATUS_SERVICE_COMPLETE) {
                tvBottomMoneyLabel.setVisibility(View.GONE);
                tvBottomMoney.setVisibility(View.GONE);
                tvBottomRmb.setVisibility(View.GONE);

                btnAction.setText("评价");
                btnAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onComment();
                    }
                });
            } else {
                bottomLayout.setVisibility(View.GONE);
            }
        }
    }

    private void onPayRest() {
        if (serviceOrder.getOrderSub()
                .getMoneyStatus() == ServiceOrderSub.MONEY_STATUS_PAID_INTENT) {
            // 如果是已支付意向金，后续支付
            if (serviceOrder.getOrderSub()
                    .getMerchant()
                    .getPropertyId() != Merchant.PROPERTY_WEDDING_DRESS_PHOTO && serviceOrder
                    .getWeddingTime() == null) {
                // 需要向检测婚期
                Toast.makeText(this, R.string.msg_service_order_no_wedding_time, Toast.LENGTH_SHORT)
                        .show();
                return;
            }
            // 进入选择支付方式的支付页面
            Intent intent = new Intent(this, ServiceOrderPaymentActivity.class);
            intent.putExtra("order", serviceOrder);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        } else if (serviceOrder.getOrderSub()
                .getMerchant()
                .getPropertyId() == Merchant.PROPERTY_WEDDING_PLAN) {
            // 婚礼策划使用分笔支付
            Intent intent = new Intent(this, ServiceOrderPayRestActivity.class);
            intent.putExtra("order", serviceOrder);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        } else {
            onPay(PayConfig.PAY_TYPE_REST);
        }
    }

    private void onPay(int payType) {
        if (paySubscriber == null) {
            paySubscriber = new RxBusSubscriber<PayRxEvent>() {
                @Override
                protected void onEvent(PayRxEvent rxEvent) {
                    Intent intent;
                    switch (rxEvent.getType()) {
                        case PAY_SUCCESS:
                            // 支付成功，跳转成功页面
                            intent = new Intent(ServiceOrderDetailActivity.this,
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
                            break;
                        case INSTALLMENT_PAY_SUCCESS:
                            // 分期支付成功
                            intent = new Intent(ServiceOrderDetailActivity.this,
                                    MyBillListActivity.class);
                            intent.putExtra(MyBillListActivity.ARG_IS_BACK_ORDER_LIST, true);
                            intent.putExtra(RouterPath.IntentPath.Customer.MyOrder.ARG_SELECT_TAB,
                                    RouterPath.IntentPath.Customer.MyOrder.Tab.SERVICE_ORDER);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(0, 0);
                            break;
                    }
                }
            };
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("order_id", serviceOrder.getId());
            jsonObject.put("pay_type", payType);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        PayConfig.Builder builder = new PayConfig.Builder(this);
        DataConfig dataConfig = Session.getInstance()
                .getDataConfig(this);
        List<String> payAgents = DataConfig.getServicePayAgents();
        if (!serviceOrder.getOrderSub()
                .getWork()
                .isInstallment() || payType == PayConfig.PAY_TYPE_DEPOSIT || payType == PayConfig
                .PAY_TYPE_INTENT) {
            // 移除分期支付
            payAgents.remove(PayAgent.XIAO_XI_PAY);
        }
        builder.payAgents(dataConfig != null ? dataConfig.getPayTypes() : null, payAgents);
        builder.params(jsonObject)
                .path(Constants.getAbsUrl(Constants.HttpPath.SERVICE_ORDER_PAYMENT))
                .price(needPay > 0 ? needPay : 0)
                .subscriber(paySubscriber)
                .build()
                .pay();
    }

    private void onCancel() {
        cancelSub = HljHttpSubscriber.buildSubscriber(this)
                .setProgressDialog(DialogUtil.createProgressDialog(this))
                .setOnNextListener(new SubscriberOnNextListener<ServiceOrder>() {
                    @Override
                    public void onNext(ServiceOrder order) {
                        // 订单内容修改，更新视图
                        serviceOrder = order;
                        setOrderView();
                    }
                })
                .build();

        cancelDlg = DialogUtil.createDoubleButtonDialog(this,
                "确定要取消订单？",
                "",
                "",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cancelDlg.cancel();
                        ServiceOrderIdBody body = new ServiceOrderIdBody();
                        body.setOrderId(serviceOrder.getId());
                        OrderApi.cancelServiceOrder(body)
                                .subscribe(cancelSub);
                    }
                },
                null);
        cancelDlg.show();
    }

    private void onDelete() {
        deleteSub = HljHttpSubscriber.buildSubscriber(this)
                .setProgressDialog(DialogUtil.createProgressDialog(this))
                .setDataNullable(true)
                .setOnNextListener(new SubscriberOnNextListener() {
                    @Override
                    public void onNext(Object o) {
                        // 删除订单后需要退出详情页面
                        onBackPressed();
                    }
                })
                .build();

        deleteDlg = DialogUtil.createDoubleButtonDialog(this,
                "确定要删除订单？",
                "",
                "",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteDlg.cancel();
                        ServiceOrderIdBody body = new ServiceOrderIdBody();
                        body.setOrderId(serviceOrder.getId());
                        OrderApi.deleteServiceOrder(serviceOrder.getId())
                                .subscribe(deleteSub);
                    }
                },
                null);
        deleteDlg.show();
    }

    private void onConfirm() {
        confirmSub = HljHttpSubscriber.buildSubscriber(this)
                .setProgressDialog(DialogUtil.createProgressDialog(this))
                .setOnNextListener(new SubscriberOnNextListener<ServiceOrder>() {
                    @Override
                    public void onNext(ServiceOrder order) {
                        // 更新视图
                        serviceOrder = order;
                        setOrderView();
                        // 跳转到交易成功
                        Intent intent = new Intent(ServiceOrderDetailActivity.this,
                                AfterConfirmReceiveActivity.class);
                        intent.putExtra("service_order_id",
                                serviceOrder.getOrderSub()
                                        .getId());
                        intent.putExtra("service_order_no",
                                serviceOrder.getOrderSub()
                                        .getOrderNo());
                        intent.putExtra("is_service_order", true);
                        startActivityForResult(intent, Constants.RequestCode.ORDER_CONFIRM);
                        overridePendingTransition(R.anim.slide_in_from_bottom, 0);
                    }
                })
                .build();

        confirmDlg = DialogUtil.createDoubleButtonDialog(this,
                "请在服务完成之后进行确认，确认后商家将收到此订单所有服务费",
                "服务已完成",
                "我点错了",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        confirmDlg.cancel();
                        ServiceOrderIdBody body = new ServiceOrderIdBody();
                        body.setOrderId(serviceOrder.getId());
                        OrderApi.confirmServiceOrder(body)
                                .subscribe(confirmSub);
                    }
                },
                null);
        confirmDlg.show();
    }

    private void onDelayConfirmOrder() {
        delayConfirmSub = HljHttpSubscriber.buildSubscriber(this)
                .setProgressDialog(DialogUtil.createProgressDialog(this))
                .setOnNextListener(new SubscriberOnNextListener<ServiceOrder>() {
                    @Override
                    public void onNext(ServiceOrder order) {
                        // 更新视图
                        Toast.makeText(ServiceOrderDetailActivity.this,
                                R.string.msg_success_to_delay,
                                Toast.LENGTH_SHORT)
                                .show();
                        serviceOrder = order;
                        setOrderView();
                    }
                })
                .build();
        delayDlg = DialogUtil.createDoubleButtonDialog(this,
                "是否延期确认服务？",
                "申请延期可延长自动确认服务时间15天\n" + "申请后7天可再次申请",
                "我要延期",
                "我点错了",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        delayDlg.cancel();
                        ServiceOrderIdBody body = new ServiceOrderIdBody();
                        body.setOrderId(serviceOrder.getId());
                        OrderApi.delayConfirmServiceOrder(body)
                                .subscribe(delayConfirmSub);
                    }
                },
                null);
        delayDlg.show();
    }

    private void onComment() {
        Intent intent = new Intent(this, CommentServiceActivity.class);
        intent.putExtra(CommentServiceActivity.ARG_SUB_ORDER_NO,
                serviceOrder.getOrderSub()
                        .getOrderNo());
        startActivity(intent);
    }

    private void onMerchantChat() {
        Intent intent = new Intent(this, WSCustomerChatActivity.class);

        intent.putExtra("user",
                serviceOrder.getOrderSub()
                        .getMerchant()
                        .toUser());
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    private void onMerchantCall() {
        if (serviceOrder.getOrderSub()
                .getMerchant()
                .getContactPhone() != null && serviceOrder.getOrderSub()
                .getMerchant()
                .getContactPhone()
                .size() > 0) {
            callUp();
        } else {
            // 提示没有商家的联系电话
            Toast.makeText(this, getString(R.string.msg_no_merchant_number), Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void onRefundOrder() {
        Intent intent = new Intent(this, RefundApplyActivity.class);
        intent.putExtra("order_num",
                serviceOrder.getOrderSub()
                        .getOrderNo());
        // 在用户付完意向金且没有付余款的情况下，传入一个参数，说明是直接退还意向金
        intent.putExtra("is_refund_intent_money", isRefundIntentMoney);
        intent.putExtra("is_refund_installment", serviceOrder.isInstallment());
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    public void onCancelRefundOrder() {
        cancelRefundSub = HljHttpSubscriber.buildSubscriber(this)
                .setProgressDialog(DialogUtil.createProgressDialog(this))
                .setOnNextListener(new SubscriberOnNextListener<JsonElement>() {
                    @Override
                    public void onNext(JsonElement jsonElement) {
                        reloadDetail();
                    }
                })
                .build();
        cancelRefundDlg = DialogUtil.createDoubleButtonDialog(this,
                getString(R.string.msg_cancel_refund),
                getString(R.string.label_cancel_refund),
                "我点错了",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cancelRefundDlg.cancel();
                        ServiceOrderNoBody body = new ServiceOrderNoBody();
                        body.setOrderNo(serviceOrder.getOrderSub()
                                .getOrderNo());
                        OrderApi.cancelServiceOrderRefund(body)
                                .subscribe(cancelRefundSub);
                    }
                },
                null);
        cancelRefundDlg.show();
    }

    private void callUp() {
        if (serviceOrder.getOrderSub()
                .getMerchant()
                .getContactPhone()
                .size() == 1) {
            String phone = serviceOrder.getOrderSub()
                    .getMerchant()
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
                    serviceOrder.getOrderSub()
                            .getMerchant()
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

    @OnClick(R.id.work_info_layout)
    void onWorkInfo() {
        // 跳转套餐快照
        Intent intent = new Intent(this, WorkActivity.class);
        intent.putExtra("id",
                serviceOrder.getOrderSub()
                        .getWork()
                        .getId());
        intent.putExtra("isSnapshot", true);
        intent.putExtra("order_id", serviceOrder.getId());
        intent.putExtra("set_meal_id",
                serviceOrder.getOrderSub()
                        .getWork()
                        .getId());
        intent.putExtra("work_id",
                serviceOrder.getOrderSub()
                        .getPrdId());
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    @OnClick(R.id.info_layout)
    void onEditInfo() {
        ServeCustomerInfo customerInfo = ServeCustomerInfoUtil.readServeCustomerInfo(this);
        customerInfo.setCustomerName(serviceOrder.getBuyerName());
        customerInfo.setCustomerPhone(serviceOrder.getBuyerPhone());
        customerInfo.setServeTime(serviceOrder.getWeddingTime() != null ? serviceOrder
                .getWeddingTime()
                .getMillis() : -1);

        Intent intent = new Intent(this, OrderInfoEditActivity.class);
        intent.putExtra("info", customerInfo);
        intent.putExtra("is_need_wedding_time", true);
        startActivityForResult(intent, Constants.RequestCode.SERVICE_ORDER_EDIT_WEDDING_TIME);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    @Override
    public void onOkButtonClick() {
        if (serviceOrder.getOrderSub()
                .getStatus() == ServiceOrderSub.STATUS_WAITING_FOR_THE_PAYMENT) {
            onCancel();
        }
        super.onOkButtonClick();
    }

    @OnClick(R.id.btn_lucky_draw)
    void onLuckyDraw() {
        Intent intent = new Intent(this, HljWebViewActivity.class);
        intent.putExtra("path", serviceOrder.getFreeOrderLink());
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    @Override
    protected void onPause() {
        if (alertTimer != null) {
            alertTimer.cancel();
            alertTimer = null;
        }
        if (confirmAlertTimer != null) {
            confirmAlertTimer.cancel();
            confirmAlertTimer = null;
        }
        super.onPause();
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(detailSub,
                paySubscriber,
                deleteSub,
                cancelRefundSub,
                confirmSub,
                cancelRefundSub,
                delayConfirmSub,
                updateSub);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.RequestCode.SERVICE_ORDER_EDIT_WEDDING_TIME:
                    if (data != null) {
                        ServeCustomerInfo info = data.getParcelableExtra("info");
                        onUpdateCustomerInfo(info);
                        infoUpdated = true;
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void onUpdateCustomerInfo(final ServeCustomerInfo info) {
        if (info != null) {
            updateSub = HljHttpSubscriber.buildSubscriber(this)
                    .setProgressDialog(DialogUtil.createProgressDialog(this))
                    .setOnNextListener(new SubscriberOnNextListener() {
                        @Override
                        public void onNext(Object o) {
                            // 更新当前页面的信息
                            serviceOrder.setWeddingTime(info.getServeTime());
                            serviceOrder.setBuyerName(info.getCustomerName());
                            serviceOrder.setBuyerPhone(info.getCustomerPhone());
                            setCustomerInfo();
                        }
                    })
                    .build();
            OrderApi.updateCustomerInfo(serviceOrder.getId(), info)
                    .subscribe(updateSub);
        }
    }

    /**
     * 根据是否是活动计算全款优惠金额
     *
     * @return
     */
    private int getPayAllSavedMoney() {
        Work work = serviceOrder.getOrderSub()
                .getWork();
        DateTime date = new DateTime();
        WorkRule rule = serviceOrder.getRule();
        if (rule != null && rule.getId() > 0 && ((rule.getEndTime() == null || rule.getEndTime()
                .isAfter(date)) && rule.getStartTime() == null || rule.getStartTime()
                .isBefore(date))) {
            return (int) Math.round(work.getSalePayAllPercent() * work.getSalePrice());
        } else {
            return (int) Math.round(work.getPayAllPercent() * work.getActualPrice());
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        reloadDetail();
    }

    private class AlertTimer extends CountDownTimer {

        int alertHintStrId;

        public AlertTimer(long millisInFuture, long countDownInterval, int stringId) {
            super(millisInFuture, countDownInterval);
            this.alertHintStrId = stringId;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            // 距离结束时间小于15分钟，开始显示倒计时
            // 活动套餐的倒计时总是小于15分钟，所以开始就是倒计时
            if (millisUntilFinished < 1000 * 60 * 15) {
                int minutes = (int) (millisUntilFinished / (1000 * 60));
                int seconds = (int) ((millisUntilFinished - minutes * 1000 * 60) / 1000);
                tvOrderAlert.setText(Html.fromHtml(getString(alertHintStrId, minutes, seconds)));
            }
        }

        @Override
        public void onFinish() {
            reloadDetail();
        }
    }

    public class ConfirmAlertTimer extends CountDownTimer {

        private String hintStr;

        public ConfirmAlertTimer(long millisInFuture, long countDownInterval, String hintStr) {
            super(millisInFuture, countDownInterval);
            this.hintStr = hintStr;
            tvOrderAlert.setText(Html.fromHtml(String.format(hintStr,
                    TimeUtil.getSpecialTimeLiteral(ServiceOrderDetailActivity.this,
                            millisInFuture))));
        }

        @Override
        public void onTick(long millisUntilFinished) {
            tvOrderAlert.setText(Html.fromHtml(String.format(hintStr,
                    TimeUtil.getSpecialTimeLiteral(ServiceOrderDetailActivity.this,
                            millisUntilFinished))));
        }

        @Override
        public void onFinish() {
            reloadDetail();
        }
    }

    static class ImageViewHolder {
        @BindView(R.id.img_cover)
        ImageView imgCover;

        ImageViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
