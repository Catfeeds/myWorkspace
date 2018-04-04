package com.hunliji.hljcommonlibrary.views.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.R;

public class CusImage extends View {

    public TextView value;
    public float temp;
    float sweepAngle;
    RectF rect;
    int pix = 0;
    private Paint myPaint;

    public CusImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CusImage(Context context) {
        super(context);
        init();
    }

    private void init() {

        myPaint = new Paint();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        pix = Math.round(dm.density * 55);

        myPaint.setAntiAlias(true);
        myPaint.setStyle(Paint.Style.STROKE);
        myPaint.setColor(getResources().getColor(R.color.colorPrimary));  //Edit this to change progress arc color.
        myPaint.setStrokeWidth(2 * dm.density);


        float startX = (float) (pix * 0.05);
        float endX = (float) (pix * 0.95);
        float startY = (float) (pix * 0.05);
        float endY = (float) (pix * 0.95);
        rect = new RectF(startX, startY, endX, endY);
    }

    public void progress(int progress) {
        sweepAngle = (float) (progress * 3.6);
        invalidate();

    }

    public void reset() {
        sweepAngle = 0;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int desiredWidth = pix;
        int desiredHeight = pix;
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;


        if (widthMode == MeasureSpec.EXACTLY) {

            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {

            width = Math.min(desiredWidth, widthSize);
        } else {

            width = desiredWidth;
        }


        if (heightMode == MeasureSpec.EXACTLY) {

            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {

            height = Math.min(desiredHeight, heightSize);
        } else {

            height = desiredHeight;
        }


        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawArc(rect, -90, sweepAngle, false, myPaint);
    }
}
