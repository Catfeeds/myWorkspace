package com.hunliji.marrybiz.api.customer;

import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.marrybiz.model.customer.MerchantCustomer;
import com.hunliji.marrybiz.model.customer.MerchantCustomerModifyBody;

import java.util.List;
import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by jinxin on 2017/8/15 0015.
 */

public interface CustomerService {

    /**
     * 客资列表
     * deal_will int 成交意愿 0低 1 一般 2高
     * nick      用户名
     * user_name 用户姓名
     * user_phone 用户电话
     * weddingday 婚期
     * page
     * per_page
     *
     * @return
     */
    @GET("p/wedding/index.php/Admin/APIMerchantCustomer/list")
    Observable<HljHttpResult<HljHttpData<List<MerchantCustomer>>>> getCustomerList(
            @QueryMap Map<String, String> map);

    /**
     * 保存客资信息
     *
     * @return
     */
    @POST("p/wedding/index.php/Admin/APIMerchantCustomer/modify")
    Observable<HljHttpResult> saveCustomer(@Body MerchantCustomerModifyBody body);

    /**
     * 获得客资详情
     * @param userId
     * @return
     */
    @GET("p/wedding/index.php/Admin/APIMerchantCustomer/detail")
    Observable<HljHttpResult<MerchantCustomer>> getMerchantCustomerDetail(@Query("user_id") long
                                                                                  userId);

}
