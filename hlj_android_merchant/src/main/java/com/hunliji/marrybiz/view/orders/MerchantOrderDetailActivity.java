package com.hunliji.marrybiz.view.orders;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.suncloud.hljweblibrary.HljWeb;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.NoUnderlineSpan;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljpaymentlibrary.PayConfig;
import com.hunliji.hljpaymentlibrary.models.PayRxEvent;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.api.order.OrderApi;
import com.hunliji.marrybiz.model.DataConfig;
import com.hunliji.marrybiz.model.orders.MerchantOrder;
import com.hunliji.marrybiz.model.orders.MerchantOrderSub;
import com.hunliji.marrybiz.util.Session;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by mo_yu on 2017/12/11.商家支付订单详情(cpm,店铺，转化等)
 */

public class MerchantOrderDetailActivity extends HljBaseActivity implements PullToRefreshBase
        .OnRefreshListener {
    private static final double PAY_PRICE_OVER = 5000;

    public static final int REQUEST_MULTI_PAY = 1;
    public static final String ARG_ORDER_ID = "order_id";
    @BindView(R.id.tv_alert_msg)
    TextView tvAlertMsg;
    @BindView(R.id.alert_layout)
    LinearLayout alertLayout;
    @BindView(R.id.tv_copy_pay_order_no)
    TextView tvCopyPayOrderNo;
    @BindView(R.id.tv_order_channel)
    TextView tvOrderChannel;
    @BindView(R.id.tv_create_time)
    TextView tvCreateTime;
    @BindView(R.id.tv_original_price)
    TextView tvOriginalPrice;
    @BindView(R.id.tv_order_amount)
    TextView tvOrderAmount;
    @BindView(R.id.tv_order_state)
    TextView tvOrderState;
    @BindView(R.id.product_layout)
    LinearLayout productLayout;
    @BindView(R.id.cb_agreement)
    CheckBox cbAgreement;
    @BindView(R.id.tv_agreement)
    TextView tvAgreement;
    @BindView(R.id.agreement_layout)
    LinearLayout agreementLayout;
    @BindView(R.id.action_pay_now)
    TextView actionPayNow;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.tv_pay_time)
    TextView tvPayTime;
    @BindView(R.id.pay_time_layout)
    RelativeLayout payTimeLayout;
    @BindView(R.id.scroll_view)
    PullToRefreshScrollView scrollView;
    @BindView(R.id.tv_paid_amount)
    TextView tvPaidAmount;
    @BindView(R.id.paid_amount_layout)
    RelativeLayout paidAmountLayout;
    @BindView(R.id.tv_remaining_amount)
    TextView tvRemainingAmount;
    @BindView(R.id.remaining_amount_layout)
    RelativeLayout remainingAmountLayout;

    private long id;
    private MerchantOrder merchantOrder;
    private double originalPrice;//原价
    private ExpireTimeCountDown countDownTimer;
    private HljHttpSubscriber refreshSubscriber;
    private Subscriber<PayRxEvent> paySub;
    private Subscription rxSubscription;
    private double payPrice;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_order_detail);
        ButterKnife.bind(this);
        initValue();
        initView();
        onRefresh(scrollView);
        initRxSub();
    }

    private void initValue() {
        id = getIntent().getLongExtra(ARG_ORDER_ID, 0);
    }

    private void initView() {
        scrollView.setOnRefreshListener(this);
    }

    private void initRxSub() {
        if (!CommonUtil.isUnsubscribed(rxSubscription)) {
            return;
        }
        rxSubscription = RxBus.getDefault()
                .toObservable(RxEvent.class)
                .filter(new Func1<RxEvent, Boolean>() {
                    @Override
                    public Boolean call(RxEvent rxEvent) {
                        return rxEvent.getType() == RxEvent.RxEventType.MERCHANT_ORDER_PAID;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<RxEvent>() {
                    @Override
                    public void call(RxEvent rxEvent) {
                        finish();
                    }
                });
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        if (refreshSubscriber == null || refreshSubscriber.isUnsubscribed()) {
            refreshSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<MerchantOrder>() {
                        @Override
                        public void onNext(MerchantOrder merchantOrder) {
                            setOrderDetailInfo(merchantOrder);
                        }
                    })
                    .setPullToRefreshBase(scrollView)
                    .setProgressBar(scrollView.isRefreshing() ? null : progressBar)
                    .setContentView(scrollView)
                    .build();
            OrderApi.getMerchantOrderDetailObb(id)
                    .subscribe(refreshSubscriber);
        }
    }

    /**
     * 订单详情信息
     */
    private void setOrderDetailInfo(MerchantOrder merchantOrder) {
        if (merchantOrder.getStatus() == MerchantOrder.ORDER_REFUSE || merchantOrder.getStatus()
                == MerchantOrder.ORDER_IN_REVIEW) {
            showOrderDialog(merchantOrder.getStatus());
            return;
        }
        scrollView.setVisibility(View.VISIBLE);
        scrollView.getRefreshableView()
                .setVisibility(View.VISIBLE);
        this.merchantOrder = merchantOrder;
        setOrderNoAndCopy(merchantOrder.getTradeNo());
        String channelStr;
        if (merchantOrder.getChannel() == MerchantOrder.ONLINE_CHANNEL) {
            channelStr = "线上下单";
        } else {
            channelStr = "线下创建" + " | " + "客户经理：" + merchantOrder.getBdName();
        }
        tvOrderChannel.setText(channelStr);
        if (merchantOrder.getCreateAt() != null) {
            tvCreateTime.setText(merchantOrder.getCreateAt()
                    .toString(HljTimeUtils.DATE_FORMAT_LONG_SIMPLE));
        }
        tvOrderAmount.setText(getString(R.string.label_price,
                CommonUtil.formatDouble2StringWithTwoFloat(merchantOrder.getActualMoney())));
        switch (merchantOrder.getStatus()) {
            case MerchantOrder.ORDER_WAIT_FOR_PAY:
                DateTime endTime = merchantOrder.getExpireTime();
                if (endTime != null && merchantOrder.getPaidMoney() <= 0) {
                    DateTime now = new DateTime();
                    long delTime = endTime.getMillis() - now.getMillis();
                    if (delTime > 0) {
                        alertLayout.setVisibility(View.VISIBLE);
                        String endTimeStr = endTime.toString(HljTimeUtils.DATE_FORMAT_LONG_SIMPLE);
                        tvAlertMsg.setText(getString(R.string.format_order_close_alert,
                                endTimeStr));
                        countDownTimer = new ExpireTimeCountDown(delTime, 1000);
                        countDownTimer.start();
                    } else {
                        alertLayout.setVisibility(View.GONE);
                    }
                }
                tvOrderState.setText("待支付");
                tvOrderState.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
                agreementLayout.setVisibility(View.VISIBLE);
                actionPayNow.setVisibility(View.VISIBLE);
                break;
            case MerchantOrder.ORDER_HAVE_PAID:
                alertLayout.setVisibility(View.GONE);
                tvOrderState.setText("已支付");
                tvOrderState.setTextColor(Color.parseColor("#02ca5b"));
                agreementLayout.setVisibility(View.GONE);
                actionPayNow.setVisibility(View.GONE);
                break;
            case MerchantOrder.ORDER_PAY_CLOSED:
                alertLayout.setVisibility(View.GONE);
                tvOrderState.setText("已关闭");
                tvOrderState.setTextColor(ContextCompat.getColor(this, R.color.colorGray));
                agreementLayout.setVisibility(View.GONE);
                actionPayNow.setVisibility(View.GONE);
                break;
        }
        DateTime paidAt = merchantOrder.getPayTime();//支付时间
        if (paidAt != null) {
            payTimeLayout.setVisibility(View.VISIBLE);
            tvPayTime.setText(paidAt.toString(HljTimeUtils.DATE_FORMAT_LONG_SIMPLE));
        } else {
            payTimeLayout.setVisibility(View.GONE);
        }
        if (merchantOrder.getStatus() == MerchantOrder.ORDER_WAIT_FOR_PAY && merchantOrder
                .getPaidMoney() > 0) {
            agreementLayout.setVisibility(View.GONE);
            paidAmountLayout.setVisibility(View.VISIBLE);
            remainingAmountLayout.setVisibility(View.VISIBLE);
            tvPaidAmount.setText(getString(R.string.label_price,
                    CommonUtil.formatDouble2StringWithTwoFloat(merchantOrder.getPaidMoney())));
            tvRemainingAmount.setText(getString(R.string.label_price,
                    CommonUtil.formatDouble2StringWithTwoFloat(merchantOrder.getActualMoney() -
                            merchantOrder.getPaidMoney())));
        } else {
            paidAmountLayout.setVisibility(View.GONE);
            remainingAmountLayout.setVisibility(View.GONE);
        }
        cbAgreement.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    actionPayNow.setEnabled(true);
                } else {
                    actionPayNow.setEnabled(false);
                }
            }
        });
        setOrderSubList(merchantOrder.getMerchantOrderSubs());
    }

    /**
     * 订单号与复制
     */
    private void setOrderNoAndCopy(final String orderNo) {
        String orderNoCopyStr = orderNo + " | 复制";
        int copyStart = orderNoCopyStr.indexOf("复制");
        SpannableString sp = new SpannableString(orderNoCopyStr);
        sp.setSpan(new NoUnderlineSpan() {
            @Override
            public void onClick(View widget) {
                ClipboardManager cmb = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                assert cmb != null;
                ToastUtil.showToast(MerchantOrderDetailActivity.this, "复制成功", 0);
                cmb.setPrimaryClip(ClipData.newPlainText(getString(R.string.app_name), orderNo));
            }
        }, copyStart, copyStart + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvCopyPayOrderNo.setText(sp);
        tvCopyPayOrderNo.setLinkTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        tvCopyPayOrderNo.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * 设置购买的产品列表
     */
    private void setOrderSubList(List<MerchantOrderSub> merchantOrderSubs) {
        productLayout.removeAllViews();
        originalPrice = 0;
        SpannableStringBuilder serviceSpanStr = new SpannableStringBuilder();
        for (int i = 0; i < merchantOrderSubs.size(); i++) {
            final MerchantOrderSub merchantOrderSub = merchantOrderSubs.get(i);
            View view = View.inflate(this, R.layout.merchant_order_sub_item, null);
            ProductItemViewHolder productItemViewHolder = new ProductItemViewHolder(view);
            productItemViewHolder.setViewData(this, merchantOrderSub, i);
            originalPrice += merchantOrderSub.getOriginalMoney();
            String protocolTitle = merchantOrderSub.getProtocolTitle();
            if (!CommonUtil.isEmpty(protocolTitle)) {
                SpannableString sp = new SpannableString(protocolTitle);
                sp.setSpan(new NoUnderlineSpan() {
                    @Override
                    public void onClick(View widget) {
                        HljWeb.startHtmlWebView(MerchantOrderDetailActivity.this,
                                merchantOrderSub.getProtocolContent());
                    }
                }, 0, protocolTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                serviceSpanStr.append(sp)
                        .append(" ");
            }
            productLayout.addView(view);
        }
        tvOriginalPrice.setText(getString(R.string.label_price,
                CommonUtil.formatDouble2StringWithTwoFloat(originalPrice)));
        if (serviceSpanStr.length() == 0) {
            agreementLayout.setVisibility(View.GONE);
        } else {
            serviceSpanStr.insert(0, getString(R.string.label_merchant_agreement));
            tvAgreement.setText(serviceSpanStr);
            tvAgreement.setLinkTextColor(ContextCompat.getColor(this, R.color.colorLink));
            tvAgreement.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    /**
     * 审核中的订单不能查看
     */
    private void showOrderDialog(int status) {
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        if (dialog == null) {
            dialog = DialogUtil.createSingleButtonDialog(this,
                    getString(status == MerchantOrder.ORDER_IN_REVIEW ? R.string
                            .label_merchant_order_in_review : R.string.label_merchant_order_refuse),
                    "知道了",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            onBackPressed();
                        }
                    });
            dialog.setCancelable(false);
        }
        dialog.show();
    }

    @OnClick(R.id.action_pay_now)
    public void onActionPayNowClicked() {
        if (merchantOrder == null) {
            return;
        }
        double price = merchantOrder.getActualMoney();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("order_id", merchantOrder.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (merchantOrder.getPaidMoney() > 0) {
            price -= merchantOrder.getPaidMoney();
            try {
                jsonObject.put("input_money", price);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (CommonUtil.isUnsubscribed(paySub)) {
            paySub = initSubscriber();
        }
        ArrayList<String> payTypes = null;
        if (Session.getInstance()
                .getDataConfig(this) != null) {
            payTypes = Session.getInstance()
                    .getDataConfig(this)
                    .getPayTypes();
        }
        payPrice = price;
        new PayConfig.Builder(this).params(jsonObject)
                .path(Constants.HttpPath.MERCHANT_ORDER_PAY)
                .price(price)
                .subscriber(paySub)
                .llpayMode(true)
                .failToFinish(payPrice > PAY_PRICE_OVER)
                .payAgents(payTypes, DataConfig.getPayAgents())
                .build()
                .pay();
    }

    //支付回调事件
    private Subscriber<PayRxEvent> initSubscriber() {
        return new RxBusSubscriber<PayRxEvent>() {
            @Override
            protected void onEvent(PayRxEvent rxEvent) {
                switch (rxEvent.getType()) {
                    case PAY_SUCCESS:
                        RxBus.getDefault()
                                .post(new RxEvent(RxEvent.RxEventType.MERCHANT_ORDER_PAID,
                                        merchantOrder));
                        break;
                    case PAY_FAIL:
                        //支付失败
                        if (payPrice <= PAY_PRICE_OVER) {
                            return;
                        }
                        DialogUtil.createSingleButtonDialog(MerchantOrderDetailActivity.this,
                                "支付金额超过每日支付限额时，\n可进行分笔支付",
                                "分笔支付",
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(MerchantOrderDetailActivity.this,
                                                MerchantOrderMultiPayActivity.class);
                                        intent.putExtra(MerchantOrderMultiPayActivity.ARG_ORDER,
                                                merchantOrder);
                                        startActivityForResult(intent, REQUEST_MULTI_PAY);
                                    }
                                })
                                .show();
                        break;
                }
            }
        };
    }

    class ProductItemViewHolder {
        @BindView(R.id.tv_product_name_number)
        TextView tvProductNameNumber;
        @BindView(R.id.tv_product_name)
        TextView tvProductName;
        @BindView(R.id.tv_service_name)
        TextView tvServiceName;
        @BindView(R.id.tv_service_info)
        TextView tvServiceInfo;
        @BindView(R.id.service_info_layout)
        RelativeLayout serviceInfoLayout;
        @BindView(R.id.tv_show_city)
        TextView tvShowCity;
        @BindView(R.id.show_city_layout)
        RelativeLayout showCityLayout;
        @BindView(R.id.tv_product_instructions)
        TextView tvProductInstructions;
        @BindView(R.id.product_instructions_layout)
        RelativeLayout productInstructionsLayout;
        @BindView(R.id.tv_original_price)
        TextView tvOriginalPrice;
        @BindView(R.id.action_check_agreement)
        TextView actionCheckAgreement;

        ProductItemViewHolder(View view) {ButterKnife.bind(this, view);}

        private void setViewData(
                Context mContext, final MerchantOrderSub merchantOrderSub, int position) {
            tvProductNameNumber.setText(mContext.getString(R.string.format_bd_product_name,
                    String.valueOf(position + 1)));
            tvProductName.setText(merchantOrderSub.getProduct()
                    .getTitle());
            if (!CommonUtil.isEmpty(merchantOrderSub.getDesc())) {
                productInstructionsLayout.setVisibility(View.VISIBLE);
                tvProductInstructions.setText(merchantOrderSub.getDesc());
            } else {
                productInstructionsLayout.setVisibility(View.GONE);
            }
            tvOriginalPrice.setText(mContext.getString(R.string.label_price,
                    CommonUtil.formatDouble2StringWithTwoFloat(merchantOrderSub.getOriginalMoney
                            ())));
            if (!CommonUtil.isEmpty(merchantOrderSub.getProtocolContent()) && merchantOrder
                    .getStatus() == MerchantOrder.ORDER_HAVE_PAID) {
                actionCheckAgreement.setText(mContext.getString(R.string.label_check_agreement));
                actionCheckAgreement.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HljWeb.startHtmlWebView(MerchantOrderDetailActivity.this,
                                merchantOrderSub.getProtocolContent());
                    }
                });
                actionCheckAgreement.setVisibility(View.VISIBLE);
            } else {
                actionCheckAgreement.setVisibility(View.GONE);
            }
            if (!CommonUtil.isEmpty(merchantOrderSub.getCity())) {
                showCityLayout.setVisibility(View.VISIBLE);
                tvShowCity.setText(merchantOrderSub.getCity());
            } else {
                showCityLayout.setVisibility(View.GONE);
            }
            String serviceName = null;
            String serviceInfo = null;
            switch (merchantOrderSub.getNumType()) {
                case MerchantOrderSub.TIME_TYPE:
                    serviceInfo = merchantOrderSub.getNum() + "个月";
                    serviceName = mContext.getString(R.string.label_service_period);
                    break;
                case MerchantOrderSub.AMOUNT_TYPE:
                    serviceInfo = merchantOrderSub.getNum() + "元";
                    serviceName = mContext.getString(R.string.label_service_amount);
                    break;
                case MerchantOrderSub.NUMBER_TYPE:
                    serviceInfo = merchantOrderSub.getNum() + "次";
                    serviceName = mContext.getString(R.string.label_service_count);
                    break;
                case MerchantOrderSub.DATE_TYPE:
                    serviceInfo = merchantOrderSub.getNum();
                    serviceName = mContext.getString(R.string.label_service_period);
                    break;
            }
            if (!CommonUtil.isEmpty(serviceInfo)) {
                serviceInfoLayout.setVisibility(View.VISIBLE);
                tvServiceInfo.setText(serviceInfo);
                tvServiceName.setText(serviceName);
            } else {
                serviceInfoLayout.setVisibility(View.GONE);
            }
        }
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
                tvAlertMsg.setText(Html.fromHtml(getString(R.string
                                .label_order_expired_count_down_time,
                        minutes,
                        seconds)));
            }

        }

        @Override
        public void onFinish() {
            // 刷新订单列表
            onRefresh(null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(refreshSubscriber, paySub, rxSubscription);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_MULTI_PAY:
                    onRefresh(null);
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
