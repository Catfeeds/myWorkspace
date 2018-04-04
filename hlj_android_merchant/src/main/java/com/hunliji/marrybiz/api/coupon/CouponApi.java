package com.hunliji.marrybiz.api.coupon;

import com.hunliji.hljcommonlibrary.models.coupon.CouponInfo;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;
import com.hunliji.hljcommonlibrary.models.PostIdBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 商家优惠券API接口Http方法汇总
 * Created by chen_bin on 2016/9/6 0006.
 */
public class CouponApi {

    /**
     * 优惠券列表
     *
     * @param name
     * @param valid
     * @param page
     * @param perPage
     * @return
     */
    public static Observable<HljHttpData<List<CouponInfo>>> getCouponListObb(
            String name, int valid, int page, int perPage) {
        return HljHttp.getRetrofit()
                .create(CouponService.class)
                .getCouponList(name, valid == 0 ? null : String.valueOf(valid), page, perPage)
                .map(new HljHttpResultFunc<HljHttpData<List<CouponInfo>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 激活优惠券
     *
     * @param id
     * @return
     */
    public static Observable activateCouponObb(long id) {
        return HljHttp.getRetrofit()
                .create(CouponService.class)
                .activateCoupon(new PostIdBody(id))
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 创建跟修改优惠券
     *
     * @param couponInfo
     * @return
     */
    public static Observable createCouponObb(
            CouponInfo couponInfo) {
        Map<String, Object> map = new HashMap<>();
        map.put("hidden", couponInfo.isHidden() ? 1 : 0);
        map.put("money_sill", couponInfo.getMoneySill());
        map.put("value",couponInfo.getValue());
        map.put("provide_end",
                couponInfo.getProvideEnd()
                        .toString(HljTimeUtils.DATE_FORMAT_SHORT));
        map.put("provide_start",
                couponInfo.getProvideStart()
                        .toString(HljTimeUtils.DATE_FORMAT_SHORT));
        map.put("title", couponInfo.getTitle());
        map.put("total_count", couponInfo.getTotalCount());
        map.put("type", couponInfo.getType());
        map.put("valid_end",
                couponInfo.getValidEnd()
                        .toString(HljTimeUtils.DATE_FORMAT_SHORT));
        map.put("valid_start",
                couponInfo.getProvideStart()
                        .toString(HljTimeUtils.DATE_FORMAT_SHORT));
        map.put("provided_count", couponInfo.getProvidedCount());
        map.put("online_used_count", couponInfo.getOnlineUsedCount());
        map.put("offline_used_count", couponInfo.getOfflineUsedCount());
        map.put("created_at", couponInfo.getCreatedAt());
        map.put("updated_at", couponInfo.getUpdatedAt());
        return HljHttp.getRetrofit()
                .create(CouponService.class)
                .createCoupon(map)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}