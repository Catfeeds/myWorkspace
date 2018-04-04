package com.handmark.pulltorefresh.library;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;


/**
 * Created by Suncloud on 2015/9/25.
 */
public class PullToRefreshVerticalRecyclerView extends PullToRefreshBase<RecyclerView> {

    private RecyclerView recyclerView;

    public PullToRefreshVerticalRecyclerView(Context context) {
        super(context);
    }

    public PullToRefreshVerticalRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullToRefreshVerticalRecyclerView(Context context, Mode mode) {
        super(context, mode);
    }

    public PullToRefreshVerticalRecyclerView(Context context, Mode mode, AnimationStyle animStyle) {
        super(context, mode, animStyle);
    }

    @Override
    public Orientation getPullToRefreshScrollDirection() {
        return Orientation.VERTICAL;
    }

    @Override
    protected RecyclerView createRefreshableView(Context context, AttributeSet attrs) {
        recyclerView = new RecyclerView(context, attrs);
        recyclerView.setId(R.id.recycle_view);
        return recyclerView;
    }

    @Override
    protected boolean isReadyForPullEnd() {
        return isLastItemVisible();
    }

    @Override
    protected boolean isReadyForPullStart() {
        return isFirstItemVisible();
    }


    private boolean isLastItemVisible() {
        RecyclerView.Adapter adapter = mRefreshableView.getAdapter();
        if (adapter == null) {
            return true;
        }

        int lastVisiblePosition = mRefreshableView.getChildAdapterPosition(mRefreshableView
                .getChildAt(
                mRefreshableView.getChildCount() - 1));
        if (lastVisiblePosition >= mRefreshableView.getAdapter()
                .getItemCount() - 1) {
            return mRefreshableView.getChildAt(mRefreshableView.getChildCount() - 1)
                    .getBottom() <= mRefreshableView.getBottom();
        }
        return false;
    }

    private boolean isFirstItemVisible() {
        RecyclerView.Adapter adapter = mRefreshableView.getAdapter();
        if (adapter == null) {
            return true;
        }
        if (mRefreshableView.getChildCount() <= 0)
            return true;
        int firstVisiblePosition = mRefreshableView.getChildAdapterPosition(mRefreshableView
                .getChildAt(
                0));
        int top = mRefreshableView.getLayoutManager()
                .getDecoratedTop(mRefreshableView.getChildAt(0));
        return firstVisiblePosition == 0 && mRefreshableView.getPaddingTop() - top <= 0;
    }

    @Override
    protected void onDetachedFromWindow() {
        if(mRefreshableView!=null){
            mRefreshableView.setAdapter(null);
        }
        super.onDetachedFromWindow();
    }
}
