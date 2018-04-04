package com.hunliji.hljkefulibrary.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hunliji.hljkefulibrary.moudles.SupportListData;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by wangtao on 2017/10/20.
 */

public interface KeFuService {

    @GET("p/wedding/index.php/Home/APIUser/UserBaseInfo")
    Observable<JsonObject> getHxUser();


    @GET("https://kefu.easemob.com/v1/Tenants/{tenantId}/robots/visitor/greetings/app")
    Observable<JsonObject> getGreeting(@Path("tenantId")String tenantId);


    @GET("p/wedding/index.php/home/APIManagers/Supports/kind")
    Observable<JsonObject> getSupports();


    /**
     * <a href="http://doc.hunliji.com/workspace/myWorkspace.do?projectId=60#4257">给用户发送回复</a>*
     */
    @POST("p/wedding/index.php/Home/APIHxUser/reply")
    Observable<JsonElement> postReply(@Body JsonObject body);
}
