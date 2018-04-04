package com.hunliji.hljcardcustomerlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

/**
 * Created by werther on 16/10/18.
 * 使用自动反序列化的新版红包model
 * 完全与老版本的model是一样的结构，新建只是为了用于新的http请求方法和解析方便
 */

public class RedPacket implements Parcelable {
    @SerializedName(value = "id")
    private long id;
    @SerializedName(value = "red_packet_type_category_id")
    private long categoryId;
    @SerializedName(value = "red_packet_id")
    private long redPacketId;
    @SerializedName(value = "start_at", alternate = "validity_starttime")
    private DateTime startAt;
    @SerializedName(value = "end_at", alternate = "validity_endtime")
    private DateTime endAt;
    @SerializedName(value = "use_time")
    private DateTime useTime;
    @SerializedName(value = "provide_starttime")
    private DateTime provideStartTime;
    @SerializedName(value = "ticket_no")
    private String ticketNo;
    @SerializedName(value = "state")
    private String state; // # init 领红包 #used 已使用 #expired 已过期
    @SerializedName(value = "red_packet_name")
    private String redPacketName;
    @SerializedName(value = "red_packet_type_category_name")
    private String categoryType;
    @SerializedName(value = "money_sill_text")
    private String moneySillText; // 红包满减金额描述字段
    @SerializedName(value = "redemption_code")
    private String redemptionCode; //红包码
    @SerializedName(value = "price", alternate = "money_value")
    private double amount;
    @SerializedName(value = "money_sill")
    private double moneySill; // 红包满减金额字段
    @SerializedName(value = "status")
    private int status;
    @SerializedName(value = "red_packet_type")
    private int redPacketType;//跳转类型
    @SerializedName(value = "get_status")
    private int getStatus; //1待领取 2可领取 3抢光 4已领取
    @SerializedName(value = "validity_days")
    int validityDays;//几天后过期

    public transient static final int GET_STATUS_WAITING_RECEIVE = 1; //待领取
    public transient static final int GET_STATUS_CAN_RECEIVE = 2; //可领取
    public transient static final int GET_STATUS_LOOT_ALL = 3; //抢光
    public transient static final int GET_STATUS_RECEIVED = 4; //已领取

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.categoryId);
        dest.writeLong(this.redPacketId);
        dest.writeSerializable(this.startAt);
        dest.writeSerializable(this.endAt);
        dest.writeSerializable(this.useTime);
        dest.writeSerializable(this.provideStartTime);
        dest.writeString(this.ticketNo);
        dest.writeString(this.state);
        dest.writeString(this.redPacketName);
        dest.writeString(this.categoryType);
        dest.writeString(this.moneySillText);
        dest.writeString(this.redemptionCode);
        dest.writeDouble(this.amount);
        dest.writeDouble(this.moneySill);
        dest.writeInt(this.status);
        dest.writeInt(this.redPacketType);
        dest.writeInt(this.getStatus);
        dest.writeInt(this.validityDays);
    }

    public RedPacket() {}

    protected RedPacket(Parcel in) {
        this.id = in.readLong();
        this.categoryId = in.readLong();
        this.redPacketId = in.readLong();
        this.startAt = (DateTime) in.readSerializable();
        this.endAt = (DateTime) in.readSerializable();
        this.useTime = (DateTime) in.readSerializable();
        this.provideStartTime = (DateTime) in.readSerializable();
        this.ticketNo = in.readString();
        this.state = in.readString();
        this.redPacketName = in.readString();
        this.categoryType = in.readString();
        this.moneySillText = in.readString();
        this.redemptionCode = in.readString();
        this.amount = in.readDouble();
        this.moneySill = in.readDouble();
        this.status = in.readInt();
        this.redPacketType = in.readInt();
        this.getStatus = in.readInt();
        this.validityDays = in.readInt();
    }

    public static final Creator<RedPacket> CREATOR = new Creator<RedPacket>() {
        @Override
        public RedPacket createFromParcel(Parcel source) {return new RedPacket(source);}

        @Override
        public RedPacket[] newArray(int size) {return new RedPacket[size];}
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public long getRedPacketId() {
        return redPacketId;
    }

    public void setRedPacketId(long redPacketId) {
        this.redPacketId = redPacketId;
    }

    public DateTime getStartAt() {
        return startAt;
    }

    public void setStartAt(DateTime startAt) {
        this.startAt = startAt;
    }

    public DateTime getEndAt() {
        return endAt;
    }

    public void setEndAt(DateTime endAt) {
        this.endAt = endAt;
    }

    public DateTime getUseTime() {
        return useTime;
    }

    public void setUseTime(DateTime useTime) {
        this.useTime = useTime;
    }

    public DateTime getProvideStartTime() {
        return provideStartTime;
    }

    public void setProvideStartTime(DateTime provideStartTime) {
        this.provideStartTime = provideStartTime;
    }

    public String getTicketNo() {
        return ticketNo;
    }

    public void setTicketNo(String ticketNo) {
        this.ticketNo = ticketNo;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getRedPacketName() {
        return redPacketName;
    }

    public void setRedPacketName(String redPacketName) {
        this.redPacketName = redPacketName;
    }

    public String getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
    }

    public String getMoneySillText() {
        return moneySillText;
    }

    public void setMoneySillText(String moneySillText) {
        this.moneySillText = moneySillText;
    }

    public String getRedemptionCode() {
        return redemptionCode;
    }

    public void setRedemptionCode(String redemptionCode) {
        this.redemptionCode = redemptionCode;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getMoneySill() {
        return moneySill;
    }

    public void setMoneySill(double moneySill) {
        this.moneySill = moneySill;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getRedPacketType() {
        return redPacketType;
    }

    public void setRedPacketType(int redPacketType) {
        this.redPacketType = redPacketType;
    }

    public int getGetStatus() {
        return getStatus;
    }

    public void setGetStatus(int getStatus) {
        this.getStatus = getStatus;
    }

    public int getValidityDays() {
        return validityDays;
    }
}
