package com.hunliji.marrybiz.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.util.Session;

public class LoginPhoneAlertActivity extends HljBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_phone_alert);
        setSwipeBackEnable(false);
        hideBackButton();
    }

    @Override
    public void onBackPressed() {
        return;
    }

    public void onSetting(View view) {
        Intent intent = new Intent(this, SettingLoginPhoneActivity.class);
        String email = Session.getInstance()
                .getCurrentUser(this)
                .getEmail();
        intent.putExtra("current_email", email);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }
}
