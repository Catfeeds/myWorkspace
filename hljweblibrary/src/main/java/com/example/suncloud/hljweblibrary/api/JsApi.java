package com.example.suncloud.hljweblibrary.api;


import android.text.TextUtils;

import com.example.suncloud.hljweblibrary.models.JsInfo;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hunliji.hljhttplibrary.HljHttp;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Suncloud on 2016/8/1.
 */
public class JsApi {

    public static Observable<JsInfo> getJsInfo() {
        return HljHttp.getRetrofit()
                .create(JsService.class)
                .getJsInfo()
                .map(new Func1<JsonObject, JsInfo>() {
                    @Override
                    public JsInfo call(JsonObject jsonObject) {
                        if (jsonObject==null) {
                            return null;
                        }
                        try {
                            return new Gson().fromJson(jsonObject.getAsJsonObject("config"),JsInfo.class);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
