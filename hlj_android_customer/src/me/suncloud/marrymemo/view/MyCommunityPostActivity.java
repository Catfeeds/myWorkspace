package me.suncloud.marrymemo.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.fragment.UserPostThreadFragment;
import me.suncloud.marrymemo.fragment.UserThreadFragment;
import me.suncloud.marrymemo.widget.TabPageIndicator;

/**
 * Created by mo_yu on 2016/5/11. 新版-我的话题
 */
public class MyCommunityPostActivity extends HljBaseNoBarActivity implements TabPageIndicator
        .OnTabChangeListener {
    @BindView(R.id.tv_title)
    TextView title;
    @BindView(R.id.indicator)
    TabPageIndicator indicator;
    @BindView(R.id.pager)
    ViewPager mViewPager;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    private UserThreadFragment publishThreadFragment;
    private UserThreadFragment collectThreadFragment;
    private UserPostThreadFragment postThreadFragment;

    private SectionPagerAdapter sectionPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_community_post);
        ButterKnife.bind(this);
        setDefaultStatusBarPadding();

        title = (TextView) findViewById(R.id.tv_title);
        title.setText(getString(R.string.label_my_thread));
        sectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());
        indicator.setTabViewId(R.layout.menu_tab_widget2);
        indicator.setPagerAdapter(sectionPagerAdapter);
        mViewPager.setAdapter(sectionPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                indicator.setCurrentItem(position);
            }
        });
        mViewPager.setOffscreenPageLimit(2);
        indicator.setOnTabChangeListener(this);
        mViewPager.setCurrentItem(0);
    }

    public void onBackPressed(View view) {
        super.onBackPressed();
    }

    @Override
    public void onTabChanged(int position) {
        mViewPager.setCurrentItem(position);
    }

    public class SectionPagerAdapter extends FragmentPagerAdapter {

        public SectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                default:
                    if (publishThreadFragment == null) {
                        publishThreadFragment = UserThreadFragment.newInstance(UserThreadFragment
                                .TYPE_PUBLISH_THREAD);
                    }
                    return publishThreadFragment;
                case 1:
                    if (postThreadFragment == null) {
                        postThreadFragment = UserPostThreadFragment.newInstance();
                    }
                    return postThreadFragment;
                case 2:
                    if (collectThreadFragment == null) {
                        collectThreadFragment = UserThreadFragment.newInstance(UserThreadFragment
                                .TYPE_COLLECT_THREAD);
                    }
                    return collectThreadFragment;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                default:
                    return getString(R.string.label_upload).toUpperCase();
                case 1:
                    return getString(R.string.label_return).toUpperCase();
                case 2:
                    return getString(R.string.label_collect).toUpperCase();
            }
        }
    }
}
