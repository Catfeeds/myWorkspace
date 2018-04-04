package me.suncloud.marrymemo.api.bindbank;

import com.hunliji.hljcommonlibrary.models.BankCard;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by jinxin on 2017/4/5 0005.
 */

public class BindApi {

    /**
     * 绑定银行卡
     *
     * @param body
     * @return
     */
    public static Observable<HljHttpResult> bindBankCard(BindBankPostBody body) {
        return HljHttp.getRetrofit()
                .create(BindBankService.class)
                .bindBankCard(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 获得银行卡信息
     *
     * @return
     */
    public static Observable<BankCard> getBankCard() {
        return HljHttp.getRetrofit()
                .create(BindBankService.class)
                .getBankCard()
                .map(new HljHttpResultFunc<BankCard>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取礼金功能开关状态
     *
     * @param cardId
     * @return
     */
    public static Observable<CashGiftStatus> getCashGiftOn(long cardId) {
        return HljHttp.getRetrofit()
                .create(BindBankService.class)
                .getCashGiftOn(cardId)
                .map(new HljHttpResultFunc<CashGiftStatus>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 开启或关闭全局礼金功能
     *
     * @param body
     * @return
     */
    public static Observable<HljHttpResult> cashGiftOn(SwitchCashGiftBody body) {
        return HljHttp.getRetrofit()
                .create(BindBankService.class)
                .cashGiftOn(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
