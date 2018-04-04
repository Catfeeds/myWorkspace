package com.hunliji.marrybiz.api.shoptheme;

import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.marrybiz.model.shoptheme.ShopTheme;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by hua_rong on 2017/5/22.
 * 店铺主题
 */

public interface ShopThemeService {

    /**
     * 获取全部主题列表
     *
     * @return
     */
    @GET("/p/wedding/index.php/home/APIMerchant/theme_list")
    Observable<HljHttpResult<HljHttpData<List<ShopTheme>>>> getThemeList();

    /**
     * 获取旗舰版配置
     */
    @GET("/p/wedding/index.php/home/APIMerchant/decoration")
    Observable<HljHttpResult<ShopTheme>> getDecoration(
            @Query("merchant_id") long merchantId);

    /**
     * 选择主题
     * @param shopTheme
     * @return
     */
    @POST("/p/wedding/index.php/Admin/APIMerchant/chose_theme")
    Observable<HljHttpResult> postChooseTheme(@Body ShopTheme shopTheme);

}
