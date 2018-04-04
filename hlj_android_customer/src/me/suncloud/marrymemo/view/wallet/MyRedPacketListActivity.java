package me.suncloud.marrymemo.view.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;

import com.hunliji.hljcommonlibrary.adapters.OnTabTextChangeListener;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.widgets.TabPageIndicator;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.fragment.wallet.MyRedPacketListFragment;

/**
 * 我的红包界面
 * Created by chen_bin on 2016/10/15 0015.
 */
public class MyRedPacketListActivity extends HljBaseActivity implements TabPageIndicator
        .OnTabChangeListener {

    @BindView(R.id.indicator)
    TabPageIndicator indicator;
    @BindView(R.id.view_pager)
    ViewPager viewPager;

    private SparseArray<RefreshFragment> fragments;
    public final static int TAB_UNUSED = 0; //未使用tab
    public final static int TAB_EXPIRED = 1; //已过期tab
    public final static int TAB_USED = 2; //已使用tab

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_red_packet_and_coupon_list);
        ButterKnife.bind(this);
        hideDividerView();
        setOkText(R.string.label_tips_use);
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
        indicator.setPagerAdapter(pagerAdapter);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(final int position) {
            RefreshFragment fragment = fragments.get(position);
            if (fragment != null) {
                return fragment;
            }
            switch (position) {
                case TAB_UNUSED:
                    fragment = MyRedPacketListFragment.newInstance(MyRedPacketListFragment
                            .STATUS_UNUSED);
                    break;
                case TAB_EXPIRED:
                    fragment = MyRedPacketListFragment.newInstance(MyRedPacketListFragment
                            .STATUS_EXPIRED);
                    break;
                case TAB_USED:
                    fragment = MyRedPacketListFragment.newInstance(MyRedPacketListFragment
                            .STATUS_USED);
                    break;
            }
            if (fragment != null) {
                fragment.setOnTabTextChangeListener(new OnTabTextChangeListener() {
                    @Override
                    public void onTabTextChange(int totalCount) {
                        setTabText(position, totalCount);
                    }
                });
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
            return getPageTitleStr(position, 0);
        }
    }

    @Override
    public void onTabChanged(int position) {
        viewPager.setCurrentItem(position);
    }

    @Override
    public void onOkButtonClick() {
        startActivity(new Intent(this, MyRedPacketDescriptionActivity.class));
    }

    private String getPageTitleStr(int position, int totalCount) {
        switch (position) {
            case 0:
                return getString(R.string.label_unused, totalCount);
            case 1:
                return getString(R.string.label_expired, totalCount);
            case 2:
                return getString(R.string.label_used, totalCount);
            default:
                return null;
        }
    }

    private void setTabText(int position, int totalCount) {
        indicator.setTabText(getPageTitleStr(position, totalCount), position);
    }

}