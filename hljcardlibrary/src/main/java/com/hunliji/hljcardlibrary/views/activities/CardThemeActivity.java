package com.hunliji.hljcardlibrary.views.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hunliji.hljcardlibrary.HljCard;
import com.hunliji.hljcardlibrary.R;
import com.hunliji.hljcardlibrary.R2;
import com.hunliji.hljcommonlibrary.adapters.CommonPagerAdapter;
import com.hunliji.hljcardlibrary.api.CardApi;
import com.hunliji.hljcardlibrary.models.CardRxEvent;
import com.hunliji.hljcardlibrary.utils.PrivilegeUtil;
import com.hunliji.hljcardlibrary.views.fragments.CardThemeFragment;
import com.hunliji.hljcommonlibrary.models.Mark;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljcommonlibrary.views.widgets.TabPageIndicator;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscription;

/**
 * Created by hua_rong on 2017/6/21.
 * 选择模板
 */

public class CardThemeActivity extends HljBaseActivity implements TabPageIndicator
        .OnTabChangeListener {


    @BindView(R2.id.pager)
    ViewPager pager;
    @BindView(R2.id.tab_indicator)
    TabPageIndicator tabIndicator;
    @BindView(R2.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    private HljHttpSubscriber subscriber;
    private List<Fragment> fragmentList;
    private Subscription rxSubscription;
    private Subscription rxMemberSubscription;
    private boolean hasChecked; // 是否已经获得vip模板使用权限的结果
    private boolean isVipAvailable; // vip模板使用权限的结果
    private HljHttpSubscriber checkSub;

    @Override
    public String pageTrackTagName() {
        return "选择模板";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_theme_list___card);
        ButterKnife.bind(this);
        initValue();
        onLoad();
        initNetError();
        registerRxBusEvent();

        initTracker();
    }

    private void initValue() {
        if (HljCard.isCardMaster(this)) {
            User user = UserSession.getInstance()
                    .getUser(this);
            boolean login = user != null && user.getId() > 0;
            setSwipeBackEnable(login);
        }
    }

    private void initTracker() {

    }

    private void initNetError() {
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                onLoad();
            }
        });
    }

    private void onLoad() {
        if (subscriber == null || subscriber.isUnsubscribed()) {
            Observable<HljHttpData<List<Mark>>> observable = CardApi.getCardMarks();
            subscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<Mark>>>() {
                        @Override
                        public void onNext(HljHttpData<List<Mark>> hljHttpData) {
                            List<Mark> marks = hljHttpData.getData();
                            if (marks != null && !marks.isEmpty()) {
                                showView(marks);
                            }
                        }
                    })
                    .setEmptyView(emptyView)
                    .setProgressBar(progressBar)
                    .build();
            observable.subscribe(subscriber);
        }
        PrivilegeUtil.isVipAvailable(this, checkSub, new PrivilegeUtil.PrivilegeCallback() {
            @Override
            public void checkDone(boolean isAvailable) {
                hasChecked = true;
                isVipAvailable = isAvailable;
            }
        });
    }

    private void registerRxBusEvent() {
        rxSubscription = RxBus.getDefault()
                .toObservable(CardRxEvent.class)
                .subscribe(new RxBusSubscriber<CardRxEvent>() {
                    @Override
                    protected void onEvent(CardRxEvent cardRxEvent) {
                        switch (cardRxEvent.getType()) {
                            case CREATE_CARD:
                                finish();
                                break;
                            case CARD_APP_SHARE_SUCCESS:
                                refreshFragment();
                                break;
                        }
                    }
                });
        rxMemberSubscription = RxBus.getDefault()
                .toObservable(RxEvent.class)
                .subscribe(new RxBusSubscriber<RxEvent>() {
                    @Override
                    protected void onEvent(RxEvent rxEvent) {
                        switch (rxEvent.getType()) {
                            case OPEN_MEMBER_SUCCESS:
                                refreshFragment();
                                break;
                        }
                    }
                });
    }

    /**
     * 推荐 和 会员专享 放在本地显示  is_member:1 会员专享
     *
     * @param marks 标签
     */
    private void showView(List<Mark> marks) {
        fragmentList = new ArrayList<>();
        List<String> titleList = new ArrayList<>();
        titleList.add(getString(R.string.label_recommend___card));
        fragmentList.add(CardThemeFragment.newInstance(0, 0));
        if (HljCard.isCustomer(this) || HljCard.isCardMaster(this)) {
            titleList.add(getString(R.string.label_member_privilege___card));
            fragmentList.add(CardThemeFragment.newInstance(0, 1));
        }
        for (Mark mark : marks) {
            String title = mark.getName();
            if (!TextUtils.isEmpty(title)) {
                titleList.add(title);
                fragmentList.add(CardThemeFragment.newInstance(mark.getId(), 0));
            }
        }
        tabIndicator.setTabViewId(R.layout.menu_tab_view___card);
        CommonPagerAdapter pagerAdapter = new CommonPagerAdapter(getSupportFragmentManager(),
                fragmentList,
                titleList);
        pager.setAdapter(pagerAdapter);
        tabIndicator.setPagerAdapter(pagerAdapter);
        tabIndicator.setOnTabChangeListener(this);
        for (int i = 0; i < titleList.size(); i++) {
            View tabView = tabIndicator.getTabView(i);
            if (tabView != null) {
                TextView textView = (TextView) tabView.findViewById(R.id.title);
                if (textView != null) {
                    textView.getLayoutParams().width = (int) textView.getPaint()
                            .measureText(textView.getText()
                                    .toString()
                                    .trim()) + CommonUtil.dp2px(this, 24);
                }
            }
        }
        pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                tabIndicator.setCurrentItem(position);
                super.onPageSelected(position);
            }
        });
    }

    /**
     * 刷新所有的子fragment
     */
    public void refreshFragment() {
        if (fragmentList != null) {
            for (int i = 0; i < fragmentList.size(); i++) {
                Fragment fragment = fragmentList.get(i);
                if (fragment instanceof CardThemeFragment) {
                    CardThemeFragment cardThemeFragment = (CardThemeFragment) fragment;
                    cardThemeFragment.refresh();
                }
            }
        }
    }

    @Override
    public void onTabChanged(int position) {
        pager.setCurrentItem(position);
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(subscriber, rxSubscription, rxMemberSubscription, checkSub);
    }

    public boolean hasChecked() {
        return hasChecked;
    }

    public boolean isVipAvailable() {
        return isVipAvailable;
    }
}
