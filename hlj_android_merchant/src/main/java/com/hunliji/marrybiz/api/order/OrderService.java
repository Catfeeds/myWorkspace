package com.hunliji.marrybiz.api.order;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.marrybiz.model.Label;
import com.hunliji.marrybiz.model.orders.ChangeOrderPriceBody;
import com.hunliji.marrybiz.model.orders.MerchantOrder;
import com.hunliji.marrybiz.model.orders.MerchantOrderFilter;
import com.hunliji.marrybiz.model.orders.OrderProtocolPhotosPostBody;

import java.util.List;
import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by luohanlin on 28/02/2017.
 */

public interface OrderService {

    /**
     * 确认已收到尾款
     *
     * @param jsonObject
     * @return
     */
    @POST("p/wedding/index.php/Admin/APIOrder/confirm_rest")
    Observable<HljHttpResult<Object>> confirmRestMoney(@Body JsonObject jsonObject);

    /**
     * 上传合同图片
     *
     * @param body
     * @return
     */
    @POST("p/wedding/index.php/Admin/APIOrder/protocol")
    Observable<HljHttpResult> postProtocolPhotos(@Body OrderProtocolPhotosPostBody body);

    /**
     * 修改价格
     *
     * @param body
     * @return
     */
    @POST("p/wedding/index.php/Admin/APIOrder/change_price_v3")
    Observable<HljHttpResult<JsonElement>> postChangePrice(@Body ChangeOrderPriceBody body);

    /**
     * 获取商家订单列表（BD）
     *
     * @return
     */
    @GET
    Observable<HljHttpResult<HljHttpData<List<MerchantOrder>>>> getMerchantOrderList(@Url String url);

    /**
     * 获取商家订单详情
     *
     * @param id
     * @return
     */
    @GET("p/wedding/index.php/Admin/APIMerchantOrder/info")
    Observable<HljHttpResult<MerchantOrder>> getMerchantOrderDetail(@Query("id") long id);

    /**
     * 获取商家筛选列表
     *
     * @return
     */
    @GET("p/wedding/index.php/Admin/APIMerchantOrder/filter")
    Observable<HljHttpResult<List<MerchantOrderFilter>>> getMerchantOrderFilterList();

    /**
     * 获取商家订单未支付数量
     *
     * @return
     */
    @GET("p/wedding/index.php/Admin/APIMerchantOrder/wait_pay_count")
    Observable<HljHttpResult<JsonElement>> getWaitPayCount();
}
