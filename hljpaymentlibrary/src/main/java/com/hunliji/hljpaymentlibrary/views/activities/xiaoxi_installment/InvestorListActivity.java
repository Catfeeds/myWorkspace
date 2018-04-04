package com.hunliji.hljpaymentlibrary.views.activities.xiaoxi_installment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljpaymentlibrary.R;
import com.hunliji.hljpaymentlibrary.R2;
import com.hunliji.hljpaymentlibrary.adapters.xiaoxi_installment.InvestorListAdapter;
import com.hunliji.hljpaymentlibrary.api.xiaoxi_installment.XiaoxiInstallmentApi;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.Investor;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 出借人列表
 * Created by chen_bin on 2017/12/13 0013.
 */
public class InvestorListActivity extends HljBaseActivity implements PullToRefreshBase
        .OnRefreshListener<RecyclerView> {

    @BindView(R2.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R2.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;

    private View headerView;

    private InvestorListAdapter adapter;

    private String assetOrderId;

    private HljHttpSubscriber refreshSub;

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
        headerView = View.inflate(this, R.layout.investor_header_item___pay, null);
        emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                onRefresh(null);
            }
        });
        recyclerView.setOnRefreshListener(this);
        recyclerView.getRefreshableView()
                .setLayoutManager(new LinearLayoutManager(this));
        adapter = new InvestorListAdapter(this);
        adapter.setAssetOrderId(assetOrderId);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
    }

    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        if (refreshSub == null || refreshSub.isUnsubscribed()) {
            refreshSub = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<Investor>>>() {
                        @Override
                        public void onNext(HljHttpData<List<Investor>> listHljHttpData) {
                            adapter.setHeaderView(headerView);
                            adapter.setInvestors(listHljHttpData.getData());
                        }
                    })
                    .setEmptyView(emptyView)
                    .setContentView(recyclerView)
                    .setPullToRefreshBase(recyclerView)
                    .setProgressBar(recyclerView.isRefreshing() ? null : progressBar)
                    .build();
            XiaoxiInstallmentApi.getInvestorsObb(assetOrderId)
                    .subscribe(refreshSub);
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(refreshSub);
    }
}
