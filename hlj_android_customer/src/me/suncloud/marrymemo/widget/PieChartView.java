package me.suncloud.marrymemo.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import me.suncloud.marrymemo.R;

/**
 * Created by Suncloud on 2015/5/11.
 */
public class PieChartView extends View {

    private static final int PIE_CHART_BACKGROUND_COLOR = 0x55ffffff;
    private static final int PIE_CHART_COLOR = 0xf4ffffff;
    private int mBackgroundColor = PIE_CHART_BACKGROUND_COLOR;
    private int mColor = PIE_CHART_COLOR;
    private Paint mBackgroundColorPaint;
    private Paint mColorPaint;
    private RectF oval;
    private float proportion;

    public PieChartView(Context context) {
        super(context);
        init(context, null);
    }

    public PieChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PieChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PieChartView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.PieChartView, 0, 0);
            try {
                mBackgroundColor = ta.getColor(R.styleable.PieChartView_pieBackgroundColor, PIE_CHART_BACKGROUND_COLOR);
                mColor = ta.getColor(R.styleable.PieChartView_pieColor, PIE_CHART_COLOR);
            } finally {
                ta.recycle();
            }
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(widthSize, heightSize);
    }

    public void setProportion(float proportion){
        this.proportion=proportion;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mBackgroundColorPaint == null) {
            mBackgroundColorPaint = new Paint();
            mBackgroundColorPaint.setColor(mBackgroundColor);
            mBackgroundColorPaint.setAntiAlias(true);
        }
        if (mColorPaint == null) {
            mColorPaint = new Paint();
            mColorPaint.setColor(mColor);
            mColorPaint.setAntiAlias(true);
        }
        int cx = getWidth() / 2;
        int cy = getHeight() / 2;
        int radius = Math.min(cx, cy);
        canvas.drawCircle(cx, cy, radius,
                mBackgroundColorPaint);
        if(oval==null) {
            oval = new RectF(cx - radius, cy - radius, cx + radius, cy + radius);
        }
        canvas.drawArc(oval, -90, proportion, true, mColorPaint);
    }
}
