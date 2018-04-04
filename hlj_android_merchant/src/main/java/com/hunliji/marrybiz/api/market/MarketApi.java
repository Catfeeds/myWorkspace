package com.hunliji.marrybiz.api.market;

import com.hunliji.hljcommonlibrary.models.modelwrappers.PosterData;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;
import com.hunliji.marrybiz.model.market.MarketTransform;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 营销管理
 * Created by jinxin on 2016/9/13.
 */
public class MarketApi {

    public static Observable<PosterData> getBanner(String url) {
        return HljHttp.getRetrofit()
                .create(MarketService.class)
                .getBanner(url)
                .map(new HljHttpResultFunc<PosterData>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 商家 用户访问联系订单转化
     *
     * @param merchantId 商家id
     * @param date       用本周一 获取上周客单转化率
     * @return
     */
    public static Observable<HljHttpResult<MarketTransform>> getUserTransform(
            long merchantId, String date) {
        return HljHttp.getRetrofit()
                .create(MarketService.class)
                .getUserTransform(merchantId, date)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
