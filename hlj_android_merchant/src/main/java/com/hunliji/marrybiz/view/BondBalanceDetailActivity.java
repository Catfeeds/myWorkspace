package com.hunliji.marrybiz.view;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.revenue.BondBalanceDetail;
import com.hunliji.marrybiz.util.Util;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BondBalanceDetailActivity extends HljBaseActivity {


    @BindView(R.id.tv_detail_type)
    TextView tvDetailType;
    @BindView(R.id.tv_money1)
    TextView tvMoney1;
    @BindView(R.id.tv_money2)
    TextView tvMoney2;
    @BindView(R.id.tv_pay_method)
    TextView tvPayMethod;
    @BindView(R.id.tv_pay_method_layout)
    LinearLayout tvPayMethodLayout;
    @BindView(R.id.tv_pay_time)
    TextView tvPayTime;
    @BindView(R.id.pay_bond_time_layout)
    LinearLayout payBondTimeLayout;
    @BindView(R.id.tv_deal_no)
    TextView tvDealNo;
    @BindView(R.id.deal_no_layout)
    LinearLayout dealNoLayout;
    @BindView(R.id.tv_bond_balance)
    TextView tvBondBalance;
    @BindView(R.id.bond_balance_layout)
    LinearLayout bondBalanceLayout;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.tv_pay_label)
    TextView tvPayLabel;
    @BindView(R.id.tv_pay_time_label)
    TextView tvPayTimeLabel;
    private BondBalanceDetail detail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bond_balance_detail);
        ButterKnife.bind(this);

        detail = (BondBalanceDetail) getIntent().getSerializableExtra("detail");
        if (detail != null) {
            tvDetailType.setText(detail.getType() != 3 ? R.string.label_pay_bond_fee2 : R.string
                    .label_withhold_money);

            tvPayTimeLabel.setText(detail.getType() != 3 ? R.string.label_pay_bond_time : R
                    .string.label_withhold_time);
            tvPayLabel.setText(detail.getType() != 3 ? R.string.label_pay_bond_method : R.string
                    .label_withhold_method);

            tvPayMethod.setText(detail.getMemo());
            tvMoney1.setText(Util.getIntegerPartFromDouble(detail.getAmount()));
            tvMoney2.setText(Util.getFloatPartFromDouble(detail.getAmount()));

            tvBondBalance.setText(Util.formatDouble2String(detail.getBalance()));
            tvPayTime.setText(detail.getDateTime()
                    .toString(getString(R.string.format_date_type11)));
            tvDealNo.setText(detail.getTitle());
        }
    }

}
