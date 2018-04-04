package me.suncloud.marrymemo.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.hunliji.hljcardcustomerlibrary.views.activities.AccountSecurityActivity;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljupdatelibrary.HljUpdate;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.util.CacheClear;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.binding_partner.BindingPartnerActivity;

public class SettingActivity extends HljBaseActivity {

    private TextView cacheSize;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        if (HljUpdate.getInstance()
                .hasUpdate(this)) {
            findViewById(R.id.new_versions_view).setVisibility(View.VISIBLE);
        }
        cacheSize = (TextView) findViewById(R.id.cache_size);
        try {
            cacheSize.setText((float) ((CacheClear.getFolderSize(getCacheDir()) + CacheClear
                    .getFolderSize(
                    getExternalCacheDir())) / 104857) / 10 + "M");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void safe(View v) {
        if (Util.loginBindChecked(SettingActivity.this, Constants.RequestCode.SETTING_SAFE)) {
            Intent intent = new Intent(this, AccountSecurityActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

    public void about(View v) {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    public void clearCache(View v) {
        CacheClear.cleanInternalCache(this);
        CacheClear.cleanExternalCache(this);
        CacheClear.clearWebStorage();
        HljHttp.clearCache(this);
        try {
            cacheSize.setText((float) ((CacheClear.getFolderSize(getCacheDir()) + CacheClear
                    .getFolderSize(
                    getExternalCacheDir())) / 104857) / 10 + "M");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onInformation(View v) {
        Intent intentEdit = new Intent(this, AccountEditActivity.class);
        startActivityForResult(intentEdit, Constants.RequestCode.EDIT_USER_INFO);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    public void onAccount(View view) {
        Intent intent = new Intent(this, ThirdBindAccountActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    public void onPartner(View view) {
        startActivity(new Intent(this, BindingPartnerActivity.class));
    }

    public void logout(View v) {
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        if (dialog == null) {
            dialog = new Dialog(this, R.style.BubbleDialogTheme);
            dialog = DialogUtil.createDoubleButtonDialog(this,
                    getResources().getString(R.string.hint_logout),
                    getResources().getString(R.string.action_ok),
                    getResources().getString(R.string.action_cancel),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            Session.getInstance()
                                    .logout(SettingActivity.this);
                            Intent intent = getIntent();
                            intent.putExtra("logout", true);
                            setResult(RESULT_OK, intent);
                            onBackPressed();
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
            Window window = dialog.getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            Point point = JSONUtil.getDeviceSize(this);
            params.width = Math.round(point.x * 5 / 7);
            window.setAttributes(params);
        }
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            switch (requestCode) {
                case Constants.RequestCode.SETTING_SAFE:
                    Intent intent = new Intent(this, AccountSecurityActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}