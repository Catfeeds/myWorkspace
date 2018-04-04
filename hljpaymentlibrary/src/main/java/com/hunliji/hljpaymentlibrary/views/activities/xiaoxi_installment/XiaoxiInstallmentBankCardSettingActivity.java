package com.hunliji.hljpaymentlibrary.views.activities.xiaoxi_installment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.suncloud.hljweblibrary.HljWeb;
import com.google.gson.JsonElement;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljpaymentlibrary.R;
import com.hunliji.hljpaymentlibrary.R2;
import com.hunliji.hljpaymentlibrary.api.xiaoxi_installment.XiaoxiInstallmentApi;
import com.hunliji.hljpaymentlibrary.models.PayRxEvent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * 还款银行卡设置页
 * Created by chen_bin on 2017/8/17 0017.
 */
public class XiaoxiInstallmentBankCardSettingActivity extends HljBaseActivity {

    @BindView(R2.id.img_bank_logo)
    ImageView imgBankLogo;
    @BindView(R2.id.tv_bank_name)
    TextView tvBankName;
    @BindView(R2.id.tv_real_name)
    TextView tvRealName;
    @BindView(R2.id.tv_bank_card_no)
    TextView tvBankCardNo;
    @BindView(R2.id.content_layout)
    LinearLayout contentLayout;
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
        setContentView(R.layout.activity_xiaoxi_installment_bank_card_setting___pay);
        ButterKnife.bind(this);
        initViews();
        initLoad();
        registerRxBusEvent();
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
            initSub = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<JsonElement>() {
                        @Override
                        public void onNext(JsonElement jsonElement) {
                            String bankLogo = CommonUtil.getAsString(jsonElement, "bank_logo");
                            String bankName = CommonUtil.getAsString(jsonElement, "bank_name");
                            String realName = CommonUtil.getAsString(jsonElement, "real_name");
                            String bankCardNo = CommonUtil.getAsString(jsonElement, "bank_account");
                            Glide.with(XiaoxiInstallmentBankCardSettingActivity.this)
                                    .load(bankLogo)
                                    .apply(new RequestOptions().dontAnimate())
                                    .into(imgBankLogo);
                            tvBankName.setText(bankName);
                            tvRealName.setText("(" + realName + ")");
                            if (!TextUtils.isEmpty(bankCardNo) && bankCardNo.length() > 4) {
                                StringBuilder sb = new StringBuilder();
                                for (int i = 0, size = bankCardNo.length() - 4; i < size; i++) {
                                    sb.append("*");
                                }
                                String suffixCardNo = bankCardNo.substring(bankCardNo.length() - 4,
                                        bankCardNo.length());
                                sb.append(suffixCardNo);
                                tvBankCardNo.setText(sb.toString());
                            }
                        }
                    })
                    .setEmptyView(emptyView)
                    .setProgressBar(progressBar)
                    .setContentView(contentLayout)
                    .build();
            XiaoxiInstallmentApi.getBankCardBindedInfoObb()
                    .subscribe(initSub);
        }
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
                                case BIND_BANK_CARD_SUCCESS:
                                    initLoad();
                                    break;
                            }
                        }
                    });
        }
    }

    @OnClick(R2.id.btn_change_bank_card)
    void onChangeBankCard() {
        startActivity(new Intent(this, AddXiaoxiInstallmentBankCardActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    @OnClick(R2.id.tv_bank_card_limit)
    void onBankCardLimit() {
        HljWeb.startWebView(this, XiaoxiInstallmentApi.XIAOXI_INSTALLMENT_BANK_CARD_LIMIT_URL);
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(rxBusEventSub, initSub);
    }
}
