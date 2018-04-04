package com.handmark.pulltorefresh.library;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

/**
 * Created by mo_yu on 2016/5/11.
 */
public class InterceptScrollView extends ScrollView {

    private View interceptLayout;
    private ScrollChangeListener scrollViewListener = null;

    public interface ScrollChangeListener {

        void onScrollChange(InterceptScrollView scrollView, int l, int t, int oldl, int oldt);
    }

    public InterceptScrollView(Context context) {
        super(context);
    }

    public InterceptScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InterceptScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setInterceptLayout(View interceptLayout) {
        this.interceptLayout = interceptLayout;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (scrollViewListener != null) {
            scrollViewListener.onScrollChange(this, l, t, oldl, oldt);
        }
    }

    public void setScrollChangeListener(ScrollChangeListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull MotionEvent ev) {
        switch (ev.getAction()) {
            default:
                boolean b = (interceptLayout != null && getScrollY() <= interceptLayout.getHeight
                        () + TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        45,
                        getContext().getResources()
                                .getDisplayMetrics()));
                return super.onInterceptTouchEvent(ev) && b;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            default:
                boolean b = (interceptLayout != null && getScrollY() <= interceptLayout.getHeight
                        () + TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        45,
                        getContext().getResources()
                                .getDisplayMetrics()));
                return super.onTouchEvent(ev) && b;
        }
    }

    @Override
    public void fling(int velocityY) {
        super.fling(velocityY / 300);
    }
}
