package me.suncloud.marrymemo.fragment.work_case;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.models.modelwrappers.MerchantServiceFilter;
import com.hunliji.hljcommonlibrary.models.search.WorksSearchResult;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.views.fragments.ScrollAbleFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.work_case.BaseMerchantServiceWorkRecyclerAdapter;
import me.suncloud.marrymemo.adpter.work_case.MerchantServiceCpmAdapter;
import me.suncloud.marrymemo.api.work_case.WorkApi;
import me.suncloud.marrymemo.util.Session;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 通用服务套餐fragment
 * Created by chen_bin on 2017/8/2 0002.
 */
public abstract class BaseMerchantServiceWorkListFragment extends ScrollAbleFragment implements
        PullToRefreshBase.OnRefreshListener<RecyclerView> {

    public static final String ARG_PARENT_NAME = "parent_name";

    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    protected View footerView;
    private View endView;
    private View loadView;
    protected View cpmView;
    private CpmViewHolder cpmViewHolder;
    protected BaseMerchantServiceWorkRecyclerAdapter adapter;
    private MerchantServiceCpmAdapter cpmAdapter;
    protected ArrayList<Work> works;
    protected ArrayList<Merchant> merchants;
    private long cid;
    protected MerchantServiceFilter serviceFilter;
    private String keyword;
    private String sort = "score";
    private boolean isNeedRefresh;
    private long propertyId;
    private long categoryId;
    private long categoryMarkId;
    private long secondCategoryId;
    private Unbinder unbinder;
    private HljHttpSubscriber cpmSub;
    private HljHttpSubscriber refreshSub;
    private HljHttpSubscriber pageSub;
    private String parentName;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            keyword = getArguments().getString("keyword");
            propertyId = getArguments().getLong("property_id");
            categoryId = getArguments().getLong("category_id");
            categoryMarkId = getArguments().getLong("category_mark_id");
            secondCategoryId = getArguments().getLong("filter_second_category");
            serviceFilter = new MerchantServiceFilter();
            serviceFilter.setCategoryMarkId(categoryMarkId);
            serviceFilter.setSaleWay(getArguments().getInt("sale_way"));
            serviceFilter.setFilterSecondCategory(secondCategoryId);
            parentName = getArguments().getString(ARG_PARENT_NAME);
        }
        works = new ArrayList<>();
        merchants = new ArrayList<>();
        cid = Session.getInstance()
                .getMyCity(getContext())
                .getId();
        footerView = View.inflate(getContext(), R.layout.hlj_foot_no_more___cm, null);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        loadView.setVisibility(View.INVISIBLE);
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.hlj_common_fragment_ptr_recycler_view___cm,
                container,
                false);
        unbinder = ButterKnife.bind(this, rootView);
        initViews();
        initCpmView();
        initTracker();
        return rootView;
    }

    private void initCpmView() {
        if (hasCpm()) {
            cpmView = View.inflate(getContext(), R.layout.service_merchant_cmp_layout, null);
            cpmViewHolder = new CpmViewHolder(cpmView);
            cpmAdapter = new MerchantServiceCpmAdapter(getContext(), merchants);
            cpmViewHolder.cpmRecyclerView.setFocusable(false);
            cpmViewHolder.cpmRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                    LinearLayoutManager.HORIZONTAL,
                    false));
            cpmViewHolder.cpmRecyclerView.setAdapter(cpmAdapter);
        }
    }

    private void initTracker() {
        HljVTTagger.tagViewParentName(recyclerView.getRefreshableView(), getParentName());
    }

    private String getParentName() {
        if (!TextUtils.isEmpty(parentName)) {
            return parentName;
        }
        return "package_list";
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onRefresh(null);
    }

    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        getCpmList();
        CommonUtil.unSubscribeSubs(refreshSub);
        refreshSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<WorksSearchResult>() {
                    @Override
                    public void onNext(WorksSearchResult worksSearchResult) {
                        recyclerView.getRefreshableView()
                                .scrollToPosition(0);
                        adapter.setWorks(worksSearchResult.getWorkList());
                        initPagination(worksSearchResult.getPageCount());
                    }
                })
                .setEmptyView(emptyView)
                .setContentView(recyclerView)
                .setPullToRefreshBase(recyclerView)
                .setProgressBar(recyclerView.isRefreshing() && progressBar.getVisibility() !=
                        View.VISIBLE ? null : progressBar)
                .build();
        WorkApi.getWorksObb(cid,
                propertyId,
                categoryId,
                serviceFilter,
                keyword,
                sort,
                1,
                HljCommon.PER_PAGE)
                .subscribe(refreshSub);
    }

    //只有精选tab需要获取cpm广告
    private void getCpmList() {
        if (!hasCpm()) {
            return;
        }
        CommonUtil.unSubscribeSubs(cpmSub);
        cpmSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<Merchant>>>() {
                    @Override
                    public void onNext(HljHttpData<List<Merchant>> listHljHttpData) {
                        if (cpmViewHolder != null) {
                            cpmViewHolder.cpmRecyclerView.scrollToPosition(0);
                        }
                        if (listHljHttpData.getData()
                                .size() < 3) {
                            adapter.setCpmView(null);
                        } else {
                            merchants.clear();
                            merchants.addAll(listHljHttpData.getData());
                            cpmAdapter.notifyDataSetChanged();
                            adapter.setCpmView(cpmView);
                        }
                        adapter.notifyDataSetChanged();
                    }
                })
                .build();
        WorkApi.getCPMMerchantList(propertyId)
                .subscribe(cpmSub);
    }

    private void initPagination(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSub);
        Observable<WorksSearchResult> observable = PaginationTool.buildPagingObservable
                (recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<WorksSearchResult>() {
                    @Override
                    public Observable<WorksSearchResult> onNextPage(int page) {
                        return WorkApi.getWorksObb(cid,
                                propertyId,
                                categoryId,
                                serviceFilter,
                                keyword,
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
        pageSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<WorksSearchResult>() {
                    @Override
                    public void onNext(WorksSearchResult worksSearchResult) {
                        adapter.addWorks(worksSearchResult.getWorkList());
                    }
                })
                .build();
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSub);
    }

    private boolean hasCpm() {
        return categoryId == 0 && categoryMarkId == 0 && TextUtils.isEmpty(keyword) && propertyId
                != Merchant.PROPERTY_WEDDING_SHOOTING && propertyId != Merchant
                .PROPERTY_WEDDING_COMPERE && propertyId != Merchant.PROPERTY_WEDDING_DRESS;
    }

    public void setNeedRefresh(boolean needRefresh) {
        this.isNeedRefresh = needRefresh;
    }

    public void setFilterParams(long cid, String sort, MerchantServiceFilter serviceFilter) {
        this.cid = cid;
        this.sort = sort;
        this.serviceFilter.setShopAreaId(serviceFilter.getShopAreaId());
        this.serviceFilter.setPriceMax(serviceFilter.getPriceMax());
        this.serviceFilter.setPriceMin(serviceFilter.getPriceMin());
        this.serviceFilter.setTags(serviceFilter.getTags());
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isNeedRefresh) {
            isNeedRefresh = false;
            refresh();
        }
    }

    @Override
    public View getScrollableView() {
        return recyclerView == null ? null : recyclerView.getRefreshableView();
    }

    @Override
    public void refresh(Object... params) {
        if (recyclerView != null) {
            onRefresh(null);
        }
    }

    protected abstract void initViews();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
        CommonUtil.unSubscribeSubs(refreshSub, pageSub, cpmSub);
    }

    class CpmViewHolder {
        @BindView(R.id.cpm_recycler_view)
        RecyclerView cpmRecyclerView;
        @BindView(R.id.merchant_cpm_view)
        LinearLayout merchantCpmView;

        CpmViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
