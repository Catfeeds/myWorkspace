package com.hunliji.hljcardcustomerlibrary.views.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hunliji.hljcardcustomerlibrary.R;
import com.hunliji.hljcardcustomerlibrary.R2;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mo_yu on 2017/11/24.转入转出理财成功结果提示
 */

public class AfterRollInOutActivity extends HljBaseActivity {

    public static final String ARG_TITLE = "title";
    public static final String ARG_AMOUNT = "amount";
    public static final String ARG_MSG = "msg";

    @BindView(R2.id.tv_notice_msg)
    TextView tvNoticeMsg;
    @BindView(R2.id.tv_roll_in_time_tip)
    TextView tvRollInTimeTip;
    @BindView(R2.id.action_confirm)
    TextView actionConfirm;
    @BindView(R2.id.tv_amount)
    TextView tvAmount;
    @BindView(R2.id.content_layout)
    LinearLayout contentLayout;
    private String title;//标题
    private String msg;//预计信息
    private double amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_roll_in);
        ButterKnife.bind(this);
        initValue();
        initView();
    }

    private void initValue() {
        title = getIntent().getStringExtra(ARG_TITLE);
        msg = getIntent().getStringExtra(ARG_MSG);
        amount = getIntent().getDoubleExtra(ARG_AMOUNT, 0);
    }

    private void initView() {
        if (!TextUtils.isEmpty(title)) {
            setTitle(title);
            tvNoticeMsg.setText(title);
        }
        hideBackButton();
        tvAmount.setText(getString(R.string.label_price___cm,
                CommonUtil.formatDouble2String(amount)));
        tvRollInTimeTip.setText(msg);
    }

    @OnClick(R2.id.action_confirm)
    public void onActionConfirm() {
        super.onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}