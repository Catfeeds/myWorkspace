package com.hunliji.marrybiz.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.util.Util;
import com.hunliji.marrybiz.view.revenue.RevenueManageActivity;

import org.joda.time.DateTime;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AfterWithdrawedActivity extends HljBaseActivity {

    @BindView(R.id.tv_withdraw_amount)
    TextView tvWithdrawAmount;
    @BindView(R.id.tv_withdraw_time)
    TextView tvWithdrawTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_withdrawed);
        ButterKnife.bind(this);

        hideBackButton();

        double amount = getIntent().getDoubleExtra("amount", 0);
        tvWithdrawAmount.setText(getString(R.string.label_price, Util.formatDouble2String(amount)));
        DateTime now = new DateTime();
        tvWithdrawTime.setText(now.toString(getString(R.string.format_date_type11)));
    }

    @OnClick(R.id.btn_finish)
    void onFinishBtn() {
        Intent intent = new Intent(this, RevenueManageActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        onFinishBtn();
    }
}
