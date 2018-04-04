package com.hunliji.marrybiz.api.potential_customer;

import com.google.gson.JsonObject;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.marrybiz.model.potential_customer.PotentialCustomer;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by wangtao on 2017/8/8.
 */

public interface PotentialCustomerService {

    /**
     * <a href="http://doc.hunliji.com/workspace/myWorkspace.do?projectId=15#3329">未处理客资数</a>
     *
     * @return
     */
    @GET("p/wedding/index.php/admin/APIMerchantPotentialCustomer/pending_num")
    Observable<HljHttpResult<JsonObject>> getPendingNum();


    /**
     * <a href="http://doc.hunliji.com/workspace/myWorkspace.do?projectId=15#3004">潜在客资列表</a>
     *
     * @param page
     * @param perPage
     * @param status 0未处理 1已处理 2超时未处理
     * @return
     */
    @GET("p/wedding/index.php/admin/APIMerchantPotentialCustomer/index")
    Observable<HljHttpResult<HljHttpData<List<PotentialCustomer>>>> getPotentialCustomers(
            @Query("page") int page,
            @Query("per_page") int perPage,
            @Query("status[]") int... status);
}
