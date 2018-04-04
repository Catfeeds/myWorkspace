package com.hunliji.hljhttplibrary.entities;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import com.hunliji.hljhttplibrary.utils.GsonUtil;

/**
 * Created by Suncloud on 2016/8/24.
 */
public class HljUploadResult {
    private String domain;
    @SerializedName(value = "image_path", alternate = {"video_path", "audio_path"})
    private String path;
    @SerializedName("persistent_id")
    private String persistentId;
    private int width;
    private int height;
    private JsonElement orientation;

    @SerializedName("avinfo")
    private JsonElement avinfoJson;
    private QiNiuAvInfo qiNiuAvInfo;

    public String getUrl() {
        if (TextUtils.isEmpty(domain) || TextUtils.isEmpty(path)) {
            return null;
        }
        return domain + path;
    }

    public String getPersistentId() {
        return persistentId;
    }

    public int getWidth() {
        if (isRotate()) {
            return height;
        }
        return width;
    }

    public int getHeight() {
        if (isRotate()) {
            return width;
        }
        return height;
    }

    private boolean isRotate() {
        if (orientation == null) {
            return false;
        }
        try {
            String val = orientation.getAsJsonObject()
                    .get("val")
                    .getAsString();
            switch (val.toLowerCase()) {
                case "left-top":
                case "left-bottom":
                case "right-top":
                case "right-bottom":
                    return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public QiNiuAvInfo getAvinfo() {
        if (qiNiuAvInfo != null) {
            return qiNiuAvInfo;
        }
        if (avinfoJson == null) {
            return null;
        }
        try {
            qiNiuAvInfo = GsonUtil.getGsonInstance()
                    .fromJson(avinfoJson, QiNiuAvInfo.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return qiNiuAvInfo;
    }

    public int getVideoWidth() {
        try {
            return getAvinfo().getVideoInfo()
                    .getWidth();
        } catch (Exception ignored) {

        }
        return 0;
    }

    public int getVideoHeight() {
        try {
            return getAvinfo().getVideoInfo()
                    .getHeight();
        } catch (Exception ignored) {

        }
        return 0;
    }

    public float getVideoDuration() {
        try {
            return getAvinfo().getVideoInfo()
                    .getDuration();
        } catch (Exception ignored) {

        }
        return 0;
    }
}
