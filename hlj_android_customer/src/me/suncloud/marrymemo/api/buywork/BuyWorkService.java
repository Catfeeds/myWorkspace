package me.suncloud.marrymemo.api.buywork;

import com.hunliji.hljhttplibrary.entities.HljHttpResult;

import java.util.Map;

import me.suncloud.marrymemo.model.buyWork.SetMeal;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by jinxin on 2016/12/6 0006.
 */

public interface BuyWorkService {
    /**
     * 买套餐列表 list
     * @return
     */
    @GET("/p/wedding/Home/APISearchV2/set_meal")
    Observable<HljHttpResult<SetMeal>> getBuyWork(
            @Query("category_id") long categoryId,
            @Query("property_id") long propertyId,
            @Query("city_code") long cityCode,
            @Query("sort") String sort,
            @QueryMap Map<String, String> filterMap,
            @Query("page") int page,
            @Query("per_page") int perPage);
}
