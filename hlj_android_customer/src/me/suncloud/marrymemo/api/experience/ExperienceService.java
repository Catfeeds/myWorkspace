package me.suncloud.marrymemo.api.experience;

import com.hunliji.hljcommonlibrary.models.Comment;
import com.hunliji.hljcommonlibrary.models.event.EventInfo;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.entities.HljResultAction;

import java.util.List;

import me.suncloud.marrymemo.model.experience.ExperiencePhoto;
import me.suncloud.marrymemo.model.experience.ExperienceReservationBody;
import me.suncloud.marrymemo.model.experience.ExperienceShop;
import me.suncloud.marrymemo.model.experience.Planner;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * 体验店
 * Created by jinxin on 2016/10/28.
 */

public interface ExperienceService {

    /**
     * 体验店入口
     *
     * @return
     */
    @GET("/p/wedding/index.php/home/APITestStoreAtlas")
    Observable<HljHttpResult<ExperienceShop>> getStoreAtlas(@Query("store_id") long id);

    /**
     * 体验店全部印象里面的评论列表
     *
     * @param test_store_id 体验店id
     * @return
     */
    @GET("/p/wedding/index.php/home/APITestStoreOrganizer/test_store_and_organizer_comment")
    Observable<HljHttpResult<HljHttpData<List<Comment>>>> getMerchantCommentList(
            @Query("page") int page,
            @Query("per_page") int per_page,
            @Query("store_id") long test_store_id);

    /**
     * 获取统筹师详情
     *
     * @param id 统筹师Id
     * @return
     */
    @GET("p/wedding/Home/APITestStoreOrganizer/detail")
    Observable<HljHttpResult<Planner>> getPlannerDetail(
            @Query("id") long id);

    /**
     * 全部活动
     *
     * @return
     */
    @GET("/p/wedding/index.php/home/APITestStoreAtlas/list")
    Observable<HljHttpResult<HljHttpData<List<EventInfo>>>> getEventList(@Query("store_id") long id,
            @Query("page") int page, @Query("per_page") int perPage);

    /**
     * 全部店铺照片
     *
     * @param id
     * @return
     */
    @GET("p/wedding/index.php/home/APITestStoreAtlas/atlasItems")
    Observable<HljHttpResult<HljHttpData<List<ExperiencePhoto>>>> getShopPhoto(
            @Query("atlas_id") long id, @Query("page") int page, @Query("per_page") int perPage);

    /**
     * 预约
     *
     * @param body
     * @return
     */
    @POST("p/wedding/index.php/Home/APIActivityAppointment/test_store_sign_up")
    Observable<HljHttpResult<HljResultAction>> reservation(@Body ExperienceReservationBody body);


    /**
     * 获得统筹师列表(带搜索)
     *
     * @param fullName
     * @return
     */
    @GET("p/wedding/Home/APITestStoreOrganizer/index")
    Observable<HljHttpResult<HljHttpData<List<Planner>>>> getPlannerList(
            @Query("store_id") long id,
            @Query("_fullname") String fullName);


    /**
     * 获得体验店评价
     *
     * @param id 体验店id 杭州传1
     * @return
     */
    @GET("p/wedding/index.php/home/APITestStoreOrganizer/test_store_and_organizer_comment")
    Observable<HljHttpResult<HljHttpData<List<Comment>>>> getComments(@Query("store_id")
                                                                              long id);

    /**
     * 获取统筹师评论列表
     *
     * @param entity_id
     * @param entity_type
     * @param page
     * @param per_page
     * @return
     */
    @GET("/p/wedding/index.php/Home/APICommunityComment/comment_list")
    Observable<HljHttpResult<HljHttpData<List<Comment>>>> getPlannerCommentList(
            @Query("entity_id") long entity_id,
            @Query("entity_type") String entity_type,
            @Query("page") int page,
            @Query("per_page") int per_page);


}
