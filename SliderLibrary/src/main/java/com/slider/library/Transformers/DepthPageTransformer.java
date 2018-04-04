package com.slider.library.Transformers;

import android.view.View;

import com.nineoldandroids.view.ViewHelper;

public class DepthPageTransformer extends BaseTransformer {

    private static final float MIN_SCALE = 0.75f;

    @Override
    protected void onTransform(View view, float position) {
        if (position <= 0f) {
            ViewHelper.setTranslationX(view, 0f);
        } else if (position <= 1f) {
//            final float scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position));
            // ViewHelper.setAlpha(view,1-position);
//            ViewHelper.setPivotY(view, 0.5f * view.getHeight());
            ViewHelper.setTranslationX(view, view.getWidth() * -position / 2);
            // ViewHelper.setScaleX(view,scaleFactor);
            // ViewHelper.setScaleY(view,scaleFactor);
        }
    }

    @Override
    protected boolean isPagingEnabled() {
        return true;
    }

}
