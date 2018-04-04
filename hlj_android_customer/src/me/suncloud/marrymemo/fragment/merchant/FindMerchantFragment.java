package me.suncloud.marrymemo.fragment.merchant;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.JsonObject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.MerchantProperty;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.models.modelwrappers.PosterData;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.SPUtils;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljcommonlibrary.views.widgets.PullToRefreshScrollableLayout;
import com.hunliji.hljcommonlibrary.views.widgets.ScrollableHelper;
import com.hunliji.hljcommonlibrary.views.widgets.ScrollableLayout;
import com.hunliji.hljhttplibrary.api.CommonApi;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.PosterUtil;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljtrackerlibrary.TrackerHelper;
import com.slider.library.Indicators.CirclePageExIndicator;
import com.slider.library.SliderLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.FlowAdapter;
import me.suncloud.marrymemo.api.merchant.MerchantApi;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.view.CityListActivity;
import me.suncloud.marrymemo.widget.merchant.FindMerchantMenuFilterView;
import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;
import rx.functions.Func2;


/**
 * Created by hua_rong on 2018/3/15
 * 找商家单个tab
 */
public class FindMerchantFragment extends RefreshFragment implements PullToRefreshBase
        .OnRefreshListener<ScrollableLayout> {

    private static final String CPM_SOURCE = "find_merchant_list";
    private static final String BANNER_CPM_SOURCE = "find_merchant_top_banner";

    @Override
    public String fragmentPageTrackTagName() {
        return "找商家";
    }

    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.property_menu_layout)
    GridLayout propertyMenuLayout;
    @BindView(R.id.scrollable_layout)
    PullToRefreshScrollableLayout scrollableLayout;
    @BindView(R.id.menu_filter_view)
    FindMerchantMenuFilterView merchantMenuFilterView;
    @BindView(R.id.top_posters_slider_layout)
    SliderLayout topPostersSliderLayout;
    @BindView(R.id.top_posters_indicator)
    CirclePageExIndicator topPostersIndicator;
    @BindView(R.id.top_posters_layout)
    RelativeLayout topPostersLayout;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;

    private City city;
    private long propertyId;
    private MerchantListFragment fragment;
    private boolean isCityRefresh;
    private ScrollableLayout mRefreshableView;
    private FlowAdapter flowAdapter;
    private HljHttpSubscriber refreshSubscriber;
    private Subscription rxBusEventSub;
    private static final String TAG_FRAGMENT = "merchantListFragment";
    public static final String ARG_CITY = "city";
    private Unbinder unbinder;

    public static FindMerchantFragment newInstance(
            City city, long propertyId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CITY, city);
        args.putLong(RouterPath.IntentPath.Customer.MerchantListActivityPath.ARG_PROPERTY_ID,
                propertyId);
        FindMerchantFragment fragment = new FindMerchantFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initValue();
    }

    private void initValue() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            city = (City) bundle.getSerializable(ARG_CITY);
            propertyId = bundle.getLong(RouterPath.IntentPath.Customer.MerchantListActivityPath
                            .ARG_PROPERTY_ID,
                    0);
        }
        if (city == null) {
            city = Session.getInstance()
                    .getMyCity(getContext());
        }
        isCityRefresh = false;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_find_merchant, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initError();
        onRefresh(scrollableLayout);
        registerRxBusEvent();
        initTracker();
    }

    public boolean hideMenu() {
        return merchantMenuFilterView != null && merchantMenuFilterView.hideMenu(0);
    }

    private void initView() {
        Point point = CommonUtil.getDeviceSize(getContext());
        topPostersLayout.getLayoutParams().width = point.x;
        topPostersLayout.getLayoutParams().height = Math.round(point.x * 28 / 75);
        scrollableLayout.setOnRefreshListener(this);
        mRefreshableView = scrollableLayout.getRefreshableView();
        mRefreshableView.addOnScrollListener(new ScrollableLayout.OnScrollListener() {
            @Override
            public void onScroll(int currentY, int maxY) {
                ScrollableHelper scrollableHelper = mRefreshableView.getHelper();
                if (scrollableHelper != null && scrollableHelper.getScrollableView() == null) {
                    scrollableHelper.setCurrentScrollableContainer(fragment);
                }
            }
        });

        //筛选视图
        merchantMenuFilterView.setScrollableLayout(mRefreshableView);
        merchantMenuFilterView.setPropertyId(propertyId);
        //刷新回调
        merchantMenuFilterView.setOnRefreshCallback(new FindMerchantMenuFilterView
                .onRefreshCallback() {
            @Override
            public void onRefresh(long propertyId) {
                SPUtils.put(getContext(),
                        RouterPath.IntentPath.Customer.MerchantListActivityPath.ARG_PROPERTY_ID,
                        propertyId);
                onRefreshFragment();
            }

            @Override
            public void onAllCityClick() {
                if (merchantMenuFilterView != null && merchantMenuFilterView.hideCityMenu()) {
                    return;
                }
                Intent intent = new Intent(getContext(), CityListActivity.class);
                startActivity(intent);
                if (getActivity() != null) {
                    getActivity().overridePendingTransition(R.anim.slide_in_up_to_top,
                            R.anim.activity_anim_default);
                }
            }
        });
        // 类目选择点击回调
        merchantMenuFilterView.setOnPropertyMenuCallback(new FindMerchantMenuFilterView
                .onPropertyMenuCallback() {
            @Override
            public void onPropertyMenu(ArrayList<MerchantProperty> properties) {
                initPropertyMenu(properties);
            }
        });
    }

    private void initError() {
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                onRefresh(scrollableLayout);
            }
        });
    }


    private class ResultZip {

        JsonObject merchantFilter;
        List<Poster> posters;

        public ResultZip(
                JsonObject merchantFilter, List<Poster> posters) {
            this.merchantFilter = merchantFilter;
            this.posters = posters;
        }
    }

    private void initTracker() {
        HljVTTagger.tagViewParentName(topPostersSliderLayout, "find_merchant_home_banner_list");
    }

    private void setPoster(List<Poster> posters) {
        flowAdapter = new FlowAdapter(getContext(), new ArrayList<>(posters), R.layout.flow_item);
        flowAdapter.setCpmSource(BANNER_CPM_SOURCE);
        flowAdapter.setSliderLayout(topPostersSliderLayout);
        topPostersSliderLayout.setPagerAdapter(flowAdapter);
        topPostersSliderLayout.setCustomIndicator(topPostersIndicator);
        if (flowAdapter.getCount() == 0) {
            topPostersSliderLayout.stopAutoCycle();
            topPostersLayout.setVisibility(View.GONE);
        } else {
            topPostersLayout.setVisibility(View.VISIBLE);
            if (flowAdapter.getCount() > 1) {
                topPostersSliderLayout.startAutoCycle();
            } else {
                topPostersSliderLayout.stopAutoCycle();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (flowAdapter != null && flowAdapter.getCount() > 1) {
            topPostersSliderLayout.startAutoCycle();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        topPostersSliderLayout.stopAutoCycle();
        hideMenu();
    }


    @Override
    public void refresh(Object... params) {}

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser) {
            hideMenu();
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase<ScrollableLayout> refreshView) {
        CommonUtil.unSubscribeSubs(refreshSubscriber);
        long cid = city == null ? 0 : city.getId();
        merchantMenuFilterView.setCity(city);
        Observable<JsonObject> merchantObb = MerchantApi.getMerchantFilter(cid);
        Observable<List<Poster>> posterObb = CommonApi.getBanner(getContext(),
                HljCommon.BLOCK_ID.FindMerchantHomeFragment,
                cid)
                .concatMap(new Func1<PosterData, Observable<List<Poster>>>() {
                    @Override
                    public Observable<List<Poster>> call(PosterData posterData) {
                        if (posterData != null) {
                            List<Poster> posters = PosterUtil.getPosterList(posterData.getFloors(),
                                    HljCommon.POST_SITES.SITE_LOOK_FOR_MERCHANT,
                                    false);
                            return Observable.from(posters)
                                    .filter(new Func1<Poster, Boolean>() {
                                        @Override
                                        public Boolean call(Poster poster) {
                                            return poster != null && poster.getId() > 0;
                                        }
                                    })
                                    .toList();
                        }
                        return Observable.just(null);
                    }
                });
        Observable<ResultZip> observable = Observable.zip(merchantObb,
                posterObb,
                new Func2<JsonObject, List<Poster>, ResultZip>() {
                    @Override
                    public ResultZip call(
                            JsonObject merchantFilter, List<Poster> posters) {
                        return new ResultZip(merchantFilter, posters);
                    }
                });
        refreshSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                .setProgressBar(scrollableLayout.isRefreshing() ? null : progressBar)
                .setContentView(scrollableLayout)
                .setPullToRefreshBase(scrollableLayout)
                .setEmptyView(emptyView)
                .setOnNextListener(new SubscriberOnNextListener<ResultZip>() {
                    @Override
                    public void onNext(ResultZip resultZip) {
                        setMerchantFilter(resultZip.merchantFilter);
                        setPoster(resultZip.posters);
                        onRefreshFragment();
                    }
                })
                .build();
        observable.subscribe(refreshSubscriber);
    }


    private void setMerchantFilter(JsonObject merchantFilter) {
        if (merchantFilter != null) {
            try {
                merchantMenuFilterView.setTabMenuView(View.VISIBLE);
                merchantMenuFilterView.initMerchantMenu(merchantFilter, isCityRefresh);
                if (!isCityRefresh) {
                    JsonObject hotelFilterJson = merchantFilter.getAsJsonObject("hotel");
                    if (hotelFilterJson != null) {
                        merchantMenuFilterView.initHotelMenu(hotelFilterJson);
                    }
                }
                //刷新商家列表
                merchantMenuFilterView.onRefresh();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            emptyView.showEmptyView();
            scrollableLayout.setVisibility(View.GONE);
        }
    }

    private void onRefreshFragment() {
        String urlQuery = merchantMenuFilterView.getUrlQuery();
        if (!TextUtils.isEmpty(urlQuery)) {
            fragment = (MerchantListFragment) getChildFragmentManager().findFragmentByTag(
                    TAG_FRAGMENT);
            if (fragment == null) {
                FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                fragment = MerchantListFragment.newInstance(urlQuery, city, CPM_SOURCE);
                ft.add(R.id.content_layout, fragment, TAG_FRAGMENT);
                ft.commitAllowingStateLoss();
            } else {
                fragment.refresh(urlQuery, city);
            }
        }
    }

    private void initPropertyMenu(final ArrayList<MerchantProperty> properties) {
        int iconSize = CommonUtil.dp2px(getContext(), 40);
        propertyMenuLayout.setVisibility(View.VISIBLE);
        propertyMenuLayout.removeAllViews();
        for (MerchantProperty property : properties) {
            View.inflate(getContext(), R.layout.merchant_filter_property, propertyMenuLayout);
            int index = propertyMenuLayout.getChildCount() - 1;
            View view = propertyMenuLayout.getChildAt(index);
            view.setTag(property);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MerchantProperty property = (MerchantProperty) v.getTag();
                    TrackerHelper.findMerchantProperty(v.getContext(), property.getId());
                    merchantMenuFilterView.setProperty(property);
                    mRefreshableView.scrollToBottom();
                    merchantMenuFilterView.onRefresh();
                }
            });
            ImageView ivIcon = view.findViewById(R.id.iv_icon);
            TextView tvName = view.findViewById(R.id.tv_name);
            Glide.with(this)
                    .load(ImagePath.buildPath(property.getIcon())
                            .width(iconSize)
                            .height(iconSize)
                            .cropPath())
                    .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image)
                            .fitCenter())
                    .into(ivIcon);
            tvName.setText(property.getName());
        }
    }

    //首页城市的改变
    public void cityRefresh(City c) {
        if (!city.getId()
                .equals(c.getId())) {
            city = c;
            isCityRefresh = true;
            onRefresh(scrollableLayout);
        }
    }

    //注册RxEventBus监听
    private void registerRxBusEvent() {
        if (rxBusEventSub == null || rxBusEventSub.isUnsubscribed()) {
            rxBusEventSub = RxBus.getDefault()
                    .toObservable(RxEvent.class)
                    .subscribe(new RxBusSubscriber<RxEvent>() {
                        @Override
                        protected void onEvent(RxEvent rxEvent) {
                            switch (rxEvent.getType()) {
                                case CITY_CHANGE:
                                    City city = (City) rxEvent.getObject();
                                    if (city != null && !city.getId()
                                            .equals(FindMerchantFragment.this.city.getId())) {
                                        FindMerchantFragment.this.city = city;
                                        isCityRefresh = true;
                                        onRefresh(scrollableLayout);
                                    }
                                    break;
                            }
                        }
                    });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
        CommonUtil.unSubscribeSubs(rxBusEventSub, refreshSubscriber);
    }

}




