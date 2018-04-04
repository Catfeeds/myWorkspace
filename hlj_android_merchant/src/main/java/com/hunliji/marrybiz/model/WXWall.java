package com.hunliji.marrybiz.model;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.MinProgramShareInfo;

/**
 * Created by wangtao on 2017/8/31.
 */

public class WXWall {

    @SerializedName("url")
    private String url;
    @SerializedName("share")
    private MinProgramShareInfo shareInfo;

    public MinProgramShareInfo getShareInfo() {
        return shareInfo;
    }

    public String getUrl() {
        return url;
    }
}
