package com.hunliji.posclient;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.alibaba.android.arouter.launcher.ARouter;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.realm.CommonRealmModule;
import com.hunliji.hljcommonlibrary.utils.SPUtils;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.authorization.CustomerUserConverter;
import com.hunliji.posclient.models.relam.PosModule;
import com.hunliji.posclient.models.relam.PosRealmMigration;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by chen_bin on 2018/1/17 0017.
 */
public class HLJPosClientApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        if (appIsRunning()) {
            initRealmConfiguration();
            initHosts();
            initModules();
            initARouter();
        }
    }

    private boolean appIsRunning() {
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

    private void initRealmConfiguration() {
        Realm.init(this);
        RealmConfiguration.Builder builder = new RealmConfiguration.Builder().modules(new
                PosModule(),
                new CommonRealmModule())
                .schemaVersion(PosRealmMigration.REALM_VERSION);
        Realm.setDefaultConfiguration(builder.migration(new PosRealmMigration())
                .build());
    }

    /**
     * 初始化host配置
     */
    public void initHosts() {
        if (Constants.DEBUG) {
            // 从配置文件中提取 host 值
            Constants.setHOST(SPUtils.getString(this,"HOST",Constants.HOST));
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

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}
