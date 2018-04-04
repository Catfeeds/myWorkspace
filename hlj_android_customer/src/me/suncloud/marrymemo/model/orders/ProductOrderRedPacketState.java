package me.suncloud.marrymemo.model.orders;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by luohanlin on 17/01/2017.
 * 检查红包是否有效的结果
 */

public class ProductOrderRedPacketState implements Parcelable {
    @SerializedName("red_packet_limit")
    double redPacketLimit;
    @SerializedName("red_packet_money")
    double redPacketMoney;
    @SerializedName("red_packet_useless")
    int redPacketUseless; // 1原红包无效，弹窗提醒用户 0红包正常

    public double getRedPacketLimit() {
        return redPacketLimit;
    }

    public double getRedPacketMoney() {
        return redPacketMoney;
    }

    public int getRedPacketUseless() {
        return redPacketUseless;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.redPacketLimit);
        dest.writeDouble(this.redPacketMoney);
        dest.writeInt(this.redPacketUseless);
    }

    public ProductOrderRedPacketState() {}

    protected ProductOrderRedPacketState(Parcel in) {
        this.redPacketLimit = in.readDouble();
        this.redPacketMoney = in.readDouble();
        this.redPacketUseless = in.readInt();
    }

    public static final Parcelable.Creator<ProductOrderRedPacketState> CREATOR = new Parcelable
            .Creator<ProductOrderRedPacketState>() {
        @Override
        public ProductOrderRedPacketState createFromParcel(Parcel source) {
            return new ProductOrderRedPacketState(source);
        }

        @Override
        public ProductOrderRedPacketState[] newArray(int size) {
            return new ProductOrderRedPacketState[size];
        }
    };
}
