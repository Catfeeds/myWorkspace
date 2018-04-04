package com.hunliji.marrybiz.view.revenue;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.suncloud.hljweblibrary.views.activities.HljWebViewActivity;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpStatus;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.api.revenue.RevenueApi;
import com.hunliji.marrybiz.model.MerchantUser;
import com.hunliji.marrybiz.model.revenue.Bank;
import com.hunliji.marrybiz.model.revenue.RevenueManager;
import com.hunliji.marrybiz.task.OnHttpRequestListener;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.LinkUtil;
import com.hunliji.marrybiz.util.Session;
import com.hunliji.marrybiz.util.Util;
import com.hunliji.marrybiz.view.ApplyWithdrawActivity;
import com.hunliji.marrybiz.view.RevenueActivity;
import com.hunliji.marrybiz.view.login.OpenShopScheduleActivity;
import com.hunliji.marrybiz.view.merchantservice.BondPlanDetailActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.functions.Func2;

/**
 * 收入管理
 * edited by hua_rong 2017-08-15
 */
public class RevenueManageActivity extends HljBaseActivity implements PullToRefreshScrollView
        .OnRefreshListener {


    @BindView(R.id.tv_alert_msg)
    TextView tvAlertMsg;
    @BindView(R.id.alert_layout)
    LinearLayout alertLayout;
    @BindView(R.id.tv_account_balance1)
    TextView tvAccountBalance1;
    @BindView(R.id.tv_account_balance2)
    TextView tvAccountBalance2;
    @BindView(R.id.scroll_view)
    PullToRefreshScrollView scrollView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    private Dialog certifyDialog;
    private HljHttpSubscriber refreshSubscriber;
    private HljHttpSubscriber subscriber;
    private RevenueManager revenue;
    private MerchantUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revenue_manage);
        ButterKnife.bind(this);
        user = Session.getInstance()
                .getCurrentUser(this);
        scrollView.setOnRefreshListener(this);
        onRefresh(scrollView);
        initError();
    }

    private void initError() {
        emptyView.setOnEmptyClickListener(new HljEmptyView.OnEmptyClickListener() {
            @Override
            public void onEmptyClickListener() {
                onRefresh(scrollView);
            }
        });
    }

    @Override
    protected void onResume() {
        onRefresh(null);
        super.onResume();
    }

    @Override
    public void onOkButtonClick() {
        Intent intent = new Intent(this, RevenueActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    @OnClick(R.id.alert_layout)
    void onAlert() {
        Intent intent = new Intent(this, BondPlanDetailActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    /**
     * 提现的判断条件流程
     * 实名认证审核通过——>是否绑定
     */
    @OnClick(R.id.btn_apply_withdraw)
    void applyWithdraw() {
        if (revenue == null) {
            return;
        }
        CommonUtil.unSubscribeSubs(subscriber);
        Observable<Bank> aObservable = RevenueApi.getAppBindBank();
        Observable<HljHttpStatus> bObservable = RevenueApi.getCanWithdraw();
        Observable<ResultZip> observable = Observable.zip(aObservable,
                bObservable,
                new Func2<Bank, HljHttpStatus, ResultZip>() {
                    @Override
                    public ResultZip call(Bank bank, HljHttpStatus httpStatus) {
                        return new ResultZip(bank, httpStatus);
                    }
                });
        subscriber = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<ResultZip>() {
                    @Override
                    public void onNext(ResultZip resultZip) {
                        Bank bank = resultZip.bank;
                        if (bank == null) {
                            bank = new Bank();
                            bank.setStatus(-1);
                        }
                        withDraw(bank, resultZip.httpStatus);
                    }
                })
                .setDataNullable(true)
                .setProgressBar(progressBar)
                .build();
        observable.subscribe(subscriber);
    }

    class ResultZip {

        Bank bank;
        HljHttpStatus httpStatus;

        ResultZip(Bank bank, HljHttpStatus httpStatus) {
            this.bank = bank;
            this.httpStatus = httpStatus;
        }
    }

    private void withDraw(Bank bank, HljHttpStatus httpStatus) {
        if (user.getKind() == 1 || user.getCertifyStatus() == 3) {
            //结算账户状态；-1未录入 0待审核 1通过 2失败
            final int status = bank.getStatus();
            String changeProtocol = bank.getChangeProtocol();
            String title = getString(R.string.label_not_complete_withdraw);
            String msgStr;
            String confirmMStr;
            if (status == 1) {
                if (httpStatus != null) {
                    if (httpStatus.getRetCode() == 0) {
                        Intent intent = new Intent(this, ApplyWithdrawActivity.class);
                        intent.putExtra("balance", revenue.getWithdrawAbility());
                        intent.putExtra("withdraw_account", revenue.getWithdrawAccount());
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    } else {
                        DialogUtil.createSingleButtonDialog(this,
                                "无法提现",
                                httpStatus.getMsg(),
                                "知道了",
                                null)
                                .show();
                    }
                }
            } else if (status == 0) {
                msgStr = getString(R.string.label_check_back_the_account);
                confirmMStr = getString(R.string.label_confirm);
                DialogUtil.createSingleButtonWithImageDialog(RevenueManageActivity.this,
                        R.drawable.icon_withdraw_fail_tip,
                        title,
                        msgStr,
                        confirmMStr,
                        null)
                        .show();
            } else {
                if (TextUtils.isEmpty(changeProtocol)) {
                    msgStr = getString(R.string.label_not_bound_present_account);
                    confirmMStr = getString(R.string.label_immediately_bind);
                    DialogUtil.createSingleButtonWithImageDialog(RevenueManageActivity.this,
                            R.drawable.icon_withdraw_fail_tip,
                            title,
                            msgStr,
                            confirmMStr,
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(RevenueManageActivity.this,
                                            BondAccountActivity.class);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.slide_in_right,
                                            R.anim.activity_anim_default);
                                }
                            })
                            .show();
                } else {
                    String reason = bank.getReason();
                    reason = TextUtils.isEmpty(reason) ? "原因：无" : "原因：" + reason;
                    DialogUtil.createSingleButtonDialog(this,
                            getString(R.string.hint_billing_account_modify_bond_failed),
                            getString(R.string.hint_billing_account_modify_bond_failed_reason,
                                    reason),
                            getString(R.string.label_confirm),
                            null)
                            .show();
                }
            }
        } else {//没有实名认证
            if (certifyDialog == null) {
                certifyDialog = DialogUtil.createDoubleButtonDialog(this,
                        getString(R.string.msg_withdraw_certify),
                        getString(R.string.label_certify3),
                        getString(R.string.label_cancel),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(RevenueManageActivity.this,
                                        OpenShopScheduleActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_right,
                                        R.anim.activity_anim_default);
                                certifyDialog.cancel();
                            }
                        },
                        null);
            }
            certifyDialog.show();
        }
    }

    //收支明细
    @OnClick(R.id.ll_revenue_detail)
    void goDetails() {
        if (revenue != null) {
            Intent intent = new Intent(this, RevenueDetailTabListActivity.class);
            intent.putExtra("withdraw_account", revenue.getWithdrawAccount());
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

    @OnClick(R.id.ll_question)
    void commonQuestions() {
        progressBar.setVisibility(View.VISIBLE);
        LinkUtil.getInstance(this)
                .getLink(Constants.LinkNames.MERCHANT_HELP_V2, new OnHttpRequestListener() {
                    @Override
                    public void onRequestCompleted(Object obj) {
                        progressBar.setVisibility(View.GONE);
                        String url = (String) obj;
                        if (!JSONUtil.isEmpty(url)) {
                            Intent intent = new Intent(RevenueManageActivity.this,
                                    HljWebViewActivity.class);
                            intent.putExtra("path", url);
                            intent.putExtra("title", getString(R.string.label_faq));
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right,
                                    R.anim.activity_anim_default);
                        }
                    }

                    @Override
                    public void onRequestFailed(Object obj) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    //提现记录
    @OnClick(R.id.ll_withdraw_record)
    void withdrawingFunds() {
        Intent intent = new Intent(this, WithdrawRecordActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        if (refreshSubscriber == null || refreshSubscriber.isUnsubscribed()) {
            boolean showProgressBar = scrollView.isRefreshing() || refreshView == null;
            Observable<RevenueManager> observable = RevenueApi.getwithdraw();
            refreshSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setContentView(scrollView)
                    .setEmptyView(emptyView)
                    .setProgressBar(showProgressBar ? null : progressBar)
                    .setPullToRefreshBase(scrollView)
                    .setOnNextListener(new SubscriberOnNextListener<RevenueManager>() {
                        @Override
                        public void onNext(RevenueManager revenue) {
                            RevenueManageActivity.this.revenue = revenue;
                            setNumbers();
                        }
                    })
                    .build();
            observable.subscribe(refreshSubscriber);
        }
    }


    private void setNumbers() {
        if (revenue != null) {
            tvAccountBalance1.setText(Util.getIntegerPartFromDouble(revenue.getWithdrawAbility()));
            tvAccountBalance2.setText(Util.getFloatPartFromDouble(revenue.getWithdrawAbility()));
            //            if (revenue.isHasOld()) {
            //                setOkText(R.string.label_see_old_revenue);
            //                showOkText();
            //            } else {
            //                hideOkText();
            //            }
            MerchantUser user = Session.getInstance()
                    .getCurrentUser(this);
            if (user.isBondPaid() && !user.isBondSign()) {
                // 余额不足超过限定期限,保证金权限过期
                alertLayout.setVisibility(View.VISIBLE);
                tvAlertMsg.setText(getString(R.string.msg_bond_fee_short_expire,
                        user.getBondMerchantExpireDays()));
            } else if (user.isBondPaid() && !revenue.isBondEnough()) {
                // 金额不足
                alertLayout.setVisibility(View.VISIBLE);
                tvAlertMsg.setText(R.string.msg_bond_fee_short);
            } else {
                alertLayout.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(refreshSubscriber, subscriber);
    }

}
