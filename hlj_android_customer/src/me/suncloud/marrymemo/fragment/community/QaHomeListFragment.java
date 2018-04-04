package me.suncloud.marrymemo.fragment.community;

import android.content.Intent;
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
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.questionanswer.QaVipMerchant;
import com.hunliji.hljcommonlibrary.models.questionanswer.Question;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.views.fragments.ScrollAbleFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;
import com.hunliji.hljquestionanswer.activities.QuestionDetailActivity;
import com.hunliji.hljquestionanswer.api.QuestionAnswerApi;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.community.QaHomeListAdapter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by luohanlin on 2017/10/23.
 */
public class QaHomeListFragment extends ScrollAbleFragment implements
        PullToRefreshVerticalRecyclerView.OnRefreshListener, OnItemClickListener {


    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.btn_scroll_top)
    ImageButton btnScrollTop;
    Unbinder unbinder;

    public static final String ARG_QA_HOME_LIST_TYPE = "qa_list_type";
    public static final int TYPE_HOTEST = 1;
    public static final int TYPE_NEWEST = 2;
    public static final int TYPE_MARKED = 3;
    private int listType;
    private ArrayList<Question> questions;
    private QaHomeListAdapter adapter;
    private View footerView;
    private View endView;
    private View loadView;
    private LinearLayoutManager layoutManager;
    private HljHttpSubscriber pageSubscriber;
    private long lastAnswerTime;//全部问题页面需要传lastAnswerTime，防止重复
    private final static String ALL_QA_URL = "p/wedding/index" + "" + "" + "" + "" + "" + "" + ""
            + ".php/home/APIQaQuestion/latestQuestion";
    private final static String ALL_QA_LAST_URL = "p/wedding/index" + "" + "" + "" + "" + "" + ""
            + ".php/home/APIQaQuestion/latestQuestion?last_answer_time=%s";
    private HljHttpSubscriber vSub;
    private HljHttpSubscriber listSub;

    public static QaHomeListFragment newInstance(int type) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_QA_HOME_LIST_TYPE, type);
        QaHomeListFragment fragment = new QaHomeListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initValues();
    }

    private void initValues() {
        Bundle args = getArguments();
        if (args != null) {
            listType = args.getInt(ARG_QA_HOME_LIST_TYPE, TYPE_HOTEST);
        }
        questions = new ArrayList<>();
        adapter = new QaHomeListAdapter(getContext(), questions, listType);
        adapter.setOnItemClickListener(this);
        footerView = View.inflate(getContext(),
                com.hunliji.hljquestionanswer.R.layout.hlj_foot_no_more___cm,
                null);
        endView = footerView.findViewById(com.hunliji.hljquestionanswer.R.id.no_more_hint);
        loadView = footerView.findViewById(com.hunliji.hljquestionanswer.R.id.loading);
        loadView.setVisibility(View.INVISIBLE);
        adapter.setFooterView(footerView);
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
        initViews();
        initTracker();
        return rootView;
    }

    private void initTracker() {
        switch (listType) {
            case TYPE_MARKED:
                HljVTTagger.tagViewParentName(recyclerView.getRefreshableView(),
                        "city_question_list");
                break;
            case TYPE_NEWEST:
                HljVTTagger.tagViewParentName(recyclerView.getRefreshableView(),
                        "latest_question_list");
                break;
            default:
                HljVTTagger.tagViewParentName(recyclerView.getRefreshableView(),
                        "hot_question_list");
                break;
        }
    }

    private void initViews() {
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.getRefreshableView()
                .setLayoutManager(layoutManager);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
        recyclerView.setOnRefreshListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (questions.isEmpty()) {
            initLoad(false);
        }
    }

    private void initLoad(boolean isPullDown) {
        CommonUtil.unSubscribeSubs(listSub);
        Observable<HljHttpData<List<Question>>> listObservable = getListObb(1);
        listSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setProgressBar(isPullDown ? null : progressBar)
                .setContentView(recyclerView)
                .setEmptyView(emptyView)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<Question>>>() {
                    @Override
                    public void onNext(HljHttpData<List<Question>> listHljHttpData) {
                        recyclerView.onRefreshComplete();
                        questions.clear();
                        questions.addAll(listHljHttpData.getData());
                        if (listType == TYPE_NEWEST && questions.size() > 0 && questions.get(0)
                                .getLastAnswerTime() != null) {
                            lastAnswerTime = questions.get(0)
                                    .getLastAnswerTime()
                                    .getMillis();
                        }
                        adapter.notifyDataSetChanged();
                        initPagination(listHljHttpData.getPageCount());
                    }
                })
                .build();
        listObservable.subscribe(listSub);

        if (listType == TYPE_HOTEST) {
            CommonUtil.unSubscribeSubs(vSub);
            Observable<List<QaVipMerchant>> vObservable = QuestionAnswerApi.getQaVipMerchantList();
            vSub = HljHttpSubscriber.buildSubscriber(getContext())
                    .setContentView(recyclerView)
                    .setEmptyView(emptyView)
                    .setOnNextListener(new SubscriberOnNextListener<List<QaVipMerchant>>() {
                        @Override
                        public void onNext(List<QaVipMerchant> qaVipMerchants) {
                            adapter.setVipMerchants(qaVipMerchants);
                            adapter.notifyDataSetChanged();
                        }
                    })
                    .build();
            vObservable.subscribe(vSub);
        }
    }

    @Override
    public void refresh(Object... params) {
        initLoad(false);
    }

    private Observable<HljHttpData<List<Question>>> getListObb(int page) {
        Observable<HljHttpData<List<Question>>> listObb;
        switch (listType) {
            case TYPE_MARKED:
                listObb = QuestionAnswerApi.getMarkQuestionsV2Obb(page);
                break;
            case TYPE_NEWEST:
                listObb = QuestionAnswerApi.getLatestQaAnswerObb(getUrl(page), page);
                break;
            default:
                listObb = QuestionAnswerApi.getHotQaQuestionObb(page);
                break;
        }

        return listObb;
    }

    private String getUrl(int pageCount) {
        if (lastAnswerTime != 0 && pageCount != 1) {
            return String.format(ALL_QA_LAST_URL, lastAnswerTime);
        } else {
            return String.format(ALL_QA_URL);
        }
    }

    private void initPagination(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSubscriber);
        Observable<HljHttpData<List<Question>>> observable = PaginationTool.buildPagingObservable(
                recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpData<List<Question>>>() {
                    @Override
                    public Observable<HljHttpData<List<Question>>> onNextPage(int page) {
                        return getListObb(page);
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
    public View getScrollableView() {
        return recyclerView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        CommonUtil.unSubscribeSubs(listSub, vSub, pageSubscriber);
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        initLoad(true);
    }

    @Override
    public void onItemClick(int position, Object object) {
        Question question = (Question) object;
        if (question != null) {
            Intent intent = new Intent(getContext(), QuestionDetailActivity.class);
            intent.putExtra("questionId", question.getId());
            startActivity(intent);
        }
    }
}
