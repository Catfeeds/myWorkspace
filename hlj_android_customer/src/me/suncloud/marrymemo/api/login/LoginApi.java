package me.suncloud.marrymemo.api.login;

import android.content.Context;

import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.entities.HljApiException;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;
import com.hunliji.hljsharelibrary.models.ThirdLoginParameter;

import java.util.List;

import me.suncloud.marrymemo.R;
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
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 登录api
 * Created by jinxin on 2016/8/30.
 */
public class LoginApi {

    /**
     * 获得验证码
     * post 请求
     *
     * @param body
     * @return
     */
    public static Observable<HljHttpResult<CertifyCodeMsg>> getPostCertifyCode(
            CertifyPostBody body) {
        return HljHttp.getRetrofit()
                .create(LoginService.class)
                .getCertifyCode(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 验证码登录
     *
     * @param context
     * @param phone
     * @param code
     * @return
     */
    public static Observable<LoginResult> phoneLogin(final Context context, String phone, String code) {
        return HljHttp.getRetrofit()
                .create(LoginService.class)
                .phoneLogin( new LoginPostBody(context,phone,code))
                .map(new HljHttpResultFunc<LoginResult>())
                .map(new Func1<LoginResult, LoginResult>() {
                    @Override
                    public LoginResult call(LoginResult loginResult) {
                        if(loginResult==null||loginResult.getUser()==null){
                            throw new HljApiException(context.getString(R.string.msg_login_error));
                        }
                        return loginResult;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 账密登录
     *
     * @param body
     * @return
     */
    public static Observable<LoginResult> phonePwdLogin(
            LoginPwdPostBody body) {
        return HljHttp.getRetrofit()
                .create(LoginService.class)
                .phoneLoginWithPwd(body)
                .map(new HljHttpResultFunc<LoginResult>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 登录标签
     *
     * @return
     */
    public static Observable<HljHttpData<List<LoginMark>>> getWeddingSegment() {
        return HljHttp.getRetrofit()
                .create(LoginService.class)
                .getWeddingSegment()
                .map(new HljHttpResultFunc<HljHttpData<List<LoginMark>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 用户打标签
     *
     * @param body
     * @return
     */
    public static Observable<HljHttpResult> userMark(LoginMarkBody body) {
        return HljHttp.getRetrofit()
                .create(LoginService.class)
                .userMark(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 验证手机号是否可用
     *
     * @param phone
     * @return
     */
    public static Observable<HljHttpResult> certifyPhone(String phone) {
        return HljHttp.getRetrofit()
                .create(LoginService.class)
                .certifyPhone(new LoginPhoneBody(phone))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 三方登录
     *
     * @param type
     * @param openid
     * @return
     */
    public static Observable<HljHttpResult<LoginResult>> thirdLogin(String type, String openid) {
        return HljHttp.getRetrofit()
                .create(LoginService.class)
                .thirdLogin(type, openid)
                .map(new Func1<HljHttpResult<LoginResult>, HljHttpResult<LoginResult>>() {
                    @Override
                    public HljHttpResult<LoginResult> call(HljHttpResult<LoginResult>
                                                                   result) {
                        if(result.getStatus().getRetCode()==404||(result.getStatus().getRetCode()==0&&result.getData().getUser() != null)) {
                            return result;
                        }
                        throw new HljApiException("登录失败");
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 三方注册
     *
     * @return
     */
    public static Observable<LoginResult> thirdRegister(
            ThirdLoginPostBody body) {
        return HljHttp.getRetrofit()
                .create(LoginService.class)
                .thirdRegister(body)
                .map(new HljHttpResultFunc<LoginResult>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获得三方绑定列表
     *
     * @return
     */
    public static Observable<HljHttpData<List<ThirdBind>>> getThirdBind() {
        return HljHttp.getRetrofit()
                .create(LoginService.class)
                .getThirdBind()
                .map(new HljHttpResultFunc<HljHttpData<List<ThirdBind>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 三方绑定
     *
     * @return
     */
    public static Observable<HljHttpResult> thirdBind(ThirdLoginParameter parameter) {
        return HljHttp.getRetrofit()
                .create(LoginService.class)
                .thirdBind(parameter)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 三方解绑
     *
     * @return
     */
    public static Observable<HljHttpResult> thirdUnBind(ThirdBindPostBody body) {
        return HljHttp.getRetrofit()
                .create(LoginService.class)
                .thirdUnbind(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获得提现验证码
     * @return
     */
    public static Observable<HljHttpResult<CertifyCodeMsg>> getWithdrawCodeObb() {
        return HljHttp.getRetrofit()
                .create(LoginService.class)
                .getWithdrawCode()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 通用验证码预验证接口
     * @return
     */
    public static Observable preCheckSmsCodeObb(String smsCode) {
        return HljHttp.getRetrofit()
                .create(LoginService.class)
                .preCheckSmsCode(new ThirdLoginPostBody(smsCode))
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
