package com.hunliji.hljpaymentlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.hunliji.hljcommonlibrary.utils.NumberFormatUtil;

/**
 * Created by werther on 15/12/29.
 */
public class LLPayer implements Parcelable {

    private double price;
    private String payPath;
    private String payParams;
    private boolean isFirst;
    private long bindCardId;
    private boolean isSimple; //true 简单模式，不绑定银行卡无密码


    public LLPayer(double price, String payPath, String payParams, boolean isSimple) {
        this.price = price;
        this.payPath = payPath;
        this.payParams = payParams;
        this.isSimple = isSimple;
    }
    public boolean isFirst() {
        return isFirst;
    }

    public void setFirst(boolean first) {
        isFirst = first;
    }

    public void setBindCardId(long bindCardId) {
        this.bindCardId = bindCardId;
    }

    public String getPriceStr() {
        return NumberFormatUtil.formatDouble2String(price);
    }

    public double getPrice() {
        return price;
    }

    public String getPayPath() {
        return payPath;
    }

    public String getPayParams() {
        return payParams;
    }

    public long getBindCardId() {
        return bindCardId;
    }

    public boolean isSimple() {
        return isSimple;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.price);
        dest.writeString(this.payPath);
        dest.writeString(this.payParams);
        dest.writeByte(this.isFirst ? (byte) 1 : (byte) 0);
        dest.writeLong(this.bindCardId);
        dest.writeByte(this.isSimple ? (byte) 1 : (byte) 0);
    }

    protected LLPayer(Parcel in) {
        this.price = in.readDouble();
        this.payPath = in.readString();
        this.payParams = in.readString();
        this.isFirst = in.readByte() != 0;
        this.bindCardId = in.readLong();
        this.isSimple = in.readByte() != 0;
    }

    public static final Creator<LLPayer> CREATOR = new Creator<LLPayer>() {
        @Override
        public LLPayer createFromParcel(Parcel source) {return new LLPayer(source);}

        @Override
        public LLPayer[] newArray(int size) {return new LLPayer[size];}
    };
}
