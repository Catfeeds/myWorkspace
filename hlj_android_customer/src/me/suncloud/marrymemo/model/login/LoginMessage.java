package me.suncloud.marrymemo.model.login;

import org.json.JSONObject;

/**
 * 登录handler 使用的 message
 * Created by jinxin on 2016/12/12 0012.
 */

public class LoginMessage {
    String type;//qq sina weixin
    JSONObject user;//婚礼纪服务器user 信息
    String openId;
    int retCode;
    String msg;//HttpStatus 传过来的msg
    String thirdUserInfo;//三方登录的user信息

    public JSONObject getUser() {
        return user;
    }

    public void setUser(JSONObject user) {
        this.user = user;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public int getRetCode() {
        return retCode;
    }

    public void setRetCode(int retCode) {
        this.retCode = retCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getThirdUserInfo() {
        return thirdUserInfo;
    }

    public void setThirdUserInfo(String thirdUserInfo) {
        this.thirdUserInfo = thirdUserInfo;
    }
}
