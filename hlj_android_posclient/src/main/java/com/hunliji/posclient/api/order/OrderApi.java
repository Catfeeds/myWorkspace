package com.hunliji.posclient.api.order;

import com.google.gson.JsonElement;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.utils.GsonUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;
import com.hunliji.posclient.models.MerchantOrder;
import com.hunliji.posclient.models.relam.PosPayResult;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by chen_bin on 2018/1/17 0017.
 */
public class OrderApi {

    /**
     * POS机获取订单详情
     *
     * @param posCode
     * @return
     */
    public static Observable<MerchantOrder> getOrderDetailObb(String posCode) {
        Map<String, Object> map = new HashMap<>();
        map.put("pos_code", posCode);
        return HljHttp.getRetrofit()
                .create(OrderService.class)
                .getOrderDetail(map)
                .map(new HljHttpResultFunc<MerchantOrder>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * POS机确认支付金额
     *
     * @param posCode
     * @param amount
     * @return
     */
    public static Observable<PosPayResult> confirmPayObb(String posCode, final double amount) {
        Map<String, Object> map = new HashMap<>();
        map.put("pos_code", posCode);
        map.put("input_money", amount);
        return HljHttp.getRetrofit()
                .create(OrderService.class)
                .confirmPay(map)
                .map(new HljHttpResultFunc<JsonElement>())
                .map(new Func1<JsonElement, PosPayResult>() {
                    @Override
                    public PosPayResult call(JsonElement jsonElement) {
                        PosPayResult payResult = new PosPayResult();
                        payResult.setAmount(CommonUtil.formatDouble2String(amount));
                        if (jsonElement != null) {
                            String outTradeNo = CommonUtil.getAsString(jsonElement, "out_trade_no");
                            payResult.setOutTradeNo(outTradeNo);
                            //解析回调到婚礼纪的url
                            String payParams = CommonUtil.getAsString(jsonElement, "pay_params");
                            if (!CommonUtil.isEmpty(payParams)) {
                                String notifyUrl = CommonUtil.getAsString(GsonUtil.getGsonInstance()
                                        .fromJson(payParams, JsonElement.class), "notify_url");
                                payResult.setNotifyUrl(notifyUrl);
                            }
                        }
                        return payResult;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * pos机支付成功跟婚礼纪的回调
     *
     * @param payResult
     * @return
     */
    public static Observable submitPosPayResultObb(PosPayResult payResult) {
        return HljHttp.getRetrofit()
                .create(OrderService.class)
                .submitPosPayResult(payResult.getNotifyUrl(), payResult)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
