package me.suncloud.marrymemo.api.card;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hunliji.hljcommonlibrary.models.Mark;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;

import java.util.List;

import me.suncloud.marrymemo.model.card.ShareLink;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by wangtao on 2017/4/1.
 */

public class CardApi {

    public static Observable<ShareLink> getShareLinkObb(long id) {
        return HljHttp.getRetrofit()
                .create(CardService.class)
                .getShareLink(id)
                .map(new HljHttpResultFunc<ShareLink>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 记录分享事件
     *
     * @param id 请帖id
     * @return
     */
    public static Observable postShareClick(long id) {
        JsonObject idJson = new JsonObject();
        idJson.addProperty("card_id", id);
        return HljHttp.getRetrofit()
                .create(CardService.class)
                .postShareClick(idJson)
                .map(new HljHttpResultFunc<JsonElement>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
