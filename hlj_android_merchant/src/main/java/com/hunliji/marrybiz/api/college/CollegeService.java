package com.hunliji.marrybiz.api.college;

import com.google.gson.JsonElement;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.marrybiz.model.college.CollegeItem;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by luohanlin on 2017/11/22.
 */

public interface CollegeService {

    /**
     * 商学院顶部banner
     *
     * @return
     */
    @GET("p/wedding/index.php/Admin/APIBusinessCollege/banner")
    Observable<HljHttpResult<HljHttpData<List<CollegeItem>>>> getCollegeBanners();

    @GET("p/wedding/index.php/Admin/APIBusinessCollege/index")
    Observable<HljHttpResult<HljHttpData<List<CollegeItem>>>> getCollegeItems(
            @Query("type") int type, @Query("page") int page, @Query("per_page") int perPage);

    /**
     * 获取新课程个数
     * @return
     */
    @GET("p/wedding/index.php/Admin/APIBusinessCollege/new_lessons")
    Observable<HljHttpResult<JsonElement>> getNewLessonCount();
}
