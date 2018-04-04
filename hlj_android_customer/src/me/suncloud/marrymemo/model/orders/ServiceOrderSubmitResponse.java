package me.suncloud.marrymemo.model.orders;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by werther on 16/10/27.
 * 服务订单提交后的返回response结构model
 */

public class ServiceOrderSubmitResponse implements Parcelable {
    long id;
    String action;
    ServiceOrder order;

    public long getId() {
        return id;
    }

    public String getAction() {
        return action;
    }

    public ServiceOrder getOrder() {
        return order;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.action);
        dest.writeParcelable(this.order, flags);
    }

    public ServiceOrderSubmitResponse() {}

    protected ServiceOrderSubmitResponse(Parcel in) {
        this.id = in.readLong();
        this.action = in.readString();
        this.order = in.readParcelable(ServiceOrder.class.getClassLoader());
    }

    public static final Parcelable.Creator<ServiceOrderSubmitResponse> CREATOR = new Parcelable
            .Creator<ServiceOrderSubmitResponse>() {
        @Override
        public ServiceOrderSubmitResponse createFromParcel(Parcel source) {
            return new ServiceOrderSubmitResponse(source);
        }

        @Override
        public ServiceOrderSubmitResponse[] newArray(int size) {return new
                ServiceOrderSubmitResponse[size];}
    };
}
