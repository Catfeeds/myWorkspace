package me.suncloud.marrymemo.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ExpandableListView;

/**
 * Created by LuoHanLin on 15/1/19.
 */
public class SwipeExpandableListView extends ExpandableListView{

    private Boolean mIsHorizontal;
    private View mPreItemView;
    private View mCurrentItemView;
    private float mFirstX;
    private float mFirstY;
    private int mAddedViewWidth = 260;
    private final int mDuration = 50;
    private final int mDurationStep = 10;
    private boolean mIsShown;
    private int currentPosition;
    private OnSwipeListener onSwipeListener;
    private boolean mEnabled = true;

    public interface OnSwipeListener{
        public void onSwipe(boolean show);
    }

    public SwipeExpandableListView(Context context) {
        super(context);
    }

    public SwipeExpandableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SwipeExpandableListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        float lastX = ev.getX();
        float lastY = ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mIsHorizontal = null;
                mFirstX = lastX;
                mFirstY = lastY;
                int motionPosition = pointToPosition((int) mFirstX, (int) mFirstY);

                if (motionPosition >= 0) {
                    currentPosition = motionPosition - getFirstVisiblePosition();
                    View currentItemView = getChildAt(currentPosition);
                    mPreItemView = mCurrentItemView;
                    mCurrentItemView = currentItemView;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = lastX - mFirstX;
                float dy = lastY - mFirstY;

                if (Math.abs(dx) >= 5 && Math.abs(dy) >= 5) {
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mIsShown && (mPreItemView != mCurrentItemView || isHitCurrentItemLeftEdge
                        (lastX))) {
                    hiddenItemAddedView(mPreItemView);
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        float lastX = ev.getX();
        float lastY = ev.getY();
        if (isHeadOrFooter(lastX, lastY) || !mEnabled) {
            return super.onTouchEvent(ev);
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = lastX - mFirstX;
                float dy = lastY - mFirstY;

                if (mIsHorizontal == null) {
                    if (!isScrollable(dx, dy)) {
                        break;
                    }
                }

                // 可以横向滚动
                if (mIsHorizontal) {
                    if (mIsShown && mPreItemView != mCurrentItemView) {
                        hiddenItemAddedView(mPreItemView);
                    }

                    if (mIsShown && mPreItemView == mCurrentItemView) {
                        dx = dx - mAddedViewWidth;
                    }

                    if (dx < 0 && dx > -mAddedViewWidth) {
                        mCurrentItemView.scrollTo((int)(-dx), 0);
                    }

                    return true;
                }else {
                    if (mIsShown) {
                        hiddenItemAddedView(mPreItemView);
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                clearPressedState();
                if (mIsShown) {
                    hiddenItemAddedView(mPreItemView);
                }

                if (mIsHorizontal != null && mIsHorizontal) {
                    if (mFirstX - lastX > mAddedViewWidth / 2) {

                        showItemAddedView(mCurrentItemView);
                    }else{
                        hiddenItemAddedView(mCurrentItemView);
                    }

                    MotionEvent motionEvent = MotionEvent.obtain(ev);
                    motionEvent.setAction(MotionEvent.ACTION_CANCEL);
                    super.onTouchEvent(motionEvent);
                    return true;
                }
                break;
        }


        return super.onTouchEvent(ev);
    }

    private void clearPressedState() {
        if (mCurrentItemView != null) {
            mCurrentItemView.setPressed(false);
            setPressed(false);
            refreshDrawableState();
        }
    }

    /**
     * 判断当前触发的位置是否是头部尾部
     * @param posX
     * @param posY
     * @return
     */
    private boolean isHeadOrFooter(float posX, float posY) {
        int pos = pointToPosition((int) posX, (int) posY);
        if ((pos >= 0 && pos < getHeaderViewsCount()) || (pos >= (getCount() -
                getFooterViewsCount()))) {
            return true;
        }

        return false;
    }

    /**
     * 判断是应该响应左右滑动还是上下滚动
     * @param dx
     * @param dy
     * @return
     */
    private boolean isScrollable(float dx, float dy) {
        boolean scrollable = true;

        if (Math.abs(dx) > 30 && Math.abs(dx) > 2 * Math.abs(dy)) {
            mIsHorizontal = true;
        }else if (Math.abs(dy) > 30 && Math.abs(dy) > 2 * Math.abs(dx)) {
            mIsHorizontal = false;
        }else{
            scrollable = false;
        }

        return scrollable;
    }

    private boolean isHitCurrentItemLeftEdge(float x) {
        return x < getWidth() - mAddedViewWidth;
    }

    private void collapseAll() {
        for (int i = 0; i < getCount(); i++) {
            if (isGroupExpanded(i)) {
                collapseGroup(i);
            }
        }
    }

    /**
     * 动画显示新增view
     * @param view
     */
    private void showItemAddedView(View view) {
        Message msg = new ActionHandler().obtainMessage();
        msg.obj = view;
        msg.arg1 = view.getScrollX();
        msg.arg2 = mAddedViewWidth;
        msg.sendToTarget();

        mIsShown = true;

        if (onSwipeListener != null) {
            onSwipeListener.onSwipe(mIsShown);
        }
    }

    /**
     * 动画隐藏新增的view
     * @param view
     */
    private void hiddenItemAddedView(View view) {
        if (mCurrentItemView == null) {
            return;
        }
        Message msg = new ActionHandler().obtainMessage();
        msg.obj = view;
        msg.arg1 = view.getScrollX();
        msg.arg2 = 0;

        msg.sendToTarget();
        mIsShown = false;
        if (onSwipeListener != null) {
            onSwipeListener.onSwipe(mIsShown);
        }
    }

    /**
     * 隐藏删除按钮
     */
    public void hiddenItemAddedView() {
        if (currentPosition > -1) {
            View view = getChildAt(currentPosition);
            hiddenItemAddedView(view);
        }
    }


    /**
     * 处理view的显示或隐藏事件
     */
    class ActionHandler extends Handler {
        int stepX = 0;
        int fromX;
        int toX;

        View view;

        private boolean mIsInAnimation = false;

        private void animationOver() {
            mIsInAnimation = false;
            stepX = 0;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (stepX == 0) {
                if (mIsInAnimation) {
                    return;
                }
                mIsInAnimation = true;
                view = (View) msg.obj;
                fromX = msg.arg1;
                toX = msg.arg2;
                stepX = (int) ((toX - fromX) * mDurationStep * 1.0 / mDuration);
                if (stepX < 0 && stepX > -1) {
                    stepX = -1;
                }else if (stepX > 0 && stepX < 1) {
                    stepX = 1;
                }

                if (Math.abs(toX - fromX) < 10) {
                    view.scrollTo(toX, 0);
                    animationOver();
                    return;
                }
            }

            fromX += stepX;
            boolean isLastStep = (stepX > 0 && fromX > toX) || (stepX < 0 && fromX < toX);
            if (isLastStep) {
                fromX = toX;
            }

            view.scrollTo(fromX, 0);
            invalidate();

            if (!isLastStep) {
                this.sendEmptyMessageDelayed(0, mDurationStep);
            }else {
                animationOver();
            }
        }
    }

    public int getmAddedViewWidth() {
        return mAddedViewWidth;
    }

    /**
     * 设置监听滑动事件的改变状态
     * @param onSwipeListener
     */
    public void setOnSwipeListener(OnSwipeListener onSwipeListener) {
        this.onSwipeListener = onSwipeListener;
    }

    public boolean isSwipeEnabled() {
        return mEnabled;
    }

    public void setSwipeEnabled(boolean mEnabled) {
        this.mEnabled = mEnabled;
    }

}
