package com.hunliji.marrybiz.api.work_case;

import com.hunliji.hljcommonlibrary.models.CustomSetmeal;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljcommonlibrary.models.PostIdBody;
import com.hunliji.marrybiz.model.work_case.ExchangeOrderPostBody;
import com.hunliji.marrybiz.model.work_case.PublishedPostBody;
import com.hunliji.marrybiz.model.work_case.SetSoldOutPostBody;
import com.hunliji.marrybiz.model.work_case.SetTopPostBody;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;


/**
 * 套餐跟案例相关的API服务接口
 * Created by chen_bin on 2016/9/6 0006.
 */
public interface WorkService {

    /**
     * 套餐管理，案例管理列表
     *
     * @param commodityType
     * @param status
     * @param isSoldOut
     * @param rating
     * @param page
     * @param perPage
     * @return
     */
    @GET("p/wedding/index.php/Admin/APISetMeal/index")
    Observable<HljHttpResult<HljHttpData<List<Work>>>> getWorks(
            @Query("commodity_type") int commodityType,
            @Query("status") String status,
            @Query("is_sold_out") int isSoldOut,
            @Query("rating") String rating,
            @Query("page") int page,
            @Query("per_page") int perPage);

    /**
     * 定制套餐列表
     *
     * @param page
     * @param perPage
     * @return
     */
    @GET("p/wedding/index.php/Admin/APICustomSetMeal/list")
    Observable<HljHttpResult<HljHttpData<List<CustomSetmeal>>>> getCustomSetmeals(
            @Query("page") int page, @Query("per_page") int perPage);

    /**
     * 套餐跟案例上架与下架
     *
     * @param postBody
     * @return
     */
    @POST("p/wedding/index.php/Admin/APISetMeal/set_sold_out")
    Observable<HljHttpResult> setSoldOut(@Body SetSoldOutPostBody postBody);

    /**
     * 定制套餐上架与下架
     *
     * @param postBody
     * @return
     */
    @POST("p/wedding/index.php/Admin/APICustomSetMeal/edit")
    Observable<HljHttpResult> setPublished(@Body PublishedPostBody postBody);

    /**
     * 套餐、案例的删除
     *
     * @param postBody
     * @return
     */
    @POST("p/wedding/index.php/Admin/APISetMeal/delete")
    Observable<HljHttpResult> deleteWork(@Body PostIdBody postBody);

    /**
     * 定制套餐的删除
     *
     * @param postBody
     * @return
     */
    @POST("p/wedding/index.php/Admin/APICustomSetMeal/dropmeal")
    Observable<HljHttpResult> deleteCustomSetMeal(@Body PostIdBody postBody);

    /**
     * 套餐案例的排序
     *
     * @param postBody
     * @return
     */
    @POST("p/wedding/index.php/admin/APISetMeal/exchange_order")
    Observable<HljHttpResult> exchangeOrder(@Body ExchangeOrderPostBody postBody);

    /**
     * 套餐案例置顶
     *
     * @param setTopPostBody
     * @return
     */
    @POST("p/wedding/index.php/Admin/APISetMeal/set_is_top")
    Observable<HljHttpResult> setTop(@Body SetTopPostBody setTopPostBody);
}
