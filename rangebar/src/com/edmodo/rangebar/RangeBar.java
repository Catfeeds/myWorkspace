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
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class RangeBar extends View {

    // Member Variables ////////////////////////////////////////////////////////

    private static final String TAG = "RangeBar";

    // Default values for variables
    private static final int DEFAULT_TICK_COUNT = 3;
    // Instance variables for all of the customizable attributes
    private int mTickCount = DEFAULT_TICK_COUNT;
    private int mRightIndex = mTickCount - 1;
    private static final float DEFAULT_TICK_HEIGHT_DP = 24;
    private float mTickHeightDP = DEFAULT_TICK_HEIGHT_DP;
    private static final float DEFAULT_BAR_WEIGHT_PX = 2;
    private float mBarWeight = DEFAULT_BAR_WEIGHT_PX;
    private static final int DEFAULT_BAR_COLOR = Color.LTGRAY;
    private int mBarColor = DEFAULT_BAR_COLOR;
    private static final float DEFAULT_CONNECTING_LINE_WEIGHT_PX = 4;
    private float mConnectingLineWeight = DEFAULT_CONNECTING_LINE_WEIGHT_PX;
    private static final int DEFAULT_THUMB_IMAGE_NORMAL = R.drawable.seek_thumb_normal;
    private int mThumbImageNormal = DEFAULT_THUMB_IMAGE_NORMAL;
    private static final int DEFAULT_THUMB_IMAGE_PRESSED = R.drawable.seek_thumb_pressed;
    private int mThumbImagePressed = DEFAULT_THUMB_IMAGE_PRESSED;
    // Corresponds to android.R.color.holo_blue_light.
    private static final int DEFAULT_CONNECTING_LINE_COLOR = 0xff33b5e5;
    private int mConnectingLineColor = DEFAULT_CONNECTING_LINE_COLOR;
    private static final int DEFAULT_DIS_ENABLE_COLOR = 0xffcccccc;
    private int mDisEnableColor = DEFAULT_DIS_ENABLE_COLOR;
    // Indicator value tells Thumb.java whether it should draw the circle or not
    private static final float DEFAULT_THUMB_RADIUS_DP = -1;
    private float mThumbRadiusDP = DEFAULT_THUMB_RADIUS_DP;
    private static final int DEFAULT_THUMB_COLOR_NORMAL = -1;
    private int mThumbColorNormal = DEFAULT_THUMB_COLOR_NORMAL;
    private static final int DEFAULT_THUMB_COLOR_PRESSED = -1;
    private int mThumbColorPressed = DEFAULT_THUMB_COLOR_PRESSED;
    private static final int DEFAULT_THUMB_TEXT_COLOR = -1;
    private int mThumbTextColor = DEFAULT_THUMB_TEXT_COLOR;
    private static final int DEFAULT_THUMB_TEXT_SIZE = 24;
    private float mThumbTextSize = DEFAULT_THUMB_TEXT_SIZE;
    private static final int DEFAULT_THUMB_TEXT_MARGIN = 24;
    private float mThumbTextMarginBottom = DEFAULT_THUMB_TEXT_MARGIN;
    private static final float DEFAULT_DISTANCE = 0.1f;
    private static final String MAX_VALUE = "∞";
    private int[] mIndexList;
    // setTickCount only resets indices before a thumb has been pressed or a
    // setThumbIndices() is called, to correspond with intended usage
    private boolean mFirstSetTickCount = true;
    private Thumb mLeftThumb;
    private Thumb mRightThumb;
    private Bar mBar;
    private ConnectingLine mConnectingLine;
    private RangeBar.OnRangeBarChangeListener mListener;
    private int mLeftIndex = 0;
    private float initDistance = 0.1f;


    // Constructors ////////////////////////////////////////////////////////////

    public RangeBar(Context context) {
        super(context);
    }

    public RangeBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        rangeBarInit(context, attrs);
    }

    public RangeBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        rangeBarInit(context, attrs);
    }

    // View Methods ////////////////////////////////////////////////////////////

    @Override
    public Parcelable onSaveInstanceState() {

        final Bundle bundle = new Bundle();

        bundle.putParcelable("instanceState", super.onSaveInstanceState());

        bundle.putInt("TICK_COUNT", mTickCount);
        bundle.putFloat("TICK_HEIGHT_DP", mTickHeightDP);
        bundle.putFloat("BAR_WEIGHT", mBarWeight);
        bundle.putInt("BAR_COLOR", mBarColor);
        bundle.putFloat("CONNECTING_LINE_WEIGHT", mConnectingLineWeight);
        bundle.putInt("CONNECTING_LINE_COLOR", mConnectingLineColor);

        bundle.putInt("THUMB_IMAGE_NORMAL", mThumbImageNormal);
        bundle.putInt("THUMB_IMAGE_PRESSED", mThumbImagePressed);

        bundle.putFloat("THUMB_RADIUS_DP", mThumbRadiusDP);
        bundle.putInt("THUMB_COLOR_NORMAL", mThumbColorNormal);
        bundle.putInt("THUMB_COLOR_PRESSED", mThumbColorPressed);

        bundle.putInt("THUMB_TEXT_COLOR", mThumbTextColor);
        bundle.putFloat("THUMB_TEXT_SIZE", mThumbTextSize);
        bundle.putFloat("THUMB_TEXT_MARGIN", mThumbTextMarginBottom);

        bundle.putInt("LEFT_INDEX", mLeftIndex);
        bundle.putInt("RIGHT_INDEX", mRightIndex);
        bundle.putIntArray("INDEX_LIST", mIndexList);

        bundle.putFloat("INIT_DISTANCE", initDistance);
        bundle.putInt("DIS_ENABLE_COLOR", mDisEnableColor);
        bundle.putBoolean("DIS_ENABLE", isEnabled());


        bundle.putBoolean("FIRST_SET_TICK_COUNT", mFirstSetTickCount);

        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {

        if (state instanceof Bundle) {

            final Bundle bundle = (Bundle) state;

            mTickCount = bundle.getInt("TICK_COUNT");
            mTickHeightDP = bundle.getFloat("TICK_HEIGHT_DP");
            mBarWeight = bundle.getFloat("BAR_WEIGHT");
            mBarColor = bundle.getInt("BAR_COLOR");
            mConnectingLineWeight = bundle.getFloat("CONNECTING_LINE_WEIGHT");
            mConnectingLineColor = bundle.getInt("CONNECTING_LINE_COLOR");

            mThumbImageNormal = bundle.getInt("THUMB_IMAGE_NORMAL");
            mThumbImagePressed = bundle.getInt("THUMB_IMAGE_PRESSED");

            mThumbRadiusDP = bundle.getFloat("THUMB_RADIUS_DP");
            mThumbColorNormal = bundle.getInt("THUMB_COLOR_NORMAL");
            mThumbColorPressed = bundle.getInt("THUMB_COLOR_PRESSED");

            mThumbTextColor = bundle.getInt("THUMB_TEXT_COLOR");
            mThumbTextSize = bundle.getInt("THUMB_TEXT_SIZE");
            mThumbTextMarginBottom = bundle.getInt("THUMB_TEXT_MARGIN");

            mLeftIndex = bundle.getInt("LEFT_INDEX");
            mRightIndex = bundle.getInt("RIGHT_INDEX");
            mFirstSetTickCount = bundle.getBoolean("FIRST_SET_TICK_COUNT");
            mIndexList = bundle.getIntArray("INDEX_LIST");

            initDistance = bundle.getFloat("INIT_DISTANCE");
            mDisEnableColor = bundle.getInt("DIS_ENABLE_COLOR");
            setEnabled(bundle.getBoolean("DIS_ENABLE"));

            setThumbIndices(mLeftIndex, mRightIndex);

            super.onRestoreInstanceState(bundle.getParcelable("instanceState"));

        } else {

            super.onRestoreInstanceState(state);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width;
        int height;

        // Get measureSpec mode and size values.
        final int measureWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int measureHeightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        final int measureHeight = MeasureSpec.getSize(heightMeasureSpec);

        // The RangeBar width should be as large as possible.
        if (measureWidthMode == MeasureSpec.AT_MOST) {
            width = measureWidth;
        } else if (measureWidthMode == MeasureSpec.EXACTLY) {
            width = measureWidth;
        } else {
            width = 500;
        }

        // The RangeBar height should be as small as possible.
        int mDefaultHeight = 150;
        if (measureHeightMode == MeasureSpec.AT_MOST) {
            height = Math.min(mDefaultHeight, measureHeight);
        } else if (measureHeightMode == MeasureSpec.EXACTLY) {
            height = measureHeight;
        } else {
            height = mDefaultHeight;
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        super.onSizeChanged(w, h, oldw, oldh);

        final Context ctx = getContext();

        final float yPos = h / 2f;
        mLeftThumb = new Thumb(ctx, yPos, mThumbColorNormal, mThumbColorPressed, mThumbRadiusDP,
                mThumbImageNormal, mThumbImagePressed, mThumbTextColor, mThumbTextSize,
                mThumbTextMarginBottom, mDisEnableColor, isEnabled());
        mRightThumb = new Thumb(ctx, yPos, mThumbColorNormal, mThumbColorPressed, mThumbRadiusDP,
                mThumbImageNormal, mThumbImagePressed, mThumbTextColor, mThumbTextSize,
                mThumbTextMarginBottom, mDisEnableColor, isEnabled());

        // Create the underlying bar.
        final float marginLeft = mLeftThumb.getHalfWidth() + getPaddingLeft();
        final float barLength = w - 2 * marginLeft;
        mBar = new Bar(ctx, marginLeft, yPos, barLength, mTickCount, mTickHeightDP, mBarWeight,
                mBarColor, mThumbTextColor, mThumbTextSize, initDistance, mDisEnableColor, isEnabled());
        mBar.setmIndexList(mIndexList);

        // Initialize thumbs to the desired indices
        mLeftThumb.setX(marginLeft + (mLeftIndex / (float) (mTickCount - 1)) * barLength);
        mRightThumb.setX(marginLeft + (mRightIndex / (float) (mTickCount - 1)) * barLength);

        // Set the thumb indices.
        final int newLeftIndex = mBar.getNearestTickIndex(mLeftThumb);
        final int newRightIndex = mBar.getNearestTickIndex(mRightThumb);

        // Call the listener.
        if (newLeftIndex != mLeftIndex || newRightIndex != mRightIndex) {

            mLeftIndex = newLeftIndex;
            mRightIndex = newRightIndex;
            if (mListener != null) {
                mListener.onIndexChangeListener(this, mLeftIndex, mRightIndex);
            }
        }
        setIndexChange(mRightIndex, mLeftIndex);

        // Create the line connecting the two thumbs.
        mConnectingLine = new ConnectingLine(ctx, yPos, mConnectingLineWeight, mConnectingLineColor, mDisEnableColor, isEnabled());
    }

    public void setIndexChange(int mRightIndex, int mLeftIndex) {
        mRightThumb.setText(index2Value(mRightIndex));
        mLeftThumb.setText(index2Value(mLeftIndex));
    }

    public String index2Value(int mIndex) {
        if (mIndex > mTickCount * 99 / 100) {
            return MAX_VALUE;
        }
        if (mIndexList != null) {
            int size = mIndexList.length;
            if (size > 1) {
                float f = (float) mIndex / mTickCount;
                if (f < initDistance) {
                    return String.valueOf(Math.round(mIndexList[0] * f / (100 * initDistance)) * 100);
                } else if (f > (1 - initDistance)) {
                    return String.valueOf(mIndexList[size - 1] + (int) Math.pow((double) (mIndex - mTickCount * (1 - initDistance)), 1.5) * 100);
                } else {
                    int i = (int) ((f - initDistance) * (size - 1) / (1 - 2 * initDistance));
                    float t = (f - initDistance) * (size - 1) / (1 - 2 * initDistance);
                    return String.valueOf((mIndexList[i] + Math.round((mIndexList[i + 1] - mIndexList[i]) * (t - i) / 100) * 100));
                }
            }
        }
        return String.valueOf(((int) Math.pow(mIndex, 1.02)) * 100);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        mBar.draw(canvas);

        mConnectingLine.draw(canvas, mLeftThumb, mRightThumb);

        mLeftThumb.draw(canvas);
        mRightThumb.draw(canvas);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        // If this View is not enabled, don't allow for touch interactions.
        if (!isEnabled()) {
            return false;
        }

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                onActionDown(event.getX(), event.getY());
                return true;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                this.getParent().requestDisallowInterceptTouchEvent(false);
                onActionUp(event.getX());
                return true;

            case MotionEvent.ACTION_MOVE:
                onActionMove(event.getX());
                this.getParent().requestDisallowInterceptTouchEvent(true);
                return true;

            default:
                return false;
        }
    }

    public void setOnRangeBarChangeListener(RangeBar.OnRangeBarChangeListener listener) {
        mListener = listener;
    }

    public void setTickCount(int tickCount) {

        if (isValidTickCount(tickCount)) {
            mTickCount = tickCount;

            if (mFirstSetTickCount) {
                mLeftIndex = 0;
                mRightIndex = mTickCount - 1;

                setIndexChange(mRightIndex, mLeftIndex);
                if (mListener != null) {
                    mListener.onIndexChangeListener(this, mLeftIndex, mRightIndex);
                }
            }
            if (indexOutOfRange(mLeftIndex, mRightIndex)) {
                mLeftIndex = 0;
                mRightIndex = mTickCount - 1;

                setIndexChange(mRightIndex, mLeftIndex);
                if (mListener != null)
                    mListener.onIndexChangeListener(this, mLeftIndex, mRightIndex);
            }

            createBar();
            createThumbs();
        } else {
            Log.e(TAG, "tickCount less than 2; invalid tickCount.");
            throw new IllegalArgumentException("tickCount less than 2; invalid tickCount.");
        }
    }

    public void setTickHeight(float tickHeight) {

        mTickHeightDP = tickHeight;
        createBar();
    }

    public void setBarWeight(float barWeight) {

        mBarWeight = barWeight;
        createBar();
    }

    public void setBarColor(int barColor) {

        mBarColor = barColor;
        createBar();
    }

    public void setConnectingLineWeight(float connectingLineWeight) {

        mConnectingLineWeight = connectingLineWeight;
        createConnectingLine();
    }

    public void setConnectingLineColor(int connectingLineColor) {

        mConnectingLineColor = connectingLineColor;
        createConnectingLine();
    }

    public void setThumbRadius(float thumbRadius) {

        mThumbRadiusDP = thumbRadius;
        createThumbs();
    }

    public void setThumbImageNormal(int thumbImageNormalID) {
        mThumbImageNormal = thumbImageNormalID;
        createThumbs();
    }

    public void setThumbImagePressed(int thumbImagePressedID) {
        mThumbImagePressed = thumbImagePressedID;
        createThumbs();
    }

    public void setThumbColorNormal(int thumbColorNormal) {
        mThumbColorNormal = thumbColorNormal;
        createThumbs();
    }

    public void setThumbColorPressed(int thumbColorPressed) {
        mThumbColorPressed = thumbColorPressed;
        createThumbs();
    }

    public void setmIndexList(ArrayList<Integer> mIndexList) {
        if (mIndexList != null && !mIndexList.isEmpty()) {
            this.mIndexList=new int[mIndexList.size()];
            for(int i=0,size=mIndexList.size();i<size;i++){
                this.mIndexList[i]=mIndexList.get(i);
            }
            reset();
        }
    }

    public void setThumbIndices(int leftThumbIndex, int rightThumbIndex) {
        if (indexOutOfRange(leftThumbIndex, rightThumbIndex)) {

            Log.e(TAG, "A thumb index is out of bounds. Check that it is between 0 and mTickCount - 1");
            throw new IllegalArgumentException("A thumb index is out of bounds. Check that it is between 0 and mTickCount - 1");

        } else {

            if (mFirstSetTickCount)
                mFirstSetTickCount = false;

            mLeftIndex = leftThumbIndex;
            mRightIndex = rightThumbIndex;
            createThumbs();

            setIndexChange(mRightIndex, mLeftIndex);
            if (mListener != null) {
                mListener.onIndexChangeListener(this, mLeftIndex, mRightIndex);
            }
        }

        invalidate();
        requestLayout();
    }

    public int getLeftIndex() {
        return mLeftIndex;
    }

    public int getRightIndex() {
        return mRightIndex;
    }

    public void setThumbIndex(int leftIndex, int rightIndex) {
        mLeftIndex = leftIndex;
        mRightIndex = rightIndex;
        if (mBar != null) {
            mBar.setmIndexList(this.mIndexList);
        }
        if (mLeftThumb != null && mRightThumb != null) {
            float marginLeft = getMarginLeft();
            float barLength = getBarLength();
            mLeftThumb.setX(marginLeft + (mLeftIndex / (float) (mTickCount - 1)) * barLength);
            mRightThumb.setX(marginLeft + (mRightIndex / (float) (mTickCount - 1)) * barLength);
            setIndexChange(mRightIndex, mLeftIndex);
            invalidate();
        } else {
            createThumbs();
        }
    }

    public void setmRightIndex(int mRightIndex) {
        this.mRightIndex = mRightIndex;
    }

    public String getLeftValue() {
        return mLeftThumb != null ? mLeftThumb.getmText() : null;
    }

    public String getRightValue() {
        return mRightThumb != null ? mRightThumb.getmText() : null;
    }


    private void rangeBarInit(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RangeBar, 0, 0);

        try {

            final Integer tickCount = ta.getInteger(R.styleable.RangeBar_tickCount, DEFAULT_TICK_COUNT);

            if (isValidTickCount(tickCount)) {
                mTickCount = tickCount;
                mLeftIndex = 0;
                mRightIndex = mTickCount - 1;

                if (mListener != null) {
                    mListener.onIndexChangeListener(this, mLeftIndex, mRightIndex);
                }

            } else {

                Log.e(TAG, "tickCount less than 2; invalid tickCount. XML input ignored.");
            }

            mTickHeightDP = ta.getDimension(R.styleable.RangeBar_tickHeight, DEFAULT_TICK_HEIGHT_DP);
            mBarWeight = ta.getDimension(R.styleable.RangeBar_barWeight, DEFAULT_BAR_WEIGHT_PX);
            mBarColor = ta.getColor(R.styleable.RangeBar_rbBarColor, DEFAULT_BAR_COLOR);
            mConnectingLineWeight = ta.getDimension(R.styleable.RangeBar_connectingLineWeight,
                    DEFAULT_CONNECTING_LINE_WEIGHT_PX);
            mConnectingLineColor = ta.getColor(R.styleable.RangeBar_connectingLineColor,
                    DEFAULT_CONNECTING_LINE_COLOR);
            mThumbRadiusDP = ta.getDimension(R.styleable.RangeBar_thumbRadius, DEFAULT_THUMB_RADIUS_DP);
            mThumbImageNormal = ta.getResourceId(R.styleable.RangeBar_thumbImageNormal,
                    DEFAULT_THUMB_IMAGE_NORMAL);
            mThumbImagePressed = ta.getResourceId(R.styleable.RangeBar_thumbImagePressed,
                    DEFAULT_THUMB_IMAGE_PRESSED);
            mThumbColorNormal = ta.getColor(R.styleable.RangeBar_thumbColorNormal, DEFAULT_THUMB_COLOR_NORMAL);
            mThumbColorPressed = ta.getColor(R.styleable.RangeBar_thumbColorPressed,
                    DEFAULT_THUMB_COLOR_PRESSED);
            mThumbTextSize = ta.getDimension(R.styleable.RangeBar_thumbTextSize, DEFAULT_THUMB_TEXT_SIZE);
            mThumbTextMarginBottom = ta.getDimension(R.styleable.RangeBar_thumbTextMarginBottom, DEFAULT_THUMB_TEXT_MARGIN);
            mThumbTextColor = ta.getColor(R.styleable.RangeBar_thumbTextColor,
                    DEFAULT_THUMB_TEXT_COLOR);

            initDistance = ta.getFloat(R.styleable.RangeBar_initDistance,
                    DEFAULT_DISTANCE);
            mDisEnableColor = ta.getColor(R.styleable.RangeBar_disEnabledColor,
                    DEFAULT_DIS_ENABLE_COLOR);
            setEnabled(ta.getBoolean(R.styleable.RangeBar_rangEnable,
                    true));

        } finally {

            ta.recycle();
        }

    }

    private void createBar() {
        mBar = new Bar(getContext(), getMarginLeft() + getPaddingLeft(),
                getYPos(), getBarLength(), mTickCount, mTickHeightDP, mBarWeight,
                mBarColor, mThumbTextColor, mThumbTextSize, initDistance, mDisEnableColor, isEnabled());
        mBar.setmIndexList(mIndexList);
        invalidate();
    }

    private void createConnectingLine() {

        mConnectingLine = new ConnectingLine(getContext(),
                getYPos(),
                mConnectingLineWeight,
                mConnectingLineColor, mDisEnableColor, isEnabled());
        invalidate();
    }

    private void createThumbs() {

        Context ctx = getContext();
        float yPos = getYPos();
        if (mLeftThumb == null) {
            mLeftThumb = new Thumb(ctx, yPos, mThumbColorNormal, mThumbColorPressed,
                    mThumbRadiusDP, mThumbImageNormal, mThumbImagePressed, mThumbTextColor,
                    mThumbTextSize, mThumbTextMarginBottom, mDisEnableColor, isEnabled());
        }
        if (mRightThumb == null) {
            mRightThumb = new Thumb(ctx, yPos, mThumbColorNormal, mThumbColorPressed,
                    mThumbRadiusDP, mThumbImageNormal, mThumbImagePressed, mThumbTextColor,
                    mThumbTextSize, mThumbTextMarginBottom, mDisEnableColor, isEnabled());
        }

        float marginLeft = getMarginLeft();
        float barLength = getBarLength();

        // Initialize thumbs to the desired indices
        mLeftThumb.setX(marginLeft + (mLeftIndex / (float) (mTickCount - 1)) * barLength);
        mRightThumb.setX(marginLeft + (mRightIndex / (float) (mTickCount - 1)) * barLength);

        setIndexChange(mRightIndex, mLeftIndex);
        invalidate();
    }

    private float getMarginLeft() {
        return ((mLeftThumb != null) ? mLeftThumb.getHalfWidth() : 0) + getPaddingLeft();
    }

    private float getYPos() {
        return (getHeight() / 2f);
    }

    private float getBarLength() {
        return (getWidth() - 2 * getMarginLeft());
    }

    private boolean indexOutOfRange(int leftThumbIndex, int rightThumbIndex) {
        return (leftThumbIndex < 0 || leftThumbIndex >= mTickCount
                || rightThumbIndex < 0
                || rightThumbIndex >= mTickCount);
    }

    private boolean isValidTickCount(int tickCount) {
        return (tickCount > 1);
    }

    private void onActionDown(float x, float y) {

        if (!mLeftThumb.isPressed() && mLeftThumb.isInTargetZone(x, y)) {

            pressThumb(mLeftThumb);

        } else if (!mLeftThumb.isPressed() && mRightThumb.isInTargetZone(x, y)) {

            pressThumb(mRightThumb);
        }
    }

    private void onActionUp(float x) {

        if (mLeftThumb.isPressed()) {

            releaseThumb(mLeftThumb);

        } else if (mRightThumb.isPressed()) {

            releaseThumb(mRightThumb);

        } else {

            float leftThumbXDistance = Math.abs(mLeftThumb.getX() - x);
            float rightThumbXDistance = Math.abs(mRightThumb.getX() - x);

            if (leftThumbXDistance < rightThumbXDistance) {
                if (mRightThumb.getX() - x < mLeftThumb.getHalfWidth() + mRightThumb.getHalfWidth()) {
                    x = mRightThumb.getX() - mLeftThumb.getHalfWidth() - mRightThumb.getHalfWidth();
                }
                mLeftThumb.setX(x);
                releaseThumb(mLeftThumb);
            } else {
                if (x - mLeftThumb.getX() < mLeftThumb.getHalfWidth() + mRightThumb.getHalfWidth()) {
                    x = mLeftThumb.getX() + mLeftThumb.getHalfWidth() + mRightThumb.getHalfWidth();
                }
                mRightThumb.setX(x);
                releaseThumb(mRightThumb);
            }

            // Get the updated nearest tick marks for each thumb.
            final int newLeftIndex = mBar.getNearestTickIndex(mLeftThumb);
            final int newRightIndex = mBar.getNearestTickIndex(mRightThumb);

            // If either of the indices have changed, update and call the listener.
            if (newLeftIndex != mLeftIndex || newRightIndex != mRightIndex) {

                mLeftIndex = newLeftIndex;
                mRightIndex = newRightIndex;

                if (mListener != null) {
                    mListener.onIndexChangeListener(this, mLeftIndex, mRightIndex);
                }
            }
            setIndexChange(mRightIndex, mLeftIndex);
        }
    }

    private void onActionMove(float x) {

        // Move the pressed thumb to the new x-position.
        if (mLeftThumb.isPressed()) {
            if (mRightThumb.getX() - x < mLeftThumb.getHalfWidth() + mRightThumb.getHalfWidth()) {
                x = mRightThumb.getX() - mLeftThumb.getHalfWidth() - mRightThumb.getHalfWidth();
            }
            moveThumb(mLeftThumb, x);
        } else if (mRightThumb.isPressed()) {
            if (x - mLeftThumb.getX() < mLeftThumb.getHalfWidth() + mRightThumb.getHalfWidth()) {
                x = mLeftThumb.getX() + mLeftThumb.getHalfWidth() + mRightThumb.getHalfWidth();
            }
            moveThumb(mRightThumb, x);
        }

        // If the thumbs have switched order, fix the references.
        if (mLeftThumb.getX() > mRightThumb.getX()) {
            final Thumb temp = mLeftThumb;
            mLeftThumb = mRightThumb;
            mRightThumb = temp;
        }

        // Get the updated nearest tick marks for each thumb.
        final int newLeftIndex = mBar.getNearestTickIndex(mLeftThumb);
        final int newRightIndex = mBar.getNearestTickIndex(mRightThumb);

        // If either of the indices have changed, update and call the listener.
        if (newLeftIndex != mLeftIndex || newRightIndex != mRightIndex) {

            mLeftIndex = newLeftIndex;
            mRightIndex = newRightIndex;

            if (mListener != null) {
                mListener.onIndexChangeListener(this, mLeftIndex, mRightIndex);
            }
        }

        setIndexChange(mRightIndex, mLeftIndex);
    }

    private void pressThumb(Thumb thumb) {
        if (mFirstSetTickCount)
            mFirstSetTickCount = false;
        thumb.press();
        invalidate();
    }

    private void releaseThumb(Thumb thumb) {

        final float nearestTickX = mBar.getNearestTickCoordinate(thumb);
        thumb.setX(nearestTickX);
        thumb.release();
        invalidate();
    }

    private void moveThumb(Thumb thumb, float x) {

        // If the user has moved their finger outside the range of the bar,
        // do not move the thumbs past the edge.
        if (x < mBar.getLeftX()) {
            thumb.setX(mBar.getLeftX());
            invalidate();
        } else if (x > mBar.getRightX()) {
            thumb.setX(mBar.getRightX());
            invalidate();
        } else {
            thumb.setX(x);
            invalidate();
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (mBar == null) {
            createBar();
        }
        if (mLeftThumb == null || mRightThumb == null) {
            createThumbs();
        }
        if (mConnectingLine == null) {
            createConnectingLine();
        }
        mBar.setEnabled(enabled);
        mLeftThumb.setEnabled(enabled);
        mRightThumb.setEnabled(enabled);
        mConnectingLine.setEnabled(enabled);
        invalidate();
        super.setEnabled(enabled);
    }

    public void reset() {
        mLeftIndex = Math.round(initDistance * mTickCount);
        mRightIndex = Math.round((1 - initDistance) * mTickCount)-1;
        if (mBar != null) {
            mBar.setmIndexList(this.mIndexList);
        }
        if (mLeftThumb != null && mRightThumb != null) {
            float marginLeft = getMarginLeft();
            float barLength = getBarLength();
            mLeftThumb.setX(marginLeft + (mLeftIndex / (float) (mTickCount - 1)) * barLength);
            mRightThumb.setX(marginLeft + (mRightIndex / (float) (mTickCount - 1)) * barLength);
            setIndexChange(mRightIndex, mLeftIndex);
            invalidate();
        } else {
            createThumbs();
        }
    }

    public static interface OnRangeBarChangeListener {

        public void onIndexChangeListener(RangeBar rangeBar, int leftThumbIndex, int rightThumbIndex);
    }
}
