package me.suncloud.marrymemo.view.brigade;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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
import com.hunliji.hljhttplibrary.entities.HljHttpResultZip;
import com.hunliji.hljhttplibrary.entities.HljRZField;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.PosterUtil;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImagePath;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.brigade.BrigadeAdapter;
import me.suncloud.marrymemo.api.brigade.BrigadeApi;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.util.BannerUtil;
import me.suncloud.marrymemo.util.NoticeUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.notification.MessageHomeActivity;
import rx.Observable;
import rx.functions.Func2;

/**
 * Created by hua_rong on 2017/7/24.
 * 本周热卖
 */

public class BrigadeWeekHotsActivity extends HljBaseNoBarActivity implements
        PullToRefreshVerticalRecyclerView.OnRefreshListener {

    public static final String CPM_SOURCE = "trave_hot_sale_weekly";

    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.msg_notice)
    View msgNotice;
    @BindView(R.id.msg_count)
    TextView msgCount;
    private BrigadeAdapter adapter;
    private List<Work> brigadeList;
    private View headerView;
    private HeaderViewHolder viewHolder;
    private HljHttpSubscriber refreshSubscriber;
    private City city;
    private NoticeUtil noticeUtil;

    @Override
    public String pageTrackTagName() {
        return "本周热卖";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bridage_weeks_hot);
        ButterKnife.bind(this);
        setDefaultStatusBarPadding();
        initHeaderView();
        initNetError();
        initView();
        onRefresh(recyclerView);
        tvTitle.setText(R.string.title_activity_hot_this_week);

        initTracker();
    }

    private void initTracker() {
        HljVTTagger.tagViewParentName(recyclerView.getRefreshableView(), "hot_sale_weekly_list");
    }

    @OnClick(R.id.msg_layout)
    public void onOkButtonClick() {
        if (Util.loginBindChecked(this, Constants.RequestCode.NOTIFICATION_PAGE)) {
            Intent intent = new Intent(this, MessageHomeActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

    @OnClick(R.id.btn_back)
    void onBack() {
        super.onBackPressed();
    }

    private void initHeaderView() {
        headerView = View.inflate(this, R.layout.brigade_hot_this_week_header, null);
        viewHolder = new HeaderViewHolder(headerView);
    }

    private void initNetError() {
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                onRefresh(recyclerView);
            }
        });
    }

    private void initView() {
        noticeUtil = new NoticeUtil(this, msgCount, msgNotice);
        noticeUtil.onResume();
        brigadeList = new ArrayList<>();
        city = Session.getInstance()
                .getMyCity(this);
        adapter = new BrigadeAdapter(this, brigadeList);
        adapter.setHeaderView(headerView);
        recyclerView.setOnRefreshListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.getRefreshableView()
                .setLayoutManager(linearLayoutManager);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (noticeUtil != null) {
            noticeUtil.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (noticeUtil != null) {
            noticeUtil.onPause();
        }
    }

    @Override
    public void onRefresh(final PullToRefreshBase refreshView) {
        if (refreshSubscriber == null || refreshSubscriber.isUnsubscribed()) {
            Observable<HljHttpData<List<Work>>> aObservable = BrigadeApi.getWeekHots();
            Observable<PosterData> bObservable = CommonApi.getBanner(this,
                    HljCommon.BLOCK_ID.WeekHotsActivity,
                    city.getId());
            Observable observable = Observable.zip(aObservable,
                    bObservable,
                    new Func2<HljHttpData<List<Work>>, PosterData, ResultZip>() {
                        @Override
                        public ResultZip call(
                                HljHttpData<List<Work>> hljHttpData, PosterData posterData) {
                            return new ResultZip(hljHttpData, posterData);
                        }
                    });
            refreshSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<ResultZip>() {

                        @Override
                        public void onNext(ResultZip resultZip) {
                            HljHttpData<List<Work>> hljHttpData = resultZip.hljHttpData;
                            recyclerView.setBackgroundResource(R.drawable.bg_gradient_purple);
                            List<Poster> weekHotListPoster = PosterUtil.getPosterList(resultZip
                                            .posterData.getFloors(),
                                    Constants.POST_SITES.SITE_WEEKLY_HOT_SALE,
                                    false);
                            viewHolder.setHeaderView(weekHotListPoster);
                            brigadeList.clear();
                            if (hljHttpData != null && !CommonUtil.isCollectionEmpty(hljHttpData
                                    .getData())) {
                                brigadeList.addAll(hljHttpData.getData());
                            }
                            adapter.setBrigadeList(brigadeList);
                            adapter.notifyDataSetChanged();
                        }
                    })
                    .setProgressBar(refreshView.isRefreshing() ? null : progressBar)
                    .setEmptyView(emptyView)
                    .setContentView(refreshView)
                    .setPullToRefreshBase(refreshView)
                    .build();
            observable.subscribe(refreshSubscriber);
        }

    }

    private class ResultZip extends HljHttpResultZip {
        @HljRZField
        PosterData posterData;
        @HljRZField
        HljHttpData<List<Work>> hljHttpData;

        public ResultZip(HljHttpData<List<Work>> hljHttpData, PosterData posterData) {
            this.posterData = posterData;
            this.hljHttpData = hljHttpData;
        }
    }

    class HeaderViewHolder {

        @BindView(R.id.iv_header)
        ImageView ivHeader;
        @BindView(R.id.rl_header)
        RelativeLayout rlHeader;
        private Context context;

        public HeaderViewHolder(View view) {
            ButterKnife.bind(this, view);
            context = view.getContext();
        }

        public void setHeaderView(List<Poster> weekHotListPoster) {
            rlHeader.setVisibility(View.VISIBLE);
            if (!CommonUtil.isCollectionEmpty(weekHotListPoster)) {
                final Poster poster = weekHotListPoster.get(0);
                if (poster != null) {
                    String path = poster.getPath();
                    Point point = CommonUtil.getDeviceSize(context);
                    int width = point.x;
                    int height = point.x / 2;
                    ivHeader.getLayoutParams().width = width;
                    ivHeader.getLayoutParams().height = height;
                    if (!TextUtils.isEmpty(path)) {
                        Glide.with(context)
                                .load(ImagePath.buildPath(path)
                                        .width(width)
                                        .height(height)
                                        .cropPath())
                                .apply(new RequestOptions().override(width, height)
                                        .placeholder(R.mipmap.icon_empty_image)
                                        .error(R.mipmap.icon_empty_image))
                                .into(ivHeader);
                    } else {
                        Glide.with(context)
                                .clear(ivHeader);
                        ivHeader.setImageBitmap(null);
                    }
                    ivHeader.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            BannerUtil.bannerJump(context, poster, null);
                        }
                    });
                }
            }
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(refreshSubscriber);
    }


}
