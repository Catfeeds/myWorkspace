package me.suncloud.marrymemo.api.product;

import com.google.gson.JsonElement;
import com.hunliji.hljcommonlibrary.models.CategoryMark;
import com.hunliji.hljcommonlibrary.models.Label;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.TopicUrl;
import com.hunliji.hljcommonlibrary.models.coupon.CouponInfo;
import com.hunliji.hljcommonlibrary.models.product.ProductTopic;
import com.hunliji.hljcommonlibrary.models.product.ShopProduct;
import com.hunliji.hljcommonlibrary.models.product.wrappers.FreeShippingFeeWrapper;
import com.hunliji.hljcommonlibrary.models.search.ProductSearchResult;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;

import java.util.List;
import java.util.Map;

import me.suncloud.marrymemo.model.Category;
import me.suncloud.marrymemo.model.orders.ProductOrderExpressInfo;
import me.suncloud.marrymemo.model.shoppingcard.ShoppingCartGroup;

import com.hunliji.hljcardcustomerlibrary.models.RedPacket;

import me.suncloud.marrymemo.model.wrappers.HljHttpMarksData;
import me.suncloud.marrymemo.model.wrappers.ProductCartBody;
import me.suncloud.marrymemo.model.wrappers.ProductCollect;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by Administrator on 2016/11/11.
 */

public interface ProductService {

    /**
     * 商品详情
     *
     * @param id  商品id
     * @param cid 城市id 运费判断
     */
    @GET("p/wedding/index.php/shop/APIShopProduct/info")
    Observable<HljHttpResult<ShopProduct>> getShopProduct(
            @Query("id") long id, @Query("cid") long cid);

    /**
     * 商品详情
     *
     * @param mer_id 商家mer_id
     */
    @GET("p/wedding/index.php/home/APIMerchant/detailMerchantV2")
    Observable<HljHttpResult<Merchant>> getDetailMerchant(
            @Query("mer_id") long mer_id);

    /**
     * 取消收藏
     *
     * @param collect id {"id":id};
     * @return 收藏结果 {"action":"delete"} ;
     */
    @POST("p/wedding/index.php/shop/APIShopProduct/collect_delete")
    Observable<HljHttpResult<ProductCollect>> cancelCollect(@Body ProductCollect collect);

    /**
     * 收藏
     *
     * @param collect id {"id":id};
     * @return 收藏结果 {"action":"insert"} ;
     */
    @POST("p/wedding/index.php/shop/APIShopProduct/collect")
    Observable<HljHttpResult<ProductCollect>> collect(@Body ProductCollect collect);

    @POST("p/wedding/index.php/Shop/APIShopCart/cart")
    Observable<HljHttpResult<ProductCartBody>> addCart(@Body ProductCartBody cart);

    /**
     * chen_bin
     * 婚品分类列表
     *
     * @return
     */
    @GET("p/wedding/index.php/Shop/APIShopProduct/shop_category")
    Observable<HljHttpResult<HljHttpData<List<Category>>>> getProductCategories();

    /**
     * 婚品频道tab列表
     *
     * @return
     */
    @GET("p/wedding/home/APISubPageShop")
    Observable<HljHttpResult<HljHttpData<List<Label>>>> getProductLabels(@Query("type") int type);

    /**
     * 婚品专题列表跟
     *
     * @param page
     * @param perPage
     * @return
     */
    @GET("p/wedding/index.php/Home/APISubPageShopProduct/SubPageList?category=1")
    Observable<HljHttpResult<HljHttpData<List<ProductTopic>>>> getProductSubPages(
            @Query("page") int page, @Query("per_page") int perPage);

    /**
     * 婚品专题标签页列表
     *
     * @param mark_id
     * @param page
     * @param perPage
     * @return
     */
    @GET("p/wedding/index.php/Home/APISubPageShopProduct/SubPageList")
    Observable<HljHttpResult<HljHttpMarksData<List<ProductTopic>>>> getProductSubPagesByMarkId(
            @Query("mark_id") long mark_id,
            @Query("page") int page,
            @Query("per_page") int perPage);

    /**
     * 婚品专题详情
     *
     * @param id
     * @return
     */
    @GET("p/wedding/index.php/home/APISubPageShopProduct/sub_page_detail")
    Observable<HljHttpResult<TopicUrl>> getProductSubPageDetail(@Query("id") long id);

    /**
     * 获得婚品分类
     *
     * @return
     */
    @GET
    Observable<HljHttpResult<JsonElement>> getProductProperty(@Url String url);

    /**
     * 获得婚品分类页 专题
     *
     * @return
     */
    @GET
    Observable<HljHttpResult<Map<Long, ProductTopic>>> getProductSubPage(@Url String url);

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
     * 婚品分类详情页 婚品列表
     *
     * @return
     */
    @GET("p/wedding/index.php/home/APISearchV2/list")
    Observable<HljHttpResult<ProductSearchResult>> getCategoryDetailProductList(
            @QueryMap Map<String, String> filterMap,
            @Query("type") int type,
            @Query("sort") String order,
            @Query("page") int page);

    /**
     * 获得购物车列表
     * 为使用老model 手动解析数据
     *
     * @return
     */
    @GET("p/wedding/Shop/APIShopCart/list_v2")
    Observable<HljHttpResult<List<ShoppingCartGroup>>> getShoppingCartItem();

    /**
     * @param type 婚品推荐列表(1:用户行为 2:购物车 3:支付成功)
     * @return
     */
    @GET("p/wedding/index.php/Shop/APIShopProduct/userRecommendProduct")
    Observable<HljHttpResult<HljHttpData<List<ShopProduct>>>> getUserRecommendProduct(
            @Query("type") int type);

    /**
     * 获取婚礼购首页的人气推荐列表，与上面这个接口一样，但是需要分页
     *
     * @param page
     * @return
     */
    @GET("p/wedding/index.php/Shop/APIShopProduct/userRecommendProduct?type=1&per_page=20")
    Observable<HljHttpResult<HljHttpData<List<ShopProduct>>>> getHomeRecommendProduct(
            @Query("page") int page);

    /**
     * 获得订单物流状态信息
     *
     * @param id
     * @return
     */
    @GET("p/wedding/index.php/shop/APIShopOrder/Express")
    Observable<HljHttpResult<ProductOrderExpressInfo>> getOrderExpressInfo(@Query("id") long id);

    /**
     * 婚礼购首页每周上新
     *
     * @param page
     * @param perPage
     * @return
     */
    @GET("p/wedding/index.php/Home/APISubPageShop/week_new_product")
    Observable<HljHttpResult<HljHttpData<List<ShopProduct>>>> getWeekNewProducts(
            @Query("page") int page, @Query("per_page") int perPage);

    /*
     * 根据地址返回邮费
     * @return
     */
    @GET("p/wedding/index.php/shop/APIShopProduct/product_express")
    Observable<HljHttpResult<FreeShippingFeeWrapper>> getFreeShippingFee(
            @Query("address_id") long addressId, @Query("id") long id);

    /**
     * 获取婚品商家优惠券列表
     * @param id
     * @return
     */
    @GET("p/wedding/index.php/Home/APIMerchant/merchant_coupons")
    Observable<HljHttpResult<HljHttpData<List<CouponInfo>>>> getProductMerchantCoupons(@Query("merchant_id") long id);

}