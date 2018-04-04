package com.hunliji.marrybiz.view;

import android.app.Activity;

import com.umeng.analytics.MobclickAgent;

/**
 * Created by luohanlin on 15/3/5.
 */
public class BaseActivity extends Activity{
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
