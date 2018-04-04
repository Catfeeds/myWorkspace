package com.hunliji.marrybiz.view.work_case;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.TabPageIndicator;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.fragment.work_case.WorkManageListFragment;
import com.hunliji.marrybiz.util.work_case.WorkStatusEnum;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 套餐和案例的管理列表
 * Created by chen_bin on 2017/2/3 0003.
 */
public class WorkManageListActivity extends HljBaseActivity implements TabPageIndicator
        .OnTabChangeListener {
    @BindView(R.id.indicator)
    TabPageIndicator indicator;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    private int type; //type=0代表套餐，否则代表案例
    private WorkManageListFragment onFragment; //上架
    private WorkManageListFragment reviewFragment; //审核中
    private WorkManageListFragment rejectedFragment; //审核不通过
    private WorkManageListFragment offFragment; //下架

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_manage_list);
        ButterKnife.bind(this);
        hideDividerView();
        type = getIntent().getIntExtra("type", 0);
        setTitle(type == 0 ? R.string.label_work_manage : R.string.label_case_manage);
        SectionsPagerAdapter pagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(3);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                indicator.setCurrentItem(position);
            }
        });
        indicator.setOnTabChangeListener(this);
        indicator.setPagerAdapter(pagerAdapter);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                //已上架 is_sold_out=0&status=1
                case 0:
                    if (onFragment == null) {
                        onFragment = WorkManageListFragment.newInstance(type, WorkStatusEnum.ON);
                    }
                    return onFragment;
                //审核中 status=0&is_sold_out=0
                case 1:
                    if (reviewFragment == null) {
                        reviewFragment = WorkManageListFragment.newInstance(type,
                                WorkStatusEnum.REVIEW);
                    }
                    return reviewFragment;
                //审核不通过  is_sold_out:0 status:3
                case 2:
                    if (rejectedFragment == null) {
                        rejectedFragment = WorkManageListFragment.newInstance(type,
                                WorkStatusEnum.REJECTED);
                    }
                    return rejectedFragment;
                //已下架 is_sold_out=1
                case 3:
                    if (offFragment == null) {
                        offFragment = WorkManageListFragment.newInstance(type, WorkStatusEnum.OFF);
                    }
                    return offFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return WorkStatusEnum.ON.getStatusDescription();
                case 1:
                    return WorkStatusEnum.REVIEW.getStatusDescription();
                case 2:
                    return WorkStatusEnum.REJECTED.getStatusDescription();
                case 3:
                    return WorkStatusEnum.OFF.getStatusDescription();
                default:
                    return null;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                //优化列表操作回来
                case Constants.RequestCode.WORK_OPTIMIZE:
                    if (onFragment != null) {
                        onFragment.refresh();
                    }
                    if (reviewFragment != null) {
                        reviewFragment.setNeedRefresh(true);
                    }
                    if (rejectedFragment != null) {
                        rejectedFragment.setNeedRefresh(true);
                    }
                    if (offFragment != null) {
                        offFragment.setNeedRefresh(true);
                    }
                    break;
                //排序回来刷新
                case Constants.RequestCode.WORK_SORT:
                    if (onFragment != null) {
                        onFragment.refresh();
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void refreshData() {
        if (onFragment != null && viewPager.getCurrentItem() != 0) {
            onFragment.setNeedRefresh(true);
        }
        if (reviewFragment != null && viewPager.getCurrentItem() != 1) {
            reviewFragment.setNeedRefresh(true);
        }
        if (rejectedFragment != null && viewPager.getCurrentItem() != 2) {
            rejectedFragment.setNeedRefresh(true);
        }
        if (offFragment != null && viewPager.getCurrentItem() != 3) {
            offFragment.setNeedRefresh(true);
        }
    }

    @Override
    public void onTabChanged(int position) {
        viewPager.setCurrentItem(position);
    }

}