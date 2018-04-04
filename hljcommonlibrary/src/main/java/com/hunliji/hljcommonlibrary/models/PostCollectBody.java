package com.hunliji.hljcommonlibrary.models;

import com.google.gson.annotations.SerializedName;

/**
 * 专题收藏的model
 * Created by chen_bin on 2016/9/20 0020.
 */
public class PostCollectBody {
    @SerializedName(value = "followable_type")
    private String followableType;
    @SerializedName(value = "id")
    private long id;

    public PostCollectBody() {}

    public String getFollowableType() {
        return followableType;
    }

    public void setFollowableType(String followableType) {
        this.followableType = followableType;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
