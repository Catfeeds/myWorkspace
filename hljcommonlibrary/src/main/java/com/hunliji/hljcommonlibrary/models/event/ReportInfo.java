package com.hunliji.hljcommonlibrary.models.event;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

/**
 * 举报接口
 * Created by chen_bin on 2016/9/21 0021.
 */
public class ReportInfo implements Parcelable {
    @SerializedName(value = "id")
    long id;
    @SerializedName(value = "message")
    String message;
    @SerializedName(value = "type")
    int type;

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.message);
        dest.writeInt(this.type);
    }

    public ReportInfo() {}

    protected ReportInfo(Parcel in) {
        this.id = in.readLong();
        this.message = in.readString();
        this.type = in.readInt();
    }

    public static final Creator<ReportInfo> CREATOR = new Creator<ReportInfo>() {
        @Override
        public ReportInfo createFromParcel(Parcel source) {return new ReportInfo(source);}

        @Override
        public ReportInfo[] newArray(int size) {return new ReportInfo[size];}
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMessage() {
        return TextUtils.isEmpty(message) ? "" : message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
