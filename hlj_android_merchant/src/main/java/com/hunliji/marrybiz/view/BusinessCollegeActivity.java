package com.hunliji.marrybiz.view;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljcommonlibrary.views.widgets.TabPageIndicator;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.adapter.PosterFlowAdapter;
import com.hunliji.marrybiz.api.college.CollegeApi;
import com.hunliji.marrybiz.fragment.market.BusinessCollegeFragment;
import com.hunliji.marrybiz.model.college.CollegeItem;
import com.slider.library.Indicators.CirclePageExIndicator;
import com.slider.library.SliderLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BusinessCollegeActivity extends HljBaseActivity {

    @BindView(R.id.view_flow)
    SliderLayout viewFlow;
    @BindView(R.id.flow_indicator)
    CirclePageExIndicator flowIndicator;
    @BindView(R.id.banner_layout)
    RelativeLayout bannerLayout;
    @BindView(R.id.tab_page_indicator)
    TabPageIndicator tabPageIndicator;
    @BindView(R.id.tab_layout)
    FrameLayout tabLayout;
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private int bannerHeight;
    private HljHttpSubscriber posterSub;
    private BusinessCollegeFragment fineFragment;
    private BusinessCollegeFragment marketFragment;
    private PosterFlowAdapter flowAdapter;
    private CollegeFragmentAdapter fragmentAdapter;
    private List<Poster> posters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schoolactivity);
        ButterKnife.bind(this);

        initValues();
        initViews();
        initLoad();
    }

    private void initValues() {
        posters = new ArrayList<>();
        bannerHeight = CommonUtil.getDeviceSize(this).x * 9 / 16;
        viewFlow.getLayoutParams().height = bannerHeight;
        flowAdapter = new PosterFlowAdapter(this, posters, R.layout.poster_flow_item);
        fragmentAdapter = new CollegeFragmentAdapter(getSupportFragmentManager());
    }

    private void initViews() {
        viewFlow.setPagerAdapter(flowAdapter);
        flowAdapter.setSliderLayout(viewFlow);
        viewFlow.setCustomIndicator(flowIndicator);
        if (flowAdapter.getCount() > 0) {
            bannerLayout.setVisibility(View.VISIBLE);
            if (flowAdapter.getCount() > 1) {
                viewFlow.startAutoCycle();
            }
        }
        appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                BusinessCollegeFragment fragment = (BusinessCollegeFragment) fragmentAdapter
                        .getItem(
                        viewPager.getCurrentItem());
                PullToRefreshVerticalRecyclerView view = (PullToRefreshVerticalRecyclerView)
                        fragment.getScrollableView();
                if (view != null) {
                    view.setMode(verticalOffset == 0 ? PullToRefreshBase.Mode.PULL_FROM_START :
                            PullToRefreshBase.Mode.DISABLED);
                }
            }
        });
    }

    private void initLoad() {
        CommonUtil.unSubscribeSubs(posterSub);
        posterSub = HljHttpSubscriber.buildSubscriber(this)
                .setDataNullable(true)
                .setProgressBar(progressBar)
                .setDataNullable(true)
                .setOnNextListener(new SubscriberOnNextListener<List<Poster>>() {
                    @Override
                    public void onNext(List<Poster> posters) {
                        if (!CommonUtil.isCollectionEmpty(posters)) {
                            setPosters(posters);
                        } else {
                            bannerLayout.setVisibility(View.GONE);
                        }
                        setViewPager();
                    }
                })
                .build();

        CollegeApi.getCollegeBanners()
                .subscribe(posterSub);
    }

    private void setPosters(List<Poster> list) {
        posters.clear();
        posters.addAll(list);
        flowAdapter.setmDate(posters);
        if (!posters.isEmpty()) {
            bannerLayout.setVisibility(View.VISIBLE);
            if (flowAdapter.getCount() == 0 || posters.size() == 0) {
                viewFlow.stopAutoCycle();
            } else if (flowAdapter.getCount() > 1) {
                viewFlow.startAutoCycle();
            }
        } else {
            bannerLayout.setVisibility(View.GONE);
        }
    }

    private void setViewPager() {
        tabLayout.setVisibility(View.VISIBLE);
        viewPager.setAdapter(fragmentAdapter);
        tabPageIndicator.setPagerAdapter(fragmentAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabPageIndicator.setCurrentItem(position);
            }
        });
        tabPageIndicator.setOnTabChangeListener(new TabPageIndicator.OnTabChangeListener() {
            @Override
            public void onTabChanged(int position) {
                viewPager.setCurrentItem(position);
            }
        });
        tabPageIndicator.setCurrentItem(0);
        viewPager.setCurrentItem(0);
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    @Override
    protected void onFinish() {
        CommonUtil.unSubscribeSubs(posterSub);
        super.onFinish();
    }

    private class CollegeFragmentAdapter extends FragmentStatePagerAdapter {
        public CollegeFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            BusinessCollegeFragment fragment;
            if (position == 0) {
                if (fineFragment == null) {
                    fineFragment = BusinessCollegeFragment.newInstance(CollegeItem.TYPE_FINE_CLASS);
                }
                fragment = fineFragment;
            } else {
                if (marketFragment == null) {
                    marketFragment = BusinessCollegeFragment.newInstance(CollegeItem
                            .TYPE_MARKET_CLASS);
                }
                fragment = marketFragment;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return "精品课程";
            } else {
                return "运营攻略";
            }
        }
    }
}
