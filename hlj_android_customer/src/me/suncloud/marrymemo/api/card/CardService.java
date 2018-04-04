package me.suncloud.marrymemo.api.card;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hunliji.hljcommonlibrary.models.Mark;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;

import java.util.List;

import me.suncloud.marrymemo.model.card.ShareLink;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by wangtao on 2017/4/1.
 */

public interface CardService {

    /**
     * 获取请帖分享连接
     *
     * @param id 请帖id
     */
    @GET("p/wedding/index.php/Home/APIInvationV2/shareLink")
    Observable<HljHttpResult<ShareLink>> getShareLink(@Query("card_id") long id);


    /**
     * 记录分享事件
     *
     * @param idJson 请帖id
     * @return
     */
    @POST("p/wedding/index.php/Home/APIInvationV2/share")
    Observable<HljHttpResult<JsonElement>> postShareClick(@Body JsonObject idJson);

}
