package com.hunliji.marrybiz.api.customer;

import android.text.TextUtils;

import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;
import com.hunliji.marrybiz.model.customer.MerchantCustomer;
import com.hunliji.marrybiz.model.customer.MerchantCustomerModifyBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by jinxin on 2017/8/15 0015.
 */

public class CustomerApi {

    /**
     * 客资列表
     * deal_will int 成交意愿 0低 1 一般 2高
     * nick      用户名
     * user_name 用户姓名
     * user_phone 用户电话
     * weddingday 婚期
     * page
     * per_page
     *
     * @param nick      用户名
     * @param userName  用户姓名
     * @param userPhone 用户姓名
     * @param page      页码
     * @return
     */
    public static Observable<HljHttpData<List<MerchantCustomer>>> getCustomerList(
            String nick, String userPhone, String userName, int page) {
        Map<String, String> params = new HashMap<>();
        params.put("nick", TextUtils.isEmpty(nick) ? "" : nick);
        params.put("user_phone", TextUtils.isEmpty(userPhone) ? "" : userPhone);
        params.put("user_name", TextUtils.isEmpty(userName) ? "" : userName);
        params.put("page", String.valueOf(page));
        params.put("per_page", String.valueOf(HljCommon.PER_PAGE));
        return HljHttp.getRetrofit()
                .create(CustomerService.class)
                .getCustomerList(params)
                .map(new HljHttpResultFunc<HljHttpData<List<MerchantCustomer>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 保存客资信息
     *
     * @param body
     * @return
     */
    public static Observable<HljHttpResult> saveCustomer(MerchantCustomerModifyBody body) {
        return HljHttp.getRetrofit()
                .create(CustomerService.class)
                .saveCustomer(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获得客资详情
     * @param userId
     * @return
     */
    public static Observable<MerchantCustomer> getMerchantCustomerDetail(long userId){
        return HljHttp.getRetrofit()
                .create(CustomerService.class)
                .getMerchantCustomerDetail(userId)
                .map(new HljHttpResultFunc<MerchantCustomer>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
