package me.suncloud.marrymemo.fragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.models.event.EventInfo;
import com.hunliji.hljcommonlibrary.models.search.NewHotKeyWord;
import com.hunliji.hljcommonlibrary.utils.AnimUtil;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.OncePrefUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.fragments.HomePageScrollAbleFragment;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljcommonlibrary.views.widgets.HljHorizontalScrollView;
import com.hunliji.hljcommonlibrary.views.widgets.PullToRefreshScrollableLayout;
import com.hunliji.hljcommonlibrary.views.widgets.ScrollableLayout;
import com.hunliji.hljcommonlibrary.views.widgets.SpringScrollView;
import com.hunliji.hljcommonlibrary.views.widgets.TabPageIndicator;
import com.hunliji.hljhttplibrary.api.CommonApi;
import com.hunliji.hljhttplibrary.api.newsearch.NewSearchApi;
import com.hunliji.hljhttplibrary.entities.HljHttpResultZip;
import com.hunliji.hljhttplibrary.entities.HljRZField;
import com.hunliji.hljhttplibrary.entities.PosterSite;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.PosterAnnotationFunc;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.slider.library.ClipSliderLayout;
import com.slider.library.Indicators.CirclePageExIndicator;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.ClipFlowAdapter;
import me.suncloud.marrymemo.adpter.home.HomeFeedsFragmentCallBack;
import me.suncloud.marrymemo.api.ad.MadApi;
import me.suncloud.marrymemo.api.home.HomeApi;
import me.suncloud.marrymemo.fragment.home.HomeFeedsFragment;
import me.suncloud.marrymemo.fragment.product.HomeProductListFragment;
import me.suncloud.marrymemo.fragment.product.HomeSelectedProductListFragment;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.model.FeedProperty;
import me.suncloud.marrymemo.model.ad.MadPoster;
import me.suncloud.marrymemo.util.BannerUtil;
import me.suncloud.marrymemo.util.DataConfig;
import me.suncloud.marrymemo.util.NoticeUtil;
import me.suncloud.marrymemo.util.SameTextsUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.TrackerUtil;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.CityListActivity;
import me.suncloud.marrymemo.view.HotelChannelActivity;
import me.suncloud.marrymemo.view.ShoppingCartActivity;
import me.suncloud.marrymemo.view.ShoppingCategoryActivity;
import me.suncloud.marrymemo.view.newsearch.NewSearchActivity;
import me.suncloud.marrymemo.view.notification.MessageHomeActivity;
import me.suncloud.marrymemo.view.themephotography.ThemeHotCityActivity;
import me.suncloud.marrymemo.widget.CustomViewPager;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by Suncloud on 2015/6/30.
 */
public class HomePageFragment extends RefreshFragment implements PullToRefreshBase
        .OnRefreshListener<ScrollableLayout>, me.suncloud.marrymemo.widget.TabPageIndicator
        .OnTabChangeListener, HomeFeedsFragmentCallBack {

    @Override
    public String fragmentPageTrackTagName() {
        return "首页";
    }

    public static final String TRACK_TAG_NAME = "main_banner_item";
    public static final String CPM_SOURCE = "home_page_top_banner";
    @BindView(R.id.posters_view)
    ClipSliderLayout postersView;
    @BindView(R.id.flow_indicator)
    CirclePageExIndicator flowIndicator;
    @BindView(R.id.top_posters_layout)
    RelativeLayout topPostersLayout;
    @BindView(R.id.grid_poster)
    GridLayout gridPoster;
    @BindView(R.id.buttons_scroll)
    HljHorizontalScrollView buttonsScroll;
    @BindView(R.id.scroll_content)
    View scrollContent;
    @BindView(R.id.buttons_scroll2)
    FrameLayout buttonsScroll2;
    @BindView(R.id.layout_poster_buttons)
    LinearLayout layoutPosterButtons;
    @BindView(R.id.single_promotion_view)
    ImageView singlePromotionView;
    @BindView(R.id.tv_more_destination)
    TextView tvMoreDestination;
    @BindView(R.id.img_travel_layout)
    LinearLayout imgTravelLayout;
    @BindView(R.id.img_travel_four_layout)
    GridLayout imgTravelFourLayout;
    @BindView(R.id.lvpai_destination)
    GridLayout lvpaiDestination;
    @BindView(R.id.travel_poster_layout)
    LinearLayout travelPosterLayout;
    @BindView(R.id.wedding_flow_indicator)
    TabPageIndicator weddingFlowIndicator;
    @BindView(R.id.wedding_flow_viewpager)
    CustomViewPager weddingFlowViewpager;
    @BindView(R.id.img_experience_shop)
    ImageView imgExperienceShop;
    @BindView(R.id.ll_wedding_flow)
    LinearLayout llWeddingFlow;
    @BindView(R.id.tv_more_product)
    TextView tvMoreProduct;
    @BindView(R.id.layout_product_img)
    LinearLayout layoutProductImg;
    @BindView(R.id.layout_product)
    LinearLayout layoutProduct;
    @BindView(R.id.tv_more_hotel)
    TextView tvMoreHotel;
    @BindView(R.id.layout_hotel_img)
    LinearLayout layoutHotelImg;
    @BindView(R.id.layout_hotel)
    LinearLayout layoutHotel;
    @BindView(R.id.img_stroll_layout)
    LinearLayout imgStrollLayout;
    @BindView(R.id.marriage_stroll_layout)
    LinearLayout marriageStrollLayout;
    @BindView(R.id.single_poster_view)
    ImageView singlePosterView;
    @BindView(R.id.header_layout)
    LinearLayout headerLayout;
    @BindView(R.id.tabpage_indicator)
    me.suncloud.marrymemo.widget.TabPageIndicator tabpageIndicator;
    @BindView(R.id.page_indicator_line)
    View pageIndicatorLine;
    @BindView(R.id.viewpager)
    ViewPager viewpager;
    @BindView(R.id.scrollable_layout)
    PullToRefreshScrollableLayout scrollableLayout;
    @BindView(R.id.backtop_btn)
    ImageButton backtopBtn;
    @BindView(R.id.empty_layout)
    HljEmptyView emptyLayout;
    @BindView(R.id.arrow_r)
    ImageView arrowR;
    @BindView(R.id.label_city_r)
    TextView labelCityR;
    @BindView(R.id.city_layout_r)
    LinearLayout cityLayoutR;
    @BindView(R.id.tv_hot_word)
    TextView tvHotWord;
    @BindView(R.id.searchView_r)
    RelativeLayout searchViewR;
    @BindView(R.id.msg_layout_r)
    ImageButton msgLayoutR;
    @BindView(R.id.title_line)
    View titleLine;
    @BindView(R.id.msg_notice)
    View msgNotice;
    @BindView(R.id.msg_count)
    TextView msgCount;
    @BindView(R.id.action_layout)
    LinearLayout actionLayout;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.line_poster_buttons)
    View linePosterButtons;
    @BindView(R.id.line_travel_poster)
    View lineTravelPoster;
    @BindView(R.id.line_wedding_flow)
    View lineWeddingFlow;
    @BindView(R.id.line_product)
    View lineProduct;
    @BindView(R.id.line_hotel)
    View lineHotel;
    @BindView(R.id.line_marriage_stroll)
    View lineMarriageStroll;

    private City city;

    private ArrayList<FeedProperty> tabPropertyList;
    private PropertyFragmentAdapter fragmentAdapter;

    private PosterMeasures posterMeasures;
    private NoticeUtil noticeUtil;

    private List<Poster> topPosters; //首页轮播banner
    private ClipFlowAdapter flowAdapter;
    private ArrayList<EventInfo> events;

    private boolean loadPosterError;
    private String propertyId; // 当前propertyId
    private boolean isHide; // 回到顶部按钮隐藏状态
    private Map<String, Boolean> childEmptyMap; //FeedsFragment 空标识

    private HljHttpSubscriber headerSubscriber;
    private Subscription propertiesSubscription;
    private Subscription madSubscription;
    private Unbinder unbinder;
    private HljHttpSubscriber inputSubscriber;
    private NewHotKeyWord newHotKeyWord;
    private SparseArray<List<Poster>> weddingFlowPoster;
    private WeddingFlowAdapter weddingFlowAdapter;

    private AnimatorSet scrollAnimatorSet;
    private boolean isPause;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        tabPropertyList = new ArrayList<>();
        events = new ArrayList<>();
        childEmptyMap = new HashMap<>();
        topPosters = new ArrayList<>();
        posterMeasures = new PosterMeasures(getContext());
        city = Session.getInstance()
                .getMyCity(getActivity());

        flowAdapter = new ClipFlowAdapter(getActivity(), topPosters);
        flowAdapter.setCity(city);
        weddingFlowPoster = new SparseArray<>();
        weddingFlowAdapter = new WeddingFlowAdapter(weddingFlowPoster);
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home_page, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        HljBaseActivity.setActionBarPadding(getContext(), actionLayout);
        initPosterHeader();
        initWeddingFlowViewpager();
        initViewPager();
        initScrollLayout();
        initEmptyLayout();
        initNotice();
        getHeaderData();
        initTracker();
        return rootView;
    }

    private void initViewPager() {
        fragmentAdapter = new PropertyFragmentAdapter(getChildFragmentManager());
        tabpageIndicator.setTabViewId(R.layout.menu_tab_widget2);
        tabpageIndicator.setOnTabChangeListener(this);
        tabpageIndicator.setPagerAdapter(fragmentAdapter);
        viewpager.setAdapter(fragmentAdapter);
        viewpager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                if (position >= tabPropertyList.size()) {
                    return;
                }
                propertyId = tabPropertyList.get(position)
                        .getStringId();
                tabpageIndicator.setCurrentItem(position);

                // 刷新回到顶部按钮状态
                HomePageScrollAbleFragment feedsFragment = getCurrentFragment();
                if (feedsFragment != null) {
                    scrollableLayout.getRefreshableView()
                            .getHelper()
                            .setCurrentScrollableContainer(feedsFragment);
                    if (feedsFragment.getScrollableView() == null) {
                        return;
                    }
                    RecyclerView recycleView = (RecyclerView) feedsFragment.getScrollableView();
                    if (recycleView != null && recycleView.getLayoutManager() != null) {
                        int itemPosition = 0;
                        if (recycleView.getLayoutManager() instanceof LinearLayoutManager) {
                            LinearLayoutManager manager = (LinearLayoutManager) recycleView
                                    .getLayoutManager();
                            itemPosition = manager.findFirstVisibleItemPosition();
                        } else if (recycleView.getLayoutManager() instanceof
                                StaggeredGridLayoutManager) {
                            StaggeredGridLayoutManager gridLayoutManager =
                                    (StaggeredGridLayoutManager) recycleView.getLayoutManager();
                            int[] positions = gridLayoutManager.findLastVisibleItemPositions(new
                                    int[2]);


                            itemPosition = positions[0];
                        }
                        if (itemPosition > 3) {
                            backtopBtn.setVisibility(VISIBLE);
                            isHide = false;
                        } else {
                            backtopBtn.setVisibility(GONE);
                            isHide = true;
                        }
                    }
                }
            }
        });
    }

    private void initNotice() {
        noticeUtil = new NoticeUtil(getContext(), msgCount, msgNotice);
        // cityViewR 内容同步
        new SameTextsUtil(labelCityR);
        labelCityR.setText(Util.maxEmsText(city.getName(), 3));
    }

    private void initEmptyLayout() {
        emptyLayout.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                progressBar.setVisibility(VISIBLE);
                getFeedsProperty();
                getInputBoxHotWord();
                onRefresh(null);
            }
        });
    }

    private void initScrollLayout() {
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
                        setActionLayoutAlpha(currentY);
                    }
                });
    }

    private void initWeddingFlowViewpager() {
        weddingFlowIndicator.setTabViewId(R.layout.menu_tab_view4___cm);
        weddingFlowIndicator.setOnTabChangeListener(new TabPageIndicator.OnTabChangeListener() {
            @Override
            public void onTabChanged(int position) {
                weddingFlowViewpager.setCurrentItem(position);
            }
        });
        weddingFlowViewpager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                weddingFlowIndicator.setCurrentItem(position);
                super.onPageSelected(position);
            }
        });
        weddingFlowViewpager.getLayoutParams().height = posterMeasures.weddingFlowviewpagerHeight;
        weddingFlowIndicator.setPagerAdapter(weddingFlowAdapter);
        weddingFlowViewpager.setAdapter(weddingFlowAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (noticeUtil != null) {
            noticeUtil.onResume();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getInputBoxHotWord();
        getFeedsProperty();
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
                        if (hotKeyWord != null && !TextUtils.isEmpty(hotKeyWord.getTitle())) {
                            tvHotWord.setText(hotKeyWord.getTitle());
                        } else {
                            tvHotWord.setText(null);
                        }
                    }
                })
                .setDataNullable(true)
                .build();
        NewSearchApi.getInputWord(NewSearchApi.InputType.TYPE_HOME_PAGE)
                .subscribe(inputSubscriber);
    }

    private void initTracker() {
        HljVTTagger.tagViewParentName(postersView, "main_header_banner_slider");
        flowAdapter.setViewTrackTagName(TRACK_TAG_NAME);
        flowAdapter.setCpmSource(CPM_SOURCE);
    }

    /**
     * 初始化顶部poster
     */
    private void initPosterHeader() {
        topPostersLayout.getLayoutParams().height = posterMeasures.topPosterHeight;
        ((RelativeLayout.LayoutParams) postersView.getLayoutParams()).topMargin = -posterMeasures
                .statusBarHeight;
        postersView.getmViewPager()
                .setOffscreenPageLimit(2);
        postersView.setPagerAdapter(flowAdapter);
        flowAdapter.setSliderLayout(postersView);
        postersView.setCustomIndicator(flowIndicator);
        postersView.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (topPosters == null || topPosters.isEmpty()) {
                    return;
                }
                Poster poster = topPosters.get(position % topPosters.size());
                if (poster instanceof MadPoster) {
                    if (madSubscription != null && !madSubscription.isUnsubscribed()) {
                        return;
                    }
                    //通过更新mad广告发送展示统计
                    madSubscription = MadApi.getHomeMadAd(getContext())
                            .subscribe(new Subscriber<MadPoster>() {
                                @Override
                                public void onCompleted() {
                                }

                                @Override
                                public void onError(Throwable e) {
                                    e.printStackTrace();
                                }

                                @Override
                                public void onNext(MadPoster madPoster) {
                                }
                            });
                }
            }
        });

        singlePromotionView.getLayoutParams().height = posterMeasures.singlePromotionImageHeight;
        singlePosterView.getLayoutParams().height = posterMeasures.singlePromotionImageHeight;

        //旅拍三图
        for (int i = 0, size = imgTravelLayout.getChildCount(); i < size; i++) {
            View child = imgTravelLayout.getChildAt(i);
            child.getLayoutParams().width = posterMeasures.lvPaiWidth;
            child.getLayoutParams().height = posterMeasures.lvPaiHeight;
        }
        //旅拍四图
        for (int i = 0, size = imgTravelFourLayout.getChildCount(); i < size; i++) {
            View child = imgTravelFourLayout.getChildAt(i);
            child.getLayoutParams().width = posterMeasures.lvPaiWidth2;
            child.getLayoutParams().height = posterMeasures.lvPaiHeight2;
        }

        //体验店
        imgExperienceShop.getLayoutParams().width = posterMeasures.imgExperienceShopWidth;
        imgExperienceShop.getLayoutParams().height = posterMeasures.imgExperienceShopHeight;

        //婚品图片
        for (int i = 0, size = layoutProductImg.getChildCount(); i < size; i++) {
            View child = layoutProductImg.getChildAt(i);
            child.getLayoutParams().width = posterMeasures.imgProductWidth;
            child.getLayoutParams().height = posterMeasures.imgProductHeight;
        }
        //婚宴图片 婚宴图片和备婚必逛图片大小一样
        for (int i = 0, size = layoutHotelImg.getChildCount(); i < size; i++) {
            View child = layoutHotelImg.getChildAt(i);
            child.getLayoutParams().width = posterMeasures.imgStrollWidth;
            child.getLayoutParams().height = posterMeasures.imgStrollHeight;
        }

        //备婚必逛
        for (int i = 0, size = imgStrollLayout.getChildCount(); i < size; i++) {
            View child = imgStrollLayout.getChildAt(i);
            child.getLayoutParams().width = posterMeasures.imgStrollWidth;
            child.getLayoutParams().height = posterMeasures.imgStrollHeight;
        }
    }

    /**
     * 首页feed流FeedProperty
     */
    private void getFeedsProperty() {
        if (propertiesSubscription != null && !propertiesSubscription.isUnsubscribed()) {
            return;
        }
        propertiesSubscription = HomeApi.getFeedProperties(getContext(), city.getId())
                .subscribe(new Subscriber<List<FeedProperty>>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(List<FeedProperty> properties) {
                        setFeedProperty(properties);
                    }
                });
    }

    private void setFeedProperty(List<FeedProperty> properties) {
        if (properties != null && !properties.isEmpty()) {
            childEmptyMap.clear();
            tabPropertyList.clear();
            tabPropertyList.addAll(properties);
            boolean isProduct = false;
            if (!tabPropertyList.isEmpty()) {
                isProduct = tabPropertyList.get(0)
                        .isShopProduct();
            }
            if (isProduct) {
                //添加精选FeedProperty
                FeedProperty select = new FeedProperty();
                select.setName("精选");
                select.setIdStr("0");
                select.setShopProduct(true);
                tabPropertyList.add(0, select);
            }
            if (!tabPropertyList.isEmpty()) {
                propertyId = tabPropertyList.get(0)
                        .getStringId();
            }
            if (viewpager.getAdapter() == null) {
                fragmentAdapter = new PropertyFragmentAdapter(getChildFragmentManager());
                tabpageIndicator.setTabViewId(R.layout.menu_tab_widget2);
                tabpageIndicator.setOnTabChangeListener(this);
                tabpageIndicator.setPagerAdapter(fragmentAdapter);
                viewpager.setAdapter(fragmentAdapter);
            } else {
                tabpageIndicator.setPagerAdapter(fragmentAdapter);
                fragmentAdapter.notifyDataSetChanged();
            }
            viewpager.setOffscreenPageLimit(tabPropertyList.size() - 1);
            if (!tabPropertyList.isEmpty()) {
                viewpager.setCurrentItem(0);
                tabpageIndicator.setCurrentItem(0);
            }
        }
    }

    /**
     * 首页header 数据的获取
     */
    private void getHeaderData() {
        if (headerSubscriber != null && !headerSubscriber.isUnsubscribed()) {
            return;
        }
        headerSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                .setProgressBar(scrollableLayout.isRefreshing() ? null : progressBar)
                .setPullToRefreshBase(scrollableLayout)
                .setOnNextListener(new SubscriberOnNextListener<HomePagePosterZip>() {
                    @Override
                    public void onNext(HomePagePosterZip posterData) {
                        tabpageIndicator.setVisibility(VISIBLE);
                        pageIndicatorLine.setVisibility(VISIBLE);
                        viewpager.setVisibility(VISIBLE);
                        if (posterData != null) {
                            emptyLayout.hideEmptyView();
                            scrollableLayout.setVisibility(VISIBLE);
                            headerLayout.setVisibility(VISIBLE);
                            loadPosterError = false;
                            setHeaderBanner(posterData);
                        } else {
                            onHeaderError();
                        }
                    }
                })
                .setOnErrorListener(new SubscriberOnErrorListener<Throwable>() {
                    @Override
                    public void onError(Throwable e) {
                        tabpageIndicator.setVisibility(VISIBLE);
                        pageIndicatorLine.setVisibility(VISIBLE);
                        viewpager.setVisibility(VISIBLE);
                        onHeaderError();
                        if (e instanceof SocketTimeoutException || e instanceof ConnectException
                                || e instanceof UnknownHostException) {
                            scrollableLayout.setVisibility(GONE);
                            emptyLayout.showEmptyView();
                        }
                    }
                })
                .build();

        getHomePagePosterZipObb().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(headerSubscriber);
    }

    private Observable<HomePagePosterZip> getHomePagePosterZipObb() {

        Observable<HomePagePosterZip> hljPosterObb = CommonApi.getBanner(getContext(),
                HljCommon.BLOCK_ID.HomePageFragment_V2,
                city.getId())
                .map(new PosterAnnotationFunc<>(new HomePagePosterZip()));

        return Observable.zip(hljPosterObb,
                getMadPosterObb(),
                new Func2<HomePagePosterZip, Map.Entry<Integer, MadPoster>, HomePagePosterZip>() {
                    @Override
                    public HomePagePosterZip call(
                            HomePagePosterZip homePagePosterZip,
                            Map.Entry<Integer, MadPoster> madPosterEntry) {
                        if (madPosterEntry != null) {
                            homePagePosterZip.addMadPoster(madPosterEntry.getKey(),
                                    madPosterEntry.getValue());
                        }
                        return homePagePosterZip;
                    }
                });
    }

    private Observable<Map.Entry<Integer, MadPoster>> getMadPosterObb() {
        DataConfig dataConfig = Session.getInstance()
                .getDataConfig(getContext());
        if (dataConfig != null && dataConfig.getMadAdMainBannerIndex() > 0) {
            final int finalIndex = dataConfig.getMadAdMainBannerIndex();
            return MadApi.getHomeMadAd(getContext())
                    .map(new Func1<MadPoster, Map.Entry<Integer, MadPoster>>() {
                        @Override
                        public Map.Entry<Integer, MadPoster> call(MadPoster madPoster) {
                            if (!TextUtils.isEmpty(madPoster.getPath())) {
                                return new AbstractMap.SimpleEntry<>(finalIndex, madPoster);
                            }
                            return null;
                        }
                    })
                    .onErrorReturn(new Func1<Throwable, Map.Entry<Integer, MadPoster>>() {
                        @Override
                        public Map.Entry<Integer, MadPoster> call(Throwable throwable) {
                            return null;
                        }
                    });
        } else {
            return Observable.just(null);
        }
    }

    /**
     * actionLayout 切换
     *
     * @param currentY 当前滑动位置
     */
    private void setActionLayoutAlpha(int currentY) {
        float rate;
        if (topPostersLayout.getVisibility() != GONE && headerLayout.getVisibility() != GONE) {
            //只有顶部banner显示时才显示透明actionLayout
            rate = Math.min(currentY * 1.0f / (posterMeasures.topPosterHeight - posterMeasures
                            .actionBarHeight),
                    1.0f);
            if (rate < 1 && flowAdapter.getCount() > 1) {
                //顶部banner开始滚动
                postersView.startAutoCycle();
            } else if (rate == 1) {
                //顶部banner停止
                postersView.stopAutoCycle();
            }
        }
        int childCount = headerLayout.getChildCount();
        int height = 0;
        for (int i = 0; i < childCount; i++) {
            View childView = headerLayout.getChildAt(i);
            if (childView.getVisibility() == VISIBLE && (childView instanceof ViewGroup ||
                    childView instanceof ImageView)) {
                height = childView.getMeasuredHeight();
                break;
            }
        }
        if (currentY > height) {
            titleLine.setVisibility(VISIBLE);
        } else {
            titleLine.setVisibility(GONE);
        }
    }

    /**
     * @return 当前FeedsFragment
     */
    private HomePageScrollAbleFragment getCurrentFragment() {
        if (viewpager.getAdapter() != null && viewpager.getAdapter()
                .getCount() > 0 && viewpager.getAdapter() instanceof PropertyFragmentAdapter) {
            PropertyFragmentAdapter adapter = (PropertyFragmentAdapter) viewpager.getAdapter();
            Fragment fragment = (Fragment) adapter.instantiateItem(viewpager,
                    viewpager.getCurrentItem());
            if (fragment != null && fragment instanceof HomePageScrollAbleFragment && fragment
                    .isAdded() && !fragment.isDetached()) {
                return (HomePageScrollAbleFragment) fragment;
            }
        }
        return null;
    }

    private void setHeaderBanner(HomePagePosterZip posterZip) {
        //banner
        topPosters = posterZip.topSliderPosters;
        flowAdapter.setmDate(topPosters);
        postersView.setPagerAdapter(flowAdapter);
        if (CommonUtil.getCollectionSize(topPosters) > 1) {
            postersView.setCurrentItem(topPosters.size() * 100, false);
        }
        if (flowAdapter.getCount() == 0) {
            postersView.stopAutoCycle();
            topPostersLayout.setVisibility(GONE);
        } else {
            topPostersLayout.setVisibility(VISIBLE);
        }

        setActionLayoutAlpha(scrollableLayout.getRefreshableView()
                .getScrollY());

        //button
        setPosterButtons(posterZip.posterButtons);

        //首页大促
        if (posterZip.singlePoster != null) {
            linePosterButtons.setVisibility(VISIBLE);
            singlePromotionView.setVisibility(VISIBLE);
            setPosterViewValue(singlePromotionView,
                    singlePromotionView,
                    null,
                    posterZip.singlePoster,
                    null,
                    0,
                    CommonUtil.getDeviceSize(getContext()).x,
                    posterMeasures.singlePromotionImageHeight,
                    false,
                    0);
        } else {
            singlePromotionView.setVisibility(GONE);
        }

        //旅拍
        List<Poster> posters = posterZip.travelPosters;
        imgTravelFourLayout.setVisibility(GONE);
        imgTravelLayout.setVisibility(GONE);
        ViewGroup imgGroup = imgTravelFourLayout;
        int imgWidth = posterMeasures.lvPaiWidth2;
        int imgHeight = posterMeasures.lvPaiHeight2;
        if (CommonUtil.isCollectionEmpty(posters)) {
            lineTravelPoster.setVisibility(VISIBLE);
            posters = posterZip.oldTravelPosters;
            imgGroup = imgTravelLayout;
            imgTravelFourLayout.setVisibility(GONE);
            imgWidth = posterMeasures.lvPaiWidth;
            imgHeight = posterMeasures.lvPaiHeight;
        }
        if (!CommonUtil.isCollectionEmpty(posters)) {
            imgGroup.setVisibility(VISIBLE);
            for (int i = 0, size = imgGroup.getChildCount(); i < size; i++) {
                View view = imgGroup.getChildAt(i);
                if (i >= posters.size()) {
                    view.setVisibility(GONE);
                    continue;
                }
                if (view instanceof ImageView) {
                    ImageView imageView = (ImageView) view;
                    Poster p = posters.get(i);
                    setPosterViewValue(imageView,
                            imageView,
                            null,
                            p,
                            null,
                            i,
                            imgWidth,
                            imgHeight,
                            false,
                            3);
                }
            }
        } else {
            imgGroup.setVisibility(GONE);
        }

        //旅拍目的地
        posters = posterZip.destinationPosters;
        if (!CommonUtil.isCollectionEmpty(posters)) {
            lvpaiDestination.setVisibility(VISIBLE);
            int posterSize = posters.size();
            int childCount = lvpaiDestination.getChildCount();
            if (childCount > posterSize) {
                lvpaiDestination.removeViews(posterSize, childCount - posterSize);
            }
            for (int i = 0; i < posterSize; i++) {
                View childView = null;
                if (i < childCount) {
                    childView = lvpaiDestination.getChildAt(i);
                }
                if (childView == null) {
                    childView = LayoutInflater.from(getContext())
                            .inflate(R.layout.lvpai_destintation_view, lvpaiDestination, false);
                    childView.getLayoutParams().width = posterMeasures.lvDestinationWidth;
                    childView.getLayoutParams().height = posterMeasures.lvDestinationHeight;
                    lvpaiDestination.addView(childView);
                }
                if (childView instanceof ImageView) {
                    Poster poster = posters.get(i);
                    ImageView imageView = (ImageView) childView;
                    setPosterViewValue(imageView,
                            imageView,
                            null,
                            poster,
                            null,
                            i,
                            posterMeasures.lvDestinationWidth,
                            posterMeasures.lvDestinationHeight,
                            false,
                            posterMeasures.lvDestinationHeight / 2);

                }
            }
        } else {
            lvpaiDestination.setVisibility(GONE);
        }

        if (imgGroup.getVisibility() == VISIBLE || lvpaiDestination.getVisibility() == VISIBLE) {
            lineTravelPoster.setVisibility(VISIBLE);
            travelPosterLayout.setVisibility(VISIBLE);
        } else {
            travelPosterLayout.setVisibility(GONE);
        }

        //新人备婚流程
        weddingFlowPoster.clear();
        posters = posterZip.beforeMarriagePosters;
        if (!CommonUtil.isCollectionEmpty(posters)) {
            weddingFlowPoster.put(WeddingFlowAdapter.TYPE_BEFORE, posters);
        }

        posters = posterZip.inMarriagePosters;
        if (!CommonUtil.isCollectionEmpty(posters)) {
            weddingFlowPoster.put(WeddingFlowAdapter.TYPE_MID, posters);
        }

        posters = posterZip.afterMarriagePosters;
        if (!CommonUtil.isCollectionEmpty(posters)) {
            weddingFlowPoster.put(WeddingFlowAdapter.TYPE_AFTER, posters);
        }
        weddingFlowIndicator.setPagerAdapter(weddingFlowAdapter);
        weddingFlowAdapter.notifyDataSetChanged();
        if (weddingFlowPoster.size() > 0) {
            weddingFlowViewpager.setVisibility(VISIBLE);
        } else {
            weddingFlowViewpager.setVisibility(GONE);
        }

        //体验店
        if (posterZip.experiencePoster != null) {
            imgExperienceShop.setVisibility(VISIBLE);
            setPosterViewValue(imgExperienceShop,
                    imgExperienceShop,
                    null,
                    posterZip.experiencePoster,
                    null,
                    0,
                    posterMeasures.imgExperienceShopWidth,
                    posterMeasures.imgExperienceShopHeight,
                    false,
                    3);
        } else {
            imgExperienceShop.setVisibility(GONE);
        }
        if (weddingFlowViewpager.getVisibility() == VISIBLE || imgExperienceShop.getVisibility()
                == VISIBLE) {
            lineWeddingFlow.setVisibility(VISIBLE);
            llWeddingFlow.setVisibility(VISIBLE);
        } else {
            llWeddingFlow.setVisibility(GONE);
        }

        //婚品
        posters = posterZip.productPosters;
        if (!CommonUtil.isCollectionEmpty(posters)) {
            lineProduct.setVisibility(VISIBLE);
            layoutProduct.setVisibility(VISIBLE);
            layoutProductImg.setVisibility(VISIBLE);
            for (int i = 0, size = layoutProductImg.getChildCount(); i < size; i++) {
                View view = layoutProductImg.getChildAt(i);
                if (i >= posters.size()) {
                    view.setVisibility(GONE);
                    continue;
                }
                Poster p = posters.get(i);
                if (view instanceof ImageView) {
                    ImageView imageView = (ImageView) view;
                    setPosterViewValue(imageView,
                            imageView,
                            null,
                            p,
                            null,
                            i,
                            posterMeasures.imgProductWidth,
                            posterMeasures.imgProductHeight,
                            false,
                            3);

                }
            }
        } else {
            layoutProductImg.setVisibility(GONE);
            layoutProduct.setVisibility(GONE);
        }

        //婚宴酒店
        posters = posterZip.hotelPosters;
        if (!CommonUtil.isCollectionEmpty(posters)) {
            lineHotel.setVisibility(VISIBLE);
            layoutHotelImg.setVisibility(VISIBLE);
            layoutHotel.setVisibility(VISIBLE);
            for (int i = 0, size = layoutHotelImg.getChildCount(); i < size; i++) {
                View view = layoutHotelImg.getChildAt(i);
                if (i >= posters.size()) {
                    view.setVisibility(GONE);
                    continue;
                }
                Poster p = posters.get(i);
                if (view instanceof ImageView) {
                    ImageView imageView = (ImageView) view;
                    setPosterViewValue(imageView,
                            imageView,
                            null,
                            p,
                            null,
                            i,
                            posterMeasures.imgStrollWidth,
                            posterMeasures.imgStrollHeight,
                            false,
                            3);

                }
            }
        } else {
            layoutHotelImg.setVisibility(GONE);
            layoutHotel.setVisibility(GONE);
        }

        //备婚必逛
        posters = posterZip.marriagePosters;
        if (!CommonUtil.isCollectionEmpty(posters)) {
            lineMarriageStroll.setVisibility(VISIBLE);
            marriageStrollLayout.setVisibility(VISIBLE);
            for (int i = 0, size = imgStrollLayout.getChildCount(); i < size; i++) {
                View view = imgStrollLayout.getChildAt(i);
                if (i >= posters.size()) {
                    view.setVisibility(GONE);
                    continue;
                }
                if (view instanceof ImageView) {
                    Poster p = posters.get(i);
                    ImageView img = (ImageView) view;
                    setPosterViewValue(img,
                            img,
                            null,
                            p,
                            null,
                            i,
                            posterMeasures.imgStrollWidth,
                            posterMeasures.imgStrollHeight,
                            false,
                            3);
                }
            }
        } else {
            marriageStrollLayout.setVisibility(GONE);
        }

        //单图banner
        if (posterZip.singleBottomPoster != null) {
            //单图poster上最后显示的线不显示
            setLineVisible();
            singlePosterView.setVisibility(VISIBLE);
            setPosterViewValue(singlePosterView,
                    singlePosterView,
                    null,
                    posterZip.singleBottomPoster,
                    "B1/S1",
                    0,
                    posterMeasures.singlePosterWidth,
                    posterMeasures.singlePromotionImageHeight,
                    false,
                    0);
        } else {
            singlePosterView.setVisibility(GONE);
        }
        //控制最后一条Line的显示
        setLineVisible();
    }

    private void setPosterButtons(List<Poster> posters) {
        if (posters.isEmpty()) {
            layoutPosterButtons.setVisibility(GONE);
        } else {
            linePosterButtons.setVisibility(VISIBLE);
            layoutPosterButtons.setVisibility(VISIBLE);
            int size = posters.size();
            gridPoster.removeAllViews();
            // 横排变为竖排
            int column = (size + 1) / 2;
            gridPoster.setColumnCount(column);
            List<Poster> firstRow = new ArrayList<>();
            List<Poster> secondRow = new ArrayList<>();
            for (int i = 0; i < posters.size(); i++) {
                if (i % 2 == 0) {
                    firstRow.add(posters.get(i));
                } else {
                    secondRow.add(posters.get(i));
                }
            }
            posters = new ArrayList<>(firstRow);
            posters.addAll(secondRow);

            for (int i = 0; i < posters.size(); i++) {
                Poster poster = posters.get(i);
                View posterView = LayoutInflater.from(getContext())
                        .inflate(R.layout.btn_poster_view, null, false);
                //使用Spec定义子控件的位置和比重
                GridLayout.LayoutParams layoutParams = (GridLayout.LayoutParams) posterView
                        .getLayoutParams();
                if (layoutParams != null) {
                    layoutParams.rowSpec = GridLayout.spec(i / 5, 1f);
                    layoutParams.columnSpec = GridLayout.spec(i % 5, 1f);
                    layoutParams.width = posterMeasures.buttonPosterLayoutWidth;
                }
                View posterLayout = posterView.findViewById(R.id.poster_view_layout);
                if (posterLayout.getLayoutParams() != null) {
                    posterLayout.getLayoutParams().width = posterMeasures.buttonPosterLayoutWidth;
                }
                gridPoster.addView(posterView);
                ImageView ivIcon = posterView.findViewById(R.id.poster_img);
                TextView tvName = posterView.findViewById(R.id.poster_title);
                setPosterViewValue(posterView,
                        ivIcon,
                        tvName,
                        poster,
                        "B1/C1",
                        i,
                        posterMeasures.buttonPosterWidth,
                        posterMeasures.buttonPosterHeight,
                        true,
                        0);

            }

            if (size > 10) {
                buttonsScroll2.setVisibility(VISIBLE);

                scrollContent.post(new Runnable() {
                    @Override
                    public void run() {
                        if (gridPoster == null) {
                            return;
                        }
                        showPosterButtonsScrollAnimFirst();

                        final float ratio = (float) gridPoster.getMeasuredWidth() / (float)
                                buttonsScroll2.getLayoutParams().width;
                        scrollContent.getLayoutParams().width = (int) ((float) CommonUtil
                                .getDeviceSize(
                                getContext()).x / ratio);
                        scrollContent.setTranslationX(0);
                        scrollContent.requestLayout();
                        buttonsScroll.setOnMyScrollChangeListener(new HljHorizontalScrollView
                                .OnMyScrollChangeListener() {
                            @Override
                            public void onScrollChange(int l, int t, int oldl, int oldt) {
                                if (l != oldl && scrollContent != null) {
                                    scrollContent.setTranslationX(((float) l / ratio));
                                }
                            }
                        });
                    }
                });
                scrollContent.requestLayout();
            } else {
                buttonsScroll2.setVisibility(GONE);
            }
        }
    }

    private void showPosterButtonsScrollAnimFirst() {
        if (OncePrefUtil.hasDoneThis(getContext(),
                HljCommon.SharedPreferencesNames.HOME_PAGE_POSTER_BUTTON_SCROLL_FIRST)) {
            return;
        }
        OncePrefUtil.doneThis(getContext(),
                HljCommon.SharedPreferencesNames.HOME_PAGE_POSTER_BUTTON_SCROLL_FIRST);

        if (scrollAnimatorSet != null) {
            return;
        }
        ObjectAnimator animator = ObjectAnimator.ofInt(buttonsScroll,
                "scrollX",
                0,
                gridPoster.getMeasuredWidth() - buttonsScroll.getMeasuredWidth())
                .setDuration(1000);
        ObjectAnimator animator2 = ObjectAnimator.ofInt(buttonsScroll,
                "scrollX",
                gridPoster.getMeasuredWidth() - buttonsScroll.getMeasuredWidth(),
                0)
                .setDuration(1000);

        scrollAnimatorSet = new AnimatorSet();
        scrollAnimatorSet.play(animator2)
                .after(animator);
        scrollAnimatorSet.setStartDelay(1000);
        onScrollAnimStart();
    }

    private void onScrollAnimStart() {
        if (isPause) {
            return;
        }
        if (scrollAnimatorSet == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (scrollAnimatorSet.isPaused()) {
                scrollAnimatorSet.resume();
            } else {
                scrollAnimatorSet.start();
            }
        } else {
            scrollAnimatorSet.start();
        }
        scrollAnimatorSet = null;
    }


    private void onScrollAnimStop() {
        if (scrollAnimatorSet == null) {
            return;
        }
        if (scrollAnimatorSet.isStarted()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                scrollAnimatorSet.pause();
                return;
            }
        }
        scrollAnimatorSet = null;
    }

    private void setLineVisible() {
        int childCount = headerLayout.getChildCount();
        for (int i = childCount - 1; i >= 0; i--) {
            View childView = headerLayout.getChildAt(i);
            if (childView.getVisibility() == VISIBLE) {
                int id = childView.getId();
                switch (id) {
                    case R.id.marriage_stroll_layout:
                        lineMarriageStroll.setVisibility(GONE);
                        break;
                    case R.id.layout_hotel:
                        lineHotel.setVisibility(GONE);
                        break;
                    case R.id.layout_product:
                        lineProduct.setVisibility(GONE);
                        break;
                    case R.id.ll_wedding_flow:
                        lineWeddingFlow.setVisibility(GONE);
                        break;
                    case R.id.travel_poster_layout:
                        lineTravelPoster.setVisibility(GONE);
                        break;
                    case R.id.layout_poster_buttons:
                        linePosterButtons.setVisibility(GONE);
                        break;
                    default:
                        break;
                }
                break;
            }
        }
    }

    private void onHeaderError() {
        loadPosterError = true;
        postersView.stopAutoCycle();
        headerLayout.setVisibility(GONE);
        setActionLayoutAlpha(scrollableLayout.getRefreshableView()
                .getScrollY());
        if (childEmptyMap.get(propertyId) != null && childEmptyMap.get(propertyId)) {
            //当前feedsfragment为空显示空视图
            emptyLayout.showNetworkError();
            scrollableLayout.setVisibility(GONE);
        }
    }

    public void scrollTop() {
        HomePageScrollAbleFragment feedsFragment = getCurrentFragment();
        if (feedsFragment != null) {
            feedsFragment.scrollTop();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden) {
            if (noticeUtil != null) {
                noticeUtil.onPause();
            }
            TrackerUtil.onTCAgentPageEnd(getActivity(), "首页");
            if (postersView != null) {
                postersView.stopAutoCycle();
            }
        } else {
            if (noticeUtil != null) {
                noticeUtil.onResume();
            }

            setActionLayoutAlpha(scrollableLayout.getRefreshableView()
                    .getScrollY());
            cityRefresh(Session.getInstance()
                    .getMyCity(getActivity()));
            TrackerUtil.onTCAgentPageStart(getActivity(), "首页");
            if (loadPosterError) {
                getHeaderData();
            }
        }
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onRefresh(PullToRefreshBase<ScrollableLayout> refreshView) {
        getHeaderData();
        HomePageScrollAbleFragment fragment = getCurrentFragment();
        if (fragment != null) {
            fragment.refresh(city);
        }
    }

    @Override
    public void onTabChanged(int position) {
        viewpager.setCurrentItem(position);
    }

    @Override
    public void onRefreshComplete(HomePageScrollAbleFragment feedsFragment) {
        if (feedsFragment != getCurrentFragment()) {
            return;
        }
        progressBar.setVisibility(GONE);
        scrollableLayout.onRefreshComplete();
    }

    @Override
    public void onFiltrateAnimation(HomePageScrollAbleFragment fragment, boolean isHide) {
        if (fragment != getCurrentFragment() || this.isHide == isHide) {
            return;
        }
        if (isHide) {
            hideTopAnimation();
        } else {
            if (backtopBtn.getVisibility() != VISIBLE) {
                backtopBtn.setVisibility(VISIBLE);
            }
            showTopAnimation();
        }
    }

    @Override
    public void onShowEmptyView(
            HomePageScrollAbleFragment fragment, String propertyId, boolean isEmpty) {
        childEmptyMap.put(propertyId, isEmpty);
        if (fragment != getCurrentFragment() || !loadPosterError) {
            return;
        }
        if (isEmpty) {
            emptyLayout.showNetworkError();
            scrollableLayout.setVisibility(GONE);
        } else {
            emptyLayout.hideEmptyView();
            scrollableLayout.setVisibility(VISIBLE);
        }
    }

    @OnClick({R.id.tv_more_product, R.id.tv_more_destination, R.id.tv_more_hotel})
    public void onMoreClick(View view) {
        int id = view.getId();
        Intent intent = null;
        switch (id) {
            case R.id.tv_more_product:
                intent = new Intent(getContext(), ShoppingCategoryActivity.class);
                break;
            case R.id.tv_more_destination:
                intent = new Intent(getContext(), ThemeHotCityActivity.class);
                break;
            case R.id.tv_more_hotel:
                intent = new Intent(getContext(), HotelChannelActivity.class);
                break;
            default:
                break;
        }
        if (intent != null) {
            startActivity(intent);
        }
    }

    /**
     * 城市选择
     */
    @OnClick(R.id.city_layout_r)
    public void onCityChange() {
        Intent intent = new Intent(getActivity(), CityListActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_up_to_top,
                R.anim.activity_anim_default);
    }

    /**
     * 搜索
     */
    @OnClick(R.id.searchView_r)
    public void onSearch() {
        Intent intent = new Intent(getContext(), NewSearchActivity.class);
        intent.putExtra(NewSearchApi.ARG_SEARCH_INPUT_TYPE, NewSearchApi.InputType.TYPE_HOME_PAGE);
        intent.putExtra(NewSearchActivity.ARG_HOT_KEY_WORD, newHotKeyWord);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().overridePendingTransition(R.anim.slide_in_right,
                    R.anim.activity_anim_default);
        }
    }

    /**
     * 跳转通知私信
     */
    @OnClick(R.id.msg_layout_r)
    public void onMessage() {
        if (Util.loginBindChecked(this, getActivity(), Constants.RequestCode.NOTIFICATION_PAGE)) {
            Intent intent = new Intent(getActivity(), MessageHomeActivity.class);
            startActivity(intent);
        }
    }

    /**
     * 回到顶部
     */
    @OnClick(R.id.backtop_btn)
    public void onBackTop() {
        HomePageScrollAbleFragment fragment = getCurrentFragment();
        if (fragment != null) {
            fragment.scrollTop();
        }
    }

    private void setPosterViewValue(
            View posterView,
            ImageView posterImageView,
            TextView posterTitleView,
            Poster poster,
            String sid,
            int position,
            int width,
            int height,
            boolean rawPath,
            int cornerRadius) {
        if (poster != null) {
            if (poster.getId() > 0) {
                posterView.setVisibility(VISIBLE);
                posterView.setOnClickListener(new OnPosterClickListener(poster, position + 1, sid));
            } else {
                posterView.setVisibility(View.INVISIBLE);
                posterView.setOnClickListener(null);
            }
            if (posterTitleView != null) {
                posterTitleView.setText(poster.getTitle());
            }

            MultiTransformation<Bitmap> transformation;
            if (cornerRadius > 0) {
                transformation = new MultiTransformation<>(new CenterCrop(),
                        new RoundedCorners(CommonUtil.dp2px(posterView.getContext(),
                                cornerRadius)));
            } else {
                transformation = new MultiTransformation<>(new CenterCrop());
            }

            if (posterImageView != null) {
                if (rawPath) {
                    Glide.with(this)
                            .load(poster.getPath())
                            .apply(new RequestOptions().format(DecodeFormat.PREFER_ARGB_8888)
                                    .transform(transformation)
                                    .placeholder(R.mipmap.icon_empty_image))
                            .into(posterImageView);
                } else {
                    Glide.with(this)
                            .load(ImagePath.buildPath(poster.getPath())
                                    .width(width)
                                    .height(height)
                                    .ignoreFormat(true)
                                    .cropPath())
                            .apply(new RequestOptions().format(DecodeFormat.PREFER_ARGB_8888)
                                    .transform(transformation)
                                    .placeholder(R.mipmap.icon_empty_image))
                            .into(posterImageView);
                }
            }
        } else {
            posterView.setVisibility(GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (scrollAnimatorSet != null) {
            scrollAnimatorSet.cancel();
        }
        CommonUtil.unSubscribeSubs(propertiesSubscription,
                headerSubscriber,
                madSubscription,
                inputSubscriber);
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    private class OnPosterClickListener implements View.OnClickListener {

        private Poster poster;
        private String sid;
        private int position;

        private OnPosterClickListener(Poster poster, int position, String sid) {
            this.position = position;
            this.poster = poster;
            this.sid = sid;
        }

        @Override
        public void onClick(View v) {
            if (poster != null) {
                BannerUtil.bannerAction(getActivity(),
                        poster,
                        city,
                        false,
                        com.hunliji.hljtrackerlibrary.utils.TrackerUtil.getSiteJson(sid,
                                position,
                                poster.getTitle()));
            }
        }

    }

    //首页城市的改变
    public void cityRefresh(City c) {
        if (!city.getId()
                .equals(c.getId())) {
            scrollableLayout.getRefreshableView()
                    .scrollToTop();
            city = c;
            labelCityR.setText(Util.maxEmsText(city.getName(), 3));
            flowAdapter.setCity(city);
            progressBar.setVisibility(VISIBLE);
            CommonUtil.unSubscribeSubs(headerSubscriber);
            getHeaderData();
            getFeedsProperty();
            getInputBoxHotWord();
        }
    }

    @Override
    public void refresh(Object... params) {
        HomePageScrollAbleFragment fragment = getCurrentFragment();
        if (fragment != null) {
            fragment.refresh(city);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Constants.RequestCode.NOTIFICATION_PAGE:
                    Intent intent = new Intent(getActivity(), MessageHomeActivity.class);
                    startActivity(intent);
                    break;
                case Constants.Login.SHOP_CART_LOGIN:
                    intent = new Intent(getActivity(), ShoppingCartActivity.class);
                    startActivity(intent);
                    break;
                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        isPause = false;
        onScrollAnimStart();
        if (!isHidden()) {
            if (noticeUtil != null) {
                noticeUtil.onResume();
            }
            TrackerUtil.onTCAgentPageStart(getActivity(), "首页");
            setActionLayoutAlpha(scrollableLayout.getRefreshableView()
                    .getScrollY());
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        isPause = true;
        onScrollAnimStop();
        if (noticeUtil != null) {
            noticeUtil.onPause();
        }
        if (!isHidden()) {
            TrackerUtil.onTCAgentPageEnd(getActivity(), "首页");
        }
        if (postersView != null) {
            postersView.stopAutoCycle();
        }
        super.onPause();
    }

    private HomePageScrollAbleFragment getFeedsFragment(FeedProperty feedProperty) {
        HomePageScrollAbleFragment fragment;
        boolean isProduct = feedProperty.isShopProduct();
        if (isProduct) {
            if (feedProperty.getStringId()
                    .equals("0")) {
                fragment = HomeSelectedProductListFragment.newInstance(String.format(Constants
                                .HttpPath.GET_RECOMMEND_PRODUCTS,
                        1), "1");
            } else {
                fragment = HomeProductListFragment.newInstance(String.format(Constants.HttpPath
                                .HOME_PRODUCT_LIST_V2,
                        feedProperty.getStringId()), feedProperty.getStringId());
            }
        } else {
            fragment = HomeFeedsFragment.newInstance(feedProperty.getStringId());
        }
        return fragment;
    }

    private class PropertyFragmentAdapter extends FragmentStatePagerAdapter {

        private PropertyFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            return getFeedsFragment(tabPropertyList.get(position));
        }

        @Override
        public int getCount() {
            return tabPropertyList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            FeedProperty p = tabPropertyList.get(position);
            return p.getName();
        }
    }

    class HomePagePosterZip extends HljHttpResultZip {
        @HljRZField
        @PosterSite(name = Constants.POST_SITES.SITE_MAIN_TOP_BANNER_V3)
        List<Poster> topSliderPosters; //首页轮播
        @HljRZField
        @PosterSite(name = Constants.POST_SITES.SITE_MAIN_ENTRY_BUTTON_V3, emptyVerify = false)
        List<Poster> posterButtons; //网格按钮
        @HljRZField
        @PosterSite(name = Constants.POST_SITES.SITE_DACU, index = 0)
        Poster singlePoster; //首页大促,单条广告
        @HljRZField
        @PosterSite(name = Constants.POST_SITES.SITE_MAIN_GLOBAL_TRIP_SHOOT_V3)
        List<Poster> travelPosters; //全球旅拍
        @HljRZField
        @PosterSite(name = Constants.POST_SITES.SITE_MAIN_GLOBAL_TRIP_SHOOT_V2)
        List<Poster> oldTravelPosters; //老数据兼容全球旅拍
        @HljRZField
        @PosterSite(name = Constants.POST_SITES.SITE_MAIN_GLOBAL_TRIP_SHOOT_DESTINATION)
        List<Poster> destinationPosters; //旅拍底部，目的地按钮
        @HljRZField
        @PosterSite(name = HljCommon.POST_SITES.SITE_MAIN_BEFORE_MARRIAGE)
        List<Poster> beforeMarriagePosters; //新人备婚流程,备婚前
        @HljRZField
        @PosterSite(name = HljCommon.POST_SITES.SITE_MAIN_IN_MARRIAGE)
        List<Poster> inMarriagePosters; //新人备婚流程,备婚中
        @HljRZField
        @PosterSite(name = HljCommon.POST_SITES.SITE_MAIN_AFTER_MARRIAGE)
        List<Poster> afterMarriagePosters; //新人备婚流程,备婚后
        @HljRZField
        @PosterSite(name = Constants.POST_SITES.SITE_MAIN_WEDDING_TOOL_BANNER, index = 0)
        Poster experiencePoster; //工具底部，体验店，单条广告
        @HljRZField
        @PosterSite(name = Constants.POST_SITES.SITE_MAIN_WEDDING_PRODUCT)
        List<Poster> productPosters; //婚品入口
        @HljRZField
        @PosterSite(name = Constants.POST_SITES.SITE_MAIN_HOTEL)
        List<Poster> hotelPosters; //酒店入口
        @HljRZField
        @PosterSite(name = Constants.POST_SITES.SITE_MAIN_PREPARE_MARRIAGE_STROLL_V2)
        List<Poster> marriagePosters; //备婚必逛
        @HljRZField
        @PosterSite(name = Constants.POST_SITES.SITE_MAIN_SINGLE_PIC_BANNER, index = 0)
        Poster singleBottomPoster; //活动底部，单条广告

        private void addMadPoster(int mAdIndex, MadPoster poster) {
            if (CommonUtil.isCollectionEmpty(topSliderPosters)) {
                return;
            }
            if (mAdIndex <= topSliderPosters.size()) {
                topSliderPosters.add(mAdIndex - 1, poster);
            } else {
                topSliderPosters.add(poster);
            }
        }
    }

    private class PosterMeasures {
        private int buttonPosterLayoutWidth;
        private int buttonPosterWidth;
        private int buttonPosterHeight;
        private int singlePosterWidth;
        private int actionBarHeight;
        private int topPosterHeight;
        private int statusBarHeight;
        private int lvPaiWidth;
        private int lvPaiHeight;
        private int lvPaiWidth2;
        private int lvPaiHeight2;
        private int lvDestinationWidth;
        private int lvDestinationHeight;

        private int imgExperienceShopWidth;
        private int imgExperienceShopHeight;
        private int imgProductWidth;
        private int imgProductHeight;
        private int singlePromotionImageHeight;
        private int imgStrollWidth;
        private int imgStrollHeight;
        private int weddingFlowviewpagerHeight;

        private PosterMeasures(Context context) {
            Point point = CommonUtil.getDeviceSize(context);
            DisplayMetrics dm = context.getResources()
                    .getDisplayMetrics();
            int width = point.x;
            buttonPosterLayoutWidth = Math.round((width - CommonUtil.dp2px(getContext(),
                    20)) * 1.0F / 5);
            buttonPosterWidth = CommonUtil.dp2px(getContext(), 60);
            buttonPosterHeight = CommonUtil.dp2px(getContext(), 48);
            singlePosterWidth = width;
            actionBarHeight = Math.round(dm.density * 45);
            singlePromotionImageHeight = Math.round(width * 1.0F * 270 / 1080);
            lvPaiWidth = Math.round((width - CommonUtil.dp2px(getContext(), 42)) * 1.0F / 3);
            lvPaiHeight = Math.round(lvPaiWidth * 1.0F * 200 / 222);
            lvPaiWidth2 = Math.round((width - CommonUtil.dp2px(getContext(), 38)) * 1.0F / 2);
            lvPaiHeight2 = lvPaiWidth2 * 74 / 169;

            //获得状态栏高度
            int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                //根据资源ID获取响应的尺寸值
                statusBarHeight = getResources().getDimensionPixelSize(resourceId);
            }
            topPosterHeight = Math.round((width - CommonUtil.dp2px(getContext(), 32)) * 398 / 988);
            lvDestinationWidth = Math.round((width - CommonUtil.dp2px(getContext(),
                    47)) * 1.0F / 4);
            lvDestinationHeight = CommonUtil.dp2px(getContext(), 32);
            imgExperienceShopWidth = Math.round(width - CommonUtil.dp2px(getContext(), 32));
            imgExperienceShopHeight = Math.round(imgExperienceShopWidth * 1.0F * 220 / 686);
            imgProductWidth = Math.round((width - CommonUtil.dp2px(getContext(), 42)) * 1.0F / 3);
            imgProductHeight = Math.round(imgProductWidth * 1.0F * 234 / 222);
            imgStrollWidth = Math.round(((width - CommonUtil.dp2px(getContext(), 37)) / 2));
            imgStrollHeight = Math.round(imgStrollWidth * 160 / 338);

            Paint paint = new Paint();
            paint.setTextSize(CommonUtil.sp2px(getContext(), 12));
            Paint.FontMetrics fm = paint.getFontMetrics();
            weddingFlowviewpagerHeight = (int) Math.ceil(fm.descent - fm.top) + 2 + CommonUtil
                    .dp2px(
                    getContext(),
                    46) + CommonUtil.dp2px(getContext(), 8);
        }
    }

    private void showTopAnimation() {
        if (backtopBtn == null) {
            return;
        }
        isHide = false;
        if (AnimUtil.isAnimEnded(backtopBtn)) {
            Animation animation = AnimationUtils.loadAnimation(getActivity(),
                    R.anim.slide_in_bottom2);
            animation.setFillBefore(true);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    backtopBtn.post(new Runnable() {
                        @Override
                        public void run() {
                            if (isHide) {
                                hideTopAnimation();
                            }
                        }
                    });
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            backtopBtn.startAnimation(animation);
        }
    }

    private void hideTopAnimation() {
        if (backtopBtn == null) {
            return;
        }
        isHide = true;
        if (AnimUtil.isAnimEnded(backtopBtn)) {
            Animation animation = AnimationUtils.loadAnimation(getActivity(),
                    R.anim.slide_out_bottom2);
            animation.setFillAfter(true);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    backtopBtn.post(new Runnable() {
                        @Override
                        public void run() {
                            if (!isHide) {
                                showTopAnimation();
                            }
                        }
                    });
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            backtopBtn.startAnimation(animation);
        }
    }

    class WeddingFlowAdapter extends PagerAdapter {

        private static final int TYPE_BEFORE = 1;//备婚前
        private static final int TYPE_MID = 2;//备婚中
        private static final int TYPE_AFTER = 3;//备婚后

        private SparseArray<List<Poster>> posters;

        WeddingFlowAdapter(SparseArray<List<Poster>> posters) {
            this.posters = posters;
        }

        @Override
        public int getCount() {
            return posters == null ? 0 : posters.size();
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, final int position) {
            View itemView = getLayoutInflater().inflate(R.layout.wedding_flow_grid_layout,
                    container,
                    false);
            SpringScrollView springScrollView = itemView.findViewById(R.id.spring_scroll_view);
            springScrollView.setSpringListener(new SpringScrollView.SpringListener() {
                @Override
                public void onStartSpring(int distance, int max) {
                    if (Math.abs(distance) * 3 < max) {
                        return;
                    }
                    if (distance < 0) {
                        if (position < getCount() - 1) {
                            weddingFlowViewpager.setCurrentItem(position + 1, true);
                        }
                    } else {
                        if (position > 0) {
                            weddingFlowViewpager.setCurrentItem(position - 1, true);
                        }
                    }
                }
            });
            GridLayout gridLayout = itemView.findViewById(R.id.grid_layout);
            List<Poster> posters = this.posters.valueAt(position);
            gridLayout.setColumnCount(posters.size());
            setGridItem(gridLayout, posters);
            container.addView(itemView);
            return itemView;
        }

        private void setGridItem(GridLayout gridLayout, List<Poster> posters) {
            if (gridLayout == null || CommonUtil.isCollectionEmpty(posters)) {
                return;
            }
            for (int i = 0; i < posters.size(); i++) {
                Poster poster = posters.get(i);
                View posterView = LayoutInflater.from(getContext())
                        .inflate(R.layout.wedding_flow_poster_view, null, false);
                //使用Spec定义子控件的位置和比重
                GridLayout.LayoutParams layoutParams = (GridLayout.LayoutParams) posterView
                        .getLayoutParams();
                if (layoutParams != null) {
                    layoutParams.rowSpec = GridLayout.spec(i / 5, 1f);
                    layoutParams.columnSpec = GridLayout.spec(i % 5, 1f);
                    layoutParams.width = posterMeasures.buttonPosterLayoutWidth;
                }
                View posterLayout = posterView.findViewById(R.id.poster_view_layout);
                if (posterLayout.getLayoutParams() != null) {
                    posterLayout.getLayoutParams().width = posterMeasures.buttonPosterLayoutWidth;
                }
                gridLayout.addView(posterView);
                ImageView ivIcon = posterView.findViewById(R.id.poster_img);
                TextView tvName = posterView.findViewById(R.id.poster_title);
                setPosterViewValue(posterView,
                        ivIcon,
                        tvName,
                        poster,
                        "B1/C1",
                        i,
                        posterMeasures.buttonPosterWidth,
                        posterMeasures.buttonPosterHeight,
                        true,
                        0);
            }
        }

        @Override
        public void destroyItem(
                @NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            int type = posters.keyAt(position);
            switch (type) {
                case TYPE_BEFORE:
                    return "备婚前";
                case TYPE_MID:
                    return "备婚中";
                case TYPE_AFTER:
                    return "备婚后";
                default:
                    break;
            }
            return super.getPageTitle(position);
        }
    }
}