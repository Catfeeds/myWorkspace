package me.suncloud.marrymemo.model.orders;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by werther on 17/1/7.
 * 收货地址model
 */

public class ShippingAddress implements Parcelable {
    long id;
    @SerializedName("buyer_name")
    String buyerName;
    String mobile;
    String street;
    @SerializedName("region_id")
    long regionId;
    @SerializedName("is_default")
    boolean isDefault;
    String country;
    String province;
    String city;
    String district;
    String zip;

    public long getId() {
        return id;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public String getMobile() {
        return mobile;
    }

    public String getStreet() {
        return street;
    }

    public long getRegionId() {
        return regionId;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public String getCountry() {
        return country;
    }

    public String getProvince() {
        return province;
    }

    public String getCity() {
        return city;
    }

    public String getDistrict() {
        return district;
    }

    public String getZip() {
        return zip;
    }

    @Override
    public String toString() {
        return (JSONUtil.isEmpty(country) ? "" : country) + province + city + (JSONUtil.isEmpty(
                district) ? "" : district) + street;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.buyerName);
        dest.writeString(this.mobile);
        dest.writeString(this.street);
        dest.writeLong(this.regionId);
        dest.writeByte(this.isDefault ? (byte) 1 : (byte) 0);
        dest.writeString(this.country);
        dest.writeString(this.province);
        dest.writeString(this.city);
        dest.writeString(this.district);
        dest.writeString(this.zip);
    }

    public ShippingAddress() {}

    protected ShippingAddress(Parcel in) {
        this.id = in.readLong();
        this.buyerName = in.readString();
        this.mobile = in.readString();
        this.street = in.readString();
        this.regionId = in.readLong();
        this.isDefault = in.readByte() != 0;
        this.country = in.readString();
        this.province = in.readString();
        this.city = in.readString();
        this.district = in.readString();
        this.zip = in.readString();
    }

    public static final Parcelable.Creator<ShippingAddress> CREATOR = new Parcelable
            .Creator<ShippingAddress>() {
        @Override
        public ShippingAddress createFromParcel(Parcel source) {return new ShippingAddress(source);}

        @Override
        public ShippingAddress[] newArray(int size) {return new ShippingAddress[size];}
    };
}
