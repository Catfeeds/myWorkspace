package com.hunliji.marrybiz.view;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.widget.FrameLayout;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.fragment.RevenueDetailFragment;
import com.hunliji.marrybiz.model.revenue.RevenueDetail;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BondBalanceListActivity extends HljBaseActivity {

    @BindView(R.id.content_layout)
    FrameLayout contentLayout;
    private RevenueDetailFragment bondBalanceFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bond_balance_list);
        ButterKnife.bind(this);

        bondBalanceFragment = new RevenueDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(RevenueDetailFragment.ARG_NAME_TYPE, RevenueDetail.TYPE_BOND_BALANCE_DETAIL);
        bondBalanceFragment.setArguments(bundle);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.content_layout, bondBalanceFragment, "withdraw_fragment");
        ft.commit();
    }

}
