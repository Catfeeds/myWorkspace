package com.hunliji.hljnotelibrary.views.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.alibaba.android.arouter.launcher.ARouter;
import com.example.suncloud.hljweblibrary.HljWeb;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.models.search.WorksSearchResult;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.fragments.ScrollAbleFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.BigWorkViewHolder;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;
import com.hunliji.hljnotelibrary.R;
import com.hunliji.hljnotelibrary.R2;
import com.hunliji.hljnotelibrary.api.NoteApi;
import com.hunliji.hljnotelibrary.views.activities.NoteMarkDetailActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 标签详情页的套餐列表
 * Created by jinxin on 2017/7/5 0005.
 */

public class NoteMarkWorkFragment extends ScrollAbleFragment implements PullToRefreshBase
        .OnRefreshListener<RecyclerView> {

    @BindView(R2.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R2.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R2.id.btn_scroll_top)
    ImageButton btnScrollTop;

    private String sort;
    private long tags;
    private Unbinder unbinder;
    private List<Work> workList;
    private WorkAdapter workAdapter;
    private View footerView;
    private View endView;
    private View loadView;
    private HljHttpSubscriber refreshSubscriber;
    private HljHttpSubscriber pageSubscriber;

    public static NoteMarkWorkFragment newInstance(long tags) {
        NoteMarkWorkFragment workFragment = new NoteMarkWorkFragment();
        Bundle arg = new Bundle();
        arg.putLong("tags", tags);
        workFragment.setArguments(arg);
        return workFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tags = getArguments().getLong("tags");
        }
        sort = NoteMarkDetailActivity.SORT_DEFAULT;
        workList = new ArrayList<>();
        footerView = View.inflate(getContext(), R.layout.hlj_foot_no_more___cm, null);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        loadView.setVisibility(View.INVISIBLE);
        workAdapter = new WorkAdapter();
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
        initWidget();
        return rootView;
    }

    private void initWidget() {
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.getRefreshableView()
                .setLayoutManager(manager);
        recyclerView.getRefreshableView()
                .setAdapter(workAdapter);
        recyclerView.setOnRefreshListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onRefresh(null);
    }

    @Override
    public void refresh(Object... params) {
        if (params.length > 0) {
            sort = (String) params[0];
            onRefresh(null);
        }
    }

    @Override
    public View getScrollableView() {
        return recyclerView.getRefreshableView();
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        CommonUtil.unSubscribeSubs(refreshSubscriber, pageSubscriber);
        super.onDestroyView();
    }

    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        if (refreshSubscriber != null && !refreshSubscriber.isUnsubscribed()) {
            return;
        }
        refreshSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                .setProgressBar(refreshView == null ? progressBar : null)
                .setPullToRefreshBase(recyclerView)
                .setContentView(recyclerView)
                .setEmptyView(emptyView)
                .setOnNextListener(new SubscriberOnNextListener<WorksSearchResult>() {
                    @Override
                    public void onNext(WorksSearchResult worksSearchResult) {
                        if (worksSearchResult == null || worksSearchResult.getWorkList() == null) {
                            emptyView.showEmptyView();
                            return;
                        }
                        workList.clear();
                        workList.addAll(worksSearchResult.getWorkList());
                        initPagination(worksSearchResult.getPageCount());
                        if (workList.isEmpty()) {
                            emptyView.showEmptyView();
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            emptyView.hideEmptyView();
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                        recyclerView.getRefreshableView()
                                .scrollToPosition(0);
                        workAdapter.notifyDataSetChanged();
                    }

                })
                .build();

        Observable<WorksSearchResult> obb = NoteApi.getMarkWorkList(tags, sort, 1);
        obb.subscribe(refreshSubscriber);
    }

    private void initPagination(int pageCount) {
        if (pageSubscriber != null && !pageSubscriber.isUnsubscribed()) {
            return;
        }

        Observable<WorksSearchResult> noteObb = PaginationTool.buildPagingObservable(recyclerView
                        .getRefreshableView(),
                pageCount,
                new PagingListener<WorksSearchResult>() {
                    @Override
                    public Observable<WorksSearchResult> onNextPage(int page) {
                        return NoteApi.getMarkWorkList(tags, sort, page);
                    }
                })
                .setLoadView(loadView)
                .setEndView(endView)
                .build()
                .getPagingObservable()
                .observeOn(AndroidSchedulers.mainThread());
        pageSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<WorksSearchResult>() {
                    @Override
                    public void onNext(WorksSearchResult worksSearchResult) {
                        if (worksSearchResult != null && worksSearchResult.getWorkList() != null) {
                            workList.addAll(worksSearchResult.getWorkList());
                        }
                        workAdapter.notifyDataSetChanged();
                    }
                })
                .build();
        noteObb.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSubscriber);
    }

    class WorkAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final int ITEM_FOOTER = 11;
        private final int ITEM_WORK = 12;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case ITEM_FOOTER:
                    ExtraBaseViewHolder footerHolder = new ExtraBaseViewHolder(footerView);
                    return footerHolder;
                case ITEM_WORK:
                    View itemView = LayoutInflater.from(getContext())
                            .inflate(R.layout.big_commom_work_item__cv, parent, false);
                    BigWorkViewHolder holder = new BigWorkViewHolder(itemView);
                    return holder;
                default:
                    break;
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int viewType = getItemViewType(position);
            if (viewType != ITEM_WORK) {
                return;
            }
            BigWorkViewHolder workHolder = (BigWorkViewHolder) holder;
            Work work = workList.get(position);
            workHolder.setView(getContext(), work, position, viewType);
            workHolder.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(int position, Object object) {
                    if (object != null) {
                        Work work = (Work) object;
                        String link = work.getLink();
                        if (TextUtils.isEmpty(link)) {
                            ARouter.getInstance()
                                    .build(RouterPath.IntentPath.Customer.WORK_ACTIVITY)
                                    .withLong("id", work.getId())
                                    .navigation(getContext());
                        } else {
                            HljWeb.startWebView((Activity) getContext(), link);
                        }
                    }
                }
            });
        }

        @Override
        public int getItemViewType(int position) {
            int type;
            if (position == getItemCount() - 1) {
                type = ITEM_FOOTER;
            } else {
                type = ITEM_WORK;
            }
            return type;
        }

        @Override
        public int getItemCount() {
            return workList.size() + 1;
        }
    }
}
