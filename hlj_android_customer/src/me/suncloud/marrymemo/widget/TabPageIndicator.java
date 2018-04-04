package me.suncloud.marrymemo.widget;

import android.content.Context;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.hunliji.hljcommonlibrary.models.Label;
import com.hunliji.hljcommonlibrary.models.Mark;

import java.util.ArrayList;
import java.util.List;

import me.suncloud.marrymemo.R;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class TabPageIndicator extends HorizontalScrollView {

    private static final CharSequence EMPTY_TITLE = "";
    private int tabViewId;
    private Runnable mTabSelector;
    private LinearLayout mTabLayout;
    private int mSelectedTabIndex;
    private OnTabChangeListener mTabChangeListener;
    private final OnClickListener mTabClickListener = new OnClickListener() {
        public void onClick(View view) {
            TabView tabView = (TabView) view;
            int newSelected = tabView.getIndex();
            if (mSelectedTabIndex != newSelected) {
                setCurrentItem(newSelected);
                if (mSelectedTabIndex == newSelected && mTabChangeListener != null) {
                    mTabChangeListener.onTabChanged(newSelected);
                }
            }
        }
    };

    public TabPageIndicator(Context context) {
        this(context, null);
    }

    public TabPageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        setHorizontalScrollBarEnabled(false);

        mTabLayout = new LinearLayout(context);
        addView(mTabLayout, new ViewGroup.LayoutParams(WRAP_CONTENT, MATCH_PARENT));
    }

    public void setOnTabChangeListener(OnTabChangeListener listener) {
        mTabChangeListener = listener;
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final boolean lockedExpanded = widthMode == MeasureSpec.EXACTLY;
        setFillViewport(lockedExpanded);
        final int oldWidth = getMeasuredWidth();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int newWidth = getMeasuredWidth();

        if (lockedExpanded && oldWidth != newWidth) {
            setCurrentItem(mSelectedTabIndex);
        }
    }

    private void animateToTab(final int position) {
        final View tabView = mTabLayout.getChildAt(position);
        if (mTabSelector != null) {
            removeCallbacks(mTabSelector);
        }
        mTabSelector = new Runnable() {
            public void run() {
                final int scrollPos = tabView.getLeft() - (getWidth() - tabView.getWidth()) / 2;
                smoothScrollTo(scrollPos, 0);
                mTabSelector = null;
            }
        };
        post(mTabSelector);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mTabSelector != null) {
            // Re-post the selector we saved
            post(mTabSelector);
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mTabSelector != null) {
            removeCallbacks(mTabSelector);
        }
    }

    public void setTabViewId(int tabViewId) {
        this.tabViewId = tabViewId;
    }

    public View getTabView(int position) {
        if (position < mTabLayout.getChildCount()) {
            View tabView = mTabLayout.getChildAt(position);
            if (tabView != null && tabView instanceof TabView) {
                return tabView;
            }
        }
        return null;
    }

    public void setTabText(String text, int position) {
        if (position < mTabLayout.getChildCount()) {
            View tabView = mTabLayout.getChildAt(position);
            if (tabView != null && tabView instanceof TabView) {
                ((TabView) tabView).setText(text);
            }
        }
    }

    private void addTab(int index, CharSequence text) {
        if (tabViewId == 0) {
            tabViewId = R.layout.menu_tab_widget;
        }
        TabView tabView = (TabView) inflate(getContext(), tabViewId, null);
        tabView.mIndex = index;
        tabView.setFocusable(true);
        tabView.setOnClickListener(mTabClickListener);
        tabView.setText(text);
        mTabLayout.addView(tabView, new LinearLayout.LayoutParams(0, MATCH_PARENT, 1));
    }

    public void notifyDataSetChanged(ArrayList<? extends Label> items) {
        mTabLayout.removeAllViews();
        int count = items.size();
        for (int i = 0; i < count; i++) {
            CharSequence title = items.get(i)
                    .getName();
            if (TextUtils.isEmpty(title)) {
                title = EMPTY_TITLE;
            }
            addTab(i, title);
        }
        if (mSelectedTabIndex > count) {
            mSelectedTabIndex = count - 1;
        }
        setCurrentItem(mSelectedTabIndex);
        requestLayout();
    }

    public void notifyDataSetChanged(List<? extends Mark> items) {
        mTabLayout.removeAllViews();
        int count = items.size();
        for (int i = 0; i < count; i++) {
            CharSequence title = items.get(i)
                    .getName();
            if (TextUtils.isEmpty(title)) {
                title = EMPTY_TITLE;
            }
            addTab(i, title);
        }
        if (mSelectedTabIndex > count) {
            mSelectedTabIndex = count - 1;
        }
        setCurrentItem(mSelectedTabIndex);
        requestLayout();
    }

    public void setPagerAdapter(FragmentPagerAdapter pagerAdapter) {
        mTabLayout.removeAllViews();
        int count = pagerAdapter.getCount();
        for (int i = 0; i < count; i++) {
            CharSequence title = pagerAdapter.getPageTitle(i);
            if (title == null) {
                title = EMPTY_TITLE;
            }
            addTab(i, title);
        }
        if (mSelectedTabIndex > count) {
            mSelectedTabIndex = count - 1;
        }
        setCurrentItem(mSelectedTabIndex);
        requestLayout();

    }

    public void setPagerAdapter(FragmentStatePagerAdapter pagerAdapter) {
        mTabLayout.removeAllViews();
        int count = pagerAdapter.getCount();
        for (int i = 0; i < count; i++) {
            CharSequence title = pagerAdapter.getPageTitle(i);
            if (title == null) {
                title = EMPTY_TITLE;
            }
            addTab(i, title);
        }
        if (mSelectedTabIndex > count) {
            mSelectedTabIndex = count - 1;
        }
        setCurrentItem(mSelectedTabIndex);
        requestLayout();

    }


    public void setCurrentItem(int item) {
        mSelectedTabIndex = item;
        int tabCount = mTabLayout.getChildCount();
        for (int i = 0; i < tabCount; i++) {
            View child = mTabLayout.getChildAt(i);
            boolean isSelected = (i == item);
            child.setSelected(isSelected);
            if (isSelected) {
                animateToTab(item);
            }
        }
    }

    public interface OnTabChangeListener {
        void onTabChanged(int position);
    }
}