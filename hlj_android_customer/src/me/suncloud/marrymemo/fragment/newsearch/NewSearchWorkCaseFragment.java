package me.suncloud.marrymemo.fragment.newsearch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljhttplibrary.api.newsearch.NewSearchApi;
import com.hunliji.hljhttplibrary.entities.HljHttpResultZip;
import com.hunliji.hljhttplibrary.entities.HljHttpSearch;
import com.hunliji.hljhttplibrary.entities.HljRZField;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.newsearch.NewSearchWorkResultsAdapter;
import me.suncloud.marrymemo.view.CaseDetailActivity;
import me.suncloud.marrymemo.view.WorkActivity;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * Created by hua_rong on 2018/1/8
 * 套餐和案例共用的搜索结果Fragment，两者之间的不同就是searchType不同导致的显示区别和跳转区别，搜索结果的列表数据结构也不一样，
 * 模型一致都是Work
 */

public class NewSearchWorkCaseFragment extends NewBaseSearchResultFragment implements
        OnItemClickListener<Work>, NewSearchFilterViewHolder.OnSearchFilterListener {


    public static final String CPM_SOURCE = "search_header_merchant";

    private ArrayList<Work> works = new ArrayList<>();
    private NewSearchWorkResultsAdapter adapter;
    private HljHttpSubscriber initSub;
    private HljHttpSubscriber pageSub;
    private HashMap<NewSearchApi.SearchType, NewSearchFilterViewHolder> holderHashMap = new
            HashMap<>();
    private LinearLayoutManager layoutManager;

    public static NewSearchWorkCaseFragment newInstance(Bundle args) {
        NewSearchWorkCaseFragment fragment = new NewSearchWorkCaseFragment();
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

        adapter = new NewSearchWorkResultsAdapter(getContext(), works, searchType);
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
        Observable<HljHttpSearch<List<Merchant>>> shopObb = Observable.just(sort)
                .concatMap(new Func1<String, Observable<? extends HljHttpSearch<List<Merchant>>>>
                        () {
                    @Override
                    public Observable<? extends HljHttpSearch<List<Merchant>>> call(String s) {
                        if (NewSearchApi.SORT_DEFAULT.equals(s)) {
                            return NewSearchApi.getMerchantList(cid,
                                    1,
                                    1,
                                    keyword,
                                    NewSearchApi.SearchType.SEARCH_TYPE_MERCHANT,
                                    null,
                                    null,
                                    1);
                        } else {
                            return Observable.just(null);
                        }
                    }
                });
        Observable<HljHttpSearch<List<Work>>> workObb = NewSearchApi.getWordCaseList(cid,
                NewSearchApi.SORT_DEFAULT.equals(sort) ? 20 : 0,
                keyword,
                searchType,
                filter,
                sort,
                1);
        Observable<ResultZip> observable = Observable.zip(shopObb,
                workObb,
                new Func2<HljHttpSearch<List<Merchant>>, HljHttpSearch<List<Work>>, ResultZip>() {
                    @Override
                    public ResultZip call(
                            HljHttpSearch<List<Merchant>> hljMerchant,
                            HljHttpSearch<List<Work>> hljWork) {
                        return new ResultZip(hljMerchant, hljWork);
                    }
                });
        initSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setProgressBar(progressBar)
                .setContentView(recyclerView)
                .setEmptyView(emptyView)
                .setOnNextListener(new SubscriberOnNextListener<ResultZip>() {
                    @Override
                    public void onNext(ResultZip resultZip) {
                        HljHttpSearch<List<Merchant>> hljMerchant = resultZip.hljMerchant;
                        HljHttpSearch<List<Work>> hljWork = resultZip.getHljWork();
                        if (hljMerchant != null && NewSearchApi.SORT_DEFAULT.equals(sort)) {
                            List<Merchant> merchants = hljMerchant.getCpmList();
                            if (!CommonUtil.isCollectionEmpty(merchants)) {
                                Merchant merchant = merchants.get(0);
                                adapter.setShopCpm(merchant);
                            }
                        }
                        List<Work> cpmWorks = hljWork.getCpmList();
                        List<Work> workList = getDistinctWork(hljWork.getData(), cpmWorks);
                        adapter.setCpmFeeds(cpmWorks);
                        if (!CommonUtil.isCollectionEmpty(workList)) {
                            works.addAll(workList);
                        }
                        initPage(hljWork.getPageCount());
                        adapter.setData(works);
                        adapter.notifyDataSetChanged();
                        layoutManager.scrollToPosition(0);
                    }
                })
                .build();
        observable.subscribe(initSub);
    }

    private List<Work> getDistinctWork(List<Work> works, List<Work> cpmWorks) {
        if (!CommonUtil.isCollectionEmpty(works) && !CommonUtil.isCollectionEmpty(cpmWorks)) {
            Iterator<Work> iterator = works.iterator();
            while (iterator.hasNext()) {
                Work work = iterator.next();
                if (work != null) {
                    for (Work cpmWork : cpmWorks) {
                        if (work.getId() == cpmWork.getId()) {
                            iterator.remove();
                        }
                    }
                }
            }
        }
        return works;
    }

    private class ResultZip extends HljHttpResultZip {

        @HljRZField
        HljHttpSearch<List<Merchant>> hljMerchant;
        @HljRZField
        HljHttpSearch<List<Work>> hljWork;

        public ResultZip(
                HljHttpSearch<List<Merchant>> hljMerchant, HljHttpSearch<List<Work>> hljWork) {
            this.hljMerchant = hljMerchant;
            this.hljWork = hljWork;
        }

        private HljHttpSearch<List<Work>> getHljWork() {
            if (hljWork == null) {
                hljWork = new HljHttpSearch<>();
            }
            return hljWork;
        }
    }

    private void initPage(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSub);
        Observable<HljHttpSearch<List<Work>>> pageObb = PaginationTool.buildPagingObservable(
                recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpSearch<List<Work>>>() {
                    @Override
                    public Observable<HljHttpSearch<List<Work>>> onNextPage(int page) {
                        return NewSearchApi.getWordCaseList(cid,
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
                .setOnNextListener(new SubscriberOnNextListener<HljHttpSearch<List<Work>>>() {
                    @Override
                    public void onNext(HljHttpSearch<List<Work>> hljHttpSearch) {
                        if (!CommonUtil.isCollectionEmpty(hljHttpSearch.getData())) {
                            List<Work> workList = getDistinctWork(hljHttpSearch.getData(),
                                    (List<Work>) adapter.getCpmFeeds());
                            adapter.addData(workList);
                        }
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
        NewSearchFilterViewHolder holder = holderHashMap.get(searchType);
        if (holder != null) {
            filter = holder.resetSearchFilter();
        }
        super.onKeywordRefresh(keyword);
    }

    @Override
    protected void resetFilterView() {
        NewSearchFilterViewHolder holder;
        if (holderHashMap.containsKey(searchType)) {
            holder = holderHashMap.get(searchType);
            filter = holder.resetSearchFilter();
        } else {
            holder = NewSearchFilterViewHolder.newInstance(getContext(), searchType, this);
            holderHashMap.put(searchType, holder);
            filter = holder.getSearchFilter();
        }
        sort = holder.getSort();
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
    public void onItemClick(int position, Work work) {
        Intent intent;
        if (work != null) {
            if (searchType == NewSearchApi.SearchType.SEARCH_TYPE_WORK) {
                // 跳转套餐
                intent = new Intent(getContext(), WorkActivity.class);
            } else {
                intent = new Intent(getContext(), CaseDetailActivity.class);
            }
            intent.putExtra("id", work.getId());
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().overridePendingTransition(R.anim.slide_in_right,
                        R.anim.activity_anim_default);
            }
        }
    }

}
