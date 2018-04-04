package com.hunliji.hljcardcustomerlibrary.views.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.ProgressBar;

import com.hunliji.hljcardcustomerlibrary.R;
import com.hunliji.hljcardcustomerlibrary.R2;
import com.hunliji.hljcardcustomerlibrary.api.CustomerCardApi;
import com.hunliji.hljcardcustomerlibrary.models.UserBalanceDetail;
import com.hunliji.hljcardcustomerlibrary.models.WithdrawParam;
import com.hunliji.hljcardcustomerlibrary.views.fragments.WithdrawCashFragment;
import com.hunliji.hljcardcustomerlibrary.views.fragments.WithdrawGiftFragment;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.TabPageIndicator;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnCompletedListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mo_yu on 2017/2/8.旧版提现
 */
public class WithdrawActivity extends HljBaseActivity implements TabPageIndicator
        .OnTabChangeListener {
    public static final String ARG_PAGE_POSITION = "page_position";
    public static final String ARG_GIFT_BALANCE = "gift_balance";
    public static final String ARG_CASH_BALANCE = "cash_balance";
    public static final String ARG_SMS_CODE = "sms_code";

    @BindView(R2.id.indicator)
    TabPageIndicator indicator;
    @BindView(R2.id.view_pager)
    ViewPager viewPager;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    private int position;
    private String smsCode;
    private WithdrawParam withdrawParam;
    private WithdrawGiftFragment withdrawGiftFragment;
    private WithdrawCashFragment withdrawCashFragment;
    private UserBalanceDetail balanceDetail;
    private HljHttpSubscriber initSubscriber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideDividerView();
        setSwipeBackEnable(false);
        setContentView(R.layout.activity_withdraw);
        ButterKnife.bind(this);
        position = getIntent().getIntExtra(ARG_PAGE_POSITION, 0);
        smsCode = getIntent().getStringExtra(ARG_SMS_CODE);
        withdrawParam = getIntent().getParcelableExtra(WithdrawV2Activity.ARG_WITHDRAW_PARAM);
        indicator.setOnTabChangeListener(this);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                indicator.setCurrentItem(position);
            }
        });
        initLoad();
    }

    private void initLoad() {
        if (initSubscriber == null || initSubscriber.isUnsubscribed()) {
            initSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<UserBalanceDetail>() {
                        @Override
                        public void onNext(UserBalanceDetail userBalanceDetail) {
                            balanceDetail = userBalanceDetail;
                        }
                    })
                    .setOnCompletedListener(new SubscriberOnCompletedListener() {
                        @Override
                        public void onCompleted() {
                            SectionsPagerAdapter pagerAdapter = new SectionsPagerAdapter(
                                    getSupportFragmentManager());
                            indicator.setPagerAdapter(pagerAdapter);
                            viewPager.setAdapter(pagerAdapter);
                            viewPager.setCurrentItem(position, false);
                        }
                    })
                    .setOnErrorListener(new SubscriberOnErrorListener() {
                        @Override
                        public void onError(Object o) {
                            SectionsPagerAdapter pagerAdapter = new SectionsPagerAdapter(
                                    getSupportFragmentManager());
                            indicator.setPagerAdapter(pagerAdapter);
                            viewPager.setAdapter(pagerAdapter);
                        }
                    })
                    .setProgressBar(progressBar)
                    .build();
            CustomerCardApi.getUserBalanceDetailObb()
                    .subscribe(initSubscriber);
        }
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle args = new Bundle();
            args.putString(WithdrawActivity.ARG_SMS_CODE, smsCode);
            args.putParcelable(WithdrawV2Activity.ARG_WITHDRAW_PARAM, withdrawParam);
            switch (position) {
                case 0:
                    if (withdrawGiftFragment == null) {
                        args.putDouble(ARG_GIFT_BALANCE,
                                balanceDetail != null ? balanceDetail.getGiftBalance() : 0);
                        withdrawGiftFragment = WithdrawGiftFragment.newInstance(args);
                    }
                    return withdrawGiftFragment;
                default:
                    if (withdrawCashFragment == null) {
                        args.putDouble(ARG_CASH_BALANCE,
                                balanceDetail != null ? balanceDetail.getCashBalance() : 0);
                        withdrawCashFragment = WithdrawCashFragment.newInstance(args);
                    }
                    return withdrawCashFragment;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.label_withdraw_gift_tab);
                default:
                    return getString(R.string.label_withdraw_cash_tab);
            }
        }
    }

    @Override
    public void onTabChanged(int position) {
        viewPager.setCurrentItem(position);
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(initSubscriber);
    }
}
