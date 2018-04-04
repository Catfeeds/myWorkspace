package com.hunliji.hljcommonlibrary.models.chat_ext_object;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Photo;

import org.joda.time.DateTime;

import java.util.ArrayList;

/**
 * Created by luohanlin on 2017/11/13.
 * 商家私信统计信息
 */

public class WSMerchantStats implements Parcelable {

    private long id;
    private String address;
    @SerializedName("bond_merchant_expire_time")
    private DateTime bondMerchantExpireTime;
    @SerializedName("good_rate")
    private double goodRate;//	好评率
    private String grade;
    private double latitude;
    private double longitude;
    @SerializedName("logo_path")
    private String logoPath;
    private String name;
    @SerializedName("pro_price")
    private double proPrice;
    @SerializedName("real_photos")
    private ArrayList<Photo> realPhotos;
    @SerializedName("reply_rate")
    private double replyRate;
    @SerializedName("reply_time")
    private double replyTime;
    @SerializedName("show_grade")
    private boolean showGrade;

    public long getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public DateTime getBondMerchantExpireTime() {
        return bondMerchantExpireTime;
    }

    public double getGoodRate() {
        return goodRate;
    }

    public int getGrade() {
        return Integer.parseInt(grade);
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getLogoPath() {
        return logoPath;
    }

    public String getName() {
        return name;
    }

    public double getProPrice() {
        return proPrice;
    }

    public ArrayList<Photo> getRealPhotos() {
        return realPhotos;
    }

    public double getReplyRate() {
        return replyRate;
    }

    public double getReplyTime() {
        return replyTime;
    }

    public boolean isShowGrade() {
        return showGrade;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.address);
        dest.writeSerializable(this.bondMerchantExpireTime);
        dest.writeDouble(this.goodRate);
        dest.writeString(this.grade);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeString(this.logoPath);
        dest.writeString(this.name);
        dest.writeDouble(this.proPrice);
        dest.writeTypedList(this.realPhotos);
        dest.writeDouble(this.replyRate);
        dest.writeDouble(this.replyTime);
        dest.writeByte(this.showGrade ? (byte) 1 : (byte) 0);
    }

    public WSMerchantStats() {}

    protected WSMerchantStats(Parcel in) {
        this.id = in.readLong();
        this.address = in.readString();
        this.bondMerchantExpireTime = (DateTime) in.readSerializable();
        this.goodRate = in.readDouble();
        this.grade = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.logoPath = in.readString();
        this.name = in.readString();
        this.proPrice = in.readDouble();
        this.realPhotos = in.createTypedArrayList(Photo.CREATOR);
        this.replyRate = in.readDouble();
        this.replyTime = in.readDouble();
        this.showGrade = in.readByte() != 0;
    }

    public static final Parcelable.Creator<WSMerchantStats> CREATOR = new Parcelable
            .Creator<WSMerchantStats>() {
        @Override
        public WSMerchantStats createFromParcel(Parcel source) {return new WSMerchantStats(source);}

        @Override
        public WSMerchantStats[] newArray(int size) {return new WSMerchantStats[size];}
    };
}
