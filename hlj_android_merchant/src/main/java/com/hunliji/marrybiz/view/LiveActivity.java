package com.hunliji.marrybiz.view;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljlivelibrary.fragments.SocialLiveFragment;
import com.hunliji.marrybiz.R;

/**
 * Created by mo_yu on 2016/10/25.我的直播列表
 */

public class LiveActivity extends HljBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_view);

        long merchantId=getIntent().getLongExtra("id",0);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.content_view, SocialLiveFragment.newInstance(merchantId,0), "SocialLiveFragment");
        transaction.commit();
    }
}
