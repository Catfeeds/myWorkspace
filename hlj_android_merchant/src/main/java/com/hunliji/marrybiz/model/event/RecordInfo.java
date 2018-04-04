package com.hunliji.marrybiz.model.event;

import android.text.TextUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljhttplibrary.utils.GsonUtil;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * 活动申请记录model
 * Created by chen_bin on 2016/9/26 0026.
 */
public class RecordInfo {
    @SerializedName(value = "id")
    private long id;
    @SerializedName(value = "merchant_id")
    private long merchantId;
    @SerializedName(value = "created_at")
    private DateTime createdAt;
    @SerializedName(value = "updated_at")
    private DateTime updatedAt;
    @SerializedName(value = "title")
    private String title;
    @SerializedName(value = "activity_time")
    private String showTimeTitle;
    @SerializedName(value = "content")
    private String content;
    @SerializedName(value = "imgs")
    private String imgs;
    @SerializedName(value = "merchant_info")
    private String merchantInfo;
    @SerializedName(value = "reason")
    private String reason;
    @SerializedName(value = "people_limit")
    private String signUpLimit;
    @SerializedName(value = "gift")
    private String gift;
    @SerializedName(value = "visit_gift")
    private String shopGift;
    @SerializedName(value = "comment_gift")
    private String commentGift;
    @SerializedName(value = "order_gift")
    private String orderGift;
    @SerializedName(value = "status")
    private int status;
    @SerializedName(value = "has_prize")
    private int hasPrize; //是否抽奖 0否1是

    private transient JsonElement giftJson;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(long merchantId) {
        this.merchantId = merchantId;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
    }

    public DateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShowTimeTitle() {
        return showTimeTitle;
    }

    public void setShowTimeTitle(String showTimeTitle) {
        this.showTimeTitle = showTimeTitle;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ArrayList<Photo> getImgs() {
        if (TextUtils.isEmpty(imgs)) {
            return null;
        }
        return GsonUtil.getGsonInstance()
                .fromJson(imgs, new TypeToken<List<Photo>>() {}.getType());
    }

    public void setImgs(List<Photo> photos) {
        JsonArray array = new JsonArray();
        for (Photo photo : photos) {
            if (!TextUtils.isEmpty(photo.getImagePath())) {
                JsonObject json = new JsonObject();
                json.addProperty("img", photo.getImagePath());
                json.addProperty("width", photo.getWidth());
                json.addProperty("height", photo.getHeight());
                array.add(json);
            }
        }
        this.imgs = array.toString();
    }

    public String getMerchantInfo() {return merchantInfo;}

    public void setMerchantInfo(String merchantInfo) {
        this.merchantInfo = merchantInfo;
    }

    public String getReason() {
        return reason;
    }

    public String getSignUpLimit() {
        return signUpLimit;
    }

    public void setSignUpLimit(String signUpLimit) {
        this.signUpLimit = signUpLimit;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getHasPrize() {
        return hasPrize;
    }

    public void setHasPrize(int hasPrize) {
        this.hasPrize = hasPrize;
    }

    private JsonElement getGiftJson() {
        if (giftJson == null) {
            giftJson = GsonUtil.getGsonInstance()
                    .fromJson(gift, JsonElement.class);
        }
        return giftJson;
    }

    public String getShopGift() {
        if (shopGift == null) {
            shopGift = CommonUtil.getAsString(getGiftJson(), "visit_gift");
        }
        return shopGift;
    }

    public void setShopGift(String shopGift) {
        this.shopGift = shopGift;
    }

    public String getCommentGift() {
        if (commentGift == null) {
            commentGift = CommonUtil.getAsString(getGiftJson(), "comment_gift");
        }
        return commentGift;
    }

    public void setCommentGift(String commentGift) {
        this.commentGift = commentGift;
    }

    public String getOrderGift() {
        if (orderGift == null) {
            orderGift = CommonUtil.getAsString(getGiftJson(), "order_gift");
        }
        return orderGift;
    }

    public void setOrderGift(String orderGift) {
        this.orderGift = orderGift;
    }

}