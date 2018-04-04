package com.hunliji.hljpaymentlibrary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.alipay.sdk.app.PayTask;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hunliji.hljcommonlibrary.models.Location;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.utils.DeviceUuidFactory;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.LocationSession;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.NetUtil;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljpaymentlibrary.api.PaymentApi;
import com.hunliji.hljpaymentlibrary.models.LLPaySecurePayer;
import com.hunliji.hljpaymentlibrary.models.PayRxEvent;
import com.hunliji.hljpaymentlibrary.models.Result;
import com.hunliji.hljpaymentlibrary.views.activities.CmbPayActivity;
import com.hunliji.hljpaymentlibrary.views.activities.JDPayActivity;
import com.hunliji.hljsharelibrary.HljShare;
import com.hunliji.hljsharelibrary.utils.WXCallbackUtil;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.unionpay.UPPayAssistEx;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

import rx.Subscription;


/**
 * Created by Suncloud on 2016/7/25.
 */
public class HljPay {

    private JSONObject params;
    private String path;
    private WeakReference<Activity> activityWeakReference;
    private JsonObject payResult;
    private double price;
    private Subscription paymentSubscription;

    public static class PayResultCode {
        public static final int ALI_PAY = 1; //支付宝消息回调类型
        public static final int LL_PAY = 2; //连连支付消息回调类型
        public static final int WEIXIN_PAY = 3; //微信支付消息回调类型
        public static final int WALLET_PAY = 4; //钱包支付消息回调类型
    }


    private HljPay(Builder builder) {
        this.params = builder.params;
        this.path = builder.path;
        this.activityWeakReference = new WeakReference<>(builder.activity);
        this.price = builder.price;
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case PayResultCode.ALI_PAY:
                    Result resultObj = new Result((String) msg.obj);
                    String resultStatus = resultObj.resultStatus;
                    switch (resultStatus) {
                        case "9000"://订单支付成功
                        case "8000"://正在处理中
                            onCompleted(payResult);
                            break;
//                        case "4000"://订单支付失败
//                            onFailed();
//                            ToastUtil.showToast(activityWeakReference.get(),
//                                    null,
//                                    R.string.msg_pay_fail___pay);
//                            break;
                        default:
                            onFailed();
                            ToastUtil.showToast(activityWeakReference.get(),
                                    null,
                                    R.string.msg_pay_fail___pay);
                            break;
                    }
                    break;
                case PayResultCode.LL_PAY:
                    try {
                        String retStr = (String) msg.obj;
                        JSONObject contentObj = new JSONObject(retStr);
                        String retCode = contentObj.optString("ret_code");
                        String retMsg = contentObj.optString("ret_msg");
                        String resultPay = contentObj.optString("result_pay");
                        if ("0000".equals(retCode) && "SUCCESS".equals(resultPay)) {
                            ToastUtil.showToast(activityWeakReference.get(),
                                    retMsg,
                                    R.string.msg_pay_success___pay);
                            onCompleted(payResult);
                        } else {
                            // 返回去显示错误信息
                            ToastUtil.showToast(activityWeakReference.get(),
                                    retMsg,
                                    R.string.msg_pay_fail___pay);
//                            if("1006".equals(retCode)) {
//                                //取消
//                            }
                            onFailed();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case PayResultCode.WEIXIN_PAY:
                    String code = (String) msg.obj;
                    if ("0".equals(code)) {
                        ToastUtil.showToast(activityWeakReference.get(),
                                null,
                                R.string.msg_pay_success___pay);
                        onCompleted(payResult);
                    } else {
                        ToastUtil.showToast(activityWeakReference.get(),
                                null,
                                R.string.msg_pay_fail___pay);
//                        if ("-2".equals(code)) {
//                            //取消支付
//                        }
                        onFailed();
                    }
                    break;
                case PayResultCode.WALLET_PAY:
                    if (msg.obj != null && msg.obj instanceof Integer && (Integer) msg.obj == 1) {
                        ToastUtil.showToast(activityWeakReference.get(),
                                null,
                                R.string.msg_pay_success___pay);
                        onCompleted(payResult);
                    } else {
                        ToastUtil.showToast(activityWeakReference.get(),
                                null,
                                R.string.msg_pay_fail___pay);
                        onFailed();
                    }
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    public void onPayAli() {
        onPay(PayAgent.ALI_PAY);
    }

    public void onPayWallet() {
        onPay(PayAgent.WALLET_PAY);
    }

    public void onPayUnion() {
        onPay(PayAgent.UNION_PAY);
    }

    public void onPayWeixin() {
        onPay(PayAgent.WEIXIN_PAY);
    }

    public void onPayCmb() {
        onPay(PayAgent.CMB_PAY);
    }

    public void onPayLLPay() {
        onPay(PayAgent.LL_PAY);
    }

    public void onPayJD() {
        params = getJDDeviceInfo(activityWeakReference.get(), params);
        onPay(PayAgent.JD_PAY);
    }

    public void onPayXiaoxiInstallment(int stageNum) {
        // 小犀分期需要额外的参数
        try {
            params.put("period", stageNum);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        onPay(PayAgent.XIAO_XI_PAY);
    }

    private JSONObject getJDDeviceInfo(Context context, JSONObject jsonObject) {
        if (jsonObject == null) {
            jsonObject = new JSONObject();
        }
        try {
            //渠道信息 3 android
            jsonObject.put("channelInfo", 3);
            Location location = LocationSession.getInstance()
                    .getLocation(context);
            if (location != null) {
                jsonObject.put("longtitude", location.getLongitude());
                jsonObject.put("latitude", location.getLatitude());
            }
            jsonObject.put("netType", NetUtil.getNetType(context));
            jsonObject.put("appType", "android");
            try {
                jsonObject.put("appVersion",
                        context.getPackageManager()
                                .getPackageInfo(context.getPackageName(), 0).versionName);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            jsonObject.put("appOsType", "android");
            jsonObject.put("deviceType", android.os.Build.MODEL);
            jsonObject.put("osVersion", android.os.Build.VERSION.RELEASE);
            DisplayMetrics dm = context.getResources()
                    .getDisplayMetrics();
            jsonObject.put("resolution", dm.widthPixels + "*" + dm.heightPixels);
            jsonObject.put("intranetIp", NetUtil.getLocalIPAddress());
            jsonObject.put("guid",
                    DeviceUuidFactory.getInstance()
                            .getDeviceUuidString(context));
            jsonObject.put("macAddress", NetUtil.getMacAddr(context));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    @SuppressWarnings("unchecked")
    private void onPay(String payAgent) {
        try {
            params.put("agent", payAgent);
            params.put("kind", 2);//支付途径Android
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (paymentSubscription != null && !paymentSubscription.isUnsubscribed()) {
            paymentSubscription.unsubscribe();
        }
        paymentSubscription = PaymentApi.getPayParams(path, params)
                .subscribe(HljHttpSubscriber.
                        buildSubscriber(activityWeakReference.get())
                        .setProgressDialog(DialogUtil.createProgressDialog(activityWeakReference
                                .get()))
                        .setOnNextListener(subscriberOnNextListener)
                        .setDataNullable(true)
                        .build());
    }

    private SubscriberOnNextListener<HljHttpResult<JsonElement>> subscriberOnNextListener = new
            SubscriberOnNextListener<HljHttpResult<JsonElement>>() {
        @Override
        public void onNext(HljHttpResult<JsonElement> result) {
            switch (result.getStatus()
                    .getRetCode()) {
                case 3024:
                    // 分期支付的结果特殊处理
                    // 显示额度 == n * 真实额度(n >= 1)
                    // 3024, 51返回的授信额度不足，意味着： 真实额度 < amount < 显示额度
                    RxBus.getDefault()
                            .post(new PayRxEvent(PayRxEvent.RxEventType.CREDIT_NOT_ENOUGH,
                                    params.toString()));
                    break;
                case 900001:
                    // 分期支付的结果特殊处理
                    // 900001, 婚礼纪服务器返回的额度不足，意味着： amount > 显示额度
                    RxBus.getDefault()
                            .post(new PayRxEvent(PayRxEvent.RxEventType.CREDIT_NOT_ENOUGH2,
                                    params.toString()));
                    break;
                case 3023:
                    // 风控校验未通过
                    RxBus.getDefault()
                            .post(new PayRxEvent(PayRxEvent.RxEventType.AUTHORIZE_NOT_PASSED,
                                    params.toString()));
                    break;
                default:
                    // 0，常规正确的返回
                    onPayResult(result);
                    break;
            }

        }
    };

    private void onPayResult(final HljHttpResult<JsonElement> result) {
        JsonElement jsonElement = result.getData();
        if (jsonElement != null) {
            payResult = jsonElement.getAsJsonObject();
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            RxBus.getDefault()
                    .post(new RxEvent(RxEvent.RxEventType.ORDER_BIND, null));
            double fee = jsonObject.get("fee")
                    .getAsDouble();
            if (fee <= 0) {
                onCompleted(jsonObject);
                // 零元支付
                return;
            }
            String payAgent = jsonObject.get("pay_agent")
                    .getAsString();
            if (PayAgent.WALLET_PAY.equals(payAgent)) {
                Message msg = new Message();
                msg.what = PayResultCode.WALLET_PAY;
                if (jsonObject.has("pay_success")) {
                    try {
                        msg.obj = jsonObject.get("pay_success")
                                .getAsInt();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                mHandler.sendMessage(msg);
                return;
            }
            final String orderInfo = jsonObject.get("pay_params")
                    .getAsString();
            if (!TextUtils.isEmpty(orderInfo)) {
                switch (payAgent) {
                    case PayAgent.ALI_PAY: // 支付宝支付
                        new Thread() {
                            public void run() {
                                PayTask alipay = new PayTask(activityWeakReference.
                                        get());
                                String result = alipay.pay(orderInfo);
                                Message msg = new Message();
                                msg.what = PayResultCode.ALI_PAY;
                                msg.obj = result;
                                mHandler.sendMessage(msg);
                            }
                        }.start();
                        return;
                    case PayAgent.UNION_PAY:    // 银联支付
                        JSONObject json = null;
                        try {
                            json = new JSONObject(orderInfo);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (json != null && !TextUtils.isEmpty(json.optString("tn"))) {
                            UPPayAssistEx.startPay(activityWeakReference.get(),
                                    null,
                                    null,
                                    json.optString("tn"),
                                    "00");
                            return;
                        }
                        break;
                    case PayAgent.LL_PAY:
                        new LLPaySecurePayer().pay(orderInfo,
                                mHandler,
                                PayResultCode.LL_PAY,
                                activityWeakReference.get(),
                                false);
                        return;
                    case PayAgent.WEIXIN_PAY:
                        json = null;
                        try {
                            json = new JSONObject(orderInfo);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (json != null) {
                            PayReq req = new PayReq();
                            req.appId = json.optString("appid");
                            req.partnerId = json.optString("partnerid");
                            req.prepayId = json.optString("prepayid");
                            req.nonceStr = json.optString("noncestr");
                            req.timeStamp = json.optString("timestamp");
                            req.packageValue = json.optString("package");
                            req.sign = json.optString("sign");
                            req.extData = "app data"; // optional
                            IWXAPI api = WXAPIFactory.createWXAPI(activityWeakReference.get(),
                                    HljShare.WEIXINKEY,
                                    true);
                            api.registerApp(HljShare.WEIXINKEY);
                            api.sendReq(req);
                            WXCallbackUtil.getInstance()
                                    .registerPayCallback(new WXCallbackUtil
                                            .WXOnCompleteCallbackListener() {
                                        @Override
                                        public void OnComplete(String code) {
                                            Message msg = new Message();
                                            msg.what = PayResultCode.WEIXIN_PAY;
                                            msg.obj = code;
                                            mHandler.sendMessage(msg);
                                        }

                                        @Override
                                        public void OnError() {

                                        }

                                        @Override
                                        public void OnCancel() {

                                        }
                                    });

                        }
                        return;
                    case PayAgent.JD_PAY:// 京东白条
                        json = null;
                        try {
                            json = new JSONObject(orderInfo);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (json != null && !TextUtils.isEmpty(json.optString("gateway"))) {
                            Intent intent = new Intent(activityWeakReference.get(),
                                    JDPayActivity.class);
                            intent.putExtra("orderInfo", orderInfo);
                            intent.putExtra("payResult", payResult.toString());
                            intent.putExtra("gateway", json.optString("gateway"));
                            activityWeakReference.get()
                                    .startActivity(intent);
                            return;
                        }
                        break;
                    case PayAgent.CMB_PAY:
                        String queryOrderParams = null;
                        try {
                            queryOrderParams = jsonObject.getAsJsonObject("query_order_params")
                                    .toString();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Intent intent = new Intent(activityWeakReference.get(),
                                CmbPayActivity.class);
                        intent.putExtra("url", orderInfo);
                        intent.putExtra("queryOrderParams", queryOrderParams);
                        intent.putExtra("payResult", payResult.toString());
                        activityWeakReference.get()
                                .startActivity(intent);
                        break;
                    case PayAgent.XIAO_XI_PAY:
                        RxBus.getDefault()
                                .post(new PayRxEvent(PayRxEvent.RxEventType.ORDER_SUBMIT_SUCCESS,
                                        payResult));
                        break;
                }

            }
        }
    }

    public void onPayActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null && data.getExtras() != null) {
            // 银联支付返回的信息判断
            String str = data.getExtras()
                    .getString("pay_result");
            if (!TextUtils.isEmpty(str)) {
                switch (str) {
                    case "success":
                        onCompleted(payResult);
                        break;
                    case "fail":
                        onFailed();
                        ToastUtil.showToast(activityWeakReference.get(),
                                null,
                                R.string.msg_pay_fail___pay);
                        break;
                    case "cancel":
                        onFailed();
                        ToastUtil.showToast(activityWeakReference.get(),
                                null,
                                R.string.msg_pay_cancel___pay);
                        break;
                }
            }
        }
    }

    private void onCompleted(JsonObject payResult) {
        RxBus.getDefault()
                .post(new PayRxEvent(PayRxEvent.RxEventType.PAY_SUCCESS, payResult));
    }

    private void onFailed() {
        RxBus.getDefault()
                .post(new PayRxEvent(PayRxEvent.RxEventType.PAY_FAIL, null));
    }

    public static class Builder {
        private JSONObject params;
        private String path;
        private Activity activity;
        private double price;

        public Builder(Activity activity) {
            this.activity = activity;
        }

        public Builder params(String paramsStr) {
            try {
                this.params = new JSONObject(paramsStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return this;
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

        public HljPay build() {
            if (activity == null)
                throw new IllegalStateException("context == null");
            if (params == null)
                throw new IllegalStateException("params == null");
            if (path == null)
                throw new IllegalStateException("path == null");
            return new HljPay(this);
        }
    }
}
