package com.hunliji.hljimagelibrary.views.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by wangtao on 2017/6/26.
 */

public class TouchMatrixImageView extends AppCompatImageView {

    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM_OR_ROTATE = 3;

    private Bitmap bitmap;
    private boolean frozen;
    private float dist = 1f;
    private int mode = NONE;

    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();
    private PointF pA = new PointF();
    private PointF pB = new PointF();
    private PointF mid = new PointF();

    private PointF lastClickPos = new PointF();
    private long lastClickTime = 0;

    private float imageW;
    private float imageH;
    private float rotatedImageW;
    private float rotatedImageH;
    private int cropWidth;
    private int cropHeight;


    public TouchMatrixImageView(Context context) {
        super(context);
    }

    public TouchMatrixImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchMatrixImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
        bitmap = bm;
        resetCropMatrix(bitmap);
    }

    private void resetCropMatrix(Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }
        imageW = rotatedImageW = bitmap.getWidth();
        imageH = rotatedImageH = bitmap.getHeight();
        if (cropWidth <= 0 || cropHeight <= 0 || imageW <= 0 || imageH <= 0) {
            return;
        }
        mode = NONE;
        matrix.setScale(0, 0);
        fixScale();
        fixTranslation();
        setScaleType(ScaleType.MATRIX);
        setImageMatrix(matrix);
    }

    protected void fixScale() {
        float p[] = new float[9];
        matrix.getValues(p);
        float curScale = Math.abs(p[0]) + Math.abs(p[1]);

        float maxScale = Math.max(cropWidth / rotatedImageW, cropHeight / rotatedImageH);
        if (curScale < maxScale) {
            if (curScale > 0) {
                double scale = maxScale / curScale;
                p[0] = (float) (p[0] * scale);
                p[1] = (float) (p[1] * scale);
                p[3] = (float) (p[3] * scale);
                p[4] = (float) (p[4] * scale);
                matrix.setValues(p);
            } else {
                matrix.setScale(maxScale, maxScale);
            }
        }
    }

    private void fixTranslation() {
        RectF rect = new RectF(0, 0, imageW, imageH);
        matrix.mapRect(rect);

        float height = rect.height();
        float width = rect.width();

        float deltaX = (getWidth() - cropWidth) / 2, deltaY = (getHeight() - cropHeight) / 2;

        if (width > cropWidth) {
            deltaX += (cropWidth - width) / 2;
        }

        if (height > cropHeight) {
            deltaY += (cropHeight - height) / 2;
        }
        matrix.postTranslate(deltaX, deltaY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                if (frozen) {
                    break;
                }
                savedMatrix.set(matrix);
                pA.set(event.getX(), event.getY());
                pB.set(event.getX(), event.getY());
                mode = DRAG;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (frozen) {
                    break;
                }
                if (event.getActionIndex() > 1)
                    break;
                dist = spacing(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
                if (dist > 10f) {
                    savedMatrix.set(matrix);
                    pA.set(event.getX(0), event.getY(0));
                    pB.set(event.getX(1), event.getY(1));
                    mid.set((event.getX(0) + event.getX(1)) / 2,
                            (event.getY(0) + event.getY(1)) / 2);
                    mode = ZOOM_OR_ROTATE;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                if (frozen) {
                    performClick();
                    break;
                }
                if (mode == DRAG) {
                    if (spacing(pA.x, pA.y, pB.x, pB.y) < 50) {
                        long now = System.currentTimeMillis();
                        if (now - lastClickTime < 500 && spacing(pA.x,
                                pA.y,
                                lastClickPos.x,
                                lastClickPos.y) < 50) {
                            now = 0;
                        } else {
                            this.performClick();
                        }
                        lastClickPos.set(pA);
                        lastClickTime = now;
                    }
                }
                mode = NONE;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    matrix.set(savedMatrix);
                    pB.set(event.getX(), event.getY());
                    matrix.postTranslate(event.getX() - pA.x, event.getY() - pA.y);
                    setImageMatrix(matrix);
                } else if (mode == ZOOM_OR_ROTATE) {
                    float newDist = spacing(event.getX(0),
                            event.getY(0),
                            event.getX(1),
                            event.getY(1));
                    Matrix matrix = null;
                    if (newDist > 10f) {
                        matrix = new Matrix();
                        matrix.set(savedMatrix);
                        float tScale = newDist / dist;
                        matrix.postScale(tScale, tScale, mid.x, mid.y);
                    }
                    PointF pC = new PointF(event.getX(1) - event.getX(0) + pA.x,
                            event.getY(1) - event.getY(0) + pA.y);
                    double a = spacing(pB.x, pB.y, pC.x, pC.y);
                    double b = spacing(pA.x, pA.y, pC.x, pC.y);
                    double c = spacing(pA.x, pA.y, pB.x, pB.y);
                    if (b > 10) {
                        double cosA = (b * b + c * c - a * a) / (2 * b * c);
                        double angleA = Math.acos(cosA);
                        double ta = pB.y - pA.y;
                        double tb = pA.x - pB.x;
                        double tc = pB.x * pA.y - pA.x * pB.y;
                        double td = ta * pC.x + tb * pC.y + tc;
                        if (td > 0) {
                            angleA = 2 * Math.PI - angleA;
                        }
                        double rotation = angleA;
                        if (matrix == null) {
                            matrix = new Matrix();
                            matrix.set(savedMatrix);
                        }
                        matrix.postRotate((float) (rotation * 180 / Math.PI), mid.x, mid.y);
                    }
                    if (matrix != null) {
                        this.matrix.set(matrix);
                        setImageMatrix(matrix);
                    }
                }

                break;
        }
        return true;
    }

    public void setCropArea(int cropWidth, int cropHeight) {
        this.cropWidth = cropWidth;
        this.cropHeight = cropHeight;
        resetCropMatrix(bitmap);
    }

    private float spacing(float x1, float y1, float x2, float y2) {
        float x = x1 - x2;
        float y = y1 - y2;
        return (float) Math.sqrt(x * x + y * y);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
        System.gc();
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
}
