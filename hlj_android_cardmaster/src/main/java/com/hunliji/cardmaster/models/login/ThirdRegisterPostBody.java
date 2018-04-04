package com.hunliji.cardmaster.models.login;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljsharelibrary.models.ThirdLoginParameter;

/**
 * Created by wangtao on 2017/11/24.
 */

public class ThirdRegisterPostBody extends ThirdBindPostBody {
    String phone;
    @SerializedName("sms_code")
    String code;

    public ThirdRegisterPostBody(
            String phone, String code, ThirdLoginParameter loginParameter) {
        super(loginParameter);
        this.phone = phone;
        this.code = code;
    }
}
