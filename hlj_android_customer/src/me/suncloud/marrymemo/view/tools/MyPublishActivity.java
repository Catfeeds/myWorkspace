package me.suncloud.marrymemo.view.tools;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;

import com.hunliji.hljcommonlibrary.adapters.OnTabTextChangeListener;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.widgets.TabPageIndicator;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljnotelibrary.views.fragments.MyNoteListFragment;
import com.hunliji.hljquestionanswer.fragments.MyAnswersFragment;
import com.hunliji.hljquestionanswer.fragments.MyQuestionsFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.user.UserApi;
import me.suncloud.marrymemo.fragment.UserPostThreadFragment;
import me.suncloud.marrymemo.fragment.UserThreadFragment;
import me.suncloud.marrymemo.model.user.CountStatistics;
import me.suncloud.marrymemo.util.Session;

/**
 * 我的发布
 * Created by chen_bin on 2017/11/6 0006.
 */
public class MyPublishActivity extends HljBaseActivity implements TabPageIndicator
        .OnTabChangeListener {

    @BindView(R.id.indicator)
    TabPageIndicator indicator;
    @BindView(R.id.view_pager)
    ViewPager viewPager;

    private SparseArray<RefreshFragment> fragments;
    private HljHttpSubscriber getStatisticsSub;

    public final static int TAB_NOTE = 0; //笔记tab
    public final static int TAB_THREAD = 1;//话题tab
    public final static int TAB_POST_THREAD = 2; //回帖tab
    public final static int TAB_QUESTION = 3; //提问tab
    public final static int TAB_ANSWER = 4; //问答tab

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hlj_common_tab_view_pager___cm);
        ButterKnife.bind(this);
        hideDividerView();
        initValues();
        initViews();
        getCountStatistics();
    }

    private void initValues() {
        fragments = new SparseArray<>();
    }

    private void initViews() {
        SectionsPagerAdapter pagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                indicator.setCurrentItem(position);
            }
        });
        indicator.setOnTabChangeListener(this);
        indicator.setPagerAdapter(pagerAdapter);
    }

    private void getCountStatistics() {
        if (getStatisticsSub == null || getStatisticsSub.isUnsubscribed()) {
            getStatisticsSub = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<CountStatistics>() {
                        @Override
                        public void onNext(CountStatistics statistics) {
                            setTabText(TAB_NOTE, statistics.getNoteCount());
                            setTabText(TAB_THREAD, statistics.getThreadCount());
                            setTabText(TAB_POST_THREAD, statistics.getPostCount());
                            setTabText(TAB_QUESTION, statistics.getQuestionCount());
                            setTabText(TAB_ANSWER, statistics.getAnswerCount());
                        }
                    })
                    .build();
            UserApi.getCountStatisticsObb(CountStatistics.TYPE_COMMUNITY)
                    .subscribe(getStatisticsSub);
        }
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        private SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(final int position) {
            RefreshFragment fragment = fragments.get(position);
            if (fragment != null) {
                return fragment;
            }
            switch (position) {
                case TAB_NOTE:
                    fragment = MyNoteListFragment.newInstance(MyNoteListFragment.TYPE_MY_NOTE,
                            Session.getInstance()
                                    .getCurrentUser(MyPublishActivity.this)
                                    .getId());
                    break;
                case TAB_THREAD:
                    fragment = UserThreadFragment.newInstance(UserThreadFragment
                            .TYPE_PUBLISH_THREAD);
                    break;
                case TAB_POST_THREAD:
                    fragment = UserPostThreadFragment.newInstance();
                    break;
                case TAB_QUESTION:
                    fragment = MyQuestionsFragment.newInstance();
                    break;
                case TAB_ANSWER:
                    fragment = MyAnswersFragment.newInstance();
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
            return 5;
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

    private String getPageTitleStr(int position, int totalCount) {
        switch (position) {
            case TAB_NOTE:
                return getString(R.string.label_publish_note_count, totalCount);
            case TAB_THREAD:
                return getString(R.string.label_publish_thread_count, totalCount);
            case TAB_POST_THREAD:
                return getString(R.string.label_publish_post_thread_count, totalCount);
            case TAB_QUESTION:
                return getString(R.string.label_publish_question_count, totalCount);
            case TAB_ANSWER:
                return getString(R.string.label_publish_answer_count, totalCount);
            default:
                return null;
        }
    }

    private void setTabText(int position, int totalCount) {
        indicator.setTabText(getPageTitleStr(position, totalCount), position);
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(getStatisticsSub);
    }
}
