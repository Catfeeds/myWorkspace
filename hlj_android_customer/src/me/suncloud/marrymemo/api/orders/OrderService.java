package me.suncloud.marrymemo.api.orders;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hunliji.hljcommonlibrary.models.product.wrappers.ShippingFeeList;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;

import java.util.List;
import java.util.Map;

import me.suncloud.marrymemo.model.CustomSetmealOrder;
import me.suncloud.marrymemo.model.orders.ProductOrder;
import me.suncloud.marrymemo.model.orders.ProductOrderRedPacketState;
import me.suncloud.marrymemo.model.orders.ServiceOrder;
import me.suncloud.marrymemo.model.orders.ServiceOrderCountInfo;
import me.suncloud.marrymemo.model.orders.ServiceOrderIdBody;
import me.suncloud.marrymemo.model.orders.ServiceOrderNoBody;
import me.suncloud.marrymemo.model.orders.ServiceOrderSubmitBody;
import me.suncloud.marrymemo.model.orders.ServiceOrderSubmitResponse;
import me.suncloud.marrymemo.model.orders.UpdateCustomerInfoBody;

import com.hunliji.hljcommonlibrary.models.orders.HotelPeriodOrder;

import me.suncloud.marrymemo.model.orders.hotelperoid.HotelPeriodBody;
import me.suncloud.marrymemo.model.wallet.CouponRecord;
import me.suncloud.marrymemo.model.wallet.MerchantCouponList;

import com.hunliji.hljcardcustomerlibrary.models.RedPacket;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by werther on 16/10/11.
 */

public interface OrderService {

    /**
     * 获取本地服务订单列表
     *
     * @return
     */
    @GET("p/wedding/Home/APIOrderV2/list_v2?per_page=20")
    Observable<HljHttpResult<HljHttpData<List<ServiceOrder>>>> getServiceOrders(
            @Query("page") int page);

    /**
     * 获取对应商家的可用的优惠券
     *
     * @param merchantId
     * @param price
     * @return
     */
    @GET("p/wedding/index.php/home/APIUserCoupon/UserCoupon")
    Observable<HljHttpResult<HljHttpData<List<CouponRecord>>>> getAvailableCoupons(
            @Query("merchant_id") long merchantId, @Query("price") double price);

    /**
     * 获取对应婚品商家的可用的优惠券
     *
     * @param map
     * @return
     */
    @POST("p/wedding/index.php/home/APIUserCoupon/available_list")
    Observable<HljHttpResult<List<MerchantCouponList>>> postAvailableProductCoupons(
            @Body Map<String, Object> map);

    /**
     * 获取对应套餐和对应选择的优惠券的可用的红包列表
     *
     * @param setMealId
     * @param userCouponId
     * @return
     */
    @GET("p/wedding/index.php/Shop/APIRedPacket/MyServerRedPacketList")
    Observable<HljHttpResult<HljHttpData<List<RedPacket>>>> getAvailableRedPacketList(
            @Query("set_meal_id") long setMealId, @Query("user_coupon_id") long userCouponId);

    /**
     * 提交服务订单
     *
     * @param body
     * @return
     */
    @POST("p/wedding/index.php/Home/APIOrderV2/submit")
    Observable<HljHttpResult<ServiceOrderSubmitResponse>> submitServiceOrder(@Body ServiceOrderSubmitBody body);

    /**
     * 获取本地服务订单详情
     *
     * @param id
     * @return
     */
    @GET("p/wedding/Home/APIOrderV2/detail_v2")
    Observable<HljHttpResult<ServiceOrder>> getServiceOrderDetail(
            @Query("order_id") long id);

    /**
     * 取消本地服务订单
     *
     * @param orderIdBody
     * @return
     */
    @POST("p/wedding/Home/APIOrderV2/cancel")
    Observable<HljHttpResult<ServiceOrder>> cancelServiceOrder(
            @Body ServiceOrderIdBody orderIdBody);

    /**
     * 删除本地服务订单
     *
     * @param orderId
     * @return
     */
    @DELETE("p/wedding/Home/APIOrderV2/order")
    Observable<HljHttpResult> deleteServiceOrder(@Query("order_id") long orderId);

    /**
     * 确认服务
     *
     * @param orderIdBody
     * @return
     */
    @POST("p/wedding/Home/APIOrderV2/finish")
    Observable<HljHttpResult<ServiceOrder>> confirmService(@Body ServiceOrderIdBody orderIdBody);

    /**
     * 获取三种特殊本地服务订单数量信息
     *
     * @return
     */
    @GET("p/wedding/index.php/home/APIOrder/GetOrderCountInfoV2")
    Observable<HljHttpResult<ServiceOrderCountInfo>> getServiceOrdersCountInfo();

    /**
     * 获取全部定制套餐订单列表
     *
     * @return
     */
    @GET("p/wedding/Home/APIOrderV2/customlist")
    Observable<HljHttpResult<List<CustomSetmealOrder>>> getCustomSetmealOrders();

    /**
     * 取消退款申请
     *
     * @param noBody
     * @return
     */
    @POST("p/wedding/index.php/home/APIOrder/CancelRefund")
    Observable<HljHttpResult<JsonElement>> cancelServiceOrderRefund(
            @Body ServiceOrderNoBody noBody);

    /**
     * 获取婚品订单列表
     *
     * @param page
     * @return
     */
    @GET("p/wedding/shop/APIShopOrder?per_page=20")
    Observable<HljHttpResult<HljHttpData<List<ProductOrder>>>> getProductOrderList(
            @Query("page") int page);

    /**
     * 删除婚品订单
     *
     * @param jsonObject
     * @return
     */
    @POST("p/wedding/shop/APIShopOrder/delete")
    Observable<HljHttpResult> deleteProductOrder(@Body JsonObject jsonObject);

    /**
     * 确认婚品订单收货
     *
     * @param
     * @return
     */
    @PUT("p/wedding/shop/APIShopOrder/confirm_Receive")
    Observable<HljHttpResult<ProductOrder>> confirmProductOrderShipping(
            @Body JsonObject jsonObject);

    /**
     * 取消婚品订单
     *
     * @param jsonObject
     * @return
     */
    @PUT("p/wedding/shop/APIShopOrder/cancel_order")
    Observable<HljHttpResult<ProductOrder>> cancelProductOrder(
            @Body JsonObject jsonObject);

    /**
     * 婚品订单详情
     *
     * @param orderId
     * @return
     */
    @GET("p/wedding/shop/APIShopOrder/info")
    Observable<HljHttpResult<ProductOrder>> getProductOrderDetail(@Query("id") long orderId);

    /**
     * 检查红包是否可用
     *
     * @return
     */
    @POST("p/wedding/shop/APIShopOrder/check_red_packet")
    Observable<HljHttpResult<ProductOrderRedPacketState>> checkProductOrderRedPacket(
            @Body JsonObject jsonObject);

    /**
     * 延期确认服务
     *
     * @param orderIdBody
     * @return
     */
    @POST("p/wedding/index.php/Home/APIOrderV2/delay_order_auto_close")
    Observable<HljHttpResult<ServiceOrder>> delayConfirmService(
            @Body ServiceOrderIdBody orderIdBody);

    /**
     * 修改客户信息
     *
     * @param body
     * @return
     */
    @POST("p/wedding/index.php/Home/APIOrderV2/serve_time")
    Observable<HljHttpResult<Object>> updateCustomerInfo(@Body UpdateCustomerInfoBody body);

    /**
     * 婚品订单单独请求邮费
     *
     * @param map
     * @return
     */
    @POST("p/wedding/index.php/Shop/APIShopOrder/confirmOrder")
    Observable<HljHttpResult<ShippingFeeList>> postForShippingFee(@Body Map<String, Object> map);

    /**
     * 婚宴订单列表
     *
     * @param page
     * @param perPage
     * @return
     */
    @GET("p/wedding/home/APIHotelPeriodOrder/list")
    Observable<HljHttpResult<HljHttpData<List<HotelPeriodOrder>>>> getHotelPeriodOrders(
            @Query("page") int page, @Query("per_page") int perPage);

    /**
     * 取消婚宴订单
     *
     * @return
     */
    @POST("p/wedding/home/APIHotelPeriodOrder/cancel")
    Observable<HljHttpResult> cancelHotelPeriodOrder(@Body Map<String, Object> map);

    /**
     * 删除婚宴订单
     *
     * @return
     */
    @POST("p/wedding/home/APIHotelPeriodOrder/delete")
    Observable<HljHttpResult> deleteHotelPeriodOrder(@Body Map<String, Object> map);

    /**
     * 婚宴订单提交
     *
     * @return
     */
    @POST("p/wedding/home/APIHotelPeriodOrder/submit")
    Observable<HljHttpResult<JsonElement>> submitHotelPeriodOrder(@Body HotelPeriodBody body);
}
