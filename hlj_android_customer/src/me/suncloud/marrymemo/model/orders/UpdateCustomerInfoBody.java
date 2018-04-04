package me.suncloud.marrymemo.model.orders;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

/**
 * Created by luohanlin on 2017/5/23.
 */

public class UpdateCustomerInfoBody {
    @SerializedName("buyer_name")
    String buyerName;
    @SerializedName("buyer_phone")
    String buyerPhone;
    @SerializedName("time")
    DateTime weddingTime;
    @SerializedName("id")
    Long orderId;

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getBuyerPhone() {
        return buyerPhone;
    }

    public void setBuyerPhone(String buyerPhone) {
        this.buyerPhone = buyerPhone;
    }

    public DateTime getWeddingTime() {
        return weddingTime;
    }

    public void setWeddingTime(DateTime weddingTime) {
        this.weddingTime = weddingTime;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
}
