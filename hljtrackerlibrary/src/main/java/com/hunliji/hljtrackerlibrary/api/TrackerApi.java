package com.hunliji.hljtrackerlibrary.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Suncloud on 2016/8/1.
 */
public class TrackerApi {

    public static Observable<Boolean> postTrackers(JsonArray array) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("events", array);
        return HljHttp.getRetrofit()
                .create(TrackerService.class)
                .postTrackers(jsonObject)
                .map(new Func1<JsonElement, Boolean>() {
                    @Override
                    public Boolean call(JsonElement jsonElement) {
                        try {
                            if (jsonElement.isJsonObject()) {
                                return jsonElement.getAsJsonObject()
                                        .getAsJsonObject("meta")
                                        .get("result")
                                        .getAsBoolean();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return false;
                    }
                })
                .subscribeOn(Schedulers.io());
    }

    public static Observable<Boolean> postTrackersV2(JsonArray array) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("events", array);
        return HljHttp.getRetrofit()
                .create(TrackerService.class)
                .postTrackersV2(jsonObject)
                .map(new Func1<JsonElement, Boolean>() {
                    @Override
                    public Boolean call(JsonElement jsonElement) {
                        try {
                            if (jsonElement.isJsonObject()) {
                                return jsonElement.getAsJsonObject()
                                        .getAsJsonObject("status")
                                        .get("RetCode")
                                        .getAsInt() == 0;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return false;
                    }
                })
                .subscribeOn(Schedulers.io());
    }


    /**
     * @param id      分享实体id
     * @param type    分享类型 merchant/set_meal/answer/note(动态)
     * @return
     */
    public static Observable<JsonElement> postShareAction(long id, String type) {
        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("entity_id",id);
        jsonObject.addProperty("type",type);
        return HljHttp.getRetrofit()
                .create(TrackerService.class)
                .postShareAction(jsonObject)
                .map(new HljHttpResultFunc<JsonElement>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    public static Observable miaoZhenTracker(String url) {
        return HljHttp.getRetrofit()
                .create(TrackerService.class)
                .miaoZhenTracker(url)
                .subscribeOn(Schedulers.io());
    }
}
