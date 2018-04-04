package com.hunliji.hljkefulibrary.moudles;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wangtao on 2017/10/20.
 */

public class HxUser implements Parcelable {
    @SerializedName("username")
    String userName;
    @SerializedName("password")
    String passWord;

    public String getPassWord() {
        return passWord;
    }

    public String getUserName() {
        return userName;
    }

    protected HxUser(Parcel in) {
        userName = in.readString();
        passWord = in.readString();
    }

    public static final Creator<HxUser> CREATOR = new Creator<HxUser>() {
        @Override
        public HxUser createFromParcel(Parcel in) {
            return new HxUser(in);
        }

        @Override
        public HxUser[] newArray(int size) {
            return new HxUser[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userName);
        dest.writeString(passWord);
    }
}
