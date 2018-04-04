package com.hunliji.hljquestionanswer.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hunliji.hljcommonlibrary.models.questionanswer.Question;
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
import com.hunliji.hljquestionanswer.activities.QuestionDetailActivity;
import com.hunliji.hljquestionanswer.adapters.MyQuestionAdapter;
import com.hunliji.hljquestionanswer.api.QuestionAnswerApi;

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
 * Created by Suncloud on 2016/8/29.
 */
public class MyQuestionsFragment extends RefreshFragment implements PullToRefreshBase
        .OnRefreshListener<ListView>, AdapterView.OnItemClickListener {

    @BindView(R2.id.list_view)
    PullToRefreshListView listView;
    @BindView(R2.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;

    private Subscriber refreshSubscriber;
    private Subscriber pageSubscriber;

    private ArrayList<Question> questions;
    private MyQuestionAdapter adapter;
    private View endView;
    private View loadView;
    private View footerView;
    private Unbinder unbinder;

    public static MyQuestionsFragment newInstance() {
        Bundle args = new Bundle();
        MyQuestionsFragment fragment = new MyQuestionsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        questions = new ArrayList<>();
        adapter = new MyQuestionAdapter(getContext(), questions);
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
        View rootView = inflater.inflate(R.layout.hlj_common_fragment_ptr_list_view___cm,
                container,
                false);
        unbinder = ButterKnife.bind(this, rootView);
        emptyView.setHintId(R.string.label_my_question_empty___qa);
        emptyView.setHint2Id(R.string.label_my_question_empty_hint___qa);
        listView.getRefreshableView()
                .addFooterView(footerView);
        listView.setOnItemClickListener(this);
        listView.setOnRefreshListener(this);
        listView.getRefreshableView()
                .setAdapter(adapter);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (CommonUtil.isCollectionEmpty(questions)) {
            onRefresh(null);
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        if (refreshSubscriber == null || refreshSubscriber.isUnsubscribed()) {
            refreshSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<Question>>>() {
                        @Override
                        public void onNext(HljHttpData<List<Question>> listHljHttpData) {
                            questions.clear();
                            questions.addAll(listHljHttpData.getData());
                            adapter.notifyDataSetChanged();
                            listView.getRefreshableView()
                                    .setSelection(0);
                            initPagination(listHljHttpData.getPageCount());
                            if (onTabTextChangeListener != null) {
                                onTabTextChangeListener.onTabTextChange(listHljHttpData
                                        .getTotalCount());
                            }
                        }
                    })
                    .setProgressBar(listView.isRefreshing() ? null : progressBar)
                    .setEmptyView(emptyView)
                    .setPullToRefreshBase(listView)
                    .setListView(listView.getRefreshableView())
                    .build();

            QuestionAnswerApi.getMyQuestionsObb(1)
                    .subscribe(refreshSubscriber);
        }
    }

    private void initPagination(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSubscriber);
        Observable<HljHttpData<List<Question>>> observable = PaginationTool.buildPagingObservable(
                listView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpData<List<Question>>>() {
                    @Override
                    public Observable<HljHttpData<List<Question>>> onNextPage(int page) {
                        return QuestionAnswerApi.getMyQuestionsObb(page);
                    }
                })
                .setEndView(endView)
                .setLoadView(loadView)
                .build()
                .getPagingObservable();

        pageSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<Question>>>() {
                    @Override
                    public void onNext(HljHttpData<List<Question>> listHljHttpData) {
                        questions.addAll(listHljHttpData.getData());
                        adapter.notifyDataSetChanged();
                    }
                })
                .build();

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSubscriber);
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Question question = (Question) adapterView.getAdapter()
                .getItem(i);
        if (question != null && question.getId() > 0) {
            Intent intent = new Intent(getActivity(), QuestionDetailActivity.class);
            intent.putExtra("questionId", question.getId());
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_right,
                    R.anim.activity_anim_default);
        }
    }

    @Override
    public void refresh(Object... params) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (listView != null) {
            listView.getRefreshableView()
                    .setAdapter(null);
        }
        if (unbinder != null) {
            unbinder.unbind();
        }
        CommonUtil.unSubscribeSubs(refreshSubscriber, pageSubscriber);
    }
}
