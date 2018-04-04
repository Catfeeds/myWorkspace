package com.hunliji.hljcardcustomerlibrary.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.suncloud.hljweblibrary.HljWeb;
import com.google.gson.JsonElement;
import com.hunliji.hljcardcustomerlibrary.R;
import com.hunliji.hljcardcustomerlibrary.R2;
import com.hunliji.hljcardcustomerlibrary.api.CustomerCardApi;
import com.hunliji.hljcardcustomerlibrary.models.FundIndex;
import com.hunliji.hljcardlibrary.HljCard;
import com.hunliji.hljcommonlibrary.models.BindInfo;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.makeramen.rounded.RoundedImageView;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mo_yu on 2017/11/24.理财转出
 */
public class BankRollOutFundActivity extends HljBaseActivity {

    public static final String ARG_BIND_INFO = "bind_info";
    public static final String ARG_FUND_INDEX = "fund_index";

    @BindView(R2.id.img_bank_logo)
    RoundedImageView imgBankLogo;
    @BindView(R2.id.tv_bank_name)
    TextView tvBankName;
    @BindView(R2.id.tv_roll_out_amount)
    TextView tvRollOutAmount;
    @BindView(R2.id.action_roll_out_all)
    TextView actionRollOutAll;
    @BindView(R2.id.et_bank_amount)
    EditText etBankAmount;
    @BindView(R2.id.action_roll_out_confirm)
    TextView actionRollOutConfirm;
    @BindView(R2.id.tv_roll_out_tip)
    TextView tvRollOutTip;
    @BindView(R2.id.bank_roll_out_view)
    LinearLayout bankRollOutView;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    private double rollOutMax = 20000;//每日每次最大金额
    private double rollOutMin = 2;
    private double rollOutCash;//转出金额
    private FundIndex fundIndex;
    private BindInfo bindInfo;

    private HljHttpSubscriber rollOutSubscriber;
    private HljHttpSubscriber initSubscriber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roll_out_fund);
        ButterKnife.bind(this);
        initValue();
        initView();
        if (bindInfo == null) {
            getBindBankInfo();
        } else {
            refreshBankInfo(bindInfo);
        }
    }

    private void initValue() {
        bindInfo = getIntent().getParcelableExtra(ARG_BIND_INFO);
        fundIndex = getIntent().getParcelableExtra(ARG_FUND_INDEX);
    }

    private void initView() {
        setOkButton(R.mipmap.icon_question_primary_44_44);
        etBankAmount.addTextChangedListener(textWatcher);
        if (fundIndex != null) {
            tvRollOutAmount.setText(getString(R.string.format_total_fund,
                    CommonUtil.formatDouble2String(fundIndex.getFundTotal())));
            rollOutMax = fundIndex.getFundOutcomeMax();
            rollOutMin = fundIndex.getFundOutcomeMin();
            tvRollOutTip.setText(getString(R.string.label_roll_out_fund_tip,
                    CommonUtil.formatDouble2String(rollOutMin),
                    CommonUtil.formatDouble2String(rollOutMax / 10000.0d)));
        }
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

    @Override
    public void onOkButtonClick() {
        super.onOkButtonClick();
        HljWeb.startWebView(this, HljCard.fundQaUrl);
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

    @OnClick(R2.id.action_roll_out_all)
    public void rollOutAll() {
        if (fundIndex != null) {
            etBankAmount.setText(CommonUtil.formatDouble2String(fundIndex.getFundTotal()));
        }
    }

    @OnClick(R2.id.action_roll_out_confirm)
    public void rollOutConfirm() {
        if (TextUtils.isEmpty(etBankAmount.getText())) {
            ToastUtil.showToast(this, "请输入正确的金额", 0);
            return;
        } else {
            try {
                rollOutCash = Double.valueOf(etBankAmount.getText()
                        .toString());
            } catch (NumberFormatException ignored) {
            }
            if (rollOutCash < rollOutMin) {
                ToastUtil.showToast(this,
                        "最少转出" + CommonUtil.formatDouble2String(rollOutMin) + "元",
                        0);
                return;
            } else if (rollOutCash > rollOutMax) {
                ToastUtil.showToast(this,
                        "单次转入最多" + CommonUtil.formatDouble2String(rollOutMax) + "元",
                        0);
                return;
            }
        }
        if (rollOutSubscriber == null || rollOutSubscriber.isUnsubscribed()) {
            rollOutSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setProgressDialog(DialogUtil.createProgressDialog(this))
                    .setOnNextListener(new SubscriberOnNextListener<JsonElement>() {
                        @Override
                        public void onNext(JsonElement jsonElement) {
                            String message = null;
                            try {
                                message = jsonElement.getAsJsonObject()
                                        .get("message")
                                        .getAsString();
                            } catch (Exception e) {
                            }
                            RxBus.getDefault()
                                    .post(new RxEvent(RxEvent.RxEventType
                                            .ROLL_IN_OR_OUT_FUND_SUCCESS,
                                            false));
                            Intent intent = new Intent(BankRollOutFundActivity.this,
                                    AfterRollInOutActivity.class);
                            intent.putExtra(AfterRollInOutActivity.ARG_TITLE,
                                    getString(R.string.title_activity_after_roll_out));
                            intent.putExtra(AfterRollInOutActivity.ARG_AMOUNT, rollOutCash);
                            intent.putExtra(AfterRollInOutActivity.ARG_MSG, message);
                            startActivity(intent);
                            overridePendingTransition(R.anim.activity_anim_default,
                                    R.anim.slide_in_up);
                            finish();
                        }
                    })
                    .build();
            HashMap<String, Object> map = new HashMap<>();
            map.put("input_money", String.valueOf(rollOutCash));
            CustomerCardApi.fundRollOutObb(map)
                    .subscribe(rollOutSubscriber);
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(rollOutSubscriber, initSubscriber);
    }
}
