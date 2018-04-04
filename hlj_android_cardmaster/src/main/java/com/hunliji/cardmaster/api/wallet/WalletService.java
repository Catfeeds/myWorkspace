package com.hunliji.cardmaster.api.wallet;

import com.hunliji.hljcardcustomerlibrary.models.UserGift;
import com.hunliji.hljcommonlibrary.models.wallet.Wallet;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;

import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import rx.Observable;

/**
 * 钱包相关
 * Created by jinxin on 2017/11/27 0027.
 */

public interface WalletService {

    /**
     * 获取用户钱包信息
     */
    @GET("p/wedding/index.php/Shop/APIRedPacket/myWalletV2")
    Observable<HljHttpResult<Wallet>> getWallet();

    /**
     * 开启最新的一个礼金
     *
     * @return
     */
    @POST("p/wedding/index.php/Home/APICardCashGift/open")
    Observable<HljHttpResult<UserGift>> openLatestCashGift(@Body Map<String, Object> map);
}
