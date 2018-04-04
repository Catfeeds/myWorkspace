package com.hunliji.marrybiz;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import android.webkit.WebView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.hunliji.hljchatlibrary.WebSocket;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.realm.CommonRealmModule;
import com.hunliji.hljcommonlibrary.models.realm.WSChat;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljViewTracker;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.authorization.MerchantUserConverter;
import com.hunliji.hljkefulibrary.HljKeFu;
import com.hunliji.hljkefulibrary.moudles.EMChat;
import com.hunliji.hljkefulibrary.utils.SupportUtil;
import com.hunliji.hljlivelibrary.HljLive;
import com.hunliji.hljlivelibrary.api.apiimpl.MerchantLiveApiImpl;
import com.hunliji.hljsharelibrary.HljShare;
import com.hunliji.hljtrackerlibrary.TrackerCollection;
import com.hunliji.marrybiz.model.realm.MerchantRealmMigration;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.view.HomeActivity;
import com.hunliji.marrybiz.view.debug.ChangeHostActivity;
import com.hyphenate.chat.Message;
import com.tendcloud.tenddata.TCAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;

/**
 * Created by luohanlin on 15/7/6.
 */
public class HLJMerchantApplication extends MultiDexApplication implements Application
        .ActivityLifecycleCallbacks, HljKeFu.KeFuMessageBackgroundCallback {
    private ArrayList<Activity> activities;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        activities = new ArrayList<>();
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        if (Constants.DEBUG) {
            setupHosts();
        }
        if (appIsRuning()) {
            initModules();
            initARouter();

            registerActivityLifecycleCallbacks(this);
            CrashHandler crashHandler = CrashHandler.getInstance();
            crashHandler.init(getApplicationContext());
            setTCAgent();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Constants.DEBUG) {
                WebView.setWebContentsDebuggingEnabled(true);
            }
            initRealmConfiguration();
        }
        super.onCreate();
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
                new MerchantUserConverter());
        Map<String, String> keys = new HashMap<>();
        keys.put("QQKEY", Constants.QQKEY);

        keys.put("WEIBOKEY", Constants.WEIBOKEY);
        keys.put("WEIBO_CALLBACK", Constants.WEIBO_CALLBACK);

        keys.put("WEIXINKEY", Constants.WEIXINKEY);
        HljShare.initShareKey(keys);

        HljLive.init(Constants.DEBUG, new MerchantLiveApiImpl());
        //客服
        HljKeFu.init(this, Constants.DEBUG, this);


        // 视图曝光统计模块初始化
        TrackerCollection.INSTANCE.registerSubscription();
        HljViewTracker.INSTANCE.initHljViewTracker();
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

    private void initRealmConfiguration() {
        Realm.init(this);
        RealmConfiguration.Builder builder = new RealmConfiguration.Builder().modules(Realm
                        .getDefaultModule(),
                new CommonRealmModule())
                .schemaVersion(MerchantRealmMigration.REALM_VERSION);
        try {
            Realm.setDefaultConfiguration(builder.migration(new MerchantRealmMigration())
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

    public void setTCAgent() {
        TCAgent.LOG_ON = false;
        TCAgent.init(this, "99DA79C0F13AA38535B4868A114C206E", "hunliji");
        TCAgent.setReportUncaughtExceptions(true);
    }

    public void setupHosts() {
        if (Constants.DEBUG) {
            // 从配置文件中提取 host 值
            SharedPreferences preferences = getSharedPreferences(Constants.PREF_FILE, Context
                    .MODE_PRIVATE);
            ChangeHostActivity.HOSTS defaultHosts = ChangeHostActivity.HOSTS.测试;
            String host = preferences.getString("HOST", defaultHosts.getHljHost());
            String webSocketHost = preferences.getString("WEB_SOCKET_HOST", defaultHosts.getSocketHost());
            String liveHost = preferences.getString("LIVE_HOST", defaultHosts.getLiveHost());
            String webHost = preferences.getString("WEB_HOST", defaultHosts.getWebHost());
            Constants.setHOST(host);
            Constants.setWebHost(webHost);
            WebSocket.setSocketHost(webSocketHost);
            if (!TextUtils.isEmpty(liveHost)) {
                HljLive.setLiveHost(liveHost);
            }
        }
    }

    public boolean isAppOnForeground() {
        ActivityManager activityManager = (ActivityManager) getApplicationContext()
                .getSystemService(
                Context.ACTIVITY_SERVICE);
        String packageName = getApplicationContext().getPackageName();

        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null)
            return false;

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName) && appProcess.importance ==
                    ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (activities == null) {
            activities = new ArrayList<>();
        }
        activities.add(activity);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        if (activities != null && !activities.isEmpty()) {
            activities.remove(activity);
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {
        if (!isAppOnForeground()) {
            TrackerCollection.INSTANCE.sendTracker(true);
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    public Activity getCurrentActivity() {
        if (activities != null && !activities.isEmpty()) {
            return activities.get(activities.size() - 1);
        }
        return null;
    }


    private boolean appIsRuning() {
        int pid = android.os.Process.myPid();
        String packageName = getApplicationContext().getPackageName();
        ActivityManager am = (ActivityManager) getApplicationContext().getSystemService(Context
                .ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        for (Object aL : l) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo)
                    (aL);
            try {
                if (info.pid == pid) {
                    return info.processName.equals(packageName);
                }
            } catch (Exception e) {
                // Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return false;
    }

    private void sendNotification(String content, String ticker) {
        NotificationManager manager = (NotificationManager) getSystemService(Context
                .NOTIFICATION_SERVICE);
        Intent i = new Intent(this, HomeActivity.class);
        i.putExtra("index", 4);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0,
                i,
                PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification;
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.icon_app_icon_96_96)
                .setTicker(ticker)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(content);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notification = builder.build();
        } else {
            notification = builder.getNotification();
        }
        notification.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        manager.notify(4, notification);

    }

    @Override
    public void onMessageNotifier(final Message message) {
        SupportUtil.getInstance(getApplicationContext())
                .getSupports(getApplicationContext(), new SupportUtil.SimpleSupportCallback() {
                    @Override
                    public void onSupportsCompleted(
                            List<com.hunliji.hljkefulibrary.moudles.Support> supports) {
                        super.onSupportsCompleted(supports);
                        String supportNick = message.getUserName();
                        if (!CommonUtil.isCollectionEmpty(supports)) {
                            for (com.hunliji.hljkefulibrary.moudles.Support support : supports) {
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
}
