package com.hunliji.marrybiz.api.revenue;

import com.google.gson.JsonObject;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.entities.HljHttpStatus;
import com.hunliji.marrybiz.model.revenue.Bank;
import com.hunliji.marrybiz.model.revenue.RevenueManager;

import java.util.HashMap;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by $hua_rong on 2017/8/15 0015.
 * 收入管理
 */

public interface RevenueService {

    /**
     * 获取收入管理资金明细
     *
     * @return
     */
    @GET("p/wedding/index.php/Shopadmin/APIShopWallet/withdraw_statistics_v2")
    Observable<HljHttpResult<RevenueManager>> getwithdraw();

    /**
     * 获取银行卡信息
     */
    @GET("p/wedding/Admin/APIMerchantBank/AppBindBank")
    Observable<HljHttpResult<Bank>> getAppBindBank();

    /**
     * 可否提现
     */
    @GET("/p/wedding/index.php/shopadmin/APIShopMerchantWithdraw/can_withdraw")
    Observable<HljHttpResult> getCanWithdraw();

    /**
     * 提交银行卡信息
     *
     * @param map
     * @return
     */
    @POST("p/wedding/Admin/APIMerchantBank/AppBindBank")
    Observable<HljHttpResult> postAppBindBank(@Body HashMap<String, Object> map);

    /**
     * 获得验证码
     *
     * @param object
     * @return
     */
    @POST("p/wedding/index.php/Home/APIUser/sendMsg")
    Observable<HljHttpResult<JsonObject>> getCertifyCode(@Body JsonObject object);

}
