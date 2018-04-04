package com.hunliji.marrybiz.view.event;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.suncloud.hljweblibrary.HljWeb;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljcommonlibrary.views.widgets.PullToRefreshScrollableLayout;
import com.hunliji.hljcommonlibrary.views.widgets.ScrollableHelper;
import com.hunliji.hljcommonlibrary.views.widgets.ScrollableLayout;
import com.hunliji.hljcommonlibrary.views.widgets.TabPageIndicator;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnCompletedListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.api.event.EventApi;
import com.hunliji.marrybiz.fragment.event.ApplyRecordListFragment;
import com.hunliji.marrybiz.fragment.event.OnlineEventListFragment;
import com.hunliji.marrybiz.fragment.event.StayOnlineEventListFragment;
import com.hunliji.marrybiz.model.event.EventWallet;
import com.hunliji.marrybiz.task.OnHttpRequestListener;
import com.hunliji.marrybiz.util.LinkUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 我的活动
 * Created by chen_bin on 2016/9/6 0006.
 */
public class MyEventListActivity extends HljBaseActivity implements TabPageIndicator
        .OnTabChangeListener, PullToRefreshBase.OnRefreshListener<ScrollableLayout> {
    @BindView(R.id.scrollable_layout)
    PullToRefreshScrollableLayout scrollableLayout;
    @BindView(R.id.tv_point_count)
    TextView tvPointCount;
    @BindView(R.id.indicator)
    TabPageIndicator indicator;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.bottom_layout)
    LinearLayout bottomLayout;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.tv_coupon_count)
    TextView tvCouponCount;
    private SectionsPagerAdapter pagerAdapter;
    private OnlineEventListFragment onlineFragment;//已上线
    private StayOnlineEventListFragment stayOnlineFragment; //待上线
    private ApplyRecordListFragment applyRecordFragment; //申请记录
    private HljHttpSubscriber refreshSub;
    private EventWallet eventWallet;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_event_list);
        ButterKnife.bind(this);
        emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                onRefresh(null);
            }
        });
        indicator.setOnTabChangeListener(this);
        scrollableLayout.setOnRefreshListener(this);
        scrollableLayout.getRefreshableView()
                .addOnScrollListener(new ScrollableLayout.OnScrollListener() {
                    @Override
                    public void onScroll(int currentY, int maxY) {
                        if (scrollableLayout.getRefreshableView()
                                .getHelper()
                                .getScrollableView() == null) {
                            scrollableLayout.getRefreshableView()
                                    .getHelper()
                                    .setCurrentScrollableContainer(getCurrentScrollableContainer());
                        }
                    }
                });
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                indicator.setCurrentItem(position);
                scrollableLayout.getRefreshableView()
                        .getHelper()
                        .setCurrentScrollableContainer(getCurrentScrollableContainer());
            }
        });
        onRefresh(null);
    }

    @Override
    public void onRefresh(PullToRefreshBase<ScrollableLayout> refreshView) {
        if (refreshSub == null || refreshSub.isUnsubscribed()) {
            refreshSub = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<EventWallet>() {
                        @Override
                        public void onNext(EventWallet eventWallet) {
                            setEventWallet(eventWallet);
                        }
                    })
                    .setOnCompletedListener(new SubscriberOnCompletedListener() {
                        @Override
                        public void onCompleted() {
                            if (pagerAdapter == null) {
                                pagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager
                                        ());
                                viewPager.setAdapter(pagerAdapter);
                                viewPager.setOffscreenPageLimit(2);
                                indicator.setPagerAdapter(pagerAdapter);
                            } else {
                                if (onlineFragment != null) {
                                    onlineFragment.setEventWallet(eventWallet);
                                    if (viewPager.getCurrentItem() == 0) {
                                        onlineFragment.setShowProgressBar(false);
                                        onlineFragment.refresh();
                                    } else {
                                        onlineFragment.setNeedRefresh(true);
                                    }
                                }
                                if (stayOnlineFragment != null) {
                                    if (viewPager.getCurrentItem() == 1) {
                                        stayOnlineFragment.setShowProgressBar(false);
                                        stayOnlineFragment.refresh();
                                    } else {
                                        stayOnlineFragment.setNeedRefresh(true);
                                    }
                                }
                                if (applyRecordFragment != null) {
                                    if (viewPager.getCurrentItem() == 2) {
                                        applyRecordFragment.setShowProgressBar(false);
                                        applyRecordFragment.refresh();
                                    } else {
                                        applyRecordFragment.setNeedRefresh(true);
                                    }
                                }
                            }
                        }
                    })
                    .setEmptyView(emptyView)
                    .setContentView(scrollableLayout)
                    .setPullToRefreshBase(scrollableLayout)
                    .setProgressBar(scrollableLayout.isRefreshing() ? null : progressBar)
                    .build();
            EventApi.getEventWallet()
                    .subscribe(refreshSub);
        }
    }

    private void setEventWallet(EventWallet eventWallet) {
        this.eventWallet = eventWallet;
        bottomLayout.setVisibility(View.VISIBLE);
        scrollableLayout.getRefreshableView()
                .setVisibility(View.VISIBLE);
        tvPointCount.setText(String.valueOf(eventWallet.getPoints()));
        tvCouponCount.setText(String.valueOf(eventWallet.getTickets()));
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    if (onlineFragment == null) {
                        onlineFragment = OnlineEventListFragment.newInstance(eventWallet);
                    }
                    return onlineFragment;
                case 1:
                    if (stayOnlineFragment == null) {
                        stayOnlineFragment = StayOnlineEventListFragment.newInstance();
                    }
                    return stayOnlineFragment;
                case 2:
                    if (applyRecordFragment == null) {
                        applyRecordFragment = ApplyRecordListFragment.newInstance();
                    }
                    return applyRecordFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.label_online);
                case 1:
                    return getString(R.string.label_stay_online);
                case 2:
                    return getString(R.string.label_apply_record);
                default:
                    return null;
            }
        }
    }

    //活动点说明
    @OnClick(R.id.btn_point_explain)
    public void onPointExplain() {
        progressBar.setVisibility(View.VISIBLE);
        LinkUtil.getInstance(this)
                .getLink(Constants.LinkNames.FINDER_ACTIVITY_POINT_INSTRUCTIONS,
                        new OnHttpRequestListener() {
                            @Override
                            public void onRequestCompleted(Object obj) {
                                if (!isFinishing()) {
                                    progressBar.setVisibility(View.GONE);
                                    HljWeb.startWebView(MyEventListActivity.this, (String) obj);
                                }
                            }

                            @Override
                            public void onRequestFailed(Object obj) {
                                if (!isFinishing()) {
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        });
    }

    @OnClick(R.id.btn_coupon_explain)
    public void onCouponExplain() {
        DialogUtil.createSingleButtonDialog(this,
                getString(R.string.label_coupon_explain),
                getString(R.string.label_know_it),
                null)
                .show();
    }

    //查看冲扣流水
    @OnClick(R.id.rl_see_recharge_record)
    public void onSeeRechargeRecord() {
        startActivity(new Intent(this, PunchWaterActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    //活动码验证
    @OnClick(R.id.btn_check_valid_code)
    public void onCheckValidCode() {
        startActivity(new Intent(this, CheckValidCodeActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    //申请新活动
    @OnClick(R.id.btn_apply_event)
    public void onApplyEvent() {
        startActivityForResult(new Intent(this, ApplyEventActivity.class),
                Constants.RequestCode.APPLY_EVENT);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    @Override
    public void onTabChanged(int position) {
        viewPager.setCurrentItem(position);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.RequestCode.APPLY_EVENT:
                    if (applyRecordFragment != null) {
                        if (viewPager.getCurrentItem() == 2) {
                            applyRecordFragment.refresh();
                        } else {
                            applyRecordFragment.setNeedRefresh(true);
                        }
                    }
                    break;
                case Constants.RequestCode.PAY:
                    onRefresh(null);
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //获取当前fragment
    private ScrollableHelper.ScrollableContainer getCurrentScrollableContainer() {
        if (viewPager.getAdapter() != null && viewPager.getAdapter() instanceof
                SectionsPagerAdapter) {
            SectionsPagerAdapter adapter = (SectionsPagerAdapter) viewPager.getAdapter();
            Fragment fragment = (Fragment) adapter.instantiateItem(viewPager,
                    viewPager.getCurrentItem());
            if (fragment != null && fragment instanceof ScrollableHelper.ScrollableContainer) {
                return (ScrollableHelper.ScrollableContainer) fragment;
            }
        }
        return null;
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(refreshSub);
    }
}