package com.hunliji.hljcommonlibrary.views.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.hunliji.hljcommonlibrary.R;

import java.util.ArrayList;


/**
 * Created by Suncloud on 2016/1/4.
 */
public class RecordAmplitudeView extends View {

    private static final int DEFAULT_COLOR = 0xff705e;
    private static final int DEFAULT_WIDTH = 12;
    private static final int DEFAULT_NUM = 10;
    private int mColor = DEFAULT_COLOR;
    private int mNum = DEFAULT_NUM;
    private float mWidth = DEFAULT_WIDTH;
    private float mSpace = DEFAULT_WIDTH;
    private float mStart = 0f;
    private ArrayList<Float> floats;
    private boolean isLeft;
    private Paint mPaint;
    private int widthSize;
    private int heightSize;
    private int mode;
    private int index;

    public RecordAmplitudeView(Context context) {
        this(context, null);
    }

    public RecordAmplitudeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecordAmplitudeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        floats = new ArrayList<>();
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RecordAmplitudeView,
                0, 0);
        try {
            mNum = ta.getInt(R.styleable.RecordAmplitudeView_bar_num, DEFAULT_NUM);
            isLeft = ta.getBoolean(R.styleable.RecordAmplitudeView_left, false);
            mColor = ta.getColor(R.styleable.RecordAmplitudeView_bar_color, DEFAULT_COLOR);
            mWidth = ta.getDimension(R.styleable.RecordAmplitudeView_bar_width, DEFAULT_WIDTH);
            mSpace = ta.getDimension(R.styleable.RecordAmplitudeView_bar_space, DEFAULT_WIDTH);
            mStart = ta.getFloat(R.styleable.RecordAmplitudeView_start, 0f);
        } finally {
            ta.recycle();
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        widthSize = MeasureSpec.getSize(widthMeasureSpec) - getPaddingRight() - getPaddingLeft();
        heightSize = MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop() - getPaddingBottom();
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        widthSize = w - getPaddingRight() - getPaddingLeft();
        heightSize = h - getPaddingTop() - getPaddingBottom();
        invalidate();
    }

    public void addFloat(float f) {
        if (floats == null) {
            floats = new ArrayList<>();
        }
        floats.add(0, f);
        mode = 0;
        invalidate();
    }

    public void setIndex(int index) {
        this.index = index;
        mode = 1;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mPaint == null) {
            mPaint = new Paint();
            mPaint.setStrokeWidth(mWidth);
            mPaint.setColor(mColor);
            mPaint.setAntiAlias(true);
        }
        int size = floats == null ? 0 : floats.size();
        for (int i = 0; i < mNum; i++) {
            float f = (mode == 0 ? (size > i ? floats.get(i) : 0) :(size>index-i&&index-i>=0?floats.get(size-(index-i)-1):0)) * (1 - mStart) + mStart;
            float x, startY, stopY;
            startY = (1 - f) * heightSize / 2;
            stopY = (1 + f) * heightSize / 2;
            x = isLeft ? (widthSize - i * mSpace - (i + 1) * mWidth + getPaddingRight()) : (mWidth * (i + 1) + mSpace * i + getPaddingLeft());
            canvas.drawLine(x, startY, x, stopY, mPaint);
        }
        super.onDraw(canvas);
    }
}
