package com.hunliji.hljcommonlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;

import org.joda.time.DateTime;

/**
 * 活动信息
 * Created by jinxin on 2017/12/27 0027.
 */

public class Rule implements Parcelable {

    long id;
    String name;
    @SerializedName(value = "start_time")
    DateTime startTime;
    @SerializedName(value = "end_time")
    DateTime endTime;
    int type;
    @SerializedName(value = "showtxt")
    String showTxt;
    @SerializedName(value = "showimg")
    String showImg;
    @SerializedName(value = "is_time_viewable")
    boolean isTimeAble;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(DateTime startTime) {
        this.startTime = startTime;
    }

    public DateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(DateTime endTime) {
        this.endTime = endTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getShowTxt() {
        return showTxt;
    }

    public void setShowTxt(String showTxt) {
        this.showTxt = showTxt;
    }

    public String getShowImg() {
        return showImg;
    }

    public void setShowImg(String showImg) {
        this.showImg = showImg;
    }

    public boolean isTimeAble() {
        return isTimeAble;
    }

    public void setTimeAble(boolean timeAble) {
        isTimeAble = timeAble;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
        HljTimeUtils.writeDateTimeToParcel(dest,this.startTime);
        HljTimeUtils.writeDateTimeToParcel(dest,this.endTime);
        dest.writeInt(this.type);
        dest.writeString(this.showTxt);
        dest.writeString(this.showImg);
        dest.writeByte(this.isTimeAble ? (byte) 1 : (byte) 0);
    }

    public Rule() {}

    protected Rule(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.startTime = HljTimeUtils.readDateTimeToParcel(in);
        this.endTime = HljTimeUtils.readDateTimeToParcel(in);
        this.type = in.readInt();
        this.showTxt = in.readString();
        this.showImg = in.readString();
        this.isTimeAble = in.readByte() != 0;
    }

    public static final Parcelable.Creator<Rule> CREATOR = new Parcelable.Creator<Rule>() {
        @Override
        public Rule createFromParcel(Parcel source) {return new Rule(source);}

        @Override
        public Rule[] newArray(int size) {return new Rule[size];}
    };
}
