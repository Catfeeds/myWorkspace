package com.hunliji.hljcommonlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.realm.ExtendMember;

import org.joda.time.DateTime;

/**
 * Created by werther on 16/7/23.
 * 客户端用于Http验证用户信息的基类
 */
public class User implements Parcelable {

    @SerializedName(value = "name")
    String name;
    @SerializedName(value = "nick")
    String nick;
    String phone;
    @SerializedName(value = "avatar")
    String avatar;
    ExtendMember extend;//私信会员标识

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public long getId() {
        return 0;
    }

    public String getNick() {
        if (TextUtils.isEmpty(nick)) {
            return name;
        }
        return nick;
    }

    public ExtendMember getExtend() {
        return extend;
    }

    public void setExtend(ExtendMember extend) {
        this.extend = extend;
    }

    public String getName() {
        if (TextUtils.isEmpty(name)) {
            return nick;
        }
        return name;
    }

    public String getRealName(){
        return name;
    }

    public String getToken() {
        return null;
    }

    public String getAccessToken() {
        return null;
    }

    public String getHttpHeadCityStr() {
        return null;
    }

    public String getPhone() {
        return phone;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    /**
     * 默认婚期待定
     *
     * @return
     */
    public int getIsPending() {
        return 1;
    }

    public User() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.nick);
        dest.writeString(this.name);
        dest.writeString(this.phone);
        dest.writeString(this.avatar);
        dest.writeParcelable(this.extend, flags);
    }

    protected User(Parcel in) {
        this.nick = in.readString();
        this.name = in.readString();
        this.phone = in.readString();
        this.avatar = in.readString();
        this.extend = in.readParcelable(ExtendMember.class.getClassLoader());
    }

    public DateTime getWeddingDay() {
        return new DateTime();
    }

    public Author toAuthor() {
        Author author = new Author();
        author.setId(getId());
        author.setName(getNick());
        author.setAvatar(getAvatar());
        return author;
    }
}
