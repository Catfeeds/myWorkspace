package me.suncloud.marrymemo.fragment.newsearch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljhttplibrary.api.newsearch.NewSearchApi;
import com.hunliji.hljhttplibrary.entities.HljHttpSearch;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.newsearch.NewBaseSearchResultAdapter;
import me.suncloud.marrymemo.adpter.newsearch.NewSearchHotelResultsAdapter;
import me.suncloud.marrymemo.adpter.newsearch.NewSearchMerchantResultsAdapter;
import me.suncloud.marrymemo.view.merchant.MerchantDetailActivity;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by huarong on 18/1/15.
 * 酒店和商家 共用
 */

public class NewSearchMerchantsResultFragment extends NewBaseSearchResultFragment implements
        OnItemClickListener<Merchant>, NewSearchFilterViewHolder.OnSearchFilterListener {

    private ArrayList<Merchant> merchants = new ArrayList<>();
    private NewBaseSearchResultAdapter adapter;

    private HljHttpSubscriber initSub;
    private HljHttpSubscriber pageSub;
    private NewSearchFilterViewHolder holder;
    private LinearLayoutManager layoutManager;

    public static NewSearchMerchantsResultFragment newInstance(
            Bundle args) {
        NewSearchMerchantsResultFragment fragment = new NewSearchMerchantsResultFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initViews() {
        super.initViews();

        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.getRefreshableView()
                .setLayoutManager(layoutManager);

        if (searchType == NewSearchApi.SearchType.SEARCH_TYPE_MERCHANT) {
            adapter = new NewSearchMerchantResultsAdapter(getContext(), merchants);
        } else {
            adapter = new NewSearchHotelResultsAdapter(getContext(), merchants);
        }
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
                .setOnNextListener(new SubscriberOnNextListener<HljHttpSearch<List<Merchant>>>() {
                    @Override
                    public void onNext(HljHttpSearch<List<Merchant>> httpSearch) {
                        List<Merchant> cpmMerchants = httpSearch.getCpmList();
                        adapter.setCpmFeeds(cpmMerchants);
                        List<Merchant> MerchantList = getDistinctMerchant(httpSearch.getData(),
                                cpmMerchants);
                        if (!CommonUtil.isCollectionEmpty(MerchantList)) {
                            merchants.addAll(MerchantList);
                        }
                        initPage(httpSearch.getPageCount());
                        adapter.setData(merchants);
                        adapter.notifyDataSetChanged();
                        layoutManager.scrollToPosition(0);
                    }
                })
                .build();
        NewSearchApi.getMerchantList(cid,
                NewSearchApi.SORT_DEFAULT.equals(sort) && searchType == NewSearchApi.SearchType
                        .SEARCH_TYPE_MERCHANT ? 20 : 0,
                0,
                keyword,
                searchType,
                filter,
                sort,
                1)
                .subscribe(initSub);
    }

    private void initPage(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSub);
        Observable<HljHttpSearch<List<Merchant>>> pageObb = PaginationTool.buildPagingObservable(
                recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpSearch<List<Merchant>>>() {
                    @Override
                    public Observable<HljHttpSearch<List<Merchant>>> onNextPage(int page) {
                        return NewSearchApi.getMerchantList(cid,
                                0,
                                0,
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
                .setOnNextListener(new SubscriberOnNextListener<HljHttpSearch<List<Merchant>>>() {
                    @Override
                    public void onNext(HljHttpSearch<List<Merchant>> hljHttpSearch) {
                        List<Merchant> merchants = getDistinctMerchant(hljHttpSearch.getData(),
                                (List<Merchant>) adapter.getCpmFeeds());
                        adapter.addData(merchants);
                    }
                })
                .build();
        pageObb.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSub);
    }


    private List<Merchant> getDistinctMerchant(
            List<Merchant> merchants, List<Merchant> cpmMerchants) {
        if (!CommonUtil.isCollectionEmpty(merchants) && !CommonUtil.isCollectionEmpty
                (cpmMerchants)) {
            Iterator<Merchant> iterator = merchants.iterator();
            while (iterator.hasNext()) {
                Merchant merchant = iterator.next();
                if (merchant != null) {
                    for (Merchant cpmMerchant : cpmMerchants) {
                        if (merchant.getId() == cpmMerchant.getId()) {
                            iterator.remove();
                        }
                    }
                }
            }
        }
        return merchants;
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
    public void onItemClick(int position, Merchant merchant) {
        if (merchant != null) {
            Intent intent = new Intent(getActivity(), MerchantDetailActivity.class);
            intent.putExtra("id", merchant.getId());
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().overridePendingTransition(R.anim.slide_in_right,
                        R.anim.activity_anim_default);
            }
        }
    }

}
