package com.hunliji.marrybiz.view.comment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.models.Author;
import com.hunliji.hljcommonlibrary.models.RepliedComment;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.models.ServiceComment;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.entities.HljHttpStatus;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljnotelibrary.views.activities.PostServiceCommentActivity;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.adapter.comment.CommentDetailAdapter;
import com.hunliji.marrybiz.adapter.comment.viewholder.CommentDetailHeaderViewHolder;
import com.hunliji.marrybiz.api.comment.CommentApi;
import com.hunliji.marrybiz.model.MerchantUser;
import com.hunliji.marrybiz.model.comment.SubmitAppealBody;
import com.hunliji.marrybiz.util.Session;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;

/**
 * Created by hua_rong on 2017/4/17.
 * 评论管理---评论详情
 */

public class CommentDetailActivity extends HljBaseActivity implements
        PullToRefreshVerticalRecyclerView.OnRefreshListener, CommentDetailAdapter
        .OnCommentListener {
    private static final int COMMENT_REQUEST = 100;//底部的回复
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView pullToRefreshVerticalRecyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.ll_bottom)
    LinearLayout llBottom;
    @BindView(R.id.root_item)
    TextView item;
    private View headerView;
    private String commentContent;
    private long orderCommentId;
    private long lastUserId;
    private HljHttpSubscriber refreshSubscriber;
    private CommentDetailHeaderViewHolder viewHolder;
    private List<RepliedComment> replyList;
    private CommentDetailAdapter adapter;
    private ServiceComment comment;
    private MerchantUser merchantUser;
    private boolean isReplyComment;
    private HljHttpSubscriber delSubscriber;
    private Context context;
    private static final int COMPLAIN_CODE = 66;
    private HljHttpSubscriber checkSubscriber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_detail);
        ButterKnife.bind(this);
        context = this;
        merchantUser = Session.getInstance()
                .getCurrentUser(this);
        replyList = new ArrayList<>();
        orderCommentId = getIntent().getLongExtra("order_comment_id", 0);
        isReplyComment = getIntent().getBooleanExtra("is_reply_comment", false);//自动弹出回复键盘
        initHeaderView();
        RecyclerView recyclerView = pullToRefreshVerticalRecyclerView.getRefreshableView();
        pullToRefreshVerticalRecyclerView.setOnRefreshListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new CommentDetailAdapter(this, replyList);
        adapter.setHeaderView(headerView);
        adapter.setOnReplyUserListener(this);
        recyclerView.setAdapter(adapter);
        onRefresh(pullToRefreshVerticalRecyclerView);
        onError();
    }

    private static Handler mHandler = new Handler();

    private void onError() {
        emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                onRefresh(pullToRefreshVerticalRecyclerView);
            }
        });
    }

    private void initHeaderView() {
        headerView = View.inflate(this, R.layout.header_comment_detail, null);
        viewHolder = new CommentDetailHeaderViewHolder(headerView);
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        if (refreshSubscriber == null || refreshSubscriber.isUnsubscribed()) {
            Observable<HljHttpResult<ServiceComment>> observable = CommentApi
                    .getOrderCommentDetail(
                    orderCommentId);
            refreshSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpResult<ServiceComment>>() {

                        @Override
                        public void onNext(HljHttpResult<ServiceComment> httpResult) {
                            final HljHttpStatus httpStatus = httpResult.getStatus();
                            int retCode = httpStatus.getRetCode();
                            if (retCode == 0) {
                                ServiceComment comment = httpResult.getData();
                                llBottom.setVisibility(View.VISIBLE);
                                if (comment != null) {
                                    showCommentDetail(comment);
                                }
                            } else if (retCode == 1001) {
                                Toast.makeText(context.getApplicationContext(),
                                        httpStatus.getMsg(),
                                        Toast.LENGTH_SHORT)
                                        .show();
                                mHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        onBackPressed();
                                    }
                                }, 1000);
                            }
                        }
                    })
                    .setDataNullable(true)
                    .setProgressBar(refreshView.isRefreshing() ? null : progressBar)
                    .setContentView(refreshView)
                    .setPullToRefreshBase(refreshView)
                    .build();
            observable.subscribe(refreshSubscriber);
        }
    }

    @OnClick(R.id.btn_reply)
    public void onReply() {
        onReplyUser(null);
    }

    @Override
    public void onReplyUser(RepliedComment repliedComment) {
        long userId = repliedComment == null ? merchantUser.getUserId() : repliedComment.getUser()
                .getId();
        if (lastUserId != userId) {
            lastUserId = userId;
            commentContent = "";
        }
        Intent intent = new Intent(this, PostServiceCommentActivity.class);
        intent.putExtra("comment", comment);
        intent.putExtra("is_merchant", true);
        intent.putExtra("replied_comment", repliedComment);
        intent.putExtra("comment_content", commentContent);
        startActivityForResult(intent, COMMENT_REQUEST);
        overridePendingTransition(0, 0);
    }

    @Override
    public void onDeleteClick(final int position, final RepliedComment reply) {
        DialogUtil.showBottomDialog(context,
                context.getString(R.string.label_opu_delete),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (delSubscriber == null || delSubscriber.isUnsubscribed()) {
                            Observable<Object> observable = CommentApi.delComment(reply.getId());
                            delSubscriber = HljHttpSubscriber.buildSubscriber(context)
                                    .setOnNextListener(new SubscriberOnNextListener() {

                                        @Override
                                        public void onNext(Object o) {
                                            if (progressBar != null) {
                                                progressBar.setVisibility(View.GONE);
                                            }
                                            if (position < replyList.size()) {
                                                replyList.remove(position);
                                                adapter.setReplyList(replyList);
                                                viewHolder.showOrHideHeaderBottom(replyList);
                                                adapter.notifyDataSetChanged();
                                                ToastUtil.showCustomToast(context,
                                                        R.string.msg_delete_success);
                                                delSubscriber = null;
                                                RxBus.getDefault()
                                                        .post(new RxEvent(RxEvent.RxEventType
                                                                .COMMENT_DETAIL_REPLY_OR_DELETE_SUCCESS,
                                                                null));
                                            }
                                        }
                                    })
                                    .setProgressBar(progressBar)
                                    .build();
                            if (delSubscriber != null) {
                                observable.subscribe(delSubscriber);
                            }
                        }
                    }
                })
                .show();
    }

    @Override
    public void onComplainClick(final int position, final RepliedComment reply, final int type) {
        CommonUtil.unSubscribeSubs(checkSubscriber);
        Observable<HljHttpResult> observable = CommentApi.CheckCommunityAppeal(reply.getId());
        checkSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpResult>() {

                    @Override
                    public void onNext(HljHttpResult hljHttpResult) {
                        if (hljHttpResult != null) {
                            HljHttpStatus httpStatus = hljHttpResult.getStatus();
                            if (httpStatus != null) {
                                int retCode = httpStatus.getRetCode();
                                if (retCode == 0) {
                                    goComplainDialog(position, reply, type);
                                } else {
                                    Toast.makeText(context, httpStatus.getMsg(), Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }
                        }
                    }
                })
                .setDataNullable(true)
                .build();
        observable.subscribe(checkSubscriber);
    }

    private void goComplainDialog(final int position, final RepliedComment reply, final int type) {
        DialogUtil.showBottomDialog(context,
                context.getString(R.string.title_activity_complaint),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ComplaintActivity.class);
                        if (type == SubmitAppealBody.TYPE_ORDER) {
                            intent.putExtra("id", orderCommentId);
                        } else {
                            intent.putExtra("id", reply.getId());
                            intent.putExtra("position", position);
                        }
                        intent.putExtra("type", type);
                        startActivityForResult(intent, COMPLAIN_CODE);
                        overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    }
                })
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == COMMENT_REQUEST) {
                RepliedComment repliedComment = data.getParcelableExtra("comment_response");
                if (repliedComment == null) {
                    commentContent = data.getStringExtra("comment_content");
                } else {
                    commentContent = "";
                    Author author = new Author();
                    author.setMerchant(true);
                    author.setAvatar(merchantUser.getAvatar());
                    author.setId(merchantUser.getUserId());
                    author.setName(merchantUser.getName());
                    repliedComment.setUser(author);
                    replyList.add(repliedComment);
                    viewHolder.showOrHideHeaderBottom(replyList);
                    adapter.setReplyList(replyList);
                    adapter.notifyDataSetChanged();
                    RxBus.getDefault()
                            .post(new RxEvent(RxEvent.RxEventType
                                    .COMMENT_DETAIL_REPLY_OR_DELETE_SUCCESS,
                                    null));
                }
            } else if (requestCode == COMPLAIN_CODE) {
                int status = data.getIntExtra("type", -1);
                if (status == SubmitAppealBody.TYPE_ORDER) {
                    comment.setAppealStatus(0);
                    okTextStatus(0);
                } else if (status == SubmitAppealBody.TYPE_COMMENT) {
                    int position = data.getIntExtra("position", -1);
                    if (position != -1) {
                        RepliedComment repliedComment = replyList.get(position);
                        repliedComment.setAppealStatus(0);
                        adapter.setReplyList(replyList);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        }
    }

    private void okTextStatus(int status) {
        item.setVisibility(View.VISIBLE);
        if (status == 0) {
            item.setText(R.string.label_complain_under_review);
            item.setTextColor(ContextCompat.getColor(context, R.color.colorGray));
            item.setClickable(false);
        } else {
            item.setText(R.string.label_initiate_complaint);
            item.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goComplainDialog(-1, null, SubmitAppealBody.TYPE_ORDER);
                }
            });
        }
    }

    private void showCommentDetail(ServiceComment comment) {
        this.comment = comment;
        if (comment != null) {
            int status = comment.getAppealStatus();
            okTextStatus(status);
            if (viewHolder != null && replyList != null) {
                viewHolder.setHeaderView(comment);
                replyList.clear();
                List<RepliedComment> comments = comment.getRepliedComments();
                if (comments != null) {
                    replyList.addAll(comments);
                    viewHolder.showOrHideHeaderBottom(replyList);
                    adapter.setReplyList(replyList);
                    adapter.notifyDataSetChanged();
                    if (isReplyComment) {
                        isReplyComment = false;
                        onReply();//自动弹出回复键盘
                    }
                }
            }
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(refreshSubscriber, delSubscriber);
    }

}
