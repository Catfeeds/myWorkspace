package me.suncloud.marrymemo.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hunliji.hljcommonlibrary.models.Location;
import com.hunliji.hljcommonlibrary.utils.ChannelUtil;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DeviceUuidFactory;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljcommonlibrary.utils.LocationSession;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.entities.HljApiException;
import com.hunliji.hljhttplibrary.utils.GsonUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;
import com.hunliji.hljhttplibrary.utils.NetUtil;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.TimeZone;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.model.CityData;
import me.suncloud.marrymemo.model.Splash;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.model.WXWall;
import me.suncloud.marrymemo.model.WeddingConsult;
import me.suncloud.marrymemo.model.main.YouLike;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.TimeUtil;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 公用的一些api
 * Created by jinxin on 2016/11/18 0018.
 */

public class CustomCommonApi {

    /**
     * 获得猜你喜欢
     *
     * @param onlyUser 首页传1 买套餐传0 1表示会根据userId 去取数据
     * @return
     */
    public static Observable<YouLike> getYouLike(int onlyUser) {
        return HljHttp.getRetrofit()
                .create(CustomCommonService.class)
                .getYouLike(onlyUser)
                .map(new HljHttpResultFunc<YouLike>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    public static Observable<JsonElement> getJson(String url) {
        return HljHttp.getRetrofit()
                .create(CustomCommonService.class)
                .getJson(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    public static Observable<CityData> getCities(final Context context, final long cid) {
        return HljHttp.getRetrofit()
                .create(CustomCommonService.class)
                .getCities(cid > 0 ? cid : null)
                .map(new HljHttpResultFunc<JsonObject>())
                .map(new Func1<JsonObject, CityData>() {
                    @Override
                    public CityData call(JsonObject jsonObject) {
                        if (jsonObject != null) {
                            try {
                                jsonObject.addProperty("cityId", cid); //记录当前城市id
                                OutputStreamWriter out = new OutputStreamWriter(context
                                        .openFileOutput(
                                        Constants.NEW_CITIES_FILE,
                                        Context.MODE_PRIVATE));
                                out.write(jsonObject.toString());
                                out.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            CityData cityData = GsonUtil.getGsonInstance()
                                    .fromJson(jsonObject, CityData.class);
                            cityData.setCityId(cid);
                            return cityData;
                        }
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 意见反馈
     *
     * @param content 问题描述
     * @param phone   联系电话
     * @return
     */
    public static Observable postFeedback(String content, String phone) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("content", content);
        jsonObject.addProperty("contact", phone);
        jsonObject.addProperty("device", android.os.Build.MODEL);
        jsonObject.addProperty("system", android.os.Build.VERSION.RELEASE);
        return HljHttp.getRetrofit()
                .create(CustomCommonService.class)
                .postFeedback(jsonObject)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * @return
     */
    public static Observable saveClientInfo(Context context, String clientId) {
        User user = Session.getInstance()
                .getCurrentUser(context);
        if (user == null || user.getId() == 0 || CommonUtil.isEmpty(clientId)) {
            return Observable.empty();
        }
        JsonObject infoJson = new JsonObject();
        infoJson.addProperty("cid", clientId);
        infoJson.addProperty("user_id", user.getId());
        infoJson.addProperty("from", "android");
        infoJson.addProperty("phone_token",
                DeviceUuidFactory.getInstance()
                        .getDeviceUuidString(context));
        infoJson.addProperty("apns_token", "");
        infoJson.addProperty("app_version", Constants.APP_VERSION);
        infoJson.addProperty("phone_type", 2);
        infoJson.addProperty("device", android.os.Build.MODEL);
        infoJson.addProperty("system", android.os.Build.VERSION.RELEASE);
        Location location = LocationSession.getInstance()
                .getLocation(context);
        if (location != null) {
            infoJson.addProperty("city", location.getCity());
            infoJson.addProperty("province", location.getProvince());
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("info", infoJson);
        return HljHttp.getRetrofit()
                .create(CustomCommonService.class)
                .saveClientInfo(jsonObject)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    public static Observable<Splash> getSplash() {
        return HljHttp.getRetrofit()
                .create(CustomCommonService.class)
                .getSplash()
                .map(new HljHttpResultFunc<Splash>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 微信墙信息
     */
    public static Observable<WXWall> getWXWall() {
        return HljHttp.getRetrofit()
                .create(CustomCommonService.class)
                .getWXWall()
                .map(new HljHttpResultFunc<WXWall>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 首页是否点赞接口
     *
     * @return
     */
    public static Observable<JsonElement> getCommentApp() {
        return HljHttp.getRetrofit()
                .create(CustomCommonService.class)
                .commentApp()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 备婚清单获得分类接口
     *
     * @return
     */
    public static Observable<JsonElement> getWeddingPrepareCategoryList() {
        return HljHttp.getRetrofit()
                .create(CustomCommonService.class)
                .getWeddingPrepareCategoryList()
                .map(new HljHttpResultFunc<JsonElement>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 备婚清单获得详情接口
     *
     * @param cid
     * @param categoryId
     * @return
     */
    public static Observable<JsonElement> getWeddingPrepareData(long cid, long categoryId) {
        return HljHttp.getRetrofit()
                .create(CustomCommonService.class)
                .getWeddingPrepareData(cid, categoryId)
                .map(new HljHttpResultFunc<JsonElement>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    public static Observable<JsonElement> addBudgetInfo(JsonObject jsonObject) {
        return HljHttp.getRetrofit()
                .create(CustomCommonService.class)
                .addBudgetInfo(jsonObject)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<WeddingConsult> getWeddingConsult(String from) {
        return HljHttp.getRetrofit()
                .create(CustomCommonService.class)
                .getWeddingConsult(from)
                .map(new HljHttpResultFunc<WeddingConsult>())
                .map(new Func1<WeddingConsult, WeddingConsult>() {
                    @Override
                    public WeddingConsult call(WeddingConsult weddingConsult) {
                        if (weddingConsult == null || weddingConsult.getSupport() == null ||
                                TextUtils.isEmpty(
                                weddingConsult.getSupport()
                                        .getHxIm())) {
                            throw new HljApiException("环信用户异常，不能使用顾问");
                        }
                        return weddingConsult;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获得购物车数量
     */
    public static Observable<JsonElement> getCartItemsCount() {
       return HljHttp.getRetrofit()
                .create(CustomCommonService.class)
                .getCartItemsCount()
                .map(new HljHttpResultFunc<JsonElement>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 设备统计
     * @return
     */
    public static Observable<Long> createPhone(Context context,Location location) {
        JsonObject body=new JsonObject();
        try {
            JsonObject phoneObject=new JsonObject();
            phoneObject.addProperty("phone_token",DeviceUuidFactory.getInstance()
                    .getDeviceUuidString(context));
            phoneObject.addProperty("app_version",Constants.APP_VERSION);
            phoneObject.addProperty("apns_token","");
            phoneObject.addProperty("phone_type",String.valueOf(2));
            phoneObject.addProperty("device",android.os.Build.MODEL);
            phoneObject.addProperty("system",android.os.Build.VERSION.RELEASE);
            if(location==null){
                location = LocationSession.getInstance()
                        .getLocation(context);
            }
            if (location != null) {
                if (TextUtils.isEmpty(location.getCity())) {
                    phoneObject.addProperty("city", location.getCity());
                }
                if (TextUtils.isEmpty(location.getProvince())) {
                    phoneObject.addProperty("province", location.getProvince());
                }
            }
            body.add("phone",phoneObject);
        }catch (Exception e){
            e.printStackTrace();
        }
        return HljHttp.getRetrofit()
                .create(CustomCommonService.class)
                .createPhone(body)
                .map(new Func1<JsonObject, Long>() {
                    @Override
                    public Long call(JsonObject jsonObject) {
                        long systemTime = jsonObject.get("current_time").getAsLong();
                        if (systemTime > 0) {
                            TimeUtil.setTimeOffset(systemTime * 1000);
                            HljTimeUtils.setTimeOffset(systemTime * 1000);
                        }
                        return systemTime;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 设备统计
     * @param context
     * @return
     */
    public static Observable<JsonElement> sendAppAnalytics(Context context) {
        JsonObject body=new JsonObject();
        try {
            SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREF_FILE,
                    Context.MODE_PRIVATE);
            String cid = sharedPreferences.getString("clientid", "");
            User user = Session.getInstance()
                    .getCurrentUser(context);
            String source = ChannelUtil.getChannel(context);

            body.addProperty("action","view");
            body.addProperty("screen", 0);
            body.addProperty("os", "android");
            body.addProperty("version", Constants.APP_VERSION);
            body.addProperty("phone_token",
                    DeviceUuidFactory.getInstance()
                            .getDeviceUuidString(context));
            body.addProperty("user", user == null ? 0 : user.getId());
            body.addProperty("imei", DeviceUuidFactory.getIMEI(context));
            body.addProperty("android_id", DeviceUuidFactory.getAndroidId(context));
            body.addProperty("mac", NetUtil.getMacAddr(context));
            if (!TextUtils.isEmpty(cid)) {
                body.addProperty("cid", cid);
            }
            if (!TextUtils.isEmpty(source)) {
                body.addProperty("source", source);
            }
            body.addProperty("device", android.os.Build.MODEL);
            body.addProperty("sysver", android.os.Build.VERSION.RELEASE);
            body.addProperty("unix_time", System.currentTimeMillis() / 1000);
            body.addProperty("zone",
                    TimeZone.getDefault()
                            .getRawOffset() / 1000);
        }catch (Exception e){
            e.printStackTrace();
        }
        return HljHttp.getRetrofit()
                .create(CustomCommonService.class)
                .sendAppAnalytics(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
