package com.hunliji.hljcommonlibrary.adapters;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by hua_rong on 2017/6/16.
 * 通用的tab-page-adapter
 */

public class CommonPagerAdapter extends FragmentPagerAdapter {

    private FragmentManager fragmentManager;
    private List<Fragment> fragmentList;
    private List<String> titles;

    public CommonPagerAdapter(
            FragmentManager fragmentManager, List<Fragment> fragmentList, List<String> titles) {
        super(fragmentManager);
        this.fragmentManager = fragmentManager;
        this.fragmentList = fragmentList;
        this.titles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList == null ? 0 : fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (titles == null || titles.isEmpty()) {
            return null;
        }
        return titles.get(position);
    }

    @NonNull
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = getItem(position);
        if (fragment.isAdded()) {
            fragmentManager.beginTransaction()
                    .show(fragment)
                    .commitAllowingStateLoss();
            return fragment;
        }
        return super.instantiateItem(container, position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Fragment fragment = getItem(position);
        fragmentManager.beginTransaction()
                .hide(fragment)
                .commitAllowingStateLoss();
    }

}
