package com.hunliji.hljvideolibrary.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljvideolibrary.R;
import com.hunliji.hljvideolibrary.interfaces.HljOnRangeSeekBarListener;

import java.util.ArrayList;
import java.util.List;

public class HljRangeSeekBarView extends View {

    private int mHeightTimeLine;
    private List<HljThumb> mThumbs;
    private List<HljOnRangeSeekBarListener> mListeners;
    private float mMaxWidth;
    private float mThumbWidth;
    private float mThumbHeight;
    private int mViewWidth;
    private float mPixelRangeMin;
    private float mPixelRangeMax;
    private float mScaleRangeMax;
    private boolean mFirstRun;
    private int mTimeLineEdgeWidth;
    private float mPixelMinLength; // 允许截取最小时间视频的长度

    private final Paint mShadow = new Paint();
    private final Paint mEdge = new Paint();

    public HljRangeSeekBarView(@NonNull Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HljRangeSeekBarView(@NonNull Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mThumbs = HljThumb.initThumbs(getResources(), getContext());
        mThumbWidth = HljThumb.getWidthBitmap(mThumbs);
        mThumbHeight = HljThumb.getHeightBitmap(mThumbs);

        mScaleRangeMax = 100;
        mHeightTimeLine = CommonUtil.dp2px(getContext(), HljVideoTrimView.VIEW_HEIGHT);
        mTimeLineEdgeWidth = CommonUtil.dp2px(getContext(), 2);

        setFocusable(true);
        setFocusableInTouchMode(true);

        mFirstRun = true;

        int shadowColor = Color.parseColor("#59000000");
        mShadow.setAntiAlias(true);
        mShadow.setColor(shadowColor);
        mEdge.setAntiAlias(true);
        mEdge.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
    }

    public void initMaxWidth() {
        mMaxWidth = mThumbs.get(1)
                .getPos() - mThumbs.get(0)
                .getPos();

        onSeekStop(this,
                0,
                mThumbs.get(0)
                        .getVal());
        onSeekStop(this,
                1,
                mThumbs.get(1)
                        .getVal());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int minW = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();
        mViewWidth = resolveSizeAndState(minW, widthMeasureSpec, 1);

        int minH = getPaddingBottom() + getPaddingTop() + (int) mThumbHeight + mHeightTimeLine;
        int viewHeight = resolveSizeAndState(minH, heightMeasureSpec, 1);

        setMeasuredDimension(mViewWidth, viewHeight);

        mPixelRangeMin = 0;
        mPixelRangeMax = mViewWidth - mThumbWidth;

        if (mFirstRun) {
            for (int i = 0; i < mThumbs.size(); i++) {
                HljThumb th = mThumbs.get(i);
                th.setVal(mScaleRangeMax * i);
                th.setPos(mPixelRangeMax * i);
            }
            // Fire listener callback
            onCreate(this, currentThumb, getThumbValue(currentThumb));
            mFirstRun = false;
        }
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        drawShadow(canvas);
        drawThumbs(canvas);
    }

    private int currentThumb = 0;

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent ev) {
        final HljThumb mThumb;
        final HljThumb mThumb2;
        final float coordinate = ev.getX();
        final int action = ev.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                // Remember where we started
                currentThumb = getClosestThumb(coordinate);

                if (currentThumb == -1) {
                    return false;
                }

                mThumb = mThumbs.get(currentThumb);
                Log.d(HljVideoTrimView.TAG, "set last touchx = " + coordinate);
                mThumb.setLastTouchX(coordinate);
                onSeekStart(this, currentThumb, mThumb.getVal());
                return true;
            }
            case MotionEvent.ACTION_UP: {

                if (currentThumb == -1) {
                    return false;
                }

                mThumb = mThumbs.get(currentThumb);
                onSeekStop(this, currentThumb, mThumb.getVal());
                return true;
            }

            case MotionEvent.ACTION_MOVE: {
                mThumb = mThumbs.get(currentThumb);
                mThumb2 = mThumbs.get(currentThumb == 0 ? 1 : 0);
                // Calculate the distance moved
                final float dx = coordinate - mThumb.getLastTouchX();
                final float newX = mThumb.getPos() + dx;
                if (currentThumb == 0) {
                    if (newX >= mThumb2.getPos() - mPixelMinLength - mThumb.getWidthBitmap()) {
                        Log.d(HljVideoTrimView.TAG, "left 1");
                        mThumb.setPos(mThumb2.getPos() - mPixelMinLength - mThumb.getWidthBitmap());
                    } else if (newX < mPixelRangeMin) {
                        Log.e(HljVideoTrimView.TAG, "left 2");
                        Log.e(HljVideoTrimView.TAG,
                                coordinate + "/" + newX + "/" + mThumb.getLastTouchX() + "/" +
                                        mThumb.getPos());
                        mThumb.setPos(mPixelRangeMin);
                    } else {
                        Log.d(HljVideoTrimView.TAG, "left 3");
                        //Check if thumb is not out of max width
//                        checkPositionThumb(mThumb, mThumb2, dx, true);
                        // Move the object
                        mThumb.setPos(mThumb.getPos() + dx);

                        // Remember this touch position for the next move event
                        mThumb.setLastTouchX(coordinate);
                    }
                } else {
                    if (newX <= mThumb2.getPos() + mThumb2.getWidthBitmap() + mPixelMinLength) {
                        Log.d(HljVideoTrimView.TAG, "right 1");
                        mThumb.setPos(mThumb2.getPos() + mPixelMinLength + mThumb.getWidthBitmap());
                    } else if (newX > mPixelRangeMax) {
                        Log.d(HljVideoTrimView.TAG, "right 2");
                        mThumb.setPos(mPixelRangeMax);
                    } else {
                        Log.d(HljVideoTrimView.TAG, "right 3");
                        //Check if thumb is not out of max width
//                        checkPositionThumb(mThumb2, mThumb, dx, false);
                        // Move the object
                        mThumb.setPos(mThumb.getPos() + dx);
                        // Remember this touch position for the next move event
                        mThumb.setLastTouchX(coordinate);
                    }
                }
                setThumbPos(currentThumb, mThumb.getPos());

                // Invalidate to request a redraw
                invalidate();
                return true;
            }
        }
        return false;
    }

    private void checkPositionThumb(
            @NonNull HljThumb mThumbLeft,
            @NonNull HljThumb mThumbRight,
            float dx,
            boolean isLeftMove) {
        if (isLeftMove && dx < 0) {
            if ((mThumbRight.getPos() - (mThumbLeft.getPos() + dx)) > mMaxWidth) {
                mThumbRight.setPos(mThumbLeft.getPos() + dx + mMaxWidth);
                setThumbPos(1, mThumbRight.getPos());
            }
        } else if (!isLeftMove && dx > 0) {
            if (((mThumbRight.getPos() + dx) - mThumbLeft.getPos()) > mMaxWidth) {
                mThumbLeft.setPos(mThumbRight.getPos() + dx - mMaxWidth);
                setThumbPos(0, mThumbLeft.getPos());
            }
        }
    }

    private int getUnstuckFrom(int index) {
        int unstuck = 0;
        float lastVal = mThumbs.get(index)
                .getVal();
        for (int i = index - 1; i >= 0; i--) {
            HljThumb th = mThumbs.get(i);
            if (th.getVal() != lastVal)
                return i + 1;
        }
        return unstuck;
    }

    private float pixelToScale(int index, float pixelValue) {
        float scale = (pixelValue * 100) / mPixelRangeMax;
        if (index == 0) {
            float pxThumb = (scale * mThumbWidth) / 100;
            return scale + (pxThumb * 100) / mPixelRangeMax;
        } else {
            float pxThumb = ((100 - scale) * mThumbWidth) / 100;
            return scale - (pxThumb * 100) / mPixelRangeMax;
        }
    }

    private float scaleToPixel(int index, float scaleValue) {
        float px = (scaleValue * mPixelRangeMax) / 100;
        if (index == 0) {
            float pxThumb = (scaleValue * mThumbWidth) / 100;
            return px - pxThumb;
        } else {
            float pxThumb = ((100 - scaleValue) * mThumbWidth) / 100;
            return px + pxThumb;
        }
    }

    private void calculateThumbValue(int index) {
        if (index < mThumbs.size() && !mThumbs.isEmpty()) {
            HljThumb th = mThumbs.get(index);
            th.setVal(pixelToScale(index, th.getPos()));
            onSeek(this, index, th.getVal());
        }
    }

    private void calculateThumbPos(int index) {
        if (index < mThumbs.size() && !mThumbs.isEmpty()) {
            HljThumb th = mThumbs.get(index);
            th.setPos(scaleToPixel(index, th.getVal()));
        }
    }

    private float getThumbValue(int index) {
        return mThumbs.get(index)
                .getVal();
    }

    public void setThumbValue(int index, float value) {
        mThumbs.get(index)
                .setVal(value);
        calculateThumbPos(index);
        // Tell the view we want a complete redraw
        invalidate();
    }

    public void setPixelMinLength(float value) {
        mPixelMinLength = (value * mPixelRangeMax) / 100;
        if (mPixelMinLength < 20) {
            mPixelMinLength = 20;
        }
    }

    private void setThumbPos(int index, float pos) {
        mThumbs.get(index)
                .setPos(pos);
        calculateThumbValue(index);
        // Tell the view we want a complete redraw
        invalidate();
    }

    private int getClosestThumb(float coordinate) {
        int closest = -1;
        if (!mThumbs.isEmpty()) {
            for (int i = 0; i < mThumbs.size(); i++) {
                // Find thumb closest to x coordinate
                final float tcoordinate = mThumbs.get(i)
                        .getPos() + mThumbWidth;
                if (coordinate >= mThumbs.get(i)
                        .getPos() && coordinate <= tcoordinate) {
                    closest = mThumbs.get(i)
                            .getIndex();
                }
            }
        }
        return closest;
    }

    private void drawShadow(@NonNull Canvas canvas) {
        if (!mThumbs.isEmpty()) {

            float x1 = 0;
            float x2 = 0;
            for (HljThumb th : mThumbs) {
                if (th.getIndex() == 0) {
                    final float x = th.getPos() + getPaddingLeft();
                    x1 = x;
                    if (x > mPixelRangeMin) {
                        Rect mRect = new Rect((int) mThumbWidth,
                                0,
                                (int) (x + mThumbWidth),
                                mHeightTimeLine);
                        canvas.drawRect(mRect, mShadow);
                    }
                } else {
                    final float x = th.getPos() - getPaddingRight();
                    x2 = x;
                    if (x < mPixelRangeMax) {
                        Rect mRect = new Rect((int) x,
                                0,
                                (int) (mViewWidth - mThumbWidth),
                                mHeightTimeLine);
                        canvas.drawRect(mRect, mShadow);
                    }
                }
            }

            Rect mRectTop = new Rect((int) (x1 + mThumbWidth),
                    0,
                    Math.round(x2),
                    mTimeLineEdgeWidth);
            Rect mRectBottom = new Rect((int) (x1 + mThumbWidth),
                    mHeightTimeLine - mTimeLineEdgeWidth,
                    Math.round(x2),
                    mHeightTimeLine);
            canvas.drawRect(mRectTop, mEdge);
            canvas.drawRect(mRectBottom, mEdge);
        }
    }

    private void drawThumbs(@NonNull Canvas canvas) {
        if (!mThumbs.isEmpty()) {
            for (HljThumb th : mThumbs) {
                if (th.getIndex() == 0) {
                    canvas.drawBitmap(th.getBitmap(),
                            th.getPos() + getPaddingLeft(),
                            getPaddingTop(),
                            null);
                } else {
                    canvas.drawBitmap(th.getBitmap(),
                            th.getPos() - getPaddingRight(),
                            getPaddingTop(),
                            null);
                }
            }
        }
    }

    public void addOnRangeSeekBarListener(HljOnRangeSeekBarListener listener) {

        if (mListeners == null) {
            mListeners = new ArrayList<>();
        }

        mListeners.add(listener);
    }

    private void onCreate(HljRangeSeekBarView rangeSeekBarView, int index, float value) {
        if (mListeners == null)
            return;

        for (HljOnRangeSeekBarListener item : mListeners) {
            item.onCreate(rangeSeekBarView, index, value);
        }
    }

    private void onSeek(HljRangeSeekBarView rangeSeekBarView, int index, float value) {
        if (mListeners == null)
            return;

        for (HljOnRangeSeekBarListener item : mListeners) {
            item.onSeek(rangeSeekBarView, index, value);
        }
    }

    private void onSeekStart(HljRangeSeekBarView rangeSeekBarView, int index, float value) {
        if (mListeners == null)
            return;

        for (HljOnRangeSeekBarListener item : mListeners) {
            item.onSeekStart(rangeSeekBarView, index, value);
        }
    }

    private void onSeekStop(HljRangeSeekBarView rangeSeekBarView, int index, float value) {
        if (mListeners == null)
            return;

        for (HljOnRangeSeekBarListener item : mListeners) {
            item.onSeekStop(rangeSeekBarView, index, value);
        }
    }

    public List<HljThumb> getThumbs() {
        return mThumbs;
    }
}
