package com.hunliji.marrybiz.api.merchantserver;

import com.google.gson.JsonElement;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;
import com.hunliji.marrybiz.model.merchantservice.BondInfo;
import com.hunliji.marrybiz.model.merchantservice.MerchantServer;
import com.hunliji.marrybiz.model.merchantservice.WeAppDetail;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by jinxin on 2018/1/29 0029.
 */

public class MerchantServerApi {

    /**
     * 获得商家服务列表
     *
     * @return
     */
    public static Observable<HljHttpData<List<MerchantServer>>>
    getMerchantServerList() {
        return HljHttp.getRetrofit()
                .create(MerchantServerService.class)
                .getMerchantServerList()
                .map(new HljHttpResultFunc<HljHttpData<List<MerchantServer>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获得小程序详情
     *
     * @return
     */
    public static Observable<WeAppDetail> getMerchantWeAppDetailObb() {
        return HljHttp.getRetrofit()
                .create(MerchantServerService.class)
                .getMerchantWeAppDetail()
                .map(new HljHttpResultFunc<WeAppDetail>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获得商家旗舰版支付费用
     *
     * @return
     */
    public static Observable<JsonElement> getMerchantProMoneyObb() {
        return HljHttp.getRetrofit()
                .create(MerchantServerService.class)
                .getMerchantProMoney()
                .map(new HljHttpResultFunc<JsonElement>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取保证金信息
     *
     * @return
     */
    public static Observable<BondInfo> getBondInfoObb() {
        return HljHttp.getRetrofit()
                .create(MerchantServerService.class)
                .getBondInfo()
                .map(new HljHttpResultFunc<BondInfo>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
