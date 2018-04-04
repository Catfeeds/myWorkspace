package me.suncloud.marrymemo.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.views.fragments.ScrollAbleFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.work_case.BigCaseListAdapter;
import me.suncloud.marrymemo.api.work_case.WorkApi;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by wangtao on 2016/12/9.
 */

public class CaseListFragment extends ScrollAbleFragment {

    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;

    private View endView;
    private View loadView;

    private BigCaseListAdapter adapter;
    private HashMap<String, String> queries;
    private Unbinder unbinder;
    private Subscription initSubscription;
    private Subscription pageSubscription;

    public static CaseListFragment newInstance(HashMap<String, String> queries) {
        CaseListFragment fragment = new CaseListFragment();
        Bundle args = new Bundle();
        args.putSerializable("queries", queries);
        fragment.setArguments(args);
        return fragment;
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
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(getArguments()!=null){
            queries= (HashMap<String, String>) getArguments().getSerializable("queries");
        }
        initView();
        initLoad();
        initTracker();
    }

    private void initTracker(){
        HljVTTagger.tagViewParentName(recyclerView,"example_list");
    }

    private void initView() {
        View footerView = View.inflate(getContext(), R.layout.hlj_foot_no_more___cm, null);
        endView = footerView.findViewById(com.hunliji.hljquestionanswer.R.id.no_more_hint);
        loadView = footerView.findViewById(com.hunliji.hljquestionanswer.R.id.loading);
        loadView.setVisibility(View.INVISIBLE);
        recyclerView.setMode(PullToRefreshBase.Mode.DISABLED);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.getRefreshableView()
                .setLayoutManager(layoutManager);


        adapter = new BigCaseListAdapter(getContext(), footerView);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
    }

    private void initLoad() {
        if (initSubscription != null && !initSubscription.isUnsubscribed()) {
            return;
        }
        initSubscription = WorkApi.getCasesObb(20, 1, queries)
                .subscribe(HljHttpSubscriber.buildSubscriber(getContext())
                        .setProgressBar(progressBar)
                        .setContentView(recyclerView)
                        .setEmptyView(emptyView)
                        .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<Work>>>() {
                            @Override
                            public void onNext(HljHttpData<List<Work>> listHljHttpData) {
                                adapter.setCases(listHljHttpData.getData());
                                initPagination(listHljHttpData.getPageCount());
                            }
                        })
                        .build());
    }

    private void initPagination(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSubscription);
        pageSubscription = PaginationTool.buildPagingObservable(recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpData<List<Work>>>() {
                    @Override
                    public Observable<HljHttpData<List<Work>>> onNextPage(int page) {
                        return WorkApi.getCasesObb(20, page, queries);
                    }
                })
                .setEndView(endView)
                .setLoadView(loadView)
                .build()
                .getPagingObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(HljHttpSubscriber.buildSubscriber(getContext())
                        .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<Work>>>() {
                            @Override
                            public void onNext(HljHttpData<List<Work>> listHljHttpData) {
                                adapter.addCases(listHljHttpData.getData());
                            }
                        })
                        .build());
    }

    @Override
    public void refresh(Object... params) {
        if (params != null && params.length > 0 && params[0] instanceof HashMap) {
            queries= (HashMap<String, String>) params[0];
            CommonUtil.unSubscribeSubs(initSubscription);
            adapter.clear();
            initLoad();
        }
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
        CommonUtil.unSubscribeSubs(initSubscription, pageSubscription);
        super.onDestroyView();
    }
}
