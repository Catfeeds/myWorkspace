package me.suncloud.marrymemo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import me.suncloud.marrymemo.R;


/**
 * Created by Helen on 2015/5/14.
 */
public class HeartProgressBar extends View {
    private Bitmap mHeartBitmap;//空心图片
    private Bitmap mHeartedBitmap;//实心图
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int progress = 0;//当前进度
    private int max = 100;//最大进度
    private boolean isFinish = false;//是否完成
    private boolean isFillFinish = false;//是否填充完成
    private boolean isEmptyFinish = false;//是否反填充完成
    private boolean isAutoFill = false;//是否自动填充
    private boolean isAutoEmpty = false;//是否自动反填充
    private Drawable emptyDrawable;
    private Drawable fullDrawable;
    final Handler handler = new Handler();

    public HeartProgressBar(Context context) {
        super(context);
        init();
    }

    public HeartProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HeartProgressBar);
        progress = a.getInteger(R.styleable.HeartProgressBar_progress, 0);
        max = a.getInteger(R.styleable.HeartProgressBar_max, 100);
        emptyDrawable = a.getDrawable(R.styleable.HeartProgressBar_src_empty);
        fullDrawable = a.getDrawable(R.styleable.HeartProgressBar_src_full);
        if (emptyDrawable != null) {
            mHeartBitmap = drawableToBitmap(emptyDrawable);
        }
        if (fullDrawable != null) {
            mHeartedBitmap = drawableToBitmap(fullDrawable);
        }
        if (max <= 0) {
            max = 100;
        }
        a.recycle();
    }

    public HeartProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (mHeartBitmap == null) {
            mHeartBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_praise_gray_40_40_3);
        }
        if (mHeartedBitmap == null) {
            mHeartedBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_praise_primary_40_40_3);

        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final int width = getWidth();//mHeartBitmap.getWidth();
        final int height = getHeight();//mHeartBitmap.getHeight();
        float percent = progress * 1.0f / max;//进度百分比
        if (percent >= 1) {
            percent = 1;
        }
        canvas.save();
        //绘制空心图
        canvas.drawBitmap(mHeartBitmap, 0, 0, mPaint);
        //计算绘制实心图的范围
        canvas.clipRect(0, 0, width * percent, height);
        //绘制实心图
        canvas.drawBitmap(mHeartedBitmap, 0, 0, mPaint);
        canvas.restore();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //int widthMode=MeasureSpec.getMode(widthMeasureSpec);
        //if(widthMode==MeasureSpec.AT_MOST){//layout_width=wrap_content
        //设置控件宽高跟图片一样
        setMeasuredDimension(mHeartBitmap.getWidth(), mHeartBitmap.getHeight());
        //}
        //setMeasuredDimension(getMeasureSize(widthMeasureSpec, true), getMeasureSize
        // (heightMeasureSpec, true));
    }

    private int getMeasureSize(int spec, boolean isWidth) {
        int size = MeasureSpec.getSize(spec);
        int mode = MeasureSpec.getMode(spec);
        if (mode == MeasureSpec.AT_MOST) {
            if (isWidth) {
                size = mHeartBitmap.getWidth();
            } else {
                size = mHeartBitmap.getHeight();
            }
        }
        return size;
    }


    /**
     * 设置当前进度
     *
     * @param progress
     */
    public void setProgress(int progress) {
        if (isAutoFill)
            return;
        if (isAutoEmpty)
            return;
        this.progress = progress;
        if (!isFinish) {
            invalidate();
        }
        if (progress >= max) {
            isFinish = true;
        }
    }

    public int getProgress() {
        return progress;
    }

    /**
     * 是否完成
     *
     * @return
     */
    public boolean isFinish() {
        return isFinish;
    }

    public boolean isFillFinish() {
        return isFillFinish;
    }

    public boolean isEmptyFinish() {
        return isEmptyFinish;
    }

    public boolean isAutoFill() {
        return isAutoFill;
    }

    public boolean isAutoEmpty() {
        return isAutoEmpty;
    }

    /**
     * 设置最大进度值
     */
    public void setMax(int max) {
        this.max = max;
    }

    public int getMax() {
        return max;
    }

    /**
     * 开启自动填充
     */
    public void startAutoFill() {
        isFillFinish = false;
        isAutoFill = true;
        isAutoEmpty=false;
        setProgress(0);
        final int step = 10;
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(isAutoEmpty){
                    isAutoFill=false;
                    return;
                }
                progress += step;
                invalidate();
                if (progress >= max) {
                    isFillFinish = true;
                    isAutoFill = false;
                }
                if (!isFillFinish()) {
                    handler.postDelayed(this, 50);
                } else {
                    setProgress(100);
                    handler.removeCallbacks(this);
                }
            }
        });
    }

    /**
     * 开启自动反填充
     */
    public void startAutoEmpty() {
        isEmptyFinish = false;
        isAutoEmpty = true;
        isAutoFill = false;
        setProgress(100);
        final int step = 10;
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(isAutoFill){
                    isAutoEmpty=false;
                    return;
                }
                progress -= step;
                invalidate();
                if (progress <= 0) {
                    isEmptyFinish = true;
                    isAutoEmpty=false;
                }
                if (!isEmptyFinish()) {
                    handler.postDelayed(this, 50);
                } else {
                    setProgress(0);
                    handler.removeCallbacks(this);
                }
            }
        });
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1,
                    1,
                    Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(),
                    Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

}