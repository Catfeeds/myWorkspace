package com.hunliji.hljcardcustomerlibrary.api;

import com.google.gson.JsonElement;
import com.hunliji.hljcardcustomerlibrary.models.Balance;
import com.hunliji.hljcardcustomerlibrary.models.BankRollInResult;
import com.hunliji.hljcardcustomerlibrary.models.BindFundBank;
import com.hunliji.hljcardcustomerlibrary.models.CardBalance;
import com.hunliji.hljcardcustomerlibrary.models.CardBalanceDetail;
import com.hunliji.hljcardcustomerlibrary.models.CardGift;
import com.hunliji.hljcardcustomerlibrary.models.CardReply;
import com.hunliji.hljcardcustomerlibrary.models.FeedBackBody;
import com.hunliji.hljcardcustomerlibrary.models.FundDetail;
import com.hunliji.hljcardcustomerlibrary.models.FundIndex;
import com.hunliji.hljcardcustomerlibrary.models.MemberOrder;
import com.hunliji.hljcardcustomerlibrary.models.UserBalanceDetail;
import com.hunliji.hljcardcustomerlibrary.models.UserGift;
import com.hunliji.hljcardcustomerlibrary.models.WithDraw;
import com.hunliji.hljcardcustomerlibrary.models.WithDrawCash;
import com.hunliji.hljcardcustomerlibrary.models.WithdrawParam;
import com.hunliji.hljcardcustomerlibrary.models.WithdrawPost;
import com.hunliji.hljcardlibrary.models.Card;
import com.hunliji.hljcommonlibrary.models.BankCard;
import com.hunliji.hljcommonlibrary.models.BindInfo;
import com.hunliji.hljcommonlibrary.models.customer.CustomerUser;
import com.hunliji.hljcommonlibrary.models.userprofile.WXInfo;
import com.hunliji.hljhttplibrary.entities.HljHttpCardData;
import com.hunliji.hljhttplibrary.entities.HljHttpCountData;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;

import java.util.List;
import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * 请帖Service
 * Created by mo_yu on 2017/06/13 0015.
 */
public interface CustomerCardService {

    /**
     * 宾客反馈列表
     *
     * @param cardId 请帖id
     * @param state  0 赴宴 1祝福
     * @return
     */
    @GET("/p/wedding/index.php/home/APIInvitationV3/reply_list")
    Observable<HljHttpResult<HljHttpData<List<CardReply>>>> getReplyList(
            @Query("card_id") Long cardId,
            @Query("state") int state,
            @Query("page") int page,
            @Query("per_page") int per_page);

    /**
     * 有祝福/赴宴的请帖列表
     *
     * @return
     */
    @GET("/p/wedding/index.php/home/APIInvitationV3/reply_card_list")
    Observable<HljHttpResult<HljHttpData<List<Card>>>> getReplyCardList();

    /**
     * 删除祝福
     */
    @DELETE("/p/wedding/index.php/home/APIInvitationV3/reply")
    Observable<HljHttpResult> deleteReply(@Query("reply_id") long replyId);

    /**
     * 意见反馈
     */
    @POST("p/wedding/index.php/Home/APIFeedBack/feedBack")
    Observable<HljHttpResult> postFeedback(@Body FeedBackBody body);

    /**
     * 我的礼金V2
     *
     * @return
     */
    @GET("p/wedding/index.php/Home/APICardGift2Recv/index")
    Observable<HljHttpResult<HljHttpCardData<List<UserGift>>>> getUserCashGifts(@Query("type") int type);

    /**
     * 绑定信息
     *
     * @return
     */
    @GET("p/wedding/index.php/home/APICardUserBank/binding_v2")
    Observable<HljHttpResult<BindInfo>> getBindInfo(@Query("card_id") long cardId);


    /**
     * 获取微信token
     */
    @GET("https://api.weixin.qq.com/sns/oauth2/access_token?grant_type=authorization_code")
    Observable<WXInfo> getWXToken(
            @Query("appid") String appId,
            @Query("secret") String secret,
            @Query("code") String code);

    /**
     * 获取微信信息
     */
    @GET("https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s")
    Observable getWXInfo(
            @Query("access_token") String accessToken, @Query("appid") String appId);

    /**
     * 绑定微信
     */
    @POST("p/wedding/index.php/home/APICardUserBank/bind_weixin_v2")
    Observable<HljHttpResult<BindInfo>> bindWX(
            @Body Map<String, Object> bindMap);

    /**
     * 解绑
     */
    @POST("p/wedding/index.php/home/APICardUserBank/unbind")
    Observable<HljHttpResult> unBind(
            @Body Map<String, Object> unbindMap);


    /**
     * 绑定银行卡
     */
    @POST("p/wedding/index.php/home/APICardUserBank/bind_card_v2")
    Observable<HljHttpResult<BindInfo>> bindBank(
            @Body Map<String, Object> bindMap);

    /**
     * 请帖礼物回复
     */
    @POST("p/wedding/index.php/Home/APICardUserReply/edit")
    Observable<HljHttpResult> replyCardUserCash(@Body Map<String, Object> map);

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
    Observable<HljHttpResult<CardBalanceDetail>> getCardBalanceDetail(@Query("card_id") String
                                                                              cardId);

    /**
     * 获取可提现余额和银行卡(旧版版)
     */
    @GET("p/wedding/index.php/home/APIUserBalance/index")
    Observable<HljHttpResult<UserBalanceDetail>> getUserBalanceDetail();

    /**
     * 获取提现手续费等信息
     *
     * @return
     */
    @GET("p/wedding/index.php/Home/APICardBalance/withdraw_param")
    Observable<HljHttpResult<WithdrawParam>> getWithdrawParam();

    /**
     * 绑定理财银行卡
     */
    @POST("p/wedding/home/APIFundUserBank/bind")
    Observable<HljHttpResult<BindFundBank>> bindFundBank(@Body Map<String, Object> map);

    /**
     * 通过卡号取得 银行信息
     */
    @GET("p/wedding/index.php/Home/APIUserBankInfo/bankCardBin")
    Observable<HljHttpResult<JsonElement>> getBankCardInfo(@Query("card_no") String cardNo);

    /**
     * 绑卡检查请帖的名字
     */
    @POST("p/wedding/home/APIFundUserBank/check_name")
    Observable<HljHttpResult<JsonElement>> checkCardName(@Body Map<String, Object> map);

    /**
     * 银行卡转入理财
     */
    @POST("p/wedding/home/APIFundDeposit/pay")
    Observable<HljHttpResult<BankRollInResult>> bankRollIn(@Body Map<String, Object> map);

    /**
     * 请帖转入理财
     */
    @GET("p/wedding/home/APIFundDeposit/card_list")
    Observable<HljHttpResult<HljHttpCardData<List<CardBalance>>>> getFundCardList();

    /**
     * 请帖转入提交
     */
    @POST("p/wedding/home/APIFundDeposit/card_pay")
    Observable<HljHttpResult<JsonElement>> postCardRollInFund(@Body Map<String,Object> map);

    /**
     * 理财设置读取
     */
    @GET("p/wedding/home/APIFundUser/setup")
    Observable<HljHttpResult<JsonElement>> getSetUpFund();

    /**
     * 理财设置更新
     */
    @POST("p/wedding/home/APIFundUser/setup")
    Observable<HljHttpResult> postSetUpFund(@Body Map<String, Object> map);

    /**
     * 我的银行卡
     */
    @GET("p/wedding/home/APIFundUserBank/index")
    Observable<HljHttpResult<BindInfo>> getMyFundBankInfo();

    /**
     * 理财银行列表
     *
     * @return
     */
    @GET("p/wedding/home/APIFundUserBank/bank_list")
    Observable<HljHttpResult<List<BankCard>>> getFundBankList();

    /**
     * 理财明细
     *
     * @return
     */
    @GET("p/wedding/home/APIFundBalance/detail_list")
    Observable<HljHttpResult<HljHttpData<List<FundDetail>>>> getFundDetails(
            @Query("page") int page,
            @Query("per_page") int perPage);

    /**
     * 理财首页
     */
    @GET("p/wedding/home/APIFundUser/index")
    Observable<HljHttpResult<FundIndex>> getFundIndex();

    /**
     * 理财转出银行卡
     */
    @POST("p/wedding/home/APIFundBalance/withdraw")
    Observable<HljHttpResult<JsonElement>> fundRollOut(@Body Map<String, Object> map);

    /**
     * 新人气泡红包列表
     * @return
     */
    @GET("p/wedding/Shop/APIRedPacket/ProductNewUserRedPacketList")
    Observable<HljHttpResult<JsonElement>> getUserRedPacketList();

    /**
     * 会员订单提交
     *
     * @return
     */
    @POST("p/wedding/index.php/home/APIUserMemberOrder/submit")
    Observable<HljHttpResult<MemberOrder>> submitMemberOrder();

    /**
     * 修改用户信息
     * @param map
     * @return
     */
    @POST("p/wedding/index.php/home/APIUser/EditMyBaseInfo")
    Observable<HljHttpResult<CustomerUser>> editMyBaseInfo(@Body Map<String, Object> map);

    /**
     * 查询手机号绑定
     * @param phone
     * @return
     */
    @GET("p/wedding/Home/APIUser/PhoneBindStatus")
    Observable<HljHttpResult<JsonElement>> getPhoneBindStatus(@Query("phone") String phone);

    /**
     * 检测当前设备是否为常用登录设备
     * @return
     */
    @GET("p/wedding/Home/APIUser/CheckOperationSecurity")
    Observable<HljHttpResult<JsonElement>> checkPhoneStatus();
}
