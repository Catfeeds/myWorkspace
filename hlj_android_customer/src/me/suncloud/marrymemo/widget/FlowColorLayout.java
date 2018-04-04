package me.suncloud.marrymemo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton;

import java.util.ArrayList;

import me.suncloud.marrymemo.R;

public class FlowColorLayout extends ViewGroup {
    private int mHorizontalSpacing;
    private int mVerticalSpacing;
    private OnChildCheckedChangeListener onChildCheckedChangeListener;
    private Paint mPaint;
    private ArrayList<CompoundButton> checkedChilds;

    public FlowColorLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FlowLayout);
        try {
            mHorizontalSpacing = a.getDimensionPixelSize(R.styleable.FlowLayout_horizontalSpacing, 0);
            mVerticalSpacing = a.getDimensionPixelSize(R.styleable.FlowLayout_verticalSpacing, 0);
        } finally {
            a.recycle();
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int count = getChildCount();
        if (count == 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        int widthSize = MeasureSpec.getSize(widthMeasureSpec) - getPaddingRight();
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        boolean growHeight = widthMode != MeasureSpec.UNSPECIFIED;

        View childAt = getChildAt(0);
        measureChild(childAt, widthMeasureSpec, heightMeasureSpec);
        int childWidth = getChildAt(0).getMeasuredWidth();
        //计算宽度间距7等分
        int spacing = (MeasureSpec.getSize(widthMeasureSpec)-childWidth*6)/7;
        //计算高度间距5等分
        int spacingH = (MeasureSpec.getSize(heightMeasureSpec)-childWidth*4)/5;
        int width = 0;
        int height = spacingH;
        int currentWidth = spacing;
        int currentHeight = 0;

        boolean breakLine = false;
        boolean newLine;
        int linePosition = 1;

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            spacing=(MeasureSpec.getSize(widthMeasureSpec)-child.getMeasuredWidth()*6)/7;
            if (growHeight && (breakLine || currentWidth + child.getMeasuredWidth() >= widthSize)) {
                newLine = true;
                //高度换一行了
                height += currentHeight + spacingH;
                width = Math.max(width, currentWidth - spacing);
                //currentHeight = 0;
                currentWidth = spacing;
                linePosition = linePosition+1;
            } else {
                newLine = false;
            }

            //偶数行第一位进行缩进处理，缩进距离为圆的半径和1/2等分间距之和
            if (newLine&&linePosition%2==0){
                int margin = (childWidth+spacing)/2;
                lp.x = spacing+margin;
                currentWidth += childWidth + spacing+margin;
            }else {
                lp.x = currentWidth;
                currentWidth += childWidth + spacing;
            }
            lp.y = height;

            currentHeight = Math.max(currentHeight, child.getMeasuredHeight());

            breakLine = lp.breakLine;
        }

        //if (!newLine)
        {
            height += currentHeight;
            width = Math.max(width, currentWidth - spacing);
        }

        width += getPaddingRight();
        height += getPaddingBottom();
        int mDestW = resolveSize(width, widthMeasureSpec);
        int mDestH = resolveSize(height, heightMeasureSpec);
        setMeasuredDimension(mDestW, mDestH);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            child.layout(lp.x, lp.y, lp.x + child.getMeasuredWidth(), lp.y + child.getMeasuredHeight());
        }
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        boolean more = super.drawChild(canvas, child, drawingTime);
        return more;
    }

    public void addView2(@NonNull CompoundButton checkBox, ViewGroup.LayoutParams layoutParams) {
        super.addView(checkBox, layoutParams);
        if (checkedChilds == null) {
            checkedChilds = new ArrayList<>();
        }
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (checkedChilds != null && !checkedChilds.isEmpty()) {
                    for (CompoundButton checkBox : checkedChilds) {
                        if (b && !checkBox.equals(compoundButton)) {
                            checkBox.setChecked(false);
                        } else if (!b) {
                            if (checkBox.isChecked()) {
                                return;
                            }
                        }
                    }
                    if (onChildCheckedChangeListener != null) {
                        onChildCheckedChangeListener.onCheckedChange(compoundButton, checkedChilds.isEmpty() ? 0 : checkedChilds.indexOf(compoundButton));
                    }
                }
            }
        });
        checkedChilds.add(checkBox);
    }

    @Override
    public void addView(@NonNull View child) {
        super.addView(child);
        if (child instanceof CheckBox) {
            if (checkedChilds == null) {
                checkedChilds = new ArrayList<>();
            }
            ((CheckBox) child).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        if (checkedChilds != null && !checkedChilds.isEmpty()) {
                            for (CompoundButton checkBox : checkedChilds) {
                                checkBox.setChecked(checkBox.equals(compoundButton));
                            }
                        }
                        compoundButton.setEnabled(false);
                        if (onChildCheckedChangeListener != null) {
                            onChildCheckedChangeListener.onCheckedChange(compoundButton, checkedChilds.isEmpty() ? 0 : checkedChilds.indexOf(compoundButton));
                        }
                    }else{
                        compoundButton.setEnabled(true);
                    }
                }
            });
            if (checkedChilds.isEmpty()) {
                ((CheckBox) child).setChecked(true);
            }
            checkedChilds.add((CompoundButton) child);
        }
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p.width, p.height);
    }

    public void setOnChildCheckedChangeListener(OnChildCheckedChangeListener onChildCheckedChangeListener) {
        this.onChildCheckedChangeListener = onChildCheckedChangeListener;
    }

    public void setCheckedChild(int index) {
        if (checkedChilds != null && (checkedChilds.size() > index)) {
            checkedChilds.get(index).setChecked(true);
        }
    }

    public int getCheckedIndex() {
        if (checkedChilds != null && !checkedChilds.isEmpty()) {
            for (CompoundButton checkBox : checkedChilds) {
                if (checkBox.isChecked()) {
                    return checkedChilds.indexOf(checkBox);
                }
            }
        }
        return 0;
    }

    public interface OnChildCheckedChangeListener {
        public void onCheckedChange(View childView, int index);
    }

    public static class LayoutParams extends ViewGroup.LayoutParams {
        public int horizontalSpacing;
        public boolean breakLine;
        int x;
        int y;

        public LayoutParams(Context context, AttributeSet attrs) {
            super(context, attrs);
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FlowLayout_LayoutParams);
            try {
                horizontalSpacing = a.getDimensionPixelSize(R.styleable.FlowLayout_LayoutParams_layout_horizontalSpacing, -1);
                breakLine = a.getBoolean(R.styleable.FlowLayout_LayoutParams_layout_breakLine, false);
            } finally {
                a.recycle();
            }
        }

        public LayoutParams(int w, int h) {
            super(w, h);
        }
    }
}
