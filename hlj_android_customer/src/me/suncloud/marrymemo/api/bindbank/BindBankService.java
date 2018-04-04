package me.suncloud.marrymemo.api.bindbank;

import com.hunliji.hljcardcustomerlibrary.models.UserGift;
import com.hunliji.hljcommonlibrary.models.BankCard;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by jinxin on 2017/4/5 0005.
 */

public interface BindBankService {

    /**
     * 绑定银行卡
     * @param body
     * @return
     */
    @POST("p/wedding/index.php/home/APICardUserBank/bind_card")
    Observable<HljHttpResult> bindBankCard(@Body BindBankPostBody body);


    /**
     * 获取礼金功能开关状态
     * @param cardId 请帖id
     * @return
     */
    @GET("p/wedding/index.php/Home/APICardCashGift/cash_gift_on")
    Observable<HljHttpResult<CashGiftStatus>> getCashGiftOn(@Query("card_id") long cardId);

    /**
     * 获得银行卡信息
     * @return
     */
    @GET("p/wedding/index.php/home/APICardUserBank/bank_card")
    Observable<HljHttpResult<BankCard>> getBankCard();

    /**
     * 开启或关闭全局礼金功能
     * @param body
     * @return
     */
    @POST("p/wedding/index.php/Home/APICardCashGift/cash_gift_on")
    Observable<HljHttpResult> cashGiftOn(@Body SwitchCashGiftBody body);
}
