package com.hunliji.hljpaymentlibrary.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hunliji.hljcommonlibrary.models.BankCard;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by Suncloud on 2016/8/1.
 */
public interface PaymentService {

    /**
     * 获取支付参数
     */
    @POST
    Observable<HljHttpResult<JsonElement>> postPayParams(
            @Url String path, @Body JsonObject jsonParams);

    /**
     * 绑定银行卡列表
     */
    @GET("p/wedding/index.php/Home/APIUserBankInfo")
    Observable<HljHttpResult<List<BankCard>>> getBindBanks();

    /**
     * 验证支付密码
     */
    @FormUrlEncoded
    @POST("p/wedding/index.php/Home/APIUserSecurity/checkPwd")
    Observable<HljHttpResult<Object>> checkPassword(@Field("password") String password);


    /**
     * 首次设置密码
     */
    @POST("p/wedding/index.php/Home/APIUserSecurity/setPwd")
    Observable<HljHttpResult<Object>> setPassword(@Body JsonObject jsonParams);

    /**
     * 忘记密码
     */
    @POST("p/wedding/index.php/Home/APIUserSecurity/forgetPwd")
    Observable<HljHttpResult<Object>> findPassword(@Body JsonObject jsonParams);

    /**
     * 修改密码
     */
    @POST("p/wedding/index.php/Home/APIUserSecurity/setNewPwd")
    Observable<HljHttpResult<Object>> resetPassword(@Body JsonObject jsonParams);


    /**
     * 获取银行卡信息
     */
    @GET("p/wedding/index.php/Home/APIUserBankInfo/bankCardBin")
    Observable<HljHttpResult<BankCard>> getCardInfo(@Query("card_no") String cardNo);

    /**
     * 支持的银行卡列表
     */
    @GET("p/wedding/index.php/Home/APIUserBankInfo/bankList")
    Observable<HljHttpResult<List<BankCard>>> getSupportCards();


    /**
     * 获取商家钱包金额
     */
    @GET("p/wedding/index.php/admin/APIMerchant/wallet")
    Observable<HljHttpResult<JsonElement>> getMerchantWallet();

    /**
     * 获取用户钱包金额
     */
    @GET("p/wedding/index.php/Shop/APIRedPacket/myWallet")
    Observable<HljHttpResult<JsonElement>> getUserWallet();

    /**
     * 招行支付验证
     */
    @GET("p/wedding/index.php/Home/APIOrder/querysingleorder")
    Observable<HljHttpResult<JsonElement>> getOrderQuery(
            @Query("out_trade_no") String outTradeNo,
            @Query("source") String source);
}
