package me.suncloud.marrymemo.view;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.suncloud.hljweblibrary.utils.JsUtil;
import com.google.gson.JsonElement;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.EmptySubscriber;
import com.hunliji.hljcommonlibrary.utils.SystemNotificationUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljhttplibrary.utils.NetUtil;
import com.hunliji.hljkefulibrary.utils.SupportUtil;
import com.hunliji.hljtrackerlibrary.HljTracker;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.CustomCommonApi;
import me.suncloud.marrymemo.fragment.ad.LaunchAdFragment;
import me.suncloud.marrymemo.model.Notify;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.service.GetuiIntentService;
import me.suncloud.marrymemo.util.DataConfig;
import me.suncloud.marrymemo.util.DataConfigUtil;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.PropertiesUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.acm.CustomerACMUtil;
import me.suncloud.marrymemo.view.login.LoginActivity;
import me.suncloud.marrymemo.view.login.WeddingDateSetActivity;
import rx.internal.util.SubscriptionList;

public class SplashActivity extends HljBaseNoBarActivity implements LaunchAdFragment
        .LaunchAdFragmentInterface {

    private SubscriptionList configSubscriptions;

    @Override
    public String pageTrackTagName() {
        return "启动页";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initAppConfig();
        loginOut();
        initTracker();

        User user = Session.getInstance()
                .getCurrentUser(this);
        if (user != null) {
            Notify lastNotify = GetuiIntentService.lastNotify;
            if (lastNotify != null && lastNotify.getLastMsg() != null) {
                try {
                    GetuiIntentService.lastNotify = null;
                    NotificationManager manager = (NotificationManager) getSystemService(Context
                            .NOTIFICATION_SERVICE);

                    manager.cancel(lastNotify.getId());
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra(SystemNotificationUtil.ASG_MSG,
                            lastNotify.getLastMsg()
                                    .toString());
                    //                    intent.putExtra(SystemNotificationUtil.ASG_TASK_ID,
                    // lastNotify.getTaskid());
                    //                    intent.putExtra(SystemNotificationUtil.ASG_MESSAGE_ID,
                    // lastNotify.getMessageid());
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.splash_fade_in, R.anim.splash_fade_out);
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        DataConfig dataConfig = Session.getInstance()
                .getDataConfig(this);
        setContentView(R.layout.hlj_common_fragment_content);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_content,
                        LaunchAdFragment.newInstance(dataConfig != null && dataConfig
                                .isMadAdSplashOpen()))
                .commit();

    }

    private void initAppConfig() {
        Session.getInstance()
                .init(this);
        JsUtil.getInstance()
                .loadJsInfo(this);
        DataConfigUtil.getInstance()
                .executeDataConfigTask(this);
        //记录app启动次数
        recordAppStartCount();
        SupportUtil.getInstance(this)
                .getSupports(this, null);
        new PropertiesUtil.PropertiesSyncTask(this, null).execute();
        configSubscriptions = new SubscriptionList();
        configSubscriptions.add(CustomerACMUtil.synAppConfigParameter(this));
        configSubscriptions.add(CustomerACMUtil.initFinancialSwitch(this));
    }

    private void initTracker() {
        CustomCommonApi.createPhone(this, null)
                .subscribe(new EmptySubscriber<Long>());
        CustomCommonApi.sendAppAnalytics(this)
                .subscribe(new EmptySubscriber<JsonElement>());
        new HljTracker.Builder(this).eventableType("User")
                .action("launch")
                .screen("splash")
                .additional(NetUtil.getNetType(this))
                .build()
                .send();

    }

    //记录app启动次数
    private void recordAppStartCount() {
        SharedPreferences sharedPreferences = getSharedPreferences(HljCommon.FileNames.PREF_FILE,
                Context.MODE_PRIVATE);
        int startCount = sharedPreferences.getInt(HljCommon.SharedPreferencesNames.APP_START_COUNT,
                0);
        sharedPreferences.edit()
                .putInt(HljCommon.SharedPreferencesNames.APP_START_COUNT, ++startCount)
                .apply();
    }

    /**
     * 有token没有phone的用户 要登出 确保 进来的用户是(有token 有phone  or 无token 无phone)
     */
    private void loginOut() {
        User user = Session.getInstance()
                .getCurrentUser(this);
        if (user == null) {
            return;
        }
        String token = user.getToken();
        String phone = user.getPhone();
        if (!JSONUtil.isEmpty(token) && JSONUtil.isEmpty(phone)) {
            //登出
            Session.getInstance()
                    .clearLogout(this);
        }
    }

    /**
     * show  true 显示引导
     * show false 婚期判断  show = false 一定是有token 有phone
     * 因为在oncreate 中已经将有token 无phone的用户Logout()
     *
     * @return
     */
    public boolean isShowGudieLogin() {
        User user = Session.getInstance()
                .getCurrentUser(this);
        boolean show = false;
        if (user == null) {
            show = true;
        } else {
            String token = user.getToken();
            String phone = user.getPhone();
            if (JSONUtil.isEmpty(token)) {
                show = true;
            } else {
                if (!JSONUtil.isEmpty(phone)) {
                    show = false;
                }
            }
        }
        return show;
    }


    private void gotoMainActivity(Poster poster) {
        Intent intent;
        if (Session.getInstance()
                .hasSetMyCity(this)) {
            intent = new Intent(this, MainActivity.class);
        } else {
            intent = new Intent(this, CityListActivity.class);
            intent.putExtra(CityListActivity.ARG_IS_INITIAL_PAGE, true);
        }
        if (poster != null) {
            intent.putExtra("poster", poster);
        }
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.splash_fade_in, R.anim.splash_fade_out);
    }

    @Override
    public void onLaunchFinish() {
        if (isShowGudieLogin()) {
            Intent i = new Intent(SplashActivity.this, LoginActivity.class);
            i.putExtra("type", Constants.Login.REGISTER);
            SplashActivity.this.startActivity(i);
            SplashActivity.this.finish();
            SplashActivity.this.overridePendingTransition(R.anim.splash_fade_in,
                    R.anim.splash_fade_out);
        } else {
            //有token  有phone
            User user = Session.getInstance()
                    .getCurrentUser(SplashActivity.this);
            if (user.getWeddingDay() == null && user.getIsPending() != 1) {
                //未设置婚期
                Intent i = new Intent(SplashActivity.this, WeddingDateSetActivity.class);
                i.putExtra("type", Constants.Login.REGISTER);
                SplashActivity.this.startActivity(i);
                SplashActivity.this.finish();
                SplashActivity.this.overridePendingTransition(R.anim.splash_fade_in,
                        R.anim.splash_fade_out);
            } else {
                //设置了婚期 或者婚期待定
                gotoMainActivity(null);
            }
        }
    }

    @Override
    public void onPosterClick(Poster poster) {
        gotoMainActivity(poster);
    }


    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(configSubscriptions);
    }
}
