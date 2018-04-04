package com.hunliji.marrybiz.util.modules;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.webkit.WebView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.example.suncloud.hljweblibrary.constructors.JsInterfaceConstructor;
import com.example.suncloud.hljweblibrary.jsinterface.BaseWebHandler;
import com.example.suncloud.hljweblibrary.utils.WebUtil;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.modules.services.SetLiveWebViewService;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.task.OnHttpRequestListener;
import com.hunliji.marrybiz.util.LinkUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by luohanlin on 2017/7/3.
 * 设置webview为直播宣传页面的实现
 */
@Route(path = RouterPath.ServicePath.SET_LIVE_WEB_VIEW)
public class SetLiveWebViewImpl implements SetLiveWebViewService {

    @Override
    public void setLiveWebView(final Activity activity, final WebView webView) {
        LinkUtil.getInstance(activity)
                .getLink(Constants.LinkNames.LIVE_CHAT_APPLY, new OnHttpRequestListener() {
                    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
                    @Override
                    public void onRequestCompleted(Object obj) {
                        String path = (String) obj;
                        Map<String, String> header = new HashMap<>();
                        path = WebUtil.addPathQuery(activity, path);
                        if (Uri.parse(path)
                                .getHost() != null && (Uri.parse(path)
                                .getHost()
                                .contains("hunliji") || HljHttp.getHOST()
                                .contains(Uri.parse(path)
                                        .getHost()))) {
                            header = WebUtil.getWebHeaders(activity);
                            BaseWebHandler webHandler = JsInterfaceConstructor.getJsInterface(
                                    activity,
                                    path,
                                    webView,
                                    null);
                            //js调用的函数接口
                            if (webHandler != null) {
                                webView.addJavascriptInterface(webHandler, "handler");
                            }
                        }
                        webView.getSettings()
                                .setJavaScriptEnabled(true);
                        webView.getSettings()
                                .setAllowFileAccess(true);
                        //        wenbview缓存
                        webView.getSettings()
                                .setDomStorageEnabled(true);
                        webView.getSettings()
                                .setAppCachePath(webView.getContext()
                                        .getCacheDir()
                                        .getAbsolutePath());
                        webView.getSettings()
                                .setAppCacheEnabled(true);
                        if (!header.isEmpty()) {
                            webView.loadUrl(path, header);
                        } else {
                            webView.loadUrl(path);
                        }
                    }

                    @Override
                    public void onRequestFailed(Object obj) {
                    }
                });
    }

    @Override
    public void init(Context context) {

    }
}
