package com.example.suncloud.hljweblibrary.client;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.suncloud.hljweblibrary.utils.JsUtil;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Suncloud on 2016/8/20.
 */
public abstract class HljWebClient extends WebViewClient {

    private Context context;

    public HljWebClient(Context context){
        this.context=context;
    }

    @Override
    public void onReceivedSslError(
            WebView view, SslErrorHandler handler, SslError error) {
        handler.proceed();
        super.onReceivedSslError(view, handler, error);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest
            request) {
        String path = request.getUrl().getPath();
        String host=request.getUrl().getHost();
        if(TextUtils.isEmpty(path)||TextUtils.isEmpty(host)||!host.contains("hunliji")){
            return null;
        }
        InputStream localCopy = JsUtil.getInstance().getJs(path,context);
        if (localCopy == null) {
            return null;
        }
        return new WebResourceResponse("text/javascript", "UTF-8", localCopy);
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        String path = url;
        String host =url;
        try {
            URL url1 = new URL(url);
            if (!TextUtils.isEmpty(url1.getPath())) {
                path = url1.getPath();
                host = url1.getHost();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if(TextUtils.isEmpty(path)||TextUtils.isEmpty(host)||!host.contains("hunliji")){
            return null;
        }
        InputStream localCopy = JsUtil.getInstance().getJs(path,context);
        if (localCopy == null) {
            return null;
        }
        return new WebResourceResponse("text/javascript", "UTF-8", localCopy);
    }

}
