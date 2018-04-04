package com.hunliji.hljcardcustomerlibrary.views.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.widget.ProgressBar;

import com.hunliji.hljcardcustomerlibrary.R;
import com.hunliji.hljcardcustomerlibrary.R2;
import com.hunliji.hljcardcustomerlibrary.views.fragments.WithdrawV2Fragment;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mo_yu on 2017/6/19.新版余额提现
 */

public class WithdrawV2Activity extends HljBaseActivity {

    public static final String ARG_WITHDRAW_PARAM = "withdraw_param";
    public static final String ARG_ID = "id";

    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hlj_common_fragment_content);
        ButterKnife.bind(this);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Bundle args = new Bundle();
        args.putParcelable(ARG_WITHDRAW_PARAM, getIntent().getParcelableExtra(ARG_WITHDRAW_PARAM));
        args.putLong(ARG_ID, getIntent().getLongExtra(ARG_ID, 0));
        transaction.add(R.id.fragment_content,
                WithdrawV2Fragment.newInstance(args),
                "WithdrawV2Fragment");
        transaction.commitAllowingStateLoss();
    }

    @Override
    protected void onFinish() {
        super.onFinish();
    }
}
