package me.suncloud.marrymemo.model.login;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jinxin on 2016/9/5.
 */
public class LoginBindPhoneBody implements Parcelable {
    String phone;
    @SerializedName(value = "sms_code")
    String smsCode;

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(
            Parcel dest,
            int flags) {
        dest.writeString(this.phone);
        dest.writeString(this.smsCode);
    }

    public LoginBindPhoneBody() {}

    protected LoginBindPhoneBody(Parcel in) {
        this.phone = in.readString();
        this.smsCode = in.readString();
    }

    public static final Parcelable.Creator<LoginBindPhoneBody> CREATOR = new
            Parcelable.Creator<LoginBindPhoneBody>() {
        @Override
        public LoginBindPhoneBody createFromParcel(Parcel source) {
            return new LoginBindPhoneBody(source);
        }

        @Override
        public LoginBindPhoneBody[] newArray(int size) {return new LoginBindPhoneBody[size];}
    };

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSmsCode() {
        return smsCode;
    }

    public void setSmsCode(String smsCode) {
        this.smsCode = smsCode;
    }
}
