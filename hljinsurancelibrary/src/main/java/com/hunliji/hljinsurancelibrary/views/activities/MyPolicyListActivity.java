package com.hunliji.hljinsurancelibrary.views.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpPosterData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;
import com.hunliji.hljinsurancelibrary.R;
import com.hunliji.hljinsurancelibrary.R2;
import com.hunliji.hljinsurancelibrary.adapter.PolicyAdapter;
import com.hunliji.hljinsurancelibrary.api.InsuranceApi;
import com.hunliji.hljinsurancelibrary.models.MyPolicy;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by hua_rong on 2017/5/24.
 * 我的保单列表activity
 */

public class MyPolicyListActivity extends HljBaseActivity implements
        PullToRefreshVerticalRecyclerView.OnRefreshListener {
    @BindView(R2.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R2.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R2.id.fl_policy_list)
    FrameLayout flPolicyList;
    @BindView(R2.id.tv_to_buy)
    TextView tvToBuy;
    @BindView(R2.id.tv_history_policy)
    TextView tvHistoryPolicy;
    @BindView(R2.id.ll_empty_policy)
    LinearLayout llEmptyPolicy;
    private List<MyPolicy> policyList;
    private PolicyAdapter policyAdapter;
    private View footView;
    private View endView;
    private View loadView;
    private HljHttpSubscriber refreshSubscriber;
    private HljHttpSubscriber pageSubscriber;
    private Subscription rxBusSub;
    private Subscription rxBusSub2;
    private boolean backMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        backMain = getIntent().getBooleanExtra("backMain", false);
        setSwipeBackEnable(!backMain);
        setContentView(R.layout.activity_my_policy);
        ButterKnife.bind(this);
        initFootView();
        initView();
        initNetError();
        onRefresh(recyclerView);
        registerRxBusEvent();
    }

    private void initNetError() {
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                onRefresh(recyclerView);
            }
        });
    }

    private void initFootView() {
        footView = View.inflate(this, R.layout.look_history_policy_item, null);
        endView = footView.findViewById(R.id.no_more_hint);
        loadView = footView.findViewById(R.id.loading);
        TextView tvHistoryPolicy = (TextView) footView.findViewById(R.id.tv_history_policy);
        tvHistoryPolicy.getLayoutParams().width = CommonUtil.getDeviceSize(this).x - CommonUtil
                .dp2px(
                this,
                32);
        tvHistoryPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onHistoryPolicy();
            }
        });
        footView.setVisibility(View.GONE);
    }

    @OnClick(R2.id.tv_history_policy)
    public void onHistoryPolicy() {
        Intent intent = new Intent(this, HistoryPolicyListActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    private void initView() {
        recyclerView.setOnRefreshListener(this);
        policyList = new ArrayList<>();
        int hon = CommonUtil.dp2px(this, 16);
        int vertical = CommonUtil.dp2px(this, 10);
        recyclerView.getRefreshableView()
                .setPadding(hon, vertical, hon, vertical);
        policyAdapter = new PolicyAdapter(this, policyList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.getRefreshableView()
                .setLayoutManager(linearLayoutManager);
        policyAdapter.setFooterView(footView);
        recyclerView.getRefreshableView()
                .setAdapter(policyAdapter);
    }

    /**
     * 资料不全的保单填写成功后，刷新一下
     * 购买保险成功
     */
    private void registerRxBusEvent() {
        if (rxBusSub == null || rxBusSub.isUnsubscribed()) {
            rxBusSub = RxBus.getDefault()
                    .toObservable(RxEvent.class)
                    .subscribe(new RxBusSubscriber<RxEvent>() {
                        @Override
                        protected void onEvent(RxEvent rxEvent) {
                            switch (rxEvent.getType()) {
                                case POLICY_INFO_COMPLETED_SUCCESS:
                                    onRefresh(recyclerView);
                                    break;
                            }
                        }
                    });
        }
//        if (rxBusSub2 == null || rxBusSub2.isUnsubscribed()) {
//            rxBusSub2 = RxBus.getDefault()
//                    .toObservable(PayRxEvent.class)
//                    .subscribe(new RxBusSubscriber<PayRxEvent>() {
//                        @Override
//                        protected void onEvent(PayRxEvent rxEvent) {
//                            switch (rxEvent.getType()) {
//                                case PAY_SUCCESS:
//                                    onRefresh(recyclerView);
//                                    break;
//                            }
//                        }
//                    });
//        }
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        if (refreshSubscriber == null || refreshSubscriber.isUnsubscribed()) {
            Observable<HljHttpPosterData<List<MyPolicy>>> observable = InsuranceApi.getMyInsurance(
                    "ongoing",
                    1);
            refreshSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpPosterData<List<MyPolicy>>>() {

                        @Override
                        public void onNext(HljHttpPosterData<List<MyPolicy>> listHljHttpData) {
                            if (listHljHttpData != null) {
                                List<MyPolicy> insuranceList = listHljHttpData.getData();
                                if (insuranceList != null && !insuranceList.isEmpty()) {
                                    flPolicyList.setVisibility(View.VISIBLE);
                                    llEmptyPolicy.setVisibility(View.GONE);
                                    policyList.clear();
                                    footView.setVisibility(View.VISIBLE);
                                    policyList.addAll(insuranceList);
                                    if (listHljHttpData.getPageCount() > 1) {
                                        initPagination(listHljHttpData.getPageCount());
                                    }
                                    policyAdapter.setDataList(policyList);
                                    policyAdapter.notifyDataSetChanged();
                                } else {
                                    flPolicyList.setVisibility(View.GONE);
                                    footView.setVisibility(View.GONE);
                                    llEmptyPolicy.setVisibility(View.VISIBLE);
                                    llEmptyPolicy.setBackgroundColor(Color.WHITE);
                                }
                            }
                        }
                    })
                    .setDataNullable(true)
                    .setEmptyView(emptyView)
                    .setContentView(refreshView)
                    .setPullToRefreshBase(refreshView)
                    .setProgressBar(refreshView.isRefreshing() ? null : progressBar)
                    .build();
            observable.subscribe(refreshSubscriber);
        }
    }

    private void initPagination(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSubscriber);
        Observable<HljHttpPosterData<List<MyPolicy>>> pageObservable = PaginationTool
                .buildPagingObservable(
                recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpPosterData<List<MyPolicy>>>() {
                    @Override
                    public Observable<HljHttpPosterData<List<MyPolicy>>> onNextPage(int page) {
                        return InsuranceApi.getMyInsurance("ongoing", page);
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
                    public void onNext(HljHttpPosterData<List<MyPolicy>> listHljHttpData) {
                        if (listHljHttpData != null && listHljHttpData.getData() != null) {
                            policyList.addAll(listHljHttpData.getData());
                            policyAdapter.notifyDataSetChanged();
                        }
                    }
                })
                .build();
        pageObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSubscriber);
    }

    @Override
    public void onBackPressed() {
        if (!backMain) {
            super.onBackPressed();
        } else {
            ARouter.getInstance()
                    .build(RouterPath.IntentPath.Customer.MY_WALLET_ACTIVITY)
                    .withBoolean("backMain", true)
                    .withString("action", "myPolicies")
                    .withTransition(R.anim.slide_in_left, R.anim.activity_anim_default)
                    .navigation(this);
            finish();
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(refreshSubscriber, rxBusSub, rxBusSub2, pageSubscriber);
    }
}
