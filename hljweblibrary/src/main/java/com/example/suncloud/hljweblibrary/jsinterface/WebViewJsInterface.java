package com.example.suncloud.hljweblibrary.jsinterface;

import android.content.Intent;
import android.webkit.WebView;

import com.hunliji.hljsharelibrary.utils.ShareUtil;

/**
 * 网页activity中使用到的 webHandler相关接口
 * Created by wangtao on 2017/1/19.
 */

public interface WebViewJsInterface {

    void getShareInfo(WebView webView);

    void checkShareInfo(WebView webView);

    boolean goBack(WebView webView);

    ShareUtil getShareUtil();

    void onActivityResult(int requestCode, int resultCode, Intent data);

    void onRemove(WebView webView);

    void onDestroy();
}
