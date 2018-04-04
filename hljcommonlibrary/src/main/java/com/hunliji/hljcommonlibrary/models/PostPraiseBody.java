package com.hunliji.hljcommonlibrary.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by chen_bin on 2016/9/20 0020.
 */
public class PostPraiseBody {
    @SerializedName(value = "entity_type")
    private String entityType;
    @SerializedName(value = "id")
    private long id;
    @SerializedName(value = "type")
    private int type;//3表示专题评论，5表示回答评论点赞,7订单类评论点赞

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}