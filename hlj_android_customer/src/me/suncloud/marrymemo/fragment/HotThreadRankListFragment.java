package me.suncloud.marrymemo.fragment;

import android.os.Bundle;
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
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityThread;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.HotThreadRankListRecyclerAdapter;
import me.suncloud.marrymemo.api.communitythreads.CommunityThreadApi;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 热门排行榜
 * Created by chen_bin on 2016/12/20 0020.
 */
public class HotThreadRankListFragment extends RefreshFragment implements PullToRefreshBase
        .OnRefreshListener<RecyclerView> {
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    private View footerView;
    private View endView;
    private View loadView;
    private HotThreadRankListRecyclerAdapter adapter;
    private ArrayList<CommunityThread> threads;
    private String type;
    private Unbinder unbinder;
    private HljHttpSubscriber refreshSubscriber;
    private HljHttpSubscriber pageSubscriber;

    public static HotThreadRankListFragment newInstance(String type) {
        HotThreadRankListFragment fragment = new HotThreadRankListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getString("type");
        }
        threads = new ArrayList<>();
        footerView = View.inflate(getContext(), R.layout.hlj_foot_no_more___cm, null);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        loadView.setVisibility(View.INVISIBLE);
        adapter = new HotThreadRankListRecyclerAdapter(getContext(), threads);
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.hlj_common_fragment_ptr_recycler_view___cm,
                container,
                false);
        unbinder = ButterKnife.bind(this, rootView);
        emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                onRefresh(null);
            }
        });
        ((SimpleItemAnimator) recyclerView.getRefreshableView()
                .getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setMode(PullToRefreshBase.Mode.DISABLED);
        recyclerView.getRefreshableView()
                .setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.addFooterView(footerView);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (threads.isEmpty()) {
            onRefresh(null);
        }
    }

    @Override
    public void onRefresh(final PullToRefreshBase<RecyclerView> refreshView) {
        if (refreshSubscriber == null || refreshSubscriber.isUnsubscribed()) {
            refreshSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<CommunityThread>>>() {
                        @Override
                        public void onNext(HljHttpData<List<CommunityThread>> listHljHttpData) {
                            adapter.setItems(listHljHttpData.getData());
                            initPagination(listHljHttpData.getPageCount());
                        }
                    })
                    .setEmptyView(emptyView)
                    .setProgressBar(progressBar)
                    .setContentView(recyclerView)
                    .build();
            CommunityThreadApi.getHotThreadRanks(type, 1, 10)
                    .subscribe(refreshSubscriber);
        }
    }

    private void initPagination(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSubscriber);
        Observable<HljHttpData<List<CommunityThread>>> observable = PaginationTool
                .buildPagingObservable(
                recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpData<List<CommunityThread>>>() {
                    @Override
                    public Observable<HljHttpData<List<CommunityThread>>> onNextPage(int page) {
                        return CommunityThreadApi.getHotThreadRanks(type, page, 10);
                    }
                })
                .setLoadView(loadView)
                .setEndView(endView)
                .build()
                .getPagingObservable();
        pageSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<CommunityThread>>>() {
                    @Override
                    public void onNext(HljHttpData<List<CommunityThread>> listHljHttpData) {
                        adapter.addItems(listHljHttpData.getData());
                    }
                })
                .build();
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSubscriber);
    }

    @Override
    public void refresh(Object... params) {
        if (recyclerView == null) {
            return;
        }
        type = (String) params[0];
        onRefresh(recyclerView);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
        CommonUtil.unSubscribeSubs(refreshSubscriber, pageSubscriber);
    }
}