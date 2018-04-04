package com.hunliji.hljcarlibrary.views.activities;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.hunliji.hljcarlibrary.R;
import com.hunliji.hljcarlibrary.R2;
import com.hunliji.hljcarlibrary.adapter.WeddingCarLessonHorizontalAdapter;
import com.hunliji.hljcarlibrary.adapter.WeddingHotBrandAdapter;
import com.hunliji.hljcarlibrary.api.WeddingCarApi;
import com.hunliji.hljcarlibrary.models.Brand;
import com.hunliji.hljcarlibrary.models.CarLesson;
import com.hunliji.hljcarlibrary.models.CarMerchantContactInfo;
import com.hunliji.hljcarlibrary.models.HljCarHttpData;
import com.hunliji.hljcarlibrary.models.SecKill;
import com.hunliji.hljcarlibrary.util.WeddingCarSession;
import com.hunliji.hljcarlibrary.views.fragments.WeddingCarHotFragment;
import com.hunliji.hljcarlibrary.views.fragments.WeddingCarSelfFragment;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.City;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.models.TopicUrl;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityThread;
import com.hunliji.hljcommonlibrary.models.merchant.MerchantUser;
import com.hunliji.hljcommonlibrary.models.modelwrappers.PosterData;
import com.hunliji.hljcommonlibrary.models.wedding_car.WeddingCarProduct;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljcommonlibrary.utils.LocationSession;
import com.hunliji.hljcommonlibrary.view_tracker.HljTaggerName;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.view_tracker.VTMetaData;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.fragments.ScrollAbleFragment;
import com.hunliji.hljcommonlibrary.views.widgets.PullToRefreshScrollableLayout;
import com.hunliji.hljcommonlibrary.views.widgets.ScrollableLayout;
import com.hunliji.hljcommonlibrary.views.widgets.TabPageIndicator;
import com.hunliji.hljcommonviewlibrary.adapters.FlowAdapter;
import com.hunliji.hljhttplibrary.api.CommonApi;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.PosterUtil;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.makeramen.rounded.RoundedImageView;
import com.slider.library.Indicators.CirclePageExIndicator;
import com.slider.library.SliderLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func5;

/**
 * Created by mo_yu on 2017/12/26.婚车二级页
 */
public class WeddingCarSubPageActivity extends HljBaseNoBarActivity implements PullToRefreshBase
        .OnRefreshListener, TabPageIndicator.OnTabChangeListener, OnItemClickListener<Brand> {

    @Override
    public String pageTrackTagName() {
        return "婚车首页";
    }

    @Override
    public VTMetaData pageTrackData() {
        long cityId = getIntent().getLongExtra(ARG_CITY_ID, 0);
        return new VTMetaData(cityId, VTMetaData.DATA_TYPE.DATA_TYPE_CITY);
    }

    public static final String ARG_CITY_ID = "city_id";
    public static final String ARG_CITY_NAME = "city_name";
    public static final String ARG_POSITION = "arg_position";

    @BindView(R2.id.poster_view)
    SliderLayout posterView;
    @BindView(R2.id.flow_indicator)
    CirclePageExIndicator flowIndicator;
    @BindView(R2.id.poster_layout)
    RelativeLayout posterLayout;
    @BindView(R2.id.car_promises_layout)
    LinearLayout carPromisesLayout;
    @BindView(R2.id.tv_limit_hour)
    TextView tvLimitHour;
    @BindView(R2.id.tv_limit_minute)
    TextView tvLimitMinute;
    @BindView(R2.id.tv_limit_second)
    TextView tvLimitSecond;
    @BindView(R2.id.tv_see_all_car)
    TextView tvSeeAllCar;
    @BindView(R2.id.img_car_cover)
    RoundedImageView imgCarCover;
    @BindView(R2.id.tv_wedding_car_title)
    TextView tvWeddingCarTitle;
    @BindView(R2.id.tv_lowest_price)
    TextView tvLowestPrice;
    @BindView(R2.id.tv_original_price)
    TextView tvOriginalPrice;
    @BindView(R2.id.tv_sale_price)
    TextView tvSalePrice;
    @BindView(R2.id.action_buy_now)
    Button actionBuyNow;
    @BindView(R2.id.sec_kill_layout)
    LinearLayout secKillLayout;
    @BindView(R2.id.sec_kill_item)
    LinearLayout secKillItem;
    @BindView(R2.id.brand_recycler_view)
    RecyclerView brandRecyclerView;
    @BindView(R2.id.popular_brand_layout)
    LinearLayout popularBrandLayout;
    @BindView(R2.id.tab_indicator)
    TabPageIndicator tabIndicator;
    @BindView(R2.id.view_pager)
    ViewPager viewPager;
    @BindView(R2.id.scroll_view)
    PullToRefreshScrollableLayout scrollView;
    @BindView(R2.id.action_chat)
    TextView actionChat;
    @BindView(R2.id.bottom_layout)
    LinearLayout bottomLayout;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R2.id.tv_see_all_lesson)
    TextView tvSeeAllLesson;
    @BindView(R2.id.car_lesson_recycler_view)
    RecyclerView carLessonRecyclerView;
    @BindView(R2.id.car_lesson_layout)
    LinearLayout carLessonLayout;
    @BindView(R2.id.tv_car_count)
    TextView tvCarCount;
    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.action_car_call)
    LinearLayout actionCarCall;

    private FlowAdapter flowAdapter;
    private WeddingHotBrandAdapter brandAdapter;
    private WeddingCarLessonHorizontalAdapter carLessonHorizontalAdapter;
    private City city;
    private WeddingCarHotFragment weddingCarHotFragment;
    private WeddingCarSelfFragment weddingCarSelfFragment;
    private Handler handler;
    private long countDownTime;
    private int coverWidth;
    private int coverHeight;
    private CarMerchantContactInfo carMerchantContactInfo;
    private HljHttpSubscriber loadSubscriber;
    private Subscription rxBusSub;
    private boolean isScrollPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wedding_car_sub_page___car);
        ButterKnife.bind(this);
        setDefaultStatusBarPadding();
        initValue();
        initView();
        onRefresh(scrollView);
        registerRxBus();
        initTracker();
    }

    private void initValue() {
        long cityId = getIntent().getLongExtra(ARG_CITY_ID, 0);
        String cityName = getIntent().getStringExtra(ARG_CITY_NAME);
        isScrollPosition = getIntent().getBooleanExtra(ARG_POSITION, false);
        if (cityId == 0) {
            city = LocationSession.getInstance()
                    .getCity(this);
        } else {
            city = new City();
            city.setCid(cityId);
            city.setName(cityName);
        }
        handler = new Handler();
        coverWidth = CommonUtil.dp2px(this, 148);
        coverHeight = CommonUtil.dp2px(this, 96);
    }

    private void initView() {
        initPosterView();
        initViewPager();
        initSecKill();
        initHotBrand();
        initCarLesson();
    }

    private void initTracker() {
        HljVTTagger.buildTagger(secKillItem)
                .tagName(HljTaggerName.WeddingCarSubPageActivity.SEC_KILL_ITEM)
                .hitTag();
        HljVTTagger.buildTagger(tvSeeAllCar)
                .tagName(HljTaggerName.WeddingCarSubPageActivity.SEC_KILL_BTN_ALL)
                .hitTag();
        HljVTTagger.buildTagger(actionBuyNow)
                .tagName(HljTaggerName.WeddingCarSubPageActivity.SEC_KILL_BTN_BUY)
                .hitTag();
        HljVTTagger.buildTagger(actionCarCall)
                .tagName(HljTaggerName.BTN_CALL)
                .hitTag();
        HljVTTagger.buildTagger(actionChat)
                .tagName(HljTaggerName.BTN_CHAT)
                .hitTag();
    }

    private void initViewPager() {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabIndicator.setTabViewId(R.layout.menu_tab_view_wedding_car___car);
        tabIndicator.setPagerAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabIndicator.setCurrentItem(position);
                scrollView.getRefreshableView()
                        .getHelper()
                        .setCurrentScrollableContainer(getCurrentFragment());

            }
        });
        tabIndicator.setOnTabChangeListener(this);
        scrollView.setOnRefreshListener(this);
        scrollView.getRefreshableView()
                .addOnScrollListener(new ScrollableLayout.OnScrollListener() {
                    @Override
                    public void onScroll(int currentY, int maxY) {
                        if (scrollView.getRefreshableView()
                                .getHelper()
                                .getScrollableView() == null) {
                            scrollView.getRefreshableView()
                                    .getHelper()
                                    .setCurrentScrollableContainer(getCurrentFragment());
                        }
                    }
                });
    }

    private void initPosterView() {
        if (city.getCid() > 0) {
            tvTitle.setText("婚车租赁·" + city.getName());
        }
        ArrayList<Poster> posters = new ArrayList<>();
        flowAdapter = new FlowAdapter(this, posters, 0, R.layout.flow_card_item___cv);
        flowAdapter.setCity(city);
        posterLayout.getLayoutParams().height = (CommonUtil.getDeviceSize(this).x - CommonUtil
                .dp2px(
                this,
                32)) / 16 * 9 + CommonUtil.dp2px(this, 10);
        posterView.setPagerAdapter(flowAdapter);
        flowAdapter.setSliderLayout(posterView);
        posterView.setCustomIndicator(flowIndicator);
        posterView.setPresetTransformer(SliderLayout.Transformer.Default);
    }

    private void initSecKill() {
        tvOriginalPrice.getPaint()
                .setAntiAlias(true);
        tvOriginalPrice.getPaint()
                .setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
    }

    private void initHotBrand() {
        brandAdapter = new WeddingHotBrandAdapter(this);
        brandAdapter.setOnItemClickListener(this);
        brandRecyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL,
                false));
        brandRecyclerView.setAdapter(brandAdapter);
    }

    private void initCarLesson() {
        carLessonHorizontalAdapter = new WeddingCarLessonHorizontalAdapter(this);
        carLessonRecyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL,
                false));
        carLessonRecyclerView.setAdapter(carLessonHorizontalAdapter);
        carLessonHorizontalAdapter.setOnItemClickListener(new OnItemClickListener<CarLesson>() {
            @Override
            public void onItemClick(int position, CarLesson carLesson) {
                if (carLesson != null) {
                    if (carLesson.getSourceType() == CarLesson.THREAD_SOURCE) {
                        CommunityThread communityThread = (CommunityThread) carLesson
                                .getEntityJson();
                        ARouter.getInstance()
                                .build(RouterPath.IntentPath.Customer.COMMUNITY_THREAD_DETAIL)
                                .withLong("id", communityThread.getId())
                                .navigation(WeddingCarSubPageActivity.this);
                    } else {
                        TopicUrl topicUrl = (TopicUrl) carLesson.getEntityJson();
                        ARouter.getInstance()
                                .build(RouterPath.IntentPath.Customer.SUB_PAGE_DETAIL_ACTIVITY)
                                .withLong("id", topicUrl.getId())
                                .navigation(WeddingCarSubPageActivity.this);
                    }
                }
            }
        });
    }

    private void registerRxBus() {
        rxBusSub = RxBus.getDefault()
                .toObservable(RxEvent.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxBusSubscriber<RxEvent>() {
                    @Override
                    protected void onEvent(RxEvent rxEvent) {
                        if (rxEvent == null) {
                            return;
                        }
                        switch (rxEvent.getType()) {
                            case WEDDING_CAR_CART_COUNT:
                                refreshCarCount();
                                break;
                            default:
                                break;
                        }
                    }
                });
    }

    private void refreshCarCount() {
        if (tvCarCount == null || this.isFinishing()) {
            return;
        }
        int count = WeddingCarSession.getInstance()
                .getCarCartCount(this, 0L);
        if (count > 0) {
            tvCarCount.setText(count > 99 ? "99+" : String.valueOf(count));
            tvCarCount.setVisibility(View.VISIBLE);
        } else {
            tvCarCount.setVisibility(View.GONE);
        }
    }

    @Override
    public void onTabChanged(int position) {
        viewPager.setCurrentItem(position);
    }

    @OnClick(R2.id.tv_see_all_car)
    public void onTvSeeAllCarClicked() {
        Intent intent = new Intent(this, WeddingCarSecKillActivity.class);
        intent.putExtra(WeddingCarSecKillActivity.CITY_ID, city.getCid());
        startActivity(intent);
    }

    @OnClick(R2.id.tv_see_all_lesson)
    public void onTvSeeAllLessonClicked() {
        Intent intent = new Intent(this, WeddingCarLessonListActivity.class);
        startActivity(intent);
    }

    @OnClick(R2.id.action_car_call)
    public void onActionCarCallClicked() {
        if (carMerchantContactInfo == null || CommonUtil.isCollectionEmpty(carMerchantContactInfo
                .getContactPhones())) {
            return;
        }
        String phone = carMerchantContactInfo.getContactPhones()
                .get(0);
        if (!TextUtils.isEmpty(phone)) {
            try {
                callUp(Uri.parse("tel:" + phone.trim()));
            } catch (Exception ignored) {
            }
        }
    }

    @OnClick(R2.id.action_wedding_motorcade)
    public void onActionWeddingMotorcadeClicked() {
        ARouter.getInstance()
                .build(RouterPath.IntentPath.Customer.WEDDING_CAR_TEAM_ACTIVITY)
                .withParcelable("city", city)
                .navigation(this);
    }

    @OnClick(R2.id.action_chat)
    public void onActionChatClicked() {
        if (carMerchantContactInfo == null) {
            return;
        }
        MerchantUser user = new MerchantUser();
        user.setId(carMerchantContactInfo.getUserId());
        user.setMerchantId(carMerchantContactInfo.getId());
        user.setShopType(MerchantUser.SHOP_TYPE_CAR);
        ARouter.getInstance()
                .build(RouterPath.IntentPath.Customer.WsCustomChatActivityPath
                        .WS_CUSTOMER_CHAT_ACTIVITY)
                .withParcelable(RouterPath.IntentPath.Customer.BaseWsChat.ARG_USER, user)
                .withParcelable(RouterPath.IntentPath.Customer.BaseWsChat.ARG_CITY, city)
                .withStringArrayList(RouterPath.IntentPath.Customer.BaseWsChat.ARG_CONTACT_PHONES,
                        carMerchantContactInfo.getContactPhones())
                .navigation(this);
    }

    @OnClick(R2.id.btn_back)
    void onBack() {
        onBackPressed();
    }

    @Override
    public void onItemClick(int position, Brand brand) {
        if (brand == null) {
            return;
        }
        getScrollableLayout().scrollToBottom();
        if (weddingCarHotFragment != null) {
            weddingCarHotFragment.refresh(brand);
        }
        if (weddingCarSelfFragment != null) {
            weddingCarSelfFragment.refresh(brand);
        }
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        private SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    if (weddingCarHotFragment == null) {
                        weddingCarHotFragment = WeddingCarHotFragment.newInstance(city.getCid());
                    }
                    return weddingCarHotFragment;
                default:
                    if (weddingCarSelfFragment == null) {
                        weddingCarSelfFragment = WeddingCarSelfFragment.newInstance(city.getCid());
                    }
                    return weddingCarSelfFragment;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.label_wedding_car_hot).toUpperCase();
                default:
                    return getString(R.string.label_wedding_car_self).toUpperCase();
            }
        }
    }

    private ScrollAbleFragment getCurrentFragment() {
        if (viewPager.getAdapter() != null) {
            PagerAdapter adapter = viewPager.getAdapter();
            Fragment fragment = (Fragment) adapter.instantiateItem(viewPager,
                    viewPager.getCurrentItem());
            if (fragment instanceof ScrollAbleFragment) {
                return (ScrollAbleFragment) fragment;
            }
        }
        return null;
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        loadData();
        if (weddingCarSelfFragment != null) {
            weddingCarSelfFragment.refresh();
        }
        if (weddingCarHotFragment != null) {
            weddingCarHotFragment.refresh();
        }
    }

    private void loadData() {
        if (loadSubscriber == null || loadSubscriber.isUnsubscribed()) {
            loadSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setProgressBar(scrollView.isRefreshing() ? null : progressBar)
                    .setPullToRefreshBase(scrollView)
                    .setContentView(scrollView)
                    .setDataNullable(true)
                    .setOnNextListener(new SubscriberOnNextListener<ResultZip>() {
                        @Override
                        public void onNext(ResultZip result) {
                            scrollView.setVisibility(View.VISIBLE);
                            scrollView.getRefreshableView()
                                    .setVisibility(View.VISIBLE);
                            carPromisesLayout.setVisibility(View.VISIBLE);
                            if (!CommonUtil.isCollectionEmpty(result.hotBrands)) {
                                brandAdapter.setHotBrands(result.hotBrands);
                                popularBrandLayout.setVisibility(View.VISIBLE);
                            } else {
                                popularBrandLayout.setVisibility(View.GONE);
                            }
                            flowAdapter.setmDate(result.posters);
                            if (flowAdapter.getCount() == 0) {
                                posterLayout.setVisibility(View.GONE);
                            } else {
                                posterLayout.setVisibility(View.VISIBLE);
                            }
                            carMerchantContactInfo = result.carMerchantContactInfo;
                            setSecKillView(result.secKillData);
                            setCarLessonsView(result.carLessons);
                            if (isScrollPosition) {
                                scrollView.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (scrollView == null || WeddingCarSubPageActivity.this
                                                .isFinishing()) {
                                            return;
                                        }
                                        scrollView.getRefreshableView()
                                                .scrollToBottom();
                                    }
                                }, 500);
                            }
                        }
                    })
                    .setOnErrorListener(new SubscriberOnErrorListener() {
                        @Override
                        public void onError(Object o) {
                            scrollView.setVisibility(View.VISIBLE);
                            scrollView.getRefreshableView()
                                    .setVisibility(View.VISIBLE);
                        }
                    })
                    .build();
            Observable.zip(WeddingCarApi.getHotBrandsObb(),
                    CommonApi.getBanner(this, HljCommon.BLOCK_ID.WeddingCarActivity, city.getCid())
                            .map(new Func1<PosterData, List<Poster>>() {
                                @Override
                                public List<Poster> call(PosterData posterData) {
                                    if (posterData == null || posterData.getFloors() == null) {
                                        return new ArrayList<>();
                                    }
                                    return PosterUtil.getPosterList(posterData.getFloors(),
                                            HljCommon.POST_SITES.SITE_CAR_BANNER_V3,
                                            false);
                                }
                            }),
                    WeddingCarApi.getCarLessonsObb(3, 1),
                    WeddingCarApi.getSecKillsObb(city.getCid(), 1, 1),
                    WeddingCarApi.getCarMerchantContactInfoObb(city.getCid()),
                    new Func5<List<Brand>, List<Poster>, HljHttpData<List<CarLesson>>,
                            HljCarHttpData<List<SecKill>>, CarMerchantContactInfo, Object>() {
                        @Override
                        public Object call(
                                List<Brand> brands,
                                List<Poster> posters,
                                HljHttpData<List<CarLesson>> listHljHttpData,
                                HljCarHttpData<List<SecKill>> listHljCarHttpData,
                                CarMerchantContactInfo carMerchantContactInfo) {
                            return new ResultZip(brands,
                                    posters,
                                    listHljHttpData.getData(),
                                    listHljCarHttpData,
                                    carMerchantContactInfo);
                        }
                    })
                    .subscribe(loadSubscriber);
        }
    }

    //婚车必修课
    private void setCarLessonsView(List<CarLesson> carLessons) {
        if (CommonUtil.isCollectionEmpty(carLessons)) {
            carLessonLayout.setVisibility(View.GONE);
            return;
        } else {
            carLessonLayout.setVisibility(View.VISIBLE);
        }
        carLessonHorizontalAdapter.setCarLessons(carLessons);
    }

    //特价秒杀
    private void setSecKillView(HljCarHttpData<List<SecKill>> secKillData) {
        List<SecKill> secKills = secKillData.getData();
        countDownTime = secKillData.getCountDownTime();
        if (!CommonUtil.isCollectionEmpty(secKills)) {
            final SecKill secKill = secKills.get(0);
            final WeddingCarProduct weddingCarProduct = secKill.getExtraData();
            secKillLayout.setVisibility(View.VISIBLE);
            tvWeddingCarTitle.setText(secKill.getTitle());
            Glide.with(this)
                    .load(ImagePath.buildPath(secKill.getImg())
                            .width(coverWidth)
                            .height(coverHeight)
                            .cropPath())
                    .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                    .into(imgCarCover);
            secKillItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(WeddingCarSubPageActivity.this,
                            WeddingCarSecKillActivity.class);
                    intent.putExtra(WeddingCarSecKillActivity.CITY_ID, city.getCid());
                    startActivity(intent);
                }
            });
            actionBuyNow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(WeddingCarSubPageActivity.this,
                            WeddingCarSecKillActivity.class);
                    intent.putExtra(WeddingCarSecKillActivity.CITY_ID, city.getCid());
                    startActivity(intent);
                }
            });
            if (weddingCarProduct != null) {
                tvSalePrice.setText(getString(R.string.label_price9___cv,
                        CommonUtil.formatDouble2String(weddingCarProduct.getShowPrice())));
                tvOriginalPrice.setText(getString(R.string.label_price10___cv,
                        CommonUtil.formatDouble2String(weddingCarProduct.getMarketPrice())));
            }

            if (secKill.getParams() != null && secKill.getParams()
                    .isLowest()) {
                tvLowestPrice.setVisibility(View.VISIBLE);
            } else {
                tvLowestPrice.setVisibility(View.GONE);
            }
            //倒计时
            handler.removeCallbacks(mRunnable);
            handler.post(mRunnable);
        } else {
            secKillLayout.setVisibility(View.GONE);
        }
    }

    private Runnable mRunnable = new Runnable() {
        public void run() {
            long timeDistance = (countDownTime - HljTimeUtils.getServerCurrentTimeMillis()) / 1000;
            if (timeDistance > 0) {
                // 还在倒计时
                int nextHour;
                int nextMinute;
                int nextSecond;
                nextHour = (int) (timeDistance / (60 * 60));
                timeDistance = timeDistance - (nextHour * 60 * 60);
                nextMinute = (int) (timeDistance / (60));
                timeDistance = timeDistance - (nextMinute * 60);
                nextSecond = (int) timeDistance;
                tvLimitHour.setText(nextHour >= 10 ? String.valueOf(nextHour) : "0" + nextHour);
                tvLimitMinute.setText(nextMinute >= 10 ? String.valueOf(nextMinute) : "0" +
                        nextMinute);
                tvLimitSecond.setText(nextSecond >= 10 ? String.valueOf(nextSecond) : "0" +
                        nextSecond);
                handler.postDelayed(this, 1000);
            } else {
                handler.removeCallbacks(this);
            }
        }
    };

    private class ResultZip {
        List<Brand> hotBrands;
        List<Poster> posters;
        List<CarLesson> carLessons;
        HljCarHttpData<List<SecKill>> secKillData;
        CarMerchantContactInfo carMerchantContactInfo;


        public ResultZip(
                List<Brand> hotBrands,
                List<Poster> posters,
                List<CarLesson> carLessons,
                HljCarHttpData<List<SecKill>> secKillData,
                CarMerchantContactInfo carMerchantContactInfo) {
            this.hotBrands = hotBrands;
            this.posters = posters;
            this.carLessons = carLessons;
            this.secKillData = secKillData;
            this.carMerchantContactInfo = carMerchantContactInfo;
        }
    }

    public ScrollableLayout getScrollableLayout() {
        return scrollView.getRefreshableView();
    }

    public void setHotCount(int hotCount) {
        TextView tabTitle = tabIndicator.getTabView(0)
                .findViewById(R.id.title);
        tabTitle.setText("精选车队" + (hotCount == 0 ? "" : "·" + String.valueOf(hotCount)));
    }

    public void setSelfCount(int selfCount) {
        TextView tabTitle = tabIndicator.getTabView(1)
                .findViewById(R.id.title);
        tabTitle.setText("个性自选" + (selfCount == 0 ? "" : "·" + String.valueOf(selfCount)));
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshCarCount();
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(loadSubscriber, rxBusSub);
        if (handler != null) {
            handler.removeCallbacks(mRunnable);
        }
    }
}
