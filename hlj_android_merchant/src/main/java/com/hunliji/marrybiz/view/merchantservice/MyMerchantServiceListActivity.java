package com.hunliji.marrybiz.view.merchantservice;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.adapter.merchantservice.MyMerchantServiceAdapter;
import com.hunliji.marrybiz.api.merchantserver.MerchantServerApi;
import com.hunliji.marrybiz.model.merchantservice.MerchantServer;
import com.hunliji.marrybiz.model.orders.BdProduct;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 我的服务list
 * Created by jinxin on 2018/1/24 0024.
 */

public class MyMerchantServiceListActivity extends HljBaseActivity implements PullToRefreshBase
        .OnRefreshListener<RecyclerView>, MyMerchantServiceAdapter
        .onMerchantServerAdapterClickListener {

    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.btn_scroll_top)
    ImageButton btnScrollTop;

    private MyMerchantServiceAdapter serviceAdapter;
    private View footerView;
    private HljHttpSubscriber refreshSub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hlj_common_fragment_ptr_recycler_view___cm);
        ButterKnife.bind(this);

        initConstant();
        initWidget();
        initLoad();
    }

    private void initConstant() {
        footerView = LayoutInflater.from(this)
                .inflate(R.layout.hlj_foot_no_more___cm, null, false);
        footerView.findViewById(R.id.no_more_hint)
                .setVisibility(View.VISIBLE);
        serviceAdapter = new MyMerchantServiceAdapter(this);
        serviceAdapter.setOnMerchantServerAdapterClickListener(this);
        serviceAdapter.setFooterView(footerView);
    }

    private void initWidget() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.getRefreshableView()
                .setLayoutManager(manager);
        recyclerView.getRefreshableView()
                .setAdapter(serviceAdapter);
        recyclerView.setOnRefreshListener(this);
    }

    private void initLoad() {
        onRefresh(null);
    }

    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        CommonUtil.unSubscribeSubs(refreshSub);
        refreshSub = HljHttpSubscriber.buildSubscriber(this)
                .setContentView(recyclerView)
                .setEmptyView(emptyView)
                .setPullToRefreshBase(recyclerView)
                .setProgressBar(refreshView == null ? progressBar : null)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<MerchantServer>>>() {

                    @Override
                    public void onNext(HljHttpData<List<MerchantServer>> data) {
                        setHttpResult(data);
                    }
                })
                .build();
        MerchantServerApi.getMerchantServerList()
                .subscribe(refreshSub);
    }

    private void setHttpResult(HljHttpData<List<MerchantServer>> listHljHttpData) {
        if (listHljHttpData != null && listHljHttpData.getData() != null) {
            serviceAdapter.setServerList(listHljHttpData.getData());
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(refreshSub);
    }

    @Override
    public void onLook(MerchantServer server) {
        goMerchantServerDetail(server);
    }

    @Override
    public void onUse(MerchantServer server) {
    }

    @Override
    public void onFeed(MerchantServer server) {
        goMerchantServerDetail(server);
    }

    @Override
    public void onUpdate(MerchantServer server) {
        Intent intent = new Intent(this, MerchantUltimateDetailActivity.class);
        startActivity(intent);
    }

    private void goMerchantServerDetail(MerchantServer merchantServer) {
        Intent intent;
        switch ((int) merchantServer.getProductId()) {
            case BdProduct.QI_JIAN_BAN:
            case BdProduct.ZHUAN_YE_BAN:
                intent = new Intent(this, MerchantUltimateDetailActivity.class);
                intent.putExtra(MarketingDetailActivity.ARG_PRODUCT_ID,
                        merchantServer.getProductId());
                break;
            case BdProduct.BAO_ZHENG_JIN:
                intent = new Intent(this, BondPlanDetailActivity.class);
                break;
            default:
                intent = new Intent(this, MarketingDetailActivity.class);
                intent.putExtra(MarketingDetailActivity.ARG_PRODUCT_ID,
                        merchantServer.getProductId());
                break;
        }
        startActivity(intent);
    }
}
