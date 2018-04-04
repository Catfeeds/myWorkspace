package me.suncloud.marrymemo.model;

import org.json.JSONObject;

import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by werther on 1/19/16.
 */
public class CustomSetmeal implements Identifiable {
    private static final long serialVersionUID = 3791283094755363439L;
    private long id;
    private long merchantId;
    private String title;
    private String imgCover;
    private NewMerchant merchant;
    private String headerPhoto;
    private String detailImage;
    private String purchaseNotes;
    private int status;
    private String orderGift;
    private int collectorsCount;
    private double actualPrice;
    private boolean isPublished;
    private int watchCount;
    private String createAt;
    private String updateAt;
    private boolean deleted;
    private Photo flowChart;
    private String promiseStaticPath;
    private String promiseImage;
    private Comment comment;
    private String payAllGift;
    private ShareInfo shareInfo;

    public CustomSetmeal(JSONObject jsonObject) {
        if (jsonObject != null) {
            id = jsonObject.optLong("id");
            merchantId = jsonObject.optLong("merchant_id");
            headerPhoto = jsonObject.optString("header_photo");
            detailImage = jsonObject.optString("detail_images");
            purchaseNotes = jsonObject.optString("purchase_notes");
            status = jsonObject.optInt("status");
            orderGift = jsonObject.optString("order_gift");
            collectorsCount = jsonObject.optInt("collectors_count");
            actualPrice = jsonObject.optDouble("actual_price");
            isPublished = jsonObject.optInt("is_published", 0) > 0;
            watchCount = jsonObject.optInt("watch_count");
            createAt = jsonObject.optString("created_at");
            updateAt = jsonObject.optString("update_at");
            deleted = jsonObject.optInt("deleted", 0) > 0;
            flowChart = new Photo(jsonObject.optJSONObject("flow_chart"));
            title = JSONUtil.getString(jsonObject, "title");
            imgCover = JSONUtil.getString(jsonObject,"cover_path");
            merchant = new NewMerchant(jsonObject.optJSONObject("merchant"));
            payAllGift =jsonObject.optString("pay_all_gift");
            if ("0".equals(orderGift)) {
                orderGift = null;
            }
            if ("0".equals(payAllGift)) {
                payAllGift = null;
            }
            JSONObject promise = jsonObject.optJSONObject("promise");
            if (promise != null) {
                promiseStaticPath = promise.optString("static_path");
                promiseImage = JSONUtil.getString(promise.optJSONObject("image"), "url");
            }
            JSONObject commentJson = jsonObject.optJSONObject("comment");
            if(commentJson != null){
                comment = new Comment(commentJson);
            }
            if (!jsonObject.isNull("share")) {
                ShareInfo share = new ShareInfo(jsonObject.optJSONObject("share"));
                if (!JSONUtil.isEmpty(share.getTitle()) && !JSONUtil.isEmpty(share.getUrl())) {
                    shareInfo = share;
                }
            }
        }
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getImgCover() {
        return imgCover;
    }

    public NewMerchant getMerchant() {
        return merchant;
    }

    public long getMerchantId() {
        return merchantId;
    }

    public String getHeaderPhoto() {
        return headerPhoto;
    }

    public String getDetailImage() {
        return detailImage;
    }

    public String getPurchaseNotes() {
        return purchaseNotes;
    }

    public int getStatus() {
        return status;
    }

    public String getOrderGift() {
        return orderGift;
    }

    public int getCollectorsCount() {
        return collectorsCount;
    }

    public double getActualPrice() {
        return actualPrice;
    }

    public boolean getIsPublished() {
        return isPublished;
    }

    public int getWatchCount() {
        return watchCount;
    }

    public String getCreateAt() {
        return createAt;
    }

    public String getUpdateAt() {
        return updateAt;
    }

    public boolean getDeleted() {
        return deleted;
    }

    public Photo getFlowChart() {
        return flowChart;
    }

    public String getPromiseStaticPath() {
        return promiseStaticPath;
    }

    public String getPromiseImage() {
        return promiseImage;
    }

    public Comment getComment() {
        return comment;
    }

    public String getPayAllGift() {
        return payAllGift;
    }
    public ShareInfo getShareInfo() {
        return shareInfo;
    }
}
