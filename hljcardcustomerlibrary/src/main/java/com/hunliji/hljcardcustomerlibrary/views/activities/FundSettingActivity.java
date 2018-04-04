package com.hunliji.hljcardcustomerlibrary.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.gson.JsonElement;
import com.hunliji.hljcardcustomerlibrary.R;
import com.hunliji.hljcardcustomerlibrary.R2;
import com.hunliji.hljcardcustomerlibrary.api.CustomerCardApi;
import com.hunliji.hljcommonlibrary.models.BindInfo;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljhttplibrary.entities.HljHttpResultZip;
import com.hunliji.hljhttplibrary.entities.HljRZField;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.functions.Func2;

/**
 * Created by mo_yu on 2017/11/24.理财设置
 */
public class FundSettingActivity extends HljBaseActivity {

    @BindView(R2.id.my_bank_view)
    RelativeLayout myBankView;
    @BindView(R2.id.cb_auto_into)
    CheckBox cbAutoInto;
    @BindView(R2.id.auto_into_view)
    RelativeLayout autoIntoView;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;

    private boolean lockState;
    private boolean isAutoIncome;
    private BindInfo bindInfo;//绑卡信息
    private boolean isBindBank;
    private HljHttpSubscriber initSubscriber;
    private HljHttpSubscriber setUpSubscriber;
    private Subscription rxBusEventSub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fund_setting);
        ButterKnife.bind(this);
        initValue();
        initView();
        initLoad();
        registerRxBusEvent();
    }

    private void initValue() {
        isAutoIncome = false;
        isBindBank = false;
    }

    private void initView() {
        cbAutoInto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (lockState) {
                    return;
                }
                isAutoIncome = b;
                setUpFund();
            }
        });
    }

    private void initLoad() {
        if (initSubscriber == null || initSubscriber.isUnsubscribed()) {
            Observable<JsonElement> sObservable = CustomerCardApi.getSetUpFundObb();
            Observable<BindInfo> bObservable = CustomerCardApi.getMyFundBankInfoObb();
            Observable<ResultZip> observable = Observable.zip(sObservable,
                    bObservable,
                    new Func2<JsonElement, BindInfo, ResultZip>() {

                        @Override
                        public ResultZip call(
                                JsonElement jsonElement, BindInfo bindInfo) {
                            ResultZip resultZip = new ResultZip();
                            resultZip.jsonElement = jsonElement;
                            resultZip.bindInfo = bindInfo;
                            return resultZip;
                        }
                    });
            initSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<ResultZip>() {
                        @Override
                        public void onNext(ResultZip resultZip) {
                            try {
                                isAutoIncome = resultZip.jsonElement.getAsJsonObject()
                                        .get("is_auto_income")
                                        .getAsInt() > 0;
                                lockState = true;
                                cbAutoInto.setChecked(isAutoIncome);
                                lockState = false;
                            } catch (Exception ignored) {
                            }
                            bindInfo = resultZip.bindInfo;
                        }
                    })
                    .setProgressBar(progressBar)
                    .build();
            observable.subscribe(initSubscriber);
        }
    }

    public class ResultZip extends HljHttpResultZip {
        @HljRZField
        JsonElement jsonElement;
        @HljRZField
        BindInfo bindInfo;
    }

    private void setUpFund() {
        CommonUtil.unSubscribeSubs(setUpSubscriber);
        setUpSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener() {
                    @Override
                    public void onNext(Object o) {
                    }
                })
                .setProgressBar(progressBar)
                .build();
        CustomerCardApi.postSetUpFundObb(isAutoIncome)
                .subscribe(setUpSubscriber);
    }

    @OnClick(R2.id.my_bank_view)
    public void onMyBankClicked() {
        Intent intent = new Intent();
        if (bindInfo != null || isBindBank) {
            intent.setClass(this, FundMyBankActivity.class);
            intent.putExtra(FundMyBankActivity.ARG_BIND_INFO, bindInfo);
        } else {
            intent.setClass(this, BindFundBankActivity.class);
        }
        startActivity(intent);
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(initSubscriber, setUpSubscriber, rxBusEventSub);
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
                                    isBindBank = true;
                                    break;
                                case ROLL_IN_OR_OUT_FUND_SUCCESS:
                                    finish();
                                    break;
                            }
                        }
                    });
        }
    }
}
