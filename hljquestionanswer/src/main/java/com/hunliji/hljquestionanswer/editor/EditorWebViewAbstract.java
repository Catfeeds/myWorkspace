package com.hunliji.hljquestionanswer.editor;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.ConsoleMessage.MessageLevel;
import android.webkit.JsResult;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * A text editor WebView with support for JavaScript execution.
 */
public abstract class EditorWebViewAbstract extends WebView {
    public abstract void execJavaScriptFromString(String javaScript);

    private AuthHeaderRequestListener mAuthHeaderRequestListener;
    private ErrorListener mErrorListener;
    private JsCallbackReceiver mJsCallbackReceiver;
    private boolean mDebugModeEnabled;

    private Map<String, String> mHeaderMap = new HashMap<>();

    public EditorWebViewAbstract(Context context, AttributeSet attrs) {
        super(context, attrs);
        configureWebView();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void configureWebView() {
        WebSettings webSettings = this.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDefaultTextEncodingName("utf-8");

        this.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url != null && url.startsWith("callback") && mJsCallbackReceiver != null) {
                    String data = URLDecoder.decode(url);
                    String[] split = data.split(":", 2);
                    String callbackId = split[0];
                    String params = (split.length > 1 ? split[1] : "");
                    mJsCallbackReceiver.executeCallback(callbackId, params);
                }
                return true;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.e("EditorWebViewAbstract", description);
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();

                if (!URLUtil.isNetworkUrl(url)) {
                    return super.shouldInterceptRequest(view, request);
                }

                // Request and add an authorization header for HTTPS resource requests.
                // Use https:// when requesting the auth header, in case the resource is incorrectly using http://.
                // If an auth header is returned, force https:// for the actual HTTP request.
//                String authHeader = mAuthHeaderRequestListener.onAuthHeaderRequested(UrlUtils.makeHttps(url));
//                if (StringUtils.notNullStr(authHeader).length() > 0) {
//                    try {
//                        url = UrlUtils.makeHttps(url);
//
//                        // Keep any existing request headers from the WebResourceRequest
//                        Map<String, String> headerMap = request.getRequestHeaders();
//                        for (Map.Entry<String, String> entry : mHeaderMap.entrySet()) {
//                            headerMap.put(entry.getKey(), entry.getValue());
//                        }
//                        headerMap.put("Authorization", authHeader);
//
//                        HttpURLConnection conn = HTTPUtils.setupUrlConnection(url, headerMap);
//                        return new WebResourceResponse(conn.getContentType(), conn.getContentEncoding(),
//                                conn.getInputStream());
//                    } catch (IOException e) {
//                        AppLog.e(T.EDITOR, e);
//                    }
//                }

                return super.shouldInterceptRequest(view, request);
            }

            /**
             * Compatibility method for API < 21
             */
            @SuppressWarnings("deprecation")
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                if (!URLUtil.isNetworkUrl(url)) {
                    return super.shouldInterceptRequest(view, url);
                }

                // Request and add an authorization header for HTTPS resource requests.
                // Use https:// when requesting the auth header, in case the resource is incorrectly using http://.
                // If an auth header is returned, force https:// for the actual HTTP request.
//                String authHeader = mAuthHeaderRequestListener.onAuthHeaderRequested(UrlUtils.makeHttps(url));
//                if (StringUtils.notNullStr(authHeader).length() > 0) {
//                    try {
//                        url = UrlUtils.makeHttps(url);
//
//                        Map<String, String> headerMap = new HashMap<>(mHeaderMap);
//                        headerMap.put("Authorization", authHeader);
//
//                        HttpURLConnection conn = HTTPUtils.setupUrlConnection(url, headerMap);
//                        return new WebResourceResponse(conn.getContentType(), conn.getContentEncoding(),
//                                conn.getInputStream());
//                    } catch (IOException e) {
//                        AppLog.e(T.EDITOR, e);
//                    }
//                }

                return super.shouldInterceptRequest(view, url);
            }
        });

        this.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(@NonNull ConsoleMessage cm) {
                if (cm.messageLevel() == MessageLevel.ERROR) {
                    if (mErrorListener != null) {
                        mErrorListener.onJavaScriptError(cm.sourceId(), cm.lineNumber(), cm.message());
                    }
                    Log.e("EditorWebViewAbstract", cm.message() + " -- From line " + cm.lineNumber() + " of " + cm.sourceId());
                } else {
                    Log.d("EditorWebViewAbstract", cm.message() + " -- From line " + cm.lineNumber() + " of " + cm.sourceId());
                }
                return true;
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                Log.d("EditorWebViewAbstract", message);
                if (mErrorListener != null) {
                    mErrorListener.onJavaScriptAlert(url, message);
                }
                return true;
            }
        });
    }

    @Override
    public boolean onCheckIsTextEditor() {
        return true;
    }

    @Override
    public void setVisibility(int visibility) {
        notifyVisibilityChanged(visibility == View.VISIBLE);
        super.setVisibility(visibility);
    }


    public boolean shouldSwitchToCompatibilityMode() {
        return false;
    }

    public void setDebugModeEnabled(boolean enabled) {
        mDebugModeEnabled = enabled;
    }

    /**
     * Handles events that should be triggered when the WebView is hidden or is shown to the user
     *
     * @param visible the new visibility status of the WebView
     */
    public void notifyVisibilityChanged(boolean visible) {
        if (!visible) {
            this.post(new Runnable() {
                @Override
                public void run() {
                    execJavaScriptFromString("ZSSEditor.pauseAllVideos();");
                }
            });
        }
    }

    public void setAuthHeaderRequestListener(AuthHeaderRequestListener listener) {
        mAuthHeaderRequestListener = listener;
    }

    /**
     * Used on API<17 to handle callbacks as a safe alternative to JavascriptInterface (which has security risks
     * at those API levels).
     */
    public void setJsCallbackReceiver(JsCallbackReceiver jsCallbackReceiver) {
        mJsCallbackReceiver = jsCallbackReceiver;
    }

    public void setCustomHeader(String name, String value) {
        mHeaderMap.put(name, value);
    }

    public void setErrorListener(ErrorListener errorListener) {
        mErrorListener = errorListener;
    }

    public interface AuthHeaderRequestListener {
        String onAuthHeaderRequested(String url);
    }

    public interface ErrorListener {
        void onJavaScriptError(String sourceFile, int lineNumber, String message);
        void onJavaScriptAlert(String url, String message);
    }
}
