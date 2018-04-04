package com.hunliji.hljnotelibrary.views.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.models.RepliedComment;
import com.hunliji.hljcommonlibrary.models.customer.CustomerUser;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.api.CommonApi;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResultZip;
import com.hunliji.hljhttplibrary.entities.HljRZField;
import com.hunliji.hljhttplibrary.utils.AuthUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;
import com.hunliji.hljnotelibrary.HljNote;
import com.hunliji.hljnotelibrary.R;
import com.hunliji.hljnotelibrary.R2;
import com.hunliji.hljnotelibrary.adapters.NoteCommentRecyclerAdapter;
import com.hunliji.hljnotelibrary.api.NoteApi;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * Created by mo_yu on 2017/7/12.笔记和笔记本的评论列表页
 */

public class NoteCommentListActivity extends HljBaseActivity implements
        NoteCommentRecyclerAdapter.OnCommentReplyListener, PullToRefreshBase
        .OnRefreshListener<RecyclerView> {

    public static final int NOTE_COMMENT = 1;
    @BindView(R2.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R2.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R2.id.btn_scroll_top)
    ImageButton btnScrollTop;
    @BindView(R2.id.tv_content)
    TextView tvContent;
    @BindView(R2.id.btn_add_emoji)
    ImageView btnAddEmoji;
    @BindView(R2.id.bottom_layout)
    LinearLayout bottomLayout;

    private ArrayList<RepliedComment> comments;
    private NoteCommentRecyclerAdapter adapter;
    private FooterViewHolder footerViewHolder;
    private String entityType;
    private long id;
    private String commentContent; // 存储用户输入过的回复内容
    private long lastReplyId; // 存储上一次的回复id
    private int notebookType;

    private Dialog menuDialog;
    private Button btnMenu;
    private HljHttpSubscriber deleteSubscriber;
    private HljHttpSubscriber refreshSubscriber;
    private HljHttpSubscriber pageSubscriber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_comment_list___note);
        ButterKnife.bind(this);
        initValue();
        initView();
        onRefresh(null);
    }

    private void initValue() {
        comments = new ArrayList<>();
        id = getIntent().getLongExtra("id", 0);
        entityType = getIntent().getStringExtra("entity_type");
        notebookType = getIntent().getIntExtra("notebook_type", 1);
        if (CommonUtil.isEmpty(entityType)) {
            entityType = HljNote.NOTE_TYPE;
        }
    }

    private void initView() {
        emptyView.setHintId(R.string.hint_topic_comment_empty___cm);
        emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                onRefresh(null);
            }
        });
        View footerView = View.inflate(this, R.layout.hlj_foot_no_more___cm, null);
        footerViewHolder = new FooterViewHolder(footerView);
        ((SimpleItemAnimator) recyclerView.getRefreshableView()
                .getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.getRefreshableView()
                .setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNeedChangeSize(false);
        recyclerView.setOnRefreshListener(this);
        adapter = new NoteCommentRecyclerAdapter(this, comments);
        adapter.setFooterView(footerView);
        adapter.setEntityType(entityType);
        adapter.setOnCommentReplyListener(this);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
        if (entityType.equals(HljNote.NOTE_BOOK_TYPE)) {
            bottomLayout.setVisibility(View.GONE);
        } else {
            bottomLayout.setVisibility(View.VISIBLE);
            tvContent.setHint(getCommentHeaderHint(notebookType));
        }
    }

    //notebookType 1婚纱照2婚礼筹备3婚品筹备4婚礼人
    public String getCommentHeaderHint(int notebookType) {
        if (HljNote.isMerchant(this)) {
            return getString(R.string.hint_comment_something___note);
        }
        switch (notebookType) {
            case 2:
                return getString(R.string.fmt_hint_comment_header___note, "婚礼筹备经验");
            case 3:
                return getString(R.string.fmt_hint_comment_header___note, "婚品筹备经验");
            case 4:
                return getString(R.string.fmt_hint_comment_header___note, "服务内容");
            default:
                return getString(R.string.fmt_hint_comment_header___note, "拍摄经验");
        }
    }

    private void showMenuDialog(final RepliedComment comment) {
        if (menuDialog != null && menuDialog.isShowing()) {
            return;
        }
        if (menuDialog == null) {
            menuDialog = new Dialog(this, R.style.BubbleDialogTheme);
            menuDialog.setContentView(R.layout.dialog_bottom_menu___cm);
            btnMenu = (Button) menuDialog.findViewById(R.id.btn_menu);
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
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                menuDialog.dismiss();
                CommonUtil.unSubscribeSubs(deleteSubscriber);
                if (deleteSubscriber == null || deleteSubscriber.isUnsubscribed()) {
                    deleteSubscriber = HljHttpSubscriber.buildSubscriber(NoteCommentListActivity
                            .this)
                            .setOnNextListener(new SubscriberOnNextListener() {
                                @Override
                                public void onNext(Object o) {
                                    ToastUtil.showCustomToast(NoteCommentListActivity.this,
                                            R.string.msg_delete_success___cm);
                                    comments.remove(comment);
                                    adapter.notifyDataSetChanged();
                                }
                            })
                            .setProgressDialog(DialogUtil.createProgressDialog(
                                    NoteCommentListActivity.this))
                            .build();
                    CommonApi.deleteFuncObb(comment.getId())
                            .subscribe(deleteSubscriber);
                }
            }
        });
        menuDialog.show();
    }

    @Override
    public void onCommentItemClick(RepliedComment repliedComment) {
        if (entityType.equals(HljNote.NOTE_BOOK_TYPE)) {
            return;
        }
        CustomerUser user = (CustomerUser) UserSession.getInstance()
                .getUser(this);
        if (user == null || repliedComment.getUser() == null) {
            return;
        }
        if (user.getId() != repliedComment.getUser()
                .getId()) {
            onComment(repliedComment, false, null);
        } else {
            showMenuDialog(repliedComment);
        }
    }

    public void onComment(
            RepliedComment repliedComment, boolean isShowEmoji, String commentContent) {
        if (!AuthUtil.loginBindCheck(this)) {
            return;
        }
        long replyId = repliedComment == null ? 0 : repliedComment.getId();
        if (lastReplyId != replyId) {
            lastReplyId = replyId;
            commentContent = "";
        }
        if (id != 0 || !entityType.equals(HljNote.NOTE_BOOK_TYPE)) {
            Intent intent = new Intent(this, PostNoteCommentActivity.class);
            intent.putExtra("id", id);
            if (repliedComment != null) {
                intent.putExtra("replied_comment", repliedComment);
            }
            intent.putExtra("is_show_emoji", isShowEmoji);
            if (!TextUtils.isEmpty(commentContent)) {
                intent.putExtra("comment_content", commentContent);
            }
            intent.putExtra("hint_content", getCommentHeaderHint(notebookType));
            intent.putExtra("entity_type", entityType);
            startActivityForResult(intent, NOTE_COMMENT);
            overridePendingTransition(0, NOTE_COMMENT);
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        if (refreshSubscriber == null || refreshSubscriber.isUnsubscribed()) {
            if (entityType.equals(HljNote.NOTE_TYPE)) {
                refreshNoteComments();
            } else {
                refreshNotebookComments();
            }
        }
    }

    private void refreshNoteComments() {
        //最热评论
        Observable<HljHttpData<List<RepliedComment>>> hotCommentsObb = CommonApi.getHotCommentsObb(
                id,
                entityType);
        //最新评论
        Observable<HljHttpData<List<RepliedComment>>> newCommentsObb = CommonApi
                .getCommonCommentsObb(
                id,
                entityType,
                1);
        Observable<ResultZip> observable = Observable.zip(hotCommentsObb,
                newCommentsObb,
                new Func2<HljHttpData<List<RepliedComment>>, HljHttpData<List<RepliedComment>>,
                        ResultZip>() {
                    @Override
                    public ResultZip call(
                            HljHttpData<List<RepliedComment>> listHljHttpData,
                            HljHttpData<List<RepliedComment>> listHljHttpData2) {
                        ResultZip zip = new ResultZip();
                        zip.hotCommentsData = listHljHttpData;
                        zip.newCommentsData = listHljHttpData2;
                        return zip;
                    }
                });
        refreshSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<ResultZip>() {
                    @Override
                    public void onNext(ResultZip resultZip) {
                        recyclerView.getRefreshableView()
                                .scrollToPosition(0);
                        comments.clear();
                        if (resultZip.hotCommentsData != null && resultZip.hotCommentsData
                                .getData() != null) {
                            comments.addAll(resultZip.hotCommentsData.getData());
                        }
                        if (resultZip.newCommentsData != null && !CommonUtil.isCollectionEmpty(
                                resultZip.newCommentsData.getData())) {
                            comments.addAll(resultZip.newCommentsData.getData());
                            if (resultZip.newCommentsData.getPageCount() > 1) {
                                initPagination(resultZip.newCommentsData.getPageCount());
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                })
                .setEmptyView(emptyView)
                .setContentView(recyclerView)
                .setPullToRefreshBase(recyclerView)
                .setProgressBar(recyclerView.isRefreshing() ? null : progressBar)
                .build();
        observable.subscribe(refreshSubscriber);
    }

    private void refreshNotebookComments() {
        refreshSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<RepliedComment>>>() {
                    @Override
                    public void onNext(HljHttpData<List<RepliedComment>> httpData) {
                        recyclerView.getRefreshableView()
                                .scrollToPosition(0);
                        comments.clear();
                        comments.addAll(httpData.getData());
                        adapter.notifyDataSetChanged();
                        initPagination(httpData.getPageCount());
                    }
                })
                .setEmptyView(emptyView)
                .setContentView(recyclerView)
                .setPullToRefreshBase(recyclerView)
                .setProgressBar(recyclerView.isRefreshing() ? null : progressBar)
                .build();
        NoteApi.getNotebookCommentsObb(id, "latest", 1)
                .subscribe(refreshSubscriber);
    }

    private void initPagination(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSubscriber);
        Observable<HljHttpData<List<RepliedComment>>> observable = PaginationTool
                .buildPagingObservable(
                recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpData<List<RepliedComment>>>() {
                    @Override
                    public Observable<HljHttpData<List<RepliedComment>>> onNextPage(int page) {
                        if (entityType.equals(HljNote.NOTE_TYPE)) {
                            return CommonApi.getCommonCommentsObb(id, HljNote.NOTE_TYPE, page);
                        } else {
                            return NoteApi.getNotebookCommentsObb(id, "latest", page);
                        }
                    }
                })
                .setLoadView(footerViewHolder.loading)
                .setEndView(footerViewHolder.noMoreHint)
                .build()
                .getPagingObservable();
        pageSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<RepliedComment>>>() {
                    @Override
                    public void onNext(HljHttpData<List<RepliedComment>> listHljHttpData) {
                        if (listHljHttpData.getData() != null) {
                            comments.addAll(listHljHttpData.getData());
                            adapter.notifyDataSetChanged();
                        }
                    }
                })
                .build();
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSubscriber);
    }

    @OnClick(R2.id.btn_add_emoji)
    public void onAddEmoji() {
        onComment(null, true, commentContent);
    }

    @OnClick(R2.id.tv_content)
    public void onContentClick() {
        onComment(null, false, commentContent);
    }

    private class ResultZip extends HljHttpResultZip {
        @HljRZField
        HljHttpData<List<RepliedComment>> hotCommentsData;
        @HljRZField
        HljHttpData<List<RepliedComment>> newCommentsData;
    }

    static class FooterViewHolder {
        @BindView(R2.id.no_more_hint)
        TextView noMoreHint;
        @BindView(R2.id.loading)
        LinearLayout loading;

        FooterViewHolder(View view) {ButterKnife.bind(this, view);}
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(deleteSubscriber, refreshSubscriber, pageSubscriber);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case NOTE_COMMENT:
                    RepliedComment response = data.getParcelableExtra("comment_response");
                    if (response != null) {
                        // 如果成功发送内容,则进行数据更新
                        commentContent = "";
                        onRefresh(null);
                    } else {
                        // 如果没有,则记录下已输入的内容和引用的回复
                        commentContent = data.getStringExtra("comment_content");
                    }
                    break;
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
