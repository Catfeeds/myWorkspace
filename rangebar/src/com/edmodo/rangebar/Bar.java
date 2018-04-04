/*
 * Copyright 2013, Edmodo, Inc. 
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this work except in compliance with the License.
 * You may obtain a copy of the License in the LICENSE file, or at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" 
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language 
 * governing permissions and limitations under the License. 
 */

package com.edmodo.rangebar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;

/**
 * This class represents the underlying gray bar in the RangeBar (without the
 * thumbs).
 */
class Bar {

    // Member Variables ////////////////////////////////////////////////////////

    private final Paint mPaint;

    // Left-coordinate of the horizontal bar.
    private final float mLeftX;
    private final float mRightX;
    private final float mY;
    private final float mTickHeight;
    private final float mTickStartY;
    private final float mTickEndY;
    private int mNumSegments;
    private float mTickDistance;
    private int[] mIndexList;
    private int size;
    private float initDistance;
    private TextPaint mTextPaint;
    private int disEnableColor;
    private int barColor;
    private int textColor;

    // Constructor /////////////////////////////////////////////////////////////

    Bar(Context ctx,
        float x,
        float y,
        float length,
        int tickCount,
        float tickHeightDP,
        float BarWeight,
        int BarColor, int textColor, float textSize, float initDistance, int disEnableColor, boolean isEnable) {

        mLeftX = x;
        mRightX = x + length;
        mY = y;

        this.initDistance = initDistance;
        this.disEnableColor = disEnableColor;

        mNumSegments = tickCount - 1;
        mTickDistance = length / mNumSegments;
        mTickHeight = tickHeightDP;
        mTickStartY = mY;
        mTickEndY = mY + mTickHeight;
        barColor = BarColor;
        // Initialize the paint.
        mPaint = new Paint();
        mPaint.setStrokeWidth(BarWeight);
        mPaint.setAntiAlias(true);
        this.textColor = textColor;
        mTextPaint = new TextPaint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(textSize);
        setEnabled(isEnable);
    }

    public void setmIndexList(int[] mIndexList) {
        this.mIndexList = mIndexList;
        size = 0;
        if (mIndexList != null) {
            size = mIndexList.length;
        }
    }

    public void setEnabled(boolean enable) {
        if (!enable) {
            mPaint.setColor(disEnableColor);
            mTextPaint.setColor(disEnableColor);
        } else {
            mPaint.setColor(barColor);
            mTextPaint.setColor(textColor);
        }
    }

    void draw(Canvas canvas) {
        canvas.drawLine(mLeftX, mY, mRightX, mY, mPaint);

        drawTicks(canvas);
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

    // Private Methods /////////////////////////////////////////////////////////

    private void drawTicks(Canvas canvas) {

        // Loop through and draw each tick (except final tick).
        if (size > 1) {
            for (int i = 0; i < size; i++) {
                final float x = ((mNumSegments - initDistance * 2 * mNumSegments) * (i) / (size - 1) + initDistance * mNumSegments) * mTickDistance + mLeftX;
                canvas.drawLine(x, mTickStartY, x, mTickEndY, mPaint);
                Rect rect = new Rect();
                String mText = String.valueOf(mIndexList[i]);
                mTextPaint.getTextBounds(mText, 0, mText.length(), rect);
                canvas.drawText(mText, x - rect.width() / 2, mTickEndY + mTickHeight + rect.height(), mTextPaint);
            }
        }
    }
}
