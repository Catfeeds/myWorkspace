/**
 *
 */
package me.suncloud.marrymemo.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;

/**
 * @author iDay
 */
public class SuperImageView extends ImageView {

    static final float MAX_SCALE = 2.0f;
    static final int NONE = 0;
    int mode = NONE;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    static final int ROTATE = 3;
    static final int ZOOM_OR_ROTATE = 4;
    float imageW;
    float imageH;
    float rotatedImageW;
    float rotatedImageH;
    float viewW;
    float viewH;
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();
    PointF pA = new PointF();
    PointF pB = new PointF();
    PointF mid = new PointF();
    PointF lastClickPos = new PointF();
    long lastClickTime = 0;
    double rotation = 0.0;
    float dist = 1f;
    boolean frozen;
    protected ViewGroup view;

    private Bitmap maskBitmap;
    private Paint paint;
    private Rect rectF;
    private Bitmap bitmap;

    public SuperImageView(Context context) {
        super(context);
        init();
    }

    public SuperImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SuperImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        // setScaleType(ImageView.ScaleType.MATRIX);
    }

    public void setMask(Bitmap maskBitmap) {
        if (maskBitmap != null) {
            this.maskBitmap = maskBitmap;
            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setFilterBitmap(true);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            invalidate();
        }
    }

    public void setFrozen(boolean frozen) {
        this.frozen = frozen;
    }

    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        //		setImageWidthHeight();
    }

    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        if (drawable instanceof TransitionDrawable) {
            TransitionDrawable transitionDrawable = (TransitionDrawable) drawable;
            if (transitionDrawable.getNumberOfLayers() > 0) {
                int size = transitionDrawable.getNumberOfLayers();
                for (int i = 0; i < size; i++) {
                    Drawable d = transitionDrawable.getDrawable(i);
                    if (d != null && d instanceof BitmapDrawable) {
                        bitmap = ((BitmapDrawable) d).getBitmap();
                        break;
                    }
                }
            }
        } else if (drawable instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) drawable).getBitmap();
        }
        setImageWidthHeight();
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public Bitmap getMaskBitmap() {
        return maskBitmap;
    }

    public void setImageResource(int resId) {
        super.setImageResource(resId);
        setImageWidthHeight();
    }

    private void setImageWidthHeight() {
        Drawable d = getDrawable();
        if (d == null) {
            return;
        }
        imageW = rotatedImageW = d.getIntrinsicWidth();
        imageH = rotatedImageH = d.getIntrinsicHeight();
        initImage();
    }

    protected void initImage() {
        if (viewW <= 0 || viewH <= 0 || imageW <= 0 || imageH <= 0) {
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

        float maxScale = Math.max(viewW / rotatedImageW, viewH / rotatedImageH);
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

        float deltaX = 0, deltaY = 0;

        if (width > viewW) {
            deltaX = (viewW - width) / 2;
        }

        if (height > viewH) {
            deltaY = (viewH - height) / 2;
        }
        matrix.postTranslate(deltaX, deltaY);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (view != null) {
            view.requestDisallowInterceptTouchEvent(true);
        }
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
                    mid.set((event.getX(0) + event.getX(1)) / 2, (event.getY(0) + event.getY(1))
                            / 2);
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
                        if (now - lastClickTime < 500 && spacing(pA.x, pA.y, lastClickPos.x,
                                lastClickPos.y) < 50) {
                            //                            doubleClick(pA.x, pA.y);
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
                    float newDist = spacing(event.getX(0), event.getY(0), event.getX(1), event
                            .getY(1));
                    Matrix matrix = null;
                    if (newDist > 10f) {
                        matrix = new Matrix();
                        matrix.set(savedMatrix);
                        float tScale = newDist / dist;
                        matrix.postScale(tScale, tScale, mid.x, mid.y);
                    }
                    PointF pC = new PointF(event.getX(1) - event.getX(0) + pA.x, event.getY(1) -
                            event.getY(0) + pA.y);
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
                        rotation = angleA;
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

    protected float spacing(float x1, float y1, float x2, float y2) {
        float x = x1 - x2;
        float y = y1 - y2;
        return (float) Math.sqrt(x * x + y * y);
    }

    private void doubleClick(float x, float y) {
        float p[] = new float[9];
        matrix.getValues(p);
        float curScale = Math.abs(p[0]) + Math.abs(p[1]);

        float minScale = Math.min((float) viewW / (float) rotatedImageW, (float) viewH / (float)
                rotatedImageH);
        if (curScale <= minScale + 0.01) {
            float toScale = Math.max(minScale, MAX_SCALE) / curScale;
            matrix.postScale(toScale, toScale, x, y);
        } else {
            float toScale = minScale / curScale;
            matrix.postScale(toScale, toScale, x, y);
            fixTranslation();
        }
        setImageMatrix(matrix);
    }

    public void setMatrix(Matrix matrix) {
        this.matrix = matrix;
        super.setImageMatrix(matrix);
    }

    public void setScrollView(ScrollView scrollView) {
        view = scrollView;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewH = h;
        viewW = w;
        rectF = new Rect(0, 0, w, h);
        invalidate();
    }


    public boolean isMaskTouch(float x, float y) {
        if (maskBitmap != null) {
            float xScale = maskBitmap.getWidth() / (float) getWidth();
            float yScale = maskBitmap.getHeight() / (float) getHeight();
            try {
                return maskBitmap.getPixel(Math.round(x * xScale), Math.round(y * yScale)) != 0;
            } catch (Exception ignored) {
                return false;
            }
        }
        return x > 0 && y > 0 && x < getWidth() && y < getHeight();
    }


    private Bitmap tempBitmap;
    private Canvas canvas2;
    private Paint clearPaint;

    public void recycle() {
        if (canvas2 != null) {
            canvas2 = null;
        }
        if (tempBitmap != null) {
            tempBitmap.recycle();
            tempBitmap = null;
        }
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
        if (maskBitmap != null) {
            maskBitmap.recycle();
            maskBitmap = null;
        }
        System.gc();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        if (maskBitmap != null) {
            if (tempBitmap == null) {
                tempBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            }
            if (canvas2 == null) {
                canvas2 = new Canvas(tempBitmap);
                if (rectF == null) {
                    rectF = new Rect(0, 0, getWidth(), getHeight());
                }
            }
            if (clearPaint == null) {
                clearPaint = new Paint();
                clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            }
            canvas2.drawPaint(clearPaint);
            super.onDraw(canvas2);
            canvas2.drawBitmap(maskBitmap, null, rectF, paint);
            canvas.drawBitmap(tempBitmap, 0, 0, null);
        } else {
            super.onDraw(canvas);
        }
    }
}