package me.suncloud.marrymemo.view.community;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.widget.ImageView;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.fragment.HotThreadRankListFragment;
import me.suncloud.marrymemo.widget.TabPageIndicator;

/**
 * Created by mo_yu on 2016/12/19.热帖排行榜
 */
public class HotThreadRankListActivity extends HljBaseActivity implements TabPageIndicator
        .OnTabChangeListener {
    @BindView(R.id.indicator)
    TabPageIndicator indicator;
    @BindView(R.id.view_pager)
    ViewPager viewPager;

    private SparseArray<Fragment> fragments;
    private final static int[] iconResIds = {R.drawable.sl_ic_day_g_2_day_r, R.drawable
            .sl_ic_week_g_2_week_r, R.drawable.sl_ic_month_g_2_month_r};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_rank);
        ButterKnife.bind(this);
        hideDividerView();
        fragments = new SparseArray<>();
        SectionsPagerAdapter pagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(2);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                indicator.setCurrentItem(position);
            }
        });
        indicator.setOnTabChangeListener(this);
        indicator.setTabViewId(R.layout.menu_rank_tab_widget);
        indicator.setPagerAdapter(pagerAdapter);
        for (int i = 0, size = pagerAdapter.getCount(); i < size; i++) {
            ((ImageView) indicator.getTabView(i)
                    .findViewById(R.id.iv_rank_tab)).setImageResource(iconResIds[i]);
        }
    }

    //	last_day,last_month,last_week
    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = fragments.get(position);
            if (fragment != null) {
                return fragment;
            }
            switch (position) {
                case 0:
                    fragment = HotThreadRankListFragment.newInstance("last_day");
                    break;
                case 1:
                    fragment = HotThreadRankListFragment.newInstance("last_week");
                    break;
                case 2:
                    fragment = HotThreadRankListFragment.newInstance("last_month");
                    break;
            }
            fragments.put(position, fragment);
            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getString(R.string.label_hot_thread_rank);
        }
    }

    @Override
    public void onTabChanged(int position) {
        viewPager.setCurrentItem(position);
    }
}