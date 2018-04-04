package com.hunliji.hljkefulibrary.moudles;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Suncloud on 2015/12/2.
 */
public class Support implements Parcelable {

    public static final int SUPPORT_KIND_DEFAULT = 0; // 默认的通用客服
    public static final int SUPPORT_KIND_CAR = 1; // 婚车客服
    public static final int SUPPORT_KIND_CONSULTANT = 2; // 顾问客服
    public static final int SUPPORT_KIND_TRAVEL = 3; // 旅拍客服
    public static final int SUPPORT_KIND_ADVISER = 5; // 结婚顾问
    public static final int SUPPORT_KIND_HOTEL = 6; // 酒店客服
    public static final int SUPPORT_KIND_EXPERIENCE_SHOP = 7;//体验店
    public static final int SUPPORT_KIND_DEFAULT_ROBOT = 8; // 默认机器人客服
    public static final int SUPPORT_KIND_CARD_MASTER = 9; // 请帖大师客服

    private long id;
    @SerializedName("user_id")
    private long userId;
    private int kind;
    @SerializedName("hx_im")
    private String hxIm;
    @SerializedName("hx_skill_group")
    private String hxSkillGroup;
    private String phone;
    private String nick;
    private String avatar;
    @SerializedName("ser_num")
    private int serNum;
    @SerializedName("ser_time")
    private String serTime;
    @SerializedName("advise_merchant_count")
    private int adviseMerchantCount;

    public long getId() {
        return id;
    }

    public int getKind() {
        return kind;
    }

    public long getUserId() {
        return userId;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getHxIm() {
        return hxIm;
    }

    public String getHxSkillGroup() {
        return hxSkillGroup;
    }

    public String getNick() {
        return nick;
    }

    public String getPhone() {
        return phone;
    }

    public int getAdviseMerchantCount() {
        return adviseMerchantCount;
    }

    public int getSerNum() {
        return serNum;
    }

    public String getSerTime() {
        return serTime;
    }

    public boolean isSupportRobot() {
        return kind == SUPPORT_KIND_DEFAULT_ROBOT;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.userId);
        dest.writeInt(this.kind);
        dest.writeString(this.hxIm);
        dest.writeString(this.hxSkillGroup);
        dest.writeString(this.phone);
        dest.writeString(this.nick);
        dest.writeString(this.avatar);
        dest.writeInt(this.serNum);
        dest.writeString(this.serTime);
        dest.writeInt(this.adviseMerchantCount);
    }

    public Support() {}

    protected Support(Parcel in) {
        this.id = in.readLong();
        this.userId = in.readLong();
        this.kind = in.readInt();
        this.hxIm = in.readString();
        this.hxSkillGroup = in.readString();
        this.phone = in.readString();
        this.nick = in.readString();
        this.avatar = in.readString();
        this.serNum = in.readInt();
        this.serTime = in.readString();
        this.adviseMerchantCount = in.readInt();
    }

    public static final Creator<Support> CREATOR = new Creator<Support>() {
        @Override
        public Support createFromParcel(Parcel source) {return new Support(source);}

        @Override
        public Support[] newArray(int size) {return new Support[size];}
    };
}
