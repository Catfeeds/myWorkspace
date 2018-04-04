package com.hunliji.hljnotelibrary.views.widgets;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import com.hunliji.hljnotelibrary.interfaces.ITagView;
import com.hunliji.hljnotelibrary.models.Direction;

public class RippleView extends View implements ITagView {


    private int mRadius;
    private int mAlpha;
    private Direction mDirection;
    private Paint mPaint;
    private AnimatorSet mAnimatorSet;
    private int mX, mY;
    private int mColor = Color.WHITE;
    private int mRepeatCount = 1;

    public RippleView(Context context) {
        this(context, null);
    }

    public RippleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RippleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPaint.setDither(true);
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(mColor);
        mPaint.setAlpha(mAlpha);
        canvas.drawCircle(mX, mY, mRadius, mPaint);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopRipple();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startRipple();
    }

    @Override
    public void setMaxWidth(int maxWidth) {

    }

    @Override
    public void setDirection(Direction direction) {
        mDirection = direction;
    }

    @Override
    public Direction getDirection() {
        return mDirection;
    }

    public void startRipple() {
        if (mAnimatorSet != null && !mAnimatorSet.isRunning()) {
            mAnimatorSet.start();
        }
    }

    public void stopRipple() {
        if (mAnimatorSet != null && mAnimatorSet.isRunning()) {
            mAnimatorSet.end();
        }
    }

    //设置水波纹半径
    public void setRippleRadius(int radius) {
        this.mRadius = radius;
        invalidate();
    }

    //设置水波纹 alpha 范围[0-255]
    public void setRippleAlpha(int alpha) {
        this.mAlpha = alpha;
        invalidate();
    }

    public void setCenterPoint(int x, int y) {
        mX = x;
        mY = y;
    }

    public void setRepeatCount(int repeatCount) {
        this.mRepeatCount = repeatCount;
    }

    public void setColor(int color) {
        this.mColor = color;
    }

    public void initAnimator(int minRadius, int maxRadius, int alpha) {
        ObjectAnimator radiusAnim = ObjectAnimator.ofInt(this,
                "RippleRadius",
                minRadius,
                maxRadius);
        radiusAnim.setRepeatMode(ObjectAnimator.RESTART);
        radiusAnim.setRepeatCount(mRepeatCount);
        ObjectAnimator alphaAnim = ObjectAnimator.ofInt(this, "RippleAlpha", alpha, 0);
        alphaAnim.setRepeatMode(ObjectAnimator.RESTART);
        alphaAnim.setRepeatCount(mRepeatCount);
        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.playTogether(radiusAnim, alphaAnim);
        mAnimatorSet.setDuration(1000);
        mAnimatorSet.setInterpolator(new AccelerateInterpolator());
    }
}
