package com.hunliji.hljpaymentlibrary;

import android.app.Activity;
import android.content.Intent;

import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljpaymentlibrary.models.PayRxEvent;
import com.hunliji.hljpaymentlibrary.views.activities.HljPaymentActivity;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Suncloud on 2016/8/17.
 */
public class PayConfig {

    private static Subscriber<PayRxEvent> eventSubscriber;
    private ArrayList<String> payAgents;
    private JSONObject params;
    private String path;
    private double price;
    private WeakReference<Activity> activityWeakReference;
    private boolean llpaySimple;
    private boolean failToFinish;

    public static final int PAY_TYPE_PAY_ALL = 1;
    public static final int PAY_TYPE_DEPOSIT = 2;
    public static final int PAY_TYPE_REST = 3;
    public static final int PAY_TYPE_INTENT = 5;

    private PayConfig(Builder builder) {
        this.payAgents = new ArrayList<>(builder.payAgents);
        this.params = builder.params;
        this.path = builder.path;
        this.activityWeakReference = new WeakReference<>(builder.activity);
        this.price = builder.price;
        if (eventSubscriber != builder.eventSubscriber) {
            CommonUtil.unSubscribeSubs(eventSubscriber);
            if (builder.eventSubscriber != null) {
                eventSubscriber = builder.eventSubscriber;
                RxBus.getDefault()
                        .toObservable(PayRxEvent.class)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(eventSubscriber);
            }
        }
        this.llpaySimple = builder.llpaySimple;
        this.failToFinish = builder.failToFinish;
    }

    public HljPay pay() {
        if (activityWeakReference.get() == null || activityWeakReference.get()
                .isFinishing() || payAgents.isEmpty()) {
            return null;
        }
        if (!payAgents.contains(PayAgent.JD_PAY)) {
            Intent intent = new Intent(activityWeakReference.get(), HljPaymentActivity.class);
            intent.putExtra("pay_params", params.toString());
            intent.putExtra("pay_path", path);
            intent.putExtra("price", price);
            intent.putExtra("llpaySimple", llpaySimple);
            intent.putExtra("fail_to_finish", failToFinish);
            intent.putStringArrayListExtra("payAgents", payAgents);
            activityWeakReference.get()
                    .startActivity(intent);
            activityWeakReference.get()
                    .overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        } else {
            new HljPay.Builder(activityWeakReference.get()).params(params)
                    .path(path)
                    .price(price)
                    .build()
                    .onPayJD();
        }
        return null;
    }

    public static class Builder {
        private JSONObject params;
        private String path;
        private Activity activity;
        private double price;
        private Subscriber<PayRxEvent> eventSubscriber;
        private ArrayList<String> payAgents; //支付途径
        private boolean llpaySimple; // 无密码连连支付
        private boolean failToFinish;

        public Builder(Activity activity) {
            this.activity = activity;
            this.payAgents = new ArrayList<>();
        }

        public Builder params(JSONObject params) {
            this.params = params;
            return this;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public Builder price(double price) {
            this.price = price;
            return this;
        }

        public Builder subscriber(Subscriber<PayRxEvent> eventSubscriber) {
            this.eventSubscriber = eventSubscriber;
            return this;
        }

        public Builder aliPay() {
            this.payAgents.add(PayAgent.ALI_PAY);
            return this;
        }

        public Builder unionPay() {
            this.payAgents.add(PayAgent.UNION_PAY);
            return this;
        }

        public Builder llPay() {
            this.payAgents.add(PayAgent.LL_PAY);
            return this;
        }

        public Builder jdPay() {
            this.payAgents.add(PayAgent.JD_PAY);
            return this;
        }

        public Builder weixinPay() {
            this.payAgents.add(PayAgent.WEIXIN_PAY);
            return this;
        }

        public Builder walletPay() {
            this.payAgents.add(PayAgent.WALLET_PAY);
            return this;
        }

        /**
         * 招行支付
         */
        public Builder cmbPay() {
            this.payAgents.add(PayAgent.CMB_PAY);
            return this;
        }

        /**
         * 连连支付模式
         *
         * @param isSimple true 简单模式，不绑定银行卡无密码
         */
        public Builder llpayMode(boolean isSimple) {
            this.llpaySimple = isSimple;
            return this;
        }

        /**
         * 处理一般支付列表
         *
         * @param payTypes  服务器支付开关
         * @param payAgents 当前支付允许使用的支付方式
         */
        public Builder payAgents(List<String> payTypes, List<String> payAgents) {
            if (payAgents != null) {
                for (String payAgent : payAgents) {
                    if (payTypes == null || payTypes.isEmpty() || payTypes.contains(payAgent)) {
                        this.payAgents.add(payAgent);
                    }
                }
            }
            return this;
        }


        /**
         * 失败时退出支付页
         * @return
         */
        public Builder failToFinish(boolean failToFinish) {
            this.failToFinish = failToFinish;
            return this;
        }

        public PayConfig build() {
            if (activity == null)
                throw new IllegalStateException("context == null");
            if (path == null)
                throw new IllegalStateException("path == null");
            if (params == null) {
                params = new JSONObject();
            }
            return new PayConfig(this);
        }
    }
}
