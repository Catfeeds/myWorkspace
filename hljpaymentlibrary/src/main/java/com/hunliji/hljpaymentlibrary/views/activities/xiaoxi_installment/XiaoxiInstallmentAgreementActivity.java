package com.hunliji.hljpaymentlibrary.views.activities.xiaoxi_installment;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.example.suncloud.hljweblibrary.client.HljWebClient;
import com.google.gson.JsonElement;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljpaymentlibrary.R;
import com.hunliji.hljpaymentlibrary.R2;
import com.hunliji.hljpaymentlibrary.api.xiaoxi_installment.XiaoxiInstallmentApi;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 小犀分期-相关协议界面
 * Created by chen_bin on 2017/8/30 0030.
 */
public class XiaoxiInstallmentAgreementActivity extends HljBaseActivity {

    @BindView(R2.id.progress)
    ProgressBar progress;
    @BindView(R2.id.web_view)
    WebView webView;
    @BindView(R2.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;

    private String assetOrderId;
    private int agreementType;
    private long investorId;

    private HljHttpSubscriber initSub;

    public final static String ARG_ASSET_ORDER_ID = "asset_order_id";
    public final static String ARG_TYPE = "type";
    public final static String ARG_INVESTOR_ID = "investor_id";  //查看债权人合同时传递

    public final static int TYPE_LOAN = 1;//借款协议
    public final static int TYPE_PLATFORM_SERVICE = 2; //平台服务协议
    public final static int TYPE_AUTHENTICATION_WITHHOLD = 4;//授权扣款委托书
    public final static int TYPE_ENTRUSTED_PAYMENT = 10;//受托支付协议
    public final static int TYPE_AUTHENTICATION_SERVICE = 11; //授权服务协议
    public final static int TYPE_USER_REGISTER = 13; //用户注册协议
    public final static int TYPE_BEIJING_BANK_DEPOSITORY_SERVICE = 14; //北京银行存管协议服务
    public final static int TYPE_PLAN_MANAGEMENT = 16; //安心计划管理协议

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSwipeBackEnable(false);
        setContentView(R.layout.activity_xiaoxi_installment_agreement);
        ButterKnife.bind(this);
        initValues();
        initViews();
        initLoad();
    }

    private void initValues() {
        setTitle("");
        assetOrderId = getIntent().getStringExtra(ARG_ASSET_ORDER_ID);
        agreementType = getIntent().getIntExtra(ARG_TYPE, 0);
        investorId = getIntent().getLongExtra(ARG_INVESTOR_ID, 0);
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    private void initViews() {
        emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                initLoad();
            }
        });
        webView.getSettings()
                .setJavaScriptEnabled(true);
        webView.getSettings()
                .setAllowFileAccess(true);
        webView.getSettings()
                .setDomStorageEnabled(true);
        webView.getSettings()
                .setAppCachePath(getCacheDir().getAbsolutePath());
        webView.getSettings()
                .setAppCacheEnabled(true);
        if (HljCommon.debug) {
            webView.getSettings()
                    .setCacheMode(WebSettings.LOAD_NO_CACHE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings()
                    .setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webView.setWebViewClient(new HljWebClient(this) {

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                setTitle(view.getTitle());
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (isFinishing()) {
                    return;
                }
                if (newProgress < 100) {
                    progress.setVisibility(View.VISIBLE);
                    progress.setProgress(newProgress);
                } else {
                    progress.setVisibility(View.GONE);
                }
                super.onProgressChanged(view, newProgress);
            }
        });
    }

    private void initLoad() {
        if (initSub == null || initSub.isUnsubscribed()) {
            initSub = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<JsonElement>() {
                        @Override
                        public void onNext(JsonElement jsonElement) {
                            String contextMimeType = CommonUtil.getAsString(jsonElement,
                                    "contextMimeType");
                            String base64Context = CommonUtil.getAsString(jsonElement,
                                    "base64Context");
                            if (!TextUtils.isEmpty(base64Context)) {
                                webView.loadDataWithBaseURL(null,
                                        new String(Base64.decode(base64Context, Base64.DEFAULT)),
                                        contextMimeType,
                                        "UTF-8",
                                        null);
                            }
                        }
                    })
                    .setContentView(webView)
                    .setEmptyView(emptyView)
                    .setProgressBar(progressBar)
                    .build();
            if (!TextUtils.isEmpty(assetOrderId)) {
                XiaoxiInstallmentApi.getAgreementByTypeObb(this,
                        assetOrderId,
                        agreementType,
                        investorId)
                        .subscribe(initSub);
            } else {
                XiaoxiInstallmentApi.getPreviewAgreementByTypeObb(this, agreementType)
                        .subscribe(initSub);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        if (webView != null) {
            webView.loadUrl("about:blank");
            webView.destroy();
        }
        CommonUtil.unSubscribeSubs(initSub);
    }
}