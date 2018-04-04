package me.suncloud.marrymemo.view.notification;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.widgets.TabPageIndicator;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.fragment.notification.CommunityCommonFragment;
import me.suncloud.marrymemo.fragment.notification.CommunityPraiseFragment;

/**
 * Created by Suncloud on 2016/9/8.
 */
public class CommunityNotificationActivity extends HljBaseNoBarActivity {

    @BindView(R.id.indicator)
    TabPageIndicator indicator;
    @BindView(R.id.view_pager)
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_notification);
        ButterKnife.bind(this);
        setDefaultStatusBarPadding();

        CommunityPagerAdapter mSectionsPagerAdapter = new CommunityPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mSectionsPagerAdapter);
        indicator.setPagerAdapter(mSectionsPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                indicator.setCurrentItem(position);
                super.onPageSelected(position);
            }
        });
        indicator.setOnTabChangeListener(new TabPageIndicator.OnTabChangeListener() {
            @Override
            public void onTabChanged(int position) {
                viewPager.setCurrentItem(position);
            }
        });
    }

    public void onBackPressed(View view) {
        onBackPressed();
    }


    private class CommunityPagerAdapter extends FragmentPagerAdapter {

        private CommunityPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return CommunityCommonFragment.newInstance();
                case 1:
                    return CommunityPraiseFragment.newInstance();
            }

            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.notification).toUpperCase();
                case 1:
                    return getString(R.string.label_praise).toUpperCase();
            }
            return null;
        }
    }
}
