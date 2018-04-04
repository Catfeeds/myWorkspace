package com.hunliji.marrybiz.model.college;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

/**
 * Created by luohanlin on 2017/11/22.
 */

public class CollegeItem {
    String id;
    @SerializedName("list_img")
    String imgPath;
    @SerializedName("rich_text_url")
    String url;
    @SerializedName("watch_count")
    String watchCount;
    @SerializedName("is_stick")
    boolean isStick;
    String title;
    @SerializedName("sub_title")
    String subTitle;

    // 1精品课程2运营攻略3课程预告
    private transient String typeTitle;

    public static final int TYPE_FINE_CLASS = 1;
    public static final int TYPE_MARKET_CLASS = 2;
    public static final int TYPE_CLASS_PRE = 3;

    public boolean isStick() {
        return isStick;
    }

    public String getTitle() {
        return title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public String getId() {
        return id;
    }

    public String getImgPath() {
        return imgPath;
    }

    public String getUrl() {
        return url;
    }

    public String getWatchCount() {
        return watchCount;
    }

    public void setTypeTitle(int type) {
        this.typeTitle = typeTitle;
        switch (type) {
            case TYPE_CLASS_PRE:
                typeTitle = "课程预告";
                break;
            case TYPE_FINE_CLASS:
                typeTitle = "精品课程";
                break;
            case TYPE_MARKET_CLASS:
                typeTitle = "运营攻略";
                break;
        }
    }

    public String getTypeTitle() {
        if (!TextUtils.isEmpty(typeTitle)) {
            return typeTitle;
        }
        return title;
    }
}
