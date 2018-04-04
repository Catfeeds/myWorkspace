package com.hunliji.hljcardcustomerlibrary.views.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.hunliji.hljcardcustomerlibrary.models.CardBalanceDetail;
import com.hunliji.hljcardcustomerlibrary.models.WithDrawCash;
import com.hunliji.hljcardcustomerlibrary.models.WithdrawParam;
import com.hunliji.hljcardcustomerlibrary.views.activities.AfterWithdrawCashActivity;
import com.hunliji.hljcardcustomerlibrary.views.activities.WithdrawV2Activity;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.BindInfo;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.widgets.NoUnderlineSpan;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.makeramen.rounded.RoundedImageView;

import java.text.DecimalFormat;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rx.Observable;

/**
 * Created by mo_yu on 2017/6/18.新版余额提现
 */
public class WithdrawV2Fragment extends RefreshFragment {

    @BindView(R2.id.img_bank_logo)
    RoundedImageView imgBankLogo;
    @BindView(R2.id.tv_bank_name)
    TextView tvBankName;
    @BindView(R2.id.et_withdraw_cash_count)
    EditText etWithdrawCashCount;
    @BindView(R2.id.product_list_layout)
    LinearLayout productListLayout;
    @BindView(R2.id.tv_balance_count)
    TextView tvBalanceCount;
    @BindView(R2.id.action_withdraw_cash_confirm)
    TextView actionWithdrawCashConfirm;
    @BindView(R2.id.tv_withdraw_cash_tip_bottom)
    TextView tvWithdrawCashTipBottom;
    @BindView(R2.id.with_draw_cash_view)
    LinearLayout withDrawCashView;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    Unbinder unbinder;

    private double withdrawMax = 20000.00;//每日每次最大金额
    private double withdrawMin = 2;
    private double withdrawRate = 0.006;
    private double balance;//余额
    private double withdrawCash;//提现金额
    private int bindType;
    private long cardId;
    private CardBalanceDetail balanceDetail;
    private WithdrawParam withdrawParam;

    private HljHttpSubscriber withdrawSubscriber;
    private HljHttpSubscriber initSubscriber;

    public static WithdrawV2Fragment newInstance(Bundle args) {
        WithdrawV2Fragment fragment = new WithdrawV2Fragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_withdraw_v2, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            withdrawParam = getArguments().getParcelable(WithdrawV2Activity.ARG_WITHDRAW_PARAM);
            cardId = getArguments().getLong(WithdrawV2Activity.ARG_ID, 0);
        }
        if (withdrawParam != null) {
            withdrawMax = withdrawParam.getWithdrawMax();
            withdrawMin = withdrawParam.getWithdrawMin();
            withdrawRate = withdrawParam.getWithdrawRate();
        }
        initView();
        initLoad();
    }

    private void initView() {
        tvBalanceCount.setText(getString(R.string.label_balance_count2, String.valueOf(balance)));
        etWithdrawCashCount.addTextChangedListener(textWatcher);
    }

    private void initLoad() {
        if (initSubscriber == null || initSubscriber.isUnsubscribed()) {
            initSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                    .setOnNextListener(new SubscriberOnNextListener<CardBalanceDetail>() {
                        @Override
                        public void onNext(CardBalanceDetail cardBalanceDetail) {
                            balanceDetail = cardBalanceDetail;
                            refreshBindInfo();
                        }
                    })
                    .setProgressBar(progressBar)
                    .build();
            Observable<CardBalanceDetail> observable = CustomerCardApi.getCardBalanceDetailObb(
                    cardId == 0 ? null : String.valueOf(cardId));
            observable.subscribe(initSubscriber);
        }
    }

    private void refreshBindInfo() {
        if (balanceDetail != null && balanceDetail.getBindInfo() != null) {
            BindInfo bindInfo = balanceDetail.getBindInfo();
            bindType = bindInfo.getType();
            String tip;
            String withdrawMaxStr;
            if (withdrawMax >= 10000) {
                withdrawMaxStr = withdrawMax / 10000 + "万";
            } else {
                withdrawMaxStr = String.valueOf(withdrawMax);
            }
            if (bindType == BindInfo.BIND_BANK) {

                tip = getString(R.string.label_withdraw_cash_tip2,
                        withdrawMin,
                        withdrawRate * 100,
                        withdrawMaxStr,
                        withdrawMin);
                tvBankName.setText(getString(R.string.format_bind_info___card,
                        bindInfo.getBankDesc(),
                        bindInfo.getAccNo()));
                String imgUrl = ImagePath.buildPath(bindInfo.getBankLogo())
                        .height(CommonUtil.dp2px(getContext(), 28))
                        .width(CommonUtil.dp2px(getContext(), 28))
                        .cropPath();
                Glide.with(getContext())
                        .load(imgUrl)
                        .apply(new RequestOptions().dontAnimate())
                        .into(imgBankLogo);
            } else {
                imgBankLogo.setImageResource(R.mipmap.icon_wx_63_63___card);
                tvBankName.setText(getString(R.string.format_bind_info___card,
                        "微信钱包",
                        bindInfo.getIdHolder()));
                tip = getString(R.string.label_withdraw_cash_tip,
                        withdrawMin,
                        withdrawRate * 100,
                        withdrawMaxStr,
                        withdrawMin);
            }
            balance = balanceDetail.getBalance();
            //0表示不可修改提现金额
            if (balanceDetail.getCustomize() == 0) {
                etWithdrawCashCount.setEnabled(false);
                etWithdrawCashCount.setFocusable(false);
                etWithdrawCashCount.setText(String.valueOf(balance));
            } else {
                etWithdrawCashCount.setEnabled(true);
                etWithdrawCashCount.setFocusable(true);
            }
            int insuranceStart = tip.indexOf("相关说明");
            SpannableString sp = new SpannableString(tip);
            sp.setSpan(new NoUnderlineSpan() {
                @Override
                public void onClick(View widget) {
                    HljWeb.startWebView(getActivity(), HljCommon.WX_EDU_URL);
                }
            }, insuranceStart, insuranceStart + 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            int startWx = tip.indexOf("微信钱包，");
            if (startWx > 0) {
                sp.setSpan(new ForegroundColorSpan(Color.parseColor("#f83244")),
                        startWx,
                        startWx + 4,
                        Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            }
            int startBank = tip.indexOf("银行卡，");
            if (startBank > 0) {
                sp.setSpan(new ForegroundColorSpan(Color.parseColor("#f83244")),
                        startBank,
                        startBank + 3,
                        Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            }
            tvWithdrawCashTipBottom.setText(sp);
            tvWithdrawCashTipBottom.setLinkTextColor(ContextCompat.getColor(getContext(),
                    R.color.colorLink));
            tvWithdrawCashTipBottom.setMovementMethod(LinkMovementMethod.getInstance());
            setWithdrawState(etWithdrawCashCount.getText()
                    .toString());
        }
    }

    private void setWithdrawState(String str) {
        if (TextUtils.isEmpty(str)) {
            actionWithdrawCashConfirm.setEnabled(false);
            tvBalanceCount.setText(getString(R.string.label_balance_count2,
                    String.valueOf(balance)));
        } else {
            try {
                withdrawCash = Double.valueOf(str);
            } catch (NumberFormatException ignored) {
            }
            if (withdrawCash > balance) {
                tvBalanceCount.setText("输入金额超出余额");
                tvBalanceCount.setTextColor(ContextCompat.getColor(getContext(),
                        R.color.colorPrimary));
                actionWithdrawCashConfirm.setEnabled(false);
                return;
            }
            if (withdrawCash < withdrawMin) {
                tvBalanceCount.setTextColor(ContextCompat.getColor(getContext(),
                        R.color.colorPrimary));
                tvBalanceCount.setText("金额不低于" + withdrawMin + "元");
                actionWithdrawCashConfirm.setEnabled(false);
            } else if (withdrawCash > withdrawMax) {
                tvBalanceCount.setTextColor(ContextCompat.getColor(getContext(),
                        R.color.colorPrimary));
                tvBalanceCount.setText("金额不能大于" + withdrawMax + "元");
                actionWithdrawCashConfirm.setEnabled(false);
            } else {
                double fee = withdrawCash * withdrawRate;
                DecimalFormat df = new DecimalFormat("#####0.00");
                String feeStr = df.format(fee);
                String cashStr = df.format(withdrawCash - Double.valueOf(feeStr));
                tvBalanceCount.setTextColor(ContextCompat.getColor(getContext(),
                        R.color.colorPrimary));
                tvBalanceCount.setText(CommonUtil.fromHtml(getContext(),
                        getString(R.string.label_withdraw_cash_fee_tip, feeStr, cashStr)));
                actionWithdrawCashConfirm.setEnabled(true);
            }
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
                    etWithdrawCashCount.setText(text);
                    etWithdrawCashCount.setSelection(text.length());
                }
            }
            setWithdrawState(s.toString());
        }

        public void afterTextChanged(Editable s) {}
    };

    @OnClick({R2.id.tv_balance_count_all, R2.id.action_withdraw_cash_confirm})
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.tv_balance_count_all) {
            if (balance > withdrawMax) {
                etWithdrawCashCount.setText(String.valueOf(withdrawMax));
            } else {
                etWithdrawCashCount.setText(String.valueOf(balance));
            }

        } else if (i == R.id.action_withdraw_cash_confirm) {
            withdrawCash();

        }
    }


    private void withdrawCash() {
        if (withdrawSubscriber == null || withdrawSubscriber.isUnsubscribed()) {
            withdrawSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                    .setProgressDialog(DialogUtil.createProgressDialog(getContext()))
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpResult<WithDrawCash>>() {
                        @Override
                        public void onNext(HljHttpResult<WithDrawCash> httpResult) {
                            WithDrawCash withDrawCash = httpResult.getData();
                            Intent intent = new Intent(getActivity(),
                                    AfterWithdrawCashActivity.class);
                            if (withDrawCash != null) {
                                //1提现成功 2失败
                                if (withDrawCash.getStatus() == 1) {
                                    RxBus.getDefault()
                                            .post(new RxEvent(RxEvent.RxEventType
                                                    .WITHDRAW_CASH_SUCCESS,
                                                    null));
                                } else {
                                    RxBus.getDefault()
                                            .post(new RxEvent(RxEvent.RxEventType
                                                    .CLOSE_WITHDRAW_CARD_LIST,
                                                    null));
                                    ToastUtil.showToast(getContext(), withDrawCash.getErrMsg(), 0);
                                }
                                intent.putExtra("isSuccess", withDrawCash.getStatus() == 1);
                            } else if (httpResult.getStatus()
                                    .getRetCode() != 0) {
                                RxBus.getDefault()
                                        .post(new RxEvent(RxEvent.RxEventType
                                                .CLOSE_WITHDRAW_CARD_LIST,
                                                null));
                                if (httpResult.getStatus()
                                        .getRetCode() == 4) {
                                    intent.putExtra("error_msg", "请帖姓名与提现账号实名不一致，");
                                }
                                intent.putExtra("isSuccess", false);
                                ToastUtil.showToast(getContext(),
                                        httpResult.getStatus()
                                                .getMsg(),
                                        0);
                            }
                            intent.putExtra("isCash", bindType == BindInfo.BIND_BANK);
                            startActivity(intent);
                            getActivity().finish();
                        }
                    })
                    .setOnErrorListener(new SubscriberOnErrorListener() {
                        @Override
                        public void onError(Object o) {
                            RxBus.getDefault()
                                    .post(new RxEvent(RxEvent.RxEventType.CLOSE_WITHDRAW_CARD_LIST,
                                            null));
                            Intent intent = new Intent(getActivity(),
                                    AfterWithdrawCashActivity.class);
                            intent.putExtra("isSuccess", false);
                            startActivity(intent);
                            getActivity().finish();
                        }
                    })
                    .build();
            HashMap<String, Object> map = new HashMap<>();
            if (cardId != 0) {
                map.put("card_id", cardId);
            }
            map.put("insurance", 0);
            map.put("money", String.valueOf(withdrawCash));
            CustomerCardApi.withdrawCardBalanceObb(map)
                    .subscribe(withdrawSubscriber);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CommonUtil.unSubscribeSubs(withdrawSubscriber, initSubscriber);
    }

    @Override
    public void refresh(Object... params) {}

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
