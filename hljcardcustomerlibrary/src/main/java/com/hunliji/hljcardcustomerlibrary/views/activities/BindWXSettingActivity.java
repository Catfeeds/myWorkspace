package com.hunliji.hljcardcustomerlibrary.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.google.gson.JsonObject;
import com.hunliji.hljcardcustomerlibrary.api.CustomerCardApi;
import com.hunliji.hljcardcustomerlibrary.R;
import com.hunliji.hljcardcustomerlibrary.R2;
import com.hunliji.hljcardlibrary.HljCard;
import com.hunliji.hljcardlibrary.api.CardApi;
import com.hunliji.hljcardlibrary.models.Card;
import com.hunliji.hljcardlibrary.models.CardRxEvent;
import com.hunliji.hljcardcustomerlibrary.utils.WXHelper;
import com.hunliji.hljcardlibrary.views.activities.CardWebActivity;
import com.hunliji.hljcommonlibrary.models.BindInfo;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.models.userprofile.WXInfo;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljsharelibrary.third.ThirdApi;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by mo_yu on 2017/6/12.微信绑定设置页
 */

public class BindWXSettingActivity extends HljBaseActivity implements WXHelper.OnWXLoginListener {


    @BindView(R2.id.tv_modify_name)
    TextView tvModifyName;
    @BindView(R2.id.btn_bind_groom)
    Button btnBindGroom;
    @BindView(R2.id.btn_bind_bride)
    Button btnBindBride;
    @BindView(R2.id.tv_other_certification)
    TextView tvOtherCertification;
    @BindView(R2.id.unbind_wx_layout)
    ScrollView unbindWxLayout;
    @BindView(R2.id.tv_wx_info)
    TextView tvWxInfo;
    @BindView(R2.id.btn_change_certification)
    Button btnChangeCertification;
    @BindView(R2.id.tv_hint)
    TextView tvHint;
    @BindView(R2.id.tv_change_bank)
    TextView tvChangeBank;
    @BindView(R2.id.tv_withdraw_bank_tip)
    TextView tvWithdrawBankTip;
    @BindView(R2.id.bind_wx_layout)
    RelativeLayout bindWxLayout;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;

    private Card card;
    private long cardId;
    private boolean isCanModifyName;
    private boolean isFromBank;
    private BindInfo bindInfo;
    private String groomName;
    private String brideName;
    private String idHolder;
    private HljHttpSubscriber unbindSubscriber;
    private HljHttpSubscriber bindSubscriber;
    private HljHttpSubscriber initSubscriber;
    private HljHttpSubscriber wxSubscriber;
    private Subscription cardRxBusSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_wx_setting___card);
        ButterKnife.bind(this);
        initValue();
        if (bindInfo != null || cardId == 0) {
            refreshBindView();
        } else {
            getBindInfo();
        }
        registerRxBusEvent();
    }

    private void initValue() {
        card = getIntent().getParcelableExtra("card");
        if (card != null) {
            cardId = card.getId();
            groomName = card.getGroomName();
            brideName = card.getBrideName();
        }
        bindInfo = getIntent().getParcelableExtra("bind_info");
        isCanModifyName = getIntent().getBooleanExtra("can_modify_name", true);
        isFromBank = getIntent().getBooleanExtra("is_from_bank", false);
        if (!isCanModifyName) {
            hindModifyName();
        }
    }

    private void hindModifyName() {
        tvModifyName.setVisibility(View.GONE);
    }

    private void refreshBindView() {
        if (bindInfo == null) {
            setTitle("提现设置");
            WXHelper.getInstance(this)
                    .setOnWXLoginListener(this);
            unbindWxLayout.setVisibility(View.VISIBLE);
            bindWxLayout.setVisibility(View.GONE);
            btnBindBride.setText(getString(R.string.format_bind_name___card, brideName));
            btnBindGroom.setText(getString(R.string.format_bind_name___card, groomName));
        } else {
            setTitle("微信钱包提现");
            unbindWxLayout.setVisibility(View.GONE);
            bindWxLayout.setVisibility(View.VISIBLE);
            tvWxInfo.setText(getString(R.string.format_bind_info___card,
                    "微信钱包",
                    bindInfo.getIdHolder()));
            tvHint.setText(CommonUtil.fromHtml(this,
                    getString(R.string.label_switch_cash_hint___card)));
            btnChangeCertification.setText(HljCard.isCustomer(this) ? getString(R.string
                    .label_change_certification___card) : getString(
                    R.string.label_change__card));
        }
    }

    private void getBindInfo() {
        if (initSubscriber == null || initSubscriber.isUnsubscribed()) {
            initSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<BindInfo>() {
                        @Override
                        public void onNext(BindInfo data) {
                            bindInfo = data;
                            refreshBindView();
                        }
                    })
                    .setOnErrorListener(new SubscriberOnErrorListener() {
                        @Override
                        public void onError(Object o) {
                            refreshBindView();
                        }
                    })
                    .setDataNullable(true)
                    .build();
            CustomerCardApi.getBindInfo(cardId)
                    .subscribe(initSubscriber);
        }
    }

    private void getBindWxInfo(final WXInfo wxInfo) {
        if (wxSubscriber == null || wxSubscriber.isUnsubscribed()) {
            wxSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<JsonObject>() {
                        @Override
                        public void onNext(JsonObject jsonObject) {
                            String openId = wxInfo.getOpenId();
                            String nickName = jsonObject.get("nickname")
                                    .getAsString();
                            bindWX(openId, nickName);
                        }
                    })
                    .build();
            ThirdApi.getWeixnUserInfo(wxInfo.getAccessToken(), wxInfo.getOpenId())
                    .subscribe(wxSubscriber);
        }
    }

    /**
     * 绑定微信
     *
     * @param openId
     * @param wxNickName
     */
    private void bindWX(String openId, String wxNickName) {
        if (bindSubscriber == null || bindSubscriber.isUnsubscribed()) {
            bindSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setProgressBar(progressBar)
                    .setOnNextListener(new SubscriberOnNextListener<BindInfo>() {
                        @Override
                        public void onNext(BindInfo info) {
                            bindInfo = info;
                            refreshBindView();
                            RxBus.getDefault()
                                    .post(new RxEvent(RxEvent.RxEventType.BIND_WX_SUCCESS, null));
                        }
                    })
                    .build();
            CustomerCardApi.bindWXObb(cardId, idHolder, openId, wxNickName)
                    .subscribe(bindSubscriber);
        }
    }

    /**
     * 解绑
     *
     * @param cardId
     */
    private void unBindWX(long cardId) {
        if (unbindSubscriber == null || unbindSubscriber.isUnsubscribed()) {
            unbindSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener() {
                        @Override
                        public void onNext(Object o) {
                            RxBus.getDefault()
                                    .post(new RxEvent(RxEvent.RxEventType.UNBIND_WX_SUCCESS, null));
                            if (!HljCard.isCustomer(BindWXSettingActivity.this)) {
                                //请帖大师不能绑定微信钱包
                                if (isFromBank) {
                                    onBackPressed();
                                } else {
                                    Intent intent = new Intent(BindWXSettingActivity.this,
                                            BindBankSettingActivity.class);
                                    intent.putExtra("card", card);
                                    intent.putExtra("is_from_wx", true);
                                    intent.putExtra("can_modify_name", isCanModifyName);
                                    startActivity(intent);
                                    finish();
                                }
                            } else {
                                bindInfo = null;
                                refreshBindView();
                            }
                        }
                    })
                    .setDataNullable(true)
                    .setProgressBar(progressBar)
                    .build();
            CustomerCardApi.unbindObb(cardId)
                    .subscribe(unbindSubscriber);
        }
    }

    @OnClick(R2.id.btn_bind_groom)
    public void onBindGroomClicked() {
        idHolder = groomName;
        progressBar.setVisibility(View.VISIBLE);
        WXHelper.getInstance(this)
                .weChatLogin();
    }

    @OnClick(R2.id.btn_bind_bride)
    public void onBindBrideClicked() {
        idHolder = brideName;
        progressBar.setVisibility(View.VISIBLE);
        WXHelper.getInstance(this)
                .weChatLogin();
    }

    @OnClick(R2.id.btn_change_certification)
    public void onChangeWxBind() {
        DialogUtil.createDoubleButtonDialog(this,
                HljCard.isCustomer(this) ? "是否确认更换微信钱包？" : "是否确认解绑微信钱包？",
                "确认",
                "取消",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        unBindWX(cardId);
                    }
                },
                null)
                .show();
    }

    @OnClick({R2.id.tv_other_certification, R2.id.tv_change_bank})
    public void onChangeBank(View view) {
        if (isFromBank && bindInfo == null) {
            onBackPressed();
        } else {
            Intent intent = new Intent(this, BindBankSettingActivity.class);
            intent.putExtra("card", card);
            intent.putExtra("is_from_wx", true);
            intent.putExtra("can_modify_name", isCanModifyName);
            startActivity(intent);
        }
    }

    @OnClick(R2.id.tv_modify_name)
    public void onModifyName() {
        ARouter.getInstance()
                .build(RouterPath.IntentPath.Card.CARD_INFO_EDIT)
                .withParcelable("card", card)
                .navigation(this);
    }

    @Override
    public void onLogin(WXInfo wxInfo) {
        if (wxInfo != null && !TextUtils.isEmpty(wxInfo.getOpenId())) {
            getBindWxInfo(wxInfo);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onError(String error) {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onCancel() {
        progressBar.setVisibility(View.GONE);
    }

    //注册RxEventBus监听
    private void registerRxBusEvent() {
        if (cardRxBusSubscription == null || cardRxBusSubscription.isUnsubscribed()) {
            cardRxBusSubscription = RxBus.getDefault()
                    .toObservable(CardRxEvent.class)
                    .subscribe(new RxBusSubscriber<CardRxEvent>() {
                        @Override
                        protected void onEvent(CardRxEvent cardRxEvent) {
                            switch (cardRxEvent.getType()) {
                                case CARD_INFO_EDIT:
                                    BindWXSettingActivity.this.card = (Card) cardRxEvent
                                            .getObject();
                                    groomName = card.getGroomName();
                                    brideName = card.getBrideName();
                                    btnBindBride.setText(getString(R.string.format_bind_name___card,
                                            brideName));
                                    btnBindGroom.setText(getString(R.string.format_bind_name___card,
                                            groomName));
                                    break;
                            }
                        }
                    });
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(bindSubscriber,
                wxSubscriber,
                initSubscriber,
                unbindSubscriber,
                cardRxBusSubscription);
    }

}
