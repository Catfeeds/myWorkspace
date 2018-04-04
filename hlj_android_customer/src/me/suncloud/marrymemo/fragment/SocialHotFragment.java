package me.suncloud.marrymemo.fragment;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityChannel;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityThread;
import com.hunliji.hljcommonlibrary.models.modelwrappers.HotCommunityChannel;
import com.hunliji.hljcommonlibrary.models.modelwrappers.PosterData;
import com.hunliji.hljcommonlibrary.models.questionanswer.Question;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.views.fragments.ScrollAbleFragment;
import com.hunliji.hljcommonlibrary.views.widgets.FlowLayout;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljcommonviewlibrary.models.CommunityFeed;
import com.hunliji.hljhttplibrary.api.CommonApi;
import com.hunliji.hljhttplibrary.entities.HljHttpCountData;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.AuthUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.PosterUtil;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljquestionanswer.activities.QuestionDetailActivity;
import com.hunliji.hljtrackerlibrary.utils.TrackerUtil;
import com.makeramen.rounded.RoundedImageView;
import com.slider.library.Indicators.CirclePageExIndicator;
import com.slider.library.SliderLayout;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.FlowAdapter;
import me.suncloud.marrymemo.adpter.SocialHotRecyclerAdapter;
import me.suncloud.marrymemo.adpter.community.viewholder.CommunityChannelViewHolder;
import me.suncloud.marrymemo.api.community.CommunityApi;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.model.PointRecord;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.model.community.LiveEntranceData;
import me.suncloud.marrymemo.model.community.QaLiveEntranceData;
import me.suncloud.marrymemo.util.BannerUtil;
import me.suncloud.marrymemo.util.PointUtil;
import me.suncloud.marrymemo.util.RecommendThreadUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.community.ChannelListActivity;
import me.suncloud.marrymemo.view.community.CommunityChannelActivity;
import me.suncloud.marrymemo.view.community.CommunityThreadDetailActivity;
import me.suncloud.marrymemo.view.community.CreatePostActivity;
import me.suncloud.marrymemo.view.community.RecommendThreadActivity;
import me.suncloud.marrymemo.view.wallet.GoldMarketWebViewActivity;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func3;
import rx.functions.Func4;
import rx.functions.Func5;
import rx.schedulers.Schedulers;

import static android.support.v7.widget.LinearLayoutManager.VERTICAL;

/**
 * Created by mo_yu on 2016/9/23.社区首页热门
 */
@Deprecated
public class SocialHotFragment extends ScrollAbleFragment implements View.OnClickListener,
        PullToRefreshVerticalRecyclerView.OnRefreshListener, SocialHotRecyclerAdapter
                .OnFeedItemClickListener, SocialHotRecyclerAdapter.OnReplyItemClickListener,
        SocialHotRecyclerAdapter.OnQaItemClickListener {

    public final static int REPLY_RESULT = 101;
    public final static int SCROLL_TOP_RESULT = 102;
    @BindView(R.id.scroll_view)
    PullToRefreshScrollView scrollView;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.backtop_btn)
    ImageButton backTopView;
    @BindView(R.id.btn_sign_up_gold)
    ImageButton btnSignUpGold;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.recycler_parent_layout)
    FrameLayout recyclerParentLayout;
    @BindView(R.id.view_flow)
    SliderLayout viewFlow;
    @BindView(R.id.flow_indicator)
    CirclePageExIndicator flowIndicator;
    @BindView(R.id.banner_layout)
    RelativeLayout bannerLayout;
    @BindView(R.id.rl_all_info_view)
    RelativeLayout rlAllInfoView;
    @BindView(R.id.flow_layout)
    FlowLayout flowLayout;
    @BindView(R.id.hot_channel_view)
    LinearLayout hotChannelView;
    @BindView(R.id.social_header_view)
    LinearLayout socialHeaderView;
    @BindView(R.id.head_tip_view)
    RelativeLayout headTipView;
    Unbinder unbinder;
    @BindView(R.id.tv_qa_desc)
    TextView tvQaDesc;
    @BindView(R.id.tv_qa_title)
    TextView tvQaTitle;
    @BindView(R.id.tv_qa_count)
    TextView tvQaCount;
    @BindView(R.id.tv_live_desc)
    TextView tvLiveDesc;
    @BindView(R.id.tv_live_title)
    TextView tvLiveTitle;
    @BindView(R.id.dot_live_status)
    ImageView dotLiveStatus;
    @BindView(R.id.tv_live_status)
    TextView tvLiveStatus;
    @BindView(R.id.tv_qa_title2)
    TextView tvQaTitle2;
    @BindView(R.id.tv_qa_desc2)
    TextView tvQaDesc2;
    @BindView(R.id.tv_qa_count2)
    TextView tvQaCount2;
    @BindView(R.id.tv_live_title2)
    TextView tvLiveTitle2;
    @BindView(R.id.tv_live_desc2)
    TextView tvLiveDesc2;
    @BindView(R.id.dot_live_status2)
    ImageView dotLiveStatus2;
    @BindView(R.id.tv_live_status2)
    TextView tvLiveStatus2;
    @BindView(R.id.qa_entry_layout_1)
    CardView qaEntryLayout1;
    @BindView(R.id.live_entry_layout_1)
    CardView liveEntryLayout1;
    @BindView(R.id.entry_layout_1)
    LinearLayout entryLayout1;
    @BindView(R.id.banner_entry_layout)
    RelativeLayout bannerEntryLayout;
    @BindView(R.id.qa_entry_layout_2)
    CardView qaEntryLayout2;
    @BindView(R.id.live_entry_layout_2)
    CardView liveEntryLayout2;
    @BindView(R.id.entry_layout_2)
    LinearLayout entryLayout2;
    private View refreshTipView;//刷新提示视图
    private View socialRefreshView;

    private Handler mHandler;
    private boolean isHide;
    private FlowAdapter flowAdapter;
    private ArrayList<Poster> posters;
    private ArrayList<CommunityFeed> recommendThreadList;
    private ArrayList<CommunityThread> prizeThreadList;
    private ArrayList<HotCommunityChannel> channelList;
    private LinearLayoutManager layoutManager;
    private SocialHotRecyclerAdapter adapter;
    private QaLiveEntranceData entranceData;

    private int bannerWidth;
    private int bannerHeight;
    private int entryWidth2;
    private int entryHeight2;
    private City city;
    private User user;
    private int offset;
    private int totalCount;
    private PointRecord pointRecord;//金币
    private boolean isFirstRefresh;
    private boolean isShow;
    private boolean isIntent;
    private int headHeight;

    private HljHttpSubscriber initSubscriber;//热门推荐(初始化)
    private HljHttpSubscriber refreshSubscriber;//热门推荐
    private HljHttpSubscriber cityRefreshSubscriber;//城市刷新
    private Poster cityPoster;

    public static SocialHotFragment newInstance() {
        return new SocialHotFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initValue();
    }


    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_social_hot, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        rootView.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        rootView.getViewTreeObserver()
                                .removeOnGlobalLayoutListener(this);
                        recyclerParentLayout.getLayoutParams().height = rootView.getHeight();
                    }
                });
        initView();
        initLoad();

        initTracker();
        return rootView;
    }

    private void initTracker() {
        HljVTTagger.tagViewParentName(recyclerView, "community_feed_list");
        HljVTTagger.tagViewParentName(viewFlow, "community_feed_list-community_banner_list");
        HljVTTagger.buildTagger(qaEntryLayout1)
                .tagName("qa_button")
                .hitTag();
        HljVTTagger.buildTagger(qaEntryLayout2)
                .tagName("qa_button")
                .hitTag();
        HljVTTagger.buildTagger(liveEntryLayout1)
                .tagName("live_button")
                .hitTag();
        HljVTTagger.buildTagger(liveEntryLayout2)
                .tagName("live_button")
                .hitTag();
    }


    private void initValue() {
        mHandler = new Handler();
        recommendThreadList = new ArrayList<>();
        channelList = new ArrayList<>();
        posters = new ArrayList<>();
        prizeThreadList = new ArrayList<>();
        flowAdapter = new FlowAdapter(getActivity(),
                posters,
                R.layout.flow_item_social_hot_home);
        isFirstRefresh = false;
        isShow = true;

        int contentWidth = CommonUtil.getDeviceSize(getContext()).x - CommonUtil.dp2px(getContext(),
                36);
        bannerWidth = (contentWidth * 458 / 678);
        bannerHeight = bannerWidth * 384 / 458;
        contentWidth = CommonUtil.getDeviceSize(getContext()).x - CommonUtil.dp2px(getContext(),
                40);
        entryWidth2 = contentWidth / 2;
        entryHeight2 = entryWidth2 * 168 / 335;

        city = Session.getInstance()
                .getMyCity(getContext());
        user = Session.getInstance()
                .getCurrentUser(getContext());
        isShow = true;
        if (user != null) {
            pointRecord = PointUtil.getInstance()
                    .getPointRecord(getActivity(), user.getId());
        }
    }

    private void initView() {
        backTopView.setOnClickListener(this);
        btnSignUpGold.setOnClickListener(this);
        pointRefresh(pointRecord);

        emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                initLoad();
            }
        });
        emptyView.setOnEmptyClickListener(new HljEmptyView.OnEmptyClickListener() {
            @Override
            public void onEmptyClickListener() {
                initLoad();
            }
        });

        //顶部banner
        viewFlow.setOverScrollMode(View.OVER_SCROLL_NEVER);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) bannerLayout
                .getLayoutParams();
        params.width = bannerWidth;
        params.height = bannerHeight;
        viewFlow.setPagerAdapter(flowAdapter);
        flowAdapter.setSliderLayout(viewFlow);
        viewFlow.setCustomIndicator(flowIndicator);

        if (flowAdapter.getCount() > 0) {
            bannerLayout.setVisibility(View.VISIBLE);
            if (flowAdapter.getCount() > 1) {
                viewFlow.startAutoCycle();
            }
        }
        // 顶部入口
        qaEntryLayout2.getLayoutParams().width = liveEntryLayout2.getLayoutParams().width =
                entryWidth2;
        qaEntryLayout2.getLayoutParams().height = liveEntryLayout2.getLayoutParams().height =
                entryHeight2;

        //刷新提示视图
        refreshTipView = getActivity().getLayoutInflater()
                .inflate(R.layout.social_hot_refresh_view, null);
        socialRefreshView = refreshTipView.findViewById(R.id.social_refresh_view);
        socialRefreshView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollToFeeds();
                scrollView.setRefreshing(true);
            }
        });
        //每日精选点击事件
        headTipView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollView.getRefreshableView()
                        .fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
        //热门推荐列表
        adapter = new SocialHotRecyclerAdapter(getActivity());
        adapter.setRefreshTipView(refreshTipView);
        adapter.setOnFeedItemClickListener(this);
        adapter.setOnQaItemClickListener(this);

        adapter.setOnReplyItemClickListener(this);

        layoutManager = new LinearLayoutManager(getContext());
        scrollView.setOnRefreshListener(this);
        layoutManager.setOrientation(VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        scrollView.setOnScrollListener(new PullToRefreshScrollView.OnScrollListener() {
            @Override
            public void onScrollChanged(int l, int t, int oldl, int oldt) {
                if (headHeight == 0) {
                    headHeight = socialHeaderView.getMeasuredHeight() + headTipView
                            .getMeasuredHeight();
                }
                int dy = t - oldt;
                if (dy > 0 && t >= headHeight) {
                    showLookThread();
                    return;
                }
                if (dy > 25 && isShow) {
                    //向上滑动距离大于25显示状态，隐藏
                    isShow = false;
                    hideToolbar();
                }
                if (dy < -25 && !isShow) {
                    isShow = true;
                    showToolbar();
                }
            }
        });
    }

    private void hideToolbar() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(btnSignUpGold,
                View.TRANSLATION_X,
                0,
                btnSignUpGold.getWidth());
        animator.setDuration(500);
        animator.start();
    }

    private void showToolbar() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(btnSignUpGold,
                View.TRANSLATION_X,
                btnSignUpGold.getWidth(),
                0);
        animator.setDuration(500);
        animator.start();
    }


    private void showLookThread() {
        if (!isIntent) {
            isIntent = true;
            Intent intent = new Intent();
            intent.setClass(getActivity(), RecommendThreadActivity.class);
            intent.putExtra("offset", offset);
            intent.putExtra("totalCount", totalCount);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivityForResult(intent, SCROLL_TOP_RESULT);
            getActivity().overridePendingTransition(0, 0);
            isIntent = false;
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void initLoad() {
        if (initSubscriber == null || initSubscriber.isUnsubscribed()) {
            offset = 0;
            //话题feeds流
            Observable<HljHttpCountData<List<CommunityFeed>>> hObservable = CommunityApi
                    .getRecommendListObb(
                    0,
                    20,
                    offset);
            //社区频道推荐
            Observable<List<HotCommunityChannel>> cObservable = CommunityApi.getChoiceListObb
                    (city.getId(),
                    1,
                    20);
            // banner
            Observable<PosterData> bObservable = CommonApi.getBanner(getContext(),
                    HljCommon.BLOCK_ID.SocialHotFragment,
                    city.getId());
            Observable<QaLiveEntranceData> qlObservable = CommunityApi.getQaLiveEntranceData();
            //有奖话题
            Observable<HljHttpData<List<CommunityThread>>> pObservable = CommunityApi
                    .getPrizeThreadListObb(
                    1,
                    20);

            Observable<ResultZip> observable = Observable.zip(hObservable,
                    cObservable,
                    bObservable,
                    pObservable,
                    qlObservable,
                    new Func5<HljHttpCountData<List<CommunityFeed>>, List<HotCommunityChannel>,
                            PosterData, HljHttpData<List<CommunityThread>>, QaLiveEntranceData,
                            ResultZip>() {
                        @Override
                        public ResultZip call(
                                HljHttpCountData<List<CommunityFeed>> listHljHttpData,
                                List<HotCommunityChannel> listHljHttpData2,
                                PosterData posterData,
                                HljHttpData<List<CommunityThread>> listHljHttpData3,
                                QaLiveEntranceData qaLiveEntranceData) {
                            ResultZip zip = new ResultZip();
                            if (listHljHttpData.getData() != null) {
                                zip.recommendThreadList.addAll(listHljHttpData.getData());
                                offset = listHljHttpData.getCurrentCount();
                                totalCount = listHljHttpData.getTotalCount();
                            }
                            if (listHljHttpData2 != null) {
                                zip.channelList.addAll(listHljHttpData2);
                            }
                            zip.qaLiveEntranceData = qaLiveEntranceData;
                            if (listHljHttpData3 != null) {
                                zip.prizeThreadList.addAll(listHljHttpData3.getData());
                            }
                            setZipPosters(zip, posterData);
                            return zip;
                        }
                    });

            initSubscriber = HljHttpSubscriber.buildSubscriber(getActivity())
                    .setOnNextListener(new SubscriberOnNextListener<ResultZip>() {
                        @Override
                        public void onNext(
                                ResultZip zip) {
                            recommendThreadList.clear();
                            setAdapterView(zip.recommendThreadList, false);
                            setCityChannelList(zip.channelList);
                            posters.clear();
                            posters.addAll(zip.posters);
                            entranceData = zip.qaLiveEntranceData;
                            prizeThreadList.clear();
                            prizeThreadList.addAll(zip.prizeThreadList);
                            setHeaderView();
                            adapter.notifyDataSetChanged();
                            isFirstRefresh = true;
                            scrollView.setRefreshing(true);
                        }
                    })
                    .setProgressBar(progressBar)
                    .setEmptyView(emptyView)
                    .setContentView(scrollView)
                    .setPullToRefreshBase(scrollView)
                    .build();
            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(initSubscriber);
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        if (refreshSubscriber == null || refreshSubscriber.isUnsubscribed()) {
            Observable<HljHttpCountData<List<CommunityFeed>>> hObservable = CommunityApi
                    .getRecommendListObb(
                    1,
                    8,
                    offset);
            //有奖话题
            Observable<HljHttpData<List<CommunityThread>>> pObservable = CommunityApi
                    .getPrizeThreadListObb(
                    1,
                    20);
            //社区频道推荐
            Observable<List<HotCommunityChannel>> cObservable = CommunityApi.getChoiceListObb
                    (city.getId(),
                    1,
                    20);
            Observable<QaLiveEntranceData> qlObservable = CommunityApi.getQaLiveEntranceData();
            Observable<ResultZip> observable = Observable.zip(hObservable,
                    pObservable,
                    cObservable,
                    qlObservable,
                    new Func4<HljHttpCountData<List<CommunityFeed>>,
                            HljHttpData<List<CommunityThread>>, List<HotCommunityChannel>,
                            QaLiveEntranceData, ResultZip>() {
                        @Override
                        public ResultZip call(
                                HljHttpCountData<List<CommunityFeed>> listHljHttpData,
                                HljHttpData<List<CommunityThread>> data2,
                                List<HotCommunityChannel> data3,
                                QaLiveEntranceData entranceData) {
                            ResultZip zip = new ResultZip();
                            zip.qaLiveEntranceData = entranceData;
                            if (listHljHttpData.getData() != null) {
                                zip.recommendThreadList.addAll(listHljHttpData.getData());
                                adapter.setNewCount(listHljHttpData.getData()
                                        .size());
                                offset = offset + listHljHttpData.getCurrentCount();
                                totalCount = listHljHttpData.getTotalCount();
                            }
                            if (data2.getData() != null) {
                                zip.prizeThreadList.addAll(data2.getData());
                            }
                            if (data3 != null) {
                                zip.channelList.addAll(data3);
                            }
                            return zip;
                        }
                    });

            refreshSubscriber = HljHttpSubscriber.buildSubscriber(getActivity())
                    .setOnNextListener(new SubscriberOnNextListener<ResultZip>() {
                        @Override
                        public void onNext(
                                ResultZip zip) {
                            //记录最后的刷新时间
                            if (zip.recommendThreadList.size() > 0) {
                                socialRefreshView.setVisibility(View.VISIBLE);
                            } else {
                                socialRefreshView.setVisibility(View.GONE);
                            }
                            setAdapterView(zip.recommendThreadList, true);
                            entranceData = zip.qaLiveEntranceData;
                            prizeThreadList.clear();
                            prizeThreadList.addAll(zip.prizeThreadList);
                            setCityChannelList(zip.channelList);
                            setHeaderView();
                            adapter.notifyDataSetChanged();
                        }
                    })
                    .setProgressBar(scrollView == null || scrollView.isRefreshing() ? null :
                            progressBar)
                    .setEmptyView(emptyView)
                    .setContentView(scrollView)
                    .setPullToRefreshBase(scrollView)
                    .build();
            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(refreshSubscriber);
        }
    }

    //切换城市，刷新banner和频道信息
    private void refreshBanner() {
        if (cityRefreshSubscriber == null || cityRefreshSubscriber.isUnsubscribed()) {
            Observable<PosterData> bObservable = CommonApi.getBanner(getContext(),
                    HljCommon.BLOCK_ID.SocialHotFragment,
                    city.getId());
            Observable<List<HotCommunityChannel>> cObservable = CommunityApi.getChoiceListObb
                    (city.getId(),
                    1,
                    20);
            Observable<QaLiveEntranceData> qlObservable = CommunityApi.getQaLiveEntranceData();
            Observable<ResultZip> observable = Observable.zip(bObservable,
                    cObservable,
                    qlObservable,
                    new Func3<PosterData, List<HotCommunityChannel>, QaLiveEntranceData,
                            ResultZip>() {
                        @Override
                        public ResultZip call(
                                PosterData posterData,
                                List<HotCommunityChannel> listHljHttpData,
                                QaLiveEntranceData entranceData) {
                            ResultZip zip = new ResultZip();
                            zip.qaLiveEntranceData = entranceData;
                            if (listHljHttpData != null) {
                                zip.channelList.addAll(listHljHttpData);
                            }
                            setZipPosters(zip, posterData);
                            return zip;
                        }
                    });
            cityRefreshSubscriber = HljHttpSubscriber.buildSubscriber(getActivity())
                    .setOnNextListener(new SubscriberOnNextListener<ResultZip>() {
                        @Override
                        public void onNext(ResultZip zip) {
                            posters.clear();
                            posters.addAll(zip.posters);
                            entranceData = zip.qaLiveEntranceData;
                            setCityChannelList(zip.channelList);
                            setHeaderView();
                        }
                    })
                    .setProgressBar(progressBar)
                    .build();
            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(cityRefreshSubscriber);
        }
    }

    private void setZipPosters(ResultZip zip, PosterData posterData) {
        List<Poster> posterList = PosterUtil.getPosterList(posterData.getFloors(),
                Constants.POST_SITES.SITE_BRIDE_TOP_CAROUSEL,
                false);
        zip.posters.addAll(posterList);
        List<Poster> cityPosters = PosterUtil.getPosterList(posterData.getFloors(),
                Constants.POST_SITES.SITE_BRIDE_LOCAL_CHANNEL,
                false);
        if (!CommonUtil.isCollectionEmpty(cityPosters)) {
            cityPoster = cityPosters.get(0);
        } else {
            cityPoster = null;
        }
    }

    private void setCityChannelList(ArrayList<HotCommunityChannel> list) {
        channelList.clear();
        if (cityPoster != null) {
            int count = 0;
            for (HotCommunityChannel channel : list) {
                count += channel.getEntity()
                        .getTodayWatchCount();
            }
            count = (int) (count / list.size() * 1.3);
            HotCommunityChannel cityChannel = new HotCommunityChannel(cityPoster.getPath(),
                    city.getName(),
                    count);

            this.channelList.add(cityChannel);
        }
        channelList.addAll(list);
    }

    private void setAdapterView(ArrayList<CommunityFeed> list, boolean isRefresh) {
        if (isRefresh) {
            recommendThreadList.addAll(0, list);
        } else {
            recommendThreadList.addAll(list);
        }
        RecommendThreadUtil.getInstance()
                .saveRecommendThreads(recommendThreadList);
        adapter.setRecommendThreads(recommendThreadList);
    }

    private void setHeaderView() {
        headHeight = 0;
        headTipView.setVisibility(View.VISIBLE);
        //banner视图
        flowAdapter.setmDate(posters);
        viewFlow.setPagerAdapter(flowAdapter);
        if (bannerLayout != null && viewFlow != null) {
            if (flowAdapter.getCount() == 0 || posters.size() == 0) {
                viewFlow.stopAutoCycle();
                bannerLayout.setVisibility(View.GONE);
            } else {
                bannerLayout.setVisibility(View.VISIBLE);
                if (flowAdapter.getCount() > 1) {
                    viewFlow.startAutoCycle();
                } else {
                    viewFlow.stopAutoCycle();
                }
            }
        }

        // 问答和直播入口数据
        setEntranceView();

        //热门频道视图
        if (!channelList.isEmpty()) {
            hotChannelView.setVisibility(View.VISIBLE);
            rlAllInfoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), ChannelListActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_right,
                            R.anim.activity_anim_default);
                }
            });
            int channelCount = flowLayout.getChildCount();
            int channelSize = channelList.size();
            if (channelSize >= 8) {
                channelSize = 8;
            }
            if (channelCount > channelSize) {
                flowLayout.removeViews(channelSize, channelCount - channelSize);
            }
            for (int i = 0; i < channelSize && i < 8; i++) {
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
                holder.setView(getContext(),
                        channelList.get(i)
                                .getEntity(),
                        i,
                        0);
                holder.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(int position, Object object) {
                        CommunityChannel communityChannel = (CommunityChannel) object;
                        if (communityChannel.getId() > 0) {
                            Intent intent = new Intent();
                            intent.setClass(getActivity(), CommunityChannelActivity.class);
                            intent.putExtra("id", communityChannel.getId());
                            intent.putExtra("type", communityChannel.getType());
                            startActivity(intent);
                        } else if (cityPoster != null) {
                            BannerUtil.bannerAction(getContext(), cityPoster, city, false, null);
                        }
                    }
                });
            }
        } else {
            hotChannelView.setVisibility(View.GONE);
        }
    }

    private void setEntranceView() {
        if (entranceData != null) {
            if (!posters.isEmpty()) {
                bannerEntryLayout.setVisibility(View.VISIBLE);
                entryLayout2.setVisibility(View.GONE);
                tvQaCount.setText("今+" + String.valueOf(entranceData.getQa()
                        .getCount()));
                tvQaDesc.setText(entranceData.getQa()
                        .getDesc());
                tvLiveDesc.setText(entranceData.getLive()
                        .getDesc());
                if (entranceData.getLive()
                        .getStatus() == LiveEntranceData.STATUS_LIVING) {
                    dotLiveStatus.setImageResource(R.drawable.anim_live_start);
                    ((AnimationDrawable) dotLiveStatus.getDrawable()).start();
                    tvLiveStatus.setText(R.string.label_live_in___live);
                } else {
                    dotLiveStatus.setImageResource(R.drawable.sp_oval_47d698);
                    tvLiveStatus.setText(R.string.label_live_prepare);
                }
                qaEntryLayout1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (getParentFragment() != null && getParentFragment() instanceof
                                EntranceViewClickListener) {
                            ((EntranceViewClickListener) getParentFragment()).onQATab();
                        }
                    }
                });
                liveEntryLayout1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (getParentFragment() != null && getParentFragment() instanceof
                                EntranceViewClickListener) {
                            ((EntranceViewClickListener) getParentFragment()).onLiveTab();
                        }
                    }
                });
            } else {
                bannerEntryLayout.setVisibility(View.GONE);
                entryLayout2.setVisibility(View.VISIBLE);
                tvQaCount2.setText("今+" + String.valueOf(entranceData.getQa()
                        .getCount()));
                tvQaDesc2.setText(entranceData.getQa()
                        .getDesc());
                tvLiveDesc2.setText(entranceData.getLive()
                        .getDesc());
                if (entranceData.getLive()
                        .getStatus() == LiveEntranceData.STATUS_LIVING) {
                    dotLiveStatus2.setImageResource(R.drawable.anim_live_start);
                    ((AnimationDrawable) dotLiveStatus2.getDrawable()).start();
                    tvLiveStatus2.setText(R.string.label_live_in___live);
                } else {
                    dotLiveStatus2.setImageResource(R.drawable.sp_oval_47d698);
                    tvLiveStatus2.setText(R.string.label_live_prepare);
                }
                qaEntryLayout2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (getParentFragment() != null && getParentFragment() instanceof
                                EntranceViewClickListener) {
                            ((EntranceViewClickListener) getParentFragment()).onQATab();
                        }
                    }
                });
                liveEntryLayout2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (getParentFragment() != null && getParentFragment() instanceof
                                EntranceViewClickListener) {
                            ((EntranceViewClickListener) getParentFragment()).onLiveTab();
                        }
                    }
                });
            }
        }
    }

    public void startAutoCycle() {
        if (viewFlow != null && flowAdapter.getCount() > 1) {
            viewFlow.startAutoCycle();
        }
    }

    public void stopAutoCycle() {
        if (viewFlow != null) {
            viewFlow.stopAutoCycle();
        }
    }

    /**
     * Feed点击事件
     *
     * @param position
     * @param item
     * @param type
     */
    @Override
    public void onFeedItemClickListener(
            int position, long id, Object item, int type) {
        Intent intent = new Intent();
        switch (type) {
            case SocialHotRecyclerAdapter.THREAD_TYPE:
                CommunityThread communityThread = (CommunityThread) item;
                intent.setClass(getActivity(), CommunityThreadDetailActivity.class);
                JSONObject jsonObject = TrackerUtil.getSiteJson("D1/G1", position + 1, null);
                if (jsonObject != null) {
                    intent.putExtra("site", jsonObject.toString());
                }
                intent.putExtra("id", communityThread.getId());
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right,
                        R.anim.activity_anim_default);
                break;
            case SocialHotRecyclerAdapter.QUESTION_TYPE:
                Question question = (Question) item;
                intent.setClass(getActivity(), QuestionDetailActivity.class);
                JSONObject jsonObject1 = TrackerUtil.getSiteJson("D1/G1", position + 1, null);
                if (jsonObject1 != null) {
                    intent.putExtra("site", jsonObject1.toString());
                }
                intent.putExtra("questionId", question.getId());
                getActivity().startActivity(intent);
                break;
        }
    }

    @Override
    public View getScrollableView() {
        return scrollView == null ? null : scrollView.getRefreshableView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        RecommendThreadUtil.getInstance()
                .cleanRecommendThreads();
    }

    @Override
    public void onReply(CommunityThread item, int position) {
        if (!AuthUtil.loginBindCheck(getContext())) {
            return;
        }
        if (item.getPostCount() == 0) {
            Intent intent = new Intent(getActivity(), CreatePostActivity.class);
            intent.putExtra(CreatePostActivity.ARG_POST, item.getPost());
            intent.putExtra(CreatePostActivity.ARG_POSITION, position);
            intent.putExtra(CreatePostActivity.ARG_IS_REPLY_THREAD, true);
            startActivityForResult(intent, REPLY_RESULT);
            getActivity().overridePendingTransition(R.anim.slide_in_from_bottom,
                    R.anim.activity_anim_default);
        }
    }

    @Override
    public void onQaItemClickListener() {
        if (getParentFragment() != null && getParentFragment() instanceof
                EntranceViewClickListener) {
            ((EntranceViewClickListener) getParentFragment()).onQATab();
        }
    }

    private class ResultZip {
        ArrayList<CommunityFeed> recommendThreadList = new ArrayList<>();
        ArrayList<HotCommunityChannel> channelList = new ArrayList<>();
        ArrayList<CommunityThread> prizeThreadList = new ArrayList<>();
        ArrayList<Poster> posters = new ArrayList<>();
        QaLiveEntranceData qaLiveEntranceData;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void refresh(Object... params) {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backtop_btn:
                scrollTop();
                break;
            case R.id.btn_sign_up_gold:
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
                break;
            default:
                break;
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

    /**
     * 选择城市之后，刷新数据
     *
     * @param c
     */
    public void cityRefresh(City c) {
        if (!city.getId()
                .equals(c.getId())) {
            city = c;
            if (flowAdapter != null) {
                flowAdapter.setCity(city);
            }
            scrollTop();
            refreshBanner();
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
                    initLoad();
                }
            } else {
                user = u;
                scrollTop();
                initLoad();
            }
        } else if (user != null) {
            user = null;
            scrollTop();
            initLoad();
        }
    }

    //滑动到顶部
    public void scrollTop() {
        scrollView.getRefreshableView()
                .fullScroll(ScrollView.FOCUS_UP);
    }

    //滑动到feeds流视图
    public void scrollToFeeds() {
        //        if (layoutManager == null || recyclerView == null) {
        //            return;
        //        }
        //        recyclerView.getRefreshableView()
        //                .stopScroll();
        //        mIndex = 1;
        //        smoothMoveToPosition(mIndex);
    }

    private void showFiltrateAnimation() {
        if (backTopView == null) {
            return;
        }
        isHide = false;
        if (isAnimEnded()) {
            Animation animation = AnimationUtils.loadAnimation(getActivity(),
                    R.anim.slide_in_bottom2);
            animation.setFillBefore(true);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (isHide) {
                                hideFiltrateAnimation();
                            }
                        }
                    });
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            backTopView.startAnimation(animation);
        }
    }

    private boolean isAnimEnded() {
        return backTopView != null && (backTopView.getAnimation() == null || backTopView
                .getAnimation()
                .hasEnded());
    }

    private void hideFiltrateAnimation() {
        if (backTopView == null) {
            return;
        }
        isHide = true;
        if (isAnimEnded()) {
            Animation animation = AnimationUtils.loadAnimation(getActivity(),
                    R.anim.slide_out_bottom2);
            animation.setFillAfter(true);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (!isHide) {
                                showFiltrateAnimation();
                            }
                        }
                    });
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            backTopView.startAnimation(animation);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REPLY_RESULT:
                    int position = data.getIntExtra(CreatePostActivity.ARG_POSITION, 0);
                    CommunityThread thread = (CommunityThread) recommendThreadList.get(position)
                            .getEntity();
                    if (thread != null) {
                        thread.setPostCount(thread.getPostCount() + 1);
                    }
                    adapter.notifyDataSetChanged();
                    break;
                case SCROLL_TOP_RESULT:
                    if (data != null) {
                        offset = data.getIntExtra("offset", offset);
                        ArrayList<CommunityFeed> threads = RecommendThreadUtil.getInstance()
                                .getRecommendThreads();
                        if (!threads.isEmpty()) {
                            recommendThreadList.clear();
                            recommendThreadList.addAll(threads);
                            adapter.setRecommendThreads(recommendThreadList);
                            adapter.notifyDataSetChanged();
                        }
                        scrollTop();
                    }
                    break;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CommonUtil.unSubscribeSubs(refreshSubscriber, initSubscriber, cityRefreshSubscriber);
    }

    static class ChannelItemViewHolder {
        @BindView(R.id.tv_head_name)
        TextView tvHeadName;
        @BindView(R.id.channel_head_view)
        LinearLayout channelHeadView;
        @BindView(R.id.channel_bottom_line)
        View channelBottomLine;
        @BindView(R.id.iv_find_channel_img)
        RoundedImageView ivFindChannelImg;
        @BindView(R.id.iv_same_city)
        TextView ivSameCity;
        @BindView(R.id.tv_find_channel_name)
        TextView tvFindChannelName;
        @BindView(R.id.tv_find_channel_dec)
        TextView tvFindChannelDec;
        @BindView(R.id.tv_find_channel_hot_count)
        TextView tvFindChannelHotCount;
        @BindView(R.id.tv_find_channel_post_count)
        TextView tvFindChannelPostCount;
        @BindView(R.id.tv_find_channel_popularity)
        TextView tvFindChannelPopularity;
        @BindView(R.id.channel_view)
        RelativeLayout channelView;
        @BindView(R.id.iv_channel_default)
        ImageView ivChannelDefault;

        ChannelItemViewHolder(View view) {ButterKnife.bind(this, view);}
    }


    static class ChannelViewHolder {
        @BindView(R.id.iv_arrow_right)
        ImageView ivArrowRight;
        @BindView(R.id.rl_all_info_view)
        RelativeLayout rlAllInfoView;
        @BindView(R.id.line_layout)
        View lineLayout;

        ChannelViewHolder(View view) {ButterKnife.bind(this, view);}
    }

    static class ViewHolder {
        @BindView(R.id.iv_more_cover)
        ImageView ivMoreCover;
        @BindView(R.id.tv_more_name)
        TextView tvMoreName;
        @BindView(R.id.more_view)
        RelativeLayout moreView;

        ViewHolder(View view) {ButterKnife.bind(this, view);}
    }

    @Override
    public String fragmentPageTrackTagName() {
        return "社区";
    }


    public interface EntranceViewClickListener {


        void onLiveTab();


        void onQATab();

    }
}
