package me.suncloud.marrymemo.model.login;

/**
 * Created by jinxin on 2016/8/30.
 */
public class CertifyPostBody {
    String flag;
    String phone;


    public CertifyPostBody(String phone, String flag) {
        this.phone = phone;
        this.flag = flag;
    }
}
