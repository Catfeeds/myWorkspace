package com.hunliji.hljcardcustomerlibrary.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.suncloud.hljweblibrary.HljWeb;
import com.hunliji.hljcardcustomerlibrary.R;
import com.hunliji.hljcardcustomerlibrary.R2;
import com.hunliji.hljcardcustomerlibrary.api.CustomerCardApi;
import com.hunliji.hljcardcustomerlibrary.models.BankRollInResult;
import com.hunliji.hljcardcustomerlibrary.models.CardBalance;
import com.hunliji.hljcardlibrary.HljCard;
import com.hunliji.hljcommonlibrary.models.BindInfo;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.NoUnderlineSpan;
import com.hunliji.hljhttplibrary.entities.HljHttpCardData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.makeramen.rounded.RoundedImageView;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by mo_yu on 2017/11/24.银行转入理财
 */
public class BankRollInFundActivity extends HljBaseActivity {

    public static final String ARG_BIND_INFO = "bind_info";
    @BindView(R2.id.img_bank_logo)
    RoundedImageView imgBankLogo;
    @BindView(R2.id.tv_bank_name)
    TextView tvBankName;
    @BindView(R2.id.et_bank_amount)
    EditText etBankAmount;
    @BindView(R2.id.action_roll_in_cash_confirm)
    TextView actionRollInCashConfirm;
    @BindView(R2.id.cb_agreement)
    CheckBox cbAgreement;
    @BindView(R2.id.tv_agreement)
    TextView tvAgreement;
    @BindView(R2.id.bank_roll_in_view)
    LinearLayout bankRollInView;
    @BindView(R2.id.tv_card_cash_amount)
    TextView tvCardCashAmount;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;

    private double rollInMax = 20000;//每日每次最大金额
    private double rollInMin = 5;
    private double rollInCash;//提现金额
    private double giftCashMoney;//请帖中的礼金礼物余额
    private BindInfo bindInfo;
    private String message;//收益提示信息

    private Subscription rxBusEventSub;
    private HljHttpSubscriber rollInSubscriber;
    private HljHttpSubscriber initSubscriber;
    private HljHttpSubscriber cardSubscriber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_roll_in_fund);
        ButterKnife.bind(this);
        initValue();
        initView();
        if (bindInfo == null) {
            getBindBankInfo();
        } else {
            refreshBankInfo(bindInfo);
        }
        getCardList();
        registerRxBusEvent();
    }

    private void initValue() {
        bindInfo = getIntent().getParcelableExtra(ARG_BIND_INFO);
        rollInMax = HljCard.getFundIncomeMax();
        rollInMin = HljCard.getFundIncomeMin();
    }

    private void initView() {
        setOkButton(R.mipmap.icon_question_primary_44_44);
        etBankAmount.addTextChangedListener(textWatcher);
        String serviceAgreement = "同意礼金理财服务协议";
        int insuranceStart = serviceAgreement.indexOf("礼金理财服务协议");
        SpannableString sp = new SpannableString(serviceAgreement);
        sp.setSpan(new NoUnderlineSpan() {
            @Override
            public void onClick(View widget) {
                HljWeb.startWebView(BankRollInFundActivity.this, HljCard.fundProtocolUrl);
            }
        }, insuranceStart, insuranceStart + 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvAgreement.setText(sp);
        tvAgreement.setLinkTextColor(ContextCompat.getColor(this, R.color.colorLink));
        tvAgreement.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void refreshBankInfo(BindInfo bindInfo) {
        tvBankName.setText(getString(R.string.format_bind_info___card,
                bindInfo.getBankDesc(),
                bindInfo.getAccNo()));
        String imgUrl = ImagePath.buildPath(bindInfo.getBankLogo())
                .height(CommonUtil.dp2px(this, 28))
                .width(CommonUtil.dp2px(this, 28))
                .cropPath();
        Glide.with(this)
                .load(imgUrl)
                .apply(new RequestOptions().dontAnimate())
                .into(imgBankLogo);
    }

    private void refreshCashGiftView() {
        if (giftCashMoney == 0) {
            tvCardCashAmount.setVisibility(View.GONE);
        } else {
            tvCardCashAmount.setVisibility(View.VISIBLE);
            tvCardCashAmount.setText(getString(R.string.format_card_cash_amount,
                    CommonUtil.formatDouble2String(giftCashMoney)));
        }
    }

    @Override
    public void onOkButtonClick() {
        super.onOkButtonClick();
        HljWeb.startWebView(this, HljCard.fundQaUrl);
    }

    private void getBindBankInfo() {
        if (initSubscriber == null || initSubscriber.isUnsubscribed()) {
            initSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<BindInfo>() {
                        @Override
                        public void onNext(BindInfo bindInfo) {
                            refreshBankInfo(bindInfo);
                        }
                    })
                    .setProgressBar(progressBar)
                    .build();
            CustomerCardApi.getMyFundBankInfoObb()
                    .subscribe(initSubscriber);
        }
    }

    /**
     * 可转入的礼金列表
     */
    private void getCardList() {
        if (cardSubscriber == null || cardSubscriber.isUnsubscribed()) {
            cardSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpCardData<List<CardBalance>>>() {

                        @Override
                        public void onNext(HljHttpCardData<List<CardBalance>> listHljHttpCardData) {
                            giftCashMoney = 0;
                            List<CardBalance> cardBalances = listHljHttpCardData.getData();
                            if (!CommonUtil.isCollectionEmpty(cardBalances)) {
                                for (CardBalance cardBalance : cardBalances) {
                                    giftCashMoney += cardBalance.getBalance();
                                }
                            }
                            refreshCashGiftView();
                        }
                    })
                    .build();
            CustomerCardApi.getFundCardListObb()
                    .subscribe(cardSubscriber);
        }
    }

    private TextWatcher textWatcher = new TextWatcher() {

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String text = s.toString();
            if (text.contains(".")) {
                int index = text.indexOf(".");
                if (index + 3 < text.length()) {
                    text = text.substring(0, index + 3);
                    etBankAmount.setText(text);
                    etBankAmount.setSelection(text.length());
                }
            }
        }

        public void afterTextChanged(Editable s) {}
    };

    @OnClick(R2.id.action_roll_in_cash_confirm)
    public void rollInConfirm() {
        if (TextUtils.isEmpty(etBankAmount.getText())) {
            ToastUtil.showToast(this, "请输入正确的金额", 0);
            return;
        } else {
            if (!cbAgreement.isChecked()) {
                ToastUtil.showToast(this, "请勾选同意协议", 0);
                return;
            }
            try {
                rollInCash = Double.valueOf(etBankAmount.getText()
                        .toString());
            } catch (NumberFormatException ignored) {
            }
            if (rollInCash < rollInMin) {
                ToastUtil.showToast(this, "最少转入" + CommonUtil.formatDouble2String(rollInMin) + "元", 0);
                return;
            } else if (rollInCash > rollInMax) {
                ToastUtil.showToast(this, "单次转入最多" + CommonUtil.formatDouble2String(rollInMax) + "元", 0);
                return;
            }
        }
        if (rollInSubscriber == null || rollInSubscriber.isUnsubscribed()) {
            rollInSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setProgressDialog(DialogUtil.createProgressDialog(this))
                    .setOnNextListener(new SubscriberOnNextListener<BankRollInResult>() {
                        @Override
                        public void onNext(BankRollInResult result) {
                            onLLpay(result);
                        }
                    })
                    .build();
            HashMap<String, Object> map = new HashMap<>();
            map.put("input_money", String.valueOf(rollInCash));
            CustomerCardApi.bankRollInObb(map)
                    .subscribe(rollInSubscriber);
        }
    }

    @OnClick(R2.id.tv_card_cash_amount)
    public void cardCashRollInFund() {
        Intent intent = new Intent(this, CardRollInFundActivity.class);
        intent.putExtra(CardRollInFundActivity.ARG_IS_FROM_BANK_ROLL_IN,true);
        startActivity(intent);
    }

    /**
     * 调用连连sdk
     */
    private void onLLpay(BankRollInResult result) {
        message = result.getMessage();
//        new LLPaySecurePayer().pay(result.getPayParams(),
//                mHandler,
//                HljPay.PayResultCode.LL_PAY,
//                this,
//                false);
    }

//    //连连支付回调
//    private Handler mHandler = new Handler(new Handler.Callback() {
//        @Override
//        public boolean handleMessage(Message msg) {
//            switch (msg.what) {
//                case HljPay.PayResultCode.LL_PAY:
//                    try {
//                        String retStr = (String) msg.obj;
//                        JSONObject contentObj = new JSONObject(retStr);
//                        String retCode = contentObj.optString("ret_code");
//                        String retMsg = contentObj.optString("ret_msg");
//                        String resultPay = contentObj.optString("result_pay");
//                        if ("0000".equals(retCode) && "SUCCESS".equals(resultPay)) {
//                            ToastUtil.showToast(BankRollInFundActivity.this,
//                                    retMsg,
//                                    com.hunliji.hljpaymentlibrary.R.string.msg_pay_success___pay);
//                            RxBus.getDefault()
//                                    .post(new RxEvent(RxEvent.RxEventType
//                                            .ROLL_IN_OR_OUT_FUND_SUCCESS,
//                                            false));
//                            Intent intent = new Intent(BankRollInFundActivity.this,
//                                    AfterRollInOutActivity.class);
//                            intent.putExtra(AfterRollInOutActivity.ARG_TITLE,
//                                    getString(R.string.title_activity_after_roll_in));
//                            intent.putExtra(AfterRollInOutActivity.ARG_MSG, message);
//                            intent.putExtra(AfterRollInOutActivity.ARG_AMOUNT, rollInCash);
//                            startActivity(intent);
//                            overridePendingTransition(R.anim.activity_anim_default,
//                                    R.anim.slide_in_up);
//                            finish();
//                        } else {
//                            // 返回去显示错误信息
//                            ToastUtil.showToast(BankRollInFundActivity.this,
//                                    retMsg,
//                                    com.hunliji.hljpaymentlibrary.R.string.msg_pay_fail___pay);
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    break;
//                default:
//                    break;
//            }
//            return false;
//        }
//    });

    //注册RxEventBus监听
    private void registerRxBusEvent() {
        if (rxBusEventSub == null || rxBusEventSub.isUnsubscribed()) {
            rxBusEventSub = RxBus.getDefault()
                    .toObservable(RxEvent.class)
                    .subscribe(new RxBusSubscriber<RxEvent>() {
                        @Override
                        protected void onEvent(RxEvent rxEvent) {
                            switch (rxEvent.getType()) {
                                case ROLL_IN_OR_OUT_FUND_SUCCESS:
                                    boolean isCardRollIn = (boolean) rxEvent.getObject();
                                    if (isCardRollIn) {
                                        finish();
                                    }
                                    break;
                            }
                        }
                    });
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(rollInSubscriber, initSubscriber, cardSubscriber, rxBusEventSub);
    }
}
