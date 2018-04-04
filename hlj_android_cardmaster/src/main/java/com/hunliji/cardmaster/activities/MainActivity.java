package com.hunliji.cardmaster.activities;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hunliji.cardmaster.activities.login.LoginActivity;
import com.hunliji.cardmaster.models.PushData;
import com.hunliji.cardmaster.utils.BannerUtil;
import com.hunliji.cardmaster.utils.DataConfigUtil;
import com.hunliji.cardmaster.utils.NotificationUtil;
import com.hunliji.cardmaster.R;
import com.hunliji.cardmaster.fragments.CardMasterCardListFragment;
import com.hunliji.cardmaster.fragments.login.LoginFragment;
import com.hunliji.cardmaster.service.GetuiIntentService;
import com.hunliji.cardmaster.service.GetuiPushService;
import com.hunliji.hljcommonlibrary.models.MaintainEvent;
import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.EmptySubscriber;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.hljkefulibrary.HljKeFu;
import com.hunliji.hljupdatelibrary.HljUpdate;
import com.igexin.sdk.PushManager;

import java.util.Timer;
import java.util.TimerTask;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * Created by wangtao on 2017/11/24.
 */

@Route(path = RouterPath.IntentPath.Customer.MAIN)
@RuntimePermissions
public class MainActivity extends HljBaseNoBarActivity implements LoginFragment.LoginCallback {

    private final String TAG_CARD_LIST_FRAGMENT = "cardListFragment";
    private final String TAG_LOGIN_FRAGMENT = "loginFragment";


    private Subscription hxLoginSubscription;
    private Subscription errorSubscription;
    private boolean isExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSwipeBackEnable(false);
        setContentView(R.layout.hlj_common_fragment_content);
        MainActivityPermissionsDispatcher.requestPermissionWithCheck(this);
        DataConfigUtil.INSTANCE.executeDataConfig(this);
        onNewIntent(getIntent());
        registerErrorBusEvent();
    }


    @Override
    public void onComplete() {
        if (getSupportFragmentManager().findFragmentByTag(TAG_CARD_LIST_FRAGMENT) != null) {
            return;
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_right,
                R.anim.slide_out_left)
                .replace(R.id.fragment_content,
                        CardMasterCardListFragment.newInstance(),
                        TAG_CARD_LIST_FRAGMENT);
        ft.commitAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_LOGIN_FRAGMENT);
        if (fragment != null) {
            if (fragment.getChildFragmentManager()
                    .getBackStackEntryCount() > 0) {
                fragment.getChildFragmentManager()
                        .popBackStack();
                return;
            }
        }
        exitBy2Click();
    }

    @Override
    protected void onResume() {
        super.onResume();

        HljUpdate.getInstance()
                .update(this);

        User user = UserSession.getInstance()
                .getUser(this);
        if (user != null && user.getId() > 0) {
            hxLoginSubscription = HljKeFu.loginObb(this)
                    .subscribe(new EmptySubscriber<Boolean>());
            NotificationUtil.INSTANCE.getNewNotifications(this, user.getId());
        }else {
            CommonUtil.unSubscribeSubs(hxLoginSubscription);
        }

        if (user != null && user.getId() > 0) {
            if (getSupportFragmentManager().findFragmentByTag(TAG_CARD_LIST_FRAGMENT) != null) {
                return;
            }
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Fragment loginFragment = getSupportFragmentManager().findFragmentByTag(
                    TAG_LOGIN_FRAGMENT);
            if (loginFragment != null) {
                ft.replace(R.id.fragment_content,
                        CardMasterCardListFragment.newInstance(),
                        TAG_CARD_LIST_FRAGMENT);
            } else {
                ft.add(R.id.fragment_content,
                        CardMasterCardListFragment.newInstance(),
                        TAG_CARD_LIST_FRAGMENT);
            }
            ft.commitAllowingStateLoss();
        } else {
            if (getSupportFragmentManager().findFragmentByTag(TAG_LOGIN_FRAGMENT) != null) {
                return;
            }
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Fragment cardListFragment = getSupportFragmentManager().findFragmentByTag(
                    TAG_CARD_LIST_FRAGMENT);
            if (cardListFragment != null) {
                ft.replace(R.id.fragment_content,
                        LoginFragment.newInstance(true),
                        TAG_LOGIN_FRAGMENT);
            } else {
                ft.add(R.id.fragment_content, LoginFragment.newInstance(true), TAG_LOGIN_FRAGMENT);
            }
            ft.commitAllowingStateLoss();
        }
    }

    @Override
    protected void onFinish() {
        CommonUtil.unSubscribeSubs(hxLoginSubscription,errorSubscription);
        super.onFinish();
    }

    @NeedsPermission({Manifest.permission.READ_PHONE_STATE, Manifest.permission
            .WRITE_EXTERNAL_STORAGE})
    void requestPermission() {
        PushManager.getInstance()
                .initialize(this.getApplicationContext(), GetuiPushService.class);
        PushManager.getInstance()
                .registerPushIntentService(this.getApplicationContext(), GetuiIntentService.class);
    }

    @OnPermissionDenied({Manifest.permission.READ_PHONE_STATE, Manifest.permission
            .WRITE_EXTERNAL_STORAGE})
    void onDeniedForPermission() {
        PushManager.getInstance()
                .initialize(this.getApplicationContext(), GetuiPushService.class);
        PushManager.getInstance()
                .registerPushIntentService(this.getApplicationContext(), GetuiIntentService.class);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this,
                requestCode,
                grantResults);
    }

    private void exitBy2Click() {
        Timer tExit;
        if (!isExit) {
            isExit = true;
            ToastUtil.showToast(this, null, R.string.label_quit);
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false;
                }
            }, 2000);
        } else {
            finish();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        PushData pushData = intent.getParcelableExtra(GetuiIntentService.ARG_PUSH_DATA);
        if (pushData != null) {
            User user = UserSession.getInstance()
                    .getUser(this);
            if (user == null) {
                return;
            }
            if (!TextUtils.isEmpty(pushData.getTaskId()) && !TextUtils.isEmpty(pushData
                    .getMessageId())) {
                //推送消息回馈
                PushManager.getInstance()
                        .sendFeedbackMessage(this,
                                pushData.getTaskId(),
                                pushData.getMessageId(),
                                90002);
            }
            switch (pushData.getType()) {
                case PushData.POSTER:
                    BannerUtil.bannerJump(this, pushData.getPoster(), null);
                    break;
            }
        } else {
            String action = intent.getStringExtra("action");
            if (!TextUtils.isEmpty(action)) {
                switch (action) {
                    case "exit":
                        finish();
                        break;
                }
            }

        }
    }

    /**
     * 初始化服务异常rxBus注册
     */
    private void registerErrorBusEvent() {
        if (errorSubscription == null || errorSubscription.isUnsubscribed()) {
            errorSubscription = RxBus.getDefault()
                    .toObservable(MaintainEvent.class)
                    .filter(new Func1<MaintainEvent, Boolean>() {
                        @Override
                        public Boolean call(MaintainEvent rxEvent) {
                            return rxEvent.getType() == MaintainEvent.EventType.USER_TOKEN_ERROR;
                        }
                    })
                    .first()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new RxBusSubscriber<MaintainEvent>() {
                        @Override
                        protected void onEvent(MaintainEvent rxEvent) {
                            switch (rxEvent.getType()) {
                                case MaintainEvent.EventType.USER_TOKEN_ERROR:
                                    Intent intent = new Intent(MainActivity.this,
                                            LoginActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent
                                            .FLAG_ACTIVITY_NEW_TASK);
                                    intent.putExtra(RouterPath.IntentPath.Customer.Login
                                                    .ARG_IS_RESET,
                                            true);
                                    getApplicationContext().startActivity(intent);
                                    break;
                            }
                        }
                    });
        }
    }

}
