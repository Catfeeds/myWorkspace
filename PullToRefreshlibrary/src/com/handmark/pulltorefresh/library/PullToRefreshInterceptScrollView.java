package com.handmark.pulltorefresh.library;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Suncloud on 2015/10/8.
 */
public class PullToRefreshInterceptScrollView extends PullToRefreshBase<InterceptScrollView> {

    public PullToRefreshInterceptScrollView(Context context) {
        super(context);
    }

    public PullToRefreshInterceptScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullToRefreshInterceptScrollView(Context context, Mode mode) {
        super(context, mode);
    }

    public PullToRefreshInterceptScrollView(Context context, Mode mode, AnimationStyle style) {
        super(context, mode, style);
    }

    private InterceptScrollView scrollView;

    @Override
    public final Orientation getPullToRefreshScrollDirection() {
        return Orientation.VERTICAL;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected InterceptScrollView createRefreshableView(Context context, AttributeSet attrs) {
        scrollView = new InterceptScrollView(context, attrs);
        scrollView.setId(R.id.scrollview);
        return scrollView;
    }

    @Override
    protected boolean isReadyForPullStart() {
        return mRefreshableView.getScrollY() == 0;
    }

    @Override
    protected boolean isReadyForPullEnd() {
        View scrollViewChild = mRefreshableView.getChildAt(0);
        if (null != scrollViewChild) {
            return mRefreshableView.getScrollY() >= (scrollViewChild.getHeight() - getHeight());
        }
        return false;
    }
}
