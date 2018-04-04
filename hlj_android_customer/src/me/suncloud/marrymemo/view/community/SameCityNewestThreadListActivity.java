package me.suncloud.marrymemo.view.community;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.fragment.community.CommunityFragment;

/**
 * 同城最新,显示所在地为重点城市及该市关联城市用户所发的帖子。
 * Created by chen_bin on 2017/3/23 0023.
 */
public class SameCityNewestThreadListActivity extends HljBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hlj_common_fragment_content);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        CommunityFragment fragment = (CommunityFragment) fm.findFragmentByTag("CommunityFragment");
        if (fragment == null) {
            fragment = CommunityFragment.newInstance(Constants.HttpPath
                            .GET_SAME_CITY_NEWEST_THREADS,
                    true,
                    true,
                    true,
                    true);
            ft.add(R.id.fragment_content, fragment, "CommunityFragment");
        }
        ft.show(fragment);
        ft.commitAllowingStateLoss();
    }

}
