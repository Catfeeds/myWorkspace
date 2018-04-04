package me.suncloud.marrymemo.widget.guestanim;


import android.animation.AnimatorSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;


public abstract class BaseEffects {


    long mDuration = 2000;

    private AnimatorSet mAnimatorSet = new AnimatorSet();

    public abstract void setupAnimation(View view, int screenWidth);

    public AnimatorSet getAnimatorSet() {
        mAnimatorSet.setInterpolator(new DecelerateInterpolator());
        return mAnimatorSet;
    }

    public void setDuration(long duration) {
        this.mDuration = duration;
    }

}
