package com.hunliji.marrybiz.api.revenue;

import com.google.gson.JsonObject;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.entities.HljHttpStatus;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;
import com.hunliji.marrybiz.model.revenue.Bank;
import com.hunliji.marrybiz.model.revenue.RevenueManager;

import java.util.HashMap;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by $hua_rong on 2017/8/15 0015.
 * 收入管理
 */

public class RevenueApi {

    /**
     * 获取收入管理明细
     *
     * @return
     */
    public static Observable<RevenueManager> getwithdraw() {
        return HljHttp.getRetrofit()
                .create(RevenueService.class)
                .getwithdraw()
                .map(new HljHttpResultFunc<RevenueManager>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取绑定银行信息
     *
     * @return
     */
    public static Observable<Bank> getAppBindBank() {
        return HljHttp.getRetrofit()
                .create(RevenueService.class)
                .getAppBindBank()
                .map(new HljHttpResultFunc<Bank>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 可否提现
     */
    public static Observable<HljHttpStatus> getCanWithdraw() {
        return HljHttp.getRetrofit()
                .create(RevenueService.class)
                .getCanWithdraw()
                .map(new Func1<HljHttpResult, HljHttpStatus>() {
                    @Override
                    public HljHttpStatus call(HljHttpResult httpStatus) {
                        return httpStatus.getStatus();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 提交银行卡信息
     *
     * @param bank
     * @return
     */
    public static Observable postAppBindBank(Bank bank) {
        HashMap<String, Object> map = new HashMap<>();
        if (bank.getAgent() == 0) {
            map.put("bank", bank.getBank());
            map.put("bank_name", bank.getBankName());
            map.put("bank_cid", bank.getBankCid());
        }
        map.put("account", bank.getAccount());
        map.put("name", bank.getName());
        map.put("agent", bank.getAgent());
        map.put("change_protocol", bank.getChangeProtocol());
        map.put("code", bank.getCode());
        return HljHttp.getRetrofit()
                .create(RevenueService.class)
                .postAppBindBank(map)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取验证码
     *
     * @param phone
     * @return
     */
    public static Observable<HljHttpResult<JsonObject>> getCertifyCode(String phone) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("phone", phone);
        return HljHttp.getRetrofit()
                .create(RevenueService.class)
                .getCertifyCode(jsonObject)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
