package com.hunliji.hljnotelibrary.utils;

import android.util.DisplayMetrics;

/**
 * Created by mo_yu on 16/9/14.
 * 用于笔记本详情和列表通用的几个size参数值
 */
public class NoteMeasures {
    public int feedImgWidth;
    public int singleImgWidth;
    public int singleImgHeight;
    public int doubleImgWidth;
    public int gridWidth;
    public int doubleSpace;
    public int feedSpace;
    public int faceSize;
    public int faceSizeS;
    public int logoSize;
    public int logoSizeS;
    public int screenShotWidth;
    public int screenShotHeight;

    public NoteMeasures(DisplayMetrics dm) {
        feedImgWidth = Math.round((dm.widthPixels - 32 * dm.density) / 3);
        singleImgWidth = Math.round(dm.widthPixels);
        doubleImgWidth = Math.round((dm.widthPixels - 4 * dm.density) / 2);
        singleImgHeight = Math.round(singleImgWidth * 3.0f / 4.0f);
        gridWidth = Math.round(dm.widthPixels);
        doubleSpace = Math.round(4 * dm.density);
        feedSpace = Math.round(2 * dm.density);
        faceSize = Math.round(24 * dm.density);
        faceSizeS = Math.round(14 * dm.density);
        logoSize = Math.round(40 * dm.density);
        logoSizeS = Math.round(28 * dm.density);
        screenShotWidth = Math.round(dm.widthPixels - 32 * dm.density);
        screenShotHeight = Math.round(screenShotWidth * 9.0f / 16.0f);
    }
}
