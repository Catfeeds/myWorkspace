package com.hunliji.hunlijicalendar;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by LuoHanLin on 14/11/15.
 * 根据当前显示的元素来决定其高度
 */
public class WrapContentHeightScheduleViewPager extends ViewPager {
    public WrapContentHeightScheduleViewPager(Context context) {
        super(context);
    }

    public WrapContentHeightScheduleViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = 0;

        if (getChildCount() > 0) {
            // 获取当前 item 的视图高度
            View child = ((HLJScheduleCalendarView.MonthAdapter) getAdapter()).getItemAtPosition
                    (getCurrentItem());
            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0,
                    MeasureSpec.UNSPECIFIED));
            height = child.getMeasuredHeight();
        }

        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
