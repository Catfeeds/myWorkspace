package me.suncloud.marrymemo.widget.guestanim;

public enum EffectStyle {

    TRANS_LEFT(TransLeft.class), TRANS_Right(TransRight.class), SCALE_ZOOM(ScaleZoom.class),
    SCALE_SHRINK(
            ScaleShrink.class);

    private Class<? extends BaseEffects> effectsClazz;

    EffectStyle(Class<? extends BaseEffects> mclass) {
        effectsClazz = mclass;
    }

    public BaseEffects getAnimator() {
        BaseEffects bEffects;
        try {
            bEffects = effectsClazz.newInstance();
        } catch (ClassCastException e) {
            throw new Error("Can not init animatorClazz instance");
        } catch (InstantiationException e) {
            throw new Error("Can not init animatorClazz instance");
        } catch (IllegalAccessException e) {
            throw new Error("Can not init animatorClazz instance");
        }
        return bEffects;
    }
}
