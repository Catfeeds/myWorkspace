package com.hunliji.hljnotelibrary.utils;

import com.nineoldandroids.animation.TypeEvaluator;

/**
 * 回弹动画
 * Created by chen_bin on 2017/7/14 0014.
 */
public class KickBackAnimator implements TypeEvaluator<Float> {
    private final float s = 0.70158f;
    float mDuration = 0f;

    public void setDuration(float duration) {
        mDuration = duration;
    }

    public Float evaluate(float fraction, Float startValue, Float endValue) {
        float t = mDuration * fraction;
        float b = startValue.floatValue();
        float c = endValue.floatValue() - startValue.floatValue();
        float d = mDuration;
        float result = calculate(t, b, c, d);
        return result;
    }

    public Float calculate(float t, float b, float c, float d) {
        return c * ((t = t / d - 1) * t * ((s + 1) * t + s) + 1) + b;
    }
}
