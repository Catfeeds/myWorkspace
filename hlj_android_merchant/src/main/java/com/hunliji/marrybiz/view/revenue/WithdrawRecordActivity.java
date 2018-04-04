package com.hunliji.marrybiz.view.revenue;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RadioButton;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.fragment.RevenueDetailFragment;
import com.hunliji.marrybiz.model.revenue.RevenueDetail;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hua_rong on 2017/8/15 0015.
 * 提现记录
 */

public class WithdrawRecordActivity extends HljBaseActivity {

    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.rb_withdrawing)
    RadioButton rbWithdrawing;
    @BindView(R.id.rb_withdraw_success)
    RadioButton rbWithdrawSuccess;
    @BindView(R.id.rb_withdraw_fail)
    RadioButton rbWithdrawFail;

    private RevenueDetailFragment withdrawingFragment;
    private RevenueDetailFragment withdrawSuccessFragment;
    private RevenueDetailFragment withdrawFailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_record);
        ButterKnife.bind(this);

        SectionPagerAdapter sectionPagerAdapter = new SectionPagerAdapter
                (getSupportFragmentManager());
        viewPager.setAdapter(sectionPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 1:
                        rbWithdrawing.setChecked(false);
                        rbWithdrawSuccess.setChecked(true);
                        rbWithdrawFail.setChecked(false);
                        break;
                    case 2:
                        rbWithdrawing.setChecked(false);
                        rbWithdrawSuccess.setChecked(false);
                        rbWithdrawFail.setChecked(true);
                        break;
                    default:
                        rbWithdrawing.setChecked(true);
                        rbWithdrawSuccess.setChecked(false);
                        rbWithdrawFail.setChecked(false);
                        break;
                }
            }
        });
        RadioButtonCheckListener radioButtonCheckListener = new RadioButtonCheckListener();
        rbWithdrawing.setOnClickListener(radioButtonCheckListener);
        rbWithdrawSuccess.setOnClickListener(radioButtonCheckListener);
        rbWithdrawFail.setOnClickListener(radioButtonCheckListener);
    }

    private class SectionPagerAdapter extends FragmentPagerAdapter {

        SectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 1:
                    if (withdrawSuccessFragment == null) {
                        withdrawSuccessFragment = new RevenueDetailFragment();
                        Bundle bundle = new Bundle();
                        bundle.putInt(RevenueDetailFragment.ARG_NAME_TYPE,
                                RevenueDetail.TYPE_WITHDRAW_SUCCESS);
                        withdrawSuccessFragment.setArguments(bundle);
                    }
                    return withdrawSuccessFragment;
                case 2:
                    if (withdrawFailFragment == null) {
                        withdrawFailFragment = new RevenueDetailFragment();
                        Bundle bundle = new Bundle();
                        bundle.putInt(RevenueDetailFragment.ARG_NAME_TYPE,
                                RevenueDetail.TYPE_WITHDRAWING_FAIL);
                        withdrawFailFragment.setArguments(bundle);
                    }
                    return withdrawFailFragment;
                default:
                    if (withdrawingFragment == null) {
                        withdrawingFragment = new RevenueDetailFragment();
                        Bundle bundle = new Bundle();
                        bundle.putInt(RevenueDetailFragment.ARG_NAME_TYPE,
                                RevenueDetail.TYPE_WITHDRAWING);
                        withdrawingFragment.setArguments(bundle);
                    }
                    return withdrawingFragment;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 1:
                    return getString(R.string.label_withdraw_success).toUpperCase(l);
                case 2:
                    return getString(R.string.label_withdraw_fail).toUpperCase(l);
                default:
                    return getString(R.string.label_withdrawing).toUpperCase(l);
            }
        }
    }

    private class RadioButtonCheckListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.rb_withdrawing:
                    viewPager.setCurrentItem(0, true);
                    rbWithdrawing.setChecked(true);
                    rbWithdrawSuccess.setChecked(false);
                    rbWithdrawFail.setChecked(false);
                    break;
                case R.id.rb_withdraw_success:
                    viewPager.setCurrentItem(1, true);
                    rbWithdrawing.setChecked(false);
                    rbWithdrawSuccess.setChecked(true);
                    rbWithdrawFail.setChecked(false);
                    break;
                case R.id.rb_withdraw_fail:
                    viewPager.setCurrentItem(2, true);
                    rbWithdrawing.setChecked(false);
                    rbWithdrawSuccess.setChecked(false);
                    rbWithdrawFail.setChecked(true);
                    break;
            }
        }
    }

}
