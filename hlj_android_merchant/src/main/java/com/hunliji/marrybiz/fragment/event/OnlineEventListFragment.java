package com.hunliji.marrybiz.fragment.event;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.event.EventInfo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.fragments.ScrollAbleFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.adapter.event.OnlineEventRecyclerAdapter;
import com.hunliji.marrybiz.api.event.EventApi;
import com.hunliji.marrybiz.model.event.EventWallet;
import com.hunliji.marrybiz.view.event.SignUpListActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 已上线的活动列表
 * Created by chen_bin on 2016/9/26 0026.
 */
public class OnlineEventListFragment extends ScrollAbleFragment implements
        OnItemClickListener<EventInfo> {
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    private View footerView;
    private View endView;
    private View loadView;
    private OnlineEventRecyclerAdapter adapter;
    private EventWallet eventWallet;
    private boolean isNeedRefresh; //是否需要重新加载
    private boolean isShowProgressBar = true; //是否显示加载券圈
    private Unbinder unbinder;
    private HljHttpSubscriber initSub;
    private HljHttpSubscriber pageSub;
    private static final String ARG_EVENT_WALLET = "event_wallet";

    public static OnlineEventListFragment newInstance(EventWallet eventWallet) {
        OnlineEventListFragment fragment = new OnlineEventListFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_EVENT_WALLET, eventWallet);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventWallet = getArguments().getParcelable(ARG_EVENT_WALLET);
        }
        footerView = View.inflate(getContext(), R.layout.hlj_foot_no_more___cm, null);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        loadView.setVisibility(View.INVISIBLE);
        adapter = new OnlineEventRecyclerAdapter(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.hlj_common_fragment_ptr_recycler_view___cm,
                container,
                false);
        unbinder = ButterKnife.bind(this, rootView);
        initViews();
        return rootView;
    }

    private void initViews() {
        emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                initLoad();
            }
        });
        ((SimpleItemAnimator) recyclerView.getRefreshableView()
                .getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setMode(PullToRefreshBase.Mode.DISABLED);
        recyclerView.getRefreshableView()
                .setPadding(0, 0, 0, CommonUtil.dp2px(getContext(), 10));
        recyclerView.getRefreshableView()
                .setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.setFooterView(footerView);
        adapter.setOnItemClickListener(this);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initLoad();
    }

    private void initLoad() {
        if (initSub == null || initSub.isUnsubscribed()) {
            initSub = HljHttpSubscriber.buildSubscriber(getContext())
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<EventInfo>>>
                            () {
                        @Override
                        public void onNext(HljHttpData<List<EventInfo>> listHljHttpData) {
                            recyclerView.getRefreshableView()
                                    .scrollToPosition(0);
                            adapter.setEventWallet(eventWallet);
                            adapter.setEvents(listHljHttpData.getData());
                            initPagination(listHljHttpData.getPageCount());
                        }
                    })
                    .setEmptyView(emptyView)
                    .setContentView(recyclerView)
                    .setPullToRefreshBase(recyclerView)
                    .setProgressBar(isShowProgressBar ? progressBar : null)
                    .build();
            isShowProgressBar = true;
            EventApi.getEventsObb(0, 1, 20)
                    .subscribe(initSub);
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
                        return EventApi.getEventsObb(0, page, 20);
                    }
                })
                .setLoadView(loadView)
                .setEndView(endView)
                .build()
                .getPagingObservable();
        pageSub = HljHttpSubscriber.buildSubscriber(getContext())
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

    @Override
    public void onItemClick(int position, EventInfo eventInfo) {
        if (eventInfo != null && eventInfo.getId() > 0) {
            Intent intent = new Intent(getContext(), SignUpListActivity.class);
            intent.putExtra("id", eventInfo.getId());
            getActivity().startActivityForResult(intent, Constants.RequestCode.PAY);
            getActivity().overridePendingTransition(R.anim.slide_in_right,
                    R.anim.activity_anim_default);
        }
    }

    public void setEventWallet(EventWallet eventWallet) {
        this.eventWallet = eventWallet;
    }

    public void setShowProgressBar(boolean showProgressBar) {
        this.isShowProgressBar = showProgressBar;
    }

    public void setNeedRefresh(boolean needRefresh) {
        this.isNeedRefresh = needRefresh;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isNeedRefresh) {
            isNeedRefresh = false;
            refresh();
        }
    }

    @Override
    public View getScrollableView() {
        return recyclerView == null ? null : recyclerView.getRefreshableView();
    }

    @Override
    public void refresh(Object... params) {
        if (recyclerView != null) {
            initLoad();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
        CommonUtil.unSubscribeSubs(initSub, pageSub);
    }
}