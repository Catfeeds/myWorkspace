package com.hunliji.hljcommonlibrary.views.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.hunliji.hljcommonlibrary.R;

/**
 * Created by hua_rong on 2017/5/12.
 * 上传进度 progressBar
 */

public class HljUploadProgressBar extends View {

    private int progress;
    private Paint bgPaint;
    private Paint progressPaint;
    private int bgColor;
    private int progressColor;

    public HljUploadProgressBar(Context context) {
        this(context, null);
    }

    public HljUploadProgressBar(
            Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HljUploadProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs,
                    R.styleable.HljUploadProgressBar);
            bgColor = a.getColor(R.styleable.HljUploadProgressBar_bg_color,
                    ContextCompat.getColor(context, R.color.colorGray3));
            progressColor = a.getColor(R.styleable.HljUploadProgressBar_bg_color,
                    ContextCompat.getColor(context, R.color.colorPrimary));
            a.recycle();
        }
        initValue();
    }

    private void initValue() {
        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setColor(bgColor);
        bgPaint.setStyle(Paint.Style.FILL);
        progressPaint = new Paint();
        progressPaint.setColor(progressColor);
        progressPaint.setStyle(Paint.Style.FILL);
        //http://www.jianshu.com/p/d11892bbe055
        progressPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawHorizontalProgressBar(canvas);
    }

    private void drawHorizontalProgressBar(Canvas canvas) {
        int radius = getHeight() / 2;
        RectF rectF = new RectF(0, 0, getWidth(), getHeight());
        canvas.drawRoundRect(rectF, radius, radius, bgPaint);
        RectF progressRectF = new RectF(0, 0, progress * getWidth() / 100, getHeight());
        canvas.drawRoundRect(progressRectF, radius, radius, progressPaint);
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        if (progress > 100) {
            progress = 100;
        } else if (progress < 0) {
            progress = 0;
        }
        this.progress = progress;
        postInvalidate();
    }
}
