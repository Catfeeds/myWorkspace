package com.hunliji.hljcommonlibrary.views.widgets;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;

/**
 * Created by jinxin on 2018/3/27 0027.
 */

public class HljNestedScrollView extends NestedScrollView {

    private HljOnScrollChangeListener hljOnScrollChangeListener;

    public HljNestedScrollView(@NonNull Context context) {
        super(context);
    }

    public HljNestedScrollView(
            @NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public HljNestedScrollView(
            @NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setHljOnScrollChangeListener(HljOnScrollChangeListener hljOnScrollChangeListener) {
        this.hljOnScrollChangeListener = hljOnScrollChangeListener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if(hljOnScrollChangeListener != null){
            hljOnScrollChangeListener.onScrollChange(this,l,t,oldl,oldt);
        }
    }

    public interface HljOnScrollChangeListener {
        /**
         * Called when the scroll position of a view changes.
         *
         * @param v The view whose scroll position has changed.
         * @param scrollX Current horizontal scroll origin.
         * @param scrollY Current vertical scroll origin.
         * @param oldScrollX Previous horizontal scroll origin.
         * @param oldScrollY Previous vertical scroll origin.
         */
        void onScrollChange(NestedScrollView v, int scrollX, int scrollY,
                            int oldScrollX, int oldScrollY);
    }
}
