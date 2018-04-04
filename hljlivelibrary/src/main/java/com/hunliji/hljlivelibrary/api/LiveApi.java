package com.hunliji.hljlivelibrary.api;

import com.google.gson.JsonElement;
import com.hunliji.hljcommonlibrary.models.live.LiveChannel;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.entities.HljApiException;
import com.hunliji.hljhttplibrary.entities.HljHttpCountData;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;
import com.hunliji.hljlivelibrary.HljLive;
import com.hunliji.hljlivelibrary.models.LiveMessage;
import com.hunliji.hljlivelibrary.models.LiveRelevantWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by mo_yu on 2016/10/24.
 * 直播接口Http方法汇总
 */

public class LiveApi {
    /**
     * 获取直播列表
     *
     * @param page
     * @return
     */
    public static Observable<HljHttpCountData<List<LiveChannel>>> getLiveChannelListObb(
            int page, long merchantId) {
        return HljHttp.getRetrofit()
                .create(LiveService.class)
                .getLiveChannelList(HljLive.apiInterface.liveChannelUrl(), 20, page, merchantId)
                .map(new HljHttpResultFunc<HljHttpCountData<List<LiveChannel>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取直播相关列表
     *
     * @param page
     * @return
     */
    public static Observable<HljHttpData<List<LiveRelevantWrapper>>> getLiveRelateListObb(
            long channelId, int page) {
        return HljHttp.getRetrofit()
                .create(LiveService.class)
                .getLiveRelateList(channelId, 20, page)
                .map(new HljHttpResultFunc<HljHttpData<List<LiveRelevantWrapper>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取直播相关列表
     *
     * @return
     */
    public static Observable<HljHttpData<List<LiveRelevantWrapper>>> getLiveShopListObb(
            long channelId) {
        return HljHttp.getRetrofit()
                .create(LiveService.class)
                .getLiveShopList(channelId)
                .map(new HljHttpResultFunc<HljHttpData<List<LiveRelevantWrapper>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 直播详情
     *
     * @param id channelId
     * @return
     */
    public static Observable<LiveChannel> getLiveChannelObb(long id) {
        return HljHttp.getRetrofit()
                .create(LiveService.class)
                .getLiveChannel(id)
                .map(new HljHttpResultFunc<LiveChannel>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 直播历史消息列表
     * history?channel_id=%s&room_type=%s&last_id=%s&no_stick=%s&per_page=%s
     *
     * @param id      channelId
     * @param type    房间类型 1直播 2 聊天室
     * @param lastId  获取小于该Id的消息（不包含）为 0 时从头开始
     * @param noStick 为1不包含置顶的消息
     * @param count   返回数量
     * @return
     */
    public static Observable<List<LiveMessage>> getLiveHistoriesObb(
            long id, int type, long lastId, int noStick, int count) {
        return HljHttp.getRetrofit()
                .create(LiveService.class)
                .getLiveHistories(HljLive.getLivePath(HljLive.LIVE_HISTORIES,
                        id,
                        type,
                        lastId,
                        noStick,
                        count))
                .map(new HljHttpResultFunc<HljHttpData<List<LiveMessage>>>())
                .map(new Func1<HljHttpData<List<LiveMessage>>, List<LiveMessage>>() {
                    @Override
                    public List<LiveMessage> call(HljHttpData<List<LiveMessage>> listHljHttpData) {
                        if (listHljHttpData != null && listHljHttpData.getData() != null) {
                            return listHljHttpData.getData();
                        }
                        return new ArrayList<>();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 领取优惠券
     *
     * @param ids
     * @return
     */
    public static Observable receiveCouponObb(String ids) {
        return HljHttp.getRetrofit()
                .create(LiveService.class)
                .receiveCoupon(ids)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable getLiveCouponObb(String ids) {
        return HljHttp.getRetrofit()
                .create(LiveService.class)
                .getLiveCoupon(ids, "live")
                .map(new LiveGetHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 领取红包
     *
     * @param exchangeCode
     * @return
     */
    public static Observable receiveRedPacketObb(String exchangeCode) {
        return HljHttp.getRetrofit()
                .create(LiveService.class)
                .receiveRedPacket(exchangeCode)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 直播领取红包
     *
     * @param exchangeCode
     * @return
     */
    public static Observable getLiveRedPacketObb(String exchangeCode) {
        return HljHttp.getRetrofit()
                .create(LiveService.class)
                .receiveRedPacket(exchangeCode, "live")
                .map(new LiveGetHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    static class LiveGetHttpResultFunc<T> implements Func1<HljHttpResult<T>, T> {

        @Override
        public T call(HljHttpResult<T> tHljHttpResult) {
            if (tHljHttpResult.getStatus()
                    .getRetCode() != 0 && tHljHttpResult.getStatus()
                    .getRetCode() != 11) {
                // 11 也是正确的
                throw new HljApiException(tHljHttpResult.getStatus());
            }
            if (tHljHttpResult.getCurrentTime() > 0) {
                HljTimeUtils.setTimeOffset(tHljHttpResult.getCurrentTime() * 1000);
            }

            return tHljHttpResult.getData();
        }
    }

    /**
     * 判断是否已收藏商家is_collected
     *
     * @param merchatnId
     * @return
     */
    public static Observable<Boolean> isCollectMerchant(long merchatnId) {
        return HljHttp.getRetrofit()
                .create(LiveService.class)
                .getCollectStatus(merchatnId)
                .map(new HljHttpResultFunc<JsonElement>())
                .map(new Func1<JsonElement, Boolean>() {
                    @Override
                    public Boolean call(JsonElement jsonElement) {
                        return CommonUtil.getAsBoolean(jsonElement, "is_collected");
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable makeAppointment(long id) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        return HljHttp.getRetrofit()
                .create(LiveService.class)
                .makeAppointment(map)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
