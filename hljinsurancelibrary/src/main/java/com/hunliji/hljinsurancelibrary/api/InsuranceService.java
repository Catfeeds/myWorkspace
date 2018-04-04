package com.hunliji.hljinsurancelibrary.api;


import com.google.gson.JsonElement;
import com.hunliji.hljcommonlibrary.models.BindInfo;
import com.hunliji.hljcommonlibrary.models.PostAddressId;
import com.hunliji.hljcommonlibrary.models.coupon.CouponInfo;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpPosterData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljinsurancelibrary.models.MyPolicy;
import com.hunliji.hljinsurancelibrary.models.PolicyDetail;
import com.hunliji.hljinsurancelibrary.models.PostHlbPolicy;
import com.hunliji.hljinsurancelibrary.models.wrappers.HljHttpInsuranceProductsData;

import java.util.List;
import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * 红包、优惠券Service
 * Created by chen_bin on 2016/9/9 0009.
 */
public interface InsuranceService {

    /**
     * 获取保险产品列表
     */
    @GET("p/wedding/index.php/home/APIUserInsurance/products")
    Observable<HljHttpResult<HljHttpInsuranceProductsData>> getInsuranceProducts();

    /**
     * 我的保单  	ongoing进行中 history 历史保单
     */
    @GET("/p/wedding/index.php/home/APIUserInsurance/my_insurance")
    Observable<HljHttpResult<HljHttpPosterData<List<MyPolicy>>>> getMyInsurance(
            @Query("tab") String tab, @Query("page") int page, @Query("per_page") int perPage);

    /**
     * 婚礼保保单详情
     */
    @GET("/p/wedding/index.php/home/APIUserInsurance/hlb_detail")
    Observable<HljHttpResult<PolicyDetail>> getPolicyDetail(
            @Query("id") String id);

    /**
     * 填写婚礼保保单
     */
    @POST("p/wedding/index.php/home/APIUserInsurance/fill_hlb")
    Observable<HljHttpResult> fillHlb(@Body PostHlbPolicy postHlbPolicy);

    /**
     * 填写蜜月宝保单
     * @return
     */
    @POST("p/wedding/index.php/home/APIUserInsurance/fill_myb")
    Observable<HljHttpResult> fillMyb(@Body Map<String, Object> params);

    /**
     * 蜜月保（保单详情）
     * @return
     */
    @GET("p/wedding/index.php/home/APIUserInsurance/myb_detail")
    Observable<HljHttpResult<PolicyDetail>> getMybDetail(@Query("id") String id);
}