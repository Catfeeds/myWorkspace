package com.hunliji.hljpaymentlibrary.views.activities.xiaoxi_installment;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.example.suncloud.hljweblibrary.client.HljWebClient;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljpaymentlibrary.R;
import com.hunliji.hljpaymentlibrary.R2;
import com.hunliji.hljpaymentlibrary.models.PayRxEvent;
import com.hunliji.hljpaymentlibrary.models.PayRxEvent.RxEventType;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.AuthItem;
import com.hunliji.hljpaymentlibrary.utils.xiaoxi_installment.XiaoxiInstallmentAuthorization;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 小犀分期-通用webView界面
 * Created by chen_bin on 2017/8/29 0029.
 */
public class XiaoxiInstallmentWebViewActivity extends HljBaseActivity {

    @BindView(R2.id.web_view)
    WebView webView;
    @BindView(R2.id.progress)
    ProgressBar progress;

    private String url;
    private boolean isAuto;
    private boolean isCanSkip;
    private int code;

    private static final String SUCCESS_URL = "https://www.hunliji.com/empty/51";
    private static final String FAIL_URL = "https://www.hunliji.com/empty/51/fail";

    public static final String ARG_IS_AUTO = "is_auto";
    public static final String ARG_URL = "url";
    public static final String ARG_CODE = "code";
    public static final String ARG_IS_CAN_SKIP = "is_can_skip";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSwipeBackEnable(false);
        setContentView(R.layout.activity_web_view___web);
        ButterKnife.bind(this);
        initValues();
        initViews();
    }

    private void initValues() {
        setTitle("");
        url = getIntent().getStringExtra(ARG_URL);
        code = getIntent().getIntExtra(ARG_CODE, 0);
        isAuto = getIntent().getBooleanExtra(ARG_IS_AUTO, false);
        isCanSkip = getIntent().getBooleanExtra(ARG_IS_CAN_SKIP, false);
        if (isCanSkip) {
            setOkText("跳过");
        }
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    private void initViews() {
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
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!TextUtils.isEmpty(url)) {
                    if (url.contains(FAIL_URL)) {
                        finishActivity();
                        return true;
                    }
                    if (url.contains(SUCCESS_URL)) {
                        RxBus.getDefault()
                                .post(new PayRxEvent(getRxEventType(code), null));
                        XiaoxiInstallmentAuthorization.getInstance()
                                .onCurrentItemAuthorized(XiaoxiInstallmentWebViewActivity.this,
                                        code,
                                        isAuto);
                        return true;
                    }
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

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
        if (!TextUtils.isEmpty(url)) {
            webView.loadUrl(url);
        }
    }

    private PayRxEvent.RxEventType getRxEventType(int code) {
        switch (code) {
            case AuthItem.CODE_CREDIT_CARD_BILL:
                return RxEventType.AUTHORIZE_CREDIT_CARD_BILL_SUCCESS;
            case AuthItem.CODE_DEPOSIT_CARD_BILL:
                return RxEventType.AUTHORIZE_DEPOSIT_CARD_BILL_SUCCESS;
            case AuthItem.CODE_HOUSE_FUND:
                return RxEventType.AUTHORIZE_HOUSE_FUND_SUCCESS;
            default:
                return null;
        }
    }

    @Override
    public void onOkButtonClick() {
        RxBus.getDefault()
                .post(new PayRxEvent(getRxEventType(code), null));
        XiaoxiInstallmentAuthorization.getInstance()
                .onCurrentItemAuthorized(XiaoxiInstallmentWebViewActivity.this,
                        code,
                        AuthItem.STATUS_UNAUTHORIZED,
                        isAuto);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            finishActivity();
        }
    }

    private void finishActivity() {
        super.onBackPressed();
        if (isAuto) {
            RxBus.getDefault()
                    .post(new PayRxEvent(PayRxEvent.RxEventType.AUTHORIZE_CANCEL, null));
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        if (webView != null) {
            webView.loadUrl("about:blank");
            webView.destroy();
        }
    }
}