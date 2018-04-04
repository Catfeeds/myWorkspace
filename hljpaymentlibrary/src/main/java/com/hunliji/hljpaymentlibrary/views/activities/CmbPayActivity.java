package com.hunliji.hljpaymentlibrary.views.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljpaymentlibrary.R;
import com.hunliji.hljpaymentlibrary.R2;
import com.hunliji.hljpaymentlibrary.api.PaymentApi;
import com.hunliji.hljpaymentlibrary.models.PayRxEvent;

import butterknife.BindView;
import butterknife.ButterKnife;
import cmb.pb.util.CMBKeyboardFunc;
import rx.Subscription;

/**
 * Created by Suncloud on 2016/9/26.
 */

public class CmbPayActivity extends HljBaseActivity {

    @BindView(R2.id.web_view)
    WebView webView;

    private String payResult;
    private String queryOrderParams;

    private Subscription querySubscription;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cmb_pay___pay);
        ButterKnife.bind(this);
        String url = getIntent().getStringExtra("url");
        payResult = getIntent().getStringExtra("payResult");
        queryOrderParams = getIntent().getStringExtra("queryOrderParams");

        WebSettings webSettings = webView.getSettings();
        webSettings.setUseWideViewPort(true);//设置此属性，可任意比例缩放
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSaveFormData(false);
        webSettings.setSavePassword(false);
        webSettings.setSupportZoom(false);
        try {
            CookieSyncManager.createInstance(getApplicationContext());
            CookieManager.getInstance()
                    .removeAllCookie();
            CookieSyncManager.getInstance()
                    .sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
        webView.loadUrl(url);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                setTitle(title);
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 使用当前的WebView加载页面
                if (!TextUtils.isEmpty(url)) {
                    if (url.contains("m.hunliji.com")) {
                        RxBus.getDefault()
                                .post(new PayRxEvent(PayRxEvent.RxEventType.PAY_SUCCESS,
                                        new JsonParser().parse(payResult)
                                                .getAsJsonObject()));
                        finish();
                    }
                }
                return new CMBKeyboardFunc(CmbPayActivity.this).HandleUrlCall(webView,
                        url) || super.shouldOverrideUrlLoading(view, url);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (!TextUtils.isEmpty(queryOrderParams)) {
            try {
                JsonObject jsonObject = (JsonObject) new JsonParser().parse(queryOrderParams);
                String outTradeNo = jsonObject.get("out_trade_no")
                        .getAsString();
                String source = jsonObject.get("source")
                        .getAsString();
                if (querySubscription != null && !querySubscription.isUnsubscribed()) {
                    querySubscription.unsubscribe();
                }
                querySubscription = PaymentApi.getOrderQuery(outTradeNo, source)
                        .subscribe(HljHttpSubscriber.buildSubscriber(this)
                                .setOnNextListener(new SubscriberOnNextListener<HljHttpResult<JsonElement>>() {
                                    @Override
                                    public void onNext(HljHttpResult<JsonElement> httpResult) {
                                        if (httpResult.getStatus() != null && httpResult.getStatus()
                                                .getRetCode() == 0) {
                                            RxBus.getDefault()
                                                    .post(new PayRxEvent(PayRxEvent.RxEventType
                                                            .PAY_SUCCESS,
                                                            new JsonParser().parse(payResult)
                                                                    .getAsJsonObject()));
                                            finish();
                                        } else {
                                            RxBus.getDefault()
                                                    .post(new PayRxEvent(PayRxEvent.RxEventType
                                                            .PAY_FAIL,
                                                            null));
                                            CmbPayActivity.super.onBackPressed();
                                        }
                                    }
                                })
                                .setProgressDialog(DialogUtil.createProgressDialog(this))
                                .build());
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!TextUtils.isEmpty(webView.getUrl()) && webView.getUrl()
                .contains("MB_EUserP_PayOK")) {
            RxBus.getDefault()
                    .post(new PayRxEvent(PayRxEvent.RxEventType.PAY_SUCCESS,
                            new JsonParser().parse(payResult)
                                    .getAsJsonObject()));
        } else {
            RxBus.getDefault()
                    .post(new PayRxEvent(PayRxEvent.RxEventType.PAY_FAIL, null));
        }
        super.onBackPressed();
    }

    @Override
    protected void onFinish() {
        webView.destroy();
        if (querySubscription != null && !querySubscription.isUnsubscribed()) {
            querySubscription.unsubscribe();
        }
        super.onFinish();
    }
}
