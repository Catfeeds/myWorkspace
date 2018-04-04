package com.hunliji.marrybiz.view.notification;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.hunliji.hljcommonlibrary.models.realm.Notification;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.widgets.TabPageIndicator;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.fragment.notification.CommunityCommonFragment;
import com.hunliji.marrybiz.fragment.notification.CommunityPraiseFragment;
import com.hunliji.marrybiz.util.Session;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

/**
 * Created by wangtao on 2016/11/22.
 */

public class CommunityNotificationActivity extends HljBaseNoBarActivity {

    @BindView(R.id.indicator)
    TabPageIndicator indicator;
    @BindView(R.id.view_pager)
    ViewPager viewPager;

    private CommunityCommonFragment communityCommonFragment;
    private CommunityPraiseFragment communityPraiseFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_notification);
        ButterKnife.bind(this);
        setDefaultStatusBarPadding();

        CommunityPagerAdapter mSectionsPagerAdapter = new CommunityPagerAdapter(
                getSupportFragmentManager());
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
        long userId = Session.getInstance()
                .getCurrentUser(this)
                .getId();
        Realm realm = Realm.getDefaultInstance();
        long count1= (int) realm.where(Notification.class)
                .equalTo("userId", userId)
                .notEqualTo("status", 2)
                .equalTo("notifyType", Notification.NotificationType.COMMUNITY)
                .not()
                .in("action", CommunityPraiseFragment.COMMUNITY_PRAISE_ACTIONS)
                .count();
        long count2= (int) realm.where(Notification.class)
                .equalTo("userId", userId)
                .notEqualTo("status", 2)
                .equalTo("notifyType", Notification.NotificationType.COMMUNITY)
                .in("action", CommunityPraiseFragment.COMMUNITY_PRAISE_ACTIONS)
                .count();
        realm.close();
        if(count1==0&&count2>0){
            viewPager.setCurrentItem(1);
        }
    }

    public void onBackPressed(View view) {
        onBackPressed();
    }

    @OnClick(R.id.tv_read_all)
    public void onAllRead() {
        switch (viewPager.getCurrentItem()) {
            case 0:
                communityCommonFragment.onReadAll();
                break;
            case 1:
                communityPraiseFragment.onReadAll(null);
                break;
        }

    }

    private class CommunityPagerAdapter extends FragmentPagerAdapter {

        private CommunityPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    if (communityCommonFragment == null) {
                        communityCommonFragment = CommunityCommonFragment.newInstance();
                    }
                    return communityCommonFragment;
                case 1:
                    if (communityPraiseFragment == null) {
                        communityPraiseFragment = CommunityPraiseFragment.newInstance();
                    }
                    return communityPraiseFragment;
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
                    return getString(R.string.label_notify_type_default).toUpperCase();
                case 1:
                    return getString(R.string.label_praise).toUpperCase();
            }
            return null;
        }
    }
}
