package me.suncloud.marrymemo.model.orders;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.interfaces.HljRZData;

/**
 * Created by werther on 16/10/28.
 * 本地服务订单列表顶部的三种其他服务订单数量model
 */

public class ServiceOrderCountInfo implements Parcelable, HljRZData {
    @SerializedName(value = "refund_count")
    int refundCount; // 退款订单数量
    @SerializedName(value = "order_face_count")
    int offlineOrderCount; // 线下订单数量

    public int getRefundCount() {
        return refundCount;
    }


    public int getOfflineOrderCount() {
        return offlineOrderCount;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.refundCount);
        dest.writeInt(this.offlineOrderCount);
    }

    public ServiceOrderCountInfo() {}

    protected ServiceOrderCountInfo(Parcel in) {
        this.refundCount = in.readInt();
        this.offlineOrderCount = in.readInt();
    }

    public static final Parcelable.Creator<ServiceOrderCountInfo> CREATOR = new Parcelable
            .Creator<ServiceOrderCountInfo>() {
        @Override
        public ServiceOrderCountInfo createFromParcel(Parcel source) {
            return new ServiceOrderCountInfo(source);
        }

        @Override
        public ServiceOrderCountInfo[] newArray(int size) {return new ServiceOrderCountInfo[size];}
    };

    @Override
    public boolean isEmpty() {
        return refundCount + offlineOrderCount <= 0;
    }
}
