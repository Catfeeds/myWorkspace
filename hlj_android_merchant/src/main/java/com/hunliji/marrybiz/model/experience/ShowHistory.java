package com.hunliji.marrybiz.model.experience;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

/**
 * Created by jinxin on 2017/12/19 0019.
 */

public class ShowHistory implements Parcelable {

    long id;
    @SerializedName(value = "created_at")
    DateTime createAt;
    String message;//备注
    int status;//订单状态 0 未查看 1.已查看 2已过期 3已跟进 4 跟进失败 5已成单 6已退单,-1未派单
    @SerializedName("is_come")
    boolean isCome; // 单独的用于判断是否是"已到店"历史记录的标识为，此状态是由status和call_history联合判断

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public DateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(DateTime createAt) {
        this.createAt = createAt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isCome() {
        return isCome;
    }

    public ShowHistory() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeSerializable(this.createAt);
        dest.writeString(this.message);
        dest.writeInt(this.status);
        dest.writeByte(this.isCome ? (byte) 1 : (byte) 0);
    }

    protected ShowHistory(Parcel in) {
        this.id = in.readLong();
        this.createAt = (DateTime) in.readSerializable();
        this.message = in.readString();
        this.status = in.readInt();
        this.isCome = in.readByte() != 0;
    }

    public static final Creator<ShowHistory> CREATOR = new Creator<ShowHistory>() {
        @Override
        public ShowHistory createFromParcel(Parcel source) {return new ShowHistory(source);}

        @Override
        public ShowHistory[] newArray(int size) {return new ShowHistory[size];}
    };
}
