package me.suncloud.marrymemo.fragment.community;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.widgets.FlowLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.fragment.HotThreadRankListFragment;

/**
 * Created by mo_yu on 2016/12/19.热帖排行榜
 */
public class HotThreadRankHomeFragment extends RefreshFragment implements FlowLayout
        .OnChildCheckedChangeListener {

    @BindView(R.id.flow_layout)
    FlowLayout flowLayout;
    @BindView(R.id.content_view)
    LinearLayout contentView;

    Unbinder unbinder;
    private String[] tabs = {"日榜", "周榜", "月榜"};
    private String[] rankTypes = {"last_day", "last_week", "last_month"};


    public static HotThreadRankHomeFragment newInstance() {
        Bundle args = new Bundle();
        HotThreadRankHomeFragment fragment = new HotThreadRankHomeFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_hot_thread_rank, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        flowLayout.removeAllViews();
        for (int i = 0, size = tabs.length; i < size; i++) {
            CheckBox checkBox = (CheckBox) getActivity().getLayoutInflater()
                    .inflate(R.layout.rank_tab_flow_item, flowLayout, false);
            checkBox.setText(tabs[i]);
            checkBox.setChecked(i == 0);
            flowLayout.addView(checkBox);
        }
        flowLayout.setOnChildCheckedChangeListener(this);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.content_view,
                HotThreadRankListFragment.newInstance(rankTypes[0]),
                "HotThreadRankListFragment");
        transaction.commit();
    }

    @Override
    public void refresh(Object... params) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onCheckedChange(View childView, int index) {
        FragmentManager fm = getChildFragmentManager();
        HotThreadRankListFragment hotThreadRankListFragment = (HotThreadRankListFragment) fm
                .findFragmentByTag(
                "HotThreadRankListFragment");
        hotThreadRankListFragment.refresh(rankTypes[index]);
    }
}