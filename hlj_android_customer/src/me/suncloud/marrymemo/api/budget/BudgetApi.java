package me.suncloud.marrymemo.api.budget;


import com.google.gson.JsonElement;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;

import retrofit2.http.GET;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by jinxin on 2017/11/20 0020.
 */

public class BudgetApi {

    /**
     * 获得结婚预算信息
     *
     * @return
     */
    public static Observable<JsonElement> getBudgetInfo() {
        return HljHttp.getRetrofit()
                .create(BudgetService.class)
                .getBudgetInfo()
                .map(new HljHttpResultFunc<JsonElement>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获得分享信息
     * @param id
     * @return
     */
    public static Observable<JsonElement> getShareInfo(long id){
        return HljHttp.getRetrofit()
                .create(BudgetService.class)
                .getShareInfo(id)
                .map(new HljHttpResultFunc<JsonElement>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    /**
     * 获得结婚预算类别
     *
     * @return
     */
    public static Observable<JsonElement> getBudgetCategory(){
        return HljHttp.getRetrofit()
                .create(BudgetService.class)
                .getBudgetCategory()
                .map(new HljHttpResultFunc<JsonElement>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
