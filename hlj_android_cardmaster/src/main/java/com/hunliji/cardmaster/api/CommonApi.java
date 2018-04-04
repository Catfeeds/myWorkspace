package com.hunliji.cardmaster.api;

import android.content.Context;

import com.google.gson.JsonObject;
import com.hunliji.cardmaster.Constants;
import com.hunliji.cardmaster.models.DataConfig;
import com.hunliji.cardmaster.utils.DataConfigUtil;
import com.hunliji.hljcommonlibrary.models.customer.CustomerUser;
import com.hunliji.hljcommonlibrary.models.realm.Notification;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.utils.GsonUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by jinxin on 2017/11/27 0027.
 */

public class CommonApi {


    /**
     * 意见反馈
     *
     * @param content 问题描述
     * @param phone   联系电话
     * @return
     */
    public static Observable<HljHttpResult> postFeedback(String content, String phone) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("content", content);
        jsonObject.addProperty("contact", phone);
        jsonObject.addProperty("device", android.os.Build.MODEL);
        jsonObject.addProperty("system", android.os.Build.VERSION.RELEASE);
        return HljHttp.getRetrofit()
                .create(CommonService.class)
                .postFeedback(jsonObject)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 修改用户昵称
     *
     * @param nick
     * @return
     */
    public static Observable<HljHttpResult<CustomerUser>> modifyUserNick(String nick) {
        Map<String, Object> params = new HashMap<>();
        params.put("nick", nick);
        return HljHttp.getRetrofit()
                .create(CommonService.class)
                .modifyUserInformation(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 修改用户信息
     *
     * @param params
     * @return
     */
    public static Observable<HljHttpResult<CustomerUser>> modifyUserInformation(
            Map<String, Object> params) {
        return HljHttp.getRetrofit()
                .create(CommonService.class)
                .modifyUserInformation(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 获取通知 Observable
     */
    public static Observable<List<Notification>> getNotifications(long lastId) {
        return HljHttp.getRetrofit()
                .create(CommonService.class)
                .getNotifications(lastId)
                .map(new HljHttpResultFunc<List<Notification>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }


    public static Observable<DataConfig> getDataConfig(Context context) {
        final WeakReference<Context> contextWeakReference = new WeakReference<>(context);
        return HljHttp.getRetrofit()
                .create(CommonService.class)
                .getDataConfig()
                .map(new Func1<DataConfigUtil.DataConfigData, DataConfig>() {
                    @Override
                    public DataConfig call(DataConfigUtil.DataConfigData dataConfigData) {
                        DataConfig dataConfig = dataConfigData.getDataConfig();
                        if (dataConfig != null && contextWeakReference.get() != null) {
                            try {
                                OutputStreamWriter out = new OutputStreamWriter
                                        (contextWeakReference.get()
                                        .openFileOutput(Constants.DATA_CONFIG_FILE,
                                                Context.MODE_PRIVATE));
                                out.write(GsonUtil.getGsonInstance().toJson(dataConfig));
                                out.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        return dataConfig;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }
}
