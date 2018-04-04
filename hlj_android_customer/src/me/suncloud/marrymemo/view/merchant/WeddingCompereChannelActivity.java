package me.suncloud.marrymemo.view.merchant;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.models.modelwrappers.MerchantServiceFilter;
import com.hunliji.hljcommonlibrary.models.search.WorksSearchResult;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljcommonviewlibrary.widgets.ServiceWorkFilterViewHolder;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.work_case.WeddingCompereAdapter;
import me.suncloud.marrymemo.adpter.work_case.WeddingCompereHeaderAdapter;
import me.suncloud.marrymemo.api.work_case.WorkApi;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.util.NoticeUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.notification.MessageHomeActivity;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * Created by hua_rong on 2017/7/31.
 * 婚礼司仪
 */

public class WeddingCompereChannelActivity extends HljBaseNoBarActivity implements
        PullToRefreshVerticalRecyclerView.OnRefreshListener {

    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.msg_notice)
    View msgNotice;
    @BindView(R.id.msg_count)
    TextView msgCount;
    @BindView(R.id.service_filter_bottom_layout)
    RelativeLayout serviceFilterBottomLayout;
    private View footerView;
    private TextView endView;
    private View loadView;
    private View headerView;
    private HeaderViewHolder headerViewHolder;
    private long propertyId;
    private NoticeUtil noticeUtil;
    private HljHttpSubscriber refreshSubscriber;
    private HljHttpSubscriber pageSubscriber;
    private MerchantServiceFilter serviceFilter;
    private WeddingCompereAdapter adapter;
    private String sort = "score";
    private Context context;
    private ServiceWorkFilterViewHolder serviceWorkFilterViewHolder;
    private long cid;
    private Subscription rxBusEventSub;


    @Override
    public String pageTrackTagName() {
        return "婚礼司仪二级页";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wedding_compere);
        setDefaultStatusBarPadding();
        ButterKnife.bind(this);
        context = this;
        initFootView();
        initHeadView();
        initValues();
        initView();
        onRefresh(recyclerView);
        initErrorView();
        registerRxBusEvent();

        initTracker();
    }


    private void initTracker() {
        HljVTTagger.tagViewParentName(recyclerView.getRefreshableView(), "package_list");
    }

    private void initValues() {
        serviceFilter = new MerchantServiceFilter();
        City city = Session.getInstance()
                .getMyCity(this);
        if (city != null) {
            cid = city.getId();
        }
        propertyId = Merchant.PROPERTY_WEDDING_COMPERE;
        noticeUtil = new NoticeUtil(this, msgCount, msgNotice);
        noticeUtil.onResume();
    }

    private void initHeadView() {
        headerView = View.inflate(this, R.layout.property_wedding_compere_header, null);
        headerViewHolder = new HeaderViewHolder(headerView);
    }

    private void initFootView() {
        footerView = View.inflate(this, R.layout.list_foot_no_more_2, null);
        endView = (TextView) footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
    }

    private void initView() {
        tvTitle.setText(getTitle());
        recyclerView.setOnRefreshListener(this);
        RecyclerView mRecyclerView = recyclerView.getRefreshableView();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == adapter.getHeaderViewCount() - 1 || position == adapter
                        .getItemCount() - adapter.getFooterViewCount()) {
                    return 2;
                } else {
                    return 1;
                }
            }
        });
        mRecyclerView.setLayoutManager(gridLayoutManager);
        adapter = new WeddingCompereAdapter(context);
        adapter.setHeaderView(headerView);
        adapter.setFooterView(footerView);
        mRecyclerView.addItemDecoration(new CompereItemDecoration(context));
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorWhite));
        setBottomView();
    }

    public void setBottomView() {
        serviceWorkFilterViewHolder = ServiceWorkFilterViewHolder.newInstance(this,
                cid,
                propertyId,
                new ServiceWorkFilterViewHolder.OnFilterResultListener() {
                    @Override
                    public void onFilterResult(
                            String sort,
                            long cid,
                            long areaId,
                            double minPrice,
                            double maxPrice,
                            List<String> tags) {
                        WeddingCompereChannelActivity.this.sort = sort;
                        WeddingCompereChannelActivity.this.cid = cid;
                        serviceFilter.setShopAreaId(areaId);
                        serviceFilter.setPriceMin(minPrice);
                        serviceFilter.setPriceMax(maxPrice);
                        serviceFilter.setTags(tags);
                        onRefresh(recyclerView);
                    }
                });
        serviceFilterBottomLayout.removeAllViews();
        serviceFilterBottomLayout.addView(serviceWorkFilterViewHolder.getRootView());
    }


    @OnClick(R.id.btn_back)
    public void onClick() {
        super.onBackPressed();
    }


    @OnClick(R.id.msg_layout)
    public void onOkButtonClick() {
        if (Util.loginBindChecked(this, Constants.RequestCode.NOTIFICATION_PAGE)) {
            Intent intent = new Intent(this, MessageHomeActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (noticeUtil != null) {
            noticeUtil.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (noticeUtil != null) {
            noticeUtil.onPause();
        }
    }


    private void initErrorView() {
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                onRefresh(recyclerView);
            }
        });
    }

    @Override
    public void onRefresh(final PullToRefreshBase refreshView) {
        if (refreshSubscriber == null || refreshSubscriber.isUnsubscribed()) {
            Observable<WorksSearchResult> aObservable = WorkApi.getWorksObb(cid,
                    propertyId,
                    0,
                    serviceFilter,
                    null,
                    sort,
                    1,
                    HljCommon.PER_PAGE);
            Observable<HljHttpData<List<Merchant>>> bObservable = WorkApi.getMerchantList
                    (propertyId);
            Observable observable = Observable.zip(aObservable,
                    bObservable,
                    new Func2<WorksSearchResult, HljHttpData<List<Merchant>>, ResultZip>() {
                        @Override
                        public ResultZip call(
                                WorksSearchResult worksSearchResult,
                                HljHttpData<List<Merchant>> hljHttpData) {
                            return new ResultZip(worksSearchResult, hljHttpData);
                        }
                    });

            refreshSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<ResultZip>() {
                        @Override
                        public void onNext(ResultZip resultZip) {
                            serviceFilterBottomLayout.setVisibility(View.VISIBLE);
                            headerViewHolder.setHeaderView(resultZip.merchantsData.getData());
                            int pageCount = 0;
                            List<Work> works = null;
                            if (resultZip.worksSearchResult != null) {
                                pageCount = resultZip.worksSearchResult.getPageCount();
                                works = resultZip.worksSearchResult.getWorkList();
                            }
                            if (CommonUtil.isCollectionEmpty(works)) {
                                emptyView.showEmptyView();
                                recyclerView.setVisibility(View.GONE);
                            }
                            adapter.setWorks(works);
                            initPagination(pageCount);
                        }
                    })
                    .setDataNullable(true)
                    .setPullToRefreshBase(recyclerView)
                    .setContentView(recyclerView)
                    .setProgressBar(recyclerView.isRefreshing() ? null : progressBar)
                    .build();
            observable.subscribe(refreshSubscriber);
        }
    }

    private void initPagination(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSubscriber);
        Observable<WorksSearchResult> observable = PaginationTool.buildPagingObservable
                (recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<WorksSearchResult>() {
                    @Override
                    public Observable<WorksSearchResult> onNextPage(int page) {
                        return WorkApi.getWorksObb(cid,
                                propertyId,
                                0,
                                serviceFilter,
                                null,
                                sort,
                                page,
                                HljCommon.PER_PAGE);
                    }
                })
                .setLoadView(loadView)
                .setEndView(endView)
                .build()
                .getPagingObservable()
                .observeOn(AndroidSchedulers.mainThread());
        pageSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<WorksSearchResult>() {
                    @Override
                    public void onNext(WorksSearchResult worksSearchResult) {
                        adapter.addWorks(worksSearchResult.getWorkList());
                    }
                })
                .build();
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSubscriber);
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(refreshSubscriber, pageSubscriber, rxBusEventSub);
        if (serviceWorkFilterViewHolder != null) {
            serviceWorkFilterViewHolder.onDestroy();
        }
    }

    public class HeaderViewHolder {
        @BindView(R.id.rv_header)
        RecyclerView rvHeader;
        @BindView(R.id.ll_item_view)
        LinearLayout llItemView;
        private Context context;

        HeaderViewHolder(View view) {
            ButterKnife.bind(this, view);
            context = view.getContext();

            initTracker();
        }


        private void initTracker() {
            HljVTTagger.tagViewParentName(rvHeader, "recommend_merchant_list");
        }

        public void setHeaderView(List<Merchant> merchantList) {
            if (!CommonUtil.isCollectionEmpty(merchantList)) {
                llItemView.setVisibility(View.VISIBLE);
                if (rvHeader.getAdapter() != null) {
                    WeddingCompereHeaderAdapter adapter = (WeddingCompereHeaderAdapter) rvHeader
                            .getAdapter();
                    adapter.setMerchantList(merchantList);
                } else {
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                    linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                    rvHeader.setLayoutManager(linearLayoutManager);
                    WeddingCompereHeaderAdapter headerAdapter = new WeddingCompereHeaderAdapter(
                            context,
                            merchantList);
                    rvHeader.setAdapter(headerAdapter);
                    rvHeader.setPadding(CommonUtil.dp2px(context, 4), 0, 0, 0);
                }
            }
        }
    }

    private class ResultZip {
        WorksSearchResult worksSearchResult;
        HljHttpData<List<Merchant>> merchantsData;

        public ResultZip(
                WorksSearchResult worksSearchResult, HljHttpData<List<Merchant>> merchantsData) {
            this.worksSearchResult = worksSearchResult;
            this.merchantsData = merchantsData;
        }
    }

    public class CompereItemDecoration extends RecyclerView.ItemDecoration {

        private int left;
        private int space;
        private int right;

        public CompereItemDecoration(Context context) {
            left = CommonUtil.dp2px(context, 10);
            space = CommonUtil.dp2px(context, 4);
            right = CommonUtil.dp2px(context, 10);
        }

        @Override
        public void getItemOffsets(
                Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            if (position >= adapter.getHeaderViewCount() && position < adapter.getItemCount() -
                    adapter.getFooterViewCount()) {
                if ((position - 1) % 2 == 0) {
                    outRect.right = space;
                    outRect.left = left;
                } else {
                    outRect.left = space;
                    outRect.right = right;
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (serviceWorkFilterViewHolder != null && serviceWorkFilterViewHolder.isShow()) {
            serviceWorkFilterViewHolder.hideFilterView();
            return;
        }
        super.onBackPressed();
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
                                    City rxCity = (City) rxEvent.getObject();
                                    if (rxCity != null && !rxCity.getId()
                                            .equals(cid)) {
                                        cid = rxCity.getId();
                                        if (serviceWorkFilterViewHolder != null) {
                                            serviceFilter.setShopAreaId(0);
                                            serviceWorkFilterViewHolder.refreshArea(cid);
                                            onRefresh(null);
                                        }
                                    }
                                    break;
                            }
                        }
                    });
        }
    }

}
