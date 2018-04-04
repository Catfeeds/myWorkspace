package me.suncloud.marrymemo.model.wallet;

import com.google.gson.annotations.SerializedName;

/**
 * Created by luohanlin on 2017/9/4.
 */

public class FinancialProduct {
    long id;
    String title;
    String img;
    @SerializedName("new_img")
    String newImg;
    int group;
    @SerializedName("target_type")
    String targetType;
    String url;
    @SerializedName("min_version")
    String minVersion;
    @SerializedName("group_name")
    String groupName;

    public String getNewImg() {
        return newImg;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getImg() {
        return img;
    }

    public int getGroup() {
        return group;
    }

    public String getTargetType() {
        return targetType;
    }

    public String getUrl() {
        return url;
    }

    public String getMinVersion() {
        return minVersion;
    }

    public String getGroupName() {
        return groupName;
    }
}
