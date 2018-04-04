package com.hunliji.hljcarlibrary.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcarlibrary.R;
import com.hunliji.hljcarlibrary.R2;
import com.hunliji.hljcarlibrary.adapter.WeddingCarCommentListAdapter;
import com.hunliji.hljcarlibrary.adapter.viewholder.WeddingCarCommentMarksViewHolder;
import com.hunliji.hljcarlibrary.api.WeddingCarApi;
import com.hunliji.hljcarlibrary.models.HljHttpCommentsData;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.CommentMark;
import com.hunliji.hljcommonlibrary.models.chat_ext_object.TrackWeddingCarProduct;
import com.hunliji.hljcommonlibrary.models.chat_ext_object.WSTrack;
import com.hunliji.hljcommonlibrary.models.questionanswer.Question;
import com.hunliji.hljcommonlibrary.models.wedding_car.WeddingCarComment;
import com.hunliji.hljcommonlibrary.models.wedding_car.WeddingCarProduct;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResultZip;
import com.hunliji.hljhttplibrary.entities.HljRZField;
import com.hunliji.hljhttplibrary.utils.AuthUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljquestionanswer.activities.AskQuestionListActivity;
import com.hunliji.hljquestionanswer.api.QuestionAnswerApi;
import com.hunliji.hljquestionanswer.models.HljHttpQuestion;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func3;
import rx.schedulers.Schedulers;

/**
 * 婚车评价
 * Created by jinxin on 2018/1/11 0011.
 */

public class WeddingCarCommentListActivity extends HljBaseNoBarActivity implements
        WeddingCarCommentMarksViewHolder.OnCommentFilterListener,
        OnItemClickListener<WeddingCarComment> {

    public static final String ARG_MERCHANT_ID = "merchant_id";
    public static final String ARG_MERCHANT_USER_ID = "merchant_user_id";
    public static final String ARG_MARK_ID = "mark_id";
    public static final String ARG_CAR_PRODUCT = "car_product";

    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.question_layout)
    LinearLayout questionLayout;
    @BindView(R2.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R2.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R2.id.bottom_layout)
    LinearLayout bottomLayout;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;

    private WeddingCarProduct carProduct;
    private long merchantId;
    private long markId;
    private View bottomMoreLayout;
    private View endView;
    private View loadView;
    private LinearLayoutManager layoutManager;
    private WeddingCarCommentListAdapter adapter;
    private HljHttpSubscriber initSub;
    private HljHttpSubscriber getCommentsSub;
    private WeddingCarCommentMarksViewHolder marksViewHolder;
    private int currentPage;
    private int pageCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wedding_car_comment_list___car);
        ButterKnife.bind(this);

        initConstant();
        initWidgets();
        initLoad();
    }

    private void initConstant() {
        if (getIntent() != null) {
            merchantId = getIntent().getLongExtra(ARG_MERCHANT_ID, 0);
            markId = getIntent().getLongExtra(ARG_MARK_ID, 0);
            carProduct = getIntent().getParcelableExtra(ARG_CAR_PRODUCT);
        }
    }

    private void initWidgets() {
        setDefaultStatusBarPadding();
        View footerView = View.inflate(this,
                R.layout.wedding_car_comment_footer_layout___car,
                null);
        bottomMoreLayout = footerView.findViewById(R.id.bottom_more_layout);
        bottomMoreLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int firstSixMonthAgoIndex = adapter.getFirstSixMonthAgoIndex();
                int positionStart = adapter.getItemCount() - adapter.getFooterViewCount();
                int itemCount = CommonUtil.getCollectionSize(adapter.getComments()) -
                        firstSixMonthAgoIndex;
                adapter.setFirstSixMonthAgoIndex(0);
                adapter.notifyItemRangeInserted(positionStart, itemCount);
                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (recyclerView != null) {
                            setShowFooterView();
                        }
                    }
                }, 120);
            }
        });
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        loadView.setVisibility(View.INVISIBLE);
        emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                initLoad();
            }
        });
        ((SimpleItemAnimator) recyclerView.getRefreshableView()
                .getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setNeedChangeSize(false);
        recyclerView.setMode(PullToRefreshBase.Mode.DISABLED);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.getRefreshableView()
                .setLayoutManager(layoutManager);
        adapter = new WeddingCarCommentListAdapter(this);
        adapter.setFooterView(footerView);
        adapter.setOnItemClickListener(this);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
        recyclerView.getRefreshableView()
                .addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                        switch (newState) {
                            case RecyclerView.SCROLL_STATE_IDLE:
                                if (!CommonUtil.isUnsubscribed(getCommentsSub)) {
                                    return;
                                }
                                int position = layoutManager.findLastVisibleItemPosition();
                                int itemCount = recyclerView.getAdapter()
                                        .getItemCount();
                                if (position >= itemCount - 5 && currentPage < pageCount &&
                                        adapter.getFirstSixMonthAgoIndex() <= 0) {
                                    endView.setVisibility(View.GONE);
                                    loadView.setVisibility(View.VISIBLE);
                                    initPagination();
                                } else {
                                    setShowFooterView();
                                }
                                break;
                        }
                    }
                });
    }

    private void initLoad() {
        CommonUtil.unSubscribeSubs(initSub);
        if (initSub == null || initSub.isUnsubscribed()) {
            //问答
            Observable<HljHttpQuestion<List<Question>>> questionsObb = QuestionAnswerApi.getQAList(
                    merchantId,
                    1,
                    1);
            //标签
            Observable<HljHttpData<List<CommentMark>>> marksObb = WeddingCarApi.getWeddingCarMarks(
                    merchantId);
            //评论
            Observable<HljHttpCommentsData> commentsObb = WeddingCarApi.getMerchantCommentsObb(this,
                    merchantId,
                    markId,
                    1,
                    HljCommon.PER_PAGE);

            Observable<ResultZip> observable = Observable.zip(questionsObb,
                    marksObb,
                    commentsObb,
                    new Func3<HljHttpQuestion<List<Question>>, HljHttpData<List<CommentMark>>,
                            HljHttpCommentsData, ResultZip>() {
                        @Override
                        public ResultZip call(
                                HljHttpQuestion<List<Question>> questionsData,
                                HljHttpData<List<CommentMark>> marksData,
                                HljHttpCommentsData commentsData) {
                            ResultZip resultZip = new ResultZip();
                            resultZip.marks = marksData == null ? null : marksData.getData();
                            resultZip.questions = questionsData == null ? null : questionsData
                                    .getData();
                            resultZip.commentsData = commentsData;
                            return resultZip;
                        }
                    });
            initSub = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<ResultZip>() {
                        @Override
                        public void onNext(ResultZip resultZip) {
                            bottomLayout.setVisibility(View.VISIBLE);
                            setQuestions(resultZip.questions);
                            setCommentMarks(resultZip.marks);
                            setComments(resultZip.commentsData);
                        }
                    })
                    .setDataNullable(true)
                    .setEmptyView(emptyView)
                    .setProgressBar(progressBar)
                    .setContentView(recyclerView)
                    .build();
            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(initSub);
        }
    }

    private void setQuestions(List<Question> questions) {
        questionLayout.setVisibility(View.VISIBLE);
        if (CommonUtil.isCollectionEmpty(questions)) {
            tvTitle.setText(getString(R.string.hint_answer_content_empty___cm) + "~");
        } else {
            tvTitle.setText(questions.get(0)
                    .getTitle());
        }
    }

    private void setCommentMarks(List<CommentMark> marks) {
        if (CommonUtil.isCollectionEmpty(marks)) {
            return;
        }
        View headerView = View.inflate(this, R.layout.wedding_car_comment_marks_flow___car, null);
        marksViewHolder = (WeddingCarCommentMarksViewHolder) headerView.getTag();
        if (marksViewHolder == null) {
            adapter.setHeaderView(headerView);
            marksViewHolder = new WeddingCarCommentMarksViewHolder(headerView);
            marksViewHolder.setMarkId(markId);
            marksViewHolder.setShowBottomLineView(true);
            marksViewHolder.setOnCommentFilterListener(this);
            headerView.setTag(marksViewHolder);
        }
        marksViewHolder.setView(this, marks, 0, 0);
    }

    private void setComments(HljHttpCommentsData commentsData) {
        currentPage = 1;
        pageCount = 0;
        int firstSixMonthAgoIndex = -1;
        List<WeddingCarComment> comments = null;
        if (commentsData != null) {
            pageCount = commentsData.getPageCount();
            firstSixMonthAgoIndex = commentsData.getFirstSixMonthAgoIndex();
            comments = commentsData.getData();
        }
        adapter.setFirstSixMonthAgoIndex(firstSixMonthAgoIndex);
        adapter.setComments(comments);
        setShowEmptyView();
        setShowFooterView();
    }

    private void setShowEmptyView() {
        boolean isDataEmpty = CommonUtil.isCollectionEmpty(adapter.getComments());
        if (marksViewHolder != null) {
            marksViewHolder.setShowEmptyView(isDataEmpty);
        } else if (isDataEmpty) {
            emptyView.showEmptyView();
            recyclerView.setVisibility(View.GONE);
        }
    }


    private void initPagination() {
        getCommentsSub = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpCommentsData>() {
                    @Override
                    public void onNext(HljHttpCommentsData commentsData) {
                        addComments(commentsData);
                    }
                })
                .setOnErrorListener(new SubscriberOnErrorListener() {
                    @Override
                    public void onError(Object o) {
                        loadView.setVisibility(View.GONE);
                        endView.setVisibility(View.INVISIBLE);
                    }
                })
                .setDataNullable(true)
                .build();
        int page = currentPage + 1;
        WeddingCarApi.getMerchantCommentsObb(this, merchantId, markId, page, HljCommon.PER_PAGE)
                .subscribe(getCommentsSub);
    }

    private void addComments(HljHttpCommentsData commentsData) {
        currentPage++;
        int firstSixMonthAgoIndex = -1;
        List<WeddingCarComment> comments = null;
        if (commentsData != null) {
            firstSixMonthAgoIndex = commentsData.getFirstSixMonthAgoIndex();
            comments = commentsData.getData();
        }
        if (firstSixMonthAgoIndex >= 0) {
            firstSixMonthAgoIndex = firstSixMonthAgoIndex + CommonUtil.getCollectionSize(adapter
                    .getComments());
        }
        adapter.setFirstSixMonthAgoIndex(firstSixMonthAgoIndex);
        adapter.addComments(comments);
        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (recyclerView != null) {
                    setShowFooterView();
                }
            }
        }, 120);
    }

    private void setShowFooterView() {
        loadView.setVisibility(View.GONE);
        endView.setVisibility(adapter.getFirstSixMonthAgoIndex() > 0 || CommonUtil
                .isCollectionEmpty(
                adapter.getComments()) ? View.INVISIBLE : View.VISIBLE);
        bottomMoreLayout.setVisibility(adapter.getFirstSixMonthAgoIndex() > 0 ? View.VISIBLE :
                View.GONE);
    }

    @OnClick(R2.id.btn_back)
    void onBack() {
        onBackPressed();
    }

    @OnClick(R2.id.question_layout)
    void onQuestionList() {
        if (!AuthUtil.loginBindCheck(this)) {
            return;
        }
        Intent intent = new Intent(this, AskQuestionListActivity.class);
        intent.putExtra(AskQuestionListActivity.ARG_MERCHANT_ID, merchantId);
        startActivity(intent);
    }

    @OnClick(R2.id.bottom_layout)
    void onChat() {
        if (!AuthUtil.loginBindCheck(this)) {
            return;
        }
        if (carProduct == null || carProduct.getMerchantComment() == null) {
            return;
        }

        WSTrack wsTrack = new WSTrack("发起咨询页");
        TrackWeddingCarProduct trackWeddingCarProduct = new TrackWeddingCarProduct(carProduct);
        wsTrack.setAction(WSTrack.WEDDING_CAR);
        wsTrack.setCarProduct(trackWeddingCarProduct);

        ARouter.getInstance()
                .build(RouterPath.IntentPath.Customer.WsCustomChatActivityPath
                        .WS_CUSTOMER_CHAT_ACTIVITY)
                .withLong(RouterPath.IntentPath.Customer.BaseWsChat.ARG_USER_ID,
                        carProduct.getMerchantComment()
                                .getUserId())
                .withParcelable(RouterPath.IntentPath.Customer.BaseWsChat.ARG_WS_TRACK, wsTrack)
                .withParcelable(RouterPath.IntentPath.Customer.BaseWsChat.ARG_CITY,
                        carProduct.getCity())
                .navigation(this);
    }

    @Override
    public void onCommentFilter(long markId) {
        this.markId = markId;
        CommonUtil.unSubscribeSubs(getCommentsSub);
        getCommentsSub = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpCommentsData>() {
                    @Override
                    public void onNext(HljHttpCommentsData commentsData) {
                        setComments(commentsData);
                    }
                })
                .setOnErrorListener(new SubscriberOnErrorListener() {
                    @Override
                    public void onError(Object o) {
                        setComments(null);
                    }
                })
                .setDataNullable(true)
                .setProgressBar(progressBar)
                .build();
        WeddingCarApi.getMerchantCommentsObb(this, merchantId, markId, 1, HljCommon.PER_PAGE)
                .subscribe(getCommentsSub);
    }

    @Override
    public void onItemClick(
            int position, WeddingCarComment comment) {

    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(initSub, getCommentsSub);
    }

    private class ResultZip extends HljHttpResultZip {
        @HljRZField
        List<Question> questions;
        @HljRZField
        List<CommentMark> marks;
        @HljRZField
        HljHttpCommentsData commentsData;
    }
}
