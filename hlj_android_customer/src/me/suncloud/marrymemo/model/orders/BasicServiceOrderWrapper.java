package me.suncloud.marrymemo.model.orders;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by werther on 16/10/11.
 * 服务订单分为普通套餐(本地服务)和定制套餐两种,这个wrapper用于包装两种完全不同的订单model到同一个列表中
 */

public class BasicServiceOrderWrapper implements Parcelable {
    int type; // 1: 定制套餐, 2:普通套餐
    Parcelable order;

    public void setOrder(Parcelable order) {
        this.order = order;
    }

    public Parcelable getOrder() {
        return order;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public BasicServiceOrderWrapper() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type);
        dest.writeParcelable(this.order, flags);
    }

    protected BasicServiceOrderWrapper(Parcel in) {
        this.type = in.readInt();
        this.order = in.readParcelable(Object.class.getClassLoader());
    }

    public static final Creator<BasicServiceOrderWrapper> CREATOR = new
            Creator<BasicServiceOrderWrapper>() {
        @Override
        public BasicServiceOrderWrapper createFromParcel(Parcel source) {
            return new BasicServiceOrderWrapper(source);
        }

        @Override
        public BasicServiceOrderWrapper[] newArray(int size) {
            return new BasicServiceOrderWrapper[size];
        }
    };
}
