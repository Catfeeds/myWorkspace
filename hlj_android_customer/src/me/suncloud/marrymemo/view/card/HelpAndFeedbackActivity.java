package me.suncloud.marrymemo.view.card;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.TabPageIndicator;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.fragment.FeedBackFragment;
import me.suncloud.marrymemo.fragment.card.HelpFragment;
import me.suncloud.marrymemo.util.DataConfig;
import me.suncloud.marrymemo.util.Session;

/**
 * Created by wangtao on 2017/4/26.
 */

public class HelpAndFeedbackActivity extends HljBaseActivity {

    @BindView(R.id.indicator)
    TabPageIndicator indicator;
    @BindView(R.id.view_pager)
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_help_and_feedback);
        ButterKnife.bind(this);
        HelpAndFeedbackPagerAdapter pagerAdapter = new HelpAndFeedbackPagerAdapter(
                getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        indicator.setPagerAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                indicator.setCurrentItem(position);
                hideKeyboard(null);
                super.onPageSelected(position);
            }
        });
        indicator.setOnTabChangeListener(new TabPageIndicator.OnTabChangeListener() {
            @Override
            public void onTabChanged(int position) {
                viewPager.setCurrentItem(position);
            }
        });
        hideDividerView();
    }

    private class HelpAndFeedbackPagerAdapter extends FragmentPagerAdapter {

        private HelpAndFeedbackPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    DataConfig dataConfig = Session.getInstance()
                            .getDataConfig(HelpAndFeedbackActivity.this);
                    String path = null;
                    if (dataConfig != null && !TextUtils.isEmpty(dataConfig.getEcardFaqUrl())) {
                        path = dataConfig.getEcardFaqUrl();
                    }
                    return HelpFragment.newInstance(path);
                case 1:
                    return FeedBackFragment.newInstance();
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
                    return getString(R.string.label_card_fnq).toUpperCase();
                case 1:
                    return getString(R.string.settings_feedback).toUpperCase();
            }
            return null;
        }
    }
}
