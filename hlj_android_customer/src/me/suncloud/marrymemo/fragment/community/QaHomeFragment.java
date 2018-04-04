package me.suncloud.marrymemo.fragment.community;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.City;
import com.hunliji.hljcommonlibrary.models.Mark;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.models.modelwrappers.PosterData;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.LocationSession;
import com.hunliji.hljcommonlibrary.views.fragments.ScrollAbleFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljcommonlibrary.views.widgets.TabPageIndicator;
import com.hunliji.hljhttplibrary.entities.HljHttpCountData;
import com.hunliji.hljhttplibrary.entities.HljHttpResultZip;
import com.hunliji.hljhttplibrary.entities.HljRZField;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.PosterUtil;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljquestionanswer.api.QuestionAnswerApi;
import com.slider.library.Indicators.CirclePageExIndicator;
import com.slider.library.SliderLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.FlowAdapter;
import me.suncloud.marrymemo.api.community.CommunityApi;
import me.suncloud.marrymemo.model.community.PosterWatchFeed;
import rx.Observable;
import rx.Subscription;
import rx.functions.Func2;

/**
 * Created by luohanlin on 2017/10/23.
 */

public class QaHomeFragment extends ScrollAbleFragment implements PullToRefreshBase
        .OnRefreshListener<RecyclerView> {

    @Override
    public String fragmentPageTrackTagName() {
        return "问答列表";
    }

    Unbinder unbinder;
    @BindView(R.id.view_flow)
    SliderLayout viewFlow;
    @BindView(R.id.flow_indicator)
    CirclePageExIndicator flowIndicator;
    @BindView(R.id.banner_layout)
    RelativeLayout bannerLayout;
    @BindView(R.id.tab_page_indicator)
    TabPageIndicator tabPageIndicator;
    @BindView(R.id.tv_tab_title)
    TextView tvTabTitle;
    @BindView(R.id.tab_layout)
    FrameLayout tabLayout;
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    private int bannerHeight;
    private ArrayList<Poster> posters;
    private FlowAdapter flowAdapter;
    private City city;
    private String appVersion;
    private HljHttpSubscriber initSub;
    private Subscription posterSub;
    private QaHomeListFragmentAdapter fragmentStatePagerAdapter;
    private Mark localMark;
    private QaHomeListFragment hotListFragment;
    private QaHomeListFragment newListFragment;
    private QaHomeListFragment cityListFragment;

    public static QaHomeFragment newInstance() {
        Bundle args = new Bundle();
        QaHomeFragment fragment = new QaHomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initValues();
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_qa_home, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        initViews();
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initLoad();
    }

    private void initValues() {
        city = LocationSession.getInstance()
                .getCity(getActivity());
        fragmentStatePagerAdapter = new QaHomeListFragmentAdapter(getChildFragmentManager());
        bannerHeight = (CommonUtil.getDeviceSize(getContext()).x - CommonUtil.dp2px(getContext(),
                32)) * 276 / 686;
        posters = new ArrayList<>();
        try {
            appVersion = getActivity().getPackageManager()
                    .getPackageInfo(getActivity().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void initViews() {
        emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                refresh();
            }
        });
        viewFlow.getLayoutParams().height = bannerHeight;
        flowAdapter = new FlowAdapter(getActivity(), posters, R.layout.flow_item_qa_home_page);
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
                QaHomeListFragment fragment = (QaHomeListFragment) fragmentStatePagerAdapter
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
        posterSub = CommunityApi.addPosterWatchCountObb(PosterWatchFeed.QA_TYPE)
                .subscribe(HljHttpSubscriber.buildSubscriber(getContext())
                        .toastHidden()
                        .build());
        refresh();
    }

    private void setViewPager() {
        tabLayout.setVisibility(View.VISIBLE);
        viewPager.setAdapter(fragmentStatePagerAdapter);
        tabPageIndicator.setPagerAdapter(fragmentStatePagerAdapter);
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
        for (int i = 0; i < fragmentStatePagerAdapter.getCount(); i++) {
            View tabView = tabPageIndicator.getTabView(i);
            if (tabView != null) {
                TextView textView = tabView.findViewById(R.id.title);
                if (textView != null) {
                    textView.getLayoutParams().width = (int) textView.getPaint()
                            .measureText(textView.getText()
                                    .toString()
                                    .trim()) + CommonUtil.dp2px(getContext(), 24);
                }
            }
        }
    }

    @Override
    public void refresh(Object... params) {
        city = LocationSession.getInstance()
                .getCity(getActivity());
        Observable<PosterData> bObservable = QuestionAnswerApi.getQaBannerObb(HljCommon.BLOCK_ID
                        .QuestionAnswerFragment,
                appVersion,
                city.getCid());
        Observable<HljHttpCountData<List<Mark>>> mObservable = QuestionAnswerApi.getMarkListObb(1,
                100,
                city.getCid());
        Observable zipObb = Observable.zip(bObservable,
                mObservable,
                new Func2<PosterData, HljHttpCountData<List<Mark>>, ResultZip>() {
                    @Override
                    public ResultZip call(
                            PosterData posterData,
                            HljHttpCountData<List<Mark>> listHljHttpCountData) {
                        ResultZip resultZip = new ResultZip();
                        resultZip.posterData = posterData;
                        resultZip.marks = listHljHttpCountData;
                        return resultZip;
                    }
                });
        initSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setProgressBar(progressBar)
                .setOnNextListener(new SubscriberOnNextListener<ResultZip>() {
                    @Override
                    public void onNext(ResultZip resultZip) {
                        List<Poster> posterList = PosterUtil.getPosterList(resultZip.posterData
                                        .getFloors(),
                                HljCommon.POST_SITES.SITE_USER_ASQUESTION_BANNER,
                                false);
                        setPosters(posterList);
                        localMark = resultZip.marks.getLocalMark();
                        setViewPager();
                    }
                })
                .setContentView(coordinatorLayout)
                .setEmptyView(emptyView)
                .build();
        zipObb.subscribe(initSub);
    }

    private static class ResultZip extends HljHttpResultZip {
        @HljRZField
        PosterData posterData;
        @HljRZField
        HljHttpCountData<List<Mark>> marks;

    }

    private void setPosters(List<Poster> posterList) {
        posters.clear();
        posters.addAll(posterList);
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

    @Override
    public View getScrollableView() {
        return null;
    }

    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        CommonUtil.unSubscribeSubs(initSub,posterSub);
    }

    private class QaHomeListFragmentAdapter extends FragmentStatePagerAdapter {

        public QaHomeListFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            QaHomeListFragment fragment;
            switch (position) {
                case 1:
                    if (newListFragment == null) {
                        newListFragment = QaHomeListFragment.newInstance(QaHomeListFragment
                                .TYPE_NEWEST);
                    }
                    fragment = newListFragment;
                    break;
                case 2:
                    if (cityListFragment == null) {
                        cityListFragment = QaHomeListFragment.newInstance(QaHomeListFragment
                                .TYPE_MARKED);
                    }
                    fragment = cityListFragment;
                    break;
                default:
                    if (hotListFragment == null) {
                        hotListFragment = QaHomeListFragment.newInstance(QaHomeListFragment
                                .TYPE_HOTEST);
                    }
                    fragment = hotListFragment;
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String backupName = (city != null && !TextUtils.isEmpty(city.getName())) ? city
                    .getName() : "旅拍";
            switch (position) {
                case 1:
                    return "最新";
                case 2:
                    return localMark == null ? backupName : localMark.getName();
                default:
                    return "最热";
            }
        }
    }
}
