package com.hunliji.marrybiz.api.login;

import android.text.TextUtils;

import com.google.gson.JsonElement;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by mo_yu on 2017/8/10.登陆注册模块
 */

public class LoginApi {

    /**
     * 获取验证码
     *
     * @param phone
     * @return
     */
    public static Observable getValidCodeObb(String phone) {
        return HljHttp.getRetrofit()
                .create(LoginService.class)
                .getValidCode(phone)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 注册
     *
     * @param phone
     * @param code
     * @param password
     * @param confirmPassword
     * @return
     */
    public static Observable<JsonElement> postRegisterObb(
            String phone, String code, String password, String confirmPassword) {
        Map<String, Object> map = new HashMap<>();
        map.put("contact_phone", phone);
        map.put("code", code);
        map.put("password", password);
        map.put("shop_type", String.valueOf(0));
        return HljHttp.getRetrofit()
                .create(LoginService.class)
                .postRegister(map)
                .map(new HljHttpResultFunc<JsonElement>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取实名认证
     *
     * @param merchantId
     * @return
     */
    public static Observable<JsonElement> getCertifyInfoObb(long merchantId) {
        return HljHttp.getRetrofit()
                .create(LoginService.class)
                .getCertifyInfo(merchantId)
                .map(new HljHttpResultFunc<JsonElement>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 编辑认证
     *
     * @param realName     真实姓名或法人姓名
     * @param certify      身份证号码
     * @param type         1 个人 2企业
     * @param certifyFront 正面照
     * @param certifyBack  背面照
     * @return
     */
    public static Observable postCertifyInfoObb(
            String realName, String certify, int type, String certifyFront, String certifyBack) {
        return postCertifyInfoObb(realName, certify, type, certifyFront, certifyBack, null);
    }

    /**
     * 企业编辑认证
     *
     * @param realName       真实姓名或法人姓名
     * @param certify        身份证号码
     * @param type           1 个人 2企业
     * @param certifyFront   正面照
     * @param certifyBack    背面照
     * @param companyLicense 企业法人营业执照
     * @return
     */
    public static Observable postCertifyInfoObb(
            String realName,
            String certify,
            int type,
            String certifyFront,
            String certifyBack,
            String companyLicense) {
        Map<String, Object> map = new HashMap<>();
        map.put("realname", realName);
        map.put("certify", certify);
        map.put("type", type);
        map.put("certify_front", certifyFront);
        map.put("certify_back", certifyBack);
        if (!TextUtils.isEmpty(companyLicense)) {
            map.put("company_license", companyLicense);
        }
        return HljHttp.getRetrofit()
                .create(LoginService.class)
                .postCertifyInfo(map)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
