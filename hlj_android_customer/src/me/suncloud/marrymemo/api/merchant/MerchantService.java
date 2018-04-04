package me.suncloud.marrymemo.api.merchant;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hunliji.hljcommonlibrary.models.CategoryMark;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.merchant.MerchantChatData;
import com.hunliji.hljcommonlibrary.models.merchant.MerchantRecommendPosterItem;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;

import java.util.List;

import me.suncloud.marrymemo.model.PostScheduleDateBody;
import me.suncloud.marrymemo.model.merchant.wrappers.MerchantChatLinkTriggerPostBody;
import me.suncloud.marrymemo.model.merchant.wrappers.AppointmentPostBody;
import me.suncloud.marrymemo.model.wrappers.HljHttpHotelHallData;
import me.suncloud.marrymemo.model.wrappers.MerchantListData;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

/**
 * 跟商家相关的网络请求操作
 * Created by chen_bin on 2016/11/11 0011.
 */

public interface MerchantService {

    /**
     * 档期查询
     */
    @POST("p/wedding/index.php/home/APIHotelSchedule/add_schedule")
    Observable<HljHttpResult> submitHotelSchedule(
            @Body PostScheduleDateBody postScheduleDateBody);

    /**
     * 商家列表
     */
    @GET
    Observable<HljHttpResult<MerchantListData>> getMerchants(
            @Url String url, @Query("city") long cityId);


    /**
     * 商家问答头信息
     *
     * @param id 商家id
     */
    @GET("p/wedding/index.php/home/APIQaAnswer/merchant_qa")
    Observable<HljHttpResult<Merchant>> getQaMerchatInfo(@Query("merchant_id") long id);

    /**
     * 商家主页获得merchant
     *
     * @param id
     * @return
     */
    @GET("p/wedding/index.php/home/APIMerchant/detailMerchantV2")
    Observable<HljHttpResult<Merchant>> getMerchantInfoV2(@Query("mer_id") long id);

    /**
     * 大图模式列表
     *
     * @param merchantId
     * @return
     */
    @GET("p/wedding/index.php/home/APIMerchant/recommend_poster_list")
    Observable<HljHttpResult<HljHttpData<List<MerchantRecommendPosterItem>>>>
    getMerchantRecommendPosters(
            @Query("merchant_id") long merchantId);

    /**
     * 预约
     *
     * @param body
     * @return
     */
    @POST("p/wedding/index.php/home/APIMerchant/MakeAppointment")
    Observable<HljHttpResult<JsonElement>> makeAppointment(@Body AppointmentPostBody body);

    /**
     * 获取旗舰版配置
     *
     * @param id 商家id
     * @return data<p/>
     * theme	主题 0默认 1白 2黑 3绿 4蓝 5粉
     */
    @GET("p/wedding/index.php/home/APIMerchant/decoration")
    Observable<HljHttpResult<JsonElement>> getMerchantDecoration(@Query("merchant_id") long id);

    /**
     * 获取商家轻松聊内容
     *
     * @param id 商家id
     * @return
     */
    @GET("p/wedding/home/APIMerchantChatlink")
    Observable<HljHttpResult<MerchantChatData>> getMerchantChatData(@Query("merchant_id") long id);

    /**
     * 请求让服务器自动发送商家轻松聊信息
     *
     * @param body
     * @return
     */
    @POST("p/wedding/home/APIMerchantChatlink")
    Observable<HljHttpResult<JsonElement>> postMerchantChatLinkTrigger(
            @Body MerchantChatLinkTriggerPostBody body);

    /**
     * 头部标签组区域
     *
     * @param entityId
     * @return
     */
    @GET("p/wedding/home/APIMark/mark_category_service_head")
    Observable<HljHttpResult<HljHttpData<List<CategoryMark>>>> getMarkCategoryServiceHead(@Query("entity_id") long entityId);

    /**
     * 宴会厅详情
     *
     * @param id
     * @return
     */
    @GET("p/wedding/home/APIHotelHall/info")
    Observable<HljHttpResult<HljHttpHotelHallData>> getHotelHallDetail(@Query("id") long id);

    /**
     * 获取商家筛选菜单
     *
     * @param city
     * @return
     */
    @GET("p/wedding/index.php/home/APIMerchant/merchant_filter")
    Observable<HljHttpResult<JsonObject>> getMerchantFilter(@Query("city") long city);


    /**
     * <a href=http://doc.hunliji.com/workspace/myWorkspace.do?projectId=53#4066>商家公告</a>
     *
     * @param id 商家ID
     * @return
     */
    @GET("p/wedding/Home/APIMerchant/merchant_notice")
    Observable<HljHttpResult<JsonElement>> getMerchantNotice(@Query("merchant_id") long id);


    /**
     * 返回商家电话
     * @param userId 商家userId
     * @return
     */
    @GET("p/wedding/index.php/Home/APIUser/Phone")
    Observable<HljHttpResult<List<String>>> getMerchantPhones(@Query("user_id") long userId);
}