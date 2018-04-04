package com.hunliji.marrybiz.api.market;

import com.hunliji.hljcommonlibrary.models.modelwrappers.PosterData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.marrybiz.model.market.MarketTransform;

import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

/**
 * 营销管理
 * Created by jinxin on 2016/9/13.
 */
public interface MarketService {

    @GET
    public Observable<HljHttpResult<PosterData>> getBanner(@Url String url);


    /**
     * 商家 用户访问联系订单转化
     *
     * @param merchantId
     * @param date
     * @return
     */
    @GET("data/v1/merchant_new?app=user_transform")
    Observable<HljHttpResult<MarketTransform>> getUserTransform(
            @Query("merchant_id") long merchantId, @Query("date") String date);

}
