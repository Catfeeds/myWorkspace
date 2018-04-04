package me.suncloud.marrymemo.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;

import me.suncloud.marrymemo.model.Splash;
import me.suncloud.marrymemo.model.WXWall;
import me.suncloud.marrymemo.model.WeddingConsult;
import me.suncloud.marrymemo.model.main.YouLike;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by jinxin on 2016/11/18 0018.
 */

public interface CustomCommonService {
    /**
     * 猜你喜欢
     *
     * @param only_user 首页传1 买套餐传0 1表示会根据userId 去取数据
     * @return
     */
    @GET("/p/wedding/index.php/Home/APISetMeal/guess_your_like")
    Observable<HljHttpResult<YouLike>> getYouLike(@Query("only_user") int only_user);


    @GET
    Observable<JsonElement> getJson(@Url String url);

    /**
     * 城市列表
     *
     * @param cid 当前城市id，获取附近推荐城市
     * @return
     */
    @GET("p/wedding/index.php/home/APICity/cities")
    Observable<HljHttpResult<JsonObject>> getCities(@Query("relative_id") Long cid);


    /**
     * 意见反馈
     */
    @POST("p/wedding/index.php/Home/APIFeedBack/feedBack")
    Observable<HljHttpResult> postFeedback(@Body JsonObject body);


    /**
     * 保存用户个推id
     */
    @POST("p/wedding/index.php/Home/APIGeTuiUser/SaveClientInfo")
    Observable<HljHttpResult> saveClientInfo(@Body JsonObject body);


    /**
     * 获取启动页
     */
    @GET("p/wedding/index.php/Home/APISplashe/SplasheListV2")
    Observable<HljHttpResult<Splash>> getSplash();


    /**
     * 微信墙信息
     */
    @GET("p/wedding/index.php/home/APIInvation/getWxWallLink")
    Observable<HljHttpResult<WXWall>> getWXWall();

    /**
     * 首页是否点赞接口
     *
     * @return
     */
    @GET("p/wedding/index.php/home/APISetting/CommentApp")
    Observable<JsonElement> commentApp();

    /**
     * 备婚清单获得分类接口
     *
     * @return
     */
    @GET("p/wedding/index.php/home/APIWeddingPreparation/CategoryList")
    Observable<HljHttpResult<JsonElement>> getWeddingPrepareCategoryList();

    /**
     * 备婚清单获得详情接口
     *
     * @param cid
     * @param categoryId
     * @return
     */
    @GET("p/wedding/index.php/home/APIWeddingPreparation/WeddingPreparationIndex")
    Observable<HljHttpResult<JsonElement>> getWeddingPrepareData(
            @Query("cid") long cid, @Query("category_id") long categoryId);


    /**
     * 顾问添加咨询
     *
     * @param jsonObject
     * @return
     */
    @POST("p/wedding/index.php/home/APIAdvHelper/AddBudgetInfo")
    Observable<JsonElement> addBudgetInfo(@Body JsonObject jsonObject);


    @GET("p/wedding/index.php/home/APIAdvHelper/weddingConsult")
    Observable<HljHttpResult<WeddingConsult>> getWeddingConsult(@Query("from") String from);

    /**
     *  获得购物车数量
     */
    @GET("p/wedding/Shop/APIShopCart/list_count")
    Observable<HljHttpResult<JsonElement>> getCartItemsCount();


    @POST("p/wedding/index.php/Home/APIPhone/create")
    Observable<JsonObject> createPhone(@Body JsonObject body);


    @POST("http://aa.hunliji.com/analytics/send")
    Observable<JsonElement> sendAppAnalytics(@Body JsonObject body);
}
