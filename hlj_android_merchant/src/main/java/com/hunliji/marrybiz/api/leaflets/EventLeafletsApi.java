package com.hunliji.marrybiz.api.leaflets;

import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.model.leaflets.EventSignUp;
import com.hunliji.marrybiz.model.leaflets.EventSource;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 活动微传单api
 * Created by jinxin on 2017/5/24 0024.
 */

public class EventLeafletsApi {

    /**
     * 活动微传单列表
     *
     * @param title 标题
     * @return
     */
    public static Observable<HljHttpData<List<EventSource>>> getEventLeafletsList(
            String title, int page) {
        return HljHttp.getRetrofit()
                .create(EventLeafletsService.class)
                .getEventLeafletsList(title, page, Constants.PER_PAGE)
                .map(new HljHttpResultFunc<HljHttpData<List<EventSource>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 活动报名名单列表
     * @param page
     * @return
     */
    public static Observable<EventSignUp> getEventLeafletApplyList(long activityId,int page) {
        return HljHttp.getRetrofit()
                .create(EventLeafletsService.class)
                .getEventLeafletApplyList(1,activityId,page, Constants.PER_PAGE)
                .map(new HljHttpResultFunc<EventSignUp>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
