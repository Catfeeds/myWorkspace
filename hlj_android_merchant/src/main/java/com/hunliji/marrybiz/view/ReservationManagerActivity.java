package com.hunliji.marrybiz.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.TabPageIndicator;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.adapter.reservation.ReservationManagerAdapter;
import com.hunliji.marrybiz.fragment.ReservationManagerFragment;
import com.hunliji.marrybiz.model.Label;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * 预约管理
 * Created by jinxin on 2017/5/22 0022.
 */

public class ReservationManagerActivity extends HljBaseActivity implements TabPageIndicator
        .OnTabChangeListener {

    @BindView(R.id.tab_indicator)
    TabPageIndicator tabIndicator;
    @BindView(R.id.view_pager)
    ViewPager viewPager;

    private ReservationFragmentAdapter fragmentAdapter;
    private List<Label> labels;
    private List<ReservationManagerFragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_manager);
        ButterKnife.bind(this);
        initWidget();
    }

    private void initWidget() {
        hideDividerView();
        labels = new ArrayList<>();

        Label confirm = new Label(null);
        confirm.setName("待预约确认");
        confirm.setId(0);
        labels.add(confirm);
        Label list = new Label(null);
        list.setName("预约列表");
        list.setId(1);
        labels.add(list);
        Label history = new Label(null);
        history.setName("预约历史");
        history.setId(3);
        labels.add(history);

        fragments = new ArrayList<>();

        fragments.add(ReservationManagerFragment.newInstance(ReservationManagerAdapter
                .RESERVATION_CONFIRM));
        fragments.add(ReservationManagerFragment.newInstance(ReservationManagerAdapter
                .RESERVATION_LIST));
        fragments.add(ReservationManagerFragment.newInstance(ReservationManagerAdapter
                .RESERVATION_HISTORY));
        fragmentAdapter = new ReservationFragmentAdapter(getSupportFragmentManager());
        tabIndicator.setOnTabChangeListener(this);
        tabIndicator.setPagerAdapter(fragmentAdapter);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(fragmentAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                tabIndicator.setCurrentItem(position);
            }
        });
    }

    public void onRefresh() {
        for (ReservationManagerFragment fragment : fragments) {
            fragment.refresh();
        }
    }

    @Override
    public void onTabChanged(int position) {
        viewPager.setCurrentItem(position);
    }

    class ReservationFragmentAdapter extends FragmentStatePagerAdapter {

        public ReservationFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return labels.get(position)
                    .getName();
        }
    }
}
