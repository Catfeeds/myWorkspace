package com.hunliji.marrybiz.model.customer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jinxin on 2017/8/16 0016.
 */

public class MerchantCustomerModifyBody implements Parcelable {

    int deal_will;//成交意愿
    long id;
    String remark;
    String user_name;
    String user_phone;
    String user_wechat;
    String weddingday;

    public void setDeal_will(int deal_will) {
        this.deal_will = deal_will;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public void setUser_phone(String user_phone) {
        this.user_phone = user_phone;
    }

    public void setUser_wechat(String user_wechat) {
        this.user_wechat = user_wechat;
    }

    public void setWeddingday(String weddingday) {
        this.weddingday = weddingday;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.deal_will);
        dest.writeLong(this.id);
        dest.writeString(this.remark);
        dest.writeString(this.user_name);
        dest.writeString(this.user_phone);
        dest.writeString(this.user_wechat);
        dest.writeString(this.weddingday);
    }

    public MerchantCustomerModifyBody() {}

    protected MerchantCustomerModifyBody(Parcel in) {
        this.deal_will = in.readInt();
        this.id = in.readLong();
        this.remark = in.readString();
        this.user_name = in.readString();
        this.user_phone = in.readString();
        this.user_wechat = in.readString();
        this.weddingday = in.readString();
    }

    public static final Parcelable.Creator<MerchantCustomerModifyBody> CREATOR = new Parcelable
            .Creator<MerchantCustomerModifyBody>() {
        @Override
        public MerchantCustomerModifyBody createFromParcel(Parcel source) {
            return new MerchantCustomerModifyBody(source);
        }

        @Override
        public MerchantCustomerModifyBody[] newArray(int size) {return new MerchantCustomerModifyBody[size];}
    };
}
