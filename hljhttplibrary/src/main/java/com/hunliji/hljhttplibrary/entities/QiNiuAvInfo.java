package com.hunliji.hljhttplibrary.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wangtao on 2017/8/3.
 */

public class QiNiuAvInfo {

    @SerializedName("video")
    private QiNiuVideoInfo videoInfo;

    public QiNiuVideoInfo getVideoInfo() {
        return videoInfo;
    }

    public class QiNiuVideoInfo{
        @SerializedName("duration")
        float duration;
        @SerializedName("width")
        int width;
        @SerializedName("height")
        int height;

        public float getDuration() {
            return duration;
        }

        public int getHeight() {
            return height;
        }

        public int getWidth() {
            return width;
        }
    }
}
