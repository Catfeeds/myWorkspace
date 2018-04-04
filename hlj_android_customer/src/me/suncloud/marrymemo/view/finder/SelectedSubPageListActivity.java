package me.suncloud.marrymemo.view.finder;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.fragment.finder.SubPageListFragment;

/**
 * 精选专栏列表
 * Created by chen_bin on 2016/12/29 0029.
 */
public class SelectedSubPageListActivity extends HljBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hlj_common_fragment_content);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        SubPageListFragment fragment = (SubPageListFragment) fm.findFragmentByTag(
                "SubPageListFragment");
        if (fragment == null) {
            fragment = SubPageListFragment.newInstance();
            ft.add(R.id.fragment_content, fragment, "SubPageListFragment");
        }
        ft.show(fragment);
        ft.commitAllowingStateLoss();
    }
}