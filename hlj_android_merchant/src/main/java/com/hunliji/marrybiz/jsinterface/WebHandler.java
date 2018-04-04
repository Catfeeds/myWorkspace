package com.hunliji.marrybiz.jsinterface;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.example.suncloud.hljweblibrary.jsinterface.BaseWebHandler;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljpaymentlibrary.PayConfig;
import com.hunliji.hljpaymentlibrary.models.PayRxEvent;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.City;
import com.hunliji.marrybiz.model.DataConfig;
import com.hunliji.marrybiz.model.MerchantUser;
import com.hunliji.marrybiz.util.BannerUtil;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.Session;
import com.hunliji.marrybiz.util.popuptip.PopupRule;
import com.hunliji.marrybiz.view.BondPayActivity;
import com.hunliji.marrybiz.view.ViolateListActivity;
import com.hunliji.marrybiz.view.login.OpenShopScheduleActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rx.Subscriber;

/**
 * Created by Suncloud on 2015/12/14.
 */
public class WebHandler extends BaseWebHandler {
    private MerchantUser user;
    private Subscriber<PayRxEvent> paySubscriber;
    private WebView webView;

    public WebHandler(
            Context context, String path, WebView webView, Handler handler) {
        super(context, path, webView, handler);
        this.webView = webView;
        user = Session.getInstance()
                .getCurrentUser(context);
    }

    public boolean isCanBack() {
        return canBack;
    }

    @JavascriptInterface
    public void pay_bond_sign_money() {
        Intent intent = new Intent(context, BondPayActivity.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.slide_in_right,
                R.anim.activity_anim_default);
    }

    @JavascriptInterface
    public void join_online_sale() {
        Intent intent = new Intent(context, OpenShopScheduleActivity.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.slide_in_right,
                R.anim.activity_anim_default);
    }

    @JavascriptInterface
    public void pay_flagship(String money) throws NumberFormatException, JSONException {
        if (TextUtils.isEmpty(money)) {
            return;
        }
        Activity activity = (Activity) context;
        if (PopupRule.getDefault()
                .showShopReview(activity, user)) {
            return;
        }
        CommonUtil.unSubscribeSubs(paySubscriber);
        if (paySubscriber == null) {
            paySubscriber = initSubscriber();
        }
        DataConfig dataConfig = Session.getInstance()
                .getDataConfig(context);
        ArrayList<String> payTypes = new ArrayList<>();
        if (dataConfig != null && dataConfig.getPayTypes() != null) {
            payTypes.addAll(dataConfig.getPayTypes());
        }
        new PayConfig.Builder(activity).params(new JSONObject())
                .path(Constants.HttpPath.PRO_PAY_URL)
                .price(Double.valueOf(money))
                .subscriber(paySubscriber)
                .llpayMode(true)
                .payAgents(payTypes, DataConfig.getPayAgents())
                .build()
                .pay();
    }

    //支付成功
    private Subscriber<PayRxEvent> initSubscriber() {
        return new RxBusSubscriber<PayRxEvent>() {
            @Override
            protected void onEvent(PayRxEvent rxEvent) {
                switch (rxEvent.getType()) {
                    case PAY_SUCCESS:
                        ToastUtil.showCustomToast(context, R.string.label_pay_success);
                        if (webView != null) {
                            webView.reload();
                        }
                        break;
                }
            }
        };
    }

    //立即购买
    //    @JavascriptInterface
    //    public void shop_pro_buy() {
    //        if (user.getExamine() != 1 || user.getCertifyStatus() != 3) {
    //            showHintDialog(context.getString(R.string.hint_merchant_pro2));
    //            return;
    //        }
    //        Intent intent = new Intent(context, MerchantProPayActivity.class);
    //        context.startActivity(intent);
    //        ((Activity) context).overridePendingTransition(R.anim.slide_in_right,
    //                R.anim.activity_anim_default);
    //    }

    //购买记录
    //    @JavascriptInterface
    //    public void shop_pro_record() {
    //        Intent intent = new Intent(context, MerchantProHistoryActivity.class);
    //        context.startActivity(intent);
    //        ((Activity) context).overridePendingTransition(R.anim.slide_in_right,
    //                R.anim.activity_anim_default);
    //    }

    @JavascriptInterface
    public void violate_list() {
        Intent intent = new Intent(context, ViolateListActivity.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.slide_in_right,
                R.anim.activity_anim_default);
    }


    @JavascriptInterface
    public void bannerAction(String bannerStr) {
        if (JSONUtil.isEmpty(bannerStr)) {
            return;
        }
        ArrayList<String> strings = stringSplit(bannerStr);
        int property = 0;
        long forwardId = 0;
        String url = null;
        if (strings.size() > 0) {
            String string = strings.get(0);
            if (!JSONUtil.isEmpty(string)) {
                try {
                    property = Integer.valueOf(string);
                } catch (NumberFormatException ignored) {

                }
            }
        }
        if (strings.size() > 1) {
            String string = strings.get(1);
            if (!JSONUtil.isEmpty(string)) {
                try {
                    forwardId = Long.valueOf(string);
                } catch (NumberFormatException ignored) {

                }
            }
        }
        if (strings.size() > 2) {
            url = strings.get(2);
        }
        City c = null;
        if (strings.size() > 3) {
            String string = strings.get(3);
            if (!JSONUtil.isEmpty(string)) {
                try {
                    long cityId = Long.valueOf(string);
                    c = new City(new JSONObject());
                    c.setCid(cityId);
                    c.setName(url);
                } catch (NumberFormatException ignored) {

                }
            }
        }
        BannerUtil.bannerAction(context, property, forwardId, url);

    }

    private ArrayList<String> stringSplit(String string) {
        ArrayList<String> strings = new ArrayList<>();
        Pattern facePattern = Pattern.compile("[a-zA-z]+://[^:]+|[^:]+");
        Matcher matcher = facePattern.matcher(string);
        while (matcher.find()) {
            strings.add(matcher.group(0));
        }
        return strings;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CommonUtil.unSubscribeSubs(paySubscriber);
    }

}
