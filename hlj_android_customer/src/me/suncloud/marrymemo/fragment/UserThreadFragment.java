package me.suncloud.marrymemo.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import me.suncloud.marrymemo.adpter.community.UserThreadAdapter;
import me.suncloud.marrymemo.api.community.CommunityApi;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.util.Session;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by mo_yu on 2016/5/22.新版话题发布列表
 */
public class UserThreadFragment extends RefreshFragment implements PullToRefreshBase
        .OnRefreshListener<RecyclerView> {
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.btn_scroll_top)
    ImageButton btnScrollTop;
    Unbinder unbinder;
    private long userId;
    private UserThreadAdapter adapter;
    private ArrayList<CommunityThread> threads;
    private View endView;
    private View loadView;

    private HljHttpSubscriber refreshSubscriber;
    private HljHttpSubscriber pageSubscriber;
    private int type;

    public final static int TYPE_PUBLISH_THREAD = 0;
    public final static int TYPE_RETURN_THREAD = 1;
    public final static int TYPE_COLLECT_THREAD = 2;

    public static UserThreadFragment newInstance(int type) {
        Bundle args = new Bundle();
        UserThreadFragment fragment = new UserThreadFragment();
        args.putInt("type", type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        User user = Session.getInstance()
                .getCurrentUser();
        if (user != null) {
            userId = user.getId();
        }
        threads = new ArrayList<>();
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
        if (getArguments() != null) {
            type = getArguments().getInt("type");
        }
        unbinder = ButterKnife.bind(this, rootView);
        recyclerView.setOnRefreshListener(this);
        View footerView = View.inflate(getContext(), R.layout.hlj_foot_no_more___cm, null);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        loadView.setVisibility(View.INVISIBLE);
        adapter = new UserThreadAdapter(getContext(), threads);
        adapter.setFooterView(footerView);
        recyclerView.getRefreshableView()
                .setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (CommonUtil.isCollectionEmpty(threads)) {
            onRefresh(null);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (recyclerView != null) {
            recyclerView.getRefreshableView()
                    .setAdapter(null);
        }
        if (unbinder != null) {
            unbinder.unbind();
        }
        CommonUtil.unSubscribeSubs(refreshSubscriber, pageSubscriber);
    }

    @Override
    public void refresh(Object... params) {}

    @Override
    public void onRefresh(final PullToRefreshBase refreshView) {
        if (refreshSubscriber == null || refreshSubscriber.isUnsubscribed()) {
            refreshSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<CommunityThread>>>() {
                        @Override
                        public void onNext(HljHttpData<List<CommunityThread>> listHljHttpData) {
                            threads.clear();
                            threads.addAll(listHljHttpData.getData());
                            adapter.notifyDataSetChanged();
                            initPagination(listHljHttpData.getPageCount());
                            if (onTabTextChangeListener != null) {
                                onTabTextChangeListener.onTabTextChange(listHljHttpData
                                        .getTotalCount());
                            }
                        }
                    })
                    .setEmptyView(emptyView)
                    .setContentView(recyclerView)
                    .setPullToRefreshBase(recyclerView)
                    .build();
            if (type == TYPE_PUBLISH_THREAD)
                CommunityApi.getPublishThreadsObb(userId, 1)
                        .subscribe(refreshSubscriber);
            else {
                CommunityApi.getCollectThreadsObb(userId, 1)
                        .subscribe(refreshSubscriber);
            }
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
                        if (type == 0) {
                            return CommunityApi.getPublishThreadsObb(userId, page);
                        } else {
                            return CommunityApi.getCollectThreadsObb(userId, page);
                        }
                    }
                })
                .setEndView(endView)
                .setLoadView(loadView)
                .build()
                .getPagingObservable();
        pageSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<CommunityThread>>>() {
                    @Override
                    public void onNext(HljHttpData<List<CommunityThread>> listHljHttpData) {
                        threads.addAll(listHljHttpData.getData());
                        adapter.notifyDataSetChanged();
                    }
                })
                .build();
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSubscriber);
    }

}
