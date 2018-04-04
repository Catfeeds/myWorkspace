package com.hunliji.marrybiz.widget;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.util.JSONUtil;

/**
 * Created by LuoHanLin on 14/10/27.
 */
public class ParallaxScrollView extends ScrollView implements AbsListView.OnScrollListener {

    public final static double NO_ZOOM = 1;
    public final static double ZOOM_X2 = 2;

    private View mHeadView;
    private int mDrawableMaxHeight = -1;
    private int mImageViewHeight = -1;
    private int mDefaultImageViewHeight = 0;

    private interface OnOverScrollByListener {
         boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY,
                                    int scrollRangeX, int scrollRangeY, int maxOverScrollX,
                                    int maxOverScrollY, boolean isTouchEvent);
    }

    private interface OnTouchEventListener {
         void onTouchEvent(MotionEvent ev);
    }


    public ParallaxScrollView(Context context) {
        super(context);
        init(context);
    }

    public ParallaxScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ParallaxScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public void init(Context mContext) {
        Point point = JSONUtil.getDeviceSize(mContext);
        mDefaultImageViewHeight = Math.round(point.x * 9 / 16);
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY,
                                   int scrollRangeX, int scrollRangeY, int maxOverScrollX,
                                   int maxOverScrollY, boolean isTouchEvent) {
        boolean isCollapseAnimation = false;

        isCollapseAnimation = scrollByListener.overScrollBy(deltaX, deltaY, scrollX, scrollY,
                scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY,
                isTouchEvent) || isCollapseAnimation;

        return isCollapseAnimation ? true : super.overScrollBy(deltaX, deltaY, scrollX, scrollY,
                scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                         int totalItemCount) {
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        View firstView = (View) mHeadView.getParent();
        if (firstView.getTop() < getPaddingTop() && mHeadView.getHeight() > mImageViewHeight) {
            mHeadView.getLayoutParams().height = Math.max(mHeadView.getHeight() - (getPaddingTop
                    () - firstView.getTop()), mImageViewHeight);
            firstView.layout(firstView.getLeft(), 0, firstView.getRight(), firstView.getHeight());
            mHeadView.requestLayout();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        touchListener.onTouchEvent(ev);
        return super.onTouchEvent(ev);
    }

    public void setParallaxHeadView(View v) {
        mHeadView = v;
        if (mHeadView instanceof ImageView) {
            ((ImageView) mHeadView).setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
//        mDefaultImageViewHeight = height;
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mHeadView
                .getLayoutParams();
        params.height = mDefaultImageViewHeight;

        setViewsBounds(ZOOM_X2);
    }

    public void setParallaxHeadView(View v, int imageHeight) {
        mHeadView = v;
        if (mHeadView instanceof ImageView) {
            ((ImageView) mHeadView).setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
        mDefaultImageViewHeight = imageHeight;
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mHeadView
                .getLayoutParams();
        params.height = mDefaultImageViewHeight;

        setViewsBounds(ZOOM_X2);
    }

    public void setViewsBounds(double zoomRatio) {
        if (mImageViewHeight == -1) {
            mImageViewHeight = mHeadView.getHeight();
            if (mImageViewHeight <= 0) {
                mImageViewHeight = mDefaultImageViewHeight;
            }

            mDrawableMaxHeight = (int) (mImageViewHeight * (zoomRatio > 1 ? zoomRatio : 1));

            //            if (mHeadView instanceof ImageView) {
            //                // 图片的实际宽度/图片被设置在屏幕上的宽度
            //                double ratio = ((double) mHeadView.getDrawable().getIntrinsicWidth
            // ()) / ((double)
            //                        mHeadView.getWidth());
            //
            //                // 图片实际的高度/初始的缩放比例, 再乘以缩放倍数, 至少是原来大小
            //                mDrawableMaxHeight = (int) ((mHeadView.getDrawable()
            // .getIntrinsicHeight() / ratio) *
            //                        (zoomRatio > 1 ? zoomRatio : 1)); // 至少是一倍的缩放
            //            }

        }
    }

    private OnOverScrollByListener scrollByListener = new OnOverScrollByListener() {
        @Override
        public boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY,
                                    int scrollRangeX, int scrollRangeY, int maxOverScrollX,
                                    int maxOverScrollY, boolean isTouchEvent) {
            // 判断当前图片是否已经达到设置的最大高度, 如果是的话,就不再作放大
            if (mHeadView.getHeight() <= mDrawableMaxHeight && isTouchEvent) {
                if (deltaY < 0) {
                    // 下拉
                    if (mHeadView.getHeight() - deltaY / 2 >= mImageViewHeight) {
                        // 边下拉边改变图片高度
                        mHeadView.getLayoutParams().height = mHeadView.getHeight() - deltaY / 2 <
                                mDrawableMaxHeight ? mHeadView.getHeight() - deltaY / 2 :
                                mDrawableMaxHeight;
                        mHeadView.requestLayout();
                    }
                } else {
                    if (mHeadView.getHeight() > mImageViewHeight) {
                        mHeadView.getLayoutParams().height = mHeadView.getHeight() - deltaY >
                                mImageViewHeight ? mHeadView.getHeight() - deltaY :
                                mImageViewHeight;
                        mHeadView.requestLayout();

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
                if (mImageViewHeight - 1 < mHeadView.getHeight()) {
                    ResetAnimimation animation = new ResetAnimimation(mHeadView, mImageViewHeight);
                    animation.setDuration(300);
                    mHeadView.startAnimation(animation);
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
        }
    }
}
