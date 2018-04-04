package com.hunliji.marrybiz.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hunliji.hljchatlibrary.WebSocket;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljlivelibrary.HljLive;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.adapter.ContactsAdapter;
import com.hunliji.marrybiz.model.MerchantUser;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.Session;
import com.hunliji.marrybiz.util.Util;
import com.hunliji.marrybiz.view.login.PrePhoneRegisterActivity;

import java.util.ArrayList;

import rx.Subscription;


public class PreLoginActivity extends Activity {

    private boolean isLoginDone;
    private boolean isRegisterDone;
    private boolean isLogout;
    private Subscription rxBusEventSub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean isFirst = getSharedPreferences(Constants.PREF_FILE,
                Context.MODE_PRIVATE).getBoolean("isFirst", true);
        if (isFirst) {
            Intent intent = new Intent(this, FirstActivity.class);
            startActivity(intent);
            finish();
        }

        //        getSharedPreferences(Constants.PREF_FILE, Context
        // .MODE_PRIVATE).edit().putBoolean("isFirst", true).commit();

        redirectAtRelaunch(getIntent());

        MerchantUser user = Session.getInstance()
                .getCurrentUser(this);
        if (user != null) {
            if (JSONUtil.isEmpty(user.getPhone())) {
                // 本地有登陆成功的存储信息,但没有设置手机号码登陆
                Session.getInstance()
                        .logout(this);
            } else {
                // 用户已经登录,直接到启动页
                Intent i = new Intent(this, SplashActivity.class);
                startActivity(i);
                finish();
            }
        }

        // 用户没有登录
        // 显示当前登录页面
        setContentView(R.layout.activity_pre_login);
        registerRxBusEvent();
    }

    @Override
    protected void onNewIntent(Intent data) {
        super.onNewIntent(data);

        redirectAtRelaunch(data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CommonUtil.unSubscribeSubs(rxBusEventSub);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isFinishing()){
            CommonUtil.unSubscribeSubs(rxBusEventSub);
        }
    }

    // 验证不需要之后彻底删除
    private void redirectAtRelaunch(Intent data) {
        if (data != null) {
            isLoginDone = data.getBooleanExtra("is_login_done", false);
            isRegisterDone = data.getBooleanExtra("is_register_done", false);
            isLogout = data.getBooleanExtra("logout", false);
        }
        if (isLogout) {
            Session.getInstance()
                    .logout(this);
        }
        if (isLoginDone || isRegisterDone) {
            MerchantUser user = Session.getInstance()
                    .getCurrentUser(this);
            if (user != null) {
                if (JSONUtil.isEmpty(user.getPhone())) {
                    // 登陆成功,但没有设置手机号码登陆
                    Intent intent = new Intent(PreLoginActivity.this,
                            LoginPhoneAlertActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                    finish();
                } else {
                    // 登陆成功跳转到主界面
                    Intent intent = new Intent(PreLoginActivity.this, HomeActivity.class);
                    intent.putExtra("is_login_done", true);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                    finish();
                }
            }
        }
    }

    public void onRegister(View view) {
        Intent intent = new Intent(this, PrePhoneRegisterActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    public void onLogin(View view) {
        Intent intent = new Intent(this, NewLoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }


    //注册RxEventBus监听
    private void registerRxBusEvent() {
        if (rxBusEventSub == null || rxBusEventSub.isUnsubscribed()) {
            rxBusEventSub = RxBus.getDefault()
                    .toObservable(RxEvent.class)
                    .subscribe(new RxBusSubscriber<RxEvent>() {
                        @Override
                        protected void onEvent(RxEvent rxEvent) {
                            switch (rxEvent.getType()) {
                                case REGISTER_SUCCESS:
                                    finish();
                                    break;
                            }
                        }
                    });
        }
    }
}
