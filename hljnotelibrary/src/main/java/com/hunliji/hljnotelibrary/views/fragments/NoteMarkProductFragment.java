package com.hunliji.hljnotelibrary.views.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalStaggeredRecyclerView;
import com.hunliji.hljcommonlibrary.models.product.ShopProduct;
import com.hunliji.hljcommonlibrary.models.search.ProductSearchResultList;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.fragments.ScrollAbleFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljcommonviewlibrary.adapters.ProductRecyclerAdapter;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;
import com.hunliji.hljnotelibrary.R;
import com.hunliji.hljnotelibrary.R2;
import com.hunliji.hljnotelibrary.api.NoteApi;
import com.hunliji.hljnotelibrary.views.activities.NoteMarkDetailActivity;
import com.hunliji.hljnotelibrary.views.widgets.SpacesItemDecoration;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 标签详情页的婚品列表
 * Created by jinxin on 2017/7/5 0005.
 */

public class NoteMarkProductFragment extends ScrollAbleFragment implements PullToRefreshBase
        .OnRefreshListener<RecyclerView> {

    @BindView(R2.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R2.id.recycler_view)
    PullToRefreshVerticalStaggeredRecyclerView recyclerView;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R2.id.btn_scroll_top)
    ImageButton btnScrollTop;

    private String sort;
    private long tags;
    private Unbinder unbinder;
    private View footerView;
    private View endView;
    private View loadView;
    private HljHttpSubscriber refreshSubscriber;
    private HljHttpSubscriber pageSubscriber;
    private ArrayList<ShopProduct> productList;
    private ProductRecyclerAdapter adapter;
    private StaggeredGridLayoutManager layoutManager;

    public static NoteMarkProductFragment newInstance(long tags) {
        NoteMarkProductFragment productFragment = new NoteMarkProductFragment();
        Bundle arg = new Bundle();
        arg.putLong("tags", tags);
        productFragment.setArguments(arg);
        return productFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tags = getArguments().getLong("tags");
        }
        sort = NoteMarkDetailActivity.SORT_DEFAULT;
        productList = new ArrayList<>();
        footerView = View.inflate(getContext(), R.layout.hlj_product_no_more_footer___cv, null);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        loadView.setVisibility(View.INVISIBLE);
        adapter = new ProductRecyclerAdapter(getContext(), productList);
        adapter.setFooterView(footerView);
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
        initWidget();
        return rootView;
    }

    private void initWidget() {
        ((SimpleItemAnimator) recyclerView.getRefreshableView()
                .getItemAnimator()).setSupportsChangeAnimations(false);
        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setItemPrefetchEnabled(false);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        recyclerView.getRefreshableView()
                .addItemDecoration(new SpacesItemDecoration(getContext(), 0, 1));
        recyclerView.getRefreshableView()
                .setLayoutManager(layoutManager);
        recyclerView.setOnRefreshListener(this);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onRefresh(recyclerView);
    }

    @Override
    public void refresh(Object... params) {
        if (params.length > 0) {
            sort = (String) params[0];
            onRefresh(null);
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        if (refreshSubscriber != null && !refreshSubscriber.isUnsubscribed()) {
            return;
        }

        refreshSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                .setProgressBar(refreshView == null ? progressBar : null)
                .setPullToRefreshBase(recyclerView)
                .setContentView(refreshView)
                .setEmptyView(emptyView)
                .setOnNextListener(new SubscriberOnNextListener<ProductSearchResultList>() {
                    @Override
                    public void onNext(ProductSearchResultList productSearchResultList) {
                        if (productSearchResultList == null || productSearchResultList
                                .getProducts() == null) {
                            emptyView.showEmptyView();
                            return;
                        }
                        productList.clear();
                        productList.addAll(productSearchResultList.getProducts());
                        initPagination(productSearchResultList.getPageCount());
                        if (productList.isEmpty()) {
                            emptyView.showEmptyView();
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            emptyView.hideEmptyView();
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                        recyclerView.getRefreshableView()
                                .scrollToPosition(0);
                        adapter.notifyDataSetChanged();
                    }
                })
                .build();

        Observable<ProductSearchResultList> obb = NoteApi.getMarkProductList(tags, sort, 1);
        obb.subscribe(refreshSubscriber);
    }

    private void initPagination(int pageCount) {
        if (pageSubscriber != null && !pageSubscriber.isUnsubscribed()) {
            return;
        }

        Observable<ProductSearchResultList> noteObb = PaginationTool.buildPagingObservable(
                recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<ProductSearchResultList>() {
                    @Override
                    public Observable<ProductSearchResultList> onNextPage(int page) {
                        return NoteApi.getMarkProductList(tags, sort, page);
                    }
                })
                .setLoadView(loadView)
                .setEndView(endView)
                .build()
                .getPagingObservable()
                .observeOn(AndroidSchedulers.mainThread());
        pageSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<ProductSearchResultList>() {
                    @Override
                    public void onNext(ProductSearchResultList productSearchResultList) {
                        if (productSearchResultList != null && productSearchResultList
                                .getProducts() != null) {
                            productList.addAll(productSearchResultList.getProducts());
                        }
                        adapter.notifyDataSetChanged();
                    }
                })
                .build();
        noteObb.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSubscriber);
    }

    @Override
    public View getScrollableView() {
        return recyclerView.getRefreshableView();
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        CommonUtil.unSubscribeSubs(refreshSubscriber, pageSubscriber);
        super.onDestroyView();
    }

}
