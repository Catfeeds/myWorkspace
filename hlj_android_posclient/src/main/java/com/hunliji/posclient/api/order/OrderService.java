package com.hunliji.posclient.api.order;

import com.google.gson.JsonElement;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.posclient.models.MerchantOrder;
import com.hunliji.posclient.models.relam.PosPayResult;

import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by chen_bin on 2018/1/17 0017.
 */
public interface OrderService {

    /**
     * POS机获取订单详情
     *
     * @param map
     * @return
     */
    @POST(" p/wedding/index.php/Admin/APIMerchantOrder/pos_info")
    Observable<HljHttpResult<MerchantOrder>> getOrderDetail(
            @Body Map<String, Object> map);

    /**
     * POS机确认支付金额
     *
     * @param map
     * @return
     */
    @POST("p/wedding/index.php/Admin/APIMerchantOrder/pay")
    Observable<HljHttpResult<JsonElement>> confirmPay(@Body Map<String, Object> map);

    /**
     * pos机支付成功跟婚礼纪的回调
     *
     * @param url
     * @param payResult
     * @return
     */
    @POST
    Observable<HljHttpResult> submitPosPayResult(@Url String url, @Body PosPayResult payResult);
}
