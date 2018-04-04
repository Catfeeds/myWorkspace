package com.hunliji.hljcardlibrary.views.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.hunliji.hljcardlibrary.HljCard;
import com.hunliji.hljcardlibrary.R;
import com.hunliji.hljcardlibrary.R2;
import com.hunliji.hljcardlibrary.api.CardApi;
import com.hunliji.hljcardlibrary.models.Card;
import com.hunliji.hljcardlibrary.models.CardRxEvent;
import com.hunliji.hljcardlibrary.models.Theme;
import com.hunliji.hljcardlibrary.utils.PrivilegeUtil;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.models.ShareInfo;
import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljcommonlibrary.models.customer.CustomerUser;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.FileUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljhttplibrary.api.FileApi;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.entities.HljHttpStatus;
import com.hunliji.hljhttplibrary.utils.AuthUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnCompletedListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljsharelibrary.HljShare;
import com.hunliji.hljsharelibrary.utils.ShareUtil;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.sdk.CookieManager;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func1;

/**
 * Created by hua_rong on 2017/6/22.
 * 请帖预览
 */

public class CardPreviewActivity extends HljBaseNoBarActivity {


    @BindView(R2.id.webview)
    WebView webView;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R2.id.progress)
    ProgressBar progress;
    @BindView(R2.id.btn_use)
    Button btnUse;
    @BindView(R2.id.action_layout)
    RelativeLayout actionLayout;
    @BindView(R2.id.btn_back)
    ImageButton btnBack;
    @BindView(R2.id.action_holder_layout)
    LinearLayout actionHolderLayout;
    @BindView(R2.id.action_holder_layout2)
    LinearLayout actionHolderLayout2;
    @BindView(R2.id.btn_share)
    Button btnShare;

    private long themeId;
    private Theme theme;
    private boolean isThemeLock;
    private boolean isCheckTheme;

    private Card card;

    private Subscription themeSubscription;
    private Subscription downloadSubscription;
    private Subscription rxSubscription;
    private Subscription rxMemberSubscription;
    private HljHttpSubscriber shareSubscriber;
    private HljHttpSubscriber unLockSubscriber;
    private HljHttpSubscriber checkThemeSubscriber;
    private HljHttpSubscriber checkPrivilegeSub;
    private ShareUtil shareUtil;
    private Context context;
    public static final int SHARE = 0;
    public static final int MEMBER = 1;
    private boolean isCardMasterLogin;//来自请帖大师 并且没有登录
    private static final int THEME_IS_LOCK = 6666;//6666 未解锁不可用


    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case HljShare.RequestCode.SHARE_TO_PENGYOU:
                    if (unLockSubscriber == null || unLockSubscriber.isUnsubscribed()) {
                        Observable<HljHttpResult> observable = CardApi.unlockTheme();
                        unLockSubscriber = HljHttpSubscriber.buildSubscriber(CardPreviewActivity
                                .this)
                                .setOnNextListener(new SubscriberOnNextListener() {
                                    @Override
                                    public void onNext(Object o) {
                                        isThemeLock = false;
                                        download(theme);
                                        RxBus.getDefault()
                                                .post(new CardRxEvent(CardRxEvent.RxEventType
                                                        .CARD_APP_SHARE_SUCCESS,
                                                        null));
                                    }
                                })
                                .build();
                        observable.subscribe(unLockSubscriber);
                    }
                    break;
            }
            return false;
        }
    });

    @Override
    public String pageTrackTagName() {
        return "请帖预览";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_preview___card);
        ButterKnife.bind(this);
        context = this;
        initWebView();
        initData();
        initLoad();
        registerRxBusEvent();

        initTracker();
    }

    private void initTracker() {
        HljVTTagger.buildTagger(btnUse)
                .tagName("theme_preview_use_button")
                .hitTag();
    }

    private void initData() {
        themeId = getIntent().getLongExtra("id", 0);
        String path = getIntent().getStringExtra("path");
        isThemeLock = getIntent().getBooleanExtra("isThemeLock", false);

        card = getIntent().getParcelableExtra("card");

        if (themeId > 0) {
            btnUse.setVisibility(View.VISIBLE);
            btnBack.setVisibility(View.VISIBLE);
            setActionBarPadding(this, actionHolderLayout2);
        } else {
            setDefaultStatusBarPadding();
            setStatusBarPaddingColor(ContextCompat.getColor(this, R.color.colorPrimary));
            actionLayout.setVisibility(View.VISIBLE);
            btnShare.setVisibility(card != null ? View.VISIBLE : View.GONE);
        }
        webView.loadUrl(path);
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
        if (themeId == 0) {
            return;
        }
        if (themeSubscription != null && !themeSubscription.isUnsubscribed()) {
            return;
        }
        themeSubscription = CardApi.getTheme(themeId)
                .subscribe(new Subscriber<Theme>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Theme theme) {
                        CardPreviewActivity.this.theme = theme;
                    }
                });
    }

    private void registerRxBusEvent() {
        rxSubscription = RxBus.getDefault()
                .toObservable(CardRxEvent.class)
                .subscribe(new RxBusSubscriber<CardRxEvent>() {
                    @Override
                    protected void onEvent(CardRxEvent cardRxEvent) {
                        switch (cardRxEvent.getType()) {
                            case CREATE_CARD:
                                finish();
                                break;
                            case CARD_INFO_EDIT:
                                Card card = (Card) cardRxEvent.getObject();
                                CardPreviewActivity.this.card = card;
                                webView.loadUrl(card.getPreviewOnlyLink());
                                break;
                        }
                    }
                });
        rxMemberSubscription = RxBus.getDefault()
                .toObservable(RxEvent.class)
                .subscribe(new RxBusSubscriber<RxEvent>() {
                    @Override
                    protected void onEvent(RxEvent rxEvent) {
                        switch (rxEvent.getType()) {
                            case OPEN_MEMBER_SUCCESS:
                                download(theme);
                                break;
                        }
                    }
                });
    }

    @OnClick({R2.id.back, R2.id.btn_back})
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @OnClick(R2.id.btn_share)
    public void onShare() {
        webView.loadUrl("javascript:INVITATION_CARD.musicPause(true)");
        if (card == null) {
            return;
        }
        if (card.isClosed()) {
            DialogUtil.createSingleButtonDialog(this, "请在“设置”中重新开启请帖后再发送", null, null)
                    .show();
            return;
        }
        ARouter.getInstance()
                .build(RouterPath.IntentPath.Card.CARD_SEND)
                .withParcelable("card", card)
                .withTransition(R.anim.slide_in_up, R.anim.activity_anim_default)
                .navigation(this);
    }

    @OnClick(R2.id.btn_use)
    public void onBtnUseClicked() {
        webView.loadUrl("javascript:INVITATION_CARD.musicPause(true)");
        if (HljCard.isCardMaster(this) && !AuthUtil.loginBindCheck(this)) {
            isCardMasterLogin = true;
            return;
        }
        if (theme == null) {
            initLoad();
            return;
        }

        if (theme.isMember()) {
            User user = UserSession.getInstance()
                    .getUser(this);
            if (user instanceof CustomerUser) {
                PrivilegeUtil.isVipAvailable(this,
                        checkPrivilegeSub,
                        new PrivilegeUtil.PrivilegeCallback() {
                            @Override
                            public void checkDone(boolean isAvailable) {
                                if (!isAvailable) {
                                    onCardShare(MEMBER);
                                } else {
                                    startCheckAndDownload(true);
                                }
                            }
                        });
            } else {
                startCheckAndDownload(false);
            }
        } else {
            startCheckAndDownload(false);
        }
    }

    private void startCheckAndDownload(boolean isAvailable) {
        // isAvailable状态覆盖其他状态
        if (isAvailable) {
            download(theme);
            return;
        }
        if (!isCheckTheme && isCardMasterLogin && !theme.isMember()) {
            checkTheme();
        } else {
            if (isThemeLock) {
                onCardShare(SHARE);
                return;
            }
            download(theme);
        }
    }

    /**
     * 请帖大师 检测登录 成功后 检测 模版是否可用
     * 请求一次
     */
    private void checkTheme() {
        checkThemeSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpResult>() {
                    @Override
                    public void onNext(HljHttpResult hljHttpResult) {
                        HljHttpStatus hljHttpStatus = hljHttpResult.getStatus();
                        if (hljHttpStatus != null) {
                            isCheckTheme = true;
                            if (hljHttpStatus.getRetCode() == THEME_IS_LOCK) {
                                isThemeLock = true;
                                onCardShare(SHARE);
                            } else {
                                isThemeLock = false;
                                download(theme);
                            }
                        }
                    }
                })
                .setProgressBar(progressBar)
                .build();
        CardApi.checkTheme(themeId)
                .subscribe(checkThemeSubscriber);
    }


    public void onCardShare(final int type) {
        final Dialog shareDialog = new Dialog(context, R.style.BubbleDialogTheme);
        Window window = shareDialog.getWindow();
        if (window != null) {
            window.setContentView(R.layout.dialog_theme_share___card);
            Button shareBtn = window.findViewById(R.id.share_btn);
            TextView hint = window.findViewById(R.id.hint);
            if (type == SHARE) {
                shareBtn.setText(R.string.btn_share_py___card);
                hint.setText(R.string.hint_theme_share___card);
            } else if (type == MEMBER) {
                shareBtn.setText(R.string.label_join_member___card);
                hint.setText(getString(R.string.hint_join_member___card));
            }
            shareBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shareDialog.dismiss();
                    if (type == SHARE) {
                        shareToPengYou();
                    } else if (type == MEMBER) {
                        ARouter.getInstance()
                                .build(RouterPath.IntentPath.Customer.OPEN_MEMBER)
                                .navigation(CardPreviewActivity.this);
                    }
                }
            });
            window.findViewById(R.id.close_btn)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            shareDialog.dismiss();
                        }
                    });
            WindowManager.LayoutParams params = window.getAttributes();
            Point point = CommonUtil.getDeviceSize(context);
            params.width = Math.round(point.x * 27 / 32);
        }
        shareDialog.show();
    }


    private void shareToPengYou() {
        if (shareUtil == null) {
            if (shareSubscriber == null || shareSubscriber.isUnsubscribed()) {
                Observable<ShareInfo> observable = CardApi.getAppShareInfo();
                shareSubscriber = HljHttpSubscriber.buildSubscriber(context)
                        .setOnNextListener(new SubscriberOnNextListener<ShareInfo>() {
                            @Override
                            public void onNext(ShareInfo shareInfo) {
                                if (shareInfo != null) {
                                    shareUtil = new ShareUtil(context, shareInfo, handler);
                                    shareUtil.shareToPengYou();
                                }
                            }
                        })
                        .setOnErrorListener(new SubscriberOnErrorListener() {
                            @Override
                            public void onError(Object o) {
                                Toast.makeText(context, "分享失败", Toast.LENGTH_SHORT)
                                        .show();
                            }
                        })
                        .setProgressBar(progressBar)
                        .build();
                observable.subscribe(shareSubscriber);
            }
        } else {
            shareUtil.shareToPengYou();
        }
    }

    private void download(final Theme theme) {
        if (downloadSubscription != null && !downloadSubscription.isUnsubscribed()) {
            return;
        }
        downloadSubscription = Observable.from(theme.getImagePaths(this))
                .filter(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String path) {
                        File file = FileUtil.createThemeFile(CardPreviewActivity.this, path);
                        return file == null || !file.exists() || file.length() == 0;
                    }
                })
                .concatMap(new Func1<String, Observable<File>>() {
                    @Override
                    public Observable<File> call(String path) {
                        return FileApi.download(path,
                                FileUtil.createThemeFile(CardPreviewActivity.this, path)
                                        .getAbsolutePath());
                    }
                })
                .concatWith(Observable.from(theme.getFontPaths(this))
                        .filter(new Func1<String, Boolean>() {
                            @Override
                            public Boolean call(String path) {
                                File file = FileUtil.createFontFile(CardPreviewActivity.this, path);
                                return file == null || !file.exists() || file.length() == 0;
                            }
                        })
                        .concatMap(new Func1<String, Observable<File>>() {
                            @Override
                            public Observable<File> call(String path) {
                                return FileApi.download(path,
                                        FileUtil.createFontFile(CardPreviewActivity.this, path)
                                                .getAbsolutePath());
                            }
                        }))
                .subscribe(HljHttpSubscriber.buildSubscriber(this)
                        .setProgressDialog(DialogUtil.createProgressDialog(this))
                        .setOnCompletedListener(new SubscriberOnCompletedListener() {
                            @Override
                            public void onCompleted() {
                                ARouter.getInstance()
                                        .build(RouterPath.IntentPath.Card.CARD_INFO_EDIT)
                                        .withParcelable("theme", theme)
                                        .navigation(CardPreviewActivity.this);
                            }
                        })
                        .build());
    }


    @Override
    protected void onFinish() {
        CommonUtil.unSubscribeSubs(themeSubscription,
                downloadSubscription,
                rxSubscription,
                rxMemberSubscription,
                shareSubscriber,
                unLockSubscriber,
                checkThemeSubscriber,
                checkPrivilegeSub);
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
