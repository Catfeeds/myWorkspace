package com.hunliji.marrybiz.api.merchant;

import com.google.gson.JsonElement;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;
import com.hunliji.marrybiz.model.merchant.DescriptionLink;
import com.hunliji.marrybiz.model.merchant.MerchantNotice;
import com.hunliji.marrybiz.model.merchant.MerchantNoticePostBody;
import com.hunliji.marrybiz.model.merchant.MerchantPrivilegeRecord;
import com.hunliji.marrybiz.model.merchant.MerchantUpgradeInfo;
import com.hunliji.marrybiz.model.merchant.PostWorkBody;
import com.hunliji.marrybiz.model.merchant.Reservation;
import com.hunliji.marrybiz.model.merchant.ShopInfo;
import com.hunliji.marrybiz.model.wrapper.RecommendWork;
import com.hunliji.marrybiz.model.wrapper.WorkInfoData;

import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by chen_bin on 2016/9/13 0013.
 */
public class MerchantApi {

    public static Observable<ShopInfo> getShopInfoObb() {
        return HljHttp.getRetrofit()
                .create(MerchantService.class)
                .getShopInfo()
                .map(new HljHttpResultFunc<ShopInfo>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 检查是否绑定
     *
     * @return true 已绑  false 未绑
     */
    public static Observable<Boolean> checkedBindWechatObb() {
        return HljHttp.getRetrofit()
                .create(MerchantService.class)
                .checkedBindWechat()
                .map(new HljHttpResultFunc<JsonElement>())
                .map(new Func1<JsonElement, Boolean>() {
                    @Override
                    public Boolean call(JsonElement jsonElement) {
                        try {
                            return jsonElement.getAsJsonObject()
                                    .get("is_bind")
                                    .getAsInt() > 0;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return false;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 绑定微信
     *
     * @param code 微信code
     */
    public static Observable<JsonElement> bindWechatObb(String code) {
        return HljHttp.getRetrofit()
                .create(MerchantService.class)
                .bindWechat(code)
                .map(new HljHttpResultFunc<JsonElement>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 解绑无需参数
     */
    public static Observable<JsonElement> releaseBindWechatObb() {
        return HljHttp.getRetrofit()
                .create(MerchantService.class)
                .releaseBindWechat()
                .map(new HljHttpResultFunc<JsonElement>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 预约管理列表
     *
     * @param page
     * @param perPage
     * @return
     */
    public static Observable<HljHttpData<List<Reservation>>> getReservationListObb(
            int page, int perPage) {
        return HljHttp.getRetrofit()
                .create(MerchantService.class)
                .getReservationList(page, perPage)
                .map(new HljHttpResultFunc<HljHttpData<List<Reservation>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 预约管理联系
     *
     * @param id
     * @param status
     * @return
     */
    public static Observable editReservationStatusObb(long id, int status) {
        return HljHttp.getRetrofit()
                .create(MerchantService.class)
                .editReservationStatus(new Reservation(id, status))
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 商家修改到店预约
     *
     * @param id
     * @param date
     * @param fullName
     * @return
     */
    public static Observable editAppointmentObb(long id, String date, String fullName) {
        return HljHttp.getRetrofit()
                .create(MerchantService.class)
                .editAppointment(new Reservation(id, date, fullName))
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 商家主页套餐和案例的列表
     *
     * @param id
     * @param kind
     * @param sort
     * @param page
     * @param perPage
     * @return
     */
    public static Observable<HljHttpData<List<Work>>> getMerchantMealsAndCasesObb(
            long id, String kind, String sort, int page, int perPage) {
        return HljHttp.getRetrofit()
                .create(MerchantService.class)
                .getMerchantMealsAndCases(id, kind, sort, page, perPage)
                .map(new HljHttpResultFunc<HljHttpData<List<Work>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 套餐和案例列表详情
     *
     * @param id 套餐或案例id
     * @return
     */
    public static Observable<Work> getWorkInfo(long id) {
        return HljHttp.getRetrofit()
                .create(MerchantService.class)
                .getWorkInfo(id)
                .map(new HljHttpResultFunc<WorkInfoData>())
                .map(new Func1<WorkInfoData, Work>() {
                    @Override
                    public Work call(WorkInfoData workInfoData) {
                        return workInfoData.getWork();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取商家推荐橱窗（套餐）列表
     *
     * @return
     */
    public static Observable<HljHttpData<List<RecommendWork>>> getRecommendWorks() {
        return HljHttp.getRetrofit()
                .create(MerchantService.class)
                .getRecommendWorks()
                .map(new HljHttpResultFunc<HljHttpData<List<RecommendWork>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 取消套餐推荐
     *
     * @param id
     * @return
     */
    public static Observable cancelRecommendWork(long id) {
        return HljHttp.getRetrofit()
                .create(MerchantService.class)
                .cancelRecommendWork(new PostWorkBody(id))
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 推荐一个套餐
     *
     * @param seat      位置 仅支持 1 2 3
     * @param setMealId 套餐id
     * @return
     */
    public static Observable editRecommendWork(int seat, long setMealId) {
        return HljHttp.getRetrofit()
                .create(MerchantService.class)
                .editRecommendWork(new PostWorkBody(seat, setMealId))
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获得商家公告
     *
     * @return
     */
    public static Observable<MerchantNotice> getMerchantNotice() {
        return HljHttp.getRetrofit()
                .create(MerchantService.class)
                .getMerchantNotice()
                .map(new HljHttpResultFunc<MerchantNotice>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 编辑或者添加商家店铺公告
     *
     * @param body
     * @return
     */
    public static Observable<HljHttpResult> editMerchantNotice(MerchantNoticePostBody body) {
        return HljHttp.getRetrofit()
                .create(MerchantService.class)
                .editMerchantNotice(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 删除商家店铺公告
     *
     * @return
     */
    public static Observable<HljHttpResult> deleteMerchantNotice() {
        return HljHttp.getRetrofit()
                .create(MerchantService.class)
                .deleteMerchantNotice()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 是否可添加编辑店铺公告
     * retcode 返回1003 错误码为不可编辑添加
     * msg 2016-07-11 23:59:59 可编辑添加的时间节点
     *
     * @return
     */
    public static Observable<HljHttpResult> checkMerchantNotice() {
        return HljHttp.getRetrofit()
                .create(MerchantService.class)
                .checkMerchantNotice()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获得商家特权记录
     *
     * @return
     */
    public static Observable<MerchantPrivilegeRecord> getMerchantPrivilegeRecordList() {
        return HljHttp.getRetrofit()
                .create(MerchantService.class)
                .getMerchantPrivilegeRecordList()
                .map(new HljHttpResultFunc<MerchantPrivilegeRecord>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取营销 不分icon 跳转地址
     * @return
     */
    public static Observable<DescriptionLink> getmerchantDescriptionLink() {
        return HljHttp.getRetrofit()
                .create(MerchantService.class)
                .getmerchantDescriptionLink()
                .map(new HljHttpResultFunc<DescriptionLink>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 当前商家是否需要升级套餐
     *
     * @return
     */
    public static Observable<MerchantUpgradeInfo> checkWorksNeedUpgrade() {
        return HljHttp.getRetrofit()
                .create(MerchantService.class)
                .checkWorksNeedUpgrade()
                .map(new HljHttpResultFunc<MerchantUpgradeInfo>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取商家店铺信息
     */
    public static Observable<JsonElement> getMerchantInfoObb(boolean isPending) {
        return HljHttp.getRetrofit()
                .create(MerchantService.class)
                .getMerchantInfo(isPending)
                .map(new HljHttpResultFunc<JsonElement>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 编辑商家店铺信息
     */
    public static Observable postMerchantInfoObb(Map<String, Object> map) {
        return HljHttp.getRetrofit()
                .create(MerchantService.class)
                .postMerchantInfo(map)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
