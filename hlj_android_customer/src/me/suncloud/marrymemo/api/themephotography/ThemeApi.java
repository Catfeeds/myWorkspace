package me.suncloud.marrymemo.api.themephotography;


import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.PostIdBody;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;

import java.util.List;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.model.themephotography.Guide;
import me.suncloud.marrymemo.model.themephotography.JourneyTheme;
import me.suncloud.marrymemo.model.themephotography.TravelMerchantExposure;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by jinxin on 2016/9/20.
 */

public class ThemeApi {
    /**
     * 旅拍热城 特色风情 旅拍单元 三个可以用同一个方法
     *
     * @param url
     * @return
     */
    public static Observable<JourneyTheme> getTheme(String url) {
        return HljHttp.getRetrofit()
                .create(ThemeService.class)
                .getHotCity(url)
                .map(new HljHttpResultFunc<JourneyTheme>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 全部攻略
     *
     * @param url
     * @param id
     * @param page
     * @return
     */
    public static Observable<HljHttpData<List<Guide>>> getGuideList(
            String url, long id, int page) {
        return HljHttp.getRetrofit()
                .create(ThemeService.class)
                .getGuideList(url, id, page, 20)
                .map(new HljHttpResultFunc<HljHttpData<List<Guide>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    /**
     * 全部商家
     *
     * @param url
     * @param id
     * @param page
     * @return
     */
    public static Observable<HljHttpData<List<Merchant>>> getMerchantList(
            String url, long id, int page) {
        return HljHttp.getRetrofit()
                .create(ThemeService.class)
                .getMerchantList(url, id, page, Constants.PER_PAGE)
                .map(new HljHttpResultFunc<HljHttpData<List<Merchant>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 全部套餐
     *
     * @param url
     * @param id
     * @param page
     * @return
     */
    public static Observable<HljHttpData<List<Work>>> getPackageList(
            String url, long id, int page) {
        return HljHttp.getRetrofit()
                .create(ThemeService.class)
                .getPackageList(url, id, page, Constants.PER_PAGE)
                .map(new HljHttpResultFunc<HljHttpData<List<Work>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 轻奢优品
     *
     * @return
     */
    public static Observable<HljHttpData<List<Work>>> getLightLuxuryList(
            String url, int page) {
        return HljHttp.getRetrofit()
                .create(ThemeService.class)
                .getLightLuxuryList(url, page, Constants.PER_PAGE)
                .subscribeOn(Schedulers.io())
                .map(new HljHttpResultFunc<HljHttpData<List<Work>>>())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 旅拍频道展示页
     *
     * @param page
     * @param perPage
     * @return
     */
    public static Observable<HljHttpData<List<TravelMerchantExposure>>> getTravelMerchantExposuresObb(
            int page, int perPage) {
        return HljHttp.getRetrofit()
                .create(ThemeService.class)
                .getTravelMerchantExposures(page, perPage)
                .subscribeOn(Schedulers.io())
                .map(new HljHttpResultFunc<HljHttpData<List<TravelMerchantExposure>>>())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 增加人气
     *
     * @param body
     * @return
     */
    public static Observable postIncreaseWatchCountObb(PostIdBody body) {
        return HljHttp.getRetrofit()
                .create(ThemeService.class)
                .postIncreaseWatchCount(body)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 一价全包
     *
     * @param tab 1婚庆送四大 2婚纱N件套 3跟妆全员包
     * @return
     */
    public static Observable<HljHttpData<List<Work>>> allInOne(int tab,int page) {
        return HljHttp.getRetrofit()
                .create(ThemeService.class)
                .allInOne(tab,page, HljCommon.PER_PAGE)
                .map(new HljHttpResultFunc<HljHttpData<List<Work>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
