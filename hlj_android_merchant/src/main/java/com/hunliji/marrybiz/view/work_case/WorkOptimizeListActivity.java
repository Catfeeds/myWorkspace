package com.hunliji.marrybiz.view.work_case;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.fragment.work_case.WorkManageListFragment;
import com.hunliji.marrybiz.util.work_case.WorkStatusEnum;

import butterknife.ButterKnife;

/**
 * 套餐案例优化列表
 * Created by chen_bin on 2017/2/4 0004.
 */
public class WorkOptimizeListActivity extends HljBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hlj_common_fragment_content);
        ButterKnife.bind(this);
        int type = getIntent().getIntExtra("type", 0);
        setTitle(type == 0 ? R.string.label_work_optimize : R.string.label_case_optimize);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.fragment_content,
                WorkManageListFragment.newInstance(type, WorkStatusEnum.OPTIMIZE),
                "WorkManageListFragment");
        ft.commitAllowingStateLoss();
    }
}