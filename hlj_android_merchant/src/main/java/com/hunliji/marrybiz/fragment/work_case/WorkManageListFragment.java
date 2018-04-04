package com.hunliji.marrybiz.fragment.work_case;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.adapter.work_case.WorkManageListAdapter;
import com.hunliji.marrybiz.api.work_case.WorkApi;
import com.hunliji.marrybiz.util.work_case.WorkStatusEnum;
import com.hunliji.marrybiz.view.work_case.WorkManageListActivity;
import com.hunliji.marrybiz.view.work_case.WorkOptimizeListActivity;
import com.hunliji.marrybiz.view.work_case.WorkSortListActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * 套餐案例管理列表
 * Created by chen_bin on 2017/2/3 0003.
 */
public class WorkManageListFragment extends RefreshFragment implements PullToRefreshBase
        .OnRefreshListener<RecyclerView>, WorkManageListAdapter.OnTurnListener,
        WorkManageListAdapter.OnItemDeleteListener {
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.btn_sort)
    ImageButton btnSort;
    private View headerView;
    private View footerView;
    private TextView endView;
    private View loadView;
    private HeaderViewHolder headerViewHolder;
    private WorkManageListAdapter adapter;
    private WorkStatusEnum statusEnum;
    private boolean isNeedRefresh; //是否需要刷新
    private int type; //0：套餐，1：案例
    private int optimizeCount; //需要优化的个数
    private Unbinder unbinder;
    private HljHttpSubscriber refreshSub;
    private HljHttpSubscriber pageSub;
    private HljHttpSubscriber postSub;

    public static WorkManageListFragment newInstance(int type, WorkStatusEnum statusEnum) {
        WorkManageListFragment fragment = new WorkManageListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        bundle.putSerializable("status_enum", statusEnum);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getInt("type");
            statusEnum = (WorkStatusEnum) getArguments().getSerializable("status_enum");
        }
        headerView = View.inflate(getContext(), R.layout.work_manage_optimize_header_item, null);
        headerViewHolder = new HeaderViewHolder(headerView);
        footerView = View.inflate(getContext(), R.layout.hlj_foot_no_more___cm, null);
        endView = footerView.findViewById(R.id.no_more_hint);
        endView.setText(R.string.label_the_end2___cm);
        loadView = footerView.findViewById(R.id.loading);
        loadView.setVisibility(View.INVISIBLE);
        adapter = new WorkManageListAdapter(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_work_manage_list, container, false);
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
        recyclerView.setOnRefreshListener(this);
        recyclerView.getRefreshableView()
                .setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.setHeaderView(headerView);
        adapter.setFooterView(footerView);
        adapter.setStatusEnum(statusEnum);
        adapter.setOnTurnClickListener(this);
        adapter.setOnItemDeleteListener(this);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onRefresh(null);
    }

    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        if (refreshSub == null || refreshSub.isUnsubscribed()) {
            if (statusEnum != WorkStatusEnum.ON) {
                refreshSub = HljHttpSubscriber.buildSubscriber(getContext())
                        .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<Work>>>() {
                            @Override
                            public void onNext(HljHttpData<List<Work>> worksData) {
                                ResultZip resultZip = new ResultZip();
                                resultZip.worksData = worksData;
                                setData(resultZip);
                            }
                        })
                        .setDataNullable(true)
                        .setEmptyView(emptyView)
                        .setContentView(recyclerView)
                        .setPullToRefreshBase(recyclerView)
                        .setProgressBar(recyclerView.isRefreshing() ? null : progressBar)
                        .build();
                WorkApi.getWorksObb(type, statusEnum, 1, 20)
                        .subscribe(refreshSub);
            } else {
                //套餐列表
                Observable<HljHttpData<List<Work>>> worksObb = WorkApi.getWorksObb(type,
                        statusEnum,
                        1,
                        20);
                //需要优化的套餐个数，rating=1
                Observable<HljHttpData<List<Work>>> optimizeWoksObb = WorkApi.getWorksObb(type,
                        WorkStatusEnum.OPTIMIZE,
                        1,
                        1);
                Observable<ResultZip> observable = Observable.zip(worksObb,
                        optimizeWoksObb,
                        new Func2<HljHttpData<List<Work>>, HljHttpData<List<Work>>, ResultZip>() {
                            @Override
                            public ResultZip call(
                                    HljHttpData<List<Work>> worksData,
                                    HljHttpData<List<Work>> optimizeWorksData) {
                                ResultZip resultZip = new ResultZip();
                                resultZip.worksData = worksData;
                                resultZip.optimizeWorksData = optimizeWorksData;
                                return resultZip;
                            }
                        });
                refreshSub = HljHttpSubscriber.buildSubscriber(getContext())
                        .setOnNextListener(new SubscriberOnNextListener<ResultZip>() {
                            @Override
                            public void onNext(ResultZip resultZip) {
                                setData(resultZip);
                            }
                        })
                        .setDataNullable(true)
                        .setEmptyView(emptyView)
                        .setContentView(recyclerView)
                        .setPullToRefreshBase(recyclerView)
                        .setProgressBar(recyclerView.isRefreshing() ? null : progressBar)
                        .build();
                observable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(refreshSub);
            }
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
                        return WorkApi.getWorksObb(type, statusEnum, page, 20);
                    }
                })
                .setEndView(endView)
                .setLoadView(loadView)
                .build()
                .getPagingObservable();
        pageSub = HljHttpSubscriber.buildSubscriber(getContext())
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

    private class ResultZip {
        HljHttpData<List<Work>> worksData;
        HljHttpData<List<Work>> optimizeWorksData;
    }

    private void setData(ResultZip resultZip) {
        recyclerView.getRefreshableView()
                .scrollToPosition(0);
        optimizeCount = 0;
        int pageCount = 0;
        List<Work> works = null;
        if (resultZip.optimizeWorksData != null) {
            optimizeCount = resultZip.optimizeWorksData.getTotalCount();
        }
        if (resultZip.worksData != null) {
            pageCount = resultZip.worksData.getPageCount();
            works = resultZip.worksData.getData();
        }
        adapter.setWorks(works);
        initPagination(pageCount);
        setShowEmptyView();
    }

    private void setShowEmptyView() {
        boolean isDataEmpty = CommonUtil.isCollectionEmpty(adapter.getWorks());
        if (isDataEmpty) {
            emptyView.showEmptyView();
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyView.hideEmptyView();
            recyclerView.setVisibility(View.VISIBLE);
        }
        btnSort.setVisibility(!isDataEmpty && statusEnum == WorkStatusEnum.ON ? View.VISIBLE :
                View.GONE);
        if (headerViewHolder != null) {
            headerViewHolder.headerLayout.setVisibility(optimizeCount <= 0 ? View.GONE : View
                    .VISIBLE);
            headerViewHolder.tvOptimizeCount.setText(getString(type == 0 ? R.string
                            .label_work_optimize_count : R.string.label_case_optimize_count,
                    optimizeCount));
        }
    }

    @OnClick(R.id.btn_sort)
    void onSort() {
        Intent intent = new Intent(getContext(), WorkSortListActivity.class);
        intent.putExtra("type", type);
        getActivity().startActivityForResult(intent, Constants.RequestCode.WORK_SORT);
    }

    @Override
    public void onTurn(int position, Work work) {
        String str;
        String action;
        int stringId;
        str = type == 0 ? getString(R.string.label_work) : getString(R.string.label_case);
        if (work.isSoldOut()) {
            action = getString(R.string.label_opu_on);
            stringId = R.string.msg_success_to_opu_on;
        } else {
            action = getString(R.string.label_opu_off);
            stringId = R.string.msg_success_to_opu_off;
        }
        showActionDialog("turn",
                getString(R.string.label_opu_notice, action, str),
                stringId,
                work,
                position);
    }

    @Override
    public void onItemDelete(int position, Work work) {
        String str = type == 0 ? getString(R.string.label_work) : getString(R.string.label_case);
        showActionDialog("delete",
                getString(R.string.label_opu_notice, getString(R.string.label_delete___cm), str),
                R.string.msg_delete_success,
                work,
                position);
    }

    private void showActionDialog(
            final String action,
            final String alertMsg,
            final int stringId,
            final Work work,
            final int position) {
        DialogUtil.createDoubleButtonDialog(getContext(),
                alertMsg,
                null,
                null,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonUtil.unSubscribeSubs(postSub);
                        postSub = HljHttpSubscriber.buildSubscriber(getContext())
                                .setOnNextListener(new SubscriberOnNextListener() {
                                    @Override
                                    public void onNext(Object o) {
                                        ToastUtil.showCustomToast(getContext(), stringId);
                                        adapter.getWorks()
                                                .remove(work);
                                        adapter.notifyItemRemoved(position);
                                        if (statusEnum == WorkStatusEnum.ON && !TextUtils.isEmpty(
                                                work.getReason())) {
                                            optimizeCount = optimizeCount - 1;
                                        }
                                        if (statusEnum == WorkStatusEnum.OPTIMIZE) {
                                            getActivity().setResult(Activity.RESULT_OK);
                                        } else {
                                            ((WorkManageListActivity) getContext()).refreshData();
                                        }
                                        setShowEmptyView();
                                    }
                                })
                                .setDataNullable(true)
                                .setProgressDialog(DialogUtil.createProgressDialog(getContext()))
                                .build();
                        if ("delete".equals(action)) {
                            WorkApi.deleteWorkObb(work.getId())
                                    .subscribe(postSub);
                        } else {
                            WorkApi.setSoldOutObb(work.getId(), work.isSoldOut() ? 0 : 1)
                                    .subscribe(postSub);
                        }
                    }
                },
                null)
                .show();
    }

    public class HeaderViewHolder {
        @BindView(R.id.tv_optimize_count)
        TextView tvOptimizeCount;
        @BindView(R.id.header_layout)
        LinearLayout headerLayout;

        public HeaderViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
            headerLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), WorkOptimizeListActivity.class);
                    intent.putExtra("type", type);
                    getActivity().startActivityForResult(intent,
                            Constants.RequestCode.WORK_OPTIMIZE);
                }
            });
        }
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
    public void refresh(Object... params) {
        if (recyclerView != null) {
            onRefresh(null);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
        CommonUtil.unSubscribeSubs(refreshSub, pageSub, postSub);
    }
}