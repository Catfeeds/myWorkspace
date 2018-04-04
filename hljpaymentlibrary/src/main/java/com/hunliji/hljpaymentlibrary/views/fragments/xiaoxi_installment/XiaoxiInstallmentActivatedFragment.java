package com.hunliji.hljpaymentlibrary.views.fragments.xiaoxi_installment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.example.suncloud.hljweblibrary.HljWeb;
import com.handmark.pulltorefresh.library.MyScrollView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.entities.HljHttpStatus;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljpaymentlibrary.R;
import com.hunliji.hljpaymentlibrary.R2;
import com.hunliji.hljpaymentlibrary.api.xiaoxi_installment.XiaoxiInstallmentApi;
import com.hunliji.hljpaymentlibrary.models.PayRxEvent;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.AuthItem;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.CreditLimit;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.wrappers
        .XiaoxiInstallmentAuthItemsData;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.wrappers.XiaoxiInstallmentOrdersData;
import com.hunliji.hljpaymentlibrary.views.activities.xiaoxi_installment.BasicAuthItemListActivity;
import com.hunliji.hljpaymentlibrary.views.activities.xiaoxi_installment.LimitAuthItemListActivity;
import com.hunliji.hljpaymentlibrary.views.activities.xiaoxi_installment.MyBillListActivity;
import com.hunliji.hljpaymentlibrary.views.activities.xiaoxi_installment
        .XiaoxiInstallmentBankCardSettingActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * 小犀分期-已激活页
 * Created by chen_bin on 2017/9/13 0013.
 */
public class XiaoxiInstallmentActivatedFragment extends RefreshFragment implements
        PullToRefreshScrollView.OnRefreshListener<MyScrollView> {
    @BindView(R2.id.tv_available_limit)
    TextView tvAvailableLimit;
    @BindView(R2.id.tv_available_limit_decimal)
    TextView tvAvailableLimitDecimal;
    @BindView(R2.id.tv_total_limit)
    TextView tvTotalLimit;
    @BindView(R2.id.available_limit_layout)
    LinearLayout availableLimitLayout;
    @BindView(R2.id.expired_limit_layout)
    LinearLayout expiredLimitLayout;
    @BindView(R2.id.tv_prepare_repay_amount)
    TextView tvPrepareRepayAmount;
    @BindView(R2.id.scroll_view)
    PullToRefreshScrollView scrollView;
    @BindView(R2.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    Unbinder unbinder;
    private int status;
    private CreditLimit creditLimit;
    private XiaoxiInstallmentOrdersData ordersData;
    private Subscription rxBusEventSub;
    private HljHttpSubscriber refreshSub;
    private HljHttpSubscriber checkSub;
    private HljHttpSubscriber refreshOrdersSub;

    public static XiaoxiInstallmentActivatedFragment newInstance(int status) {
        XiaoxiInstallmentActivatedFragment fragment = new XiaoxiInstallmentActivatedFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("status", status);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            status = getArguments().getInt("status", AuthItem.STATUS_UNAUTHORIZED);
        }
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_xiaoxi_installment_activated___pay,
                container,
                false);
        unbinder = ButterKnife.bind(this, rootView);
        initViews();
        return rootView;
    }

    private void initViews() {
        scrollView.setOnRefreshListener(this);
        emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
        emptyView.setOnEmptyClickListener(new HljEmptyView.OnEmptyClickListener() {
            @Override
            public void onEmptyClickListener() {
                onRefresh(null);
            }
        });
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                onRefresh(null);
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onRefresh(null);
        registerRxBusEvent();
    }

    @Override
    public void onRefresh(PullToRefreshBase<MyScrollView> refreshView) {
        if (refreshSub == null || refreshSub.isUnsubscribed()) {
            Observable<HljHttpResult<CreditLimit>> creditLimitObb = XiaoxiInstallmentApi
                    .getCreditLimitObb(
                    getContext());
            Observable<XiaoxiInstallmentOrdersData> ordersObb = XiaoxiInstallmentApi.getMyBillsObb(
                    getContext());
            Observable<ResultZip> observable = Observable.zip(creditLimitObb,
                    ordersObb,
                    new Func2<HljHttpResult<CreditLimit>, XiaoxiInstallmentOrdersData, ResultZip>
                            () {
                        @Override
                        public ResultZip call(
                                HljHttpResult<CreditLimit> creditLimitHljHttpResult,
                                XiaoxiInstallmentOrdersData ordersData) {
                            ResultZip resultZip = new ResultZip();
                            resultZip.creditLimit = creditLimitHljHttpResult == null ? null :
                                    creditLimitHljHttpResult.getData();
                            resultZip.ordersData = ordersData;
                            return resultZip;
                        }
                    });
            refreshSub = HljHttpSubscriber.buildSubscriber(getContext())
                    .setOnNextListener(new SubscriberOnNextListener<ResultZip>() {
                        @Override
                        public void onNext(ResultZip resultZip) {
                            scrollView.getRefreshableView()
                                    .setVisibility(View.VISIBLE);
                            creditLimit = resultZip.creditLimit;
                            ordersData = resultZip.ordersData;
                            setData();
                        }
                    })
                    .setDataNullable(true)
                    .setEmptyView(emptyView)
                    .setContentView(scrollView)
                    .setPullToRefreshBase(scrollView)
                    .setProgressBar(scrollView.isRefreshing() ? null : progressBar)
                    .build();
            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(refreshSub);
        }
    }

    private void setData() {
        switch (status) {
            case AuthItem.STATUS_AUTHORIZED:
                availableLimitLayout.setVisibility(View.VISIBLE);
                expiredLimitLayout.setVisibility(View.GONE);
                if (creditLimit != null) {
                    String str = CommonUtil.formatDouble2StringWithTwoFloat(creditLimit
                            .getAvailableLimit());
                    String[] s = str.split("\\.");
                    tvAvailableLimit.setText(s.length > 0 ? s[0] : "0");
                    tvAvailableLimitDecimal.setText(s.length > 1 ? s[1] : "00");
                    tvTotalLimit.setText(getString(R.string.label_total_limit___pay,
                            CommonUtil.formatDouble2StringWithTwoFloat(creditLimit.getTotalLimit
                                    ())));
                }
                break;
            default:
                availableLimitLayout.setVisibility(View.GONE);
                expiredLimitLayout.setVisibility(View.VISIBLE);
                break;
        }
        tvPrepareRepayAmount.setText(ordersData == null || ordersData.getPrepareRepayAmount() <=
                0 ? "" : getString(
                R.string.label_prepare_repay_amount___pay,
                CommonUtil.formatDouble2StringWithTwoFloat(ordersData.getPrepareRepayAmount())));
    }

    private class ResultZip {
        private CreditLimit creditLimit;
        private XiaoxiInstallmentOrdersData ordersData;
    }

    private void refreshOrders() {
        if (refreshOrdersSub == null || refreshOrdersSub.isUnsubscribed()) {
            refreshOrdersSub = HljHttpSubscriber.buildSubscriber(getContext())
                    .setOnNextListener(new SubscriberOnNextListener<XiaoxiInstallmentOrdersData>() {
                        @Override
                        public void onNext(XiaoxiInstallmentOrdersData data) {
                            ordersData = data;
                            setData();
                        }
                    })
                    .setProgressBar(progressBar)
                    .build();
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
                                    refreshOrders();
                                    break;
                            }
                        }
                    });
        }
    }

    @SuppressWarnings("unchecked")
    @OnClick(R2.id.increase_limit_layout)
    void onIncreaseLimit() {
        if (checkSub == null || checkSub.isUnsubscribed()) {
            checkSub = HljHttpSubscriber.buildSubscriber(getContext())
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpResult<XiaoxiInstallmentAuthItemsData>>() {
                        @Override
                        public void onNext(HljHttpResult<XiaoxiInstallmentAuthItemsData> result) {
                            HljHttpStatus hljHttpStatus = result.getStatus();
                            if (hljHttpStatus == null || hljHttpStatus.getRetCode() != 0) {
                                ToastUtil.showToast(getContext(),
                                        hljHttpStatus == null ? null : hljHttpStatus.getMsg(),
                                        0);
                                return;
                            }
                            XiaoxiInstallmentAuthItemsData authItemsData = result.getData();
                            if (authItemsData == null || authItemsData.isEmpty()) {
                                ToastUtil.showToast(getContext(),
                                        null,
                                        R.string.hint_get_auth_items_empty___pay);
                                return;
                            }
                            startActivity(new Intent(getContext(),
                                    authItemsData.getStatus() == AuthItem.STATUS_AUTHORIZED ?
                                            LimitAuthItemListActivity.class :
                                            BasicAuthItemListActivity.class));
                        }
                    })
                    .setProgressDialog(DialogUtil.createProgressDialog(getContext()))
                    .build();
            XiaoxiInstallmentApi.getAuthItemsObb(getContext(),
                    XiaoxiInstallmentApi.AUTH_ITEM_TYPE_BASIC)
                    .subscribe(checkSub);
        }
    }

    @OnClick(R2.id.btn_activate_limit)
    void onActivateLimit() {
        startActivity(new Intent(getContext(), BasicAuthItemListActivity.class));
    }

    @OnClick(R2.id.my_bill_layout)
    void onMyBill() {
        startActivity(new Intent(getContext(), MyBillListActivity.class));
    }

    @OnClick(R2.id.bank_card_setting_layout)
    void onBankCardSetting() {
        startActivity(new Intent(getContext(), XiaoxiInstallmentBankCardSettingActivity.class));
    }

    @OnClick(R2.id.common_question_layout)
    void onCommonQuestion() {
        HljWeb.startWebView(getActivity(), XiaoxiInstallmentApi.XIAOXI_INSTALLMENT_QUESTIONS_URL);
    }

    @OnClick(R2.id.installment_market_layout)
    void onInstallmentMarket() {
        ARouter.getInstance()
                .build(RouterPath.IntentPath.Customer.INSTALMENT_MERCHANT_LIST_ACTIVITY)
                .navigation(getContext());
    }

    @Override
    public void refresh(Object... params) {
        if (scrollView == null) {
            return;
        }
        if (params == null || params.length < 2) {
            return;
        }
        status = (int) params[0];
        if (params[1] != null) {
            creditLimit = (CreditLimit) params[1];
        }
        setData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
        CommonUtil.unSubscribeSubs(rxBusEventSub, refreshSub, checkSub, refreshOrdersSub);
    }
}