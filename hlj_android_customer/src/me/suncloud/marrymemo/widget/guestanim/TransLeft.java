package me.suncloud.marrymemo.widget.guestanim;

import android.animation.ObjectAnimator;
import android.view.View;


public class TransLeft extends BaseEffects {

    @Override
    public void setupAnimation(View view, int screenWidth) {
        int translationX = (int) (0.05* screenWidth);
        getAnimatorSet().playTogether(
                ObjectAnimator.ofFloat(view, "translationX", 0,  -translationX).setDuration(mDuration),
                ObjectAnimator.ofFloat(view, "scaleY", 1.2f,  1.2f).setDuration(mDuration),
                ObjectAnimator.ofFloat(view, "scaleX", 1.2f,  1.2f).setDuration(mDuration),
                ObjectAnimator.ofFloat(view, "alpha", 1, 1).setDuration(mDuration)
        );
    }
}
