package com.hunliji.hljpaymentlibrary.views.activities.xiaoxi_installment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljpaymentlibrary.R;
import com.hunliji.hljpaymentlibrary.R2;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.XiaoxiInstallmentOrder;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 还款计划-更多信息
 * Created by chen_bin on 2017/11/9 0009.
 */
public class RepaymentScheduleExtInfoActivity extends HljBaseActivity {

    @BindView(R2.id.debt_info_layout)
    LinearLayout debtInfoLayout;
    @BindView(R2.id.debt_info_bottom_line)
    View debtInfoBottomLine;

    private String assetOrderId;
    private boolean isClear;

    public static final String ARG_ASSET_ORDER_ID = "asset_order_id";
    public static final String ARG_IS_CLEAR = "is_clear";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repayment_schedule_ext_info___pay);
        ButterKnife.bind(this);
        initValues();
    }

    private void initValues() {
        assetOrderId = getIntent().getStringExtra(ARG_ASSET_ORDER_ID);
        isClear = getIntent().getBooleanExtra(ARG_IS_CLEAR, false);
        if (isClear) { //当状态为已还清，隐藏借款使用申报。
            debtInfoLayout.setVisibility(View.GONE);
            debtInfoBottomLine.setVisibility(View.GONE);
        }
    }

    @OnClick(R2.id.basic_user_layout)
    void onBasicUserInfo() {
        Intent intent = new Intent(this, AddBasicUserInfoActivity.class);
        intent.putExtra(AddBasicUserInfoActivity.ARG_IS_EDIT, true);
        startActivity(intent);
    }

    //借款使用申报
    @OnClick(R2.id.debt_info_layout)
    void onDebtInfo() {
        Intent intent = new Intent(this, DebtInfoActivity.class);
        intent.putExtra(DebtInfoActivity.ARG_ASSET_ORDER_ID, assetOrderId);
        startActivity(intent);
    }

    //债权人列表
    @OnClick(R2.id.debts_layout)
    void onDebts() {
        Intent intent = new Intent(this, DebtListActivity.class);
        intent.putExtra(DebtListActivity.ARG_ASSET_ORDER_ID, assetOrderId);
        startActivity(intent);
    }
}