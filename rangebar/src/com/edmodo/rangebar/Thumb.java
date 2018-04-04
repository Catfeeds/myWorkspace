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
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.TypedValue;

/**
 * Represents a thumb in the RangeBar slider. This is the handle for the slider
 * that is pressed and slid.
 */
class Thumb {

    // Private Constants ///////////////////////////////////////////////////////

    // The radius (in dp) of the touchable area around the thumb. We are basing
    // this value off of the recommended 48dp Rhythm. See:
    // http://developer.android.com/design/style/metrics-grids.html#48dp-rhythm
    private static final float MINIMUM_TARGET_RADIUS_DP = 24;

    // Sets the default values for radius, normal, pressed if circle is to be
    // drawn but no value is given.
    private static final float DEFAULT_THUMB_RADIUS_DP = 14;

    // Corresponds to android.R.color.holo_blue_light.
    private static final int DEFAULT_THUMB_COLOR_NORMAL = 0xff33b5e5;
    private static final int DEFAULT_THUMB_COLOR_PRESSED = 0xff33b5e5;

    // Member Variables ////////////////////////////////////////////////////////

    // Radius (in pixels) of the touch area of the thumb.
    private final float mTargetRadiusPx;

    // The normal and pressed images to display for the thumbs.
    private final Bitmap mImageNormal;
    private final Bitmap mImagePressed;

    // Variables to store half the width/height for easier calculation.
    private final float mHalfWidthNormal;
    private final float mHalfHeightNormal;

    private final float mHalfWidthPressed;
    private final float mHalfHeightPressed;
    // The y-position of the thumb in the parent view. This should not change.
    private final float mY;
    // Indicates whether this thumb is currently pressed and active.
    private boolean mIsPressed = false;
    // The current x-position of the thumb in the parent view.
    private float mX;

    // mPaint to draw the thumbs if attributes are selected
    private Paint mPaintNormal;
    private Paint mPaintStroke;
    private Paint mPaintPressed;
    private Paint mPaintDisEnable;
    private TextPaint mTextPaint;

    // Radius of the new thumb if selected
    private float mThumbRadiusPx;

    // Toggle to select bitmap thumbImage or not
    private boolean mUseBitmap;

    private boolean isEnable;

    private int disEnableColor;

    private int textColor;

    private float mThumbTextMargin;

    private String mText;


    // Constructors ////////////////////////////////////////////////////////////
    Thumb(Context ctx,
          float y,
          int thumbColorNormal,
          int thumbColorPressed,
          float thumbRadiusDP, int disEnableColor, boolean isEnable, int thumbStrokeColor, float thumbStrokeWidth) {
        this.mImageNormal = null;
        this.mImagePressed = null;

        final Resources res = ctx.getResources();

        this.disEnableColor = disEnableColor;

        mUseBitmap = false;

        // If one of the attributes are set, but the others aren't, set the
        // attributes to default
        if (thumbRadiusDP == -1)
            mThumbRadiusPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    DEFAULT_THUMB_RADIUS_DP,
                    res.getDisplayMetrics());
        else
            mThumbRadiusPx = thumbRadiusDP;

        mPaintStroke = new Paint();
        mPaintStroke.setColor(thumbStrokeColor);
        mPaintStroke.setStrokeWidth(thumbStrokeWidth);
        mPaintStroke.setStyle(Paint.Style.STROKE);
        mPaintStroke.setAntiAlias(true);

        mPaintNormal = new Paint();
        mPaintNormal.setColor(thumbColorNormal);
        mPaintNormal.setAntiAlias(true);

        mPaintPressed = new Paint();
        mPaintPressed.setColor(thumbColorPressed);
        mPaintPressed.setAntiAlias(true);

        mHalfWidthNormal = mThumbRadiusPx;
        mHalfHeightNormal = mThumbRadiusPx;

        mHalfWidthPressed = mThumbRadiusPx;
        mHalfHeightPressed = mThumbRadiusPx;
        mPaintDisEnable = new Paint();
        mPaintDisEnable.setColor(disEnableColor);
        mPaintDisEnable.setAntiAlias(true);

        setEnabled(isEnable);

        mTargetRadiusPx = (int) Math.max(MINIMUM_TARGET_RADIUS_DP, thumbRadiusDP);

        mX = mHalfWidthNormal;
        mY = y;
    }


    Thumb(Context ctx,
          float y,
          int thumbColorNormal,
          int thumbColorPressed,
          float thumbRadiusDP,
          int thumbImageNormal,
          int thumbImagePressed, int textColor, float textSize, float thumbTextMargin, int disEnableColor, boolean isEnable) {

        final Resources res = ctx.getResources();

        mThumbTextMargin = thumbTextMargin;
        mImageNormal = BitmapFactory.decodeResource(res, thumbImageNormal);
        mImagePressed = BitmapFactory.decodeResource(res, thumbImagePressed);
        this.disEnableColor = disEnableColor;

        // If any of the attributes are set, toggle bitmap off
        if (thumbRadiusDP == -1 && thumbColorNormal == -1 && thumbColorPressed == -1) {

            mUseBitmap = true;

            mHalfWidthNormal = mImageNormal.getWidth() / 2f;
            mHalfHeightNormal = mImageNormal.getHeight() / 2f;

            mHalfWidthPressed = mImagePressed.getWidth() / 2f;
            mHalfHeightPressed = mImagePressed.getHeight() / 2f;
        } else {

            mUseBitmap = false;

            // If one of the attributes are set, but the others aren't, set the
            // attributes to default
            if (thumbRadiusDP == -1)
                mThumbRadiusPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        DEFAULT_THUMB_RADIUS_DP,
                        res.getDisplayMetrics());
            else
                mThumbRadiusPx = thumbRadiusDP;

            int mThumbColorNormal;
            if (thumbColorNormal == -1)
                mThumbColorNormal = DEFAULT_THUMB_COLOR_NORMAL;
            else
                mThumbColorNormal = thumbColorNormal;

            int mThumbColorPressed;
            if (thumbColorPressed == -1)
                mThumbColorPressed = DEFAULT_THUMB_COLOR_PRESSED;
            else
                mThumbColorPressed = thumbColorPressed;

            // Creates the paint and sets the Paint values
            mPaintNormal = new Paint();
            mPaintNormal.setColor(mThumbColorNormal);
            mPaintNormal.setAntiAlias(true);

            mPaintPressed = new Paint();
            mPaintPressed.setColor(mThumbColorPressed);
            mPaintPressed.setAntiAlias(true);

            mHalfWidthNormal = mThumbRadiusPx;
            mHalfHeightNormal = mThumbRadiusPx;

            mHalfWidthPressed = mThumbRadiusPx;
            mHalfHeightPressed = mThumbRadiusPx;
        }
        mPaintDisEnable = new Paint();
        mPaintDisEnable.setColor(disEnableColor);
        mPaintDisEnable.setAntiAlias(true);

        this.textColor = textColor;
        mTextPaint = new TextPaint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(textSize);
        setEnabled(isEnable);

        // Sets the minimum touchable area, but allows it to expand based on
        // image size

        mTargetRadiusPx = (int) Math.max(MINIMUM_TARGET_RADIUS_DP, thumbRadiusDP);

        mX = mHalfWidthNormal;
        mY = y;
    }

    // Package-Private Methods /////////////////////////////////////////////////

    float getHalfWidth() {
        return mHalfWidthNormal;
    }

    float getHalfHeight() {
        return mHalfHeightNormal;
    }

    float getX() {
        return mX;
    }

    void setX(float x) {
        mX = x;
    }

    boolean isPressed() {
        return mIsPressed;
    }

    public void setEnabled(boolean isEnable) {
        this.isEnable = isEnable;
        if (mTextPaint == null) {
            return;
        }
        if (!isEnable) {
            mTextPaint.setColor(disEnableColor);
        } else {
            mTextPaint.setColor(textColor);
        }
    }

    void press() {
        mIsPressed = true;
    }

    void release() {
        mIsPressed = false;
    }

    boolean isInTargetZone(float x, float y) {

        if (Math.abs(x - mX) <= mTargetRadiusPx * 1.5 && Math.abs(y - mY) <= mTargetRadiusPx * 1.5) {
            return true;
        }
        return false;
    }

    void setText(String mIndex) {
        mText = mIndex;
    }

    public String getmText() {
        return mText;
    }

    void draw(Canvas canvas) {

        // If a bitmap is to be printed. Determined by thumbRadius attribute.
        if (mText != null && mText.length() > 0 && mTextPaint != null) {
            Rect rect = new Rect();
            mTextPaint.getTextBounds(mText, 0, mText.length(), rect);
            canvas.drawText(mText, mX - rect.width() / 2, mY - mThumbTextMargin, mTextPaint);
        }
        if (mUseBitmap) {

            final Bitmap bitmap = (mIsPressed) ? mImagePressed : mImageNormal;

            if (mIsPressed) {
                final float topPressed = mY - mHalfHeightPressed;
                final float leftPressed = mX - mHalfWidthPressed;
                canvas.drawBitmap(bitmap, leftPressed, topPressed, null);
            } else {
                final float topNormal = mY - mHalfHeightNormal;
                final float leftNormal = mX - mHalfWidthNormal;
                canvas.drawBitmap(bitmap, leftNormal, topNormal, null);
            }

        } else {

            // Otherwise use a circle to display.
            if (!isEnable)
                canvas.drawCircle(mX, mY, mThumbRadiusPx, mPaintDisEnable);
            else if (mIsPressed)
                canvas.drawCircle(mX, mY, mThumbRadiusPx, mPaintPressed);
            else
                canvas.drawCircle(mX, mY, mThumbRadiusPx, mPaintNormal);

            if (mPaintStroke != null) {
                canvas.drawCircle(mX, mY, mThumbRadiusPx, mPaintStroke);
            }

        }
    }
}
