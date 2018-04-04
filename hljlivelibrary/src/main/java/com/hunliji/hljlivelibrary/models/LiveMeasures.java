package com.hunliji.hljlivelibrary.models;

import android.graphics.Point;
import android.util.DisplayMetrics;

/**
 * Created by mo_yu on 16/10/24.
 * 用于直播详情和列表通用的几个size参数值
 */
public class LiveMeasures {
    public int faceSize;
    public int liveImgWidth;
    public int liveImgHeight;
    public int workImgWidth;
    public int qaAvatarWidth;
    public int threadWidth;

    public int leftMargin;

    public LiveMeasures(DisplayMetrics dm, Point point) {
        faceSize = Math.round(dm.density * 20);
        liveImgWidth = Math.round(point.x);
        liveImgHeight = Math.round(liveImgWidth * 1 / 2);
        workImgWidth = Math.round(dm.density * 116);
        qaAvatarWidth = Math.round(dm.density * 36);
        threadWidth = Math.round(dm.density * 74);
        leftMargin = Math.round(dm.density * 16);
    }
}
