package me.suncloud.marrymemo.api.caseapi;

import com.google.gson.JsonElement;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * 案例相关
 * Created by jinxin on 2018/2/5 0005.
 */

public class CaseApi {

    /**
     * 案例详情
     *
     * @param id
     * @return
     */
    public static Observable<JsonElement> getCaseDetail(long id) {
        return HljHttp.getRetrofit()
                .create(CaseService.class)
                .getCaseDetail(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * /**
     * 推荐案例
     */
    public static Observable<JsonElement> getRecommendCase(
            long id, String kind, String sort, int page, int perPage) {
        return HljHttp.getRetrofit()
                .create(CaseService.class)
                .getRecommendCase(id, kind, sort, page, perPage)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    /**
     * 推荐套餐
     *
     * @param caseId 案例id
     * @return
     */
    public static Observable<HljHttpData<List<Work>>> getRecommendWork(long caseId) {
        return HljHttp.getRetrofit()
                .create(CaseService.class)
                .getRecommendWork(caseId)
                .map(new HljHttpResultFunc<HljHttpData<List<Work>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
