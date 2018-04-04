package me.suncloud.marrymemo.model.login;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * user 字段验证码登录用到
 * action token userId 等字段 账密登录时用到
 * Created by jinxin on 2016/8/31.
 */
public class LoginResult implements Parcelable {
    String action;//登录或者注册
    String token;//用户token
    @SerializedName(value = "user_id")
    long userId;
    JsonElement user;

    public LoginResult() {}

    public String getToken() {
        return token;
    }

    public long getUserId() {
        return userId;
    }

    public JSONObject getUser() {
        if (user == null) {
            return null;
        }
        try {
            return new JSONObject(user.toString());
        } catch (JSONException e) {
            return null;
        }
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.action);
        dest.writeString(this.token);
        dest.writeLong(this.userId);
    }

    protected LoginResult(Parcel in) {
        this.action = in.readString();
        this.token = in.readString();
        this.userId = in.readLong();
    }

    public static final Creator<LoginResult> CREATOR = new Creator<LoginResult>() {
        @Override
        public LoginResult createFromParcel(Parcel source) {return new LoginResult(source);}

        @Override
        public LoginResult[] newArray(int size) {return new LoginResult[size];}
    };
}
