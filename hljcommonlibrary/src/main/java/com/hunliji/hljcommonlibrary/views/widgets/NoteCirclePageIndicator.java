package com.hunliji.hljcommonlibrary.views.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.hunliji.hljcommonlibrary.R;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by mo_yu on 2017/9/28.笔记轮播指示条
 */
public class NoteCirclePageIndicator extends HorizontalScrollView {

    public static final int SMALL_INDICATOR = 0;
    public static final int MIDDLE_INDICATOR = 1;
    public static final int LARGER_INDICATOR = 2;
    private Runnable mTabSelector;
    protected LinearLayout mTabLayout;
    protected int mSelectedTabIndex;
    private Context mContext;
    private int itemWidth;//每个圆点所占区域
    private int showItemCount;//显示圆点数量
    private int mLargerCount;//大点数量，中点和小点固定左右各一位
    private int normalColor;//未选中点颜色
    private int selectColor;//选中的点颜色
    private float lagerRadius;//大点半径
    private float middleRadius;//中点半径
    private float smallRadius;//小点半径

    public NoteCirclePageIndicator(Context context) {
        this(context, null);
    }

    public NoteCirclePageIndicator(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public NoteCirclePageIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setHorizontalScrollBarEnabled(false);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.NoteCirclePageIndicator, 0, 0);
        selectColor = ta.getColor(R.styleable.NoteCirclePageIndicator_select_color,
                ContextCompat.getColor(context, R.color.colorWhite));
        normalColor = ta.getColor(R.styleable.NoteCirclePageIndicator_normal_color,
                Color.parseColor("#dddddd"));
        lagerRadius = ta.getDimension(R.styleable.NoteCirclePageIndicator_lager_radius,
                CommonUtil.dp2px(context, 3));
        middleRadius = ta.getDimension(R.styleable.NoteCirclePageIndicator_middle_radius,
                CommonUtil.dp2px(context, 2.5f));
        smallRadius = ta.getDimension(R.styleable.NoteCirclePageIndicator_small_radius,
                CommonUtil.dp2px(context, 2));
        mContext = context;
        itemWidth = CommonUtil.dp2px(context, 10);
        showItemCount = 0;
        mLargerCount = 3;
        mTabLayout = new LinearLayout(context);
        addView(mTabLayout, new ViewGroup.LayoutParams(WRAP_CONTENT, MATCH_PARENT));
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(itemWidth * showItemCount + getPaddingLeft() + getPaddingRight(),
                heightMeasureSpec);
    }

    private void animateToTab(final int position, final boolean isLeftScroll) {
        final View tabView = mTabLayout.getChildAt(position);
        if (mTabSelector != null) {
            removeCallbacks(mTabSelector);
        }
        mTabSelector = new Runnable() {
            public void run() {
                int scrollPos;
                if (isLeftScroll) {
                    scrollPos = tabView.getLeft() - (getWidth() - getPaddingLeft() -
                            getPaddingRight() - tabView.getWidth() * 3);
                } else {
                    scrollPos = tabView.getRight() - (getWidth() - getPaddingLeft() +
                            getPaddingRight() - tabView.getWidth() * (showItemCount - 1));
                }
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

    public void addTab(int index, int count) {
        NoteCircleView circleView = new NoteCircleView(mContext);
        circleView.setLagerRadius(lagerRadius);
        circleView.setSmallRadius(smallRadius);
        circleView.setMiddleRadius(middleRadius);
        circleView.setNormalColor(normalColor);
        circleView.setSelectColor(selectColor);
        circleView.setFocusable(true);
        circleView.setIndex(index);
        circleView.setState(index < mLargerCount || count <= mLargerCount + 2 ? LARGER_INDICATOR
                : index < mLargerCount + 1 ? MIDDLE_INDICATOR : SMALL_INDICATOR);
        circleView.setTotalCount(count);
        mTabLayout.addView(circleView, new LinearLayout.LayoutParams(itemWidth, MATCH_PARENT));
    }

    public void setPagerAdapter(PagerAdapter pagerAdapter) {
        mSelectedTabIndex = 0;
        mTabLayout.removeAllViews();
        int count = pagerAdapter.getCount();
        if (count <= 1) {
            return;
        } else if (count >= mLargerCount + 2) {
            showItemCount = mLargerCount + 2;
        } else {
            showItemCount = count;
        }
        for (int i = 0; i < count; i++) {
            addTab(i, count);
        }
        if (mSelectedTabIndex > count) {
            mSelectedTabIndex = count - 1;
        }
        setCurrentItem(mSelectedTabIndex);
        requestLayout();
    }

    public void setLargerCount(int largerCount) {
        this.mLargerCount = largerCount;
    }

    public void setCurrentItem(int selectedTabIndex) {
        int tabCount = mTabLayout.getChildCount();
        if (selectedTabIndex > tabCount - 1) {
            selectedTabIndex = tabCount - 1;
        }
        mSelectedTabIndex = selectedTabIndex;
        boolean isChanged = false;
        boolean isLeftScroll = true;
        NoteCircleView selectItem = (NoteCircleView) mTabLayout.getChildAt(selectedTabIndex);
        //原点有大，中，小三种状态，选中点为不为大点时会发生状态变化
        if (selectItem != null && selectItem.getState() != LARGER_INDICATOR) {
            selectItem.setState(LARGER_INDICATOR);
            isChanged = true;
            //获取中点后一项
            NoteCircleView nextItem = (NoteCircleView) mTabLayout.getChildAt(selectedTabIndex + 1);
            if (nextItem == null) {
                //中点后一项取不到，表示已经是最后一项，item显示个数置为初始值mLargerCount+2，向左滑动
                isLeftScroll = true;
                showItemCount = mLargerCount + 2;
                requestLayout();
            } else if (nextItem.getState() == 0) {
                //中点后一项为小点，将小点变为中点，表示向左滑动
                nextItem.setState(MIDDLE_INDICATOR);
                isLeftScroll = true;
                if (selectedTabIndex == tabCount - 2 || selectedTabIndex == mLargerCount) {
                    //向左滑动且选中点为倒数第二项或者第mLargerCount+1项（selectedTabIndex=mLargerCount），
                    // item显示个数为mLargerCount+3
                    showItemCount = mLargerCount + 3;
                } else {
                    //其他情况显示为mLargerCount+4个点
                    showItemCount = mLargerCount + 4;
                }
                requestLayout();
            } else {
                //中点后一项有点且不为小点，表示向右滑动
                if (selectedTabIndex < 1) {
                    showItemCount = mLargerCount + 2;
                } else if (selectedTabIndex == 1 || selectedTabIndex == tabCount - mLargerCount -
                        1) {
                    //向右滑动且选中点为第二项（selectedTabIndex=1）或者倒数第mLargerCount+1项时，
                    // item显示个数为mLargerCount+3
                    showItemCount = mLargerCount + 3;
                } else {
                    //其他情况显示为mLargerCount+4个点
                    showItemCount = mLargerCount + 4;
                }
                requestLayout();
                isLeftScroll = false;
            }
        }
        for (int i = 0; i < tabCount; i++) {
            NoteCircleView child = (NoteCircleView) mTabLayout.getChildAt(i);
            if (isChanged) {
                if (isLeftScroll) {
                    if (i == selectedTabIndex - mLargerCount - 1) {
                        //左滑，选中点左边第mLargerCount+1位变为小点
                        child.setState(SMALL_INDICATOR);
                    } else if (i == selectedTabIndex - mLargerCount) {
                        //左滑，选中点左边第mLargerCount位变为中点
                        child.setState(MIDDLE_INDICATOR);
                    } else if (i >= selectedTabIndex - mLargerCount + 1 && i < selectedTabIndex) {
                        //左滑，选中点左边第mLargerCount-1位到选中点（不包含选中点）变为大点
                        child.setState(LARGER_INDICATOR);
                    } else if (i == selectedTabIndex) {
                        //选中点或者选中点后一位执行滑动动画
                        animateToTab(i, true);
                    }
                } else {
                    if (i == selectedTabIndex + mLargerCount) {
                        child.setState(MIDDLE_INDICATOR);
                    } else if (i == selectedTabIndex + mLargerCount + 1) {
                        child.setState(SMALL_INDICATOR);
                    } else if (i == selectedTabIndex - 1) {
                        child.setState(MIDDLE_INDICATOR);
                        animateToTab(i, false);
                    } else if (i == selectedTabIndex - 2) {
                        child.setState(SMALL_INDICATOR);
                    }
                }
            }
            child.setSelected(i == selectedTabIndex);
            child.invalidate();
        }
    }
}