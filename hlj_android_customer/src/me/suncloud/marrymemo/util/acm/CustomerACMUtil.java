package me.suncloud.marrymemo.util.acm;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.hunliji.hljcommonlibrary.utils.EmptySubscriber;
import com.hunliji.hljhttplibrary.utils.FinancialSwitch;
import com.hunliji.hljcommonlibrary.utils.SPUtils;
import com.hunliji.hljcommonlibrary.view_tracker.HljTrackerParameter;
import com.hunliji.hljhttplibrary.utils.ACMConfigService;
import com.hunliji.hljhttplibrary.utils.GsonUtil;
import com.hunliji.hljsharelibrary.HljShare;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import me.suncloud.marrymemo.BuildConfig;
import me.suncloud.marrymemo.ad.MiaoZhenUtil;
import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;

/**
 * Created by wangtao on 2018/3/20.
 */

public class CustomerACMUtil {

    public static final String IS_SHOW_FINDER_FRAGMENT = "is_show_finder_fragment";


    public static class ACMConfig {
        //        public static final String DOMAIN="acm.aliyun.com";
        //        public static final String NAME_SPACE="3b1cfd2a-ac3a-462f-b886-720a3ffc1d56";
        //        public static final String ACCESS_KEY="37192405795c432fa7c448c47e96701c";
        //        public static final String SECRET_KEY="WOXjign3hssrKURNLPM8DryRxFI=";
        public static final String DOMAIN = "addr-hz-internal.edas.aliyun.com";
        public static final String NAME_SPACE = "a1f6712d-554c-4efe-8286-a508a6531f40";
        public static final String ACCESS_KEY = "fa7316ed7f144388a7ac7b7b58a48675";
        public static final String SECRET_KEY = "lzLorSqG1dwKGMUur2PVHWti1OM=";
        public static final String DATA_ID = BuildConfig.APPLICATION_ID;
    }

    public static class ACMGroup {
        public static final String TRACKER = "tracker_" + BuildConfig.VERSION_NAME;
        public static final String SHARE_POSTER = "AppSharePosterConfig";
        public static final String APP_CONFIG = "AppConfig_" + BuildConfig.VERSION_CODE;
        public static final String FINANCIAL_SWITCH = "FinancialSwitch_" + BuildConfig.VERSION_CODE;
    }

    public static Subscription synTrackerParameter(Context context) {
        return ACMConfigService.getInstance()
                .getServerConfigObb(context, CustomerACMUtil.ACMConfig.DATA_ID, ACMGroup.TRACKER)
                .map(new Func1<String, JsonObject>() {
                    @Override
                    public JsonObject call(String s) {
                        if (!TextUtils.isEmpty(s)) {
                            JsonElement jsonElement = new JsonParser().parse(s);
                            if (jsonElement.isJsonObject()) {
                                return jsonElement.getAsJsonObject();
                            }
                        }
                        return null;
                    }
                })
                .subscribe(new EmptySubscriber<JsonObject>() {
                    @Override
                    public void onNext(JsonObject jsonObject) {
                        HljTrackerParameter.INSTANCE.setTrackerConfig(jsonObject);
                    }
                });
    }


    public static Subscription initSharePosterConfig(final Context context) {
        return Observable.timer(5, TimeUnit.SECONDS)
                .concatMap(new Func1<Long, Observable<String>>() {
                    @Override
                    public Observable<String> call(Long aLong) {
                        return ACMConfigService.getInstance()
                                .getConfigAndSynObb(context,
                                        CustomerACMUtil.ACMConfig.DATA_ID,
                                        ACMGroup.SHARE_POSTER);
                    }
                })
                .map(new Func1<String, List<String>>() {
                    @Override
                    public List<String> call(String s) {
                        if (!TextUtils.isEmpty(s)) {
                            JsonElement jsonElement = new JsonParser().parse(s);
                            if (jsonElement.isJsonArray()) {
                                return GsonUtil.getGsonInstance()
                                        .fromJson(jsonElement,
                                                new TypeToken<List<String>>() {}.getType());
                            }
                        }
                        return null;
                    }
                })
                .subscribe(new EmptySubscriber<List<String>>() {
                    @Override
                    public void onNext(List<String> strings) {
                        HljShare.setSharePosterDisablePages(strings);
                        HljShare.setMiaoZhenUrl(MiaoZhenUtil.getImpUrl(context,
                                MiaoZhenUtil.PId.SHARE_POSTER),
                                MiaoZhenUtil.getClickUrl(context, MiaoZhenUtil.PId.SHARE_POSTER));
                    }
                });
    }

    public static Subscription synAppConfigParameter(final Context context) {
        return ACMConfigService.getInstance()
                .getServerConfigObb(context, CustomerACMUtil.ACMConfig.DATA_ID, ACMGroup.APP_CONFIG)
                .subscribe(new EmptySubscriber<String>() {
                    @Override
                    public void onNext(String s) {
                        setIsShowFinder(context, s);
                    }
                });
    }


    private static void setIsShowFinder(Context context, String acmConfig) {
        if (TextUtils.isEmpty(acmConfig)) {
            return;
        }
        Boolean isShowFinder = false;
        try {
            isShowFinder = GsonUtil.getGsonInstance()
                    .fromJson(acmConfig, JsonObject.class)
                    .get(IS_SHOW_FINDER_FRAGMENT)
                    .getAsBoolean();
        } catch (Exception e) {
            e.printStackTrace();
        }
        SPUtils.put(context, IS_SHOW_FINDER_FRAGMENT, isShowFinder);
    }


    public static Subscription initFinancialSwitch(final Context context) {
        if(FinancialSwitch.isOverTime()){
            return Observable.empty().subscribe();
        }
        return ACMConfigService.getInstance()
                .getConfigAndSynObb(context,
                        ACMConfig.DATA_ID,
                        ACMGroup.FINANCIAL_SWITCH)
                .map(new Func1<String, List<String>>() {
                    @Override
                    public List<String> call(String s) {
                        return GsonUtil.getGsonInstance().fromJson(s,new TypeToken<List<String>>(){}.getType());
                    }
                })
                .subscribe(new EmptySubscriber<List<String>>() {
                    @Override
                    public void onNext(List<String> channels) {
                        FinancialSwitch.INSTANCE.setClosedChannels(channels);
                    }
                });

    }
}
