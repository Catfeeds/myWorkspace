package me.suncloud.marrymemo.api.event;


import com.google.gson.JsonElement;
import com.hunliji.hljcommonlibrary.models.event.EventInfo;
import com.hunliji.hljcommonlibrary.models.event.SignUpInfo;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by chen_bin on 2016/9/9 0009.
 */
public class EventApi {

    /**
     * 首页和发现页活动
     *
     * @param position 1首页 2发现页
     * @return
     */
    public static Observable<HljHttpData<List<EventInfo>>> getEventRandom(
            int position) {
        return HljHttp.getRetrofit()
                .create(EventService.class)
                .getEventRandom(position)
                .map(new HljHttpResultFunc<HljHttpData<List<EventInfo>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     *首页和发现页活动
     *
     * @param position 1首页 2发现页
     * @return
     */
    /**
     * 活动列表
     *
     * @param page
     * @param perPage
     * @return
     */
    public static Observable<HljHttpData<List<EventInfo>>> getEventListObb(
            int page, int perPage) {
        return HljHttp.getRetrofit()
                .create(EventService.class)
                .getEventList(page, perPage)
                .map(new HljHttpResultFunc<HljHttpData<List<EventInfo>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 活动详情
     *
     * @param id
     * @return
     */
    public static Observable<EventInfo> getEventDetailObb(long id) {
        return HljHttp.getRetrofit()
                .create(EventService.class)
                .getEventDetail(id)
                .map(new HljHttpResultFunc<EventInfo>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 我参加的活动列表
     *
     * @param page
     * @param perPage
     * @return
     */
    public static Observable<HljHttpData<List<EventInfo>>> getMyEventListObb(
            int page, int perPage) {
        return HljHttp.getRetrofit()
                .create(EventService.class)
                .getMyEventList(page, perPage)
                .map(new HljHttpResultFunc<HljHttpData<List<EventInfo>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 活动报名
     *
     * @return
     */
    public static Observable<SignUpInfo> signUpObb(
            long id, String realName, String phone, String smsCode) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("name", realName);
        map.put("phone", phone);
        map.put("sms_code", smsCode);
        return HljHttp.getRetrofit()
                .create(EventService.class)
                .signUp(map)
                .map(new HljHttpResultFunc<SignUpInfo>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 中奖列表
     *
     * @param id
     * @param page
     * @param perPage
     * @return
     */
    public static Observable<HljHttpData<List<SignUpInfo>>> getWinnerListObb(
            long id, int page, int perPage) {
        return HljHttp.getRetrofit()
                .create(EventService.class)
                .getWinnerList(id, page, perPage)
                .map(new HljHttpResultFunc<HljHttpData<List<SignUpInfo>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 投诉
     *
     * @param id
     * @param message
     * @param type
     * @return
     */
    public static Observable reportObb(long id, String message, int type) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("message", message);
        map.put("type", type);
        return HljHttp.getRetrofit()
                .create(EventService.class)
                .report(map)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 检测手机号是否真实
     *
     * @param phone
     * @param smsCode
     * @return
     */
    public static Observable<String> checkPhoneIsRealObb(String phone, String smsCode) {
        Map<String, Object> map = new HashMap<>();
        map.put("phone", phone);
        map.put("sms_code", smsCode);
        return HljHttp.getRetrofit()
                .create(EventService.class)
                .checkPhoneIsReal(map)
                .map(new HljHttpResultFunc<JsonElement>())
                .map(new Func1<JsonElement, String>() {
                    @Override
                    public String call(JsonElement jsonElement) {
                        try {
                            return jsonElement.getAsJsonObject()
                                    .get("code")
                                    .getAsString();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
