package com.hunliji.hljcardcustomerlibrary.views.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.hunliji.hljcardcustomerlibrary.R;
import com.hunliji.hljcardcustomerlibrary.R2;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.modules.services.SupportJumpService;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljkefulibrary.moudles.Support;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mo_yu on 2017/2/8.提现结果提示
 */

public class AfterWithdrawCashActivity extends HljBaseActivity {
    @BindView(R2.id.iv_withdraw_cash)
    ImageView ivWithdrawCash;
    @BindView(R2.id.tv_withdraw_cash)
    TextView tvWithdrawCash;
    @BindView(R2.id.tv_withdraw_cash_tip)
    TextView tvWithdrawCashTip;
    @BindView(R2.id.action_back)
    TextView actionBack;
    @BindView(R2.id.action_contact)
    TextView actionContact;
    private boolean isSuccess;
    private boolean isCash;//true为礼金
    private String errorMsg;//体现错误信息

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_withdraw_cash);
        ButterKnife.bind(this);
        initValue();
        initView();
    }

    private void initValue() {
        isSuccess = getIntent().getBooleanExtra("isSuccess", true);
        isCash = getIntent().getBooleanExtra("isCash", false);
        errorMsg = getIntent().getStringExtra("error_msg");
    }

    private void initView() {
        if (isSuccess) {
            setTitle(getString(R.string.label_withdraw_cash_success_title));
            ivWithdrawCash.setImageResource(R.mipmap.icon_check_round_green_202_202);
            tvWithdrawCash.setText(getString(R.string.label_withdraw_cash_success));
            tvWithdrawCashTip.setText(getString(isCash ? R.string.label_withdraw_cash_success_tip
                    : R.string.label_withdraw_gift_success_tip));
            actionContact.setVisibility(View.GONE);
        } else {
            setTitle(getString(R.string.label_withdraw_cash_failed_title));
            ivWithdrawCash.setImageResource(R.mipmap.icon_warnning_round_yellow_202_202);
            tvWithdrawCash.setText(getString(R.string.label_withdraw_cash_failed));
            if (TextUtils.isEmpty(errorMsg)) {
                tvWithdrawCashTip.setText(getString(R.string.label_withdraw_cash_failed_tip, ""));
            } else {
                tvWithdrawCashTip.setText(getString(R.string.label_withdraw_cash_failed_tip,
                        errorMsg));
            }
            actionContact.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R2.id.action_back)
    public void onActionBack() {
        super.onBackPressed();
    }

    @OnClick(R2.id.action_contact)
    public void onActionContact() {
        SupportJumpService supportJumpService = (SupportJumpService) ARouter.getInstance()
                .build(RouterPath.ServicePath.GO_TO_SUPPORT)
                .navigation();
        if (supportJumpService != null) {
            supportJumpService.gotoSupport(this, Support.SUPPORT_KIND_DEFAULT_ROBOT);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}