package me.suncloud.marrymemo.api.caseapi;

import com.google.gson.JsonElement;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

/**
 * 案例相关
 * Created by jinxin on 2018/2/5 0005.
 */

public interface CaseService {

    /**
     * 案例详情
     *
     * @return
     */
    @GET("p/wedding/index.php/home/APISetMeal/info/id/{id}")
    Observable<JsonElement> getCaseDetail(@Path("id") long  id);

    /**
     * 推荐案例
     *
     * @param id
     * @param kind
     * @param sort
     * @param page
     * @param perPage
     * @return
     */
    @GET("p/wedding/index.php/home/APISetMeal/list")
    Observable<JsonElement> getRecommendCase(
            @Query("id") long id,
            @Query("kind") String kind,
            @Query("sort") String sort,
            @Query("page") int page,
            @Query("per_page") int perPage);

    /**
     * 推荐套餐
     *
     * @param exampleId 案例id
     * @return
     */
    @GET("p/wedding/Home/APISetMealRelation/relative_set_meals")
    Observable<HljHttpResult<HljHttpData<List<Work>>>> getRecommendWork(@Query("example_id") long exampleId);
}
