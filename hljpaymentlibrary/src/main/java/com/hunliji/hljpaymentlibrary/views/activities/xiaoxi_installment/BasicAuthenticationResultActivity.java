package com.hunliji.hljpaymentlibrary.views.activities.xiaoxi_installment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.entities.HljHttpStatus;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljpaymentlibrary.R;
import com.hunliji.hljpaymentlibrary.R2;
import com.hunliji.hljpaymentlibrary.api.xiaoxi_installment.XiaoxiInstallmentApi;
import com.hunliji.hljpaymentlibrary.models.PayRxEvent;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.CreditLimit;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 小犀分期-基础授信结果页
 * Created by chen_bin on 2017/8/10 0010.
 */
public class BasicAuthenticationResultActivity extends HljBaseActivity {

    @BindView(R2.id.fail_layout)
    LinearLayout failLayout;
    @BindView(R2.id.tv_available_limit)
    TextView tvAvailableLimit;
    @BindView(R2.id.btn_success)
    Button btnSuccess;
    @BindView(R2.id.success_layout)
    LinearLayout successLayout;
    @BindView(R2.id.loading_layout)
    LinearLayout loadingLayout;

    private CreditLimit creditLimit;
    private boolean isPay;
    private int loadCount;

    private Subscription timerSub;
    private HljHttpSubscriber initSub;

    private final static int DELAY = 5;
    private final static int MAX_LOAD_COUNT = 20;

    public final static String ARG_IS_PAY = "is_pay";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSwipeBackEnable(false);
        setContentView(R.layout.activity_basic_authentication_result___pay);
        ButterKnife.bind(this);
        initValues();
        initLoad();
    }

    private void initValues() {
        isPay = getIntent().getBooleanExtra(ARG_IS_PAY, false);
        setOkButton(R.mipmap.icon_refresh_primary_44_44);
    }

    private void initLoad() {
        if (initSub == null || initSub.isUnsubscribed()) {
            loadCount++;
            loadingLayout.setVisibility(View.VISIBLE);
            successLayout.setVisibility(View.GONE);
            failLayout.setVisibility(View.GONE);
            initSub = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpResult<CreditLimit>>() {
                        @Override
                        public void onNext(HljHttpResult<CreditLimit> creditLimitHljHttpResult) {
                            creditLimit = creditLimitHljHttpResult.getData();
                            if (creditLimit == null) {
                                reload();
                                return;
                            }
                            HljHttpStatus hljHttpStatus = creditLimitHljHttpResult.getStatus();
                            if ((creditLimit.getTotalLimit() == 0 && !creditLimit
                                    .isRiskCheckNotReady()) || hljHttpStatus == null ||
                                    hljHttpStatus.getRetCode() != 0) {
                                //如果isRiskCheckNotReady=false则表明风控校验失败，不需要再轮询。
                                loadCount = !creditLimit.isRiskCheckNotReady() ? MAX_LOAD_COUNT :
                                        loadCount;
                                reload();
                                return;
                            }
                            loadingLayout.setVisibility(View.GONE);
                            successLayout.setVisibility(View.VISIBLE);
                            tvAvailableLimit.setText(CommonUtil.fromHtml(
                                    BasicAuthenticationResultActivity.this,
                                    getString(R.string.html_available_limit___pay,
                                            CommonUtil.formatDouble2StringWithTwoFloat
                                                    (creditLimit.getAvailableLimit()))));
                            btnSuccess.setText(isPay ? R.string.btn_continue_pay___pay : R.string
                                    .btn_finished___pay);
                        }
                    })
                    .setOnErrorListener(new SubscriberOnErrorListener() {
                        @Override
                        public void onError(Object o) {
                            reload();
                        }
                    })
                    .build();
            XiaoxiInstallmentApi.getCreditLimitObb(this)
                    .subscribe(initSub);
        }
    }

    private void reload() {
        if (loadCount == MAX_LOAD_COUNT) {
            loadingLayout.setVisibility(View.GONE);
            failLayout.setVisibility(View.VISIBLE);
            return;
        }
        if (timerSub == null || timerSub.isUnsubscribed()) {
            timerSub = Observable.timer(DELAY, TimeUnit.SECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Long>() {
                        @Override
                        public void call(Long aLong) {
                            initLoad();
                        }
                    });
        }
    }

    @Override
    public void onOkButtonClick() {
        if (CommonUtil.isUnsubscribed(initSub, timerSub)) {
            loadCount = 0;
            initLoad();
        }
    }

    @OnClick(R2.id.btn_fail)
    void onFail() {
        onBackPressed();
    }

    @OnClick(R2.id.btn_success)
    void onSuccess() {
        if (isPay) {
            finish();
            RxBus.getDefault()
                    .post(new PayRxEvent(PayRxEvent.RxEventType.LIMIT_CONTINUE_PAY, null));
        } else {
            startActivity(new Intent(this, MyInstallmentActivity.class));
            finish();
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        RxBus.getDefault()
                .post(new PayRxEvent(PayRxEvent.RxEventType.INIT_LIMIT_CLOSE, creditLimit));
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(timerSub, initSub);
    }
}