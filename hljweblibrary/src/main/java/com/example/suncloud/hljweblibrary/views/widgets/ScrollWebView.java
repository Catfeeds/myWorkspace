package com.example.suncloud.hljweblibrary.views.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 * Created by mo_yu on 2017/7/19.可监听滚动事件的webview
 */

public class ScrollWebView extends WebView {
    public ScrollWebView(Context context) {
        super(context);
    }

    private OnScrollChangedCallback mOnScrollChangedCallback;

    public ScrollWebView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollWebView(
            final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onScrollChanged(
            final int l, final int t, final int oldl, final int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        if (mOnScrollChangedCallback != null) {
            mOnScrollChangedCallback.onScroll(l, t, oldl, oldt);
        }
    }

    public OnScrollChangedCallback getOnScrollChangedCallback() {
        return mOnScrollChangedCallback;
    }

    public void setOnScrollChangedCallback(
            final OnScrollChangedCallback onScrollChangedCallback) {
        mOnScrollChangedCallback = onScrollChangedCallback;
    }

    /**
     * Impliment in the activity/fragment/view that you want to listen to the webview
     */
    public static interface OnScrollChangedCallback {
        public void onScroll(int l, int t, int oldl, int oldt);
    }
}
