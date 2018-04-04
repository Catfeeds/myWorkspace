package com.hunliji.marrybiz.fragment.comment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.models.ServiceComment;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.adapter.comment.CommentManagerAdapter;
import com.hunliji.marrybiz.api.comment.CommentApi;
import com.hunliji.marrybiz.util.Session;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by hua_rong on 2017/4/15.
 * 评价列表--item-fragment
 */

public class CommentItemFragment extends RefreshFragment implements PullToRefreshBase
        .OnRefreshListener<RecyclerView> {

    private static final String ARG_STATUS = "arg_status";
    private static final String ARG_TITLE = "arg_title";
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView pullToRefreshVerticalRecyclerView;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    Unbinder unbinder;
    private long reputation = -1;
    private long merchant_id;
    private HljHttpSubscriber refreshSubscriber;
    private HljHttpSubscriber pageSubscriber;
    private View footerView;
    private TextView endView;
    private View loadView;
    private List<ServiceComment> comments;
    private CommentManagerAdapter adapter;
    private Subscription rxBusSub;

    public static CommentItemFragment newInstance(long status, String title) {

        Bundle args = new Bundle();
        args.putLong(ARG_STATUS, status);
        args.putString(ARG_TITLE, title);
        CommentItemFragment fragment = new CommentItemFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFooter();
        merchant_id = Session.getInstance()
                .getCurrentUser(getContext())
                .getMerchantId();
        comments = new ArrayList<>();
        Bundle bundle = getArguments();
        if (bundle != null) {
            reputation = bundle.getLong(ARG_STATUS);
            registerRxBusEvent();
        }
    }

    @Override
    public void refresh(Object... params) {

    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.hlj_common_fragment_ptr_recycler_view___cm,
                container,
                false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        onNetError();
        if (reputation != -1) {
            onRefresh(pullToRefreshVerticalRecyclerView);
        }
    }

    private void onNetError() {
        emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                onRefresh(pullToRefreshVerticalRecyclerView);
            }
        });
    }

    /**
     * 详情评论或删除成功后，刷一下
     */
    private void registerRxBusEvent() {
        if (rxBusSub == null || rxBusSub.isUnsubscribed()) {
            rxBusSub = RxBus.getDefault()
                    .toObservable(RxEvent.class)
                    .subscribe(new RxBusSubscriber<RxEvent>() {
                        @Override
                        protected void onEvent(RxEvent rxEvent) {
                            switch (rxEvent.getType()) {
                                case COMMENT_DETAIL_REPLY_OR_DELETE_SUCCESS:
                                    onRefresh(pullToRefreshVerticalRecyclerView);
                                    break;
                            }
                        }
                    });
        }
    }

    private void initFooter() {
        footerView = View.inflate(getContext(), R.layout.list_foot_no_more_2, null);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
    }

    private void initView() {
        RecyclerView recyclerView = pullToRefreshVerticalRecyclerView.getRefreshableView();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        pullToRefreshVerticalRecyclerView.setOnRefreshListener(this);
        adapter = new CommentManagerAdapter(getContext(), comments);
        adapter.setFooterView(footerView);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        if (refreshSubscriber == null || refreshSubscriber.isUnsubscribed()) {
            Observable<HljHttpData<List<ServiceComment>>> observable = CommentApi
                    .getMerchantCommentList(
                    1,
                    merchant_id,
                    reputation);
            refreshSubscriber = HljHttpSubscriber.buildSubscriber(getActivity())
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<ServiceComment>>>() {

                        @Override
                        public void onNext(HljHttpData<List<ServiceComment>> hljHttpData) {
                            comments.clear();
                            comments.addAll(hljHttpData.getData());
                            adapter.setComments(comments);
                            adapter.notifyDataSetChanged();
                            initPage(hljHttpData.getPageCount());
                        }
                    })
                    .setProgressBar(refreshView.isRefreshing() ? null : progressBar)
                    .setEmptyView(emptyView)
                    .setContentView(refreshView)
                    .setPullToRefreshBase(refreshView)
                    .build();
            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(refreshSubscriber);
        }
    }

    private void initPage(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSubscriber);
        Observable<HljHttpData<List<ServiceComment>>> pageObservable = PaginationTool
                .buildPagingObservable(
                pullToRefreshVerticalRecyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpData<List<ServiceComment>>>() {
                    @Override
                    public Observable<HljHttpData<List<ServiceComment>>> onNextPage(int page) {
                        return CommentApi.getMerchantCommentList(page, merchant_id, reputation);
                    }
                })
                .setLoadView(loadView)
                .setEndView(endView)
                .build()
                .getPagingObservable()
                .observeOn(AndroidSchedulers.mainThread());
        pageSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<ServiceComment>>>() {

                    @Override
                    public void onNext(HljHttpData<List<ServiceComment>> hljHttpData) {
                        comments.addAll(hljHttpData.getData());
                        adapter.setComments(comments);
                        adapter.notifyDataSetChanged();
                    }
                })
                .build();
        pageObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSubscriber);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
        CommonUtil.unSubscribeSubs(refreshSubscriber, pageSubscriber, rxBusSub);
    }

}
