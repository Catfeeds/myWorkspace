package com.hunliji.marrybiz.view.experience;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.View;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.TabPageIndicator;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.fragment.experience.AdvListFragment;
import com.hunliji.marrybiz.util.AdvStatusEnum;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mo_yu on 2017/12/19.体验店推荐、客资推荐列表
 */

public class AdvListActivity extends HljBaseActivity implements TabPageIndicator
        .OnTabChangeListener {

    public static final String ARG_ADV_TYPE = "arg_adv_type";
    public static final int ADV_FOR_EXPERIENCE = 0; // 体验店
    public static final int ADV_FOR_OTHERS = 1; // 其他的客资推荐，目前有来自婚宴的客资adv

    @BindView(R.id.indicator)
    TabPageIndicator indicator;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    private SparseArray<Fragment> fragments;

    private int advType;
    AdvStatusEnum[] statusEnum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experience_shop_list);
        ButterKnife.bind(this);
        initValue();
        initView();
    }

    private void initValue() {
        fragments = new SparseArray<>();
        advType = getIntent().getIntExtra(ARG_ADV_TYPE, 0);
        statusEnum = advType == AdvListActivity.ADV_FOR_EXPERIENCE ? AdvStatusEnum
                .exShopEnums : AdvStatusEnum.hotelEnums;
    }

    private void initView() {
        if (advType == ADV_FOR_EXPERIENCE) {
            setTitle("体验店推荐");
        } else {
            setTitle("客资推荐");
        }
        setOkButton(R.drawable.icon_search_primary_46_44);
        SectionsPagerAdapter pagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        indicator.setTabViewId(R.layout.menu_tab_view_with_dot___cm);
        indicator.setPagerAdapter(pagerAdapter);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(statusEnum.length - 1);
        indicator.setOnTabChangeListener(this);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                indicator.setCurrentItem(position);
            }
        });
    }

    @Override
    public void onOkButtonClick() {
        super.onOkButtonClick();
        Intent intent = new Intent(this, SearchAdvShopActivity.class);
        intent.putExtra(ARG_ADV_TYPE, advType);
        startActivity(intent);
    }

    public void showTabDotView(int position) {
        indicator.getTabView(position)
                .findViewById(R.id.dot_view)
                .setVisibility(View.VISIBLE);
    }

    public void hideTabDotView(int position) {
        indicator.getTabView(position)
                .findViewById(R.id.dot_view)
                .setVisibility(View.GONE);
    }

    @Override
    public void onTabChanged(int position) {
        viewPager.setCurrentItem(position);
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = fragments.get(position);
            if (fragment == null) {
                fragment = AdvListFragment.newInstance(statusEnum[position].getStatus(),
                        null,
                        statusEnum[position].getTab(),
                        advType);
                fragments.put(position, fragment);
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return statusEnum.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return statusEnum[position].getTabName();
        }
    }
}
