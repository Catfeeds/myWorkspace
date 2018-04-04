package com.hunliji.hljcardcustomerlibrary.views.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.suncloud.hljweblibrary.HljWeb;
import com.google.gson.JsonElement;
import com.hunliji.hljcardcustomerlibrary.R;
import com.hunliji.hljcardcustomerlibrary.R2;
import com.hunliji.hljcardcustomerlibrary.adapter.FundCardRecyclerAdapter;
import com.hunliji.hljcardcustomerlibrary.api.CustomerCardApi;
import com.hunliji.hljcardcustomerlibrary.models.CardBalance;
import com.hunliji.hljcardlibrary.HljCard;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljcommonlibrary.views.widgets.NoUnderlineSpan;
import com.hunliji.hljhttplibrary.entities.HljHttpCardData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mo_yu on 2017/11/24.礼金转入理财
 */
public class CardRollInFundActivity extends HljBaseActivity implements FundCardRecyclerAdapter
        .OnCheckClickListener {

    public static final String ARG_IS_FROM_BANK_ROLL_IN = "is_from_bank_roll_in";

    @BindView(R2.id.tv_card_roll_in_amount)
    TextView tvCardRollInAmount;
    @BindView(R2.id.tv_roll_in_rate)
    TextView tvRollInRate;
    @BindView(R2.id.tv_check_relevant_specification)
    TextView tvCheckRelevantSpecification;
    @BindView(R2.id.tv_roll_in)
    TextView tvRollIn;
    @BindView(R2.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R2.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;

    private boolean isFromBankRollIn;
    private double depositRate;
    private ArrayList<CardBalance> cardBalances;//可转入的礼金列表
    private FundCardRecyclerAdapter adapter;
    private ArrayList<Long> cardIds;
    private Dialog confirmDialog;
    private double totalAmount;

    private HljHttpSubscriber initSubscriber;
    private HljHttpSubscriber rollInSubscriber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_roll_in_fund);
        ButterKnife.bind(this);
        initValue();
        initView();
        initLoad();
    }

    private void initValue() {
        isFromBankRollIn = getIntent().getBooleanExtra(ARG_IS_FROM_BANK_ROLL_IN, false);
        cardBalances = new ArrayList<>();
        cardIds = new ArrayList<>();
        depositRate = 0.006;
    }

    private void initView() {
        tvRollInRate.setText(getString(R.string.format_deposit_rate_amount,
                CommonUtil.formatDouble2String(0)));
        adapter = new FundCardRecyclerAdapter(this, cardBalances);
        adapter.setOnCheckClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void initLoad() {
        if (initSubscriber == null || initSubscriber.isUnsubscribed()) {
            initSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpCardData<List<CardBalance>>>() {

                        @Override
                        public void onNext(HljHttpCardData<List<CardBalance>> listHljHttpCardData) {
                            cardBalances.clear();
                            if (listHljHttpCardData.getData() != null) {
                                cardBalances.addAll(listHljHttpCardData.getData());
                            }
                            depositRate = listHljHttpCardData.getDepositRate();
                            adapter.notifyDataSetChanged();
                            refreshRollInAmount();
                        }
                    })
                    .setProgressBar(progressBar)
                    .build();
            CustomerCardApi.getFundCardListObb()
                    .subscribe(initSubscriber);
        }
    }

    private void refreshRollInAmount() {
        totalAmount = 0;
        double rateAmount;
        cardIds.clear();
        for (CardBalance cardBalance : cardBalances) {
            if (cardBalance.isSelected()) {
                totalAmount += cardBalance.getBalance();
                cardIds.add(cardBalance.getCard()
                        .getId());
            }
        }
        rateAmount = totalAmount * depositRate;
        String rateAmountStr = CommonUtil.formatDouble2StringWithTwoFloat(rateAmount);
        totalAmount = totalAmount - Double.valueOf(rateAmountStr);
        if (totalAmount < 0) {
            totalAmount = 0;
        }
        tvRollInRate.setText(getString(R.string.format_deposit_rate_amount, rateAmountStr));
        tvCardRollInAmount.setText(CommonUtil.formatDouble2String(totalAmount));
        if (CommonUtil.isCollectionEmpty(cardIds)) {
            tvRollIn.setText("跳过");
        } else {
            tvRollIn.setText("转入");
        }
    }

    @OnClick(R2.id.tv_check_relevant_specification)
    public void onCheckRelevantSpecificationClicked() {
        HljWeb.startWebView(this, HljCommon.WX_EDU_URL);
    }

    @OnClick(R2.id.tv_roll_in)
    public void onRollInClicked() {
        if (CommonUtil.isCollectionEmpty(cardIds)) {
            if (isFromBankRollIn) {
                onBackPressed();
            } else {
                Intent intent = new Intent();
                intent.setClass(this, BankRollInFundActivity.class);
                startActivity(intent);
                finish();
            }
            return;
        }
        if (totalAmount <= 0) {
            ToastUtil.showToast(this, "可转入金额不能小于等于0", 0);
            return;
        }
        if (confirmDialog != null && confirmDialog.isShowing()) {
            return;
        }
        confirmDialog = new Dialog(this, R.style.BubbleDialogTheme);
        confirmDialog.setContentView(R.layout.dialog_check_card_roll_in);
        confirmDialog.setCancelable(true);
        TextView tvCardRollInAmount = confirmDialog.findViewById(R.id.tv_card_roll_in_amount);
        final CheckBox cbAgreement = confirmDialog.findViewById(R.id.cb_agreement);
        TextView tvAgreement = confirmDialog.findViewById(R.id.tv_agreement);
        final CheckBox cbAutoInto = confirmDialog.findViewById(R.id.cb_auto_into);
        confirmDialog.findViewById(R.id.action_roll_in_confirm)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!cbAgreement.isChecked()) {
                            ToastUtil.showToast(CardRollInFundActivity.this, "请勾选同意协议", 0);
                            return;
                        }
                        confirmDialog.dismiss();
                        cardRollInFund(cbAutoInto.isChecked());
                    }
                });
        tvCardRollInAmount.setText(CommonUtil.formatDouble2String(totalAmount));
        String serviceAgreement = "同意礼金理财服务协议";
        int insuranceStart = serviceAgreement.indexOf("礼金理财服务协议");
        SpannableString sp = new SpannableString(serviceAgreement);
        sp.setSpan(new NoUnderlineSpan() {
            @Override
            public void onClick(View widget) {
                HljWeb.startWebView(CardRollInFundActivity.this, HljCard.fundProtocolUrl);
            }
        }, insuranceStart, insuranceStart + 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvAgreement.setText(sp);
        tvAgreement.setLinkTextColor(ContextCompat.getColor(this, R.color.colorLink));
        tvAgreement.setMovementMethod(LinkMovementMethod.getInstance());
        Window win = confirmDialog.getWindow();
        if (win != null) {
            WindowManager.LayoutParams params = win.getAttributes();
            Point point = CommonUtil.getDeviceSize(this);
            params.width = point.x;
            win.setGravity(Gravity.BOTTOM);
            win.setWindowAnimations(R.style.dialog_anim_rise_style);
        }
        confirmDialog.show();
    }

    private void cardRollInFund(boolean isAutoIncome) {
        if (rollInSubscriber == null || rollInSubscriber.isUnsubscribed()) {
            rollInSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<JsonElement>() {
                        @Override
                        public void onNext(JsonElement jsonElement) {
                            double money = 0;
                            String message = null;
                            try {
                                money = jsonElement.getAsJsonObject()
                                        .get("deposit")
                                        .getAsDouble();
                                message = jsonElement.getAsJsonObject()
                                        .get("message")
                                        .getAsString();
                            } catch (Exception e) {
                            }
                            RxBus.getDefault()
                                    .post(new RxEvent(RxEvent.RxEventType
                                            .ROLL_IN_OR_OUT_FUND_SUCCESS,
                                            true));
                            Intent intent = new Intent(CardRollInFundActivity.this,
                                    AfterRollInOutActivity.class);
                            intent.putExtra(AfterRollInOutActivity.ARG_TITLE,
                                    getString(R.string.title_activity_after_roll_in));
                            intent.putExtra(AfterRollInOutActivity.ARG_AMOUNT, money);
                            intent.putExtra(AfterRollInOutActivity.ARG_MSG, message);
                            startActivity(intent);
                            overridePendingTransition(R.anim.activity_anim_default,
                                    R.anim.slide_in_up);
                            finish();
                        }
                    })
                    .setProgressBar(progressBar)
                    .build();
            CustomerCardApi.postCardRollInFundObb(getCardIdsStr(), isAutoIncome)
                    .subscribe(rollInSubscriber);
        }
    }

    /**
     * 讲选中的请帖id拼接成字符串
     */
    private String getCardIdsStr() {
        StringBuilder cardIdsStr = new StringBuilder();
        for (int i = 0; i < cardIds.size(); i++) {
            cardIdsStr.append(cardIds.get(i));
            if (i != cardIds.size() - 1) {
                cardIdsStr.append(",");
            }
        }
        return cardIdsStr.toString();
    }

    @Override
    public void onCheck(CardBalance item, int position) {
        refreshRollInAmount();
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(initSubscriber, rollInSubscriber);
    }
}
