package me.suncloud.marrymemo.view.work_case;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.fragment.work_case.MerchantCaseFragment;

/**
 * 商家案例
 * Created by chen_bin on 2017/5/23 0023.
 */
public class MerchantCaseListActivity extends HljBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hlj_common_fragment_content);
        long id = getIntent().getLongExtra("id", 0);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        MerchantCaseFragment fragment = (MerchantCaseFragment) fm.findFragmentByTag(
                "MerchantCaseFragment");
        if (fragment == null) {
            fragment = MerchantCaseFragment.newInstance(id);
            ft.add(R.id.fragment_content, fragment, "MerchantCaseFragment");
        }
        ft.show(fragment);
        ft.commitAllowingStateLoss();
    }
}
