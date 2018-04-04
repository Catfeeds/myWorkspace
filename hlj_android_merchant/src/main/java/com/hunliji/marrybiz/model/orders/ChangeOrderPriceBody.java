package com.hunliji.marrybiz.model.orders;

import com.google.gson.annotations.SerializedName;

/**
 * Created by luohanlin on 2017/6/2.
 */

public class ChangeOrderPriceBody {
    @SerializedName("order_sub_id")
    long orderSubId;
    @SerializedName("actual_price")
    Double actualPrice;
    @SerializedName("earnest_money")
    Double earnestMoney;

    public ChangeOrderPriceBody(long orderSubId, Double actualPrice, Double earnestMoney) {
        this.orderSubId = orderSubId;
        this.actualPrice = actualPrice;
        this.earnestMoney = earnestMoney;
    }

    public long getOrderSubId() {
        return orderSubId;
    }

    public void setOrderSubId(long orderSubId) {
        this.orderSubId = orderSubId;
    }

    public Double getActualPrice() {
        return actualPrice;
    }

    public void setActualPrice(Double actualPrice) {
        this.actualPrice = actualPrice;
    }

    public Double getEarnestMoney() {
        return earnestMoney;
    }

    public void setEarnestMoney(Double earnestMoney) {
        this.earnestMoney = earnestMoney;
    }
}
