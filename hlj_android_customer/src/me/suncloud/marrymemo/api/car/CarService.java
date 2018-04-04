package me.suncloud.marrymemo.api.car;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hunliji.hljcommonlibrary.models.Label;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;

import java.util.List;
import java.util.Map;

import me.suncloud.marrymemo.model.car.CarComment;
import me.suncloud.marrymemo.model.car.CarProduct;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by wangtao on 2017/4/20.
 */

public interface CarService {

    /**
     * 收集婚车信息
     *
     * @return
     */
    @POST("p/wedding/index.php/Car/APICarOrder/data_info")
    Observable<HljHttpResult<JsonElement>> postCarEntryData(@Body JsonObject body);


    /**
     * 婚车评价列表
     *
     * @param cid       城市
     * @param perPage   每页数量
     * @param pageCount 页码
     * @return
     */
    @GET("p/wedding/index.php/Car/APICarOrderComment/MerchantCommentList")
    Observable<HljHttpResult<HljHttpData<List<CarComment>>>> getCarComments(
            @Query("cid") long cid, @Query("per_page") int perPage, @Query("page") int pageCount);


    /**
     * 婚车套餐列表
     *
     * @param cid       城市id
     * @param perPage   每页数量
     * @param pageCount 页码
     * @return
     */
    @GET("p/wedding/index.php/Car/APICarProduct/CarProductMeal")
    Observable<HljHttpResult<HljHttpData<List<CarProduct>>>> getCarMeals(
            @Query("cid") long cid, @Query("per_page") int perPage, @Query("page") int pageCount);

    /**
     * 婚车列表
     *
     * @param cid       城市id
     * @param perPage   每页数量
     * @param pageCount 页码
     * @param queries   筛选排序参数
     *                  <p>
     *                  color_title     颜色筛选
     *                  brand_id   婚车品牌
     *                  sort      排序条件
     *                  order     排序方式
     * @return
     */
    @GET("p/wedding/index.php/Car/APICarProduct/CarProductOptional")
    Observable<HljHttpResult<HljHttpData<List<CarProduct>>>> getCars(
            @Query("cid") long cid,
            @Query("per_page") int perPage,
            @Query("page") int pageCount,
            @QueryMap Map<String, String> queries);


    /**
     * 婚车颜色筛选项
     *
     * @param cid 城市id
     * @return
     */
    @GET("p/wedding/index.php/Car/APICarProduct/CarColorsList")
    Observable<HljHttpResult<HljHttpData<List<Label>>>> getCarColors(
            @Query("cid") long cid);

    /**
     * 婚车类型筛选项
     *
     * @param cid 城市id
     * @return
     */
    @GET("p/wedding/index.php/Car/APICarProduct/CarBrandsList")
    Observable<HljHttpResult<HljHttpData<List<Label>>>> getCarBrands(
            @Query("cid") long cid);

}
