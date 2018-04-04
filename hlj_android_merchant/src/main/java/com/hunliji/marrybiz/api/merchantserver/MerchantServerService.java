package com.hunliji.marrybiz.api.merchantserver;

import com.google.gson.JsonElement;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.marrybiz.model.merchantservice.BondInfo;
import com.hunliji.marrybiz.model.merchantservice.MerchantServer;
import com.hunliji.marrybiz.model.merchantservice.WeAppDetail;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * 商家服务
 * Created by jinxin on 2018/1/29 0029.
 */

public interface MerchantServerService {

    /**
     * 获得商家服务列表
     *
     * @return
     */
    @GET("p/wedding/index.php/Admin/APIMerchantServer/list")
    Observable<HljHttpResult<HljHttpData<List<MerchantServer>>>> getMerchantServerList();

    /**
     * 获得商家旗舰版支付费用
     *
     * @return
     */
    @POST("p/wedding/index.php/admin/APIMerchantPro/pay_money")
    Observable<HljHttpResult<JsonElement>> getMerchantProMoney();

    /**
     * 获得小程序详情
     *
     * @return
     */
    @GET("p/wedding/Admin/APIMerchantWeapp")
    Observable<HljHttpResult<WeAppDetail>> getMerchantWeAppDetail();

    /**
     * 获取保证金信息
     *
     * @return
     */
    @GET("p/wedding/index.php/Shopadmin/APIShopWallet/withdraw_statistics_v2")
    Observable<HljHttpResult<BondInfo>> getBondInfo();
}
