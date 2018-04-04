package me.suncloud.marrymemo.view.themephotography;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.example.suncloud.hljweblibrary.HljWeb;
import com.google.gson.JsonObject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.models.modelwrappers.PosterData;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.api.CommonApi;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.PosterUtil;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;
import com.hunliji.hljtrackerlibrary.utils.TrackerUtil;
import com.slider.library.Indicators.LinePageExIndicator;
import com.slider.library.SliderLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.FlowAdapter;
import me.suncloud.marrymemo.adpter.themephotography.ThemeLightLuxuryAdapter;
import me.suncloud.marrymemo.api.themephotography.ThemeApi;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.util.BannerUtil;
import me.suncloud.marrymemo.util.DataConfig;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.NoticeUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.notification.MessageHomeActivity;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * 旅拍严选
 * Created by jinxin on 2016/10/10.
 */

public class ThemeLightLuxuryActivity extends HljBaseNoBarActivity implements
        PullToRefreshVerticalRecyclerView.OnRefreshListener, View.OnClickListener {

    public static final String CPM_SOURCE = "trave_light_luxury";

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.msg_notice)
    View msgNotice;
    @BindView(R.id.msg_count)
    TextView msgCount;
    @BindView(R.id.list)
    PullToRefreshVerticalRecyclerView list;
    @BindView(R.id.emptyView)
    HljEmptyView emptyView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    private NoticeUtil noticeUtil;

    private View headerView;
    private View bannerLayout;
    private SliderLayout posterView;
    private LinePageExIndicator indicator;
    private View floorLayout;
    private ImageView imgFloorLeft;
    private ImageView imgFloorRight1;
    private ImageView imgFloorRight2;
    private View serviceLayout;
    private View footView;
    private View endView;
    private View loadView;
    private String url;
    private LinearLayoutManager manager;
    private SpaceItemDecoration itemDecoration;
    private ThemeLightLuxuryAdapter adapter;
    private HljHttpSubscriber pageSubscriber;//分页
    private List<Work> works;
    private City mCity;
    private Point point;
    private int bannerHeight;
    private int floorImageLeftWidth;
    private int floorImageRightWidth;
    private FlowAdapter flowAdapter;
    private ArrayList<Poster> cyclePosters;
    private HljHttpSubscriber initSubscriber;

    @Override
    public String pageTrackTagName() {
        return "旅拍严选";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_theme);
        ButterKnife.bind(this);
        setDefaultStatusBarPadding();
        initHeaderAndFooter();
        initConstant();
        initWidget();
        onRefresh(list);

        initTracker();
    }

    private void initTracker() {
        HljVTTagger.tagViewParentName(list, "yanxuan_list");
    }

    public void onBackPressed(View view) {
        super.onBackPressed();
    }

    private void initConstant() {
        url = Constants.HttpPath.GET_LUXURY_LIST;
        mCity = Session.getInstance()
                .getMyCity(this);
        works = new ArrayList<>();
        cyclePosters = new ArrayList<>();
        adapter = new ThemeLightLuxuryAdapter(this, works);
        adapter.setHeaderView(headerView);
        adapter.setFooterView(footView);
        flowAdapter = new FlowAdapter(this, cyclePosters,  R.layout.flow_item);
        flowAdapter.setCity(mCity);
        posterView.setPagerAdapter(flowAdapter);
        flowAdapter.setSliderLayout(posterView);
        posterView.setPresetTransformer(4);
        posterView.setCustomIndicator(indicator);
    }

    private void initHeaderAndFooter() {
        point = JSONUtil.getDeviceSize(this);
        bannerHeight = Math.round(point.x / 2);
        floorImageLeftWidth = Math.round(point.x * 1.0f * 7 / (7 + 9));
        floorImageRightWidth = Math.round(point.x * 1.0f * 9 / (7 + 9));
        headerView = getLayoutInflater().inflate(R.layout.light_luxury_header, null);
        bannerLayout = headerView.findViewById(R.id.posters_layout);
        posterView = (SliderLayout) headerView.findViewById(R.id.posters_view);
        indicator = (LinePageExIndicator) headerView.findViewById(R.id.flow_indicator);
        serviceLayout = headerView.findViewById(R.id.service_layout);
        serviceLayout.setOnClickListener(this);
        floorLayout = headerView.findViewById(R.id.floor_layout);
        imgFloorLeft = (ImageView) headerView.findViewById(R.id.img_floor_left);
        imgFloorRight1 = (ImageView) headerView.findViewById(R.id.img_floor_right1);
        imgFloorRight2 = (ImageView) headerView.findViewById(R.id.img_floor_right2);
        bannerLayout.getLayoutParams().height = bannerHeight;
        floorLayout.getLayoutParams().height = bannerHeight;
        imgFloorLeft.getLayoutParams().width = floorImageLeftWidth;
        imgFloorLeft.getLayoutParams().height = bannerHeight;
        imgFloorRight1.getLayoutParams().width = floorImageRightWidth;
        imgFloorRight1.getLayoutParams().height = Math.round(bannerHeight / 2);
        imgFloorRight2.getLayoutParams().width = floorImageRightWidth;
        imgFloorRight2.getLayoutParams().height = Math.round(bannerHeight / 2);
        footView = getLayoutInflater().inflate(R.layout.list_foot_no_more_2, null);
        endView = footView.findViewById(com.hunliji.hljquestionanswer.R.id.no_more_hint);
        loadView = footView.findViewById(com.hunliji.hljquestionanswer.R.id.loading);
    }

    private void initBanner(PosterData posterData) {
        if (posterData == null || posterData.getFloors() == null) {
            headerView.setVisibility(View.GONE);
            return;
        } else {
            headerView.setVisibility(View.VISIBLE);
        }
        JsonObject floor = posterData.getFloors();
        List<Poster> posters = PosterUtil.getPosterList(floor,
                Constants.POST_SITES.TRAVEL_STRICT_SELECTED_BANNER,
                false);
        //轮播
        if (!posters.isEmpty()) {
            bannerLayout.setVisibility(View.VISIBLE);
            cyclePosters.clear();
            cyclePosters.addAll(posters);
            flowAdapter.setmDate(cyclePosters);
            if (flowAdapter.getCount() > 1) {
                posterView.startAutoCycle();
            } else {
                posterView.stopAutoCycle();
            }
        } else {
            bannerLayout.setVisibility(View.GONE);
            posterView.stopAutoCycle();
        }

        //楼层
        posters = PosterUtil.getPosterList(floor,
                Constants.POST_SITES.TRAVEL_STRICT_SELECTED_ALBUM,
                false);
        if (!posters.isEmpty()) {
            floorLayout.setVisibility(View.VISIBLE);
            for (int i = 0, size = posters.size(); i < size; i++) {
                Poster p = posters.get(i);
                if (p != null && p.getId() > 0) {
                    switch (i) {
                        case 0:
                            setPosterViewValue(imgFloorLeft,
                                    imgFloorLeft,
                                    null,
                                    p,
                                    null,
                                    i,
                                    imgFloorLeft.getLayoutParams().height);
                            break;
                        case 1:
                            setPosterViewValue(imgFloorRight1,
                                    imgFloorRight1,
                                    null,
                                    p,
                                    null,
                                    i,
                                    imgFloorRight1.getLayoutParams().width);
                            break;
                        case 2:
                            setPosterViewValue(imgFloorRight2,
                                    imgFloorRight2,
                                    null,
                                    p,
                                    null,
                                    i,
                                    imgFloorRight2.getLayoutParams().width);
                            break;
                        default:
                            break;
                    }
                }
            }
        } else {
            floorLayout.setVisibility(View.GONE);
        }
        serviceLayout.setVisibility(View.VISIBLE);
    }

    private void setPosterViewValue(
            View posterView,
            ImageView posterImageView,
            TextView posterTitleView,
            Poster poster,
            String sid,
            int position,
            int width) {
        if (poster != null) {
            if (poster.getId() > 0) {
                posterView.setVisibility(View.VISIBLE);
                posterView.setOnClickListener(new OnPosterClickListener(poster, position + 1, sid));
            } else {
                posterView.setVisibility(View.INVISIBLE);
                posterView.setOnClickListener(null);
            }
            if (posterTitleView != null) {
                posterTitleView.setText(poster.getTitle());
            }
            if (posterImageView != null) {
                String url = JSONUtil.getImagePathWithoutFormat(poster.getPath(), width);
                Glide.with(this)
                        .asBitmap()
                        .load(url)
                        .apply(new RequestOptions().format(DecodeFormat.PREFER_ARGB_8888)
                                .placeholder(R.mipmap.icon_empty_image)
                                .dontAnimate())
                        .into(posterImageView);
            }
        } else {
            posterView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.service_layout:
                DataConfig config = Session.getInstance()
                        .getDataConfig(this);
                if (config != null) {
                    HljWeb.startWebView(this, config.getTravelStrictSelectedInstructionUrl());
                }
                break;
            default:
                break;
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
                BannerUtil.bannerAction(ThemeLightLuxuryActivity.this,
                        poster,
                        mCity,
                        false,
                        TrackerUtil.getSiteJson(sid, position, poster.getTitle()));
            }
        }
    }

    private void initPagination(int pageCount) {
        if (pageSubscriber != null && !pageSubscriber.isUnsubscribed()) {
            pageSubscriber.unsubscribe();
        }

        Observable<HljHttpData<List<Work>>> observable = PaginationTool.buildPagingObservable(

                list.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpData<List<Work>>>() {
                    @Override
                    public Observable<HljHttpData<List<Work>>> onNextPage(int page) {
                        return ThemeApi.getLightLuxuryList(url, page);
                    }
                })
                .setLoadView(loadView)
                .setEndView(endView)
                .build()
                .getPagingObservable();
        pageSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<Work>>>() {

                    @Override
                    public void onNext(
                            HljHttpData<List<Work>> listHljHttpData) {
                        works.addAll(listHljHttpData.getData());
                        adapter.notifyDataSetChanged();
                    }
                })
                .build();
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSubscriber);
    }

    private void initWidget() {
        this.title.setText(getString(R.string.label_light_luxury));
        manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        itemDecoration = new SpaceItemDecoration();
        list.getRefreshableView()
                .setLayoutManager(manager);
        list.getRefreshableView()
                .addItemDecoration(itemDecoration);
        list.getRefreshableView()
                .setAdapter(adapter);
        list.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        if (initSubscriber == null || initSubscriber.isUnsubscribed()) {
            Observable<PosterData> bannerObservable = CommonApi.getBanner(this,
                    HljCommon.BLOCK_ID.KBLOCK_LIGHT_LUXURY_PAGE,
                    mCity.getId());

            Observable<HljHttpData<List<Work>>> worksObservable = ThemeApi.getLightLuxuryList(url,
                    1);

            Observable<ResultZip> observable = Observable.zip(bannerObservable,
                    worksObservable,
                    new Func2<PosterData, HljHttpData<List<Work>>, ResultZip>() {
                        @Override
                        public ResultZip call(
                                PosterData posterData, HljHttpData<List<Work>> listHljHttpData) {
                            ResultZip zip = new ResultZip();
                            zip.posterData = posterData;
                            zip.listHljHttpData = listHljHttpData;
                            return zip;
                        }
                    });
            initSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setContentView(list.getRefreshableView())
                    .setEmptyView(emptyView)
                    .setPullToRefreshBase(list)
                    .setProgressBar(list.isRefreshing() ? null : progressBar)
                    .setOnNextListener(new SubscriberOnNextListener<ResultZip>() {
                        @Override
                        public void onNext(ResultZip resultZip) {
                            int pageCount = 0;
                            initBanner(resultZip.posterData);
                            if (resultZip.listHljHttpData != null && !CommonUtil.isCollectionEmpty(
                                    resultZip.listHljHttpData.getData())) {
                                pageCount = resultZip.listHljHttpData.getPageCount();
                                works.clear();
                                works.addAll(resultZip.listHljHttpData.getData());
                                adapter.notifyDataSetChanged();
                            }
                            initPagination(pageCount);
                        }
                    })
                    .build();
            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(initSubscriber);


        }
    }

    @Override
    protected void onFinish() {
        if (pageSubscriber != null && !pageSubscriber.isUnsubscribed()) {
            pageSubscriber.unsubscribe();
        }
        if (initSubscriber != null && !initSubscriber.isUnsubscribed()) {
            initSubscriber.unsubscribe();
        }
        if (posterView != null && flowAdapter != null && flowAdapter.getCount() > 1) {
            posterView.stopAutoCycle();
        }
        super.onFinish();
    }

    @Override
    protected void onResume() {
        if (noticeUtil == null) {
            noticeUtil = new NoticeUtil(this, msgCount, msgNotice);
        }
        noticeUtil.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (noticeUtil == null) {
            noticeUtil = new NoticeUtil(this, msgCount, msgNotice);
        }
        noticeUtil.onPause();
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

    class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        private int padding;

        public SpaceItemDecoration() {
            DisplayMetrics dm = getResources().getDisplayMetrics();
            padding = Math.round(dm.density * 10);
        }

        @Override
        public void getItemOffsets(
                Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            int size = parent.getAdapter()
                    .getItemCount();
            if (position == 0 || position == size - 1) {
                outRect.top = 0;
            } else {
                outRect.top = padding;
            }
        }
    }

    private static class ResultZip {
        PosterData posterData;
        HljHttpData<List<Work>> listHljHttpData;
    }


}
