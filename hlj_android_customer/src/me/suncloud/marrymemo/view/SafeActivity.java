package me.suncloud.marrymemo.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.Session;

public class SafeActivity extends HljBaseActivity {

    private TextView realNameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe);
        realNameView = (TextView) findViewById(R.id.real_name);
        User user = Session.getInstance()
                .getCurrentUser(SafeActivity.this);
        realNameView.setText(user.getRealName());
    }

    public void name(View v) {
        Intent intent = new Intent(this, EditRealNameActivity.class);
        startActivityForResult(intent, Constants.RequestCode.EDIT_REAL_NAME);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.RequestCode.EDIT_REAL_NAME:
                    if (data != null && data.getBooleanExtra("refresh", false)) {
                        Intent intent = getIntent();
                        intent.putExtra("refresh", true);
                        setResult(RESULT_OK, intent);
                        User user = Session.getInstance()
                                .getCurrentUser(this);
                        if (!JSONUtil.isEmpty(user.getRealName())) {
                            realNameView.setText(user.getRealName());
                        }
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
