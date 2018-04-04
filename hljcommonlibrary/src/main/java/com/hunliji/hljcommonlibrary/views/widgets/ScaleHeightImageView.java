package com.hunliji.hljcommonlibrary.views.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Suncloud on 2014/11/27.
 */
public class ScaleHeightImageView extends AppCompatImageView {

    private int width;
    private int height;
    private int bitmapWidth;
    private int bitmapHeight;

    public ScaleHeightImageView(Context context) {
        super(context);
    }

    public ScaleHeightImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScaleHeightImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        if(drawable!=null) {
            bitmapWidth = drawable.getIntrinsicWidth();
            bitmapHeight = drawable.getIntrinsicHeight();
            if(width>2&&height>2){
                onChangeScaleType(width,height,bitmapWidth,bitmapHeight);
            }
        }
        super.setImageDrawable(drawable);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        width=w;
        height=h;
        if(bitmapHeight >2&&bitmapWidth>2){
            onChangeScaleType(width,height,bitmapWidth,bitmapHeight);
        }
        super.onSizeChanged(w, h, oldw, oldh);
    }

    private void onChangeScaleType(int width,int height,int bitmapWidth,int bitmapHeight){
        if (bitmapHeight * width < bitmapWidth * height) {
            setScaleType(ScaleType.CENTER_CROP);
        } else if (bitmapHeight < height) {
            setScaleType(ImageView.ScaleType.MATRIX);
            float scale = (float) height / bitmapHeight;
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            matrix.postTranslate((width - scale * bitmapWidth) / 2, (height - scale * bitmapHeight) / 2);
            setImageMatrix(matrix);
        } else {
            setScaleType(ScaleType.CENTER_INSIDE);
        }
    }
}
