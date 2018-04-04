/**
 *
 */
package me.suncloud.marrymemo;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.hunliji.hljcardlibrary.HljCard;
import com.hunliji.hljchatlibrary.WebSocket;
import com.hunliji.hljcommonlibrary.HljApplicationInterface;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.realm.CommonRealmModule;
import com.hunliji.hljcommonlibrary.utils.ChannelUtil;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljTrackerParameter;
import com.hunliji.hljcommonlibrary.view_tracker.HljViewTracker;
import com.hunliji.hljdebuglibrary.HljDebuger;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.authorization.CustomerUserConverter;
import com.hunliji.hljhttplibrary.utils.ACMConfigService;
import com.hunliji.hljkefulibrary.HljKeFu;
import com.hunliji.hljkefulibrary.moudles.EMChat;
import com.hunliji.hljkefulibrary.moudles.Support;
import com.hunliji.hljkefulibrary.utils.SupportUtil;
import com.hunliji.hljlivelibrary.HljLive;
import com.hunliji.hljlivelibrary.api.apiimpl.CustomerLiveApiImpl;
import com.hunliji.hljpushlibrary.HljPush;
import com.hunliji.hljsharelibrary.HljShare;
import com.hunliji.hljtrackerlibrary.TrackerCollection;
import com.hyphenate.chat.Message;
import com.tencent.smtt.sdk.QbSdk;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.AnalyticsConfig;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import me.suncloud.marrymemo.model.realm.CustomerRealmMigration;
import me.suncloud.marrymemo.util.CacheUtil;
import me.suncloud.marrymemo.util.GoogleAnalyticsUtil;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.acm.CustomerACMUtil;
import me.suncloud.marrymemo.view.MainActivity;
import me.suncloud.marrymemo.view.debug.ChangeHostActivity;

public class HLJCustomerApplication extends MultiDexApplication implements
        HljApplicationInterface, HljKeFu.KeFuMessageBackgroundCallback {

    @Override
    public void onCreate() {
        super.onCreate();
        if (appIsRuning()) {
            CrashHandler crashHandler = CrashHandler.getInstance();
            crashHandler.init(getApplicationContext());

            initRealmConfiguration();
            // 初始化host配置和子模块配置,必须先配置好host再子模块
            initHosts();
            initModules();
            initTracker();
            initARouter();
            initX5WebView();

            CacheUtil.getInstance(getApplicationContext());
            registerActivityLifecycleCallbacks(HljActivityLifeCycleImpl.getInstance());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Constants.DEBUG) {
                WebView.setWebContentsDebuggingEnabled(true);
            }
        }
    }

    /**
     * 初始化host配置
     */
    public void initHosts() {
        if (Constants.DEBUG) {
            // 从配置文件中提取 host 值
            SharedPreferences preferences = getSharedPreferences(Constants.PREF_FILE,
                    Context.MODE_PRIVATE);
            ChangeHostActivity.HOSTS defaultHosts= ChangeHostActivity.HOSTS.测试;
            String host = preferences.getString("HOST", defaultHosts.getHljHost());
            String socketHost = preferences.getString("SOCKET_HOST", defaultHosts.getSocketHost());
            String httpsHost = preferences.getString("HTTPS_HOST", defaultHosts.getHttpsHost());
            String liveHost = preferences.getString("LIVE_HOST", defaultHosts.getLiveHost());
            String pushHost = preferences.getString("PUSH_HOST", defaultHosts.getPushHost());


            Constants.setHOST(host);
            Constants.setHttpsHost(httpsHost);
            WebSocket.setSocketHost(socketHost);
            HljPush.setPushHost(pushHost);
            if (!TextUtils.isEmpty(liveHost)) {
                HljLive.setLiveHost(liveHost);
            }
        }
    }

    /**
     * 初始化通用模块和功能模块配置
     * 主工程用到的所有的子模块都需要在这里进行初始化配置
     */
    public void initModules() {
        // Common和Http两个是最基础的模块,直接初始化
        HljCommon.setDebug(Constants.DEBUG);
        HljHttp.init(this.getApplicationContext(),
                Constants.DEBUG,
                Constants.HOST,
                new CustomerUserConverter());
        // 直播模块的初始化
        HljLive.init(Constants.DEBUG, new CustomerLiveApiImpl());

        HljCard.setCardHost(Constants.HOST);
        //客服
        HljKeFu.init(this, Constants.DEBUG, this);

        HljDebuger.init(Constants.DEBUG);

        HljShare.openSharePoster();
    }

    /**
     * 第三方统计初始化
     */
    public void initTracker() {

        GoogleAnalyticsUtil.getInstance(this);

        String channel = ChannelUtil.getChannel(this);
        TCAgent.LOG_ON = false;
        AnalyticsConfig.setChannel(channel);
        TCAgent.init(this, "BAEB8777A7CF7E06DBF107746666027A", channel);

        // 视图曝光统计模块初始化
        TrackerCollection.INSTANCE.registerSubscription();
        HljViewTracker.INSTANCE.initHljViewTracker();

        initTrackerParameter();
    }

    private void initTrackerParameter() {
        JsonElement trackerConfigJson = null;
        try {
            String trackerConfigStr = ACMConfigService.getInstance()
                    .getFileConfig(this,
                            CustomerACMUtil.ACMConfig.DATA_ID,
                            CustomerACMUtil.ACMGroup.TRACKER);
            if (TextUtils.isEmpty(trackerConfigStr)) {
                InputStream in = getResources().openRawResource(R.raw.hlj_tracker);
                trackerConfigJson = new JsonParser().parse(new InputStreamReader(in));
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                trackerConfigJson = new JsonParser().parse(trackerConfigStr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (trackerConfigJson != null && trackerConfigJson.isJsonObject()) {
            HljTrackerParameter.INSTANCE.setTrackerConfig(trackerConfigJson.getAsJsonObject());
        }
    }

    /**
     * 初始化路由组件
     */
    private void initARouter() {
        if (Constants.DEBUG) {
            ARouter.openLog();
            ARouter.openDebug();
        }
        ARouter.init(this);
    }

    /**
     * x5內核初始化
     */
    private void initX5WebView() {
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {

            @Override
            public void onViewInitFinished(boolean arg0) {
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Log.d("app", " onViewInitFinished is " + arg0);
            }

            @Override
            public void onCoreInitFinished() {
            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(), cb);
    }

    private void initRealmConfiguration() {
        Realm.init(this);
        RealmConfiguration.Builder builder = new RealmConfiguration.Builder().modules(Realm
                        .getDefaultModule(),
                new CommonRealmModule())
                .schemaVersion(CustomerRealmMigration.REALM_VERSION);
        try {
            Realm.setDefaultConfiguration(builder.migration(new CustomerRealmMigration())
                    .build());
            Realm.getDefaultInstance()
                    .close();
        } catch (Exception e) {
            e.printStackTrace();
            if (!Constants.DEBUG) {
                Realm.setDefaultConfiguration(builder.deleteRealmIfMigrationNeeded()
                        .build());
            }
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private boolean appIsRuning() {
        try {
            int pid = android.os.Process.myPid();
            String packageName = getApplicationContext().getPackageName();
            ActivityManager activityManager = (ActivityManager) getApplicationContext()
                    .getSystemService(
                    Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                    .getRunningAppProcesses();
            if (appProcesses == null)
                return false;
            for (ActivityManager.RunningAppProcessInfo info : appProcesses) {
                if (info.pid != pid) {
                    continue;
                }
                return info.processName.equals(packageName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onMessageNotifier(final Message message) {
        SupportUtil.getInstance(getApplicationContext())
                .getSupports(getApplicationContext(), new SupportUtil.SimpleSupportCallback() {
                    @Override
                    public void onSupportsCompleted(List<Support> supports) {
                        super.onSupportsCompleted(supports);
                        String supportNick = message.getUserName();
                        if (!CommonUtil.isCollectionEmpty(supports)) {
                            for (Support support : supports) {
                                if (support.getHxIm()
                                        .equals(message.getUserName())) {
                                    supportNick = support.getNick();
                                    break;
                                }
                            }
                        }
                        String body = "";
                        EMChat chat = new EMChat(message);
                        switch (chat.getType()) {
                            case EMChat.IMAGE:
                                body = getString(R.string.label_hx_msg_type1);
                                break;
                            case EMChat.MERCHANT:
                                body = getString(R.string.label_hx_msg_type2);
                                break;
                            case EMChat.VOICE:
                                body = getString(R.string.label_hx_msg_type3);
                                break;
                            default:
                                if (!JSONUtil.isEmpty(chat.getContent())) {
                                    body = chat.getContent();
                                }
                                break;
                        }
                        String ticker = getString(R.string.label_hx_msg_ticker, supportNick, body);
                        sendNotification(getString(R.string.label_hx_msg_content), ticker);
                    }
                });
    }

    private void sendNotification(String content, String ticker) {
        NotificationManager manager = (NotificationManager) getSystemService(Context
                .NOTIFICATION_SERVICE);
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("index", 4);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0,
                i,
                PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this).setContentIntent(
                pendingIntent)
                .setSmallIcon(R.drawable.icon_app_icon_48_48)
                .setTicker(ticker)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(content)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setVisibility(Notification.VISIBILITY_PUBLIC);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notification = builder.build();
        } else {
            notification = builder.getNotification();
        }
        notification.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE |
                Notification.DEFAULT_LIGHTS;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        manager.notify(4, notification);

    }

    @Override
    public Activity getCurrentActivity() {
        return HljActivityLifeCycleImpl.getInstance()
                .getCurrentActivity();
    }

    @Override
    public String getActivityHistory() {
        return HljActivityLifeCycleImpl.getInstance()
                .getActivityHistoryString();
    }
}
