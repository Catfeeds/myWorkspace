package com.hunliji.cardmaster.api.wallet;

import com.hunliji.hljcardcustomerlibrary.models.UserGift;
import com.hunliji.hljcommonlibrary.models.wallet.Wallet;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;

import java.util.HashMap;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 钱包相关
 * Created by jinxin on 2017/11/27 0027.
 */

public class WalletApi {

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
}
