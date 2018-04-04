package com.hunliji.hljcommonlibrary.models.realm;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by hua_rong on 2018/2/26
 * 私信界面用户logo增加会员标识
 */

public class ExtendMember extends RealmObject implements Parcelable {

    @SerializedName(value = "user_id")
    private long userId;
    @SerializedName(value = "hlj_member_privilege")
    private int hljMemberPrivilege;//用户vip标识
    private int specialty;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getHljMemberPrivilege() {
        return hljMemberPrivilege;
    }

    public void setHljMemberPrivilege(int hljMemberPrivilege) {
        this.hljMemberPrivilege = hljMemberPrivilege;
    }

    public int getSpecialty() {
        return specialty;
    }

    public void setSpecialty(int specialty) {
        this.specialty = specialty;
    }


    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.userId);
        dest.writeInt(this.hljMemberPrivilege);
        dest.writeInt(this.specialty);
    }

    public ExtendMember() {}

    protected ExtendMember(Parcel in) {
        this.userId = in.readLong();
        this.hljMemberPrivilege = in.readInt();
        this.specialty = in.readInt();
    }

    public static final Parcelable.Creator<ExtendMember> CREATOR = new Parcelable
            .Creator<ExtendMember>() {
        @Override
        public ExtendMember createFromParcel(Parcel source) {return new ExtendMember(source);}

        @Override
        public ExtendMember[] newArray(int size) {return new ExtendMember[size];}
    };
}
