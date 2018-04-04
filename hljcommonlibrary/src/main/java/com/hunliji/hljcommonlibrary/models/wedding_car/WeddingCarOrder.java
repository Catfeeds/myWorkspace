package com.hunliji.hljcommonlibrary.models.wedding_car;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jinxin on 2017/12/27 0027.
 */

public class WeddingCarOrder implements Parcelable {

    @SerializedName(value = "buyer_phone")
    String buyerPhone;
    @SerializedName(value = "created_at")
    int createdAt ;//几秒前创建

    public String getBuyerPhone() {
        return buyerPhone;
    }

    public void setBuyerPhone(String buyerPhone) {
        this.buyerPhone = buyerPhone;
    }

    public int getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(int createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.buyerPhone);
        dest.writeInt(this.createdAt);
    }

    public WeddingCarOrder() {}

    protected WeddingCarOrder(Parcel in) {
        this.buyerPhone = in.readString();
        this.createdAt = in.readInt();
    }

    public static final Parcelable.Creator<WeddingCarOrder> CREATOR = new Parcelable
            .Creator<WeddingCarOrder>() {
        @Override
        public WeddingCarOrder createFromParcel(Parcel source) {return new WeddingCarOrder(source);}

        @Override
        public WeddingCarOrder[] newArray(int size) {return new WeddingCarOrder[size];}
    };
}
