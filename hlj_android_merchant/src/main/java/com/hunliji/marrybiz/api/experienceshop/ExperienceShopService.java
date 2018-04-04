package com.hunliji.marrybiz.api.experienceshop;

import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.marrybiz.model.experience.AdvDetail;
import com.hunliji.marrybiz.model.experience.HljExperienceData;
import com.hunliji.marrybiz.model.experience.ShowHistory;

import java.util.List;
import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by jinxin on 2017/12/19 0019.
 */

public interface ExperienceShopService {

    /**
     * 体验店订单详情
     *
     * @param id
     * @return
     */
    @GET("p/wedding/index.php/admin/APIHotelOrderSub/detail")
    Observable<HljHttpResult<AdvDetail>> getExperienceOrderDetail(@Query("id") long id);

    /**
     * 体验店跟进历史
     *
     * @param id
     * @return
     */
    @GET("p/wedding/index.php/Admin/APIHotelOrderSub/show_history")
    Observable<HljHttpResult<HljHttpData<List<ShowHistory>>>> getExperienceShowHistory(@Query("id") long id);

    /**
     * 修改销售
     * @param params
     * @return
     */
    @POST("p/wedding/index.php/Admin/APIHotelOrderSub/saler")
    Observable<HljHttpResult> modifySalerName(@Body Map<String ,Object> params);

    /**
     * 体验店推荐订单列表
     *
     * @param map
     * @return
     */
    @GET("p/wedding/index.php/admin/APIHotelOrderSub/index")
    Observable<HljHttpResult<HljExperienceData<List<AdvDetail>>>>
    getExperienceShopOrderList(
            @QueryMap Map<String, Object> map);

    /**
     * 填写备注
     * @param params
     * @return
     */
    @POST("p/wedding/index.php/Admin/APIHotelOrderSub/followup")
    Observable<HljHttpResult> fillRemark(@Body Map<String,Object> params);

    /**
     * 确认成单
     * @param params
     * @return
     */
    @POST("p/wedding/index.php/Admin/APIHotelOrderSub/form_order")
    Observable<HljHttpResult> confirmOrder(@Body Map<String,Object> params);

    /**
     * 跟进失败
     * @param params
     * @return
     */
    @POST("p/wedding/index.php/Admin/APIHotelOrderSub/follow_fail")
    Observable<HljHttpResult> followFailed(@Body Map<String,Object> params);

    /**
     * 确认到店
     * @return
     */
    @POST("p/wedding/index.php/Admin/APIHotelOrderSub/come_merchant")
    Observable<HljHttpResult> arriveShop(@Body Map<String,Object> params);
}
