package me.suncloud.marrymemo.api.budget;


import com.google.gson.JsonElement;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by jinxin on 2017/11/20 0020.
 */

public interface BudgetService {

    /**
     * 获得结婚预算信息
     *
     * @return
     */
    @GET("p/wedding/home/APIBudget/info")
    Observable<HljHttpResult<JsonElement>> getBudgetInfo();

    /**
     * 获得分享信息
     *
     * @param id
     * @return
     */
    @GET("p/wedding/home/APIBudget/share_info")
    Observable<HljHttpResult<JsonElement>> getShareInfo(@Query("id") long id);

    /**
     * 获得结婚预算类别
     *
     * @return
     */
    @GET("p/wedding/home/APIBudget/category")
    Observable<HljHttpResult<JsonElement>> getBudgetCategory();
}
