package com.hunliji.hljpaymentlibrary.views.activities.xiaoxi_installment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ProgressBar;

import com.google.gson.JsonElement;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.entities.HljHttpStatus;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljpaymentlibrary.R;
import com.hunliji.hljpaymentlibrary.R2;
import com.hunliji.hljpaymentlibrary.adapters.xiaoxi_installment.AuthItemListAdapter;
import com.hunliji.hljpaymentlibrary.adapters.xiaoxi_installment.viewholders
        .BasicAuthItemHeaderViewHolder;
import com.hunliji.hljpaymentlibrary.api.xiaoxi_installment.XiaoxiInstallmentApi;
import com.hunliji.hljpaymentlibrary.models.PayRxEvent;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.AuthItem;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.wrappers
        .XiaoxiInstallmentAuthItemsData;
import com.hunliji.hljpaymentlibrary.utils.xiaoxi_installment.XiaoxiInstallmentAuthorization;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * 基础授信
 * Created by chen_bin on 2017/8/10 0010.
 */
public class BasicAuthItemListActivity extends HljBaseActivity implements
        OnItemClickListener<AuthItem> {

    @BindView(R2.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R2.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;

    private View headerView;

    private AuthItemListAdapter adapter;

    private XiaoxiInstallmentAuthItemsData authItemsData;
    private AuthItem authItem;

    private boolean isPay;

    private Subscription rxBusEventSub;
    private HljHttpSubscriber initSub;

    public final static String ARG_IS_PAY = "is_pay";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSwipeBackEnable(false);
        setContentView(R.layout.hlj_common_fragment_ptr_recycler_view___cm);
        ButterKnife.bind(this);
        initValues();
        initViews();
        initLoad();
        registerRxBusEvent();
    }

    private void initValues() {
        isPay = getIntent().getBooleanExtra(ARG_IS_PAY, false);
    }

    private void initViews() {
        headerView = View.inflate(this, R.layout.basic_auth_item_header_item___pay, null);
        emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
        emptyView.setOnEmptyClickListener(new HljEmptyView.OnEmptyClickListener() {
            @Override
            public void onEmptyClickListener() {
                initLoad();
            }
        });
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                initLoad();
            }
        });
        recyclerView.setMode(PullToRefreshBase.Mode.DISABLED);
        recyclerView.getRefreshableView()
                .setPadding(0, 0, 0, CommonUtil.dp2px(this, 10));
        recyclerView.getRefreshableView()
                .setLayoutManager(new LinearLayoutManager(this));
        adapter = new AuthItemListAdapter(this);
        adapter.setOnItemClickListener(this);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
    }

    private void initLoad() {
        if (initSub == null || initSub.isUnsubscribed()) {
            Observable<JsonElement> realNameObb = XiaoxiInstallmentApi.getRealNameObb();
            Observable<HljHttpResult<XiaoxiInstallmentAuthItemsData>> authItemsDataObb =
                    XiaoxiInstallmentApi.getAuthItemsObb(
                    this,
                    XiaoxiInstallmentApi.AUTH_ITEM_TYPE_BASIC);
            Observable<ResultZip> observable = Observable.zip(realNameObb,
                    authItemsDataObb,
                    new Func2<JsonElement, HljHttpResult<XiaoxiInstallmentAuthItemsData>,
                            ResultZip>() {
                        @Override
                        public ResultZip call(
                                JsonElement jsonElement,
                                HljHttpResult<XiaoxiInstallmentAuthItemsData>
                                        authItemsDataHljHttpResult) {
                            ResultZip resultZip = new ResultZip();
                            resultZip.jsonElement = jsonElement;
                            resultZip.authItemsDataHljHttpResult = authItemsDataHljHttpResult;
                            return resultZip;
                        }
                    });
            initSub = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<ResultZip>() {
                        @Override
                        public void onNext(ResultZip resultZip) {
                            setData(resultZip);
                        }
                    })
                    .setDataNullable(true)
                    .setEmptyView(emptyView)
                    .setProgressBar(progressBar)
                    .setContentView(recyclerView)
                    .build();
            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(initSub);
        }
    }

    private void setData(ResultZip resultZip) {
        HljHttpStatus hljHttpStatus = resultZip.authItemsDataHljHttpResult.getStatus();
        if (hljHttpStatus == null || hljHttpStatus.getRetCode() != 0) {
            ToastUtil.showToast(this, hljHttpStatus == null ? null : hljHttpStatus.getMsg(), 0);
            emptyView.showEmptyView();
            recyclerView.setVisibility(View.GONE);
            return;
        }
        authItemsData = resultZip.authItemsDataHljHttpResult.getData();
        if (authItemsData == null || authItemsData.isEmpty()) {
            emptyView.showEmptyView();
            recyclerView.setVisibility(View.GONE);
            return;
        }
        BasicAuthItemHeaderViewHolder headerViewHolder = (BasicAuthItemHeaderViewHolder)
                headerView.getTag();
        if (headerViewHolder == null) {
            adapter.setHeaderView(headerView);
            headerViewHolder = new BasicAuthItemHeaderViewHolder(headerView);
            headerView.setTag(headerViewHolder);
        }
        headerViewHolder.setView(BasicAuthItemListActivity.this, resultZip.jsonElement, 0, 0);
        adapter.setGroupIndex(authItemsData.getGroupIndex());
        adapter.setAuthItems(authItemsData.getAuthItems());
    }

    private class ResultZip {
        private JsonElement jsonElement;
        private HljHttpResult<XiaoxiInstallmentAuthItemsData> authItemsDataHljHttpResult;
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
                                case ADD_BASIC_USER_INFO_SUCCESS:
                                case AUTHORIZE_HOUSE_FUND_SUCCESS:
                                case AUTHORIZE_CREDIT_CARD_BILL_SUCCESS:
                                    if (authItem == null || authItemsData == null ||
                                            authItemsData.isEmpty()) {
                                        return;
                                    }
                                    authItem.setStatus(AuthItem.STATUS_AUTHORIZED);
                                    adapter.notifyDataSetChanged();
                                    if (authItemsData.getStatus() == AuthItem.STATUS_AUTHORIZED) {
                                        Intent intent = new Intent(BasicAuthItemListActivity.this,
                                                BasicAuthenticationResultActivity.class);
                                        intent.putExtra(BasicAuthenticationResultActivity
                                                        .ARG_IS_PAY,
                                                isPay);
                                        startActivity(intent);
                                        finish();
                                        overridePendingTransition(0, 0);
                                    }
                                    break;
                            }
                        }
                    });
        }
    }

    @Override
    public void onItemClick(int position, AuthItem authItem) {
        if (authItem == null || authItem.getStatus() == AuthItem.STATUS_AUTHORIZED) {
            return;
        }
        this.authItem = authItem;
        XiaoxiInstallmentAuthorization.getInstance()
                .authorizationJump(this, authItem.getUrl(), authItem.getCode());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        RxBus.getDefault()
                .post(new PayRxEvent(PayRxEvent.RxEventType.AUTHORIZE_CANCEL, null));
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(rxBusEventSub, initSub);
    }
}