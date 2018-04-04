package me.suncloud.marrymemo.api.merchant;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hunliji.hljcommonlibrary.models.CategoryMark;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.merchant.MerchantChatData;
import com.hunliji.hljcommonlibrary.models.merchant.MerchantRecommendPosterItem;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;

import java.util.ArrayList;
import java.util.List;

import me.suncloud.marrymemo.model.PostScheduleDateBody;
import me.suncloud.marrymemo.model.merchant.wrappers.MerchantChatLinkTriggerPostBody;
import me.suncloud.marrymemo.model.merchant.wrappers.AppointmentPostBody;
import me.suncloud.marrymemo.model.wrappers.HljHttpHotelHallData;
import me.suncloud.marrymemo.model.wrappers.MerchantListData;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 跟商家相关的网络请求操作
 * Created by chen_bin on 2016/11/11 0011.
 */

public class MerchantApi {

    /**
     * 档期添加
     *
     * @param postScheduleDateBody 选中的档期列表post
     * @return
     */
    public static Observable submitHotelScheduleObb(
            PostScheduleDateBody postScheduleDateBody) {
        return HljHttp.getRetrofit()
                .create(MerchantService.class)
                .submitHotelSchedule(postScheduleDateBody)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 获取第一页商家列表用于区分推广商家和一般商家
     */
    public static Observable<MerchantListData> getMerchantListDataObb(
            String url, final int currentPage, long cityId) {
        return HljHttp.getRetrofit()
                .create(MerchantService.class)
                .getMerchants(url, cityId)
                .map(new HljHttpResultFunc<MerchantListData>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取商家列表
     */
    public static Observable<HljHttpData<List<Merchant>>> getMerchantListObb(
            String url, final int currentPage, long cityId) {
        return getMerchantListDataObb(url,
                currentPage,
                cityId).map(new Func1<MerchantListData, HljHttpData<List<Merchant>>>() {
            @Override
            public HljHttpData<List<Merchant>> call(
                    MerchantListData merchantListData) {
                HljHttpData<List<Merchant>> merchantsData = merchantListData.getNormalMerchant();
                if (currentPage == 1) {
                    HljHttpData<List<Merchant>> popularData = merchantListData.getPopularMerchant();
                    if (popularData != null && popularData.getData() != null) {
                        List<Merchant> list = merchantsData.getData();
                        if (list == null) {
                            list = new ArrayList<>();
                            merchantsData.setData(list);
                        }
                        list.addAll(0, popularData.getData());
                    }
                }
                return merchantsData;
            }
        });
    }


    /**
     * 商家问答头信息
     *
     * @param id 商家id
     */
    public static Observable<Merchant> getQaMerchantInfoObb(long id) {
        return HljHttp.getRetrofit()
                .create(MerchantService.class)
                .getQaMerchatInfo(id)
                .map(new HljHttpResultFunc<Merchant>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 商家主页获得merchant
     *
     * @param id
     * @return
     */
    public static Observable<HljHttpResult<Merchant>> getMerchantInfoV2(long id) {
        return HljHttp.getRetrofit()
                .create(MerchantService.class)
                .getMerchantInfoV2(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 大图模式列表
     *
     * @param merchantId
     * @return
     */
    public static Observable<HljHttpData<List<MerchantRecommendPosterItem>>>
    getMerchantRecommendPostersObb(
            long merchantId) {
        return HljHttp.getRetrofit()
                .create(MerchantService.class)
                .getMerchantRecommendPosters(merchantId)
                .map(new HljHttpResultFunc<HljHttpData<List<MerchantRecommendPosterItem>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 预约
     *
     * @param body
     * @return
     */
    public static Observable<JsonElement> makeAppointmentObb(AppointmentPostBody body) {
        return HljHttp.getRetrofit()
                .create(MerchantService.class)
                .makeAppointment(body)
                .map(new HljHttpResultFunc<JsonElement>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 获取旗舰版配置
     *
     * @param id 商家id
     * @return 0默认 1白 2黑 3绿 4蓝 5粉
     */
    public static Observable<Integer> getMerchantDecoration(long id) {
        return HljHttp.getRetrofit()
                .create(MerchantService.class)
                .getMerchantDecoration(id)
                .map(new HljHttpResultFunc<JsonElement>())
                .map(new Func1<JsonElement, Integer>() {
                    @Override
                    public Integer call(JsonElement jsonElement) {
                        try {
                            return jsonElement.getAsJsonObject()
                                    .get("theme")
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

    /**
     * 获取商家轻松聊内容
     *
     * @param id 商家id
     * @return
     */
    public static Observable<MerchantChatData> getMerchantChatData(long id) {
        return HljHttp.getRetrofit()
                .create(MerchantService.class)
                .getMerchantChatData(id)
                .map(new HljHttpResultFunc<MerchantChatData>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 请求让服务器自动发送商家轻松聊信息
     *
     * @param id   商家ID
     * @param type 轻松聊消息类型
     * @return
     */
    public static Observable<JsonElement> postMerchantChatLinkTrigger(
            Long id, MerchantChatLinkTriggerPostBody.MerchantChatLinkType type, String carSpeech) {
        MerchantChatLinkTriggerPostBody body = new MerchantChatLinkTriggerPostBody(id,
                type,
                carSpeech);
        return HljHttp.getRetrofit()
                .create(MerchantService.class)
                .postMerchantChatLinkTrigger(body)
                .map(new HljHttpResultFunc<JsonElement>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 头部标签组区域
     *
     * @param entityId
     * @return
     */
    public static Observable<HljHttpData<List<CategoryMark>>> getMarkCategoryServiceHeadObb(
            long entityId) {
        return HljHttp.getRetrofit()
                .create(MerchantService.class)
                .getMarkCategoryServiceHead(entityId)
                .map(new HljHttpResultFunc<HljHttpData<List<CategoryMark>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 宴会厅详情
     *
     * @param id
     * @return
     */
    public static Observable<HljHttpHotelHallData> getHotelHallDetailObb(long id) {
        return HljHttp.getRetrofit()
                .create(MerchantService.class)
                .getHotelHallDetail(id)
                .map(new HljHttpResultFunc<HljHttpHotelHallData>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取商家筛选菜单
     *
     * @param city
     * @return
     */
    public static Observable<JsonObject> getMerchantFilter(long city) {
        return HljHttp.getRetrofit()
                .create(MerchantService.class)
                .getMerchantFilter(city)
                .map(new HljHttpResultFunc<JsonObject>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 商家公告
     *
     * @param id 商家id
     */
    public static Observable<String> getMerchantNotice(long id) {
        return HljHttp.getRetrofit()
                .create(MerchantService.class)
                .getMerchantNotice(id)
                .map(new HljHttpResultFunc<JsonElement>())
                .map(new Func1<JsonElement, String>() {
                    @Override
                    public String call(JsonElement jsonElement) {
                        try {
                            return jsonElement.getAsJsonObject()
                                    .get("content")
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


    public static Observable<List<String>> getMerchantPhones(long userId) {
        return HljHttp.getRetrofit()
                .create(MerchantService.class)
                .getMerchantPhones(userId)
                .map(new HljHttpResultFunc<List<String>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}

