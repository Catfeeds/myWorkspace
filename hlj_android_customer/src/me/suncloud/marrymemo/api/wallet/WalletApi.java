package me.suncloud.marrymemo.api.wallet;

import android.util.SparseArray;

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
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.entities.HljHttpCardData;
import com.hunliji.hljhttplibrary.entities.HljHttpCountData;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.utils.GsonUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.model.wallet.CouponRecord;
import me.suncloud.marrymemo.model.wallet.FinancialMarketGroup;
import me.suncloud.marrymemo.model.wallet.FinancialProduct;
import me.suncloud.marrymemo.model.wallet.MemberRedPacket;
import me.suncloud.marrymemo.model.wallet.Wallet;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 红包、优惠券Api
 * Created by chen_bin on 2016/10/15 0015.
 */

public class WalletApi {

    /**
     * 我的红包列表
     *
     * @param status
     * @param page
     * @param perPage
     * @return
     */
    public static Observable<HljHttpData<List<RedPacket>>> getMyRedPacketListObb(
            int status, int page, int perPage) {
        return HljHttp.getRetrofit()
                .create(WalletService.class)
                .getMyRedPacketList(status, page, perPage)
                .map(new HljHttpResultFunc<HljHttpData<List<RedPacket>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 我的优惠券列表
     *
     * @param type
     * @param page
     * @param perPage
     * @return
     */
    public static Observable<HljHttpData<List<CouponRecord>>> getMyCouponListObb(
            int type, int page, int perPage) {
        return HljHttp.getRetrofit()
                .create(WalletService.class)
                .getMyCouponList(type, page, perPage)
                .map(new HljHttpResultFunc<HljHttpData<List<CouponRecord>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 商家优惠券
     *
     * @param merchantId
     * @return
     */
    public static Observable<List<CouponInfo>> getMerchantCouponListObb(long merchantId) {
        return HljHttp.getRetrofit()
                .create(WalletService.class)
                .getMerchantCouponList(merchantId)
                .map(new HljHttpResultFunc<List<CouponInfo>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 领取优惠券
     *
     * @param ids
     * @return
     */
    public static Observable receiveCouponObb(String ids) {
        return HljHttp.getRetrofit()
                .create(WalletService.class)
                .receiveCoupon(ids)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 领取红包
     *
     * @param exchangeCode
     * @return
     */
    public static Observable receiveRedPacketObb(String exchangeCode) {
        return HljHttp.getRetrofit()
                .create(WalletService.class)
                .receiveRedPacket(exchangeCode)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 使用优惠券问题
     *
     * @param id
     * @param type type=1代表的是线下使用
     * @return
     */
    public static Observable useCouponObb(long id, int type) {
        return HljHttp.getRetrofit()
                .create(WalletService.class)
                .useCoupon(id, type)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 领券中心，红包跟优惠券列表
     *
     * @return
     */
    public static Observable<SparseArray<Object>> getCouponsAndRedPacketsObb(long id) {
        return HljHttp.getRetrofit()
                .create(WalletService.class)
                .getCouponsAndRedPackets(id)
                .map(new HljHttpResultFunc<JsonElement>())
                .map(new Func1<JsonElement, SparseArray<Object>>() {
                    @Override
                    public SparseArray<Object> call(JsonElement jsonElement) {
                        SparseArray<Object> sparseArray = new SparseArray<>();
                        ArrayList<RedPacket> redPackets = new ArrayList<>();
                        MemberRedPacket memberRedPacket = null;
                        try {
                            redPackets.addAll(Arrays.asList(GsonUtil.getGsonInstance()
                                    .fromJson(jsonElement.getAsJsonObject()
                                            .get("red_packets")
                                            .getAsJsonArray(), RedPacket[].class)));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        ArrayList<CouponInfo> coupons = new ArrayList<>();
                        try {
                            coupons.addAll(Arrays.asList(GsonUtil.getGsonInstance()
                                    .fromJson(jsonElement.getAsJsonObject()
                                            .get("coupons")
                                            .getAsJsonArray(), CouponInfo[].class)));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            memberRedPacket = GsonUtil.getGsonInstance()
                                    .fromJson(jsonElement.getAsJsonObject()
                                            .get("member_red_packet_group"), MemberRedPacket.class);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        sparseArray.put(0, redPackets);
                        sparseArray.put(1, coupons);
                        sparseArray.put(2, memberRedPacket);
                        return sparseArray;
                    }
                })
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
                .create(WalletService.class)
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
                .create(WalletService.class)
                .getUserGifts()
                .map(new HljHttpResultFunc<HljHttpCountData<List<UserGift>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取我的礼金列表
     *
     * @return
     */
    public static Observable<HljHttpCountData<List<UserGift>>> getUserCashGiftsObb() {
        return HljHttp.getRetrofit()
                .create(WalletService.class)
                .getUserCashGifts()
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
                .create(WalletService.class)
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
                .create(WalletService.class)
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
                .create(WalletService.class)
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
                .create(WalletService.class)
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
                .create(WalletService.class)
                .getBankList(page, perPage)
                .map(new HljHttpResultFunc<HljHttpData<List<BindInfo>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 会员订单提交
     *
     * @return
     */
    public static Observable addMemberAddressObb(PostAddressId postAddressId) {
        return HljHttp.getRetrofit()
                .create(WalletService.class)
                .addMemberAddress(postAddressId)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取用户钱包信息
     */
    public static Observable<Wallet> getWallet() {
        return HljHttp.getRetrofit()
                .create(WalletService.class)
                .getWallet()
                .map(new HljHttpResultFunc<Wallet>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 可提现请帖列表
     */
    public static Observable<HljHttpCardData<List<CardBalance>>> getCardBalancesObb(int page) {
        return HljHttp.getRetrofit()
                .create(WalletService.class)
                .getCardBalances(page, Constants.PER_PAGE)
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
                .create(WalletService.class)
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
                .create(WalletService.class)
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
                .create(WalletService.class)
                .getUserBalanceDetail()
                .map(new HljHttpResultFunc<UserBalanceDetail>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 金融超市列表
     *
     * @return
     */
    public static Observable<List<FinancialMarketGroup>> getFinancialMarktList() {
        return HljHttp.getRetrofit()
                .create(WalletService.class)
                .getFinancialMarketList()
                .map(new HljHttpResultFunc<List<FinancialMarketGroup>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 拆开最新的礼金
     *
     * @param type 礼金礼物类型区别, 1：礼金，2：礼物
     * @return
     */
    public static Observable<UserGift> openLatestCashGift(int type) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("type", type);
        return HljHttp.getRetrofit()
                .create(WalletService.class)
                .openLatestCashGift(map)
                .map(new HljHttpResultFunc<UserGift>())
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
                .create(WalletService.class)
                .getWithdrawParam()
                .map(new HljHttpResultFunc<WithdrawParam>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 金融超市列表V3
     *
     * @return
     */
    public static Observable<HljHttpData<List<FinancialProduct>>> getFinancialMarketListV3(int type) {
        return HljHttp.getRetrofit()
                .create(WalletService.class)
                .getFinancialMarketListV3(type)
                .map(new HljHttpResultFunc<HljHttpData<List<FinancialProduct>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}