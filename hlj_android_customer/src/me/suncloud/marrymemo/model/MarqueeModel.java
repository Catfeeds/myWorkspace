package me.suncloud.marrymemo.model;

/**
 * Created by mo_yu on 2018/3/14.跑马灯所使用的model
 */

public class MarqueeModel {

    CharSequence title;
    String imagePath;
    boolean hasImage;

    public CharSequence getTitle() {
        return title;
    }

    public void setTitle(CharSequence title) {
        this.title = title;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public boolean isHasImage() {
        return hasImage;
    }

    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
    }
}
