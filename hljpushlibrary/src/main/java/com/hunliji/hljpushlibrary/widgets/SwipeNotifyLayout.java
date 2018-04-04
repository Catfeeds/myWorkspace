package com.hunliji.hljpushlibrary.widgets;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.hunliji.hljpushlibrary.R;

/**
 * Created by wangtao on 2017/12/6.
 */

public class SwipeNotifyLayout extends FrameLayout {

    private static final float DRAG_OFFSET = 0.3F;
    private static final int OVER_SCROLL_DISTANCE = 10;

    private ViewDragHelper viewDragHelper;
    private int verticalDragRange = 0;
    private boolean swipeEnable = true;


    public int mEdgeFlag = ViewDragHelper.EDGE_BOTTOM;
    private float mScrollPercent;

    private SwipeListener listener;

    public SwipeNotifyLayout(Context context) {
        this(context,null);
    }

    public SwipeNotifyLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SwipeNotifyLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.viewDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelperCallBack());
        viewDragHelper.setEdgeTrackingEnabled(mEdgeFlag);

    }

    public void setListener(SwipeListener listener) {
        this.listener = listener;
    }

    public void setSwipeEnable(boolean swipeEnable) {
        this.swipeEnable = swipeEnable;
    }

    private void finish() {
        if(listener!=null){
            listener.onFinish();
        }
    }


    private class ViewDragHelperCallBack extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return true;
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return mEdgeFlag & (ViewDragHelper.EDGE_TOP | ViewDragHelper.EDGE_BOTTOM);
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            final int bottomBound = 0;
            final int topBound = getPaddingTop()-verticalDragRange;
            return Math.min(Math.max(top,topBound),bottomBound);
        }


        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            mScrollPercent = Math.abs((float) top / verticalDragRange);
            if (mScrollPercent >= 1) {
                finish();
            }
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            final int childHeight = releasedChild.getHeight();
            int top;
            top = yvel < 0 || yvel == 0 && mScrollPercent > DRAG_OFFSET ? childHeight +
                    OVER_SCROLL_DISTANCE : 0;
            viewDragHelper.settleCapturedViewAt(0, -top);
            invalidate();
        }
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        verticalDragRange = h;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!isEnabled()||!swipeEnable) {
            return false;
        }
        try {
            return viewDragHelper.shouldInterceptTouchEvent(ev);
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!isEnabled()||!swipeEnable) {
            return false;
        }
        viewDragHelper.processTouchEvent(ev);
        return true;
    }

    @Override
    public void computeScroll() {
        if (!isInEditMode() && viewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public interface SwipeListener {

        public void onScrollStateChange(int state, float scrollPercent);

        public void onFinish();
    }
}
