package com.hunliji.hljquestionanswer.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.models.questionanswer.Answer;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;
import com.hunliji.hljquestionanswer.R;
import com.hunliji.hljquestionanswer.R2;
import com.hunliji.hljquestionanswer.activities.AnswerDetailActivity;
import com.hunliji.hljquestionanswer.adapters.MerchantQuestionAdapter;
import com.hunliji.hljquestionanswer.api.QuestionAnswerApi;
import com.hunliji.hljquestionanswer.models.wrappers.RecQaWrappers;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by mo_yu on 2016/11/11 我关注的问题
 */
public class MyMerchantFollowFragment extends RefreshFragment implements PullToRefreshBase
        .OnRefreshListener<RecyclerView> {

    @BindView(R2.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R2.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;

    private Subscriber refreshSubscriber;
    private Subscriber pageSubscriber;

    private ArrayList<RecQaWrappers> recQaWrapperses;
    private MerchantQuestionAdapter adapter;
    private View endView;
    private View loadView;
    private View footerView;
    private Unbinder unbinder;
    private LinearLayoutManager layoutManager;


    public static MyMerchantFollowFragment newInstance() {
        Bundle args = new Bundle();
        MyMerchantFollowFragment fragment = new MyMerchantFollowFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recQaWrapperses = new ArrayList<>();
        adapter = new MerchantQuestionAdapter(getContext());
        footerView = View.inflate(getContext(), R.layout.hlj_foot_no_more___cm, null);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        loadView.setVisibility(View.INVISIBLE);
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
        emptyView.setEmptyDrawableId(R.mipmap.icon_empty_message);
        emptyView.setHintId(R.string.label_my_follow_question_empty___qa);
        adapter.setFooterView(footerView);
        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.getRefreshableView()
                .setLayoutManager(layoutManager);
        recyclerView.setOnRefreshListener(this);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
        return rootView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (recQaWrapperses.isEmpty()) {
            onRefresh(recyclerView);
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        if (refreshSubscriber == null || refreshSubscriber.isUnsubscribed()) {
            refreshSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<RecQaWrappers>>>() {
                        @Override
                        public void onNext(HljHttpData<List<RecQaWrappers>> listHljHttpData) {
                            recQaWrapperses.clear();
                            recQaWrapperses.addAll(listHljHttpData.getData());
                            adapter.setData(recQaWrapperses);
                            adapter.notifyDataSetChanged();
                            initPagination(listHljHttpData.getPageCount());
                        }
                    })
                    .setProgressBar(recyclerView.isRefreshing() ? null : progressBar)
                    .setEmptyView(emptyView)
                    .setPullToRefreshBase(recyclerView)
                    .setContentView(recyclerView.getRefreshableView())
                    .build();

            QuestionAnswerApi.getMyMerchantFollowObb(1)
                    .subscribe(refreshSubscriber);
        }
    }

    private void initPagination(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSubscriber);
        Observable<HljHttpData<List<RecQaWrappers>>> observable = PaginationTool
                .buildPagingObservable(
                recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpData<List<RecQaWrappers>>>() {
                    @Override
                    public Observable<HljHttpData<List<RecQaWrappers>>> onNextPage(int page) {
                        return QuestionAnswerApi.getMyMerchantFollowObb(page);
                    }
                })
                .setEndView(endView)
                .setLoadView(loadView)
                .build()
                .getPagingObservable();

        pageSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<RecQaWrappers>>>
                        () {
                    @Override
                    public void onNext(HljHttpData<List<RecQaWrappers>> listHljHttpData) {
                        recQaWrapperses.addAll(listHljHttpData.getData());
                        adapter.setData(recQaWrapperses);
                        adapter.notifyDataSetChanged();
                    }
                })
                .build();

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSubscriber);
    }

    @Override
    public void refresh(Object... params) {

    }

    @Override
    public void onDestroy() {
        if (refreshSubscriber != null && !refreshSubscriber.isUnsubscribed()) {
            refreshSubscriber.unsubscribe();
        }
        if (pageSubscriber != null && !pageSubscriber.isUnsubscribed()) {
            pageSubscriber.unsubscribe();
        }
        unbinder.unbind();
        super.onDestroy();
    }
}
