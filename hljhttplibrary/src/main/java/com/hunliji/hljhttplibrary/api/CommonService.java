package com.hunliji.hljhttplibrary.api;

import com.google.gson.JsonElement;
import com.hunliji.hljcommonlibrary.models.CategoryMark;
import com.hunliji.hljcommonlibrary.models.CertifyCodeMsg;
import com.hunliji.hljcommonlibrary.models.Comment;
import com.hunliji.hljcommonlibrary.models.PostCollectBody;
import com.hunliji.hljcommonlibrary.models.PostCommentBody;
import com.hunliji.hljcommonlibrary.models.PostIdBody;
import com.hunliji.hljcommonlibrary.models.PostPraiseBody;
import com.hunliji.hljcommonlibrary.models.RepliedComment;
import com.hunliji.hljcommonlibrary.models.modelwrappers.ChildrenArea;
import com.hunliji.hljcommonlibrary.models.modelwrappers.ParentArea;
import com.hunliji.hljcommonlibrary.models.modelwrappers.PosterData;
import com.hunliji.hljcommonlibrary.models.product.ShopCategory;
import com.hunliji.hljcommonlibrary.models.product.ShopProduct;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;

import java.util.List;
import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;
import rx.Observable;

/**
 * 请求
 * Created by chen_bin on 2016/11/18 0018.
 */

public interface CommonService {

    /**
     * 通用点赞APICommunityPraise
     *
     * @param body
     * @return
     */
    @POST("p/wedding/index.php/Home/APICommunityPraise/like")
    Observable<HljHttpResult> postPraise(@Body PostPraiseBody body);

    /**
     * 通用收藏
     *
     * @param url
     * @param body
     * @return
     */
    @POST
    Observable<HljHttpResult> postCollect(@Url String url, @Body PostCollectBody body);

    /**
     * 通用添加评论
     *
     * @param body
     * @return
     */
    @POST("p/wedding/index.php/Home/APICommunityComment/addFunc")
    Observable<HljHttpResult<RepliedComment>> addFunc(@Body PostCommentBody body);

    /**
     * 通用删除
     *
     * @param postIdBody
     * @return
     */
    @POST("p/wedding/index.php/Home/APICommunityComment/deleteFunc")
    Observable<HljHttpResult> deleteFunc(@Body PostIdBody postIdBody);

    /**
     * 商家婚品分类
     *
     * @param merchantId
     * @return
     */
    @GET("p/wedding/index.php/shop/APIShopCategory/merchant_category_list")
    Observable<HljHttpResult<HljHttpData<List<ShopCategory>>>> getMerchantProductCategories
    (@Query("merchant_id") long merchantId);

    /**
     * 婚品列表
     *
     * @param url
     * @param page
     * @param perPage
     * @return
     */
    @GET
    Observable<HljHttpResult<HljHttpData<List<ShopProduct>>>> getProducts(
            @Url String url, @Query("page") int page, @Query("per_page") int perPage);

    /**
     * 通用评论列表（热门）
     *
     * @return
     */
    @GET("p/wedding/index.php/home/APICommunityComment/hot_list")
    Observable<HljHttpResult<HljHttpData<List<RepliedComment>>>> getHotComments(
            @Query("entity_id") long entityId, @Query("entity_type") String entityType);


    /**
     * 通用评论列表（最新）
     *
     * @return
     */
    @GET("p/wedding/index.php/home/APICommunityComment/latest_list")
    Observable<HljHttpResult<HljHttpData<List<Comment>>>> getLatestComments(
            @Query("entity_id") long entityId,
            @Query("entity_type") String entityType,
            @Query("page") int page,
            @Query("per_page") int per_page);

    /**
     * 通用评论列表
     *
     * @return
     */
    @GET("p/wedding/index.php/home/APICommunityComment/latest_list")
    Observable<HljHttpResult<HljHttpData<List<RepliedComment>>>> getCommonComments(
            @Query("entity_id") long entityId,
            @Query("entity_type") String entityType,
            @Query("page") int page,
            @Query("per_page") int per_page);

    /**
     * 获得banner
     *
     * @param id
     * @param version
     * @param city
     * @return
     */
    @GET("p/wedding/index.php/home/APIPosterBlock/block_info")
    Observable<HljHttpResult<PosterData>> getBanner(
            @Query("id") long id, @Query("app_version") String version, @Query("city") long city);

    /**
     * 关注或取消用户
     *
     * @param map user_id
     * @return
     */
    @POST("p/wedding/index.php/home/APISubscription/focus")
    Observable<HljHttpResult> followOrUnfollowUser(@Body Map<String, Object> map);


    /**
     * 举报
     *
     * @param map id,kind,message
     * @return
     */
    @POST("p/wedding/index.php/home/APICommunityReport/community_report")
    Observable<HljHttpResult> report(@Body Map<String, Object> map);

    /**
     * 本地服务底部筛选标签组区域
     *
     * @return
     */
    @GET("p/wedding/home/APIMark/mark_category_service_bottom")
    Observable<HljHttpResult<HljHttpData<List<CategoryMark>>>> getServiceMarks(@QueryMap Map<String,String> params);

    /**
     * 婚品标签
     *
     * @param entityId
     * @return
     */
    @GET("p/wedding/shop/APIShopProduct/shop_product_tags")
    Observable<HljHttpResult<HljHttpData<List<CategoryMark>>>> getShopProductTags(
            @Query("entity_id") long entityId);


    /**
     * 通过cid获取所在省信息
     *
     * @param cid
     * @return
     */
    @GET("p/wedding/home/APICity/city_detail")
    Observable<HljHttpResult<ParentArea>> getCityDetail(@Query("cid") long cid);


    /**
     * 通过省获取下属城市信息
     *
     * @param pid
     * @return
     */
    @GET("p/wedding/Shop/APIShopAddress/AllAddress")
    Observable<HljHttpResult<List<ChildrenArea>>> getChildrenCities(@Query("pid") long pid);

    /**
     * 关注商家
     *
     * @param map
     * @return
     */
    @POST("p/wedding/index.php/shop/APIProduct/focus_merchant")
    Observable<HljHttpResult> postMerchantFollow(@Body Map<String, Object> map);

    /**
     * 取消商家关注
     *
     * @return
     */
    @DELETE("p/wedding/index.php/shop/APIProduct/focus_merchant")
    Observable<HljHttpResult> deleteMerchantFollow(@Query("merchant_id") long id);

    /**
     * 获取省市区数据
     *
     * @return
     */
    @GET("p/wedding/Shop/APIShopAddress/AllAddress")
    Observable<HljHttpResult<List<ChildrenArea>>> getAllAddress();

    /**
     * 获得验证码
     *
     * @param map
     * @return
     */
    @POST("p/wedding/index.php/Home/APIUser/sendMsg")
    Observable<HljHttpResult<CertifyCodeMsg>> getCertifyCode(@Body Map<String,Object> map);

    /**
     * 通用验证码预验证接口
     *
     * @return
     */
    @POST("p/wedding/index.php/Home/APIUser/pre_check_sms_code")
    Observable<HljHttpResult> preCheckSmsCode(@Body Map<String,Object> map);

    /**
     * 获取系统维护状态
     * @param url
     * @return
     */
    @GET
    Observable<HljHttpResult<JsonElement>> getServiceState(@Url String url);

    /**
     * APICommunityPostPraise
     *
     * @param map
     * @return
     */
    @POST("p/wedding/index.php/Home/APICommunityPostPraise/PostPraises")
    Observable<HljHttpResult> postThreadPraise(@Body Map<String,Object> map);

    /**
     * 修改绑定手机号
     *
     * @return
     */
    @POST("p/wedding/Home/APIUser/ModifyBindPhone")
    Observable<HljHttpResult> modifyBindPhone(@Body Map<String,Object> map);

}