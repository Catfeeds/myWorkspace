package me.suncloud.marrymemo.api.brigade;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.GsonUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import me.suncloud.marrymemo.model.wrappers.LimitBuyList;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by hua_rong on 2017/7/24.
 * 旅拍本周热卖
 */

public class BrigadeApi {

    /**
     * 获取旅拍封面图以及列表
     *
     * @return
     */
    public static Observable<HljHttpData<List<Work>>> getWeekHots() {
        return HljHttp.getRetrofit()
                .create(BrigadeService.class)
                .getWeekHots()
                .map(new HljHttpResultFunc<HljHttpData<List<Work>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<JsonElement> getTravelDetailObb(long id) {
        return HljHttp.getRetrofit()
                .create(BrigadeService.class)
                .getTravelDetail(id)
                .map(new HljHttpResultFunc<JsonElement>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

}
