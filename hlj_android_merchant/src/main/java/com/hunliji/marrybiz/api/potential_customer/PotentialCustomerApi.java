package com.hunliji.marrybiz.api.potential_customer;

import com.google.gson.JsonObject;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;
import com.hunliji.marrybiz.model.potential_customer.PotentialCustomer;

import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by wangtao on 2017/8/8.
 */

public class PotentialCustomerApi {


    /**
     * 未处理客资数
     *
     * @return
     */
    public static Observable<Integer> getPendingNum() {
        return HljHttp.getRetrofit()
                .create(PotentialCustomerService.class)
                .getPendingNum()
                .map(new HljHttpResultFunc<JsonObject>())
                .map(new Func1<JsonObject, Integer>() {
                    @Override
                    public Integer call(JsonObject jsonObject) {
                        try {
                            return jsonObject.get("pending_num")
                                    .getAsInt();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return 0;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }


    public static Observable<HljHttpData<List<PotentialCustomer>>> getPotentialCustomers(
            int page, int perPage, int... status) {
        return HljHttp.getRetrofit()
                .create(PotentialCustomerService.class)
                .getPotentialCustomers(page, perPage, status)
                .map(new HljHttpResultFunc<HljHttpData<List<PotentialCustomer>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
