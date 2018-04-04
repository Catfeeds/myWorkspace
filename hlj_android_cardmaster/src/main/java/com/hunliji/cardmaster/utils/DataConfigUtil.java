package com.hunliji.cardmaster.utils;

import android.content.Context;

import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.hunliji.cardmaster.Constants;
import com.hunliji.cardmaster.api.CommonApi;
import com.hunliji.cardmaster.models.DataConfig;
import com.hunliji.hljcardlibrary.HljCard;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljhttplibrary.utils.GsonUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;

import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by wangtao on 2017/11/30.
 */

public enum DataConfigUtil {
    INSTANCE;

    DataConfig dataConfig;

    private Subscription dataConfigSubscription;
    private WeakReference<Context> contextWeakReference;


    public DataConfig getDataConfig(Context context) {
        if (dataConfig == null) {
            if (context.getFileStreamPath(Constants.DATA_CONFIG_FILE) != null && context
                    .getFileStreamPath(
                    Constants.DATA_CONFIG_FILE)
                    .exists()) {
                try {
                    InputStream in = context.openFileInput(Constants.DATA_CONFIG_FILE);
                    dataConfig = GsonUtil.getGsonInstance()
                            .fromJson(new JsonReader(new InputStreamReader(in)), DataConfig.class);
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return dataConfig;
    }

    public void executeDataConfig(final Context context) {
        if (!CommonUtil.isUnsubscribed(dataConfigSubscription)) {
            return;
        }
        contextWeakReference = new WeakReference<>(context);
        dataConfigSubscription = CommonApi.getDataConfig(context)
                .onErrorReturn(new Func1<Throwable, DataConfig>() {
                    @Override
                    public DataConfig call(Throwable throwable) {
                        throwable.printStackTrace();
                        if (contextWeakReference != null && contextWeakReference.get() != null) {
                            return getDataConfig(contextWeakReference.get());
                        }
                        return dataConfig;
                    }
                })
                .subscribe(new Action1<DataConfig>() {
                    @Override
                    public void call(DataConfig dataConfig) {
                        if (dataConfig == null) {
                            return;
                        }
                        DataConfigUtil.this.dataConfig = dataConfig;
                        HljCard.setInvitationBankListUrl(dataConfig.getInvitationCardBankListUrl());
                        HljCard.setCardFaqUrl(dataConfig.getEcardFaqUrl());
                        HljCard.setEcardTutorialUrl(dataConfig.getEcardTutorialUrl());
                        HljCard.setFund(dataConfig.isFund());
                    }
                });
    }

    public class DataConfigData {
        @SerializedName("config")
        DataConfig dataConfig;

        public DataConfig getDataConfig() {
            return dataConfig;
        }
    }
}
