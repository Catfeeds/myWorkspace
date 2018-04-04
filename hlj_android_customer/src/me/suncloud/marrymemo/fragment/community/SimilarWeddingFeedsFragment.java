package me.suncloud.marrymemo.fragment.community;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.views.fragments.ScrollAbleFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljcommonviewlibrary.models.CommunityFeed;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.community.SimilarWeddingFeedsAdapter;
import me.suncloud.marrymemo.api.community.CommunityApi;
import me.suncloud.marrymemo.view.community.SimilarWeddingActivity;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 同婚期列表
 * Created by chen_bin on 2018/3/15 0015.
 */
public class SimilarWeddingFeedsFragment extends ScrollAbleFragment implements PullToRefreshBase
        .OnRefreshListener<RecyclerView> {

    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private View endView;
    private View loadView;

    private SimilarWeddingFeedsAdapter adapter;

    private boolean isNeedRefresh;
    private int tab;

    private Unbinder unbinder;

    private HljHttpSubscriber refreshSub;
    private HljHttpSubscriber pageSub;

    public final static int TAB_REFINED = 0; //查漏补缺
    public final static int TAB_NEW = 1; //讨论

    public final static String ARG_TAB = "tab";

    public static SimilarWeddingFeedsFragment newInstance(int tab) {
        SimilarWeddingFeedsFragment fragment = new SimilarWeddingFeedsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_TAB, tab);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tab = getArguments().getInt(ARG_TAB, TAB_NEW);
        }
        View footerView = View.inflate(getContext(), R.layout.hlj_foot_no_more___cm, null);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        loadView.setVisibility(View.INVISIBLE);
        adapter = new SimilarWeddingFeedsAdapter(getContext());
        adapter.setFooterView(footerView);
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
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initTracker();
        initViews();
        if (CommonUtil.isCollectionEmpty(adapter.getFeeds())) {
            onRefresh(null);
        }
    }

    private void initTracker() {
        HljVTTagger.tagViewParentName(recyclerView, "similar_wedding_list?tab=" + tab);
    }

    private void initViews() {
        emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                onRefresh(null);
            }
        });
        ((SimpleItemAnimator) recyclerView.getRefreshableView()
                .getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setOnRefreshListener(this);
        recyclerView.getRefreshableView()
                .setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
    }

    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        if (refreshSub == null || refreshSub.isUnsubscribed()) {
            refreshSub = HljHttpSubscriber.buildSubscriber(getContext())
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<CommunityFeed>>>() {
                        @Override
                        public void onNext(HljHttpData<List<CommunityFeed>> listHljHttpData) {
                            recyclerView.getRefreshableView()
                                    .scrollToPosition(0);
                            adapter.setFeeds(listHljHttpData.getData());
                            initPagination(listHljHttpData.getPageCount());
                        }
                    })
                    .setEmptyView(emptyView)
                    .setContentView(recyclerView)
                    .setPullToRefreshBase(recyclerView)
                    .setProgressBar(recyclerView.isRefreshing() ? null : progressBar)
                    .build();
            CommunityApi.getSimilarWeddingFeedsObb(tab == TAB_NEW ? null : 1,
                    1,
                    HljCommon.PER_PAGE)
                    .subscribe(refreshSub);
        }
    }

    private void initPagination(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSub);
        Observable<HljHttpData<List<CommunityFeed>>> observable = PaginationTool
                .buildPagingObservable(
                recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpData<List<CommunityFeed>>>() {
                    @Override
                    public Observable<HljHttpData<List<CommunityFeed>>> onNextPage(int page) {
                        return CommunityApi.getSimilarWeddingFeedsObb(tab == TAB_NEW ? null : 1,
                                page,
                                HljCommon.PER_PAGE);
                    }
                })
                .setEndView(endView)
                .setLoadView(loadView)
                .build()
                .getPagingObservable();
        pageSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<CommunityFeed>>>
                        () {
                    @Override
                    public void onNext(HljHttpData<List<CommunityFeed>> listHljHttpData) {
                        adapter.addFeeds(listHljHttpData.getData());
                    }
                })
                .build();
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSub);
    }

    @Override
    public View getScrollableView() {
        return recyclerView == null ? null : recyclerView.getRefreshableView();
    }

    @Override
    public void refresh(Object... params) {
        onRefresh(null);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isNeedRefresh) {
            isNeedRefresh = false;
            refresh();
        }
    }

    public void setNeedRefresh(boolean needRefresh) {
        isNeedRefresh = needRefresh;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        CommonUtil.unSubscribeSubs(refreshSub, pageSub);
        recyclerView.getRefreshableView()
                .setAdapter(null);
        unbinder.unbind();
    }
}
