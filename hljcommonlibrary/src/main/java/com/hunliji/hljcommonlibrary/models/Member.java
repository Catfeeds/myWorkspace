package com.hunliji.hljcommonlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

/**
 * Created by mo_yu on 2017/4/17.会员
 */

public class Member implements Parcelable {

    @SerializedName(value = "id")
    private long id;//会员id
    @SerializedName(value = "address_id")
    private long addressId;//标识是否已经填写收货地址
    @SerializedName(value = "intro_url")
    private String introUrl;

    public String getIntroUrl() {
        return introUrl;
    }

    public void setIntroUrl(String introUrl) {
        this.introUrl = introUrl;
    }

    public long getId() {
        return id;
    }

    public long getAddressId() {
        return addressId;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setAddressId(long addressId) {
        this.addressId = addressId;
    }

    public Member() {

    }

    public Member(JSONObject jsonObject) {
        if (jsonObject != null) {
            this.addressId = jsonObject.optLong("address_id", 0);
            this.id = jsonObject.optLong("id", 0);
        }
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.addressId);
        dest.writeString(this.introUrl);
    }

    protected Member(Parcel in) {
        this.id = in.readLong();
        this.addressId = in.readLong();
        this.introUrl = in.readString();
    }

    public static final Creator<Member> CREATOR = new Creator<Member>() {
        @Override
        public Member createFromParcel(Parcel source) {return new Member(source);}

        @Override
        public Member[] newArray(int size) {return new Member[size];}
    };
}
