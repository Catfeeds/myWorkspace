package com.hunliji.hljcommonlibrary.models;

import com.google.gson.annotations.SerializedName;

/**
 * 提交评论postBody
 * Created by chen_bin on 2016/9/13 0013.
 */
public class PostCommentBody {
    @SerializedName(value = "content")
    private String content;
    @SerializedName("entity_type")
    private String entityType;
    @SerializedName(value = "entity_id")
    private long entityId;
    @SerializedName("reply_id")
    private long replyId;

    public PostCommentBody() {}

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public long getEntityId() {
        return entityId;
    }

    public void setEntityId(long entityId) {
        this.entityId = entityId;
    }

    public long getReplyId() {
        return replyId;
    }

    public void setReplyId(long replyId) {
        this.replyId = replyId;
    }
}