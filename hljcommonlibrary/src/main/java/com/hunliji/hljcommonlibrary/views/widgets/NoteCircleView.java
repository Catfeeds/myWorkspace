package com.hunliji.hljcommonlibrary.views.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.hunliji.hljcommonlibrary.R;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import java.util.HashMap;
import java.util.Map;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

/**
 * Created by mo_yu on 2017/9/28.笔记轮播指示点
 */
public class NoteCircleView extends View {
    private int mIndex;
    private int state;
    private int totalCount;
    private float mRadius;
    private int largerCircleCount;
    private int normalColor;//未选中点颜色
    private int selectColor;//选中的点颜色
    private float lagerRadius;//大点半径
    private float middleRadius;//中点半径
    private float smallRadius;//小点半径
    private final Paint mPaintPageFill = new Paint(ANTI_ALIAS_FLAG);
    private Map<String, RectF> rectFCache;

    public NoteCircleView(Context context) {
        this(context, null);
    }

    public NoteCircleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NoteCircleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        selectColor = ContextCompat.getColor(context, R.color.colorWhite);
        normalColor = Color.parseColor("#dddddd");
        lagerRadius = CommonUtil.dp2px(context, 3);
        middleRadius = CommonUtil.dp2px(context, 2.5f);
        smallRadius = CommonUtil.dp2px(context, 2);
        largerCircleCount = 5;
        mRadius = lagerRadius;
        mPaintPageFill.setStyle(Paint.Style.FILL);
        mPaintPageFill.setColor(normalColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isSelected()) {
            state = NoteCirclePageIndicator.LARGER_INDICATOR;
            mRadius = lagerRadius;
            mPaintPageFill.setColor(selectColor);
        } else {
            if (totalCount <= largerCircleCount) {
                mRadius = lagerRadius;
                state = NoteCirclePageIndicator.LARGER_INDICATOR;
            } else {
                switch (state) {
                    case NoteCirclePageIndicator.SMALL_INDICATOR:
                        mRadius = smallRadius;
                        break;
                    case NoteCirclePageIndicator.MIDDLE_INDICATOR:
                        mRadius = middleRadius;
                        break;
                    case NoteCirclePageIndicator.LARGER_INDICATOR:
                        mRadius = lagerRadius;
                        break;
                }
            }
            mPaintPageFill.setColor(normalColor);
        }
        float top = (getHeight() - mRadius * 2) / 2;
        float left = (getWidth() - mRadius * 2) / 2;
        float bottom = top + mRadius * 2;
        float right = left + mRadius * 2;
        canvas.save();
        if (isSelected()) {
            drawRoundRect(canvas, left, top, right, bottom, mRadius, mPaintPageFill);
        } else {
            drawRoundRect(canvas, left, top, right, bottom, mRadius, mPaintPageFill);
        }
        canvas.restore();
    }

    private void drawRoundRect(
            Canvas canvas,
            float left,
            float top,
            float right,
            float bottom,
            float radius,
            Paint paint) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawRoundRect(left, top, right, bottom, radius, radius, paint);
        } else {
            if (rectFCache == null) {
                rectFCache = new HashMap<>();
            }
            String key = left + "," + top + "," + right + "," + bottom;
            RectF rectF = rectFCache.get(key);
            if (rectF == null) {
                rectF = new RectF(left, top, right, bottom);
                rectFCache.put(key, rectF);
            }
            canvas.drawRoundRect(rectF, radius, radius, paint);
        }
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public int getIndex() {
        return mIndex;
    }

    public void setIndex(int index) {
        this.mIndex = index;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public void setLargerCircleCount(int largerCircleCount) {
        this.largerCircleCount = largerCircleCount;
    }

    public void setSelectColor(int selectColor) {
        this.selectColor = selectColor;
    }

    public void setNormalColor(int normalColor) {
        this.normalColor = normalColor;
    }

    public void setLagerRadius(float lagerRadius) {
        this.lagerRadius = lagerRadius;
    }

    public void setMiddleRadius(float middleRadius) {
        this.middleRadius = middleRadius;
    }

    public void setSmallRadius(float smallRadius) {
        this.smallRadius = smallRadius;
    }
}