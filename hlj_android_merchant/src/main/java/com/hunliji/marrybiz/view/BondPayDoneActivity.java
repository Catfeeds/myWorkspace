package com.hunliji.marrybiz.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.util.Util;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Suncloud on 2015/12/15.
 */
public class BondPayDoneActivity extends HljBaseActivity {

    @BindView(R.id.tv_amount)
    TextView tvAmount;
    @BindView(R.id.btn_finish)
    Button btnFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bond_pay_done);
        ButterKnife.bind(this);
        setSwipeBackEnable(false);

        double fee = getIntent().getDoubleExtra("fee", 0);

        tvAmount.setText(Util.formatDouble2String(fee));
    }

    @OnClick(R.id.btn_finish)
    void onFinish(View view) {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("index", -1);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}
