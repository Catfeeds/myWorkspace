package me.suncloud.marrymemo.model.orders;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by werther on 16/10/28.
 */

public class ServiceOrderIdBody implements Parcelable {
    @SerializedName(value = "order_id")
    long orderId;

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {dest.writeLong(this.orderId);}

    public ServiceOrderIdBody() {}

    protected ServiceOrderIdBody(Parcel in) {this.orderId = in.readLong();}

    public static final Parcelable.Creator<ServiceOrderIdBody> CREATOR = new Parcelable
            .Creator<ServiceOrderIdBody>() {
        @Override
        public ServiceOrderIdBody createFromParcel(Parcel source) {
            return new ServiceOrderIdBody(source);
        }

        @Override
        public ServiceOrderIdBody[] newArray(int size) {return new ServiceOrderIdBody[size];}
    };
}
