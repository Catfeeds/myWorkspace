package me.suncloud.marrymemo.fragment.community;

/**
 * Created by mo_yu on 2018/3/9.新娘说3.0精选主页
 */

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityChannel;
import com.hunliji.hljcommonlibrary.models.modelwrappers.PosterData;
import com.hunliji.hljcommonlibrary.models.realm.Notification;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljTaggerName;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.fragments.ScrollAbleFragment;
import com.hunliji.hljcommonlibrary.views.widgets.FlowLayout;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljcommonlibrary.views.widgets.PullToRefreshScrollableLayout;
import com.hunliji.hljcommonlibrary.views.widgets.ScrollableLayout;
import com.hunliji.hljhttplibrary.api.CommonApi;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.PosterUtil;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.makeramen.rounded.RoundedImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.community.MyCommunityChannelAdapter;
import me.suncloud.marrymemo.adpter.community.viewholder.CommunityChannelViewHolder;
import me.suncloud.marrymemo.adpter.community.viewholder.CommunityTopPosterViewHolder;
import me.suncloud.marrymemo.api.community.CommunityApi;
import me.suncloud.marrymemo.fragment.SocialHomeFragment;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.model.PointRecord;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.model.community.PosterWatchFeed;
import me.suncloud.marrymemo.util.BannerUtil;
import me.suncloud.marrymemo.util.PointUtil;
import me.suncloud.marrymemo.util.RecentChannelUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.notification.CommunityNotificationActivity;
import me.suncloud.marrymemo.view.wallet.GoldMarketWebViewActivity;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func4;
import rx.schedulers.Schedulers;

import static android.support.v7.widget.LinearLayoutManager.HORIZONTAL;

public class CommunityChoiceHomeFragment extends RefreshFragment implements PullToRefreshBase
        .OnRefreshListener<ScrollableLayout> {

    private static final int FRAGMENT_HOT_COMMUNITY = 0;
    public static final int COMMUNITY_LISTING = 1;
    public static final int WEDDING_COST = 2;
    public static final int WEDDING_SHOPPING = 3;
    public static final int WEDDING_DATE = 4;

    @BindView(R.id.header_layout)
    LinearLayout headerLayout;
    @BindView(R.id.scrollable_layout)
    PullToRefreshScrollableLayout scrollableLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    Unbinder unbinder;
    @BindView(R.id.flow_layout)
    FlowLayout flowLayout;
    @BindView(R.id.channel_recycler_view)
    RecyclerView channelRecyclerView;
    @BindView(R.id.tv_msg_count)
    TextView tvMsgCount;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.riv_auth_avatar)
    RoundedImageView rivAuthAvatar;
    @BindView(R.id.msg_view)
    LinearLayout msgView;
    @BindView(R.id.img_poster)
    RoundedImageView imgPoster;
    @BindView(R.id.img_poster2)
    RoundedImageView imgPoster2;
    @BindView(R.id.poster_layout)
    LinearLayout posterLayout;
    @BindView(R.id.tv_hot_community)
    TextView tvHotCommunity;
    @BindView(R.id.tv_community_listing)
    TextView tvCommunityListing;
    @BindView(R.id.tv_wedding_cost)
    TextView tvWeddingCost;
    @BindView(R.id.tv_wedding_shopping)
    TextView tvWeddingShopping;
    @BindView(R.id.tv_wedding_date)
    TextView tvWeddingDate;
    @BindView(R.id.page_indicator_line)
    View pageIndicatorLine;
    @BindView(R.id.btn_sign_up_gold)
    ImageButton btnSignUpGold;

    private int msgAvatarSize;
    private int posterChannelWidth;
    private int posterChannelHeight;
    private int lastPosition = -1;
    private boolean isShow;
    private PointRecord pointRecord;//金币
    private City city;
    private User user;
    private Realm realm;
    private SparseArray<ScrollAbleFragment> fragments;
    private MyCommunityChannelAdapter channelAdapter;
    private CommunityFragmentAdapter communityFragmentAdapter;
    private HljHttpSubscriber initSub;
    private Subscription newsSubscription;
    private ArrayList<TextView> tabs;
    private final static String[] TAB_TITLES = {"精选", "清单", " 婚礼花费", "新娘购物车", "大婚当天"};
    private final static Integer[] TAB_IDS = {-1, COMMUNITY_LISTING, WEDDING_COST,
            WEDDING_SHOPPING, WEDDING_DATE};//1清单、2婚礼花费、3新娘购物、4大婚当天

    public static CommunityChoiceHomeFragment newInstance() {
        Bundle args = new Bundle();
        CommunityChoiceHomeFragment fragment = new CommunityChoiceHomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public String fragmentPageTrackTagName() {
        return "新娘说";
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        msgAvatarSize = CommonUtil.dp2px(getContext(), 28);
        posterChannelWidth = (CommonUtil.getDeviceSize(getContext()).x - CommonUtil.dp2px
                (getContext(),
                48)) / 2;
        posterChannelHeight = posterChannelWidth * 144 / 327;
        city = Session.getInstance()
                .getMyCity(getContext());
        user = Session.getInstance()
                .getCurrentUser(getContext());
        realm = Realm.getDefaultInstance();
        fragments = new SparseArray<>();
        for (int i = 0; i < TAB_TITLES.length; i++) {
            if (i == FRAGMENT_HOT_COMMUNITY) {
                fragments.put(i, CommunityChoiceFeedListFragment.newInstance());
            } else {
                fragments.put(i, CommunityFeedListFragment.newInstance(TAB_IDS[i]));
            }
        }
        isShow = true;
        if (user != null) {
            pointRecord = PointUtil.getInstance()
                    .getPointRecord(getActivity(), user.getId());
        }
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_community_choice_home, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        pointRefresh(pointRecord);
        initLoad();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshNotificationMsg();
        if (newsSubscription != null && !newsSubscription.isUnsubscribed()) {
            return;
        }
        newsSubscription = RxBus.getDefault()
                .toObservable(RxEvent.class)
                .buffer(1, TimeUnit.SECONDS)
                .filter(new Func1<List<RxEvent>, Boolean>() {
                    @Override
                    public Boolean call(List<RxEvent> rxEvents) {
                        for (RxEvent rxEvent : rxEvents) {
                            switch (rxEvent.getType()) {
                                case NEW_NOTIFICATION:
                                    if (rxEvent.getObject() != null && rxEvent.getObject()
                                            .equals(-1)) {
                                        continue;
                                    }
                                    return true;
                            }

                        }
                        return false;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxBusSubscriber<List<RxEvent>>() {
                    @Override
                    protected void onEvent(List<RxEvent> rxEvents) {
                        refreshNotificationMsg();
                    }
                });
    }

    private void refreshNotificationMsg() {
        User user = Session.getInstance()
                .getCurrentUser(getActivity());
        if (user != null && user.getId() > 0 & realm != null && !realm.isClosed()) {
            long msgCount = realm.where(Notification.class)
                    .equalTo("userId", user.getId())
                    .notEqualTo("status", 2)
                    .equalTo("notifyType", Notification.NotificationType.COMMUNITY)
                    .beginGroup()
                    .equalTo("action", "post_reply")
                    .or()
                    .equalTo("action", "thread_reply")
                    .or()
                    .equalTo("action", "qa_answer_comment")
                    .or()
                    .equalTo("action", "qa_answer_comment_reply")
                    .or()
                    .equalTo("action", "qa_answer")
                    .endGroup()
                    .count();
            long praiseCount = realm.where(Notification.class)
                    .equalTo("userId", user.getId())
                    .notEqualTo("status", 2)
                    .equalTo("notifyType", Notification.NotificationType.COMMUNITY)
                    .beginGroup()
                    .equalTo("action", "post_praise")
                    .or()
                    .equalTo("action", "qa_answer_praise")
                    .or()
                    .equalTo("action", "plus1")
                    .or()
                    .equalTo("action", "qa_praise")
                    .endGroup()
                    .count();
            if (msgCount > 0 || praiseCount > 0) {
                msgView.setVisibility(View.VISIBLE);
                tvMsgCount.setText("收到" + msgCount + "个回复，" + praiseCount + "个点赞");
                RealmResults<Notification> item = realm.where(Notification.class)
                        .equalTo("userId", user.getId())
                        .notEqualTo("status", 2)
                        .equalTo("notifyType", Notification.NotificationType.COMMUNITY)
                        .findAllSorted("id", Sort.DESCENDING);
                String path = null;
                if (!CommonUtil.isCollectionEmpty(item)) {
                    path = ImagePath.buildPath(item.get(0)
                            .getParticipantAvatar())
                            .width(msgAvatarSize)
                            .height(msgAvatarSize)
                            .cropPath();
                }
                Glide.with(rivAuthAvatar.getContext())
                        .load(path)
                        .apply(new RequestOptions().dontAnimate()
                                .placeholder(R.mipmap.icon_avatar_primary))
                        .into(rivAuthAvatar);
            } else {
                msgView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (newsSubscription != null && !newsSubscription.isUnsubscribed()) {
            newsSubscription.unsubscribe();
        }
    }

    /**
     * 金币签到按钮状态刷新
     *
     * @param pointRecord
     */
    public void pointRefresh(final PointRecord pointRecord) {
        if (pointRecord != null && btnSignUpGold != null) {
            this.pointRecord = pointRecord;
            btnSignUpGold.post(new Runnable() {
                @Override
                public void run() {
                    if (btnSignUpGold == null) {
                        return;
                    }
                    if (pointRecord.isSignToday()) {
                        btnSignUpGold.setVisibility(View.GONE);
                    } else {
                        btnSignUpGold.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

    private void initView() {
        tabs = new ArrayList<>(Arrays.asList(tvHotCommunity,
                tvCommunityListing,
                tvWeddingCost,
                tvWeddingShopping,
                tvWeddingDate));
        communityFragmentAdapter = new CommunityFragmentAdapter(getChildFragmentManager());
        viewPager.setAdapter(communityFragmentAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                onTabChanged(position);
                if (getCurrentFragment() instanceof CommunityChoiceFeedListFragment) {
                    ((CommunityChoiceFeedListFragment) getCurrentFragment()).refreshPointView();
                } else if (getCurrentFragment() instanceof CommunityFeedListFragment) {
                    ((CommunityFeedListFragment) getCurrentFragment()).refreshPointView();
                }
                scrollableLayout.getRefreshableView()
                        .getHelper()
                        .setCurrentScrollableContainer(getCurrentFragment());
            }
        });
        scrollableLayout.setOnRefreshListener(this);
        scrollableLayout.getRefreshableView()
                .addOnScrollListener(new ScrollableLayout.OnScrollListener() {
                    @Override
                    public void onScroll(int currentY, int maxY) {
                        if (scrollableLayout.getRefreshableView()
                                .getHelper()
                                .getScrollableView() == null && getCurrentFragment() != null) {
                            scrollableLayout.getRefreshableView()
                                    .getHelper()
                                    .setCurrentScrollableContainer(getCurrentFragment());
                        }
                    }
                });
        emptyView.setOnEmptyClickListener(new HljEmptyView.OnEmptyClickListener() {
            @Override
            public void onEmptyClickListener() {
                refresh();
            }
        });
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                refresh();
            }
        });
        scrollableLayout.setAwaysRefresh(true);
        channelRecyclerView.setFocusable(false);
        channelRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                HORIZONTAL,
                false));
        channelAdapter = new MyCommunityChannelAdapter(getContext());
        channelAdapter.setHasFooter(true);
        channelAdapter.setHasHeader(true);
        channelAdapter.setOnChannelClickListener(new MyCommunityChannelAdapter
                .OnChannelClickListener() {
            @Override
            public void onChannelClick() {
                onChangeTabToChannel();
            }
        });
        channelRecyclerView.setAdapter(channelAdapter);
        onTabChanged(FRAGMENT_HOT_COMMUNITY);

        imgPoster.getLayoutParams().width = posterChannelWidth;
        imgPoster.getLayoutParams().height = posterChannelHeight;
        imgPoster2.getLayoutParams().width = posterChannelWidth;
        imgPoster2.getLayoutParams().height = posterChannelHeight;
    }


    private void initLoad() {
        if (initSub == null || initSub.isUnsubscribed()) {
            Observable<PosterData> bObservable = CommonApi.getBanner(getContext(),
                    HljCommon.BLOCK_ID.SocialHotFragment,
                    city.getId())
                    .onErrorReturn(new Func1<Throwable, PosterData>() {
                        @Override
                        public PosterData call(Throwable throwable) {
                            return null;
                        }
                    });
            Observable<List<CommunityChannel>> cObservable = CommunityApi.getHomeFixedChannels()
                    .onErrorReturn(new Func1<Throwable, List<CommunityChannel>>() {
                        @Override
                        public List<CommunityChannel> call(Throwable throwable) {
                            return null;
                        }
                    });
            Observable<List<CommunityChannel>> rObservable = CommunityApi.getRecentScanChannelsObb(
                    getRecentChannelIdsStr())
                    .onErrorReturn(new Func1<Throwable, List<CommunityChannel>>() {
                        @Override
                        public List<CommunityChannel> call(Throwable throwable) {
                            return null;
                        }
                    });
            Observable<List<PosterWatchFeed>> mObservable = CommunityApi.getPosterWatchCountObb()
                    .onErrorReturn(new Func1<Throwable, List<PosterWatchFeed>>() {
                        @Override
                        public List<PosterWatchFeed> call(Throwable throwable) {
                            return null;
                        }
                    });
            Observable<ResultZip> observable = Observable.zip(bObservable,
                    cObservable,
                    rObservable,
                    mObservable,
                    new Func4<PosterData, List<CommunityChannel>, List<CommunityChannel>,
                            List<PosterWatchFeed>, ResultZip>() {
                        @Override
                        public ResultZip call(
                                PosterData posterData,
                                List<CommunityChannel> communityChannels,
                                List<CommunityChannel> recentChannels,
                                List<PosterWatchFeed> posterWatchFeeds) {
                            List<Poster> middlePosters = null;
                            List<Poster> topPosters = null;
                            if (posterData != null) {
                                middlePosters = PosterUtil.getPosterList(posterData.getFloors(),
                                        Constants.POST_SITES.SITE_BRIDE_CHOICE_MIDDLE_FLOOR,
                                        false);
                                topPosters = PosterUtil.getPosterList(posterData.getFloors(),
                                        Constants.POST_SITES.SITE_BRIDE_CHOICE_TOP_FLOOR,
                                        false);
                            }
                            return new ResultZip(topPosters,
                                    middlePosters,
                                    communityChannels,
                                    recentChannels,
                                    posterWatchFeeds);
                        }
                    });
            initSub = HljHttpSubscriber.buildSubscriber(getContext())
                    .setOnNextListener(new SubscriberOnNextListener<ResultZip>() {
                        @Override
                        public void onNext(ResultZip resultZip) {
                            scrollableLayout.getRefreshableView()
                                    .setVisibility(View.VISIBLE);
                            setMiddlePosterLayout(resultZip.middlePosters);
                            setFixChannelLayout(resultZip.fixedChannels,
                                    resultZip.topPosters,
                                    resultZip.posterWatchFeeds);
                            if (!CommonUtil.isCollectionEmpty(resultZip.recentChannels)) {
                                channelRecyclerView.setVisibility(View.VISIBLE);
                                channelAdapter.setList(resultZip.recentChannels);
                                channelAdapter.notifyDataSetChanged();
                            } else {
                                channelRecyclerView.setVisibility(View.GONE);
                            }
                        }
                    })
                    .setPullToRefreshBase(scrollableLayout)
                    .setContentView(scrollableLayout)
                    .setEmptyView(emptyView)
                    .setProgressBar(scrollableLayout.isRefreshing() ? null : progressBar)
                    .build();
            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(initSub);
        }
    }

    @OnClick({R.id.tv_hot_community, R.id.tv_community_listing, R.id.tv_wedding_cost, R.id
            .tv_wedding_shopping, R.id.tv_wedding_date})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_hot_community:
                onTabChanged(FRAGMENT_HOT_COMMUNITY);
                break;
            case R.id.tv_community_listing:
                onTabChanged(COMMUNITY_LISTING);
                break;
            case R.id.tv_wedding_cost:
                onTabChanged(WEDDING_COST);
                break;
            case R.id.tv_wedding_shopping:
                onTabChanged(WEDDING_SHOPPING);
                break;
            case R.id.tv_wedding_date:
                onTabChanged(WEDDING_DATE);
                break;
        }
    }

    @OnClick(R.id.msg_view)
    void onMsgCountView() {
        if (Util.loginBindChecked(getActivity(), Constants.RequestCode.NOTIFICATION_SOCIAL_PAGE)) {
            Intent intent = new Intent(getActivity(), CommunityNotificationActivity.class);
            startActivityForResult(intent, Constants.RequestCode.NOTIFICATION_SOCIAL_PAGE);
            getActivity().overridePendingTransition(R.anim.slide_in_right,
                    R.anim.activity_anim_default);
        }
    }

    @OnClick(R.id.btn_sign_up_gold)
    public void onBtnSignUpClicked() {
        if (Util.loginBindChecked(getActivity())) {
            if (pointRecord == null) {
                return;
            }
            Intent intent = new Intent(getActivity(), GoldMarketWebViewActivity.class);
            intent.putExtra("pointRecord", pointRecord);
            startActivityForResult(intent, Constants.RequestCode.SIGN_COLD_COIN);
            getActivity().overridePendingTransition(R.anim.slide_in_right,
                    R.anim.activity_anim_default);
        }
    }

    private class ResultZip {
        List<Poster> topPosters;
        List<Poster> middlePosters;
        List<CommunityChannel> fixedChannels;//固定位频道
        List<CommunityChannel> recentChannels;//常逛新娘圈
        List<PosterWatchFeed> posterWatchFeeds;

        private ResultZip(
                List<Poster> topPosters,
                List<Poster> middlePosters,
                List<CommunityChannel> fixedChannels,
                List<CommunityChannel> recentChannels,
                List<PosterWatchFeed> posterWatchFeeds) {
            this.topPosters = topPosters;
            this.middlePosters = middlePosters;
            this.fixedChannels = fixedChannels;
            this.recentChannels = recentChannels;
            this.posterWatchFeeds = posterWatchFeeds;
        }
    }

    private String getRecentChannelIdsStr() {
        StringBuilder idsStr = new StringBuilder();
        List<Long> recentChannelIds = RecentChannelUtil.getRecentChannelIds(getContext());
        int size = Math.min(4, recentChannelIds.size());
        for (int i = 0; i < size; i++) {
            idsStr.append(recentChannelIds.get(i));
            if (i != size - 1) {
                idsStr.append(",");
            }
        }
        return idsStr.toString();
    }

    private void setMiddlePosterLayout(List<Poster> posters) {
        if (CommonUtil.isCollectionEmpty(posters)) {
            posterLayout.setVisibility(View.GONE);
            return;
        }
        posterLayout.setVisibility(View.VISIBLE);
        ArrayList<RoundedImageView> imgLists = new ArrayList<>(Arrays.asList(imgPoster,
                imgPoster2));
        int size = 2;
        for (int i = 0; i < size; i++) {
            RoundedImageView imageView = imgLists.get(i);
            if (posters.size() <= i) {
                imageView.setVisibility(View.INVISIBLE);
                continue;
            }
            final Poster poster = posters.get(i);
            String imagePath = null;
            if (poster != null) {
                HljVTTagger.buildTagger(imageView)
                        .tagName(HljTaggerName.CommunityHomeFragment.COMMUNITY_MIDDLE_FLOOR_CLICK)
                        .poster(poster)
                        .hitTag();
                imagePath = poster.getPath();
            }
            if (CommonUtil.isEmpty(imagePath)) {
                imageView.setVisibility(View.INVISIBLE);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
            } else {
                imageView.setVisibility(View.VISIBLE);
                Glide.with(getContext())
                        .load(imagePath)
                        .apply(new RequestOptions().dontAnimate()
                                .placeholder(R.mipmap.icon_empty_image))
                        .into(imageView);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BannerUtil.bannerJump(getContext(), poster, null);
                    }
                });
            }
        }
    }

    private void setFixChannelLayout(
            List<CommunityChannel> fixCommunityChannels,
            List<Poster> topPosters,
            List<PosterWatchFeed> posterWatchFeeds) {
        flowLayout.removeAllViews();
        int sameCityWatchCount = 0;
        int qaWatchCount = 0;
        if (!CommonUtil.isCollectionEmpty(posterWatchFeeds)) {
            for (int i = 0; i < posterWatchFeeds.size(); i++) {
                PosterWatchFeed posterWatchFeed = posterWatchFeeds.get(i);
                if (posterWatchFeed.getType()
                        .equals(PosterWatchFeed.SAME_CITY_TYPE)) {
                    sameCityWatchCount = posterWatchFeed.getTodayWatchCount();
                } else if (posterWatchFeed.getType()
                        .equals(PosterWatchFeed.QA_TYPE)) {
                    qaWatchCount = posterWatchFeed.getTodayWatchCount();
                }
            }
        }
        //热门频道视图
        int channelCount = flowLayout.getChildCount();
        int channelSize = fixCommunityChannels == null ? 0 : Math.min(fixCommunityChannels.size(),
                6);
        int posterSize = topPosters == null ? 0 : Math.min(topPosters.size(), 2);
        if (channelCount > channelSize + posterSize) {
            flowLayout.removeViews(channelSize + posterSize, channelCount - channelSize);
        }
        //插入宝典和问答两个固定坑位
        if (!CommonUtil.isCollectionEmpty(topPosters)) {
            for (int i = 0; i < posterSize; i++) {
                Poster poster = topPosters.get(i);
                if (poster.getTargetType() == 91) {
                    poster.setExtention(String.valueOf(sameCityWatchCount));
                } else if (poster.getTargetType() == 61) {
                    poster.setExtention(String.valueOf(qaWatchCount));
                }
                View view = null;
                CommunityTopPosterViewHolder holder;
                if (channelCount > i) {
                    view = flowLayout.getChildAt(i);
                }
                if (view == null) {
                    View.inflate(getContext(), R.layout.community_channel_flow_item, flowLayout);
                    view = flowLayout.getChildAt(flowLayout.getChildCount() - 1);
                }
                holder = (CommunityTopPosterViewHolder) view.getTag();
                if (holder == null) {
                    holder = new CommunityTopPosterViewHolder(view);
                    view.setTag(holder);
                }
                holder.setView(getContext(), poster, i, 0);
            }
        }
        if (!CommonUtil.isCollectionEmpty(fixCommunityChannels)) {
            for (int i = posterSize; i < channelSize + posterSize; i++) {
                int position = i - posterSize;
                CommunityChannel communityChannel = fixCommunityChannels.get(position);
                View view = null;
                CommunityChannelViewHolder holder;
                if (channelCount > i) {
                    view = flowLayout.getChildAt(i);
                }
                if (view == null) {
                    View.inflate(getContext(), R.layout.community_channel_flow_item, flowLayout);
                    view = flowLayout.getChildAt(flowLayout.getChildCount() - 1);
                }
                holder = (CommunityChannelViewHolder) view.getTag();
                if (holder == null) {
                    holder = new CommunityChannelViewHolder(view);
                    view.setTag(holder);
                }
                holder.setView(getContext(), communityChannel, position, 0);
            }
        }
        if (CommonUtil.isCollectionEmpty(fixCommunityChannels) && CommonUtil.isCollectionEmpty(
                topPosters)) {
            flowLayout.setVisibility(View.GONE);
        } else {
            flowLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void refresh(Object... params) {
        initLoad();
        ScrollAbleFragment fragment = fragments.get(viewPager.getCurrentItem());
        if (fragment != null) {
            fragment.refresh();
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
    public void onRefresh(PullToRefreshBase<ScrollableLayout> refreshView) {
        refresh();
    }

    /**
     * 选择城市之后，刷新数据
     *
     * @param c
     */
    public void cityRefresh(City c) {
        if (city == null || c == null) {
            return;
        }
        if (!city.getId()
                .equals(c.getId())) {
            city = c;
            scrollTop();
            refresh();
        }
    }

    /**
     * 退出登录或者切换用户之后，刷新数据
     *
     * @param u
     */
    public void userRefresh(User u) {
        if (u != null) {
            if (user != null) {
                if (!u.getId()
                        .equals(user.getId())) {
                    user = u;
                    scrollTop();
                    refresh();
                }
            } else {
                user = u;
                scrollTop();
                refresh();
            }
        } else if (user != null) {
            user = null;
            scrollTop();
            refresh();
        }
    }

    private void scrollTop() {
        scrollableLayout.getRefreshableView()
                .scrollToTop();
    }

    public void onTabChanged(int position) {
        viewPager.setCurrentItem(position);
        if (lastPosition >= 0) {
            TextView lastTv = tabs.get(lastPosition);
            lastTv.setSelected(false);
            lastTv.setTextSize(14);
            lastTv.getPaint()
                    .setFakeBoldText(false);
        }
        lastPosition = position;
        TextView tv = tabs.get(position);
        tv.setSelected(true);
        tv.setTextSize(16);
        tv.getPaint()
                .setFakeBoldText(true);
    }

    private class CommunityFragmentAdapter extends FragmentStatePagerAdapter {

        private FragmentManager fragmentManager;

        private CommunityFragmentAdapter(FragmentManager fm) {
            super(fm);
            fragmentManager = fm;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return TAB_TITLES.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TAB_TITLES[position];
        }

        @NonNull
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = getItem(position);
            if (fragment.isAdded()) {
                fragmentManager.beginTransaction()
                        .show(fragment)
                        .commitNowAllowingStateLoss();
                return fragment;
            }
            return super.instantiateItem(container, position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            Fragment fragment = getItem(position);
            fragmentManager.beginTransaction()
                    .hide(fragment)
                    .commitNowAllowingStateLoss();
        }
    }

    //切换tab
    public void onChangeTabToChannel() {
        if (getParentFragment() instanceof SocialHomeFragment) {
            SocialHomeFragment socialHomeFragment = (SocialHomeFragment) getParentFragment();
            socialHomeFragment.onTabChanged(SocialHomeFragment.TAB_INDEX_CHANNEL);
        }
    }

    public void hideToolbar() {
        if (btnSignUpGold.getVisibility() == View.VISIBLE && isShow) {
            isShow = false;
            ObjectAnimator animator = ObjectAnimator.ofFloat(btnSignUpGold,
                    View.TRANSLATION_X,
                    0,
                    btnSignUpGold.getWidth());
            animator.setDuration(500);
            animator.start();
        }
    }

    public void showToolbar() {
        if (btnSignUpGold.getVisibility() == View.VISIBLE && !isShow) {
            isShow = true;
            ObjectAnimator animator = ObjectAnimator.ofFloat(btnSignUpGold,
                    View.TRANSLATION_X,
                    btnSignUpGold.getWidth(),
                    0);
            animator.setDuration(500);
            animator.start();
        }
    }

    /**
     * @return 当前FeedsFragment
     */
    private ScrollAbleFragment getCurrentFragment() {
        if (viewPager.getAdapter() != null && viewPager.getAdapter()
                .getCount() > 0 && viewPager.getAdapter() instanceof CommunityFragmentAdapter) {
            CommunityFragmentAdapter adapter = (CommunityFragmentAdapter) viewPager.getAdapter();
            Fragment fragment = (Fragment) adapter.instantiateItem(viewPager,
                    viewPager.getCurrentItem());
            if (fragment instanceof ScrollAbleFragment && fragment.isAdded() && !fragment
                    .isDetached()) {
                return (ScrollAbleFragment) fragment;
            }
        }
        return null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        if (realm != null) {
            realm.close();
        }
        CommonUtil.unSubscribeSubs(initSub);
    }
}

