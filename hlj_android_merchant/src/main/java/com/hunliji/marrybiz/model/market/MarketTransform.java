package com.hunliji.marrybiz.model.market;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.text.DecimalFormat;

/**
 * Created by hua_rong on 2017/8/16 0016
 * 营销转化
 */

public class MarketTransform implements Parcelable {

    @SerializedName(value = "view_num")
    private long viewNum;//访客人数
    @SerializedName(value = "connect_num")
    private long connectNum;//咨询人数
    @SerializedName(value = "order_num")
    private long orderNum;//下单人数
    @SerializedName(value = "order_price")
    private double orderPrice;//单价

    public long getViewNum() {
        return viewNum;
    }

    public void setViewNum(long viewNum) {
        this.viewNum = viewNum;
    }

    public long getConnectNum() {
        return connectNum;
    }

    public void setConnectNum(long connectNum) {
        this.connectNum = connectNum;
    }

    public long getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(long orderNum) {
        this.orderNum = orderNum;
    }

    public double getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(double orderPrice) {
        this.orderPrice = orderPrice;
    }


    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.viewNum);
        dest.writeLong(this.connectNum);
        dest.writeLong(this.orderNum);
        dest.writeDouble(this.orderPrice);
    }

    public MarketTransform() {}

    protected MarketTransform(Parcel in) {
        this.viewNum = in.readLong();
        this.connectNum = in.readLong();
        this.orderNum = in.readLong();
        this.orderPrice = in.readDouble();
    }

    public static final Parcelable.Creator<MarketTransform> CREATOR = new Parcelable
            .Creator<MarketTransform>() {
        @Override
        public MarketTransform createFromParcel(Parcel source) {return new MarketTransform(source);}

        @Override
        public MarketTransform[] newArray(int size) {return new MarketTransform[size];}
    };

    public String getConsultRate() {
        Double rate = geConsultDoubleRate();
        if (viewNum == 0 || rate == null) {
            return "--";
        }
        return getFloat(rate) + "%";
    }

    public Double geConsultDoubleRate() {
        if (viewNum > 0) {
            return (connectNum * 100.0) / (viewNum * 1.0);
        }
        return null;
    }

    public Double getOrderDoubleRate() {
        if (viewNum > 0) {
            return (orderNum * 100.0) / (viewNum * 1.0);
        }
        return null;
    }

    public String getOrderRate() {
        Double rate = getOrderDoubleRate();
        if (viewNum == 0 || rate == null) {
            return "--";
        }
        return getFloat(rate) + "%";
    }


    public Double getConsultOrderDoubleRate() {
        if (connectNum > 0) {
            return (orderNum * 100.0) / (connectNum * 1.0);
        }
        return null;
    }

    public String getConsultOrderRate() {
        Double rate = getConsultOrderDoubleRate();
        if (connectNum == 0 || rate == null) {
            return "--";
        }
        return getFloat(rate) + "%";
    }

    public static String getFloat(double d) {
        return String.valueOf(new DecimalFormat("0.00").format(d));
    }

}
