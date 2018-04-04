package com.hunliji.hljquestionanswer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.hunliji.hljcommonlibrary.models.Mark;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljcommonlibrary.views.widgets.PullToRefreshScrollableLayout;
import com.hunliji.hljcommonlibrary.views.widgets.ScrollableHelper;
import com.hunliji.hljcommonlibrary.views.widgets.ScrollableLayout;
import com.hunliji.hljcommonlibrary.views.widgets.TabPageIndicator;
import com.hunliji.hljhttplibrary.utils.AuthUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljquestionanswer.R;
import com.hunliji.hljquestionanswer.R2;
import com.hunliji.hljquestionanswer.api.QuestionAnswerApi;
import com.hunliji.hljquestionanswer.fragments.MarkAnswersFragment;
import com.hunliji.hljquestionanswer.fragments.MarkQuestionsFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;

/**
 * Created by Suncloud on 2016/8/29.
 * 问答模块下的标签详情
 */
public class QAMarkDetailActivity extends HljBaseNoBarActivity implements PullToRefreshBase
        .OnRefreshListener<ScrollableLayout>, ScrollableLayout.OnScrollListener {

    @BindView(R2.id.indicator)
    TabPageIndicator indicator;
    @BindView(R2.id.view_pager)
    ViewPager viewPager;
    @BindView(R2.id.scrollable_layout)
    PullToRefreshScrollableLayout scrollableLayout;
    @BindView(R2.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.btn_bar_question)
    Button btnBarQuestion;

    private long id;
    private boolean isShowHot;

    private Subscriber infoSubscriber;
    private MarkAnswersFragment answersFragment;
    private MarkQuestionsFragment questionsFragment;

    private final int QUESTION_CREATE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        id = getIntent().getLongExtra("id", 0);
        isShowHot = getIntent().getBooleanExtra("isShowHot", false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_detail___qa);
        ButterKnife.bind(this);
        setDefaultStatusBarPadding();
        scrollableLayout.setOnRefreshListener(this);
        scrollableLayout.getRefreshableView()
                .setOnScrollListener(this);
        infoSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setProgressBar(progressBar)
                .setEmptyView(emptyView)
                .setContentView(scrollableLayout)
                .setPullToRefreshBase(scrollableLayout)
                .setOnNextListener(new SubscriberOnNextListener<Mark>() {

                    @Override
                    public void onNext(Mark mark) {
                        scrollableLayout.setVisibility(View.VISIBLE);
                        scrollableLayout.getRefreshableView()
                                .setVisibility(View.VISIBLE);
                        initMarkInfo(mark);
                        initViewPager();
                    }
                })
                .build();
        QuestionAnswerApi.getMaskObb(id)
                .subscribe(infoSubscriber);
    }

    @OnClick(R2.id.btn_back)
    void onBack() {
        onBackPressed();
    }

    @OnClick(R2.id.btn_bar_question)
    public void onCreateQuestion() {
        if (!AuthUtil.loginBindCheck(this)) {
            return;
        }
        Intent intent = new Intent(this, CreateQuestionTitleActivity.class);
        intent.putExtra("markId", id);
        startActivityForResult(intent, QUESTION_CREATE);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    @Override
    public void onRefresh(PullToRefreshBase<ScrollableLayout> refreshView) {
        if (infoSubscriber == null || infoSubscriber.isUnsubscribed()) {
            infoSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setPullToRefreshBase(scrollableLayout)
                    .setOnNextListener(new SubscriberOnNextListener<Mark>() {

                        @Override
                        public void onNext(Mark mark) {
                            initMarkInfo(mark);
                        }
                    })
                    .build();
            QuestionAnswerApi.getMaskObb(id)
                    .subscribe(infoSubscriber);
            if (answersFragment != null) {
                answersFragment.refresh(true);
            }
            if (questionsFragment != null) {
                questionsFragment.refresh();
            }
        }
    }

    private void initMarkInfo(Mark mark) {
        tvTitle.setText(mark.getName());
    }

    private void initViewPager() {
        MyQAPagerAdapter mSectionsPagerAdapter = new MyQAPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mSectionsPagerAdapter);
        indicator.setPagerAdapter(mSectionsPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                indicator.setCurrentItem(position);
                Fragment fragment = ((MyQAPagerAdapter) viewPager.getAdapter()).getItem(position);
                if (fragment != null && fragment instanceof ScrollableHelper.ScrollableContainer) {
                    scrollableLayout.getRefreshableView()
                            .getHelper()
                            .setCurrentScrollableContainer((ScrollableHelper.ScrollableContainer)
                                    fragment);
                }
                super.onPageSelected(position);
            }
        });
        indicator.setOnTabChangeListener(new TabPageIndicator.OnTabChangeListener() {
            @Override
            public void onTabChanged(int position) {
                viewPager.setCurrentItem(position);
            }
        });
        if (isShowHot) {
            viewPager.setCurrentItem(0);
            indicator.setCurrentItem(0);
        }
    }

    @Override
    public void onScroll(int currentY, int maxY) {
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
                    if (answersFragment == null) {
                        answersFragment = MarkAnswersFragment.newInstance(id);
                        if (position == viewPager.getCurrentItem()) {
                            scrollableLayout.getRefreshableView()
                                    .getHelper()
                                    .setCurrentScrollableContainer(answersFragment);
                        }
                    }
                    return answersFragment;
                case 1:
                    if (questionsFragment == null) {
                        questionsFragment = MarkQuestionsFragment.newInstance(id);
                        if (position == viewPager.getCurrentItem()) {
                            scrollableLayout.getRefreshableView()
                                    .getHelper()
                                    .setCurrentScrollableContainer(questionsFragment);
                        }
                    }
                    return questionsFragment;
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
                    return getString(R.string.label_hot_answer___qa).toUpperCase();
                case 1:
                    return getString(R.string.label_all_question___qa).toUpperCase();
            }
            return null;
        }
    }

    @Override
    protected void onFinish() {
        if (infoSubscriber != null && !infoSubscriber.isUnsubscribed()) {
            infoSubscriber.unsubscribe();
        }
        super.onFinish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case QUESTION_CREATE:
                    onRefresh(null);
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
