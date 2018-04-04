package me.suncloud.marrymemo.api.home;

import com.google.gson.JsonElement;
import com.hunliji.hljcommonlibrary.models.live.LiveChannel;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;

import java.util.List;
import java.util.Map;

import me.suncloud.marrymemo.model.home.FeedList;
import me.suncloud.marrymemo.model.home.HomeFeed;
import me.suncloud.marrymemo.model.home.HomeFeedList;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * 首页接口
 * Created by wangtao on 2016/12/5.
 */

public interface HomeService {

    /**
     * 首页分类
     */
    @GET("p/wedding/index.php/Home/APIFrontPageFeed/property_list")
    Observable<HljHttpResult<JsonElement>> getFeedProperties(@Query("city") long city);


    /**
     * feed流
     *
     * @param cid        城市id
     * @param propertyId 首页分类id
     * @param page       分页
     * @param lastTime   请求时间戳
     */
    @GET("p/wedding/index.php/home/APIFrontPageFeed")
    Observable<HljHttpResult<FeedList>> getFeedList(
            @Query("cid") long cid,
            @Query("property_id") String propertyId,
            @Query("page") int page,
            @Query("per_page") int perPage,
            @Query("last_time") Long lastTime);

    /**
     * 获取首页feed流
     *
     * @param cid
     * @param propertyIdStr
     * @param page
     * @param perPage
     * @return
     */
    @GET("p/wedding/index.php/home/APIFrontPageFeed/indexV3")
    Observable<HljHttpResult<HomeFeedList<List<HomeFeed>>>> getHomeFeedList(
            @QueryMap Map<String,Object> map,
            @Query("page") int page,
            @Query("per_page") int perPage);

    /**
     * 获取最新的直播信息
     * http://doc.hunliji.com/workspace/myWorkspace.do?projectId=25#4051
     *
     * @return
     */
    @GET("p/wedding/Home/APILiveChannel/latest_live")
    Observable<HljHttpResult<LiveChannel>> getLatestLiveInfo();
}
