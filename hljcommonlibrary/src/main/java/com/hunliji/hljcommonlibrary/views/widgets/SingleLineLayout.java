package com.hunliji.hljcommonlibrary.views.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by Suncloud on 2015/7/13.
 */
public class SingleLineLayout extends LinearLayout {
    public SingleLineLayout(Context context) {
        super(context);
    }

    public SingleLineLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SingleLineLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SingleLineLayout(
            Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec) - getPaddingRight();
        if (widthSize == 0) {
            return;
        }
        int currentWidth = getPaddingLeft();

        int count = getChildCount();

        if (count == 0) {
            setMeasuredDimension(0, 0);
            return;
        }
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
            int childWidth = child.getMeasuredWidth() + layoutParams.leftMargin + layoutParams
                    .rightMargin;
            if (currentWidth + childWidth >= widthSize) {
                removeView(child);
                count--;
                i--;
            } else {
                currentWidth += childWidth;
            }
        }
    }
}
