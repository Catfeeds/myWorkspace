package com.hunliji.hljcarlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mo_yu on 2018/1/4.婚车商家联系信息（私信，电话）
 */

public class CarMerchantContactInfo implements Parcelable {

    @SerializedName("bond_merchant_expire_time")
    DateTime bondMerchantExpireTime;
    @SerializedName("contact_phones")
    ArrayList<String> contactPhones;
    long id;
    @SerializedName("pro_price")
    double proPrice;
    @SerializedName("user_id")
    long userId;


    public DateTime getBondMerchantExpireTime() {
        return bondMerchantExpireTime;
    }

    public ArrayList<String> getContactPhones() {
        return contactPhones;
    }

    public long getId() {
        return id;
    }

    public double getProPrice() {
        return proPrice;
    }

    public long getUserId() {
        return userId;
    }

    public CarMerchantContactInfo() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(
            Parcel dest,
            int flags) {
        HljTimeUtils.writeDateTimeToParcel(dest,this.bondMerchantExpireTime);
        dest.writeStringList(this.contactPhones);
        dest.writeLong(this.id);
        dest.writeDouble(this.proPrice);
        dest.writeLong(this.userId);
    }

    protected CarMerchantContactInfo(Parcel in) {
        this.bondMerchantExpireTime = HljTimeUtils.readDateTimeToParcel(in);
        this.contactPhones = in.createStringArrayList();
        this.id = in.readLong();
        this.proPrice = in.readDouble();
        this.userId = in.readLong();
    }

    public static final Creator<CarMerchantContactInfo> CREATOR = new
            Creator<CarMerchantContactInfo>() {
        @Override
        public CarMerchantContactInfo createFromParcel(Parcel source) {
            return new CarMerchantContactInfo(source);
        }

        @Override
        public CarMerchantContactInfo[] newArray(int size) {return new CarMerchantContactInfo[size];}
    };
}
