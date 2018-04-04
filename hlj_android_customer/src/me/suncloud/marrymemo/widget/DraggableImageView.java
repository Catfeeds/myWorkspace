package me.suncloud.marrymemo.widget;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import me.suncloud.marrymemo.model.V2.ImageHoleV2;
import me.suncloud.marrymemo.view.ImageViewsContainer;

/**
 * Created by luohanlin on 15/5/13.
 */
public class DraggableImageView extends SuperImageView {


    private static final int DRAG_MOVE = 5;
    private long onClickTime=0;
    private boolean isChange;
    private ImageHoleV2 imageHoleV2;

    private ImageViewsContainer belongToContainer;

    public DraggableImageView(Context context) {
        super(context);
    }

    public DraggableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DraggableImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    public void setBelongToContainer(ImageViewsContainer belongToContainer) {
        this.belongToContainer = belongToContainer;
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (view != null) {
            view.requestDisallowInterceptTouchEvent(true);
        }
        if (belongToContainer==null||!belongToContainer.isTouchImageView(this)) {
            return false;
        }
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                if (frozen) {
                    break;
                }
                if (!belongToContainer.checkedTouchEvent(this, event.getX(), event.getY())) {
                    return false;
                }
                savedMatrix.set(matrix);
                pA.set(event.getX(), event.getY());
                pB.set(event.getX(), event.getY());
                mode = DRAG;
                belongToContainer.setHolderShadow(this, true);
                onClickTime=System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (frozen) {
                    break;
                }
                if (mode == DRAG_MOVE) {
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
                belongToContainer.setHolderShadow(this, true);
                break;
            case MotionEvent.ACTION_UP:
                if (frozen) {
                    performClick();
                    break;
                }
                boolean isClick=false;
                if (mode == DRAG || mode == DRAG_MOVE) {
                    if (spacing(pA.x, pA.y, pB.x, pB.y) < 50) {
                        long now = System.currentTimeMillis();
                        if (now - lastClickTime < 500 && spacing(pA.x, pA.y, lastClickPos.x,
                                lastClickPos.y) < 50){
                            //                            doubleClick(pA.x, pA.y);
                            now = 0;
                        } else {
                            if(now - onClickTime <300) {
                                isClick = true;
                                this.performClick();
                            }
                        }
                        lastClickPos.set(pA);
                        lastClickTime = now;
                    }
                }
                mode = NONE;
                if(!isClick) {
                    belongToContainer.setHolderShadow(this, false);
                }
                belongToContainer.exchangeImageResource(this);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                if (frozen) {
                    performClick();
                    break;
                }
                if (mode == DRAG || mode == DRAG_MOVE) {
                    if (spacing(pA.x, pA.y, pB.x, pB.y) < 50) {
                        long now = System.currentTimeMillis();
                        if (now - lastClickTime < 500 && spacing(pA.x, pA.y, lastClickPos.x,
                                lastClickPos.y) < 50) {
                            //                            doubleClick(pA.x, pA.y);
                            now = 0;
                        } else {
//                            this.performClick();
                        }
                        lastClickPos.set(pA);
                        lastClickTime = now;
                    }
                } else {
                    mode = NONE;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG || mode == DRAG_MOVE) {
                    matrix.set(savedMatrix);
                    pB.set(event.getX(), event.getY());
                    matrix.postTranslate(event.getX() - pA.x, event.getY() - pA.y);
                    isChange=true;
                    setImageMatrix(matrix);

                    // 拖动到其他holder中
                    if (belongToContainer.checkingCrossState(this, event.getX(), event.getY())) {
                        mode = DRAG_MOVE;
                    }
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
                        isChange=true;
                        this.matrix.set(matrix);
                        setImageMatrix(matrix);
                    }

                }
                break;
        }
        return true;
    }


    public void setIsChange(boolean isChange) {
        this.isChange = isChange;
    }

    public boolean isChange() {
        return isChange;
    }

    public void setImageHoleV2(ImageHoleV2 imageHoleV2) {
        this.imageHoleV2 = imageHoleV2;
    }

    public ImageHoleV2 getImageHoleV2() {
        return imageHoleV2;
    }
}
