package com.hunliji.hljcommonlibrary.models.userprofile;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mo_yu on 2017/6/15.微信返回的信息集合
 */

public class WXInfo {

    @SerializedName("access_token")
    private String accessToken;
    @SerializedName("expires_in")
    private String expiresIn;
    @SerializedName("refresh_token")
    private String refreshToken;
    @SerializedName("openid")
    private String openId;
    @SerializedName("scope")
    private String scope;
    @SerializedName("unionid")
    private String unionid;

    public String getAccessToken() {
        return accessToken;
    }

    public String getExpiresIn() {
        return expiresIn;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getOpenId() {
        return openId;
    }

    public String getScope() {
        return scope;
    }

    public String getUnionid() {
        return unionid;
    }
}
