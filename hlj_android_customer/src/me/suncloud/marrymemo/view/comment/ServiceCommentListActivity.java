package me.suncloud.marrymemo.view.comment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.interfaces.OnFinishedListener;
import com.hunliji.hljcommonlibrary.models.Author;
import com.hunliji.hljcommonlibrary.models.Comment;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.RepliedComment;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.models.ServiceComment;
import com.hunliji.hljcommonlibrary.models.ServiceCommentMark;
import com.hunliji.hljcommonlibrary.models.questionanswer.Question;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.view_tracker.VTMetaData;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.api.CommonApi;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.AuthUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljkefulibrary.moudles.Support;
import com.hunliji.hljnotelibrary.views.activities.PostServiceCommentActivity;
import com.hunliji.hljquestionanswer.activities.AskQuestionListActivity;
import com.hunliji.hljquestionanswer.api.QuestionAnswerApi;
import com.hunliji.hljquestionanswer.models.HljHttpQuestion;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.comment.ServiceCommentListAdapter;
import me.suncloud.marrymemo.adpter.comment.viewholder.ServiceCommentMarksViewHolder;
import me.suncloud.marrymemo.adpter.comment.viewholder.ServiceCommentViewHolder;
import me.suncloud.marrymemo.api.comment.CommentApi;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.model.wrappers.HljHttpCommentsData;
import me.suncloud.marrymemo.util.CustomerSupportUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.merchant.MerchantTogglesUtil;
import me.suncloud.marrymemo.view.chat.WSCustomerChatActivity;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func3;
import rx.schedulers.Schedulers;

/**
 * 商家评论列表
 * Created by chen_bin on 2017/4/26 0026.
 */
public class ServiceCommentListActivity extends HljBaseActivity implements
        ServiceCommentMarksViewHolder.OnCommentFilterListener,
        OnItemClickListener<ServiceComment>, ServiceCommentViewHolder.OnPraiseListener,
        ServiceCommentViewHolder.OnCommentListener {

    @Override
    public String pageTrackTagName() {
        return "商家评论列表";
    }

    @Override
    public VTMetaData pageTrackData() {
        long id = getIntent().getLongExtra(ARG_MERCHANT_ID, 0);
        return new VTMetaData(id, "Merchant");
    }

    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.bottom_layout)
    LinearLayout bottomLayout;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private View bottomMoreLayout;
    private View endView;
    private View loadView;
    private LinearLayoutManager layoutManager;
    private ServiceCommentMarksViewHolder marksViewHolder;
    private Dialog menuDialog;
    private ServiceCommentViewHolder commentViewHolder;
    private ServiceCommentListAdapter adapter;
    private ServiceComment comment;
    private RepliedComment repliedComment;
    private Merchant merchant;
    private String commentContent;// 存储用户输入过的回复内容
    private long markId;
    private long lastUserId;// 存储上一次的userId
    private long merchantId;
    private long merchantUserId;
    private long workId;
    private int currentPage;
    private int pageCount;
    private int position;
    private Subscription rxBusEventSub;
    private HljHttpSubscriber initSub;
    private HljHttpSubscriber getCommentsSub;
    private HljHttpSubscriber praiseSub;
    private HljHttpSubscriber deleteSub;
    private QuestionLayoutViewHolder questionLayoutViewHolder;

    public static final String ARG_MERCHANT = "merchant";
    public static final String ARG_MERCHANT_ID = "merchant_id";
    public static final String ARG_MERCHANT_USER_ID = "merchant_user_id";
    public static final String ARG_WORK_ID = "work_id";
    public static final String ARG_MARK_ID = "mark_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_comment_list);
        ButterKnife.bind(this);
        initValues();
        initViews();
        initLoad();
        registerRxBusEvent();
    }

    private void initValues() {
        merchant = getIntent().getParcelableExtra(ARG_MERCHANT);
        merchantId = getIntent().getLongExtra(ARG_MERCHANT_ID, 0);
        merchantUserId = getIntent().getLongExtra(ARG_MERCHANT_USER_ID, 0);
        markId = getIntent().getLongExtra(ARG_MARK_ID, 0);
        workId = getIntent().getLongExtra(ARG_WORK_ID, 0);
    }

    private void initViews() {
        View questionLayout = View.inflate(this, R.layout.service_comment_question, null);
        questionLayoutViewHolder = new QuestionLayoutViewHolder(questionLayout);
        View footerView = View.inflate(this, R.layout.service_comment_footer_layout, null);
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
        recyclerView.getRefreshableView()
                .setLayoutManager(layoutManager);
        adapter = new ServiceCommentListAdapter(this);
        adapter.setFooterView(footerView);
        adapter.setQuestionLayout(questionLayout);
        adapter.setOnItemClickListener(this);
        adapter.setOnPraiseListener(this);
        adapter.setOnCommentListener(this);
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
                                if (CommonUtil.isCollectionEmpty(adapter.getComments())) {
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
        if (initSub == null || initSub.isUnsubscribed()) {
            Observable<HljHttpQuestion<List<Question>>> questionsObb = QuestionAnswerApi.getQAList(
                    merchantId,
                    1,
                    1);
            Observable<HljHttpData<List<ServiceCommentMark>>> marksObb = CommentApi
                    .getServiceCommentMarksObb(
                    merchantId);
            Observable<HljHttpCommentsData> commentsObb = CommentApi.getMerchantCommentsObb(this,
                    merchantId,
                    markId,
                    1,
                    HljCommon.PER_PAGE);
            Observable<ResultZip> observable = Observable.zip(questionsObb,
                    marksObb,
                    commentsObb,
                    new Func3<HljHttpQuestion<List<Question>>,
                            HljHttpData<List<ServiceCommentMark>>, HljHttpCommentsData,
                            ResultZip>() {
                        @Override
                        public ResultZip call(
                                HljHttpQuestion<List<Question>> questionsData,
                                HljHttpData<List<ServiceCommentMark>> marksData,
                                HljHttpCommentsData commentsData) {
                            ResultZip resultZip = new ResultZip();
                            resultZip.questions = questionsData == null ? null : questionsData
                                    .getData();
                            resultZip.marks = marksData == null ? null : marksData.getData();
                            resultZip.commentsData = commentsData;
                            return resultZip;
                        }
                    });
            initSub = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<ResultZip>() {
                        @Override
                        public void onNext(ResultZip resultZip) {
                            bottomLayout.setVisibility(View.VISIBLE);
                            questionLayoutViewHolder.setQuestions(resultZip.questions);
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
        CommentApi.getMerchantCommentsObb(this, merchantId, markId, 1, HljCommon.PER_PAGE)
                .subscribe(getCommentsSub);
    }

    private class ResultZip {
        List<Question> questions;
        List<ServiceCommentMark> marks;
        HljHttpCommentsData commentsData;
    }

    private void setCommentMarks(List<ServiceCommentMark> marks) {
        if (CommonUtil.isCollectionEmpty(marks)) {
            return;
        }
        View headerView = View.inflate(this, R.layout.service_comment_marks_flow, null);
        marksViewHolder = (ServiceCommentMarksViewHolder) headerView.getTag();
        if (marksViewHolder == null) {
            adapter.setHeaderView(headerView);
            marksViewHolder = new ServiceCommentMarksViewHolder(headerView);
            marksViewHolder.setMarkId(markId);
            marksViewHolder.setShowBottomLineView(true);
            marksViewHolder.setOnCommentFilterListener(this);
            headerView.setTag(marksViewHolder);
        }
        marksViewHolder.setView(this, marks, 0, 0);
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
        CommentApi.getMerchantCommentsObb(this, merchantId, markId, page, HljCommon.PER_PAGE)
                .subscribe(getCommentsSub);
    }

    private void setComments(HljHttpCommentsData commentsData) {
        currentPage = 1;
        pageCount = 0;
        int firstSixMonthAgoIndex = -1;
        List<ServiceComment> comments = null;
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

    private void addComments(HljHttpCommentsData commentsData) {
        currentPage++;
        int firstSixMonthAgoIndex = -1;
        List<ServiceComment> comments = null;
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

    @Override
    public void onItemClick(int position, ServiceComment comment) {
        if (comment != null && comment.getId() > 0) {
            this.position = position;
            this.comment = comment;
            Intent intent = new Intent(this, ServiceCommentDetailActivity.class);
            intent.putExtra("id", comment.getId());
            startActivity(intent);
        }
    }

    @Override
    public void onPraise(
            final ServiceCommentViewHolder commentViewHolder, final ServiceComment comment) {
        if (comment != null && comment.getId() > 0) {
            praiseSub = MerchantTogglesUtil.getInstance()
                    .onServiceOrderCommentPraise(this,
                            comment,
                            commentViewHolder.checkPraised,
                            commentViewHolder.imgPraise,
                            commentViewHolder.tvPraiseCount,
                            praiseSub,
                            new OnFinishedListener() {
                                @Override
                                public void onFinished(Object... objects) {
                                    User user = Session.getInstance()
                                            .getCurrentUser(ServiceCommentListActivity.this);
                                    final Author author = new Author();
                                    author.setId(user.getId());
                                    author.setName(user.getNick());
                                    author.setAvatar(user.getAvatar());
                                    if (!comment.isLike()) {
                                        commentViewHolder.removePraisedUser(comment, author);
                                    } else {
                                        comment.getPraisedUsers()
                                                .add(author);
                                        commentViewHolder.addPraisedUsers(comment);
                                    }
                                }
                            });
        }
    }

    @Override
    public void onComment(
            ServiceCommentViewHolder commentViewHolder,
            ServiceComment comment,
            RepliedComment repliedComment) {
        if (comment == null || comment.getId() == 0) {
            return;
        }
        if (!AuthUtil.loginBindCheck(this)) {
            return;
        }
        if (comment.getRating() <= 2) {
            Intent intent = new Intent(this, ServiceCommentDetailActivity.class);
            intent.putExtra("id", comment.getId());
            startActivity(intent);
            return;
        }
        this.comment = comment;
        this.repliedComment = repliedComment;
        this.commentViewHolder = commentViewHolder;
        User user = Session.getInstance()
                .getCurrentUser(this);
        if (repliedComment != null && repliedComment.getId() > 0 && user.getId() ==
                repliedComment.getUser()
                .getId()) {
            showMenuDialog();
        } else {
            long userId = repliedComment == null ? user.getId() : repliedComment.getUser()
                    .getId();
            if (lastUserId != userId) {
                lastUserId = userId;
                commentContent = "";
            }
            Intent intent = new Intent(this, PostServiceCommentActivity.class);
            intent.putExtra("comment", comment);
            intent.putExtra("comment_content", commentContent);
            intent.putExtra("replied_comment", repliedComment);
            startActivityForResult(intent, Constants.RequestCode.POST_SERVICE_ORDER_COMMENT);
            overridePendingTransition(0, 0);
        }
    }

    private void showMenuDialog() {
        if (menuDialog != null && menuDialog.isShowing()) {
            return;
        }
        if (menuDialog == null) {
            menuDialog = new Dialog(this, R.style.BubbleDialogTheme);
            menuDialog.setContentView(R.layout.dialog_bottom_menu___cm);
            Button btnMenu = menuDialog.findViewById(R.id.btn_menu);
            btnMenu.setText(R.string.label_delete___cm);
            menuDialog.findViewById(R.id.btn_cancel)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            menuDialog.dismiss();
                        }
                    });
            Window win = menuDialog.getWindow();
            if (win != null) {
                WindowManager.LayoutParams params = win.getAttributes();
                params.width = CommonUtil.getDeviceSize(this).x;
                win.setAttributes(params);
                win.setGravity(Gravity.BOTTOM);
                win.setWindowAnimations(R.style.dialog_anim_rise_style);
            }
        }
        menuDialog.findViewById(R.id.btn_menu)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonUtil.unSubscribeSubs(deleteSub);
                        deleteSub = HljHttpSubscriber.buildSubscriber(ServiceCommentListActivity
                                .this)
                                .setOnNextListener(new SubscriberOnNextListener() {
                                    @Override
                                    public void onNext(Object o) {
                                        comment.getRepliedComments()
                                                .remove(repliedComment);
                                        if (commentViewHolder != null) {
                                            commentViewHolder.addComments
                                                    (ServiceCommentListActivity.this,
                                                    comment);
                                        }
                                    }
                                })
                                .setDataNullable(true)
                                .setProgressDialog(DialogUtil.createProgressDialog(
                                        ServiceCommentListActivity.this))
                                .build();
                        CommonApi.deleteFuncObb(repliedComment.getId())
                                .subscribe(deleteSub);
                    }
                });
        menuDialog.show();
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

    private void setShowFooterView() {
        loadView.setVisibility(View.GONE);
        endView.setVisibility(adapter.getFirstSixMonthAgoIndex() > 0 || CommonUtil
                .isCollectionEmpty(
                adapter.getComments()) ? View.INVISIBLE : View.VISIBLE);
        bottomMoreLayout.setVisibility(adapter.getFirstSixMonthAgoIndex() > 0 ? View.VISIBLE :
                View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Constants.RequestCode.POST_SERVICE_ORDER_COMMENT:
                    if (data == null || comment == null || comment.getId() == 0) {
                        return;
                    }
                    RepliedComment repliedComment = data.getParcelableExtra("comment_response");
                    if (repliedComment == null) {
                        commentContent = data.getStringExtra("comment_content");
                    } else {
                        commentContent = "";
                        User user = Session.getInstance()
                                .getCurrentUser(this);
                        Author author = new Author();
                        author.setId(user.getId());
                        author.setName(user.getNick());
                        repliedComment.setUser(author);
                        comment.getRepliedComments()
                                .add(repliedComment);
                        if (commentViewHolder != null && comment.isRepliesExpanded()) {
                            commentViewHolder.addComments(this, comment);
                        }
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void registerRxBusEvent() {
        if (rxBusEventSub == null || rxBusEventSub.isUnsubscribed()) {
            rxBusEventSub = RxBus.getDefault()
                    .toObservable(RxEvent.class)
                    .subscribe(new RxBusSubscriber<RxEvent>() {
                        @Override
                        protected void onEvent(RxEvent rxEvent) {
                            switch (rxEvent.getType()) {
                                case MEASURE_KEYBOARD_HEIGHT:
                                    if (commentViewHolder != null) {
                                        int offset = getScrollPositionOffset((int) rxEvent
                                                .getObject());
                                        layoutManager.scrollToPositionWithOffset
                                                (commentViewHolder.getAdapterPosition(),
                                                offset);
                                    }
                                    break;
                                case EDIT_COMMENT_SUCCESS:
                                    Comment c = (Comment) rxEvent.getObject();
                                    if (comment != null && c != null && comment.getId() == c
                                            .getId()) {
                                        comment.setContent(c.getContent());
                                        comment.setRating(c.getRating());
                                        comment.setPhotos(c.getPhotos());
                                        adapter.notifyItemChanged(position);
                                    }
                                    break;
                            }
                        }
                    });
        }
    }

    //获取偏移量
    private int getScrollPositionOffset(int keyboardHeight) {
        int offset = recyclerView.getMeasuredHeight() + bottomLayout.getMeasuredHeight() -
                commentViewHolder.itemView.getMeasuredHeight() - keyboardHeight;
        if (repliedComment != null) {
            offset = offset + commentViewHolder.commentLayout.getMeasuredHeight() + CommonUtil
                    .dp2px(
                    this,
                    8);
            for (int i = 0, size = comment.getRepliedComments()
                    .indexOf(repliedComment); i <= size; i++) {
                offset = offset - commentViewHolder.commentListLayout.getChildAt(i)
                        .getMeasuredHeight();
            }
        }
        return offset;
    }

    /**
     * 评价列表 问大家
     */
    public class QuestionLayoutViewHolder {

        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.question_layout)
        LinearLayout questionLayout;

        public QuestionLayoutViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        @OnClick(R.id.question_layout)
        void onQuestionList() {
            Intent intent = new Intent(ServiceCommentListActivity.this,
                    AskQuestionListActivity.class);
            intent.putExtra(AskQuestionListActivity.ARG_MERCHANT_ID, merchantId);
            intent.putExtra(AskQuestionListActivity.ARG_WORK_ID, workId);
            startActivity(intent);
        }

        private void setQuestions(List<Question> questions) {
            questionLayout.setVisibility(View.VISIBLE);
            if (CommonUtil.isCollectionEmpty(questions)) {
                tvTitle.setText(getString(R.string.hint_answer_content_empty___qa) + "~");
            } else {
                tvTitle.setText(questions.get(0)
                        .getTitle());
            }
        }
    }

    @OnClick(R.id.bottom_layout)
    void onChat() {
        if (!AuthUtil.loginBindCheck(this)) {
            return;
        }
        if (merchant == null) {
            Intent intent = new Intent(this, WSCustomerChatActivity.class);
            intent.putExtra("id", merchantUserId);
            startActivity(intent);
        } else if (merchant.getShopType() == Merchant.SHOP_TYPE_HOTEL) {
            CustomerSupportUtil.goToSupport(this, Support.SUPPORT_KIND_HOTEL, merchant);
        } else {
            Intent intent = new Intent(this, WSCustomerChatActivity.class);
            intent.putExtra("user", merchant.toUser());
            if (!CommonUtil.isCollectionEmpty(merchant.getContactPhone())) {
                intent.putStringArrayListExtra("contact_phones", merchant.getContactPhone());
            }
            startActivity(intent);
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(rxBusEventSub, initSub, getCommentsSub, deleteSub);
    }
}