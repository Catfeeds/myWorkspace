package me.suncloud.marrymemo.api.notification;

import com.hunliji.hljcommonlibrary.models.realm.Notification;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;

import java.util.List;

import me.suncloud.marrymemo.model.HintData;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Suncloud on 2016/9/2.
 */
public interface NotificationService {
    /**
     * 获取通知
     * @param lastId 最后一条通知id
     */
    @GET("p/wedding/index.php/home/APINotification/list")
    Observable<HljHttpResult<List<Notification>>> getNotifications(
            @Query("last_id") long lastId);

    /**
     * 获取红点提示的相关数据（与通知有一点相关性，所以将其接口声明放在这里）
     * @return
     */
    @GET("p/wedding/index.php/home/APIMine/GetHintData")
    Observable<HintData> getHintData();
}
