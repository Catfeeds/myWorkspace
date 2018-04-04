/**
 *
 */
package me.suncloud.marrymemo.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.hunliji.hljcardlibrary.HljCard;
import com.hunliji.hljchatlibrary.WebSocket;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.Location;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.LocationSession;
import com.hunliji.hljcommonlibrary.view_tracker.HljViewTracker;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.utils.GsonUtil;
import com.hunliji.hljkefulibrary.HljKeFu;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.CustomCommonApi;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.modulehelper.ModuleUtils;
import rx.Subscription;
import rx.functions.Action1;

/**
 * @author iDay
 */
public class Session {

    private User currentUser;
    private DataConfig dataConfig;
    private City locationCity;
    private City myCity;
    private ArrayList<City> cities;
    private boolean newCart;
    private Subscription cartCountSubscription;

    private Session() {
    }

    public static Session getInstance() {
        return SessionHolder.INSTANCE;
    }

    public void init(Context context) {
        getMyCity(context);
        getCurrentUser(context);
        refreshCartItemsCount(context);
        ModuleUtils.setCityToModules(myCity);
    }

    /**
     * @return the user
     */
    public User getCurrentUser(Context context) {
        if (currentUser == null && context != null) {
            try {
                InputStream in = context.openFileInput(Constants.USER_FILE);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length;
                while ((length = in.read(buffer)) != -1) {
                    stream.write(buffer, 0, length);
                }

                currentUser = new User(new JSONObject(stream.toString()));
                ModuleUtils.setUserToModules(context, currentUser);
            } catch (FileNotFoundException e) {
                return null;
            } catch (IOException e) {
                return null;
            } catch (JSONException e) {
                return null;
            }
        }
        return currentUser;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(Context context, JSONObject user) {
        if (context == null) {
            return;
        }
        try {
            User u = new User(user);
            if (this.currentUser != null) {
                if (JSONUtil.isEmpty(u.getToken())) {
                    u.setToken(this.currentUser.getToken());
                    user.put("token", currentUser.getToken());
                }
                if (u.getId() == 0) {
                    u.setId(currentUser.getId());
                    user.put("id", currentUser.getId());
                }
            }
            this.currentUser = u;
            HljViewTracker.INSTANCE.setCurrentUserId(currentUser.getId());
            FileOutputStream fileOutputStream = context.openFileOutput(Constants.USER_FILE,
                    Context.MODE_PRIVATE);
            if (fileOutputStream != null) {
                OutputStreamWriter out = new OutputStreamWriter(fileOutputStream);
                out.write(user.toString());
                out.flush();
                out.close();
                fileOutputStream.close();
            }
            getCurrentUser(context);
            // 有没有更好的办法?
            ModuleUtils.setUserToModules(context, currentUser);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public void editCurrentUser(Context context, JSONObject user) {
        User u = Session.getInstance()
                .getCurrentUser(context);
        u.editUser(user);
        this.currentUser = u;
        ModuleUtils.setUserToModules(context, currentUser);
    }

    public void logout(Context context) {
        if (currentUser == null) {
            return;
        }
        clearLogout(context);
        //发送登出的rxevent
        RxBus.getDefault()
                .post(new RxEvent(RxEvent.RxEventType.LOGIN_OUT, null));
    }

    /**
     * 登录调用 目的是重置http requestheader 重置一些配置
     *
     * @param context
     */
    public void clearLogout(Context context) {
        if (currentUser == null) {
            return;
        }
        CookieSyncManager.createInstance(context);
        CookieSyncManager.getInstance()
                .startSync();
        CookieManager.getInstance()
                .removeAllCookie();
        if (context.getFileStreamPath(Constants.USER_FILE) != null && context.getFileStreamPath(
                Constants.USER_FILE)
                .exists()) {
            context.deleteFile(Constants.USER_FILE);
        }

        if (context.getFileStreamPath(Constants.WEIBO_FILE) != null && context.getFileStreamPath(
                Constants.WEIBO_FILE)
                .exists()) {
            context.deleteFile(Constants.WEIBO_FILE);
        }

        if (context.getFileStreamPath(Constants.QQ_FILE) != null && context.getFileStreamPath(
                Constants.QQ_FILE)
                .exists()) {
            context.deleteFile(Constants.QQ_FILE);
        }

        //猜你喜欢删除 今天和前一天的 配置
        Calendar currentTime = Calendar.getInstance();
        SharedPreferences sp = context.getSharedPreferences(Constants.PREF_FILE,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String time = format.format(currentTime.getTime());
        String todayKey = "guess_you_like_" + time;
        //删除前一天的数据
        currentTime.add(Calendar.DAY_OF_MONTH, -1);
        String beforeTodayKey = "guess_you_like_" + format.format(currentTime.getTime());
        if (sp.contains(beforeTodayKey)) {
            editor.remove(beforeTodayKey);
        }
        if (sp.contains(todayKey)) {
            editor.remove(todayKey);
        }
        editor.apply();
        //清除购物车数量
        clearCartCount(context);
        // 有没有更好的办法?
        ModuleUtils.logoutModules(context);

        HljKeFu.logout(context);

        this.currentUser = null;
        this.newCart = false;

        if (WebSocket.getInstance() != null) {
            WebSocket.getInstance()
                    .disconnect(context);
        }
    }

    public void setMyCity(Context context, City city) throws IOException {
        if (city == null) {
            return;
        }
        if (this.myCity != null && myCity.getId().equals(city.getId())) {
            return;
        }
        ModuleUtils.saveCommonCity(context, city);
        this.myCity = city;
        ModuleUtils.setCityToModules(city);
        //清除缓存
        HljHttp.clearCache(context);
        FileOutputStream fileOutputStream = context.openFileOutput(Constants.MY_CITIE_FILE,
                Context.MODE_PRIVATE);
        if (fileOutputStream != null) {
            OutputStreamWriter out = new OutputStreamWriter(fileOutputStream);
            out.write(JsonHelper.ToJsonString(city));
            out.flush();
            out.close();
            fileOutputStream.close();
        }
    }

    public City getMyCity(Context context) {
        if (myCity == null) {
            try {
                if (context.getFileStreamPath(Constants.MY_CITIE_FILE) != null && context
                        .getFileStreamPath(
                        Constants.MY_CITIE_FILE)
                        .exists()) {
                    InputStream in = context.openFileInput(Constants.MY_CITIE_FILE);
                    myCity = GsonUtil.getGsonInstance()
                            .fromJson(new InputStreamReader(in), City.class);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (myCity == null || myCity.getId() == 0) {
                myCity = new City(0, context.getString(R.string.all_city));
            }

            if (LocationSession.getInstance()
                    .getCity(context)
                    .getCid() == 0) {
                ModuleUtils.saveCommonCity(context, myCity);
            }
        }
        return myCity;
    }

    /**
     * 是否手动选择过城市
     *
     * @param context
     * @return
     */
    public boolean hasSetMyCity(Context context) {
        City tmpCity = null;
        try {
            if (context.getFileStreamPath(Constants.MY_CITIE_FILE) != null && context
                    .getFileStreamPath(
                    Constants.MY_CITIE_FILE)
                    .exists()) {
                InputStream in = context.openFileInput(Constants.MY_CITIE_FILE);
                tmpCity = GsonUtil.getGsonInstance()
                        .fromJson(new InputStreamReader(in), City.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (tmpCity == null) {
            return false;
        } else {
            return true;
        }
    }

    public void setLocationCity(Context context, City city) throws IOException {
        if (city == null) {
            return;
        }
        if (this.locationCity != null && city.getId()
                .equals(this.locationCity.getId())) {
            return;
        }
        this.locationCity = city;
        //清除缓存
        HljHttp.clearCache(context);
        FileOutputStream fileOutputStream = context.openFileOutput(Constants.LOCATION_CITY_FILE,
                Context.MODE_PRIVATE);
        if (fileOutputStream != null) {
            OutputStreamWriter out = new OutputStreamWriter(fileOutputStream);
            out.write(JsonHelper.ToJsonString(city));
            out.flush();
            out.close();
            fileOutputStream.close();
        }
    }

    public City getLocationCity(Context context) {
        if (locationCity == null) {
            try {
                if (context.getFileStreamPath(Constants.LOCATION_CITY_FILE) != null && context
                        .getFileStreamPath(
                        Constants.LOCATION_CITY_FILE)
                        .exists()) {
                    InputStream in = context.openFileInput(Constants.LOCATION_CITY_FILE);
                    locationCity = GsonUtil.getGsonInstance()
                            .fromJson(new InputStreamReader(in), City.class);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return locationCity;
    }

    public DataConfig getDataConfig(Context context) {
        if (dataConfig == null) {
            if (context.getFileStreamPath(Constants.DATA_CONFIG_FILE) != null && context
                    .getFileStreamPath(
                    Constants.DATA_CONFIG_FILE)
                    .exists()) {
                try {
                    InputStream in = context.openFileInput(Constants.DATA_CONFIG_FILE);
                    String jsonStr = JSONUtil.readStreamToString(in);
                    JSONObject object = new JSONObject(jsonStr);
                    dataConfig = new DataConfig(object);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return dataConfig;
    }

    public void setDataConfig(
            Context context, JSONObject jsonObject) throws IOException {
        if (jsonObject == null) {
            return;
        }
        dataConfig = new DataConfig(jsonObject);
        //设置子库中的DataConfig部分数据
        HljCard.setInvitationBankListUrl(dataConfig.getInvitationCardBankListUrl());
        HljCard.setCardFaqUrl(dataConfig.getEcardFaqUrl());
        HljCard.setEcardTutorialUrl(dataConfig.getEcardTutorialUrl());
        HljCard.setFund(dataConfig.isFund());
        FileOutputStream fileOutputStream = context.openFileOutput(Constants.DATA_CONFIG_FILE,
                Context.MODE_PRIVATE);
        if (fileOutputStream != null) {
            OutputStreamWriter out = new OutputStreamWriter(fileOutputStream);
            out.write(jsonObject.toString());
            out.flush();
            out.close();
            fileOutputStream.close();
        }

    }

    private static class SessionHolder {
        private static final Session INSTANCE = new Session();
    }

    //刷新购物车的数量
    public void refreshCartItemsCount(Context context) {
        if (currentUser == null) {
            return;
        }
        CommonUtil.unSubscribeSubs(cartCountSubscription);
        final Context appContext = context.getApplicationContext();
        cartCountSubscription = CustomCommonApi.getCartItemsCount()
                .subscribe(new Action1<JsonElement>() {
                    @Override
                    public void call(JsonElement jsonElement) {
                        int count = 0;
                        if (jsonElement.isJsonObject()) {
                            count = jsonElement.getAsJsonObject()
                                    .get("cart_list_count")
                                    .getAsInt();
                            appContext.getSharedPreferences(HljCommon.FileNames.PREF_FILE,
                                    Context.MODE_PRIVATE)
                                    .edit()
                                    .putInt(HljCommon.SharedPreferencesNames.CART_LIST_COUNT, count)
                                    .apply();
                        }
                        RxBus.getDefault()
                                .post(new RxEvent(RxEvent.RxEventType.REFRESH_CART_ITEM_COUNT,
                                        count));
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
    }

    public void setNewCart(boolean newCart) {
        if (currentUser == null || currentUser.getId() == 0) {
            return;
        }
        this.newCart = newCart;
    }

    public boolean isNewCart() {
        return !(currentUser == null || currentUser.getId() == 0) && newCart;
    }

    public int getCartCount(Context mContext) {
        return mContext.getSharedPreferences(HljCommon.FileNames.PREF_FILE, Context.MODE_PRIVATE)
                .getInt(HljCommon.SharedPreferencesNames.CART_LIST_COUNT, 0);
    }

    public void setCartCount(Context context, int count) {
        context.getSharedPreferences(HljCommon.FileNames.PREF_FILE, Context.MODE_PRIVATE)
                .edit()
                .putInt(HljCommon.SharedPreferencesNames.CART_LIST_COUNT, count)
                .apply();
    }

    public void clearCartCount(Context context) {
        setCartCount(context, 0);
    }

    public void saveAccessCities(Context context, ArrayList<City> cities) {
        if (!CommonUtil.isCollectionEmpty(cities)) {
            try {
                OutputStreamWriter out = new OutputStreamWriter(context.openFileOutput(Constants
                                .ACCESS_CITIES_FILE,
                        Context.MODE_PRIVATE));
                out.write(JsonHelper.ToJsonString(cities));
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            context.deleteFile(Constants.ACCESS_CITIES_FILE);
        }
    }

    public ArrayList<City> getAccessCities(Context context) {
        if (cities == null) {
            cities = new ArrayList<>();
            if (context.getFileStreamPath(Constants.ACCESS_CITIES_FILE) != null && context
                    .getFileStreamPath(
                    Constants.ACCESS_CITIES_FILE)
                    .exists()) {
                try {
                    InputStream in = context.openFileInput(Constants.ACCESS_CITIES_FILE);
                    String jsonStr = JSONUtil.readStreamToString(in);
                    if (!JSONUtil.isEmpty(jsonStr)) {
                        cities.addAll(JsonHelper.FromJson(jsonStr,
                                new TypeToken<ArrayList<City>>() {}.getType(),
                                City.class));
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return cities;
    }



    public String getAddress(Context context) {
        City city = Session.getInstance()
                .getMyCity(context);
        if (city != null && city.getId() > 0) {
            return city.getName();
        }
        Location location = LocationSession.getInstance()
                .getLocation(context);
        if (location != null && !TextUtils.isEmpty(location.getCity())) {
            return location.getCity();
        }
        return "";
    }

    public void unSubscriber() {
        CommonUtil.unSubscribeSubs(cartCountSubscription);
    }
}
