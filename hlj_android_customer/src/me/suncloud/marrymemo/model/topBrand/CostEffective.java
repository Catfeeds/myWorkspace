package me.suncloud.marrymemo.model.topBrand;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Work;

/**
 * 性价比model
 * Created by jinxin on 2016/11/16 0016.
 */

public class CostEffective implements Parcelable {
    long id;
    @SerializedName(value = "is_schedule")
    boolean isSchedule;
    String remark;
    int score;
    Work entity;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isSchedule() {
        return isSchedule;
    }

    public void setSchedule(boolean schedule) {
        isSchedule = schedule;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Work getEntity() {
        return entity;
    }

    public void setEntity(Work entity) {
        this.entity = entity;
    }

    public CostEffective() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeByte(this.isSchedule ? (byte) 1 : (byte) 0);
        dest.writeString(this.remark);
        dest.writeInt(this.score);
        dest.writeParcelable(this.entity, flags);
    }

    protected CostEffective(Parcel in) {
        this.id = in.readLong();
        this.isSchedule = in.readByte() != 0;
        this.remark = in.readString();
        this.score = in.readInt();
        this.entity = in.readParcelable(Work.class.getClassLoader());
    }

    public static final Creator<CostEffective> CREATOR = new Creator<CostEffective>() {
        @Override
        public CostEffective createFromParcel(Parcel source) {return new CostEffective(source);}

        @Override
        public CostEffective[] newArray(int size) {return new CostEffective[size];}
    };
}
