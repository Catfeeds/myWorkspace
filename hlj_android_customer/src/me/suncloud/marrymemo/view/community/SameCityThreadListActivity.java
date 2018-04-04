package me.suncloud.marrymemo.view.community;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityAuthor;
import com.hunliji.hljcommonlibrary.models.modelwrappers.PosterData;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljcommonlibrary.views.widgets.PullToRefreshScrollableLayout;
import com.hunliji.hljcommonlibrary.views.widgets.ScrollableHelper;
import com.hunliji.hljcommonlibrary.views.widgets.ScrollableLayout;
import com.hunliji.hljcommonlibrary.views.widgets.TabPageIndicator;
import com.hunliji.hljhttplibrary.api.CommonApi;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.PosterUtil;
import com.hunliji.hljhttplibrary.utils.SubscriberOnCompletedListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.makeramen.rounded.RoundedImageView;
import com.slider.library.Indicators.CirclePageExIndicator;
import com.slider.library.SliderLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.FlowAdapter;
import me.suncloud.marrymemo.api.community.CommunityApi;
import me.suncloud.marrymemo.fragment.community.CommunityFragment;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.Util;
import rx.Observable;
import rx.functions.Func2;

/**
 * 同城备婚
 * Created by chen_bin on 2017/3/23 0023.
 */
@Deprecated
public class SameCityThreadListActivity extends HljBaseActivity implements TabPageIndicator
        .OnTabChangeListener, PullToRefreshBase.OnRefreshListener<ScrollableLayout> {
    @BindView(R.id.scrollable_layout)
    PullToRefreshScrollableLayout scrollableLayout;
    @BindView(R.id.header_layout)
    RelativeLayout headerLayout;
    @BindView(R.id.top_posters_layout)
    RelativeLayout topPostersLayout;
    @BindView(R.id.slider_layout)
    SliderLayout sliderLayout;
    @BindView(R.id.flow_indicator)
    CirclePageExIndicator flowIndicator;
    @BindView(R.id.newest_thread_layout)
    LinearLayout newestThreadLayout;
    @BindView(R.id.newest_thread_list_layout)
    RelativeLayout newestThreadListLayout;
    @BindView(R.id.tv_newest_thread_count)
    TextView tvNewestThreadCount;
    @BindView(R.id.indicator)
    TabPageIndicator indicator;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.bottom_layout)
    LinearLayout bottomLayout;
    @BindView(R.id.btn_create_thread_hint)
    ImageButton btnCreateThreadHint;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    private FlowAdapter flowAdapter;
    private SparseArray<CommunityFragment> fragments;
    private ArrayList<CommunityAuthor> authors;
    private City city;
    private int logoSize;
    private int marginLeft;
    private HljHttpSubscriber initSubscriber;

    @Override
    public String pageTrackTagName() {
        return "社区同城";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_same_city_thread_list);
        ButterKnife.bind(this);
        authors = new ArrayList<>();
        fragments = new SparseArray<>();
        logoSize = CommonUtil.dp2px(this, 30);
        marginLeft = CommonUtil.dp2px(this, 22);
        city = Session.getInstance()
                .getMyCity(this);
        int topPostersWidth = CommonUtil.getDeviceSize(this).x;
        topPostersLayout.getLayoutParams().width = topPostersWidth;
        topPostersLayout.getLayoutParams().height = Math.round(topPostersWidth / 2.0f);
        flowAdapter = new FlowAdapter(this, null,  R.layout.flow_item);
        sliderLayout.setPagerAdapter(flowAdapter);
        flowAdapter.setSliderLayout(sliderLayout);
        sliderLayout.setCustomIndicator(flowIndicator);
        sliderLayout.setPresetTransformer(4);
        SectionsPagerAdapter pagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(2);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                indicator.setCurrentItem(position);
                scrollableLayout.getRefreshableView()
                        .getHelper()
                        .setCurrentScrollableContainer(getCurrentScrollableContainer());
            }
        });
        indicator.setOnTabChangeListener(this);
        indicator.setPagerAdapter(pagerAdapter);
        scrollableLayout.setOnRefreshListener(this);
        scrollableLayout.getRefreshableView()
                .addOnScrollListener(new ScrollableLayout.OnScrollListener() {
                    @Override
                    public void onScroll(int currentY, int maxY) {
                        if (scrollableLayout.getRefreshableView()
                                .getHelper()
                                .getScrollableView() == null) {
                            scrollableLayout.getRefreshableView()
                                    .getHelper()
                                    .setCurrentScrollableContainer(getCurrentScrollableContainer());
                        }
                    }
                });
        onRefresh(null);
    }

    @Override
    public void onRefresh(PullToRefreshBase<ScrollableLayout> refreshView) {
        if (initSubscriber == null || initSubscriber.isUnsubscribed()) {
            final boolean isRefreshing = scrollableLayout.isRefreshing();
            //poster
            Observable<PosterData> posterObb = CommonApi.getBanner(this,
                    HljCommon.BLOCK_ID.SameCityThreadListActivity,
                    city.getId());
            //同城备婚下最新4个用户的数据
            Observable<HljHttpData<List<CommunityAuthor>>> authorsObb = CommunityApi
                    .getNewestThreadsObb(
                    1,
                    5);
            Observable<ResultZip> observable = Observable.zip(posterObb,
                    authorsObb,
                    new Func2<PosterData, HljHttpData<List<CommunityAuthor>>, ResultZip>() {
                        @Override
                        public ResultZip call(
                                PosterData posterData,
                                HljHttpData<List<CommunityAuthor>> authorsData) {
                            ResultZip resultZip = new ResultZip();
                            resultZip.posterData = posterData;
                            resultZip.authorsData = authorsData;
                            return resultZip;
                        }
                    });
            initSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<ResultZip>() {
                        @Override
                        public void onNext(ResultZip resultZip) {
                            setData(resultZip);
                        }
                    })
                    .setOnCompletedListener(new SubscriberOnCompletedListener() {
                        @Override
                        public void onCompleted() {
                            if (isRefreshing) {
                                for (int i = 0, size = fragments.size(); i < size; i++) {
                                    CommunityFragment fragment = fragments.get(i);
                                    if (fragment != null) {
                                        if (viewPager.getCurrentItem() == i) {
                                            fragment.refresh();
                                        } else {
                                            fragment.setNeedRefresh(true);
                                        }
                                    }
                                }
                            }
                        }
                    })
                    .setDataNullable(true)
                    .setEmptyView(emptyView)
                    .setContentView(scrollableLayout)
                    .setPullToRefreshBase(scrollableLayout)
                    .setProgressBar(isRefreshing ? null : progressBar)
                    .build();
            observable.subscribe(initSubscriber);
        }
    }

    private class ResultZip {
        PosterData posterData;
        HljHttpData<List<CommunityAuthor>> authorsData;
    }

    //设置数据
    private void setData(ResultZip resultZip) {
        //当用户首次进入频道页时，显示该引导
        SharedPreferences preferences = getSharedPreferences(Constants.PREF_FILE,
                Context.MODE_PRIVATE);
        if (!preferences.getBoolean(Constants.PREF_CREATE_THREAD_HINT_CLICKED, false)) {
            preferences.edit()
                    .putBoolean(Constants.PREF_CREATE_THREAD_HINT_CLICKED, true)
                    .apply();
            btnCreateThreadHint.setVisibility(View.VISIBLE);
            btnCreateThreadHint.postDelayed(new Runnable() {
                @Override
                public void run() {
                    onCreateThreadHintClicked();
                }
            }, 3000);
        }
        bottomLayout.setVisibility(View.VISIBLE);
        scrollableLayout.getRefreshableView()
                .setVisibility(View.VISIBLE);
        boolean isDataEmpty = true;
        //poster
        if (resultZip.posterData == null) {
            sliderLayout.stopAutoCycle();
            topPostersLayout.setVisibility(View.GONE);
        } else {
            List<Poster> posters = PosterUtil.getPosterList(resultZip.posterData.getFloors(),
                    HljCommon.POST_SITES.CITY_WEDDING_TOP_BANNER,
                    false);
            flowAdapter.setmDate(posters);
            if (flowAdapter.getCount() == 0) {
                sliderLayout.stopAutoCycle();
                topPostersLayout.setVisibility(View.GONE);
            } else {
                isDataEmpty = false;
                topPostersLayout.setVisibility(View.VISIBLE);
                if (flowAdapter.getCount() > 1) {
                    sliderLayout.startAutoCycle();
                } else {
                    sliderLayout.stopAutoCycle();
                }
            }
        }
        //authors
        authors.clear();
        if (resultZip.authorsData == null || CommonUtil.isCollectionEmpty(resultZip.authorsData
                .getData())) {
            newestThreadLayout.setVisibility(View.GONE);
        } else {
            isDataEmpty = false;
            newestThreadLayout.setVisibility(View.VISIBLE);
            authors.addAll(resultZip.authorsData.getData());
            tvNewestThreadCount.setText(getString(R.string.label_newest_thread_count,
                    resultZip.authorsData.getTotalCount()));
            int count = newestThreadListLayout.getChildCount();
            int size = Math.min(5, authors.size());
            if (count > size) {
                newestThreadListLayout.removeViews(size, count - size);
            }
            for (int i = 0; i < size; i++) {
                CommunityAuthor author = authors.get(i);
                View view = null;
                if (count > i) {
                    view = newestThreadListLayout.getChildAt(i);
                }
                if (view == null) {
                    View.inflate(this, R.layout.newest_thread_list_item, newestThreadListLayout);
                    view = newestThreadListLayout.getChildAt(newestThreadListLayout.getChildCount
                            () - 1);
                }
                RoundedImageView imgAvatar = view.findViewById(R.id.img_avatar);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imgAvatar
                        .getLayoutParams();
                params.setMargins(i * marginLeft, 0, 0, 0);
                Glide.with(this)
                        .load(ImageUtil.getImagePath2(author.getAvatar(), logoSize))
                        .apply(new RequestOptions().dontAnimate()
                                .placeholder(R.mipmap.icon_avatar_primary)
                                .error(R.mipmap.icon_avatar_primary))
                        .into(imgAvatar);
            }
        }
        headerLayout.setVisibility(isDataEmpty ? View.GONE : View.VISIBLE);
    }

    //同城最新,显示所在地为重点城市及该市关联城市用户所发的帖子。
    @OnClick(R.id.newest_thread_layout)
    void onGoSameCityNewestThreads() {
        startActivity(new Intent(this, SameCityNewestThreadListActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    @OnClick(R.id.bottom_layout)
    void onCreateThread() {
        if (btnCreateThreadHint.getVisibility() == View.VISIBLE) {
            onCreateThreadHintClicked();
            return;
        }
        if (!Util.loginBindChecked(this, Constants.Login.SEND_THREAD_LOGIN)) {
            return;
        }
        Intent intent = new Intent(this, CreateThreadActivity.class);
        startActivityForResult(intent, Constants.RequestCode.SEND_THREAD_COMPLETE);
        overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.activity_anim_default);
    }

    //点击取消提示图片
    @OnClick(R.id.btn_create_thread_hint)
    void onCreateThreadHintClicked() {
        if (btnCreateThreadHint != null) {
            btnCreateThreadHint.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.RequestCode.COMPLETE_PROFILE:
                    if (data != null && data.getBooleanExtra("modified", false)) {
                        onCreateThread();
                    }
                    break;
                case Constants.RequestCode.SEND_THREAD_COMPLETE:
                    for (int i = 0, size = fragments.size(); i < size; i++) {
                        CommunityFragment fragment = fragments.get(i);
                        if (fragment != null && fragment.getScrollableView() != null) {
                            if (viewPager.getCurrentItem() == i) {
                                fragment.refresh();
                            } else {
                                fragment.setNeedRefresh(true);
                            }
                        }
                    }
                    indicator.setCurrentItem(0);
                    viewPager.setCurrentItem(0);
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onTabChanged(int position) {
        viewPager.setCurrentItem(position);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            CommunityFragment fragment = fragments.get(position);
            if (fragment != null) {
                return fragment;
            }
            switch (position) {
                case 0:
                    fragment = CommunityFragment.newInstance(getUrl("all"), true, true, true, true);
                    break;
                case 1:
                    fragment = CommunityFragment.newInstance(getUrl("is_refined"),
                            true,
                            true,
                            true,
                            true);
                    break;
                case 2:
                    fragment = CommunityFragment.newInstance(getUrl("recommend"),
                            true,
                            true,
                            false,
                            true);
                    break;
            }
            if (fragment != null) {
                fragments.put(position, fragment);
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 1:
                    return getString(R.string.label_essence).toUpperCase();
                case 2:
                    return getString(R.string.label_recommend).toUpperCase();
                default:
                    return getString(R.string.label_all).toUpperCase();
            }
        }
    }

    //url
    private String getUrl(String listType) {
        return String.format(Constants.HttpPath.GET_SAME_CITY_THREADS, listType);
    }

    // 获取当前fragment
    private ScrollableHelper.ScrollableContainer getCurrentScrollableContainer() {
        if (viewPager.getAdapter() != null && viewPager.getAdapter() instanceof
                SectionsPagerAdapter) {
            SectionsPagerAdapter adapter = (SectionsPagerAdapter) viewPager.getAdapter();
            Fragment fragment = (Fragment) adapter.instantiateItem(viewPager,
                    viewPager.getCurrentItem());
            if (fragment != null && fragment instanceof ScrollableHelper.ScrollableContainer) {
                return (ScrollableHelper.ScrollableContainer) fragment;
            }
        }
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sliderLayout != null && flowAdapter != null && flowAdapter.getCount() > 1) {
            sliderLayout.startAutoCycle();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sliderLayout != null) {
            sliderLayout.stopAutoCycle();
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(initSubscriber);
    }
}