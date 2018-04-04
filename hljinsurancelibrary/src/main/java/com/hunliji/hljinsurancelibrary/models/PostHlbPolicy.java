package com.hunliji.hljinsurancelibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mo_yu on 2017/5/26.填写保单post
 */

public class PostHlbPolicy implements Parcelable {
    @SerializedName("id")
    private String id;
    @SerializedName("certi_no")
    private String certiNo;
    @SerializedName("full_name")
    private String fullName;
    @SerializedName("phone")
    private String phone;
    @SerializedName("spouse_name")
    private String spouseName;
    @SerializedName("trans_begin_date")
    private String transBeginDate;
    @SerializedName("wedding_address")
    private String weddingAddress;
    @SerializedName("wedding_hotel")
    private String weddingHotel;//酒店名称

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCertiNo() {
        return certiNo;
    }

    public void setCertiNo(String certiNo) {
        this.certiNo = certiNo;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSpouseName() {
        return spouseName;
    }

    public void setSpouseName(String spouseName) {
        this.spouseName = spouseName;
    }

    public String getTransBeginDate() {
        return transBeginDate;
    }

    public void setTransBeginDate(String transBeginDate) {
        this.transBeginDate = transBeginDate;
    }

    public String getWeddingAddress() {
        return weddingAddress;
    }

    public void setWeddingAddress(String weddingAddress) {
        this.weddingAddress = weddingAddress;
    }

    public void setWeddingHotel(String weddingHotel) {
        this.weddingHotel = weddingHotel;
    }

    public String getWeddingHotel() {
        return weddingHotel;
    }

    public PostHlbPolicy() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.certiNo);
        dest.writeString(this.fullName);
        dest.writeString(this.phone);
        dest.writeString(this.spouseName);
        dest.writeString(this.transBeginDate);
        dest.writeString(this.weddingAddress);
        dest.writeString(this.weddingHotel);
    }

    protected PostHlbPolicy(Parcel in) {
        this.id = in.readString();
        this.certiNo = in.readString();
        this.fullName = in.readString();
        this.phone = in.readString();
        this.spouseName = in.readString();
        this.transBeginDate = in.readString();
        this.weddingAddress = in.readString();
        this.weddingHotel = in.readString();
    }

    public static final Creator<PostHlbPolicy> CREATOR = new Creator<PostHlbPolicy>() {
        @Override
        public PostHlbPolicy createFromParcel(Parcel source) {return new PostHlbPolicy(source);}

        @Override
        public PostHlbPolicy[] newArray(int size) {return new PostHlbPolicy[size];}
    };
}
