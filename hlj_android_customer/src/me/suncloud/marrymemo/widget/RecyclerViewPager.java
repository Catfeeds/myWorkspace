package me.suncloud.marrymemo.widget;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

import java.util.ArrayList;

import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by Filippo on 9/15/2015.
 */

public class RecyclerViewPager extends RecyclerView {

    private static final String TAG = RecyclerViewPager.class.getSimpleName();
    private static final int INITIAL_POSITION = -1;
    private int mRecyclerViewWidth;
    private int mPosition = INITIAL_POSITION;

    private ArrayList<SnapRecyclerViewListener> mListeners = new ArrayList<>();
    private LinearLayoutManager mSnapHorizontalLinearLayoutManager;
    private int mOldPosition;
    private boolean mScrollToLeft;
    private int mCenterHolderPosition;
    private SwipeType mScrollType;
    private int space;
    private int imageViewHeight;


    public RecyclerViewPager(Context context) {
        super(context);
        init();
    }

    public RecyclerViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RecyclerViewPager(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setSpace(int space) {
        this.space = space;
        addItemDecoration(new SpacesItemDecoration(space));
    }

    public void setImageViewHeight(int imageViewHeight) {
        this.imageViewHeight = imageViewHeight;
    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.right = space;
            outRect.left = space;
        }
    }

    /**
     * This method initializes the properties for the recyclerView
     */
    private void init() {
        mRecyclerViewWidth = JSONUtil.getDeviceSize(getContext()).x;
        mSnapHorizontalLinearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,
                false);
        setHasFixedSize(true);
        setLayoutManager(mSnapHorizontalLinearLayoutManager);
        setRecycledViewPool(new RecyclerView.RecycledViewPool());
        addOnScrollListener(new SnapRecyclerViewScrollListener());
    }

    public void setAdapterChanged() {
        mScrollToLeft = true;
    }


    /**
     * This method places to center a Holder from the recyclerView elements
     * with smoothScroll Animation
     *
     * @param position
     */
    public void setCenterViewAtPosition(int position) {
//        if (mOldPosition > position) {
//            mScrollToLeft = true;
//        }
//        mPosition = position;
//        mSnapHorizontalLinearLayoutManager.scrollToPosition(position);
//        mSnapHorizontalLinearLayoutManager.smoothScrollToPosition(this, null, position);
//        mOldPosition = position;
        setCenterViewAtPositionWithOffset(position,0);
    }

    /**
     * This method places to center a Holder from the recyclerView elements
     * with smoothScroll Animation
     *
     * @param position
     */
    public void setCenterViewAtPositionWithOffset(int position,int offset) {
        if (mOldPosition > position) {
            mScrollToLeft = true;
        }
        mPosition = position;
        mSnapHorizontalLinearLayoutManager.scrollToPositionWithOffset(position,offset);
//        mSnapHorizontalLinearLayoutManager.smoothScrollToPosition(this, null, position);
        mOldPosition = position;
    }

    /**
     * This method is overriden to trigger the animation after a change to the layout
     * Further documentation can be found android developer site documentation
     *
     * @param changed
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

        int preFirstChildPosition = getChildCount() != 0 ? getChildViewHolder(getChildAt(0)).getLayoutPosition() : -1;
        int preLastChildPosition = getChildCount() != 0 ? getChildViewHolder(getChildAt(getChildCount() - 1))
                .getLayoutPosition() : -1;
        super.onLayout(changed, left, top, right, bottom);
        int postFirstChildPosition = getChildCount() != 0 ? getChildViewHolder(getChildAt(0)).getLayoutPosition() : -1;
        int postLastChildPosition = getChildCount() != 0 ? getChildViewHolder(getChildAt(getChildCount() - 1))
                .getLayoutPosition() : -1;

        if (preFirstChildPosition != postFirstChildPosition || preLastChildPosition != postLastChildPosition) {
            centerFirstVisibleHolder();
        }
    }

    /**
     * This method is used to trigger {@link #onScrollStateChanged} of RecyclerView.OnScrollListener
     */
    private void centerFirstVisibleHolder() {
        int scrollNeeded = mRecyclerViewWidth / 2 - mRecyclerViewWidth;
        if (mScrollToLeft) {
            mScrollToLeft = false;
            smoothScrollBy(-scrollNeeded, 0);
        } else {
            smoothScrollBy(scrollNeeded, 0);
        }
    }

    /**
     * Register a listener callback to get notified about the snapped view
     *
     * @param listener
     */
    public void addSnapListener(SnapRecyclerViewListener listener) {
        mListeners.add(listener);
    }

    public void setScrollType(SwipeType scrollType) {
        mScrollType = scrollType;
    }


    /**
     * Custom OnScrollListener to handle the swiping process with animation
     */
    private class SnapRecyclerViewScrollListener extends RecyclerView.OnScrollListener {

        private boolean mAutoSet = true;
        private int mCenterPivot;

        public SnapRecyclerViewScrollListener() {
            mCenterPivot = mRecyclerViewWidth / 2;
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

            if (mCenterPivot == 0) {
                mCenterPivot = recyclerView.getLeft() + recyclerView.getRight();
            }
            if (!mAutoSet) {

                synchronized (this) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {

                        View view = linearLayoutManager.findViewByPosition(mPosition);

                        mPosition = INITIAL_POSITION;

                        if (view == null) {
                            view = findCenterView(linearLayoutManager);
                        }

                        if (view == null) {
                            return;
                        }

                        int viewCenter = (view.getLeft() + view.getRight()) / 2;
                        int viewWidth = view.getRight() - view.getLeft();

                        int scrollNeeded = viewCenter - mCenterPivot;
                        recyclerView.smoothScrollBy(scrollNeeded, 0);

                        mCenterHolderPosition = recyclerView.getChildLayoutPosition(view);

                        if (mScrollType == null) {
                            mScrollType = SwipeType.SWIPE;
                        }

                        onCenterItemSnap(mCenterHolderPosition, recyclerView.getChildViewHolder(view), mScrollType);

                        mScrollType = null;
                        mAutoSet = true;
                    }
                }
            }
            if (newState == RecyclerView.SCROLL_STATE_DRAGGING || newState == RecyclerView.SCROLL_STATE_SETTLING) {
                mAutoSet = false;
            }
        }

        /**
         * This method is used to notify snapped center holder to subscribed listeners
         *
         * @param childLayoutPosition
         * @param centerHolder
         * @param scrollType
         */
        private void onCenterItemSnap(int childLayoutPosition, ViewHolder centerHolder, SwipeType scrollType) {
            for (SnapRecyclerViewListener listener : mListeners) {
                listener.onCenterItemSnapped(childLayoutPosition, centerHolder, scrollType);
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            int childCount = recyclerView.getChildCount();
            if (childCount == 0) {
                return;
            }
            int width = recyclerView.getChildAt(0).getWidth();
            int padding = (recyclerView.getWidth() - width) / 2;

            for (int i = 0; i < childCount; i++) {
                View view = recyclerView.getChildAt(i);

                float ratio = 0;

                if (view.getLeft() <= padding) {
                    ratio = (padding - view.getLeft()) * 1f / (view.getWidth() + 2 * space);
                    float scale = 1.6f - ratio * 0.6f;
                    view.setAlpha(scale * 5 / 12f + 1 / 3f);
                    if (view.getLeft() < padding - view.getWidth() - 2 * space) {
                        scale = 1;
                    }
                    view.setTranslationX(-view.getWidth() * (1.6f / scale - 1) / 2);
                    view.setScaleY(scale);
                    view.setScaleX(scale);

                } else {
                    ratio = (recyclerView.getWidth() - padding - view.getLeft() + 2 * space) * 1f / (view.getWidth()
                            + space * 2);
                    float scale = 1 + ratio * 0.6f;
                    view.setAlpha(scale * 5 / 12f + 1 / 3f);
                    if (view.getLeft() > recyclerView.getWidth() - padding + 2 * space) {
                        scale = 1;
                    }
                    view.setTranslationX(view.getWidth() * (1.6f / scale - 1) / 2);
                    view.setScaleY(scale);
                    view.setScaleX(scale);
                }
            }
        }

        /**
         * This method takes layout manager and finds the right center element of it
         *
         * @param layoutManager
         * @return View
         */
        private View findCenterView(LinearLayoutManager layoutManager) {

            int minDistance = 0;
            View view, returnView = null;
            boolean notFound = true;

            for (int i = layoutManager.findFirstVisibleItemPosition(); i <= layoutManager.findLastVisibleItemPosition
                    () && notFound; i++) {

                view = layoutManager.findViewByPosition(i);

                if (view == null) {
                    continue;
                }

                int center = (view.getLeft() + view.getRight()) / 2;

                int leastDifference = Math.abs(mCenterPivot - center);

                if (leastDifference <= minDistance || i == layoutManager.findFirstVisibleItemPosition()) {
                    minDistance = leastDifference;
                    returnView = view;
                } else {
                    notFound = false;
                }
            }
            return returnView;
        }
    }

    @Override
    public void smoothScrollBy(int dx, int dy) {
        super.smoothScrollBy(dx, dy);

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                ViewHolder rightHolder = findViewHolderForAdapterPosition(mCenterHolderPosition + 1);

                if (rightHolder != null) {
                    if (Build.VERSION.SDK_INT < 16) {
                        getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                    onRightItemSnap(mCenterHolderPosition + 1, rightHolder);
                }
            }
        });

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                ViewHolder leftHolder = findViewHolderForAdapterPosition(mCenterHolderPosition - 1);

                if (leftHolder != null) {
                    if (Build.VERSION.SDK_INT < 16) {
                        getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                    onLeftItemSnap(mCenterHolderPosition - 1, leftHolder);
                }
            }
        });
    }

    /**
     * This method is used to notify snapped left holder to subscribed listeners
     *
     * @param childLayoutPosition
     * @param leftHolder
     */
    private void onLeftItemSnap(int childLayoutPosition, ViewHolder leftHolder) {
        for (SnapRecyclerViewListener listener : mListeners) {
            listener.onLeftItemSnapped(childLayoutPosition, leftHolder);
        }
    }

    /**
     * This method is used to notify snapped right holder to subscribed listeners
     *
     * @param childLayoutPosition
     * @param rightHolder
     */
    private void onRightItemSnap(int childLayoutPosition, ViewHolder rightHolder) {
        for (SnapRecyclerViewListener listener : mListeners) {
            listener.onRightItemSnapped(childLayoutPosition, rightHolder);
        }
    }


    /**
     * Callback listener for snapping elements in the recyclerview
     */
    public interface SnapRecyclerViewListener {

        void onCenterItemSnapped(int centerHolderPosition, ViewHolder centerHolder, SwipeType scrollType);

        void onLeftItemSnapped(int holderPosition, ViewHolder leftHolder);

        void onRightItemSnapped(int holderPosition, ViewHolder rightHolder);
    }

    public enum SwipeType {
        SWIPE, TAP;
    }
}