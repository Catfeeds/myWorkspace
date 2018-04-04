package me.suncloud.marrymemo.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hunliji.hljcardcustomerlibrary.views.activities.BalanceActivity;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljhttplibrary.utils.FinancialSwitch;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljinsurancelibrary.views.activities.MyPolicyListActivity;
import com.hunliji.hljpaymentlibrary.api.xiaoxi_installment.XiaoxiInstallmentApi;
import com.hunliji.hljpaymentlibrary.models.PayRxEvent;
import com.hunliji.hljpaymentlibrary.views.activities.xiaoxi_installment.MyInstallmentActivity;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.wallet.WalletApi;
import me.suncloud.marrymemo.model.PointRecord;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.model.wallet.Wallet;
import me.suncloud.marrymemo.util.PointUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.view.wallet.FinancialHomeActivity;
import me.suncloud.marrymemo.view.wallet.GoldMarketWebViewActivity;
import me.suncloud.marrymemo.view.wallet.MyCouponListActivity;
import me.suncloud.marrymemo.view.wallet.MyRedPacketListActivity;
import rx.Observable;
import rx.Subscription;
import rx.functions.Func2;

@Route(path = RouterPath.IntentPath.Customer.MY_WALLET_ACTIVITY)
public class MyWalletActivity extends HljBaseActivity {
    private static final int EXCHANGE_CODE = 100;
    private static final int SIGN_CODE = 101;
    private static final int COUPON_CODE = 102;
    private static final int BALANCE_CODE = 103;
    @BindView(R.id.packet_count)
    TextView packetCount;
    @BindView(R.id.coupon_count)
    TextView couponCount;
    @BindView(R.id.balance_count)
    TextView balanceCount;
    @BindView(R.id.gold_count)
    TextView goldCount;
    @BindView(R.id.tv_insurance_count)
    TextView tvInsuranceCount;
    @BindView(R.id.my_installment_layout)
    FrameLayout myInstallmentLayout;
    @BindView(R.id.content_layout)
    LinearLayout contentLayout;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.market_layout)
    LinearLayout marketLayout;
    @BindView(R.id.tv_fund_name)
    TextView tvFundName;
    private PointRecord pointRecord;
    private User user;
    private boolean backMain;
    private boolean isFund;//理财开关
    private Wallet wallet;
    private Subscription rxBusEventSub;
    private Subscription rxBusPaySub;
    private HljHttpSubscriber initSub;
    private HljHttpSubscriber refreshWalletSub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        backMain = getIntent().getBooleanExtra("backMain", false);
        setSwipeBackEnable(!backMain);
        setContentView(R.layout.activity_my_wallet);
        ButterKnife.bind(this);
        initValues();
        initViews();
        initLoad();
        registerRxBusEvent();
    }

    private void initValues() {
        user = Session.getInstance()
                .getCurrentUser(this);
        pointRecord = PointUtil.getInstance()
                .getPointRecord(this, user.getId());
        //        DataConfig dataConfig = Session.getInstance()
        //                .getDataConfig(this);
        //        if (dataConfig != null) {
        //            isFund = dataConfig.isFund();
        //        }
        if (pointRecord != null) {
            goldCount.setText(getString(R.string.label_red_packet_count, pointRecord.getBalance()));
        }
    }

    private void initViews() {
        emptyView.setNetworkHint2Id(com.hunliji.hljpaymentlibrary.R.string
                .label_click_to_reload___cm);
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

        onAppSubmit();
    }

    /**
     * 审核特殊处理
     */
    private void onAppSubmit() {
        if (FinancialSwitch.INSTANCE.isClosed(this)) {
            marketLayout.setVisibility(View.GONE);
        }
    }

    private void initLoad() {
        if (initSub == null || initSub.isUnsubscribed()) {
            Observable<Wallet> walletObb = WalletApi.getWallet();
            Observable<Boolean> isSupportedObb = XiaoxiInstallmentApi.isSupportedObb();
            Observable<ResultZip> observable = Observable.zip(walletObb,
                    isSupportedObb,
                    new Func2<Wallet, Boolean, ResultZip>() {
                        @Override
                        public ResultZip call(Wallet wallet, Boolean aBoolean) {
                            ResultZip resultZip = new ResultZip();
                            resultZip.wallet = wallet;
                            resultZip.isSupported = aBoolean != null && aBoolean;
                            return resultZip;
                        }
                    });
            initSub = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<ResultZip>() {
                        @Override
                        public void onNext(ResultZip resultZip) {
                            setWalletData(resultZip.wallet);
                            setSupportedData(resultZip.isSupported);
                        }
                    })
                    .setDataNullable(true)
                    .setEmptyView(emptyView)
                    .setProgressBar(progressBar)
                    .setContentView(contentLayout)
                    .build();
            observable.subscribe(initSub);
        }
    }

    private class ResultZip {
        private Wallet wallet;
        private boolean isSupported;
    }

    private void refreshWalletData() {
        if (refreshWalletSub == null || refreshWalletSub.isUnsubscribed()) {
            refreshWalletSub = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<Wallet>() {
                        @Override
                        public void onNext(Wallet wallet) {
                            setWalletData(wallet);
                        }
                    })
                    .setProgressBar(progressBar)
                    .build();
            WalletApi.getWallet()
                    .subscribe(refreshWalletSub);
        }
    }

    private void setSupportedData(boolean isSupported) {
        myInstallmentLayout.setVisibility(isSupported ? View.VISIBLE : View.GONE);
    }

    private void setWalletData(Wallet wallet) {
        this.wallet = wallet;
        packetCount.setText(String.valueOf(wallet.getPacketCount()));
        couponCount.setText(String.valueOf(wallet.getCouponCount()));
        DecimalFormat df = new DecimalFormat("#####0.00");
        //        if (isFund || wallet.isFundUserFlag()) {
        //            tvFundName.setText(getString(R.string.label_cash_fund));
        //            balanceCount.setText(getString(R.string.label_balance_count,
        //                    df.format(wallet.getFundTotal())));
        //        } else {
        tvFundName.setText(getString(R.string.label_balance));
        balanceCount.setText(getString(R.string.label_balance_count,
                df.format(wallet.getBalance())));
        //        }

        if (wallet.getPendingInsuranceNum() > 0) {
            tvInsuranceCount.setText(wallet.getPendingInsuranceNum() + "份保险待领取");
        } else {
            tvInsuranceCount.setText(null);
        }
    }

    //优惠码
    public void onExchange(View view) {
        Intent intent = new Intent(this, ExchangeCodeActivity.class);
        startActivityForResult(intent, EXCHANGE_CODE);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    //红包
    public void onRedPacket(View view) {
        startActivity(new Intent(this, MyRedPacketListActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    //商家优惠券的点击
    public void onCoupon(View view) {
        startActivityForResult(new Intent(this, MyCouponListActivity.class), COUPON_CODE);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    //余额
    public void onBalance(View view) {
        if (wallet == null) {
            return;
        }
        Intent intent = new Intent();
        //  礼金理财该版本隐藏
        //        if (isFund || wallet.isFundUserFlag()) {
        //            //开关打开
        //            //开关关闭后，如果转入过理财，显示礼金理财，且跳转至b1.礼金理财
        //            intent.setClass(this, FundManageActivity.class);
        //        } else {
        //开关关闭后，如果用户未转入过理财，则显示余额，且跳转至b2.余额（明细）
        intent.setClass(this, BalanceActivity.class);
        //        }
        startActivityForResult(intent, BALANCE_CODE);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    //金币
    public void onGold(View view) {
        if (pointRecord == null) {
            return;
        }
        Intent intent = new Intent(this, GoldMarketWebViewActivity.class);
        intent.putExtra("pointRecord", pointRecord);
        startActivityForResult(intent, SIGN_CODE);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    //金融超市
    public void onMarket(View view) {
        Intent intent = new Intent(this, FinancialHomeActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    //我的保单
    @OnClick(R.id.insurance_layout)
    public void onMyInsurance() {
        Intent intent = new Intent(this, MyPolicyListActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    //我的分期
    @OnClick(R.id.my_installment_layout)
    void onMyInstallment() {
        startActivity(new Intent(this, MyInstallmentActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    private void refresh() {
        pointRecord = PointUtil.getInstance()
                .getPointRecord(this, user.getId());
        if (goldCount != null) {
            goldCount.setText(getString(R.string.label_red_packet_count, pointRecord.getBalance()));
        }
        refreshWalletData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SIGN_CODE:
                refresh();
                break;
            case EXCHANGE_CODE:
            case COUPON_CODE:
                if (resultCode == RESULT_OK && data != null) {
                    int totalCount = data.getIntExtra("totalCount", -1);
                    if (totalCount > -1) {
                        if (requestCode == EXCHANGE_CODE) {
                            packetCount.setText(getString(R.string.label_red_packet_count,
                                    totalCount));
                        } else {
                            couponCount.setText(getString(R.string.label_coupon_count, totalCount));
                        }
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //注册RxEventBus监听
    private void registerRxBusEvent() {
        if (rxBusEventSub == null || rxBusEventSub.isUnsubscribed()) {
            rxBusEventSub = RxBus.getDefault()
                    .toObservable(RxEvent.class)
                    .subscribe(new RxBusSubscriber<RxEvent>() {
                        @Override
                        protected void onEvent(RxEvent rxEvent) {
                            switch (rxEvent.getType()) {
                                case WITHDRAW_CASH_SUCCESS:
                                case POLICY_WRITE:
                                    refreshWalletData();
                                    break;
                            }
                        }
                    });
        }
        if (rxBusPaySub == null || rxBusPaySub.isUnsubscribed()) {
            rxBusPaySub = RxBus.getDefault()
                    .toObservable(PayRxEvent.class)
                    .subscribe(new RxBusSubscriber<PayRxEvent>() {
                        @Override
                        protected void onEvent(PayRxEvent rxEvent) {
                            switch (rxEvent.getType()) {
                                case PAY_SUCCESS:
                                    refreshWalletData();
                                    break;
                            }
                        }
                    });
        }
    }


    @Override
    public void onBackPressed() {
        if (!backMain) {
            super.onBackPressed();
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("action", getIntent().getStringExtra("action"));
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.activity_anim_default);
            finish();
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(rxBusEventSub, rxBusPaySub, initSub, refreshWalletSub);
    }
}
