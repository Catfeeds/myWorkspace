package com.hunliji.marrybiz.view.customer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.adapter.customer.CustomerRecyclerAdapter;
import com.hunliji.marrybiz.api.customer.CustomerApi;
import com.hunliji.marrybiz.model.customer.MerchantCustomer;
import com.hunliji.marrybiz.view.chat.WSMerchantChatActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 客资管理activity
 * Created by jinxin on 2017/8/10 0010.
 */

public class CustomerManagerActivity extends HljBaseActivity implements PullToRefreshBase
        .OnRefreshListener<RecyclerView>, OnItemClickListener<MerchantCustomer> {

    @BindView(R.id.layout_search)
    RelativeLayout layoutSearch;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.btn_scroll_top)
    ImageButton btnScrollTop;

    private View footerView;
    private View endView;
    private View loadView;
    private CustomerRecyclerAdapter customerListAdapter;
    private HljHttpSubscriber refreshSubscriber;
    private HljHttpSubscriber pageSubscriber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_manager);
        ButterKnife.bind(this);

        initConstant();
        initWidget();
        initLoad();
    }

    private void initConstant() {
        footerView = LayoutInflater.from(this)
                .inflate(R.layout.hlj_foot_no_more___cm, null, false);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        customerListAdapter = new CustomerRecyclerAdapter(this);
        customerListAdapter.setFooterView(footerView);
        customerListAdapter.setOnItemClickListener(this);
    }

    private void initWidget() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setOnRefreshListener(this);
        recyclerView.getRefreshableView()
                .setLayoutManager(manager);
        recyclerView.getRefreshableView()
                .setAdapter(customerListAdapter);
    }

    private void initLoad() {
        onRefresh(null);
    }

    @OnClick(R.id.layout_search_content)
    void onSearch() {
        Intent intent = new Intent(this, CustomerSearchActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_anim_default, R.anim.slide_in_right);
    }

    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        if (refreshSubscriber != null && !refreshSubscriber.isUnsubscribed()) {
            return;
        }

        refreshSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setContentView(recyclerView)
                .setPullToRefreshBase(recyclerView)
                .setProgressBar(refreshView == null ? progressBar : null)
                .setEmptyView(emptyView)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<MerchantCustomer>>>() {

                    @Override
                    public void onNext(HljHttpData<List<MerchantCustomer>> listHljHttpData) {
                        if (listHljHttpData != null && listHljHttpData.getData() != null) {
                            customerListAdapter.setCustomerList(listHljHttpData.getData());
                            if (listHljHttpData.getData()
                                    .isEmpty()) {
                                layoutSearch.setVisibility(View.GONE);
                            }
                            if (listHljHttpData.getPageCount() > 1) {
                                initPagination(listHljHttpData.getPageCount());
                            }
                        } else {
                            layoutSearch.setVisibility(View.GONE);
                        }
                    }
                })
                .build();
        CustomerApi.getCustomerList(null, null, null, 1)
                .subscribe(refreshSubscriber);
    }

    private void initPagination(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSubscriber);
        Observable<HljHttpData<List<MerchantCustomer>>> pageObservable = PaginationTool
                .buildPagingObservable(
                recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpData<List<MerchantCustomer>>>() {
                    @Override
                    public Observable<HljHttpData<List<MerchantCustomer>>> onNextPage(int page) {
                        return CustomerApi.getCustomerList(null, null, null, page);
                    }
                })
                .setLoadView(loadView)
                .setEndView(endView)
                .build()
                .getPagingObservable()
                .observeOn(AndroidSchedulers.mainThread());
        pageSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<MerchantCustomer>>>() {
                    @Override
                    public void onNext(HljHttpData<List<MerchantCustomer>> listHljHttpData) {
                        if (listHljHttpData != null && listHljHttpData.getData() != null) {
                            customerListAdapter.addCustomerList(listHljHttpData.getData());
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
        CommonUtil.unSubscribeSubs(refreshSubscriber, pageSubscriber);
    }

    @Override
    public void onItemClick(int position, MerchantCustomer customer) {
        Intent intent = new Intent(this, WSMerchantChatActivity.class);
        intent.putExtra("user", customer.getUser());
        intent.putExtra("channelId", customer.getChannelId());
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }
}
