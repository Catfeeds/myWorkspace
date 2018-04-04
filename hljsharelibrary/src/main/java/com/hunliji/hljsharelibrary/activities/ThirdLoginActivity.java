package com.hunliji.hljsharelibrary.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ProgressBar;

import com.google.gson.JsonObject;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.FileUtil;
import com.hunliji.hljhttplibrary.entities.HljApiException;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljsharelibrary.HljShare;
import com.hunliji.hljsharelibrary.HljThirdLogin;
import com.hunliji.hljsharelibrary.R;
import com.hunliji.hljsharelibrary.models.ThirdLoginParameter;
import com.hunliji.hljsharelibrary.third.ThirdApi;
import com.hunliji.hljsharelibrary.utils.WXCallbackUtil;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WbAuthListener;
import com.sina.weibo.sdk.auth.WbConnectErrorMessage;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.tencent.connect.UserInfo;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by wangtao on 2017/7/14.
 */

public class ThirdLoginActivity extends Activity {

    private ProgressBar progressBar;

    public static final String KEY_LOGIN_TYPE = "login_type";
    public static final String KEY_INFO_TYPE = "info_type";

    private Tencent tencent;
    private SsoHandler mSsoHandler;
    private Oauth2AccessToken mAccessToken;
    private IUiListener qqLoginListener;

    private String loginType;
    private String infoType;

    private Subscription loginSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third_empty___share);
        progressBar = findViewById(R.id.progress_bar);
        initData();
        login();
    }

    @Override
    public void onBackPressed() {
        CommonUtil.unSubscribeSubs(loginSubscription);
        finish();
        overridePendingTransition(0, 0);
    }

    private void initData() {
        loginType = getIntent().getStringExtra(KEY_LOGIN_TYPE);
        infoType = getIntent().getStringExtra(KEY_INFO_TYPE);
    }

    private void login() {
        if (TextUtils.isEmpty(loginType)) {
            onBackPressed();
            return;
        }
        Subscriber<ThirdLoginParameter> loginSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setProgressBar(progressBar)
                .setOnNextListener(new SubscriberOnNextListener<ThirdLoginParameter>() {
                    @Override
                    public void onNext(ThirdLoginParameter thirdLoginParameter) {
                        switch (infoType) {
                            case HljThirdLogin.InfoType.LOGIN:
                                RxBus.getDefault()
                                        .post(new RxEvent(RxEvent.RxEventType.THIRD_LOGIN_CALLBACK,
                                                thirdLoginParameter));
                                break;
                            case HljThirdLogin.InfoType.BIND:
                                RxBus.getDefault()
                                        .post(new RxEvent(RxEvent.RxEventType.THIRD_BIND_CALLBACK,
                                                thirdLoginParameter));
                                break;

                        }
                        onBackPressed();
                    }
                })
                .setOnErrorListener(new SubscriberOnErrorListener() {
                    @Override
                    public void onError(Object o) {
                        onBackPressed();
                    }
                })
                .build();
        switch (loginType) {
            case HljThirdLogin.LoginType.QQ:
                loginSubscription = Observable.interval(500, TimeUnit.MILLISECONDS)
                        .first()
                        .concatMap(new Func1<Long, Observable<ThirdLoginParameter>>() {
                            @Override
                            public Observable<ThirdLoginParameter> call(Long aLong) {
                                return qqLoginObb();
                            }
                        })
                        .subscribe(loginSubscriber);
                break;
            case HljThirdLogin.LoginType.WEIBO:
                loginSubscription = weiboLoginObb().subscribe(loginSubscriber);
                break;
            case HljThirdLogin.LoginType.WEIXIN:
                loginSubscription = Observable.interval(500, TimeUnit.MILLISECONDS)
                        .first()
                        .concatMap(new Func1<Long, Observable<ThirdLoginParameter>>() {
                            @Override
                            public Observable<ThirdLoginParameter> call(Long aLong) {
                                return weixinLoginObb();
                            }
                        })
                        .subscribe(loginSubscriber);
                break;
            default:
                onBackPressed();
                break;
        }
    }


    private Observable<ThirdLoginParameter> qqLoginObb() {
        return Observable.create(new Observable.OnSubscribe<ThirdLoginParameter>() {
            @Override
            public void call(final Subscriber<? super ThirdLoginParameter> subscriber) {
                if (!HljThirdLogin.isQQClientAvailable(ThirdLoginActivity.this)) {
                    subscriber.onError(new HljApiException(getString(R.string.unfind_qq___share)));
                    return;
                }
                tencent = Tencent.createInstance(HljShare.QQKEY, ThirdLoginActivity.this);
                if (tencent.isSessionValid()) {
                    tencent.logout(ThirdLoginActivity.this);
                }
                qqLoginListener = new IUiListener() {
                    @Override
                    public void onComplete(Object arg0) {
                        String openId = null;
                        try {
                            JSONObject json = (JSONObject) arg0;
                            if (tencent == null) {
                                subscriber.onError(new HljApiException("登录失败"));
                                return;
                            }
                            if (tencent.isSessionValid()) {
                                openId = json.optString("openid");
                                tencent.setOpenId(openId);
                                String expiresIn = json.optString("expires_in");
                                tencent.setAccessToken(json.optString("access_token"), expiresIn);
                            }
                            long expiresIn = tencent.getQQToken()
                                    .getExpireTimeInSecond();
                            json.put("expires_in", expiresIn);
                            FileUtil.saveStringToFile(json.toString(),
                                    openFileOutput(HljShare.QQ_FILE, Context.MODE_PRIVATE));
                            subscriber.onNext(new ThirdLoginParameter(openId,
                                    json.toString(),
                                    loginType));
                            subscriber.onCompleted();
                        } catch (FileNotFoundException | JSONException e1) {
                            e1.printStackTrace();
                            subscriber.onError(new HljApiException("登录失败"));
                        }
                    }

                    @Override
                    public void onError(UiError uiError) {
                        subscriber.onError(new HljApiException(uiError.errorDetail));
                    }

                    @Override
                    public void onCancel() {
                        subscriber.onError(new HljApiException("取消登录"));
                    }
                };
                tencent.login(ThirdLoginActivity.this, "all", qqLoginListener);
            }
        })
                .concatMap(new Func1<ThirdLoginParameter, Observable<? extends
                        ThirdLoginParameter>>() {
                    @Override
                    public Observable<? extends ThirdLoginParameter> call(
                            ThirdLoginParameter thirdLoginParameter) {
                        switch (infoType) {
                            case HljThirdLogin.InfoType.LOGIN:
                                return getQQUserInfoObb(thirdLoginParameter);
                            case HljThirdLogin.InfoType.BIND:
                                break;
                        }
                        return Observable.just(thirdLoginParameter);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Observable<ThirdLoginParameter> getQQUserInfoObb(
            final ThirdLoginParameter thirdLoginParameter) {
        return Observable.create(new Observable.OnSubscribe<ThirdLoginParameter>() {
            @Override
            public void call(final Subscriber<? super ThirdLoginParameter> subscriber) {
                new UserInfo(ThirdLoginActivity.this,
                        tencent.getQQToken()).getUserInfo(new IUiListener() {

                    @Override
                    public void onError(UiError arg0) {
                        subscriber.onError(new HljApiException("登录失败"));
                    }

                    @Override
                    public void onComplete(Object arg0) {
                        try {
                            if (arg0 != null) {
                                thirdLoginParameter.setLoginInfo(arg0.toString());
                            }
                            subscriber.onNext(thirdLoginParameter);
                            subscriber.onCompleted();
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                            subscriber.onError(new HljApiException("登录失败"));
                        }
                    }

                    @Override
                    public void onCancel() {
                        subscriber.onError(new HljApiException("取消登录"));
                    }
                });
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Observable<ThirdLoginParameter> weiboLoginObb() {
        return Observable.create(new Observable.OnSubscribe<ThirdLoginParameter>() {
            @Override
            public void call(final Subscriber<? super ThirdLoginParameter> subscriber) {
                WbSdk.install(ThirdLoginActivity.this,
                        new AuthInfo(ThirdLoginActivity.this,
                                HljShare.WEIBOKEY,
                                HljShare.WEIBO_CALLBACK,
                                HljShare.SCOPE));
                mSsoHandler = new SsoHandler(ThirdLoginActivity.this);
                mSsoHandler.authorize(new WbAuthListener() {
                    @Override
                    public void onSuccess(
                            Oauth2AccessToken oauth2AccessToken) {
                        mAccessToken = oauth2AccessToken;
                        try {
                            JSONObject obj = new JSONObject();
                            obj.put("uid", mAccessToken.getUid());
                            obj.put("access_token", mAccessToken.getToken());
                            obj.put("expires_in", mAccessToken.getExpiresTime());
                            FileUtil.saveStringToFile(obj.toString(),
                                    openFileOutput(HljShare.WEIBO_FILE, Context.MODE_PRIVATE));
                            subscriber.onNext(new ThirdLoginParameter(mAccessToken.getUid(),
                                    obj.toString(),
                                    loginType));
                            subscriber.onCompleted();
                        } catch (FileNotFoundException | JSONException e1) {
                            e1.printStackTrace();
                            subscriber.onError(new HljApiException("登录失败"));
                        }

                    }

                    @Override
                    public void cancel() {
                        subscriber.onError(new HljApiException("取消登录"));
                    }

                    @Override
                    public void onFailure(WbConnectErrorMessage wbConnectErrorMessage) {
                        subscriber.onError(new HljApiException(wbConnectErrorMessage
                                .getErrorMessage()));
                    }
                });
            }
        })
                .concatMap(new Func1<ThirdLoginParameter, Observable<? extends
                        ThirdLoginParameter>>() {
                    @Override
                    public Observable<? extends ThirdLoginParameter> call(
                            final ThirdLoginParameter thirdLoginParameter) {
                        switch (infoType) {
                            case HljThirdLogin.InfoType.LOGIN:
                                return ThirdApi.getWeiboUserInfo(mAccessToken.getToken(),
                                        mAccessToken.getUid())
                                        .map(new Func1<JsonObject, ThirdLoginParameter>() {
                                            @Override
                                            public ThirdLoginParameter call(JsonObject jsonObject) {
                                                thirdLoginParameter.setLoginInfo(jsonObject
                                                        .toString());
                                                return thirdLoginParameter;
                                            }
                                        });
                            case HljThirdLogin.InfoType.BIND:
                                break;
                        }
                        return Observable.just(thirdLoginParameter);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    private Observable<ThirdLoginParameter> weixinLoginObb() {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(final Subscriber<? super String> subscriber) {
                IWXAPI api = WXAPIFactory.createWXAPI(ThirdLoginActivity.this,
                        HljShare.WEIXINKEY,
                        true);
                if (!api.isWXAppInstalled()) {
                    subscriber.onError(new HljApiException(getString(R.string
                            .unfind_weixin___share)));
                    return;
                }
                WXCallbackUtil.getInstance()
                        .registerLoginCallback(new WXCallbackUtil.WXOnCompleteCallbackListener() {
                            @Override
                            public void OnComplete(String code) {
                                subscriber.onNext(code);
                                subscriber.onCompleted();
                            }

                            @Override
                            public void OnError() {
                                subscriber.onError(new HljApiException("登录失败"));
                            }

                            @Override
                            public void OnCancel() {
                                subscriber.onError(new HljApiException("取消登录"));

                            }
                        });
                api.registerApp(HljShare.WEIXINKEY);
                SendAuth.Req req = new SendAuth.Req();
                req.scope = "snsapi_userinfo";
                api.sendReq(req);
            }
        })
                .concatMap(new Func1<String, Observable<? extends JsonObject>>() {
                    @Override
                    public Observable<? extends JsonObject> call(String code) {
                        return ThirdApi.getWeixnAccessToken(code);
                    }
                })
                .concatMap(new Func1<JsonObject, Observable<ThirdLoginParameter>>() {
                    @Override
                    public Observable<ThirdLoginParameter> call(JsonObject jsonObject) {
                        String token = jsonObject.get("access_token")
                                .getAsString();
                        final String openid = jsonObject.get("openid")
                                .getAsString();
                        final String unionid = jsonObject.get("unionid")
                                .getAsString();
                        return ThirdApi.getWeixnUserInfo(token, openid)
                                .map(new Func1<JsonObject, ThirdLoginParameter>() {
                                    @Override
                                    public ThirdLoginParameter call(JsonObject jsonObject) {
                                        if (jsonObject.get("openid") == null) {
                                            jsonObject.addProperty("openid", openid);
                                        }
                                        if (jsonObject.get("unionid") == null) {
                                            jsonObject.addProperty("unionid", unionid);
                                        }
                                        return new ThirdLoginParameter(unionid,
                                                jsonObject.toString(),
                                                loginType);
                                    }
                                });
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == com.tencent.connect.common.Constants.REQUEST_API) {
            if (resultCode == com.tencent.connect.common.Constants.RESULT_LOGIN &&
                    qqLoginListener != null) {
                Tencent.handleResultData(data, qqLoginListener);
            }
        }
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            CommonUtil.unSubscribeSubs(loginSubscription);
        }
    }

    @Override
    protected void onDestroy() {
        CommonUtil.unSubscribeSubs(loginSubscription);
        super.onDestroy();
    }
}
