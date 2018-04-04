package com.hunliji.hljnotelibrary.views.widgets;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljnotelibrary.R;

/**
 * 笔记详情收藏提示hint
 * Created by chen_bin on 2017/9/25 0025.
 */
public class NoteCollectHintView extends RelativeLayout {
    private Paint mPaint;
    private Bitmap srcBitmap;
    private RippleView mRippleView;

    private int mRippleMaxRadius;//水波纹最大半径
    private int mRippleMinRadius;//水波纹最小半径

    private int mRadius;//外圆半径
    private int mInnerRadius;//内圆半径
    private int mImgTopDistance; //图片距离中心点的偏移量
    private int mCenterX;
    private int mCenterY;
    private int height;
    private int width;
    private int xDistance;

    private final static int mRippleAlpha = 100; //水波纹起始透明度

    public NoteCollectHintView(Context context) {
        this(context, null);
    }

    public NoteCollectHintView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NoteCollectHintView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        height = CommonUtil.dp2px(context, 100);
        width = CommonUtil.getDeviceSize(context).x / 3 * 2;
        xDistance = CommonUtil.getDeviceSize(context).x - width;
        mRadius = CommonUtil.dp2px(context, 14);
        mInnerRadius = CommonUtil.dp2px(context, 10);
        mRippleMaxRadius = CommonUtil.dp2px(context, 20);
        mRippleMinRadius = mInnerRadius + (mRadius - mInnerRadius) / 2;
        mImgTopDistance = mRadius + CommonUtil.dp2px(context, 10);
        try {
            srcBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.image_hint_note_collect);
        }catch (Exception e){
            srcBitmap=null;
            e.printStackTrace();
        }
        mPaint = new Paint();
        mPaint.setDither(true);
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if(srcBitmap==null){
            return;
        }
        //外圆
        mPaint.setColor(Color.parseColor("#00000000"));
        canvas.drawCircle(mCenterX, mCenterY, mRadius, mPaint);
        //内圆
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.parseColor("#f2f83244"));
        canvas.drawCircle(mCenterX, mCenterY, mInnerRadius, mPaint);
        //图片
        mPaint.setColor(Color.WHITE);
        canvas.drawBitmap(srcBitmap,
                mCenterX - srcBitmap.getWidth() / 6,
                mCenterY - srcBitmap.getHeight() - mImgTopDistance,
                mPaint);

    }

    public NoteCollectHintView setCenterPoint(int centerX, int centerY) {
        mCenterX = centerX - xDistance;
        mCenterY = height - centerY;
        return this;
    }

    public void addRippleView() {
        if (getChildCount() > 0) {
            mRippleView = (RippleView) getChildAt(getChildCount() - 1);
        } else {
            mRippleView = new RippleView(getContext());
            mRippleView.setColor(Color.parseColor("#f83244"));
            mRippleView.setCenterPoint(mCenterX, mCenterY);
            mRippleView.setRepeatCount(ObjectAnimator.INFINITE);
            mRippleView.initAnimator(mRippleMinRadius, mRippleMaxRadius, mRippleAlpha);
            addView(mRippleView);
        }
    }

}