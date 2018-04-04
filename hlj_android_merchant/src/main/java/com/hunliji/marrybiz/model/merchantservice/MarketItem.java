package com.hunliji.marrybiz.model.merchantservice;

import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.marrybiz.model.Identifiable;
import com.hunliji.marrybiz.model.orders.BdProduct;
import com.hunliji.marrybiz.util.JSONUtil;

import org.json.JSONObject;

/**
 * Created by mo_yu on 2018/1/30.营销单项
 */

public class MarketItem implements Identifiable {
    private long productId;
    private int logo;
    private String title;
    private String subTitle1;
    private String subTitle2;
    private String imagePath;
    private String weddingPlanImagePath;
    private String weddingDressPhotoImagePath;
    int status;//服务状态 1使用中 2已过期

    public MarketItem() {

    }

    public MarketItem(JSONObject jsonObject) {
        if (jsonObject != null) {
            this.productId = jsonObject.optLong("id", 0);
            this.title = JSONUtil.getString(jsonObject, "title");
            this.subTitle1 = JSONUtil.getString(jsonObject, "sub_title1");
            this.subTitle2 = JSONUtil.getString(jsonObject, "sub_title2");
            this.imagePath = JSONUtil.getString(jsonObject, "image_path");
            if (!jsonObject.isNull("property_image_path")) {
                JSONObject imageJsonObject = jsonObject.optJSONObject("property_image_path");
                weddingPlanImagePath = JSONUtil.getString(imageJsonObject, "2");
                weddingDressPhotoImagePath = JSONUtil.getString(imageJsonObject, "6");
            }
            if (productId == BdProduct.YUN_KE) {
                //云蝌婚礼策划和婚礼策划的宣传图片不同，需要做特殊处理
            }
        }
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public int getLogo() {
        return logo;
    }

    public void setLogo(int logo) {
        this.logo = logo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle1() {
        return subTitle1;
    }

    public void setSubTitle1(String subTitle1) {
        this.subTitle1 = subTitle1;
    }

    public String getSubTitle2() {
        return subTitle2;
    }

    public void setSubTitle2(String subTitle2) {
        this.subTitle2 = subTitle2;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public Long getId() {
        return productId;
    }

    public String getYunKeImagePath(long propertyId) {
        if (propertyId == Merchant.PROPERTY_WEDDING_PLAN) {
            return weddingPlanImagePath;
        } else if (propertyId == Merchant.PROPERTY_WEDDING_DRESS_PHOTO) {
            return weddingDressPhotoImagePath;
        }
        return imagePath;
    }

    public void setWeddingPlanImagePath(String weddingPlanImagePath) {
        this.weddingPlanImagePath = weddingPlanImagePath;
    }

    public void setWeddingDressPhotoImagePath(String weddingDressPhotoImagePath) {
        this.weddingDressPhotoImagePath = weddingDressPhotoImagePath;
    }
}
