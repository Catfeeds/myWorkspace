package com.hunliji.marrybiz.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

/**
 * Created by LuoHanLin on 14/12/9.
 * 具有缩放动画的 list header
 */
public class AnimatedHeaderFrameLayout extends FrameLayout{
    public static final String TAG = AnimatedHeaderFrameLayout.class.getSimpleName();
    private int bottomOld = -1;

    public AnimatedHeaderFrameLayout(Context context) {
        super(context);
    }

    public AnimatedHeaderFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AnimatedHeaderFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private String getMeasureSpecTypeString(int measureSpec) {
        switch (MeasureSpec.getMode(measureSpec)) {
            case MeasureSpec.AT_MOST:
                return "AT_MOST";
            case MeasureSpec.EXACTLY:
                return "EXACTLY";
            case MeasureSpec.UNSPECIFIED:
                return "UNSPECIFIED";
            default:
                return "SORT_DEFAULT???";
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.UNSPECIFIED) {
            //Log.i(TAG, "OVERIDE!");
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (bottomOld == -1) {
            bottomOld = bottom;
        } else if (bottomOld < bottom) {
            Log.i(TAG, "onLayout() changed: " + changed + " left: " + left + " top: " + top + " " +
                    "right: " + right + " bottom: " + bottom);
        }

        bottomOld = bottom;
        super.onLayout(changed, left, top, right, bottom);
    }
}
