package me.suncloud.marrymemo.api.Address;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;

import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by mo_yu on 2017/11/16.地址相关
 */

public interface AddressService {

    @GET("p/wedding/index.php/shop/APIShopAddress/default")
    Observable<HljHttpResult<JsonElement>> getDefaultAddress();
}
