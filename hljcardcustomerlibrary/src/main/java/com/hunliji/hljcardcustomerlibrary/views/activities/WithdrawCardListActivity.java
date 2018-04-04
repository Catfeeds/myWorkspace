package com.hunliji.hljcardcustomerlibrary.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcardcustomerlibrary.R;
import com.hunliji.hljcardcustomerlibrary.R2;
import com.hunliji.hljcardcustomerlibrary.adapter.WithdrawCardRecyclerAdapter;
import com.hunliji.hljcardcustomerlibrary.api.CustomerCardApi;
import com.hunliji.hljcardcustomerlibrary.models.CardBalance;
import com.hunliji.hljcardcustomerlibrary.models.WithdrawParam;
import com.hunliji.hljcardlibrary.models.Card;
import com.hunliji.hljcommonlibrary.models.BindInfo;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpCardData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;

/**
 * Created by mo_yu on 2017/6/13.提现请帖列表
 */

public class WithdrawCardListActivity extends HljBaseActivity implements
        WithdrawCardRecyclerAdapter.OnWithdrawClickListener, WithdrawCardRecyclerAdapter
        .OnUnbindClickListener {

    @BindView(R2.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R2.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R2.id.tv_total_balance)
    TextView tvTotalBalance;

    private ArrayList<CardBalance> cardBalances;
    private WithdrawCardRecyclerAdapter adapter;
    private String smsCode;
    private double totalBalance;
    private int canAllinType;
    private WithdrawParam withdrawParam;

    private HljHttpSubscriber refreshSubscriber;
    private HljHttpSubscriber unbindSubscriber;
    private HljHttpSubscriber paramSubscriber;
    private Subscription rxBusEventSub;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_card_list);
        ButterKnife.bind(this);
        setSwipeBackEnable(false);
        initValue();
        initView();
        initWithdrawParam();
        initLoad();
        registerRxBusEvent();
    }

    @Override
    public void onOkButtonClick() {
        super.onOkButtonClick();
        //1可全部提现 0不显示 2显示，但提示请先设置提现账号
        if (canAllinType == 1) {
            Intent intent = new Intent(this, WithdrawV2Activity.class);
            intent.putExtra(WithdrawV2Activity.ARG_WITHDRAW_PARAM, withdrawParam);
            startActivity(intent);
        } else if (canAllinType == 2) {
            ToastUtil.showToast(this, "请先设置提现账号", 0);
        }
    }

    private void initValue() {
        cardBalances = new ArrayList<>();
        smsCode = getIntent().getStringExtra("smsCode");
    }

    private void initView() {
        recyclerView.getRefreshableView()
                .setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setMode(PullToRefreshBase.Mode.DISABLED);
        adapter = new WithdrawCardRecyclerAdapter(this, cardBalances);
        adapter.setOnWithdrawClickListener(this);
        adapter.setOnUnbindClickListener(this);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
    }

    private void initLoad() {
        if (refreshSubscriber == null || refreshSubscriber.isUnsubscribed()) {
            refreshSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpCardData<List<CardBalance>>>() {

                        @Override
                        public void onNext(
                                HljHttpCardData<List<CardBalance>> listHljHttpCardData) {
                            cardBalances.clear();
                            cardBalances.addAll(listHljHttpCardData.getData());
                            totalBalance = listHljHttpCardData.getAmount();
                            tvTotalBalance.setText(getString(R.string.label_price___cm,
                                    totalBalance));
                            canAllinType = listHljHttpCardData.getCanAllin();
                            if (canAllinType != 0) {
                                setOkText(R.string.label_withdraw_cash_count_all);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    })
                    .setProgressBar(progressBar)
                    .build();
            CustomerCardApi.getCardBalancesObb(1)
                    .subscribe(refreshSubscriber);
        }
    }

    public void initWithdrawParam() {
        if (paramSubscriber == null || paramSubscriber.isUnsubscribed()) {
            paramSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<WithdrawParam>() {
                        @Override
                        public void onNext(WithdrawParam withdrawParam) {
                            WithdrawCardListActivity.this.withdrawParam = withdrawParam;
                        }
                    })
                    .build();
            CustomerCardApi.getWithdrawParam()
                    .subscribe(paramSubscriber);
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(refreshSubscriber,
                rxBusEventSub,
                unbindSubscriber,
                paramSubscriber);
    }

    @Override
    public void onWithdraw(CardBalance item, int position) {
        if (item != null) {
            if (item.getBindInfo() == null) {
                Intent intent = new Intent(this, BindBankSettingActivity.class);
                intent.putExtra("card", item.getCard());
                intent.putExtra("can_modify_name", false);
                startActivity(intent);
            } else {
                Intent intent = new Intent();
                if (item.getVersion() == CardBalance.NEW_CARD) {
                    intent.setClass(this, WithdrawV2Activity.class);
                    if (item.getCard() != null) {
                        intent.putExtra(WithdrawV2Activity.ARG_ID,
                                item.getCard()
                                        .getId());
                    }
                } else {
                    intent.putExtra(WithdrawActivity.ARG_SMS_CODE, smsCode);
                    intent.setClass(this, WithdrawActivity.class);
                    if (item.getBindInfo() != null && item.getBindInfo()
                            .getType() == BindInfo.BIND_BANK) {
                        intent.putExtra("page_position", 1);
                    }
                }
                intent.putExtra(WithdrawV2Activity.ARG_WITHDRAW_PARAM, withdrawParam);
                startActivity(intent);
            }
        }
    }


    @Override
    public void onUnbind(final CardBalance item, int position) {
        BindInfo bindInfo = item.getBindInfo();
        if (bindInfo != null) {
            if (bindInfo.getType() == BindInfo.BIND_BANK) {
                DialogUtil.createDoubleButtonDialog(this,
                        "是否确认更换银行卡？",
                        "确认",
                        "取消",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                unBind(item.getCard());
                            }
                        },
                        null)
                        .show();
            } else {
                DialogUtil.createDoubleButtonDialog(this,
                        "是否确认更换微信钱包？",
                        "确认",
                        "取消",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                unBind(item.getCard());
                            }
                        },
                        null)
                        .show();
            }
        }
    }

    /**
     * 解绑
     *
     * @param card
     */
    private void unBind(final Card card) {
        if (unbindSubscriber == null || unbindSubscriber.isUnsubscribed()) {
            unbindSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener() {
                        @Override
                        public void onNext(Object o) {
                            initLoad();
                            Intent intent = new Intent(WithdrawCardListActivity.this,
                                    BindBankSettingActivity.class);
                            intent.putExtra("card", card);
                            intent.putExtra("can_modify_name", false);
                            startActivity(intent);
                        }
                    })
                    .setDataNullable(true)
                    .setProgressBar(progressBar)
                    .build();
            CustomerCardApi.unbindObb(card.getId())
                    .subscribe(unbindSubscriber);
        }
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
                                case CLOSE_WITHDRAW_CARD_LIST:
                                case WITHDRAW_CASH_SUCCESS:
                                    finish();
                                    break;
                                case BIND_BANK_SUCCESS:
                                case BIND_WX_SUCCESS:
                                case UNBIND_BANK_SUCCESS:
                                case UNBIND_WX_SUCCESS:
                                    initLoad();
                                    break;
                            }
                        }
                    });
        }
    }
}
