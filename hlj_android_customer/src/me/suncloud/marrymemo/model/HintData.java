package me.suncloud.marrymemo.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by werther on 16/11/11.
 * 红点提示的数据
 */

public class HintData implements Parcelable {
    @SerializedName(value = "order_notification")
    int orderNotification;

    public int getOrderNotification() {
        return orderNotification;
    }

    /**
     * 判断是否需要显示红点提示，目前只与未处理订单数目相关
     * @return
     */
    public boolean hasNewHint() {
        return orderNotification > 0;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {dest.writeInt(this.orderNotification);}

    public HintData() {}

    protected HintData(Parcel in) {this.orderNotification = in.readInt();}

    public static final Parcelable.Creator<HintData> CREATOR = new Parcelable.Creator<HintData>() {
        @Override
        public HintData createFromParcel(Parcel source) {return new HintData(source);}

        @Override
        public HintData[] newArray(int size) {return new HintData[size];}
    };
}
