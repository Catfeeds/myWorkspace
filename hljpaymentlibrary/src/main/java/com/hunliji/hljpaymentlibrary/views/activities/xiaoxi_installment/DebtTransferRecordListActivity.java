package com.hunliji.hljpaymentlibrary.views.activities.xiaoxi_installment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.widget.ProgressBar;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljpaymentlibrary.R;
import com.hunliji.hljpaymentlibrary.R2;
import com.hunliji.hljpaymentlibrary.adapters.xiaoxi_installment.DebtTransferRecordListAdapter;
import com.hunliji.hljpaymentlibrary.api.xiaoxi_installment.XiaoxiInstallmentApi;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.DebtTransferRecord;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 订单债权记录查询
 * Created by chen_bin on 2017/11/3 0003.
 */
public class DebtTransferRecordListActivity extends HljBaseActivity implements PullToRefreshBase
        .OnRefreshListener<RecyclerView> {

    @BindView(R2.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R2.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;

    private View headerView;
    private View endView;
    private View loadView;
    private LinearLayoutManager layoutManager;
    private DebtTransferRecordListAdapter adapter;
    private String assetOrderId;
    private boolean isEnd = true;
    private int currentPage;
    private HljHttpSubscriber getRecordsObb;

    public static final String ARG_ASSET_ORDER_ID = "asset_order_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hlj_common_fragment_ptr_recycler_view___cm);
        ButterKnife.bind(this);
        initValues();
        initViews();
        onRefresh(null);
    }

    private void initValues() {
        assetOrderId = getIntent().getStringExtra(ARG_ASSET_ORDER_ID);
    }

    private void initViews() {
        headerView = View.inflate(this, R.layout.debt_header_item___pay, null);
        View footerView = View.inflate(this, R.layout.hlj_foot_no_more___cm, null);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        loadView.setVisibility(View.INVISIBLE);
        emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                onRefresh(null);
            }
        });
        ((SimpleItemAnimator) recyclerView.getRefreshableView()
                .getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setOnRefreshListener(this);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.getRefreshableView()
                .setLayoutManager(layoutManager);
        adapter = new DebtTransferRecordListAdapter(this);
        adapter.setFooterView(footerView);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
        recyclerView.getRefreshableView()
                .addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                        switch (newState) {
                            case RecyclerView.SCROLL_STATE_IDLE:
                                if (!CommonUtil.isUnsubscribed(getRecordsObb)) {
                                    return;
                                }
                                int position = getLastVisibleItemPosition();
                                int itemCount = recyclerView.getAdapter()
                                        .getItemCount();
                                if (position >= itemCount - 5 && !isEnd) {
                                    loadView.setVisibility(View.VISIBLE);
                                    endView.setVisibility(View.GONE);
                                    initPagination();
                                } else {
                                    setShowFooterView();
                                }
                                break;
                        }
                    }
                });
    }

    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        CommonUtil.unSubscribeSubs(getRecordsObb);
        getRecordsObb = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<DebtTransferRecord>>>() {
                    @Override
                    public void onNext(HljHttpData<List<DebtTransferRecord>> listHljHttpData) {
                        currentPage = 1;
                        adapter.setHeaderView(headerView);
                        adapter.setRecords(listHljHttpData.getData());
                    }
                })
                .setEmptyView(emptyView)
                .setContentView(recyclerView)
                .setPullToRefreshBase(recyclerView)
                .setProgressBar(recyclerView.isRefreshing() && progressBar.getVisibility() !=
                        View.VISIBLE ? null : progressBar)
                .build();
        XiaoxiInstallmentApi.getDebtTransferRecordsObb(assetOrderId, 1, HljCommon.PER_PAGE)
                .subscribe(getRecordsObb);
    }

    private void initPagination() {
        getRecordsObb = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<DebtTransferRecord>>>() {

                    @Override
                    public void onNext(HljHttpData<List<DebtTransferRecord>> listHljHttpData) {
                        currentPage++;
                        isEnd = listHljHttpData == null || listHljHttpData.isEmpty();
                        if (!isEnd) {
                            adapter.addRecords(listHljHttpData.getData());
                        }
                        recyclerView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (recyclerView != null) {
                                    setShowFooterView();
                                }
                            }
                        }, 120);
                    }
                })
                .setOnErrorListener(new SubscriberOnErrorListener() {
                    @Override
                    public void onError(Object o) {
                        loadView.setVisibility(View.GONE);
                        endView.setVisibility(View.INVISIBLE);
                    }
                })
                .setDataNullable(true)
                .build();
        int page = currentPage + 1;
        XiaoxiInstallmentApi.getDebtTransferRecordsObb(assetOrderId, page, HljCommon.PER_PAGE)
                .subscribe(getRecordsObb);
    }

    private int getLastVisibleItemPosition() {
        return layoutManager == null ? 0 : layoutManager.findLastVisibleItemPosition();
    }

    private void setShowFooterView() {
        loadView.setVisibility(View.GONE);
        endView.setVisibility(CommonUtil.isCollectionEmpty(adapter.getRecords()) ? View.INVISIBLE
                : View.VISIBLE);
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(getRecordsObb);
    }
}
