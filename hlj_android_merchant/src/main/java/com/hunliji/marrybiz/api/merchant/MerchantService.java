package com.hunliji.marrybiz.api.merchant;

import com.google.gson.JsonElement;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.marrybiz.model.merchant.DescriptionLink;
import com.hunliji.marrybiz.model.merchant.MerchantUpgradeInfo;
import com.hunliji.marrybiz.model.merchant.PostWorkBody;
import com.hunliji.marrybiz.model.wrapper.RecommendWork;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.marrybiz.model.merchant.MerchantNotice;
import com.hunliji.marrybiz.model.merchant.MerchantNoticePostBody;
import com.hunliji.marrybiz.model.merchant.MerchantPrivilegeRecord;
import com.hunliji.marrybiz.model.merchant.Reservation;
import com.hunliji.marrybiz.model.merchant.ShopInfo;
import com.hunliji.marrybiz.model.wrapper.WorkInfoData;

import java.util.List;
import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by chen_bin on 2016/9/13 0013.
 */
public interface MerchantService {

    /**
     * 商家客户端主页的一些统计信息
     *
     * @return
     */
    @GET("/p/wedding/index.php/Admin/APIMerchant/shopInfo?is_app=1")
    Observable<HljHttpResult<ShopInfo>> getShopInfo();


    /**
     * 检查是否绑定
     *
     * @return is_bind    1已绑 0未绑
     */
    @GET("p/wedding/index.php/admin/APIMerchant/check_bind")
    Observable<HljHttpResult<JsonElement>> checkedBindWechat();


    /**
     * 绑定微信
     *
     * @param code 微信code
     */
    @FormUrlEncoded
    @POST("p/wedding/index.php/admin/APIMerchant/bind_wechat")
    Observable<HljHttpResult<JsonElement>> bindWechat(@Field("code") String code);


    /**
     * 解绑无需参数
     */
    @POST("p/wedding/index.php/admin/APIMerchant/release_bind")
    Observable<HljHttpResult<JsonElement>> releaseBindWechat();

    /**
     * 预约管理列表
     *
     * @param page
     * @param perPage
     * @return
     */
    @GET("p/wedding/index.php/Admin/APIAppointment/index")
    Observable<HljHttpResult<HljHttpData<List<Reservation>>>> getReservationList(
            @Query("page") int page, @Query("per_page") int perPage);

    /**
     * 预约管理联系
     *
     * @param body
     * @return
     */
    @POST("p/wedding/index.php/Admin/APIAppointment/edit")
    Observable<HljHttpResult> editReservationStatus(@Body Reservation body);

    /**
     * 商家修改到店预约
     *
     * @param body
     * @return
     */
    @POST("p/wedding/Admin/APIMerchantCalender/edit_appointment")
    Observable<HljHttpResult> editAppointment(@Body Reservation body);


    /**
     * 套餐和案例列表
     *
     * @param id
     * @param kind
     * @param sort
     * @param page
     * @param perPage
     * @return
     */
    @GET("p/wedding/index.php/home/APISetMeal/list")
    Observable<HljHttpResult<HljHttpData<List<Work>>>> getMerchantMealsAndCases(
            @Query("id") long id,
            @Query("kind") String kind,
            @Query("sort") String sort,
            @Query("page") int page,
            @Query("per_page") int perPage);


    /**
     * 套餐和案例列表详情
     *
     * @param id 套餐或案例id
     * @return
     */
    @GET("p/wedding/index.php/home/APISetMeal/info/id/{id}?admin=1")
    Observable<HljHttpResult<WorkInfoData>> getWorkInfo(@Path("id") long id);

    /**
     * 商家推荐套餐列表
     *
     * @return
     */
    @GET("p/wedding/index.php/Admin/APIMerchantRecommendMeal/lists")
    Observable<HljHttpResult<HljHttpData<List<RecommendWork>>>> getRecommendWorks();

    /**
     * 取消推荐
     *
     * @param body
     * @return
     */
    @POST("p/wedding/index.php/Admin/APIMerchantRecommendMeal/cancel")
    Observable<HljHttpResult> cancelRecommendWork(@Body PostWorkBody body);

    /**
     * 推荐一个套餐
     *
     * @param body
     * @return
     */
    @POST("p/wedding/index.php/Admin/APIMerchantRecommendMeal/edit")
    Observable<HljHttpResult> editRecommendWork(@Body PostWorkBody body);

    /**
     * 获得商家公告
     *
     * @return
     */
    @GET("p/wedding/index.php/Admin/APIMerchantNotice/index")
    Observable<HljHttpResult<MerchantNotice>> getMerchantNotice();

    /**
     * 编辑或者添加商家店铺公告
     *
     * @param body
     * @return
     */
    @POST("p/wedding/index.php/Admin/APIMerchantNotice/edit")
    Observable<HljHttpResult> editMerchantNotice(@Body MerchantNoticePostBody body);

    /**
     * 删除商家店铺公告
     *
     * @return
     */
    @POST("p/wedding/index.php/Admin/APIMerchantNotice/delete")
    Observable<HljHttpResult> deleteMerchantNotice();

    /**
     * 是否可添加编辑店铺公告
     * retcode 返回1003 错误码为不可编辑添加
     * msg 2016-07-11 23:59:59 可编辑添加的时间节点
     *
     * @return
     */
    @GET("p/wedding/index.php/Admin/APIMerchantNotice/check")
    Observable<HljHttpResult> checkMerchantNotice();

    /**
     * 获得商家特权记录
     *
     * @return
     */
    @GET("p/wedding/Admin/APIMerchantPrivilege/record_list_v2")
    Observable<HljHttpResult<MerchantPrivilegeRecord>> getMerchantPrivilegeRecordList();

    /**
     *
     */
    @GET("/p/wedding/home/APIMerchant/merchant_description_link")
    Observable<HljHttpResult<DescriptionLink>> getmerchantDescriptionLink();

    /**
     * 当前商家是否需要升级套餐
     *
     * @return
     */
    @POST("p/wedding/index.php/Admin/APIMerchant/myWorksNeedUpgrade")
    Observable<HljHttpResult<MerchantUpgradeInfo>> checkWorksNeedUpgrade();

    /**
     * 获取商家店铺信息
     */
    @GET("p/wedding/index.php/Admin/APIMerchant/edit")
    Observable<HljHttpResult<JsonElement>> getMerchantInfo(@Query("is_pending") boolean isPending);

    /**
     * 编辑商家店铺信息
     */
    @POST("p/wedding/index.php/Admin/APIMerchant/edit")
    Observable<HljHttpResult> postMerchantInfo(@Body Map<String, Object> map);
}
