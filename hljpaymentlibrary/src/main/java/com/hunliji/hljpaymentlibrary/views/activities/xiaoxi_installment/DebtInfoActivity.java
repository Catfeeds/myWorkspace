package com.hunliji.hljpaymentlibrary.views.activities.xiaoxi_installment;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljpaymentlibrary.R;
import com.hunliji.hljpaymentlibrary.R2;
import com.hunliji.hljpaymentlibrary.api.xiaoxi_installment.XiaoxiInstallmentApi;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.DebtInfo;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 借款使用申报
 * Created by jinxin on 2017/11/10 0010.
 */
public class DebtInfoActivity extends HljBaseActivity {

    @BindView(R2.id.tv_use_of_funds)
    TextView tvUseOfFunds;
    @BindView(R2.id.tv_repayment_ability)
    TextView tvRepaymentAbility;
    @BindView(R2.id.tv_litigation)
    TextView tvLitigation;
    @BindView(R2.id.tv_situation_status)
    TextView tvSituationStatus;
    @BindView(R2.id.scroll_view)
    ScrollView scrollView;
    @BindView(R2.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;

    private Dialog useOfFundsDialog;
    private Dialog repaymentAbilityDialog;
    private Dialog litigationDialog;
    private Dialog situationStatusDialog;

    private List<String> useOfFounds;
    private List<String> repaymentAbilities;
    private List<String> litigations;
    private List<String> situationStatuss;

    private int useOfFundsIndex = -1;
    private int repaymentAbilityIndex = -1;
    private int litigationIndex = -1;
    private int situationStatusIndex = -1;

    private String assetOrderId;

    private HljHttpSubscriber initSub;
    private HljHttpSubscriber submitSub;

    public static final String ARG_ASSET_ORDER_ID = "asset_order_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debt_info___pay);
        ButterKnife.bind(this);
        initValues();
        initViews();
        initLoad();
    }

    private void initValues() {
        assetOrderId = getIntent().getStringExtra(ARG_ASSET_ORDER_ID);
        useOfFounds = Arrays.asList(getResources().getStringArray(R.array.useOfFunds));
        repaymentAbilities = Arrays.asList(getResources().getStringArray(R.array
                .repaymentAbilities));
        litigations = Arrays.asList(getResources().getStringArray(R.array.litigations));
        situationStatuss = Arrays.asList(getResources().getStringArray(R.array.situationStatuss));
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
        initSub = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<DebtInfo>() {
                    @Override
                    public void onNext(DebtInfo debtInfo) {
                        setDebtInfo(debtInfo);
                    }
                })
                .setEmptyView(emptyView)
                .setContentView(scrollView)
                .setProgressBar(progressBar)
                .build();
        XiaoxiInstallmentApi.getDebtInfoObb(assetOrderId)
                .subscribe(initSub);
    }

    private void setDebtInfo(DebtInfo debtInfo) {
        useOfFundsIndex = debtInfo.getUseOfFunds() - 1;
        if (useOfFundsIndex < 0 || useOfFundsIndex >= useOfFounds.size()) {
            useOfFundsIndex = DebtInfo.DEFAULT_USE_OF_FUNDS - 1;
        }
        repaymentAbilityIndex = debtInfo.getRepaymentAbility() - 1;
        if (repaymentAbilityIndex < 0 || repaymentAbilityIndex >= repaymentAbilities.size()) {
            repaymentAbilityIndex = DebtInfo.DEFAULT_REPAYMENT_ABILITY - 1;
        }
        litigationIndex = debtInfo.getLitigation() - 1;
        if (litigationIndex < 0 || litigationIndex >= litigations.size()) {
            litigationIndex = DebtInfo.DEFAULT_LITIGATION - 1;
        }
        situationStatusIndex = debtInfo.getSituationStatus() - 1;
        if (situationStatusIndex < 0 || situationStatusIndex >= situationStatuss.size()) {
            situationStatusIndex = DebtInfo.DEFAULT_SITUATION_STATUS - 1;
        }
        tvUseOfFunds.setText(useOfFounds.get(useOfFundsIndex));
        tvRepaymentAbility.setText(repaymentAbilities.get(repaymentAbilityIndex));
        tvLitigation.setText(litigations.get(litigationIndex));
        tvSituationStatus.setText(situationStatuss.get(situationStatusIndex));
    }

    @OnClick(R2.id.use_of_funds_layout)
    void onUseOfFunds() {
        if (useOfFundsDialog != null && useOfFundsDialog.isShowing()) {
            return;
        }
        if (useOfFundsDialog == null) {
            useOfFundsDialog = DialogUtil.createSingleWheelPickerDialog(this,
                    useOfFounds,
                    Math.max(0, useOfFundsIndex),
                    new DialogUtil.OnWheelSelectedListener() {
                        @Override
                        public void onWheelSelected(int position, String str) {
                            useOfFundsIndex = position;
                            tvUseOfFunds.setText(str);
                        }
                    });
        }
        useOfFundsDialog.show();
    }

    @OnClick(R2.id.repayment_ability_layout)
    void onRepaymentAbility() {
        if (repaymentAbilityDialog != null && repaymentAbilityDialog.isShowing()) {
            return;
        }
        if (repaymentAbilityDialog == null) {
            repaymentAbilityDialog = DialogUtil.createSingleWheelPickerDialog(this,
                    repaymentAbilities,
                    Math.max(0, repaymentAbilityIndex),
                    new DialogUtil.OnWheelSelectedListener() {

                        @Override
                        public void onWheelSelected(int position, String str) {
                            repaymentAbilityIndex = position;
                            tvRepaymentAbility.setText(str);
                        }
                    });
        }
        repaymentAbilityDialog.show();
    }

    @OnClick(R2.id.litigation_layout)
    void onLitigation() {
        if (litigationDialog != null && litigationDialog.isShowing()) {
            return;
        }
        if (litigationDialog == null) {
            litigationDialog = DialogUtil.createSingleWheelPickerDialog(this,
                    litigations,
                    Math.max(0, litigationIndex),
                    new DialogUtil.OnWheelSelectedListener() {

                        @Override
                        public void onWheelSelected(int position, String str) {
                            litigationIndex = position;
                            tvLitigation.setText(str);
                        }
                    });
        }
        litigationDialog.show();
    }

    @OnClick(R2.id.situation_status_layout)
    void onSituationStatus() {
        if (situationStatusDialog != null && situationStatusDialog.isShowing()) {
            return;
        }
        if (situationStatusDialog == null) {
            situationStatusDialog = DialogUtil.createSingleWheelPickerDialog(this,
                    situationStatuss,
                    Math.max(0, situationStatusIndex),
                    new DialogUtil.OnWheelSelectedListener() {

                        @Override
                        public void onWheelSelected(int position, String str) {
                            situationStatusIndex = position;
                            tvSituationStatus.setText(str);
                        }
                    });
        }
        situationStatusDialog.show();
    }

    @OnClick(R2.id.btn_submit)
    void onSubmit() {
        int useOfFunds = useOfFundsIndex == DebtInfo.DEFAULT_USE_OF_FUNDS - 1 ? 0 :
                useOfFundsIndex + 1;
        int repaymentAbility = repaymentAbilityIndex + 1;
        int litigation = litigationIndex + 1;
        int situationStatus = situationStatusIndex + 1;
        DebtInfo debtInfo = new DebtInfo();
        debtInfo.setAssetOrderId(assetOrderId);
        debtInfo.setUseOfFunds(useOfFunds);
        debtInfo.setRepaymentAbility(repaymentAbility);
        debtInfo.setLitigation(litigation);
        debtInfo.setSituationStatus(situationStatus);
        CommonUtil.unSubscribeSubs(submitSub);
        submitSub = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener() {
                    @Override
                    public void onNext(Object o) {
                        ToastUtil.showCustomToast(DebtInfoActivity.this,
                                R.string.msg_submit_success___pay);
                        onBackPressed();
                    }
                })
                .setDataNullable(true)
                .build();
        XiaoxiInstallmentApi.submitDebtInfoObb(debtInfo)
                .subscribe(submitSub);
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(initSub, submitSub);
    }
}
