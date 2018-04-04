package com.edmodo.rangebar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by Suncloud on 2015/4/2.
 */
public class HotelRangeBar  extends View {

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
    private float mBarStrokeWidth;
    private float mThumbStrokeWidth;
    private int mThumbStrokeColor=DEFAULT_CONNECTING_LINE_COLOR;
    private int mThumbTextColor2=DEFAULT_THUMB_TEXT_COLOR;
    private ArrayList<String> mIndexList;
    // setTickCount only resets indices before a thumb has been pressed or a
    // setThumbIndices() is called, to correspond with intended usage
    private boolean mFirstSetTickCount = true;
    private Thumb mLeftThumb;
    private Thumb mRightThumb;
    private HotelBar mBar;
    private ConnectingLine mConnectingLine;
    private OnRangeBarChangeListener mListener;
    private int mLeftIndex = 0;


    // Constructors ////////////////////////////////////////////////////////////

    public HotelRangeBar(Context context) {
        super(context);
    }

    public HotelRangeBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        rangeBarInit(context, attrs);
    }

    public HotelRangeBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        rangeBarInit(context, attrs);
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
        int mDefaultHeight = (int) (mThumbRadiusDP+mTickHeightDP*2)*2;
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
        mLeftThumb = new Thumb(ctx, yPos, mThumbColorNormal, mThumbColorPressed,
                mThumbRadiusDP, mDisEnableColor, isEnabled(),mThumbStrokeColor,mThumbStrokeWidth);
        mRightThumb = new Thumb(ctx, yPos, mThumbColorNormal, mThumbColorPressed,
                mThumbRadiusDP, mDisEnableColor, isEnabled(),mThumbStrokeColor,mThumbStrokeWidth);

        // Create the underlying bar.
        final float marginLeft = mLeftThumb.getHalfWidth() + getPaddingLeft();
        final float barLength = w - 2 * marginLeft;
        mBar = new HotelBar(ctx, marginLeft, yPos, barLength, mTickCount, mTickHeightDP, mBarWeight,
                mBarColor, mThumbTextColor, mThumbTextSize, mDisEnableColor, isEnabled(),mBarStrokeWidth,mThumbTextColor2);
        mBar.setStringList(mIndexList);

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

        // Create the line connecting the two thumbs.
        mConnectingLine = new ConnectingLine(ctx, yPos, mConnectingLineWeight, mConnectingLineColor, mDisEnableColor, isEnabled());
    }


    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        mBar.draw(canvas,mLeftThumb,mRightThumb);

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

    public void setOnRangeBarChangeListener(OnRangeBarChangeListener listener) {
        mListener = listener;
    }

    public void setTickCount(int tickCount) {

        if (isValidTickCount(tickCount)) {
            mTickCount = tickCount;

            if (mFirstSetTickCount) {
                mLeftIndex = 0;
                mRightIndex = mTickCount - 1;

                if (mListener != null) {
                    mListener.onIndexChangeListener(this, mLeftIndex, mRightIndex);
                }
            }
            if (indexOutOfRange(mLeftIndex, mRightIndex)) {
                mLeftIndex = 0;
                mRightIndex = mTickCount - 1;

                if (mListener != null)
                    mListener.onIndexChangeListener(this, mLeftIndex, mRightIndex);
            }

            createBar();
            createThumbs();
        } else {
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

    public void setThumbColorNormal(int thumbColorNormal) {
        mThumbColorNormal = thumbColorNormal;
        createThumbs();
    }

    public void setThumbColorPressed(int thumbColorPressed) {
        mThumbColorPressed = thumbColorPressed;
        createThumbs();
    }

    public void setmIndexList(ArrayList<String> mIndexList) {
        if (mIndexList != null && !mIndexList.isEmpty()) {
            this.mIndexList = mIndexList;
            mTickCount=mIndexList.size();
            reset();
        }
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
            mBar.setStringList(this.mIndexList);
        }
        if (mLeftThumb != null && mRightThumb != null) {
            float marginLeft = getMarginLeft();
            float barLength = getBarLength();
            mLeftThumb.setX(marginLeft + (mLeftIndex / (float) (mTickCount - 1)) * barLength);
            mRightThumb.setX(marginLeft + (mRightIndex / (float) (mTickCount - 1)) * barLength);
            invalidate();
        } else {
            createThumbs();
        }
    }


    private void rangeBarInit(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RangeBar, 0, 0);
        TypedArray ta2 = context.obtainStyledAttributes(attrs, R.styleable.HotelRangeBar, 0, 0);

        try {

            final Integer tickCount = ta.getInteger(R.styleable.RangeBar_tickCount, DEFAULT_TICK_COUNT);

            if (isValidTickCount(tickCount)) {
                mTickCount = tickCount;
                mLeftIndex = 0;
                mRightIndex = mTickCount - 1;

                if (mListener != null) {
                    mListener.onIndexChangeListener(this, mLeftIndex, mRightIndex);
                }

            }

            mTickHeightDP = ta.getDimension(R.styleable.RangeBar_tickHeight, DEFAULT_TICK_HEIGHT_DP);
            mBarWeight = ta.getDimension(R.styleable.RangeBar_barWeight, DEFAULT_BAR_WEIGHT_PX);
            mBarColor = ta.getColor(R.styleable.RangeBar_rbBarColor, DEFAULT_BAR_COLOR);
            mConnectingLineWeight = ta.getDimension(R.styleable.RangeBar_connectingLineWeight,
                    DEFAULT_CONNECTING_LINE_WEIGHT_PX);
            mConnectingLineColor = ta.getColor(R.styleable.RangeBar_connectingLineColor,
                    DEFAULT_CONNECTING_LINE_COLOR);
            mThumbRadiusDP = ta.getDimension(R.styleable.RangeBar_thumbRadius, DEFAULT_THUMB_RADIUS_DP);
            mThumbColorNormal = ta.getColor(R.styleable.RangeBar_thumbColorNormal, DEFAULT_THUMB_COLOR_NORMAL);
            mThumbColorPressed = ta.getColor(R.styleable.RangeBar_thumbColorPressed, DEFAULT_THUMB_COLOR_PRESSED);
            mThumbTextSize = ta.getDimension(R.styleable.RangeBar_thumbTextSize, DEFAULT_THUMB_TEXT_SIZE);
            mThumbTextColor = ta.getColor(R.styleable.RangeBar_thumbTextColor,
                    DEFAULT_THUMB_TEXT_COLOR);

            mDisEnableColor = ta.getColor(R.styleable.RangeBar_disEnabledColor,
                    DEFAULT_DIS_ENABLE_COLOR);
            mBarStrokeWidth = ta2.getDimension(R.styleable.HotelRangeBar_barStrokeWidth, 0);
            mThumbStrokeWidth = ta2.getDimension(R.styleable.HotelRangeBar_thumbStrokeWidth, 0);
            mThumbStrokeColor=ta2.getColor(R.styleable.HotelRangeBar_thumbStrokeColor, DEFAULT_CONNECTING_LINE_COLOR);
            mThumbTextColor2 = ta2.getColor(R.styleable.HotelRangeBar_thumbTextColor2,DEFAULT_THUMB_TEXT_COLOR);
            setEnabled(ta.getBoolean(R.styleable.RangeBar_rangEnable,
                    true));
        } finally {
            ta.recycle();
        }

    }

    private void createBar() {
        mBar = new HotelBar(getContext(), getMarginLeft() + getPaddingLeft(),
                getYPos(), getBarLength(), mTickCount, mTickHeightDP, mBarWeight,
                mBarColor, mThumbTextColor, mThumbTextSize, mDisEnableColor, isEnabled(),mBarStrokeWidth,mThumbTextColor2);
        mBar.setStringList(mIndexList);
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
                    mThumbRadiusDP, mDisEnableColor, isEnabled(),mThumbStrokeColor,mThumbStrokeWidth);
        }
        if (mRightThumb == null) {
            mRightThumb = new Thumb(ctx, yPos, mThumbColorNormal, mThumbColorPressed,
                    mThumbRadiusDP, mDisEnableColor, isEnabled(),mThumbStrokeColor,mThumbStrokeWidth);
        }

        float marginLeft = getMarginLeft();
        float barLength = getBarLength();

        // Initialize thumbs to the desired indices
        mLeftThumb.setX(marginLeft + (mLeftIndex / (float) (mTickCount - 1)) * barLength);
        mRightThumb.setX(marginLeft + (mRightIndex / (float) (mTickCount - 1)) * barLength);

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
        }
    }

    private void onActionMove(float x) {

        // Move the pressed thumb to the new x-position.
        if (mLeftThumb.isPressed()) {
            if (mRightThumb.getX() - x < mLeftThumb.getHalfWidth() + mRightThumb.getHalfWidth()) {
                x = mRightThumb.getX() - mLeftThumb.getHalfWidth() - mRightThumb.getHalfWidth();
            }
            moveThumb(mLeftThumb,Math.min(x,mRightThumb.getX()-mBar.getmTickDistance()));
        } else if (mRightThumb.isPressed()) {
            if (x - mLeftThumb.getX() < mLeftThumb.getHalfWidth() + mRightThumb.getHalfWidth()) {
                x = mLeftThumb.getX() + mLeftThumb.getHalfWidth() + mRightThumb.getHalfWidth();
            }
            moveThumb(mRightThumb, Math.max(x,mLeftThumb.getX()+mBar.getmTickDistance()));
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
        mLeftIndex = 0;
        mRightIndex = mTickCount-1;
        if (mBar != null) {
            mBar.setStringList(this.mIndexList);
        }
        if (mLeftThumb != null && mRightThumb != null) {
            float marginLeft = getMarginLeft();
            float barLength = getBarLength();
            mLeftThumb.setX(marginLeft + (mLeftIndex / (float) (mTickCount - 1)) * barLength);
            mRightThumb.setX(marginLeft + (mRightIndex / (float) (mTickCount - 1)) * barLength);
            invalidate();
        } else {
            createThumbs();
        }
    }

    public static interface OnRangeBarChangeListener {

        public void onIndexChangeListener(HotelRangeBar rangeBar, int leftThumbIndex, int rightThumbIndex);
    }
}
