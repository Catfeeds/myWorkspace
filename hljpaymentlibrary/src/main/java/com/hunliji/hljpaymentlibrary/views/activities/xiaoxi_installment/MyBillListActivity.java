package com.hunliji.hljpaymentlibrary.views.activities.xiaoxi_installment;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.widget.ProgressBar;

import com.alibaba.android.arouter.launcher.ARouter;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljpaymentlibrary.R;
import com.hunliji.hljpaymentlibrary.R2;
import com.hunliji.hljpaymentlibrary.adapters.xiaoxi_installment.MyBillListAdapter;
import com.hunliji.hljpaymentlibrary.adapters.xiaoxi_installment.viewholders.MyBillHeaderViewHolder;
import com.hunliji.hljpaymentlibrary.api.xiaoxi_installment.XiaoxiInstallmentApi;
import com.hunliji.hljpaymentlibrary.models.PayRxEvent;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.wrappers.XiaoxiInstallmentOrdersData;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;

/**
 * 小犀分期-我的账单
 * Created by chen_bin on 2017/8/17 0017.
 */
public class MyBillListActivity extends HljBaseActivity implements PullToRefreshBase
        .OnRefreshListener<RecyclerView> {

    @BindView(R2.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R2.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;

    private View headerView;

    private MyBillListAdapter adapter;

    private boolean isBackOrderList; //是否跳转到“我的订单”列表

    private Subscription rxBusEventSub;
    private HljHttpSubscriber refreshSub;

    public static final String ARG_IS_BACK_ORDER_LIST = "is_back_order_list";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSwipeBackEnable(false);
        setContentView(R.layout.hlj_common_fragment_ptr_recycler_view___cm);
        ButterKnife.bind(this);
        initValues();
        initViews();
        onRefresh(null);
        registerRxBusEvent();
    }

    private void initValues() {
        isBackOrderList = getIntent().getBooleanExtra(ARG_IS_BACK_ORDER_LIST, false);
    }

    @Override
    public void onBackPressed() {
        // 返回我的订单列表
        if (isBackOrderList) {
            int selectTab = getIntent().getIntExtra(RouterPath.IntentPath.Customer.MyOrder
                            .ARG_SELECT_TAB,
                    RouterPath.IntentPath.Customer.MyOrder.Tab.SERVICE_ORDER);
            ARouter.getInstance()
                    .build(RouterPath.IntentPath.Customer.MyOrderListActivityPath.ORDER)
                    .withBoolean(RouterPath.IntentPath.Customer.MyOrder.ARG_BACK_MAIN, true)
                    .withInt(RouterPath.IntentPath.Customer.MyOrder.ARG_SELECT_TAB, selectTab)
                    .navigation(this);
            finish();
        } else {
            super.onBackPressed();
        }
    }

    private void initViews() {
        headerView = View.inflate(this, R.layout.my_bills_header_item___pay, null);
        emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                onRefresh(null);
            }
        });
        ((SimpleItemAnimator) recyclerView.getRefreshableView()
                .getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.getRefreshableView()
                .addItemDecoration(new SpacesItemDecoration());
        recyclerView.setOnRefreshListener(this);
        recyclerView.getRefreshableView()
                .setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyBillListAdapter(this);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
    }

    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        if (refreshSub == null || refreshSub.isUnsubscribed()) {
            refreshSub = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<XiaoxiInstallmentOrdersData>() {

                        @Override
                        public void onNext(XiaoxiInstallmentOrdersData ordersData) {
                            MyBillHeaderViewHolder headerViewHolder = (MyBillHeaderViewHolder)
                                    headerView.getTag();
                            if (headerViewHolder == null) {
                                adapter.setHeaderView(headerView);
                                headerViewHolder = new MyBillHeaderViewHolder(headerView);
                                headerView.setTag(headerViewHolder);
                            }
                            headerViewHolder.setView(MyBillListActivity.this, ordersData, 0, 0);
                            adapter.setOrders(ordersData.getOrders());
                        }
                    })
                    .setEmptyView(emptyView)
                    .setContentView(recyclerView)
                    .setPullToRefreshBase(refreshView)
                    .setProgressBar(recyclerView.isRefreshing() ? null : progressBar)
                    .build();
            XiaoxiInstallmentApi.getMyBillsObb(this)
                    .subscribe(refreshSub);
        }
    }

    //注册RxEventBus监听
    private void registerRxBusEvent() {
        if (rxBusEventSub == null || rxBusEventSub.isUnsubscribed()) {
            rxBusEventSub = RxBus.getDefault()
                    .toObservable(PayRxEvent.class)
                    .subscribe(new RxBusSubscriber<PayRxEvent>() {
                        @Override
                        protected void onEvent(PayRxEvent payRxEvent) {
                            switch (payRxEvent.getType()) {
                                case REPAY_SUCCESS:
                                    onRefresh(null);
                                    break;
                            }
                        }
                    });
        }
    }

    private class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        SpacesItemDecoration() {
            space = CommonUtil.dp2px(MyBillListActivity.this, 10);
        }

        @Override
        public void getItemOffsets(
                Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.set(0, 0, 0, space);
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(rxBusEventSub, refreshSub);
    }
}