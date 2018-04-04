package com.hunliji.marrybiz.adapter.comment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.hunliji.marrybiz.model.Label;

import java.util.List;

/**
 * Created by hua_rong on 2017/4/15.
 * 评论viewpager
 */

public class CommentPagerAdapter extends FragmentPagerAdapter {

    private FragmentManager fragmentManager;
    private List<Fragment> fragmentList;
    private List<Label> titleLabels;

    public CommentPagerAdapter(
            FragmentManager fragmentManager, List<Fragment> fragmentList, List<Label> titleLabels) {
        super(fragmentManager);
        this.fragmentManager = fragmentManager;
        this.fragmentList = fragmentList;
        this.titleLabels = titleLabels;
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
        return titleLabels.get(position)
                .getName();
    }

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
        Fragment fragment = fragmentList.get(position);
        fragmentManager.beginTransaction()
                .hide(fragment)
                .commitAllowingStateLoss();
    }
}
