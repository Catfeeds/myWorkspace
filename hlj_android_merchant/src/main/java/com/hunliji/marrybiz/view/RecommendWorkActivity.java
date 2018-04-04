package com.hunliji.marrybiz.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.adapter.RecommendWorkAdapter;
import com.hunliji.marrybiz.api.merchant.MerchantApi;
import com.hunliji.marrybiz.model.wrapper.RecommendWork;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by mo_yu on 2017/2/3.推荐橱窗
 */

public class RecommendWorkActivity extends HljBaseActivity implements PullToRefreshBase
        .OnRefreshListener<RecyclerView> {

    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private LinearLayoutManager layoutManager;
    private RecommendWorkAdapter adapter;
    private ArrayList<RecommendWork> recommendWorks;//推荐套餐，包括空坑位
    private ArrayList<String> workIds;//已经设置的推荐套餐id
    private final static int WORK_SELECT = 1;

    private HljHttpSubscriber initSubscriber;
    private HljHttpSubscriber cancelSubscriber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_work);
        ButterKnife.bind(this);
        initValue();
        initView();
        initLoad();
    }

    private void initValue() {
        recommendWorks = new ArrayList<>();
        workIds = new ArrayList<>();
    }

    private void initView() {
        recyclerView.setOnRefreshListener(this);
        layoutManager = new LinearLayoutManager(this);
        adapter = new RecommendWorkAdapter(this, recommendWorks);
        View footerView = View.inflate(this, R.layout.recommend_work_footer, null);
        adapter.setFooterView(footerView);
        recyclerView.getRefreshableView()
                .setLayoutManager(layoutManager);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);

        adapter.setOnAddClickListener(new RecommendWorkAdapter.OnAddClickListener() {
            @Override
            public void onAdd(int seat) {
                Intent intent = new Intent();
                intent.setClass(RecommendWorkActivity.this, RecommendWorkSelectActivity.class);
                intent.putExtra("seat", seat);
                intent.putExtra("workIds", workIds);
                startActivityForResult(intent, WORK_SELECT);
            }
        });

        adapter.setOnCancelClickListener(new RecommendWorkAdapter.OnCancelClickListener() {
            @Override
            public void onCancel(long id) {
                cancelRecommendWork(id);
            }
        });
    }

    private void initLoad() {
        if (initSubscriber == null || initSubscriber.isUnsubscribed()) {
            initSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<RecommendWork>>>() {
                        @Override
                        public void onNext(HljHttpData<List<RecommendWork>> data) {
                            if (data.getData() != null) {
                                recommendWorks.clear();
                                recommendWorks.addAll(data.getData());
                                adapter.notifyDataSetChanged();
                                //存储已推荐的套餐ids
                                workIds.clear();
                                for (RecommendWork work : recommendWorks) {
                                    if (work.getWork() != null) {
                                        workIds.add(String.valueOf(work.getWork()
                                                .getId()));
                                    }
                                }
                            }
                        }
                    })
                    .setDataNullable(true)
                    .setPullToRefreshBase(recyclerView)
                    .setProgressBar(recyclerView.isRefreshing() ? null : progressBar)
                    .build();

            MerchantApi.getRecommendWorks()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(initSubscriber);
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        initLoad();
    }

    private void cancelRecommendWork(long id) {
        if (cancelSubscriber == null || cancelSubscriber.isUnsubscribed()) {
            cancelSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener() {
                        @Override
                        public void onNext(Object o) {
                            onRefresh(recyclerView);
                        }
                    })
                    .build();
            MerchantApi.cancelRecommendWork(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(cancelSubscriber);
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(initSubscriber, cancelSubscriber);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            onRefresh(recyclerView);
        }
    }
}
