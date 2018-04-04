package com.hunliji.marrybiz.model.comment;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Photo;

import java.util.ArrayList;

/**
 * Created by hua_rong on 2017/6/13.
 * 评论申诉
 */

public class SubmitAppealBody {

    public static final int TYPE_COMMENT = 2;//1：订单 2：回复
    public static final int TYPE_ORDER = 1;

    private String content;
    private String reason;
    private int type;
    private ArrayList<Photo> photos;
    @SerializedName(value = "merchant_id")
    private Long merchantId;
    @SerializedName(value = "community_comment_id")
    private Long communityCommentId;
    @SerializedName(value = "order_comment_id")
    private Long orderCommentId;
    @SerializedName(value = "entity_id")
    private Long entityId;//问题id或回答id

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public void setCommunityCommentId(Long communityCommentId) {
        this.communityCommentId = communityCommentId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setOrderCommentId(Long orderCommentId) {
        this.orderCommentId = orderCommentId;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setPhotos(ArrayList<Photo> photos) {
        this.photos = photos;
    }

}
