package com.hunliji.hljcommonlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

public class ShareInfo implements Parcelable {

    private String title;
    private String desc;
    private String desc2;
    private String url;
    private String icon;
    private String sms;

    public ShareInfo(String title, String desc, String desc2, String url, String icon) {
        this(title, desc, desc2, url, icon, null);
    }

    public ShareInfo(String title, String desc, String desc2, String url, String icon, String sms) {
        this.title = title;
        this.desc = desc;
        this.desc2 = desc2;
        this.url = url;
        this.icon = icon;
        this.sms = sms;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDesc2() {
        return desc2;
    }

    public void setDesc2(String desc2) {
        this.desc2 = desc2;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getSms() {
        return sms;
    }

    public void setSms(String sms) {
        this.sms = sms;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.desc);
        dest.writeString(this.desc2);
        dest.writeString(this.url);
        dest.writeString(this.icon);
        dest.writeString(this.sms);
    }

    public ShareInfo() {

    }

    protected ShareInfo(Parcel in) {
        this.title = in.readString();
        this.desc = in.readString();
        this.desc2 = in.readString();
        this.url = in.readString();
        this.icon = in.readString();
        this.sms = in.readString();
    }

    public static final Creator<ShareInfo> CREATOR = new Creator<ShareInfo>() {
        @Override
        public ShareInfo createFromParcel(Parcel source) {return new ShareInfo(source);}

        @Override
        public ShareInfo[] newArray(int size) {return new ShareInfo[size];}
    };
}
