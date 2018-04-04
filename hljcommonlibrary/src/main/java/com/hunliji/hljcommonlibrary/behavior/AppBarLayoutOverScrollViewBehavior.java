package com.hunliji.hljcommonlibrary.behavior;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by wangtao on 2017/12/11.
 */

public class AppBarLayoutOverScrollViewBehavior extends AppBarLayout.Behavior {


    private boolean isRecovering = false;//是否正在自动回弹中
    private int height;
    private int maxOverScroll = 500;
    private boolean isOverScroll;
    private List<OnOverScrollListener> listeners;
    private boolean disableOverScroll; // 是否禁止这个动画
    private ValueAnimator anim;


    public AppBarLayoutOverScrollViewBehavior() {}

    public AppBarLayoutOverScrollViewBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setMaxOverScroll(int maxOverScroll) {
        this.maxOverScroll = maxOverScroll;
    }

    public void addOnOverScrollListener(OnOverScrollListener listener) {
        if (listeners == null) {
            listeners = new ArrayList<>();
        }
        listeners.add(listener);
    }

    @Override
    public boolean onMeasureChild(
            CoordinatorLayout parent,
            AppBarLayout child,
            int parentWidthMeasureSpec,
            int widthUsed,
            int parentHeightMeasureSpec,
            int heightUsed) {
        if (!isOverScroll) {
            height = child.getMeasuredHeight();
        }
        return super.onMeasureChild(parent,
                child,
                parentWidthMeasureSpec,
                widthUsed,
                parentHeightMeasureSpec,
                heightUsed);
    }

    @Override
    public boolean layoutDependsOn(
            CoordinatorLayout parent, AppBarLayout child, View dependency) {
        return super.layoutDependsOn(parent, child, dependency);
    }

    @Override
    public boolean onLayoutChild(
            CoordinatorLayout parent, AppBarLayout abl, int layoutDirection) {
        return super.onLayoutChild(parent, abl, layoutDirection);
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
        if (isRecovering) {
            if (Math.abs(dy) > 10) {
                if (anim != null && anim.isRunning()) {
                    anim.cancel();
                    anim = null;
                }
            } else {
                consumed[1] = dy;
                super.onNestedPreScroll(coordinatorLayout, child, target, dx, 0, consumed, type);
                return;
            }
        }
        if (disableOverScroll) {
            super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type);
            return;
        }
        if (child.getHeight() > height && dy > 0) {
            overScroll(child, target, dy);
            consumed[1] = Math.min(dy, child.getHeight() - height);
            dy = dy - consumed[1];
        }
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type);
    }

    @Override
    public void onNestedScroll(
            CoordinatorLayout coordinatorLayout,
            AppBarLayout child,
            View target,
            int dxConsumed,
            int dyConsumed,
            int dxUnconsumed,
            int dyUnconsumed,
            int type) {
        if (!isRecovering && type == ViewCompat.TYPE_TOUCH && !disableOverScroll) {
            if (dyUnconsumed < 0 && child.getTop() >= 0 && child.getHeight() >= height) {
                overScroll(child, target, Math.max(dyUnconsumed, -100));
            }
        }
        super.onNestedScroll(coordinatorLayout,
                child,
                target,
                dxConsumed,
                dyConsumed,
                dxUnconsumed,
                dyUnconsumed,
                type);
    }

    @Override
    public void onStopNestedScroll(
            CoordinatorLayout coordinatorLayout, AppBarLayout abl, View target, int type) {
        recovery(abl);
        super.onStopNestedScroll(coordinatorLayout, abl, target, type);
    }

    private void overScroll(AppBarLayout abl, View target, int dy) {
        int bottom = abl.getBottom() - dy;
        bottom = Math.min(Math.max(bottom, height + abl.getTop()),
                height + maxOverScroll + abl.getTop());
        if (abl.getBottom() == bottom) {
            return;
        }
        isOverScroll = bottom - abl.getTop() > height;
        abl.setBottom(bottom);
        abl.postInvalidate();
        if (!CommonUtil.isCollectionEmpty(listeners)) {
            for (OnOverScrollListener listener : listeners) {
                listener.onOverScrollBy(height, bottom - abl.getTop() - height);
            }
        }
    }

    private void recovery(final AppBarLayout abl) {
        if (isRecovering)
            return;
        if (abl.getHeight() > height) {
            isRecovering = true;
            anim = ValueAnimator.ofInt(abl.getBottom(), height + abl.getTop())
                    .setDuration(200);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int bottom = (int) animation.getAnimatedValue();
                    abl.setBottom(bottom);
                    abl.postInvalidate();
                    if (!CommonUtil.isCollectionEmpty(listeners)) {
                        for (OnOverScrollListener listener : listeners) {
                            listener.onOverScrollBy(height, abl.getHeight() - height);
                        }
                    }
                }
            });
            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    abl.setBottom(height + abl.getTop());
                    abl.postInvalidate();
                    isOverScroll = false;
                    isRecovering = false;
                    if (!CommonUtil.isCollectionEmpty(listeners)) {
                        for (OnOverScrollListener listener : listeners) {
                            listener.onOverScrollBy(height, abl.getHeight() - height);
                        }
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    isOverScroll = abl.getHeight() > height;
                    isRecovering = false;
                    if (!CommonUtil.isCollectionEmpty(listeners)) {
                        for (OnOverScrollListener listener : listeners) {
                            listener.onOverScrollBy(height, abl.getHeight() - height);
                        }
                    }
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
            anim.start();
        }
    }

    public void setDisableOverScroll(boolean disableOverScroll) {
        this.disableOverScroll = disableOverScroll;
    }

    public interface OnOverScrollListener {
        void onOverScrollBy(int height, int overScroll);
    }
}
