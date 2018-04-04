package me.suncloud.marrymemo.fragment.community;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityThread;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.fragments.ScrollAbleFragment;
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
import me.suncloud.marrymemo.adpter.community.CommunityThreadListAdapter;
import me.suncloud.marrymemo.api.community.CommunityApi;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 新娘说 帖子列表
 * Created by jinxin on 2018/3/19 0019.
 */

public class CommunityThreadListFragment extends ScrollAbleFragment implements PullToRefreshBase
        .OnRefreshListener {

    public static final String ARG_ID = "id";

    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.btn_scroll_top)
    ImageButton btnScrollTop;
    Unbinder unbinder;

    private List<CommunityThread> threadList;
    private CommunityThreadListAdapter communityThreadListAdapter;
    private View footerView;
    private View endView;
    private View loadView;
    private HljHttpSubscriber pageSub;
    private HljHttpSubscriber loadSub;
    private long id;

    public static CommunityThreadListFragment newInstance(long id) {
        CommunityThreadListFragment fragment = new CommunityThreadListFragment();
        Bundle data = new Bundle();
        data.putLong(ARG_ID, id);
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initValues();
    }

    private void initValues() {
        if (getArguments() != null) {
            id = getArguments().getLong(ARG_ID, 0L);
        }
        footerView = getLayoutInflater().inflate(R.layout.hlj_foot_no_more___cm, null, false);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        threadList = new ArrayList<>();
        communityThreadListAdapter = new CommunityThreadListAdapter(getContext());
        communityThreadListAdapter.setFooterView(footerView);
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
        initWidget();
        return rootView;
    }

    private void initWidget() {
        recyclerView.setOnRefreshListener(this);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.getRefreshableView()
                .setLayoutManager(manager);
        recyclerView.getRefreshableView()
                .setAdapter(communityThreadListAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initLoad();
    }

    private void initLoad() {
        onRefresh(null);
    }

    private void setCommunityThreadList(HljHttpData<List<CommunityThread>> listHljHttpData) {
        if (listHljHttpData == null || CommonUtil.isCollectionEmpty(listHljHttpData.getData())) {
            return;
        }
        threadList.clear();
        threadList.addAll(listHljHttpData.getData());
        communityThreadListAdapter.setThreadList(threadList);
        initPagination(listHljHttpData.getPageCount());
    }

    private void initPagination(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSub);
        Observable<HljHttpData<List<CommunityThread>>> pageObservable = PaginationTool
                .buildPagingObservable(
                recyclerView.getRefreshableView(),
                pageCount, new PagingListener<HljHttpData<List<CommunityThread>>>() {
                    @Override
                    public Observable<HljHttpData<List<CommunityThread>>> onNextPage(int page) {
                        return CommunityApi.getCommunityEventThreadList(id, page);
                    }
                })
                .setLoadView(loadView)
                .setEndView(endView)
                .build()
                .getPagingObservable()
                .observeOn(AndroidSchedulers.mainThread());
        pageSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<CommunityThread>>>() {
                    @Override
                    public void onNext(HljHttpData<List<CommunityThread>> listHljHttpData) {
                        if (listHljHttpData != null && !CommonUtil.isCollectionEmpty
                                (listHljHttpData.getData())) {
                            communityThreadListAdapter.addThreadList(listHljHttpData.getData());
                        }
                    }
                })
                .build();
        pageObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSub);
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        CommonUtil.unSubscribeSubs(loadSub);
        loadSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setEmptyView(emptyView)
                .setProgressBar(refreshView == null ? progressBar : null)
                .setContentView(recyclerView)
                .setPullToRefreshBase(recyclerView)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<CommunityThread
                        >>>() {

                    @Override
                    public void onNext(HljHttpData<List<CommunityThread>> listHljHttpData) {
                        setCommunityThreadList(listHljHttpData);
                    }
                })
                .build();
        CommunityApi.getCommunityEventThreadList(id, 1)
                .subscribe(loadSub);
    }

    @Override
    public void refresh(Object... params) {
        if (params != null && params.length > 0) {
            id = (long) params[0];
            initLoad();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        CommonUtil.unSubscribeSubs(pageSub, loadSub);
    }


    @Override
    public View getScrollableView() {
        return recyclerView;
    }
}
