package com.hunliji.hljupdatelibrary.api;

import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljupdatelibrary.models.UpdateInfo;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Suncloud on 2016/8/1.
 */
public interface UpdateService {


    /**
     * 获取最新版本
     *
     * @param versionCode 版本号
     * @param channel     渠道
     */

    @GET("p/wedding/index.php/Home/APIAppVersion/lastVersion")
    Observable<HljHttpResult<UpdateInfo>> getUpdateInfo(
            @Query("version_code") int versionCode,
            @Query("channel") String channel);
}
