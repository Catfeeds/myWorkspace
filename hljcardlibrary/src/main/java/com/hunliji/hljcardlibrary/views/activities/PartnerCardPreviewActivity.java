package com.hunliji.hljcardlibrary.views.activities;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.alibaba.android.arouter.launcher.ARouter;
import com.hunliji.hljcardlibrary.R;
import com.hunliji.hljcardlibrary.R2;
import com.hunliji.hljcardlibrary.api.CardApi;
import com.hunliji.hljcardlibrary.models.Card;
import com.hunliji.hljcardlibrary.utils.CardEditObbUtil;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.sdk.CookieManager;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by hua_rong on 2017/6/22.
 * 另一半请帖预览
 */

public class PartnerCardPreviewActivity extends HljBaseNoBarActivity {


    @BindView(R2.id.webview)
    WebView webView;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R2.id.progress)
    ProgressBar progress;
    @BindView(R2.id.action_layout)
    RelativeLayout actionLayout;

    private Subscription infoSubscription;
    private Subscription frontSubscription;
    private long cardId;
    private String previewPath;
    private Card card;

    @Override
    public String pageTrackTagName() {
        return "另一半请帖预览";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_preview___card);
        ButterKnife.bind(this);
        initWebView();
        initData();
        initLoad();
    }

    private void initData() {
        cardId = getIntent().getLongExtra("id", 0);
        previewPath = getIntent().getStringExtra("path");

        setDefaultStatusBarPadding();
        setStatusBarPaddingColor(ContextCompat.getColor(this, R.color.colorPrimary));
        actionLayout.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(previewPath)) {
            webView.loadUrl(previewPath);
        }
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    private void initWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        //        wenbview缓存
        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCachePath(getCacheDir().getAbsolutePath());
        webSettings.setAppCacheEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings()
                    .setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onReceivedSslError(
                    WebView webView, SslErrorHandler sslErrorHandler, SslError sslError) {
                sslErrorHandler.proceed();
                super.onReceivedSslError(webView, sslErrorHandler, sslError);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
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
        if (cardId == 0) {
            return;
        }
        infoSubscription = CardApi.getCard(cardId)
                .subscribe(HljHttpSubscriber.buildSubscriber(this)
                        .setProgressBar(TextUtils.isEmpty(previewPath) ? progressBar : null)
                        .setOnNextListener(new SubscriberOnNextListener<Card>() {
                            @Override
                            public void onNext(Card card) {
                                if (TextUtils.isEmpty(previewPath)) {
                                    webView.loadUrl(card.getPreviewOnlyLink());
                                }
                                PartnerCardPreviewActivity.this.card = card;
                                onCreateFrontPageThumb();
                            }
                        })
                        .build());
    }


    private void onCreateFrontPageThumb() {
        if (!CommonUtil.isUnsubscribed(frontSubscription)) {
            return;
        }
        frontSubscription = CardEditObbUtil.createPageThumbObb(this,
                card.getFrontPage(),
                true,
                false);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (card != null) {
            onCreateFrontPageThumb();
        }
    }

    @OnClick({R2.id.back, R2.id.btn_back})
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @OnClick(R2.id.btn_share)
    public void onShare() {
        if (card == null) {
            return;
        }
        if (card.isClosed()) {
            DialogUtil.createSingleButtonDialog(this, "请在“设置”中重新开启请帖后再发送", null, null)
                    .show();
            return;
        }
        webView.loadUrl("javascript:INVITATION_CARD.musicPause(true)");
        ARouter.getInstance()
                .build(RouterPath.IntentPath.Card.CARD_SEND)
                .withParcelable("card", card)
                .withTransition(R.anim.slide_in_up, R.anim.activity_anim_default)
                .navigation(this);
    }


    @Override
    protected void onFinish() {
        CommonUtil.unSubscribeSubs(infoSubscription, frontSubscription);
        super.onFinish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            webView.loadUrl("about:blank");
            webView.destroy();
        }
    }
}
