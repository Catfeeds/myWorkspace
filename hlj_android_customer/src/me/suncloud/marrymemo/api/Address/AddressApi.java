package me.suncloud.marrymemo.api.Address;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by mo_yu on 2017/11/16.地址相关
 */

public class AddressApi {

    /**
     * 获取默认地址
     *
     * @return
     */
    public static Observable<JsonObject> getDefaultAddressObb() {
        return HljHttp.getRetrofit()
                .create(AddressService.class)
                .getDefaultAddress()
                .map(new HljHttpResultFunc<JsonElement>())
                .map(new Func1<JsonElement, JsonObject>() {
                    @Override
                    public JsonObject call(JsonElement jsonElement) {
                        try {
                            return jsonElement.getAsJsonObject();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
