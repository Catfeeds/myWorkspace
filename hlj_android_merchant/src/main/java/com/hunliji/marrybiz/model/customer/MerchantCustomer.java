package com.hunliji.marrybiz.model.customer;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljcommonlibrary.models.customer.CustomerUser;
import com.hunliji.hljcommonlibrary.models.realm.WSCity;

/**
 * 客资model
 * Created by jinxin on 2017/8/14 0014.
 */

public class MerchantCustomer implements Parcelable {

    long id;
    @SerializedName(value = "deal_will")
    int dealWill;//成交意愿 0低 1 一般 2高
    String remark;
    @SerializedName(value = "user_name")
    String userName;
    @SerializedName(value = "user_phone")
    String userPhone;
    @SerializedName(value = "user_wechat")
    String userWechat;
    String weddingday;
    @SerializedName(value = "channel_id")
    long channelId;
    CustomerUser user;
    WSCity city;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getDealWill() {
        return dealWill;
    }

    public void setDealWill(int dealWill) {
        this.dealWill = dealWill;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public String getUserWechat() {
        return userWechat;
    }

    public void setUserWechat(String userWechat) {
        this.userWechat = userWechat;
    }

    public User getUser() {
        return user;
    }

    public String getWeddingday() {
        return weddingday;
    }

    public void setWeddingday(String weddingday) {
        this.weddingday = weddingday;
    }

    public long getChannelId() {
        return channelId;
    }

    public void setChannelId(long channelId) {
        this.channelId = channelId;
    }

    public MerchantCustomer() {}

    public WSCity getCity() {
        if (city == null || city.getId() == 0 || TextUtils.isEmpty(city.getName())) {
            return null;
        }
        return city;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeInt(this.dealWill);
        dest.writeString(this.remark);
        dest.writeString(this.userName);
        dest.writeString(this.userPhone);
        dest.writeString(this.userWechat);
        dest.writeString(this.weddingday);
        dest.writeLong(this.channelId);
        dest.writeParcelable(this.user, flags);
    }

    protected MerchantCustomer(Parcel in) {
        this.id = in.readLong();
        this.dealWill = in.readInt();
        this.remark = in.readString();
        this.userName = in.readString();
        this.userPhone = in.readString();
        this.userWechat = in.readString();
        this.weddingday = in.readString();
        this.channelId = in.readLong();
        this.user = in.readParcelable(CustomerUser.class.getClassLoader());
    }

    public static final Creator<MerchantCustomer> CREATOR = new Creator<MerchantCustomer>() {
        @Override
        public MerchantCustomer createFromParcel(Parcel source) {return new MerchantCustomer
                (source);}

        @Override
        public MerchantCustomer[] newArray(int size) {return new MerchantCustomer[size];}
    };
}
