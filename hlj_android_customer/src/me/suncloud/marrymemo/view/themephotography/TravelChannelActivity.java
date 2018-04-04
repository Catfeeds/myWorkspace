package me.suncloud.marrymemo.view.themephotography;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.models.modelwrappers.PosterData;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.api.CommonApi;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.PosterUtil;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;
import com.slider.library.Indicators.CirclePageExIndicator;
import com.slider.library.SliderLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.FlowAdapter;
import me.suncloud.marrymemo.adpter.themephotography.TravelMerchantExposureRecyclerAdapter;
import me.suncloud.marrymemo.api.themephotography.ThemeApi;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.model.themephotography.TravelMerchantExposure;
import me.suncloud.marrymemo.util.Session;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * 旅拍品牌专场频道页
 * Created by chen_bin on 2017/5/13 0013.
 */
public class TravelChannelActivity extends HljBaseNoBarActivity {

    @BindView(R.id.action_holder_layout)
    LinearLayout actionHolderLayout;
    @BindView(R.id.action_holder_layout2)
    LinearLayout actionHolderLayout2;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private View headerView;
    private View endView;
    private View loadView;
    private LinearLayoutManager layoutManager;
    private HeaderViewHolder headerViewHolder;
    private TravelMerchantExposureRecyclerAdapter adapter;
    private City city;
    private int extraHeight;
    private HljHttpSubscriber refreshSubscriber;
    private HljHttpSubscriber pageSubscriber;

    @Override
    public String pageTrackTagName() {
        return "品牌专场";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_channel);
        ButterKnife.bind(this);
        setActionBarPadding(actionHolderLayout, actionHolderLayout2);
        initValues();
        initViews();
        initTracker();
        onRefresh();
    }

    private void initTracker() {
        HljVTTagger.tagViewParentName(recyclerView, "travel_merchant_exposure_list");
    }

    private void initValues() {
        extraHeight = CommonUtil.dp2px(this, 45) + getStatusBarHeight();
        city = Session.getInstance()
                .getMyCity(this);
    }

    private void initViews() {
        emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                onRefresh();
            }
        });
        headerView = View.inflate(this, R.layout.travel_merchant_exposure_header, null);
        View footerView = View.inflate(this, R.layout.hlj_foot_no_more___cm, null);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        loadView.setVisibility(View.INVISIBLE);
        ((SimpleItemAnimator) recyclerView.getRefreshableView()
                .getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setMode(PullToRefreshBase.Mode.DISABLED);
        recyclerView.getRefreshableView()
                .addItemDecoration(new SpacesItemDecoration());
        layoutManager = new LinearLayoutManager(this);
        recyclerView.getRefreshableView()
                .setLayoutManager(layoutManager);
        adapter = new TravelMerchantExposureRecyclerAdapter(this);
        adapter.setFooterView(footerView);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
        recyclerView.getRefreshableView()
                .addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        if (headerViewHolder == null) {
                            return;
                        }
                        int headerTop = Math.abs(headerView.getTop());
                        if (headerTop >= headerViewHolder.topPostersHeight - extraHeight ||
                                layoutManager.findFirstVisibleItemPosition() >= 1) {
                            actionHolderLayout.setAlpha(0);
                            actionHolderLayout2.setAlpha(1);
                        } else {
                            float f = headerTop * 1.0f / (headerViewHolder.topPostersHeight -
                                    extraHeight);
                            actionHolderLayout.setAlpha(1 - f);
                            actionHolderLayout2.setAlpha(f);
                        }
                    }
                });
    }

    public void onRefresh() {
        if (refreshSubscriber == null || refreshSubscriber.isUnsubscribed()) {
            //poster
            Observable<PosterData> posterObb = CommonApi.getBanner(this,
                    HljCommon.BLOCK_ID.TravelChannelActivity,
                    city.getId());
            //旅拍商家展示项
            Observable<HljHttpData<List<TravelMerchantExposure>>> exposuresObb = ThemeApi
                    .getTravelMerchantExposuresObb(
                    1,
                    HljCommon.PER_PAGE);
            Observable<ResultZip> observable = Observable.zip(posterObb,
                    exposuresObb,
                    new Func2<PosterData, HljHttpData<List<TravelMerchantExposure>>, ResultZip>() {
                        @Override
                        public ResultZip call(
                                PosterData posterData,
                                HljHttpData<List<TravelMerchantExposure>> exposuresData) {
                            ResultZip resultZip = new ResultZip();
                            resultZip.posterData = posterData;
                            resultZip.exposuresData = exposuresData;
                            return resultZip;
                        }
                    });
            refreshSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<ResultZip>() {
                        @Override
                        public void onNext(ResultZip resultZip) {
                            setData(resultZip);
                        }
                    })
                    .setDataNullable(true)
                    .setEmptyView(emptyView)
                    .setContentView(recyclerView)
                    .setPullToRefreshBase(recyclerView)
                    .setProgressBar(recyclerView.isRefreshing() ? null : progressBar)
                    .build();
            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(refreshSubscriber);
        }
    }

    private void setData(ResultZip resultZip) {
        headerViewHolder = (HeaderViewHolder) headerView.getTag();
        if (headerViewHolder == null) {
            headerViewHolder = new HeaderViewHolder(headerView);
            headerView.setTag(headerViewHolder);
        }
        //poster
        if (resultZip.posterData == null) {
            headerViewHolder.sliderLayout.stopAutoCycle();
            headerViewHolder.topPostersLayout.setVisibility(View.GONE);
        } else {
            List<Poster> posters = PosterUtil.getPosterList(resultZip.posterData.getFloors(),
                    HljCommon.POST_SITES.SITE_TRAVEL_MERCHANT_EXPOSURE_TOP,
                    false);
            headerViewHolder.flowAdapter.setmDate(posters);
            if (headerViewHolder.flowAdapter.getCount() == 0) {
                headerViewHolder.sliderLayout.stopAutoCycle();
                headerViewHolder.topPostersLayout.setVisibility(View.GONE);
            } else {
                headerViewHolder.topPostersLayout.setVisibility(View.VISIBLE);
                if (headerViewHolder.flowAdapter.getCount() > 1) {
                    headerViewHolder.sliderLayout.startAutoCycle();
                } else {
                    headerViewHolder.sliderLayout.stopAutoCycle();
                }
            }
        }
        int pageCount = 0;
        List<TravelMerchantExposure> exposures = null;
        if (resultZip.exposuresData != null) {
            pageCount = resultZip.exposuresData.getPageCount();
            exposures = resultZip.exposuresData.getData();
        }
        if (headerViewHolder.topPostersLayout.getVisibility() == View.GONE && CommonUtil
                .isCollectionEmpty(
                exposures)) {
            emptyView.showEmptyView();
            recyclerView.setVisibility(View.GONE);
        }
        adapter.setExposures(exposures);
        initPagination(pageCount);
    }

    private void initPagination(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSubscriber);
        Observable<HljHttpData<List<TravelMerchantExposure>>> pageObservable = PaginationTool
                .buildPagingObservable(
                recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpData<List<TravelMerchantExposure>>>() {
                    @Override
                    public Observable<HljHttpData<List<TravelMerchantExposure>>> onNextPage(int page) {
                        return ThemeApi.getTravelMerchantExposuresObb(page, HljCommon.PER_PAGE);
                    }
                })
                .setLoadView(loadView)
                .setEndView(endView)
                .build()
                .getPagingObservable()
                .observeOn(AndroidSchedulers.mainThread());
        pageSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<TravelMerchantExposure>>>() {
                    @Override
                    public void onNext(HljHttpData<List<TravelMerchantExposure>> listHljHttpData) {
                        adapter.addExposures(listHljHttpData.getData());
                    }
                })
                .build();
        pageObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSubscriber);
    }

    private class ResultZip {
        PosterData posterData;
        HljHttpData<List<TravelMerchantExposure>> exposuresData;
    }

    public class HeaderViewHolder {
        @BindView(R.id.top_posters_layout)
        RelativeLayout topPostersLayout;
        @BindView(R.id.slider_layout)
        SliderLayout sliderLayout;
        @BindView(R.id.flow_indicator)
        CirclePageExIndicator flowIndicator;
        FlowAdapter flowAdapter;
        private int topPostersHeight;

        public HeaderViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
            int topPostersWidth = CommonUtil.getDeviceSize(itemView.getContext()).x;
            this.topPostersHeight = Math.round(topPostersWidth * 9.0f / 16.0f);
            topPostersLayout.getLayoutParams().width = topPostersWidth;
            topPostersLayout.getLayoutParams().height = topPostersHeight;
            flowAdapter = new FlowAdapter(itemView.getContext(), null, R.layout.flow_item);
            flowAdapter.setCity(Session.getInstance()
                    .getMyCity(itemView.getContext()));
            sliderLayout.setPagerAdapter(flowAdapter);
            flowAdapter.setSliderLayout(sliderLayout);
            sliderLayout.setCustomIndicator(flowIndicator);
            sliderLayout.setPresetTransformer(4);
        }
    }

    private class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration() {
            this.space = CommonUtil.dp2px(TravelChannelActivity.this, 10);
        }

        @Override
        public void getItemOffsets(
                Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            int top = position >= adapter.getHeaderViewCount() && position < adapter.getItemCount
                    () - adapter.getFooterViewCount() ? space : 0;
            outRect.set(0, top, 0, 0);
        }
    }

    @OnClick({R.id.btn_back, R.id.btn_shadow_back})
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (headerViewHolder != null && headerViewHolder.sliderLayout != null && headerViewHolder
                .flowAdapter != null && headerViewHolder.flowAdapter.getCount() > 1) {
            headerViewHolder.sliderLayout.startAutoCycle();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (headerViewHolder != null && headerViewHolder.sliderLayout != null) {
            headerViewHolder.sliderLayout.stopAutoCycle();
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(refreshSubscriber, pageSubscriber);
    }
}
