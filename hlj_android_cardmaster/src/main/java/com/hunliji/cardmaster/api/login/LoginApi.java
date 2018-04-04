package com.hunliji.cardmaster.api.login;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.JsonObject;
import com.hunliji.cardmaster.BuildConfig;
import com.hunliji.cardmaster.R;
import com.hunliji.cardmaster.models.login.CertifyPostBody;
import com.hunliji.cardmaster.models.login.LoginPostBody;
import com.hunliji.cardmaster.models.login.LoginResult;
import com.hunliji.cardmaster.models.login.ThirdBind;
import com.hunliji.cardmaster.models.login.ThirdBindPostBody;
import com.hunliji.cardmaster.models.login.ThirdRegisterPostBody;
import com.hunliji.hljcommonlibrary.models.CertifyCodeMsg;
import com.hunliji.hljcommonlibrary.models.Location;
import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljcommonlibrary.utils.DeviceUuidFactory;
import com.hunliji.hljcommonlibrary.utils.LocationSession;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.hljhttplibrary.entities.HljApiException;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;
import com.hunliji.hljsharelibrary.models.ThirdLoginParameter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by wangtao on 2017/11/24.
 */

public class LoginApi {

    public static final int RESULT_CODE_CERTIFY_BUSY = 503;//提示"稍后再提交验证码"的retCode

    /**
     * 获得验证码
     * post 请求
     *
     * @param body
     * @return
     */
    public static Observable<HljHttpResult<CertifyCodeMsg>> getCertifyCode(CertifyPostBody body) {
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
    public static Observable<LoginResult> phoneLogin(
            final Context context, String phone, String code) {
        return HljHttp.getRetrofit()
                .create(LoginService.class)
                .phoneLogin(new LoginPostBody(context, phone, code))
                .map(new HljHttpResultFunc<LoginResult>())
                .map(new Func1<LoginResult, LoginResult>() {
                    @Override
                    public LoginResult call(LoginResult loginResult) {
                        if (loginResult == null || loginResult.getUser() == null) {
                            throw new HljApiException(context.getString(R.string.msg_login_error));
                        }
                        return loginResult;
                    }
                })
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
                    public HljHttpResult<LoginResult> call(HljHttpResult<LoginResult> result) {
                        if (result.getStatus()
                                .getRetCode() == 404 || (result.getStatus()
                                .getRetCode() == 0 && result.getData()
                                .getUser() != null)) {
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
            ThirdRegisterPostBody body) {
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
    public static Observable<HljHttpResult> thirdBind(ThirdLoginParameter body) {
        return HljHttp.getRetrofit()
                .create(LoginService.class)
                .thirdBind(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 三方解绑
     *
     * @return
     */
    public static Observable<HljHttpResult> thirdUnBind(String type) {
        Map<String, Object> params = new HashMap<>();
        params.put("type",type);
        return HljHttp.getRetrofit()
                .create(LoginService.class)
                .thirdUnbind(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 保存用户个推id
     *
     * @return
     */
    public static Observable saveClientInfo(Context context, String clientId) {
        User user = UserSession.getInstance()
                .getUser(context);
        if (user == null || user.getId() == 0 || TextUtils.isEmpty(clientId)) {
            return Observable.empty();
        }
        JsonObject infoJson = new JsonObject();
        infoJson.addProperty("cid", clientId);
        infoJson.addProperty("user_id", user.getId());
        infoJson.addProperty("from", "card_master_production");
        infoJson.addProperty("phone_token",
                DeviceUuidFactory.getInstance()
                        .getDeviceUuidString(context));
        infoJson.addProperty("apns_token", "");
        infoJson.addProperty("app_version", BuildConfig.VERSION_NAME);
        infoJson.addProperty("phone_type", 2);
        infoJson.addProperty("device", android.os.Build.MODEL);
        infoJson.addProperty("system", android.os.Build.VERSION.RELEASE);
        Location location = LocationSession.getInstance()
                .getLocation(context);
        if (location != null) {
            infoJson.addProperty("city", location.getCity());
            infoJson.addProperty("province", location.getProvince());
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("info", infoJson);
        return HljHttp.getRetrofit()
                .create(LoginService.class)
                .saveClientInfo(jsonObject)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
