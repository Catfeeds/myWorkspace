package me.suncloud.marrymemo.view.comment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.interfaces.OnFinishedListener;
import com.hunliji.hljcommonlibrary.models.Author;
import com.hunliji.hljcommonlibrary.models.RepliedComment;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.models.ServiceComment;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.view_tracker.VTMetaData;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearLayout;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.api.CommonApi;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.entities.HljHttpStatus;
import com.hunliji.hljhttplibrary.utils.AuthUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljnotelibrary.views.activities.PostServiceCommentActivity;
import com.hunliji.hljsharelibrary.HljShare;
import com.hunliji.hljsharelibrary.utils.ShareDialogUtil;
import com.hunliji.hljtrackerlibrary.HljTracker;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.comment.ServiceRepliedCommentListAdapter;
import me.suncloud.marrymemo.adpter.comment.viewholder.ServiceCommentDetailHeaderViewHolder;
import me.suncloud.marrymemo.adpter.comment.viewholder.ServiceRepliedCommentViewHolder;
import me.suncloud.marrymemo.api.comment.CommentApi;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.merchant.MerchantTogglesUtil;
import rx.Subscription;

/**
 * 服务评价详情
 * Created by chen_bin on 2017/4/15 0015.
 */
public class ServiceCommentDetailActivity extends HljBaseActivity implements
        ServiceCommentDetailHeaderViewHolder.OnMenuListener, ServiceRepliedCommentViewHolder
        .OnRepliedCommentListener {
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.img_praise)
    ImageView imgPraise;
    @BindView(R.id.tv_praise_count)
    TextView tvPraiseCount;
    @BindView(R.id.check_praised)
    CheckableLinearLayout checkPraised;
    @BindView(R.id.bottom_layout)
    LinearLayout bottomLayout;

    private View headerView;
    private Dialog menuDialog;
    private LinearLayoutManager layoutManager;
    private ServiceCommentDetailHeaderViewHolder headerViewHolder;
    private ServiceRepliedCommentViewHolder repliedCommentViewHolder;
    private ServiceRepliedCommentListAdapter adapter;
    private ServiceComment comment;
    private String commentContent;
    private boolean isShowEmoji;
    private long lastUserId;// 存储上一次的回复id
    private long id;
    private Subscription rxBusEventSub;
    private HljHttpSubscriber initSub;
    private HljHttpSubscriber deleteSub;
    private HljHttpSubscriber praiseSub;

    private Handler shareCallbackHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case HljShare.RequestCode.SHARE_TO_QQ:
                case HljShare.RequestCode.SHARE_TO_WEIBO:
                case HljShare.RequestCode.SHARE_TO_QQZONE:
                case HljShare.RequestCode.SHARE_TO_PENGYOU:
                case HljShare.RequestCode.SHARE_TO_WEIXIN:
                case HljShare.RequestCode.SHARE_TO_TXWEIBO:
                    new HljTracker.Builder(ServiceCommentDetailActivity.this).eventableId(id)
                            .eventableType("MerchantComment")
                            .action("share")
                            .additional(HljShare.getShareTypeName(msg.what))
                            .build()
                            .add();
                    break;
            }
            return false;
        }
    });

    @Override
    public String pageTrackTagName() {
        return "评价详情";
    }

    @Override
    public VTMetaData pageTrackData() {
        long id = getIntent().getLongExtra("id", 0);
        return new VTMetaData(id, "MerchantComment");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_comment_detail);
        ButterKnife.bind(this);
        initValues();
        initViews();
        initLoad();
        registerRxBusEvent();
    }

    private void initValues() {
        id = getIntent().getLongExtra("id", 0);
    }

    private void initViews() {
        emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                initLoad();
            }
        });
        headerView = View.inflate(this, R.layout.service_comment_detail_header_item, null);
        ((SimpleItemAnimator) recyclerView.getRefreshableView()
                .getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setMode(PullToRefreshBase.Mode.DISABLED);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.getRefreshableView()
                .setLayoutManager(layoutManager);
        adapter = new ServiceRepliedCommentListAdapter(this);
        adapter.setOnRepliedCommentListener(this);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
    }

    private void initLoad() {
        if (initSub == null || initSub.isUnsubscribed()) {
            initSub = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpResult<ServiceComment>>() {

                        @Override
                        public void onNext(
                                HljHttpResult<ServiceComment> commentHljHttpResult) {
                            setData(commentHljHttpResult);
                        }
                    })
                    .setEmptyView(emptyView)
                    .setContentView(recyclerView)
                    .setPullToRefreshBase(recyclerView)
                    .setProgressBar(recyclerView.isRefreshing() ? null : progressBar)
                    .build();
            CommentApi.getServiceCommentDetailObb(id)
                    .subscribe(initSub);
        }
    }

    private void setData(HljHttpResult<ServiceComment> commentHljHttpResult) {
        HljHttpStatus hljHttpStatus = commentHljHttpResult.getStatus();
        int retCode = hljHttpStatus == null ? -1 : hljHttpStatus.getRetCode();
        if (retCode != 0) {
            ToastUtil.showToast(this, hljHttpStatus == null ? null : hljHttpStatus.getMsg(), 0);
            if (retCode == 1001) { //评论被隐藏
                onBackPressed();
            }
            emptyView.showEmptyView();
            recyclerView.setVisibility(View.GONE);
            return;
        }
        if (commentHljHttpResult.getData() == null || commentHljHttpResult.getData()
                .getId() == 0) {
            emptyView.showEmptyView();
            recyclerView.setVisibility(View.GONE);
            return;
        }
        recyclerView.getRefreshableView()
                .scrollToPosition(0);
        bottomLayout.setVisibility(View.VISIBLE);
        comment = commentHljHttpResult.getData();
        if (comment.getShareInfo() != null) {
            setOkButton(R.mipmap.icon_share_primary_44_44);
        }
        checkPraised.setChecked(comment.isLike());
        tvPraiseCount.setText(comment.getLikesCount() <= 0 ? getString(R.string.label_be_of_use)
                : String.valueOf(
                comment.getLikesCount()));
        headerViewHolder = (ServiceCommentDetailHeaderViewHolder) headerView.getTag();
        if (headerViewHolder == null) {
            adapter.setHeaderView(headerView);
            headerViewHolder = new ServiceCommentDetailHeaderViewHolder(headerView);
            headerViewHolder.setOnMenuListener(this);
            headerView.setTag(headerViewHolder);
        }
        User user = Session.getInstance()
                .getCurrentUser(this);
        headerViewHolder.setShowMenu(user != null && user.getId() > 0 && user.getId() == comment
                .getAuthor()
                .getId());
        headerViewHolder.setView(this, comment, 0, 0);
        adapter.setRepliedComments(comment.getRepliedComments());
    }

    @OnClick({R.id.comment_layout, R.id.btn_add_emoji})
    void onComment(View v) {
        isShowEmoji = v.getId() == R.id.btn_add_emoji;
        onComment(null, null);
    }

    @OnClick(R.id.check_praised)
    void onPraise() {
        if (comment != null && comment.getId() > 0) {
            praiseSub = MerchantTogglesUtil.getInstance()
                    .onServiceOrderCommentPraise(this,
                            comment,
                            checkPraised,
                            imgPraise,
                            tvPraiseCount,
                            praiseSub,
                            new OnFinishedListener() {
                                @Override
                                public void onFinished(Object... objects) {
                                    if (headerViewHolder == null) {
                                        return;
                                    }
                                    User user = Session.getInstance()
                                            .getCurrentUser(ServiceCommentDetailActivity.this);
                                    final Author author = new Author();
                                    author.setId(user.getId());
                                    author.setName(user.getNick());
                                    author.setAvatar(user.getAvatar());
                                    if (!comment.isLike()) {
                                        headerViewHolder.removePraisedUser(
                                                ServiceCommentDetailActivity.this,
                                                comment,
                                                author);
                                    } else {
                                        comment.getPraisedUsers()
                                                .add(author);
                                        headerViewHolder.addPraisedUsers(
                                                ServiceCommentDetailActivity
                                                        .this,
                                                comment);
                                    }
                                }
                            });
        }
    }

    @Override
    public void onOkButtonClick() {
        if (comment != null && comment.getShareInfo() != null) {
            ShareDialogUtil.onCommonShare(this, comment.getShareInfo(), shareCallbackHandler);
        }
    }

    @Override
    public void onMenu(int position, final ServiceComment comment) {
        if (isShowMenuDialog()) {
            return;
        }
        Button btnMenu = menuDialog.findViewById(R.id.btn_menu);
        btnMenu.setText(R.string.label_edit);
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuDialog.dismiss();
                Intent intent = new Intent(v.getContext(), CommentServiceActivity.class);
                intent.putExtra(CommentServiceActivity.ARG_COMMENT, comment);
                startActivity(intent);
            }
        });
        menuDialog.show();
    }

    @Override
    public void onComment(
            ServiceRepliedCommentViewHolder repliedCommentViewHolder,
            RepliedComment repliedComment) {
        if (!AuthUtil.loginBindCheck(this)) {
            return;
        }
        this.repliedCommentViewHolder = repliedCommentViewHolder;
        User user = Session.getInstance()
                .getCurrentUser(this);
        if (repliedComment == null || user.getId() != repliedComment.getUser()
                .getId()) {
            replyRepliedComment(user, repliedComment);
        } else {
            deleteRepliedComment(repliedComment);
        }
    }

    private void deleteRepliedComment(final RepliedComment repliedComment) {
        if (isShowMenuDialog()) {
            return;
        }
        Button btnMenu = menuDialog.findViewById(R.id.btn_menu);
        btnMenu.setText(R.string.label_delete___cm);
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuDialog.dismiss();
                CommonUtil.unSubscribeSubs(deleteSub);
                deleteSub = HljHttpSubscriber.buildSubscriber(ServiceCommentDetailActivity
                        .this)
                        .setOnNextListener(new SubscriberOnNextListener() {
                            @Override
                            public void onNext(Object o) {
                                comment.getRepliedComments()
                                        .remove(repliedComment);
                                adapter.notifyDataSetChanged();
                                if (headerViewHolder != null) {
                                    headerViewHolder.setCommentCount(comment);
                                }
                            }
                        })
                        .setDataNullable(true)
                        .setProgressDialog(DialogUtil.createProgressDialog(
                                ServiceCommentDetailActivity.this))
                        .build();
                CommonApi.deleteFuncObb(repliedComment.getId())
                        .subscribe(deleteSub);
            }
        });
    }

    private void replyRepliedComment(User user, RepliedComment repliedComment) {
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
        if (isShowEmoji) {
            isShowEmoji = false;
            intent.putExtra("is_show_emoji", true);
        }
        startActivityForResult(intent, Constants.RequestCode.POST_SERVICE_ORDER_COMMENT);
        overridePendingTransition(0, 0);
    }

    private boolean isShowMenuDialog() {
        if (menuDialog != null && menuDialog.isShowing()) {
            return true;
        }
        if (menuDialog == null) {
            menuDialog = new Dialog(this, R.style.BubbleDialogTheme);
            menuDialog.setContentView(R.layout.dialog_bottom_menu___cm);
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
        menuDialog.show();
        return false;
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
                                    if (repliedCommentViewHolder != null) {
                                        int offset = getScrollPositionOffset((int) rxEvent
                                                .getObject());
                                        layoutManager.scrollToPositionWithOffset(
                                                repliedCommentViewHolder.getAdapterPosition(),
                                                offset);
                                    }
                                    break;
                                case EDIT_COMMENT_SUCCESS:
                                    if (headerViewHolder == null) {
                                        return;
                                    }
                                    ServiceComment c = (ServiceComment) rxEvent.getObject();
                                    if (comment == null || c == null || comment.getId() != c
                                            .getId()) {
                                        return;
                                    }
                                    comment.setPhotos(c.getPhotos());
                                    comment.setRating(c.getRating());
                                    comment.setContent(c.getContent());
                                    headerViewHolder.setView(ServiceCommentDetailActivity.this,
                                            comment,
                                            0,
                                            0);
                                    break;
                            }
                        }
                    });
        }
    }

    //获取偏移量
    private int getScrollPositionOffset(int keyboardHeight) {
        return recyclerView.getMeasuredHeight() + bottomLayout.getMeasuredHeight() -
                repliedCommentViewHolder.itemView.getMeasuredHeight() - keyboardHeight;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case HljShare.RequestCode.SHARE_TO_TXWEIBO:
                case HljShare.RequestCode.SHARE_TO_WEIBO:
                    shareCallbackHandler.sendEmptyMessage(requestCode);
                    break;
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
                        author.setAvatar(user.getAvatar());
                        author.setName(user.getNick());
                        repliedComment.setUser(author);
                        comment.getRepliedComments()
                                .add(repliedComment);
                        adapter.notifyDataSetChanged();
                        if (headerViewHolder != null) {
                            headerViewHolder.setCommentCount(comment);
                        }
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(initSub, deleteSub, praiseSub);
    }
}