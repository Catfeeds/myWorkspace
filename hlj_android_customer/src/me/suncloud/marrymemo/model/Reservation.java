package me.suncloud.marrymemo.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Merchant;

import org.joda.time.DateTime;

/**
 * 预约model
 * Created by jinxin on 2016/2/26.
 */
public class Reservation implements Parcelable {
    @SerializedName(value = "id")
    private long id;
    @SerializedName(value = "user_id")
    private long userId;
    @SerializedName(value = "merchant_id")
    private long merchantId;
    @SerializedName(value = "created_at")
    private DateTime createdAt;
    @SerializedName(value = "updated_at")
    private DateTime updatedAt;
    @SerializedName(value = "fullname")
    private String fullName;
    @SerializedName(value = "phone_num")
    private String phoneNum;
    @SerializedName(value = "go_time")
    private String goTime;
    @SerializedName(value = "gift")
    private String gift;
    @SerializedName(value = "merchant")
    private Merchant merchant;

    public long getId() {
        return id;
    }

    public long getUserId() {
        return userId;
    }

    public long getMerchantId() {
        return merchantId;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public DateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public String getGoTime() {
        return goTime;
    }

    public String getGift() {
        return gift;
    }

    public Merchant getMerchant() {
        return merchant;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.userId);
        dest.writeLong(this.merchantId);
        dest.writeSerializable(this.createdAt);
        dest.writeSerializable(this.updatedAt);
        dest.writeString(this.fullName);
        dest.writeString(this.phoneNum);
        dest.writeString(this.goTime);
        dest.writeString(this.gift);
        dest.writeParcelable(this.merchant, flags);
    }

    public Reservation() {}

    protected Reservation(Parcel in) {
        this.id = in.readLong();
        this.userId = in.readLong();
        this.merchantId = in.readLong();
        this.createdAt = (DateTime) in.readSerializable();
        this.updatedAt = (DateTime) in.readSerializable();
        this.fullName = in.readString();
        this.phoneNum = in.readString();
        this.goTime = in.readString();
        this.gift = in.readString();
        this.merchant = in.readParcelable(Merchant.class.getClassLoader());
    }

    public static final Creator<Reservation> CREATOR = new Creator<Reservation>() {
        @Override
        public Reservation createFromParcel(Parcel source) {return new Reservation(source);}

        @Override
        public Reservation[] newArray(int size) {return new Reservation[size];}
    };
}
