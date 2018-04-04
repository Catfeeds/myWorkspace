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
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.BankCard;
import com.hunliji.hljcommonlibrary.models.BindInfo;
import com.hunliji.hljcommonlibrary.models.customer.CustomerUser;
import com.hunliji.hljcommonlibrary.models.userprofile.WXInfo;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.entities.HljHttpCardData;
import com.hunliji.hljhttplibrary.entities.HljHttpCountData;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;
import com.hunliji.hljsharelibrary.HljShare;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 请帖api
 * Created by mo_yu on 2017/06/13 0015.
 */
public class CustomerCardApi {

    /**
     * 获取我的礼金列表
     *
     * @param type 1礼物 2礼金 。不传为全部
     * @return
     */
    public static Observable<HljHttpCardData<List<UserGift>>> getUserCashGiftsObb(int type) {
        return HljHttp.getRetrofit()
                .create(CustomerCardService.class)
                .getUserCashGifts(type)
                .map(new HljHttpResultFunc<HljHttpCardData<List<UserGift>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 绑定信息
     *
     * @return
     */
    public static Observable<BindInfo> getBindInfo(long cardId) {
        return HljHttp.getRetrofit()
                .create(CustomerCardService.class)
                .getBindInfo(cardId)
                .map(new HljHttpResultFunc<BindInfo>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 请获取微信token
     *
     * @return
     */
    public static Observable<WXInfo> getWXTokenObb(String code) {
        return HljHttp.getRetrofit()
                .create(CustomerCardService.class)
                .getWXToken(HljShare.WEIXINKEY, HljShare.WEIXINSECRET, code)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 绑定微信
     *
     * @param cardId   请帖id
     * @param idHolder 姓名
     * @param openId   微信openid
     * @return
     */
    public static Observable<BindInfo> bindWXObb(
            long cardId, String idHolder, String openId, String nickname) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("card_id", cardId);
        map.put("id_holder", idHolder);
        map.put("openid", openId);
        map.put("nickname", nickname);
        return HljHttp.getRetrofit()
                .create(CustomerCardService.class)
                .bindWX(map)
                .map(new HljHttpResultFunc<BindInfo>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 解绑
     *
     * @param cardId 请帖id
     * @return
     */
    public static Observable unbindObb(long cardId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("card_id", cardId);
        return HljHttp.getRetrofit()
                .create(CustomerCardService.class)
                .unBind(map)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 绑定银行卡
     *
     * @param accNo    银行卡号
     * @param cardId   请帖id
     * @param cid      开户所在地
     * @param idHolder 姓名
     * @return
     */
    public static Observable<HljHttpResult<BindInfo>> bindBankObb(
            String accNo, long cardId, long cid, String idHolder) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("acc_no", accNo);
        map.put("card_id", cardId);
        map.put("cid", cid);
        map.put("id_holder", idHolder);
        return HljHttp.getRetrofit()
                .create(CustomerCardService.class)
                .bindBank(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 宾客反馈列表
     *
     * @param cardId 请帖id
     * @param state  0 赴宴 1祝福
     * @return
     */
    public static Observable<HljHttpData<List<CardReply>>> getReplyList(
            long cardId, int state, int page) {
        return HljHttp.getRetrofit()
                .create(CustomerCardService.class)
                .getReplyList(cardId == 0 ? null : cardId, state, page, 20)
                .map(new HljHttpResultFunc<HljHttpData<List<CardReply>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 有祝福/赴宴的请帖列表
     *
     * @return
     */
    public static Observable<HljHttpData<List<Card>>> getReplyCardList() {
        return HljHttp.getRetrofit()
                .create(CustomerCardService.class)
                .getReplyCardList()
                .map(new HljHttpResultFunc<HljHttpData<List<Card>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 意见反馈
     *
     * @param content 问题描述
     * @param contact 联系电话
     * @return
     */
    public static Observable<HljHttpResult> postFeedback(String content, String contact) {
        FeedBackBody body = new FeedBackBody(content, contact);
        return HljHttp.getRetrofit()
                .create(CustomerCardService.class)
                .postFeedback(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 删除祝福
     *
     * @param replyId 祝福id
     * @return
     */
    public static Observable<HljHttpResult> deleteReply(long replyId) {
        return HljHttp.getRetrofit()
                .create(CustomerCardService.class)
                .deleteReply(replyId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 请帖回复宾客
     *
     * @param id      回复id
     * @param message 回复信息
     * @return
     */
    public static Observable replyCardUserObb(long id, String message) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("message", message);
        return HljHttp.getRetrofit()
                .create(CustomerCardService.class)
                .replyCardUserCash(map)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取礼物列表
     *
     * @return
     */
    public static Observable<HljHttpData<List<CardGift>>> getCardGiftsObb() {
        return HljHttp.getRetrofit()
                .create(CustomerCardService.class)
                .getCardGifts()
                .map(new HljHttpResultFunc<HljHttpData<List<CardGift>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取我的礼物列表
     *
     * @return
     */
    public static Observable<HljHttpCountData<List<UserGift>>> getUserGiftsObb() {
        return HljHttp.getRetrofit()
                .create(CustomerCardService.class)
                .getUserGifts()
                .map(new HljHttpResultFunc<HljHttpCountData<List<UserGift>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取用户余额及余额明细列表
     *
     * @return
     */
    public static Observable<HljHttpCountData<List<Balance>>> getBalanceObb() {
        return HljHttp.getRetrofit()
                .create(CustomerCardService.class)
                .getBalance()
                .map(new HljHttpResultFunc<HljHttpCountData<List<Balance>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 提现礼物余额
     *
     * @return
     */
    public static Observable<WithDraw> withdrawGiftObb(
            String smsCode, String money, String openid, int insurance) {
        return HljHttp.getRetrofit()
                .create(CustomerCardService.class)
                .withdrawGift(new WithdrawPost(smsCode, money, openid, insurance))
                .map(new HljHttpResultFunc<WithDraw>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 提现礼物余额
     *
     * @return
     */
    public static Observable<WithDrawCash> withdrawCashObb(
            String smsCode, String money, int insurance) {
        return HljHttp.getRetrofit()
                .create(CustomerCardService.class)
                .withdrawCash(new WithdrawPost(smsCode, money, insurance))
                .map(new HljHttpResultFunc<WithDrawCash>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 绑定银行卡信息
     *
     * @return
     */
    public static Observable<BindInfo> getBankInfoObb() {
        return HljHttp.getRetrofit()
                .create(CustomerCardService.class)
                .getBankInfo()
                .map(new HljHttpResultFunc<BindInfo>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 银行列表
     *
     * @return
     */
    public static Observable<HljHttpData<List<BindInfo>>> getBankListObb(int page, int perPage) {
        return HljHttp.getRetrofit()
                .create(CustomerCardService.class)
                .getBankList(page, perPage)
                .map(new HljHttpResultFunc<HljHttpData<List<BindInfo>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 可提现请帖列表
     */
    public static Observable<HljHttpCardData<List<CardBalance>>> getCardBalancesObb(int page) {
        return HljHttp.getRetrofit()
                .create(CustomerCardService.class)
                .getCardBalances(page, HljCommon.PER_PAGE)
                .map(new HljHttpResultFunc<HljHttpCardData<List<CardBalance>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 可提现请帖列表，取1000个，当做全部数据
     */
    public static Observable<HljHttpCardData<List<CardBalance>>> getAllCardBalancesObb() {
        return HljHttp.getRetrofit()
                .create(CustomerCardService.class)
                .getCardBalances(1, 1000)
                .map(new HljHttpResultFunc<HljHttpCardData<List<CardBalance>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 新版提现
     *
     * @param map
     * @return
     */
    public static Observable<HljHttpResult<WithDrawCash>> withdrawCardBalanceObb(
            Map<String, Object> map) {
        return HljHttp.getRetrofit()
                .create(CustomerCardService.class)
                .withdrawCardBalance(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取可提现余额和银行卡(新版)
     *
     * @param cardId 请帖id
     * @return
     */
    public static Observable<CardBalanceDetail> getCardBalanceDetailObb(String cardId) {
        return HljHttp.getRetrofit()
                .create(CustomerCardService.class)
                .getCardBalanceDetail(cardId)
                .map(new HljHttpResultFunc<CardBalanceDetail>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取可提现余额和银行卡(旧版)
     *
     * @return
     */
    public static Observable<UserBalanceDetail> getUserBalanceDetailObb() {
        return HljHttp.getRetrofit()
                .create(CustomerCardService.class)
                .getUserBalanceDetail()
                .map(new HljHttpResultFunc<UserBalanceDetail>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取提现手续费等信息
     *
     * @return
     */
    public static Observable<WithdrawParam> getWithdrawParam() {
        return HljHttp.getRetrofit()
                .create(CustomerCardService.class)
                .getWithdrawParam()
                .map(new HljHttpResultFunc<WithdrawParam>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * <<<<<<< HEAD
     * 绑定理财银行卡
     */
    public static Observable<BindFundBank> bindFundBankObb(
            String accNo, String idCard, String idHolder, String mobile) {
        Map<String, Object> map = new HashMap<>();
        map.put("acc_no", accNo);
        map.put("id_card", idCard);
        map.put("id_holder", idHolder);
        map.put("mobile", mobile);
        return HljHttp.getRetrofit()
                .create(CustomerCardService.class)
                .bindFundBank(map)
                .map(new HljHttpResultFunc<BindFundBank>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 通过卡号取得 银行信息
     */
    public static Observable<JsonElement> getBankCardInfoObb(
            String cardNo) {
        return HljHttp.getRetrofit()
                .create(CustomerCardService.class)
                .getBankCardInfo(cardNo)
                .map(new HljHttpResultFunc<JsonElement>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 绑卡检查请帖的名字
     */
    public static Observable<JsonElement> checkCardNameObb(
            String idHolder) {
        Map<String, Object> map = new HashMap<>();
        map.put("id_holder", idHolder);
        return HljHttp.getRetrofit()
                .create(CustomerCardService.class)
                .checkCardName(map)
                .map(new HljHttpResultFunc<JsonElement>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 银行卡转入理财
     *
     * @param map
     * @return
     */
    public static Observable<BankRollInResult> bankRollInObb(
            Map<String, Object> map) {
        return HljHttp.getRetrofit()
                .create(CustomerCardService.class)
                .bankRollIn(map)
                .map(new HljHttpResultFunc<BankRollInResult>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 新人气泡红包列表
     *
     * @return
     */
    public static Observable<JsonElement> getUserRedPacketList() {
        return HljHttp.getRetrofit()
                .create(CustomerCardService.class)
                .getUserRedPacketList()
                .map(new HljHttpResultFunc<JsonElement>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 请帖转入列表
     */
    public static Observable<HljHttpCardData<List<CardBalance>>> getFundCardListObb() {
        return HljHttp.getRetrofit()
                .create(CustomerCardService.class)
                .getFundCardList()
                .map(new HljHttpResultFunc<HljHttpCardData<List<CardBalance>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 请帖转入提交
     *
     * @param cardIds 请帖ID，逗号隔开
     * @return
     */
    public static Observable<JsonElement> postCardRollInFundObb(
            String cardIds, boolean is_auto_income) {
        Map<String, Object> map = new HashMap<>();
        map.put("card_ids", cardIds);
        map.put("is_auto_income", is_auto_income ? 1 : 0);
        return HljHttp.getRetrofit()
                .create(CustomerCardService.class)
                .postCardRollInFund(map)
                .map(new HljHttpResultFunc<JsonElement>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 会员订单提交
     *
     * @return
     */
    public static Observable<MemberOrder> submitMemberOrderObb() {
        return HljHttp.getRetrofit()
                .create(CustomerCardService.class)
                .submitMemberOrder()
                .map(new HljHttpResultFunc<MemberOrder>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 理财设置读取
     */
    public static Observable<JsonElement> getSetUpFundObb() {
        return HljHttp.getRetrofit()
                .create(CustomerCardService.class)
                .getSetUpFund()
                .map(new HljHttpResultFunc<JsonElement>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 理财设置更新
     */
    public static Observable postSetUpFundObb(boolean isAutoIncome) {
        Map<String, Object> map = new HashMap<>();
        map.put("is_auto_income", isAutoIncome ? 1 : 0);
        return HljHttp.getRetrofit()
                .create(CustomerCardService.class)
                .postSetUpFund(map)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 我的银行卡
     */
    public static Observable<BindInfo> getMyFundBankInfoObb() {
        return HljHttp.getRetrofit()
                .create(CustomerCardService.class)
                .getMyFundBankInfo()
                .map(new HljHttpResultFunc<BindInfo>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 理财银行列表
     *
     * @return
     */
    public static Observable<List<BankCard>> getFundBankListObb() {
        return HljHttp.getRetrofit()
                .create(CustomerCardService.class)
                .getFundBankList()
                .map(new HljHttpResultFunc<List<BankCard>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 理财明细
     *
     * @return
     */
    public static Observable<HljHttpData<List<FundDetail>>> getFundDetailsObb(int page) {
        return HljHttp.getRetrofit()
                .create(CustomerCardService.class)
                .getFundDetails(page, HljCommon.PER_PAGE)
                .map(new HljHttpResultFunc<HljHttpData<List<FundDetail>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 理财首页
     */
    public static Observable<FundIndex> getFundIndexObb() {
        return HljHttp.getRetrofit()
                .create(CustomerCardService.class)
                .getFundIndex()
                .map(new HljHttpResultFunc<FundIndex>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 理财转出银行卡
     */
    public static Observable<JsonElement> fundRollOutObb(Map<String, Object> map) {
        return HljHttp.getRetrofit()
                .create(CustomerCardService.class)
                .fundRollOut(map)
                .map(new HljHttpResultFunc<JsonElement>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 修改用户信息
     *
     * @param map
     * @return
     */
    public static Observable<CustomerUser> editMyBaseInfoObb(Map<String, Object> map) {
        return HljHttp.getRetrofit()
                .create(CustomerCardService.class)
                .editMyBaseInfo(map)
                .map(new HljHttpResultFunc<CustomerUser>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 查询手机号绑定
     * @param phone
     * @return
     */
    public static Observable<JsonElement> getPhoneBindStatusObb(String phone) {
        return HljHttp.getRetrofit()
                .create(CustomerCardService.class)
                .getPhoneBindStatus(phone)
                .map(new HljHttpResultFunc<JsonElement>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 检测当前设备是否为常用登录设备
     * @return
     */
    public static Observable<JsonElement> checkPhoneStatusObb() {
        return HljHttp.getRetrofit()
                .create(CustomerCardService.class)
                .checkPhoneStatus()
                .map(new HljHttpResultFunc<JsonElement>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
