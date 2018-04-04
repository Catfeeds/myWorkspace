package me.suncloud.marrymemo.widget.guestanim;

import android.animation.ObjectAnimator;
import android.view.View;


public class ScaleZoom extends BaseEffects {

    @Override
    public void setupAnimation(View view, int screenWidth) {
        getAnimatorSet().playTogether(
                ObjectAnimator.ofFloat(view, "scaleX", 1.2f,  1.1f).setDuration(mDuration),
                ObjectAnimator.ofFloat(view, "scaleY", 1.2f,  1.1f).setDuration(mDuration),
                ObjectAnimator.ofFloat(view, "alpha", 1, 1).setDuration(mDuration)
        );
    }
}
