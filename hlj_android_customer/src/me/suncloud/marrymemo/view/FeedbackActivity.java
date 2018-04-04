package me.suncloud.marrymemo.view;

import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;

import me.suncloud.marrymemo.R;

@Route(path = RouterPath.IntentPath.Customer.FEED_BACK_ACTIVITY)
public class FeedbackActivity extends HljBaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
    }

}
