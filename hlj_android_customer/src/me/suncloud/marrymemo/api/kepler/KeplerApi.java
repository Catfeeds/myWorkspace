package me.suncloud.marrymemo.api.kepler;

import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.entities.HljResultAction;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;

import me.suncloud.marrymemo.model.kepler.Kepler;
import me.suncloud.marrymemo.model.kepler.KeplerBody;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 平安普惠
 * Created by jinxin on 2016/11/4.
 */

public class KeplerApi {

    /**
     * 平安普惠 提交用户信息
     *
     * @param kepler
     * @return
     */
    public static Observable<HljResultAction> applyKepler(
            KeplerBody kepler) {
        return HljHttp.getRetrofit()
                .create(KeplerService.class)
                .applyKepler(kepler)
                .map(new HljHttpResultFunc<HljResultAction>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获得用户平安普惠信息
     *
     * @return
     */
    public static Observable<Kepler> lastApply() {
        return HljHttp.getRetrofit()
                .create(KeplerService.class)
                .lastApply()
                .map(new HljHttpResultFunc<Kepler>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
