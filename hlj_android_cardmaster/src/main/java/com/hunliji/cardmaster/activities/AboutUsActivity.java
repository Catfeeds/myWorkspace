package com.hunliji.cardmaster.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.example.suncloud.hljweblibrary.views.activities.HljWebViewActivity;
import com.hunliji.cardmaster.Constants;
import com.hunliji.cardmaster.R;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljupdatelibrary.HljUpdate;
import com.hunliji.hljupdatelibrary.models.UpdateInfo;

/**
 * 关于我们
 */
public class AboutUsActivity extends HljBaseActivity {

    private UpdateInfo updateInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView tvVersionName = (TextView) findViewById(R.id.tv_version_name);
        tvVersionName.setText(Constants.APP_VERSION);

        updateInfo = HljUpdate.getInstance()
                .getUpdateInfo(this);
        if (updateInfo != null) {
            findViewById(R.id.version_layout).setVisibility(View.VISIBLE);
            TextView versionTitleView = (TextView) findViewById(R.id.version_title);
            versionTitleView.setText(String.format(getString(R.string.label_version_title),
                    updateInfo.getVersionName()));
            TextView versionInfoView = (TextView) findViewById(R.id.version_info);
            versionInfoView.setMovementMethod(ScrollingMovementMethod.getInstance());
            versionInfoView.setText(updateInfo.getInfo());
            if (HljUpdate.getInstance()
                    .hasUpdate(this)) {
                findViewById(R.id.update_btn).setVisibility(View.VISIBLE);
                versionInfoView.setPadding(versionInfoView.getPaddingLeft(),
                        versionInfoView.getPaddingTop(),
                        versionInfoView.getPaddingRight(),
                        Math.round(45 * getResources().getDisplayMetrics().density));
            } else {
                findViewById(R.id.update_btn).setVisibility(View.GONE);
            }
        }
    }

    public void onUpdate(View view) {
        if (updateInfo != null) {
            HljUpdate.onUpdate(this, updateInfo);
        }
    }

    public void onAgreement(View v) {
        Intent intent = new Intent(this, HljWebViewActivity.class);
        intent.putExtra("path", Constants.USER_PROTOCOL_URL);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    public void onSecretPolicy(View v) {
        Intent intent = new Intent(this, HljWebViewActivity.class);
        intent.putExtra("path", Constants.SECRET_PROTOCOL_URL);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
