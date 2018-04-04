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
import android.widget.TextView;
import android.widget.Toast;

import com.example.suncloud.hljweblibrary.HljWeb;
import com.hunliji.hljcardcustomerlibrary.R;
import com.hunliji.hljcardcustomerlibrary.R2;
import com.hunliji.hljcardcustomerlibrary.api.CustomerCardApi;
import com.hunliji.hljcardcustomerlibrary.views.activities.WithdrawActivity;
import com.hunliji.hljcardcustomerlibrary.views.activities.WithdrawV2Activity;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.widgets.NoUnderlineSpan;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.hunliji.hljcardcustomerlibrary.models.WithDraw;
import com.hunliji.hljcardcustomerlibrary.models.WithdrawParam;

import com.hunliji.hljsharelibrary.HljThirdLogin;
import com.hunliji.hljsharelibrary.models.ThirdLoginParameter;

import com.hunliji.hljcardcustomerlibrary.views.activities.AfterWithdrawCashActivity;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * Created by mo_yu on 2017/2/8.礼物余额提现
 */
public class WithdrawGiftFragment extends RefreshFragment {
    @BindView(R2.id.tv_balance_count)
    TextView tvBalanceCount;
    @BindView(R2.id.action_withdraw_cash_confirm)
    TextView actionWithdrawCashConfirm;
    @BindView(R2.id.et_withdraw_cash_count)
    EditText etWithdrawCashCount;
    Unbinder unbinder;
    @BindView(R2.id.tv_withdraw_cash_tip_bottom)
    TextView tvWithdrawCashTipBottom;

    private double withdrawMax = 20000.00;//每日每次最大金额
    private double withdrawMin = 2;
    private double withdrawRate = 0.006;
    private double balance;//余额
    private double withdrawCash;//提现金额
    private String smsCode;//验证码
    private WithdrawParam withdrawParam;
    private Subscription rxSubscription;
    private HljHttpSubscriber withdrawSubscriber;

    public static WithdrawGiftFragment newInstance(Bundle args) {
        WithdrawGiftFragment fragment = new WithdrawGiftFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerRxBus();
    }

    private void registerRxBus() {
        rxSubscription = RxBus.getDefault()
                .toObservable(RxEvent.class)
                .filter(new Func1<RxEvent, Boolean>() {
                    @Override
                    public Boolean call(RxEvent rxEvent) {
                        return rxEvent.getType() == RxEvent.RxEventType.THIRD_BIND_CALLBACK;
                    }
                })
                .map(new Func1<RxEvent, ThirdLoginParameter>() {
                    @Override
                    public ThirdLoginParameter call(RxEvent rxEvent) {
                        return (ThirdLoginParameter) rxEvent.getObject();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxBusSubscriber<ThirdLoginParameter>() {
                    @Override
                    protected void onEvent(ThirdLoginParameter thirdLoginParameter) {
                        onBind(thirdLoginParameter);
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_withdraw_gift, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            balance = getArguments().getDouble(WithdrawActivity.ARG_GIFT_BALANCE, 0.0);
            smsCode = getArguments().getString(WithdrawActivity.ARG_SMS_CODE);
            withdrawParam = getArguments().getParcelable(WithdrawV2Activity.ARG_WITHDRAW_PARAM);
        }
        if (withdrawParam != null) {
            withdrawMax = withdrawParam.getWithdrawMax();
            withdrawMin = withdrawParam.getWithdrawMin();
            withdrawRate = withdrawParam.getWithdrawRate();
        }
        initView();
    }

    private void initView() {
        tvBalanceCount.setText(getString(R.string.label_balance_count2, String.valueOf(balance)));
        etWithdrawCashCount.addTextChangedListener(textWatcher);
        setBtnWithdraw(etWithdrawCashCount.getText()
                .toString());
        String withdrawMaxStr;
        if (withdrawMax >= 10000) {
            withdrawMaxStr = withdrawMax / 10000 + "万";
        } else {
            withdrawMaxStr = String.valueOf(withdrawMax);
        }
        String tip = getString(R.string.label_withdraw_cash_tip, withdrawMin,
                withdrawRate * 100,
                withdrawMaxStr,
                withdrawMin);
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
        tvWithdrawCashTipBottom.setText(sp);
        tvWithdrawCashTipBottom.setLinkTextColor(ContextCompat.getColor(getContext(),
                R.color.colorLink));
        tvWithdrawCashTipBottom.setMovementMethod(LinkMovementMethod.getInstance());

    }

    private void setBtnWithdraw(String str) {
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

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

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
            setBtnWithdraw(s.toString());
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
            HljThirdLogin.weixinLogin(getContext())
                    .bind();

        }
    }

    public void onBind(ThirdLoginParameter parameter) {
        if (parameter == null) {
            return;
        }
        String openid;
        try {
            //将字符串转换成jsonObject对象
            JSONObject jsonObject = new JSONObject(parameter.getLoginInfo());
            //获取对应的值
            openid = jsonObject.getString("openid");
        } catch (JSONException e) {
            return;
        }
        if (TextUtils.isEmpty(openid)) {
            return;
        }
        if (actionWithdrawCashConfirm != null) {
            actionWithdrawCashConfirm.setEnabled(false);
        }
        withdrawCash(openid);
    }

    private void withdrawCash(String openId) {
        if (withdrawSubscriber == null || withdrawSubscriber.isUnsubscribed()) {
            withdrawSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                    .setProgressDialog(DialogUtil.createProgressDialog(getContext()))
                    .setOnNextListener(new SubscriberOnNextListener<WithDraw>() {
                        @Override
                        public void onNext(WithDraw result) {
                            actionWithdrawCashConfirm.setEnabled(true);
                            if (result != null) {
                                RxBus.getDefault()
                                        .post(new RxEvent(RxEvent.RxEventType.WITHDRAW_CASH_SUCCESS,
                                                null));
                                Intent intent = new Intent(getActivity(),
                                        AfterWithdrawCashActivity.class);
                                if (TextUtils.isEmpty(result.getResultCode())) {
                                    intent.putExtra("isSuccess", true);
                                } else {
                                    intent.putExtra("isSuccess",
                                            !result.getResultCode()
                                                    .equals("FAIL"));
                                    if (!TextUtils.isEmpty(result.getErrCodeDes())) {
                                        Toast.makeText(getActivity(),
                                                result.getErrCodeDes(),
                                                Toast.LENGTH_LONG)
                                                .show();
                                    }
                                }
                                startActivity(intent);
                                getActivity().finish();
                            }
                        }
                    })
                    .setOnErrorListener(new SubscriberOnErrorListener() {
                        @Override
                        public void onError(Object o) {
                            RxBus.getDefault()
                                    .post(new RxEvent(RxEvent.RxEventType.CLOSE_WITHDRAW_CARD_LIST,
                                            null));
                            actionWithdrawCashConfirm.setEnabled(true);
                            Intent intent = new Intent(getActivity(),
                                    AfterWithdrawCashActivity.class);
                            intent.putExtra("isSuccess", false);
                            startActivity(intent);
                            getActivity().finish();
                        }
                    })
                    .build();
            CustomerCardApi.withdrawGiftObb(smsCode,
                    String.valueOf(withdrawCash),
                    openId,
                    0)
                    .subscribe(withdrawSubscriber);
        }
    }

    @Override
    public void refresh(Object... params) {}

    @Override
    public void onDestroyView() {
        CommonUtil.unSubscribeSubs(withdrawSubscriber, rxSubscription);
        super.onDestroyView();
        unbinder.unbind();
    }
}
