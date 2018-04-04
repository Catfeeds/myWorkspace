package com.example.suncloud.hljweblibrary.jsinterface;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.webkit.WebView;

import com.hunliji.hljsharelibrary.utils.ShareUtil;

/**
 * Created by wangtao on 2017/1/20.
 */

public class HljWebJsInterface implements WebViewJsInterface {

    private BaseWebHandler webHandler;
    public static final String NAME = "handler";

    @SuppressLint("AddJavascriptInterface")
    public HljWebJsInterface(WebView webView, BaseWebHandler handler) {
        if (handler != null) {
            this.webHandler = handler;
            webView.addJavascriptInterface(webHandler, NAME);
        }
    }

    @Override
    public void getShareInfo(WebView webView) {
        if (webHandler != null) {
            webView.loadUrl("javascript:window." + NAME + ".getShareInfo(getShareData());");
        }
    }

    @Override
    public void checkShareInfo(WebView webView) {
        if (webHandler != null) {
            webView.loadUrl("javascript:window." + NAME + ".checkShareInfo(getShareData())" + ""
                    + ";");
        }
    }

    @Override
    public boolean goBack(WebView webView) {
        if (webHandler != null && webHandler.isCanBack()) {
            webView.loadUrl("javascript:goBack();");
            return true;
        }
        return false;
    }

    @Override
    public ShareUtil getShareUtil() {
        if (webHandler != null) {
            return webHandler.getShareUtil();
        }
        return null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (webHandler != null) {
            webHandler.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRemove(WebView webView) {
        webView.removeJavascriptInterface(NAME);
    }

    @Override
    public void onDestroy() {
        if (webHandler != null) {
            webHandler.onDestroy();
        }
    }
}
