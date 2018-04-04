package me.suncloud.marrymemo.api.notification;

import com.hunliji.hljcommonlibrary.models.realm.Notification;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;

import java.util.List;

import me.suncloud.marrymemo.model.HintData;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Suncloud on 2016/9/2.
 */
public class NotificationApi {
    /**
     * 获取通知 Observable
     */
    public static Observable<List<Notification>> getNotifications(long lastId) {
        return HljHttp.getRetrofit()
                .create(NotificationService.class)
                .getNotifications(lastId)
                .map(new HljHttpResultFunc<List<Notification>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    /**
     * 获取红点提示的相关数据（与通知有一点相关性，所以将其接口声明放在这里）
     *
     * @return
     */
    public static Observable<HintData> getHintData() {
        return HljHttp.getRetrofit()
                .create(NotificationService.class)
                .getHintData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
