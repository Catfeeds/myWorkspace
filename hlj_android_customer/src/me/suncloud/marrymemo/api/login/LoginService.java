package me.suncloud.marrymemo.api.login;

import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljsharelibrary.models.ThirdLoginParameter;

import java.util.List;

import me.suncloud.marrymemo.model.ThirdBind;
import com.hunliji.hljcommonlibrary.models.CertifyCodeMsg;
import me.suncloud.marrymemo.model.login.CertifyPostBody;
import me.suncloud.marrymemo.model.login.LoginBindPhoneBody;
import me.suncloud.marrymemo.model.login.LoginMark;
import me.suncloud.marrymemo.model.login.LoginMarkBody;
import me.suncloud.marrymemo.model.login.LoginPhoneBody;
import me.suncloud.marrymemo.model.login.LoginPostBody;
import me.suncloud.marrymemo.model.login.LoginPwdPostBody;
import me.suncloud.marrymemo.model.login.LoginResult;
import me.suncloud.marrymemo.model.login.ThirdBindPostBody;
import me.suncloud.marrymemo.model.login.ThirdLoginPostBody;
import me.suncloud.marrymemo.model.login.WeChatLoginBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by jinxin on 2016/8/30.
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
     * 账密登录
     *
     * @param body
     * @return
     */
    @POST("p/wedding/index.php/Home/APIlogin/clientPhoneLogin")
    Observable<HljHttpResult<LoginResult>> phoneLoginWithPwd(@Body LoginPwdPostBody body);

    /**
     * 获得标签
     *
     * @return
     */
    @GET("p/wedding/index.php/Home/APIUser/marks")
    Observable<HljHttpResult<HljHttpData<List<LoginMark>>>> getWeddingSegment();

    /**
     * 标签
     *
     * @param body
     * @return
     */
    @POST("p/wedding/index.php/Home/APIUser/addMarks")
    Observable<HljHttpResult> userMark(@Body LoginMarkBody body);

    /**
     * 检查手机号是否可用
     *
     * @param body
     * @return
     */
    @POST("p/wedding/index.php/Home/APIUser/check_phone_bind")
    Observable<HljHttpResult> certifyPhone(@Body LoginPhoneBody body);

    /**
     * 三方登录
     */
    @GET("/p/wedding/home/APIUser/oauth_login_v4")
    Observable<HljHttpResult<LoginResult>> thirdLogin(
            @Query("type") String type,
            @Query("openid") String openid);

    /**
     * 三方注册
     */
    @POST("/p/wedding/home/APIUser/oauth_register_v4")
    Observable<HljHttpResult<LoginResult>> thirdRegister(@Body ThirdLoginPostBody body);

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
    Observable<HljHttpResult> thirdBind(@Body ThirdLoginParameter parameter);

    /**
     * 三方登录解绑
     *
     * @return
     */
    @POST("/p/wedding/home/APIUser/oauth_unbind_v4")
    Observable<HljHttpResult> thirdUnbind(@Body ThirdBindPostBody body);

    /**
     * 提现验证码
     *
     * @return
     */
    @GET("p/wedding/home/WeixinWall/withdraw_code")
    Observable<HljHttpResult<CertifyCodeMsg>> getWithdrawCode();

    /**
     * 通用验证码预验证接口
     *
     * @return
     */
    @POST("p/wedding/index.php/Home/APIUser/pre_check_sms_code")
    Observable<HljHttpResult> preCheckSmsCode(@Body ThirdLoginPostBody body);

}
