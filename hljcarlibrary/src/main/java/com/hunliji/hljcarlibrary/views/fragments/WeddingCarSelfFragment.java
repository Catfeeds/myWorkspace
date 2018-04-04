package com.hunliji.hljcarlibrary.views.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcarlibrary.R;
import com.hunliji.hljcarlibrary.R2;
import com.hunliji.hljcarlibrary.adapter.WeddingCarSelfRecyclerAdapter;
import com.hunliji.hljcarlibrary.api.WeddingCarApi;
import com.hunliji.hljcarlibrary.models.Brand;
import com.hunliji.hljcarlibrary.models.CarFilter;
import com.hunliji.hljcarlibrary.views.activities.WeddingCarProductDetailActivity;
import com.hunliji.hljcarlibrary.views.activities.WeddingCarSubPageActivity;
import com.hunliji.hljcarlibrary.widgets.WeddingCarFilterMenuViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.Label;
import com.hunliji.hljcommonlibrary.models.wedding_car.WeddingCarProduct;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljTaggerName;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.views.fragments.ScrollAbleFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljcommonlibrary.views.widgets.ScrollableLayout;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by mo_yu on 2017/12/26.婚车个性自选
 */

public class WeddingCarSelfFragment extends ScrollAbleFragment implements
        OnItemClickListener<WeddingCarProduct> {
    public static final String ARG_CITY_ID = "city_id";

    @BindView(R2.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R2.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R2.id.wedding_car_filter_view)
    FrameLayout weddingCarFilterView;
    Unbinder unbinder;

    private long cityId;
    private View endView;
    private View loadView;
    private WeddingCarSelfRecyclerAdapter adapter;
    private Map<String, Object> queries;
    private WeddingCarFilterMenuViewHolder weddingCarFilterMenuViewHolder;

    private HljHttpSubscriber initSubscriber;
    private Subscription pageSubscription;

    public static WeddingCarSelfFragment newInstance(long cityId) {
        Bundle args = new Bundle();
        WeddingCarSelfFragment fragment = new WeddingCarSelfFragment();
        args.putLong(ARG_CITY_ID, cityId);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View footerView = View.inflate(getContext(), R.layout.hlj_foot_no_more___cm, null);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        loadView.setVisibility(View.INVISIBLE);
        adapter = new WeddingCarSelfRecyclerAdapter(getContext());
        adapter.setFooterView(footerView);
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_wedding_car___car, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        initTracker();
        return rootView;
    }

    private void initTracker() {
        HljVTTagger.tagViewParentName(recyclerView,
                HljTaggerName.WeddingCarSubPageActivity.CAR_OPTIONAL_LIST);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null) {
            cityId = getArguments().getLong(ARG_CITY_ID);
        }
        initView();
        initLoad();
    }

    private void initView() {
        recyclerView.setMode(PullToRefreshBase.Mode.DISABLED);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (adapter.getItemViewType(position)) {
                    case WeddingCarSelfRecyclerAdapter.FOOTER:
                        return 2;
                    default:
                        return 1;
                }
            }
        });
        recyclerView.getRefreshableView()
                .setLayoutManager(layoutManager);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
        adapter.setOnItemClickListener(this);
        ScrollableLayout scrollableLayout = null;
        if (getActivity() instanceof WeddingCarSubPageActivity) {
            WeddingCarSubPageActivity weddingCarSubPageActivity = (WeddingCarSubPageActivity)
                    getActivity();
            scrollableLayout = weddingCarSubPageActivity.getScrollableLayout();
        }
        weddingCarFilterMenuViewHolder = WeddingCarFilterMenuViewHolder.newInstance(getContext(),
                cityId,
                CarFilter.OPTIONAL_TAB,
                scrollableLayout,
                new WeddingCarFilterMenuViewHolder.OnFilterResultListener() {
                    @Override
                    public void onFilterResult(Map<String, Object> map) {
                        if (map == null) {
                            return;
                        }
                        queries = map;
                        onRefresh();
                    }
                });
        weddingCarFilterView.removeAllViews();
        weddingCarFilterView.addView(weddingCarFilterMenuViewHolder.getRootView());
    }

    private void initLoad() {
        if (initSubscriber != null && !initSubscriber.isUnsubscribed()) {
            return;
        }
        CommonUtil.unSubscribeSubs(pageSubscription);
        initSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                .setProgressBar(progressBar.getVisibility() == View.VISIBLE ? progressBar : null)
                .setContentView(recyclerView)
                .setEmptyView(emptyView)
                .setDataNullable(true)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<WeddingCarProduct>>>() {
                    @Override
                    public void onNext(HljHttpData<List<WeddingCarProduct>> listHljHttpData) {
                        if (listHljHttpData == null) {
                            showEmptyView();
                            return;
                        } else if (CommonUtil.isCollectionEmpty(listHljHttpData.getData())) {
                            showEmptyView();
                        } else {
                            hideEmptyView();
                        }
                        WeddingCarSubPageActivity activity = (WeddingCarSubPageActivity)
                                getActivity();
                        if (activity != null) {
                            activity.setSelfCount(listHljHttpData.getTotalCount());
                        }
                        if (listHljHttpData.getData() != null) {
                            adapter.setCars(listHljHttpData.getData());
                        }
                        initPagination(listHljHttpData.getPageCount());
                    }
                })
                .build();
        WeddingCarApi.getSelfCarsObb(getUrl(1))
                .subscribe(initSubscriber);
    }

    private void initPagination(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSubscription);
        pageSubscription = PaginationTool.buildPagingObservable(recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpData<List<WeddingCarProduct>>>() {
                    @Override
                    public Observable<HljHttpData<List<WeddingCarProduct>>> onNextPage(int page) {
                        return WeddingCarApi.getSelfCarsObb(getUrl(page));
                    }
                })
                .setEndView(endView)
                .setLoadView(loadView)
                .build()
                .getPagingObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(HljHttpSubscriber.buildSubscriber(getContext())
                        .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<WeddingCarProduct>>>() {
                            @Override
                            public void onNext(
                                    HljHttpData<List<WeddingCarProduct>> listHljHttpData) {
                                adapter.addCars(listHljHttpData.getData());
                            }
                        })
                        .build());
    }

    private String getUrl(int page) {
        StringBuilder url = new StringBuilder(
                "p/wedding/index.php/Car/APICarProduct/CarProductOptional");
        url.append("?page=")
                .append(page)
                .append("&per_page=20")
                .append("&cid=")
                .append(cityId);
        if (queries != null) {
            for (Object o : queries.entrySet()) {
                Map.Entry entry = (Map.Entry) o;
                String key = (String) entry.getKey();
                Label label = (Label) entry.getValue();
                if (label != null && !CommonUtil.isEmpty(label.getValue())) {
                    url.append("&")
                            .append(key)
                            .append("=")
                            .append(label.getValue());
                }
            }
        }
        return url.toString();
    }

    private void showEmptyView() {
        recyclerView.setVisibility(View.GONE);
        emptyView.showEmptyView();
    }

    private void hideEmptyView() {
        recyclerView.setVisibility(View.VISIBLE);
        emptyView.hideEmptyView();
    }

    @Override
    public View getScrollableView() {
        if (recyclerView.getVisibility() == View.VISIBLE && recyclerView.getRefreshableView()
                .getAdapter() != null) {
            return recyclerView.getRefreshableView();
        }
        return null;
    }

    @Override
    public void refresh(Object... params) {
        if (params != null && params.length > 0 && params[0] instanceof Brand) {
            Brand brand = (Brand) params[0];
            weddingCarFilterMenuViewHolder.showSelectLabel(0, brand.getId());
        } else {
            initLoad();
        }
    }

    public void onRefresh() {
        progressBar.setVisibility(View.VISIBLE);
        CommonUtil.unSubscribeSubs(initSubscriber);
        adapter.clear();
        initLoad();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        CommonUtil.unSubscribeSubs(initSubscriber, pageSubscription);
        if (weddingCarFilterMenuViewHolder != null) {
            weddingCarFilterMenuViewHolder.onDestroy();
        }
    }

    @Override
    public void onItemClick(int position, WeddingCarProduct weddingCarProduct) {
        if (weddingCarProduct == null) {
            return;
        }
        Intent intent = new Intent();
        intent.setClass(getContext(), WeddingCarProductDetailActivity.class);
        intent.putExtra(WeddingCarProductDetailActivity.ARG_ID, weddingCarProduct.getId());
        startActivity(intent);
    }
}
