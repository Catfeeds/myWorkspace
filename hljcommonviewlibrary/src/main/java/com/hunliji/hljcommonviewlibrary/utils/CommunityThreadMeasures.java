package com.hunliji.hljcommonviewlibrary.utils;

import android.graphics.Point;
import android.util.DisplayMetrics;

/**
 * Created by mo_yu on 16/9/14.
 * 用于话题列表通用的几个size参数值
 */
public class CommunityThreadMeasures {
    public int faceSize;
    public int channelHeight;
    public int dynamicImgWidth;//动态图片宽度（3张）
    public int singleImgWidth;//富文本单张动态图片宽度
    public int singleImgHeight;//富文本单张动态图片高度
    public int rightImgWidth;//动态图片宽度(1,2张)
    public int titleMargin;//无图片无正文时标题间距
    public int imgMargin;//无图片无正文时标题间距
    public int bottomMargin;//无图片无正文时标题间距

    public CommunityThreadMeasures(DisplayMetrics dm, Point point) {
        faceSize = Math.round(dm.density * 14);
        channelHeight = Math.round(24 * dm.density);
        dynamicImgWidth = Math.round((point.x - 32 * dm.density) / 3);
        singleImgWidth = Math.round(point.x - 24 * dm.density);
        singleImgHeight = Math.round(singleImgWidth / 2);
        rightImgWidth = Math.round(dm.density * 80);
        titleMargin = Math.round(10 * dm.density);
        imgMargin = Math.round(30 * dm.density);
        bottomMargin = Math.round(4 * dm.density);
    }
}
