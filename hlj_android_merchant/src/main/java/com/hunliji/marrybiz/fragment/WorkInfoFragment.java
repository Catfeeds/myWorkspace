package com.hunliji.marrybiz.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.Work;
import com.hunliji.marrybiz.widget.TabPageIndicator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Suncloud on 2016/1/11.
 */
public class WorkInfoFragment extends RefreshFragment implements TabPageIndicator
        .OnTabChangeListener {

    @BindView(R.id.indicator)
    TabPageIndicator indicator;
    @BindView(R.id.pager)
    ViewPager pager;
    private WorkHotRecommendFragment hotRecommendFragment;
    private WorkParametersFragment parametersFragment;
    private WorkImagesFragment imagesFragment;
    private Work work;
    private int position;
    private boolean isSnapshot;
    private Unbinder unbinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            work = (Work) getArguments().getSerializable("work");
            position = getArguments().getInt("position");
            isSnapshot = getArguments().getBoolean("isSnapshot");
        }
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_work_info, container, false);
        rootView.setPadding(0,
                CommonUtil.dp2px(getContext(),
                        45) + HljBaseActivity.getStatusBarHeight(getContext()),
                0,
                0);
        unbinder = ButterKnife.bind(this, rootView);
        indicator.setTabViewId(R.layout.menu_tab_widget3);
        InfoPagerAdapter adapter = new InfoPagerAdapter(getChildFragmentManager());
        indicator.setPagerAdapter(adapter);
        pager.setAdapter(adapter);
        pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                indicator.setCurrentItem(position);
            }
        });
        indicator.setOnTabChangeListener(this);
        if (position > 0) {
            pager.setCurrentItem(position, false);
        }
        return rootView;
    }

    @Override
    public void refresh(Object... params) {

    }

    @Override
    public void onTabChanged(int position) {
        pager.setCurrentItem(position);
    }

    public class InfoPagerAdapter extends FragmentPagerAdapter {

        public InfoPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    if (imagesFragment == null) {
                        imagesFragment = new WorkImagesFragment();
                        Bundle args = new Bundle();
                        args.putSerializable("work", work);
                        imagesFragment.setArguments(args);
                    }
                    return imagesFragment;
                case 1:
                    if (parametersFragment == null) {
                        parametersFragment = new WorkParametersFragment();
                        Bundle args = new Bundle();
                        args.putSerializable("work", work);
                        parametersFragment.setArguments(args);
                    }
                    return parametersFragment;
                default:
                    if (hotRecommendFragment == null) {
                        hotRecommendFragment = new WorkHotRecommendFragment();
                        Bundle args = new Bundle();
                        args.putSerializable("work", work);
                        hotRecommendFragment.setArguments(args);
                    }
                    return hotRecommendFragment;
            }
        }

        @Override
        public int getCount() {
            return 2 + (work != null && work.getMerchant() != null && work.getMerchant()
                    .getActiveWorkCount() > 1 && !isSnapshot ? 1 : 0);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.label_ticket_info_more);
                case 1:
                    return getString(R.string.label_work_parameter);
                default:
                    return getString(R.string.label_hot_recommend);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public boolean isTop() {
        switch (pager.getCurrentItem()) {
            case 0:
                return imagesFragment.isTop();
            case 1:
                return parametersFragment.isTop();
            default:
                return hotRecommendFragment.isTop();
        }
    }
}
