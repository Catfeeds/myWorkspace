package com.hunliji.hljcommonlibrary.models.orders;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by chen_bin on 2017/9/12 0012.
 */
public class ServiceOrder implements Parcelable {
    @SerializedName(value = "order_sub")
    private ServiceOrderSub orderSub;

    public ServiceOrderSub getOrderSub() {
        if (orderSub == null) {
            orderSub = new ServiceOrderSub();
        }
        return orderSub;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {dest.writeParcelable(this.orderSub, flags);}

    public ServiceOrder() {}

    protected ServiceOrder(Parcel in) {
        this.orderSub = in.readParcelable(ServiceOrderSub.class.getClassLoader());
    }

    public static final Creator<ServiceOrder> CREATOR = new Creator<ServiceOrder>() {
        @Override
        public ServiceOrder createFromParcel(Parcel source) {return new ServiceOrder(source);}

        @Override
        public ServiceOrder[] newArray(int size) {return new ServiceOrder[size];}
    };
}
