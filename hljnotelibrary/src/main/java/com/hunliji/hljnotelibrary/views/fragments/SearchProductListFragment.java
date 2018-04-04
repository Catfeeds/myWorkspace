package com.hunliji.hljnotelibrary.views.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.models.note.NoteSpotEntity;
import com.hunliji.hljcommonlibrary.models.product.ShopProduct;
import com.hunliji.hljcommonlibrary.models.search.ProductSearchResult;
import com.hunliji.hljcommonlibrary.models.search.ProductSearchResultList;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljhttplibrary.api.search.SearchApi;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;
import com.hunliji.hljnotelibrary.R;
import com.hunliji.hljnotelibrary.R2;
import com.hunliji.hljnotelibrary.adapters.SearchProductListAdapter;
import com.hunliji.hljnotelibrary.adapters.viewholder.SearchCustomBriefInfoViewHolder;
import com.hunliji.hljnotelibrary.adapters.viewholder.SearchProductBriefInfoViewHolder;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 笔记婚品搜索
 * Created by chen_bin on 2017/6/28 0028.
 */
public class SearchProductListFragment extends RefreshFragment implements
        SearchProductBriefInfoViewHolder.OnSelectProductListener, SearchCustomBriefInfoViewHolder
        .OnSelectCustomListener {
    @BindView(R2.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    private View customView;
    private View footerView;
    private View endView;
    private View loadView;
    private SearchProductListAdapter adapter;
    private InputMethodManager imm;
    private String keyword;
    private int currentPage;
    private Unbinder unbinder;
    private HljHttpSubscriber initSub;
    private HljHttpSubscriber pageSub;
    private final static String sort = "score";

    public static SearchProductListFragment newInstance(String keyword) {
        SearchProductListFragment fragment = new SearchProductListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("keyword", keyword);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            keyword = getArguments().getString("keyword");
        }
        imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        customView = View.inflate(getContext(),
                R.layout.search_custom_brief_info_list_item___note,
                null);
        footerView = View.inflate(getContext(), R.layout.hlj_foot_no_more___cm, null);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        loadView.setVisibility(View.INVISIBLE);
        adapter = new SearchProductListAdapter(getContext());
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
        return rootView;
    }

    private void initViews() {
        ((SimpleItemAnimator) recyclerView.getRefreshableView()
                .getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setNeedChangeSize(false);
        recyclerView.setMode(PullToRefreshBase.Mode.DISABLED);
        recyclerView.getRefreshableView()
                .setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.setCustomView(customView);
        adapter.setFooterView(footerView);
        adapter.setOnSelectProductListener(this);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
        recyclerView.getRefreshableView()
                .addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                        if (imm != null && getActivity().getCurrentFocus() != null) {
                            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus()
                                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        }
                    }
                });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initLoad();
    }

    private void initLoad() {
        CommonUtil.unSubscribeSubs(initSub);
        initSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<ProductSearchResult>() {
                    @Override
                    public void onNext(ProductSearchResult productSearchResult) {
                        setProductsData(productSearchResult);
                    }
                })
                .setOnErrorListener(new SubscriberOnErrorListener() {
                    @Override
                    public void onError(Object o) {
                        setProductsData(null);
                    }
                })
                .setDataNullable(true)
                .setProgressBar(progressBar)
                .build();
        SearchApi.searchProduct(0,
                keyword,
                SearchApi.SubType.SUB_SEARCH_TYPE_PRODUCT,
                null,
                sort,
                1)
                .subscribe(initSub);
    }

    private void setProductsData(ProductSearchResult productSearchResult) {
        recyclerView.getRefreshableView()
                .scrollToPosition(0);
        int totalCount = 0;
        int pageCount = 0;
        List<ShopProduct> products = null;
        if (productSearchResult != null && !productSearchResult.getProductList()
                .isEmpty()) {
            ProductSearchResultList productList = productSearchResult.getProductList();
            totalCount = productList.getTotal();
            pageCount = productList.getPageCount();
            products = productList.getProducts();
        }
        if (getParentFragment() instanceof SearchNoteSpotEntityFragment) {
            ((SearchNoteSpotEntityFragment) getParentFragment()).setProductCount(totalCount);
        }
        SearchCustomBriefInfoViewHolder customBriefInfoViewHolder =
                (SearchCustomBriefInfoViewHolder) customView.getTag();
        if (customBriefInfoViewHolder == null) {
            customBriefInfoViewHolder = new SearchCustomBriefInfoViewHolder(customView);
            customBriefInfoViewHolder.setOnSelectCustomListener(SearchProductListFragment.this);
            customView.setTag(customBriefInfoViewHolder);
        }
        boolean isDataEmpty = CommonUtil.isCollectionEmpty(products);
        customBriefInfoViewHolder.setTitle(getString(R.string
                .label_unrecorded_product_hint___note));
        customBriefInfoViewHolder.setSubTitle(getString(R.string.label_custom_product_name___note,
                keyword));
        customBriefInfoViewHolder.setShowCustomView(isDataEmpty || pageCount <= 1);
        customBriefInfoViewHolder.setShowTopLineView(!isDataEmpty && pageCount <= 1);
        adapter.setProducts(products);
        initPagination(pageCount);
    }

    private void initPagination(final int pageCount) {
        CommonUtil.unSubscribeSubs(pageSub);
        Observable<ProductSearchResult> observable = PaginationTool.buildPagingObservable(
                recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<ProductSearchResult>() {
                    @Override
                    public Observable<ProductSearchResult> onNextPage(int page) {
                        currentPage = page;
                        return SearchApi.searchProduct(0,
                                keyword,
                                SearchApi.SubType.SUB_SEARCH_TYPE_PRODUCT,
                                null,
                                sort,
                                page);
                    }
                })
                .setLoadView(loadView)
                .setEndView(endView)
                .build()
                .getPagingObservable();
        pageSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<ProductSearchResult>() {
                    @Override
                    public void onNext(ProductSearchResult productSearchResult) {
                        SearchCustomBriefInfoViewHolder customViewHolder =
                                (SearchCustomBriefInfoViewHolder) customView.getTag();
                        if (customViewHolder != null && currentPage >= pageCount) {
                            customViewHolder.setShowCustomView(true);
                            customViewHolder.setShowTopLineView(true);
                        }
                        adapter.addProducts(productSearchResult.getProductList()
                                .getProducts());
                    }
                })
                .build();
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSub);
    }

    @Override
    public void onSelectProduct(ShopProduct product) {
        if (product != null && product.getId() > 0) {
            setResult(product.getId(), product.getTitle());
        }
    }

    @Override
    public void onSelectCustom() {
        setResult(0, keyword);
    }

    private void setResult(long id, String title) {
        NoteSpotEntity noteSpotEntity = new NoteSpotEntity();
        noteSpotEntity.setId(id);
        noteSpotEntity.setTitle(title);
        noteSpotEntity.setType(NoteSpotEntity.TYPE_SHOP_PRODUCT);
        Intent intent = getActivity().getIntent();
        intent.putExtra("note_spot_entity", noteSpotEntity);
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().onBackPressed();
    }

    @Override
    public void refresh(Object... params) {
        if (recyclerView == null) {
            return;
        }
        if (params != null && params.length > 0) {
            keyword = (String) params[0];
            initLoad();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
        CommonUtil.unSubscribeSubs(initSub, pageSub);
    }
}