package com.hunliji.marrybiz.api.shoptheme;

import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;
import com.hunliji.marrybiz.model.shoptheme.ShopTheme;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by hua_rong on 2017/5/22.
 * 店铺主题
 */

public class ShopThemeApi {

    /**
     * 获取全部主题列表
     * @return
     */
   public static Observable<HljHttpData<List<ShopTheme>>> getThemeList(){
       return HljHttp.getRetrofit()
               .create(ShopThemeService.class)
               .getThemeList()
               .map(new HljHttpResultFunc<HljHttpData<List<ShopTheme>>>())
               .subscribeOn(Schedulers.io())
               .observeOn(AndroidSchedulers.mainThread());
   }

    /**
     * 查询当前主题
     * @param merchantId
     * @return
     */
    public static Observable<ShopTheme> getDecoration(long merchantId){
        return HljHttp.getRetrofit()
                .create(ShopThemeService.class)
                .getDecoration(merchantId)
                .map(new HljHttpResultFunc<ShopTheme>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 选择主题
     * @param id 主题id
     * @return
     */
    public static Observable postChooseTheme(long id){
        ShopTheme shopTheme = new ShopTheme();
        shopTheme.setId(id);
        return HljHttp.getRetrofit()
                .create(ShopThemeService.class)
                .postChooseTheme(shopTheme)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


}
