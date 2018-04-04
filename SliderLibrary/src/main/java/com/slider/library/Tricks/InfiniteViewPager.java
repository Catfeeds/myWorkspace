package com.slider.library.Tricks;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Scroller;

/**
 * A {@link ViewPager} that allows pseudo-infinite paging with a wrap-around effect. Should be
 * used with an {@link
 * InfinitePagerAdapter}.
 */
public class InfiniteViewPager extends ViewPager {

    private boolean mIsWrapContentHeight;

    public InfiniteViewPager(Context context) {
        super(context);
    }

    public InfiniteViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        super.setAdapter(adapter);
        // offset first element so that we can scroll to the left
        setCurrentItem(0);
    }

    @Override
    public void setCurrentItem(int item) {
        // offset the current item to ensure there is space to scroll
        if (getAdapter().getCount() > 0) {
            item = getOffsetAmount() + (item % getAdapter().getCount());
            super.setCurrentItem(item);
        }

    }

    public void setmIsWrapContentHeight(boolean mIsWrapContentHeight) {
        this.mIsWrapContentHeight = mIsWrapContentHeight;
    }

    public void nextItem() {
        super.setCurrentItem(getCurrentItem() + 1,true);
    }

    private int getOffsetAmount() {
        if (getAdapter() instanceof InfinitePagerAdapter) {
            InfinitePagerAdapter infAdapter = (InfinitePagerAdapter) getAdapter();
            // allow for 100 back cycles from the beginning
            // should be enough to create an illusion of infinity
            // warning: scrolling to very high values (1,000,000+) results in
            // strange drawing behaviour
            return infAdapter.getRealCount() * 100;
        } else {
            return 0;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mIsWrapContentHeight) {
            int height = 0;

            View view = getChildAt(0);
            if (view != null) {
                view.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0,
                        MeasureSpec.UNSPECIFIED));

                height = view.getMeasuredHeight();
            }

            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);

            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

}