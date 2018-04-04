package me.suncloud.marrymemo.widget.guestanim;

import android.animation.ObjectAnimator;
import android.view.View;


public class ScaleShrink extends BaseEffects {

    @Override
    public void setupAnimation(View view, int screenWidth) {
        getAnimatorSet().playTogether(
                ObjectAnimator.ofFloat(view, "scaleX", 1.1f, 1.2f).setDuration(mDuration),
                ObjectAnimator.ofFloat(view, "scaleY", 1.1f, 1.2f).setDuration(mDuration),
                ObjectAnimator.ofFloat(view, "alpha", 1, 1).setDuration(mDuration)
        );
    }
}
