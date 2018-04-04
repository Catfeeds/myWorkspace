package com.hunliji.hljpaymentlibrary.api;

import android.content.Context;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hunliji.hljcommonlibrary.models.BankCard;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.entities.HljApiException;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;

import org.json.JSONObject;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Suncloud on 2016/8/1.
 */
public class PaymentApi {

    public static Observable<HljHttpResult<JsonElement>> getPayParams(
            String path, JSONObject jsonParams) {
        JsonParser jsonParser = new JsonParser();
        return HljHttp.getRetrofit()
                .create(PaymentService.class)
                .postPayParams(path, (JsonObject) jsonParser.parse(jsonParams.toString()))
                // 自定义接口返回参数retcode的筛选
                .map(new Func1<HljHttpResult<JsonElement>, HljHttpResult<JsonElement>>() {
                    @Override
                    public HljHttpResult<JsonElement> call(HljHttpResult<JsonElement> result) {
                        if (result.getStatus()
                                .getRetCode() == 0 || result.getStatus()
                                .getRetCode() == 3024 || result.getStatus()
                                .getRetCode() == 900001 || result.getStatus()
                                .getRetCode() == 3023) {
                            // 0, 收单成功
                            // 显示额度 == n * 真实额度(n >= 1)
                            // 3024, 51返回的授信额度不足，意味着： 真实额度 < amount < 显示额度
                            // 900001, 婚礼纪服务器返回的额度不足，意味着： amount > 显示额度
                            return result;
                        } else {
                            throw new HljApiException(result.getStatus());
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<List<BankCard>> getBankCards() {
        return HljHttp.getRetrofit()
                .create(PaymentService.class)
                .getBindBanks()
                .map(new HljHttpResultFunc<List<BankCard>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    public static Observable<HljHttpResult<Object>> postCheckPassword(String password) {
        return HljHttp.getRetrofit()
                .create(PaymentService.class)
                .checkPassword(password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<HljHttpResult<Object>> postSetPassword(JsonObject jsonObject) {
        return HljHttp.getRetrofit()
                .create(PaymentService.class)
                .setPassword(jsonObject)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<HljHttpResult<Object>> postFindPassword(JsonObject jsonObject) {
        return HljHttp.getRetrofit()
                .create(PaymentService.class)
                .findPassword(jsonObject)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<HljHttpResult<Object>> postResetPassword(JsonObject jsonObject) {
        return HljHttp.getRetrofit()
                .create(PaymentService.class)
                .resetPassword(jsonObject)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    public static Observable<BankCard> getCardInfo(String cardNo) {
        return HljHttp.getRetrofit()
                .create(PaymentService.class)
                .getCardInfo(cardNo)
                .map(new HljHttpResultFunc<BankCard>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    public static Observable<List<BankCard>> getSupportCards() {
        return HljHttp.getRetrofit()
                .create(PaymentService.class)
                .getSupportCards()
                .map(new HljHttpResultFunc<List<BankCard>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<Double> getWallet(Context context) {
        if (CommonUtil.getAppType() == CommonUtil.PacketType.MERCHANT) {
            return getMerchantWallet();
        } else {
            return getUserWallet();
        }
    }

    private static Observable<Double> getMerchantWallet() {
        return HljHttp.getRetrofit()
                .create(PaymentService.class)
                .getMerchantWallet()
                .map(new HljHttpResultFunc<JsonElement>())
                .map(new Func1<JsonElement, Double>() {
                    @Override
                    public Double call(JsonElement jsonElement) {
                        try {
                            return jsonElement.getAsJsonObject()
                                    .get("money")
                                    .getAsDouble();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return 0d;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private static Observable<Double> getUserWallet() {
        return HljHttp.getRetrofit()
                .create(PaymentService.class)
                .getUserWallet()
                .map(new HljHttpResultFunc<JsonElement>())
                .map(new Func1<JsonElement, Double>() {
                    @Override
                    public Double call(JsonElement jsonElement) {
                        try {
                            return jsonElement.getAsJsonObject()
                                    .get("balance")
                                    .getAsDouble();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return 0d;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    public static Observable<HljHttpResult<JsonElement>> getOrderQuery(
            String outTradeNo, String source) {
        return HljHttp.getRetrofit()
                .create(PaymentService.class)
                .getOrderQuery(outTradeNo, source)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
