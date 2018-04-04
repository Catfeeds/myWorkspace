package com.hunliji.hljinsurancelibrary.views.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpPosterData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import com.hunliji.hljinsurancelibrary.R;
import com.hunliji.hljinsurancelibrary.R2;
import com.hunliji.hljinsurancelibrary.adapter.PolicyAdapter;
import com.hunliji.hljinsurancelibrary.api.InsuranceApi;
import com.hunliji.hljinsurancelibrary.models.MyPolicy;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by hua_rong on 2017/5/24.
 * 历史保单list activity
 */

public class HistoryPolicyListActivity extends HljBaseActivity implements
        PullToRefreshVerticalRecyclerView.OnRefreshListener {


    @BindView(R2.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R2.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    private View footerView;
    private View loadView;
    private TextView endView;
    private List<MyPolicy> policyList;
    private PolicyAdapter policyAdapter;
    private Unbinder unbinder;
    private HljHttpSubscriber refreshSubscriber;
    private HljHttpSubscriber pageSubscriber;
    private static final String TAB = "history";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hlj_common_fragment_ptr_recycler_view___cm);
        unbinder = ButterKnife.bind(this);
        initFooterView();
        initErrorView();
        initView();
        onRefresh(recyclerView);
    }

    private void initErrorView() {
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                onRefresh(recyclerView);
            }
        });
    }

    private void initView() {
        recyclerView.setOnRefreshListener(this);
        policyList = new ArrayList<>();
        int hon = CommonUtil.dp2px(this, 16);
        int vertical = CommonUtil.dp2px(this, 10);
        recyclerView.getRefreshableView()
                .setPadding(hon, vertical, hon, vertical);
        policyAdapter = new PolicyAdapter(this, policyList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.getRefreshableView()
                .setLayoutManager(layoutManager);
        policyAdapter.setFooterView(footerView);
        recyclerView.getRefreshableView()
                .setAdapter(policyAdapter);
    }

    private void initFooterView() {
        footerView = View.inflate(this, R.layout.hlj_foot_no_more___cm, null);
        endView = (TextView) footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        if (refreshSubscriber == null || refreshSubscriber.isUnsubscribed()) {
            Observable<HljHttpPosterData<List<MyPolicy>>> observable = InsuranceApi.getMyInsurance(TAB,
                    1);
            refreshSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpPosterData<List<MyPolicy>>>() {

                        @Override
                        public void onNext(HljHttpPosterData<List<MyPolicy>> hljHttpData) {
                            List<MyPolicy> insuranceList = hljHttpData.getData();
                            policyList.clear();
                            policyList.addAll(insuranceList);
                            policyAdapter.setDataList(policyList);
                            policyAdapter.notifyDataSetChanged();
                            initPage(hljHttpData.getPageCount());
                        }
                    })
                    .setProgressBar(refreshView.isRefreshing() ? null : progressBar)
                    .setEmptyView(emptyView)
                    .setContentView(refreshView)
                    .setPullToRefreshBase(refreshView)
                    .build();
            observable.subscribe(refreshSubscriber);
        }
    }

    private void initPage(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSubscriber);
        Observable<HljHttpPosterData<List<MyPolicy>>> pageObservable = PaginationTool
                .buildPagingObservable(
                recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpPosterData<List<MyPolicy>>>() {
                    @Override
                    public Observable<HljHttpPosterData<List<MyPolicy>>> onNextPage(int page) {
                        return InsuranceApi.getMyInsurance(TAB, page);
                    }
                })
                .setLoadView(loadView)
                .setEndView(endView)
                .build()
                .getPagingObservable()
                .observeOn(AndroidSchedulers.mainThread());

        pageSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpPosterData<List<MyPolicy>>>() {
                    @Override
                    public void onNext(HljHttpPosterData<List<MyPolicy>> hljHttpData) {
                        if (hljHttpData != null) {
                            List<MyPolicy> insuranceList = hljHttpData.getData();
                            if (insuranceList != null) {
                                policyList.addAll(insuranceList);
                                policyAdapter.setDataList(policyList);
                                policyAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                })
                .build();

        pageObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSubscriber);

    }

    @Override
    protected void onFinish() {
        super.onFinish();
        if (unbinder != null) {
            unbinder.unbind();
        }
        CommonUtil.unSubscribeSubs(refreshSubscriber, pageSubscriber);
    }

}
