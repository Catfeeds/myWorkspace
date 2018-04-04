package com.hunliji.marrybiz.fragment.comment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.widgets.TabPageIndicator;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.adapter.comment.CommentPagerAdapter;
import com.hunliji.marrybiz.model.Label;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by hua_rong on 2017/4/14.
 * 评论管理--评论列表
 */

public class CommentListFragment extends RefreshFragment implements TabPageIndicator
        .OnTabChangeListener {

    @BindView(R.id.indicator)
    TabPageIndicator tabPageIndicator;
    @BindView(R.id.viewpager)
    ViewPager viewpager;
    private Unbinder unbinder;

    public static CommentListFragment newInstance() {
        Bundle args = new Bundle();
        CommentListFragment fragment = new CommentListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comment_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }


    private void initView() {
        //0 全部 1差 2中 3好
        List<Label> titleLabels = new ArrayList<>();
        Label title = new Label(null);
        title.setId(0);
        title.setName(getString(R.string.label_all));
        titleLabels.add(title);

        Label title1 = new Label(null);
        title1.setId(3);
        title1.setName(getString(R.string.label_comment_praise));
        titleLabels.add(title1);

        Label title2 = new Label(null);
        title2.setId(2);
        title2.setName(getString(R.string.label_comment_average));
        titleLabels.add(title2);

        Label title3 = new Label(null);
        title3.setId(1);
        title3.setName(getString(R.string.label_comment_bad));
        titleLabels.add(title3);

        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(CommentItemFragment.newInstance(title.getId(), title.getName()));
        fragmentList.add(CommentItemFragment.newInstance(title1.getId(), title1.getName()));
        fragmentList.add(CommentItemFragment.newInstance(title2.getId(), title2.getName()));
        fragmentList.add(CommentItemFragment.newInstance(title3.getId(), title3.getName()));

        CommentPagerAdapter pagerAdapter = new CommentPagerAdapter(getChildFragmentManager(),
                fragmentList,
                titleLabels);

        tabPageIndicator.setPagerAdapter(pagerAdapter);
        viewpager.setAdapter(pagerAdapter);
        tabPageIndicator.setOnTabChangeListener(this);
        viewpager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                tabPageIndicator.setCurrentItem(position);
                super.onPageSelected(position);
            }
        });

    }

    @Override
    public void onTabChanged(int position) {
        viewpager.setCurrentItem(position);
    }

    @Override
    public void refresh(Object... params) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }


}
