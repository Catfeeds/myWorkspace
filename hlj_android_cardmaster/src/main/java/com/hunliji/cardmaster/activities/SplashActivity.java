package com.hunliji.cardmaster.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.hunliji.cardmaster.R;
import com.hunliji.cardmaster.activities.login.LoginActivity;
import com.hunliji.cardmaster.utils.DataConfigUtil;
import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljhttplibrary.authorization.UserSession;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class SplashActivity extends AppCompatActivity {


    private Subscription timeSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        DataConfigUtil.INSTANCE.executeDataConfig(this);
        timeSubscription = Observable.timer(3, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.splash_fade_in, R.anim.splash_fade_out);

                    }
                });
    }

    @Override
    protected void onPause() {
        if(isFinishing()){
            CommonUtil.unSubscribeSubs(timeSubscription);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        CommonUtil.unSubscribeSubs(timeSubscription);
        super.onDestroy();
    }
}
