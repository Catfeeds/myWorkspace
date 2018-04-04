package me.suncloud.marrymemo.view.brigade;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.models.modelwrappers.PosterData;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.api.CommonApi;
import com.hunliji.hljhttplibrary.entities.HljHttpResultZip;
import com.hunliji.hljhttplibrary.entities.HljRZField;
import com.hunliji.hljhttplibrary.utils.GsonUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.PosterUtil;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.slider.library.Indicators.CirclePageExIndicator;
import com.slider.library.SliderLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.FlowAdapter;
import me.suncloud.marrymemo.adpter.brigade.BrigadeLimitBuyAdapter;
import me.suncloud.marrymemo.api.brigade.BrigadeApi;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.model.wrappers.LimitBuyContent;
import me.suncloud.marrymemo.model.wrappers.LimitBuyList;
import me.suncloud.marrymemo.util.NoticeUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.notification.MessageHomeActivity;
import rx.Observable;
import rx.functions.Func2;

/**
 * Created by mo_yu on 2017/9/6.旅拍限时团购
 */

public class BrigadeLimitBuyActivity extends HljBaseNoBarActivity implements
        PullToRefreshVerticalRecyclerView.OnRefreshListener {

    public static final String CPM_SOURCE = "trave_flash_sale";
    protected static final long COUNT_DOWN_INTERVAL = 1000;//倒计时 时间间隔
    public static final String ARG_ID = "id";

    @BindView(R.id.btn_back)
    ImageButton btnBack;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.btn_msg)
    ImageButton btnMsg;
    @BindView(R.id.msg_notice)
    View msgNotice;
    @BindView(R.id.msg_count)
    TextView msgCount;
    @BindView(R.id.msg_layout)
    RelativeLayout msgLayout;
    @BindView(R.id.action_layout)
    RelativeLayout actionLayout;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.btn_scroll_top)
    ImageButton btnScrollTop;

    private long id;
    private long countDownTime;
    private int bannerHeight;
    private boolean isHide;
    private Handler handler;
    private NoticeUtil noticeUtil;
    private City mCity;
    private FlowAdapter flowAdapter;
    private ArrayList<Poster> posters;
    private BrigadeLimitBuyAdapter adapter;
    private ArrayList<LimitBuyContent> limitBuyContents;

    private HeaderViewHolder headerViewHolder;
    private FooterViewHolder footerViewHolder;
    private LinearLayoutManager layoutManager;

    private HljHttpSubscriber refreshSubscriber;

    @Override
    public String pageTrackTagName() {
        return "限时团购";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brigade_limit_buy);
        ButterKnife.bind(this);
        setDefaultStatusBarPadding();
        initValue();
        initView();
        initTracker();
        onRefresh(null);
    }

    private void initTracker() {
        HljVTTagger.tagViewParentName(recyclerView, "travel_limit_buy_meal_list");
    }

    private void initValue() {
        id = getIntent().getLongExtra(ARG_ID, 0);
        posters = new ArrayList<>();
        limitBuyContents = new ArrayList<>();
        bannerHeight = Math.round(CommonUtil.getDeviceSize(this).x / 2);
        mCity = Session.getInstance()
                .getMyCity(this);
        handler = new Handler();
    }

    private void initView() {
        title.setText(R.string.title_activity_brigade_limit_buy);

        View headerView = View.inflate(this, R.layout.limit_buy_header, null);
        headerViewHolder = new HeaderViewHolder(headerView);
        //顶部广告位
        flowAdapter = new FlowAdapter(this, posters, R.layout.flow_item);
        headerViewHolder.viewFlow.setOverScrollMode(View.OVER_SCROLL_NEVER);
        headerViewHolder.viewFlow.getLayoutParams().height = bannerHeight;
        headerViewHolder.viewFlow.setPagerAdapter(flowAdapter);
        flowAdapter.setSliderLayout(headerViewHolder.viewFlow);
        headerViewHolder.viewFlow.setCustomIndicator(headerViewHolder.flowIndicator);
        if (flowAdapter.getCount() > 0) {
            headerViewHolder.bannerLayout.setVisibility(View.VISIBLE);
            if (flowAdapter.getCount() > 1) {
                headerViewHolder.viewFlow.startAutoCycle();
            }
        }
        headerViewHolder.viewFlow.setOnPageChangeListener(new ViewPager
                .SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }
        });
        View footerView = View.inflate(this, R.layout.list_foot_no_more_2, null);
        footerViewHolder = new FooterViewHolder(footerView);

        adapter = new BrigadeLimitBuyAdapter(this, limitBuyContents);
        adapter.setHeaderView(headerView);
        adapter.setFooterView(footerView);
        recyclerView.setOnRefreshListener(this);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.getRefreshableView()
                .setLayoutManager(layoutManager);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
        recyclerView.getRefreshableView()
                .addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(
                            RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        if (layoutManager != null && layoutManager.findFirstVisibleItemPosition()
                                < 5) {
                            if (!isHide) {
                                hideFiltrateAnimation();
                            }
                        } else if (isHide) {
                            if (btnScrollTop.getVisibility() == View.GONE) {
                                btnScrollTop.setVisibility(View.VISIBLE);
                            }
                            showFiltrateAnimation();
                        }
                    }
                });
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        if (id <= 0) {
            return;
        }
        if (refreshSubscriber == null || refreshSubscriber.isUnsubscribed()) {
            Observable<PosterData> bannerObservable = CommonApi.getBanner(this,
                    HljCommon.BLOCK_ID.BrigadeLimitBuyActivity,
                    mCity.getId());

            Observable<JsonElement> worksObservable = BrigadeApi.getTravelDetailObb(id);

            Observable<ResultZip> observable = Observable.zip(bannerObservable,
                    worksObservable,
                    new Func2<PosterData, JsonElement, ResultZip>() {
                        @Override
                        public ResultZip call(
                                PosterData posterData, JsonElement jsonElement) {
                            ResultZip zip = new ResultZip();
                            zip.posterData = posterData;
                            try {
                                countDownTime = jsonElement.getAsJsonObject()
                                        .get("count_down_time")
                                        .getAsLong();
                                List<LimitBuyList> limitBuyLists = new ArrayList<>();
                                limitBuyLists.addAll(Arrays.asList(GsonUtil.getGsonInstance()
                                        .fromJson(jsonElement.getAsJsonObject()
                                                .get("limit_buy")
                                                .getAsJsonArray(), LimitBuyList[].class)));
                                zip.limitBuyList = limitBuyLists;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return zip;
                        }
                    });
            refreshSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<ResultZip>() {
                        @Override
                        public void onNext(ResultZip resultZip) {
                            //banner
                            List<Poster> posterList = PosterUtil.getPosterList(resultZip
                                            .posterData.getFloors(),
                                    HljCommon.POST_SITES.TRAVEL_LIMIT_TOP_BANNER,
                                    false);
                            posters.clear();
                            posters.addAll(posterList);
                            setPosterView();
                            //list
                            if (!CommonUtil.isCollectionEmpty(resultZip.limitBuyList) &&
                                    !CommonUtil.isCollectionEmpty(
                                    resultZip.limitBuyList.get(0)
                                            .getContent())) {
                                limitBuyContents.clear();
                                limitBuyContents.addAll(resultZip.limitBuyList.get(0)
                                        .getContent());
                            }
                            //倒计时
                            handler.removeCallbacks(mRunnable);
                            handler.post(mRunnable);
                            adapter.notifyDataSetChanged();
                        }
                    })
                    .setProgressBar(progressBar)
                    .setEmptyView(emptyView)
                    .setContentView(recyclerView)
                    .setPullToRefreshBase(recyclerView)
                    .build();
            observable.subscribe(refreshSubscriber);
        }
    }

    @OnClick(R.id.btn_back)
    public void onBtnBackClicked() {
        onBackPressed();
    }

    @OnClick(R.id.btn_scroll_top)
    public void onScrollTop() {
        scrollTop();
    }

    private static class ResultZip extends HljHttpResultZip {
        @HljRZField
        PosterData posterData;
        @HljRZField
        List<LimitBuyList> limitBuyList;
    }

    private void setPosterView() {
        flowAdapter.setmDate(posters);
        if (headerViewHolder.bannerLayout != null && headerViewHolder.viewFlow != null) {
            if (flowAdapter.getCount() == 0 || posters.size() == 0) {
                headerViewHolder.viewFlow.stopAutoCycle();
                headerViewHolder.bannerLayout.setVisibility(View.GONE);
            } else {
                headerViewHolder.bannerLayout.setVisibility(View.VISIBLE);
                if (flowAdapter.getCount() > 1) {
                    headerViewHolder.viewFlow.startAutoCycle();
                } else {
                    headerViewHolder.viewFlow.stopAutoCycle();
                }
            }
        }
        flowAdapter.notifyDataSetChanged();
    }

    private Runnable mRunnable = new Runnable() {
        public void run() {
            long timeDistance = (countDownTime - HljTimeUtils.getServerCurrentTimeMillis()) / 1000;
            if (timeDistance > 0) {
                // 还在倒计时
                int nextDay = (int) (timeDistance / (60 * 60 * 24));
                int nextHour;
                int nextMinute;
                int nextSecond;
                if (nextDay >= 1) {
                    nextHour = 0;
                    nextMinute = 0;
                    nextDay = 1;
                    nextSecond = 0;
                } else {
                    // 距离结束日期在day天内 (判断还有几天/小时/分/秒)
                    timeDistance = timeDistance - (nextDay * 60 * 60 * 24);
                    nextHour = (int) (timeDistance / (60 * 60));
                    timeDistance = timeDistance - (nextHour * 60 * 60);
                    nextMinute = (int) (timeDistance / (60));
                    timeDistance = timeDistance - (nextMinute * 60);
                    nextSecond = (int) timeDistance;
                }
                headerViewHolder.tvLimitDay.setText(nextDay >= 10 ? String.valueOf(nextDay) : "0"
                        + nextDay);
                headerViewHolder.tvLimitHour.setText(nextHour >= 10 ? String.valueOf(nextHour) :
                        "0" + nextHour);
                headerViewHolder.tvLimitMinute.setText(nextMinute >= 10 ? String.valueOf
                        (nextMinute) : "0" + nextMinute);
                headerViewHolder.tvLimitSecond.setText(nextSecond >= 10 ? String.valueOf
                        (nextSecond) : "0" + nextSecond);
                handler.postDelayed(this, 1000);
            } else {
                handler.removeCallbacks(this);
            }
        }
    };

    @Override
    protected void onResume() {
        if (noticeUtil == null) {
            noticeUtil = new NoticeUtil(this, msgCount, msgNotice);
        }
        noticeUtil.onResume();
        handler.post(mRunnable);
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (noticeUtil == null) {
            noticeUtil = new NoticeUtil(this, msgCount, msgNotice);
        }
        noticeUtil.onPause();
        handler.removeCallbacks(mRunnable);
        super.onPause();
    }

    public void onMessage(View view) {
        if (Util.loginBindChecked(this, Constants.RequestCode.NOTIFICATION_PAGE)) {
            Intent intent = new Intent(this, MessageHomeActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.RequestCode
                .NOTIFICATION_PAGE) {
            Intent intent = new Intent(this, MessageHomeActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //滑动到顶部
    public void scrollTop() {
        if (layoutManager == null || recyclerView == null) {
            return;
        }
        if (layoutManager.findFirstVisibleItemPosition() >= 5) {
            recyclerView.getRefreshableView()
                    .scrollToPosition(5);
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    recyclerView.getRefreshableView()
                            .smoothScrollToPosition(0);
                }
            });
        } else {
            recyclerView.getRefreshableView()
                    .smoothScrollToPosition(0);
        }

    }

    private void showFiltrateAnimation() {
        if (btnScrollTop == null) {
            return;
        }
        isHide = false;
        if (isAnimEnded()) {
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_in_bottom2);
            animation.setFillBefore(true);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    btnScrollTop.post(new Runnable() {
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
            btnScrollTop.startAnimation(animation);
        }
    }

    private boolean isAnimEnded() {
        return btnScrollTop != null && (btnScrollTop.getAnimation() == null || btnScrollTop
                .getAnimation()
                .hasEnded());
    }

    private void hideFiltrateAnimation() {
        if (btnScrollTop == null) {
            return;
        }
        isHide = true;
        if (isAnimEnded()) {
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_out_bottom2);
            animation.setFillAfter(true);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    btnScrollTop.post(new Runnable() {
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
            btnScrollTop.startAnimation(animation);
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        handler.removeCallbacks(mRunnable);
        CommonUtil.unSubscribeSubs(refreshSubscriber);
    }

    static class HeaderViewHolder {
        @BindView(R.id.view_flow)
        SliderLayout viewFlow;
        @BindView(R.id.flow_indicator)
        CirclePageExIndicator flowIndicator;
        @BindView(R.id.banner_layout)
        RelativeLayout bannerLayout;
        @BindView(R.id.tv_limit_day)
        TextView tvLimitDay;
        @BindView(R.id.tv_limit_hour)
        TextView tvLimitHour;
        @BindView(R.id.tv_limit_minute)
        TextView tvLimitMinute;
        @BindView(R.id.tv_limit_second)
        TextView tvLimitSecond;

        HeaderViewHolder(View view) {ButterKnife.bind(this, view);}
    }

    static class FooterViewHolder {
        @BindView(R.id.no_more_hint)
        TextView noMoreHint;
        @BindView(R.id.loading)
        LinearLayout loading;

        FooterViewHolder(View view) {ButterKnife.bind(this, view);}
    }
}
