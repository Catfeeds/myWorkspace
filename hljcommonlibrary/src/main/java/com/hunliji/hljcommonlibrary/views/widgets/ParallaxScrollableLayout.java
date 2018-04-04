package com.hunliji.hljcommonlibrary.views.widgets;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;

/**
 * Created by Suncloud on 2016/7/9.
 */
public class ParallaxScrollableLayout extends ScrollableLayout {

    public final static double NO_ZOOM = 1;
    public final static double ZOOM_X2 = 2;

    private View mParallaxView;
    private int mDrawableMaxHeight = -1;
    private int mImageViewHeight = -1;
    private int mDefaultImageViewHeight = 0;

    private interface OnOverScrollByListener {
        boolean overScrollBy(
                int deltaX,
                int deltaY,
                int scrollX,
                int scrollY,
                int scrollRangeX,
                int scrollRangeY,
                int maxOverScrollX,
                int maxOverScrollY,
                boolean isTouchEvent);
    }

    private interface OnTouchEventListener {
        void onTouchEvent(MotionEvent ev);
    }

    public ParallaxScrollableLayout(Context context) {
        super(context);
        init(context);
    }

    public ParallaxScrollableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ParallaxScrollableLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context mContext) {
        Point point = CommonUtil.getDeviceSize(mContext);
        mDefaultImageViewHeight = Math.round(point.x * 9 / 16);
    }

    @Override
    protected boolean overScrollBy(
            int deltaX,
            int deltaY,
            int scrollX,
            int scrollY,
            int scrollRangeX,
            int scrollRangeY,
            int maxOverScrollX,
            int maxOverScrollY,
            boolean isTouchEvent) {
        boolean isCollapseAnimation = scrollByListener.overScrollBy(deltaX,
                deltaY,
                scrollX,
                scrollY,
                scrollRangeX,
                scrollRangeY,
                maxOverScrollX,
                maxOverScrollY,
                isTouchEvent);

        return isCollapseAnimation || super.overScrollBy(deltaX,
                deltaY,
                scrollX,
                scrollY,
                scrollRangeX,
                scrollRangeY,
                maxOverScrollX,
                maxOverScrollY,
                isTouchEvent);
    }

    //    @Override
    //    public boolean onTouchEvent(MotionEvent ev) {
    //        touchListener.onTouchEvent(ev);
    //        return super.onTouchEvent(ev);
    //    }

    public void setParallaxView(View v, int imageHeight) {
        mParallaxView = v;
        if (mParallaxView == null) {
            return;
        }
        if (mParallaxView instanceof ImageView) {
            ((ImageView) mParallaxView).setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
        mDefaultImageViewHeight = imageHeight;
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mParallaxView
                .getLayoutParams();
        params.height = mDefaultImageViewHeight;

        setViewsBounds(ZOOM_X2);
    }

    public void setViewsBounds(double zoomRatio) {
        if (mParallaxView == null) {
            return;
        }
        if (mImageViewHeight == -1) {
            mImageViewHeight = mParallaxView.getHeight();
            if (mImageViewHeight <= 0) {
                mImageViewHeight = mDefaultImageViewHeight;
            }
            mDrawableMaxHeight = (int) (mImageViewHeight * (zoomRatio > 1 ? zoomRatio : 1));
        }
    }

    private OnOverScrollByListener scrollByListener = new OnOverScrollByListener() {
        @Override
        public boolean overScrollBy(
                int deltaX,
                int deltaY,
                int scrollX,
                int scrollY,
                int scrollRangeX,
                int scrollRangeY,
                int maxOverScrollX,
                int maxOverScrollY,
                boolean isTouchEvent) {
            if (mParallaxView == null) {
                return false;
            }
            // 判断当前图片是否已经达到设置的最大高度, 如果是的话,就不再作放大
            if (mParallaxView.getHeight() <= mDrawableMaxHeight && isTouchEvent) {
                if (deltaY < 0) {
                    // 下拉
                    if (mParallaxView.getHeight() - deltaY / 2 >= mImageViewHeight) {
                        // 边下拉边改变图片高度
                        mParallaxView.getLayoutParams().height = mParallaxView.getHeight() -
                                deltaY / 2 < mDrawableMaxHeight ? mParallaxView.getHeight() -
                                deltaY / 2 : mDrawableMaxHeight;
                        mParallaxView.requestLayout();
                    }
                } else {
                    if (mParallaxView.getHeight() > mImageViewHeight) {
                        mParallaxView.getLayoutParams().height = mParallaxView.getHeight() -
                                deltaY > mImageViewHeight ? mParallaxView.getHeight() - deltaY :
                                mImageViewHeight;
                        mParallaxView.requestLayout();

                        return true;
                    }
                }
            }
            return false;
        }
    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mParallaxView == null) {
            return super.dispatchTouchEvent(ev);
        }
        if (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent
                .ACTION_CANCEL) {
            if (mImageViewHeight < mParallaxView.getHeight()) {
                ResetAnimimation animation = new ResetAnimimation(mParallaxView, mImageViewHeight);
                animation.setDuration(300);
                mParallaxView.startAnimation(animation);
                return false;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    //    private OnTouchEventListener touchListener = new OnTouchEventListener() {
    //        @Override
    //        public void onTouchEvent(MotionEvent ev) {
    //        }
    //    };

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
        }
    }


    @Override
    public void scrollBy(int x, int y) {
        if (mParallaxView != null) {
            int scrollY = getScrollY();
            if (scrollY == 0 && y < 0 && canPtr() && mParallaxView.getHeight() <=
                    mDrawableMaxHeight) {
                overScrollBy(0, y, 0, scrollY, 0, 0, 0, 0, true);
                return;
            }
        }
        super.scrollBy(x, y);
    }

    @Override
    public void scrollTo(int x, int y) {
        if (mParallaxView != null) {
            if (y > 0 && mParallaxView.getHeight() > mImageViewHeight) {
                mParallaxView.getLayoutParams().height = Math.max(mParallaxView.getHeight() - y,
                        mImageViewHeight);
                mParallaxView.requestLayout();
                return;
            }
        }
        super.scrollTo(x, y);
    }

    @Override
    public float getScaleY() {
        return super.getScaleY();
    }
}
