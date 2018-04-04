package com.hunliji.marrybiz.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
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
import com.hunliji.marrybiz.adapter.RecommendWorkSelectAdapter;
import com.hunliji.marrybiz.api.merchant.MerchantApi;
import com.hunliji.marrybiz.api.work_case.WorkApi;
import com.hunliji.marrybiz.util.work_case.WorkStatusEnum;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by mo_yu on 2017/2/4.推荐橱窗选择
 */

public class RecommendWorkSelectActivity extends HljBaseActivity implements PullToRefreshBase
        .OnRefreshListener<RecyclerView> {
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private LinearLayoutManager layoutManager;
    private RecommendWorkSelectAdapter adapter;
    private List<Work> works;
    private List<Long> workIds;//需要剔除的套餐ids
    private int seat;//位置，只允许1,2,3
    private View endView;
    private View loadView;

    private HljHttpSubscriber initSubscriber;
    private HljHttpSubscriber addSubscriber;
    private HljHttpSubscriber pageSubscriber;//分页

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hlj_common_fragment_ptr_recycler_view___cm);
        ButterKnife.bind(this);
        initValue();
        initView();
        initLoad();
    }

    private void initValue() {
        works = new ArrayList<>();
        seat = getIntent().getIntExtra("seat", 1);
        workIds = new ArrayList<>();
        ArrayList<String> ids = getIntent().getStringArrayListExtra("workIds");
        if (ids != null) {
            for (String id : ids) {
                workIds.add(Long.valueOf(id));
            }
        }
    }

    private void initView() {
        setOkText(R.string.complete);

        recyclerView.setOnRefreshListener(this);
        layoutManager = new LinearLayoutManager(this);
        adapter = new RecommendWorkSelectAdapter(this, works);
        View footerView = View.inflate(this, R.layout.hlj_foot_no_more___cm, null);
        endView = footerView.findViewById(com.hunliji.hljlivelibrary.R.id.no_more_hint);
        loadView = footerView.findViewById(com.hunliji.hljlivelibrary.R.id.loading);
        loadView.setVisibility(View.INVISIBLE);
        adapter.setFooterView(footerView);
        recyclerView.getRefreshableView()
                .setLayoutManager(layoutManager);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);

    }

    private void initLoad() {
        if (initSubscriber == null || initSubscriber.isUnsubscribed()) {
            initSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<Work>>>() {
                        @Override
                        public void onNext(HljHttpData<List<Work>> data) {
                            works.clear();
                            ArrayList<Work> list = (ArrayList<Work>) data.getData();
                            for (Work work : list) {
                                if (!workIds.contains(work.getId())) {
                                    works.add(work);
                                }
                            }
                            if (works.size() == 0) {
                                emptyView.showEmptyView();
                                recyclerView.setVisibility(View.GONE);
                            } else {
                                emptyView.hideEmptyView();
                                recyclerView.setVisibility(View.VISIBLE);
                            }
                            adapter.notifyDataSetChanged();
                            initPage(data.getPageCount());
                        }
                    })
                    .setPullToRefreshBase(recyclerView)
                    .setContentView(recyclerView)
                    .setEmptyView(emptyView)
                    .setProgressBar(recyclerView.isRefreshing() ? null : progressBar)
                    .build();

            WorkApi.getWorksObb(0, WorkStatusEnum.ON, 1, 20)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(initSubscriber);
        }
    }

    private void initPage(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSubscriber);
        Observable<HljHttpData<List<Work>>> pageObservable = PaginationTool.buildPagingObservable(
                recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpData<List<Work>>>() {
                    @Override
                    public Observable<HljHttpData<List<Work>>> onNextPage(
                            int page) {
                        Log.d("pagination tool", "on load: " + page);
                        return WorkApi.getWorksObb(0, WorkStatusEnum.ON, page, 20);
                    }
                })
                .setLoadView(loadView)
                .setEndView(endView)
                .build()
                .getPagingObservable()
                .observeOn(AndroidSchedulers.mainThread());

        pageSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<Work>>>() {
                    @Override
                    public void onNext(HljHttpData<List<Work>> data) {
                        works.addAll(data.getData());
                        for (Work work : works) {
                            if (workIds.contains(work.getId())) {
                                works.remove(work);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                })
                .build();

        pageObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSubscriber);
    }

    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        initLoad();
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(initSubscriber, pageSubscriber, addSubscriber);
    }

    @Override
    public void onOkButtonClick() {
        super.onOkButtonClick();
        long workId = adapter.getCheckedId();
        if (workId == 0) {
            finish();
        } else {
            editRecommendWork(workId);
        }
    }

    private void editRecommendWork(long workId) {
        if (addSubscriber == null || addSubscriber.isUnsubscribed()) {
            addSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener() {
                        @Override
                        public void onNext(Object o) {
                            setResult(Activity.RESULT_OK);
                            finish();
                        }
                    })
                    .build();
            MerchantApi.editRecommendWork(seat, workId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(addSubscriber);
        }
    }
}
