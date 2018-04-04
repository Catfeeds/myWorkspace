package me.suncloud.marrymemo.fragment.finder;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.alibaba.android.arouter.launcher.ARouter;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.widgets.TabPageIndicator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.view.finder.FinderCaseMediaPagerActivity;

/**
 * Created by mo_yu on 2018/2/8.同主题案例列表
 */

public class SameCasesHomeFragment extends RefreshFragment implements TabPageIndicator
        .OnTabChangeListener {

    public static final String ARG_FINDER_CASE = "finder_case";
    public static final String ARG_ATTR_TYPE = "attr_type";
    public final static int TAB_SAME = 0; //相似案例tab
    public final static int TAB_HOT = 1; //热门案例tab
    @BindView(R.id.indicator)
    TabPageIndicator indicator;
    @BindView(R.id.btn_back)
    ImageButton btnBack;
    @BindView(R.id.action_layout)
    RelativeLayout actionLayout;
    @BindView(R.id.action_holder_layout)
    RelativeLayout actionHolderLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    Unbinder unbinder;
    private Work finderCase;
    private String attrType;
    private SparseArray<RefreshFragment> fragments;

    public static SameCasesHomeFragment newInstance(Work finderCase,String attrType) {
        Bundle args = new Bundle();
        SameCasesHomeFragment fragment = new SameCasesHomeFragment();
        args.putParcelable(ARG_FINDER_CASE, finderCase);
        args.putString(ARG_ATTR_TYPE,attrType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            finderCase = getArguments().getParcelable(ARG_FINDER_CASE);
            attrType = getArguments().getString(ARG_ATTR_TYPE);
        }
        fragments = new SparseArray<>();
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_same_cases_home, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void refresh(Object... params) {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    private void initViews() {
        SectionsPagerAdapter pagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                indicator.setCurrentItem(position);
            }
        });
        indicator.setTabViewId(R.layout.menu_primary_tab_widget);
        indicator.setOnTabChangeListener(this);
        indicator.setPagerAdapter(pagerAdapter);
    }

    @OnClick(R.id.btn_back)
    public void onBtnBack() {
        if (getActivity() instanceof FinderCaseMediaPagerActivity) {
            FinderCaseMediaPagerActivity activity = (FinderCaseMediaPagerActivity) getActivity();
            activity.hideSameFragment();
        }
    }

    private class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            RefreshFragment fragment = fragments.get(position);
            if (fragment != null) {
                return fragment;
            }
            switch (position) {
                case TAB_SAME:
                    fragment = SameCaseListFragment.newInstance(finderCase.getId(),
                            TAB_SAME,
                            attrType);
                    break;
                case TAB_HOT:
                    fragment = SameCaseListFragment.newInstance(finderCase.getMerchant()
                            .getId(), TAB_HOT, null);
                    break;
            }
            fragments.put(position, fragment);
            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case TAB_SAME:
                    return "同主题案例";
                default:
                    return "本店热门案例";
            }
        }
    }

    @Override
    public void onTabChanged(int position) {
        viewPager.setCurrentItem(position);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
