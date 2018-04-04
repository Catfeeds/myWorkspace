package com.hunliji.marrybiz.model.event;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;

import org.joda.time.DateTime;

/**
 * 活动管理-充值记录
 * Created by chen_bin on 2016/10/10 0010.
 */
public class RechargeRecord implements Parcelable {
    @SerializedName(value = "id")
    private long id;
    @SerializedName(value = "entity_id")
    private long entityId;
    @SerializedName(value = "merchant_id")
    private long merchantId;
    @SerializedName(value = "created_at")
    private DateTime createdAt;
    @SerializedName(value = "message")
    private String message;
    @SerializedName(value = "price")
    private double price;
    @SerializedName(value = "value")
    private int value;// 点数
    @SerializedName(value = "type")
    private int type;//1商家活动点充值 2运营活动点充值 3活动点消费 4活动券充值 5活动券消费
    private int num;//活动券数
    private int subtype;//活动券充值(0兑换，1退还, 2充值） 活动券消耗（0活动券包场消费）

    public static final int TYPE_BUSINESS_POINT_RECHARGE = 1;
    public static final int TYPE_OPERATE_POINT_RECHARGE = 2;
    public static final int TYPE_POINT_CONSUMPTION = 3;
    public static final int TYPE_COUPON_RECHARGE = 4;
    public static final int TYPE_COUPON_CONSUMPTION = 5;

    public class SubType{
        public static final int TYPE_EXCHANGE = 0;
        public static final int TYPE_RETURN = 1;
        public static final int TYPE_RECHARGE = 2;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.entityId);
        dest.writeLong(this.merchantId);
        dest.writeString(this.message);
        HljTimeUtils.writeDateTimeToParcel(dest, this.createdAt);
        dest.writeDouble(this.price);
        dest.writeDouble(this.value);
        dest.writeInt(this.type);
        dest.writeInt(this.num);
        dest.writeInt(this.subtype);
    }

    public RechargeRecord() {}

    protected RechargeRecord(Parcel in) {
        this.id = in.readLong();
        this.entityId = in.readLong();
        this.merchantId = in.readLong();
        this.message = in.readString();
        this.createdAt = HljTimeUtils.readDateTimeToParcel(in);
        this.price = in.readDouble();
        this.value = in.readInt();
        this.type = in.readInt();
        this.num = in.readInt();
        this.subtype = in.readInt();
    }

    public static final Creator<RechargeRecord> CREATOR = new Creator<RechargeRecord>() {
        @Override
        public RechargeRecord createFromParcel(Parcel source) {return new RechargeRecord(source);}

        @Override
        public RechargeRecord[] newArray(int size) {return new RechargeRecord[size];}
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getEntityId() {
        return entityId;
    }

    public void setEntityId(long entityId) {
        this.entityId = entityId;
    }

    public long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(long merchantId) {
        this.merchantId = merchantId;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getSubtype() {
        return subtype;
    }

    public void setSubtype(int subtype) {
        this.subtype = subtype;
    }
}