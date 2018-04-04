package com.hunliji.marrybiz.api.weddingcar;

import com.google.gson.JsonElement;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.marrybiz.model.weddingcar.WeddingCarOrderDetail;

import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by jinxin on 2018/1/4 0004.
 */

public interface WeddingCarService {

    /**
     * 获得订单详情
     *
     * @param id
     * @return
     */
    @GET("p/wedding/index.php/Admin/APICarOrder/detail")
    Observable<HljHttpResult<WeddingCarOrderDetail>> getWeddingCarOrderDetail(@Query("id") long id);

    /**
     * 改价
     * id	订单id
     * price 价格
     *
     * @return
     */
    @POST("p/wedding/Admin/APICarOrder/ChangePrice")
    Observable<HljHttpResult> weddingCarChangePrice(@Body Map<String, Object> params);

    /**
     * 拒绝接单
     * id
     *
     * @param params
     * @return
     */
    @POST("p/wedding/index.php/Admin/APICarOrder/RefusedToOrder")
    Observable<HljHttpResult> refuseOrder(@Body Map<String, Object> params);


    @POST("p/wedding/Admin/APICarOrder/SubmitJd")
    Observable<HljHttpResult<JsonElement>> takeOrder(@Body Map<String, Object> params);

}
