package me.suncloud.marrymemo.api.topBrand;


import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;

import java.util.List;

import me.suncloud.marrymemo.model.topBrand.CostEffective;
import me.suncloud.marrymemo.model.topBrand.WeddingBrand;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 品牌馆 性价比top
 * Created by jinxin on 2016/11/14.
 */

public class TopBrandApi {

    /**
     * 品牌馆
     *
     * @return
     */
    public static Observable<WeddingBrand> getBrand() {
        return HljHttp.getRetrofit()
                .create(TopBrandServices.class)
                .getBrand()
                .map(new HljHttpResultFunc<WeddingBrand>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 性价比
     *
     * @return
     */
    public static Observable<List<CostEffective>> getCostEffective() {
        return   HljHttp.getRetrofit()
                .create(TopBrandServices.class)
                .getCostEffective()
                .map(new HljHttpResultFunc<List<CostEffective>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
