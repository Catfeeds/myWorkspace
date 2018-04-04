package me.suncloud.marrymemo.model;

/**
 * Created by werther on 1/18/16.
 */
public abstract class TypeModel implements Identifiable {
    private static final long serialVersionUID = 3711448736918803942L;

    public abstract int getType();

    @Override
    public Long getId() {
        return null;
    }
}
