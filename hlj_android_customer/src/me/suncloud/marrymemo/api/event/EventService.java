package me.suncloud.marrymemo.api.event;


import com.google.gson.JsonElement;
import com.hunliji.hljcommonlibrary.models.event.EventInfo;
import com.hunliji.hljcommonlibrary.models.event.SignUpInfo;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;

import java.util.List;
import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by chen_bin on 2016/9/9 0009.
 */
public interface EventService {

    /**
     * 首页和发现页活动列表
     *
     * @param position 1首页 2发现页
     * @return
     */
    @GET("/p/wedding/index.php/Home/APIFinderActivity/rand_activity_result")
    Observable<HljHttpResult<HljHttpData<List<EventInfo>>>> getEventRandom(
            @Query("position") int position);

    /**
     * 活动列表
     *
     * @param page
     * @param perPage
     * @return
     */
    @GET("p/wedding/index.php/Home/APIFinderActivity/index")
    Observable<HljHttpResult<HljHttpData<List<EventInfo>>>> getEventList(
            @Query("page") int page, @Query("per_page") int perPage);

    /**
     * 活动详情
     *
     * @param id
     * @return
     */
    @GET("p/wedding/index.php/Home/APIFinderActivity/detail")
    Observable<HljHttpResult<EventInfo>> getEventDetail(@Query("id") long id);

    /**
     * 我参加的活动列表
     *
     * @param page
     * @param perPage
     * @return
     */
    @GET("p/wedding/index.php/home/APIFinderActivity/myActivities")
    Observable<HljHttpResult<HljHttpData<List<EventInfo>>>> getMyEventList(
            @Query("page") int page, @Query("per_page") int perPage);

    /**
     * 活动报名
     *
     * @param map
     * @return
     */
    @POST("p/wedding/index.php/Home/APIFinderActivity/signUp")
    Observable<HljHttpResult<SignUpInfo>> signUp(@Body Map<String, Object> map);

    /**
     * 中奖列表
     *
     * @return
     */
    @GET("p/wedding/index.php/home/APIFinderActivity/winners")
    Observable<HljHttpResult<HljHttpData<List<SignUpInfo>>>> getWinnerList(
            @Query("id") long id, @Query("page") int page, @Query("per_page") int perPage);

    /**
     * 投诉
     *
     * @param map
     * @return
     */
    @POST("p/wedding/index.php/home/APIFinderActivity/report")
    Observable<HljHttpResult> report(@Body Map<String, Object> map);

    /**
     * 检测手机号是否真实
     *
     * @param map
     * @return
     */
    @POST("p/wedding/index.php/Home/APIUser/check_phone_is_real")
    Observable<HljHttpResult<JsonElement>> checkPhoneIsReal(@Body Map<String, Object> map);
}
