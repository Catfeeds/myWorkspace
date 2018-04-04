package com.hunliji.hljhttplibrary.utils;

import android.content.Context;

import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljhttplibrary.api.config.ConfigApi;

import java.io.File;
import java.io.OutputStreamWriter;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by wangtao on 2018/2/24.
 * Application Configuration manager service
 * 应用全局配置
 */

public class ACMConfigService {

    private static ACMConfigService INSTANCE;

    public static synchronized ACMConfigService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ACMConfigService();
        }
        return INSTANCE;
    }

    public String getFileConfig(
            final Context context, final String dataId, final String group) {
        try {
            File file = context.getFileStreamPath(String.format(HljCommon.FileNames.CONFIG_FILE,
                    dataId,
                    group));
            if (file != null && file.exists()) {
                return CommonUtil.readStreamToString(context.openFileInput(String.format
                        (HljCommon.FileNames.CONFIG_FILE,
                        dataId,
                        group)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Observable<String> getServerConfigObb(
            final Context context, final String dataId, final String group) {
        return ConfigApi.getConfig(dataId, group)
                .doOnNext(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        try {
                            OutputStreamWriter out = new OutputStreamWriter(context.openFileOutput(
                                    String.format(HljCommon.FileNames.CONFIG_FILE, dataId, group),
                                    Context.MODE_PRIVATE));
                            out.write(s);
                            out.flush();
                            out.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    /**
     * 获取ACM配置
     *
     * @param context
     * @param dataId
     * @param group
     * @return
     */
    public Observable<String> getConfigAndSynObb(
            final Context context, final String dataId, final String group) {
        return Observable.concatDelayError(Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext(getFileConfig(context, dataId, group));
                subscriber.onCompleted();
            }
        }), getServerConfigObb(context, dataId, group))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
