package com.hunliji.cardmaster.models.login;

import android.content.Context;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.utils.DeviceUuidFactory;

/**
 * Created by wangtao on 2017/11/24.
 */

public class LoginPostBody {
    String code;
    String 	phone;
    @SerializedName(value = "phone_token")
    String phoneToken;

    public LoginPostBody(Context context, String phone, String code) {
        this.phone = phone;
        this.code = code;
        this.phoneToken= DeviceUuidFactory.getInstance()
                .getDeviceUuidString(context);
    }}
