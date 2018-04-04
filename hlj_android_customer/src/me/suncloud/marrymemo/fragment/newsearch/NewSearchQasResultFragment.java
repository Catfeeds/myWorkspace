package me.suncloud.marrymemo.fragment.newsearch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.LinearLayout;

import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.questionanswer.Answer;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljhttplibrary.api.newsearch.NewSearchApi;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;
import com.hunliji.hljquestionanswer.activities.CreateQuestionTitleActivity;
import com.hunliji.hljquestionanswer.activities.QuestionDetailActivity;
import com.hunliji.hljtrackerlibrary.TrackerSite;

import java.util.ArrayList;
import java.util.List;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.newsearch.NewSearchQaResultAdapter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by hua_rong on 2018/1/8.
 * 问答搜索结果fragment
 */

public class NewSearchQasResultFragment extends NewBaseSearchResultFragment implements
        OnItemClickListener<Answer> {
    private ArrayList<Answer> answers = new ArrayList<>();
    private NewSearchQaResultAdapter adapter;

    private HljHttpSubscriber initSub;
    private HljHttpSubscriber pageSub;
    private LinearLayoutManager layoutManager;

    public static NewSearchQasResultFragment newInstance(Bundle args) {
        NewSearchQasResultFragment fragment = new NewSearchQasResultFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    protected void initViews() {
        super.initViews();

        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.getRefreshableView()
                .setLayoutManager(layoutManager);

        adapter = new NewSearchQaResultAdapter(getContext(), answers);
        adapter.setHeaderView(headerView);
        adapter.setFooterView(footerView);
        adapter.setOnItemClickListener(this);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
    }

    @Override
    protected void initLoad() {
        super.initLoad();
        clearData(adapter);
        CommonUtil.unSubscribeSubs(initSub);
        initSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<Answer>>>() {
                    @Override
                    public void onNext(HljHttpData<List<Answer>> hljHttpData) {
                        if (hljHttpData != null) {
                            if (!CommonUtil.isCollectionEmpty(hljHttpData.getData())) {
                                answers.addAll(hljHttpData.getData());
                            }
                            initPage(hljHttpData.getPageCount());
                        }
                        adapter.setData(answers);
                        LinearLayout addQa = headerView.findViewById(R.id.ll_add_qa);
                        if (addQa != null) {
                            if (CommonUtil.getCollectionSize(answers) < 3) {
                                if (CommonUtil.isCollectionEmpty(answers)) {
                                    footerViewHolder.noMoreHint.setVisibility(View.GONE);
                                }
                                addQa.setVisibility(View.VISIBLE);
                                addQa.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(getContext(),
                                                CreateQuestionTitleActivity.class);
                                        startActivity(intent);
                                    }
                                });
                            } else {
                                addQa.setVisibility(View.GONE);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        layoutManager.scrollToPosition(0);
                    }
                })
                .setProgressBar(progressBar)
                .setContentView(recyclerView)
                .setEmptyView(emptyView)
                .setDataNullable(true)
                .build();
        NewSearchApi.getQaList(keyword, searchType, 1)
                .subscribe(initSub);
    }

    private void initPage(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSub);
        Observable<HljHttpData<List<Answer>>> pageObb = PaginationTool.buildPagingObservable(
                recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpData<List<Answer>>>() {
                    @Override
                    public Observable<HljHttpData<List<Answer>>> onNextPage(int page) {
                        return NewSearchApi.getQaList(keyword, searchType, page);
                    }
                })
                .setLoadView(footerViewHolder.loading)
                .setEndView(footerViewHolder.noMoreHint)
                .build()
                .getPagingObservable()
                .observeOn(AndroidSchedulers.mainThread());
        pageSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<Answer>>>() {
                    @Override
                    public void onNext(HljHttpData<List<Answer>> hljHttpData) {
                        adapter.addData(hljHttpData.getData());
                    }
                })
                .build();
        pageObb.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSub);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        CommonUtil.unSubscribeSubs(initSub, pageSub);
    }

    @Override
    protected void resetFilterView() {
        if (getSearchActivity() != null) {
            getSearchActivity().setFilterView(null);
        }
    }


    @Override
    public void onItemClick(int position, Answer answer) {
        if (answer != null) {
            Intent intent = new Intent(getActivity(), QuestionDetailActivity.class);
            intent.putExtra("questionId",
                    answer.getQuestion()
                            .getId());
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().overridePendingTransition(R.anim.slide_in_right,
                        R.anim.activity_anim_default);
            }
        }
    }
}
