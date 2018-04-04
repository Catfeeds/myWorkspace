package me.suncloud.marrymemo.api.marry;

import android.text.TextUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import me.suncloud.marrymemo.model.marry.GuestCount;
import me.suncloud.marrymemo.model.marry.GuestGift;
import me.suncloud.marrymemo.model.marry.MarryBook;
import me.suncloud.marrymemo.model.marry.MarryTask;
import me.suncloud.marrymemo.model.marry.RecordBook;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by hua_rong on 2017/11/7
 * 记账本
 */

public class MarryApi {

    public static final int TYPE_GIFT_ID = 32;//type_id 礼金:32

    /**
     * 用户帐目列表
     *
     * @return
     */
    public static Observable<HljHttpData<List<MarryBook>>> getCashBookList() {
        return HljHttp.getRetrofit()
                .create(MarryService.class)
                .getCashBookList()
                .map(new HljHttpResultFunc<HljHttpData<List<MarryBook>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    public static Observable deleteBook(long id) {
        return HljHttp.getRetrofit()
                .create(MarryService.class)
                .deleteBook(id)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * @param money     金额
     * @param remark    备注
     * @param typeId    type_id 礼金:32
     * @param id        编辑时传
     * @param guestName 送礼宾客姓名
     * @param photos    账本传图
     * @return
     */
    public static Observable postCashBookAdd(
            double money,
            String remark,
            long typeId,
            long id,
            String guestName,
            ArrayList<Photo> photos) {
        HashMap<String, Object> hashMap = new LinkedHashMap<>();
        hashMap.put("money", money);
        hashMap.put("remark", remark);
        hashMap.put("type_id", typeId);
        if (typeId == TYPE_GIFT_ID && !TextUtils.isEmpty(guestName)) {
            hashMap.put("guest_name", guestName);
        }
        if (!CommonUtil.isCollectionEmpty(photos)) {
            hashMap.put("images", photos);
        }
        if (id > 0) {
            hashMap.put("id", id);
        }
        return HljHttp.getRetrofit()
                .create(MarryService.class)
                .postCashBookAdd(hashMap)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 分类列表
     *
     * @return
     */
    public static Observable<HljHttpData<List<RecordBook>>> getBookSortTypes() {
        return HljHttp.getRetrofit()
                .create(MarryService.class)
                .getBookSortTypes()
                .map(new HljHttpResultFunc<HljHttpData<List<RecordBook>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 编辑任务
     *
     * @param deleted  删除任务时传1
     * @param expireAt 到期时间
     * @param id       编辑的时候传 id 增加不传
     * @param title    内容
     * @param taTa     0分配给我, 1分配给TA
     * @return
     */
    public static Observable updateTask(
            Integer deleted, String expireAt, long id, String title, int taTa) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("expire_at", expireAt);
        if (id > 0) {
            jsonObject.addProperty("id", id);
        }
        jsonObject.addProperty("title", title);
        jsonObject.addProperty("to_ta", taTa);
        if (deleted != null) {
            jsonObject.addProperty("deleted", deleted);
        }
        return HljHttp.getRetrofit()
                .create(MarryService.class)
                .updateTask(jsonObject)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 完成任务
     *
     * @param id     任务id
     * @param status 0未完成 1已完成
     * @return
     */
    public static Observable checkTask(long id, int status) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", id);
        jsonObject.addProperty("status", status);
        return HljHttp.getRetrofit()
                .create(MarryService.class)
                .checkTask(jsonObject)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * @param toDo 默认不传全列表 传1返回未完成任务3条
     * @return
     */
    public static Observable<HljHttpData<List<MarryTask>>> getMarryTasks(Integer toDo) {
        return HljHttp.getRetrofit()
                .create(MarryService.class)
                .getMarryTasks(toDo)
                .map(new HljHttpResultFunc<HljHttpData<List<MarryTask>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 待选宾客数
     *
     * @return
     */
    public static Observable<GuestCount> getGuestNameCount() {
        return HljHttp.getRetrofit()
                .create(MarryService.class)
                .getGuestNameCount()
                .map(new HljHttpResultFunc<GuestCount>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 待选宾客列表
     *
     * @return
     */
    public static Observable<List<GuestGift>> getGuestNameList() {
        return HljHttp.getRetrofit()
                .create(MarryService.class)
                .getGuestNameList()
                .map(new HljHttpResultFunc<HljHttpData<JsonArray>>())
                .map(new Func1<HljHttpData<JsonArray>, List<GuestGift>>() {
                    @Override
                    public List<GuestGift> call(HljHttpData<JsonArray> jsonArrayHljHttpData) {
                        JsonArray jsonElements = jsonArrayHljHttpData.getData();
                        List<GuestGift> guestGifts = new ArrayList<>();
                        for (JsonElement jsonElement : jsonElements) {
                            if (jsonElement != null && !TextUtils.isEmpty(jsonElement.getAsString
                                    ())) {
                                GuestGift guestGift = new GuestGift();
                                guestGift.setGuestName(jsonElement.getAsString());
                                guestGifts.add(guestGift);
                            }
                        }
                        return guestGifts;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 批量导入礼金账本
     *
     * @return
     */
    public static Observable postBatchAdd(List<GuestGift> guestNames) {
        HashMap<String, Object> hashMap = new LinkedHashMap<>();
        hashMap.put("data", guestNames);
        return HljHttp.getRetrofit()
                .create(MarryService.class)
                .postBatchAdd(hashMap)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
