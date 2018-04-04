package me.suncloud.marrymemo.api.ad;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonElement;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.utils.GsonUtil;

import java.util.List;

import me.suncloud.marrymemo.ad.MadUtil;
import me.suncloud.marrymemo.model.ad.MadPoster;
import okhttp3.ResponseBody;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by wangtao on 2016/12/2.
 */

public class MadApi {

    public static Observable<MadPoster> getSplashMadAd(Context context) {
        return getMadAd(MadUtil.getSplashAdUrl(context), MadUtil.SPLASH_ID);
    }

    public static Observable<MadPoster> getHomeMadAd(Context context) {
        return getMadAd(MadUtil.getHomeAdUrl(context), MadUtil.HOME_ID);
    }

    /**
     * 获取mad 广告信息
     *
     * @param url       mad 广告地址
     * @param adspaceid 广告标志位 E0C5B0B3918B057A；首页 85803FD4E5DAF3DD
     * @return
     */
    private static Observable<MadPoster> getMadAd(String url, final String adspaceid) {
        return HljHttp.getRetrofit()
                .create(MadService.class)
                .getMadAd(url)
                .map(new Func1<JsonElement, MadPoster>() {
                    @Override
                    public MadPoster call(JsonElement jsonElement) {
                        try {
                            MadPoster poster= GsonUtil.getGsonInstance()
                                    .fromJson(jsonElement.getAsJsonObject()
                                            .get(adspaceid), MadPoster.class);
                            trackMadAction(poster.getImgtracking());
                            return poster;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * mad 事件统计
     *
     * @param trackUrls 统计链接数组每条必须访问
     */
    public static void trackMadAction(List<String> trackUrls) {
        if (trackUrls != null && !trackUrls.isEmpty()) {
            Observable.from(trackUrls)
                    .concatMap(new Func1<String, Observable<ResponseBody>>() {
                        @Override
                        public Observable<ResponseBody> call(String s) {
                            return HljHttp.getRetrofit()
                                    .create(MadService.class)
                                    .trackMadAction(s);
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<ResponseBody>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onNext(ResponseBody o) {}
                    });
        }
    }
}
