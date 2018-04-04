package com.hunliji.hljcommonlibrary.models.communitythreads;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Member;

import org.joda.time.DateTime;

/**
 * Created by werther on 16/8/25.
 * 社区专用的Author,发帖或者回复所属作者
 */
public class CommunityAuthor implements Parcelable {
    long id;
    @SerializedName(value = "name", alternate = "nick")
    String name;
    @SerializedName(value = "avatar", alternate = "img")
    String avatar;
    @SerializedName(value = "weddingday")
    DateTime weddingDay;
    @SerializedName(value = "is_pending")
    int isPending;
    String specialty;
    int gender;
    Member member;

    public CommunityAuthor() {}

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAvatar() {
        return avatar;
    }

    public DateTime getWeddingDay() {
        return weddingDay;
    }

    public int isPending() {
        return isPending;
    }

    public String getSpecialty() {
        return specialty;
    }

    public int getGender() {
        return gender;
    }

    public Member getMember() {
        return member;
    }

    public String getWeddingStatus() {
        String str;
        if (weddingDay != null) {
            if (getWeddingDay().isBeforeNow()) {
                str = gender == 1 ? "已婚男" : "已为人妻";
            } else {
                str = "婚期 " + weddingDay.toString("yyyy-MM-dd");
            }
        } else {
            str = gender == 1 ? "" : "待字闺中";
        }

        return str;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setWeddingDay(DateTime weddingDay) {
        this.weddingDay = weddingDay;
    }

    public void setPending(int pending) {
        isPending = pending;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeString(this.avatar);
        dest.writeSerializable(this.weddingDay);
        dest.writeInt(this.isPending);
        dest.writeString(this.specialty);
        dest.writeInt(this.gender);
        dest.writeParcelable(this.member, flags);
    }

    protected CommunityAuthor(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.avatar = in.readString();
        this.weddingDay = (DateTime) in.readSerializable();
        this.isPending = in.readInt();
        this.specialty = in.readString();
        this.gender = in.readInt();
        this.member = in.readParcelable(Member.class.getClassLoader());
    }

    public static final Creator<CommunityAuthor> CREATOR = new Creator<CommunityAuthor>() {
        @Override
        public CommunityAuthor createFromParcel(Parcel source) {return new CommunityAuthor(source);}

        @Override
        public CommunityAuthor[] newArray(int size) {return new CommunityAuthor[size];}
    };
}
