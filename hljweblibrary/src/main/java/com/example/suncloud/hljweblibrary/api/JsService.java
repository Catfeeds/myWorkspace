package com.example.suncloud.hljweblibrary.api;

import com.example.suncloud.hljweblibrary.models.JsInfo;
import com.google.gson.JsonObject;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;

import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by Suncloud on 2016/8/1.
 */
public interface JsService {


    /**
     * 获取js下载信息
     */
    @GET("p/wedding/index.php/home/APISetting/GetAppSetting")
    Observable<JsonObject> getJsInfo();
}
