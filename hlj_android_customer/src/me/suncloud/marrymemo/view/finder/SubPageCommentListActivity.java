package me.suncloud.marrymemo.view.finder;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.models.PostCommentBody;
import com.hunliji.hljcommonlibrary.models.RepliedComment;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljemojilibrary.EmojiUtil;
import com.hunliji.hljemojilibrary.adapters.EmojiPagerAdapter;
import com.hunliji.hljhttplibrary.api.CommonApi;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.AuthUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;
import com.slider.library.Indicators.CirclePageIndicator;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.finder.SubPageCommentListAdapter;
import me.suncloud.marrymemo.api.finder.FinderApi;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.model.finder.EntityComment;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.TextFaceCountWatcher;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.util.finder.FinderTogglesUtil;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * 专题评论列表界面
 * Created by chen_bin on 2016/7/25 0025.
 */
public class SubPageCommentListActivity extends HljBaseActivity implements EmojiPagerAdapter
        .OnFaceItemClickListener, PullToRefreshBase.OnRefreshListener<RecyclerView>,
        SubPageCommentListAdapter.OnCommentListener, SubPageCommentListAdapter.OnPraiseListener {

    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.et_content)
    EditText etContent;
    @BindView(R.id.btn_add_emoji)
    ImageView btnAddEmoji;
    @BindView(R.id.emoji_pager)
    ViewPager emojiPager;
    @BindView(R.id.flow_indicator)
    CirclePageIndicator flowIndicator;
    @BindView(R.id.emoji_layout)
    LinearLayout emojiLayout;
    @BindView(R.id.bottom_layout)
    LinearLayout bottomLayout;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.root_layout)
    RelativeLayout rootLayout;

    private View endView;
    private View loadView;
    private SubPageCommentListAdapter adapter;
    private EntityComment lastComment;
    private InputMethodManager imm;
    private Dialog menuDialog;
    private boolean isShowImm;
    private boolean isShowEmoji;
    private long id;
    private int totalCount;
    private int emojiSize;
    private int emojiImageSize;
    private int emojiPageHeight;
    private HljHttpSubscriber refreshSub;
    private HljHttpSubscriber pageSub;
    private HljHttpSubscriber postSub;
    private HljHttpSubscriber deleteSub;
    private HljHttpSubscriber praiseSub;

    public static final String ENTITY_TYPE = "SubPage";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_page_comment_list);
        ButterKnife.bind(this);
        setTitle("");
        id = getIntent().getLongExtra("id", 0);
        //如果是从通知中进来的话则显示查看专题文案，可以跳转到相应的专题详情。
        if (getIntent().getBooleanExtra("isFromNotification", false)) {
            setOkText(R.string.label_see_sub_page);
        }
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        emojiSize = CommonUtil.dp2px(this, 20);
        emojiImageSize = (CommonUtil.getDeviceSize(this).x - CommonUtil.dp2px(this, 20)) / 7;
        emojiPageHeight = Math.round(emojiImageSize * 3 + CommonUtil.dp2px(this, 20));
        emptyView.setHintId(R.string.hint_topic_comment_empty);
        emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                onRefresh(null);
            }
        });
        View footerView = View.inflate(this, R.layout.hlj_foot_no_more___cm, null);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        loadView.setVisibility(View.INVISIBLE);
        ((SimpleItemAnimator) recyclerView.getRefreshableView()
                .getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.getRefreshableView()
                .setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNeedChangeSize(false);
        recyclerView.setOnRefreshListener(this);
        adapter = new SubPageCommentListAdapter(this);
        adapter.setFooterView(footerView);
        adapter.setOnCommentListener(this);
        adapter.setOnPraiseListener(this);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
        initFace();
        onRefresh(null);
    }

    //初始化表情
    private void initFace() {
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
                } else if (isShowEmoji) {
                    emojiLayout.setVisibility(View.VISIBLE);
                    btnAddEmoji.setImageResource(R.mipmap.icon_keyboard_round_gray);
                }
            }
        });
        EmojiPagerAdapter pagerAdapter = new EmojiPagerAdapter(this, emojiImageSize, this);
        emojiPager.setAdapter(pagerAdapter);
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.addAll(EmojiUtil.getFaceMap(this)
                .keySet());
        pagerAdapter.setTags(arrayList);
        flowIndicator.setViewPager(emojiPager);
        emojiPager.getLayoutParams().height = emojiPageHeight;
        etContent.addTextChangedListener(new TextFaceCountWatcher(this,
                etContent,
                null,
                140,
                emojiSize));
    }

    @Override
    public void onFaceItemClickListener(AdapterView<?> parent, View view, int position, long id) {
        String tag = (String) parent.getAdapter()
                .getItem(position);
        if (!TextUtils.isEmpty(tag)) {
            if (tag.equals("delete")) {
                if (etContent.isFocused()) {
                    Util.deleteTextOrImage(etContent);
                }
            } else {
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

    @Override
    public void onRefresh(final PullToRefreshBase<RecyclerView> refreshView) {
        if (refreshSub == null || refreshSub.isUnsubscribed()) {
            //最热评论
            Observable<HljHttpData<List<EntityComment>>> hotCommentsObb = FinderApi
                    .getSubPageHotCommentsObb(
                    id,
                    ENTITY_TYPE,
                    1,
                    3);
            //最新评论
            Observable<HljHttpData<List<EntityComment>>> newCommentsObb = FinderApi
                    .getSubPageNewCommentsObb(
                    id,
                    ENTITY_TYPE,
                    1,
                    20);
            Observable<ResultZip> observable = Observable.zip(hotCommentsObb,
                    newCommentsObb,
                    new Func2<HljHttpData<List<EntityComment>>, HljHttpData<List<EntityComment>>,
                            ResultZip>() {
                        @Override
                        public ResultZip call(
                                HljHttpData<List<EntityComment>> listHljHttpData,
                                HljHttpData<List<EntityComment>> listHljHttpData2) {
                            ResultZip zip = new ResultZip();
                            zip.hotCommentsData = listHljHttpData;
                            zip.newCommentsData = listHljHttpData2;
                            return zip;
                        }
                    });
            refreshSub = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<ResultZip>() {
                        @Override
                        public void onNext(ResultZip resultZip) {
                            bottomLayout.setVisibility(View.VISIBLE);
                            recyclerView.getRefreshableView()
                                    .scrollToPosition(0);
                            totalCount = 0;
                            int hotCount = 0;
                            int pageCount = 0;
                            List<EntityComment> comments = new ArrayList<>();
                            if (resultZip.hotCommentsData != null && !CommonUtil.isCollectionEmpty(
                                    resultZip.hotCommentsData.getData())) {
                                hotCount = resultZip.hotCommentsData.getTotalCount();
                                comments.addAll(resultZip.hotCommentsData.getData());
                            }
                            if (resultZip.newCommentsData != null && !CommonUtil.isCollectionEmpty(
                                    resultZip.newCommentsData.getData())) {
                                totalCount = resultZip.newCommentsData.getTotalCount();
                                pageCount = resultZip.newCommentsData.getPageCount();
                                comments.addAll(resultZip.newCommentsData.getData());
                            }
                            setTitle(getString(R.string.label_review_count2, totalCount));
                            if (CommonUtil.isCollectionEmpty(comments)) {
                                emptyView.showEmptyView();
                                recyclerView.setVisibility(View.GONE);
                            }
                            adapter.setHotCount(hotCount);
                            adapter.setComments(comments);
                            initPagination(pageCount);
                        }
                    })
                    .setDataNullable(true)
                    .setEmptyView(emptyView)
                    .setContentView(recyclerView)
                    .setPullToRefreshBase(recyclerView)
                    .setProgressBar(recyclerView.isRefreshing() ? null : progressBar)
                    .build();
            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(refreshSub);
        }
    }

    private void initPagination(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSub);
        Observable<HljHttpData<List<EntityComment>>> observable = PaginationTool
                .buildPagingObservable(
                recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpData<List<EntityComment>>>() {
                    @Override
                    public Observable<HljHttpData<List<EntityComment>>> onNextPage(int page) {
                        return FinderApi.getSubPageNewCommentsObb(id, "SubPage", page, 20);
                    }
                })
                .setLoadView(loadView)
                .setEndView(endView)
                .build()
                .getPagingObservable();
        pageSub = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<EntityComment>>>
                        () {
                    @Override
                    public void onNext(HljHttpData<List<EntityComment>> listHljHttpData) {
                        adapter.addComments(listHljHttpData.getData());
                    }
                })
                .build();
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSub);
    }

    private class ResultZip {
        HljHttpData<List<EntityComment>> hotCommentsData;
        HljHttpData<List<EntityComment>> newCommentsData;
    }

    @Override
    public void onComment(final EntityComment comment) {
        if (comment == null || comment.getId() == 0) {
            return;
        }
        if (!AuthUtil.loginBindCheck(this)) {
            return;
        }
        //当前选中项是自己评论的话则进行删除操作，别人评论的则进行回复操作。
        User user = Session.getInstance()
                .getCurrentUser(this);
        if (user.getId() == comment.getUser()
                .getId()) {
            showMenuDialog(comment);
        } else {
            if (lastComment == null || lastComment.getId() != comment.getId()) {
                lastComment = comment;
                etContent.setText("");
                etContent.setHint("@" + comment.getUser()
                        .getName());
            }
            etContent.requestFocus();
            if (imm != null && getCurrentFocus() != null) {
                imm.toggleSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        0,
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    private void showMenuDialog(final EntityComment comment) {
        if (menuDialog != null && menuDialog.isShowing()) {
            return;
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
        Button btnMenu = menuDialog.findViewById(R.id.btn_menu);
        btnMenu.setText(R.string.label_delete___cm);
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                menuDialog.dismiss();
                CommonUtil.unSubscribeSubs(deleteSub);
                if (deleteSub == null || deleteSub.isUnsubscribed()) {
                    deleteSub = HljHttpSubscriber.buildSubscriber(SubPageCommentListActivity.this)
                            .setOnNextListener(new SubscriberOnNextListener() {
                                @Override
                                public void onNext(Object o) {
                                    ToastUtil.showCustomToast(SubPageCommentListActivity.this,
                                            R.string.msg_success_to_delete_post);
                                    totalCount = totalCount - 1;
                                    setTitle(getString(R.string.label_review_count2, totalCount));
                                    Intent intent = getIntent();
                                    intent.putExtra("total_count", totalCount);
                                    setResult(RESULT_OK, intent);
                                    List<EntityComment> comments = adapter.getComments();
                                    comments.remove(comment);
                                    for (int i = 0; i < adapter.getHotCount(); i++) {
                                        if (comments.get(i)
                                                .getId() == comment.getId()) {
                                            comments.remove(i);
                                            adapter.setHotCount(adapter.getHotCount() - 1);
                                            break;
                                        }
                                    }
                                    adapter.notifyDataSetChanged();
                                    if (CommonUtil.isCollectionEmpty(adapter.getComments())) {
                                        emptyView.showEmptyView();
                                        recyclerView.setVisibility(View.GONE);
                                    }
                                }
                            })
                            .setProgressDialog(DialogUtil.createProgressDialog(
                                    SubPageCommentListActivity.this))
                            .build();
                    CommonApi.deleteFuncObb(comment.getId())
                            .subscribe(deleteSub);
                }
            }
        });
        menuDialog.show();
    }

    @Override
    public void onPraise(
            SubPageCommentListAdapter.SubPageCommentViewHolder commentViewHolder,
            EntityComment comment) {
        if (comment != null && comment.getId() > 0) {
            praiseSub = FinderTogglesUtil.getInstance()
                    .onSubPageCommentPraise(this,
                            comment,
                            commentViewHolder.checkPraised,
                            commentViewHolder.imgPraise,
                            commentViewHolder.tvPraiseCount,
                            commentViewHolder.tvPraiseAdd,
                            praiseSub);
        }
    }

    @OnClick(R.id.btn_add_emoji)
    public void onAddEmoji() {
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
            btnAddEmoji.setImageResource(R.mipmap.icon_keyboard_round_gray);
        } else {
            isShowEmoji = false;
            if (imm != null && getCurrentFocus() != null) {
                imm.toggleSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        0,
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    @OnClick(R.id.tv_send)
    void onSend() {
        if (!AuthUtil.loginBindCheck(this)) {
            return;
        }
        final String content = etContent.getText()
                .toString();
        if (TextUtils.isEmpty(content)) {
            ToastUtil.showToast(this, null, R.string.hint_post_text_empty);
            return;
        }
        hideEmojiAndImm();
        CommonUtil.unSubscribeSubs(postSub);
        postSub = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<RepliedComment>() {
                    @Override
                    public void onNext(RepliedComment object) {
                        ToastUtil.showCustomToast(SubPageCommentListActivity.this,
                                R.string.msg_success_to_comment___note);
                        etContent.setText("");
                        etContent.setHint(R.string.label_reply_hint);
                        totalCount = totalCount + 1;
                        setTitle(getString(R.string.label_review_count2, totalCount));
                        Intent intent = getIntent();
                        intent.putExtra("total_count", totalCount);
                        setResult(RESULT_OK, intent);
                        User user = Session.getInstance()
                                .getCurrentUser(SubPageCommentListActivity.this);
                        EntityComment comment = new EntityComment();
                        comment.setId(object.getId());
                        comment.setCreatedAt(new DateTime());
                        comment.setContent(content);
                        comment.getUser()
                                .setId(user.getId());
                        comment.getUser()
                                .setName(user.getNick());
                        comment.getUser()
                                .setAvatar(user.getAvatar());
                        if (lastComment != null) {
                            comment.setReply(lastComment);
                            lastComment = null;
                        }
                        adapter.getComments()
                                .add(adapter.getHotCount(), comment);
                        adapter.notifyDataSetChanged();
                        recyclerView.getRefreshableView()
                                .scrollToPosition(adapter.getHotCount());
                        if (!CommonUtil.isCollectionEmpty(adapter.getComments())) {
                            emptyView.hideEmptyView();
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                    }
                })
                .setProgressDialog(DialogUtil.createProgressDialog(this))
                .build();
        PostCommentBody body = new PostCommentBody();
        body.setContent(content);
        body.setEntityType(ENTITY_TYPE);
        body.setEntityId(id);
        body.setReplyId(lastComment == null ? 0 : lastComment.getId());
        CommonApi.addFuncObb(body)
                .subscribe(postSub);
    }

    @Override
    public void onOkButtonClick() {
        Intent intent = new Intent(this, SubPageDetailActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (!hideEmojiAndImm()) {
            return;
        }
        if (lastComment != null) {
            lastComment = null;
            etContent.setText("");
            etContent.setHint(R.string.label_reply_hint);
            return;
        }
        super.onBackPressed();
    }

    private boolean hideEmojiAndImm() {
        if (imm != null && isShowImm && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
            return false;
        }
        if (isShowEmoji) {
            isShowEmoji = false;
            emojiLayout.setVisibility(View.GONE);
            btnAddEmoji.setImageResource(R.mipmap.icon_face_black_50_50);
            return false;
        }
        return true;
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(refreshSub, pageSub, postSub, deleteSub, praiseSub);
    }
}