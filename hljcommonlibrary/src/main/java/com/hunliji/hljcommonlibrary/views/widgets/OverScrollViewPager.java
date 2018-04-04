package com.hunliji.hljcommonlibrary.views.widgets;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

public class OverScrollViewPager extends OverscrollContainer<ViewPager> {

    public OverScrollViewPager(Context context) {
        this(context, null);
    }

    public OverScrollViewPager(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OverScrollViewPager(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private boolean overable;

    public void setOverable(boolean overable) {
        this.overable = overable;
    }

    @Override
    protected boolean canOverscrollAtStart() {
        ViewPager viewPager = getOverscrollView();
        PagerAdapter adapter = viewPager.getAdapter();
        if (null != adapter) {
            if (viewPager.getCurrentItem() == 0) {
                return true;
            }
            return false;
        }

        return false;
    }

    @Override
    protected boolean canOverscrollAtEnd() {
        ViewPager viewPager = getOverscrollView();
        PagerAdapter adapter = viewPager.getAdapter();
        if (null != adapter && adapter.getCount() > 0) {
            if (viewPager.getCurrentItem() == adapter.getCount() - 1) {
                return true;
            }
            return false;
        }

        return false;
    }

    @Override
    protected OverscrollContainer.OverscrollDirection getOverscrollDirection() {
        return overable ? OverscrollContainer.OverscrollDirection.Horizontal : OverscrollDirection.None;
    }

    @Override
    protected ViewPager createOverscrollView() {
        ViewPager viewPager = new ViewPager(getContext());
        return viewPager;
    }

}
