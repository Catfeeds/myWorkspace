package com.hunliji.hljcommonlibrary.views.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.R;


abstract public class OverscrollContainer<T extends View> extends LinearLayout {

    private T mOverscrollView = null;

    private boolean mIsBeingDragged = false;

    private float mMotionBeginX = 0;

    private float mMotionBeginY = 0;

    private int mTouchSlop;

    private int distance;
    private ImageView arrowView;
    private TextView hintView;

    private String hintStringStart;
    private String hintStringEnd;

    public OnLoadListener onLoadListener;

    public enum OverscrollDirection {
        None, Horizontal, Vertical,
    }

    abstract protected boolean canOverscrollAtStart();

    abstract protected boolean canOverscrollAtEnd();

    abstract protected OverscrollDirection getOverscrollDirection();

    abstract protected T createOverscrollView();

    public OverscrollContainer(Context context) {
        this(context, null);
    }

    public OverscrollContainer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OverscrollContainer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mOverscrollView = createOverscrollView();
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        addView(mOverscrollView, layoutParams);
        View view = View.inflate(context, R.layout.viewpager_load_view___cm, null);
        arrowView = (ImageView) view.findViewById(R.id.arrow);
        hintView = (TextView) view.findViewById(R.id.hint);
        distance = Math.round(context.getResources()
                .getDisplayMetrics().density * 90);
        layoutParams = new LayoutParams(distance, LayoutParams.MATCH_PARENT);
        addView(view, layoutParams);

        mTouchSlop = ViewConfiguration.get(getContext())
                .getScaledTouchSlop();
    }

    public T getOverscrollView() {
        return mOverscrollView;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        int action = ev.getAction();

        if (action == MotionEvent.ACTION_DOWN) {

            mMotionBeginX = ev.getX();
            mMotionBeginY = ev.getY();
            mIsBeingDragged = false;

        } else if (action == MotionEvent.ACTION_MOVE) {

            if (!mIsBeingDragged) {

                float scrollDirectionDiff = 0f;
                float anotherDirectionDiff = 0f;
                if (getOverscrollDirection() == OverscrollDirection.Horizontal) {

                    scrollDirectionDiff = ev.getX() - mMotionBeginX;
                    anotherDirectionDiff = ev.getY() - mMotionBeginY;

                } else if (getOverscrollDirection() == OverscrollDirection.Vertical) {

                    scrollDirectionDiff = ev.getY() - mMotionBeginY;
                    anotherDirectionDiff = ev.getX() - mMotionBeginX;

                }
                float absScrollDirectionDiff = Math.abs(scrollDirectionDiff);
                float absAnotherDirectionDiff = Math.abs(anotherDirectionDiff);
                if (absScrollDirectionDiff > mTouchSlop && absScrollDirectionDiff >
                        absAnotherDirectionDiff) {
                    if (canOverscrollAtStart() && scrollDirectionDiff > 0f) {

                        mIsBeingDragged = true;

                    } else if (canOverscrollAtEnd() && scrollDirectionDiff < 0f) {

                        mIsBeingDragged = true;

                    }
                }

            }

        }

        return mIsBeingDragged;
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {

        int action = event.getAction();

        float moveOffset = 0;
        if (getOverscrollDirection() == OverscrollDirection.Horizontal) {

            moveOffset = event.getX() - mMotionBeginX;

        } else if (getOverscrollDirection() == OverscrollDirection.Vertical) {

            moveOffset = event.getY() - mMotionBeginY;

        }

        if (action == MotionEvent.ACTION_MOVE) {

            if (getOverscrollDirection() == OverscrollDirection.Horizontal) {
                moveOverscrollView(moveOffset, 0);
            } else if (getOverscrollDirection() == OverscrollDirection.Vertical) {
                moveOverscrollView(0, moveOffset);
            }
            ViewParent parent = getParent();
            if (parent != null) {
                parent.requestDisallowInterceptTouchEvent(true);
            }


        } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {

            resetOverscrollViewWithAnimation(moveOffset, event.getY());
            mIsBeingDragged = false;

        }

        return true;
    }

    private void moveOverscrollView(float currentX, float currentY) {
        if (getOverscrollDirection() == OverscrollDirection.Horizontal) {
            if (currentX < 0) {
                if (distance > 0 && -currentX > distance) {
                    if (arrowView.getRotation() == 0) {
                        arrowView.setRotation(180);
                        if (TextUtils.isEmpty(hintStringEnd)) {
                            hintStringEnd = hintView.getContext()
                                    .getString(R.string.hint_pager_load_more2);
                        }
                        hintView.setText(hintStringEnd);
                    }
                } else if (arrowView.getRotation() != 0) {
                    arrowView.setRotation(0);
                    if (TextUtils.isEmpty(hintStringStart)) {
                        hintStringStart = hintView.getContext()
                                .getString(R.string.hint_pager_load_more);
                    }
                    hintView.setText(hintStringStart);
                }
                scrollTo(-(int) currentX, 0);
            } else {

            }

        } else if (getOverscrollDirection() == OverscrollDirection.Vertical) {

            scrollTo(0, -(int) currentY);

        }
    }

    private void resetOverscrollViewWithAnimation(float currentX, float currentY) {
        if (distance > 0 && -currentX > distance && onLoadListener != null) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    onLoadListener.onLoad();
                }
            }, 300);
        }
        Interpolator scrollAnimationInterpolator = new DecelerateInterpolator();
        SmoothScrollRunnable smoothScrollRunnable = new SmoothScrollRunnable((int) currentX,
                0,
                300,
                scrollAnimationInterpolator);
        post(smoothScrollRunnable);
    }

    final class SmoothScrollRunnable implements Runnable {
        private final Interpolator mInterpolator;
        private final int mScrollToPosition;
        private final int mScrollFromPosition;
        private final long mDuration;

        private boolean mContinueRunning = true;
        private long mStartTime = -1;
        private int mCurrentPosition = -1;

        public SmoothScrollRunnable(
                int fromPosition,
                int toPosition,
                long duration,
                Interpolator scrollAnimationInterpolator) {
            mScrollFromPosition = fromPosition;
            mScrollToPosition = toPosition;
            mInterpolator = scrollAnimationInterpolator;
            mDuration = duration;
        }

        @Override
        public void run() {

            /**
             * Only set mStartTime if this is the first time we're starting,
             * else actually calculate the Y delta
             */
            if (mStartTime == -1) {
                mStartTime = System.currentTimeMillis();
            } else {

                /**
                 * We do do all calculations in long to reduce software float
                 * calculations. We use 1000 as it gives us good accuracy and
                 * small rounding errors
                 */
                long normalizedTime = (1000 * (System.currentTimeMillis() - mStartTime)) /
                        mDuration;
                normalizedTime = Math.max(Math.min(normalizedTime, 1000), 0);

                final int deltaY = Math.round((mScrollFromPosition - mScrollToPosition) *
                        mInterpolator.getInterpolation(
                        normalizedTime / 1000f));
                mCurrentPosition = mScrollFromPosition - deltaY;

                if (getOverscrollDirection() == OverscrollDirection.Horizontal) {
                    moveOverscrollView(mCurrentPosition, 0);
                } else if (getOverscrollDirection() == OverscrollDirection.Vertical) {
                    moveOverscrollView(0, mCurrentPosition);
                }

            }

            if (mContinueRunning && mScrollToPosition != mCurrentPosition) {

                ViewCompat.postOnAnimation(OverscrollContainer.this, this);

            } else {

            }
        }

        public void stop() {
            mContinueRunning = false;
            removeCallbacks(this);
        }
    }

    public void setOnLoadListener(OnLoadListener onLoadListener) {
        this.onLoadListener = onLoadListener;
    }

    public interface OnLoadListener {
        void onLoad();
    }

    public void setHintStringStart(String hintStringStart) {
        this.hintStringStart = hintStringStart;
    }

    public void setHintStringEnd(String hintStringEnd) {
        this.hintStringEnd = hintStringEnd;
    }

    @SuppressLint("ResourceType")
    public void setArrowImageResId(@IdRes int imageResId) {
        this.arrowView.setImageResource(imageResId);
    }

    public void setHintTextColorResId(@IdRes int colorResId) {
        hintView.setTextColor(ContextCompat.getColor(hintView.getContext(), colorResId));
    }

    public void hideHintView() {
        hintView.setVisibility(GONE);
    }

    public void hideArrowView() {
        arrowView.setVisibility(GONE);
    }
}
