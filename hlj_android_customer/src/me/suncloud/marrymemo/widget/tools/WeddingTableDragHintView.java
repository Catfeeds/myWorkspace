package me.suncloud.marrymemo.widget.tools;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljnotelibrary.views.widgets.RippleView;

import me.suncloud.marrymemo.R;

/**
 * 宾客桌子dragHintView
 * Created by chen_bin on 2017/9/25 0025.
 */
public class WeddingTableDragHintView extends ViewGroup {
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

    private final static int mRippleAlpha = 100; //水波纹起始透明度

    public WeddingTableDragHintView(Context context) {
        this(context, null);
    }

    public WeddingTableDragHintView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeddingTableDragHintView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mRadius = CommonUtil.dp2px(context, 14);
        mInnerRadius = CommonUtil.dp2px(context, 10);
        mRippleMaxRadius = CommonUtil.dp2px(context, 20);
        mRippleMinRadius = mInnerRadius + (mRadius - mInnerRadius) / 2;
        mImgTopDistance = mRadius + CommonUtil.dp2px(context, 10);
        srcBitmap = BitmapFactory.decodeResource(getResources(),
                R.mipmap.image_bg_wedding_table_drag_hint);
        mPaint = new Paint();
        mPaint.setDither(true);
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0, size = getChildCount(); i < size; i++) {
            View child = getChildAt(i);
            child.layout(0, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
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
                mCenterX - srcBitmap.getWidth() / 2,
                mCenterY + mImgTopDistance,
                mPaint);
    }

    public WeddingTableDragHintView setCenterPoint(int centerX, int centerY) {
        mCenterX = centerX;
        mCenterY = centerY - (srcBitmap.getHeight() + mImgTopDistance) / 2;
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