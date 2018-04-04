package com.hunliji.hljquestionanswer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljcommonlibrary.views.widgets.PullToRefreshScrollableLayout;
import com.hunliji.hljcommonlibrary.views.widgets.ScrollableHelper;
import com.hunliji.hljcommonlibrary.views.widgets.ScrollableLayout;
import com.hunliji.hljcommonlibrary.views.widgets.TabPageIndicator;
import com.hunliji.hljquestionanswer.R;
import com.hunliji.hljquestionanswer.R2;
import com.hunliji.hljquestionanswer.fragments.WeekAnswersFragment;
import com.hunliji.hljquestionanswer.fragments.WeekQuestionsFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mo_yu on 2016/1/3.本周热门回答和详情
 */
public class WeekQaActivity extends HljBaseNoBarActivity implements PullToRefreshBase
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
    @BindView(R2.id.iv_week_bg)
    ImageView ivWeekBg;
    @BindView(R2.id.tv_title_w)
    TextView tvTitleW;
    @BindView(R2.id.shadow_view)
    RelativeLayout shadowView;
    @BindView(R2.id.action_layout)
    RelativeLayout actionLayout;
    @BindView(R2.id.action_holder_layout)
    LinearLayout actionHolderLayout;
    @BindView(R2.id.action_holder_layout2)
    LinearLayout actionHolderLayout2;

    private int height;
    private int headImgHeight;

    private WeekAnswersFragment answersFragment;
    private WeekQuestionsFragment questionsFragment;

    private final int QUESTION_CREATE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week___qa);
        ButterKnife.bind(this);
        setActionBarPadding(actionHolderLayout, actionHolderLayout2);
        headImgHeight = CommonUtil.getDeviceSize(this).x * 7 / 16;
        height = CommonUtil.dp2px(this, 45) + getStatusBarHeight();
        ivWeekBg.getLayoutParams().height = headImgHeight;
        shadowView.setAlpha(1);
        scrollableLayout.setMode(PullToRefreshBase.Mode.DISABLED);
        scrollableLayout.setOnRefreshListener(this);
        scrollableLayout.getRefreshableView()
                .setOnScrollListener(this);
        scrollableLayout.setVisibility(View.VISIBLE);
        scrollableLayout.getRefreshableView()
                .setVisibility(View.VISIBLE);
        initViewPager();
    }

    @Override
    public void onRefresh(PullToRefreshBase<ScrollableLayout> refreshView) {
        if (answersFragment != null) {
            answersFragment.refresh();
        }
        if (questionsFragment != null) {
            questionsFragment.refresh();
        }
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
        scrollableLayout.getRefreshableView()
                .setExtraHeight(height);
        scrollableLayout.getRefreshableView()
                .setHeaderView(ivWeekBg);
        scrollableLayout.getRefreshableView()
                .addOnScrollListener(new ScrollableLayout.OnScrollListener() {
                    @Override
                    public void onScroll(int currentY, int maxY) {
                        scrollableLayout.getRefreshableView()
                                .getHelper()
                                .setCurrentScrollableContainer(getCurrentScrollableContainer());
                        if (currentY > maxY) {
                            actionHolderLayout2.setAlpha(1);
                            shadowView.setAlpha(0);
                        } else {
                            float f = (float) currentY / maxY;
                            actionHolderLayout2.setAlpha(f);
                            shadowView.setAlpha(1 - f);
                        }
                    }
                });
    }

    @Override
    public void onScroll(int currentY, int maxY) {
    }

    public void onBackPressed(View view) {
        onBackPressed();
    }

    @OnClick({R2.id.btn_back_w, R2.id.btn_back})
    public void onClick(View view) {
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
                        answersFragment = WeekAnswersFragment.newInstance();
                    }
                    return answersFragment;
                case 1:
                    if (questionsFragment == null) {
                        questionsFragment = WeekQuestionsFragment.newInstance();
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
                    return getString(R.string.label_answer___qa).toUpperCase();
                case 1:
                    return getString(R.string.title_question___qa).toUpperCase();
            }
            return null;
        }
    }

    @Override
    protected void onFinish() {
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

    /**
     * 获取当前fragment
     *
     * @return
     */
    private ScrollableHelper.ScrollableContainer getCurrentScrollableContainer() {
        if (viewPager.getAdapter() != null && viewPager.getAdapter() instanceof MyQAPagerAdapter) {
            MyQAPagerAdapter adapter = (MyQAPagerAdapter) viewPager.getAdapter();
            Fragment fragment = (Fragment) adapter.instantiateItem(viewPager,
                    viewPager.getCurrentItem());
            if (fragment != null && fragment instanceof ScrollableHelper.ScrollableContainer) {
                return (ScrollableHelper.ScrollableContainer) fragment;
            }
        }
        return null;
    }
}
