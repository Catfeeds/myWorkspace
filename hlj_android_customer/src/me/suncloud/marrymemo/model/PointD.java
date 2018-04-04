package me.suncloud.marrymemo.model;

/**
 * Created by jinxin on 2016/5/30.
 */
public class PointD implements Identifiable {
    public double x;
    public double y;

    public PointD(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    public Long getId() {
        return null;
    }
}
