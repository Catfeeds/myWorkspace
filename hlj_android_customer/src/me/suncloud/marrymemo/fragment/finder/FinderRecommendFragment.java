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
import com.hunliji.hljcommonlibrary.views.widgets.TabPageIndicator;
import com.hunliji.hljcommonlibrary.views.widgets.TabView;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnCompletedListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljnotelibrary.models.NotebookType;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.finder.FinderApi;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.model.finder.FindTabConfig;
import me.suncloud.marrymemo.util.Session;

/**
 * 发现页-发现tab
 * Created by chen_bin on 2018/2/1 0001.
 */
public class FinderRecommendFragment extends RefreshFragment implements TabPageIndicator
        .OnTabChangeListener {

    @Override
    public String fragmentPageTrackTagName() {
        return "发现";
    }

    @BindView(R.id.indicator)
    TabPageIndicator indicator;
    @BindView(R.id.tab_layout)
    LinearLayout tabLayout;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.fragment_content)
    FrameLayout fragmentContent;
    @BindView(R.id.content_layout)
    LinearLayout contentLayout;

    private SparseArray<RefreshFragment> fragments;
    private City city;
    private int lastPosition = -1;

    private Unbinder unbinder;

    public final static String TAB_STR_CHOICE = "choice";
    public final static String TAB_STR_FAVOR = "favor";

    public static final String TITLE_CHOICE = "精选";
    public static final String TITLE_FAVOR = "好评";
    public static final String TITLE_WEDDING_PERSON = "婚礼人";
    public static final String TITLE_WEDDING_PHOTO = "婚纱照";
    public static final String TITLE_PRODUCT_PLAN = "婚品";
    public static final String TITLE_SUB_PAGE = "专栏";

    private ArrayList<String> tabTitles = new ArrayList<String>() {{
        add(TITLE_CHOICE);
        add(TITLE_FAVOR);
        add(TITLE_WEDDING_PERSON);
        add(TITLE_WEDDING_PHOTO);
        add(TITLE_PRODUCT_PLAN);
        add(TITLE_SUB_PAGE);
    }};
    private FindTabConfig findTabConfig;
    private HljHttpSubscriber tabConfSub;

    public static FinderRecommendFragment newInstance() {
        return new FinderRecommendFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_finder_recommend, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initLoad();
    }

    private void initLoad() {
        tabConfSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setContentView(contentLayout)
                .setProgressBar(progressBar)
                .setOnNextListener(new SubscriberOnNextListener<FindTabConfig>() {
                    @Override
                    public void onNext(FindTabConfig config) {
                        findTabConfig = config;
                        if (findTabConfig != null && findTabConfig.isShow()) {
                            tabTitles.add(findTabConfig.getIndex(), findTabConfig.getTitle());
                        }
                    }
                })
                .setOnCompletedListener(new SubscriberOnCompletedListener() {
                    @Override
                    public void onCompleted() {
                        contentLayout.setVisibility(View.VISIBLE);
                        initViews();
                        onTabChanged(0);
                    }
                })
                .setOnErrorListener(new SubscriberOnErrorListener() {
                    @Override
                    public void onError(Object o) {
                        contentLayout.setVisibility(View.VISIBLE);
                        initViews();
                        onTabChanged(0);
                    }
                })
                .build();
        FinderApi.getDefuConfig()
                .subscribe(tabConfSub);
    }

    private void initViews() {
        indicator.setOnTabChangeListener(this);
        indicator.setTabViewId(R.layout.menu_secondary_tab_widget);
        indicator.setPagerAdapter(tabTitles.toArray(new String[0]));
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

        for (int i = 0; i < indicator.getTabCount(); i++) {
            View tabView = indicator.getTabView(i);
            if (tabView != null) {
                TextView textView = tabView.findViewById(R.id.title);
                if (textView != null) {
                    textView.getLayoutParams().width = (int) textView.getPaint()
                            .measureText(textView.getText()
                                    .toString()
                                    .trim()) + CommonUtil.dp2px(getContext(), 24);
                }
            }
        }

        RefreshFragment fragment = fragments.get(position);
        if (fragment != null) {
            ft.show(fragment);
        } else {
            ft.add(R.id.fragment_content, getFragment(position));
        }

        ft.commitAllowingStateLoss();
    }

    private Fragment getFragment(int position) {
        RefreshFragment fragment = null;
        String title = tabTitles.get(position);
        switch (title) {
            case TITLE_CHOICE:
                fragment = FinderRecommendFeedsFragment.newInstance(TAB_STR_CHOICE);
                break;
            case TITLE_FAVOR:
                fragment = FinderRecommendFeedsFragment.newInstance(TAB_STR_FAVOR);
                break;
            case TITLE_WEDDING_PERSON:
                fragment = FinderNotesFragment.newInstance(NotebookType.TYPE_WEDDING_PERSON);
                break;
            case TITLE_WEDDING_PHOTO:
                fragment = FinderNotesFragment.newInstance(NotebookType.TYPE_WEDDING_PHOTO);
                break;
            case TITLE_PRODUCT_PLAN:
                fragment = FinderNotesFragment.newInstance(NotebookType.TYPE_PRODUCT_PLAN);
                break;
            case TITLE_SUB_PAGE:
                fragment = SubPageListFragment.newInstance();
                break;
            default:
                fragment = FinderDefuWebViewFragment.newInstance(findTabConfig.getUrl());
                break;
        }
        fragments.put(position, fragment);
        return fragment;
    }

    public void cityRefresh(City c) {
        if (lastPosition == -1) {
            return;
        }
        if (city == null || city.getId()
                .equals(c.getId())) {
            return;
        }
        city = c;
        RefreshFragment fragment = fragments.get(lastPosition);
        if (fragment == null) {
            return;
        }
        if (fragment instanceof FinderRecommendFeedsFragment) {
            ((FinderRecommendFeedsFragment) fragment).cityRefresh(c);
        } else if (fragment instanceof FinderNotesFragment) {
            ((FinderNotesFragment) fragment).cityRefresh(c);
        } else if (fragment instanceof SubPageListFragment) {
            ((SubPageListFragment) fragment).cityRefresh(c);
        }
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
        CommonUtil.unSubscribeSubs(tabConfSub);
    }
}
