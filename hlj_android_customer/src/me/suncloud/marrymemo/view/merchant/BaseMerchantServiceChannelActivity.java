package me.suncloud.marrymemo.view.merchant;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hunliji.hljcommonlibrary.models.CategoryMark;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.models.modelwrappers.MerchantServiceFilter;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.fragments.ScrollAbleFragment;
import com.hunliji.hljcommonlibrary.views.widgets.TabPageIndicator;
import com.hunliji.hljcommonlibrary.views.widgets.TabView;
import com.hunliji.hljcommonviewlibrary.widgets.ServiceWorkFilterViewHolder;
import com.hunliji.hljhttplibrary.utils.AuthUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.fragment.work_case.BaseMerchantServiceWorkListFragment;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.util.NoticeUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.view.notification.MessageHomeActivity;
import rx.Subscription;

/**
 * 服务频道页
 * Created by chen_bin on 2017/8/2 0002.
 */
public abstract class BaseMerchantServiceChannelActivity extends HljBaseNoBarActivity implements
        TabPageIndicator.OnTabChangeListener {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.msg_notice_view)
    View msgNoticeView;
    @BindView(R.id.tv_msg_count)
    TextView tvMsgCount;
    @BindView(R.id.indicator)
    TabPageIndicator indicator;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.service_filter_bottom_layout)
    RelativeLayout serviceFilterBottomLayout;
    protected ServiceWorkFilterViewHolder serviceWorkFilterViewHolder;
    private NoticeUtil noticeUtil;
    protected ArrayList<CategoryMark> categoryMarks;
    protected SparseArray<ScrollAbleFragment> fragments;
    private MerchantServiceFilter serviceFilter;
    protected TabEnum tabEnum;
    private City city;
    private long cid;//地区选择的城市，与定位无关
    private int tabItemFixedWidth; //tab项固定的宽度
    private String sort = "score";
    private Subscription rxBusEventSub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_merchant_service_channel);
        ButterKnife.bind(this);
        setDefaultStatusBarPadding();
        initValues();
        initViews();
        registerRxBusEvent();
    }

    public void initValues() {
        tabEnum = TabEnum.SUB_TITLE;
        tabItemFixedWidth = CommonUtil.dp2px(this, 76);
        city = Session.getInstance()
                .getMyCity(this);
        cid = city.getId();
        categoryMarks = new ArrayList<>();
        fragments = new SparseArray<>();
        serviceFilter = new MerchantServiceFilter();
    }

    public void initViews() {
        tvTitle.setText(getTitle());
        indicator.setOnTabChangeListener(this);
        if (tabEnum == TabEnum.SUB_TITLE) {
            indicator.setTabViewId(R.layout.menu_merchant_service_channel_sub_title_tab_widget);
        } else if (tabEnum == TabEnum.ICON) {
            indicator.setTabViewId(R.layout.menu_merchant_service_channel_icon_tab_widget);
        }
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                indicator.setCurrentItem(position);
            }
        });
        noticeUtil = new NoticeUtil(this, tvMsgCount, msgNoticeView);
        noticeUtil.onResume();
        setFragments();
        setBottomView();
    }

    public void setFragments() {
        if (!CommonUtil.isCollectionEmpty(categoryMarks)) {
            SectionsPagerAdapter pagerAdapter = new SectionsPagerAdapter
                    (getSupportFragmentManager());
            viewPager.setAdapter(pagerAdapter);
            viewPager.setOffscreenPageLimit(categoryMarks.size() - 1);
            indicator.setPagerAdapter(pagerAdapter);
            for (int i = 0, size = pagerAdapter.getCount(); i < size; i++) {
                TabView tabView = (TabView) indicator.getTabView(i);
                if (tabView != null && tabView.getChildCount() > 0) {
                    View view = tabView.getChildAt(0);
                    if (size > 5) {
                        view.getLayoutParams().width = tabItemFixedWidth;
                    } else {
                        view.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                    }
                }
            }
            setTabAttributes();
        }
    }

    public void setTabAttributes() {
        for (int i = 0, size = categoryMarks.size(); i < size; i++) {
            TabView tabView = (TabView) indicator.getTabView(i);
            switch (tabEnum) {
                case SUB_TITLE:
                    TextView tvSubTitle = tabView.findViewById(R.id.sub_title);
                    tvSubTitle.setText(categoryMarks.get(i)
                            .getMark()
                            .getDescribe());
                    break;
                case ICON:
                    ImageView imgTabIcon = tabView.findViewById(R.id.icon);
                    String path = categoryMarks.get(i)
                            .getMark()
                            .getImagePath();
                    if (TextUtils.isEmpty(path)) {
                        return;
                    }
                    if (CommonUtil.isHttpUrl(path)) {
                        Glide.with(this)
                                .load(path)
                                .into(imgTabIcon);
                    } else {
                        imgTabIcon.setImageResource(Integer.parseInt(path));
                    }
                    break;
            }
        }
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            ScrollAbleFragment fragment = fragments.get(position);
            if (fragment == null) {
                fragment = getFragment(position);
                fragments.put(position, fragment);
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return categoryMarks.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return categoryMarks.get(position)
                    .getMark()
                    .getName();
        }
    }

    public void setBottomView() {
        serviceWorkFilterViewHolder = ServiceWorkFilterViewHolder.newInstance(this,
                cid,
                getPropertyId(),
                new ServiceWorkFilterViewHolder.OnFilterResultListener() {
                    @Override
                    public void onFilterResult(
                            String sort,
                            long cid,
                            long areaId,
                            double minPrice,
                            double maxPrice,
                            List<String> tags) {
                        BaseMerchantServiceChannelActivity.this.sort = sort;
                        BaseMerchantServiceChannelActivity.this.cid = cid;
                        serviceFilter.setShopAreaId(areaId);
                        serviceFilter.setPriceMin(minPrice);
                        serviceFilter.setPriceMax(maxPrice);
                        serviceFilter.setTags(tags);
                        onFilterRefresh();
                    }
                });
        serviceFilterBottomLayout.removeAllViews();
        serviceFilterBottomLayout.addView(serviceWorkFilterViewHolder.getRootView());
    }

    public void onFilterRefresh() {
        for (int i = 0, size = fragments.size(); i < size; i++) {
            ScrollAbleFragment fragment = fragments.get(i);
            if (fragment instanceof BaseMerchantServiceWorkListFragment) {
                BaseMerchantServiceWorkListFragment workListFragment =
                        (BaseMerchantServiceWorkListFragment) fragment;
                workListFragment.setFilterParams(cid, sort, serviceFilter);
                if (viewPager.getCurrentItem() == i) {
                    workListFragment.refresh();
                } else {
                    workListFragment.setNeedRefresh(true);
                }
            }
        }
    }

    @OnClick(R.id.btn_back)
    public void onBack() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (serviceWorkFilterViewHolder != null && serviceWorkFilterViewHolder.isShow()) {
            serviceWorkFilterViewHolder.hideFilterView();
            return;
        }
        super.onBackPressed();
    }

    @OnClick(R.id.msg_layout)
    public void onNewMsg() {
        if (AuthUtil.loginBindCheck(this)) {
            startActivity(new Intent(this, MessageHomeActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

    @Override
    public void onTabChanged(int position) {
        viewPager.setCurrentItem(position);
    }

    public abstract ScrollAbleFragment getFragment(int position);

    public abstract long getPropertyId();

    public enum TabEnum {
        ICON, SUB_TITLE
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (noticeUtil != null) {
            noticeUtil.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (noticeUtil != null) {
            noticeUtil.onPause();
        }
    }

    //注册RxEventBus监听
    private void registerRxBusEvent() {
        if (rxBusEventSub == null || rxBusEventSub.isUnsubscribed()) {
            rxBusEventSub = RxBus.getDefault()
                    .toObservable(RxEvent.class)
                    .subscribe(new RxBusSubscriber<RxEvent>() {
                        @Override
                        protected void onEvent(RxEvent rxEvent) {
                            switch (rxEvent.getType()) {
                                case CITY_CHANGE:
                                    City rxCity = (City) rxEvent.getObject();
                                    if (rxCity != null && !rxCity.getId()
                                            .equals(city.getId())) {
                                        city = rxCity;
                                        if (serviceWorkFilterViewHolder != null) {
                                            cid = city.getId();
                                            serviceFilter.setShopAreaId(0);
                                            serviceWorkFilterViewHolder.refreshArea(cid);
                                            onFilterRefresh();
                                        }
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                    });
        }
    }


    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(rxBusEventSub);
        if (serviceWorkFilterViewHolder != null) {
            serviceWorkFilterViewHolder.onDestroy();
        }
    }
}