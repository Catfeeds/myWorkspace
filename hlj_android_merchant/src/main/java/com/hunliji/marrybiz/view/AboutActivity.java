package com.hunliji.marrybiz.view;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.marrybiz.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutActivity extends HljBaseActivity {

    @BindView(R.id.textView3)
    TextView textView3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        try {
            textView3.setText(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
