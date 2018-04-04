package me.suncloud.marrymemo.view.event;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.widget.ProgressBar;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.models.event.EventInfo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.BigEventViewHolder;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.event.BigEventRecyclerAdapter;
import me.suncloud.marrymemo.api.event.EventApi;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 热门活动列表
 * Created by chen_bin on 2016/8/10 0010.
 */
public class EventListActivity extends HljBaseActivity implements PullToRefreshBase
        .OnRefreshListener<RecyclerView> {
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    private View endView;
    private View loadView;
    private LinearLayoutManager layoutManager;
    private BigEventRecyclerAdapter adapter;
    private Handler handler;
    private HljHttpSubscriber refreshSub;
    private HljHttpSubscriber pageSub;

    @Override
    public String pageTrackTagName() {
        return "热门活动";
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hlj_common_fragment_ptr_recycler_view___cm);
        ButterKnife.bind(this);
        handler = new Handler();
        emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                onRefresh(null);
            }
        });
        ((SimpleItemAnimator) recyclerView.getRefreshableView()
                .getItemAnimator()).setSupportsChangeAnimations(false);
        View footerView = View.inflate(this, R.layout.hlj_foot_no_more___cm, null);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        loadView.setVisibility(View.INVISIBLE);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.getRefreshableView()
                .setLayoutManager(layoutManager);
        recyclerView.setOnRefreshListener(this);
        adapter = new BigEventRecyclerAdapter(this);
        adapter.setFooterView(footerView);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
        onRefresh(null);
        initTracker();
    }

    private void initTracker() {
        HljVTTagger.tagViewParentName(recyclerView, "activity_list");
    }

    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        if (refreshSub == null || refreshSub.isUnsubscribed()) {
            refreshSub = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<EventInfo>>>
                            () {
                        @Override
                        public void onNext(HljHttpData<List<EventInfo>> listHljHttpData) {
                            adapter.setEvents(listHljHttpData.getData());
                            handler.removeCallbacks(runnable);
                            handler.post(runnable);
                            initPagination(listHljHttpData.getPageCount());
                        }
                    })
                    .setProgressBar(recyclerView.isRefreshing() ? null : progressBar)
                    .setEmptyView(emptyView)
                    .setPullToRefreshBase(recyclerView)
                    .setContentView(recyclerView)
                    .build();
            EventApi.getEventListObb(1, 10)
                    .subscribe(refreshSub);
        }
    }

    private void initPagination(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSub);
        Observable<HljHttpData<List<EventInfo>>> observable = PaginationTool.buildPagingObservable(
                recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpData<List<EventInfo>>>() {
                    @Override
                    public Observable<HljHttpData<List<EventInfo>>> onNextPage(int page) {
                        return EventApi.getEventListObb(page, 10);
                    }
                })
                .setLoadView(loadView)
                .setEndView(endView)
                .build()
                .getPagingObservable();
        pageSub = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<EventInfo>>>() {
                    @Override
                    public void onNext(HljHttpData<List<EventInfo>> listHljHttpData) {
                        adapter.addEvents(listHljHttpData.getData());
                    }
                })
                .build();
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSub);
    }

    private Runnable runnable = new Runnable() {
        public void run() {
            if (isFinishing()) {
                return;
            }
            if (CommonUtil.isCollectionEmpty(adapter.getEvents())) {
                handler.removeCallbacks(this);
                return;
            }
            int first = layoutManager.findFirstVisibleItemPosition();
            int end = layoutManager.findLastVisibleItemPosition();
            for (int i = first; i <= end; i++) {
                RecyclerView.ViewHolder holder = recyclerView.getRefreshableView()
                        .findViewHolderForAdapterPosition(i);
                if (holder != null && holder instanceof BigEventViewHolder) {
                    BigEventViewHolder eventViewHolder = (BigEventViewHolder) holder;
                    eventViewHolder.showTimeDown(EventListActivity.this, eventViewHolder.getItem());
                }
            }
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        handler.post(runnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        handler.removeCallbacks(runnable);
        CommonUtil.unSubscribeSubs(refreshSub, pageSub);
    }
}