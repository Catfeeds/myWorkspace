package com.hunliji.marrybiz.view.work_case;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.adapter.work_case.WorkSortListAdapter;
import com.hunliji.marrybiz.api.work_case.WorkApi;
import com.hunliji.marrybiz.util.work_case.WorkStatusEnum;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 套餐排序列表
 * Created by chen_bin on 2017/2/3 0003.
 */
public class WorkSortListActivity extends HljBaseActivity implements PullToRefreshBase
        .OnRefreshListener<RecyclerView>, OnItemClickListener<Work>, WorkSortListAdapter
        .OnMoveListener, WorkSortListAdapter.OnMoveTopListener {
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    private View endView;
    private View loadView;
    private WorkSortListAdapter adapter;
    private int type;
    private HljHttpSubscriber refreshSub;
    private HljHttpSubscriber pageSub;
    private HljHttpSubscriber exchangeSub;
    private HljHttpSubscriber setTopSub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hlj_common_fragment_ptr_recycler_view___cm);
        ButterKnife.bind(this);
        type = getIntent().getIntExtra("type", 0);
        setTitle(type == 0 ? R.string.label_work_sort : R.string.label_case_sort);
        View headerView = View.inflate(this, R.layout.work_sort_header_item, null);
        TextView tvTitle = headerView.findViewById(R.id.tv_title);
        tvTitle.setText(type == 0 ? R.string.label_work_name : R.string.label_case_name);
        View footerView = View.inflate(this, R.layout.hlj_foot_no_more___cm, null);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        loadView.setVisibility(View.INVISIBLE);
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
                .setLayoutManager(new LinearLayoutManager(this));
        adapter = new WorkSortListAdapter(this);
        adapter.setHeaderView(headerView);
        adapter.setFooterView(footerView);
        adapter.setOnItemClickListener(this);
        adapter.setOnMoveListener(this);
        adapter.setOnMoveTopListener(this);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
        onRefresh(null);
    }

    @Override
    public void onRefresh(final PullToRefreshBase<RecyclerView> refreshView) {
        if (refreshSub == null || refreshSub.isUnsubscribed()) {
            refreshSub = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<Work>>>() {
                        @Override
                        public void onNext(HljHttpData<List<Work>> listHljHttpData) {
                            adapter.setWorks(listHljHttpData.getData());
                            initPagination(listHljHttpData.getPageCount());
                        }
                    })
                    .setEmptyView(emptyView)
                    .setContentView(recyclerView)
                    .setPullToRefreshBase(recyclerView)
                    .setProgressBar(recyclerView.isRefreshing() ? null : progressBar)
                    .build();
            WorkApi.getWorksObb(type, WorkStatusEnum.ON, 1, 20)
                    .subscribe(refreshSub);
        }
    }

    private void initPagination(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSub);
        Observable<HljHttpData<List<Work>>> observable = PaginationTool.buildPagingObservable(
                recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpData<List<Work>>>() {
                    @Override
                    public Observable<HljHttpData<List<Work>>> onNextPage(int page) {
                        return WorkApi.getWorksObb(type, WorkStatusEnum.ON, page, 20);
                    }
                })
                .setLoadView(loadView)
                .setEndView(endView)
                .build()
                .getPagingObservable();
        pageSub = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<Work>>>() {
                    @Override
                    public void onNext(HljHttpData<List<Work>> listHljHttpData) {
                        adapter.addWorks(listHljHttpData.getData());
                    }
                })
                .build();
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSub);
    }

    @Override
    public void onItemClick(int position, Work work) {
        int lastPosition = adapter.getSelectedPosition() + adapter.getHeaderViewCount();
        adapter.setSelectedPosition(adapter.getWorks()
                .indexOf(work));
        adapter.notifyItemChanged(lastPosition);
        adapter.notifyItemChanged(position);
    }

    @Override
    public void onMove(final int direction, final int position, final Work work) {
        if (exchangeSub != null && !exchangeSub.isUnsubscribed()) {
            return;
        }
        //direction=0表示向上。direction=1表示向下，
        final List<Work> works = adapter.getWorks();
        final int toPosition = direction == 0 ? position - 1 : position + 1;
        final int index = position - adapter.getHeaderViewCount();
        final int toIndex = toPosition - adapter.getHeaderViewCount();
        exchangeSub = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener() {
                    @Override
                    public void onNext(Object object) {
                        setResult(RESULT_OK);
                        works.remove(work);
                        works.add(toIndex, work);
                        adapter.notifyItemMoved(position, toPosition);
                        adapter.setSelectedPosition(toIndex);
                        adapter.notifyItemChanged(position);
                        adapter.notifyItemChanged(toPosition);
                    }
                })
                .setDataNullable(true)
                .build();
        WorkApi.exchangeOrderObb(works.get(toIndex)
                .getId() + "," + works.get(index)
                .getId())
                .subscribe(exchangeSub);
    }

    @Override
    public void onMoveTop(final int position, final Work work) {
        if (setTopSub != null && !setTopSub.isUnsubscribed()) {
            return;
        }
        final List<Work> works = adapter.getWorks();
        setTopSub = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener() {
                    @Override
                    public void onNext(Object object) {
                        setResult(RESULT_OK);
                        works.remove(work);
                        adapter.notifyItemRemoved(position);
                        works.add(0, work);
                        adapter.setSelectedPosition(0);
                        adapter.notifyItemInserted(adapter.getHeaderViewCount());
                        adapter.notifyItemChanged(position);
                    }
                })
                .setDataNullable(true)
                .build();
        WorkApi.setTopObb(work.getId(), 1)
                .subscribe(setTopSub);
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(refreshSub, pageSub, exchangeSub, setTopSub);
    }
}