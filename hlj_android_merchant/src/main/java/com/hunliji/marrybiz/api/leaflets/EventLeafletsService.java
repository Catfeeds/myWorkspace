package com.hunliji.marrybiz.api.leaflets;

import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.marrybiz.model.leaflets.EventSignUp;
import com.hunliji.marrybiz.model.leaflets.EventSource;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * 活动微传单
 * Created by jinxin on 2017/5/24 0024.
 */

public interface EventLeafletsService {

    /**
     * 活动微传单列表
     *
     * @param title 标题
     * @return
     */
    @GET("p/wedding/index.php/Admin/APIFinderActivitySource")
    Observable<HljHttpResult<HljHttpData<List<EventSource>>>> getEventLeafletsList(
            @Query("title") String title, @Query("page") int page, @Query("per_page") int perPage);

    /**
     * 活动报名名单列表
     * @return
     */
    @GET("p/wedding/index.php/Admin/APIFinderActivitySignUp/list")
    Observable<HljHttpResult<EventSignUp>> getEventLeafletApplyList(
            @Query("with_outside")int withOutside,
            @Query("activity_id") long activityId,
            @Query("page") int page,
            @Query("per_page") int perPage);
}
