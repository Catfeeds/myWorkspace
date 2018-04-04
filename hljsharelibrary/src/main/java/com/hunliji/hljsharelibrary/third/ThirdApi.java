package com.hunliji.hljsharelibrary.third;

import com.google.gson.JsonObject;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljsharelibrary.HljShare;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ThirdApi {

    public static Observable<JsonObject> getWeixnAccessToken(String code){
        return HljHttp.getRetrofit()
                .create(ThirdService.class)
                .getWeixnAccessToken(HljShare.WEIXINKEY,HljShare.WEIXINSECRET,code)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<JsonObject> getWeixnUserInfo(String token, final String openid){
        return HljHttp.getRetrofit()
                .create(ThirdService.class)
                .getWeixnUserInfo(token,openid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    public static Observable<JsonObject> getWeiboUserInfo(String token,String uid){
        return HljHttp.getRetrofit()
                .create(ThirdService.class)
                .getWeiboUserInfo(token,uid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
