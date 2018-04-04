package me.suncloud.marrymemo.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.OnTabTextChangeListener;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.widgets.TabPageIndicator;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.fragment.FollowHotelFragment;
import me.suncloud.marrymemo.fragment.FollowMarkFragment;
import me.suncloud.marrymemo.fragment.FollowMerchantFragment;
import me.suncloud.marrymemo.fragment.FollowUserFragment;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.util.Session;

/**
 * Created by Suncloud on 2016/2/26.
 */
public class FollowActivity extends HljBaseNoBarActivity implements TabPageIndicator
        .OnTabChangeListener {

    @BindView(R.id.indicator)
    TabPageIndicator indicator;
    @BindView(R.id.pager)
    ViewPager mViewPager;

    private SparseArray<RefreshFragment> fragments;
    private long userId;
    private boolean isHim;

    public final static int TAB_MARK = 0; //关注的标签tab
    public final static int TAB_MERCHANT = 1; //关注的商家tab
    public final static int TAB_USER = 2; // 关注的用户tab
    public final static int TAB_HOTEL = 3; //关注的酒店tab

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);
        ButterKnife.bind(this);
        setDefaultStatusBarPadding();
        fragments = new SparseArray<>();
        userId = getIntent().getLongExtra("userId", 0);
        findViewById(R.id.btn_filter).setVisibility(View.GONE);
        TextView tvTitle = (TextView) findViewById(R.id.tv_title);
        SectionsPagerAdapter pagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                indicator.setCurrentItem(position);
            }
        });
        indicator.setPagerAdapter(pagerAdapter);
        indicator.setOnTabChangeListener(this);
        User my = Session.getInstance()
                .getCurrentUser();
        if (userId == 0 && my != null) {
            userId = my.getId();
        }
        isHim = my == null || (userId > 0 && my.getId() != userId);
        tvTitle.setText(isHim ? R.string.title_activity_follow1 : R.string.title_activity_follow);
    }

    public void onBackPressed(View v) {
        onBackPressed();
    }

    @Override
    public void onTabChanged(int position) {
        mViewPager.setCurrentItem(position);
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
                case TAB_MARK:
                    fragment = FollowMarkFragment.newInstance(userId);
                    break;
                case TAB_MERCHANT:
                    fragment = FollowMerchantFragment.newInstance(userId,
                            isHim ? R.string.hint_ta_collect_merchant_empty : 0);
                    break;
                case TAB_USER:
                    fragment = FollowUserFragment.newInstance(userId,
                            isHim ? R.string.hint_ta_empty_followd : 0,
                            0);
                    break;
                case TAB_HOTEL:
                    fragment = FollowHotelFragment.newInstance(userId,
                            isHim ? R.string.hint_ta_collect_hotel_empty : 0);
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
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getPageTitleStr(position, 0);
        }
    }

    private String getPageTitleStr(int position, int totalCount) {
        switch (position) {
            case TAB_MARK:
                return getString(R.string.label_mark, totalCount);
            case TAB_MERCHANT:
                return getString(R.string.label_collect_merchant_count, totalCount);
            case TAB_USER:
                return getString(R.string.label_collect_user_count, totalCount);
            case TAB_HOTEL:
                return getString(R.string.label_collect_hotel_count, totalCount);
            default:
                return null;
        }
    }

    private void setTabText(int position, int totalCount) {
        indicator.setTabText(getPageTitleStr(position, totalCount), position);
    }

}
