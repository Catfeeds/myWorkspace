package me.suncloud.marrymemo.api.car;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hunliji.hljcommonlibrary.models.Label;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.utils.GsonUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.model.car.CarComment;
import me.suncloud.marrymemo.model.car.CarProduct;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by wangtao on 2017/4/20.
 */

public class CarApi {


    /**
     * 婚车入口信息收集
     *
     * @param context
     * @param city    城市
     * @param date    日期
     * @param userId  用户id
     * @return
     */
    public static Observable<HljHttpResult<JsonElement>> postCarEntryData(
            Context context, @NonNull City city, @Nullable String date, @Nullable Long userId) {
        JsonObject body = new JsonObject();
        body.addProperty("cid", city.getId());
        body.addProperty("use_date", date);
        body.addProperty("user_id", userId);
        context.getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE)
                .edit()
//                .putString("wedding_car_city",
//                        GsonUtil.buildHljCommonGson()
//                                .toJson(city))
                .putString("wedding_car_time", TextUtils.isEmpty(date) ? "null" : date)
                .apply();
        return HljHttp.getRetrofit()
                .create(CarService.class)
                .postCarEntryData(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }


    /**
     * 婚车评价列表
     *
     * @param cid       城市
     * @param perPage   每页数量
     * @param pageCount 页码
     * @return
     */
    public static Observable<HljHttpData<List<CarComment>>> getCarComments(
            long cid, int perPage, int pageCount) {
        return HljHttp.getRetrofit()
                .create(CarService.class)
                .getCarComments(cid, perPage, pageCount)
                .map(new HljHttpResultFunc<HljHttpData<List<CarComment>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 婚车套餐列表
     *
     * @param cid       城市id
     * @param perPage   每页数量
     * @param pageCount 页码
     */
    public static Observable<HljHttpData<List<CarProduct>>> getCarMeals(
            long cid, int perPage, int pageCount) {
        return HljHttp.getRetrofit()
                .create(CarService.class)
                .getCarMeals(cid, perPage, pageCount)
                .map(new HljHttpResultFunc<HljHttpData<List<CarProduct>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }


    /**
     * 婚车列表
     *
     * @param cid       城市id
     * @param perPage   每页数量
     * @param pageCount 页码
     * @param queries   筛选排序参数
     *                  <p>
     *                  color_title     颜色筛选
     *                  brand_id   婚车品牌
     *                  sort      排序条件
     *                  order     排序方式
     * @return
     */
    public static Observable<HljHttpData<List<CarProduct>>> getCars(
            long cid, int perPage, int pageCount, Map<String, String> queries) {
        if (queries == null) {
            queries = new HashMap<>();
        }
        return HljHttp.getRetrofit()
                .create(CarService.class)
                .getCars(cid, perPage, pageCount, queries)
                .map(new HljHttpResultFunc<HljHttpData<List<CarProduct>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }


    /**
     * 婚车颜色筛选项
     *
     * @param cid 城市id
     * @return
     */
    public static Observable<List<Label>> getCarColors(long cid) {
        return HljHttp.getRetrofit()
                .create(CarService.class)
                .getCarColors(cid)
                .map(new HljHttpResultFunc<HljHttpData<List<Label>>>())
                .map(new Func1<HljHttpData<List<Label>>, List<Label>>() {
                    @Override
                    public List<Label> call(HljHttpData<List<Label>> listHljHttpData) {
                        return listHljHttpData.getData();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 婚车类型筛选项
     *
     * @param cid 城市id
     * @return
     */
    public static Observable<List<Label>> getCarBrands(long cid) {
        return HljHttp.getRetrofit()
                .create(CarService.class)
                .getCarBrands(cid)
                .map(new HljHttpResultFunc<HljHttpData<List<Label>>>())
                .map(new Func1<HljHttpData<List<Label>>, List<Label>>() {
                    @Override
                    public List<Label> call(HljHttpData<List<Label>> listHljHttpData) {
                        return listHljHttpData.getData();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
