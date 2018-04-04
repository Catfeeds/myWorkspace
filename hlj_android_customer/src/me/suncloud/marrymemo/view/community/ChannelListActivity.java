package me.suncloud.marrymemo.view.community;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.fragment.community.ChannelListFragment;

/**
 * Created by mo_yu on 2017/1/4.频道列表页
 */
@Deprecated
public class ChannelListActivity extends HljBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hlj_common_fragment_content);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_content, new ChannelListFragment(), "ChannelListFragment");
        transaction.commitAllowingStateLoss();
    }

}
