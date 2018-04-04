package me.suncloud.marrymemo.api.work_case;

import com.hunliji.hljcommonlibrary.models.CasePhoto;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.models.search.ServiceSearchResult;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.suncloud.marrymemo.model.wrappers.WorksData;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * 套餐跟案例的请求
 * Created by chen_bin on 2016/12/6 0006.
 */
public interface WorkService {

    /**
     * 推荐套餐列表
     *
     * @param merchantId
     * @return
     */
    @GET("p/wedding/index.php/home/APISetMeal/recommend_meal_list")
    Observable<HljHttpResult<HljHttpData<List<Work>>>> getMerchantRecommendWorks(
            @Query("merchant_id") long merchantId,
            @Query("page") int page,
            @Query("per_page") int perPage);

    /**
     * 套餐列表
     *
     * @param id
     * @param kind
     * @param sort
     * @param page
     * @param perPage
     * @return
     */
    @GET("p/wedding/index.php/home/APISetMeal/list")
    Observable<HljHttpResult<HljHttpData<List<Work>>>> getMerchantWorksAndCases(
            @Query("id") long id,
            @Query("kind") String kind,
            @Query("sort") String sort,
            @Query("page") int page,
            @Query("per_page") int perPage);


    /**
     * 案例列表
     *
     * @param perPage 每页返回数
     * @param page    当前页
     * @param queries 筛选参数
     *                <p>
     *                first_query_time 时间戳
     *                sort[key] 排序字段 1
     *                sort[order] 排序字段 2
     *                city 城市id
     *                property 分类
     *                category_id 二级分类
     *                </p>
     * @return WorksData 套餐案例临时列表
     */
    @GET("p/wedding/index.php/home/APIMerchant/WorkList?commodity_type=1")
    Observable<WorksData> getCases(
            @Query("per_page") int perPage,
            @Query("page") int page,
            @QueryMap Map<String, String> queries);

    /**
     * 套餐列表
     *
     * @param cityCode
     * @param propertyId
     * @param categoryId
     * @param filterMap
     * @param keyword
     * @param sort
     * @param page
     * @param perPage
     * @return
     */
    @GET("p/wedding/Home/APISearchV2/set_meal")
    Observable<HljHttpResult<ServiceSearchResult>> getWorks(
            @Query("city_code") long cityCode,
            @Query("property_id") long propertyId,
            @Query("category_id") Long categoryId,
            @QueryMap HashMap<String, String> filterMap,
            @Query("keyword") String keyword,
            @Query("sort") String sort,
            @Query("page") int page,
            @Query("per_page") int perPage);

    /**
     * 商家列表
     *
     * @param propertyId
     * @return
     */
    @GET("/p/wedding/home/APIMerchant/merchant_list")
    Observable<HljHttpResult<HljHttpData<List<Merchant>>>> getMerchantList(
            @Query("property_id") long propertyId);

    /**
     * cpm商家列表
     *
     * @return
     */
    @GET("/p/wedding/home/APIMerchant/cpmList")
    Observable<HljHttpResult<HljHttpData<List<Merchant>>>> getCPMMerchantList(
            @Query("property_id") long propertyId);

    /**
     * 微旅拍列表
     * @param type
     * @return
     */
    @GET("p/wedding/index.php/Home/APISetMeal/micro_travel_list")
    Observable<HljHttpResult<HljHttpData<List<Work>>>> getMicroTravelList(@Query("type") int type);

    /**
     * 老接口，获取推荐套餐列表
     * @param cid
     * @param id workId
     * @return
     */
    @GET("p/wedding/index.php/home/APIMerchant/GetRecommendMeals")
    Observable<WorksData> getRecommendMeals(
            @Query("cid") long cid,
            @Query("id") long id);

    /**
     * 获取99抢旅拍的套餐列表
     * @return
     */
    @GET("p/wedding/index.php/Home/APISetMeal/grab_travel")
    Observable<HljHttpResult<HljHttpData<List<Work>>>> getNinetyNineWorks();

    /**
     * 获得套餐详情 相关客照列表
     *
     * @return
     */
    @GET("/p/wedding/Home/APISetMealRelation/relative_examples")
    Observable<HljHttpResult<HljHttpData<List<CasePhoto>>>> getCasePhotoList(@Query("set_meal_id") long setMealId);

    /**
     * 客照详情
     *
     * @param casePhotoId 客照（案例）ID
     * @return
     */
    @GET("/p/wedding/Home/APISetMealRelation/case_photo_detail")
    Observable<HljHttpResult<CasePhoto>> getCasePhotoDetail(@Query("case_photo_id") long casePhotoId);

    /**
     * 客照收藏
     * @param id case_photo_id
     * @return
     */
    @POST("p/wedding/home/APIMerchant/collectors/id/{case_photo_id}")
    Observable<Object> postCaseCollect(@Path("case_photo_id") long id);

}