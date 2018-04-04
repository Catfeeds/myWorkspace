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
import com.hunliji.hljcommonlibrary.views.widgets.TabPageIndicator;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.fragment.wallet.MyCouponListFragment;

/**
 * 我的优惠券界面
 * Created by chen_bin on 2016/10/15 0015.
 */
public class MyCouponListActivity extends HljBaseActivity implements TabPageIndicator
        .OnTabChangeListener {
    @BindView(R.id.indicator)
    TabPageIndicator indicator;
    @BindView(R.id.view_pager)
    ViewPager viewPager;

    private SparseArray<MyCouponListFragment> fragments;

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
            MyCouponListFragment fragment = fragments.get(position);
            if (fragment != null) {
                return fragment;
            }
            switch (position) {
                case TAB_UNUSED:
                    fragment = MyCouponListFragment.newInstance(MyCouponListFragment.TYPE_UNUSED);
                    fragment.setOnCouponUsedListener(new OnCouponUsedListener() {
                        @Override
                        public void onCouponUsed() {
                            MyCouponListFragment usedFragment = fragments.get(TAB_USED);
                            if (usedFragment != null) {
                                usedFragment.refresh();
                            }
                        }
                    });
                    break;
                case TAB_EXPIRED:
                    fragment = MyCouponListFragment.newInstance(MyCouponListFragment.TYPE_EXPIRED);
                    break;
                case TAB_USED:
                    fragment = MyCouponListFragment.newInstance(MyCouponListFragment.TYPE_USED);
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
        startActivity(new Intent(this, MyCouponDescriptionActivity.class));
    }

    @Override
    public void onBackPressed() {
        MyCouponListFragment unUsedFragment = fragments.get(TAB_UNUSED);
        if (unUsedFragment != null) {
            Intent intent = getIntent();
            intent.putExtra("totalCount", unUsedFragment.getTotalCount());
            setResult(RESULT_OK, intent);
        }
        super.onBackPressed();
    }

    private String getPageTitleStr(int position, int totalCount) {
        switch (position) {
            case TAB_UNUSED:
                return getString(R.string.label_unused, totalCount);
            case TAB_EXPIRED:
                return getString(R.string.label_expired, totalCount);
            case TAB_USED:
                return getString(R.string.label_used, totalCount);
            default:
                return null;
        }
    }

    private void setTabText(int position, int totalCount) {
        indicator.setTabText(getPageTitleStr(position, totalCount), position);
    }

    public interface OnCouponUsedListener {
        void onCouponUsed();
    }

}