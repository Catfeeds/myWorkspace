package com.hunliji.marrybiz.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ScrollView;

import com.hunliji.marrybiz.R;


public class ParallaxScrollView2 extends ScrollView {

    private View mHeardView;
    private int mMaxHeight = -1;
    private int mViewHeight = -1;
    private int mDefaultHeight = 0;
    private int SCROLL_TIMEOUT = 40;
    private GestureDetector mGestureDetector;
    private OnScrollChangedListener onScrollChangedListener;
    private OnOverScaleListener onOverScaleListener;
    public static final int SCROLL_START = 1;
    public static final int SCROLL_END = 0;
    public int scrollState;
    private int lastScrollY;

    private interface OnOverScrollByListener {
        boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX,
                             int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean
                                     isTouchEvent);
    }

    public interface OnOverScaleListener {
        void overScale(float scale);
    }


    public void setOnOverScaleListener(OnOverScaleListener onOverScaleListener) {
        this.onOverScaleListener = onOverScaleListener;
    }

    public interface OnScrollChangedListener {
        void onScrollChanged(int l, int t, int oldl, int oldt);

        void onScrollStateChanged(int scrollState);
    }

    public void setOnScrollChangedListener(OnScrollChangedListener onScrollChangedListener) {
        this.onScrollChangedListener = onScrollChangedListener;
    }

    private final Runnable scrollCheck = new Runnable() {
        public void run() {
            if (getScrollY() != lastScrollY) {
                lastScrollY = getScrollY();
                postDelayed(this, SCROLL_TIMEOUT);
            } else if (onScrollChangedListener != null) {
                scrollState = SCROLL_END;
                onScrollChangedListener.onScrollStateChanged(SCROLL_END);
            }
        }
    };


    void startScroll() {
        if (scrollState != SCROLL_START) {
            scrollState = SCROLL_START;
            onScrollChangedListener.onScrollStateChanged(SCROLL_START);
            postDelayed(scrollCheck, SCROLL_TIMEOUT);
        }
    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        if (onScrollChangedListener != null) {
            onScrollChangedListener.onScrollChanged(l, t, oldl, oldt);
            startScroll();
        }
        super.onScrollChanged(l, t, oldl, oldt);
    }

    private interface OnTouchEventListener {
        void onTouchEvent(MotionEvent ev);
    }

    public ParallaxScrollView2(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public ParallaxScrollView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ParallaxScrollView2(Context context) {
        super(context);
        init(context);
    }

    public void init(Context context) {
        mGestureDetector = new GestureDetector(context, new YScrollDetector());
        mDefaultHeight = context.getResources().getDimensionPixelSize(R.dimen.size_default_height);
    }

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY,
                                   int scrollRangeX, int scrollRangeY, int maxOverScrollX,
                                   int maxOverScrollY, boolean isTouchEvent) {
        if (!(scrollY > 0)) {
            scrollByListener.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX,
                    scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
        }

        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY,
                maxOverScrollX, maxOverScrollY, isTouchEvent);
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        touchListener.onTouchEvent(ev);
        return super.onTouchEvent(ev);
    }

    public void setParallaxImageView(View iv) {
        mHeardView = iv;
    }


    public void setParallaxImageView(View iv, int mDefaultHeight) {
        mHeardView = iv;
        this.mDefaultHeight = mDefaultHeight;
    }

    public void setViewsBounds(double zoomRatio) {
        if (mViewHeight == -1) {
            mViewHeight = mHeardView.getMeasuredHeightAndState();
            if (mViewHeight <= 0) {
                mViewHeight = mDefaultHeight;
            }
            mMaxHeight = (int) (mViewHeight * (zoomRatio > 1 ? zoomRatio : 1));
        }
    }

    private OnOverScrollByListener scrollByListener = new OnOverScrollByListener() {
        @Override
        public boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY,
                                    int scrollRangeX, int scrollRangeY, int maxOverScrollX,
                                    int maxOverScrollY, boolean isTouchEvent) {
            if (mHeardView.getHeight() <= mMaxHeight && isTouchEvent) {
                if (deltaY < 0) {
                    if (mHeardView.getHeight() - deltaY / 2 >= mViewHeight) {
                        int height = mHeardView.getHeight() - deltaY / 2 < mMaxHeight ?
                                mHeardView.getHeight() - deltaY / 2 : mMaxHeight;
                        mHeardView.getLayoutParams().height = height;
                        mHeardView.requestLayout();
                        if (onOverScaleListener != null) {
                            onOverScaleListener.overScale((float) height / mViewHeight);
                        }
                    }
                } else {
                    if (mHeardView.getHeight() > mViewHeight) {
                        mHeardView.getLayoutParams().height = mHeardView.getHeight() - deltaY >
                                mViewHeight ? mHeardView.getHeight() - deltaY : mViewHeight;
                        mHeardView.requestLayout();
                        return true;
                    }
                }
            }
            return false;
        }
    };

    private OnTouchEventListener touchListener = new OnTouchEventListener() {
        @Override
        public void onTouchEvent(MotionEvent ev) {
            if (ev.getAction() == MotionEvent.ACTION_UP) {
                if (mViewHeight - 1 < mHeardView.getHeight()) {
                    ResetAnimimation animation = new ResetAnimimation(mHeardView, mViewHeight);
                    animation.setDuration(300);
                    mHeardView.startAnimation(animation);
                }
            }
        }
    };

    public class ResetAnimimation extends Animation {
        int targetHeight;
        int originalHeight;
        int extraHeight;
        View mView;

        protected ResetAnimimation(View view, int targetHeight) {
            this.mView = view;
            this.targetHeight = targetHeight;
            originalHeight = view.getHeight();
            extraHeight = this.targetHeight - originalHeight;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {

            int newHeight;
            newHeight = (int) (targetHeight - extraHeight * (1 - interpolatedTime));
            mView.getLayoutParams().height = newHeight;
            mView.requestLayout();
            if (onOverScaleListener != null) {
                onOverScaleListener.overScale((float) newHeight / mViewHeight);
            }
        }
    }


    private boolean b;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return super.onInterceptTouchEvent(event) && b;
    }


    class YScrollDetector extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (Math.abs(distanceX) > Math.abs(distanceY)) {
                b = false;
                return b;
            }
            b = true;
            return b;
        }
    }
}
