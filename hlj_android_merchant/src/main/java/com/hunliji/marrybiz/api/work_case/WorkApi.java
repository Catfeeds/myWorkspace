package com.hunliji.marrybiz.api.work_case;

import com.hunliji.hljcommonlibrary.models.PostIdBody;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;
import com.hunliji.marrybiz.model.work_case.ExchangeOrderPostBody;
import com.hunliji.marrybiz.model.work_case.SetSoldOutPostBody;
import com.hunliji.marrybiz.model.work_case.SetTopPostBody;
import com.hunliji.marrybiz.util.work_case.WorkStatusEnum;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 套餐跟案例API接口Http方法汇总
 * Created by chen_bin on 2016/9/6 0006.
 */
public class WorkApi {

    /**
     * 套餐案例列表
     *
     * @param commodityType
     * @param statusEnum
     * @param page
     * @param perPage
     * @return
     */
    public static Observable<HljHttpData<List<Work>>> getWorksObb(
            int commodityType, WorkStatusEnum statusEnum, int page, int perPage) {
        return HljHttp.getRetrofit()
                .create(WorkService.class)
                .getWorks(commodityType,
                        statusEnum.getStatus() < 0 ? null : String.valueOf(statusEnum.getStatus()),
                        statusEnum.isSoldOut(),
                        statusEnum.getRating() == 0 ? null : String.valueOf(statusEnum.getRating()),
                        page,
                        perPage)
                .map(new HljHttpResultFunc<HljHttpData<List<Work>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 套餐、案例上架与下架
     *
     * @param id
     * @param soldOut
     * @return
     */
    public static Observable setSoldOutObb(long id, int soldOut) {
        return HljHttp.getRetrofit()
                .create(WorkService.class)
                .setSoldOut(new SetSoldOutPostBody(id, soldOut))
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 删除套餐、案例
     *
     * @param id
     * @return
     */
    public static Observable deleteWorkObb(long id) {
        return HljHttp.getRetrofit()
                .create(WorkService.class)
                .deleteWork(new PostIdBody(id))
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 排序（新），套餐案例的排序
     *
     * @param ids
     * @return
     */
    public static Observable exchangeOrderObb(String ids) {
        return HljHttp.getRetrofit()
                .create(WorkService.class)
                .exchangeOrder(new ExchangeOrderPostBody(ids))
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 置顶
     *
     * @param id
     * @param isTop
     * @return
     */
    public static Observable setTopObb(long id, int isTop) {
        return HljHttp.getRetrofit()
                .create(WorkService.class)
                .setTop(new SetTopPostBody(id, isTop))
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}