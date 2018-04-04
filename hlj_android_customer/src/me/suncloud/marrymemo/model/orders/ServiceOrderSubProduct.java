package me.suncloud.marrymemo.model.orders;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by werther on 16/10/25.
 * 服务订单子订单中的套餐信息，对应的work的model
 */

public class ServiceOrderSubProduct implements Parcelable {
    long id;
    String title;
    @SerializedName(value = "cover_path")
    String coverPath;
    @SerializedName(value = "actual_price")
    double actualPrice;
    @SerializedName(value = "earnest_money")
    double earnestMoney;
    @SerializedName(value = "show_price")
    double showPrice;
    @SerializedName(value = "is_installment")
    boolean isInstallment;
    int version;
    @SerializedName("allow_earnest")
    boolean isAllowEarnest;

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public double getActualPrice() {
        return actualPrice;
    }

    public double getEarnestMoney() {
        return earnestMoney;
    }

    public double getShowPrice() {
        return showPrice;
    }

    public boolean isInstallment() {
        return isInstallment;
    }

    public int getVersion() {
        return version;
    }

    public ServiceOrderSubProduct() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.title);
        dest.writeString(this.coverPath);
        dest.writeDouble(this.actualPrice);
        dest.writeDouble(this.earnestMoney);
        dest.writeDouble(this.showPrice);
        dest.writeByte(this.isInstallment ? (byte) 1 : (byte) 0);
        dest.writeInt(this.version);
        dest.writeByte(this.isAllowEarnest ? (byte) 1 : (byte) 0);
    }

    protected ServiceOrderSubProduct(Parcel in) {
        this.id = in.readLong();
        this.title = in.readString();
        this.coverPath = in.readString();
        this.actualPrice = in.readDouble();
        this.earnestMoney = in.readDouble();
        this.showPrice = in.readDouble();
        this.isInstallment = in.readByte() != 0;
        this.version = in.readInt();
        this.isAllowEarnest = in.readByte() != 0;
    }

    public static final Creator<ServiceOrderSubProduct> CREATOR = new
            Creator<ServiceOrderSubProduct>() {
        @Override
        public ServiceOrderSubProduct createFromParcel(Parcel source) {
            return new ServiceOrderSubProduct(source);
        }

        @Override
        public ServiceOrderSubProduct[] newArray(int size) {return new
                ServiceOrderSubProduct[size];}
    };

    public boolean isAllowEarnest() {
        if (version == 1) {
            return earnestMoney > 0;
        } else {
            return isAllowEarnest;
        }
    }
}
