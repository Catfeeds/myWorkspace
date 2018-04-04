package me.suncloud.marrymemo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.models.live.LiveChannel;
import com.hunliji.hljcommonlibrary.models.search.NewHotKeyWord;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.OncePrefUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljTaggerName;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearButton;
import com.hunliji.hljhttplibrary.api.newsearch.NewSearchApi;
import com.hunliji.hljhttplibrary.utils.AuthUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljlivelibrary.fragments.SocialLiveFragment;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.realm.Realm;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.RankListAdapter;
import me.suncloud.marrymemo.api.home.HomeApi;
import me.suncloud.marrymemo.fragment.community.CommunityChoiceHomeFragment;
import me.suncloud.marrymemo.fragment.community.SocialChannelHomeFragment;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.model.PointRecord;
import me.suncloud.marrymemo.model.Post;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.TrackerUtil;
import me.suncloud.marrymemo.view.MainActivity;
import me.suncloud.marrymemo.view.community.CreateThreadActivity;
import me.suncloud.marrymemo.view.newsearch.NewSearchActivity;
import rx.Subscription;
import rx.functions.Func1;

/**
 * Created by mo_yu on 2016/5/18.新版社区首页
 */
public class SocialHomeFragment extends RefreshFragment implements AdapterView.OnItemClickListener {


    public static final String ARG_TAB_PAGE = "tab_page";
    public final static int TAB_INDEX_LIVE = 0;//直播
    public final static int TAB_INDEX_SOCIAL_HOT = 1;//社区热门
    public final static int TAB_INDEX_CHANNEL = 2;//新娘圈
    @BindView(R.id.btn_search)
    ImageButton btnSearch;
    @BindView(R.id.action_layout)
    LinearLayout actionLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.scrollable_layout)
    RelativeLayout scrollableLayout;
    @BindView(R.id.tv_left_tab)
    TextView tvLeftTab;
    @BindView(R.id.left_tab)
    CheckableLinearButton leftTab;
    @BindView(R.id.tv_center_tab)
    TextView tvCenterTab;
    @BindView(R.id.center_tab)
    CheckableLinearButton centerTab;
    @BindView(R.id.tv_right_tab)
    TextView tvRightTab;
    @BindView(R.id.news_icon_right)
    TextView newsIconRight;
    @BindView(R.id.right_tab)
    CheckableLinearButton rightTab;
    @BindView(R.id.btn_create)
    TextView btnCreate;
    @BindView(R.id.gradient_view)
    View gradientView;
    private CommunityChoiceHomeFragment communityChoiceHomeFragment;//社区热门
    private SocialChannelHomeFragment socialChannelHomeFragment;//新娘圈
    private SocialLiveFragment socialLiveFragment;//直播

    private City city;
    private Unbinder unbinder;
    private Realm realm;
    private int lastPosition = -1;

    private Subscription newsSubscription;
    private Subscription pointSubscription;
    private int selectedTab = TAB_INDEX_SOCIAL_HOT;
    private NewHotKeyWord newHotKeyWord;
    private HljHttpSubscriber inputSubscriber;
    private HljHttpSubscriber liveSubscriber;

    public static SocialHomeFragment newInstance(int socialTabPage) {
        SocialHomeFragment fragment = new SocialHomeFragment();
        Bundle bundle = new Bundle();
        if (socialTabPage >= 0) {
            bundle.putSerializable(ARG_TAB_PAGE, socialTabPage);
        }
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initValues();
    }

    private void initValues() {
        city = Session.getInstance()
                .getMyCity(getActivity());
        realm = Realm.getDefaultInstance();
        if (getArguments() != null) {
            selectedTab = getArguments().getInt(ARG_TAB_PAGE, selectedTab);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (newsSubscription != null && !newsSubscription.isUnsubscribed()) {
            newsSubscription.unsubscribe();
        }
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_social_home, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        HljBaseActivity.setActionBarPadding(getContext(), rootView);
        initViews();
        initTracker();

        return rootView;
    }

    private void initTracker() {
        HljVTTagger.buildTagger(btnCreate)
                .tagName(HljTaggerName.CommunityHomeFragment.BTN_CREATE_THREAD)
                .hitTag();
    }

    private void initViews() {
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setFragment();
        getInputBoxHotWord();
        getLatestLiveInfo();
    }

    private void setFragment() {
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(
                getChildFragmentManager());
        tvLeftTab.setText(getString(R.string.label_live));
        tvCenterTab.setText(getString(R.string.label_selection));
        tvRightTab.setText(getString(R.string.label_community_channel_circle));
        viewPager.setAdapter(mSectionsPagerAdapter);
        viewPager.setOffscreenPageLimit(2);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (position == TAB_INDEX_CHANNEL) {
                    if (!OncePrefUtil.hasDoneThis(getContext(),
                            HljCommon.SharedPreferencesNames.COMMUNITY_CHANNEL_NEW)) {
                        newsIconRight.setVisibility(View.GONE);
                        OncePrefUtil.doneThis(getContext(),
                                HljCommon.SharedPreferencesNames.COMMUNITY_CHANNEL_NEW);
                        if (getActivity() instanceof MainActivity) {
                            MainActivity activity = (MainActivity) getActivity();
                            activity.setCommunityNews();
                        }
                    }
                }
                onTabChanged(position);
            }
        });

        viewPager.setCurrentItem(selectedTab, false);
        onTabChanged(selectedTab);
        if (!OncePrefUtil.hasDoneThis(getContext(),
                HljCommon.SharedPreferencesNames.COMMUNITY_CHANNEL_NEW)) {
            newsIconRight.setVisibility(View.VISIBLE);
        }
    }

    private void getLatestLiveInfo() {
        if (liveSubscriber == null || liveSubscriber.isUnsubscribed()) {
            liveSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                    .setOnNextListener(new SubscriberOnNextListener<LiveChannel>() {
                        @Override
                        public void onNext(LiveChannel liveChannel) {
                            if (liveChannel == null) {
                                tvLeftTab.setText("直播");
                            } else {
                                tvLeftTab.setText("直播中");
                            }
                        }
                    })
                    .setDataNullable(true)
                    .build();
            HomeApi.getLatestLiveInfo()
                    .onErrorReturn(new Func1<Throwable, LiveChannel>() {
                        @Override
                        public LiveChannel call(Throwable throwable) {
                            return null;
                        }
                    })
                    .subscribe(liveSubscriber);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isHidden()) {
            return;
        }
        if (communityChoiceHomeFragment != null) {
            communityChoiceHomeFragment.userRefresh(Session.getInstance()
                    .getCurrentUser());
        }
        if(socialChannelHomeFragment != null){
            socialChannelHomeFragment.userRefresh(Session.getInstance()
                    .getCurrentUser());
        }
        if (newsSubscription == null || newsSubscription.isUnsubscribed()) {
            newsSubscription = RxBus.getDefault()
                    .toObservable(RxEvent.class)
                    .subscribe(new RxBusSubscriber<RxEvent>() {
                        @Override
                        protected void onEvent(RxEvent rxEvent) {
                            switch (rxEvent.getType()) {
                                case NEW_NOTIFICATION:
                                    if (rxEvent.getObject() != null && rxEvent.getObject()
                                            .equals(-1)) {
                                        return;
                                    }
                                case WS_MESSAGE:
                                case EM_MESSAGE:
                                    break;
                            }
                        }
                    });
        }
        if (pointSubscription == null || pointSubscription.isUnsubscribed()) {
            pointSubscription = RxBus.getDefault()
                    .toObservable(RxEvent.class)
                    .subscribe(new RxBusSubscriber<RxEvent>() {
                        @Override
                        protected void onEvent(RxEvent rxEvent) {
                            switch (rxEvent.getType()) {
                                case NEW_POINT_RECORD:
                                    if (communityChoiceHomeFragment != null) {
                                        communityChoiceHomeFragment.pointRefresh((PointRecord)
                                                rxEvent.getObject());
                                    }
                                    break;
                            }
                        }
                    });
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onItemClick(
            AdapterView<?> parent, View view, int position, long id) {
        ((RankListAdapter) parent.getAdapter()).setSelectedItem(position);
    }

    @Override
    public void refresh(Object... params) {
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            //城市刷新
            if (newsSubscription == null || newsSubscription.isUnsubscribed()) {
                newsSubscription = RxBus.getDefault()
                        .toObservable(RxEvent.class)
                        .subscribe(new RxBusSubscriber<RxEvent>() {
                            @Override
                            protected void onEvent(RxEvent rxEvent) {
                                switch (rxEvent.getType()) {
                                    case NEW_NOTIFICATION:
                                        if (rxEvent.getObject() != null && rxEvent.getObject()
                                                .equals(-1)) {
                                            return;
                                        }
                                    case WS_MESSAGE:
                                    case EM_MESSAGE:
                                        break;
                                }
                            }
                        });
            }
            if (communityChoiceHomeFragment != null) {
                communityChoiceHomeFragment.userRefresh(Session.getInstance()
                        .getCurrentUser());
            }
            if(socialChannelHomeFragment != null){
                socialChannelHomeFragment.userRefresh(Session.getInstance()
                        .getCurrentUser());
            }
            cityRefresh(Session.getInstance()
                    .getMyCity(getActivity()));
            getLatestLiveInfo();
            TrackerUtil.onTCAgentPageStart(getActivity(), "新娘说");
        } else {
            if (newsSubscription != null && !newsSubscription.isUnsubscribed()) {
                newsSubscription.unsubscribe();
            }
            TrackerUtil.onTCAgentPageEnd(getActivity(), "新娘说");
        }
        super.onHiddenChanged(hidden);
    }

    public void cityRefresh(City c) {
        if (!city.getId()
                .equals(c.getId())) {
            city = c;
            if (communityChoiceHomeFragment != null) {
                communityChoiceHomeFragment.cityRefresh(city);
            }
            if (socialChannelHomeFragment != null) {
                socialChannelHomeFragment.refresh();
            }
            if (socialLiveFragment != null) {
                socialLiveFragment.refresh();
            }
            getInputBoxHotWord();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        if (realm != null) {
            realm.close();
        }
        CommonUtil.unSubscribeSubs(inputSubscriber, liveSubscriber);
    }

    @OnClick(R.id.btn_create)
    public void onCreate() {
        if (!AuthUtil.loginBindCheck(getContext())) {
            return;
        }
        Intent intent = new Intent(getContext(), CreateThreadActivity.class);
        startActivityForResult(intent, Constants.RequestCode.SEND_THREAD_COMPLETE);
        getActivity().overridePendingTransition(R.anim.slide_in_from_bottom,
                R.anim.activity_anim_default);
    }

    /**
     * 获取输入框热词
     * 切换城市也需要刷新
     */
    private void getInputBoxHotWord() {
        CommonUtil.unSubscribeSubs(inputSubscriber);
        inputSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<NewHotKeyWord>() {
                    @Override
                    public void onNext(NewHotKeyWord hotKeyWord) {
                        newHotKeyWord = hotKeyWord;
                    }
                })
                .setDataNullable(true)
                .build();
        NewSearchApi.getInputWord(NewSearchApi.InputType.TYPE_SOCIAL_HOME)
                .subscribe(inputSubscriber);
    }

    @OnClick(R.id.btn_search)
    void onSearch() {
        Intent intent = new Intent(getContext(), NewSearchActivity.class);
        intent.putExtra(NewSearchApi.ARG_SEARCH_INPUT_TYPE,
                NewSearchApi.InputType.TYPE_SOCIAL_HOME);
        intent.putExtra(NewSearchActivity.ARG_HOT_KEY_WORD, newHotKeyWord);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().overridePendingTransition(R.anim.slide_in_right,
                    R.anim.activity_anim_default);
        }
    }

    @Override
    public String fragmentPageTrackTagName() {
        return "新娘说";
    }

    public void onTabChanged(int position) {
        setTabChecked(lastPosition, false);
        lastPosition = position;
        setTabChecked(position, true);
        viewPager.setCurrentItem(position);
    }

    private void setTabChecked(int position, boolean isChecked) {
        switch (position) {
            case TAB_INDEX_LIVE:
                leftTab.setChecked(isChecked);
                break;
            case TAB_INDEX_SOCIAL_HOT:
                centerTab.setChecked(isChecked);
                break;
            case TAB_INDEX_CHANNEL:
                rightTab.setChecked(isChecked);
                break;
        }
    }


    public void setTabPage(int tabPage) {
        if (tabPage >= 0) {
            selectedTab = tabPage;
            viewPager.setCurrentItem(selectedTab);
            onTabChanged(tabPage);
        }
    }

    @OnClick(R.id.left_tab)
    public void onLeftTabClicked() {
        onTabChanged(TAB_INDEX_LIVE);
    }

    @OnClick(R.id.center_tab)
    public void onCenterTabClicked() {
        onTabChanged(TAB_INDEX_SOCIAL_HOT);
    }

    @OnClick(R.id.right_tab)
    public void onRightTabClicked() {
        onTabChanged(TAB_INDEX_CHANNEL);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case TAB_INDEX_SOCIAL_HOT:
                    if (communityChoiceHomeFragment == null) {
                        communityChoiceHomeFragment = CommunityChoiceHomeFragment.newInstance();
                    }
                    return communityChoiceHomeFragment;
                case TAB_INDEX_CHANNEL:
                    if (socialChannelHomeFragment == null) {
                        socialChannelHomeFragment = SocialChannelHomeFragment.newInstance();
                    }
                    return socialChannelHomeFragment;
                case TAB_INDEX_LIVE:
                    if (socialLiveFragment == null) {
                        socialLiveFragment = SocialLiveFragment.newInstance(0,
                                CommonUtil.dp2px(getContext(), 50));
                    }
                    return socialLiveFragment;
                default:
                    if (communityChoiceHomeFragment == null) {
                        communityChoiceHomeFragment = CommunityChoiceHomeFragment.newInstance();
                    }
                    return communityChoiceHomeFragment;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case TAB_INDEX_SOCIAL_HOT:
                    return getString(R.string.label_selection).toUpperCase();
                case TAB_INDEX_CHANNEL:
                    return getString(R.string.label_community_channel_circle).toUpperCase();
                case TAB_INDEX_LIVE:
                    return getString(R.string.label_live).toUpperCase();
                default:
                    return getString(R.string.label_selection).toUpperCase();
            }
        }
    }

}
