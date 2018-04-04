package com.hunliji.hljquestionanswer.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.DynamicDrawableSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hunliji.hljcommonlibrary.models.PostCommentBody;
import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljcommonlibrary.models.merchant.MerchantUser;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljemojilibrary.EmojiUtil;
import com.hunliji.hljemojilibrary.adapters.EmojiPagerAdapter;
import com.hunliji.hljhttplibrary.api.CommonApi;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.AuthUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnCompletedListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;
import com.hunliji.hljquestionanswer.HljQuestionAnswer;
import com.hunliji.hljquestionanswer.R;
import com.hunliji.hljquestionanswer.R2;
import com.hunliji.hljquestionanswer.adapters.AnswerCommentListAdapter;
import com.hunliji.hljquestionanswer.adapters.AnswerPopupRule;
import com.hunliji.hljquestionanswer.api.QuestionAnswerApi;
import com.hunliji.hljquestionanswer.models.AnswerComment;
import com.hunliji.hljquestionanswer.models.AnswerCommentResponse;
import com.hunliji.hljquestionanswer.utils.QuestionAnswerTogglesUtil;
import com.slider.library.Indicators.CirclePageIndicator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by mo_yu on 2016/8/22.回答评论列表
 */
public class AnswerCommentListActivity extends HljBaseActivity implements
        AnswerCommentListAdapter.OnContentClickListener, PullToRefreshBase
        .OnRefreshListener<ListView>, AbsListView.OnScrollListener, EmojiPagerAdapter
        .OnFaceItemClickListener {

    private final static String ENTITY_TYPE = "QaAnswer";

    @BindView(R2.id.list_view)
    PullToRefreshListView listView;
    @BindView(R2.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R2.id.et_content)
    EditText etContent;
    @BindView(R2.id.btn_add_emoji)
    ImageView btnAddEmoji;
    @BindView(R2.id.emoji_pager)
    ViewPager emojiPager;
    @BindView(R2.id.flow_indicator)
    CirclePageIndicator flowIndicator;
    @BindView(R2.id.emoji_layout)
    LinearLayout emojiLayout;
    @BindView(R2.id.bottom_layout)
    LinearLayout bottomLayout;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R2.id.root_layout)
    RelativeLayout rootLayout;

    private View endView;
    private View loadView;

    private HljHttpSubscriber initSubscriber;//初始加载
    private HljHttpSubscriber pageSubscriber;//分页
    private HljHttpSubscriber refreshSubscriber;//刷新
    private HljHttpSubscriber replySubscriber;//回复
    private HljHttpSubscriber deleteCommentSubscriber;//删除评论

    private long answerId;
    private long questionAuthId;
    private int totalCount;
    private AnswerCommentListAdapter adapter;
    private ArrayList<AnswerComment> answerComments;
    private User user;

    private Dialog menuDialog;
    private AnswerComment replyComment;

    //表情相关
    private DisplayMetrics dm;
    private InputMethodManager imm;
    private int emojiSize;
    private int emojiImageSize;
    private int emojiPageHeight;
    private boolean isShowImm;
    private boolean isShowEmoji;
    private Toast toast;
    private CharSequence ss;
    private int mStart;
    private int editCount;
    private MerchantUser merchantUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_comment_list___qa);
        ButterKnife.bind(this);
        initValues();
        initViews();
        initLoad();
    }

    private void initValues() {
        answerComments = new ArrayList<>();
        answerId = getIntent().getLongExtra("answerId", 0);
        questionAuthId = getIntent().getLongExtra("questionAuthId", 0);
        if (getIntent().getBooleanExtra("isFromNotification", false)) {
            setOkText(R.string.label_see_answer___qa);
        }
        emojiSize = CommonUtil.dp2px(this, 20);
        emojiImageSize = (CommonUtil.getDeviceSize(this).x - CommonUtil.dp2px(this, 20)) / 7;
        emojiPageHeight = Math.round(emojiImageSize * 3 + CommonUtil.dp2px(this, 20));
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        user = UserSession.getInstance()
                .getUser(this);
        if (HljQuestionAnswer.isMerchant(AnswerCommentListActivity.this)) {
            merchantUser = (MerchantUser) UserSession.getInstance()
                    .getUser(AnswerCommentListActivity.this);
        }
    }

    private void initViews() {
        emptyView.setHintId(R.string.msg_answer_comment_empty___qa);
        setOnTextWatcher();
        setEmojiViewPager();
        setKeyboardListener();
        adapter = new AnswerCommentListAdapter(this, answerComments, questionAuthId);

        View footerView = View.inflate(this, R.layout.hlj_foot_no_more___cm, null);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        loadView.setVisibility(View.INVISIBLE);
        listView.setNeedChangeSize(false);
        listView.getRefreshableView()
                .setDivider(new ColorDrawable(Color.TRANSPARENT));
        listView.getRefreshableView()
                .setDividerHeight(0);
        listView.getRefreshableView()
                .setOnScrollListener(this);
        listView.getRefreshableView()
                .addFooterView(footerView);
        listView.setOnRefreshListener(this);
        listView.setAdapter(adapter);
        adapter.setOnContentClickListener(this);

        if (merchantUser != null && !merchantUser.isPro()) {
            etContent.setFocusable(false);
        } else {
            etContent.setFocusable(true);
        }

        etContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (merchantUser != null && (merchantUser.getExamine() != 1 || merchantUser
                        .getCertifyStatus() != 3)) {
                    AnswerPopupRule.getDefault()
                            .showHintDialog(AnswerCommentListActivity.this);
                } else if (merchantUser != null && !merchantUser.isPro()) {
                    AnswerPopupRule.getDefault()
                            .showProDialog(AnswerCommentListActivity.this);
                } else {
                    view.post(new Runnable() {
                        @Override
                        public void run() {
                            etContent.requestFocus();
                            imm.showSoftInput(etContent, 0);
                        }
                    });
                }
            }
        });

    }

    private void initLoad() {
        Observable<HljHttpData<List<AnswerComment>>> observable = QuestionAnswerApi
                .getAnswerCommentList(
                answerId,
                ENTITY_TYPE,
                1);
        initSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<AnswerComment>>>
                        () {
                    @Override
                    public void onNext(HljHttpData<List<AnswerComment>> data) {
                        totalCount = data.getTotalCount();
                        setTitle(getString(R.string.label_answer_comment_title___qa, totalCount));
                        answerComments.addAll(data.getData());
                        adapter.notifyDataSetChanged();
                        initPageAnswers(data.getPageCount());
                    }
                })
                .setOnCompletedListener(new SubscriberOnCompletedListener() {
                    @Override
                    public void onCompleted() {
                        bottomLayout.setVisibility(View.VISIBLE);
                    }
                })
                .setEmptyView(emptyView)
                .setProgressBar(progressBar)
                .setListView(listView.getRefreshableView())
                .build();

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(initSubscriber);
    }

    private void initPageAnswers(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSubscriber);
        Observable<HljHttpData<List<AnswerComment>>> pageObservable = PaginationTool
                .buildPagingObservable(
                listView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpData<List<AnswerComment>>>() {
                    @Override
                    public Observable<HljHttpData<List<AnswerComment>>> onNextPage(
                            int page) {
                        Log.d("pagination tool", "on load: " + page);
                        return QuestionAnswerApi.getAnswerCommentList(answerId, ENTITY_TYPE, page);
                    }
                })
                .setLoadView(loadView)
                .setEndView(endView)
                .build()
                .getPagingObservable()
                .observeOn(AndroidSchedulers.mainThread());

        pageSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<AnswerComment>>>
                        () {
                    @Override
                    public void onNext(HljHttpData<List<AnswerComment>> data) {
                        answerComments.addAll(data.getData());
                        adapter.notifyDataSetChanged();
                    }
                })
                .build();

        pageObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSubscriber);
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        if (refreshSubscriber == null || refreshSubscriber.isUnsubscribed()) {
            refreshSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<AnswerComment>>>() {
                        @Override
                        public void onNext(HljHttpData<List<AnswerComment>> data) {
                            totalCount = data.getTotalCount();
                            setTitle(getString(R.string.label_answer_comment_title___qa,
                                    totalCount));
                            answerComments.clear();
                            answerComments.addAll(data.getData());
                            adapter.notifyDataSetChanged();
                            initPageAnswers(data.getPageCount());
                        }
                    })
                    .setPullToRefreshBase(listView)
                    .build();


            QuestionAnswerApi.getAnswerCommentList(answerId, ENTITY_TYPE, 1)
                    .distinctUntilChanged()
                    .subscribe(refreshSubscriber);
        }
    }

    @Override
    public void onContentClick(int position, AnswerComment item) {
        if (AuthUtil.loginBindCheck(this)) {
            //评论是自己的话
            if (user != null && user.getId() != item.getUser()
                    .getId()) {
                // 不是自己,直接评论
                showMenu(item, false);
            } else {
                showMenu(item, true);
            }
        }
    }

    /**
     * 回复弹窗
     *
     * @param comment
     * @param isUser
     */
    private void showMenu(final AnswerComment comment, boolean isUser) {
        menuDialog = new Dialog(this, R.style.BubbleDialogTheme);
        menuDialog.setContentView(R.layout.dialog_answer_comment_menu___qa);
        TextView textReply = (TextView) menuDialog.findViewById(R.id.action_delete_reply);
        if (isUser) {
            menuDialog.findViewById(R.id.action_report_reply)
                    .setVisibility(View.GONE);
            menuDialog.findViewById(R.id.report_line)
                    .setVisibility(View.GONE);
            textReply.setText("删除");
            textReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    menuDialog.cancel();
                    onDeleteComment(comment);
                }
            });
        } else {
            menuDialog.findViewById(R.id.action_report_reply)
                    .setVisibility(View.VISIBLE);
            menuDialog.findViewById(R.id.report_line)
                    .setVisibility(View.VISIBLE);
            textReply.setText("回复");
            textReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    menuDialog.cancel();
                    v.post(new Runnable() {
                        @Override
                        public void run() {
                            if (merchantUser != null && (merchantUser.getExamine() != 1 ||
                                    merchantUser.getCertifyStatus() != 3)) {
                                AnswerPopupRule.getDefault()
                                        .showHintDialog(AnswerCommentListActivity.this);
                            } else if (merchantUser != null && !merchantUser.isPro()) {
                                AnswerPopupRule.getDefault()
                                        .showProDialog(AnswerCommentListActivity.this);
                            } else {
                                replyComment = comment;
                                etContent.setHint("@" + comment.getUser()
                                        .getName());
                                etContent.requestFocus();
                                imm.showSoftInput(etContent, 0);
                            }
                        }
                    });
                }
            });
            menuDialog.findViewById(R.id.action_report_reply)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            menuDialog.cancel();
                            Intent intent = new Intent(AnswerCommentListActivity.this,
                                    ReportActivity.class);
                            intent.putExtra("id", comment.getId());
                            intent.putExtra("kind", HljQuestionAnswer.REPORT_COMMENT);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right,
                                    R.anim.activity_anim_default);
                        }
                    });
        }
        menuDialog.findViewById(R.id.action_cancel)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        menuDialog.cancel();
                    }
                });
        menuDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                Animation animation = AnimationUtils.loadAnimation(AnswerCommentListActivity
                        .this, R.anim.slide_in_up);
                animation.setFillAfter(true);
                bottomLayout.startAnimation(animation);
            }
        });
        menuDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Animation animation = AnimationUtils.loadAnimation(AnswerCommentListActivity
                        .this, R.anim.slide_out_up);
                animation.setFillAfter(true);
                bottomLayout.startAnimation(animation);
            }
        });
        Window window = menuDialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        Point point = CommonUtil.getDeviceSize(AnswerCommentListActivity.this);
        params.width = point.x;
        window.setAttributes(params);
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.dialog_anim_rise_style);
        menuDialog.show();
    }

    /**
     * 删除自己的评论
     *
     * @param comment
     */
    private void onDeleteComment(final AnswerComment comment) {
        DialogUtil.createDoubleButtonDialog(this,
                getString(R.string.hint_sure_to_delete___qa),
                null,
                null,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteComment(comment);
                    }
                },
                null)
                .show();
    }

    private void deleteComment(final AnswerComment comment) {
        if (AuthUtil.loginBindCheck(this)) {
            deleteCommentSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setDataNullable(true)
                    .setProgressDialog(DialogUtil.createProgressDialog(this))
                    .setOnNextListener(new SubscriberOnNextListener() {
                        @Override
                        public void onNext(Object o) {
                            // 成功删除
                            answerComments.remove(comment);
                            adapter.notifyDataSetChanged();
                            totalCount = totalCount - 1;
                            if (totalCount <= 0) {
                                totalCount = 0;
                                emptyView.showEmptyView();
                            }
                            setTitle(String.format(getString(R.string
                                            .label_answer_comment_title___qa),
                                    totalCount));
                        }
                    })
                    .build();
            CommonApi.deleteFuncObb(comment.getId())
                    .subscribe(deleteCommentSubscriber);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {

    }

    /**
     * 输入框事件监听
     */
    private void setOnTextWatcher() {
        etContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (count > 0) {
                    DynamicDrawableSpan[] spans = etContent.getText()
                            .getSpans(start, start + count, DynamicDrawableSpan.class);
                    int size = spans.length;
                    if (size > 0) {
                        for (DynamicDrawableSpan span : spans) {
                            etContent.getText()
                                    .removeSpan(span);
                        }
                    }
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ss = s.subSequence(start, start + count)
                        .toString();
                mStart = start;
                editCount = count;
            }

            @Override
            public void afterTextChanged(Editable s) {
                int editStart = etContent.getSelectionEnd();
                int editEnd = etContent.getSelectionEnd();
                etContent.removeTextChangedListener(this);
                int outCount = CommonUtil.getTextLength(s) - 140;
                boolean isOut = false;
                while (outCount > 0) {
                    isOut = true;
                    editStart -= outCount;
                    s.delete(editStart, editEnd);
                    if (ss.length() > 0) {
                        ss = ss.subSequence(0, ss.length() - outCount);
                        editCount -= outCount;
                    }
                    editEnd = editStart;
                    outCount = CommonUtil.getTextLength(s) - 140;
                }
                if (isOut) {
                    showToast(R.string.msg_answer_comment___qa);
                }
                if (ss.length() > 0) {
                    etContent.getText()
                            .replace(mStart,
                                    mStart + editCount,
                                    EmojiUtil.parseEmojiByText2(AnswerCommentListActivity.this,
                                            ss.toString(),
                                            emojiSize));
                }
                etContent.setSelection(editStart);
                etContent.addTextChangedListener(this);
            }
        });
    }

    private void showToast(int hintId) {
        if (toast == null) {
            toast = Toast.makeText(this, hintId, Toast.LENGTH_SHORT);
        } else {
            toast.setText(hintId);
        }
        toast.show();
    }

    //新增表情模块
    private void setEmojiViewPager() {
        EmojiPagerAdapter emojiPagerAdapter = new EmojiPagerAdapter(this, emojiImageSize, this);
        emojiPager.setAdapter(emojiPagerAdapter);
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.addAll(EmojiUtil.getFaceMap(this)
                .keySet());
        emojiPagerAdapter.setTags(arrayList);

        flowIndicator.setViewPager(emojiPager);
        emojiPager.getLayoutParams().height = emojiPageHeight;
    }

    private void setKeyboardListener() {
        rootLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(
                    View v,
                    int left,
                    int top,
                    int right,
                    int bottom,
                    int oldLeft,
                    int oldTop,
                    int oldRight,
                    int oldBottom) {
                if (bottom == 0 || oldBottom == 0 || bottom == oldBottom) {
                    return;
                }
                int height = getWindow().getDecorView()
                        .getHeight();
                isShowImm = (double) (bottom - top) / height < 0.8;
                if (isShowImm) {
                    isShowEmoji = false;
                    emojiLayout.setVisibility(View.GONE);
                    btnAddEmoji.setImageResource(R.mipmap.icon_face_black_50_50);
                } else {
                    if (isShowEmoji) {
                        emojiLayout.setVisibility(View.VISIBLE);
                        btnAddEmoji.setImageResource(R.mipmap.icon_keyboard_round_gray);
                    } else {
                        etContent.setText(null);
                        etContent.setHint(R.string.label_reply_hint___qa);
                        replyComment = null;
                    }
                }
            }
        });
    }

    public void hideKeyboard(View v) {
        if (this.getCurrentFocus() != null) {
            ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                    this.getCurrentFocus()
                            .getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void hideEmojiAndImm() {
        if (imm != null && isShowImm && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
        if (isShowEmoji) {
            emojiLayout.setVisibility(View.GONE);
            isShowEmoji = false;
            btnAddEmoji.setImageResource(R.mipmap.icon_face_black_50_50);
        }
    }

    @OnClick(R2.id.tv_send)
    void onSend() {
        if (merchantUser != null && (merchantUser.getExamine() != 1 || merchantUser
                .getCertifyStatus() != 3)) {
            AnswerPopupRule.getDefault()
                    .showHintDialog(AnswerCommentListActivity.this);
        } else if (merchantUser != null && !merchantUser.isPro()) {
            AnswerPopupRule.getDefault()
                    .showProDialog(AnswerCommentListActivity.this);
        } else {
            if (AuthUtil.loginBindCheck(this)) {
                if (answerId == 0) {
                    return;
                }
                hideEmojiAndImm();
                if (TextUtils.isEmpty(etContent.getText())) {
                    showToast(R.string.msg_post_text_empty___qa);
                    return;
                }
                final String content = etContent.getText()
                        .toString();
                // 提交内容
                PostCommentBody body = new PostCommentBody();
                body.setContent(content);
                body.setEntityId(answerId);
                body.setEntityType(ENTITY_TYPE);
                if (replyComment != null) {
                    body.setReplyId(replyComment.getId());
                }
                Observable<AnswerCommentResponse> observable = QuestionAnswerApi.postCommentObb
                        (body);


                replySubscriber = HljHttpSubscriber.buildSubscriber(this)
                        .setProgressDialog(DialogUtil.createProgressDialog(this))
                        .setOnNextListener(new SubscriberOnNextListener<AnswerCommentResponse>() {
                            @Override
                            public void onNext(
                                    AnswerCommentResponse response) {
                                // 成功提交后,组装评论model返回给列表显示
                                etContent.setText(null);
                                etContent.setHint(R.string.label_reply_hint___qa);
                                replyComment = null;
                                showToast(R.string.msg_success_to_comment___qa);
                                onRefresh(listView);
                            }
                        })
                        .build();

                observable.subscribe(replySubscriber);
            }
        }
    }

    @OnClick(R2.id.btn_add_emoji)
    void onAddEmoji() {
        if (merchantUser != null && (merchantUser.getExamine() != 1 || merchantUser
                .getCertifyStatus() != 3)) {
            AnswerPopupRule.getDefault()
                    .showHintDialog(AnswerCommentListActivity.this);
        } else if (merchantUser != null && !merchantUser.isPro()) {
            AnswerPopupRule.getDefault()
                    .showProDialog(AnswerCommentListActivity.this);
        } else {
            if (AuthUtil.loginBindCheck(this)) {
                if (imm != null && isShowImm) {
                    isShowEmoji = true;
                    if (getCurrentFocus() != null) {
                        imm.toggleSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                                0,
                                InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                } else if (!isShowEmoji) {
                    isShowEmoji = true;
                    emojiLayout.setVisibility(View.VISIBLE);
                    btnAddEmoji.setImageResource(R.mipmap.icon_face_black_50_50);
                } else {
                    isShowEmoji = false;
                    if (imm != null && getCurrentFocus() != null) {
                        imm.toggleSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                                0,
                                InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                }
            }
        }
    }

    @Override
    public void onFaceItemClickListener(
            AdapterView<?> parent, View view, int position, long id) {
        String tag = (String) parent.getAdapter()
                .getItem(position);
        if (!TextUtils.isEmpty(tag)) {
            if (tag.equals("delete")) {
                if (etContent.isFocused()) {
                    EmojiUtil.deleteTextOrImage(etContent);
                }
            } else {
                if (etContent.isFocused()) {
                    StringBuilder ss = new StringBuilder(tag);
                    int start = etContent.getSelectionStart();
                    int end = etContent.getSelectionEnd();
                    if (start == end) {
                        etContent.getText()
                                .insert(start, ss);
                    } else {
                        etContent.getText()
                                .replace(start, end, ss);
                    }
                }
            }
        }
    }

    @Override
    public void onOkButtonClick() {
        Intent intent = new Intent(this, AnswerDetailActivity.class);
        intent.putExtra("answerId", answerId);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_anim_default, R.anim.slide_out_right);
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        hideEmojiAndImm();
        setResult(RESULT_OK, new Intent().putExtra("totalCount", totalCount));
        finish();
        overridePendingTransition(R.anim.activity_anim_default, R.anim.slide_out_right);
        if (initSubscriber != null && !initSubscriber.isUnsubscribed()) {
            initSubscriber.unsubscribe();
        }
        if (pageSubscriber != null && !pageSubscriber.isUnsubscribed()) {
            pageSubscriber.unsubscribe();
        }
        if (refreshSubscriber != null && !refreshSubscriber.isUnsubscribed()) {
            refreshSubscriber.unsubscribe();
        }
        if (replySubscriber != null && !replySubscriber.isUnsubscribed()) {
            replySubscriber.unsubscribe();
        }
        if (deleteCommentSubscriber != null && !deleteCommentSubscriber.isUnsubscribed()) {
            deleteCommentSubscriber.unsubscribe();
        }
        QuestionAnswerTogglesUtil.destroySubscriber();
    }

}
