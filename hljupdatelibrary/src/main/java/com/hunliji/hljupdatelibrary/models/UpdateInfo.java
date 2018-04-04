package com.hunliji.hljupdatelibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Suncloud on 2016/9/20.
 */

public class UpdateInfo implements Parcelable {

    @SerializedName("version_code")
    private int versionCode;
    @SerializedName("suggest_code")
    private int suggestCode;
    @SerializedName("support_code")
    private int supportCode;
    @SerializedName("version_name")
    private String versionName;
    private String info;
    private String link;
    private String md5;
    private String size;

    public int getVersionCode() {
        return versionCode;
    }

    public int getSuggestCode() {
        return suggestCode;
    }

    public int getSupportCode() {
        return supportCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public String getInfo() {
        return info;
    }

    public String getLink() {
        return link;
    }

    public String getMd5() {
        return md5;
    }

    public String getSize() {
        return size;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.versionCode);
        dest.writeInt(this.suggestCode);
        dest.writeInt(this.supportCode);
        dest.writeString(this.versionName);
        dest.writeString(this.info);
        dest.writeString(this.link);
        dest.writeString(this.md5);
        dest.writeString(this.size);
    }

    public UpdateInfo() {}

    protected UpdateInfo(Parcel in) {
        this.versionCode = in.readInt();
        this.suggestCode = in.readInt();
        this.supportCode = in.readInt();
        this.versionName = in.readString();
        this.info = in.readString();
        this.link = in.readString();
        this.md5 = in.readString();
        this.size = in.readString();
    }

    public static final Creator<UpdateInfo> CREATOR = new Creator<UpdateInfo>() {
        @Override
        public UpdateInfo createFromParcel(Parcel source) {return new UpdateInfo(source);}

        @Override
        public UpdateInfo[] newArray(int size) {return new UpdateInfo[size];}
    };
}
