package me.suncloud.marrymemo.fragment.community;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
import me.suncloud.marrymemo.adpter.RichThreadRecyclerAdapter;
import me.suncloud.marrymemo.api.community.CommunityApi;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by mo_yu on 2016/9/26.精编话题列表
 */

public class RichThreadListFragment extends RefreshFragment implements
        PullToRefreshVerticalRecyclerView.OnRefreshListener {

    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    Unbinder unbinder;

    private View footView;
    private View endView;
    private View loadView;

    private ArrayList<CommunityThread> richCommunityThreadList;
    private LinearLayoutManager layoutManager;
    private RichThreadRecyclerAdapter adapter;

    private HljHttpSubscriber pageSubscriber;//分页
    private HljHttpSubscriber refreshSubscriber;//初始加载

    public static RichThreadListFragment newInstance() {
        Bundle args = new Bundle();
        RichThreadListFragment fragment = new RichThreadListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initValue();
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
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initLoad();
    }

    private void initValue() {
        richCommunityThreadList = new ArrayList<>();
    }

    private void initView() {
        footView = getActivity().getLayoutInflater()
                .inflate(R.layout.hlj_foot_no_more___cm, null);
        endView = footView.findViewById(R.id.no_more_hint);
        loadView = footView.findViewById(R.id.loading);

        adapter = new RichThreadRecyclerAdapter(getActivity(), richCommunityThreadList);
        adapter.setFooterView(footView);
        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.getRefreshableView()
                .setLayoutManager(layoutManager);
        recyclerView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        recyclerView.setOnRefreshListener(this);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
    }

    private void initLoad() {
        onRefresh(recyclerView);
    }


    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        if (refreshSubscriber == null || refreshSubscriber.isUnsubscribed()) {
            refreshSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                    .setPullToRefreshBase(refreshView)
                    .setEmptyView(emptyView)
                    .setContentView(refreshView)
                    .setProgressBar(refreshView.isRefreshing() ? null : progressBar)
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<CommunityThread>>>() {
                        @Override
                        public void onNext(
                                HljHttpData<List<CommunityThread>> data) {
                            initPageLoad(data.getPageCount());
                            richCommunityThreadList.clear();
                            richCommunityThreadList.addAll(data.getData());
                            adapter.notifyDataSetChanged();
                        }
                    })
                    .build();

            CommunityApi.getRichThreadListObb(1, 20)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(refreshSubscriber);

        }
    }

    private void initPageLoad(int pageCount) {
        if (pageSubscriber != null && !pageSubscriber.isUnsubscribed()) {
            pageSubscriber.unsubscribe();
        }
        Observable<HljHttpData<List<CommunityThread>>> pageObservable = PaginationTool
                .buildPagingObservable(
                recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpData<List<CommunityThread>>>() {
                    @Override
                    public Observable<HljHttpData<List<CommunityThread>>> onNextPage(
                            int page) {
                        Log.d("pagination tool", "on load: " + page);
                        return CommunityApi.getRichThreadListObb(page, 20);
                    }
                })
                .setEndView(endView)
                .setLoadView(loadView)
                .build()
                .getPagingObservable()
                .observeOn(AndroidSchedulers.mainThread());

        pageSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<CommunityThread>>>() {
                    @Override
                    public void onNext(
                            HljHttpData<List<CommunityThread>> data) {
                        richCommunityThreadList.addAll(data.getData());
                        adapter.notifyDataSetChanged();
                    }
                })
                .build();

        pageObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSubscriber);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CommonUtil.unSubscribeSubs(pageSubscriber, refreshSubscriber);
    }

    @Override
    public void refresh(Object... params) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
