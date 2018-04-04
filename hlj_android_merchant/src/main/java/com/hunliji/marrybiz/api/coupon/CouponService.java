package com.hunliji.marrybiz.api.coupon;

import com.hunliji.hljcommonlibrary.models.coupon.CouponInfo;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljcommonlibrary.models.PostIdBody;

import java.util.List;
import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * 商家优惠券相关API服务接口
 * Created by chen_bin on 2016/9/6 0006.
 */
public interface CouponService {

    /**
     * 优惠券列表
     *
     * @param name
     * @param valid
     * @param page
     * @param perPage
     * @return
     */
    @GET("p/wedding/index.php/Admin/APICoupon/List")
    Observable<HljHttpResult<HljHttpData<List<CouponInfo>>>> getCouponList(
            @Query("name") String name,
            @Query("is_valid") String valid,
            @Query("page") int page,
            @Query("per_page") int perPage);

    /**
     * 激活优惠券
     *
     * @param body
     * @return
     */
    @POST("p/wedding/index.php/Admin/APICoupon/Activation")
    Observable<HljHttpResult> activateCoupon(@Body PostIdBody body);


    /**
     * 创建跟修改优惠券
     *
     * @param map
     * @return
     */
    @POST("p/wedding/index.php/Admin/APICoupon/CreateCoupon")
    Observable<HljHttpResult> createCoupon(@Body Map<String,Object> map);
}