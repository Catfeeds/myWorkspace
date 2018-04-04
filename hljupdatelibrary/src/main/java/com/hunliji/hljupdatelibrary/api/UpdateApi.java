package com.hunliji.hljupdatelibrary.api;

import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljupdatelibrary.models.UpdateInfo;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Suncloud on 2016/8/1.
 */
public class UpdateApi {

    /**
     * 获取最新版本
     *
     * @param versionCode 版本号
     * @param channel     渠道
     */
    public static Observable<UpdateInfo> getUpdateInfo(int versionCode, String channel) {
        return HljHttp.getRetrofit()
                .create(UpdateService.class)
                .getUpdateInfo(versionCode, channel)
                .map(new Func1<HljHttpResult<UpdateInfo>, UpdateInfo>() {
                    @Override
                    public UpdateInfo call(HljHttpResult<UpdateInfo> updateInfoHljHttpResult) {
                        return updateInfoHljHttpResult.getData();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }
}
