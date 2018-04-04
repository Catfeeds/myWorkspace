package com.hunliji.cardmaster.activities.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hunliji.cardmaster.HLJCardMasterApplication;
import com.hunliji.cardmaster.R;
import com.hunliji.cardmaster.activities.MainActivity;
import com.hunliji.cardmaster.activities.SplashActivity;
import com.hunliji.cardmaster.fragments.login.LoginFragment;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;

/**
 * Created by wangtao on 2017/11/23.
 */

@Route(path = RouterPath.IntentPath.Customer.LoginActivityPath.LOGIN)
public class LoginActivity extends HljBaseNoBarActivity implements LoginFragment.LoginCallback{

    private boolean isReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_login);
        isReset = getIntent().getBooleanExtra(RouterPath.IntentPath.Customer.Login.ARG_IS_RESET,false);
        setSwipeBackEnable(false);
        setContentView(R.layout.hlj_common_fragment_content);
        if(isReset) {
            HLJCardMasterApplication.logout(this);
        }
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_content, LoginFragment.newInstance(false))
                .commitAllowingStateLoss();
    }



    @Override
    public void onBackPressed() {
        Fragment fragment=getSupportFragmentManager().findFragmentById(R.id.fragment_content);
        if(fragment!=null){
            if (fragment.getChildFragmentManager().getBackStackEntryCount() > 1) {
                fragment.getChildFragmentManager().popBackStack();
                return;
            }
        }
        finish();
        overridePendingTransition(R.anim.activity_anim_default, R.anim.slide_out_down);
    }

    @Override
    public void onComplete() {
        if(isReset){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }else {
            setResult(Activity.RESULT_OK);
            finish();
            overridePendingTransition(R.anim.activity_anim_default, R.anim.slide_out_down);
        }
    }
}
