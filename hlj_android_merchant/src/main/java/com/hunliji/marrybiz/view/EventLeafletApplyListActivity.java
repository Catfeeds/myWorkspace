package com.hunliji.marrybiz.view;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Author;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.api.leaflets.EventLeafletsApi;
import com.hunliji.marrybiz.model.leaflets.EventSignUp;
import com.hunliji.marrybiz.model.leaflets.EventSource;
import com.makeramen.rounded.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 报名名单activity
 * Created by jinxin on 2017/5/22 0022.
 */

public class EventLeafletApplyListActivity extends HljBaseActivity implements PullToRefreshBase
        .OnRefreshListener {

    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.btn_scroll_top)
    ImageButton btnScrollTop;

    private List<EventSource> infoList;
    private HljHttpSubscriber refreshSubscriber;
    private HljHttpSubscriber pageSubscriber;
    private EventLeafletApplyAdapter adapter;
    private View footerView;
    private View endView;
    private View loadView;
    private long activityId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activityId = getIntent().getLongExtra("id", 0);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hlj_common_fragment_ptr_recycler_view___cm);
        ButterKnife.bind(this);
        initWidget();
        onRefresh(recyclerView);
    }

    private void initWidget() {
        infoList = new ArrayList<>();
        footerView = getLayoutInflater().inflate(R.layout.hlj_foot_no_more___cm, null, false);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        recyclerView.setOnRefreshListener(this);
        adapter = new EventLeafletApplyAdapter(this);
        adapter.setFooterView(footerView);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.getRefreshableView()
                .setLayoutManager(manager);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        if (refreshSubscriber != null && !refreshSubscriber.isUnsubscribed()) {
            return;
        }
        refreshSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setContentView(recyclerView)
                .setPullToRefreshBase(recyclerView)
                .setEmptyView(emptyView)
                .setProgressBar(refreshView.isRefreshing() ? null : progressBar)
                .setOnNextListener(new SubscriberOnNextListener<EventSignUp>() {
                    @Override
                    public void onNext(EventSignUp eventSignUp) {
                        int pageCount = 0;
                        if (!CommonUtil.isCollectionEmpty(eventSignUp.getEventSources())) {
                            pageCount = eventSignUp.getPageCount();
                            adapter.setItems(eventSignUp.getEventSources());
                        } else {
                            emptyView.showEmptyView();
                            recyclerView.setVisibility(View.GONE);
                        }
                        initPagination(pageCount);
                    }
                })
                .build();
        Observable<EventSignUp> obb = EventLeafletsApi.getEventLeafletApplyList(activityId, 1);
        obb.subscribe(refreshSubscriber);
    }

    private void initPagination(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSubscriber);
        Observable<EventSignUp> pageObservable = PaginationTool.buildPagingObservable
                (recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<EventSignUp>() {
                    @Override
                    public Observable<EventSignUp> onNextPage(int page) {
                        return EventLeafletsApi.getEventLeafletApplyList(activityId, page);
                    }
                })
                .setLoadView(loadView)
                .setEndView(endView)
                .build()
                .getPagingObservable()
                .observeOn(AndroidSchedulers.mainThread());
        pageSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<EventSignUp>() {
                    @Override
                    public void onNext(EventSignUp eventSignUp) {
                        if (eventSignUp != null) {
                            if (eventSignUp.getEventSources() != null) {
                                adapter.addItems(eventSignUp.getEventSources());
                            }
                        }
                    }
                })
                .build();
        pageObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSubscriber);
    }


    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(refreshSubscriber, pageSubscriber);
    }

    public class EventLeafletApplyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final int ITEM_ITEM = 10;
        private final int ITEM_FOOTER = 11;

        private Context mContext;
        private View footerView;

        public EventLeafletApplyAdapter(Context mContext) {
            this.mContext = mContext;
        }

        public void setItems(List<EventSource> items) {
            if (!CommonUtil.isCollectionEmpty(items)) {
                infoList.clear();
                infoList.addAll(items);
                notifyDataSetChanged();
            }
        }

        public void addItems(List<EventSource> items) {
            if (!CommonUtil.isCollectionEmpty(items)) {
                int start = infoList.size();
                infoList.addAll(items);
                notifyItemRangeInserted(start, items.size());
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView;
            switch (viewType) {
                case ITEM_ITEM:
                    itemView = LayoutInflater.from(mContext)
                            .inflate(R.layout.reservation_manager_item, parent, false);
                    return new EventLeafletApplyViewHolder(itemView);
                case ITEM_FOOTER:
                    return new ExtraBaseViewHolder(footerView);
                default:
                    break;
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder h, int position) {
            int viewType = getItemViewType(position);
            if (viewType != ITEM_ITEM) {
                return;
            }
            final EventSource eventSource = infoList.get(position);
            if (eventSource == null) {
                return;
            }
            EventLeafletApplyViewHolder holder = (EventLeafletApplyViewHolder) h;
            holder.layoutButton.setVisibility(View.GONE);
            holder.layoutCondition.setVisibility(View.GONE);
            Author author = eventSource.getAuthor();
            String name = eventSource.getRealname();
            if (author != null) {
                if (TextUtils.isEmpty(name)) {
                    name = author.getName();
                }
                String avatar = author.getAvatar();
                if (!TextUtils.isEmpty(avatar)) {
                    int width = CommonUtil.dp2px(mContext, 24);
                    Glide.with(mContext)
                            .load(ImagePath.buildPath(avatar)
                                    .width(width)
                                    .height(width)
                                    .cropPath())
                            .into(holder.imgAvatar);
                } else {
                    Glide.with(mContext)
                            .clear(holder.imgAvatar);
                }
            }
            holder.tvName.setText(name);
            if (eventSource.getCreateAt() != null) {
                holder.tvDes.setText(eventSource.getCreateAt()
                        .toString(Constants.DATE_FORMAT_LONG));
            }
            String tel = eventSource.getTel();
            holder.tvTimeHint.setText("电话：");
            holder.tvTime.setText(tel);
            if (!TextUtils.isEmpty(tel) && tel.contains("*")) {
                holder.tvAdvCountHint.setVisibility(View.VISIBLE);
            } else {
                holder.tvAdvCountHint.setVisibility(View.GONE);
            }
            holder.tvStateHint.setText("来源：");
            String wap = "";
            switch (eventSource.getWap()) {
                case 0:
                case 1:
                case 2:
                    wap = "聚客宝";
                    break;
                case 3:
                    wap = "外部平台报名";
                    break;
                case 4:
                    wap = "外部平台报名（微信）";
                    break;
                case 5:
                    wap = "推广";
                    break;
                default:
                    break;
            }
            holder.tvState.setText(wap);
            holder.imgCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String tel = eventSource.getTel();
                    if (!TextUtils.isEmpty(tel) && !tel.contains("*")) {
                        callUp(Uri.parse("tel:" + eventSource.getTel()
                                .trim()));
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return infoList == null ? 0 : (infoList.size() + (footerView == null ? 0 : 1));
        }

        @Override
        public int getItemViewType(int position) {
            int type;
            if (position != getItemCount() - 1) {
                type = ITEM_ITEM;
            } else {
                type = ITEM_FOOTER;
            }
            return type;
        }

        public void setFooterView(View footerView) {
            this.footerView = footerView;
        }
    }

    public class EventLeafletApplyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.divider)
        View divider;
        @BindView(R.id.img_avatar)
        RoundedImageView imgAvatar;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_des)
        TextView tvDes;
        @BindView(R.id.tv_status)
        TextView tvStatus;
        @BindView(R.id.img_call)
        ImageView imgCall;
        @BindView(R.id.layout_user)
        LinearLayout layoutUser;
        @BindView(R.id.tv_time_hint)
        TextView tvTimeHint;
        @BindView(R.id.tv_time)
        TextView tvTime;
        @BindView(R.id.layout_time)
        LinearLayout layoutTime;
        @BindView(R.id.tv_state_hint)
        TextView tvStateHint;
        @BindView(R.id.tv_state)
        TextView tvState;
        @BindView(R.id.layout_state)
        LinearLayout layoutState;
        @BindView(R.id.tv_condition_hint)
        TextView tvConditionHint;
        @BindView(R.id.tv_condition)
        TextView tvCondition;
        @BindView(R.id.layout_condition)
        LinearLayout layoutCondition;
        @BindView(R.id.layout_content)
        LinearLayout layoutContent;
        @BindView(R.id.tv_delete)
        TextView tvDelete;
        @BindView(R.id.tv_reservation)
        TextView tvReservation;
        @BindView(R.id.layout_button)
        LinearLayout layoutButton;
        @BindView(R.id.tv_adv_count_hint)
        TextView tvAdvCountHint;

        public EventLeafletApplyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
