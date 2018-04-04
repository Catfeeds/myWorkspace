package com.hunliji.marrybiz.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.LongSparseArray;
import android.util.SparseLongArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.suncloud.hljweblibrary.HljWeb;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.models.event.EventInfo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.api.leaflets.EventLeafletsApi;
import com.hunliji.marrybiz.model.leaflets.EventSource;
import com.makeramen.rounded.RoundedImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 活动微传单activity
 * Created by jinxin on 2017/5/22 0022.
 */

public class EventLeafletsListActivity extends HljBaseActivity implements PullToRefreshBase
        .OnRefreshListener {

    private static final String EVENT_LEAFLET_NOTICE = "event_leaflet_notice";

    @BindView(R.id.tv_notice)
    TextView tvNotice;
    @BindView(R.id.layout_notice)
    LinearLayout layoutNotice;
    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.layout_header)
    LinearLayout layoutHeader;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.btn_scroll_top)
    ImageButton btnScrollTop;
    @BindView(R.id.btn_clear)
    ImageView btnClear;

    private EventLeafletsAdapter adapter;
    private List<EventSource> eventSources;
    private HljHttpSubscriber refreshSubscriber;
    private HljHttpSubscriber pageSubscriber;
    private View footerView;
    private View endView;
    private View loadView;
    private String searchTitle;
    private LongSparseArray<Boolean> signMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_leaflets);
        ButterKnife.bind(this);
        initWidget();
        onRefresh(recyclerView);
    }

    private void initWidget() {
        signMap = new LongSparseArray<>();
        SharedPreferences sp = this.getSharedPreferences(Constants.PREF_FILE, MODE_PRIVATE);
        boolean showNotice = sp.getBoolean(EVENT_LEAFLET_NOTICE, false);
        if (!showNotice) {
            layoutNotice.setVisibility(View.VISIBLE);
        } else {
            layoutNotice.setVisibility(View.GONE);
        }
        eventSources = new ArrayList<>();
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hideKeyboard(etSearch);
                    searchTitle = etSearch.getText()
                            .toString();
                    onRefresh(recyclerView);
                    return true;
                }
                return false;
            }
        });
        etSearch.addTextChangedListener(new ClearTextWatcher());
        footerView = getLayoutInflater().inflate(R.layout.hlj_foot_no_more___cm, null, false);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        adapter = new EventLeafletsAdapter(this);
        adapter.setFooterView(footerView);
        recyclerView.setOnRefreshListener(this);
        recyclerView.setOnRefreshListener(this);
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
                .setContentView(refreshView)
                .setPullToRefreshBase(recyclerView)
                .setEmptyView(emptyView)
                .setProgressBar(refreshView.isRefreshing() ? null : progressBar)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<EventSource>>>() {
                    @Override
                    public void onNext(HljHttpData<List<EventSource>> listHljHttpData) {
                        List<EventSource> sources = listHljHttpData.getData();
                        signMap.clear();
                        if (sources != null) {
                            for (EventSource s : sources) {
                                EventInfo info = s.getFinderActivity();
                                if (info != null) {
                                    boolean show = info.getSignUpNewAllCount() > 0;
                                    signMap.put(s.getId(), show);
                                }
                            }
                            adapter.setItems(sources);
                        }
                        initPagination(listHljHttpData.getPageCount());
                    }
                })
                .build();
        Observable<HljHttpData<List<EventSource>>> obb = EventLeafletsApi.getEventLeafletsList(
                searchTitle,
                1);
        obb.subscribe(refreshSubscriber);
    }

    private void initPagination(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSubscriber);
        Observable<HljHttpData<List<EventSource>>> pageObservable = PaginationTool
                .buildPagingObservable(
                recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpData<List<EventSource>>>() {
                    @Override
                    public Observable<HljHttpData<List<EventSource>>> onNextPage(int page) {
                        return EventLeafletsApi.getEventLeafletsList(searchTitle, page);
                    }
                })
                .setLoadView(loadView)
                .setEndView(endView)
                .build()
                .getPagingObservable()
                .observeOn(AndroidSchedulers.mainThread());
        pageSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<EventSource>>>() {
                    @Override
                    public void onNext(HljHttpData<List<EventSource>> productSearchResultList) {
                        if (productSearchResultList != null) {
                            List<EventSource> sources = productSearchResultList.getData();
                            if (sources != null) {
                                for (EventSource s : sources) {
                                    EventInfo info = s.getFinderActivity();
                                    if (info != null) {
                                        boolean show = info.getSignUpNewAllCount() > 0;
                                        signMap.put(s.getId(), show);
                                    }
                                }
                            }
                            adapter.addItems(productSearchResultList.getData());
                        }
                    }
                })
                .build();
        pageObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSubscriber);
    }

    @OnClick(R.id.btn_clear)
    void onClear() {
        etSearch.setText("");
    }

    @OnClick(R.id.layout_notice)
    void onNoticeClose() {
        layoutNotice.setVisibility(View.GONE);
        SharedPreferences sp = this.getSharedPreferences(Constants.PREF_FILE, MODE_PRIVATE);
        sp.edit()
                .putBoolean(EVENT_LEAFLET_NOTICE, true)
                .apply();
    }

    @Override
    protected void onFinish() {
        hideKeyboard(etSearch);
        super.onFinish();
        CommonUtil.unSubscribeSubs(refreshSubscriber, pageSubscriber);
    }

    class ClearTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!TextUtils.isEmpty(s)) {
                btnClear.setVisibility(View.VISIBLE);
            } else {
                btnClear.setVisibility(View.GONE);
            }
        }
    }

    public class EventLeafletsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final int ITEM_ITEM = 10;
        private final int ITEM_FOOTER = 11;

        private Context mContext;
        private View footerView;

        public EventLeafletsAdapter(Context mContext) {
            this.mContext = mContext;
        }

        public void setItems(List<EventSource> items) {
            if (!CommonUtil.isCollectionEmpty(items)) {
                eventSources.clear();
                eventSources.addAll(items);
                notifyDataSetChanged();
            }
        }

        public void addItems(List<EventSource> items) {
            if (!CommonUtil.isCollectionEmpty(items)) {
                int start = eventSources.size();
                eventSources.addAll(items);
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
                    return new EventLeafletsViewHolder(itemView);
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
            EventSource eventSource = eventSources.get(position);
            if (eventSource == null || eventSource.getId() <= 0) {
                return;
            }

            EventLeafletsViewHolder holder = (EventLeafletsViewHolder) h;
            holder.layoutUser.setVisibility(View.GONE);
            holder.layoutCondition.setVisibility(View.GONE);
            holder.tvTimeHint.setText("活动名称：");
            holder.tvTime.setText(eventSource.getTitle());

            holder.tvStateHint.setText("报名人数：");
            holder.tvSingUpNewCount.setVisibility(View.GONE);
            if (eventSource.getFinderActivity() != null) {
                EventInfo info = eventSource.getFinderActivity();
                holder.tvState.setText(String.valueOf(info.getSignUpCount() + info
                        .getSignUpOutsideCount()));
                boolean showSignUpCount = signMap.get(eventSource.getId());
                if (showSignUpCount) {
                    holder.tvSingUpNewCount.setVisibility(View.VISIBLE);
                    holder.tvSingUpNewCount.setText(String.valueOf(info.getSignUpNewAllCount()));
                } else {
                    holder.tvSingUpNewCount.setVisibility(View.GONE);
                }
            }

            holder.tvReservation.setText("预览");
            holder.tvReservation.setTextColor(mContext.getResources()
                    .getColor(R.color.colorPrimary));
            holder.tvReservation.setBackgroundResource(R.drawable.sp_r12_stroke_primary);
            holder.tvDelete.setText("报名名单");
            holder.tvDelete.setTextColor(mContext.getResources()
                    .getColor(R.color.colorPrimary));
            holder.tvDelete.setBackgroundResource(R.drawable.sp_r12_stroke_primary);
            OnItemClickListener onItemClickListener = new OnItemClickListener(eventSource,
                    eventSource.getId(),
                    position);
            holder.tvReservation.setOnClickListener(onItemClickListener);
            holder.tvDelete.setOnClickListener(onItemClickListener);
        }

        @Override
        public int getItemCount() {
            return eventSources == null ? 0 : (eventSources.size() + (footerView == null ? 0 : 1));
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

        class OnItemClickListener implements View.OnClickListener {

            private EventSource eventSource;
            private long eventSourceId;
            private int position;

            public OnItemClickListener(EventSource eventSource, long eventSourceId, int position) {
                this.eventSource = eventSource;
                this.eventSourceId = eventSourceId;
                this.position = position;
            }

            @Override
            public void onClick(View v) {
                int id = v.getId();
                Intent intent = null;
                switch (id) {
                    case R.id.tv_reservation:
                        //预览
                        if (!TextUtils.isEmpty(eventSource.getLink())) {
                            HljWeb.startWebView(EventLeafletsListActivity.this,
                                    eventSource.getLink());
                        }
                        break;
                    case R.id.tv_delete:
                        //报名名单
                        boolean show = signMap.get(eventSourceId);
                        if (show) {
                            signMap.put(eventSourceId, !show);
                        }
                        adapter.notifyItemChanged(position);
                        if (eventSource.getFinderActivity() != null) {
                            intent = new Intent(EventLeafletsListActivity.this,
                                    EventLeafletApplyListActivity.class);
                            intent.putExtra("id",
                                    eventSource.getFinderActivity()
                                            .getId());
                        }
                        break;
                    default:
                        break;
                }
                if (intent != null) {
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                }
            }
        }

    }

    public class EventLeafletsViewHolder extends RecyclerView.ViewHolder {

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
        @BindView(R.id.tv_sign_up_new_count)
        TextView tvSingUpNewCount;

        public EventLeafletsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
