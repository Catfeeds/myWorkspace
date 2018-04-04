package com.hunliji.hljnotelibrary.views.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.RepliedComment;
import com.hunliji.hljcommonlibrary.models.customer.CustomerUser;
import com.hunliji.hljcommonlibrary.models.note.Note;
import com.hunliji.hljcommonlibrary.models.note.NoteAuthor;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResultZip;
import com.hunliji.hljhttplibrary.entities.HljRZField;
import com.hunliji.hljhttplibrary.utils.AuthUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljnotelibrary.HljNote;
import com.hunliji.hljnotelibrary.R;
import com.hunliji.hljnotelibrary.R2;
import com.hunliji.hljnotelibrary.adapters.NoteDetailRecyclerAdapter;
import com.hunliji.hljnotelibrary.api.NoteApi;
import com.hunliji.hljnotelibrary.models.Notebook;
import com.hunliji.hljsharelibrary.utils.ShareDialogUtil;
import com.makeramen.rounded.RoundedImageView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func4;
import rx.schedulers.Schedulers;

/**
 * Created by mo_yu on 2017/6/26.笔记本列表页
 */

public class NotebookActivity extends HljBaseNoBarActivity {

    public static final int EDIT_NOTE_BOOK = 1;

    @BindView(R2.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R2.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R2.id.btn_back)
    ImageView btnBack;
    @BindView(R2.id.btn_share)
    ImageView btnShare;
    @BindView(R2.id.btn_menu)
    ImageView btnMenu;
    @BindView(R2.id.shadow_view)
    View shadowView;
    @BindView(R2.id.btn_back_2)
    ImageButton btnBack2;
    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.btn_share2)
    ImageButton btnShare2;
    @BindView(R2.id.btn_menu2)
    ImageView btnMenu2;
    @BindView(R2.id.action_layout)
    RelativeLayout actionLayout;
    @BindView(R2.id.action_holder_layout)
    LinearLayout actionHolderLayout;
    @BindView(R2.id.action_holder_layout2)
    LinearLayout actionHolderLayout2;

    private long noteBookId;
    private long userId;
    private float alpha;
    private int totalCommentCount;

    private NoteDetailRecyclerAdapter adapter;
    private NoteBookHeaderViewHolder noteBookHeaderViewHolder;
    private CommentHeaderViewHolder commentHeaderViewHolder;
    private CommentFooterViewHolder commentFooterViewHolder;
    private RelevantNoteHeaderViewHolder relevantNoteHeaderViewHolder;
    private FooterViewHolder footerViewHolder;
    private Notebook notebook;
    private ArrayList<Note> noteBookNotes;//笔记本笔记
    private ArrayList<Merchant> merchants;
    private ArrayList<RepliedComment> comments;
    private ArrayList<Note> relativeNotes;//相同标签的笔记

    private int coverWidth;
    private int coverHeight;
    private int avatarSize;
    private int avatarOffset;

    private Dialog moreSettingDlg;
    private HljHttpSubscriber refreshSubscriber;
    private HljHttpSubscriber notebookSubscriber;
    private HljHttpSubscriber pageSubscriber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_book___note);
        ButterKnife.bind(this);
        initValue();
        initView();
        getNotebookData();
    }

    private void initValue() {
        noteBookId = getIntent().getLongExtra("note_book_id", 0);

        coverWidth = CommonUtil.getDeviceSize(this).x;
        avatarOffset = CommonUtil.dp2px(this, 10);
        coverHeight = coverWidth * 9 / 16 + avatarOffset;
        avatarSize = coverWidth * 5 / 32;

        merchants = new ArrayList<>();
        noteBookNotes = new ArrayList<>();
        comments = new ArrayList<>();
        relativeNotes = new ArrayList<>();
    }

    private void initView() {
        setActionBarPadding(actionHolderLayout, actionHolderLayout2);
        View noteBookHeaderView = getLayoutInflater().inflate(R.layout.note_book_header___note,
                null);
        noteBookHeaderViewHolder = new NoteBookHeaderViewHolder(noteBookHeaderView);
        drawTriangle();
        View footerView = View.inflate(this, R.layout.hlj_foot_no_more___cm, null);
        footerViewHolder = new FooterViewHolder(footerView);
        View commentHeaderView = View.inflate(this, R.layout.note_book_comment_header___note, null);
        commentHeaderViewHolder = new CommentHeaderViewHolder(commentHeaderView);
        View commentFooterView = View.inflate(this, R.layout.note_comment_footer___note, null);
        commentFooterViewHolder = new CommentFooterViewHolder(commentFooterView);
        commentFooterViewHolder.noteCommentFooterView.setOnClickListener(new View.OnClickListener
                () {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NotebookActivity.this, NoteCommentListActivity.class);
                intent.putExtra("id", noteBookId);
                intent.putExtra("entity_type", HljNote.NOTE_BOOK_TYPE);
                startActivity(intent);
            }
        });
        View relevantNoteHeaderView = View.inflate(this,
                R.layout.relevant_note_header___note,
                null);
        relevantNoteHeaderViewHolder = new RelevantNoteHeaderViewHolder(relevantNoteHeaderView);
        adapter = new NoteDetailRecyclerAdapter(this);
        adapter.setMerchants(merchants);
        adapter.setComments(comments);
        adapter.setRelativeNotes(relativeNotes);
        adapter.setNotebookNotes(noteBookNotes);
        adapter.setEntityType(HljNote.NOTE_BOOK_TYPE);
        adapter.setNoteHeaderView(noteBookHeaderView);
        adapter.setCommentHeaderView(commentHeaderView);
        adapter.setCommentFooterView(commentFooterView);
        adapter.setRelevantNoteHeaderView(relevantNoteHeaderView);
        adapter.setFooterView(footerView);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setItemPrefetchEnabled(false);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.addItemDecoration(new SpacesItemDecoration());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int disHeight = 0;
                if (noteBookHeaderViewHolder != null && noteBookHeaderViewHolder.imgCover != null) {
                    disHeight = noteBookHeaderViewHolder.imgCover.getHeight();
                }
                StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager)
                        recyclerView.getLayoutManager();
                if (notebook != null && layoutManager != null && disHeight > 0) {
                    int[] into = layoutManager.findFirstVisibleItemPositions(new int[]{0, 1});
                    if (into != null && into.length > 0) {
                        if (into[0] > 0) {
                            alpha = 1;
                            if (into[1] >= adapter.getHeaderCount()) {
                                tvTitle.setText("看了又看");
                            } else {
                                tvTitle.setText("笔记本");
                            }
                        } else {
                            alpha = (float) -layoutManager.getChildAt(0)
                                    .getTop() / disHeight;
                        }
                    } else {
                        alpha = 1;
                    }
                } else {
                    alpha = 0;
                }
                setToolbarAlpha(alpha);
            }
        });
    }

    public void setToolbarAlpha(float alpha) {
        actionHolderLayout2.setAlpha(alpha);
        shadowView.setAlpha(1 - alpha);
    }

    private void getNotebookData() {
        if (notebookSubscriber == null || notebookSubscriber.isUnsubscribed()) {
            notebookSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<Notebook>() {
                        @Override
                        public void onNext(Notebook notebookData) {
                            notebook = notebookData;
                            userId = notebook.getAuthor()
                                    .getId();
                            refreshNotebook();
                            refresh();
                        }
                    })
                    .build();
            NoteApi.getNoteBookDetailObb(noteBookId)
                    .subscribe(notebookSubscriber);
        }

    }

    private void refresh() {
        if (refreshSubscriber == null || refreshSubscriber.isUnsubscribed()) {
            refreshSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<ResultZip>() {
                        @Override
                        public void onNext(ResultZip resultZip) {
                            if (resultZip.noteBookNotes != null) {
                                noteBookNotes.clear();
                                noteBookNotes.addAll(resultZip.noteBookNotes);
                            }
                            if (resultZip.merchants != null) {
                                merchants.clear();
                                merchants.addAll(resultZip.merchants);
                            }
                            if (resultZip.comments != null) {
                                comments.clear();
                                comments.addAll(resultZip.comments);
                                if (totalCommentCount > adapter.getMaxCommentCount()) {
                                    commentFooterViewHolder.tvLookAll.setVisibility(View.VISIBLE);
                                    commentFooterViewHolder.tvLookAll.setText(getString(R.string
                                                    .label_note_comment_count___note,
                                            totalCommentCount));
                                    commentFooterViewHolder.lineLayout.setVisibility(View.VISIBLE);
                                } else {
                                    commentFooterViewHolder.tvLookAll.setVisibility(View.GONE);
                                    commentFooterViewHolder.lineLayout.setVisibility(View.GONE);
                                }
                            }
                            if (CommonUtil.isCollectionEmpty(comments)) {
                                commentHeaderViewHolder.commentHeaderView.setVisibility(View.GONE);
                                commentFooterViewHolder.noteCommentFooterView.setVisibility(View
                                        .GONE);
                            } else {
                                commentHeaderViewHolder.commentHeaderView.setVisibility(View
                                        .VISIBLE);
                                commentFooterViewHolder.noteCommentFooterView.setVisibility(View
                                        .VISIBLE);
                            }
                            if (resultZip.relativeNotes != null) {
                                relativeNotes.clear();
                                relativeNotes.addAll(resultZip.relativeNotes);
                            }
                            if (CommonUtil.isCollectionEmpty(relativeNotes)) {
                                relevantNoteHeaderViewHolder.relevantNoteHeaderLayout.setVisibility(
                                        View.GONE);
                            } else {
                                relevantNoteHeaderViewHolder.relevantNoteHeaderLayout.setVisibility(
                                        View.VISIBLE);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    })
                    .setContentView(recyclerView)
                    .setEmptyView(emptyView)
                    .setProgressBar(progressBar)
                    .build();

            Observable<HljHttpData<List<Note>>> nlObservable = NoteApi.getNoteBookNotesObb(
                    noteBookId,
                    userId,
                    null,
                    1,
                    Integer.MAX_VALUE);
            Observable<HljHttpData<List<Merchant>>> mObservable = NoteApi.getNoteBookMerchantsObb(
                    noteBookId);
            Observable<HljHttpData<List<RepliedComment>>> cObservable = NoteApi
                    .getNotebookCommentsObb(
                    noteBookId,
                    "latest",
                    1);
            Observable<HljHttpData<List<Note>>> rnObservable = NoteApi.getNotebookRelativeNotesObb(
                    noteBookId,
                    1);
            Observable<ResultZip> observable = Observable.zip(nlObservable,
                    mObservable,
                    cObservable,
                    rnObservable,
                    new Func4<HljHttpData<List<Note>>, HljHttpData<List<Merchant>>,
                            HljHttpData<List<RepliedComment>>, HljHttpData<List<Note>>,
                            ResultZip>() {
                        @Override
                        public ResultZip call(
                                HljHttpData<List<Note>> noteBookNotesData,
                                HljHttpData<List<Merchant>> merchantsData,
                                HljHttpData<List<RepliedComment>> commentData,
                                HljHttpData<List<Note>> relativeNotesData) {
                            ResultZip resultZip = new ResultZip();
                            if (noteBookNotesData != null) {
                                resultZip.noteBookNotes = (ArrayList<Note>) noteBookNotesData
                                        .getData();
                            }
                            if (merchantsData != null) {
                                resultZip.merchants = (ArrayList<Merchant>) merchantsData.getData();
                            }
                            resultZip.comments = new ArrayList<>();
                            int commentCount = 0;
                            if (commentData != null) {
                                resultZip.comments.addAll(commentData.getData());
                                commentCount = commentData.getTotalCount();
                            }
                            totalCommentCount = commentCount;
                            int pageCount = 0;
                            if (relativeNotesData != null) {
                                pageCount = relativeNotesData.getPageCount();
                                resultZip.relativeNotes = (ArrayList<Note>) relativeNotesData
                                        .getData();
                            }
                            initPage(pageCount);
                            return resultZip;
                        }
                    });

            observable.subscribe(refreshSubscriber);
        }
    }

    /**
     * 刷新笔记本信息
     */
    private void refreshNotebook() {
        if (notebook != null) {
            noteBookHeaderViewHolder.tvContent.setVisibility(TextUtils.isEmpty(notebook.getDesc()
            ) ? View.GONE : View.VISIBLE);
            noteBookHeaderViewHolder.tvContent.setText(notebook.getDesc());
            noteBookHeaderViewHolder.tvPhotoTitle.setVisibility(TextUtils.isEmpty(notebook
                    .getTitle()) ? View.GONE : View.VISIBLE);
            noteBookHeaderViewHolder.tvPhotoTitle.setText(notebook.getTitle());
            Glide.with(this)
                    .load(ImagePath.buildPath(notebook.getCoverPath())
                            .width(coverWidth)
                            .height(coverHeight)
                            .cropPath())
                    .into(noteBookHeaderViewHolder.imgCover);
            noteBookHeaderViewHolder.tvNick.setText(notebook.getAuthor()
                    .getName());
            noteBookHeaderViewHolder.tvNoteCount.setText(getString(R.string.fmt_note_count___note,
                    notebook.getNoteCount()));
            noteBookHeaderViewHolder.tvPhotoCount.setText(getString(R.string.fmt_photo_count___note,
                    notebook.getPicsCount()));
            if (notebook.getCollectCount() != 0) {
                noteBookHeaderViewHolder.tvCollectCount.setVisibility(View.VISIBLE);
                noteBookHeaderViewHolder.tvCollectCount.setText(getString(R.string
                                .fmt_collect_count___note,
                        notebook.getCollectCount()));
            } else {
                noteBookHeaderViewHolder.tvCollectCount.setVisibility(View.GONE);
            }
            final NoteAuthor author = notebook.getAuthor();
            if (author != null) {
                Glide.with(this)
                        .load(ImagePath.buildPath(author.getAvatar())
                                .width(avatarSize)
                                .height(avatarSize)
                                .cropPath())
                        .apply(new RequestOptions().dontAnimate()
                                .placeholder(R.mipmap.icon_avatar_primary))
                        .into(noteBookHeaderViewHolder.imgAvatar);
                noteBookHeaderViewHolder.tvDate.setText(HljTimeUtils.getWeddingDate(notebook
                        .getAuthor()
                        .getWeddingDay(), author.getIsPending(), author.isGender(), "yyyy/MM/dd"));
                if (TextUtils.isEmpty(author.getSpecialty()) || author.getSpecialty()
                        .equals("普通用户")) {
                    if (author.getMember() != null && author.getMember()
                            .getId() != 0) {
                        noteBookHeaderViewHolder.imgVip.setVisibility(View.VISIBLE);
                        noteBookHeaderViewHolder.imgVip.setImageResource(R.mipmap
                                .icon_member_28_28);
                    } else {
                        noteBookHeaderViewHolder.imgVip.setVisibility(View.GONE);
                    }
                } else {
                    noteBookHeaderViewHolder.imgVip.setVisibility(View.VISIBLE);
                    noteBookHeaderViewHolder.imgVip.setImageResource(R.mipmap
                            .icon_vip_yellow_28_28);
                }
            }
            noteBookHeaderViewHolder.imgAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ARouter.getInstance()
                            .build(RouterPath.IntentPath.Customer.USER_PROFILE)
                            .withLong("id", author.getId())
                            .navigation(NotebookActivity.this);
                }
            });
        }
    }

    private class ResultZip extends HljHttpResultZip {
        @HljRZField
        ArrayList<Note> noteBookNotes;
        @HljRZField
        ArrayList<Merchant> merchants;
        @HljRZField
        ArrayList<RepliedComment> comments;
        @HljRZField
        ArrayList<Note> relativeNotes;
    }

    private void initPage(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSubscriber);
        Observable<HljHttpData<List<Note>>> pageObservable = PaginationTool.buildPagingObservable(
                recyclerView,
                pageCount,
                new PagingListener<HljHttpData<List<Note>>>() {
                    @Override
                    public Observable<HljHttpData<List<Note>>> onNextPage(
                            int page) {
                        Log.d("pagination tool", "on load: " + page);
                        return NoteApi.getNotebookRelativeNotesObb(noteBookId, page);
                    }
                })
                .setLoadView(footerViewHolder.loading)
                .setEndView(footerViewHolder.noMoreHint)
                .build()
                .getPagingObservable()
                .observeOn(AndroidSchedulers.mainThread());

        pageSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<Note>>>() {
                    @Override
                    public void onNext(HljHttpData<List<Note>> data) {
                        adapter.addItems(data.getData());
                    }
                })
                .build();

        pageObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSubscriber);
    }

    private void drawTriangle() {
        int width = coverWidth;
        int height = avatarSize - 2 * avatarOffset;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setColor(ContextCompat.getColor(this, R.color.colorWhite));
        Path path = new Path();
        path.moveTo(0, 0);// 此点为多边形的起点
        path.lineTo(0, height);
        path.lineTo(width, height);
        path.close(); // 使这些点构成封闭的多边形
        canvas.drawPath(path, p);
        noteBookHeaderViewHolder.imgTriangle.setImageBitmap(bitmap);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case EDIT_NOTE_BOOK:
                    getNotebookData();
                    break;
            }
        }
    }

    static class FooterViewHolder {
        @BindView(R2.id.no_more_hint)
        TextView noMoreHint;
        @BindView(R2.id.loading)
        LinearLayout loading;

        FooterViewHolder(View view) {ButterKnife.bind(this, view);}
    }

    @OnClick({R2.id.btn_menu, R2.id.btn_menu2})
    void onMenu() {
        if (notebook == null) {
            return;
        }
        if (moreSettingDlg == null) {
            CustomerUser user = (CustomerUser) UserSession.getInstance()
                    .getUser(this);
            LinkedHashMap<String, View.OnClickListener> map = new LinkedHashMap<>();
            if (user != null && user.getId() == notebook.getAuthor()
                    .getId()) {
                map.put("编辑", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (notebook == null) {
                            return;
                        }
                        moreSettingDlg.cancel();
                        if (AuthUtil.loginBindCheck(NotebookActivity.this)) {
                            // 编辑当前笔记本
                            Intent intent = new Intent(NotebookActivity.this,
                                    NotebookEditActivity.class);
                            intent.putExtra("notebook", notebook);
                            startActivityForResult(intent, EDIT_NOTE_BOOK);
                            moreSettingDlg.cancel();
                        }
                    }
                });
            } else {
                map.put("举报", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (notebook == null) {
                            return;
                        }
                        moreSettingDlg.cancel();
                        if (AuthUtil.loginBindCheck(NotebookActivity.this)) {
                            Intent intent = new Intent(NotebookActivity.this, ReportActivity.class);
                            intent.putExtra("id", noteBookId);
                            intent.putExtra("kind", HljCommon.Report.REPORT_NOTE_BOOK);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right,
                                    R.anim.activity_anim_default);
                        }
                    }
                });
            }
            moreSettingDlg = DialogUtil.createBottomMenuDialog(this, map, null);
        }
        moreSettingDlg.show();
    }

    @OnClick({R2.id.btn_share, R2.id.btn_share2})
    void onShare() {
        if (notebook == null || notebook.getShareInfo() == null) {
            return;
        }
        ShareDialogUtil.onCommonShare(this, notebook.getShareInfo());
    }

    @OnClick({R2.id.btn_back, R2.id.btn_back_2})
    public void onBack(View view) {
        onBackPressed();
    }

    private class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int topSpace;
        private int leftAndRightSpace;

        SpacesItemDecoration() {
            this.topSpace = CommonUtil.dp2px(NotebookActivity.this, 4);
            this.leftAndRightSpace = CommonUtil.dp2px(NotebookActivity.this, 6);
        }

        @Override
        public void getItemOffsets(
                Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            StaggeredGridLayoutManager.LayoutParams lp = (StaggeredGridLayoutManager
                    .LayoutParams) view.getLayoutParams();
            int top = 0;
            int left = 0;
            int right = 0;
            int headerCount = adapter.getHeaderCount();
            int position = parent.getChildAdapterPosition(view);
            if (position >= headerCount && position < parent.getAdapter()
                    .getItemCount() - 1) {
                top = position > headerCount + 1 ? -topSpace : topSpace / 2;
                left = lp.getSpanIndex() == 0 ? leftAndRightSpace : 0;
                right = lp.getSpanIndex() == 0 ? 0 : leftAndRightSpace;
            }
            outRect.set(left, top, right, 0);
        }
    }

    class NoteBookHeaderViewHolder {
        @BindView(R2.id.img_cover)
        ImageView imgCover;
        @BindView(R2.id.img_triangle)
        ImageView imgTriangle;
        @BindView(R2.id.img_avatar)
        RoundedImageView imgAvatar;
        @BindView(R2.id.img_vip)
        ImageView imgVip;
        @BindView(R2.id.avatar_layout)
        RelativeLayout avatarLayout;
        @BindView(R2.id.cover_layout)
        RelativeLayout coverLayout;
        @BindView(R2.id.tv_nick)
        TextView tvNick;
        @BindView(R2.id.tv_date)
        TextView tvDate;
        @BindView(R2.id.tv_photo_title)
        TextView tvPhotoTitle;
        @BindView(R2.id.tv_note_count)
        TextView tvNoteCount;
        @BindView(R2.id.tv_photo_count)
        TextView tvPhotoCount;
        @BindView(R2.id.tv_collect_count)
        TextView tvCollectCount;
        @BindView(R2.id.tv_content)
        TextView tvContent;

        NoteBookHeaderViewHolder(View view) {
            ButterKnife.bind(this, view);
            avatarLayout.getLayoutParams().height = avatarSize;
            imgTriangle.getLayoutParams().height = avatarSize - 2 * avatarOffset;
            imgAvatar.getLayoutParams().width = avatarSize;
            imgAvatar.getLayoutParams().height = avatarSize;
            imgAvatar.setCornerRadius(avatarSize / 2);
            coverLayout.getLayoutParams().height = coverHeight;
        }
    }

    static class CommentHeaderViewHolder {
        @BindView(R2.id.tv_relevant_header)
        TextView tvRelevantHeader;
        @BindView(R2.id.relevant_header_layout)
        LinearLayout relevantHeaderLayout;
        @BindView(R2.id.tv_comment_tip)
        TextView tvCommentTip;
        @BindView(R2.id.comment_layout)
        RelativeLayout commentLayout;
        @BindView(R2.id.comment_empty_layout)
        View commentEmptyLayout;
        @BindView(R2.id.comment_header_view)
        View commentHeaderView;

        CommentHeaderViewHolder(View view) {
            ButterKnife.bind(this, view);
            relevantHeaderLayout.setVisibility(View.VISIBLE);
            tvRelevantHeader.setText("评论");
            commentLayout.setVisibility(View.GONE);
        }
    }

    static class CommentFooterViewHolder {
        @BindView(R2.id.line_layout)
        View lineLayout;
        @BindView(R2.id.tv_look_all)
        TextView tvLookAll;
        @BindView(R2.id.note_comment_footer_view)
        View noteCommentFooterView;

        CommentFooterViewHolder(View view) {ButterKnife.bind(this, view);}
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(refreshSubscriber, pageSubscriber, notebookSubscriber);
    }

    static class RelevantNoteHeaderViewHolder {
        @BindView(R2.id.relevant_note_header_layout)
        LinearLayout relevantNoteHeaderLayout;

        RelevantNoteHeaderViewHolder(View view) {ButterKnife.bind(this, view);}
    }
}
