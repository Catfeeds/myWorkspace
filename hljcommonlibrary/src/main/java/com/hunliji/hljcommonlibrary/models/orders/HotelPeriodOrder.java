package com.hunliji.hljcommonlibrary.models.orders;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;

import org.joda.time.DateTime;

/**
 * 婚宴order
 * Created by chen_bin on 2018/2/28 0028.
 */
public class HotelPeriodOrder implements Parcelable {
    @SerializedName(value = "id")
    private long id;
    @SerializedName(value = "created_at")
    private DateTime createdAt;
    @SerializedName(value = "wedding_day")
    private DateTime weddingDay;
    @SerializedName(value = "order_no")
    private String orderNo;
    @SerializedName(value = "user_name")
    private String userName;
    @SerializedName(value = "user_phone")
    private String userPhone;
    @SerializedName(value = "title")
    private String title;
    @SerializedName(value = "actual_money")
    private double actualMoney;
    @SerializedName(value = "period")
    private int period; //一共多少期还款计划
    @SerializedName(value = "status")
    private int status; //0提交1已支付2已关闭3超时已关闭
    @SerializedName(value = "merchant")
    private Merchant merchant;

    public transient final static int STATUS_WAITING_FOR_THE_PAYMENT = 0; //等待付款
    public transient final static int STATUS_ORDER_PAID = 1; //已付款
    public transient final static int STATUS_ORDER_CLOSED = 2; //订单已关闭
    public transient final static int STATUS_ORDER_AUTO_CLOSED = 3; //超时关闭

    public long getId() {
        return id;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public DateTime getWeddingDay() {
        return weddingDay;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public String getTitle() {
        return title;
    }

    public double getActualMoney() {
        return actualMoney;
    }

    public int getPeriod() {
        return period;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Merchant getMerchant() {
        if (merchant == null) {
            merchant = new Merchant();
        }
        return merchant;
    }

    public String getStatusStr() {
        switch (status) {
            case STATUS_WAITING_FOR_THE_PAYMENT:
                return "等待付款";
            case STATUS_ORDER_CLOSED:
            case STATUS_ORDER_AUTO_CLOSED:
                return "订单关闭";
            case STATUS_ORDER_PAID:
                return "已付款";
            default:
                return null;
        }
    }

    public String getNegativeActionStr() {
        switch (status) {
            case STATUS_WAITING_FOR_THE_PAYMENT:
                return "取消订单";
            case STATUS_ORDER_CLOSED:
            case STATUS_ORDER_AUTO_CLOSED:
                return "删除订单";
            default:
                return null;
        }
    }

    public String getPositiveActionStr() {
        switch (status) {
            case HotelPeriodOrder.STATUS_WAITING_FOR_THE_PAYMENT:
                return "去付款";
            case HotelPeriodOrder.STATUS_ORDER_CLOSED:
            case HotelPeriodOrder.STATUS_ORDER_AUTO_CLOSED:
                return "重新下单";
            default:
                return null;
        }
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        HljTimeUtils.writeDateTimeToParcel(dest, this.createdAt);
        HljTimeUtils.writeDateTimeToParcel(dest, this.weddingDay);
        dest.writeString(this.orderNo);
        dest.writeString(this.userName);
        dest.writeString(this.userPhone);
        dest.writeString(this.title);
        dest.writeDouble(this.actualMoney);
        dest.writeInt(this.period);
        dest.writeInt(this.status);
        dest.writeParcelable(this.merchant, flags);
    }

    public HotelPeriodOrder() {}

    protected HotelPeriodOrder(Parcel in) {
        this.id = in.readLong();
        this.createdAt = HljTimeUtils.readDateTimeToParcel(in);
        this.weddingDay = HljTimeUtils.readDateTimeToParcel(in);
        this.orderNo = in.readString();
        this.userName = in.readString();
        this.userPhone = in.readString();
        this.title = in.readString();
        this.actualMoney = in.readDouble();
        this.period = in.readInt();
        this.status = in.readInt();
        this.merchant = in.readParcelable(Merchant.class.getClassLoader());
    }

    public static final Creator<HotelPeriodOrder> CREATOR = new Creator<HotelPeriodOrder>() {
        @Override
        public HotelPeriodOrder createFromParcel(Parcel source) {
            return new HotelPeriodOrder(source);
        }

        @Override
        public HotelPeriodOrder[] newArray(int size) {return new HotelPeriodOrder[size];}
    };
}
