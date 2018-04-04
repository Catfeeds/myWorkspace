package me.suncloud.marrymemo.api.experience;


import com.hunliji.hljcommonlibrary.models.Comment;
import com.hunliji.hljcommonlibrary.models.event.EventInfo;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.entities.HljResultAction;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;

import java.util.List;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.model.experience.ExperiencePhoto;
import me.suncloud.marrymemo.model.experience.ExperienceReservationBody;
import me.suncloud.marrymemo.model.experience.ExperienceShop;
import me.suncloud.marrymemo.model.experience.Planner;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 体验店api
 * Created by jinxin on 2016/10/28.
 */

public class ExperienceApi {
    /**
     * 体验店入口接口
     *
     * @return
     */
    public static Observable<ExperienceShop> getStoreAtlas(final long id) {
        return HljHttp.getRetrofit()
                .create(ExperienceService.class)
                .getStoreAtlas(id)
                .map(new HljHttpResultFunc<ExperienceShop>())
                .map(new Func1<ExperienceShop, ExperienceShop>() {
                    @Override
                    public ExperienceShop call(ExperienceShop experienceShop) {
                        if(experienceShop.getStore()!=null){
                            experienceShop.getStore().setStoreId(id);
                        }
                        return experienceShop;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 全部活动
     *
     * @return
     */
    public static Observable<HljHttpData<List<EventInfo>>> getEventList(long id,int page) {
        return HljHttp.getRetrofit()
                .create(ExperienceService.class)
                .getEventList(id,page, Constants.PER_PAGE)
                .map(new HljHttpResultFunc<HljHttpData<List<EventInfo>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取全部印象评论列表
     *
     * @param page
     * @param test_store_id 体验店id
     * @return
     */
    public static Observable<HljHttpData<List<Comment>>> getCommentList(
            int page, long test_store_id) {
        return HljHttp.getRetrofit()
                .create(ExperienceService.class)
                .getMerchantCommentList(page, Constants.PER_PAGE, test_store_id)
                .map(new HljHttpResultFunc<HljHttpData<List<Comment>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 获取统筹师详情
     *
     * @param id 统筹师id
     * @return
     */
    public static Observable<Planner> getPlannerDetail(long id) {
        return HljHttp.getRetrofit()
                .create(ExperienceService.class)
                .getPlannerDetail(id)
                .map(new HljHttpResultFunc<Planner>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 全部照片 带分页
     *
     * @param id
     * @return
     */
    public static Observable<HljHttpData<List<ExperiencePhoto>>> getShopPhoto(long id, int page) {
        return HljHttp.getRetrofit()
                .create(ExperienceService.class)
                .getShopPhoto(id, page, Constants.PER_PAGE)
                .map(new HljHttpResultFunc<HljHttpData<List<ExperiencePhoto>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<HljHttpResult<HljResultAction>> resveration
            (ExperienceReservationBody body) {
        return HljHttp.getRetrofit()
                .create(ExperienceService.class)
                .reservation(body)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    /**
     * 获得统筹师列表(带搜索)
     *
     * @param fullName
     * @return
     */
    public static Observable<HljHttpData<List<Planner>>> getPlannerList(long id,String fullName) {
        return HljHttp.getRetrofit()
                .create(ExperienceService.class)
                .getPlannerList(id,fullName)
                .map(new HljHttpResultFunc<HljHttpData<List<Planner>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获得体验店评价
     *
     * @param id
     * @return
     */
    public static Observable<HljHttpData<List<Comment>>> getComments(long id) {
        return HljHttp.getRetrofit()
                .create(ExperienceService.class)
                .getComments(id)
                .map(new HljHttpResultFunc<HljHttpData<List<Comment>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取统筹师评论列表
     *
     * @param plannedId
     * @param page
     * @return
     */
    public static Observable<HljHttpData<List<Comment>>> getPlannerCommentList(
            long plannedId, int page) {
        return HljHttp.getRetrofit()
                .create(ExperienceService.class)
                .getPlannerCommentList(plannedId, "TestStoreOrganizer", page, Constants.PER_PAGE)
                .map(new HljHttpResultFunc<HljHttpData<List<Comment>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
