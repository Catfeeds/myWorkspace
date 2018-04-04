package com.hunliji.hljcommonlibrary.models.userprofile;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Member;

import org.joda.time.DateTime;

/**
 * Created by werther on 16/8/29.
 * 用户的所有账户信息
 */
public class UserProfile implements Parcelable {
    long id;
    String name;
    String nick;
    String avatar;
    @SerializedName(value = "bind_type")
    int bindType;
    DateTime birthDay;
    @SerializedName(value = "collect_count")
    int collectCount;
    String description;
    String detail;
    @SerializedName(value = "fans_count", alternate = "_fans_count")
    int fansCount;
    @SerializedName(value = "is_following")
    boolean following;
    int gender;
    @SerializedName(value = "hashed_password")
    String hashedPassword;
    @SerializedName(value = "have_card")
    boolean haveCard;
    String hometown;
    @SerializedName(value = "hometown_id")
    long hometownId;
    @SerializedName(value = "is_faker")
    boolean isFaker;
    @SerializedName(value = "is_need")
    boolean isNeed;
    @SerializedName(value = "is_pending")
    int isPending;
    int kine;
    @SerializedName(value = "like_count")
    int likeCount;
    boolean open;
    @SerializedName(value = "praise_count", alternate = "praised_count")
    int praiseCount;
    String specialty;
    @SerializedName(value = "story_count")
    int storyCount;
    String tags;
    String token;
    @SerializedName(value = "user_token")
    String userToken;
    @SerializedName(value = "watch_count")
    int watchCount;
    @SerializedName(value = "wedding_day", alternate = "weddingday")
    DateTime weddingDay;
    @SerializedName(value = "white_bar_tag")
    String whiteBarTag;
    @SerializedName(value = "follow_count")
    int followCount;
    Member member;

    public UserProfile() {}

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNick() {
        return nick;
    }

    public String getAvatar() {
        return avatar;
    }

    public int getBindType() {
        return bindType;
    }

    public DateTime getBirthDay() {
        return birthDay;
    }

    public int getCollectCount() {
        return collectCount;
    }

    public String getDescription() {
        return description;
    }

    public String getDetail() {
        return detail;
    }

    public int getFansCount() {
        return fansCount;
    }

    public boolean isFollowing() {
        return following;
    }

    public int getGender() {
        return gender;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public boolean isHaveCard() {
        return haveCard;
    }

    public String getHometown() {
        return hometown;
    }

    public long getHometownId() {
        return hometownId;
    }

    public boolean isFaker() {
        return isFaker;
    }

    public boolean isNeed() {
        return isNeed;
    }

    public int isPending() {
        return isPending;
    }

    public int getKine() {
        return kine;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public boolean isOpen() {
        return open;
    }

    public int getPraiseCount() {
        return praiseCount;
    }

    public String getSpecialty() {
        return specialty;
    }

    public int getStoryCount() {
        return storyCount;
    }

    public String getTags() {
        return tags;
    }

    public String getToken() {
        return token;
    }

    public String getUserToken() {
        return userToken;
    }

    public int getWatchCount() {
        return watchCount;
    }

    public DateTime getWeddingDay() {
        return weddingDay;
    }

    public String getWhiteBarTag() {
        return whiteBarTag;
    }

    public void setFollowing(boolean following) {
        this.following = following;
    }

    public int getFollowCount() {
        return followCount;
    }

    public Member getMember() {
        return member;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeString(this.nick);
        dest.writeString(this.avatar);
        dest.writeInt(this.bindType);
        dest.writeSerializable(this.birthDay);
        dest.writeInt(this.collectCount);
        dest.writeString(this.description);
        dest.writeString(this.detail);
        dest.writeInt(this.fansCount);
        dest.writeByte(this.following ? (byte) 1 : (byte) 0);
        dest.writeInt(this.gender);
        dest.writeString(this.hashedPassword);
        dest.writeByte(this.haveCard ? (byte) 1 : (byte) 0);
        dest.writeString(this.hometown);
        dest.writeLong(this.hometownId);
        dest.writeByte(this.isFaker ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isNeed ? (byte) 1 : (byte) 0);
        dest.writeInt(this.isPending);
        dest.writeInt(this.kine);
        dest.writeInt(this.likeCount);
        dest.writeByte(this.open ? (byte) 1 : (byte) 0);
        dest.writeInt(this.praiseCount);
        dest.writeString(this.specialty);
        dest.writeInt(this.storyCount);
        dest.writeString(this.tags);
        dest.writeString(this.token);
        dest.writeString(this.userToken);
        dest.writeInt(this.watchCount);
        dest.writeSerializable(this.weddingDay);
        dest.writeString(this.whiteBarTag);
        dest.writeInt(this.followCount);
        dest.writeParcelable(this.member, flags);
    }

    protected UserProfile(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.nick = in.readString();
        this.avatar = in.readString();
        this.bindType = in.readInt();
        this.birthDay = (DateTime) in.readSerializable();
        this.collectCount = in.readInt();
        this.description = in.readString();
        this.detail = in.readString();
        this.fansCount = in.readInt();
        this.following = in.readByte() != 0;
        this.gender = in.readInt();
        this.hashedPassword = in.readString();
        this.haveCard = in.readByte() != 0;
        this.hometown = in.readString();
        this.hometownId = in.readLong();
        this.isFaker = in.readByte() != 0;
        this.isNeed = in.readByte() != 0;
        this.isPending = in.readInt();
        this.kine = in.readInt();
        this.likeCount = in.readInt();
        this.open = in.readByte() != 0;
        this.praiseCount = in.readInt();
        this.specialty = in.readString();
        this.storyCount = in.readInt();
        this.tags = in.readString();
        this.token = in.readString();
        this.userToken = in.readString();
        this.watchCount = in.readInt();
        this.weddingDay = (DateTime) in.readSerializable();
        this.whiteBarTag = in.readString();
        this.followCount = in.readInt();
        this.member = in.readParcelable(Member.class.getClassLoader());
    }

    public static final Creator<UserProfile> CREATOR = new Creator<UserProfile>() {
        @Override
        public UserProfile createFromParcel(Parcel source) {return new UserProfile(source);}

        @Override
        public UserProfile[] newArray(int size) {return new UserProfile[size];}
    };
}
