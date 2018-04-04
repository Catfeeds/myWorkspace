package com.edmodo.rangebar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;

import java.util.ArrayList;

/**
 * Created by Suncloud on 2015/4/2.
 */
public class HotelBar {


    // Member Variables ////////////////////////////////////////////////////////

    private final Paint mPaint;

    // Left-coordinate of the horizontal bar.
    private final float mLeftX;
    private final float mRightX;
    private final float mY;
    private float mTickDistance;
    private int size;
    private int mNumSegments;
    private TextPaint mTextPaint;
    private TextPaint mTextPaint2;
    private int disEnableColor;
    private int barColor;
    private int textColor;
    private int textColor2;
    private ArrayList<String> strings;
    private float mBarStrokeWidth;
    private float mBarWeight;
    private float mTextHeight;

    // Constructor /////////////////////////////////////////////////////////////

    HotelBar(Context ctx,
             float x,
             float y,
             float length,
             int tickCount,
             float textHight,
             float BarWeight,
             int BarColor, int textColor, float textSize,int disEnableColor, boolean isEnable, float mBarStrokeWidth,int textColor2) {

        mLeftX = x;
        mRightX = x + length;
        mY = y;

        this.disEnableColor = disEnableColor;
        this.mBarStrokeWidth = mBarStrokeWidth;

        mTextHeight = mY+mBarStrokeWidth/2+textHight;
        mNumSegments = tickCount - 1;
        mTickDistance = length / mNumSegments;
        barColor = BarColor;
        mPaint = new Paint();
        if (mBarStrokeWidth > 0) {
            mBarWeight = BarWeight;
            mPaint.setStrokeWidth(mBarStrokeWidth);
            mPaint.setStyle(Paint.Style.STROKE);
        } else {
            mPaint.setStrokeWidth(BarWeight);
        }
        mPaint.setAntiAlias(true);
        this.textColor = textColor;
        mTextPaint = new TextPaint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(textSize);
        this.textColor2 = textColor2;
        mTextPaint2 = new TextPaint();
        mTextPaint2.setAntiAlias(true);
        mTextPaint2.setTextSize(textSize);
        setEnabled(isEnable);
    }

    public void setStringList(ArrayList<String> strings) {
        if (this.strings == null) {
            this.strings = new ArrayList<>();
        }
        if (strings != null) {
            this.strings.clear();
            this.strings.addAll(strings);
        }
        size = this.strings.size();
    }

    public void setEnabled(boolean enable) {
        if (!enable) {
            mPaint.setColor(disEnableColor);
            mTextPaint.setColor(disEnableColor);
            mTextPaint2.setColor(disEnableColor);
        } else {
            mPaint.setColor(barColor);
            mTextPaint.setColor(textColor);
            mTextPaint2.setColor(textColor2);
        }
    }

    void draw(Canvas canvas, Thumb leftThumb, Thumb rightThumb) {
        if (mBarStrokeWidth > 0) {
            RectF rectF = new RectF();
            rectF.left = mLeftX;
            rectF.top = mY - mBarWeight / 2;
            rectF.right = mRightX;
            rectF.bottom = mY + mBarWeight / 2;
            canvas.drawRoundRect(rectF, mBarWeight / 2, mBarWeight / 2, mPaint);
        } else {
            canvas.drawLine(mLeftX, mY, mRightX, mY, mPaint);
        }
        drawTicks(canvas, leftThumb, rightThumb);
    }

    float getLeftX() {
        return mLeftX;
    }

    float getRightX() {
        return mRightX;
    }

    float getNearestTickCoordinate(Thumb thumb) {

        int nearestTickIndex = getNearestTickIndex(thumb);
        if (nearestTickIndex < 0) {
            nearestTickIndex = 0;
        }
        if (nearestTickIndex > mNumSegments) {
            nearestTickIndex = mNumSegments;
        }

        return mLeftX + (nearestTickIndex * mTickDistance);
    }

    int getNearestTickIndex(Thumb thumb) {
        return (int) ((thumb.getX() - mLeftX + mTickDistance / 2f) / mTickDistance);
    }

    void setTickCount(int tickCount) {

        final float barLength = mRightX - mLeftX;

        mNumSegments = tickCount - 1;
        mTickDistance = barLength / mNumSegments;
    }

    public float getmTickDistance() {
        return mTickDistance;
    }

    private void drawTicks(Canvas canvas, Thumb leftThumb, Thumb rightThumb) {
        if(size>1){
            int width=0;
            for (int i = 0; i < size; i++) {
                float x = mNumSegments *i* mTickDistance / (size - 1)  + mLeftX;
                Rect rect = new Rect();
                String mText = strings.get(i);
                mTextPaint.getTextBounds(mText, 0, mText.length(), rect);
                if(i==0){
                    width=rect.width();
                }
                if(x>Math.max(leftThumb.getX(),rightThumb.getX())||x<Math.min(leftThumb.getX(),rightThumb.getX())) {
                    canvas.drawText(mText, x - rect.width()+width/2, mTextHeight, mTextPaint2);
                }else{
                    canvas.drawText(mText, x - rect.width()+width/2, mTextHeight, mTextPaint);
                }
            }
        }
    }
}
