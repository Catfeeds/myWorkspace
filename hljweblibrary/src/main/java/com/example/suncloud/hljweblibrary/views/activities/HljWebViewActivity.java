package com.example.suncloud.hljweblibrary.views.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.ArrayMap;
import android.support.v4.util.SimpleArrayMap;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.example.suncloud.hljweblibrary.R;
import com.example.suncloud.hljweblibrary.client.HljChromeClient;
import com.example.suncloud.hljweblibrary.client.HljWebClient;
import com.example.suncloud.hljweblibrary.constructors.JsInterfaceConstructor;
import com.example.suncloud.hljweblibrary.jsinterface.BaseWebHandler;
import com.example.suncloud.hljweblibrary.jsinterface.HljWebJsInterface;
import com.example.suncloud.hljweblibrary.jsinterface.MadWebHandler;
import com.example.suncloud.hljweblibrary.jsinterface.MadWebJsInterface;
import com.example.suncloud.hljweblibrary.jsinterface.WebViewJsInterface;
import com.example.suncloud.hljweblibrary.utils.WebUtil;
import com.example.suncloud.hljweblibrary.views.widgets.CommonWebBar;
import com.example.suncloud.hljweblibrary.views.widgets.TransparentWebBar;
import com.example.suncloud.hljweblibrary.views.widgets.WebBar;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljsharelibrary.utils.ShareDialogUtil;
import com.hunliji.hljsharelibrary.utils.ShareUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Suncloud on 2016/8/19.
 */
public class HljWebViewActivity extends HljBaseNoBarActivity {

    protected WebView webView;
    protected WebBar.WebViewBarInterface barInterface;
    protected ProgressBar progress;
    protected ProgressBar progressBar;
    private Dialog dialog;
    private ValueCallback<Uri[]> mUploadMessages;
    private ValueCallback<Uri> mUploadMessage;
    private String titleStr;
    protected OnOkTextInterface okTextInterface;

    protected SimpleArrayMap<String, WebViewJsInterface> jsInterfaces; //js交互相关接口

    private static final int FILE_CHOOSER_RESULT = 1;

    public final static String ARG_PATH = "path";
    public final static String ARG_BAR_STYLE = "bar_style";
    public final static String ARG_HTML = "html";
    public static final String ARG_TITLE = "title";

    public final static int BAR_STYLE_COMMON = 0;
    public final static int BAR_STYLE_TRANSPARENT = 1;
    public final static int BAR_STYLE_CUSTOM = 2;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case BaseWebHandler.ON_WEB_SHARE:
                    onShareInfo();
                    break;
                case BaseWebHandler.WEB_SHARE_CHECK:
                    if (okTextInterface == null || !okTextInterface.onOkTextEnable()) {
                        if (msg.obj != null && msg.obj instanceof Boolean) {
                            barInterface.setShareEnable((boolean) msg.obj);
                        }
                    }
                    break;
                case BaseWebHandler.WEB_UPDATE_DONE:
                    if (TextUtils.isEmpty(titleStr)) {
                        setTitle(webView.getTitle());
                        barInterface.setTitle(webView.getTitle());
                    }
                    checkShareInfo();
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view___web);

        setDefaultStatusBarPadding();

        webView = (WebView) findViewById(R.id.web_view);
        progress = (ProgressBar) findViewById(R.id.progress);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        initBottomLayout((ViewGroup) findViewById(R.id.bottom_layout));
        String path = getPageUrl();
        String html = getHtml();
        titleStr = pageTitle();
        int barStyle = getIntent().getIntExtra(ARG_BAR_STYLE, BAR_STYLE_COMMON);
        initActionBar(barStyle);
        if (!TextUtils.isEmpty(titleStr)) {
            setTitle(titleStr);
            barInterface.setTitle(titleStr);
        }
        if (okTextInterface != null) {
            barInterface.setOkButtonEnable(okTextInterface.onOkTextEnable(),
                    okTextInterface.okTextColor(),
                    okTextInterface.okText(),
                    okTextInterface.okTextSize());
        }
        webView.getSettings()
                .setJavaScriptEnabled(true);
        webView.getSettings()
                .setAllowFileAccess(true);
        //        wenbview缓存
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

        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(
                    String url,
                    String userAgent,
                    String contentDisposition,
                    String mimetype,
                    long contentLength) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        webView.setWebViewClient(new HljWebClient(this) {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!TextUtils.isEmpty(url) && url.startsWith("tel:")) {
                    callUp(Uri.parse(url));
                    return true;
                }
                Log.d("GoldActivity", url);
                Uri uri = Uri.parse(url);
                if ("alipays".equals(uri.getScheme()) || "weixin".equals(uri.getScheme())) {
                    //支付宝支付或微信支付
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                    } catch (ActivityNotFoundException notFoundEx) {//尝试h5网页支付
                        return true;
                    }
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (!TextUtils.isEmpty(url) && (url.startsWith("http://") || url.startsWith(
                        "https://"))) {
                    initWebViewJsInterface(url);
                }
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                pageFinished(view, url);
            }
        });

        webView.setWebChromeClient(new HljChromeClient() {
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

            @Override
            public boolean onShowFileChooser(
                    WebView webView,
                    ValueCallback<Uri[]> filePathCallback,
                    FileChooserParams fileChooserParams) {
                if (mUploadMessages != null)
                    return true;
                mUploadMessages = filePathCallback;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && fileChooserParams
                        .getAcceptTypes() != null && fileChooserParams.getAcceptTypes().length >
                        0) {
                    intent.setType(fileChooserParams.getAcceptTypes()[0]);
                } else {
                    intent.setType("*/*");
                }
                startActivityForResult(Intent.createChooser(intent, "File Chooser"),
                        FILE_CHOOSER_RESULT);
                return true;
            }


            @Override
            public void openFileChooser(
                    ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                if (mUploadMessage != null)
                    return;
                mUploadMessage = uploadMsg;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                if (TextUtils.isEmpty(acceptType)) {
                    intent.setType("*/*");
                } else {
                    intent.setType(acceptType);
                }
                startActivityForResult(Intent.createChooser(intent, "File Chooser"),
                        FILE_CHOOSER_RESULT);
            }

            @Override
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                openFileChooser(uploadMsg, acceptType, "");
            }
        });
        if (!TextUtils.isEmpty(path)) {
            loadUrl(path);
        } else if (!TextUtils.isEmpty(html)) {
            loadHtml(html);
        }
    }

    protected String pageTitle() {
        return getIntent().getStringExtra(ARG_TITLE);
    }

    protected String getPageUrl() {
        return getIntent().getStringExtra(ARG_PATH);
    }

    protected String getHtml() {
        return getIntent().getStringExtra(ARG_HTML);
    }

    protected void pageFinished(WebView view, String url) {
        if (TextUtils.isEmpty(titleStr)) {
            setTitle(webView.getTitle());
            barInterface.setTitle(webView.getTitle());
        }
        barInterface.setShareEnable(false);
        checkShareInfo();
        if (okTextInterface == null || !okTextInterface.onOkTextEnable()) {
            barInterface.setCloseEnable(!webView.canGoBack());
        }
    }

    protected void loadUrl(String path) {
        Map<String, String> header = new HashMap<>();
        path = WebUtil.addPathQuery(this, path);
        if (WebUtil.isHljUrl(path)) {
            header = WebUtil.getWebHeaders(this);
        }

        initWebViewJsInterface(path);
        if (!header.isEmpty()) {
            webView.loadUrl(path, header);
        } else {
            webView.loadUrl(path);
        }
    }

    private void loadHtml(String html) {
        if (jsInterfaces == null) {
            jsInterfaces = new ArrayMap<>();
        }
        jsInterfaces.put(HljWebJsInterface.NAME,
                new HljWebJsInterface(webView,
                        JsInterfaceConstructor.getJsInterface(this, null, webView, handler)));
        webView.loadDataWithBaseURL(null, html, "text/HTML", "UTF-8", null);
    }

    private void initActionBar(int style) {
        WebBar webActionBar;
        RelativeLayout layout = findViewById(R.id.layout);
        switch (style) {
            case BAR_STYLE_TRANSPARENT:
                webActionBar = new TransparentWebBar(this);
                break;
            case BAR_STYLE_CUSTOM:
                webActionBar = initWebBar();
                webActionBar.setId(R.id.action_layout);
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) webView
                        .getLayoutParams();
                layoutParams.addRule(RelativeLayout.BELOW, R.id.action_layout);
                break;
            default:
                webActionBar = new CommonWebBar(this);
                webActionBar.setId(R.id.action_layout);
                layoutParams = (RelativeLayout.LayoutParams) webView.getLayoutParams();
                layoutParams.addRule(RelativeLayout.BELOW, R.id.action_layout);
                break;
        }
        layout.addView(webActionBar,
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
        webActionBar.setBarClickListener(new WebBar.WebViewBarClickListener() {
            @Override
            public void onSharePressed() {
                getShareInfo();
            }

            @Override
            public void onBackPressed() {
                HljWebViewActivity.this.onBackPressed();
            }

            @Override
            public void onOkButtonPressed() {
                if (okTextInterface != null && okTextInterface.onOkTextEnable()) {
                    okTextInterface.onOkButtonPressed();
                }
            }
        });
        barInterface = webActionBar.getInterface();
    }


    public void initWebViewJsInterface(String path) {
        if (jsInterfaces == null) {
            jsInterfaces = new ArrayMap<>();
        }
        //hlj js接口判断
        WebViewJsInterface hljJsInterface = jsInterfaces.get(HljWebJsInterface.NAME);
        if (WebUtil.isHljUrl(path)) {
            if (hljJsInterface == null) {
                jsInterfaces.put(HljWebJsInterface.NAME,
                        new HljWebJsInterface(webView,
                                JsInterfaceConstructor.getJsInterface(this,
                                        path,
                                        webView,
                                        handler)));
            }
        } else if (hljJsInterface != null) {
            hljJsInterface.onRemove(webView);
            jsInterfaces.remove(HljWebJsInterface.NAME);
        }


        //Mad js接口判断
        WebViewJsInterface madJsInterface = jsInterfaces.get(MadWebJsInterface.NAME);
        if (WebUtil.isMadUrl(path)) {
            if (madJsInterface == null) {
                jsInterfaces.put(MadWebJsInterface.NAME,
                        new MadWebJsInterface(webView, new MadWebHandler(this, handler)));
            }
        } else if (madJsInterface != null) {
            madJsInterface.onRemove(webView);
            jsInterfaces.remove(MadWebJsInterface.NAME);
        }
    }

    @Override
    public void onBackPressed() {
        if (goBack()) {
            return;
        }
        if (webView.canGoBack()) {
            webView.goBack();
        } else
            super.onBackPressed();
    }


    private void onShareInfo() {
        ShareDialogUtil.onCommonShare(this, getShareUtil());
    }

    public void setOkTextInterface(OnOkTextInterface okTextInterface) {
        this.okTextInterface = okTextInterface;
    }

    public interface OnOkTextInterface {

        boolean onOkTextEnable();

        int okTextColor();

        int okTextSize();

        String okText();

        void onOkButtonPressed();
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        if (jsInterfaces != null && !jsInterfaces.isEmpty()) {
            for (int i = 0, size = jsInterfaces.size(); i < size; i++) {
                WebViewJsInterface jsInterface = jsInterfaces.valueAt(i);
                jsInterface.onDestroy();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            webView.loadUrl("about:blank");
            webView.destroy();
            setContentView(new FrameLayout(this));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (jsInterfaces != null && !jsInterfaces.isEmpty()) {
            for (int i = 0, size = jsInterfaces.size(); i < size; i++) {
                WebViewJsInterface jsInterface = jsInterfaces.valueAt(i);
                jsInterface.onActivityResult(requestCode, resultCode, data);
            }
        }
        if (requestCode == FILE_CHOOSER_RESULT) {
            if (null != mUploadMessage) {
                Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
                if (result != null) {
                    mUploadMessage.onReceiveValue(result);
                }
                mUploadMessage = null;
            }
            if (null != mUploadMessages) {
                Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
                if (result != null) {
                    Uri[] uris = {result};
                    mUploadMessages.onReceiveValue(uris);
                }
                mUploadMessages = null;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void getShareInfo() {
        if (jsInterfaces == null || jsInterfaces.isEmpty()) {
            return;
        }
        for (int i = 0, size = jsInterfaces.size(); i < size; i++) {
            WebViewJsInterface jsInterface = jsInterfaces.valueAt(i);
            jsInterface.getShareInfo(webView);
        }
    }

    public void checkShareInfo() {
        if (jsInterfaces == null || jsInterfaces.isEmpty()) {
            barInterface.setShareEnable(false);
            return;
        }
        for (int i = 0, size = jsInterfaces.size(); i < size; i++) {
            WebViewJsInterface jsInterface = jsInterfaces.valueAt(i);
            jsInterface.checkShareInfo(webView);
        }
    }

    public boolean goBack() {
        if (jsInterfaces == null || jsInterfaces.isEmpty()) {
            return false;
        }
        boolean isGoBack = false;
        for (int i = 0, size = jsInterfaces.size(); i < size; i++) {
            WebViewJsInterface jsInterface = jsInterfaces.valueAt(i);
            isGoBack |= jsInterface.goBack(webView);
        }
        return isGoBack;
    }

    public ShareUtil getShareUtil() {
        if (jsInterfaces == null || jsInterfaces.isEmpty()) {
            return null;
        }
        for (int i = 0, size = jsInterfaces.size(); i < size; i++) {
            WebViewJsInterface jsInterface = jsInterfaces.valueAt(i);
            if (jsInterface.getShareUtil() != null) {
                return jsInterface.getShareUtil();
            }
        }
        return null;
    }

    protected WebBar initWebBar() {
        return null;
    }

    protected void initBottomLayout(ViewGroup bottomLayout) {

    }
}
