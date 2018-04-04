package com.hunliji.cardmaster.models.login;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljsharelibrary.models.ThirdLoginParameter;

/**
 * Created by wangtao on 2017/11/24.
 */

public class ThirdBindPostBody {
    String type;//qq sina weixin
    @SerializedName("openid")
    String openId;//QQ WeChat 用openid Sina 用id
    @SerializedName("user_info")
    String userInfo;//三方登录返回的user_info

    public ThirdBindPostBody(ThirdLoginParameter loginParameter) {
        this.type = loginParameter.getType();
        this.openId = loginParameter.getThirdId();
        this.userInfo = loginParameter.getLoginInfo();
    }

    public ThirdBindPostBody(String type, String openId, String userInfo) {
        this.type = type;
        this.openId = openId;
        this.userInfo = userInfo;
    }
}
