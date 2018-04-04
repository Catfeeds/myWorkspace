package me.suncloud.marrymemo.model.weddingdress;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hua_rong on 2017/5/8.
 * 填写晒婚纱的商家城市拍摄金额等
 */

public class WeddingInfoBody {

    /**
     *  city		long
     *  id	发布成功后，服务器返回的id	number
     *  merchant_id		number	商家id
     *  price		number
     *  unrecorded_merchant_name
     */

    private String city;
    private long id;
    @SerializedName("merchant_id")
    private Long merchantId;
    private String price;
    @SerializedName("unrecorded_merchant_name")
    private String unrecordedMerchantName;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUnrecordedMerchantName() {
        return unrecordedMerchantName;
    }

    public void setUnrecordedMerchantName(String unrecordedMerchantName) {
        this.unrecordedMerchantName = unrecordedMerchantName;
    }
}
