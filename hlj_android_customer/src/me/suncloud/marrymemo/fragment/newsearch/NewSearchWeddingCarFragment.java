package me.suncloud.marrymemo.fragment.newsearch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import com.hunliji.hljcarlibrary.views.activities.WeddingCarProductDetailActivity;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.wedding_car.WeddingCarProduct;
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
import me.suncloud.marrymemo.adpter.newsearch.NewWeddingCarResultAdapter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by hua_rong on 2018/1/8
 * 婚车结果页
 */

public class NewSearchWeddingCarFragment extends NewBaseSearchResultFragment implements
        OnItemClickListener<WeddingCarProduct> {

    private NewWeddingCarResultAdapter adapter;
    private HljHttpSubscriber initSub;
    private HljHttpSubscriber pageSub;
    private NewSearchFilterViewHolder holder;
    private ArrayList<WeddingCarProduct> weddingCars = new ArrayList<>();
    private LinearLayoutManager layoutManager;


    public static NewSearchWeddingCarFragment newInstance(Bundle bundle) {
        NewSearchWeddingCarFragment fragment = new NewSearchWeddingCarFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initViews() {
        super.initViews();

        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.getRefreshableView()
                .setLayoutManager(layoutManager);
        adapter = new NewWeddingCarResultAdapter(getContext(), weddingCars);
        adapter.setFooterView(footerView);
        adapter.setOnItemClickListener(this);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
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
                .setOnNextListener(new SubscriberOnNextListener<HljHttpSearch<List<WeddingCarProduct>>>() {
                    @Override
                    public void onNext(HljHttpSearch<List<WeddingCarProduct>> httpSearch) {
                        weddingCars.addAll(httpSearch.getData());
                        initPage(httpSearch.getPageCount());
                        adapter.setData(weddingCars);
                        adapter.notifyDataSetChanged();
                        layoutManager.scrollToPosition(0);
                    }
                })
                .build();
        NewSearchApi.getWeddingCarList(cid, keyword, searchType, filter, sort, 1)
                .subscribe(initSub);
    }

    private void initPage(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSub);
        Observable<HljHttpSearch<List<WeddingCarProduct>>> pageObb = PaginationTool
                .buildPagingObservable(
                recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpSearch<List<WeddingCarProduct>>>() {
                    @Override
                    public Observable<HljHttpSearch<List<WeddingCarProduct>>> onNextPage(int page) {
                        return NewSearchApi.getWeddingCarList(cid,
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
                .setOnNextListener(new SubscriberOnNextListener<HljHttpSearch<List<WeddingCarProduct>>>() {
                    @Override
                    public void onNext(HljHttpSearch<List<WeddingCarProduct>> hljHttpSearch) {
                        adapter.addData(hljHttpSearch.getData());
                    }
                })
                .build();
        pageObb.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSub);
    }

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
    public void onItemClick(int position, WeddingCarProduct weddingCarProduct) {
        if (weddingCarProduct != null && weddingCarProduct.getId() > 0) {
            Intent intent = new Intent(getContext(), WeddingCarProductDetailActivity.class);
            intent.putExtra(WeddingCarProductDetailActivity.ARG_ID, weddingCarProduct.getId());
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().overridePendingTransition(R.anim.slide_in_right,
                        R.anim.activity_anim_default);
            }
        }
    }

}
