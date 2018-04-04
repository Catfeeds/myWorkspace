package com.hunliji.hljquestionanswer.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.widgets.TabPageIndicator;
import com.hunliji.hljquestionanswer.R;
import com.hunliji.hljquestionanswer.R2;
import com.hunliji.hljquestionanswer.fragments.MyAnswersFragment;
import com.hunliji.hljquestionanswer.fragments.MyFollowAnswersFragment;
import com.hunliji.hljquestionanswer.fragments.MyQuestionsFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Suncloud on 2016/8/26.
 */
public class MyQuestionAnswerActivity extends HljBaseNoBarActivity {

    @BindView(R2.id.indicator)
    TabPageIndicator indicator;
    @BindView(R2.id.view_pager)
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_answer___qa);
        ButterKnife.bind(this);
        setDefaultStatusBarPadding();
        MyQAPagerAdapter mSectionsPagerAdapter = new MyQAPagerAdapter(getSupportFragmentManager());
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(mSectionsPagerAdapter);
        indicator.setPagerAdapter(mSectionsPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
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

    private class MyQAPagerAdapter extends FragmentPagerAdapter {

        private MyQAPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return MyQuestionsFragment.newInstance();
                case 1:
                    return MyAnswersFragment.newInstance();
                case 2:
                    return MyFollowAnswersFragment.newInstance();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.label_question___qa).toUpperCase();
                case 1:
                    return getString(R.string.label_answer___qa).toUpperCase();
                case 2:
                    return getString(R.string.label_collect___qa).toUpperCase();
            }
            return null;
        }
    }

}
