package me.suncloud.marrymemo.api.wallet;


import com.google.gson.JsonElement;
import com.hunliji.hljcardcustomerlibrary.models.Balance;
import com.hunliji.hljcardcustomerlibrary.models.CardBalance;
import com.hunliji.hljcardcustomerlibrary.models.CardBalanceDetail;
import com.hunliji.hljcardcustomerlibrary.models.CardGift;
import com.hunliji.hljcardcustomerlibrary.models.RedPacket;
import com.hunliji.hljcardcustomerlibrary.models.UserBalanceDetail;
import com.hunliji.hljcardcustomerlibrary.models.UserGift;
import com.hunliji.hljcardcustomerlibrary.models.WithDraw;
import com.hunliji.hljcardcustomerlibrary.models.WithDrawCash;
import com.hunliji.hljcardcustomerlibrary.models.WithdrawParam;
import com.hunliji.hljcardcustomerlibrary.models.WithdrawPost;
import com.hunliji.hljcommonlibrary.models.BindInfo;
import com.hunliji.hljcommonlibrary.models.PostAddressId;
import com.hunliji.hljcommonlibrary.models.coupon.CouponInfo;
import com.hunliji.hljhttplibrary.entities.HljHttpCardData;
import com.hunliji.hljhttplibrary.entities.HljHttpCountData;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;

import java.util.List;
import java.util.Map;

import me.suncloud.marrymemo.model.wallet.CouponRecord;
import me.suncloud.marrymemo.model.wallet.FinancialMarketGroup;
import me.suncloud.marrymemo.model.wallet.FinancialProduct;
import me.suncloud.marrymemo.model.wallet.Wallet;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * 红包、优惠券Service
 * Created by chen_bin on 2016/9/9 0009.
 */
public interface WalletService {

    /**
     * 我的红包列表
     *
     * @param status
     * @param page
     * @param perPage
     * @return
     */
    @GET("p/wedding/index.php/Shop/APIRedPacket/MyAllRedPacketsOfCenter")
    Observable<HljHttpResult<HljHttpData<List<RedPacket>>>> getMyRedPacketList(
            @Query("status") int status, @Query("page") int page, @Query("per_page") int perPage);

    /**
     * 我的优惠券列表
     *
     * @param type    0:未使用 1:已使用 2：已过期
     * @param page
     * @param perPage
     * @return
     */
    @GET("p/wedding/index.php/home/APIUserCoupon/List")
    Observable<HljHttpResult<HljHttpData<List<CouponRecord>>>> getMyCouponList(
            @Query("type") int type, @Query("page") int page, @Query("per_page") int perPage);

    /**
     * 商家优惠券
     *
     * @param merchantId
     * @return
     */
    @GET("p/wedding/index.php/home/APIUserCoupon/MerCoupon")
    Observable<HljHttpResult<List<CouponInfo>>> getMerchantCouponList(
            @Query("mer_id") long merchantId);

    /**
     * 领取优惠券
     *
     * @param ids
     * @return
     */
    @GET("p/wedding/index.php/home/APIUserCoupon/ReceiveCoupon")
    Observable<HljHttpResult> receiveCoupon(@Query("coupon_id") String ids);

    /**
     * 领取红包
     *
     * @param exchangeCode
     * @return
     */
    @GET("p/wedding/index.php/Shop/APIRedPacket/ExchangeRedPacket")
    Observable<HljHttpResult> receiveRedPacket(@Query("exchangeCode") String exchangeCode);

    /**
     * 使用优惠券
     *
     * @param id
     * @return
     */
    @GET("p/wedding/index.php/home/APIUserCoupon/UsedCoupon")
    Observable<HljHttpResult> useCoupon(@Query("id") long id, @Query("type") int type);

    /**
     * 领券中心，优惠券跟红包的列表
     *
     * @return
     */
    @GET("p/wedding/index.php/home/APICouponCenter/user_coupons_n_redpackets")
    Observable<HljHttpResult<JsonElement>> getCouponsAndRedPackets(@Query("id") long id);

    /**
     * 礼物列表
     *
     * @return
     */
    @GET("p/wedding/index.php/Home/APICardGift2/index")
    Observable<HljHttpResult<HljHttpData<List<CardGift>>>> getCardGifts();

    /**
     * 我的礼物
     *
     * @return
     */
    @GET("p/wedding/index.php/Home/APICardGift2/my_gifts")
    Observable<HljHttpResult<HljHttpCountData<List<UserGift>>>> getUserGifts();

    /**
     * 我的礼金
     *
     * @return
     */
    @GET("p/wedding/index.php/Home/APICardCashGift/my_gifts")
    Observable<HljHttpResult<HljHttpCountData<List<UserGift>>>> getUserCashGifts();

    /**
     * 用户余额
     *
     * @return
     */
    @GET("p/wedding/index.php/Home/APIUserBalance/index")
    Observable<HljHttpResult<HljHttpCountData<List<Balance>>>> getBalance();

    /**
     * 提现礼物余额
     *
     * @return
     */
    @POST("p/wedding/home/WeixinWall/withdraw")
    Observable<HljHttpResult<WithDraw>> withdrawGift(
            @Body WithdrawPost post);

    /**
     * 提现礼金余额
     *
     * @return
     */
    @POST("p/wedding/home/APICardCashGift/withdraw")
    Observable<HljHttpResult<WithDrawCash>> withdrawCash(
            @Body WithdrawPost post);

    /**
     * 银行卡信息
     *
     * @return
     */
    @GET("p/wedding/index.php/home/APICardUserBank/bank_card")
    Observable<HljHttpResult<BindInfo>> getBankInfo();

    /**
     * 银行列表
     *
     * @return
     */
    @GET("p/wedding/index.php/home/APICardUserBank/bank_card")
    Observable<HljHttpResult<HljHttpData<List<BindInfo>>>> getBankList(
            @Query("page") int page, @Query("per_page") int perPage);


    /**
     * 添加会员地址
     *
     * @return
     */
    @POST("p/wedding/index.php/home/APIUserMemberOrder/address")
    Observable<HljHttpResult> addMemberAddress(@Body PostAddressId postAddressId);

    /**
     * 获取用户钱包信息
     */
    @GET("p/wedding/index.php/Shop/APIRedPacket/myWalletV2")
    Observable<HljHttpResult<Wallet>> getWallet();

    /**
     * 可提现请帖列表
     */
    @GET("p/wedding/index.php/home/APICardBalance/card_list")
    Observable<HljHttpResult<HljHttpCardData<List<CardBalance>>>> getCardBalances(
            @Query("page") int page, @Query("per_page") int perPage);

    /**
     * 新版提现
     */
    @POST("p/wedding/index.php/home/APICardBalance/withdraw")
    Observable<HljHttpResult<WithDrawCash>> withdrawCardBalance(@Body Map<String, Object> map);

    /**
     * 获取可提现余额和银行卡(新版)
     */
    @GET("p/wedding/index.php/home/APICardBalance/card_money")
    Observable<HljHttpResult<CardBalanceDetail>> getCardBalanceDetail(
            @Query("card_id") String cardId);

    /**
     * 获取可提现余额和银行卡(旧版版)
     */
    @GET("p/wedding/index.php/home/APIUserBalance/index")
    Observable<HljHttpResult<UserBalanceDetail>> getUserBalanceDetail();

    /**
     * 金融超市列表
     */
    @GET("p/wedding/index.php/home/APIFinancialMarket/listV2")
    Observable<HljHttpResult<List<FinancialMarketGroup>>> getFinancialMarketList();

    /**
     * 开启最新的一个礼金
     *
     * @return
     */
    @POST("p/wedding/index.php/Home/APICardCashGift/open")
    Observable<HljHttpResult<UserGift>> openLatestCashGift(@Body Map<String, Object> map);

    @GET("p/wedding/index.php/Home/APICardBalance/withdraw_param")
    Observable<HljHttpResult<WithdrawParam>> getWithdrawParam();

    /**
     * 新版的金融产品获取接口
     *
     * @param type
     * @return
     */
    @GET("p/wedding/index.php/home/APIFinancialMarket/listV3?page=1&per_page=20")
    Observable<HljHttpResult<HljHttpData<List<FinancialProduct>>>> getFinancialMarketListV3
    (@Query("type") int type);
}