package com.hunliji.hljpaymentlibrary.views.activities.xiaoxi_installment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.LinearLayout;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.entities.HljHttpStatus;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljpaymentlibrary.R;
import com.hunliji.hljpaymentlibrary.R2;
import com.hunliji.hljpaymentlibrary.adapters.xiaoxi_installment.AuthItemListAdapter;
import com.hunliji.hljpaymentlibrary.adapters.xiaoxi_installment.viewholders
        .LimitAuthItemHeaderViewHolder;
import com.hunliji.hljpaymentlibrary.api.xiaoxi_installment.XiaoxiInstallmentApi;
import com.hunliji.hljpaymentlibrary.models.PayRxEvent;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.AuthItem;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.CreditLimit;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.wrappers
        .XiaoxiInstallmentAuthItemsData;
import com.hunliji.hljpaymentlibrary.utils.xiaoxi_installment.XiaoxiInstallmentAuthorization;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * 小犀分期-提升额度-额度授信列表
 * Created by chen_bin on 2017/8/10 0010.
 */
public class LimitAuthItemListActivity extends HljBaseActivity implements
        OnItemClickListener<AuthItem> {

    @BindView(R2.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R2.id.timeout_layout)
    LinearLayout timeoutLayout;
    @BindView(R2.id.fail_layout)
    LinearLayout failLayout;
    @BindView(R2.id.loading_layout)
    LinearLayout loadingLayout;

    private View headerView;

    private AuthItemListAdapter adapter;

    private AuthItem authItem;
    private CreditLimit creditLimit;

    private boolean isPay;

    private Subscription rxBusEventSub;
    private HljHttpSubscriber initSub;
    private HljHttpSubscriber refreshLimitSub;

    public final static String ARG_IS_PAY = "is_pay";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSwipeBackEnable(false);
        setContentView(R.layout.activity_limit_auth_item_list___pay);
        ButterKnife.bind(this);
        initValues();
        initViews();
        initLoad();
        registerRxBusEvent();
    }

    private void initValues() {
        isPay = getIntent().getBooleanExtra(ARG_IS_PAY, false);
        setOkButton(R.mipmap.icon_refresh_primary_44_44);
    }

    private void initViews() {
        headerView = View.inflate(this, R.layout.limit_auth_item_header_item___pay, null);
        recyclerView.setMode(PullToRefreshBase.Mode.DISABLED);
        recyclerView.getRefreshableView()
                .setLayoutManager(new LinearLayoutManager(this));
        adapter = new AuthItemListAdapter(this);
        adapter.setOnItemClickListener(this);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
    }

    private void initLoad() {
        if (initSub == null || initSub.isUnsubscribed()) {
            recyclerView.setVisibility(View.GONE);
            failLayout.setVisibility(View.GONE);
            timeoutLayout.setVisibility(View.GONE);
            final Observable<HljHttpResult<CreditLimit>> creditLimitObb = XiaoxiInstallmentApi
                    .getCreditLimitObb(
                    this);
            Observable<HljHttpResult<XiaoxiInstallmentAuthItemsData>> authItemsObb =
                    XiaoxiInstallmentApi.getAuthItemsObb(
                    this,
                    XiaoxiInstallmentApi.AUTH_ITEM_TYPE_INCREASE_LIMIT);
            Observable<ResultZip> observable = Observable.zip(creditLimitObb,
                    authItemsObb,
                    new Func2<HljHttpResult<CreditLimit>,
                            HljHttpResult<XiaoxiInstallmentAuthItemsData>, ResultZip>() {
                        @Override
                        public ResultZip call(
                                HljHttpResult<CreditLimit> creditLimitHljHttpResult,
                                HljHttpResult<XiaoxiInstallmentAuthItemsData>
                                        authItemsDataHljHttpResult) {
                            ResultZip resultZip = new ResultZip();
                            resultZip.creditLimitHljHttpResult = creditLimitHljHttpResult;
                            resultZip.authItemsData = authItemsDataHljHttpResult == null ? null :
                                    authItemsDataHljHttpResult.getData();
                            return resultZip;
                        }
                    });
            initSub = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<ResultZip>() {
                        @Override
                        public void onNext(ResultZip resultZip) {
                            setCreditLimit(resultZip.creditLimitHljHttpResult);
                            setAuthItems(resultZip.authItemsData);
                        }
                    })
                    .setOnErrorListener(new SubscriberOnErrorListener<Throwable>() {
                        @Override
                        public void onError(Throwable e) {
                            recyclerView.setVisibility(View.GONE);
                            timeoutLayout.setVisibility(View.VISIBLE);
                        }
                    })
                    .setDataNullable(true)
                    .setContentView(recyclerView)
                    .setProgressBar(loadingLayout)
                    .build();
            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(initSub);
        }
    }

    private void refreshCreditLimit() {
        if (refreshLimitSub == null || refreshLimitSub.isUnsubscribed()) {
            recyclerView.setVisibility(View.GONE);
            failLayout.setVisibility(View.GONE);
            timeoutLayout.setVisibility(View.GONE);
            refreshLimitSub = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpResult<CreditLimit>>() {
                        @Override
                        public void onNext(HljHttpResult<CreditLimit> creditLimitHljHttpResult) {
                            setCreditLimit(creditLimitHljHttpResult);
                        }
                    })
                    .setOnErrorListener(new SubscriberOnErrorListener<Throwable>() {
                        @Override
                        public void onError(Throwable e) {
                            recyclerView.setVisibility(View.GONE);
                            timeoutLayout.setVisibility(View.VISIBLE);
                        }
                    })
                    .setDataNullable(true)
                    .setProgressBar(loadingLayout)
                    .setContentView(recyclerView)
                    .build();
            XiaoxiInstallmentApi.getCreditLimitObb(this)
                    .subscribe(refreshLimitSub);
        }
    }

    private void setCreditLimit(HljHttpResult<CreditLimit> creditLimitHljHttpResult) {
        if (creditLimitHljHttpResult == null) {
            failLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            return;
        }
        HljHttpStatus hljHttpStatus = creditLimitHljHttpResult.getStatus();
        if (hljHttpStatus == null || hljHttpStatus.getRetCode() != 0) {
            ToastUtil.showToast(this, hljHttpStatus == null ? null : hljHttpStatus.getMsg(), 0);
            failLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            return;
        }
        creditLimit = creditLimitHljHttpResult.getData();
        if (creditLimit == null) {
            failLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            return;
        }
        LimitAuthItemHeaderViewHolder headerViewHolder = (LimitAuthItemHeaderViewHolder)
                headerView.getTag();
        if (headerViewHolder == null) {
            adapter.setHeaderView(headerView);
            headerViewHolder = new LimitAuthItemHeaderViewHolder(headerView);
            headerViewHolder.setShowPayBtn(isPay);
            headerView.setTag(headerViewHolder);
        }
        headerViewHolder.setView(LimitAuthItemListActivity.this, creditLimit, 0, 0);
    }

    private void setAuthItems(XiaoxiInstallmentAuthItemsData authItemsData) {
        if (authItemsData == null || authItemsData.isEmpty()) {
            failLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            return;
        }
        adapter.setAuthItems(authItemsData.getAuthItems());
    }

    private class ResultZip {
        private HljHttpResult<CreditLimit> creditLimitHljHttpResult;
        private XiaoxiInstallmentAuthItemsData authItemsData;
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
                                case AUTHORIZE_CREDIT_CARD_BILL_SUCCESS:
                                case AUTHORIZE_DEPOSIT_CARD_BILL_SUCCESS:
                                case AUTHORIZE_HOUSE_FUND_SUCCESS:
                                    if (authItem != null) {
                                        authItem.setStatus(AuthItem.STATUS_AUTHORIZED);
                                        adapter.notifyDataSetChanged();
                                        refreshCreditLimit();
                                    }
                                    break;
                            }
                        }
                    });
        }
    }

    @OnClick(R2.id.btn_fail)
    void onFail() {
        onBackPressed();
    }

    @Override
    public void onOkButtonClick() {
        if (CommonUtil.isUnsubscribed(initSub, refreshLimitSub)) {
            initLoad();
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
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(rxBusEventSub, initSub, refreshLimitSub);
    }
}