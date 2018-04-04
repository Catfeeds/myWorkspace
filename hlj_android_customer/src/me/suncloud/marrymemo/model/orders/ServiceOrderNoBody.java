package me.suncloud.marrymemo.model.orders;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by werther on 16/10/28.
 */

public class ServiceOrderNoBody implements Parcelable {
    @SerializedName(value = "order_no")
    String orderNo;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {dest.writeString(this.orderNo);}

    public ServiceOrderNoBody() {}

    protected ServiceOrderNoBody(Parcel in) {this.orderNo = in.readString();}

    public static final Creator<ServiceOrderNoBody> CREATOR = new Creator<ServiceOrderNoBody>() {
        @Override
        public ServiceOrderNoBody createFromParcel(Parcel source) {
            return new ServiceOrderNoBody(source);
        }

        @Override
        public ServiceOrderNoBody[] newArray(int size) {return new ServiceOrderNoBody[size];}
    };
}
