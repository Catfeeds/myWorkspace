package me.suncloud.marrymemo.fragment.newsearch;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.product.ShopProduct;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljhttplibrary.api.newsearch.NewSearchApi;
import com.hunliji.hljhttplibrary.entities.HljHttpSearch;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;

import java.util.ArrayList;
import java.util.List;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.newsearch.NewSearchProductResultsAdapter;
import me.suncloud.marrymemo.view.product.ShopProductDetailActivity;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by hua_rong on 2018/1/8
 * 婚品结果页
 */

public class NewSearchProductsFragment extends NewBaseSearchResultFragment implements
        OnItemClickListener<ShopProduct>, NewSearchFilterViewHolder.OnSearchFilterListener {

    private ArrayList<ShopProduct> products = new ArrayList<>();
    private NewSearchProductResultsAdapter adapter;

    private HljHttpSubscriber initSub;
    private HljHttpSubscriber pageSub;
    private NewSearchFilterViewHolder holder;
    private StaggeredGridLayoutManager layoutManager;

    public static NewSearchProductsFragment newInstance(Bundle args) {
        NewSearchProductsFragment fragment = new NewSearchProductsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initViews() {
        super.initViews();
        RecyclerView mRecyclerView = recyclerView.getRefreshableView();
        ((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setItemPrefetchEnabled(false);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(CommonUtil.dp2px(getContext(),
                8)));
        adapter = new NewSearchProductResultsAdapter(getContext(), products);
        adapter.setFooterView(footerView);
        adapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(adapter);
    }


    private class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;
        private int middleSpace;
        private StaggeredGridLayoutManager.LayoutParams lp;

        public SpacesItemDecoration(int space) {
            this.space = space;
            this.middleSpace = space / 2;
        }

        @Override
        public void getItemOffsets(
                Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            lp = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
            int top = 0;
            int left = 0;
            int right = 0;
            int position = parent.getChildAdapterPosition(view);
            int headSize = adapter.getHeaderViewCount();
            if ((headSize == 0 || position > 0) && position < parent.getAdapter()
                    .getItemCount() - 1) {
                top = headSize == 0 || position > 2 ? space : 0;
                left = lp.getSpanIndex() == 0 ? 0 : middleSpace;
                right = lp.getSpanIndex() == 0 ? middleSpace : 0;
            }
            outRect.set(left, top, right, 0);
        }
    }

    @Override
    protected void initLoad() {
        super.initLoad();
        clearData(adapter);
        CommonUtil.unSubscribeSubs(initSub, pageSub);
        initSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setProgressBar(progressBar)
                .setContentView(recyclerView)
                .setEmptyView(emptyView)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpSearch<List<ShopProduct>>>
                        () {
                    @Override
                    public void onNext(HljHttpSearch<List<ShopProduct>> httpSearch) {
                        products.addAll(httpSearch.getData());
                        initPage(httpSearch.getPageCount());
                        adapter.setData(products);
                        recyclerView.getRefreshableView()
                                .setBackgroundColor(ContextCompat.getColor(getContext(),
                                        R.color.colorWhite));
                        adapter.notifyDataSetChanged();
                        layoutManager.scrollToPosition(0);
                    }
                })
                .build();
        NewSearchApi.getShopProductList(cid, keyword, searchType, filter, sort, 1)
                .subscribe(initSub);
    }

    private void initPage(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSub);
        Observable<HljHttpSearch<List<ShopProduct>>> pageObb = PaginationTool.buildPagingObservable(
                recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpSearch<List<ShopProduct>>>() {
                    @Override
                    public Observable<HljHttpSearch<List<ShopProduct>>> onNextPage(int page) {
                        return NewSearchApi.getShopProductList(cid,
                                keyword,
                                searchType,
                                filter,
                                sort,
                                page);
                    }
                })
                .setLoadView(footerViewHolder.loading)
                .setEndView(footerViewHolder.noMoreHint)
                .build()
                .getPagingObservable()
                .observeOn(AndroidSchedulers.mainThread());
        pageSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<HljHttpSearch<List<ShopProduct>>>
                        () {
                    @Override
                    public void onNext(HljHttpSearch<List<ShopProduct>> hljHttpSearch) {
                        adapter.addData(hljHttpSearch.getData());
                    }
                })
                .build();
        pageObb.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSub);
    }

    @Override
    public void refresh(Object... params) {}

    @Override
    public void onKeywordRefresh(String keyword) {
        if (holder != null) {
            filter = holder.resetSearchFilter();
        }
        super.onKeywordRefresh(keyword);
    }

    @Override
    protected void resetFilterView() {
        if (holder == null) {
            holder = NewSearchFilterViewHolder.newInstance(getContext(), searchType, this);
            sort = holder.getSort();
            filter = holder.getSearchFilter();
        } else {
            filter = holder.resetSearchFilter();
        }
        if (getSearchActivity() != null) {
            getSearchActivity().setFilterView(holder.getRootView());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        CommonUtil.unSubscribeSubs(initSub, pageSub);
    }

    @Override
    public void onItemClick(int position, ShopProduct product) {
        if (product != null) {
            Intent intent = new Intent(getActivity(), ShopProductDetailActivity.class);
            intent.putExtra("id", product.getId());
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().overridePendingTransition(R.anim.slide_in_right,
                        R.anim.activity_anim_default);
            }
        }
    }

}
