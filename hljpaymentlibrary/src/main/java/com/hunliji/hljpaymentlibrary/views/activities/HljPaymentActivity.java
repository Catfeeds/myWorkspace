package com.hunliji.hljpaymentlibrary.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.models.BankCard;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.NumberFormatUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljpaymentlibrary.HljPay;
import com.hunliji.hljpaymentlibrary.PayAgent;
import com.hunliji.hljpaymentlibrary.R;
import com.hunliji.hljpaymentlibrary.R2;
import com.hunliji.hljpaymentlibrary.adapters.PaymentsAdapter;
import com.hunliji.hljpaymentlibrary.api.PaymentApi;
import com.hunliji.hljpaymentlibrary.api.xiaoxi_installment.XiaoxiInstallmentApi;
import com.hunliji.hljpaymentlibrary.models.LLPayer;
import com.hunliji.hljpaymentlibrary.models.LLPayment;
import com.hunliji.hljpaymentlibrary.models.PayRxEvent;
import com.hunliji.hljpaymentlibrary.models.Payment;
import com.hunliji.hljpaymentlibrary.models.WalletPayment;
import com.hunliji.hljpaymentlibrary.models.XiaoxiPayment;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.InstallmentData;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.InstallmentDetail;
import com.hunliji.hljpaymentlibrary.utils.xiaoxi_installment.XiaoxiInstallmentAuthorization;
import com.hunliji.hljpaymentlibrary.views.activities.xiaoxi_installment.InstallmentPaymentActivity;
import com.hunliji.hljpaymentlibrary.views.activities.xiaoxi_installment.MyBillListActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;
import rx.functions.Func2;


/**
 * Created by Suncloud on 2016/7/22.
 */
public class HljPaymentActivity extends HljBaseActivity implements PaymentsAdapter
        .PaymentActionListener {

    @BindView(R2.id.payment_recycler_view)
    RecyclerView recyclerView;
    @BindView(R2.id.progress_bar)
    View progressBar;
    @BindView(R2.id.tv_price1)
    TextView tvPrice1;
    @BindView(R2.id.tv_price2)
    TextView tvPrice2;
    @BindView(R2.id.bottom_layout)
    RelativeLayout bottomLayout;

    private String payParams;
    private String payPath;
    private HljPay HLJPay;
    private double price;
    private Subscription paySubscription;
    private Subscription infoSubscription;
    private RxBusSubscriber<PayRxEvent> xiaoxiAuthSub;
    private boolean llpaySimple; //true 简单模式，不绑定银行卡无密码
    private PaymentsAdapter adapter;
    public static final double XIAO_XI_INSTALLMENT_PAY_MIN = 1000;
    private HljHttpSubscriber installmentSub;
    private double installmentMoneyStartAt;
    private ArrayList<String> payAgents;
    private boolean failToFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment___pay);
        ButterKnife.bind(this);
        setSwipeBackEnable(false);
        payAgents = getIntent().getStringArrayListExtra("payAgents");
        if (payAgents == null || payAgents.isEmpty()) {
            return;
        }

        initData();
        initView();
        initLoad();

        paySubscription = RxBus.getDefault()
                .toObservable(PayRxEvent.class)
                .subscribe(new RxBusSubscriber<PayRxEvent>() {
                    @Override
                    protected void onEvent(PayRxEvent payRxEvent) {
                        switch (payRxEvent.getType()) {
                            case PAY_FAIL:
                                if (failToFinish) {
                                    HljPaymentActivity.super.onBackPressed();
                                }
                                break;
                            case PAY_SUCCESS:
                                HljPaymentActivity.super.onBackPressed();
                                break;
                        }
                    }
                });
    }

    private void initData() {
        payParams = getIntent().getStringExtra("pay_params");
        payPath = getIntent().getStringExtra("pay_path");
        price = getIntent().getDoubleExtra("price", 0);
        llpaySimple = getIntent().getBooleanExtra("llpaySimple", false);
        if (price < XIAO_XI_INSTALLMENT_PAY_MIN) {
            // 金额小于1000，不显示分期选项
            payAgents.remove(PayAgent.XIAO_XI_PAY);
        }
        failToFinish = getIntent().getBooleanExtra("fail_to_finish", false);
    }


    private void initView() {
        adapter = new PaymentsAdapter();
        adapter.setHeaderView(View.inflate(this, R.layout.pay_agent_header___pay, null));
        adapter.setPaymentActionListener(this);
        try {
            ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations
                    (false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(HljPaymentActivity.this));
        recyclerView.setAdapter(adapter);

        String priceStr = NumberFormatUtil.formatDouble2StringWithTwoFloat(price);
        tvPrice1.setText(priceStr.split("\\.")[0]);
        tvPrice2.setText("." + priceStr.split("\\.")[1]);
    }

    private void initLoad() {
        if (payAgents.contains(PayAgent.XIAO_XI_PAY)) {
            // 分期支付需要显示分期最小金额
            installmentSub = HljHttpSubscriber.buildSubscriber(this)
                    .setProgressBar(progressBar)
                    .setDataNullable(true)
                    .setOnNextListener(new SubscriberOnNextListener<InstallmentData>() {
                        @Override
                        public void onNext(InstallmentData installmentData) {
                            int selectedStageNum = 0;
                            // 取最小分期金额
                            for (InstallmentDetail detail : installmentData.getInstallment()
                                    .getDetails()) {
                                if (detail.getStageNum() > selectedStageNum) {
                                    selectedStageNum = detail.getStageNum();
                                    installmentMoneyStartAt = detail.getEachPay();
                                }
                            }
                            initPaymentInfo();
                        }
                    })
                    .build();
            XiaoxiInstallmentApi.getInstallmentInfo(price)
                    .subscribe(installmentSub);
        } else {
            initPaymentInfo();
        }
    }

    /**
     * 配置支付方式列表
     */
    @SuppressWarnings("unchecked")
    private void initPaymentInfo() {
        //支付方式排序列表
        final List<String> payAgentsSort = new ArrayList<>();
        payAgentsSort.add(PayAgent.WALLET_PAY);
        payAgentsSort.add(PayAgent.ALI_PAY);
        payAgentsSort.add(PayAgent.WEIXIN_PAY);
        payAgentsSort.add(PayAgent.LL_PAY);
        payAgentsSort.add(PayAgent.CMB_PAY);
        payAgentsSort.add(PayAgent.UNION_PAY);
        payAgentsSort.add(PayAgent.XIAO_XI_PAY);


        infoSubscription = Observable.from(payAgents)
                .map(new Func1<String, Payment>() {
                    @Override
                    public Payment call(String payAgent) {
                        Payment payment;
                        switch (payAgent) {
                            case PayAgent.LL_PAY:
                                payment = new LLPayment(payAgent);
                                break;
                            case PayAgent.WALLET_PAY:
                                payment = new WalletPayment(payAgent);
                                ((WalletPayment) payment).setPrice(price);
                                break;
                            case PayAgent.XIAO_XI_PAY:
                                payment = new XiaoxiPayment(payAgent);
                                ((XiaoxiPayment) payment).setInstallmentMoneyStartAt(
                                        installmentMoneyStartAt);
                                break;
                            default:
                                payment = new Payment(payAgent);
                                break;
                        }
                        return payment;
                    }
                })
                .concatMap(new Func1<Payment, Observable<Payment>>() {
                    @Override
                    public Observable<Payment> call(final Payment payment) {
                        //筛选出需要前置请求的支付方式
                        switch (payment.getPayAgent()) {
                            case PayAgent.LL_PAY:
                                if (llpaySimple) {
                                    //简单模式，不绑定银行卡无密码
                                    break;
                                }
                                //连连支付获取银行卡列表
                                return PaymentApi.getBankCards()
                                        .map(new Func1<List<BankCard>, Payment>() {
                                            @Override
                                            public Payment call(List<BankCard> cards) {
                                                if (payment instanceof LLPayment) {
                                                    ((LLPayment) payment).setCards(cards);
                                                }
                                                return payment;
                                            }
                                        });
                            case PayAgent.WALLET_PAY:
                                //余额支付获取账号余额，用户段和商家段不同请求
                                return PaymentApi.getWallet(HljPaymentActivity.this)
                                        .map(new Func1<Double, Payment>() {
                                            @Override
                                            public Payment call(Double walletPrice) {
                                                if (payment instanceof WalletPayment) {
                                                    ((WalletPayment) payment).setWalletPrice(
                                                            walletPrice);
                                                }
                                                return payment;
                                            }
                                        });
                        }
                        return Observable.just(payment);
                    }
                })
                .filter(new Func1<Payment, Boolean>() {
                    @Override
                    public Boolean call(Payment payment) {
                        return !(payment instanceof WalletPayment && ((WalletPayment) payment)
                                .getWalletPrice() <= 0);
                    }
                })
                .sorted(new Func2<Payment, Payment, Integer>() {
                    //对传入的支付方式按排序列表修改排序
                    @Override
                    public Integer call(Payment payment1, Payment payment2) {
                        if ((payment2 instanceof WalletPayment) && !((WalletPayment) payment2)
                                .isEnable()) {
                            //payment2 为余额支付且余额不足 插到列表后
                            return -1;
                        } else if ((payment1 instanceof WalletPayment) && !((WalletPayment)
                                payment1).isEnable()) {
                            //payment1 为余额支付且余额不足 插到列表后
                            return 1;
                        } else if (payAgentsSort.indexOf(payment2.getPayAgent()) < 0) {
                            //payment2  支付方式不在列表内插到列表后
                            return -1;
                        } else if (payAgentsSort.indexOf(payment1.getPayAgent()) < 0) {
                            //payment1  支付方式不在列表内插到列表后
                            return 1;
                        } else {
                            //对比在列表中的排序
                            return payAgentsSort.indexOf(payment1.getPayAgent()) - payAgentsSort
                                    .indexOf(
                                    payment2.getPayAgent());
                        }
                    }
                })
                .buffer(payAgents.size())
                .subscribe(HljHttpSubscriber.buildSubscriber(this)
                        .setProgressBar(progressBar)
                        .setOnNextListener(new SubscriberOnNextListener<List<Payment>>() {
                            @Override
                            public void onNext(List<Payment> payments) {
                                recyclerView.setVisibility(View.VISIBLE);
                                bottomLayout.setVisibility(View.VISIBLE);
                                for (Payment payment : payments) {
                                    if (!(payment instanceof WalletPayment) || ((WalletPayment)
                                            payment).isEnable()) {
                                        payment.setSelected(true);
                                        break;
                                    }
                                }
                                adapter.setPayments(payments);
                            }
                        })
                        .build());
    }

    @OnClick(R2.id.btn_go_pay)
    public void onGoPay() {
        if (TextUtils.isEmpty(payParams)) {
            return;
        }
        Payment payment = adapter.getSelectPayment();
        if (payment == null) {
            return;
        }
        switch (payment.getPayAgent()) {
            case PayAgent.WALLET_PAY:
                initPay().onPayWallet();
                break;
            case PayAgent.ALI_PAY:
                initPay().onPayAli();
                break;
            case PayAgent.UNION_PAY:
                initPay().onPayUnion();
                break;
            case PayAgent.WEIXIN_PAY:
                initPay().onPayWeixin();
                break;
            case PayAgent.LL_PAY:
                if (payment instanceof LLPayment) {
                    onLLpay(((LLPayment) payment).getCurrentCard(),
                            !((LLPayment) payment).getCards()
                                    .isEmpty());
                }
                break;
            case PayAgent.CMB_PAY:
                initPay().onPayCmb();
                break;
            case PayAgent.XIAO_XI_PAY:
                onXiaoxiInstallment();
                break;
        }
    }

    private HljPay initPay() {
        if (HLJPay == null) {
            HLJPay = new HljPay.Builder(this).params(payParams)
                    .path(payPath)
                    .price(price)
                    .build();
        }
        return HLJPay;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (HLJPay != null) {
            HLJPay.onPayActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void onLLpay(BankCard card, boolean needVerify) {
        LLPayer llPayer = new LLPayer(price, payPath, payParams, llpaySimple);
        Intent intent;
        if (card != null) {
            llPayer.setBindCardId(card.getId());
            intent = new Intent(this, VerificationPasswordActivity.class);
        } else if (needVerify) {
            intent = new Intent(this, VerificationPasswordActivity.class);
        } else {
            llPayer.setFirst(true);
            intent = new Intent(this, AddBankCardActivity.class);
        }
        intent.putExtra("llPayer", llPayer);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    /**
     * 小犀分期支付，首先授信请求，之后才跳转分期支付页面
     */
    private void onXiaoxiInstallment() {
        if (xiaoxiAuthSub == null) {
            xiaoxiAuthSub = new RxBusSubscriber<PayRxEvent>() {
                @Override
                protected void onEvent(PayRxEvent payRxEvent) {
                    Intent intent;
                    switch (payRxEvent.getType()) {
                        case AUTHORIZE_CANCEL:
                        case PAY_CANCEL:
                        case INIT_LIMIT_CLOSE:
                        case INCREASE_LIMIT_CLOSE:
                            // 授信取消
                            break;
                        case INSTALLMENT_PAY_SUCCESS:
                            // 分期支付成功
                            finish();
                            break;
                        case HAD_AUTHORIZED:
                        case LIMIT_CONTINUE_PAY:
                            // 授信并且获取额度成功，可以去支付
                            intent = new Intent(HljPaymentActivity.this,
                                    InstallmentPaymentActivity.class);
                            intent.putExtra(InstallmentPaymentActivity.ARG_PAY_PARAMS, payParams);
                            intent.putExtra(InstallmentPaymentActivity.ARG_PAY_PATH, payPath);
                            intent.putExtra(InstallmentPaymentActivity.ARG_PRICE, price);
                            startActivity(intent);
                            break;
                    }
                }
            };
        }
        RxBus.getDefault()
                .toObservable(PayRxEvent.class)
                .subscribe(xiaoxiAuthSub);
        XiaoxiInstallmentAuthorization.getInstance()
                .onAuthorization(this, true);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        overridePendingTransition(0, 0);
        finish();
    }

    @Override
    protected void onFinish() {
        CommonUtil.unSubscribeSubs(paySubscription, infoSubscription, xiaoxiAuthSub);
        super.onFinish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        RxBus.getDefault()
                .post(new PayRxEvent(PayRxEvent.RxEventType.PAY_CANCEL, null));
    }

    @Override
    public void llpayNewCard(boolean needVerify) {
        onLLpay(null, needVerify);
    }
}
