package me.suncloud.marrymemo.model.orders;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by werther on 17/1/3.
 * 婚品订单列表中退款单信息
 */

public class ProductOrderRefundInfo implements Parcelable {
    @SerializedName("pay_money")
    double payMoney;
    int type;

    public double getPayMoney() {
        return payMoney;
    }

    public int getType() {
        return type;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.payMoney);
        dest.writeInt(this.type);
    }

    public ProductOrderRefundInfo() {}

    protected ProductOrderRefundInfo(Parcel in) {
        this.payMoney = in.readDouble();
        this.type = in.readInt();
    }

    public static final Parcelable.Creator<ProductOrderRefundInfo> CREATOR = new Parcelable
            .Creator<ProductOrderRefundInfo>() {
        @Override
        public ProductOrderRefundInfo createFromParcel(Parcel source) {
            return new ProductOrderRefundInfo(source);
        }

        @Override
        public ProductOrderRefundInfo[] newArray(int size) {return new
                ProductOrderRefundInfo[size];}
    };
}
