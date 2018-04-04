package com.hunliji.marrybiz.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.Label;
import com.hunliji.marrybiz.model.NewOrder;
import com.hunliji.marrybiz.widget.TabPageIndicator;

import java.util.ArrayList;

public class OnlineOrdersFragment extends RefreshFragment implements TabPageIndicator
        .OnTabChangeListener {

    private ArrayList<OnlineOrdersListFragment> fragmentList;
    private ViewPager viewPager;
    private TabPageIndicator tabPageIndicator;
    private ArrayList<Label> titleLabels;

    public OnlineOrdersFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_online_orders, container, false);

        tabPageIndicator = rootView.findViewById(R.id.tab_indicator);
        viewPager = rootView.findViewById(R.id.pager);
        initTabPager();

        return rootView;
    }

    private void initTabPager() {
        tabPageIndicator.setOnTabChangeListener(this);

        titleLabels = new ArrayList<>();
        Label title = new Label(null);
        title.setId(0);
        title.setName(getString(R.string.label_all));
        titleLabels.add(title);

        Label title1 = new Label(null);
        title1.setId(1);
        title1.setName(getString(R.string.label_wait_to_pay));
        titleLabels.add(title1);

        Label title2 = new Label(null);
        title2.setId(2);
        title2.setName(getString(R.string.label_wait_to_accept));
        titleLabels.add(title2);

        Label title3 = new Label(null);
        title3.setId(3);
        title3.setName(getString(R.string.label_wait_to_service2));
        titleLabels.add(title3);

        Label title4 = new Label(null);
        title4.setId(4);
        title4.setName(getString(R.string.label_wait_refund));
        titleLabels.add(title4);

        Label title6 = new Label(null);
        title6.setId(6);
        title6.setName(getString(R.string.label_finished));
        titleLabels.add(title6);

        Label title5 = new Label(null);
        title5.setId(5);
        title5.setName(getString(R.string.label_closed));
        titleLabels.add(title5);

        fragmentList = new ArrayList<>();
        fragmentList.add(OnlineOrdersListFragment.newInstance(-1, title.getName()));
        fragmentList.add(OnlineOrdersListFragment.newInstance(NewOrder
                        .STATUS_WAITING_FOR_THE_PAYMENT,
                title1.getName()));
        fragmentList.add(OnlineOrdersListFragment.newInstance(NewOrder
                        .STATUS_WAITING_FOR_ACCEPT_ORDER,
                title2.getName()));
        fragmentList.add(OnlineOrdersListFragment.newInstance(NewOrder.STATUS_MERCHANT_ACCEPT_ORDER,
                title3.getName()));
        // 退款单合并了四种状态的订单：20（退款审核中）,23(拒绝退款)，24（退款成功），15（拒绝接单）
        fragmentList.add(OnlineOrdersListFragment.newInstance(NewOrder.STATUS_REFUND_REVIEWING,
                title4.getName()));
        fragmentList.add(OnlineOrdersListFragment.newInstance(NewOrder.STATUS_SERVICE_COMPLETE,
                title6.getName()));
        fragmentList.add(OnlineOrdersListFragment.newInstance(NewOrder.STATUS_ORDER_CLOSED,
                title5.getName()));

        TabPagerAdapter tabPagerAdapter = new TabPagerAdapter(getChildFragmentManager());
        tabPageIndicator.setPagerAdapter(tabPagerAdapter);
        viewPager.setAdapter(tabPagerAdapter);

        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                tabPageIndicator.setCurrentItem(position);
                super.onPageSelected(position);
            }
        });
    }

    @Override
    public void refresh(Object... params) {

    }

    @Override
    public void onTabChanged(int position) {
        viewPager.setCurrentItem(position);
    }

    private class TabPagerAdapter extends FragmentPagerAdapter {

        public TabPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titleLabels.get(position)
                    .getName();
        }
    }

    public void acceptOrder() {
        viewPager.setCurrentItem(3);
    }

    public void rejectOrder() {
        viewPager.setCurrentItem(4);
    }

}
