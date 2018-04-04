package com.hunliji.hljcommonlibrary.models.customer;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Member;
import com.hunliji.hljcommonlibrary.models.User;

import org.joda.time.DateTime;

import java.util.Date;

/**
 * Created by Suncloud on 2016/8/1.
 * 用户客户端Http用于验证用户信息的基本信息类
 */
public class CustomerUser extends User {
    @SerializedName(value = "id", alternate = "user_id")
    long id;
    String token;
    @SerializedName("weddingday")
    DateTime weddingDay;
    @SerializedName("is_pending")
    int isPending;//0未设置婚期 1婚期待定 2设置了婚期
    @SerializedName(value = "specialty")
    private String specialty;//达人标志 不为空或不等于“普通用户”
    private Member member;
    @SerializedName("hometown")
    private String hometown;

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public String getHttpHeadCityStr() {
        // 实现用户客户端的city头
        return super.getHttpHeadCityStr();
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public boolean isMember() {
        return member != null && member.getId() > 0;
    }

    public String getHometown() {
        return hometown;
    }

    public CustomerUser() {}

    @Override
    public DateTime getWeddingDay() {
        return isPending == 0 ? null : weddingDay;
    }

    public void setWeddingDay(Date weddingDay) {
        if (weddingDay != null) {
            this.weddingDay = new DateTime(weddingDay);
        } else {
            this.weddingDay = null;
        }
    }

    public void setIsPending(int isPending) {
        this.isPending = isPending;
    }

    @Override
    public int getIsPending() {
        return isPending;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(this.id);
        dest.writeString(this.token);
        dest.writeSerializable(this.weddingDay);
        dest.writeValue(this.isPending);
        dest.writeString(this.specialty);
        dest.writeString(this.hometown);
    }

    protected CustomerUser(Parcel in) {
        super(in);
        this.id = in.readLong();
        this.token = in.readString();
        this.weddingDay = (DateTime) in.readSerializable();
        this.isPending = (Integer) in.readValue(Integer.class.getClassLoader());
        this.specialty = in.readString();
        this.hometown = in.readString();
    }

    public static final Creator<CustomerUser> CREATOR = new Creator<CustomerUser>() {
        @Override
        public CustomerUser createFromParcel(Parcel source) {return new CustomerUser(source);}

        @Override
        public CustomerUser[] newArray(int size) {return new CustomerUser[size];}
    };
}
