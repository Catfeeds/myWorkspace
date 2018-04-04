package com.hunliji.hljcommonlibrary.models.merchant_feed;

import android.text.TextUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Photo;

import java.util.List;

/**
 * 动态上传
 * Created by chen_bin on 2017/3/17 0017.
 */
public class PublishMerchantFeedPostBody {
    @SerializedName(value = "imglist")
    JsonArray imglist;
    @SerializedName(value = "mark_ids")
    String markIds;
    @SerializedName(value = "describe")
    String describe;
    @SerializedName(value = "video_origin_path")
    String originPath;
    @SerializedName(value = "video_persistent_id")
    String persistentId;

    public void setImgList(List<Photo> photos) {
        JsonArray array = new JsonArray();
        for (Photo photo : photos) {
            if (!TextUtils.isEmpty(photo.getImagePath())) {
                JsonObject object = new JsonObject();
                object.addProperty("image_path", photo.getImagePath());
                object.addProperty("width", photo.getWidth());
                object.addProperty("height", photo.getHeight());
                array.add(object);
            }
        }
        this.imglist = array;
    }

    public String getMarkIds() {
        return markIds;
    }

    public void setMarkIds(String markIds) {
        this.markIds = markIds;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getOriginPath() {
        return originPath;
    }

    public void setOriginPath(String originPath) {
        this.originPath = originPath;
    }

    public String getPersistentId() {
        return persistentId;
    }

    public void setPersistentId(String persistentId) {
        this.persistentId = persistentId;
    }

}
