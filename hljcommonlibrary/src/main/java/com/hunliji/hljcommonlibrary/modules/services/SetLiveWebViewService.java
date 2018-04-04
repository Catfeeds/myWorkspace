package com.hunliji.hljcommonlibrary.modules.services;

import android.app.Activity;
import android.webkit.WebView;

import com.alibaba.android.arouter.facade.template.IProvider;

/**
 * Created by luohanlin on 2017/7/3.
 * 设置WebView为直播宣传页
 */

public interface SetLiveWebViewService extends IProvider {

    /**
     * 设置WebView为直播宣传页
     *
     * @param activity
     * @param webView  被设置的web view实例
     */
    void setLiveWebView(Activity activity, WebView webView);
}
