package com.hunliji.marrybiz.model.tools;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import com.hunliji.hljhttplibrary.utils.GsonUtil;
import com.hunliji.marrybiz.model.NewOrder;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONObject;

/**
 * 日程管理的model
 * Created by chen_bin on 2016/6/27 0027.
 */
public class Schedule implements Parcelable {
    @SerializedName(value = "id")
    private long id;
    @SerializedName(value = "time")
    private String time;
    @SerializedName(value = "date")
    private String date;
    @SerializedName(value = "entity_id")
    private String entityId;
    @SerializedName(value = "time_str")
    private String timeStr;
    @SerializedName(value = "activity_type")
    private String activityType;
    @SerializedName(value = "message")
    private String message;
    @SerializedName(value = "user_name")
    private String userName;
    @SerializedName(value = "phone")
    private String phone;
    @SerializedName(value = "address")
    private String address;
    @SerializedName(value = "price")
    private double price;
    @SerializedName(value = "pay_price")
    private double payPrice;
    @SerializedName(value = "type")
    private int type;
    @SerializedName("calender_info")
    private CalenderInfo calenderInfo;
    @SerializedName(value = "entity")
    private JsonElement entity;

    private transient NewOrder order;
    private transient DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
    private transient DateTimeFormatter formatter2 = DateTimeFormat.forPattern("yyyy-MM-dd " +
            "HH:mm:ss");
    public transient final static int TYPE_SCHEDULE = 0;
    public transient final static int TYPE_ORDER = 1;
    public transient final static int TYPE_RESERVATION = 2;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public DateTime getTime() {
        try {
            return DateTime.parse(time, formatter2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public DateTime getDate() {
        try {
            return DateTime.parse(calenderInfo == null ? date : calenderInfo.getDate(), formatter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setDate(String date) {
        if (calenderInfo != null) {
            calenderInfo.setDate(date);
        }
        this.date = date;
    }

    public String getEntityId() {
        return entityId;
    }

    public String getTimeStr() {
        return timeStr;
    }

    public void setTimeStr(String timeStr) {
        this.timeStr = timeStr;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getPayPrice() {
        return payPrice;
    }

    public void setPayPrice(double payPrice) {
        this.payPrice = payPrice;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public NewOrder getOrder() {
        if (order == null) {
            try {
                order = new NewOrder(new JSONObject(entity.toString()));
            } catch (Exception e) {
                e.printStackTrace();
                order = new NewOrder(null);
            }
        }
        return order;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.time);
        dest.writeString(this.date);
        dest.writeString(this.entityId);
        dest.writeString(this.timeStr);
        dest.writeString(this.activityType);
        dest.writeString(this.message);
        dest.writeString(this.userName);
        dest.writeString(this.phone);
        dest.writeString(this.address);
        dest.writeDouble(this.price);
        dest.writeDouble(this.payPrice);
        dest.writeInt(this.type);
        dest.writeParcelable(this.calenderInfo, flags);
        dest.writeString(GsonUtil.getGsonInstance()
                .toJson(this.entity));
    }

    public Schedule() {}

    protected Schedule(Parcel in) {
        this.id = in.readLong();
        this.time = in.readString();
        this.date = in.readString();
        this.entityId = in.readString();
        this.timeStr = in.readString();
        this.activityType = in.readString();
        this.message = in.readString();
        this.userName = in.readString();
        this.phone = in.readString();
        this.address = in.readString();
        this.price = in.readDouble();
        this.payPrice = in.readDouble();
        this.type = in.readInt();
        this.calenderInfo = in.readParcelable(CalenderInfo.class.getClassLoader());
        this.entity = GsonUtil.getGsonInstance()
                .fromJson(in.readString(), JsonElement.class);
    }

    public static final Parcelable.Creator<Schedule> CREATOR = new Parcelable.Creator<Schedule>() {
        @Override
        public Schedule createFromParcel(Parcel source) {return new Schedule(source);}

        @Override
        public Schedule[] newArray(int size) {return new Schedule[size];}
    };
}