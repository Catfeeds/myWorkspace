package com.hunliji.marrybiz.api.weddingcar;

import com.google.gson.JsonElement;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;
import com.hunliji.marrybiz.model.weddingcar.WeddingCarOrderDetail;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by jinxin on 2018/1/4 0004.
 */

public class WeddingCarApi {

    /**
     * 获得订单详情
     *
     * @param id
     * @return
     */
  public static   Observable<WeddingCarOrderDetail> getWeddingCarOrderDetail(long id) {
        return HljHttp.getRetrofit()
                .create(WeddingCarService.class)
                .getWeddingCarOrderDetail(id)
                .map(new HljHttpResultFunc<WeddingCarOrderDetail>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 改价
     * id	订单id
     * price 价格
     *
     * @return
     */
    public static Observable<HljHttpResult> weddingCarChangePrice(
            long id, double price) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("price", price);
        return HljHttp.getRetrofit()
                .create(WeddingCarService.class)
                .weddingCarChangePrice(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 拒绝接单
     * id
     *
     * @return
     */
    public static  Observable<HljHttpResult> refuseOrder(long id) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        return HljHttp.getRetrofit()
                .create(WeddingCarService.class)
                .refuseOrder(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 同意接单
     * bride_address	新娘地址
     * extra	其他
     * groom_address	新郎地址
     * hotel	酒店地址
     * id	订单id
     * way	途径
     *
     * @return
     */
    public static Observable<HljHttpResult<JsonElement>> takeOrder(
            long id,
            String brideAddress,
            String extra,
            String groomAddress,
            String hotel,
            String way) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("bride_address", brideAddress);
        params.put("extra", extra);
        params.put("groom_address", groomAddress);
        params.put("way", way);
        params.put("hotel", hotel);
        return HljHttp.getRetrofit()
                .create(WeddingCarService.class)
                .takeOrder(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}

