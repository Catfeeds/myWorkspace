package com.hunliji.hljcommonlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;

import org.joda.time.DateTime;

import java.util.Date;

/**
 * Created by werther on 16/10/27.
 * 套餐的活动信息，新版解析的model
 */

public class WorkRule implements Parcelable {
    long id;
    String name;
    @SerializedName(value = "start_time")
    DateTime startTime;
    @SerializedName(value = "end_time")
    DateTime endTime;
    @SerializedName(value = "confirm_time")
    DateTime confirmTime;
    int status;
    int type;
    String detail;
    @SerializedName(value = "order_now")
    int orderNow;
    @SerializedName(value = "show_txt",alternate = "showtxt")
    String showText;
    @SerializedName(value = "showimg")
    String showImg;//原活动图标，现活动小图标
    @SerializedName(value = "created_at")
    DateTime createdAt;
    @SerializedName(value = "updated_at")
    DateTime updatedAt;
    String description;
    @SerializedName(value = "is_set_time")
    boolean isSetTime;
    @SerializedName(value = "is_time_viewable")
    boolean isTimeViewable;
    String remark;
    @SerializedName(value = "activity_money")
    double activityMoney; // 只在订单中有声明，活动优惠了多少钱
    @SerializedName(value = "bigimg")
    String bigImg;//活动大图

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public DateTime getStartTime() {
        return HljTimeUtils.timeServerTimeZone(startTime);
    }

    public DateTime getEndTime() {
        return HljTimeUtils.timeServerTimeZone(endTime);
    }

    public int getStatus() {
        return status;
    }

    public int getType() {
        return type;
    }

    public String getDetail() {
        return detail;
    }

    public String getShowText() {
        return showText;
    }

    public String getShowImg() {
        return !isTimeViewable || endTime == null || endTime.isAfterNow() ? showImg : null;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public DateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getDescription() {
        return description;
    }

    public boolean isSetTime() {
        return isSetTime;
    }

    public String getRemark() {
        return remark;
    }

    public String getBigImg() {
        return !isTimeViewable || endTime == null || endTime.isAfterNow() ? bigImg:null;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
        HljTimeUtils.writeDateTimeToParcel(dest,this.startTime);
        HljTimeUtils.writeDateTimeToParcel(dest,this.endTime);
        HljTimeUtils.writeDateTimeToParcel(dest,this.confirmTime);
        dest.writeInt(this.status);
        dest.writeInt(this.type);
        dest.writeString(this.detail);
        dest.writeInt(this.orderNow);
        dest.writeString(this.showText);
        dest.writeString(this.showImg);
        HljTimeUtils.writeDateTimeToParcel(dest,this.createdAt);
        HljTimeUtils.writeDateTimeToParcel(dest,this.updatedAt);
        dest.writeString(this.description);
        dest.writeByte(this.isSetTime ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isTimeViewable ? (byte) 1 : (byte) 0);
        dest.writeString(this.remark);
        dest.writeDouble(this.activityMoney);
        dest.writeString(this.bigImg);
    }

    public WorkRule() {}

    protected WorkRule(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.startTime = HljTimeUtils.readDateTimeToParcel(in);
        this.endTime = HljTimeUtils.readDateTimeToParcel(in);
        this.confirmTime = HljTimeUtils.readDateTimeToParcel(in);
        this.status = in.readInt();
        this.type = in.readInt();
        this.detail = in.readString();
        this.orderNow = in.readInt();
        this.showText = in.readString();
        this.showImg = in.readString();
        this.createdAt = HljTimeUtils.readDateTimeToParcel(in);
        this.updatedAt = HljTimeUtils.readDateTimeToParcel(in);
        this.description = in.readString();
        this.isSetTime = in.readByte() != 0;
        this.isTimeViewable = in.readByte() != 0;
        this.remark = in.readString();
        this.activityMoney = in.readDouble();
        this.bigImg = in.readString();
    }

    public static final Parcelable.Creator<WorkRule> CREATOR = new Parcelable.Creator<WorkRule>() {
        @Override
        public WorkRule createFromParcel(Parcel source) {return new WorkRule(source);}

        @Override
        public WorkRule[] newArray(int size) {return new WorkRule[size];}
    };
}
