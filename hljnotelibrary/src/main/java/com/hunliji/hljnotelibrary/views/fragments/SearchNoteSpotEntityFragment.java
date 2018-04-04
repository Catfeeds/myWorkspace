package com.hunliji.hljnotelibrary.views.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.widgets.TabPageIndicator;
import com.hunliji.hljnotelibrary.R;
import com.hunliji.hljnotelibrary.R2;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by chen_bin on 2017/6/28 0028.
 */
public class SearchNoteSpotEntityFragment extends RefreshFragment implements TabPageIndicator
        .OnTabChangeListener {
    @BindView(R2.id.indicator)
    TabPageIndicator indicator;
    @BindView(R2.id.view_pager)
    ViewPager viewPager;
    private SearchMerchantListFragment searchMerchantListFragment;
    private SearchProductListFragment searchProductListFragment;
    private String keyword;
    private Unbinder unbinder;

    public static SearchNoteSpotEntityFragment newInstance(String keyword) {
        SearchNoteSpotEntityFragment fragment = new SearchNoteSpotEntityFragment();
        Bundle bundle = new Bundle();
        bundle.putString("keyword", keyword);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            keyword = getArguments().getString("keyword");
        }
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search_note_spot_entity___note,
                container,
                false);
        unbinder = ButterKnife.bind(this, rootView);
        initViews();
        return rootView;
    }


    private void initViews() {
        SectionsPagerAdapter pagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(1);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                indicator.setCurrentItem(position);
            }
        });
        indicator.setOnTabChangeListener(this);
        indicator.setTabViewId(R.layout.menu_tab_view_search___cm);
        indicator.setPagerAdapter(pagerAdapter);
        indicator.getTabView(1)
                .findViewById(R.id.divider)
                .setVisibility(View.GONE);
    }

    @Override
    public void refresh(Object... params) {
        if (viewPager == null) {
            return;
        }
        if (params != null && params.length > 0) {
            String keyword = (String) params[0];
            if (keyword != null && keyword.equals(this.keyword)) {
                return;
            }
            this.keyword = keyword;
            if (searchMerchantListFragment != null) {
                searchMerchantListFragment.refresh(keyword);
            }
            if (searchProductListFragment != null) {
                searchProductListFragment.refresh(keyword);
            }
        }
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    if (searchMerchantListFragment == null) {
                        searchMerchantListFragment = SearchMerchantListFragment.newInstance
                                (keyword);
                    }
                    return searchMerchantListFragment;
                case 1:
                    if (searchProductListFragment == null) {
                        searchProductListFragment = SearchProductListFragment.newInstance(keyword);
                    }
                    return searchProductListFragment;
                default:
                    return null;
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
                    return getString(R.string.label_merchant_count___note, 0);
                case 1:
                    return getString(R.string.label_shop_product_count___note, 0);
                default:
                    return null;
            }
        }
    }

    public void setMerchantCount(int merchantCount) {
        ((TextView) indicator.getTabView(0)
                .findViewById(R.id.title)).setText(getString(R.string.label_merchant_count___note,
                merchantCount));
    }

    public void setProductCount(int productCount) {
        ((TextView) indicator.getTabView(1)
                .findViewById(R.id.title)).setText(getString(R.string
                        .label_shop_product_count___note,
                productCount));
    }

    @Override
    public void onTabChanged(int position) {
        viewPager.setCurrentItem(position);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}