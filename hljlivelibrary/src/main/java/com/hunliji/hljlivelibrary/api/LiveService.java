package com.hunliji.hljlivelibrary.api;

import com.google.gson.JsonElement;
import com.hunliji.hljcommonlibrary.models.live.LiveChannel;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.entities.HljHttpCountData;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljlivelibrary.models.LiveMessage;
import com.hunliji.hljlivelibrary.models.LiveRelevantWrapper;

import java.util.List;
import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by mo_yu on 16/10/24.
 * 直播模块所有需要的接口定义
 */
public interface LiveService {

    /**
     * 获取直播列表
     *
     * @param perPage
     * @param page
     * @return
     */
    @GET
    Observable<HljHttpResult<HljHttpCountData<List<LiveChannel>>>> getLiveChannelList(
            @Url String url,
            @Query("per_page") int perPage,
            @Query("page") int page,
            @Query("merchant_id") long merchantId);


    /**
     * 直播详情
     *
     * @param id channelId
     * @return
     */
    @GET("p/wedding/index.php/Home/APILiveChannel/info")
    Observable<HljHttpResult<LiveChannel>> getLiveChannel(@Query("id") long id);


    /**
     * 直播历史消息列表
     *
     * @param path 直播所用host不同直接使用Url
     * @return HljHttpData 里面不包含page_count 和total_count
     */
    @GET
    Observable<HljHttpResult<HljHttpData<List<LiveMessage>>>> getLiveHistories(@Url String path);

    /**
     * 获取直播相关列表
     *
     * @param perPage
     * @param page
     * @return
     */
    @GET("p/wedding/index.php/home/APILiveRelateRecommend/info_v2")
    Observable<HljHttpResult<HljHttpData<List<LiveRelevantWrapper>>>> getLiveRelateList(
            @Query("channel_id") long channelId,
            @Query("per_page") int perPage,
            @Query("page") int page);

    /**
     * 获取直播相关商品列表，只有套餐和婚品
     */
    @GET("p/wedding/index.php/home/APILiveRelateRecommend/info_v3")
    Observable<HljHttpResult<HljHttpData<List<LiveRelevantWrapper>>>> getLiveShopList(
            @Query("channel_id") long channelId);

    /**
     * 领取优惠券
     *
     * @param ids
     * @return
     */
    @GET("p/wedding/index.php/home/APIUserCoupon/ReceiveCoupon")
    Observable<HljHttpResult> receiveCoupon(@Query("coupon_id") String ids);

    /**
     * 领取红包
     *
     * @param exchangeCode
     * @return
     */
    @GET("p/wedding/index.php/Shop/APIRedPacket/ExchangeRedPacket")
    Observable<HljHttpResult> receiveRedPacket(@Query("exchangeCode") String exchangeCode);

    /**
     * 领取直播红包
     *
     * @param exchangeCode
     * @return
     */
    @GET("p/wedding/index.php/Shop/APIRedPacket/ExchangeRedPacket")
    Observable<HljHttpResult> receiveRedPacket(
            @Query("exchangeCode") String exchangeCode, @Query("source") String source);

    /**
     * 领取直播红包
     * @param ids
     * @param source
     * @return
     */
    @GET("p/wedding/index.php/home/APIUserCoupon/ReceiveCoupon")
    Observable<HljHttpResult> getLiveCoupon(
            @Query("coupon_id") String ids, @Query("source") String source);

    /**
     * 检测是否收藏了某个商家
     * @param merchantId
     * @return
     */
    @GET("p/wedding/index.php/home/APIMerchant/is_collected")
    Observable<HljHttpResult<JsonElement>> getCollectStatus(@Query("merchant_id") long merchantId);

    /**
     * 预约直播
     * @param map
     * @return
     */
    @POST("p/wedding/Home/APILiveChannel/appointment")
    Observable<HljHttpResult> makeAppointment(@Body Map<String, Object> map);
}
