package com.hunliji.marrybiz.api.experienceshop;

import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;
import com.hunliji.marrybiz.model.experience.AdvDetail;
import com.hunliji.marrybiz.model.experience.HljExperienceData;
import com.hunliji.marrybiz.model.experience.ShowHistory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by jinxin on 2017/12/19 0019.
 */

public class ExperienceShopApi {

    /**
     * 体验店订单详情
     *
     * @param id
     * @return
     */
    public static Observable<AdvDetail> getExperienceOrderDetail(long id) {
        return HljHttp.getRetrofit()
                .create(ExperienceShopService.class)
                .getExperienceOrderDetail(id)
                .map(new HljHttpResultFunc<AdvDetail>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 体验店跟进历史
     *
     * @param id
     * @return
     */
    public static Observable<List<ShowHistory>> getExperienceShowHistory(long id) {
        return HljHttp.getRetrofit()
                .create(ExperienceShopService.class)
                .getExperienceShowHistory(id)
                .map(new Func1<HljHttpResult<HljHttpData<List<ShowHistory>>>, List<ShowHistory>>() {
                    @Override
                    public List<ShowHistory> call(
                            HljHttpResult<HljHttpData<List<ShowHistory>>>
                                    hljHttpDataHljHttpResult) {
                        if (hljHttpDataHljHttpResult == null || hljHttpDataHljHttpResult.getData
                                () == null) {
                            return null;
                        }
                        return hljHttpDataHljHttpResult.getData()
                                .getData();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 修改销售
     *
     * @return
     */
    public static Observable<HljHttpResult> modifySalerName(long id, String name) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("exp_saler", name);
        return HljHttp.getRetrofit()
                .create(ExperienceShopService.class)
                .modifySalerName(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 体验店推荐订单列表
     *
     * @param map
     * @return
     */
    public static Observable<HljExperienceData<List<AdvDetail>>>
    getExperienceShopOrderListObb(
            Map<String, Object> map) {
        map.put("per_page", HljCommon.PER_PAGE);
        return HljHttp.getRetrofit()
                .create(ExperienceShopService.class)
                .getExperienceShopOrderList(map)
                .map(new HljHttpResultFunc<HljExperienceData<List<AdvDetail>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 填写备注
     *
     * @return
     */
    public static Observable<HljHttpResult> fillRemark(long id, String remark) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("message", remark);
        return HljHttp.getRetrofit()
                .create(ExperienceShopService.class)
                .fillRemark(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    /**
     * 确认成单
     *
     * @return
     */
    public static Observable<HljHttpResult> confirmOrder(long id, String remark) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("message", remark);
        return HljHttp.getRetrofit()
                .create(ExperienceShopService.class)
                .confirmOrder(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 跟进失败
     *
     * @return
     */
    public static Observable<HljHttpResult> followFailed(long id, String remark) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("message", remark);
        return HljHttp.getRetrofit()
                .create(ExperienceShopService.class)
                .followFailed(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 确认到店
     * @return
     */
    public static Observable<HljHttpResult> arriveShop(long id,String message){
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("message", message);
        return HljHttp.getRetrofit()
                .create(ExperienceShopService.class)
                .arriveShop(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
