package com.hunliji.hljcarlibrary.api;


import com.google.gson.JsonElement;
import com.hunliji.hljcarlibrary.models.Brand;
import com.hunliji.hljcarlibrary.models.CarFilter;
import com.hunliji.hljcarlibrary.models.CarLesson;
import com.hunliji.hljcarlibrary.models.CarMerchantContactInfo;
import com.hunliji.hljcarlibrary.models.HljCarHttpData;
import com.hunliji.hljcarlibrary.models.HljHttpCommentsData;
import com.hunliji.hljcarlibrary.models.SecKill;
import com.hunliji.hljcommonlibrary.models.CommentMark;
import com.hunliji.hljcommonlibrary.models.questionanswer.Question;
import com.hunliji.hljcommonlibrary.models.wedding_car.WeddingCarComment;
import com.hunliji.hljcommonlibrary.models.wedding_car.WeddingCarProduct;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;

import java.util.List;
import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by mo_yu on 2017/12/26.婚车相关请求
 */

public interface WeddingCarService {

    /**
     * 获得婚车详情
     *
     * @param id
     * @return
     */
    @GET("p/wedding/index.php/Car/APICarProduct/info")
    Observable<HljHttpResult<WeddingCarProduct>> getWeddingCarProductDetail(@Query("id") long id);

    /**
     * 获得婚车评价
     *
     * @param merchantId
     * @param page
     * @param perPage
     * @return
     */
    @GET("p/wedding/index.php/Home/APIOrderComment/merchantCommentList")
    Observable<HljHttpResult<HljHttpData<List<WeddingCarComment>>>> getWeddingCarProductComment(
            @Query("merchant_id") long merchantId,
            @Query("page") int page,
            @Query("per_page") int perPage);


    /**
     * 获取婚车热门品牌
     *
     * @return
     */
    @GET("p/wedding/index.php/Car/APICarProduct/PopularBrands")
    Observable<HljHttpResult<List<Brand>>> getHotBrands();

    /**
     * 获取婚车筛选选项
     *
     * @return
     */
    @GET("p/wedding/index.php/Car/APICarProduct/FilterList")
    Observable<HljHttpResult<CarFilter>> getCarFilters(
            @Query("cid") long cid, @Query("tab") String tab);

    /**
     * 精选婚车套餐列表
     *
     *                  <p>
     *                  product_brand_id   婚车品牌
     *                  sort      排序条件
     *                  order     排序方式
     * @return
     */
    @GET
    Observable<HljHttpResult<HljHttpData<List<WeddingCarProduct>>>> getHotCarMeals(
            @Url String url);

    /**
     * 自选婚车列表
     *
     *                  <p>
     *                  color_title     颜色筛选
     *                  product_brand_id   婚车品牌
     *                  sort      排序条件
     *                  order     排序方式
     * @return
     */
    @GET
    Observable<HljHttpResult<HljHttpData<List<WeddingCarProduct>>>> getSelfCars(
            @Url String url);

    /**
     * 预约看车
     *
     * @param params
     * @return
     */
    @POST("p/wedding/index.php/home/APIMerchant/MakeAppointment")
    Observable<HljHttpResult> orderCar(@Body Map<String, Object> params);

    /**
     * 婚车必修课列表
     *
     * @return
     */
    @GET("p/wedding/Car/APICarLesson/List")
    Observable<HljHttpResult<HljHttpData<List<CarLesson>>>> getCarLessons(
            @Query("per_page") int perPage, @Query("page") int pageCount);

    /**
     * 特价秒杀列表
     *
     * @return
     */
    @GET("p/wedding/Car/APICarProduct/MiaoShaList")
    Observable<HljHttpResult<HljCarHttpData<List<SecKill>>>> getSecKills(
            @Query("city_code") long cid,
            @Query("per_page") int perPage,
            @Query("page") int pageCount);

    /**
     * 获取婚车商家联系信息
     *
     * @param cid
     * @return
     */
    @GET("p/wedding/Car/APICarProduct/MerchantContactInfo")
    Observable<HljHttpResult<CarMerchantContactInfo>> getCarMerchantContactInfo(
            @Query("city_code") long cid);

    /**
     * 婚车评价标签列表（新）
     *
     * @param merchantId
     * @return
     */
    @GET("/p/wedding/Car/APICarOrderComment/Tags")
    Observable<HljHttpResult<HljHttpData<List<CommentMark>>>>
    getWeddingCarMarks(
            @Query("merchant_id") long merchantId);

    /**
     * 婚车评论列表
     *
     * @param merchantId
     * @param markId
     * @param page
     * @param perPage
     * @return
     */
    @GET("p/wedding/Car/APICarOrderComment/MerchantCommentList")
    Observable<HljHttpResult<HljHttpCommentsData>> getMerchantComments(
            @Query("merchant_id") long merchantId,
            @Query("tag_id") long markId,
            @Query("page") int page,
            @Query("per_page") int perPage);

    /**
     * 婚车商家 请求让服务器自动发送商家轻松聊信息
     *
     * @param body
     * @return
     */
    @POST("p/wedding/home/APIMerchantChatlink")
    Observable<HljHttpResult<JsonElement>> postMerchantChatLinkTrigger(
            @Body Map<String,Object> body);
}
