package me.suncloud.marrymemo.fragment.finder;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljcommonlibrary.views.widgets.TabPageIndicator;
import com.hunliji.hljcommonlibrary.views.widgets.TabView;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.finder.FinderApi;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.model.finder.CaseCategories;
import me.suncloud.marrymemo.util.Session;

/**
 * Created by mo_yu on 2018/2/7.发现页-案例
 */

public class FinderCaseHomeFragment extends RefreshFragment implements TabPageIndicator
        .OnTabChangeListener {

    @Override
    public String fragmentPageTrackTagName() {
        return "发现";
    }

    @BindView(R.id.indicator)
    TabPageIndicator indicator;
    @BindView(R.id.tab_layout)
    LinearLayout tabLayout;
    @BindView(R.id.fragment_content)
    FrameLayout fragmentContent;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.content_view)
    LinearLayout contentView;
    private Unbinder unbinder;

    private SparseArray<RefreshFragment> fragments;
    private int lastPosition = -1;
    private List<CaseCategories> caseCategories;
    private City city;
    private HljHttpSubscriber initSubscriber;

    public static FinderCaseHomeFragment newInstance() {
        Bundle args = new Bundle();
        FinderCaseHomeFragment fragment = new FinderCaseHomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragments = new SparseArray<>();
        city = Session.getInstance()
                .getMyCity(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_find_case_home, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initLoad();
    }

    private void initView() {
        indicator.setOnTabChangeListener(this);
        indicator.setTabViewId(R.layout.menu_secondary_tab_widget);
        emptyView.setOnEmptyClickListener(new HljEmptyView.OnEmptyClickListener() {
            @Override
            public void onEmptyClickListener() {
                initLoad();
            }
        });
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                initLoad();
            }
        });
    }

    private void initLoad() {
        if (initSubscriber == null || initSubscriber.isUnsubscribed()) {
            initSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<CaseCategories>>>() {

                        @Override
                        public void onNext(HljHttpData<List<CaseCategories>> listHljHttpData) {
                            caseCategories = listHljHttpData.getData();
                            fragments.clear();
                            if (!CommonUtil.isCollectionEmpty(caseCategories)) {
                                if (caseCategories.size() == 1) {
                                    tabLayout.setVisibility(View.GONE);
                                } else {
                                    tabLayout.setVisibility(View.VISIBLE);
                                }
                                String[] titles = new String[caseCategories.size()];
                                for (int i = 0; i < caseCategories.size(); i++) {
                                    titles[i] = caseCategories.get(i)
                                            .getName();
                                }
                                indicator.setPagerAdapter(titles);
                                if (titles.length > 5) {
                                    for (int i = 0; i < titles.length; i++) {
                                        TabView tabView = (TabView) indicator.getTabView(i);
                                        tabView.getLayoutParams().width = CommonUtil.dp2px(
                                                getContext(),
                                                80);
                                    }
                                }
                                lastPosition = -1;
                                indicator.setCurrentItem(0);
                                onTabChanged(0);
                            }
                        }
                    })
                    .setEmptyView(emptyView)
                    .setContentView(contentView)
                    .setProgressBar(progressBar)
                    .build();
            FinderApi.getCaseCategoriesObb()
                    .subscribe(initSubscriber);
        }
    }

    @Override
    public void onTabChanged(int position) {
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (lastPosition >= 0) {
            TabView lastTab = (TabView) indicator.getTabView(lastPosition);
            TextView lastTv = (TextView) lastTab.getChildAt(0);
            lastTv.getPaint()
                    .setFakeBoldText(false);
            Fragment lastFragment = fragments.get(lastPosition);
            if (lastFragment != null && !lastFragment.isHidden()) {
                ft.hide(lastFragment);
            }
        }
        lastPosition = position;
        TabView tab = (TabView) indicator.getTabView(position);
        TextView tv = (TextView) tab.getChildAt(0);
        tv.getPaint()
                .setFakeBoldText(true);
        Fragment fragment = fragments.get(position);
        if (fragment != null) {
            ft.show(fragment);
        } else {
            ft.add(R.id.fragment_content, getFragment(position));
        }
        ft.commitAllowingStateLoss();
    }

    private Fragment getFragment(int position) {
        RefreshFragment fragment = fragments.get(position);
        if (fragment == null) {
            fragment = FinderCaseListFragment.newInstance(caseCategories.get(position)
                    .getIds());
            fragments.put(position, fragment);
        }
        return fragment;
    }

    public void cityRefresh(City c) {
        if (city == null || city.getId()
                .equals(c.getId())) {
            return;
        }
        city = c;
        initLoad();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            cityRefresh(Session.getInstance()
                    .getMyCity(getContext()));
        }
    }

    @Override
    public void refresh(Object... params) {
        if (lastPosition == -1) {
            return;
        }
        RefreshFragment fragment = fragments.get(lastPosition);
        if (fragment != null) {
            fragment.refresh();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        CommonUtil.unSubscribeSubs(initSubscriber);
    }
}
