package com.hunliji.posclient.models.relam;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * pos机支付成功
 * Created by chen_bin on 2018/1/23 0023.
 */
public class PosPayResult extends RealmObject {
    @PrimaryKey
    @SerializedName(value = "traceNo")
    private String traceNo; //流水号
    @SerializedName(value = "amount")
    private String amount; //金额
    @SerializedName(value = "referenceNo")
    private String referenceNo; //参考号
    @SerializedName(value = "cardNo")
    private String cardNo; //卡号
    @SerializedName(value = "out_trade_no")
    private String outTradeNo; //婚礼纪流水号
    @SerializedName(value = "notify_url")
    private String notifyUrl; //婚礼纪回调url

    public String getTraceNo() {
        return traceNo;
    }

    public void setTraceNo(String traceNo) {
        this.traceNo = traceNo;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getReferenceNo() {
        return referenceNo;
    }

    public void setReferenceNo(String referenceNo) {
        this.referenceNo = referenceNo;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }
}