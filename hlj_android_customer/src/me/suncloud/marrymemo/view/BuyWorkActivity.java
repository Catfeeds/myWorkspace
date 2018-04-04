package me.suncloud.marrymemo.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.suncloud.hljweblibrary.HljWeb;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.models.modelwrappers.PosterData;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.BigWorkViewHolder;
import com.hunliji.hljhttplibrary.entities.HljHttpResultZip;
import com.hunliji.hljhttplibrary.entities.HljRZField;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.PosterUtil;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.CustomCommonApi;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.model.main.YouLike;
import me.suncloud.marrymemo.util.BannerUtil;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.NoticeUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.newsearch.NewSearchActivity;
import me.suncloud.marrymemo.view.notification.MessageHomeActivity;
import rx.Observable;
import rx.functions.Func2;

/**
 * 买套餐activity
 * Created by jinxin on 2016/12/1 0001.
 */

public class BuyWorkActivity extends HljBaseNoBarActivity implements PullToRefreshBase
        .OnRefreshListener {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.msg_notice)
    View msgNotice;
    @BindView(R.id.msg_count)
    TextView msgCount;
    @BindView(R.id.action_layout)
    RelativeLayout actionLayout;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    private NoticeUtil noticeUtil;
    private View headerView1;
    private View headerView2;
    private View footerView;
    private GridLayout propertyGridLayout;
    private HljHttpSubscriber initSubscriber;
    private City mCity;
    private BuyWorkAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_work);
        ButterKnife.bind(this);
        setDefaultStatusBarPadding();
        mCity = Session.getInstance()
                .getMyCity(this);
        noticeUtil = new NoticeUtil(this, msgCount, msgNotice);
        initHeadAndFooter();
        adapter = new BuyWorkAdapter();
        adapter.setFooterView(footerView);
        adapter.setHeaderView1(headerView1);
        adapter.setHeaderView2(headerView2);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setOnRefreshListener(this);
        recyclerView.getRefreshableView()
                .setLayoutManager(manager);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
        onRefresh(recyclerView);

        initTracker();
    }

    private void initTracker() {
        HljVTTagger.tagViewParentName(recyclerView, "package_list");
    }

    private void initHeadAndFooter() {
        headerView1 = getLayoutInflater().inflate(R.layout.buy_work_header1, null);
        propertyGridLayout = (GridLayout) headerView1.findViewById(R.id.property_menu_layout);
        headerView2 = getLayoutInflater().inflate(R.layout.buy_work_header2, null);
        footerView = getLayoutInflater().inflate(R.layout.hlj_foot_no_more___cm, null);
        footerView.findViewById(R.id.no_more_hint)
                .setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.btn_back)
    public void onBackPressed(View view) {
        super.onBackPressed();
    }

    @OnClick(R.id.icon_search)
    public void onSearch(View view) {
        Intent intent = new Intent(this, NewSearchActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    @OnClick(R.id.btn_msg)
    public void onSms(View view) {
        if (Util.loginBindChecked(this, Constants.RequestCode.NOTIFICATION_PAGE)) {
            Intent intent = new Intent(this, MessageHomeActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }


    @Override
    public void onRefresh(final PullToRefreshBase refreshView) {
        if (initSubscriber == null || initSubscriber.isUnsubscribed()) {
            initSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setContentView(recyclerView)
                    .setEmptyView(emptyView)
                    .setPullToRefreshBase(refreshView)
                    .setProgressBar(refreshView.isRefreshing() ? null : progressBar)
                    .setOnNextListener(new SubscriberOnNextListener<ResultZip>() {
                        @Override
                        public void onNext(ResultZip resultZip) {
                            if (resultZip != null) {
                                List<Poster> posters = PosterUtil.getPosterList(resultZip
                                                .posterData.getFloors(),
                                        Constants.POST_SITES.FIND_SETMEAL_CATEGORY,
                                        false);
                                setPoster(posters);
                                setWork(resultZip.youLike);
                            }
                        }
                    })
                    .build();
        }
        Observable<PosterData> posterObservable = com.hunliji.hljhttplibrary.api.CommonApi
                .getBanner(
                this,
                HljCommon.BLOCK_ID.FIND_SETMEAL_CATEGORY,
                mCity.getId());
        Observable<YouLike> likeObservable = CustomCommonApi.getYouLike(0);
        Observable<ResultZip> resultZipObservable = Observable.zip(posterObservable,
                likeObservable,
                new Func2<PosterData, YouLike, ResultZip>() {
                    @Override
                    public ResultZip call(PosterData posterData, YouLike youLike) {
                        ResultZip zip = new ResultZip();
                        zip.posterData = posterData;
                        zip.youLike = youLike;
                        return zip;
                    }
                });
        resultZipObservable.subscribe(initSubscriber);
    }

    private void setPoster(List<Poster> posters) {
        if (posters == null || posters.isEmpty()) {
            propertyGridLayout.setVisibility(View.GONE);
            return;
        }
        propertyGridLayout.setVisibility(View.VISIBLE);
        propertyGridLayout.removeAllViews();
        int iconSize = Util.dp2px(this, 40);
        for (final Poster poster : posters) {
            View.inflate(this, R.layout.merchant_filter_property, propertyGridLayout);
            int index = propertyGridLayout.getChildCount() - 1;
            View view = propertyGridLayout.getChildAt(index);
            view.setTag(poster);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BannerUtil.bannerAction(BuyWorkActivity.this, poster, mCity, false, null);
                }
            });
            ImageView ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
            TextView tvName = (TextView) view.findViewById(R.id.tv_name);
            String imgPath = JSONUtil.getImagePath(poster.getPath(), iconSize);
            if (!JSONUtil.isEmpty(imgPath)) {
                Glide.with(this)
                        .load(imgPath)
                        .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                        .into(ivIcon);
            } else {
                Glide.with(this)
                        .clear(ivIcon);
            }
            tvName.setText(poster.getTitle());
        }
    }

    private void setWork(YouLike like) {
        if (like == null || like.getRecommend() == null) {
            return;
        }
        adapter.setWorks(like.getRecommend());
        adapter.notifyDataSetChanged();
    }


    @Override
    protected void onFinish() {
        if (initSubscriber != null && !initSubscriber.isUnsubscribed()) {
            initSubscriber.unsubscribe();
        }
        super.onFinish();
    }

    @Override
    protected void onResume() {
        if (noticeUtil != null) {
            noticeUtil.onResume();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (noticeUtil != null) {
            noticeUtil.onPause();
        }
        super.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Constants.RequestCode.NOTIFICATION_PAGE:
                    Intent intent = new Intent(this, MessageHomeActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                    break;
                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    class BuyWorkAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
            OnItemClickListener {
        private View headerView1;
        private View headerView2;
        private View footerView;
        private final int HEAD1 = 1;
        private final int HEAD2 = 2;
        private final int FOOTER = 4;
        private final int ITEM = 3;
        private List<Work> works;

        public void setWorks(List<Work> works) {
            this.works = works;
        }

        public void setHeaderView1(View headerView1) {
            this.headerView1 = headerView1;
        }

        public void setHeaderView2(View headerView2) {
            this.headerView2 = headerView2;
        }

        public void setFooterView(View footerView) {
            this.footerView = footerView;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case HEAD1:
                    return new ExtraViewHolder(headerView1);
                case HEAD2:
                    return new ExtraViewHolder(headerView2);
                case FOOTER:
                    return new ExtraViewHolder(footerView);
                case ITEM:
                    View itemView = getLayoutInflater().inflate(R.layout.big_commom_work_item__cv,
                            parent,
                            false);
                    BigWorkViewHolder holder = new BigWorkViewHolder(itemView);
                    holder.setShowMerchantProperty(true);
                    holder.setOnItemClickListener(this);
                    return holder;
                default:
                    return null;
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder h, final int position) {
            int type = getItemViewType(position);
            if (type == ITEM) {
                BigWorkViewHolder holder = (BigWorkViewHolder) h;
                int index = position - 2;
                holder.setShowBottomLineView(index < works.size() - 1);
                holder.setView(BuyWorkActivity.this, works.get(index), position, type);
            }
        }

        @Override
        public int getItemCount() {
            return (works == null ? 0 : works.size()) + (headerView1 == null ? 0 : 1) +
                    (headerView2 == null ? 0 : 1) + ((footerView == null || works == null) ? 0 : 1);
        }

        @Override
        public int getItemViewType(int position) {
            int type = -1;
            if (position == 0 && headerView1 != null) {
                type = HEAD1;
            } else if (position == 1 && headerView2 != null) {
                type = HEAD2;
            } else if (position == getItemCount() - 1 && footerView != null) {
                type = FOOTER;
            } else {
                type = ITEM;
            }
            return type;
        }

        @Override
        public void onItemClick(int position, Object object) {
            if (object != null) {
                Work work = (Work) object;
                String link = work.getLink();
                if (JSONUtil.isEmpty(link)) {
                    Intent intent = new Intent(BuyWorkActivity.this, WorkActivity.class);
                    intent.putExtra("id", work.getId());
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                } else {
                    HljWeb.startWebView(BuyWorkActivity.this, link);
                }
            }
        }
    }

    class ExtraViewHolder extends RecyclerView.ViewHolder {
        private View itemView;

        public ExtraViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
        }
    }

    class ResultZip extends HljHttpResultZip {
        @HljRZField
        YouLike youLike;
        @HljRZField
        PosterData posterData;
    }
}
