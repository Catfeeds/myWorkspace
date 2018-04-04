package com.hunliji.hljcommonlibrary.views.widgets;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

/**
 * Created by luohanlin on 2017/8/3.
 */

public class SwipeActivityRootView extends FrameLayout {

    private static final float DRAG_OFFSET = 0.3F;
    private static final int OVER_SCROLL_DISTANCE = 10;

    private View dragView;
    private View shadowView;
    private ViewDragHelper viewDragHelper;
    private int horizontalDragRange = 0;
    private boolean swipeEnable = true;

    public int mEdgeFlag = ViewDragHelper.EDGE_LEFT;
    private float mScrollPercent;

    public SwipeActivityRootView(@NonNull Context context) {
        super(context);
    }

    public SwipeActivityRootView(
            @NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SwipeActivityRootView(
            @NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 必须调用
     *
     * @param dragView
     * @param shadowView
     */
    public void initWithViews(View dragView, View shadowView) {
        this.dragView = dragView;
        this.shadowView = shadowView;

        this.viewDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelperCallBack());
        viewDragHelper.setEdgeTrackingEnabled(mEdgeFlag);
    }

    private void finish() {
        Activity act = (Activity) getContext();
        if (act != null && !act.isFinishing()) {
            hideKeyboard(act);
            act.finish();
            act.overridePendingTransition(0, 0);
        }
    }

    private void hideKeyboard() {
        Activity act = (Activity) getContext();
        if (act != null && !act.isFinishing()) {
            hideKeyboard(act);
        }
    }

    private void hideKeyboard(Activity act) {
        InputMethodManager imm = ((InputMethodManager) act.getSystemService(Activity
                .INPUT_METHOD_SERVICE));
        if (imm != null && act.getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(act.getCurrentFocus()
                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void setSwipeEnable(boolean swipeEnable) {
        this.swipeEnable = swipeEnable;
    }

    public boolean isSwipeEnable() {
        return swipeEnable;
    }

    private class ViewDragHelperCallBack extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            boolean isTouched = viewDragHelper.isEdgeTouched(mEdgeFlag, pointerId);
            if (isTouched && swipeEnable) {
                hideKeyboard();
            }
            return isTouched;
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return mEdgeFlag & (ViewDragHelper.EDGE_LEFT | ViewDragHelper.EDGE_RIGHT);
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            final int leftBound = getPaddingLeft();
            final int rightBound = horizontalDragRange;

            return Math.min(Math.max(left, leftBound), rightBound);
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            mScrollPercent = Math.abs((float) left / horizontalDragRange);
            if (mScrollPercent >= 1) {
                shadowView.setAlpha(0);
                finish();
            } else {
                shadowView.setAlpha(1 - mScrollPercent);
            }
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            final int childWidth = releasedChild.getWidth();
            int left;
            left = xvel > 0 || xvel == 0 && mScrollPercent > DRAG_OFFSET ? childWidth +
                    OVER_SCROLL_DISTANCE : 0;
            viewDragHelper.settleCapturedViewAt(left, 0);
            invalidate();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        horizontalDragRange = w;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!swipeEnable || !isEnabled()) {
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
        if (!swipeEnable) {
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


    private int[] mInsets = new int[4];

    @Override
    protected final boolean fitSystemWindows(Rect insets) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // Intentionally do not modify the bottom inset. For some reason,
            // if the bottom inset is modified, window resizing stops working.
            mInsets[0] = insets.left;
            mInsets[1] = insets.top;
            mInsets[2] = insets.right;

            insets.left = 0;
            insets.top = 0;
            insets.right = 0;
        }

        return super.fitSystemWindows(insets);
    }
}
