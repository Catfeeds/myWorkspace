package me.suncloud.marrymemo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.TextView;

import java.util.ArrayList;

import me.suncloud.marrymemo.R;

public class FlowLayoutForTextView extends ViewGroup {
    private int mHorizontalSpacing;
    private int mVerticalSpacing;
    private OnChildClickedListener onChildClickedListener;
    private OnMeasureDoneListener onMeasureDoneListener;
    private Paint mPaint;
    private ArrayList<TextView> textViewChildren;
    private int lines;

    public FlowLayoutForTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FlowLayout);
        try {
            mHorizontalSpacing = a.getDimensionPixelSize(R.styleable
                    .FlowLayout_horizontalSpacing, 0);
            mVerticalSpacing = a.getDimensionPixelSize(R.styleable.FlowLayout_verticalSpacing, 0);
        } finally {
            a.recycle();
        }

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(0xffff0000);
        mPaint.setStrokeWidth(2.0f);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        lines = 1;
        int widthSize = MeasureSpec.getSize(widthMeasureSpec) - getPaddingRight();
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        boolean growHeight = widthMode != MeasureSpec.UNSPECIFIED;

        int width = 0;
        int height = getPaddingTop();
        int totalheight = 0;


        int currentWidth = getPaddingLeft();
        int currentHeight = 0;

        boolean breakLine = false;
        boolean newLine = false;
        int spacing = 0;


        final int count = getChildCount();
        if (count == 0) {
            setMeasuredDimension(0, 0);
            return;
        }
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);

            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            spacing = mHorizontalSpacing;
            //if (lp.horizontalSpacing >= 0) {
            //spacing = lp.horizontalSpacing;
            //}

            if (growHeight && (breakLine || currentWidth + child.getMeasuredWidth() > widthSize)) {
                newLine = true;

                //高度换一行了
                height += currentHeight + mVerticalSpacing;
                width = Math.max(width, currentWidth - spacing);

                lines++;

                //currentHeight = 0;
                currentWidth = getPaddingLeft();

                // 用来通报这里有多行,该干什么干什么
                if (this.onMeasureDoneListener != null) {
                    onMeasureDoneListener.onMeasureDone();
                }
            } else {
                newLine = false;
            }

            lp.x = currentWidth;
            lp.y = height;

            currentWidth += child.getMeasuredWidth() + spacing;
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
            child.layout(lp.x, lp.y, lp.x + child.getMeasuredWidth(), lp.y + child
                    .getMeasuredHeight());
        }
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        boolean more = super.drawChild(canvas, child, drawingTime);
        LayoutParams lp = (LayoutParams) child.getLayoutParams();
        if (lp.horizontalSpacing > 0) {
            float x = child.getRight();
            float y = child.getTop() + child.getHeight() / 2.0f;
            canvas.drawLine(x, y - 4.0f, x, y + 4.0f, mPaint);
            canvas.drawLine(x, y, x + lp.horizontalSpacing, y, mPaint);
            canvas.drawLine(x + lp.horizontalSpacing, y - 4.0f, x + lp.horizontalSpacing, y + 4.0f, mPaint);
        }
        if (lp.breakLine) {
            float x = child.getRight();
            float y = child.getTop() + child.getHeight() / 2.0f;
            canvas.drawLine(x, y, x, y + 6.0f, mPaint);
            canvas.drawLine(x, y + 6.0f, x + 6.0f, y + 6.0f, mPaint);
        }
        return more;
    }

    @Override
    public void addView(final View child) {
        super.addView(child);
        if (child instanceof CheckedTextView) {
            if (textViewChildren == null) {
                textViewChildren = new ArrayList<>();
            }
            child.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onChildClickedListener != null) {
                        onChildClickedListener.onChildClicked(child, textViewChildren.isEmpty() ?
                                0 : textViewChildren.indexOf(child));
                        // 如果是checked控件则使清空其他控件的选择状态
                        if (child instanceof CheckedTextView) {
                            for (int i = 0; i < textViewChildren.size(); i++) {
                                if (textViewChildren.get(i) instanceof CheckedTextView) {
                                    CheckedTextView ctv = (CheckedTextView) textViewChildren.get(i);
                                    if (ctv != child) {
                                        ctv.setChecked(false);
                                    }
                                }
                            }
                            ((CheckedTextView) child).setChecked(true);
                        }
                    }
                }
            });
            textViewChildren.add((CheckedTextView) child);
        }
    }

    public void removeAllChildViews() {
        removeAllViews();
        if (textViewChildren != null) {
            textViewChildren.clear();
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

    public void setOnChildClickedListener(OnChildClickedListener onChildClickedListener) {
        this.onChildClickedListener = onChildClickedListener;
    }

    public void setOnMeasureDoneListener(OnMeasureDoneListener onMeasureDoneListener) {
        this.onMeasureDoneListener = onMeasureDoneListener;
    }

    public void setCheckedAt(int position, boolean checked) {
        if (textViewChildren.get(position) instanceof CheckedTextView) {
            CheckedTextView ctv = (CheckedTextView) textViewChildren.get(position);
            ctv.setChecked(checked);
        }
    }

    public interface OnChildClickedListener {
        public void onChildClicked(View childView, int index);
    }

    public interface OnMeasureDoneListener{
        public void onMeasureDone();
    }

    public static class LayoutParams extends ViewGroup.LayoutParams {
        public int horizontalSpacing;
        public boolean breakLine;
        int x;
        int y;

        public LayoutParams(Context context, AttributeSet attrs) {
            super(context, attrs);
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable
                    .FlowLayout_LayoutParams);
            try {
                horizontalSpacing = a.getDimensionPixelSize(R.styleable
                        .FlowLayout_LayoutParams_layout_horizontalSpacing, -1);
                breakLine = a.getBoolean(R.styleable.FlowLayout_LayoutParams_layout_breakLine,
                        false);
            } finally {
                a.recycle();
            }
        }

        public LayoutParams(int w, int h) {
            super(w, h);
        }
    }

    public int getLines() {
        return lines;
    }
}
