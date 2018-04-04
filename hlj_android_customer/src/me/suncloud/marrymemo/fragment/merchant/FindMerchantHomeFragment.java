package me.suncloud.marrymemo.fragment.merchant;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.CommonPagerAdapter;
import com.hunliji.hljcommonlibrary.models.search.NewHotKeyWord;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.SPUtils;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.widgets.TabPageIndicator;
import com.hunliji.hljhttplibrary.api.newsearch.NewSearchApi;
import com.hunliji.hljhttplibrary.utils.AuthUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljnotelibrary.views.activities.CreatePhotoNoteActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.fragment.finder.FinderCaseHomeFragment;
import me.suncloud.marrymemo.fragment.finder.FinderRecommendFragment;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.util.NoticeUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.newsearch.NewSearchActivity;
import me.suncloud.marrymemo.view.notification.MessageHomeActivity;

/**
 * Created by hua_rong on 2018/3/8
 * 主界面找商家tab_pager
 */
public class FindMerchantHomeFragment extends RefreshFragment implements TabPageIndicator
        .OnTabChangeListener {

    @BindView(R.id.indicator)
    TabPageIndicator indicator;
    @BindView(R.id.msg_notice)
    View msgNotice;
    @BindView(R.id.msg_count)
    TextView msgCount;
    @BindView(R.id.action_holder_layout)
    RelativeLayout actionHolderLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.msg_layout)
    RelativeLayout msgLayout;
    @BindView(R.id.btn_create_note)
    ImageButton btnCreateNote;

    private List<Fragment> fragments;
    private City city;

    private final static int TAB_FINDER = 0; //发现tab
    private final static int TAB_FIND_MERCHANT = 1; //找商家tab
    private NoticeUtil noticeUtil;
    public static final String ARG_CITY = "city";
    private Unbinder unbinder;
    private NewHotKeyWord hotKeyWord;
    private HljHttpSubscriber initSub;
    private static final String[] titles = {"发现", "找商家", "案例"};
    private boolean isShowFinder;
    public static final String ARG_IS_SHOW_FINDER = "is_show_finder";

    public static FindMerchantHomeFragment newInstance(boolean isShowFinder) {
        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_SHOW_FINDER, isShowFinder);
        FindMerchantHomeFragment fragment = new FindMerchantHomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initValue();
    }

    private void initValue() {
        fragments = new ArrayList<>();
        city = Session.getInstance()
                .getMyCity(getContext());
        if (getArguments() != null) {
            isShowFinder = getArguments().getBoolean(ARG_IS_SHOW_FINDER);
        }
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_find_merchant_home, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        HljBaseActivity.setActionBarPadding(getContext(), actionHolderLayout);
        initView();
        initLoad();
    }

    private void initView() {
        noticeUtil = new NoticeUtil(getContext(), msgCount, msgNotice);
        noticeUtil.onResume();
        fragments.add(FinderRecommendFragment.newInstance());
        fragments.add(FindMerchantFragment.newInstance(city,
                SPUtils.getLong(getContext(),
                        RouterPath.IntentPath.Customer.MerchantListActivityPath.ARG_PROPERTY_ID,
                        0)));
        fragments.add(FinderCaseHomeFragment.newInstance());
        CommonPagerAdapter pagerAdapter = new CommonPagerAdapter(getChildFragmentManager(),
                fragments,
                Arrays.asList(titles));
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                indicator.setCurrentItem(position);
                setShowMsg(position);
            }
        });
        indicator.setTabViewId(R.layout.menu_primary_tab_widget);
        indicator.setOnTabChangeListener(this);
        indicator.setPagerAdapter(pagerAdapter);
        for (int i = 0; i < titles.length; i++) {
            View tabView = indicator.getTabView(i);
            if (tabView != null) {
                TextView textView = tabView.findViewById(R.id.title);
                if (textView != null) {
                    textView.getLayoutParams().width = (int) textView.getPaint()
                            .measureText(textView.getText()
                                    .toString()
                                    .trim()) + CommonUtil.dp2px(getContext(), 12);
                }
            }
        }
        viewPager.setCurrentItem(isShowFinder ? TAB_FINDER : TAB_FIND_MERCHANT);
    }

    @Override
    public void onTabChanged(int position) {
        viewPager.setCurrentItem(position);
        setShowMsg(position);
    }

    private void setShowMsg(int position) {
        boolean showFinder = isShowFinder && position == 0;
        msgLayout.setVisibility(showFinder ? View.GONE : View.VISIBLE);
        btnCreateNote.setVisibility(showFinder ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            cityRefresh(Session.getInstance()
                    .getMyCity(getContext()));
        } else {
            hideMenu();
        }
    }

    /**
     * 切换城市，重刷数据
     */
    public void cityRefresh(City c) {
        if (city == null || city.getId()
                .equals(c.getId())) {
            return;
        }
        city = c;
        Fragment fragment = fragments.get(viewPager.getCurrentItem());
        if (fragment == null) {
            return;
        }
        if (fragment instanceof FinderRecommendFragment) {
            ((FinderRecommendFragment) fragment).cityRefresh(c);
        } else if (fragment instanceof FinderCaseHomeFragment) {
            ((FinderCaseHomeFragment) fragment).cityRefresh(c);
        } else if (fragment instanceof FindMerchantFragment) {
            ((FindMerchantFragment) fragment).cityRefresh(c);
        }
        initLoad();
    }

    public boolean hideMenu() {
        if (fragments != null) {
            Fragment fragment = fragments.get(viewPager.getCurrentItem());
            if (fragment != null && fragment instanceof FindMerchantFragment) {
                return ((FindMerchantFragment) fragment).hideMenu();
            }
        }
        return false;
    }

    private void initLoad() {
        CommonUtil.unSubscribeSubs(initSub);
        initSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<NewHotKeyWord>() {
                    @Override
                    public void onNext(NewHotKeyWord keyWord) {
                        hotKeyWord = keyWord;
                    }
                })
                .setDataNullable(true)
                .build();
        NewSearchApi.getInputWord(NewSearchApi.InputType.TYPE_FINDER)
                .subscribe(initSub);
    }

    @OnClick(R.id.msg_layout)
    void onMsgLayout() {
        if (hideMenu()) {
            return;
        }
        if (Util.loginBindChecked(getContext())) {
            Intent intent = new Intent(getContext(), MessageHomeActivity.class);
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().overridePendingTransition(R.anim.slide_in_right,
                        R.anim.activity_anim_default);
            }
        }
    }

    @OnClick(R.id.btn_create_note)
    void onCreateNote() {
        if (!AuthUtil.loginBindCheck(getContext())) {
            return;
        }
        startActivity(new Intent(getContext(), CreatePhotoNoteActivity.class));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (noticeUtil != null) {
            noticeUtil.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (noticeUtil != null) {
            noticeUtil.onPause();
        }
    }

    @Override
    public void refresh(Object... params) {
        int currentItem = viewPager.getCurrentItem();
        if (currentItem >= fragments.size()) {
            return;
        }
        Fragment fragment = fragments.get(currentItem);
        if (fragment != null && fragment instanceof RefreshFragment) {
            ((RefreshFragment) fragment).refresh();
        }
    }

    @OnClick(R.id.btn_search)
    void onSearch() {
        if (hideMenu()) {
            return;
        }
        Intent intent = new Intent(getContext(), NewSearchActivity.class);
        intent.putExtra(NewSearchApi.ARG_SEARCH_INPUT_TYPE, NewSearchApi.InputType.TYPE_FINDER);
        int position = viewPager.getCurrentItem();
        if (position != TAB_FIND_MERCHANT) {
            intent.putExtra(NewSearchActivity.ARG_HOT_KEY_WORD, hotKeyWord);
        }
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().overridePendingTransition(R.anim.slide_in_right,
                    R.anim.activity_anim_default);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
        CommonUtil.unSubscribeSubs(initSub);
    }

}
