package com.hunliji.marrybiz.view.tools;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.widgets.TabPageIndicator;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.fragment.tools.MyScheduleListFragment;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 档期查看全部界面
 * Created by chen_bin on 2016/10/24 0024.
 */
public class MyScheduleListActivity extends HljBaseNoBarActivity implements TabPageIndicator
        .OnTabChangeListener {
    @BindView(R.id.indicator)
    TabPageIndicator indicator;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    private MyScheduleListFragment waitFragment; //待办
    private MyScheduleListFragment doneFragment; //已办

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_schedule_list);
        ButterKnife.bind(this);
        setDefaultStatusBarPadding();
        SectionsPagerAdapter pagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(1);
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

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    if (waitFragment == null) {
                        waitFragment = MyScheduleListFragment.getInstance(1);
                    }
                    return waitFragment;
                case 1:
                    if (doneFragment == null) {
                        doneFragment = MyScheduleListFragment.getInstance(0);
                    }
                    return doneFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 1:
                    return getString(R.string.label_has_been_done);
                default:
                    return getString(R.string.label_need_deal_with);
            }
        }
    }

    //刷新数据
    public void refreshData() {
        if (waitFragment != null) {
            if (viewPager.getCurrentItem() == 0) {
                waitFragment.refresh();
            } else {
                waitFragment.setNeedRefresh(true);
            }
        }
        if (doneFragment != null) {
            if (viewPager.getCurrentItem() == 1) {
                doneFragment.refresh();
            } else {
                doneFragment.setNeedRefresh(true);
            }
        }
    }

    @Override
    public void onTabChanged(int position) {
        viewPager.setCurrentItem(position);
    }

    @OnClick(R.id.btn_back)
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}