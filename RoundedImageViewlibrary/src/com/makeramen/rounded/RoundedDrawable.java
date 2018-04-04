/*
	Copyright (C) 2013 Make Ramen, LLC
 */

package com.makeramen.rounded;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.util.Log;
import android.widget.ImageView.ScaleType;

public class RoundedDrawable extends Drawable {
    public static final String TAG = "RoundedDrawable";

    private static final int LEFT_TOP_DISABLED = 0x1;
    private static final int LEFT_BOTTOM_DISABLED = 0x2;
    private static final int RIGHT_TOP_DISABLED = 0x4;
    private static final int RIGHT_BOTTOM_DISABLED = 0x8;

    public static final int DEFAULT_BORDER_COLOR = Color.BLACK;

    private final RectF mBounds = new RectF();

    private final RectF mDrawableRect = new RectF();
    private final RectF mBitmapRect = new RectF();
    private final BitmapShader mBitmapShader;
    private final Paint mBitmapPaint;
    private final int mBitmapWidth;
    private final int mBitmapHeight;
    private final RectF mBorderRect = new RectF();
    private final Paint mBorderPaint;
    private final Matrix mShaderMatrix = new Matrix();
    private float mCornerRadius;
    private boolean mOval = false;
    private int disRounded = 0;
    private int mBorderWidth;
    private ColorStateList mBorderColor;
    private ScaleType mScaleType = ScaleType.FIT_XY;

    RoundedDrawable(
            Bitmap bitmap, float cornerRadius, int border, ColorStateList borderColor) {
        this(bitmap, cornerRadius, border, borderColor, false, 0);
    }

    RoundedDrawable(
            Bitmap bitmap,
            float cornerRadius,
            int border,
            ColorStateList borderColor,
            boolean mOval,
            int disRounded) {
        this.disRounded = disRounded;

        mBorderWidth = border;
        mBorderColor = borderColor != null ? borderColor : ColorStateList.valueOf(
                DEFAULT_BORDER_COLOR);
        mBitmapWidth = bitmap.getWidth();
        mBitmapHeight = bitmap.getHeight();
        mBitmapRect.set(0, 0, mBitmapWidth, mBitmapHeight);

        mCornerRadius = cornerRadius;
        mBitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mBitmapShader.setLocalMatrix(mShaderMatrix);

        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setShader(mBitmapShader);

        mBorderPaint = new Paint();
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setColor(mBorderColor.getColorForState(getState(), DEFAULT_BORDER_COLOR));
        mBorderPaint.setStrokeWidth(border);
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap;
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        if (width > 0 && height > 0) {
            bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        } else {
            bitmap = null;
        }

        return bitmap;
    }

    public static Drawable fromDrawable(Drawable drawable, float radius) {
        return fromDrawable(drawable,
                radius,
                0,
                ColorStateList.valueOf(DEFAULT_BORDER_COLOR),
                false,
                0);
    }

    public static Drawable fromDrawable(
            Drawable drawable,
            float radius,
            int border,
            ColorStateList borderColor,
            boolean isOval,
            int disRounded) {
        if (drawable != null) {
            if (drawable instanceof TransitionDrawable) {
                TransitionDrawable td = (TransitionDrawable) drawable;
                int num = td.getNumberOfLayers();

                Drawable[] drawableList = new Drawable[num];
                for (int i = 0; i < num; i++) {
                    Drawable d = td.getDrawable(i);
                    if (d instanceof ColorDrawable) {
                        drawableList[i] = d;
                    } else {
                        drawableList[i] = new RoundedDrawable(drawableToBitmap(td.getDrawable(i)),
                                radius,
                                border,
                                borderColor,
                                isOval,
                                disRounded);
                    }
                }
                return new TransitionDrawable(drawableList);
            }

            Bitmap bm = drawableToBitmap(drawable);
            if (bm != null) {
                return new RoundedDrawable(bm, radius, border, borderColor, isOval, disRounded);
            } else {
                Log.w(TAG, "Failed to create bitmap from drawable!");
            }
        }
        return drawable;
    }

    @Override
    public boolean isStateful() {
        return mBorderColor.isStateful();
    }

    @Override
    protected boolean onStateChange(int[] state) {
        int newColor = mBorderColor.getColorForState(state, 0);
        if (mBorderPaint.getColor() != newColor) {
            mBorderPaint.setColor(newColor);
            return true;
        } else {
            return super.onStateChange(state);
        }
    }

    protected ScaleType getScaleType() {
        return mScaleType;
    }

    protected void setScaleType(ScaleType scaleType) {
        if (scaleType == null) {
            scaleType = ScaleType.FIT_XY;
        }
        if (mScaleType != scaleType) {
            mScaleType = scaleType;
            setMatrix();
        }
    }

    private void setMatrix() {
        mBorderRect.set(mBounds);
        mDrawableRect.set(0 + mBorderWidth,
                0 + mBorderWidth,
                mBorderRect.width() - mBorderWidth,
                mBorderRect.height() - mBorderWidth);

        float scale;
        float dx;
        float dy;

        switch (mScaleType) {
            case CENTER:
                // Log.d(TAG, "CENTER");
                mBorderRect.set(mBounds);
                mDrawableRect.set(0 + mBorderWidth,
                        0 + mBorderWidth,
                        mBorderRect.width() - mBorderWidth,
                        mBorderRect.height() - mBorderWidth);

                mShaderMatrix.set(null);
                mShaderMatrix.setTranslate((int) ((mDrawableRect.width() - mBitmapWidth) * 0.5f +
                                0.5f),
                        (int) ((mDrawableRect.height() - mBitmapHeight) * 0.5f + 0.5f));
                break;
            case CENTER_CROP:
                // Log.d(TAG, "CENTER_CROP");
                mBorderRect.set(mBounds);
                mDrawableRect.set(0 + mBorderWidth,
                        0 + mBorderWidth,
                        mBorderRect.width() - mBorderWidth,
                        mBorderRect.height() - mBorderWidth);

                mShaderMatrix.set(null);

                dx = 0;
                dy = 0;

                if (mBitmapWidth * mDrawableRect.height() > mDrawableRect.width() * mBitmapHeight) {
                    scale = (float) mDrawableRect.height() / (float) mBitmapHeight;
                    dx = (mDrawableRect.width() - mBitmapWidth * scale) * 0.5f;
                } else {
                    scale = (float) mDrawableRect.width() / (float) mBitmapWidth;
                    dy = (mDrawableRect.height() - mBitmapHeight * scale) * 0.5f;
                }

                mShaderMatrix.setScale(scale, scale);
                mShaderMatrix.postTranslate((int) (dx + 0.5f) + mBorderWidth,
                        (int) (dy + 0.5f) + mBorderWidth);
                break;
            case CENTER_INSIDE:
                // Log.d(TAG, "CENTER_INSIDE");
                mShaderMatrix.set(null);

                if (mBitmapWidth <= mBounds.width() && mBitmapHeight <= mBounds.height()) {
                    scale = 1.0f;
                } else {
                    scale = Math.min((float) mBounds.width() / (float) mBitmapWidth,
                            (float) mBounds.height() / (float) mBitmapHeight);
                }

                dx = (int) ((mBounds.width() - mBitmapWidth * scale) * 0.5f + 0.5f);
                dy = (int) ((mBounds.height() - mBitmapHeight * scale) * 0.5f + 0.5f);

                mShaderMatrix.setScale(scale, scale);
                mShaderMatrix.postTranslate(dx, dy);

                mBorderRect.set(mBitmapRect);
                mShaderMatrix.mapRect(mBorderRect);
                mDrawableRect.set(mBorderRect.left + mBorderWidth,
                        mBorderRect.top + mBorderWidth,
                        mBorderRect.right - mBorderWidth,
                        mBorderRect.bottom - mBorderWidth);
                mShaderMatrix.setRectToRect(mBitmapRect, mDrawableRect, Matrix.ScaleToFit.FILL);
                break;
            case FIT_CENTER:
                mBorderRect.set(mBitmapRect);
                mShaderMatrix.setRectToRect(mBitmapRect, mBounds, Matrix.ScaleToFit.CENTER);
                mShaderMatrix.mapRect(mBorderRect);
                mDrawableRect.set(mBorderRect.left + mBorderWidth,
                        mBorderRect.top + mBorderWidth,
                        mBorderRect.right - mBorderWidth,
                        mBorderRect.bottom - mBorderWidth);
                mShaderMatrix.setRectToRect(mBitmapRect, mDrawableRect, Matrix.ScaleToFit.FILL);
                break;
            case FIT_END:
                mBorderRect.set(mBitmapRect);
                mShaderMatrix.setRectToRect(mBitmapRect, mBounds, Matrix.ScaleToFit.END);
                mShaderMatrix.mapRect(mBorderRect);
                mDrawableRect.set(mBorderRect.left + mBorderWidth,
                        mBorderRect.top + mBorderWidth,
                        mBorderRect.right - mBorderWidth,
                        mBorderRect.bottom - mBorderWidth);
                mShaderMatrix.setRectToRect(mBitmapRect, mDrawableRect, Matrix.ScaleToFit.FILL);
                break;
            case FIT_START:
                mBorderRect.set(mBitmapRect);
                mShaderMatrix.setRectToRect(mBitmapRect, mBounds, Matrix.ScaleToFit.START);
                mShaderMatrix.mapRect(mBorderRect);
                mDrawableRect.set(mBorderRect.left + mBorderWidth,
                        mBorderRect.top + mBorderWidth,
                        mBorderRect.right - mBorderWidth,
                        mBorderRect.bottom - mBorderWidth);
                mShaderMatrix.setRectToRect(mBitmapRect, mDrawableRect, Matrix.ScaleToFit.FILL);
                break;
            case FIT_XY:
            default:
                // Log.d(TAG, "DEFAULT TO FILL");
                mBorderRect.set(mBounds);
                mDrawableRect.set(0 + mBorderWidth,
                        0 + mBorderWidth,
                        mBorderRect.width() - mBorderWidth,
                        mBorderRect.height() - mBorderWidth);
                mShaderMatrix.set(null);
                mShaderMatrix.setRectToRect(mBitmapRect, mDrawableRect, Matrix.ScaleToFit.FILL);
                break;
        }
        mBitmapShader.setLocalMatrix(mShaderMatrix);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        // Log.i(TAG, "onboundschange: w: " + bounds.width() + "h:" +
        // bounds.height());
        super.onBoundsChange(bounds);

        mBounds.set(bounds);

        setMatrix();
    }

    @Override
    public void draw(Canvas canvas) {
        // Log.w(TAG, "Draw: " + mScaleType.toString());

        if (mOval) {
            if (mBorderWidth > 0) {
                canvas.drawOval(mBorderRect, mBorderPaint);
                canvas.drawOval(mDrawableRect, mBitmapPaint);
            } else {
                canvas.drawOval(mDrawableRect, mBitmapPaint);
            }
        } else {
            if (mBorderWidth > 0) {
                //                if (mTopQualified) {
                //                    RectF rec = new RectF(0, 0, mBorderRect.width(),
                //                            mBorderRect.height() - mCornerRadius);
                //                    canvas.drawRect(
                //                            new RectF(0, mCornerRadius, mBorderRect.width(),
                //                                    mBorderRect.height()), mBorderPaint);
                //                    canvas.drawRoundRect(rec, mCornerRadius, mCornerRadius,
                //                            mBorderPaint);
                //                    rec = new RectF(0, 0, mDrawableRect.width(),
                //                            mDrawableRect.height() - mCornerRadius);
                //                    canvas.drawRect(
                //                            new RectF(0, mCornerRadius, mDrawableRect.width(),
                //                                    mDrawableRect.height()), mBitmapPaint);
                //                    canvas.drawRoundRect(rec,
                //                            Math.max(mCornerRadius - mBorderWidth, 0),
                //                            Math.max(mCornerRadius - mBorderWidth, 0),
                //                            mBitmapPaint);
                //                } else {
                canvas.drawRoundRect(mBorderRect, mCornerRadius, mCornerRadius, mBorderPaint);
                canvas.drawRoundRect(mDrawableRect,
                        Math.max(mCornerRadius - mBorderWidth, 0),
                        Math.max(mCornerRadius - mBorderWidth, 0),
                        mBitmapPaint);
                //                }
            } else {
                if ((disRounded & LEFT_TOP_DISABLED) > 0) {
                    canvas.drawRect(new RectF(0, 0, mCornerRadius, mCornerRadius), mBitmapPaint);
                }
                if ((disRounded & LEFT_BOTTOM_DISABLED) > 0) {
                    canvas.drawRect(new RectF(0,
                            mDrawableRect.height() - mCornerRadius,
                            mCornerRadius,
                            mDrawableRect.height()), mBitmapPaint);
                }

                if ((disRounded & RIGHT_TOP_DISABLED) > 0) {
                    canvas.drawRect(new RectF(mDrawableRect.width() - mCornerRadius,
                            0,
                            mDrawableRect.width(),
                            mCornerRadius), mBitmapPaint);
                }
                if ((disRounded & RIGHT_BOTTOM_DISABLED) > 0) {
                    canvas.drawRect(new RectF(mDrawableRect.width() - mCornerRadius,
                            mDrawableRect.height() - mCornerRadius,
                            mDrawableRect.width(),
                            mDrawableRect.height()), mBitmapPaint);
                }
                canvas.drawRoundRect(mDrawableRect, mCornerRadius, mCornerRadius, mBitmapPaint);
            }
        }
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void setAlpha(int alpha) {
        mBitmapPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mBitmapPaint.setColorFilter(cf);
    }

    @Override
    public int getIntrinsicWidth() {
        return mBitmapWidth;
    }

    @Override
    public int getIntrinsicHeight() {
        return mBitmapHeight;
    }

    public float getCornerRadius() {
        return mCornerRadius;
    }

    public void setCornerRadius(float radius) {
        mCornerRadius = radius;
    }

    public int getBorderWidth() {
        return mBorderWidth;
    }

    public void setBorderWidth(int width) {
        mBorderWidth = width;
        mBorderPaint.setStrokeWidth(mBorderWidth);
    }

    public int getBorderColor() {
        return mBorderColor.getDefaultColor();
    }

    public void setBorderColor(int color) {
        setBorderColors(ColorStateList.valueOf(color));
    }

    public ColorStateList getBorderColors() {
        return mBorderColor;
    }

    public void setBorderColors(ColorStateList colors) {
        mBorderColor = colors != null ? colors : ColorStateList.valueOf(0);
        mBorderPaint.setColor(mBorderColor.getColorForState(getState(), DEFAULT_BORDER_COLOR));
    }

    public boolean isOval() {
        return mOval;
    }

    public void setOval(boolean oval) {
        mOval = oval;
    }

}