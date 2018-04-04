package me.suncloud.marrymemo.api.ad;

import com.google.gson.JsonElement;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by wangtao on 2016/12/2.
 */

public interface MadService {

    /**
     * 获取mad 广告信息
     * @param url mad 广告地址
     * @return
     */
    @GET
    Observable<JsonElement> getMadAd(@Url String url);

    /**
     * mad 统计
     * @param url 统计链接
     * @return
     */
    @GET
    Observable<ResponseBody> trackMadAction(@Url String url);
}
