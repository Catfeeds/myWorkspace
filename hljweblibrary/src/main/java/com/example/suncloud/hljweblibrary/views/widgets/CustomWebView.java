package com.example.suncloud.hljweblibrary.views.widgets;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.example.suncloud.hljweblibrary.client.HljWebClient;
import com.example.suncloud.hljweblibrary.constructors.JsInterfaceConstructor;
import com.example.suncloud.hljweblibrary.jsinterface.BaseWebHandler;


/**
 * Created by werther on 16/7/11.
 */
public class CustomWebView extends WebView {
    private Context mContext;
    private float currentHeight;

    public CustomWebView(Context context) {
        super(context);
        init(context);
    }

    public CustomWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @SuppressLint({"AddJavascriptInterface", "SetJavaScriptEnabled"})
    private void init(Context context) {
        mContext = context;
        setFocusable(false);
        setHorizontalScrollBarEnabled(false);
        setVerticalScrollBarEnabled(false);
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return (event.getAction() == MotionEvent.ACTION_MOVE);
            }
        });
        setScrollContainer(false);
        getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        getSettings().setJavaScriptEnabled(true);
        getSettings().setAllowFileAccess(true);
        //        wenbview缓存
        getSettings().setDomStorageEnabled(true);
        getSettings().setAppCachePath(getContext().getCacheDir()
                .getAbsolutePath());
        getSettings().setAppCacheEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        setWebViewClient(new HljWebClient(context) {
            @Override
            public void onPageFinished(WebView view, String url) {
                loadUrl("javascript:sizehandler.resize(document.body.scrollHeight)");
                super.onPageFinished(view, url);
            }
        });

        addJavascriptInterface(this, "sizehandler");
        Handler handler = new Handler();
        BaseWebHandler webHandler = JsInterfaceConstructor.getJsInterface(mContext,
                getUrl(),
                this,
                handler);
        //js调用的函数接口
        if (webHandler != null) {
            addJavascriptInterface(webHandler, "handler");
        }
    }

    @JavascriptInterface
    public void resize(float height) {
        if (mContext instanceof Activity && this.currentHeight != height) {
            Activity activity = (Activity) mContext;
            this.currentHeight = height;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setLayoutParams(new LinearLayout.LayoutParams(getResources()
                            .getDisplayMetrics().widthPixels,
                            (int) (currentHeight * getResources().getDisplayMetrics().density)));
                }
            });
        }
    }
}
