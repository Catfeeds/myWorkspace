package me.suncloud.marrymemo.fragment.product;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalStaggeredRecyclerView;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.product.ShopProduct;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.views.fragments.HomePageScrollAbleFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.api.CommonApi;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.home.HomeFeedsFragmentCallBack;
import me.suncloud.marrymemo.adpter.product.ProductRecyclerAdapter;
import me.suncloud.marrymemo.view.MainActivity;
import me.suncloud.marrymemo.view.ProductMerchantActivity;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 婚品瀑布流列表
 * Created by mo_yu on 2016/11/18.
 */
public class ProductListFragment extends HomePageScrollAbleFragment implements
        PullToRefreshVerticalStaggeredRecyclerView.OnRefreshListener<RecyclerView> {
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalStaggeredRecyclerView recyclerView;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.btn_scroll_top)
    ImageButton btnScrollTop;
    protected View footerView;
    protected View endView;
    protected View loadView;
    protected ProductRecyclerAdapter adapter;
    protected String url;
    private boolean isShowTopBtn; //回到顶部的按钮是否显示着
    private boolean isShowProgressBar = true;
    private boolean isRefreshable = false;
    private int scrollPosition;
    private Unbinder unbinder;
    private HljHttpSubscriber refreshSub;
    private HljHttpSubscriber pageSub;
    protected String propertyId;

    public static ProductListFragment newInstance(String url) {
        ProductListFragment fragment = new ProductListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            url = getArguments().getString("url");
            propertyId = getArguments().getString("propertyId");
        }
        scrollPosition = 6;//默认是6，估算值 让初始化activity的时候 滚到顶部按钮不显示出来
        footerView = View.inflate(getContext(), R.layout.hlj_product_no_more_footer___cv, null);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        loadView.setVisibility(View.INVISIBLE);
        adapter = new ProductRecyclerAdapter(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout
                        .hlj_common_fragment_ptr_staggered_recycler_view___cm,
                container,
                false);
        unbinder = ButterKnife.bind(this, rootView);
        emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                onRefresh(null);
            }
        });
        ((SimpleItemAnimator) recyclerView.getRefreshableView()
                .getItemAnimator()).setSupportsChangeAnimations(false);
        if (isRefreshable) {
            recyclerView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            recyclerView.setOnRefreshListener(this);
        } else {
            recyclerView.setMode(PullToRefreshBase.Mode.DISABLED);
        }
        recyclerView.getRefreshableView()
                .setPadding(0,
                        0,
                        0,
                        getContext() instanceof MainActivity ? CommonUtil.dp2px(getContext(),
                                50) : 0);
        recyclerView.getRefreshableView()
                .addItemDecoration(new SpacesItemDecoration());
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setItemPrefetchEnabled(false);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        recyclerView.getRefreshableView()
                .setLayoutManager(layoutManager);
        adapter.setFooterView(footerView);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
        recyclerView.getRefreshableView()
                .addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        if (recyclerView == null || recyclerView.getLayoutManager() == null) {
                            return;
                        }
                        onRecyclerViewScrolled(recyclerView, dx, dy);
                    }
                });
        initTracker();
        return rootView;
    }

    void initTracker() {
        HljVTTagger.tagViewParentName(recyclerView, "product_chosen_list");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getContext() instanceof ProductMerchantActivity) {
            scrollPosition = 10;
        }
        onRefresh(null);
    }

    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        CommonUtil.unSubscribeSubs(refreshSub);
        refreshSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<ShopProduct>>>() {
                    @Override
                    public void onNext(HljHttpData<List<ShopProduct>> listHljHttpData) {
                        recyclerView.getRefreshableView()
                                .scrollToPosition(0);
                        recyclerView.getRefreshableView()
                                .setBackgroundColor(ContextCompat.getColor(getContext(),
                                        R.color.colorWhite));
                        adapter.setProducts(listHljHttpData.getData());
                        initPagination(listHljHttpData.getPageCount());
                        onSubNext(listHljHttpData);
                    }
                })
                .setOnErrorListener(new SubscriberOnErrorListener() {
                    @Override
                    public void onError(Object o) {
                        onSubError(o);
                    }
                })
                .setEmptyView(emptyView)
                .setContentView(recyclerView)
                .setPullToRefreshBase(recyclerView)
                .setProgressBar(!isShowProgressBar || (recyclerView.isRefreshing() && progressBar
                        .getVisibility() != View.VISIBLE) ? null : progressBar)
                .build();
        isShowProgressBar = true;
        CommonApi.getProductsObb(url, 1, HljCommon.PER_PAGE)
                .subscribe(refreshSub);
    }

    private void initPagination(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSub);
        Observable<HljHttpData<List<ShopProduct>>> pageObservable = PaginationTool
                .buildPagingObservable(
                recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpData<List<ShopProduct>>>() {
                    @Override
                    public Observable<HljHttpData<List<ShopProduct>>> onNextPage(int page) {
                        return CommonApi.getProductsObb(url, page, HljCommon.PER_PAGE);
                    }
                })
                .setLoadView(loadView)
                .setEndView(endView)
                .build()
                .getPagingObservable()
                .observeOn(AndroidSchedulers.mainThread());
        pageSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<ShopProduct>>>() {
                    @Override
                    public void onNext(HljHttpData<List<ShopProduct>> listHljHttpData) {
                        adapter.addProducts(listHljHttpData.getData());
                    }
                })
                .build();
        pageObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSub);
    }

    @OnClick(R.id.btn_scroll_top)
    public void onScrollTop() {
        recyclerView.getRefreshableView()
                .scrollToPosition(0);
        if (getContext() instanceof ProductMerchantActivity) {
            ((ProductMerchantActivity) getContext()).scrollToTop();
        }
    }

    //显示回到顶部的按钮
    private void showFiltrateAnimation() {
        if (isShowTopBtn) {
            return;
        }
        isShowTopBtn = true;
        if (btnScrollTop.getAnimation() == null || btnScrollTop.getAnimation()
                .hasEnded()) {
            Animation animation = AnimationUtils.loadAnimation(getContext(),
                    R.anim.slide_in_bottom2);
            animation.setFillBefore(true);
            btnScrollTop.startAnimation(animation);
            btnScrollTop.setVisibility(View.VISIBLE);
        }
    }

    //隐藏回到顶部的按钮
    private void hideFiltrateAnimation() {
        if (!isShowTopBtn) {
            return;
        }
        isShowTopBtn = false;
        if (btnScrollTop.getAnimation() == null || btnScrollTop.getAnimation()
                .hasEnded()) {
            Animation animation = AnimationUtils.loadAnimation(getContext(),
                    R.anim.slide_out_bottom2);
            animation.setFillAfter(true);
            btnScrollTop.startAnimation(animation);
        }
    }

    private class SpacesItemDecoration extends RecyclerView.ItemDecoration {

        private int space;
        private StaggeredGridLayoutManager.LayoutParams lp;

        SpacesItemDecoration() {
            this.space = CommonUtil.dp2px(getContext(), 8);
        }

        @Override
        public void getItemOffsets(
                Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            lp = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
            int top = 0;
            int left = 0;
            int right = 0;
            int position = parent.getChildAdapterPosition(view);
            if (position < adapter.getItemCount() - adapter.getFooterViewCount()) {
                top = space;
                left = lp.getSpanIndex() == 0 ? 0 : space / 2;
                right = lp.getSpanIndex() == 0 ? space / 2 : 0;
            }
            outRect.set(left, top, right, 0);
        }
    }

    public void setShowProgressBar(boolean isShowProgressBar) {
        this.isShowProgressBar = isShowProgressBar;
    }

    public void setRefreshable(boolean refreshable) {
        this.isRefreshable = refreshable;
        if (recyclerView == null) {
            return;
        }
        if (refreshable) {
            recyclerView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            recyclerView.setOnRefreshListener(this);
        } else {
            recyclerView.setMode(PullToRefreshBase.Mode.DISABLED);
        }
    }

    @Override
    public View getScrollableView() {
        return recyclerView == null ? null : recyclerView.getRefreshableView();
    }

    @Override
    public void refresh(Object... params) {
        if (recyclerView == null) {
            return;
        }
        if (params != null && params.length > 0) {
            url = (String) params[0];
        }
        onRefresh(null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
        CommonUtil.unSubscribeSubs(refreshSub, pageSub);
    }

    protected HomeFeedsFragmentCallBack getHomeFeedsFragmentCallBack() {
        if (getParentFragment() != null && getParentFragment() instanceof
                HomeFeedsFragmentCallBack) {
            return (HomeFeedsFragmentCallBack) getParentFragment();
        } else {
            return null;
        }
    }

    protected void onRecyclerViewScrolled(RecyclerView recyclerView, int dx, int dy) {
        if (recyclerView.getChildAdapterPosition(recyclerView.getChildAt(0)) < scrollPosition) {
            hideFiltrateAnimation();
        } else {
            showFiltrateAnimation();
        }
    }

    @Override
    public void scrollTop() {

    }

    protected void onSubNext(HljHttpData<List<ShopProduct>> listHljHttpData) {

    }

    protected void onSubError(Object o) {

    }
}