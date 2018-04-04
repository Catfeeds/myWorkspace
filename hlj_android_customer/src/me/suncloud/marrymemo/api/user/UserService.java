package me.suncloud.marrymemo.api.user;

import com.google.gson.JsonElement;
import com.hunliji.hljcommonlibrary.models.ServiceComment;
import com.hunliji.hljcommonlibrary.models.userprofile.UserPartnerWrapper;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;

import java.util.List;
import java.util.Map;

import me.suncloud.marrymemo.model.Reservation;
import me.suncloud.marrymemo.model.user.CountStatistics;
import me.suncloud.marrymemo.model.user.UserDynamic;
import me.suncloud.marrymemo.model.user.UserStatistics;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by werther on 16/8/26.
 * 用户相关API服务接口
 */
public interface UserService {

    /**
     * 获取用户账户信息
     *
     * @return
     */
    @GET("p/wedding/index.php/Home/APIUser/user_homeV2")
    Observable<HljHttpResult<UserPartnerWrapper>> getUserProfile(@Query("user_id") long userId);

    /**
     * 用户评价列表
     *
     * @param userId
     * @param page
     * @param perPage
     * @return
     */
    @GET("p/wedding/index.php/home/APIOrderComment/list_v2")
    Observable<HljHttpResult<HljHttpData<List<ServiceComment>>>> getUserComments(
            @Query("user_id") long userId, @Query("page") int page, @Query("per_page") int perPage);

    /**
     * 我的预约列表
     *
     * @param propertyId
     * @param page
     * @param perPage
     * @return
     */
    @GET("p/wedding/index.php/home/APIUser/my_merchant_appointment")
    Observable<HljHttpResult<HljHttpData<List<Reservation>>>> getMyReservations(
            @Query("property_id") long propertyId,
            @Query("page") int page,
            @Query("per_page") int perPage);

    /**
     * 修改个人信息
     *
     * @param map
     * @return
     */
    @POST("p/wedding/index.php/home/APIUser/EditMyBaseInfo")
    Observable<HljHttpResult<JsonElement>> editMyBaseInfo(@Body Map<String, Object> map);

    /**
     * 用户信息一些数据的同步
     *
     * @return
     */
    @GET("p/wedding/index.php/home/APIUserUs/user_statistical")
    Observable<HljHttpResult<UserStatistics>> getUserStatistics();

    /**
     * 列表总数
     *
     * @param type
     * @return
     */
    @GET("p/wedding/index.php/home/APIUserUs/list_statistical")
    Observable<HljHttpResult<CountStatistics>> getCountStatistics(@Query("type") String type);


    /**
     * 用户备婚动态未读数量
     *
     * @return
     */
    @GET("p/wedding/index.php/Home/APIUserDynamicNotify/unread_count")
    Observable<HljHttpResult<JsonElement>> getUserDynamicUnreadCount();

    /**
     * 用户备婚动态列表
     *
     * @return
     */
    @GET("p/wedding/index.php/Home/APIUserDynamicNotify/index")
    Observable<HljHttpResult<HljHttpData<List<UserDynamic>>>> getUserDynamics(
            @Query("page") int page, @Query("per_page") int perPage);

    /**
     * 分享App后请求接口增加积分
     * @return
     */
    @POST("p/wedding/index.php/home/APIUser/share_app")
    Observable<HljHttpResult<JsonElement>> afterShareApp();
}
