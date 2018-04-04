package me.suncloud.marrymemo.api.themephotography;

import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.PostIdBody;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;

import java.util.List;

import me.suncloud.marrymemo.model.themephotography.Guide;
import me.suncloud.marrymemo.model.themephotography.JourneyTheme;
import me.suncloud.marrymemo.model.themephotography.TravelMerchantExposure;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by jinxin on 2016/9/20.
 */

public interface ThemeService {
    /**
     * 旅拍热城
     *
     * @param url
     * @return
     */
    @GET
    Observable<HljHttpResult<JourneyTheme>> getHotCity(@Url String url);

    /**
     * 全部攻略
     *
     * @param url
     * @param id
     * @return
     */
    @GET
    Observable<HljHttpResult<HljHttpData<List<Guide>>>> getGuideList(
            @Url String url,
            @Query("id") long id,
            @Query("page") int page,
            @Query("per_page") int perPage);

    /**
     * 全部商家
     *
     * @return
     */
    @GET
    Observable<HljHttpResult<HljHttpData<List<Merchant>>>> getMerchantList(
            @Url String url,
            @Query("id") long id,
            @Query("page") int page,
            @Query("per_page") int perPage);


    /**
     * 全部套餐
     *
     * @return
     */
    @GET
    Observable<HljHttpResult<HljHttpData<List<Work>>>> getPackageList(
            @Url String url,
            @Query("id") long id,
            @Query("page") int page,
            @Query("per_page") int perPage);


    /**
     * 轻奢优品
     *
     * @return
     */
    @GET
    Observable<HljHttpResult<HljHttpData<List<Work>>>> getLightLuxuryList(
            @Url String url, @Query("page") int page, @Query("per_page") int perPage);
    /**
     * 旅拍频道展示页
     *
     * @param page
     * @param perPage
     * @return
     */
    @GET("p/wedding/Home/APITravelChannel/list")
    Observable<HljHttpResult<HljHttpData<List<TravelMerchantExposure>>>> getTravelMerchantExposures(
            @Query("page") int page, @Query("per_page") int perPage);

    /**
     * 增加人气
     *
     * @param body
     * @return
     */
    @POST("p/wedding/Home/APITravelChannel/increase_watch_count")
    Observable<HljHttpResult> postIncreaseWatchCount(@Body PostIdBody body);

    /**
     * 一价全包
     * @param tab 1婚庆送四大 2婚纱N件套 3跟妆全员包
     * @return
     */
    @GET("p/wedding/index.php/home/APISearchV3/allInOne")
    Observable<HljHttpResult<HljHttpData<List<Work>>>> allInOne(
            @Query("tab") int tab,
            @Query("page") int page,
            @Query("per_page") int perPage);
}
