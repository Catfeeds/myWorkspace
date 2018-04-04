package com.hunliji.marrybiz.api.login;

import com.google.gson.JsonElement;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;

import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by mo_yu on 2017/8/10.登陆注册模块
 */

public interface LoginService {
    /**
     * 获取验证码
     */
    @GET("p/wedding/index.php/admin/APIMerchant/wed_reg_code")
    Observable<HljHttpResult> getValidCode(@Query("contact_phone") String phone);

    /**
     * 注册
     */
    @POST("p/wedding/index.php/admin/APIMerchant/wed_register")
    Observable<HljHttpResult<JsonElement>> postRegister(@Body Map<String, Object> map);


    /**
     * 获取实名认证信息
     */
    @GET("p/wedding/Admin/APIMerchant/certify")
    Observable<HljHttpResult<JsonElement>> getCertifyInfo(@Query("merchant_id") long merchantId);

    /**
     * 实名认证
     */
    @POST("p/wedding/Admin/APIMerchant/certify")
    Observable<HljHttpResult> postCertifyInfo(@Body Map<String, Object> map);
}
