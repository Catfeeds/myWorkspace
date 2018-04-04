package com.hunliji.cardmaster.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hunliji.cardmaster.R;
import com.hunliji.cardmaster.api.wallet.WalletApi;
import com.hunliji.hljcardcustomerlibrary.views.activities.BalanceActivity;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.models.wallet.Wallet;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljinsurancelibrary.views.activities.MyPolicyListActivity;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * 我的钱包
 * Created by jinxin on 2017/11/24 0024.
 */
@Route(path = RouterPath.IntentPath.Customer.MY_WALLET_ACTIVITY)
public class MyWalletActivity extends HljBaseActivity {

    @BindView(R.id.tv_balance)
    TextView tvBalance;
    @BindView(R.id.layout_insurance_balance)
    LinearLayout insuranceBalance;
    @BindView(R.id.tv_insurance_count)
    TextView tvInsuranceCount;
    @BindView(R.id.layout_insurance_policy)
    LinearLayout insuranceLayout;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    private HljHttpSubscriber loadSub;
    private Subscription rxSub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_wallet);
        ButterKnife.bind(this);

        initLoad();
        registerRxEvent();
    }


    private void registerRxEvent() {
        if (rxSub == null || rxSub.isUnsubscribed()) {
            rxSub = RxBus.getDefault()
                    .toObservable(RxEvent.class)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new RxBusSubscriber<RxEvent>() {
                        @Override
                        protected void onEvent(RxEvent rxEvent) {
                            switch (rxEvent.getType()) {
                                case POLICY_INFO_COMPLETED_SUCCESS:
                                    initLoad();
                                    break;
                            }
                        }
                    });
        }
    }

    private void initLoad() {
        if (loadSub == null || loadSub.isUnsubscribed()) {
            loadSub = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<Wallet>() {
                        @Override
                        public void onNext(Wallet wallet) {
                            setWalletData(wallet);
                        }
                    })
                    .setProgressBar(progressBar)
                    .build();
            WalletApi.getWallet()
                    .subscribe(loadSub);
        }
    }

    private void setWalletData(Wallet wallet) {
        DecimalFormat df = new DecimalFormat("#####0.00");
        tvBalance.setText(getString(R.string.label_balance_count, df.format(wallet.getBalance())));
        if (wallet.getPendingInsuranceNum() > 0) {
            tvInsuranceCount.setText(wallet.getPendingInsuranceNum() + "份保险待领取");
        } else {
            tvInsuranceCount.setText(null);
        }
    }

    @OnClick(R.id.layout_insurance_balance)
    void onBalance() {
        Intent intent = new Intent(this, BalanceActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.layout_insurance_policy)
    void onMyInsurance() {
        Intent intent = new Intent(this, MyPolicyListActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(loadSub, rxSub);
    }
}
