package com.slider.library.Transformers;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by jinxin on 2017/7/31 0031.
 */

public class ClipTransformer extends BaseTransformer {

    private final float MIN_SCALE = 220 * 1.0F / 276;
    private final float MAX_SCALE = 1.0F;
    private final float DIFF_SCALE = MAX_SCALE - MIN_SCALE;
    private final int MAX_ALPHA = 255;

    @Override
    protected void onTransform(View view, float position) {
    }

    @Override
    public void transformPage(View view, float position) {
        GradientDrawable gradientDrawable = null;
        RelativeLayout relativeLayout = (RelativeLayout) view;
        View bgView = relativeLayout.getChildAt(1);
        Drawable drawable = bgView.getBackground();
        if (drawable != null && drawable instanceof GradientDrawable) {
            gradientDrawable = (GradientDrawable) drawable;
        }
        int alpha;
        if (position <= -1.0F) {
            view.setScaleY(MIN_SCALE);
            alpha = MAX_ALPHA;
        } else if (position <= 0.0F) {
            float scale = Math.min(MAX_SCALE, MAX_SCALE - Math.abs(position) * DIFF_SCALE);
            view.setScaleY(scale);
            alpha = (int) Math.min(MAX_ALPHA, Math.abs(position) * MAX_ALPHA);
        } else if (position <= 1.0F) {
            float scale = Math.max(MIN_SCALE, MAX_SCALE - Math.abs(position) * DIFF_SCALE);
            view.setScaleY(scale);
            alpha = (int) Math.min(MAX_ALPHA, Math.abs(position) * MAX_ALPHA);
        } else {
            view.setScaleY(MIN_SCALE);
            alpha = MAX_ALPHA;
        }
        if (gradientDrawable != null) {
            gradientDrawable.setAlpha(alpha);
        }
    }
}