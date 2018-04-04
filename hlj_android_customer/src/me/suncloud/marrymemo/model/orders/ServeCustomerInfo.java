package me.suncloud.marrymemo.model.orders;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.text.SimpleDateFormat;

/**
 * Created by werther on 16/10/10.
 * 本地服务订单服务客户信息
 * 客户信息包括联系姓名,电话,时间(婚期)和服务地址(婚车才用到)等
 */

public class ServeCustomerInfo implements Parcelable {
    String customerName;
    String customerPhone;
    long serveTime;
    String serveAddr;

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public DateTime getServeTime() {
        if (serveTime > 0) {
            return new DateTime(serveTime);
        } else {
            return null;
        }
    }

    public void setServeTime(long serveTime) {
        this.serveTime = serveTime;
    }

    public String getServeAddr() {
        return serveAddr;
    }

    public void setServeAddr(String serveAddr) {
        this.serveAddr = serveAddr;
    }

    public ServeCustomerInfo() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.customerName);
        dest.writeString(this.customerPhone);
        dest.writeLong(this.serveTime);
        dest.writeString(this.serveAddr);
    }

    protected ServeCustomerInfo(Parcel in) {
        this.customerName = in.readString();
        this.customerPhone = in.readString();
        this.serveTime = in.readLong();
        this.serveAddr = in.readString();
    }

    public static final Creator<ServeCustomerInfo> CREATOR = new Creator<ServeCustomerInfo>() {
        @Override
        public ServeCustomerInfo createFromParcel(Parcel source) {
            return new ServeCustomerInfo(source);
        }

        @Override
        public ServeCustomerInfo[] newArray(int size) {return new ServeCustomerInfo[size];}
    };
}
