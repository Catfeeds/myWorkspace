package me.suncloud.marrymemo.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hunliji.hljcommonlibrary.adapters.OnTabTextChangeListener;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.widgets.TabPageIndicator;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.RankListAdapter;
import me.suncloud.marrymemo.api.user.UserApi;
import me.suncloud.marrymemo.fragment.CarOrderListFragment;
import me.suncloud.marrymemo.fragment.ServiceOrdersFragment;
import me.suncloud.marrymemo.fragment.orders.HotelPeriodOrdersFragment;
import me.suncloud.marrymemo.fragment.orders.ProductOrdersFragment;
import me.suncloud.marrymemo.fragment.user.MyEventListFragment;
import me.suncloud.marrymemo.fragment.user.MyReservationListFragment;
import me.suncloud.marrymemo.model.Label;
import me.suncloud.marrymemo.model.MerchantProperty;
import me.suncloud.marrymemo.model.user.CountStatistics;
import me.suncloud.marrymemo.util.AnimUtil;
import me.suncloud.marrymemo.util.PropertiesUtil;

@Route(path = RouterPath.IntentPath.Customer.MyOrderListActivityPath.ORDER)
public class MyOrderListActivity extends HljBaseActivity implements TabPageIndicator
        .OnTabChangeListener {

    @BindView(R.id.indicator)
    TabPageIndicator indicator;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.menu_bg)
    RelativeLayout menuBg;
    @BindView(R.id.filter_list)
    ListView filterList;

    private SparseArray<RefreshFragment> fragments;
    private ArrayList<Label> properties;
    private boolean backMain;

    private HljHttpSubscriber getStatisticsSub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        fragments = new SparseArray<>();
        properties = new ArrayList<>();
        Label menuItem = new Label();
        menuItem.setName(getString(R.string.label_all_kind));
        properties.add(menuItem);
        properties.addAll(PropertiesUtil.getServerPropertiesFromFile(this));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order_list);
        ButterKnife.bind(this);
        backMain = getIntent().getBooleanExtra(RouterPath.IntentPath.Customer.MyOrder.ARG_BACK_MAIN,
                false);
        setSwipeBackEnable(!backMain);
        setOkTextSize(12);
        setOkText(R.string.label_filter___cv);
        setOkTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        hideOkText();
        hideDividerView();
        SectionsPagerAdapter pagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (position == RouterPath.IntentPath.Customer.MyOrder.Tab.RESERVATION) {
                    showOkText();
                } else {
                    hideOkText();
                }
                onHideMenu();
                indicator.setCurrentItem(position);
            }
        });
        indicator.setOnTabChangeListener(this);
        indicator.setPagerAdapter(pagerAdapter);
        int selectTab = getIntent().getIntExtra(RouterPath.IntentPath.Customer.MyOrder
                        .ARG_SELECT_TAB,
                RouterPath.IntentPath.Customer.MyOrder.Tab.SERVICE_ORDER);
        indicator.setCurrentItem(selectTab);
        viewPager.setCurrentItem(selectTab);
        filterList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AnimUtil.hideMenu2Animation(menuBg, filterList);
                ((RankListAdapter) parent.getAdapter()).setSelectedItem(position);
                Label menuItem = (Label) parent.getAdapter()
                        .getItem(position);
                if (menuItem == null) {
                    return;
                }
                if (fragments.get(RouterPath.IntentPath.Customer.MyOrder.Tab.RESERVATION) != null) {
                    fragments.get(RouterPath.IntentPath.Customer.MyOrder.Tab.RESERVATION)
                            .refresh(menuItem.getId());
                }
            }
        });
        new PropertiesUtil.PropertiesSyncTask(1, this, new PropertiesUtil.OnFinishedListener() {
            @Override
            public void onFinish(ArrayList<MerchantProperty> p) {
                properties = new ArrayList<>();
                Label menuItem = new Label();
                menuItem.setName(getString(R.string.label_all_kind));
                properties.add(menuItem);
                properties.addAll(p);
            }
        }).execute();
        getCountStatisticsData();
    }

    private void getCountStatisticsData() {
        if (getStatisticsSub == null || getStatisticsSub.isUnsubscribed()) {
            getStatisticsSub = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<CountStatistics>() {
                        @Override
                        public void onNext(CountStatistics statistics) {
                            setTabText(RouterPath.IntentPath.Customer.MyOrder.Tab.SERVICE_ORDER,
                                    statistics.getSevOrderCount());
                            setTabText(RouterPath.IntentPath.Customer.MyOrder.Tab.RESERVATION,
                                    statistics.getAppointmentCount());
                            setTabText(RouterPath.IntentPath.Customer.MyOrder.Tab.PRODUCT_ORDER,
                                    statistics.getShopOrderCount());
                            setTabText(RouterPath.IntentPath.Customer.MyOrder.Tab
                                            .HOTEL_PERIOD_ORDER,
                                    statistics.getHotelOrderCount());
                            setTabText(RouterPath.IntentPath.Customer.MyOrder.Tab.EVENT,
                                    statistics.getActivityCount());
                            setTabText(RouterPath.IntentPath.Customer.MyOrder.Tab.CAR_ORDER,
                                    statistics.getCarOrderCount());
                        }
                    })
                    .build();
            UserApi.getCountStatisticsObb(CountStatistics.TYPE_ORDER)
                    .subscribe(getStatisticsSub);
        }
    }

    @Override
    public void onOkButtonClick() {
        if (properties.isEmpty()) {
            return;
        }
        if (filterList.getAdapter() == null) {
            RankListAdapter rankListAdapter = new RankListAdapter(this, properties);
            filterList.setAdapter(rankListAdapter);
        }
        if (menuBg.getVisibility() == View.VISIBLE) {
            AnimUtil.hideMenu2Animation(menuBg, filterList);
        } else {
            AnimUtil.showMenu2Animation(menuBg, filterList);
        }
    }

    @OnClick(R.id.menu_bg)
    void onHideMenu() {
        if (menuBg.getVisibility() == View.VISIBLE) {
            AnimUtil.hideMenu2Animation(menuBg, filterList);
        }
    }

    @Override
    public void onBackPressed() {
        if (backMain) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("action", "myOrders");
            startActivity(intent);
        } else {
            super.onBackPressed();
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(final int position) {
            RefreshFragment fragment = fragments.get(position);
            if (fragment != null) {
                return fragment;
            }
            switch (position) {
                case RouterPath.IntentPath.Customer.MyOrder.Tab.SERVICE_ORDER:
                    fragment = ServiceOrdersFragment.newInstance();
                    break;
                case RouterPath.IntentPath.Customer.MyOrder.Tab.RESERVATION:
                    fragment = MyReservationListFragment.newInstance();
                    break;
                case RouterPath.IntentPath.Customer.MyOrder.Tab.PRODUCT_ORDER:
                    fragment = ProductOrdersFragment.newInstance();
                    break;
                case RouterPath.IntentPath.Customer.MyOrder.Tab.HOTEL_PERIOD_ORDER:
                    fragment = HotelPeriodOrdersFragment.newInstance();
                    break;
                case RouterPath.IntentPath.Customer.MyOrder.Tab.EVENT:
                    fragment = MyEventListFragment.newInstance();
                    break;
                case RouterPath.IntentPath.Customer.MyOrder.Tab.CAR_ORDER:
                    fragment = CarOrderListFragment.newInstance();
                    break;
            }
            if (fragment != null) {
                fragment.setOnTabTextChangeListener(new OnTabTextChangeListener() {
                    @Override
                    public void onTabTextChange(int totalCount) {
                        setTabText(position, totalCount);
                    }
                });
            }
            fragments.put(position, fragment);
            return fragment;
        }

        @Override
        public int getCount() {
            return 6;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getPageTitleStr(position, 0);
        }
    }

    @Override
    public void onTabChanged(int position) {
        viewPager.setCurrentItem(position);
    }

    private String getPageTitleStr(int position, int totalCount) {
        switch (position) {
            case RouterPath.IntentPath.Customer.MyOrder.Tab.SERVICE_ORDER:
                return getString(R.string.label_service_order_count, totalCount);
            case RouterPath.IntentPath.Customer.MyOrder.Tab.RESERVATION:
                return getString(R.string.label_reservation_count, totalCount);
            case RouterPath.IntentPath.Customer.MyOrder.Tab.PRODUCT_ORDER:
                return getString(R.string.label_product_order_count, totalCount);
            case RouterPath.IntentPath.Customer.MyOrder.Tab.HOTEL_PERIOD_ORDER:
                return getString(R.string.label_hotel_period_order_count, totalCount);
            case RouterPath.IntentPath.Customer.MyOrder.Tab.EVENT:
                return getString(R.string.label_event_count, totalCount);
            case RouterPath.IntentPath.Customer.MyOrder.Tab.CAR_ORDER:
                return getString(R.string.label_car_order_count, totalCount);
            default:
                return null;
        }
    }

    private void setTabText(int position, int totalCount) {
        indicator.setTabText(getPageTitleStr(position, totalCount), position);
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(getStatisticsSub);
    }
}
