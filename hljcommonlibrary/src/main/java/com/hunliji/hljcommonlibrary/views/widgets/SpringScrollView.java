package com.hunliji.hljcommonlibrary.views.widgets;

/**
 * Created by wangtao on 2018/3/23.
 */

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.Scroller;

public class SpringScrollView extends FrameLayout {
    static final float MAX_SCROLL_FACTOR = 0.7f;

    private Scroller mScroller;
    private float damp = 1.5f;

    /**
     * Position of the last motion event.
     */
    private float mLastMotionY;
    private float mLastMotionX;


    /**
     * True if the user is currently dragging this ScrollView around. This is
     * not the same as 'is being flinged', which can be checked by
     * mScroller.isFinished() (flinging begins when the user lifts his finger).
     */
    private boolean mIsBeingDragged = false;

    /**
     * Determines speed during touch scrolling
     */
    private VelocityTracker mVelocityTracker;


    private int mTouchSlop;
    private int mMinimumVelocity;
    private int mMaximumVelocity;

    /**
     * ID of the active pointer. This is used to retain consistency during
     * drags/flings if multiple pointers are used.
     */
    private int mActivePointerId = INVALID_POINTER;

    /**
     * Sentinel value for no current active pointer.
     * Used by {@link #mActivePointerId}.
     */
    private static final int INVALID_POINTER = -1;

    private boolean mFlingEnabled = true;

    private boolean isNeedAnimation = true;

    private enum ScrollOrientation {
        NONE, HORIZONTAL, VERTICAL
    }

    private ScrollOrientation orientation = ScrollOrientation.HORIZONTAL;

    private SpringListener springListener;

    public void setSpringListener(SpringListener springListener) {
        this.springListener = springListener;
    }

    private Runnable resetRunnable = new Runnable() {
        @Override
        public void run() {
            if (!normal.isEmpty()) {
                inner.clearAnimation();
                inner.layout(normal.left, normal.top, normal.right, normal.bottom);
                normal.setEmpty();
            }
        }
    };

    public SpringScrollView(Context context) {
        this(context, null);
    }

    public SpringScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initScrollView();
    }

    public int getMaxScrollAmountV() {
        return (int) (MAX_SCROLL_FACTOR * (getBottom() - getTop()));
    }

    public int getMaxScrollAmountH() {
        return (int) (MAX_SCROLL_FACTOR * (getRight() - getLeft()));
    }

    private void initScrollView() {
        mScroller = new Scroller(getContext());
        final ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mTouchSlop = configuration.getScaledTouchSlop();
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
    }

    private boolean inChild(int x, int y) {
        if (getChildCount() > 0) {
            final int scrollX = getScrollX();
            final int scrollY = getScrollY();
            final View child = getChildAt(0);
            return !(y < child.getTop() - scrollY || y >= child.getBottom() - scrollY || x <
                    child.getLeft() - scrollX || x >= child.getRight() - scrollX);
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (orientation == ScrollOrientation.NONE) {
            return super.onInterceptTouchEvent(ev);
        }
        final int action = ev.getAction();
        if ((action == MotionEvent.ACTION_MOVE) && (mIsBeingDragged)) {
            return true;
        }

        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_MOVE: {
                final int activePointerId = mActivePointerId;
                if (activePointerId == INVALID_POINTER) {
                    break;
                }

                final int pointerIndex = ev.findPointerIndex(activePointerId);
                if (orientation == ScrollOrientation.VERTICAL) {
                    final float y = ev.getY(pointerIndex);
                    final int yDiff = (int) Math.abs(y - mLastMotionY);
                    if (yDiff > mTouchSlop) {
                        mIsBeingDragged = true;
                        mLastMotionY = y;
                    }
                } else if (orientation == ScrollOrientation.HORIZONTAL) {
                    final float x = ev.getX(pointerIndex);
                    final int xDiff = (int) Math.abs(x - mLastMotionX);
                    if (xDiff > mTouchSlop) {
                        mIsBeingDragged = true;
                        mLastMotionX = x;
                    }
                }
                break;
            }

            case MotionEvent.ACTION_DOWN: {
                mIsBeingDragged = false;
                final float x = ev.getX();
                final float y = ev.getY();
                if (!inChild((int) x, (int) y)) {
                    break;
                }

                mLastMotionY = y;
                mLastMotionX = x;
                mActivePointerId = ev.getPointerId(0);
                break;
            }

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                /* Release the drag */
                mIsBeingDragged = false;
                mActivePointerId = INVALID_POINTER;
                break;
        }

        /*
         * The only time we want to intercept motion events is if we are in the
         * drag mode.
         */
        return mIsBeingDragged;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        if (orientation == ScrollOrientation.NONE) {
            return super.onTouchEvent(ev);
        }

        if (ev.getAction() == MotionEvent.ACTION_DOWN && ev.getEdgeFlags() != 0) {
            // Don't handle edge touches immediately -- they may actually belong
            // to one of our
            // descendants.
            return false;
        }

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);

        final int action = ev.getAction();

        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                final float x = ev.getX();
                final float y = ev.getY();
                if (!inChild((int) x, (int) y)) {
                    mIsBeingDragged = false;
                    return false;
                }

                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }

                mLastMotionY = y;
                mLastMotionX = x;
                mActivePointerId = ev.getPointerId(0);
                break;
            }
            case MotionEvent.ACTION_MOVE:
                if (mIsBeingDragged) {
                    // Scroll to follow the motion event
                    final int activePointerIndex = ev.findPointerIndex(mActivePointerId);
                    if (activePointerIndex == INVALID_POINTER) {
                        break;
                    }
                    final float y = ev.getY(activePointerIndex);
                    final int deltaY = (int) (mLastMotionY - y);
                    mLastMotionY = y;

                    final float x = ev.getX(activePointerIndex);
                    final int deltaX = (int) (mLastMotionX - x);
                    mLastMotionX = x;
                    // 全方向滚动
                    scrollBy(deltaX, deltaY);
                    // 当滚动到边界时就不会再滚动，这时移动布局
                    if (isNeedMove()) {
                        removeCallbacks(resetRunnable);
                        if (normal.isEmpty()) {
                            // 保存正常的布局属性
                            normal.set(inner.getLeft(),
                                    inner.getTop(),
                                    inner.getRight(),
                                    inner.getBottom());
                        }
                        // 移动布局
                        if (orientation == ScrollOrientation.HORIZONTAL) {
                            int max = getMaxScrollAmountH();
                            int dx = Math.abs(getOverScrollX());
                            int newDeltaX = (int) ((deltaX * Math.max(max - dx, 0) / max) / damp);
                            inner.layout(inner.getLeft() - newDeltaX,
                                    inner.getTop(),
                                    inner.getRight() - newDeltaX,
                                    inner.getBottom());
                        } else if (orientation == ScrollOrientation.VERTICAL) {
                            int max = getMaxScrollAmountV();
                            int dy = Math.abs(getOverScrollY());
                            int newDeltaY = (int) ((deltaY * Math.max(max - dy, 0) / max) / damp);
                            inner.layout(inner.getLeft(),
                                    inner.getTop() - newDeltaY,
                                    inner.getRight(),
                                    inner.getBottom() - newDeltaY);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mIsBeingDragged) {
                    if (mFlingEnabled) {
                        final VelocityTracker velocityTracker = mVelocityTracker;
                        velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                        int initialVelocitx = (int) velocityTracker.getXVelocity(mActivePointerId);
                        int initialVelocity = (int) velocityTracker.getYVelocity(mActivePointerId);

                        if (getChildCount() > 0) {
                            if (Math.abs(initialVelocitx) > initialVelocitx || Math.abs(
                                    initialVelocity) > mMinimumVelocity) {
                                fling(-initialVelocitx, -initialVelocity);
                            }

                        }
                    }
                    if (springListener != null && inner != null) {
                        if (orientation == ScrollOrientation.HORIZONTAL) {
                            springListener.onStartSpring(getOverScrollX(), getMaxScrollAmountH());
                        } else if (orientation == ScrollOrientation.VERTICAL) {
                            springListener.onStartSpring(getOverScrollY(), getMaxScrollAmountV());
                        }
                    }
                    if (isNeedReset()) {
                        reset();
                    }
                    mActivePointerId = INVALID_POINTER;
                    mIsBeingDragged = false;

                    if (mVelocityTracker != null) {
                        mVelocityTracker.recycle();
                        mVelocityTracker = null;
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                if (mIsBeingDragged && getChildCount() > 0) {
                    mActivePointerId = INVALID_POINTER;
                    mIsBeingDragged = false;
                    if (mVelocityTracker != null) {
                        mVelocityTracker.recycle();
                        mVelocityTracker = null;
                    }
                }
                break;
        }
        return true;
    }


    private int getOverScrollX() {
        if (inner != null && normal != null) {
            return inner.getLeft() - normal.left;
        }
        return 0;
    }

    private int getOverScrollY() {
        if (inner != null && normal != null) {
            return inner.getTop() - normal.top;
        }
        return 0;
    }

    /**
     * <p>The scroll range of a scroll view is the overall height of all of its
     * children.</p>
     */
    @Override
    protected int computeVerticalScrollRange() {
        final int count = getChildCount();
        final int contentHeight = getHeight() - getPaddingBottom() - getPaddingTop();
        if (count == 0) {
            return contentHeight;
        }

        return getChildAt(0).getBottom();
    }

    @Override
    protected int computeHorizontalScrollRange() {
        final int count = getChildCount();
        final int contentWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        if (count == 0) {
            return contentWidth;
        }

        return getChildAt(0).getRight();
    }

    @Override
    protected int computeVerticalScrollOffset() {
        return Math.max(0, super.computeVerticalScrollOffset());
    }

    @Override
    protected int computeHorizontalScrollOffset() {
        return Math.max(0, super.computeHorizontalScrollOffset());
    }

    @Override
    protected void measureChild(
            View child, int parentWidthMeasureSpec, int parentHeightMeasureSpec) {
        int childWidthMeasureSpec;
        int childHeightMeasureSpec;

        childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);

        childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);

        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    @Override
    protected void measureChildWithMargins(
            View child,
            int parentWidthMeasureSpec,
            int widthUsed,
            int parentHeightMeasureSpec,
            int heightUsed) {
        final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

        final int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(lp.leftMargin + lp
                        .rightMargin,
                MeasureSpec.UNSPECIFIED);
        final int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(lp.topMargin + lp
                        .bottomMargin,
                MeasureSpec.UNSPECIFIED);

        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            int x = mScroller.getCurrX();
            int y = mScroller.getCurrY();

            if (getChildCount() > 0) {
                View child = getChildAt(0);
                x = clamp(x, getWidth() - getPaddingRight() - getPaddingLeft(), child.getWidth());
                y = clamp(y, getHeight() - getPaddingBottom() - getPaddingTop(), child.getHeight());
                super.scrollTo(x, y);
            }
            awakenScrollBars();
            postInvalidate();
        }
    }

    /**
     * Fling the scroll view
     *
     * @param velocityY The initial velocity in the Y direction. Positive
     *                  numbers mean that the finger/cursor is moving down the screen,
     *                  which means we want to scroll towards the top.
     */
    public void fling(int velocityX, int velocityY) {
        if (getChildCount() > 0) {
            int width = getWidth() - getPaddingRight() - getPaddingLeft();
            int right = getChildAt(0).getWidth();

            int height = getHeight() - getPaddingBottom() - getPaddingTop();
            int bottom = getChildAt(0).getHeight();
            mScroller.fling(getScrollX(),
                    getScrollY(),
                    velocityX,
                    velocityY,
                    0,
                    Math.max(0, right - width),
                    0,
                    Math.max(0, bottom - height));
            invalidate();
        }
    }

    @Override
    public void scrollTo(int x, int y) {
        // we rely on the fact the View.scrollBy calls scrollTo.
        if (getChildCount() > 0) {
            View child = getChildAt(0);
            x = clamp(x, getWidth() - getPaddingRight() - getPaddingLeft(), child.getWidth());
            y = clamp(y, getHeight() - getPaddingBottom() - getPaddingTop(), child.getHeight());
            if (x != getScrollX() || y != getScrollY()) {
                super.scrollTo(x, y);
            }
        }
    }

    private int clamp(int n, int my, int child) {
        if (my >= child || n < 0) {
            return 0;
        }
        if ((my + n) > child) {
            return child - my;
        }
        return n;
    }

    /**
     * scrollview内容与属性记录
     */
    private View inner;
    private Rect normal = new Rect();

    @Override
    protected void onFinishInflate() {
        if (getChildCount() > 0) {
            inner = getChildAt(0);
        }
        super.onFinishInflate();
    }

    // 是否需要开启动画
    public boolean isNeedReset() {
        return !normal.isEmpty();
    }

    // 开启动画移动
    public void reset() {
        if (inner == null) {
            return;
        }
        // 开启移动动画
        if (isNeedAnimation) {
            TranslateAnimation ta = new TranslateAnimation(0, -inner.getLeft(), 0, -inner.getTop());
            ta.setDuration(200);
            inner.startAnimation(ta);
        }
        // 设置回到正常的布局位置
        postDelayed(resetRunnable, 200);

    }

    // 是否需要移动布局
    public boolean isNeedMove() {
        if (orientation == ScrollOrientation.HORIZONTAL) {
            int offsetX = inner.getMeasuredWidth() - getWidth();
            int scrollX = getScrollX();
            if (scrollX == 0 || scrollX == offsetX) {
                return true;
            }
        }

        if (orientation == ScrollOrientation.VERTICAL) {
            int offsetY = inner.getMeasuredHeight() - getHeight();
            int scrollY = getScrollY();
            if (scrollY == 0 || scrollY == offsetY) {
                return true;
            }
        }
        return false;
    }

    public void setNeedAnimation(boolean needAnimation) {
        isNeedAnimation = needAnimation;
    }

    public interface SpringListener {
        void onStartSpring(int distance, int max);
    }
}
