package com.hunliji.hljcardcustomerlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mo_yu on 2017/2/10.银行卡转入理财结果
 */
public class BankRollInResult implements Parcelable {
    @SerializedName(value = "fee")
    private String fee;
    @SerializedName(value = "out_trade_no")
    private String outTradeNo;
    @SerializedName(value = "pay_agent")
    private String payAgent;
    @SerializedName(value = "pay_params")
    private String payParams;//支付凭证，传给连连
    @SerializedName(value = "pay_success")
    private boolean paySuccess;
    @SerializedName("message")
    private String message;//收益提示信息

    public String getFee() {
        return fee;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public String getPayAgent() {
        return payAgent;
    }

    public String getPayParams() {
        return payParams;
    }

    public boolean isPaySuccess() {
        return paySuccess;
    }

    public String getMessage() {
        return message;
    }

    public BankRollInResult() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.fee);
        dest.writeString(this.outTradeNo);
        dest.writeString(this.payAgent);
        dest.writeString(this.payParams);
        dest.writeByte(this.paySuccess ? (byte) 1 : (byte) 0);
        dest.writeString(this.message);
    }

    protected BankRollInResult(Parcel in) {
        this.fee = in.readString();
        this.outTradeNo = in.readString();
        this.payAgent = in.readString();
        this.payParams = in.readString();
        this.paySuccess = in.readByte() != 0;
        this.message = in.readString();
    }

    public static final Creator<BankRollInResult> CREATOR = new Creator<BankRollInResult>() {
        @Override
        public BankRollInResult createFromParcel(Parcel source) {return new BankRollInResult(source);}

        @Override
        public BankRollInResult[] newArray(int size) {return new BankRollInResult[size];}
    };
}
