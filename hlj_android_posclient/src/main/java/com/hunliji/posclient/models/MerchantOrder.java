package com.hunliji.posclient.models;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

/**
 * Created by chen_bin on 2018/1/17 0017.
 */
public class MerchantOrder {
    @SerializedName(value = "id")
    private long id;
    @SerializedName(value = "merchant_id")
    private long merchantId;
    @SerializedName(value = "merchant_name")
    private String merchantName;
    @SerializedName(value = "trade_no")
    private String tradeNo;
    @SerializedName(value = "actual_money")
    private double actualMoney;
    @SerializedName("paid_money")
    private double paidMoney;//已支付金额
    @SerializedName(value = "created_at")
    private DateTime createdAt;

    public long getId() {
        return id;
    }

    public long getMerchantId() {
        return merchantId;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public double getActualMoney() {
        return actualMoney;
    }

    public double getPaidMoney() {
        return paidMoney;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }
}
