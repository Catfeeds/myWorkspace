package me.suncloud.marrymemo.view.topBrand;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.api.CommonApi;
import com.hunliji.hljhttplibrary.entities.HljHttpResultZip;
import com.hunliji.hljhttplibrary.entities.HljRZField;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.PosterUtil;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.topBrand.TopBrandApi;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.model.topBrand.CostEffective;
import me.suncloud.marrymemo.util.BannerUtil;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.WorkActivity;
import me.suncloud.marrymemo.widget.RatingView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * 婚照性价top activity
 * Created by jinxin on 2016/11/9.
 */

public class WeddingPhotoTopActivity extends HljBaseActivity implements PullToRefreshBase
        .OnRefreshListener {
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private HljHttpSubscriber refreshSubscriber;
    private View headerView;
    private View footerView;
    private ImageView headerImageView;
    private int headerViewHeight;
    private int headerViewWidth;
    private int imgCoverSize;
    private PhotoTopAdapter adapter;
    private List<CostEffective> costEffectives;
    private LinearLayoutManager manager;
    private City mCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hlj_common_fragment_ptr_recycler_view___cm);
        ButterKnife.bind(this);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        Point p = JSONUtil.getDeviceSize(this);
        headerViewHeight = Math.round(p.x * 3 / 8);
        headerViewWidth = p.x;
        imgCoverSize = Math.round(dm.density * 116);
        initHeader();
        costEffectives = new ArrayList<>();
        adapter = new PhotoTopAdapter(costEffectives);
        adapter.setFooterView(footerView);
        manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.getRefreshableView()
                .setLayoutManager(manager);
        recyclerView.getRefreshableView()
                .setBackgroundColor(getResources().getColor(R.color.colorWhite));
        recyclerView.setOnRefreshListener(this);
        mCity = Session.getInstance()
                .getMyCity(this);
        onRefresh(recyclerView);
    }

    private void initHeader() {
        headerView = getLayoutInflater().inflate(R.layout.image_item, null);
        headerImageView = (ImageView) headerView.findViewById(R.id.image);
        headerImageView.getLayoutParams().height = headerViewHeight;
        headerImageView.getLayoutParams().width = headerViewWidth;
        footerView = getLayoutInflater().inflate(R.layout.hlj_foot_no_more___cm, null);
        footerView.findViewById(R.id.no_more_hint)
                .setVisibility(View.VISIBLE);
    }

    private void setHeader(PosterData posterData) {
        List<Poster> posters = PosterUtil.getPosterList(
                posterData.getFloors(),
                Constants.POST_SITES.COST_EFFECTIVE_TOP_BANNER,
                false);
        if (posters != null && !posters.isEmpty()) {
            Poster poster = posters.get(0);
            if (poster != null && poster.getId() > 0) {
                setPosterViewValue(headerView, headerImageView, poster, headerViewWidth);
                adapter.setHeaderView(headerView);
            }
        }
    }

    private void setPosterViewValue(
            View posterView, ImageView posterImageView, final Poster poster, int width) {
        if (poster != null) {
            if (poster.getId() > 0) {
                posterView.setVisibility(View.VISIBLE);
                posterView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BannerUtil.bannerAction(WeddingPhotoTopActivity.this,
                                poster,
                                mCity,
                                false,
                                null);
                    }
                });
            } else {
                posterView.setVisibility(View.INVISIBLE);
                posterView.setOnClickListener(null);
            }
            String url = JSONUtil.getImagePathWithoutFormat(poster.getPath(), width);
            if (!JSONUtil.isEmpty(url)) {
                if (!url.equals(posterImageView.getTag())) {
                    Glide.with(this)
                            .load(url)
                            .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                            .into(posterImageView);

                }
            } else {
                Glide.with(this)
                        .clear(posterImageView);
            }
        } else {
            posterView.setVisibility(View.GONE);
        }
    }

    private void setList(List<CostEffective> costEffective) {
        if (costEffective != null) {
            this.costEffectives.clear();
            this.costEffectives.addAll(costEffective);
            if (recyclerView.getRefreshableView()
                    .getAdapter() == null) {
                recyclerView.getRefreshableView()
                        .setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onFinish() {
        if (refreshSubscriber != null && !refreshSubscriber.isUnsubscribed()) {
            refreshSubscriber.unsubscribe();
        }
        super.onFinish();
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        if (refreshSubscriber == null || refreshSubscriber.isUnsubscribed()) {
            refreshSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setContentView(recyclerView.getRefreshableView())
                    .setPullToRefreshBase(recyclerView)
                    .setProgressBar(refreshView.isRefreshing() ? null : progressBar)
                    .setEmptyView(emptyView)
                    .setOnNextListener(new SubscriberOnNextListener<ResultZip>() {
                        @Override
                        public void onNext(ResultZip resultZip) {
                            if (resultZip != null) {
                                setHeader(resultZip.posterData);
                                setList(resultZip.costEffectives);
                            }
                        }
                    })
                    .build();
        }
        Observable<List<CostEffective>> costEffectiveObservable = TopBrandApi.getCostEffective();
        Observable<PosterData> bannerObservable = CommonApi.getBanner(this,
                HljCommon.BLOCK_ID.COST_EFFECTIVE_TOP_BANNER,
                mCity.getId());
        Observable<ResultZip> observable = Observable.zip(bannerObservable,
                costEffectiveObservable,
                new Func2<PosterData, List<CostEffective>, ResultZip>() {
                    @Override
                    public ResultZip call(
                            PosterData posterData, List<CostEffective> costEffectives) {
                        ResultZip zip = new ResultZip();
                        zip.posterData = posterData;
                        zip.costEffectives = costEffectives;
                        return zip;
                    }
                });
        observable.
                subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(refreshSubscriber);
    }

    class PhotoTopAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private final int HEADER = 1;
        private final int ITEM = 2;
        private final int FOOTER = 3;
        private View headerView;
        private View footerView;
        private List<CostEffective> costEffectives;

        public PhotoTopAdapter(List<CostEffective> costEffectives) {
            this.costEffectives = costEffectives;
        }

        public void setHeaderView(View headerView) {
            this.headerView = headerView;
        }

        public void setFooterView(View footerView) {
            this.footerView = footerView;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(
                ViewGroup parent, int viewType) {
            switch (viewType) {
                case FOOTER:
                    return new ExtraViewHolder(footerView);
                case HEADER:
                    return new ExtraViewHolder(headerView);
                case ITEM:
                    View itemView = getLayoutInflater().inflate(R.layout.wedding_photo_top_item,
                            parent,
                            false);
                    return new ViewHolder(itemView);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(
                RecyclerView.ViewHolder h, int position) {
            int type = getItemViewType(position);
            if (type == ITEM) {
                if (headerView != null) {
                    position = position - 1;
                }
                ViewHolder holder = (ViewHolder) h;
                CostEffective costEffective = costEffectives.get(position);
                setWorkView(holder, costEffective);
            }
        }

        private void setWorkView(ViewHolder holder, CostEffective costEffective) {
            if (costEffective == null || costEffective.getId() == 0) {
                return;
            }
            final Work work = costEffective.getEntity();
            if (work == null || work.getId() == 0) {
                return;
            }

            String coverPath = JSONUtil.getImagePath(work.getCoverPath(), imgCoverSize);
            if (!JSONUtil.isEmpty(coverPath)) {
                Glide.with(WeddingPhotoTopActivity.this)
                        .load(coverPath)
                        .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                        .into(holder.imgCover);
            } else {
                Glide.with(WeddingPhotoTopActivity.this)
                        .clear(holder.imgCover);
            }
            holder.imgInstallment.setVisibility(View.GONE);
            holder.tvTitle.setText(work.getTitle());

            if (work.getMarketPrice() > 0) {
                holder.priceGLayout.setVisibility(View.VISIBLE);
                holder.tvPriceG.setText(Util.formatDouble2String(work.getMarketPrice()));
            } else {
                holder.priceGLayout.setVisibility(View.GONE);
            }
            if (work.getCommodityType() == 0) {
                holder.tvPrice.setVisibility(View.VISIBLE);
                holder.tvPrice.setText(Util.formatDouble2String(work.getShowPrice()));
            } else {
                holder.tvPrice.setVisibility(View.INVISIBLE);
            }
            holder.ratingView.setRating(costEffective.getScore());
            if (!JSONUtil.isEmpty(costEffective.getRemark())) {
                holder.tvReason.setText(getString(R.string.label_top_reason,
                        costEffective.getRemark()));
                holder.liReason.setVisibility(View.VISIBLE);
            } else {
                holder.liReason.setVisibility(View.GONE);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(WeddingPhotoTopActivity.this, WorkActivity.class);
                    intent.putExtra("id", work.getId());
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                }
            });
        }

        @Override
        public int getItemCount() {
            return costEffectives.size() + (headerView == null ? 0 : 1) + (footerView == null ? 0
                    : 1);
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0 && headerView != null) {
                return HEADER;
            } else if ((position == getItemCount() - 1) && footerView != null) {
                return FOOTER;
            } else {
                return ITEM;
            }
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.img_cover)
        ImageView imgCover;
        @BindView(R.id.img_installment)
        ImageView imgInstallment;
        @BindView(R.id.img_custom)
        ImageView imgCustom;
        @BindView(R.id.img_cover_layout)
        RelativeLayout imgCoverLayout;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_price)
        TextView tvPrice;
        @BindView(R.id.price_g_layout)
        LinearLayout priceGLayout;
        @BindView(R.id.tv_price_g)
        TextView tvPriceG;
        @BindView(R.id.tv_gift)
        TextView tvGift;
        @BindView(R.id.rating_view)
        RatingView ratingView;
        @BindView(R.id.tv_reason)
        TextView tvReason;
        @BindView(R.id.line_layout)
        View lineLayout;
        @BindView(R.id.li_reason)
        LinearLayout liReason;
        @BindView(R.id.li_ratting)
        LinearLayout liRatting;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            tvPriceG.getPaint()
                    .setAntiAlias(true);
            tvPriceG.getPaint()
                    .setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }

    class ExtraViewHolder extends RecyclerView.ViewHolder {
        View itemView;

        public ExtraViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
        }
    }

    class ResultZip extends HljHttpResultZip {
        @HljRZField
        PosterData posterData;
        @HljRZField
        List<CostEffective> costEffectives;
    }
}
