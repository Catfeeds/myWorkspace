package com.hunliji.hljkefulibrary.api;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.utils.GsonUtil;
import com.hunliji.hljkefulibrary.HljKeFu;
import com.hunliji.hljkefulibrary.moudles.HxUser;
import com.hunliji.hljkefulibrary.moudles.Support;
import com.hunliji.hljkefulibrary.moudles.SupportListData;
import com.hunliji.hljkefulibrary.utils.KeFuSession;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by wangtao on 2017/10/20.
 */

public class KeFuApi {

    public static Observable<HxUser> getHxUserObb(final Context context) {
        return HljHttp.getRetrofit()
                .create(KeFuService.class)
                .getHxUser()
                .map(new Func1<JsonObject, HxUser>() {
                    @Override
                    public HxUser call(JsonObject jsonObject) {
                        JsonObject hxUserJson = jsonObject.getAsJsonObject("hx_user");
                        HxUser user = GsonUtil.getGsonInstance()
                                .fromJson(hxUserJson, HxUser.class);
                        if (!TextUtils.isEmpty(user.getUserName()) && !TextUtils.isEmpty(user
                                .getPassWord())) {
                            try {
                                KeFuSession.getInstance()
                                        .saveUser(context, user, hxUserJson.toString());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return user;
                        }
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    public static Observable<JSONObject> getRobotGreeting() {
        return HljHttp.getRetrofit()
                .create(KeFuService.class)
                .getGreeting(HljKeFu.TENANT_ID)
                .map(new Func1<JsonObject, JSONObject>() {
                    @Override
                    public JSONObject call(JsonObject jsonObject) {
                        try {
                            int type = jsonObject.get("greetingTextType")
                                    .getAsInt();
                            if (type == 1) {
                                final String greetingRobots = jsonObject.get("greetingText")
                                        .getAsString()
                                        .replaceAll("&quot;", "\"");
                                JSONObject json = new JSONObject(greetingRobots);
                                return json.optJSONObject("ext")
                                        .optJSONObject("msgtype");
                            }
                        } catch (Exception ignored) {
                        }
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<List<Support>> getSupports(final Context context) {
        return HljHttp.getRetrofit()
                .create(KeFuService.class)
                .getSupports()
                .map(new Func1<JsonObject, List<Support>>() {
                    @Override
                    public List<Support> call(JsonObject jsonObject) {
                        JsonArray array = jsonObject.getAsJsonArray("supports");
                        if (array != null && array.size() > 0) {
                            try {
                                FileOutputStream fileOutputStream = context.openFileOutput
                                        (HljCommon.FileNames.SUPPORTS_FILE,
                                        Context.MODE_PRIVATE);
                                if (fileOutputStream != null) {
                                    OutputStreamWriter out = new OutputStreamWriter
                                            (fileOutputStream);
                                    out.write(array.toString());
                                    out.flush();
                                    out.close();
                                    fileOutputStream.close();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return GsonUtil.getGsonInstance()
                                    .fromJson(array, new TypeToken<List<Support>>() {}.getType());
                        }
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }


    public static Observable<JsonElement> postReply(
            String fromUser, String kind, String ext, String message) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("ext", ext);
        jsonObject.addProperty("from_user", fromUser);
        jsonObject.addProperty("kind", kind);
        jsonObject.addProperty("message", message);
        return HljHttp.getRetrofit()
                .create(KeFuService.class)
                .postReply(jsonObject)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
