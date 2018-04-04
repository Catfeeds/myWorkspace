package com.hunliji.hljcommonlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class MinProgramShareInfo implements Parcelable {

    private String title;
    private String desc;
    private String url;
    private String icon;
    @SerializedName("min_program_hdimagepath")
    private String hdImagePath;
    @SerializedName("min_program_user_name")
    private String programUserName;
    @SerializedName("min_program_path")
    private String programPath;

    public String getTitle() {
        return title;
    }

    public MinProgramShareInfo(
            String title,
            String desc,
            String url,
            String icon,
            String programUserName,
            String programPath) {
        this.title = title;
        this.desc = desc;
        this.url = url;
        this.icon = icon;
        this.programUserName = programUserName;
        this.programPath = programPath;
    }

    public String getDesc() {
        return desc;
    }

    public String getUrl() {
        return url;
    }

    public String getIcon() {
        return icon;
    }

    public String getHdImagePath() {
        return hdImagePath;
    }

    public void setHdImagePath(String hdImagePath) {
        this.hdImagePath = hdImagePath;
    }

    public String getProgramUserName() {
        return programUserName;
    }

    public String getProgramPath() {
        return programPath;
    }

    protected MinProgramShareInfo(Parcel in) {
        title = in.readString();
        desc = in.readString();
        url = in.readString();
        icon = in.readString();
        programUserName = in.readString();
        programPath = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(desc);
        dest.writeString(url);
        dest.writeString(icon);
        dest.writeString(programUserName);
        dest.writeString(programPath);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MinProgramShareInfo> CREATOR = new Creator<MinProgramShareInfo>() {
        @Override
        public MinProgramShareInfo createFromParcel(Parcel in) {
            return new MinProgramShareInfo(in);
        }

        @Override
        public MinProgramShareInfo[] newArray(int size) {
            return new MinProgramShareInfo[size];
        }
    };
}
