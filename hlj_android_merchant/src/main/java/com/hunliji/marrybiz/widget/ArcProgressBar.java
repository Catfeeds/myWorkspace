package com.hunliji.marrybiz.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.Util;

/**
 * 进度
 * Created by chen_bin on 2016/8/30 0030.
 */
public class ArcProgressBar extends View {

    private Context context;
    private Paint paint;
    private float cx, cy;
    private float dx, dy;
    private float x0, y0, x1, y1, x2, y2, x3, y3;
    private float x00, y00, x01, y01, x02, y02;
    private float progress;
    private float outRadius;
    private float inRadius;
    private float circleRadius;
    private float inPaintWidth;
    private float outPaintWidth;
    private float transWidth;
    private float lineWidth;
    private float bitmapWidth;
    private float bitmapHeight;
    private float textSize;
    private RectF outRectF;
    private RectF inRectF;
    private Bitmap srcBitmap;
    private PaintFlagsDrawFilter pdf;

    public ArcProgressBar(Context context) {
        this(context, null);
    }

    public ArcProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArcProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        Point point = JSONUtil.getDeviceSize(context);
        float density = getResources().getDisplayMetrics().density;
        this.outPaintWidth = Math.round(density * 2.0f);
        this.circleRadius = Math.round(density * 3.0f);
        this.inPaintWidth = Math.round(density * 8.0f);
        this.transWidth = Math.round(density * 5.0f);
        this.lineWidth = Math.round(density * 1.0f);
        this.outRadius = Math.round(point.x * 9.0f / 16.0f) / 2.0f - circleRadius;
        this.inRadius = outRadius - transWidth - inPaintWidth / 2.0f - outPaintWidth / 2.0f;
        this.srcBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_square);
        this.bitmapWidth = srcBitmap.getWidth();
        this.bitmapHeight = srcBitmap.getHeight();
        this.textSize = Util.sp2px(context, 7.0f);
        this.outRectF = new RectF(circleRadius,
                circleRadius,
                outRadius * 2 + circleRadius,
                outRadius * 2 + circleRadius);
        this.inRectF = new RectF(circleRadius + outPaintWidth / 2.0f + transWidth + inPaintWidth
                / 2.0f,
                circleRadius + outPaintWidth / 2.0f + transWidth + inPaintWidth / 2.0f,
                inRadius * 2 + circleRadius + outPaintWidth / 2.0f + transWidth + inPaintWidth /
                        2.0f,
                inRadius * 2 + circleRadius + outPaintWidth / 2.0f + transWidth + inPaintWidth /
                        2.0f);
        drawLine(getMax() / 3.0f);
        drawLine(getMax() * 2.0f / 3.0f);
        drawText(getMax() / 6.0f);
        drawText(getMax() / 2.0f);
        drawText(getMax() * 5.0f / 6.0f);
        this.pdf = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        this.paint = new Paint();
        paint.setDither(true);
        paint.setAntiAlias(true);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(Math.round(outRadius * 2 + circleRadius * 2),
                Math.round(outRadius + circleRadius + Math.max(circleRadius,
                        bitmapWidth / 2.0f)) + 10);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        canvas.setDrawFilter(pdf);
        //out un_reach arc
        paint.setStrokeWidth(outPaintWidth);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(0x21ffffff);
        canvas.drawArc(outRectF, 6, -192, false, paint);

        //out reach arc
        paint.setStrokeWidth(outPaintWidth);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(0x7fffffff);
        canvas.drawArc(outRectF, -186, getProgress(), false, paint);

        //circle
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawCircle(cx, cy, circleRadius, paint);

        //in arc
        paint.setStrokeWidth(inPaintWidth);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(0x33ffffff);
        canvas.drawArc(inRectF, 6, -192, false, paint);

        //draw line
        paint.setColor(0x66ffffff);
        paint.setStrokeWidth(lineWidth);
        canvas.drawLine(x0, y0, x1, y1, paint);
        canvas.drawLine(x2, y2, x3, y3, paint);

        //draw text
        paint.setStrokeWidth(0);
        paint.setTextSize(textSize);
        paint.setColor(0x66ffffff);

        //first text
        canvas.save();
        canvas.translate(x00, y00);
        canvas.rotate(-(getMax() / 3.0f));
        canvas.drawText(context.getString(R.string.label_bad), 0, 0, paint);
        canvas.restore();

        //second text
        canvas.save();
        canvas.translate(x01, y01);
        canvas.drawText(context.getString(R.string.label_general), 0, 0, paint);
        canvas.restore();

        //third text
        canvas.save();
        canvas.translate(x02, y02);
        canvas.rotate(getMax() / 3.0f);
        canvas.drawText(context.getString(R.string.label_good), 0, 0, paint);
        canvas.restore();

        //draw square
        paint.setColor(Color.WHITE);
        canvas.save();
        canvas.translate(dx, dy);
        canvas.rotate(progress - 6);
        canvas.drawBitmap(srcBitmap, -bitmapWidth / 2.0f, -bitmapHeight / 2.0f, paint);
        canvas.restore();
    }

    public synchronized float getMax() {
        return 192.0f;
    }

    public synchronized float getProgress() {
        return progress;
    }

    public synchronized void setProgress(float progress) {
        if (progress < 0) {
            throw new IllegalArgumentException("progress not less than 0");
        }
        if (progress > getMax()) {
            progress = getMax();
        }
        if (progress <= getMax()) {
            this.progress = progress;
            double sweepAngle = (progress - 6) * Math.PI / 180;
            this.cx = (float) (outRadius + circleRadius - outRadius * Math.cos(sweepAngle));
            this.cy = (float) (outRadius + circleRadius - outRadius * Math.sin(sweepAngle));
            this.dx = (float) (outRadius + circleRadius - inRadius * Math.cos(sweepAngle));
            this.dy = (float) (outRadius + circleRadius - inRadius * Math.sin(sweepAngle));
            postInvalidate();
        }
    }

    public synchronized void drawLine(float progress) {
        double sweepAngle = (progress - 6) * Math.PI / 180;
        float dx = (float) (outRadius + circleRadius - inRadius * Math.cos(sweepAngle));
        float dy = (float) (outRadius + circleRadius - inRadius * Math.sin(sweepAngle));
        float x0 = (float) (dx - Math.abs((inPaintWidth / 2.0f) * Math.cos(sweepAngle)));
        float y0 = (float) (dy - Math.abs((inPaintWidth / 2.0f) * Math.sin(sweepAngle)));
        float x1 = (float) (dx + Math.abs((inPaintWidth / 2.0f) * Math.cos(sweepAngle)));
        float y1 = (float) (dy + Math.abs((inPaintWidth / 2.0f) * Math.sin(sweepAngle)));
        if (progress < getMax() / 2) {
            this.x0 = x0;
            this.y0 = y0;
            this.x1 = x1;
            this.y1 = y1;
        } else {
            this.x2 = x1;
            this.y2 = y0;
            this.x3 = x0;
            this.y3 = y1;
        }
    }

    public synchronized void drawText(float progress) {
        double d = (progress - 6) * Math.PI / 180;
        float dx = Math.round(outRadius + circleRadius - inRadius * Math.cos(d));
        float dy = Math.round(outRadius + circleRadius - inRadius * Math.sin(d));
        if (progress < getMax() / 2) {
            x00 = (float) (dx + (inPaintWidth / 2.0f + textSize) * Math.abs(Math.cos(d)));
            y00 = (float) (dy + (inPaintWidth / 2.0f + textSize * 2f + bitmapWidth) * Math.sin(d));
        } else if (progress == getMax() / 2) {
            x01 = dx - textSize;
            y01 = dy + inPaintWidth / 2.0f + textSize * 1.5f;
        } else {
            x02 = (float) (dx - (inPaintWidth / 2.0f + 2 * textSize) * Math.abs(Math.cos(d)));
            y02 = (float) (dy + (inPaintWidth / 2.0f - textSize / 2) * Math.sin(d));
        }
    }
}