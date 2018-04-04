package me.suncloud.marrymemo.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;

import org.joda.time.DateTime;
import org.json.JSONObject;

import java.util.Date;

import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by werther on 2/1/16.
 * 兼容parcelable
 */
public class OrderPayHistory implements Identifiable, Parcelable {
    private static final long serialVersionUID = 694703922042351802L;
    @SerializedName(value = "created_at")
    private DateTime createdAt;
    private double money;
    private String event;

    public OrderPayHistory(JSONObject jsonObject) {
        if (jsonObject != null) {
            Date date = JSONUtil.getDateFromFormatLong(jsonObject, "created_at", true);
            createdAt = new DateTime(date);
            money = jsonObject.optDouble("money");
            event = JSONUtil.getString(jsonObject, "event");
        }
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public double getMoney() {
        return money;
    }

    public String getEvent() {
        return event;
    }

    @Override
    public Long getId() {
        return serialVersionUID;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        HljTimeUtils.writeDateTimeToParcel(dest,this.createdAt);
        dest.writeDouble(this.money);
        dest.writeString(this.event);
    }

    protected OrderPayHistory(Parcel in) {
        this.createdAt = HljTimeUtils.readDateTimeToParcel(in);
        this.money = in.readDouble();
        this.event = in.readString();
    }

    public static final Parcelable.Creator<OrderPayHistory> CREATOR = new Parcelable
            .Creator<OrderPayHistory>() {
        @Override
        public OrderPayHistory createFromParcel(Parcel source) {return new OrderPayHistory(source);}

        @Override
        public OrderPayHistory[] newArray(int size) {return new OrderPayHistory[size];}
    };
}
