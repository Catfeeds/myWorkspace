package com.hunliji.cardmaster.api.login;

import com.google.gson.JsonObject;
import com.hunliji.cardmaster.models.login.CertifyPostBody;
import com.hunliji.cardmaster.models.login.LoginPostBody;
import com.hunliji.cardmaster.models.login.LoginResult;
import com.hunliji.cardmaster.models.login.ThirdBind;
import com.hunliji.cardmaster.models.login.ThirdBindPostBody;
import com.hunliji.cardmaster.models.login.ThirdRegisterPostBody;
import com.hunliji.hljcommonlibrary.models.CertifyCodeMsg;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljsharelibrary.models.ThirdLoginParameter;

import java.util.List;
import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by wangtao on 2017/11/24.
 */

public interface LoginService {
    /**
     * 获得验证码
     *
     * @param body
     * @return
     */
    @POST("p/wedding/index.php/Home/APIUser/sendMsg")
    Observable<HljHttpResult<CertifyCodeMsg>> getCertifyCode(@Body CertifyPostBody body);

    /**
     * 手机验证码登录
     *
     * @param body
     * @return
     */
    @POST("p/wedding/index.php/Home/APIlogin/clientPhoneLoginV3")
    Observable<HljHttpResult<LoginResult>> phoneLogin(@Body LoginPostBody body);


    /**
     * 三方登录
     */
    @GET("/p/wedding/home/APIUser/oauth_login_v4")
    Observable<HljHttpResult<LoginResult>> thirdLogin(
            @Query("type") String type, @Query("openid") String openid);


    /**
     * 三方注册
     */
    @POST("/p/wedding/home/APIUser/oauth_register_v4")
    Observable<HljHttpResult<LoginResult>> thirdRegister(@Body ThirdRegisterPostBody body);


    /**
     * 第三方绑定列表
     *
     * @return
     */
    @GET("/p/wedding/home/APIUser/oauth_list_v4")
    Observable<HljHttpResult<HljHttpData<List<ThirdBind>>>> getThirdBind();

    /**
     * 三方登录绑定
     *
     * @return
     */
    @POST("/p/wedding/home/APIUser/oauth_bind_v4")
    Observable<HljHttpResult> thirdBind(@Body ThirdLoginParameter body);

    /**
     * 三方登录解绑
     *
     * @return
     */
    @POST("/p/wedding/home/APIUser/oauth_unbind_v4")
    Observable<HljHttpResult> thirdUnbind(@Body Map<String,Object> params);

    /**
     * 保存用户个推id
     */
    @POST("p/wedding/index.php/Home/APIGeTuiUser/SaveClientInfo")
    Observable<HljHttpResult> saveClientInfo(@Body JsonObject body);


}
