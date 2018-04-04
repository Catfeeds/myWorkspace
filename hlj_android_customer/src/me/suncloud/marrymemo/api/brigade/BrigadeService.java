package me.suncloud.marrymemo.api.brigade;

import com.google.gson.JsonElement;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by hua_rong on 2017/7/24.
 * 旅拍本周热卖
 */

public interface BrigadeService {

    /**
     * 获取旅拍封面图以及列表
     *
     * @return
     */
    @GET("/p/wedding/index.php/home/APIContentBundle/weekHots")
    Observable<HljHttpResult<HljHttpData<List<Work>>>> getWeekHots();

    @GET("p/wedding/index.php/Home/APITravel/getTravelDetail")
    Observable<HljHttpResult<JsonElement>> getTravelDetail(@Query("id") long id);
}
