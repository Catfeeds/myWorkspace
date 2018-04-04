package me.suncloud.marrymemo.model.kepler;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * 平安普惠
 * Created by jinxin on 2016/11/4.
 */

public class KeplerBody implements Parcelable {

    long id;
    long userId;
    String certify;//身份证
    long cid;//城市id
    String city_name;
    String name;
    String phone;
    String longitude;//经度
    String latitude;//纬度
    String street;//街道信息
    String bank;//平安普惠

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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCid() {
        return cid;
    }

    public void setCid(long cid) {
        this.cid = cid;
    }

    public String getCity_name() {
        return city_name;
    }

    public void setCityName(String city_name) {
        this.city_name = city_name;
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

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public KeplerBody() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.userId);
        dest.writeString(this.certify);
        dest.writeLong(this.cid);
        dest.writeString(this.city_name);
        dest.writeString(this.name);
        dest.writeString(this.phone);
        dest.writeString(this.longitude);
        dest.writeString(this.latitude);
        dest.writeString(this.street);
        dest.writeString(this.bank);
    }

    protected KeplerBody(Parcel in) {
        this.id = in.readLong();
        this.userId = in.readLong();
        this.certify = in.readString();
        this.cid = in.readLong();
        this.city_name = in.readString();
        this.name = in.readString();
        this.phone = in.readString();
        this.longitude = in.readString();
        this.latitude = in.readString();
        this.street = in.readString();
        this.bank = in.readString();
    }

    public static final Creator<KeplerBody> CREATOR = new Creator<KeplerBody>() {
        @Override
        public KeplerBody createFromParcel(Parcel source) {
            return new KeplerBody(source);
        }

        @Override
        public KeplerBody[] newArray(int size) {return new KeplerBody[size];}
    };
}