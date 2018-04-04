package com.hunliji.hljcardcustomerlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mo_yu on 2017/4/18.会员购买订单
 */

public class MemberOrder implements Parcelable {

    @SerializedName(value = "id")
    private long id;
    @SerializedName(value = "order_no")
    private String orderNo;//订单编号
    @SerializedName(value = "pay_money")
    private double payMoney;//需支付的金额
    @SerializedName(value = "original_price")
    private double originalPrice;//原价
    @SerializedName(value = "user_id")
    private long userId;

    public long getId() {
        return id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public double getPayMoney() {
        return payMoney;
    }

    public double getOriginalPrice() {
        return originalPrice;
    }

    public long getUserId() {
        return userId;
    }

    public MemberOrder() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.orderNo);
        dest.writeDouble(this.payMoney);
        dest.writeDouble(this.originalPrice);
        dest.writeLong(this.userId);
    }

    protected MemberOrder(Parcel in) {
        this.id = in.readLong();
        this.orderNo = in.readString();
        this.payMoney = in.readDouble();
        this.originalPrice = in.readDouble();
        this.userId = in.readLong();
    }

    public static final Creator<MemberOrder> CREATOR = new Creator<MemberOrder>() {
        @Override
        public MemberOrder createFromParcel(Parcel source) {return new MemberOrder(source);}

        @Override
        public MemberOrder[] newArray(int size) {return new MemberOrder[size];}
    };
}
