package com.hunliji.cardmaster.api;

import com.google.gson.JsonObject;
import com.hunliji.cardmaster.utils.DataConfigUtil;
import com.hunliji.hljcommonlibrary.models.customer.CustomerUser;
import com.hunliji.hljcommonlibrary.models.realm.Notification;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;

import java.util.List;
import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by jinxin on 2017/11/27 0027.
 */

public interface CommonService {

    /**
     * 意见反馈
     */
    @POST("p/wedding/index.php/Home/APIFeedBack/feedBack")
    Observable<HljHttpResult> postFeedback(@Body JsonObject body);

    /**
     * 修改用户信息
     *
     * @param params
     * @return
     */
    @POST("p/wedding/index.php/home/APIUser/EditMyBaseInfo")
    Observable<HljHttpResult<CustomerUser>> modifyUserInformation(
            @Body Map<String, Object> params);

    /**
     * 获取通知
     *
     * @param lastId 最后一条通知id
     */
    @GET("p/wedding/index.php/home/APINotification/list")
    Observable<HljHttpResult<List<Notification>>> getNotifications(
            @Query("last_id") long lastId);


    /**
     * 全局配置
     */
    @GET("p/wedding/index.php/home/APISetting/GetAppSetting")
    Observable<DataConfigUtil.DataConfigData> getDataConfig();
}
