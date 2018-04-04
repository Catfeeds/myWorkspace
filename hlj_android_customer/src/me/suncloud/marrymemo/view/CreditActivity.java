package me.suncloud.marrymemo.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;

import java.util.Stack;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.util.ShareUtil;
import me.suncloud.marrymemo.util.Util;

/**
 * Created by Suncloud on 2016/3/2.
 */
public class CreditActivity extends HljBaseNoBarActivity {

    @BindView(R.id.webview)
    WebView mWebView;
    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.share)
    ImageButton share;
    @BindView(R.id.progress)
    ProgressBar progress;
    @BindView(R.id.progressBar)
    ProgressBar progressView;


    private static String ua;
    private static Stack<CreditActivity> activityStack;
    public static final String VERSION = "1.0.8";
    public static boolean IS_WAKEUP_LOGIN = false;
    public static String INDEX_URI = "/chome/index";
    private String url;
    private ShareUtil shareUtil;
    private Boolean ifRefresh = false;
    private static final int RequestCode = 100;
    private Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSwipeBackEnable(false);
        setContentView(R.layout.activity_share_web_view);
        ButterKnife.bind(this);
        setDefaultStatusBarPadding();
        url = getIntent().getStringExtra("url");
        // 管理匿名类栈，用于模拟原生应用的页面跳转。
        if (activityStack == null) {
            activityStack = new Stack<>();
        }
        activityStack.push(this);
        initWebView();
    }

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface", "AddJavascriptInterface"})
    protected void initWebView() {
        WebSettings settings = mWebView.getSettings();

        // User settings
        settings.setJavaScriptEnabled(true);    //设置webview支持javascript
        settings.setLoadsImagesAutomatically(true);    //支持自动加载图片
        settings.setUseWideViewPort(true);    //设置webview推荐使用的窗口，使html界面自适应屏幕
        settings.setLoadWithOverviewMode(true);
        settings.setSaveFormData(true);    //设置webview保存表单数据
        settings.setSavePassword(true);    //设置webview保存密码
        settings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);    //设置中等像素密度，medium=160dpi
        settings.setSupportZoom(true);    //支持缩放

        CookieManager.getInstance()
                .setAcceptCookie(true);

        if (Build.VERSION.SDK_INT > 8) {
            settings.setPluginState(WebSettings.PluginState.ON_DEMAND);
        }

        // Technical settings
        settings.setSupportMultipleWindows(true);
        mWebView.setLongClickable(true);
        mWebView.setScrollbarFadingEnabled(true);
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWebView.setDrawingCacheEnabled(true);


        settings.setAppCacheEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setDomStorageEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        if (ua == null) {
            ua = mWebView.getSettings()
                    .getUserAgentString() + " Duiba/" + VERSION;
        }
        settings.setUserAgentString(ua);

        //js调java代码接口。
        mWebView.addJavascriptInterface(new Object() {

            //用于跳转用户登录页面事件。
            @JavascriptInterface
            public void login() {
                mWebView.post(new Runnable() {
                    @Override
                    public void run() {
                        onLoginClick(mWebView, mWebView.getUrl());
                    }
                });
            }

            //复制券码
            @JavascriptInterface
            public void copyCode(final String code) {
                mWebView.post(new Runnable() {
                    @Override
                    public void run() {
                        onCopyCode(mWebView, code);
                    }
                });
            }

            //客户端本地触发刷新积分。
            @JavascriptInterface
            public void localRefresh(final String credits) {
                mWebView.post(new Runnable() {
                    @Override
                    public void run() {
                        onLocalRefresh(mWebView, credits);
                    }
                });
            }

        }, "duiba_app");

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                CreditActivity.this.onReceivedTitle(view, title);
            }

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

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return shouldOverrideUrlByDuiba(view, url);
            }
        });

        mWebView.loadUrl(url);
    }

    public void onBackPressed(View v) {
        onBackPressed();
    }


    public void onShareInfo(View v) {
        if ((dialog != null && dialog.isShowing()) || shareUtil == null) {
            return;
        }
        if (dialog == null) {
            dialog = Util.initShareDialog(this, shareUtil,null);
        }
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(99, intent);
        finishActivity(this);

        super.onBackPressed();

    }

    private void onLoginClick(WebView webView, String currentUrl) {

    }

    private void onCopyCode(WebView mWebView, String code) {
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        clipboardManager.setPrimaryClip(ClipData.newPlainText(getString(R.string.app_name), code));
        Util.showToast(this, getString(R.string.hint_code_copy, code), 0);
    }

    private void onLocalRefresh(WebView mWebView, String credits) {

    }

    protected void onReceivedTitle(WebView view, String title) {
        mTitle.setText(title);
    }

    /**
     * 拦截url请求，根据url结尾执行相应的动作。 （重要）
     * 用途：模仿原生应用体验，管理页面历史栈。
     *
     * @param view
     * @param url
     * @return
     */
    protected boolean shouldOverrideUrlByDuiba(WebView view, String url) {
        Uri uri = Uri.parse(url);
        if (this.url.equals(url)) {
            view.loadUrl(url);
            return true;
        }
        // 处理电话链接，启动本地通话应用。
        if (url.startsWith("tel:")) {
            Intent intent = new Intent(Intent.ACTION_DIAL, uri);
            startActivity(intent);
            return true;
        }
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            return false;
        }
        // 截获页面分享请求，分享数据
        if ("/client/dbshare".equals(uri.getPath())) {
            String content = uri.getQueryParameter("content");
            if (content != null) {
                String[] dd = content.split("\\|");
                if (dd.length == 4) {
                    setShareInfo(dd[0], dd[1], dd[2], dd[3]);
                    share.setVisibility(View.VISIBLE);
                }
            }
            return true;
        }
        // 截获页面唤起登录请求。（目前暂时还是用js回调的方式，这里仅作预留。）
        if ("/client/dblogin".equals(uri.getPath())) {
            mWebView.post(new Runnable() {
                @Override
                public void run() {
                    onLoginClick(mWebView, mWebView.getUrl());
                }
            });
            return true;
        }
        if (url.contains("dbnewopen")) { // 新开页面
            Intent intent = new Intent(this, CreditActivity.class);
            url = url.replace("dbnewopen", "none");
            intent.putExtra("url", url);
            startActivityForResult(intent, RequestCode);
        } else if (url.contains("dbbackrefresh")) { // 后退并刷新
            url = url.replace("dbbackrefresh", "none");
            Intent intent = new Intent();
            intent.putExtra("url", url);
            setResult(RequestCode, intent);
            finishActivity(this);
        } else if (url.contains("dbbackrootrefresh")) { // 回到积分商城首页并刷新
            if (activityStack.size() == 1) {
                finishActivity(this);
            } else {
                activityStack.get(0).ifRefresh = true;
                finishUpActivity();
            }
        } else if (url.contains("dbbackroot")) { // 回到积分商城首页
            if (activityStack.size() == 1) {
                finishActivity(this);
            } else {
                finishUpActivity();
            }
        } else if (url.contains("dbback")) { // 后退
            finishActivity(this);
        } else {
            if (url.endsWith(".apk") || url.contains(".apk?")) { // 支持应用链接下载
                Intent viewIntent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(viewIntent);
                return true;
            }
            view.loadUrl(url);
        }
        return true;
    }
    //--------------------------------------------以下为工具方法----------------------------------------------

    /**
     * 配置分享信息
     */
    private void setShareInfo(
            String shareUrl, String shareThumbnail, String shareTitle, String shareSubtitle) {
        shareUtil = new ShareUtil(this,
                shareUrl,
                shareTitle,
                shareSubtitle,
                shareSubtitle,
                shareThumbnail,
                progressView);
    }

    /**
     * 结束除了最底部一个以外的所有Activity
     */
    private void finishUpActivity() {
        int size = activityStack.size();
        for (int i = 0; i < size - 1; i++) {
            activityStack.pop()
                    .finish();
        }
    }

    /**
     * 结束指定的Activity
     */
    private void finishActivity(CreditActivity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            activity.finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == 100) {
            if (intent.getStringExtra("url") != null) {
                this.url = intent.getStringExtra("url");
                mWebView.loadUrl(this.url);
                ifRefresh = false;
            }
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (ifRefresh) {
            this.url = getIntent().getStringExtra("url");
            mWebView.loadUrl(this.url);
            ifRefresh = false;
            //如果首页含有登录的入口，返回时需要同时刷新首页的话，
            // 需要把下面判断语句中的 && this.url.indexOf(INDEX_URI) > 0 去掉。
        } else if (IS_WAKEUP_LOGIN && this.url.indexOf(INDEX_URI) > 0) {
            mWebView.reload();
            IS_WAKEUP_LOGIN = false;
        } else {
            // 返回页面时，如果页面含有onDBNewOpenBack()方法,则调用该js方法。
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mWebView.evaluateJavascript("if(window.onDBNewOpenBack){onDBNewOpenBack()}",
                        new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String value) {
                            }
                        });
            } else {
                mWebView.loadUrl("javascript:if(window.onDBNewOpenBack){onDBNewOpenBack()}");
            }
        }
    }
}
