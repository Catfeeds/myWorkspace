package com.hunliji.hljnotelibrary.behavior;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;


/**
 * Created by chen_bin on 2017/12/29.
 */
public class NoteSnapScrollViewBehavior extends AppBarLayout.Behavior {

    private int mDirection;
    private int mLastMotionY;
    private int mTouchSlop = -1;
    private boolean mIsBeingDragged;

    private final static int DIRECTION_INIT = 0;
    private final static int DIRECTION_UP = 1;
    private final static int DIRECTION_DOWN = 2;

    private static final int INVALID_POINTER = -1;
    private int mActivePointerId = INVALID_POINTER;

    public NoteSnapScrollViewBehavior() {

    }

    public NoteSnapScrollViewBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mTouchSlop = ViewConfiguration.get(context)
                .getScaledTouchSlop();
    }

    @Override
    public boolean onInterceptTouchEvent(
            CoordinatorLayout parent, AppBarLayout child, MotionEvent ev) {
        final int action = ev.getAction();

        // Shortcut since we're being dragged
        if (action == MotionEvent.ACTION_MOVE && mIsBeingDragged) {
            return true;
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                mIsBeingDragged = false;
                final int x = (int) ev.getX();
                final int y = (int) ev.getY();
                if (parent.isPointInChildBounds(child, x, y)) {
                    mLastMotionY = y;
                    mActivePointerId = ev.getPointerId(0);
                }
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                final int activePointerId = mActivePointerId;
                if (activePointerId == INVALID_POINTER) {
                    break;
                }
                final int pointerIndex = ev.findPointerIndex(activePointerId);
                if (pointerIndex == -1) {
                    break;
                }
                final int y = (int) ev.getY();
                final int yDiff = Math.abs(y - mLastMotionY);
                if (yDiff > mTouchSlop) {
                    mIsBeingDragged = true;
                    mLastMotionY = y;
                }
                break;
            }
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {
                mIsBeingDragged = false;
                mActivePointerId = INVALID_POINTER;
                break;
            }
        }
        return mIsBeingDragged;
    }

    @Override
    public boolean onTouchEvent(CoordinatorLayout parent, AppBarLayout child, MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
                if (!mIsBeingDragged) {
                    break;
                }
                final int activePointerIndex = ev.findPointerIndex(mActivePointerId);
                if (activePointerIndex == -1) {
                    return false;
                }
                final int y = (int) ev.getY(activePointerIndex);
                int dy = mLastMotionY - y;
                if (Math.abs(dy) > mTouchSlop) {
                    child.setExpanded(mDirection == (dy < 0 ? DIRECTION_DOWN : DIRECTION_UP), true);
                }
            case MotionEvent.ACTION_CANCEL: {
                mIsBeingDragged = false;
                mActivePointerId = INVALID_POINTER;
                break;
            }
        }
        return true;
    }

    @Override
    public void onNestedPreScroll(
            CoordinatorLayout coordinatorLayout,
            AppBarLayout child,
            View target,
            int dx,
            int dy,
            int[] consumed,
            int type) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type);
        mDirection = dy == 0 ? DIRECTION_INIT : dy < 0 ? DIRECTION_DOWN : DIRECTION_UP;
    }

    @Override
    public boolean onNestedPreFling(
            @NonNull CoordinatorLayout coordinatorLayout,
            @NonNull AppBarLayout child,
            @NonNull View target,
            float velocityX,
            float velocityY) {
        return true;
    }

    @Override
    public void onStopNestedScroll(
            CoordinatorLayout coordinatorLayout, AppBarLayout abl, View target, int type) {
        if (type == ViewCompat.TYPE_TOUCH) {
            snapScroll(abl);
        }
    }

    private void snapScroll(final AppBarLayout abl) {
        if (mDirection == DIRECTION_INIT) {
            return;
        }
        final int top = Math.abs(abl.getTop());
        final int ablHeight = abl.getMeasuredHeight();
        if (top == 0 || top >= ablHeight) {
            return;
        }
        if (top <= 120) {
            abl.setExpanded(true, true);
        } else if (top <= ablHeight - 120) {
            abl.setExpanded(mDirection == DIRECTION_DOWN, true);
        } else {
            abl.setExpanded(false, true);
        }
    }

}