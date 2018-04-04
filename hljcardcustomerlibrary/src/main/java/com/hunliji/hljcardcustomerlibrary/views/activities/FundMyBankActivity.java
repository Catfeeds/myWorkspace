package com.hunliji.hljcardcustomerlibrary.views.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.suncloud.hljweblibrary.HljWeb;
import com.hunliji.hljcardcustomerlibrary.R;
import com.hunliji.hljcardcustomerlibrary.R2;
import com.hunliji.hljcardcustomerlibrary.api.CustomerCardApi;
import com.hunliji.hljcardlibrary.HljCard;
import com.hunliji.hljcommonlibrary.models.BindInfo;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImagePath;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;

/**
 * 我的银行卡
 * Created by mo_yu on 2017/11/24 .
 */
public class FundMyBankActivity extends HljBaseActivity {

    public static final String ARG_BIND_INFO = "bind_info";

    @BindView(R2.id.img_bank_logo)
    ImageView imgBankLogo;
    @BindView(R2.id.tv_bank_name)
    TextView tvBankName;
    @BindView(R2.id.tv_real_name)
    TextView tvRealName;
    @BindView(R2.id.tv_bank_card_no)
    TextView tvBankCardNo;
    @BindView(R2.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R2.id.scroll_view)
    ScrollView scrollView;

    private int bankLogoWidth;
    private Subscription rxBusEventSub;
    private BindInfo bindInfo;
    private HljHttpSubscriber initSub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSwipeBackEnable(false);
        setContentView(R.layout.activity_my_bank);
        ButterKnife.bind(this);
        initValue();
        initViews();
        if (bindInfo == null) {
            initLoad();
        } else {
            refreshBindInfoView();
        }
        registerRxBusEvent();
    }

    private void initValue() {
        bindInfo = getIntent().getParcelableExtra(ARG_BIND_INFO);
        bankLogoWidth = CommonUtil.dp2px(this, 28);
    }

    private void initViews() {
        setOkButton(R.mipmap.icon_question_primary_44_44);
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
                    .setOnNextListener(new SubscriberOnNextListener<BindInfo>() {
                        @Override
                        public void onNext(BindInfo bindInfo) {
                            FundMyBankActivity.this.bindInfo = bindInfo;
                            refreshBindInfoView();
                        }
                    })
                    .setEmptyView(emptyView)
                    .setContentView(scrollView)
                    .setProgressBar(progressBar)
                    .build();
            CustomerCardApi.getMyFundBankInfoObb()
                    .subscribe(initSub);
        }
    }

    @Override
    public void onOkButtonClick() {
        super.onOkButtonClick();
        HljWeb.startWebView(this, HljCard.fundQaUrl);
    }


    private void refreshBindInfoView() {
        scrollView.setVisibility(View.VISIBLE);
        Glide.with(FundMyBankActivity.this)
                .load(ImagePath.buildPath(bindInfo.getBankLogo())
                        .width(bankLogoWidth)
                        .height(bankLogoWidth)
                        .cropPath())
                .apply(new RequestOptions().dontAnimate())
                .into(imgBankLogo);
        tvBankName.setText(bindInfo.getBankDesc());
        tvRealName.setText("(" + bindInfo.getIdHolder() + ")");
        String bankCardNo = bindInfo.getAccNo();
        tvBankCardNo.setText("尾号" + bankCardNo);
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
                            }
                        }
                    });
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(rxBusEventSub, initSub);
    }
}
