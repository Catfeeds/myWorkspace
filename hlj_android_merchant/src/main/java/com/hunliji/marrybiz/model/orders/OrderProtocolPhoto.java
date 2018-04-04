package com.hunliji.marrybiz.model.orders;

import com.google.gson.annotations.SerializedName;

/**
 * Created by luohanlin on 2017/5/31.
 */

public class OrderProtocolPhoto {
    @SerializedName("img")
    String imgPath;
    int width;
    int height;

    public OrderProtocolPhoto(String imgPath, int width, int height) {
        this.imgPath = imgPath;
        this.width = width;
        this.height = height;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
