package me.suncloud.marrymemo.view.experience;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.event.EventInfo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.makeramen.rounded.RoundedImageView;

import org.joda.time.DateTimeFieldType;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.experience.ExperienceApi;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.view.event.EventDetailActivity;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * 全部活动
 * Created by jinxin on 2016/10/27.
 */
@Deprecated
// TODO: 2018/3/26 wangtao 已被替换
public class ExperienceShopEventListActivity extends HljBaseActivity implements PullToRefreshBase
        .OnRefreshListener, OnItemClickListener<EventInfo> {
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    private View footerView;
    private LinearLayoutManager manager;
    private EventAdapter eventAdapter;
    private HljHttpSubscriber refreshSubscriber;
    private HljHttpSubscriber pageSubscriber;
    private View endView;
    private View loadView;
    private List<EventInfo> events;
    private long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        id = getIntent().getLongExtra("id", 0);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hlj_common_fragment_ptr_recycler_view___cm);
        ButterKnife.bind(this);
        events = new ArrayList<>();
        footerView = getLayoutInflater().inflate(R.layout.hlj_foot_no_more___cm, null);
        endView = footerView.findViewById(com.hunliji.hljquestionanswer.R.id.no_more_hint);
        loadView = footerView.findViewById(com.hunliji.hljquestionanswer.R.id.loading);
        manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        eventAdapter = new EventAdapter();
        recyclerView.getRefreshableView()
                .setLayoutManager(manager);
        recyclerView.getRefreshableView()
                .setAdapter(eventAdapter);
        recyclerView.setOnRefreshListener(this);
        onRefresh(recyclerView);
    }

    @Override
    protected void onFinish() {
        if (refreshSubscriber != null && !refreshSubscriber.isUnsubscribed()) {
            refreshSubscriber.unsubscribe();
        }
        if (pageSubscriber != null && !pageSubscriber.isUnsubscribed()) {
            pageSubscriber.unsubscribe();
        }
        super.onFinish();
    }

    private void initPagination(int pageCount) {
        if (pageSubscriber != null && !pageSubscriber.isUnsubscribed()) {
            pageSubscriber.unsubscribe();
        }
        Observable<HljHttpData<List<EventInfo>>> observable = PaginationTool.buildPagingObservable(
                recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpData<List<EventInfo>>>() {
                    @Override
                    public Observable<HljHttpData<List<EventInfo>>> onNextPage(int page) {
                        return ExperienceApi.getEventList(id, page);
                    }
                })
                .setEndView(endView)
                .setLoadView(loadView)
                .build()
                .getPagingObservable();

        pageSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<EventInfo>>>() {
                    @Override
                    public void onNext(
                            HljHttpData<List<EventInfo>> listHljHttpData) {
                        events.addAll(listHljHttpData.getData());
                        eventAdapter.notifyDataSetChanged();
                    }
                })
                .build();

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSubscriber);
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        if (refreshSubscriber == null || refreshSubscriber.isUnsubscribed()) {
            refreshSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setProgressBar(refreshView.isRefreshing() ? null : progressBar)
                    .setPullToRefreshBase(refreshView)
                    .setContentView(recyclerView)
                    .setEmptyView(emptyView)
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<EventInfo>>>() {
                        @Override
                        public void onNext(HljHttpData<List<EventInfo>> experienceEvents) {
                            events.clear();
                            events.addAll(experienceEvents.getData());
                            eventAdapter.notifyDataSetChanged();
                            initPagination(experienceEvents.getPageCount());
                        }
                    })
                    .build();
        }
        Observable<HljHttpData<List<EventInfo>>> observable = ExperienceApi.getEventList(id, 1);
        observable.subscribe(refreshSubscriber);
    }

    @Override
    public void onItemClick(int position, EventInfo eventInfo) {
        if (eventInfo.getId() > 0) {
            Intent intent = new Intent(this, EventDetailActivity.class);
            intent.putExtra("id", eventInfo.getId());
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

    class EventAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private int FOOTER = 2;
        private int ITEM = 3;

        public EventAdapter() {
            events = new ArrayList<>();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(
                ViewGroup parent, int viewType) {
            if (viewType != FOOTER) {
                View itemView = getLayoutInflater().inflate(R.layout.small_shop_event_item,
                        recyclerView,
                        false);
                SmallEventViewHolder holder = new SmallEventViewHolder(itemView);
                return holder;
            } else {
                return new ExtraBaseViewHolder(footerView);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder h, int position) {
            int viewType = getItemViewType(position);
            if (viewType == ITEM) {
                SmallEventViewHolder holder = (SmallEventViewHolder) h;
                final EventInfo info = events.get(position);
                holder.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(int position, Object object) {
                        Intent intent = new Intent(ExperienceShopEventListActivity.this,
                                EventDetailActivity.class);
                        intent.putExtra("id", info.getId());
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    }
                });
                holder.setOnItemClickListener(ExperienceShopEventListActivity.this);
                holder.setViewData(ExperienceShopEventListActivity.this,
                        info,
                        events.size(),
                        position);
            }
        }

        @Override
        public int getItemCount() {
            return events.isEmpty() ? 0 : events.size() + 1;
        }

        @Override
        public int getItemViewType(int position) {
            int type;
            if (position != getItemCount() - 1) {
                type = ITEM;
            } else {
                type = FOOTER;
            }
            return type;
        }
    }

    class SmallEventViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.img_cover)
        RoundedImageView imgCover;
        @BindView(R.id.tv_watch_count)
        TextView tvWatchCount;
        @BindView(R.id.cover_layout)
        RelativeLayout coverLayout;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_des)
        TextView tvDes;
        @BindView(R.id.tv_time)
        TextView tvTime;
        @BindView(R.id.line_layout)
        View lineLayout;
        View itemView;
        @BindView(R.id.layout_count)
        LinearLayout layoutCount;
        @BindView(R.id.img_event_end)
        ImageView imgEventEnd;

        private int imageWidth;
        private OnItemClickListener onItemClickListener;
        private int padding;

        public SmallEventViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            ButterKnife.bind(this, itemView);
            this.imageWidth = CommonUtil.dp2px(itemView.getContext(), 118);
            padding = CommonUtil.dp2px(itemView.getContext(), 16);
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }


        public void setViewData(
                Context context, final EventInfo eventInfo, int size, final int position) {
            if (eventInfo == null || eventInfo.getId() == 0) {
                itemView.setVisibility(GONE);
            } else {
                itemView.setVisibility(VISIBLE);
            }
            itemView.setPadding(padding,
                    itemView.getPaddingTop(),
                    padding,
                    itemView.getPaddingBottom());
            lineLayout.setVisibility(position < size - 1 ? VISIBLE : GONE);
            imgCover.setColorFilter(Color.parseColor("#4c000000"));
            Glide.with(context)
                    .load(ImageUtil.getImagePath(eventInfo.getListImg(), imageWidth))
                    .apply(new RequestOptions().dontAnimate()
                            .error(com.hunliji.hljcommonviewlibrary.R.mipmap.icon_empty_image)
                            .placeholder(com.hunliji.hljcommonviewlibrary.R.mipmap
                                    .icon_empty_image))
                    .into(imgCover);
            tvTitle.setText(eventInfo.getTitle());
            tvDes.setText(eventInfo.getSummary());
            tvWatchCount.setText(String.valueOf(eventInfo.getWatchCount()));

            String time = null;
            if (eventInfo.getPublishTime() != null && eventInfo.getSignUpEndTime() != null) {
                if (eventInfo.isSignUpEnd()) {
                    //报名已经截止
                    tvTitle.setTextColor(getResources().getColor(R.color.colorGray));
                    tvTime.setTextColor(getResources().getColor(R.color.colorGray));
                    time = getString(R.string.label_experience_store_time,
                            eventInfo.getSignUpEndTime()
                                    .toString("MM月dd日"));
                    layoutCount.setVisibility(GONE);
                    imgEventEnd.setVisibility(VISIBLE);
                } else {
                    layoutCount.setVisibility(VISIBLE);
                    imgEventEnd.setVisibility(GONE);
                    tvTitle.setTextColor(getResources().getColor(R.color.colorBlack2));
                    tvTime.setTextColor(getResources().getColor(R.color.colorBlack3));
                    if (eventInfo.getPublishTime()
                            .get(DateTimeFieldType.dayOfYear()) == eventInfo.getSignUpEndTime()
                            .get(DateTimeFieldType.dayOfYear())) {
                        time = eventInfo.getPublishTime()
                                .toString("MM月dd日 HH:mm") + eventInfo.getSignUpEndTime()
                                .toString("-HH:mm");
                    } else {
                        time = eventInfo.getPublishTime()
                                .toString("MM月dd日") + eventInfo.getSignUpEndTime()
                                .toString("-MM月dd日");
                    }
                    time = getString(R.string.label_experience_store_time, time);
                }
                tvTime.setText(time);
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(position, eventInfo);
                    }
                }
            });
        }
    }
}
