package me.suncloud.marrymemo.model.orders.hotelperoid;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jinxin on 2018/3/9 0009.
 */

public class HotelPeriodBody implements Parcelable {

    @SerializedName("actual_money")
    private String actualMoney;
    @SerializedName("merchant_id")
    private long merchantId;
    @SerializedName("user_name")
    private String userName;
    @SerializedName("user_phone")
    private String userPhone;
    @SerializedName("wedding_day")
    private String weddingDay;
    @SerializedName("images")
    private HotelPeriodImageBody images;

    public String getActualMoney() {
        return actualMoney;
    }

    public void setActualMoney(String actualMoney) {
        this.actualMoney = actualMoney;
    }

    public long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(long merchantId) {
        this.merchantId = merchantId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getWeddingDay() {
        return weddingDay;
    }

    public void setWeddingDay(String weddingDay) {
        this.weddingDay = weddingDay;
    }

    public HotelPeriodImageBody getImages() {
        return images;
    }

    public void setImages(HotelPeriodImageBody images) {
        this.images = images;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.actualMoney);
        dest.writeLong(this.merchantId);
        dest.writeString(this.userName);
        dest.writeString(this.userPhone);
        dest.writeString(this.weddingDay);
        dest.writeParcelable(this.images, flags);
    }

    public HotelPeriodBody() {}

    protected HotelPeriodBody(Parcel in) {
        this.actualMoney = in.readString();
        this.merchantId = in.readLong();
        this.userName = in.readString();
        this.userPhone = in.readString();
        this.weddingDay = in.readString();
        this.images = in.readParcelable(HotelPeriodImageBody.class.getClassLoader());
    }

    public static final Parcelable.Creator<HotelPeriodBody> CREATOR = new Parcelable
            .Creator<HotelPeriodBody>() {
        @Override
        public HotelPeriodBody createFromParcel(Parcel source) {return new HotelPeriodBody(source);}

        @Override
        public HotelPeriodBody[] newArray(int size) {return new HotelPeriodBody[size];}
    };
}
