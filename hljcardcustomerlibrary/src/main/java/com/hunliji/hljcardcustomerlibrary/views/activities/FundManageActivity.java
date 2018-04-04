package com.hunliji.hljcardcustomerlibrary.views.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.suncloud.hljweblibrary.HljWeb;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.hunliji.hljcardcustomerlibrary.R;
import com.hunliji.hljcardcustomerlibrary.R2;
import com.hunliji.hljcardcustomerlibrary.api.CustomerCardApi;
import com.hunliji.hljcardcustomerlibrary.models.FundIndex;
import com.hunliji.hljcardlibrary.HljCard;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.BindInfo;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljhttplibrary.entities.HljHttpResultZip;
import com.hunliji.hljhttplibrary.entities.HljRZField;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImagePath;

import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.functions.Func2;

/**
 * Created by mo_yu on 2017/11/23.礼金理财
 */

public class FundManageActivity extends HljBaseNoBarActivity implements PullToRefreshScrollView
        .OnRefreshListener {

    @BindView(R2.id.scroll_view)
    PullToRefreshScrollView scrollView;
    @BindView(R2.id.title)
    TextView title;
    @BindView(R2.id.btn_more)
    ImageButton btnMore;
    @BindView(R2.id.action_layout)
    LinearLayout actionLayout;
    @BindView(R2.id.tv_total_fund)
    TextView tvTotalFund;
    @BindView(R2.id.tv_fund_rate_total)
    TextView tvFundRateTotal;
    @BindView(R2.id.tv_fund_rate_add)
    TextView tvFundRateAdd;
    @BindView(R2.id.tv_earnings)
    TextView tvEarnings;
    @BindView(R2.id.tv_roll_in_fund)
    TextView tvRollInFund;
    @BindView(R2.id.tv_roll_out_fund)
    TextView tvRollOutFund;
    @BindView(R2.id.fund_view)
    LinearLayout fundView;
    @BindView(R2.id.tv_card_cash)
    TextView tvCardCash;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R2.id.img_fund_rate_bonus)
    ImageView imgFundRateBonus;

    private Dialog moreDialog;
    private FundIndex mFundIndex;
    private BindInfo bindInfo;
    private Dialog fundDialog;
    private TextView tvCashGiftMoney;
    private int fundRateBonusWidth;

    private Subscription rxBusEventSub;
    private HljHttpSubscriber refreshSubscriber;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fund_manage);
        ButterKnife.bind(this);
        initValue();
        initView();
        initLoad();
        registerRxBusEvent();
    }

    private void initValue() {
        sharedPreferences = getSharedPreferences(HljCommon.FileNames.PREF_FILE,
                Context.MODE_PRIVATE);
        fundRateBonusWidth = CommonUtil.dp2px(this, 90);
    }

    private void initView() {
        setActionBarPadding(actionLayout);
        scrollView.setOnRefreshListener(this);
    }

    private void initLoad() {
        if (refreshSubscriber == null || refreshSubscriber.isUnsubscribed()) {
            Observable<FundIndex> fObservable = CustomerCardApi.getFundIndexObb();
            Observable<BindInfo> bObservable = CustomerCardApi.getMyFundBankInfoObb();
            Observable<ResultZip> observable = Observable.zip(fObservable,
                    bObservable,
                    new Func2<FundIndex, BindInfo, ResultZip>() {

                        @Override
                        public ResultZip call(
                                FundIndex fundIndex, BindInfo bindInfo) {
                            ResultZip resultZip = new ResultZip();
                            resultZip.fundIndex = fundIndex;
                            resultZip.bindInfo = bindInfo;
                            return resultZip;
                        }
                    });
            refreshSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<ResultZip>() {
                        @Override
                        public void onNext(ResultZip resultZip) {
                            bindInfo = resultZip.bindInfo;
                            mFundIndex = resultZip.fundIndex;
                            setFundIndexView();
                        }
                    })
                    .setPullToRefreshBase(scrollView)
                    .setProgressBar(scrollView.isRefreshing() ? null : progressBar)
                    .build();
            observable.subscribe(refreshSubscriber);
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        initLoad();
    }

    public class ResultZip extends HljHttpResultZip {
        @HljRZField
        FundIndex fundIndex;
        @HljRZField
        BindInfo bindInfo;
    }

    private void refreshFundIndex() {
        if (refreshSubscriber == null || refreshSubscriber.isUnsubscribed()) {
            refreshSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<FundIndex>() {
                        @Override
                        public void onNext(FundIndex fundIndex) {
                            mFundIndex = fundIndex;
                            setFundIndexView();
                        }
                    })
                    .build();
            CustomerCardApi.getFundIndexObb()
                    .subscribe(refreshSubscriber);
        }
    }

    @SuppressLint("StringFormatInvalid")
    private void setFundIndexView() {
        if (mFundIndex == null) {
            return;
        }
        HljCard.setFundProtocolUrl(mFundIndex.getFundProtocolUrl());
        HljCard.setFundQaUrl(mFundIndex.getFundQaUrl());
        HljCard.setFundIncomeMax(mFundIndex.getFundIncomeMax());
        HljCard.setFundIncomeMin(mFundIndex.getFundIncomeMin());
        if (mFundIndex.getGiftCashMoney() > 0) {
            tvCardCash.setText(getString(R.string.format_card_cash_amount3,
                    String.valueOf(mFundIndex.getGiftCashMoney())));
        } else {
            tvCardCash.setText(getString(R.string.label_no_card_cash_amount));
        }
        if (mFundIndex.getFundTotal() > 0) {
            tvRollOutFund.setEnabled(true);
        } else {
            tvRollOutFund.setEnabled(false);
        }
        String fundRate = CommonUtil.formatDouble2String(mFundIndex.getFundRate() * 100.0d);
        String fundRateBonus = CommonUtil.formatDouble2String(mFundIndex.getFundRateBonus() *
                100.0d);
        String fundRateTotal = CommonUtil.formatDouble2StringWithTwoFloat((mFundIndex.getFundRate
                () + mFundIndex.getFundRateBonus()) * 100.0d);

        String fundRateAdd = getString(R.string.format_fund_rate, fundRate, fundRateBonus);
        ForegroundColorSpan span = new ForegroundColorSpan(ContextCompat.getColor(this,
                R.color.color_fund_gold));
        int start = fundRateAdd.indexOf("+") + 1;
        int length = fundRateBonus.length() + 1;//算上%字符
        SpannableString spanString = new SpannableString(fundRateAdd);
        spanString.setSpan(span, start, start + length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvFundRateAdd.setText(spanString);
        tvFundRateTotal.setText(fundRateTotal + "%");
        tvEarnings.setText(String.valueOf(mFundIndex.getFundProfit()));
        tvTotalFund.setText(String.valueOf(mFundIndex.getFundTotal()));
        if (TextUtils.isEmpty(mFundIndex.getFundRateBonusLogo())) {
            imgFundRateBonus.setVisibility(View.GONE);
        } else {
            imgFundRateBonus.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(ImagePath.buildPath(mFundIndex.getFundRateBonusLogo())
                            .width(fundRateBonusWidth)
                            .height(fundRateBonusWidth)
                            .cropPath())
                    .into(imgFundRateBonus);
        }

        if (mFundIndex.getFundTotal() == 0 && mFundIndex.getFundProfit() == 0 && mFundIndex
                .getGiftCashMoney() > 0) {
            // 最多出现3次。第1次：直接出现，第2次：首次后7天，第三次：首次出现后30天。本地记录即可。
            int fundDialogCount = sharedPreferences.getInt(HljCommon.SharedPreferencesNames
                            .PREF_FUND_DIALOG_COUNT,
                    0);
            if (fundDialogCount < 3) {
                long fundDialogTime = sharedPreferences.getLong(HljCommon.SharedPreferencesNames
                                .PREF_FUND_DIALOG_DATE,
                        0);
                long nowTime = HljTimeUtils.getServerCurrentTimeMillis();
                int day = (int) ((nowTime - fundDialogTime) / (24 * 60 * 60 * 1000));
                if (day <= 0 && fundDialogCount == 0) {
                    showCashGiftFundDialog(fundDialogCount, nowTime);
                } else if (day >= 7 && fundDialogCount == 1) {
                    showCashGiftFundDialog(fundDialogCount, nowTime);
                } else if (day >= 30) {
                    showCashGiftFundDialog(fundDialogCount, nowTime);
                }
            }
        }
    }

    private void showCashGiftFundDialog(int fundDialogCount, long nowTime) {
        sharedPreferences.edit()
                .putInt(HljCommon.SharedPreferencesNames.PREF_FUND_DIALOG_COUNT,
                        fundDialogCount + 1)
                .apply();
        sharedPreferences.edit()
                .putLong(HljCommon.SharedPreferencesNames.PREF_FUND_DIALOG_DATE, nowTime)
                .apply();
        if (fundDialog != null && fundDialog.isShowing()) {
            return;
        }
        if (fundDialog == null) {
            fundDialog = new Dialog(this, R.style.BubbleDialogTheme);
            fundDialog.setContentView(R.layout.dialog_cash_gift_fund);
            fundDialog.findViewById(R.id.tv_cancel)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            fundDialog.dismiss();
                        }
                    });
            fundDialog.findViewById(R.id.action_close)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            fundDialog.dismiss();
                        }
                    });
            tvCashGiftMoney = fundDialog.findViewById(R.id.tv_cash_gift_money);
            Window win = fundDialog.getWindow();
            if (win != null) {
                WindowManager.LayoutParams params = win.getAttributes();
                Point point = CommonUtil.getDeviceSize(this);
                params.width = point.x;
                win.setGravity(Gravity.CENTER);
                win.setWindowAnimations(R.style.dialog_anim_rise_style);
            }
        }
        tvCashGiftMoney.setText(CommonUtil.formatDouble2StringWithTwoFloat(mFundIndex != null ?
                mFundIndex.getGiftCashMoney() : 0));
        fundDialog.show();
    }

    public void onBackPressed(View view) {
        super.onBackPressed();
    }

    @OnClick(R2.id.btn_more)
    public void onBtnMoreClicked() {
        if (moreDialog != null && moreDialog.isShowing()) {
            return;
        }
        if (moreDialog == null) {
            LinkedHashMap<String, View.OnClickListener> map = new LinkedHashMap<>();
            map.put("常见问题", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HljWeb.startWebView(FundManageActivity.this,
                            mFundIndex != null ? mFundIndex.getFundQaUrl() : null);
                    moreDialog.dismiss();
                }
            });
            map.put("理财明细", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(FundManageActivity.this, FundDetailActivity.class);
                    startActivity(intent);
                    moreDialog.dismiss();
                }
            });
            map.put("理财设置", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(FundManageActivity.this, FundSettingActivity.class);
                    startActivity(intent);
                    moreDialog.dismiss();
                }
            });
            moreDialog = DialogUtil.createBottomMenuDialog(this, map, null);
        }
        moreDialog.show();
    }

    @OnClick(R2.id.tv_roll_in_fund)
    public void onTvRollInFundClicked() {
        Intent intent = new Intent();
        if (bindInfo == null) {
            intent.setClass(this, BindFundBankActivity.class);
        } else {
            intent.setClass(this, BankRollInFundActivity.class);
            intent.putExtra(BankRollInFundActivity.ARG_BIND_INFO, bindInfo);
        }
        startActivity(intent);
    }

    @OnClick(R2.id.tv_roll_out_fund)
    public void onTvRollOutFundClicked() {
        Intent intent = new Intent();
        intent.setClass(this, BankRollOutFundActivity.class);
        intent.putExtra(BankRollOutFundActivity.ARG_BIND_INFO, bindInfo);
        intent.putExtra(BankRollOutFundActivity.ARG_FUND_INDEX, mFundIndex);
        startActivity(intent);
    }

    @OnClick(R2.id.tv_card_cash)
    public void onTvCardCashClicked() {
        Intent intent = new Intent();
        intent.setClass(this, BalanceActivity.class);
        startActivity(intent);
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
                                case BIND_FUND_BANK_SUCCESS:
                                    initLoad();
                                    break;
                                case ROLL_IN_OR_OUT_FUND_SUCCESS:
                                    refreshFundIndex();
                                    break;
                            }
                        }
                    });
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(refreshSubscriber, rxBusEventSub);
    }
}
