package com.hunliji.cardmaster;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.hunliji.cardmaster.activities.MainActivity;
import com.hunliji.cardmaster.activities.debug.ChangeHostActivity;
import com.hunliji.cardmaster.models.realm.CardMasterRealmMigration;
import com.hunliji.cardmaster.utils.NotificationUtil;
import com.hunliji.hljcardlibrary.HljCard;
import com.hunliji.hljcommonlibrary.HljApplicationInterface;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljcommonlibrary.models.realm.CommonRealmModule;
import com.hunliji.hljcommonlibrary.utils.ChannelUtil;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.SPUtils;
import com.hunliji.hljcommonlibrary.view_tracker.HljViewTracker;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.authorization.CustomerUserConverter;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.hljkefulibrary.HljKeFu;
import com.hunliji.hljkefulibrary.moudles.EMChat;
import com.hunliji.hljkefulibrary.moudles.Support;
import com.hunliji.hljkefulibrary.utils.SupportUtil;
import com.hunliji.hljsharelibrary.HljShare;
import com.hunliji.hljtrackerlibrary.TrackerCollection;
import com.hyphenate.chat.Message;
import com.tencent.smtt.sdk.QbSdk;
import com.tendcloud.tenddata.TCAgent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by wangtao on 2017/11/22.
 */

public class HLJCardMasterApplication extends MultiDexApplication implements
        HljApplicationInterface, HljKeFu.KeFuMessageBackgroundCallback {

    @Override
    public void onCreate() {
        super.onCreate();
        if (appIsRuning()) {
            initRealmConfiguration();
            // 初始化host配置和子模块配置,必须先配置好host再子模块
            initHosts();
            initModules();
            initTracker();
            initARouter();
            initX5WebView();

            registerActivityLifecycleCallbacks(HljCardMasterLiveCycleImpl.getInstance());
        }
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    /**
     * 初始化host配置
     */
    public void initHosts() {
        if (Constants.DEBUG) {
            // 从配置文件中提取 host 值
            ChangeHostActivity.HOSTS defaultHosts = ChangeHostActivity.HOSTS.测试;
            String host = SPUtils.getString(this, "HOST", defaultHosts.getHljHost());
            Constants.setHOST(host);
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

        Map<String, String> keys = new HashMap<>();
        keys.put("QQKEY", Constants.QQKEY);

        keys.put("WEIBOKEY", Constants.WEIBOKEY);
        keys.put("WEIBO_CALLBACK", Constants.WEIBO_CALLBACK);

        keys.put("WEIXINKEY", Constants.WEIXINKEY);
        keys.put("WEIXINSECRET", Constants.WEIXINSECRET);
        HljShare.initShareKey(keys);

        HljCard.setCardHost(Constants.HOST);
        //客服
        HljKeFu.init(this, Constants.DEBUG, this);
    }

    private void initTracker() {
        //talkingdata
        String channel = ChannelUtil.getChannel(this);
        TCAgent.LOG_ON = false;
        TCAgent.init(this, "7A8E7676F64A4876931B91102E36D876", channel);
        TCAgent.setReportUncaughtExceptions(true);

        // 视图曝光统计模块初始化
        TrackerCollection.INSTANCE.registerSubscription();
        HljViewTracker.INSTANCE.initHljViewTracker();
        User user = UserSession.getInstance()
                .getUser(getApplicationContext());
        if (user != null) {
            HljViewTracker.INSTANCE.setCurrentUserId(user.getId());
        } else {
            HljViewTracker.INSTANCE.setCurrentUserId(-1);
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Constants.DEBUG) {
            WebView.setWebContentsDebuggingEnabled(true);
            com.tencent.smtt.sdk.WebView.setWebContentsDebuggingEnabled(true);
        }
    }

    private void initRealmConfiguration() {
        Realm.init(this);
        RealmConfiguration.Builder builder = new RealmConfiguration.Builder().modules(Realm
                        .getDefaultModule(),
                new CommonRealmModule())
                .schemaVersion(CardMasterRealmMigration.REALM_VERSION);
        try {
            Realm.setDefaultConfiguration(builder.migration(new CardMasterRealmMigration())
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
    public Activity getCurrentActivity() {
        return HljCardMasterLiveCycleImpl.getInstance()
                .getCurrentActivity();
    }

    @Override
    public String getActivityHistory() {
        return HljCardMasterLiveCycleImpl.getInstance()
                .getActivityHistoryString();
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
                            case EMChat.VOICE:
                                body = getString(R.string.label_hx_msg_type3);
                                break;
                            default:
                                if (!TextUtils.isEmpty(chat.getContent())) {
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
                .setSmallIcon(R.mipmap.icon_app_icon_48_48)
                .setTicker(ticker)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(content)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setVisibility(Notification.VISIBILITY_PUBLIC);
        }
        notification = builder.build();
        notification.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE |
                Notification.DEFAULT_LIGHTS;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        manager.notify(4, notification);

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

    public static void logout(Context context) {
        UserSession.getInstance()
                .logout(context);
        NotificationUtil.INSTANCE.logout();
        HljViewTracker.INSTANCE.setCurrentUserId(-1);
        HljKeFu.logout(context);
    }
}
