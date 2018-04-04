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
import com.hunliji.hljcommonlibrary.models.product.ShopCategory;
import com.hunliji.hljcommonlibrary.models.search.ProductSearchResult;
import com.hunliji.hljcommonlibrary.models.search.SearchFilter;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.fragments.ScrollAbleFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljcommonviewlibrary.widgets.ServiceProductFilterViewHolder;
import com.hunliji.hljhttplibrary.api.search.SearchApi;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.product.ProductRecyclerAdapter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 全国优选婚纱礼服婚品瀑布流列表
 * Created by mo_yu on 2017/8/4.
 */
public class WeddingDressProductListFragment extends ScrollAbleFragment implements
        PullToRefreshVerticalStaggeredRecyclerView.OnRefreshListener<RecyclerView> {
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalStaggeredRecyclerView recyclerView;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.btn_scroll_top)
    ImageButton btnScrollTop;
    private Unbinder unbinder;
    private ServiceProductFilterViewHolder serviceProductFilterViewHolder;
    private View footerView;
    private View endView;
    private View loadView;
    private StaggeredGridLayoutManager layoutManager;
    protected ProductRecyclerAdapter adapter;

    protected long categoryId;
    private String sort = "score";
    private SearchFilter filter;
    private boolean isShowTopBtn; //回到顶部的按钮是否显示着

    private HljHttpSubscriber refreshSub;
    private HljHttpSubscriber pageSub;

    public static WeddingDressProductListFragment newInstance(long categoryId) {
        WeddingDressProductListFragment fragment = new WeddingDressProductListFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("category_id", categoryId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        footerView = View.inflate(getContext(), R.layout.hlj_foot_no_more___cm, null);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        loadView.setVisibility(View.INVISIBLE);
        filter = new SearchFilter();
        adapter = new ProductRecyclerAdapter(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_wedding_dress_product_list,
                container,
                false);
        unbinder = ButterKnife.bind(this, rootView);
        if (getArguments() != null) {
            categoryId = getArguments().getLong("category_id", 0);
        }
        filter.setCategoryId(categoryId);
        emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                onRefresh(null);
            }
        });
        ((SimpleItemAnimator) recyclerView.getRefreshableView()
                .getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.getRefreshableView()
                .addItemDecoration(new SpacesItemDecoration());
        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setItemPrefetchEnabled(false);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        recyclerView.getRefreshableView()
                .setLayoutManager(layoutManager);
        recyclerView.setOnRefreshListener(this);
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
                        if (recyclerView.getChildAdapterPosition(recyclerView.getChildAt(0)) < 5) {
                            hideFiltrateAnimation();
                        } else {
                            showFiltrateAnimation();
                        }
                    }
                });
        setBottomView();
        return rootView;
    }

    private void setBottomView() {
        final WeddingDressProductHomeFragment productHomeFragment =
                (WeddingDressProductHomeFragment) getParentFragment();
        if (productHomeFragment != null && categoryId == 1) {
            final ArrayList<ShopCategory> shopCategories = productHomeFragment.getChildCategories();
            serviceProductFilterViewHolder = ServiceProductFilterViewHolder.newInstance
                    (getContext(),

                    categoryId, new ServiceProductFilterViewHolder.OnFilterResultListener() {
                        @Override
                        public void onFilterResult(
                                String sort, double maxPrice, double minPrice, List<String> tags) {

                        }

                        @Override
                        public void onFilterResult(
                                String productSort,
                                double maxPrice,
                                double minPrice,
                                long categoryId) {
                            if (categoryId > 0) {
                                productHomeFragment.setCurrentItem(categoryId);
                            } else {
                                sort = productSort;
                                filter.setPriceMin(minPrice);
                                filter.setPriceMax(maxPrice);
                                onRefresh(null);
                            }
                        }
                    });
            serviceProductFilterViewHolder.setAllProperties(shopCategories);
        } else {
            serviceProductFilterViewHolder = ServiceProductFilterViewHolder.newInstance
                    (getContext(),
                    categoryId,
                    new ServiceProductFilterViewHolder.OnFilterResultListener() {
                        @Override
                        public void onFilterResult(
                                String productSort,
                                double minPrice,
                                double maxPrice,
                                List<String> tags) {
                            sort = productSort;
                            filter.setPriceMin(minPrice);
                            filter.setPriceMax(maxPrice);
                            if (!CommonUtil.isCollectionEmpty(tags)) {
                                StringBuilder builder = new StringBuilder();
                                for (int i = 0; i < tags.size(); i++) {
                                    builder.append(tags.get(i))
                                            .append(",");
                                }
                                if (builder.length() > 0) {
                                    builder.deleteCharAt(builder.lastIndexOf(","));
                                    filter.setTags(builder.toString());
                                }
                            } else {
                                filter.setTags(null);
                            }
                            onRefresh(null);
                        }

                        @Override
                        public void onFilterResult(
                                String sort, double maxPrice, double minPrice, long categoryId) {

                        }
                    });
        }
    }

    public View getFilterView() {
        if (serviceProductFilterViewHolder != null) {
            return serviceProductFilterViewHolder.getRootView();
        }
        return null;
    }

    public boolean isShowFilterView() {
        return serviceProductFilterViewHolder != null && serviceProductFilterViewHolder.isShow();
    }

    public void hideFilterView() {
        if (serviceProductFilterViewHolder != null) {
            serviceProductFilterViewHolder.hideFilterView();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onRefresh(null);
    }

    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        CommonUtil.unSubscribeSubs(refreshSub);
        refreshSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<ProductSearchResult>() {
                    @Override
                    public void onNext(ProductSearchResult productSearchResult) {
                        recyclerView.getRefreshableView()
                                .scrollToPosition(0);
                        recyclerView.getRefreshableView()
                                .setBackgroundColor(ContextCompat.getColor(getContext(),
                                        R.color.colorWhite));
                        adapter.setProducts(productSearchResult.getProductList()
                                .getProducts());
                        initPagination(productSearchResult.getProductList()
                                .getPageCount());
                    }
                })
                .setEmptyView(emptyView)
                .setContentView(recyclerView)
                .setPullToRefreshBase(recyclerView)
                .setProgressBar(recyclerView.isRefreshing() && progressBar.getVisibility() !=
                        View.VISIBLE ? null : progressBar)
                .build();
        //婚品列表
        SearchApi.searchProduct(0, "", SearchApi.SubType.SUB_SEARCH_TYPE_PRODUCT, filter, sort, 1)
                .subscribe(refreshSub);
    }

    private void initPagination(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSub);
        Observable<ProductSearchResult> pageObservable = PaginationTool.buildPagingObservable(
                recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<ProductSearchResult>() {
                    @Override
                    public Observable<ProductSearchResult> onNextPage(int page) {
                        return SearchApi.searchProduct(0,
                                "",
                                SearchApi.SubType.SUB_SEARCH_TYPE_PRODUCT,
                                filter,
                                sort,
                                page);
                    }
                })
                .setLoadView(loadView)
                .setEndView(endView)
                .build()
                .getPagingObservable()
                .observeOn(AndroidSchedulers.mainThread());
        pageSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<ProductSearchResult>() {
                    @Override
                    public void onNext(ProductSearchResult productSearchResult) {
                        adapter.addProducts(productSearchResult.getProductList()
                                .getProducts());
                    }
                })
                .build();
        pageObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSub);
    }

    @OnClick(R.id.btn_scroll_top)
    public void onScrollTop() {
        if (layoutManager != null) {
            layoutManager.scrollToPositionWithOffset(0, 0);
        }
    }

    //显示回到顶部的按钮
    private void showFiltrateAnimation() {
        if (isShowTopBtn) {
            return;
        }
        if (btnScrollTop.getAnimation() == null || btnScrollTop.getAnimation()
                .hasEnded()) {
            isShowTopBtn = true;
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
        if (btnScrollTop.getAnimation() == null || btnScrollTop.getAnimation()
                .hasEnded()) {
            isShowTopBtn = false;
            Animation animation = AnimationUtils.loadAnimation(getContext(),
                    R.anim.slide_out_bottom2);
            animation.setFillAfter(true);
            btnScrollTop.startAnimation(animation);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
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

    @Override
    public View getScrollableView() {
        return recyclerView == null ? null : recyclerView.getRefreshableView();
    }

    @Override
    public void refresh(Object... params) {
        onRefresh(null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CommonUtil.unSubscribeSubs(refreshSub, pageSub);
        if (serviceProductFilterViewHolder != null) {
            serviceProductFilterViewHolder.onDestroy();
        }
    }
}