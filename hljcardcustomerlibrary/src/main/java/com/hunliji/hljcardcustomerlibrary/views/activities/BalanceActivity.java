package com.hunliji.hljcardcustomerlibrary.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.example.suncloud.hljweblibrary.HljWeb;
import com.hunliji.hljcardcustomerlibrary.R;
import com.hunliji.hljcardcustomerlibrary.R2;
import com.hunliji.hljcardcustomerlibrary.adapter.BalanceRecyclerAdapter;
import com.hunliji.hljcardcustomerlibrary.api.CustomerCardApi;
import com.hunliji.hljcardcustomerlibrary.models.Balance;
import com.hunliji.hljcardcustomerlibrary.models.CardBalance;
import com.hunliji.hljcardcustomerlibrary.models.WithdrawParam;
import com.hunliji.hljcardcustomerlibrary.views.fragments.CheckUserFragment;
import com.hunliji.hljcardlibrary.HljCard;
import com.hunliji.hljcardlibrary.api.CardApi;
import com.hunliji.hljcardlibrary.models.Card;
import com.hunliji.hljcardlibrary.models.CardSetupStatus;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.BindInfo;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.NoUnderlineSpan;
import com.hunliji.hljhttplibrary.entities.HljHttpCardData;
import com.hunliji.hljhttplibrary.entities.HljHttpCountData;
import com.hunliji.hljhttplibrary.entities.HljHttpResultZip;
import com.hunliji.hljhttplibrary.entities.HljRZField;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;
import rx.functions.Func3;
import rx.schedulers.Schedulers;

/**
 * Created by mo_yu on 2017/2/7.余额
 */
public class BalanceActivity extends HljBaseActivity implements BalanceRecyclerAdapter
        .OnCheckWithdrawSettingListener {

    private final static long WITHDRAW_TIME = 24 * 60 * 60 * 1000;//提现24小时限制
    private final static long WITHDRAW_TIME_DEBUG = 5 * 60 * 1000;//测试，提现时间改为5分钟
    @BindView(R2.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R2.id.balance_list_view)
    LinearLayout balanceListView;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R2.id.tv_total)
    TextView tvTotal;
    @BindView(R2.id.tv_total_price)
    TextView tvTotalPrice;
    @BindView(R2.id.tv_buy_better)
    TextView tvBuyBetter;
    @BindView(R2.id.tv_balance_tip)
    TextView tvBalanceTip;
    @BindView(R2.id.balance_view)
    LinearLayout balanceView;
    private ArrayList<Balance> balances;//余额明细列表
    private double balance;//余额
    private long lastWithdrawTime;
    private double withdrawMax = 20000;
    private double withdrawMin = 2;
    private double withdrawRate = 0.006;
    private boolean hasOld; // 是否有旧版请帖
    private BalanceRecyclerAdapter adapter;

    private HljHttpSubscriber initSubscriber;
    private HljHttpSubscriber paramSubscriber;
    private Subscription rxBusEventSub;
    private HljHttpSubscriber checkSub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);
        ButterKnife.bind(this);
        initValue();
        initView();
        refreshWithdrawParam();
        initLoad();
        initWithdrawParam();
        registerRxBusEvent();
    }

    private void initValue() {
        balances = new ArrayList<>();
    }

    private void initView() {
        setOkText(R.string.label_withdraw_cash);
        setOkTextColor(ContextCompat.getColor(this, R.color.transparent_white5));
        if (HljCard.isCustomer(this)) {
            tvBuyBetter.setVisibility(View.VISIBLE);
        } else {
            tvBuyBetter.setVisibility(View.GONE);
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        adapter = new BalanceRecyclerAdapter(this, balances);
        final View footerView = View.inflate(this, R.layout.user_balance_footer_view, null);
        View moreView = footerView.findViewById(R.id.more_view);
        moreView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.setShowMore(true);
                footerView.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
            }
        });
        adapter.setFooterView(footerView);
        adapter.setOnCheckWithdrawSettingListener(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void initLoad() {
        if (initSubscriber == null || initSubscriber.isUnsubscribed()) {
            initSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setProgressBar(progressBar)
                    .setOnNextListener(new SubscriberOnNextListener<InitResultZip>() {
                        @Override
                        public void onNext(InitResultZip initResultZip) {
                            if (initResultZip.countData.getLastWithdrawAt() != null &&
                                    initResultZip.countData.getLastWithdrawAt()
                                    .getMillis() != 0) {
                                lastWithdrawTime = initResultZip.countData.getLastWithdrawAt()
                                        .getMillis();
                            }
                            balance = initResultZip.countData.getBalance();
                            setBalanceView();
                            if (initResultZip.countData.getData() == null || initResultZip
                                    .countData.getData()
                                    .size() == 0) {
                                balanceListView.setVisibility(View.GONE);
                            } else {
                                balanceListView.setVisibility(View.VISIBLE);
                                balances.clear();
                                balances.addAll(initResultZip.countData.getData());
                                adapter.notifyDataSetChanged();
                            }

                            // 检查是否有旧版的可提现请帖
                            for (CardBalance item : initResultZip.cardBalanceList.getData()) {
                                if (item.getVersion() == CardBalance.OLD_CARD && item.getBalance
                                        () > 0) {
                                    hasOld = true;
                                    break;
                                }
                            }
                        }
                    })
                    .build();

            Observable bObb = CustomerCardApi.getBalanceObb();
            Observable cObb = CustomerCardApi.getAllCardBalancesObb();
            Observable zipObb = Observable.zip(bObb,
                    cObb,
                    new Func2<HljHttpCountData<List<Balance>>,
                            HljHttpCardData<List<CardBalance>>, InitResultZip>() {
                        @Override
                        public InitResultZip call(
                                HljHttpCountData<List<Balance>> listHljHttpCountData,
                                HljHttpCardData<List<CardBalance>> cardListData) {
                            return new InitResultZip(listHljHttpCountData, cardListData);
                        }
                    });
            zipObb.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(initSubscriber);
        }
    }

    public void initWithdrawParam() {
        if (paramSubscriber == null || paramSubscriber.isUnsubscribed()) {
            paramSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<WithdrawParam>() {
                        @Override
                        public void onNext(WithdrawParam withdrawParam) {
                            withdrawMax = withdrawParam.getWithdrawMax();
                            withdrawMin = withdrawParam.getWithdrawMin();
                            withdrawRate = withdrawParam.getWithdrawRate();
                            refreshWithdrawParam();
                        }
                    })
                    .build();
            CustomerCardApi.getWithdrawParam()
                    .subscribe(paramSubscriber);
        }
    }

    private void refreshWithdrawParam() {
        String str = getString(R.string.label_balance_tip2, withdrawMin, withdrawRate * 100);
        int insuranceStart = str.indexOf("相关说明");
        SpannableString sp = new SpannableString(str);
        sp.setSpan(new NoUnderlineSpan() {
            @Override
            public void onClick(View widget) {
                HljWeb.startWebView(BalanceActivity.this, HljCommon.WX_EDU_URL);
            }
        }, insuranceStart, insuranceStart + 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvBalanceTip.setText(sp);
        tvBalanceTip.setLinkTextColor(ContextCompat.getColor(BalanceActivity
                .this, R.color.colorLink));
        tvBalanceTip.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void setBalanceView() {
        DecimalFormat df = new DecimalFormat("#####0.00");
        String balanceStr = df.format(balance);
        if (balanceStr.length() > 2) {
            Spannable span = new SpannableString(balanceStr);
            span.setSpan(new AbsoluteSizeSpan(CommonUtil.dp2px(BalanceActivity.this, 18)),
                    span.length() - 2,
                    span.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvTotalPrice.setText(span);
        }

        //提现按钮状态
        long curTime = HljTimeUtils.getServerCurrentTimeMillis();
        if (balance < 2 || (curTime - lastWithdrawTime) < (HljCommon.debug ? WITHDRAW_TIME_DEBUG
                : WITHDRAW_TIME)) {
            setOkTextColor(ContextCompat.getColor(this, R.color.transparent_white5));
        } else {
            setOkTextColor(ContextCompat.getColor(this, R.color.colorWhite));
        }
    }

    @Override
    public void onOkButtonClick() {
        super.onOkButtonClick();
        if (initSubscriber != null && !initSubscriber.isUnsubscribed()) {
            return;
        }
        long curTime = HljTimeUtils.getServerCurrentTimeMillis();
        //提现24小时限制
        if ((curTime - lastWithdrawTime) >= (HljCommon.debug ? WITHDRAW_TIME_DEBUG :
                WITHDRAW_TIME)) {
            if (balance < 2) {
                DialogUtil.createSingleButtonDialog(BalanceActivity.this,
                        getString(R.string.label_withdraw_cash_check_tip2),
                        getString(R.string.label_confirm2___cm),
                        null)
                        .show();
            } else if (hasOld) {
                // 有旧版请帖需要验证手机号
                showCheckUserDialog();
            } else {
                // 否则直接可以提现
                Intent intent = new Intent();
                intent.setClass(this, WithdrawCardListActivity.class);
                startActivity(intent);
            }
        } else {
            DialogUtil.createSingleButtonDialog(BalanceActivity.this,
                    getString(R.string.label_withdraw_cash_check_tip),
                    getString(R.string.label_confirm2___cm),
                    null)
                    .show();
        }
    }

    public void showCheckUserDialog() {
        CheckUserFragment checkUserFragment = CheckUserFragment.newInstance();
        checkUserFragment.show(getSupportFragmentManager(), "CheckUserFragment");
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
                                    initLoad();
                                    break;
                            }
                        }
                    });
        }
    }

    @OnClick(R2.id.tv_buy_better)
    public void onBuyBetter() {
        ARouter.getInstance()
                .build(RouterPath.IntentPath.Customer.MAIN)
                .withString("action", "product")
                .navigation();
    }

    private void goSetting(CheckResultZip zip) {
        Intent intent = new Intent();
        if (zip.bindInfo == null) {
            //未绑定过提现方式，优先跳银行卡提现设置页
            intent.setClass(this, BindBankSettingActivity.class);
        } else if (zip.bindInfo.getType() == BindInfo.BIND_BANK) {
            //已绑定过银行卡提现
            intent.setClass(this, BindBankSettingActivity.class);
            intent.putExtra("bind_info", zip.bindInfo);
        } else {
            //已绑定过微信提现
            intent.setClass(this, BindWXSettingActivity.class);
            intent.putExtra("bind_info", zip.bindInfo);
        }
        intent.putExtra("card", zip.cardInfo);
        intent.putExtra("can_modify_name",
                zip.cardSetupStatus != null && zip.cardSetupStatus.isCanModifyName());

        startActivity(intent);
    }

    @Override
    public void onWithdrawSettingCheck(long cardId) {
        checkSub = HljHttpSubscriber.buildSubscriber(this)
                .setProgressDialog(DialogUtil.createProgressDialog(this))
                .setOnNextListener(new SubscriberOnNextListener<CheckResultZip>() {
                    @Override
                    public void onNext(CheckResultZip resultZip) {
                        goSetting(resultZip);
                    }
                })
                .build();
        Observable bObservable = CustomerCardApi.getBindInfo(cardId);
        Observable cObservable = CardApi.getCardSetupStatusObb(cardId);
        Observable iObservable = CardApi.getCard(cardId);

        Observable zipObservable = Observable.zip(bObservable,
                cObservable,
                iObservable,
                new Func3<BindInfo, CardSetupStatus, Card, CheckResultZip>() {
                    @Override
                    public CheckResultZip call(BindInfo o, CardSetupStatus o2, Card o3) {
                        CheckResultZip zip = new CheckResultZip();
                        zip.bindInfo = o;
                        zip.cardSetupStatus = o2;
                        zip.cardInfo = o3;
                        return zip;
                    }
                });

        zipObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(checkSub);
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(rxBusEventSub, initSubscriber, paramSubscriber, checkSub);
    }

    private class CheckResultZip extends HljHttpResultZip {
        @HljRZField
        BindInfo bindInfo;
        @HljRZField
        CardSetupStatus cardSetupStatus;
        @HljRZField
        Card cardInfo;
    }

    private class InitResultZip extends HljHttpResultZip {
        @HljRZField
        HljHttpCountData<List<Balance>> countData;
        @HljRZField
        HljHttpCardData<List<CardBalance>> cardBalanceList;

        public InitResultZip(
                HljHttpCountData<List<Balance>> countData,
                HljHttpCardData<List<CardBalance>> cardListData) {
            this.countData = countData;
            this.cardBalanceList = cardListData;
        }
    }


}
