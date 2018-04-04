package com.hunliji.marrybiz.util;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;

public class LinearSeries extends AbstractSeries {
    private PointF mLastPoint;

    @Override
    public void drawPoint(Canvas canvas, AbstractPoint point, float scaleX, float scaleY,
                          Rect gridBounds) {
        final float x = (float) (gridBounds.left + (scaleX * (point.getX() - getMinX())));
        final float y = (float) (gridBounds.bottom - (scaleY * (point.getY() - getMinY())));

        float zeroY = (float) (gridBounds.bottom);

        // 贝塞尔曲线
        //        if (mLastPoint != null) {
        //            mPaint.setStyle(Paint.Style.STROKE);
        //            mPaint.setStrokeCap(Paint.Cap.ROUND);
        //            mPaint.setStrokeWidth(2);
        //            mPaint.setAntiAlias(true);
        //            final Path path = new Path();
        //            path.moveTo(mLastPoint.x, mLastPoint.y);
        //            float x2 = (x + mLastPoint.x) / 2;
        //            float y2 = (y + mLastPoint.y) / 2;
        //
        //            path.quadTo(x2, y2, x, y);
        //            canvas.drawPath(path, mPaint);
        //        }else {
        //            mLastPoint = new PointF();
        //        }

        if (mLastPoint != null) {
            mPaint.setColor(mLineColor);
            mPaint.setStrokeWidth(2);
            canvas.drawLine(mLastPoint.x, mLastPoint.y, x, y, mPaint);

            drawShape(canvas, mPaint, mLastPoint, new PointF(x, y), zeroY);

            // 画竖线
            mPaint.setStrokeWidth(1);
            canvas.drawLine(x, y, x, zeroY, mPaint);
            mLastPoint.set(x, y);
        } else {
            mLastPoint = new PointF();

            mLastPoint.set(x, y);

            // 画竖线
            mPaint.setColor(mLineColor);
            mPaint.setStrokeWidth(1);
            canvas.drawLine(mLastPoint.x, mLastPoint.y, mLastPoint.x, zeroY, mPaint);
        }


    }

    /**
     * 下方区域阴影
     */
    private void drawShape(Canvas canvas, Paint mPaint, PointF mLastPoint, PointF currentPoint,
                           float zeroY) {
        mPaint.setColor(mShadowColor);

        float gapHeight = currentPoint.y - mLastPoint.y;

        // 先画 rect
        mPaint.setStyle(Paint.Style.FILL);
        Rect rect = new Rect((int) mLastPoint.x, gapHeight > 0 ? (int) currentPoint.y : (int)
                mLastPoint.y, (int) currentPoint.x, (int) zeroY);
        canvas.drawRect(rect, mPaint);

        // 再画 triangle
        mPaint.setStyle(Paint.Style.FILL);
        Path trianglePath = new Path();
        trianglePath.moveTo(mLastPoint.x, mLastPoint.y);
        trianglePath.lineTo(currentPoint.x, currentPoint.y);
        trianglePath.lineTo(gapHeight < 0 ? (int) currentPoint.x : (int) mLastPoint.x,
                gapHeight > 0 ? (int) currentPoint.y : (int) mLastPoint.y);
        trianglePath.close();
        canvas.drawPath(trianglePath, mPaint);
    }


    @Override
    protected void onDrawingComplete() {
        mLastPoint = null;
    }

    public static class LinearPoint extends AbstractPoint {
        public LinearPoint() {
            super();
        }

        public LinearPoint(double x, double y) {
            super(x, y);
        }
    }
}