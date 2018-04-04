package com.hunliji.marrybiz.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Suncloud on 2014/11/27.
 */
public class ScaleHeightImageView extends ImageView {

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
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        int bitmapWidth = getDrawable().getIntrinsicWidth();
        int bitmapHeight = getDrawable().getIntrinsicHeight();

        if (bitmapHeight * width < bitmapWidth * height) {
            setScaleType(ScaleType.CENTER_CROP);
        } else if (bitmapHeight < height) {
            setScaleType(ScaleType.MATRIX);
            float scale = (float) height / bitmapHeight;
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            matrix.postTranslate((width - scale * bitmapWidth) / 2,
                    (height - scale * bitmapHeight) / 2);
            setImageMatrix(matrix);
        } else {
            setScaleType(ScaleType.CENTER_INSIDE);
        }
        super.onDraw(canvas);
    }
}
