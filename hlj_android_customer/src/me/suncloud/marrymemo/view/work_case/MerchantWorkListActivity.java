package me.suncloud.marrymemo.view.work_case;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.SmallWorkViewHolder;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.fragment.work_case.MerchantWorkFragment;

/**
 * 商家套餐
 * created by chen_bin 2016/11/16
 */
@Route(path = RouterPath.IntentPath.Customer.MERCHANT_WORK_LIST)
public class MerchantWorkListActivity extends HljBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hlj_common_fragment_content);
        long id = getIntent().getLongExtra("id", 0);
        int style = getIntent().getIntExtra("style", SmallWorkViewHolder.STYLE_COMMON);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        MerchantWorkFragment fragment = (MerchantWorkFragment) fm.findFragmentByTag(
                "MerchantWorkFragment");
        if (fragment == null) {
            fragment = MerchantWorkFragment.newInstance(id, style);
            ft.add(R.id.fragment_content, fragment, "MerchantWorkFragment");
        }
        ft.show(fragment);
        ft.commitAllowingStateLoss();
    }
}