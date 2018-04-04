package com.hunliji.cardmaster.models.login;

/**
 * Created by wangtao on 2017/11/24.
 */

public class CertifyPostBody {

    public static final String FLAG_LOGIN_CERTIFY_VOICE="loginVoice";
    public static final String FLAG_LOGIN_CERTIFY_MSG="loginMsg";
    
    String flag;
    String phone;

    public CertifyPostBody(String phone, String flag) {
        this.phone = phone;
        this.flag = flag;
    }
}
