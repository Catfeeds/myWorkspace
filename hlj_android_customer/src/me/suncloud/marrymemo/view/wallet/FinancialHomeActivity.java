package me.suncloud.marrymemo.view.wallet;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.City;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.models.modelwrappers.PosterData;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.LocationSession;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.fragments.ScrollAbleFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljcommonlibrary.views.widgets.TabPageIndicator;
import com.hunliji.hljhttplibrary.api.CommonApi;
import com.hunliji.hljhttplibrary.utils.GsonUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.PosterUtil;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljpushlibrary.utils.PushUtil;
import com.slider.library.Indicators.CirclePageExIndicator;
import com.slider.library.SliderLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.FlowAdapter;
import me.suncloud.marrymemo.fragment.wallet.FinancialProductsFragment;

public class FinancialHomeActivity extends HljBaseActivity {

    @Override
    public String pageTrackTagName() {
        return "金融超市";
    }

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

    private HljHttpSubscriber posterSub;
    private FlowAdapter flowAdapter;
    private ArrayList<Poster> posters;
    private int bannerHeight;

    private FinancialProductsFragment loanFragment;
    private FinancialProductsFragment bankcardFragment;
    private FinancialFragmentAdapter fragmentAdapter;
    private City city;
    private int verticalOffset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_financial_home);
        ButterKnife.bind(this);

        initValues();
        initViews();
        initLoad();
    }

    private void initValues() {
        city = LocationSession.getInstance()
                .getCity(this);
        posters = new ArrayList<>();
        bannerHeight = CommonUtil.getDeviceSize(this).x * 180 / 375;
        viewFlow.getLayoutParams().height = bannerHeight;
        flowAdapter = new FlowAdapter(this, posters,  R.layout.flow_item);
        fragmentAdapter = new FinancialFragmentAdapter(getSupportFragmentManager());
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
            public void onOffsetChanged(AppBarLayout appBarLayout, int vo) {
                verticalOffset = vo;
                setPTRMode();
            }
        });
    }

    private void setPTRMode() {
        ScrollAbleFragment fragment = (ScrollAbleFragment) fragmentAdapter.getItem(viewPager
                .getCurrentItem());
        PullToRefreshVerticalRecyclerView view = (PullToRefreshVerticalRecyclerView) fragment
                .getScrollableView();
        if (view != null) {
            view.setMode(verticalOffset == 0 ? PullToRefreshBase.Mode.PULL_FROM_START :
                    PullToRefreshBase.Mode.DISABLED);
        }
    }

    private void initLoad() {
        CommonUtil.unSubscribeSubs(posterSub);
        posterSub = HljHttpSubscriber.buildSubscriber(this)
                .setDataNullable(true)
                .setProgressBar(progressBar)
                .setOnNextListener(new SubscriberOnNextListener<PosterData>() {
                    @Override
                    public void onNext(PosterData posterData) {
                        setPosters(PosterUtil.getPosterList(posterData.getFloors(),
                                "SITE_FINANCIAL_TOP_V2_BANNER",
                                false));
                        setPosterDlg(PosterUtil.getPosterList(posterData.getFloors(),
                                "SITE_FINANCIAL_POP_WINDOW_BANNER",
                                false));
                        setViewPager();
                    }
                })
                .build();
        CommonApi.getBanner(this, HljCommon.BLOCK_ID.FinancialMarketListActivity, city.getCid())
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

    private void setPosterDlg(List<Poster> list) {
        SharedPreferences preferences = getSharedPreferences(HljCommon.FileNames.PREF_FILE,
                Context.MODE_PRIVATE);
        String idString = preferences.getString(HljCommon.SharedPreferencesNames
                        .FINANCIAL_HOME_POSTER_IDS,
                null);
        if (CommonUtil.isCollectionEmpty(list)) {
            return;
        }
        Poster poster = list.get(0);
        List<Long> ids = new ArrayList<>();
        try {
            if (!TextUtils.isEmpty(idString)) {
                ids = GsonUtil.getGsonInstance()
                        .fromJson(idString, new TypeToken<List<Long>>() {}.getType());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!ids.contains(poster.getId())) {
            ids.add(poster.getId());
            showPosterDlg(poster);
            preferences.edit()
                    .putString(HljCommon.SharedPreferencesNames.FINANCIAL_HOME_POSTER_IDS,
                            GsonUtil.getGsonInstance()
                                    .toJson(ids))
                    .apply();
        }
    }

    private void showPosterDlg(Poster poster) {
        Dialog dialog = PushUtil.createNotifyPostDlg(this, poster, null, null);
        dialog.show();
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
                setPTRMode();
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

    private class FinancialFragmentAdapter extends FragmentStatePagerAdapter {

        public FinancialFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            ScrollAbleFragment fragment;
            switch (position) {
                case 1:
                    if (bankcardFragment == null) {
                        bankcardFragment = FinancialProductsFragment.newInstance(
                                FinancialProductsFragment.FINANCIAL_TYPE_CREDIT_CARD);
                    }
                    fragment = bankcardFragment;
                    break;
                default:
                    if (loanFragment == null) {
                        loanFragment = FinancialProductsFragment.newInstance(
                                FinancialProductsFragment.FINANCIAL_TYPE_PRODUCT);
                    }
                    fragment = loanFragment;
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return position == 0 ? "新婚贷" : "信用卡";
        }
    }

}
