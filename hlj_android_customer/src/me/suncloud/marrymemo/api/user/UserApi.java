package me.suncloud.marrymemo.api.user;

import com.google.gson.JsonElement;
import com.hunliji.hljcommonlibrary.models.ServiceComment;
import com.hunliji.hljcommonlibrary.models.userprofile.UserPartnerWrapper;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;

import java.util.List;
import java.util.Map;

import me.suncloud.marrymemo.model.Reservation;
import me.suncloud.marrymemo.model.user.CountStatistics;
import me.suncloud.marrymemo.model.user.UserDynamic;
import me.suncloud.marrymemo.model.user.UserStatistics;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by werther on 16/8/29.
 */
public class UserApi {

    /**
     * 获取用户账户信息的Observable
     * 没有返回信息错误信息判断
     *
     * @return
     */
    public static Observable<UserPartnerWrapper> getUserProfileObb(long userId) {
        return HljHttp.getRetrofit()
                .create(UserService.class)
                .getUserProfile(userId)
                .map(new HljHttpResultFunc<UserPartnerWrapper>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    /**
     * 用户评价列表
     *
     * @param userId
     * @param page
     * @param perPage
     * @return
     */
    public static Observable<HljHttpData<List<ServiceComment>>> getUserCommentsObb(
            long userId, int page, int perPage) {
        return HljHttp.getRetrofit()
                .create(UserService.class)
                .getUserComments(userId, page, perPage)
                .map(new HljHttpResultFunc<HljHttpData<List<ServiceComment>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 我的预约列表
     *
     * @param propertyId
     * @param page
     * @param perPage
     * @return
     */
    public static Observable<HljHttpData<List<Reservation>>> getMyReservationsObb(
            long propertyId, int page, int perPage) {
        return HljHttp.getRetrofit()
                .create(UserService.class)
                .getMyReservations(propertyId, page, perPage)
                .map(new HljHttpResultFunc<HljHttpData<List<Reservation>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 修改个人信息
     *
     * @param map
     * @return
     */
    public static Observable<JsonElement> editMyBaseInfoObb(Map<String, Object> map) {
        return HljHttp.getRetrofit()
                .create(UserService.class)
                .editMyBaseInfo(map)
                .map(new HljHttpResultFunc<JsonElement>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 一些数量的同步
     *
     * @return
     */
    public static Observable<UserStatistics> getUserStatisticsObb() {
        return HljHttp.getRetrofit()
                .create(UserService.class)
                .getUserStatistics()
                .map(new HljHttpResultFunc<UserStatistics>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 列表总数
     *
     * @param type
     * @return
     */
    public static Observable<CountStatistics> getCountStatisticsObb(String type) {
        return HljHttp.getRetrofit()
                .create(UserService.class)
                .getCountStatistics(type)
                .map(new HljHttpResultFunc<CountStatistics>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 用户动态未读数
     *
     * @return
     */
    public static Observable<Integer> getUserDynamicUnreadCountObb() {
        return HljHttp.getRetrofit()
                .create(UserService.class)
                .getUserDynamicUnreadCount()
                .map(new HljHttpResultFunc<JsonElement>())
                .map(new Func1<JsonElement, Integer>() {
                    @Override
                    public Integer call(JsonElement jsonElement) {
                        return CommonUtil.getAsInt(jsonElement, "count");
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 用户动态列表
     *
     * @param page
     * @param perPage
     * @return
     */
    public static Observable<HljHttpData<List<UserDynamic>>> getUserDynamicsObb(
            int page, int perPage) {
        return HljHttp.getRetrofit()
                .create(UserService.class)
                .getUserDynamics(page, perPage)
                .map(new HljHttpResultFunc<HljHttpData<List<UserDynamic>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 分享App后请求接口增加积分
     *
     * @return
     */
    public static Observable<HljHttpResult<JsonElement>> afterShareApp() {
        return HljHttp.getRetrofit()
                .create(UserService.class)
                .afterShareApp()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
