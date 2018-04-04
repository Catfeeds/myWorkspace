package com.hunliji.marrybiz.view.coupon;

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
import com.hunliji.marrybiz.fragment.coupon.MyCouponListFragment;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 我的优惠券页
 * Created by chen_bin on 2016/10/13 0013.
 */
public class MyCouponListActivity extends HljBaseActivity implements TabPageIndicator
        .OnTabChangeListener {
    @BindView(R.id.indicator)
    TabPageIndicator indicator;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    private MyCouponListFragment allListFragment;
    private MyCouponListFragment forceListFragment;
    private MyCouponListFragment effectListFragment;
    private MyCouponListFragment expireListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_coupon_list);
        ButterKnife.bind(this);
        hideDividerView();
        setOkButton(R.drawable.icon_search_primary_46_44);
        SectionPagerAdapter pagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());
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

    public class SectionPagerAdapter extends FragmentPagerAdapter {

        SectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    allListFragment = MyCouponListFragment.newInstance(null, false, 0);
                    return allListFragment;
                case 1:
                    forceListFragment = MyCouponListFragment.newInstance(null, false, 1);
                    return forceListFragment;
                case 2:
                    effectListFragment = MyCouponListFragment.newInstance(null, false, 2);
                    return effectListFragment;
                case 3:
                    expireListFragment = MyCouponListFragment.newInstance(null, false, 3);
                    return expireListFragment;
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
                    return getString(R.string.label_all);
                case 1:
                    return getString(R.string.label_stay_in_force);
                case 2:
                    return getString(R.string.label_come_into_effect);
                case 3:
                    return getString(R.string.label_expired);
                default:
                    return null;
            }
        }
    }

    @Override
    public void onTabChanged(int position) {
        viewPager.setCurrentItem(position);
    }

    @Override
    public void onOkButtonClick() {
        startActivity(new Intent(this, CouponSearchActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    //提交
    @OnClick(R.id.btn_create_coupon)
    public void onCreateCoupon() {
        startActivityForResult(new Intent(this, CreateCouponActivity.class),
                Constants.RequestCode.CREATE_COUPON);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.RequestCode.CREATE_COUPON:
                    if (allListFragment != null) {
                        if (viewPager.getCurrentItem() == 0) {
                            allListFragment.refresh();
                        } else {
                            allListFragment.setNeedRefresh(true);
                        }
                    }
                    if (forceListFragment != null) {
                        if (viewPager.getCurrentItem() == 1) {
                            forceListFragment.refresh();
                        } else {
                            forceListFragment.setNeedRefresh(true);
                        }
                    }
                    if (effectListFragment != null) {
                        if (viewPager.getCurrentItem() == 2) {
                            effectListFragment.refresh();
                        } else {
                            effectListFragment.setNeedRefresh(true);
                        }
                    }
                    if (expireListFragment != null) {
                        if (viewPager.getCurrentItem() == 3) {
                            expireListFragment.refresh();
                        } else {
                            expireListFragment.setNeedRefresh(true);
                        }
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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
