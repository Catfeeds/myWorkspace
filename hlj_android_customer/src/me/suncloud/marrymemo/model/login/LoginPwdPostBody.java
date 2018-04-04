package me.suncloud.marrymemo.model.login;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jinxin on 2016/8/31.
 */
public class LoginPwdPostBody implements Parcelable {
    String phone;
    String pwd;

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(
            Parcel dest,
            int flags) {
        dest.writeString(this.phone);
        dest.writeString(this.pwd);
    }

    public LoginPwdPostBody() {}

    protected LoginPwdPostBody(Parcel in) {
        this.phone = in.readString();
        this.pwd = in.readString();
    }

    public static final Parcelable.Creator<LoginPwdPostBody> CREATOR = new
            Parcelable.Creator<LoginPwdPostBody>() {
        @Override
        public LoginPwdPostBody createFromParcel(Parcel source) {
            return new LoginPwdPostBody(source);
        }

        @Override
        public LoginPwdPostBody[] newArray(int size) {return new LoginPwdPostBody[size];}
    };

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
}
