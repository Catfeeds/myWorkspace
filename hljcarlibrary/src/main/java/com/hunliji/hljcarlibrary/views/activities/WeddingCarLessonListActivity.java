package com.hunliji.hljcarlibrary.views.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcarlibrary.R;
import com.hunliji.hljcarlibrary.R2;
import com.hunliji.hljcarlibrary.adapter.WeddingCarLessonVerticalAdapter;
import com.hunliji.hljcarlibrary.api.WeddingCarApi;
import com.hunliji.hljcarlibrary.models.CarLesson;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.TopicUrl;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityThread;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.api.CommonApi;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by mo_yu on 2018/1/3.婚车必修课
 */

public class WeddingCarLessonListActivity extends HljBaseNoBarActivity implements PullToRefreshBase
        .OnRefreshListener, OnItemClickListener<CarLesson>, WeddingCarLessonVerticalAdapter
        .OnPraiseClickListener<CarLesson> {

    @Override
    public String pageTrackTagName() {
        return "婚车必修课";
    }

    @BindView(R2.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R2.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;

    private ArrayList<CarLesson> carLessons;
    private WeddingCarLessonVerticalAdapter adapter;
    private View endView;
    private View loadView;
    private HljHttpSubscriber refreshSubscriber;
    private HljHttpSubscriber praiseSubscriber;
    private Subscription pageSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wedding_car_lesson_list___car);
        ButterKnife.bind(this);
        setDefaultStatusBarPadding();
        initValue();
        initView();
        onRefresh(recyclerView);
    }

    private void initValue() {
        carLessons = new ArrayList<>();
    }

    private void initView() {
        View footerView = View.inflate(this, R.layout.hlj_foot_no_more___cm, null);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        loadView.setVisibility(View.INVISIBLE);
        adapter = new WeddingCarLessonVerticalAdapter(this);
        adapter.setCarLessons(carLessons);
        adapter.setFooterView(footerView);
        adapter.setOnItemClickListener(this);
        adapter.setOnPraiseClickListener(this);
        recyclerView.setOnRefreshListener(this);
        recyclerView.getRefreshableView()
                .setLayoutManager(new LinearLayoutManager(this));
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
    }


    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        if (refreshSubscriber == null || refreshSubscriber.isUnsubscribed()) {
            refreshSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<CarLesson>>>
                            () {
                        @Override
                        public void onNext(HljHttpData<List<CarLesson>> listHljHttpData) {
                            carLessons.clear();
                            carLessons.addAll(listHljHttpData.getData());
                            adapter.notifyDataSetChanged();
                            initPagination(listHljHttpData.getPageCount());
                        }
                    })
                    .setPullToRefreshBase(recyclerView)
                    .setProgressBar(recyclerView.isRefreshing() ? null : progressBar)
                    .setEmptyView(emptyView)
                    .setContentView(recyclerView)
                    .build();
            WeddingCarApi.getCarLessonsObb(HljCommon.PER_PAGE, 1)
                    .subscribe(refreshSubscriber);
        }
    }

    private void initPagination(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSubscription);
        pageSubscription = PaginationTool.buildPagingObservable(recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpData<List<CarLesson>>>() {
                    @Override
                    public Observable<HljHttpData<List<CarLesson>>> onNextPage(int page) {
                        return WeddingCarApi.getCarLessonsObb(HljCommon.PER_PAGE, page);
                    }
                })
                .setEndView(endView)
                .setLoadView(loadView)
                .build()
                .getPagingObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(HljHttpSubscriber.buildSubscriber(this)
                        .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<CarLesson>>>() {
                            @Override
                            public void onNext(
                                    HljHttpData<List<CarLesson>> listHljHttpData) {
                                carLessons.addAll(listHljHttpData.getData());
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .build());
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(refreshSubscriber, pageSubscription, praiseSubscriber);
    }

    @Override
    public void onItemClick(int position, CarLesson carLesson) {
        if (carLesson != null) {
            if (carLesson.getSourceType() == CarLesson.THREAD_SOURCE) {
                CommunityThread communityThread = (CommunityThread) carLesson.getEntityJson();
                ARouter.getInstance()
                        .build(RouterPath.IntentPath.Customer.COMMUNITY_THREAD_DETAIL)
                        .withLong("id", communityThread.getId())
                        .navigation(WeddingCarLessonListActivity.this);
            } else {
                TopicUrl topicUrl = (TopicUrl) carLesson.getEntityJson();
                ARouter.getInstance()
                        .build(RouterPath.IntentPath.Customer.SUB_PAGE_DETAIL_ACTIVITY)
                        .withLong("id", topicUrl.getId())
                        .navigation(WeddingCarLessonListActivity.this);
            }
        }
    }

    @Override
    public void onPraiseClick(
            int position,
            final CarLesson carLesson,
            final ImageView imgPraise,
            final TextView tvPraiseCount) {
        if (carLesson != null && carLesson.getSourceType() == CarLesson.THREAD_SOURCE) {
            final CommunityThread communityThread = (CommunityThread) carLesson.getEntityJson();
            if (praiseSubscriber == null || praiseSubscriber.isUnsubscribed()) {
                praiseSubscriber = HljHttpSubscriber.buildSubscriber(this)
                        .setOnNextListener(new SubscriberOnNextListener() {
                            @Override
                            public void onNext(Object o) {
                                if (communityThread.isPraised()) {
                                    ToastUtil.showToast(WeddingCarLessonListActivity.this,
                                            null,
                                            R.string.msg_success_to_un_praise___cm);
                                    communityThread.setPraised(false);
                                    communityThread.setPraisedSum(communityThread.getPraisedSum()
                                            - 1);
                                    imgPraise.setImageResource(R.mipmap
                                            .icon_praise_gold_30_24___car);
                                } else {
                                    ToastUtil.showToast(WeddingCarLessonListActivity.this,
                                            null,
                                            R.string.msg_success_to_praise___cm);
                                    communityThread.setPraised(true);
                                    communityThread.setPraisedSum(communityThread.getPraisedSum()
                                            + 1);
                                    imgPraise.setImageResource(R.mipmap
                                            .icon_praised_soild_gold_30_24___car);
                                }
                                tvPraiseCount.setText(String.valueOf(communityThread
                                        .getPraisedSum()));
                            }
                        })
                        .setProgressBar(progressBar)
                        .build();
                CommonApi.postThreadPraiseObb(communityThread.getPost()
                        .getId())
                        .subscribe(praiseSubscriber);
            }
        }
    }

    @OnClick(R2.id.btn_back)
    void onBack() {
        onBackPressed();
    }

}
