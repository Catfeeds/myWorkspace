package me.suncloud.marrymemo.model.login;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.utils.DeviceUuidFactory;

/**
 * Created by jinxin on 2016/8/31.
 */
public class LoginPostBody {
    String code;
    String 	phone;
    @SerializedName(value = "phone_token")
    String phoneToken;

    public LoginPostBody(Context context,String phone, String code) {
        this.phone = phone;
        this.code = code;
        this.phoneToken=DeviceUuidFactory.getInstance()
                .getDeviceUuidString(context);
    }
}
