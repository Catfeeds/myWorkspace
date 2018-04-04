package me.suncloud.marrymemo.model.orders;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

import me.suncloud.marrymemo.model.Identifiable;

/**
 * Created by werther on 17/1/4.
 * 婚品订单物流信息记录
 */

public class ShippingStatus implements Parcelable, Identifiable {
    long id;
    @SerializedName("context")
    String status;
    DateTime time;

    public Long getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public DateTime getTime() {
        return time;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.status);
        dest.writeLong(this.time == null ? -1 : this.time.getMillis());
    }

    public ShippingStatus() {}

    protected ShippingStatus(Parcel in) {
        this.id = in.readLong();
        this.status = in.readString();
        long millis = in.readLong();
        if (millis > 0) {
            this.time = new DateTime(millis);
        }
    }

    public static final Parcelable.Creator<ShippingStatus> CREATOR = new Parcelable
            .Creator<ShippingStatus>() {
        @Override
        public ShippingStatus createFromParcel(Parcel source) {return new ShippingStatus(source);}

        @Override
        public ShippingStatus[] newArray(int size) {return new ShippingStatus[size];}
    };
}
