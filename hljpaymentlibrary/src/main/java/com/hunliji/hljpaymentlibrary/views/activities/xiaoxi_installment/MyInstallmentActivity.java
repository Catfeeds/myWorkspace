package com.hunliji.hljpaymentlibrary.views.activities.xiaoxi_installment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.example.suncloud.hljweblibrary.HljWeb;
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
import com.hunliji.hljpaymentlibrary.api.xiaoxi_installment.XiaoxiInstallmentApi;
import com.hunliji.hljpaymentlibrary.models.PayRxEvent;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.AuthItem;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.CreditLimit;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.wrappers
        .XiaoxiInstallmentAuthItemsData;
import com.hunliji.hljpaymentlibrary.views.fragments.xiaoxi_installment
        .XiaoxiInstallmentActivatedFragment;
import com.hunliji.hljpaymentlibrary.views.fragments.xiaoxi_installment
        .XiaoxiInstallmentUnactivatedFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;

/**
 * 小犀分期-我的分期页
 * Created by chen_bin on 2017/8/17 0017.
 */
public class MyInstallmentActivity extends HljBaseActivity {

    @BindView(R2.id.fragment_content)
    FrameLayout fragmentContent;
    @BindView(R2.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;

    private Subscription rxBusEventSub;
    private HljHttpSubscriber initSub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSwipeBackEnable(false);
        setContentView(R.layout.hlj_common_fragment_content);
        ButterKnife.bind(this);
        initViews();
        initLoad();
        registerRxBusEvent();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initLoad();
    }

    private void initViews() {
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
    }

    private void initLoad() {
        if (initSub == null || initSub.isUnsubscribed()) {
            fragmentContent.setVisibility(View.GONE);
            initSub = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpResult<XiaoxiInstallmentAuthItemsData>>() {
                        @Override
                        public void onNext(HljHttpResult<XiaoxiInstallmentAuthItemsData> result) {
                            HljHttpStatus hljHttpStatus = result.getStatus();
                            int retCode = hljHttpStatus == null ? -1 : hljHttpStatus.getRetCode();
                            if (retCode != 0 && retCode != 7001) {
                                ToastUtil.showToast(MyInstallmentActivity.this,
                                        hljHttpStatus == null ? null : hljHttpStatus.getMsg(),
                                        0);
                                emptyView.showEmptyView();
                                fragmentContent.setVisibility(View.GONE);
                                return;
                            }
                            int status = AuthItem.STATUS_UNAUTHORIZED;
                            XiaoxiInstallmentAuthItemsData authItemsData = result.getData();
                            if (authItemsData != null) {
                                status = authItemsData.getStatus();
                            }
                            setFragment(status, null);
                        }
                    })
                    .setEmptyView(emptyView)
                    .setProgressBar(progressBar)
                    .setContentView(fragmentContent)
                    .build();
            XiaoxiInstallmentApi.getAuthItemsObb(this, XiaoxiInstallmentApi.AUTH_ITEM_TYPE_BASIC)
                    .subscribe(initSub);
        }
    }

    private void setFragment(int status, CreditLimit creditLimit) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        XiaoxiInstallmentUnactivatedFragment unactivatedFragment =
                (XiaoxiInstallmentUnactivatedFragment) fm.findFragmentByTag(
                "XiaoxiInstallmentUnactivatedFragment");
        XiaoxiInstallmentActivatedFragment activatedFragment =
                (XiaoxiInstallmentActivatedFragment) fm.findFragmentByTag(
                "XiaoxiInstallmentActivatedFragment");
        if (unactivatedFragment != null && !unactivatedFragment.isHidden()) {
            ft.hide(unactivatedFragment);
        }
        if (activatedFragment != null && !activatedFragment.isHidden()) {
            ft.hide(activatedFragment);
        }
        if (status == AuthItem.STATUS_UNAUTHORIZED) {
            setOkTextSize(14);
            setOkText(R.string.label_help__pay);
            setOkTextColor(ContextCompat.getColor(this, R.color.colorBlack3));
            if (unactivatedFragment != null) {
                ft.show(unactivatedFragment);
            } else {
                unactivatedFragment = XiaoxiInstallmentUnactivatedFragment.newInstance();
                ft.add(R.id.fragment_content,
                        unactivatedFragment,
                        "XiaoxiInstallmentUnactivatedFragment");
            }
        } else {
            hideOkText();
            if (activatedFragment != null) {
                activatedFragment.refresh(status, creditLimit);
                ft.show(activatedFragment);
            } else {
                activatedFragment = XiaoxiInstallmentActivatedFragment.newInstance(status);
                ft.add(R.id.fragment_content,
                        activatedFragment,
                        "XiaoxiInstallmentActivatedFragment");
            }
        }
        ft.commitAllowingStateLoss();
    }

    @Override
    public void onOkButtonClick() {
        HljWeb.startWebView(this, XiaoxiInstallmentApi.XIAOXI_INSTALLMENT_QUESTIONS_URL);
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
                                case INIT_LIMIT_CLOSE:
                                case INCREASE_LIMIT_CLOSE:
                                    setFragment(AuthItem.STATUS_AUTHORIZED,
                                            (CreditLimit) payRxEvent.getObject());
                                    break;
                            }
                        }
                    });
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(rxBusEventSub, initSub);
    }
}