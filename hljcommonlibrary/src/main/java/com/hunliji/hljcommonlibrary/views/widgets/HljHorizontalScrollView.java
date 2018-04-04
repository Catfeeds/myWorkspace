package com.hunliji.hljcommonlibrary.views.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;

/**
 * Created by wangtao on 2017/12/18.
 */

public class HljHorizontalScrollView extends HorizontalScrollView {

    private OnMyScrollChangeListener scrollChangeListener;

    public HljHorizontalScrollView(Context context) {
        super(context);
    }

    public HljHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HljHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if(scrollChangeListener!=null) {
            scrollChangeListener.onScrollChange(l, t, oldl, oldt);
        };
    }

    public void setOnMyScrollChangeListener(OnMyScrollChangeListener l) {
        this.scrollChangeListener = l;
    }

    public interface OnMyScrollChangeListener {
        public void onScrollChange(int l, int t, int oldl, int oldt);
    }
}
