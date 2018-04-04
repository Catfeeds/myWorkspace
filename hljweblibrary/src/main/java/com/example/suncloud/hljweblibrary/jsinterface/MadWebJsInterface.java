package com.example.suncloud.hljweblibrary.jsinterface;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.webkit.WebView;

import com.hunliji.hljsharelibrary.utils.ShareUtil;

/**
 * Created by wangtao on 2017/1/20.
 */

public class MadWebJsInterface implements WebViewJsInterface {

    private MadWebHandler madHandler;
    public static final String NAME="madHandler";

    @SuppressLint("AddJavascriptInterface")
    public MadWebJsInterface(
            WebView webView, MadWebHandler handler) {
        if (handler != null) {
            madHandler = handler;
            webView.addJavascriptInterface(madHandler, NAME);
        }
    }

    @Override
    public void getShareInfo(WebView webView) {
        if (madHandler != null) {
            webView.loadUrl("javascript:window."+NAME+".getShareInfo(getShareData());");
        }
    }

    @Override
    public void checkShareInfo(WebView webView) {
        if (madHandler != null) {
            webView.loadUrl("javascript:window."+NAME+".checkShareInfo(getShareData())" +
                    "" + ";");
        }
    }

    @Override
    public boolean goBack(WebView webView) {
        return false;
    }

    @Override
    public ShareUtil getShareUtil() {
        if (madHandler != null) {
            return madHandler.getShareUtil();
        }
        return null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void onRemove(WebView webView) {
        webView.removeJavascriptInterface(NAME);
    }

    @Override
    public void onDestroy() {

    }
}
