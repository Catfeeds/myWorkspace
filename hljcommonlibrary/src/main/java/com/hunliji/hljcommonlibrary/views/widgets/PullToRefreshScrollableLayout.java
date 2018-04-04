package com.hunliji.hljcommonlibrary.views.widgets;

import android.content.Context;
import android.util.AttributeSet;

import com.handmark.pulltorefresh.library.PullToRefreshBase;

/**
 * 下拉刷新ScrollableLayout
 * Created by jinxin on 2016/7/26.
 */
public class PullToRefreshScrollableLayout extends PullToRefreshBase<ScrollableLayout> implements
        ScrollableLayout.OnScrollListener, ScrollableLayout.OnReachTopListener {
    private ScrollableLayout scrollableLayout;
    private int currentY;
    private boolean isTop = true;
    private boolean isAwaysRefresh;

    public PullToRefreshScrollableLayout(Context context) {
        super(context);
    }

    public PullToRefreshScrollableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullToRefreshScrollableLayout(
            Context context, Mode mode) {
        super(context, mode);
    }

    public PullToRefreshScrollableLayout(
            Context context, Mode mode, AnimationStyle animStyle) {
        super(context, mode, animStyle);
    }

    @Override
    public Orientation getPullToRefreshScrollDirection() {
        return Orientation.VERTICAL;
    }

    @Override
    protected ScrollableLayout createRefreshableView(
            Context context, AttributeSet attrs) {
        scrollableLayout = new ScrollableLayout(context, attrs);
        scrollableLayout.addOnScrollListener(this);
        scrollableLayout.addOnReachTopListener(this);
        return scrollableLayout;
    }

    @Override
    public void isReachTop(boolean isTop) {
        this.isTop = isTop;
    }

    public void setAwaysRefresh(boolean awaysRefresh) {
        isAwaysRefresh = awaysRefresh;
    }

    @Override
    protected boolean isReadyForPullEnd() {
        return false;
    }

    @Override
    protected boolean isReadyForPullStart() {
        return currentY == 0 && (isTop || isAwaysRefresh);
    }

    @Override
    public void onScroll(int currentY, int maxY) {
        this.currentY = currentY;
    }
}
