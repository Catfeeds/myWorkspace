package com.hunliji.marrybiz.api.event;

import com.hunliji.hljcommonlibrary.models.event.EventInfo;
import com.hunliji.hljcommonlibrary.models.event.SignUpInfo;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;
import com.hunliji.marrybiz.model.event.EventExample;
import com.hunliji.marrybiz.model.event.EventPoint;
import com.hunliji.marrybiz.model.event.EventWallet;
import com.hunliji.marrybiz.model.event.RechargeRecord;
import com.hunliji.marrybiz.model.event.RecordInfo;
import com.hunliji.marrybiz.model.wrapper.HljHttpSignUpsData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 商家活动API接口Http方法汇总
 * Created by chen_bin on 2016/9/6 0006.
 */
public class EventApi {

    /**
     * 申请活动
     *
     * @param recordInfo
     * @return
     */
    public static Observable applyEventObb(RecordInfo recordInfo) {
        return HljHttp.getRetrofit()
                .create(EventService.class)
                .applyEvent(recordInfo)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 用户签到
     *
     * @param merchantId
     * @param validCode
     * @return
     */
    public static Observable checkInObb(long merchantId, String validCode) {
        Map<String, Object> map = new HashMap<>();
        map.put("merchant_id", merchantId);
        map.put("valid_code_post", validCode);
        return HljHttp.getRetrofit()
                .create(EventService.class)
                .checkIn(map)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 根据凭证获取信息
     *
     * @param validCode
     * @return
     */
    public static Observable<SignUpInfo> getSignUpInfoByCodeObb(String validCode) {
        return HljHttp.getRetrofit()
                .create(EventService.class)
                .getSignUpInfoByCode(validCode)
                .map(new HljHttpResultFunc<SignUpInfo>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 活动列表-新
     *
     * @param isNotPublish 传1 待上线 ；不传 已上线
     * @param page
     * @param perPage
     * @return
     */
    public static Observable<HljHttpData<List<EventInfo>>> getEventsObb(
            int isNotPublish, int page, int perPage) {
        return HljHttp.getRetrofit()
                .create(EventService.class)
                .getEvents(isNotPublish == 0 ? null : String.valueOf(isNotPublish), page, perPage)
                .map(new HljHttpResultFunc<HljHttpData<List<EventInfo>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 活动记录申请列表
     *
     * @param page
     * @param perPage
     * @return
     */
    public static Observable<HljHttpData<List<RecordInfo>>> getRecordEventsObb(
            int page, int perPage) {
        return HljHttp.getRetrofit()
                .create(EventService.class)
                .getRecordEvents(page, perPage)
                .map(new HljHttpResultFunc<HljHttpData<List<RecordInfo>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 活动范例列表
     *
     * @param page
     * @param perPage
     * @return
     */
    public static Observable<HljHttpData<List<EventExample>>> getEventExamplesObb(
            int page, int perPage) {
        return HljHttp.getRetrofit()
                .create(EventService.class)
                .getEventExamples(page, perPage)
                .map(new HljHttpResultFunc<HljHttpData<List<EventExample>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取申请记录详情
     *
     * @param id
     * @return
     */
    public static Observable<RecordInfo> getRecordDetailObb(long id) {
        return HljHttp.getRetrofit()
                .create(EventService.class)
                .getRecordDetail(id)
                .map(new HljHttpResultFunc<RecordInfo>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 报名列表
     *
     * @param id
     * @param status
     * @param page
     * @param perPage
     * @return
     */
    public static Observable<HljHttpSignUpsData> getSignUpListObb(
            long id, int status, int page, int perPage) {
        return HljHttp.getRetrofit()
                .create(EventService.class)
                .getSignUpList(id, status == 0 ? null : String.valueOf(status), page, perPage)
                .map(new HljHttpResultFunc<HljHttpSignUpsData>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 活动管理-流水
     *
     * @param page
     * @return
     */
    public static Observable<HljHttpData<List<RechargeRecord>>> getPunchWaterObb(
            int page) {
        return HljHttp.getRetrofit()
                .create(EventService.class)
                .getPunchWaterObb(page, 20)
                .map(new HljHttpResultFunc<HljHttpData<List<RechargeRecord>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 活动管理 - 活动点基本信息
     *
     * @return
     */
    public static Observable<EventPoint> getEventPointObb() {
        return HljHttp.getRetrofit()
                .create(EventService.class)
                .getEventPoint()
                .map(new HljHttpResultFunc<EventPoint>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取活动钱包
     *
     * @return
     */
    public static Observable<EventWallet> getEventWallet() {
        return HljHttp.getRetrofit()
                .create(EventService.class)
                .getEventWallet()
                .map(new HljHttpResultFunc<EventWallet>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 动管理 - get 联系报名用户
     *
     * @param id
     * @return
     */
    public static Observable<EventPoint> getPayViewPhoneObb(long id) {
        return HljHttp.getRetrofit()
                .create(EventService.class)
                .getPayViewPhone(id)
                .map(new HljHttpResultFunc<EventPoint>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}