package me.suncloud.marrymemo.api.orders;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hunliji.hljcardcustomerlibrary.models.RedPacket;
import com.hunliji.hljcommonlibrary.models.product.wrappers.ShippingFeeList;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.suncloud.marrymemo.model.CustomSetmealOrder;
import me.suncloud.marrymemo.model.orders.ProductOrder;
import me.suncloud.marrymemo.model.orders.ProductOrderRedPacketState;
import me.suncloud.marrymemo.model.orders.ServeCustomerInfo;
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

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by werther on 16/10/11.
 */

public class OrderApi {

    /**
     * 获取本地服务订单列表
     *
     * @return
     */
    public static Observable<HljHttpData<List<ServiceOrder>>> getServiceOrderList(int page) {
        return HljHttp.getRetrofit()
                .create(OrderService.class)
                .getServiceOrders(page)
                .map(new HljHttpResultFunc<HljHttpData<List<ServiceOrder>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取对应商家的可用的优惠券
     *
     * @param merchantId
     * @param price
     * @return
     */
    public static Observable<HljHttpData<List<CouponRecord>>> getAvailableCouponList(
            long merchantId, double price) {
        return HljHttp.getRetrofit()
                .create(OrderService.class)
                .getAvailableCoupons(merchantId, price)
                .map(new HljHttpResultFunc<HljHttpData<List<CouponRecord>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取对应婚品商家的可用的优惠券
     *
     * @param map
     * @return
     */
    public static Observable<List<MerchantCouponList>> postAvailableProductCouponList(
            Map<String, Object> map) {

        return HljHttp.getRetrofit()
                .create(OrderService.class)
                .postAvailableProductCoupons(map)
                .map(new HljHttpResultFunc<List<MerchantCouponList>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取对应套餐和对应选择的优惠券的可用的红包列表
     *
     * @param setMealId
     * @param userCouponId
     * @return
     */
    public static Observable<HljHttpData<List<RedPacket>>> getAvailableRedPacketList(
            long setMealId, long userCouponId) {
        return HljHttp.getRetrofit()
                .create(OrderService.class)
                .getAvailableRedPacketList(setMealId, userCouponId)
                .map(new HljHttpResultFunc<HljHttpData<List<RedPacket>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 提交服务订单
     *
     * @param body
     * @return
     */
    public static Observable<ServiceOrderSubmitResponse> submitServiceOrder
    (ServiceOrderSubmitBody body) {
        return HljHttp.getRetrofit()
                .create(OrderService.class)
                .submitServiceOrder(body)
                .map(new HljHttpResultFunc<ServiceOrderSubmitResponse>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取本地服务订单详情
     *
     * @param id
     * @return
     */
    public static Observable<ServiceOrder> getServiceOrderDetail(long id) {
        return HljHttp.getRetrofit()
                .create(OrderService.class)
                .getServiceOrderDetail(id)
                .map(new HljHttpResultFunc<ServiceOrder>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 取消本地服务订单
     *
     * @param body
     * @return
     */
    public static Observable<ServiceOrder> cancelServiceOrder(ServiceOrderIdBody body) {
        return HljHttp.getRetrofit()
                .create(OrderService.class)
                .cancelServiceOrder(body)
                .map(new HljHttpResultFunc<ServiceOrder>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 删除本地服务订单
     *
     * @param id
     * @return
     */
    public static Observable deleteServiceOrder(long id) {
        return HljHttp.getRetrofit()
                .create(OrderService.class)
                .deleteServiceOrder(id)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 确认服务订单已服务
     *
     * @param body
     * @return
     */
    public static Observable<ServiceOrder> confirmServiceOrder(ServiceOrderIdBody body) {
        return HljHttp.getRetrofit()
                .create(OrderService.class)
                .confirmService(body)
                .map(new HljHttpResultFunc<ServiceOrder>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取三种特殊本地服务订单数量信息
     *
     * @return
     */
    public static Observable<ServiceOrderCountInfo> getServiceOrderCountInfo() {
        return HljHttp.getRetrofit()
                .create(OrderService.class)
                .getServiceOrdersCountInfo()
                .map(new HljHttpResultFunc<ServiceOrderCountInfo>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取全部的定制套餐订单列表
     *
     * @return
     */
    public static Observable<List<CustomSetmealOrder>> getCustomSetmealOrders() {
        return HljHttp.getRetrofit()
                .create(OrderService.class)
                .getCustomSetmealOrders()
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 取消退款申请
     *
     * @param body
     * @return
     */
    public static Observable<JsonElement> cancelServiceOrderRefund(ServiceOrderNoBody body) {
        return HljHttp.getRetrofit()
                .create(OrderService.class)
                .cancelServiceOrderRefund(body)
                .map(new HljHttpResultFunc<JsonElement>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取婚品订单列表
     *
     * @param page
     * @return
     */
    public static Observable<HljHttpData<List<ProductOrder>>> getProductOrderList(int page) {
        return HljHttp.getRetrofit()
                .create(OrderService.class)
                .getProductOrderList(page)
                .map(new HljHttpResultFunc<HljHttpData<List<ProductOrder>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 删除婚品订单
     *
     * @param id
     * @return
     */
    public static Observable deleteProductOrder(long id) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("order_id", id);
        return HljHttp.getRetrofit()
                .create(OrderService.class)
                .deleteProductOrder(jsonObject)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 确认婚品订单已收货
     *
     * @param orderId
     * @return
     */
    public static Observable<ProductOrder> confirmProductOrderShipping(long orderId) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("order_id", orderId);
        return HljHttp.getRetrofit()
                .create(OrderService.class)
                .confirmProductOrderShipping(jsonObject)
                .map(new HljHttpResultFunc<ProductOrder>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 取消婚品订单
     *
     * @param orderId
     * @return
     */
    public static Observable<ProductOrder> cancelProductOrder(long orderId) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("order_id", orderId);
        return HljHttp.getRetrofit()
                .create(OrderService.class)
                .cancelProductOrder(jsonObject)
                .map(new HljHttpResultFunc<ProductOrder>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取婚品订单详情
     *
     * @param orderId
     * @return
     */
    public static Observable<ProductOrder> getProductOrderDetail(long orderId) {
        return HljHttp.getRetrofit()
                .create(OrderService.class)
                .getProductOrderDetail(orderId)
                .map(new HljHttpResultFunc<ProductOrder>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 检查红包是否可用
     *
     * @param orderId
     * @return
     */
    public static Observable<ProductOrderRedPacketState> checkProductOrderRedPacket(long orderId) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("order_ids", orderId);
        return HljHttp.getRetrofit()
                .create(OrderService.class)
                .checkProductOrderRedPacket(jsonObject)
                .map(new HljHttpResultFunc<ProductOrderRedPacketState>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 延期确认
     *
     * @param body
     * @return
     */
    public static Observable<ServiceOrder> delayConfirmServiceOrder(ServiceOrderIdBody body) {
        return HljHttp.getRetrofit()
                .create(OrderService.class)
                .delayConfirmService(body)
                .map(new HljHttpResultFunc<ServiceOrder>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 修改客户信息
     *
     * @param orderId
     * @param info
     * @return
     */
    public static Observable<Object> updateCustomerInfo(long orderId, ServeCustomerInfo info) {
        UpdateCustomerInfoBody body = new UpdateCustomerInfoBody();
        body.setOrderId(orderId);
        body.setBuyerName(info.getCustomerName());
        body.setBuyerPhone(info.getCustomerPhone());
        body.setWeddingTime(info.getServeTime());
        return HljHttp.getRetrofit()
                .create(OrderService.class)
                .updateCustomerInfo(body)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 婚品订单单独请求邮费
     *
     * @return
     */
    public static Observable<ShippingFeeList> postForShippingFeeObb(Map<String, Object> map) {
        return HljHttp.getRetrofit()
                .create(OrderService.class)
                .postForShippingFee(map)
                .map(new HljHttpResultFunc<ShippingFeeList>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 婚宴订单列表
     *
     * @return
     */
    public static Observable<HljHttpData<List<HotelPeriodOrder>>> getHotelPeriodOrdersObb(
            int page, int perPage) {
        return HljHttp.getRetrofit()
                .create(OrderService.class)
                .getHotelPeriodOrders(page, perPage)
                .map(new HljHttpResultFunc<HljHttpData<List<HotelPeriodOrder>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 取消婚宴订单
     *
     * @return
     */
    public static Observable cancelHotelPeriodOrderObb(long id) {
        Map<String, Object> map = new HashMap<>();
        map.put("order_id", id);
        return HljHttp.getRetrofit()
                .create(OrderService.class)
                .cancelHotelPeriodOrder(map)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 删除婚宴订单
     *
     * @return
     */
    public static Observable deleteHotelPeriodOrderObb(long id) {
        Map<String, Object> map = new HashMap<>();
        map.put("order_id", id);
        return HljHttp.getRetrofit()
                .create(OrderService.class)
                .deleteHotelPeriodOrder(map)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 婚宴订单提交
     *
     * @return
     */
    public static Observable<JsonElement> submitHotelPeriodOrderObb(HotelPeriodBody body) {
        return HljHttp.getRetrofit()
                .create(OrderService.class)
                .submitHotelPeriodOrder(body)
                .map(new HljHttpResultFunc<JsonElement>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
