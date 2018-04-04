package com.hunliji.marrybiz.model.merchant;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * 商家特权记录(到店礼 优惠卷 商家承诺 等等) item
 * Created by jinxin on 2017/2/8 0008.
 */

public class PrivilegeRecord implements Parcelable {
    long id;
    @SerializedName(value = "main_title")
    String mainTitle;
    @SerializedName(value = "sub_title")
    String subTitle;
    String logo;
    String path;
    @SerializedName(value = "need_bond")
    int needBond;
    int type;
    int levelAchieved;
    int level;
    boolean status;
    String reason;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMainTitle() {
        return mainTitle;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public void setMainTitle(String mainTitle) {
        this.mainTitle = mainTitle;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public int getNeedBond() {
        return needBond;
    }

    public void setNeedBond(int needBond) {
        this.needBond = needBond;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getLevelAchieved() {
        return levelAchieved;
    }

    public void setLevelAchieved(int levelAchieved) {
        this.levelAchieved = levelAchieved;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }



    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public PrivilegeRecord() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.mainTitle);
        dest.writeString(this.subTitle);
        dest.writeString(this.logo);
        dest.writeString(this.path);
        dest.writeInt(this.needBond);
        dest.writeInt(this.type);
        dest.writeInt(this.levelAchieved);
        dest.writeInt(this.level);
        dest.writeByte(this.status ? (byte) 1 : (byte) 0);
        dest.writeString(this.reason);
    }

    protected PrivilegeRecord(Parcel in) {
        this.id = in.readLong();
        this.mainTitle = in.readString();
        this.subTitle = in.readString();
        this.logo = in.readString();
        this.path = in.readString();
        this.needBond = in.readInt();
        this.type = in.readInt();
        this.levelAchieved = in.readInt();
        this.level = in.readInt();
        this.status = in.readByte() != 0;
        this.reason = in.readString();
    }

    public static final Creator<PrivilegeRecord> CREATOR = new Creator<PrivilegeRecord>() {
        @Override
        public PrivilegeRecord createFromParcel(Parcel source) {return new PrivilegeRecord(source);}

        @Override
        public PrivilegeRecord[] newArray(int size) {return new PrivilegeRecord[size];}
    };
}
