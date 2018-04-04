package me.suncloud.marrymemo.model.kepler;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * 平安普惠
 * Created by jinxin on 2016/11/4.
 */

public class Kepler implements Parcelable {

    long id;
    long userId;
    String certify;//身份证
    long cid;//城市id
    @SerializedName(value = "city_name")
    String cityName;
    String name;
    String phone;
    String channelId;//宿主渠道标识
    String longitude;//经度
    String latitude;//纬度
    String street;//街道信息
    String creditMessage;//预授权信息

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getCertify() {
        return certify;
    }

    public void setCertify(String certify) {
        this.certify = certify;
    }

    public long getCid() {
        return cid;
    }

    public void setCid(long cid) {
        this.cid = cid;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCreditMessage() {
        return creditMessage;
    }

    public void setCreditMessage(String creditMessage) {
        this.creditMessage = creditMessage;
    }

    public Kepler() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.userId);
        dest.writeString(this.certify);
        dest.writeLong(this.cid);
        dest.writeString(this.cityName);
        dest.writeString(this.name);
        dest.writeString(this.phone);
        dest.writeString(this.channelId);
        dest.writeString(this.longitude);
        dest.writeString(this.latitude);
        dest.writeString(this.street);
        dest.writeString(this.creditMessage);
    }

    protected Kepler(Parcel in) {
        this.id = in.readLong();
        this.userId = in.readLong();
        this.certify = in.readString();
        this.cid = in.readLong();
        this.cityName = in.readString();
        this.name = in.readString();
        this.phone = in.readString();
        this.channelId = in.readString();
        this.longitude = in.readString();
        this.latitude = in.readString();
        this.street = in.readString();
        this.creditMessage = in.readString();
    }

    public static final Creator<Kepler> CREATOR = new Creator<Kepler>() {
        @Override
        public Kepler createFromParcel(Parcel source) {return new Kepler(source);}

        @Override
        public Kepler[] newArray(int size) {return new Kepler[size];}
    };
}