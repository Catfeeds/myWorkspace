package com.hunliji.hljnotelibrary.views.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.models.Author;
import com.hunliji.hljcommonlibrary.models.Media;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.PostCollectBody;
import com.hunliji.hljcommonlibrary.models.RepliedComment;
import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljcommonlibrary.models.Video;
import com.hunliji.hljcommonlibrary.models.note.Note;
import com.hunliji.hljcommonlibrary.models.note.NoteInspiration;
import com.hunliji.hljcommonlibrary.models.note.NoteMark;
import com.hunliji.hljcommonlibrary.models.note.NoteSpot;
import com.hunliji.hljcommonlibrary.models.product.ShopProduct;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.HljDialogUtil;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljcommonlibrary.views.widgets.NoUnderlineSpan;
import com.hunliji.hljcommonlibrary.views.widgets.NoteCirclePageIndicator;
import com.hunliji.hljemojilibrary.EmojiUtil;
import com.hunliji.hljhttplibrary.api.CommonApi;
import com.hunliji.hljhttplibrary.api.newsearch.NewSearchApi;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResultZip;
import com.hunliji.hljhttplibrary.entities.HljRZField;
import com.hunliji.hljhttplibrary.utils.AuthUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljnotelibrary.HljNote;
import com.hunliji.hljnotelibrary.R;
import com.hunliji.hljnotelibrary.R2;
import com.hunliji.hljnotelibrary.adapters.NoteDetailRecyclerAdapter;
import com.hunliji.hljnotelibrary.adapters.NoteInspirationAdapter;
import com.hunliji.hljnotelibrary.api.NoteApi;
import com.hunliji.hljnotelibrary.models.NotebookType;
import com.hunliji.hljnotelibrary.views.activities.MerchantNoteActivity;
import com.hunliji.hljnotelibrary.views.activities.NoteCommentListActivity;
import com.hunliji.hljnotelibrary.views.activities.NoteCreateWeddingPosterActivity;
import com.hunliji.hljnotelibrary.views.activities.NoteDetailActivity;
import com.hunliji.hljnotelibrary.views.activities.NoteMarkDetailActivity;
import com.hunliji.hljnotelibrary.views.activities.NotebookActivity;
import com.hunliji.hljnotelibrary.views.activities.PostNoteCommentActivity;
import com.hunliji.hljsharelibrary.HljShare;
import com.hunliji.hljsharelibrary.utils.ShareDialogUtil;
import com.hunliji.hljtrackerlibrary.HljTracker;
import com.hunliji.hljtrackerlibrary.TrackerHelper;
import com.hunliji.hljvideolibrary.activities.VideoPreviewActivity;
import com.makeramen.rounded.RoundedImageView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.functions.Func3;
import rx.schedulers.Schedulers;

/**
 * Created by mo_yu on 2017/6/24.笔记详情
 */

public class NoteDetailFragment extends RefreshFragment implements NoteDetailRecyclerAdapter
        .OnUserNoteListener, NoteDetailRecyclerAdapter.OnCommentReplyListener,
        NoteInspirationAdapter.OnCollectClickListener {

    private final static int COMMENT_REQUEST = 1;

    @BindView(R2.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R2.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R2.id.btn_scroll_top)
    ImageButton btnScrollTop;
    Unbinder unbinder;
    private long noteId;
    private long inspirationId;
    private float alpha;
    private int avatarWidth;
    private int toolbarHeight;
    private int inspirationPosition;//大图模式下，笔记中点击的灵感位置
    private int collectCount;
    private int totalCommentCount;
    private int screenShotWidth;
    private int screenShotHeight;
    private boolean isHide;
    private boolean isMerchant;
    private boolean isLargerView;//大图模式
    private ArrayList<Merchant> merchants;
    private ArrayList<ShopProduct> products;
    private ArrayList<Note> userNotes;
    private ArrayList<RepliedComment> comments;
    private ArrayList<Note> relativeNotes;
    private Note note;
    private User user;

    private NoteDetailRecyclerAdapter adapter;
    private NoteInspirationAdapter pagerAdapter;
    private NoteDetailViewHolder noteDetailViewHolder;
    private FooterViewHolder footerViewHolder;
    private CommentHeaderViewHolder commentHeaderViewHolder;
    private CommentFooterViewHolder commentFooterViewHolder;
    private RelevantHeaderViewHolder relevantHeaderViewHolder;
    private View bottomView;
    private BottomViewHolder bottomViewHolder;
    private Dialog menuDialog;
    private Button btnMenu;

    private HljHttpSubscriber deleteSubscriber;
    private HljHttpSubscriber initSubscriber;
    private HljHttpSubscriber userNotesSubscriber;
    private HljHttpSubscriber pageSubscriber;
    private HljHttpSubscriber collectSubscriber;
    private HljHttpSubscriber followSubscriber;
    private HljHttpSubscriber refreshCommentSubscriber;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initValue();
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_note_detail___note, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        if (getArguments() != null) {
            noteId = getArguments().getLong("note_id", 0);
            isLargerView = getArguments().getBoolean("is_larger_view");
            inspirationId = getArguments().getLong("inspiration_id", 0);
            note = getArguments().getParcelable("note");
            inspirationPosition = getArguments().getInt("inspiration_position", 0);
        }
        initView();
        initBottomView();
        refresh();
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CommonUtil.unSubscribeSubs(initSubscriber,
                userNotesSubscriber,
                pageSubscriber,
                deleteSubscriber,
                followSubscriber,
                collectSubscriber,
                refreshCommentSubscriber);
    }

    public static NoteDetailFragment newInstance(Bundle args) {
        NoteDetailFragment fragment = new NoteDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void initBottomView() {
        bottomView = View.inflate(getContext(), R.layout.footer_user_action___note, null);
        bottomViewHolder = new BottomViewHolder(bottomView);
        if (getNoteDetailActivity() != null) {
            getNoteDetailActivity().refreshBottomView();
        }
    }

    private void initValue() {
        merchants = new ArrayList<>();
        products = new ArrayList<>();
        userNotes = new ArrayList<>();
        comments = new ArrayList<>();
        relativeNotes = new ArrayList<>();

        avatarWidth = CommonUtil.dp2px(getContext(), 40);
        toolbarHeight = CommonUtil.dp2px(getContext(), 45);
        screenShotWidth = CommonUtil.getDeviceSize(getContext()).x;
        screenShotHeight = Math.round(screenShotWidth * 9.0f / 16.0f);
        isMerchant = HljNote.isMerchant(getContext());
        collectCount = -1;
        user = UserSession.getInstance()
                .getUser(getContext());

    }

    private void initView() {
        adapter = new NoteDetailRecyclerAdapter(getContext());
        final View noteDetailHeaderView = View.inflate(getContext(),
                R.layout.note_detail_header___note,
                null);
        noteDetailViewHolder = new NoteDetailViewHolder(noteDetailHeaderView);
        View footerView = View.inflate(getContext(), R.layout.hlj_foot_no_more___cm, null);
        footerViewHolder = new FooterViewHolder(footerView);
        View commentHeaderView = View.inflate(getContext(),
                R.layout.note_comment_header___note,
                null);
        commentHeaderViewHolder = new CommentHeaderViewHolder(commentHeaderView);
        View commentFooterView = View.inflate(getContext(),
                R.layout.note_comment_footer___note,
                null);
        commentFooterViewHolder = new CommentFooterViewHolder(commentFooterView);
        //商家精简版不需要相关商家，笔记，婚品信息,不显示向楼主咨询一栏
        if (isMerchant) {
            commentHeaderViewHolder.commentLayout.setVisibility(View.GONE);
            commentHeaderViewHolder.commentTagView.setVisibility(View.GONE);
        } else {
            commentHeaderViewHolder.commentLayout.setVisibility(View.VISIBLE);
            adapter.setMerchants(merchants);
            adapter.setUserNotes(userNotes);
            adapter.setProducts(products);
            adapter.setOnUserNoteListener(this);
        }
        //大图模式和商家精简版不显示看了又看,评论个数限制为10个
        if (isLargerView || isMerchant) {
            commentHeaderViewHolder.tvRelevantHeader.setText("评论");
            adapter.setMaxCommentCount(10);
        } else {
            View relevantNoteHeaderView = View.inflate(getContext(),
                    R.layout.relevant_note_header___note,
                    null);
            relevantHeaderViewHolder = new RelevantHeaderViewHolder(relevantNoteHeaderView);
            adapter.setRelevantNoteHeaderView(relevantNoteHeaderView);
            adapter.setRelativeNotes(relativeNotes);
            commentHeaderViewHolder.tvRelevantHeader.setText("评论");
            adapter.setFooterView(footerView);
            adapter.setMaxCommentCount(3);
        }
        adapter.setComments(comments);
        adapter.setEntityType(HljNote.NOTE_TYPE);
        adapter.setNoteHeaderView(noteDetailHeaderView);
        adapter.setCommentHeaderView(commentHeaderView);
        adapter.setCommentFooterView(commentFooterView);
        adapter.setOnCommentReplyListener(this);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setItemPrefetchEnabled(false);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setPadding(0, 0, 0, CommonUtil.dp2px(getContext(), 44));
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
                if (!isVisibleToUser()) {
                    return;
                }
                NoteDetailActivity noteDetailActivity = getNoteDetailActivity();
                if (noteDetailActivity == null) {
                    return;
                }
                int disHeight = 0;
                if (noteDetailViewHolder != null && noteDetailViewHolder.viewPager != null) {
                    if (note != null && note.getCover()
                            .getType() == Media.TYPE_VIDEO) {
                        disHeight = noteDetailViewHolder.videoLayout.getHeight() -
                                HljBaseActivity.getStatusBarHeight(
                                getContext()) - toolbarHeight;
                    } else {
                        disHeight = noteDetailViewHolder.viewPager.getHeight() - HljBaseActivity
                                .getStatusBarHeight(
                                getContext()) - toolbarHeight;
                    }
                }
                StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager)
                        recyclerView.getLayoutManager();
                if (note != null && layoutManager != null) {
                    int[] into = layoutManager.findFirstVisibleItemPositions(new int[]{0, 1});
                    if (into != null && into.length > 0) {
                        if (into[0] > 0) {
                            alpha = 1;
                            if (into[0] >= adapter.getHeaderCount()) {
                                noteDetailActivity.hideBottomView();
                                noteDetailActivity.setTitleText("看了又看");
                                recyclerView.setPadding(0,
                                        0,
                                        0,
                                        CommonUtil.dp2px(getContext(), 10));
                            } else {
                                noteDetailActivity.showBottomView();
                                noteDetailActivity.setTitleText("笔记详情");
                            }
                        } else {
                            if (disHeight > 0) {
                                alpha = (float) -layoutManager.getChildAt(0)
                                        .getTop() / disHeight;
                            } else {
                                alpha = 1;
                            }
                        }
                        if (into[0] < adapter.getHeaderCount() + 10) {
                            if (!isHide) {
                                hideFiltrateAnimation();
                            }
                        } else if (isHide) {
                            if (btnScrollTop.getVisibility() == View.GONE) {
                                btnScrollTop.setVisibility(View.VISIBLE);
                            }
                            showFiltrateAnimation();
                        }
                    } else {
                        alpha = 1;
                    }
                } else {
                    alpha = 0;
                }
                noteDetailActivity.setToolbarAlpha(alpha);
            }
        });
    }

    private void refreshNoteDetail() {
        if (note != null) {
            adapter.setNotebookType(note.getNotebookType());
            adapter.setGender(note.getAuthor()
                    .isGender());
            if (note.getCover()
                    .getType() == Media.TYPE_VIDEO) {
                //视频类笔记
                noteDetailViewHolder.photoView.setVisibility(View.GONE);
                noteDetailViewHolder.videoLayout.setVisibility(View.VISIBLE);
                final Video video = note.getCover()
                        .getVideo();
                noteDetailViewHolder.imgScreenShot.setColorFilter(Color.parseColor("#7f000000"));
                noteDetailViewHolder.imgScreenShot.getLayoutParams().width = screenShotWidth;
                noteDetailViewHolder.imgScreenShot.getLayoutParams().height = screenShotHeight;
                Glide.with(getContext())
                        .load(ImagePath.buildPath(video.getScreenShot())
                                .width(screenShotWidth)
                                .height(screenShotHeight)
                                .cropPath())
                        .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image)
                                .error(R.mipmap.icon_empty_image))
                        .into(noteDetailViewHolder.imgScreenShot);
                noteDetailViewHolder.videoLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (video.getPersistent() == null) {
                            DialogUtil.createSingleButtonDialog(getContext(),
                                    getString(R.string.msg_video_trans_coding),
                                    null,
                                    null)
                                    .show();
                            return;
                        }
                        String videoPath = video.getVideoPath();
                        if (!TextUtils.isEmpty(videoPath)) {
                            Intent intent = new Intent(getContext(), VideoPreviewActivity.class);
                            intent.putExtra("uri", Uri.parse(videoPath));
                            startActivity(intent);
                            getActivity().overridePendingTransition(R.anim.slide_in_up,
                                    R.anim.activity_anim_default);
                        }
                    }
                });
                if (!CommonUtil.isCollectionEmpty(note.getInspirations())) {
                    boolean isFollow = note.getInspirations()
                            .get(inspirationPosition)
                            .isFollowed();
                    bottomViewHolder.tvCollect.setText(getString(R.string.fmt_common_count___note,
                            "收藏",
                            note.getCollectCount()));
                    bottomViewHolder.imgCollect.setImageResource(isFollow ? R.mipmap
                            .icon_collect_primary_40_40 : R.mipmap.icon_collect_white_40_40);
                }
            } else if (CommonUtil.isCollectionEmpty(note.getInspirations())) {
                noteDetailViewHolder.photoView.setVisibility(View.GONE);
                noteDetailViewHolder.toolbarHintLayout.setVisibility(View.VISIBLE);
            } else {
                noteDetailViewHolder.photoView.setVisibility(View.VISIBLE);
                noteDetailViewHolder.toolbarHintLayout.setVisibility(View.GONE);
                if (pagerAdapter == null) {
                    pagerAdapter = new NoteInspirationAdapter(getContext(),
                            note.getInspirations(),
                            noteDetailViewHolder.viewPager,
                            noteDetailViewHolder.flowIndicator,
                            noteDetailViewHolder.imgInspirationCollect);
                    pagerAdapter.setImgBottomCollect(bottomViewHolder.imgCollect);
                    noteDetailViewHolder.viewPager.setAdapter(pagerAdapter);
                    pagerAdapter.setOnCollectClickListener(this);
                } else {
                    pagerAdapter.setDate(note.getInspirations());
                }
                if (note.getInspirations()
                        .size() <= 1) {
                    noteDetailViewHolder.flowIndicator.setVisibility(View.GONE);
                } else {
                    noteDetailViewHolder.flowIndicator.setVisibility(View.VISIBLE);
                }
                noteDetailViewHolder.flowIndicator.setPagerAdapter(pagerAdapter);
                noteDetailViewHolder.flowIndicator.setCurrentItem(0);
                noteDetailViewHolder.viewPager.setCurrentItem(0);
                if (!CommonUtil.isCollectionEmpty(note.getInspirations())) {
                    if (inspirationId != 0) {
                        inspirationPosition = getInspirationPosition(inspirationId);
                    }
                    boolean isFollow = note.getInspirations()
                            .get(inspirationPosition)
                            .isFollowed();
                    bottomViewHolder.tvCollect.setText(getString(R.string.fmt_common_count___note,
                            "收藏",
                            note.getCollectCount()));
                    bottomViewHolder.imgCollect.setImageResource(isFollow ? R.mipmap
                            .icon_collect_primary_40_40 : R.mipmap.icon_collect_white_40_40);
                    //收藏的灵感到笔记详情，要定位到收藏的灵感
                    if (inspirationPosition != 0) {
                        noteDetailViewHolder.viewPager.setCurrentItem(inspirationPosition);
                    }
                }
            }

            if (note.getMerchant() != null && note.getMerchant()
                    .getId() != 0) {
                refreshMerchantInfo(note.getMerchant());
            } else {
                refreshUserInfo(note.getAuthor());
            }
            //关注按钮状态
            if (user != null && user.getId() == note.getAuthor()
                    .getId()) {
                noteDetailViewHolder.btnMerchantFollow.setVisibility(View.GONE);
                noteDetailViewHolder.btnUserFollow.setVisibility(View.GONE);
            }
            //标题，内容等
            if (!TextUtils.isEmpty(note.getTitle())) {
                noteDetailViewHolder.tvNoteTitle.setVisibility(View.VISIBLE);
                noteDetailViewHolder.tvNoteTitle.setText(EmojiUtil.parseEmojiByText2(getContext(),
                        note.getTitle(),
                        CommonUtil.dp2px(getContext(), 16)));
            } else {
                noteDetailViewHolder.tvNoteTitle.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(note.getContent())) {
                noteDetailViewHolder.tvNoteContent.setVisibility(View.VISIBLE);
                noteDetailViewHolder.tvNoteContent.setText(EmojiUtil.parseEmojiByText2(getContext(),
                        note.getContent(),
                        CommonUtil.dp2px(getContext(), 16)));
            } else {
                noteDetailViewHolder.tvNoteContent.setVisibility(View.GONE);
            }
            if (TextUtils.isEmpty(note.getCity()) || note.getCity()
                    .equals("false")) {
                noteDetailViewHolder.imgNoteAddress.setVisibility(View.GONE);
                noteDetailViewHolder.tvNoteAddress.setVisibility(View.GONE);
            } else {
                noteDetailViewHolder.imgNoteAddress.setVisibility(View.VISIBLE);
                noteDetailViewHolder.tvNoteAddress.setVisibility(View.VISIBLE);
                noteDetailViewHolder.tvNoteAddress.setText(note.getCity());
            }
            noteDetailViewHolder.tvNoteTime.setText(HljTimeUtils.getShowTime(getContext(),
                    note.getCreatedAt()));
            commentHeaderViewHolder.tvCommentTip.setText(getCommentHeaderHint(note
                    .getNotebookType()));
            bottomViewHolder.tvCommentCount.setText(getString(R.string.fmt_common_count___note,
                    "评论",
                    note.getCommentCount()));
        }
    }

    //非婚礼人显示用户
    private void refreshUserInfo(final Author author) {
        if (author == null) {
            return;
        }
        noteDetailViewHolder.userInfoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goUserProfile(author);
            }
        });
        noteDetailViewHolder.merchantInfoLayout.setVisibility(View.GONE);
        noteDetailViewHolder.userInfoLayout.setVisibility(View.VISIBLE);
        setUserFollowView(false);
        noteDetailViewHolder.btnUserFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //关注用户
                if (AuthUtil.loginBindCheck(getContext())) {
                    if (followSubscriber == null || followSubscriber.isUnsubscribed()) {
                        followSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                                .setProgressDialog(DialogUtil.createProgressDialog(getContext()))
                                .setOnNextListener(new SubscriberOnNextListener() {
                                    @Override
                                    public void onNext(Object o) {
                                        author.setFollowing(!author.isFollowing());
                                        setUserFollowView(true);
                                    }
                                })
                                .build();
                        CommonApi.followOrUnfollowUserObb(author.getId())
                                .subscribe(followSubscriber);
                    }
                }
            }
        });
        noteDetailViewHolder.tvAuthName.setText(author.getName());
        if (!TextUtils.isEmpty(author.getHometown())) {
            noteDetailViewHolder.tvAuthAddressAndNotes.setText(getString(R.string
                            .fmt_address_and_note_count___note,
                    author.getHometown(),
                    author.getNoteCount()));
        } else {
            noteDetailViewHolder.tvAuthAddressAndNotes.setText(getString(R.string
                            .label_note_count___note,
                    author.getNoteCount()));
        }
        if (TextUtils.isEmpty(author.getSpecialty()) || author.getSpecialty()
                .equals("普通用户")) {
            if (author.getMember() != null && author.getMember()
                    .getId() != 0) {
                noteDetailViewHolder.imgVip.setVisibility(View.VISIBLE);
                noteDetailViewHolder.imgVip.setImageResource(R.mipmap.icon_member_28_28);
            } else {
                noteDetailViewHolder.imgVip.setVisibility(View.GONE);
            }
        } else {
            noteDetailViewHolder.imgVip.setVisibility(View.VISIBLE);
            noteDetailViewHolder.imgVip.setImageResource(R.mipmap.icon_vip_yellow_28_28);
        }

        Glide.with(getContext())
                .load(ImagePath.buildPath(author.getAvatar())
                        .width(avatarWidth)
                        .height(avatarWidth)
                        .cropPath())
                .apply(new RequestOptions().dontAnimate())
                .into(noteDetailViewHolder.rivAuthAvatar);
    }

    //婚礼人显示商家
    private void refreshMerchantInfo(final Merchant merchant) {
        if (merchant == null) {
            return;
        }
        noteDetailViewHolder.merchantInfoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goMerchant(merchant, 0);
            }
        });
        noteDetailViewHolder.merchantInfoLayout.setVisibility(View.VISIBLE);
        noteDetailViewHolder.userInfoLayout.setVisibility(View.GONE);
        if (isMerchant) {
            noteDetailViewHolder.btnMerchantFollow.setVisibility(View.GONE);
        } else {
            noteDetailViewHolder.btnMerchantFollow.setVisibility(View.VISIBLE);
            noteDetailViewHolder.btnMerchantFollow.setText(note.getMerchant()
                    .isCollected() ? R.string.label_go_merchant___cm : R.string.label_follow___cv);
            noteDetailViewHolder.btnMerchantFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!note.getMerchant()
                            .isCollected()) {
                        //关注商家
                        if (AuthUtil.loginBindCheck(getContext())) {
                            if (followSubscriber == null || followSubscriber.isUnsubscribed()) {
                                followSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                                        .setProgressDialog(DialogUtil.createProgressDialog(
                                                getContext()))
                                        .setOnNextListener(new SubscriberOnNextListener() {
                                            @Override
                                            public void onNext(Object o) {
                                                noteDetailViewHolder.btnMerchantFollow.setClickable(

                                                        false);
                                                note.getMerchant()
                                                        .setCollected(true);
                                                noteDetailViewHolder.btnMerchantFollow.setText(R
                                                        .string.label_go_merchant___cm);

                                                ToastUtil.showCustomToast(getContext(),
                                                        R.string.msg_success_to_follow___cm);
                                            }
                                        })
                                        .build();
                                CommonApi.postMerchantFollowObb(note.getMerchant()
                                        .getId())
                                        .subscribe(followSubscriber);
                            }
                        }
                    } else {
                        goMerchant(merchant, 0);
                    }
                }
            });
        }
        Glide.with(getContext())
                .load(ImagePath.buildPath(merchant.getLogoPath())
                        .width(CommonUtil.dp2px(getContext(), 30))
                        .cropPath())
                .apply(new RequestOptions().dontAnimate()
                        .placeholder(com.hunliji.hljcommonviewlibrary.R.mipmap.icon_avatar_primary)
                        .error(com.hunliji.hljcommonviewlibrary.R.mipmap.icon_avatar_primary))
                .into(noteDetailViewHolder.imgMerchantAvatar);
        Author author = note.getAuthor();
        noteDetailViewHolder.tvMerchantName.setText(merchant.getName());
        if (merchant.getShopType() == Merchant.SHOP_TYPE_PRODUCT) { //婚品商家
            noteDetailViewHolder.tvCommentCount.setVisibility(View.GONE);
        } else {
            StringBuilder str = new StringBuilder();
            str.append(author.getHometown());
            if (!TextUtils.isEmpty(author.getHometown()) && !TextUtils.isEmpty(merchant
                    .getPropertyName())) {
                str.append("·");
            }
            str.append(merchant.getPropertyName());
            noteDetailViewHolder.tvCommentCount.setVisibility(View.VISIBLE);
            noteDetailViewHolder.tvCommentCount.setText(str);
        }
    }

    private void refreshCommentView() {
        if (comments.isEmpty()) {
            if (isMerchant) {
                commentHeaderViewHolder.merchantCommentEmptyTip.setVisibility(View.VISIBLE);
                commentHeaderViewHolder.customerCommentEmptyTip.setVisibility(View.GONE);
            } else {
                commentHeaderViewHolder.merchantCommentEmptyTip.setVisibility(View.GONE);
                commentHeaderViewHolder.customerCommentEmptyTip.setVisibility(View.VISIBLE);
            }
            //评论为空时，添加间隔
            commentHeaderViewHolder.commentEmptyLayout.setVisibility(View.VISIBLE);
        } else {
            commentHeaderViewHolder.commentEmptyLayout.setVisibility(View.GONE);
        }
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

    /**
     * 笔记标签去重
     */
    private void refreshNoteMarks() {
        final LinkedHashMap<String, NoteMark> markHashMap = new LinkedHashMap<>();
        Observable.concat(Observable.from(note.getNoteMarks()),
                Observable.from(note.getInspirations())
                        .concatMap(new Func1<NoteInspiration, Observable<NoteMark>>() {
                            @Override
                            public Observable<NoteMark> call(NoteInspiration noteInspiration) {
                                return Observable.from(noteInspiration.getNoteSpots())
                                        .map(new Func1<NoteSpot, NoteMark>() {


                                            @Override
                                            public NoteMark call(NoteSpot noteSpot) {
                                                return noteSpot.getNoteMark();
                                            }
                                        });
                            }
                        }))
                .filter(new Func1<NoteMark, Boolean>() {
                    @Override
                    public Boolean call(NoteMark noteMark) {
                        NoteMark mark = markHashMap.get(noteMark.getName());
                        return !TextUtils.isEmpty(noteMark.getName()) && (mark == null || mark
                                .getId() == 0);
                    }
                })
                .subscribe(new Action1<NoteMark>() {
                    @Override
                    public void call(NoteMark noteMark) {
                        markHashMap.put(noteMark.getName(), noteMark);
                    }
                });

        if (markHashMap.size() == 0) {
            noteDetailViewHolder.flNoteMarks.setVisibility(View.GONE);
        } else {
            noteDetailViewHolder.flNoteMarks.setVisibility(View.VISIBLE);
            SpannableStringBuilder marksSpanStr = new SpannableStringBuilder();
            for (String key : markHashMap.keySet()) {
                final NoteMark noteMark = markHashMap.get(key);
                String markStr = "#" + noteMark.getName() + "   ";
                SpannableString sp = new SpannableString(markStr);
                sp.setSpan(new NoUnderlineSpan() {
                    @Override
                    public void onClick(View widget) {
                        if (!isMerchant) {
                            if (noteMark.getId() != 0) {
                                Intent intent = new Intent(getContext(),
                                        NoteMarkDetailActivity.class);
                                intent.putExtra(NoteMarkDetailActivity.ARG_MARK_ID,
                                        noteMark.getId());
                                startActivity(intent);
                            } else {
                                ARouter.getInstance()
                                        .build(RouterPath.IntentPath.Customer
                                                .NEW_SEARCH_RESULT_ACTIVITY)
                                        .withString(NewSearchApi.ARG_KEY_WORD, noteMark.getName())
                                        .withSerializable(NewSearchApi.ARG_SEARCH_TYPE,
                                                NewSearchApi.SearchType.SEARCH_TYPE_NOTE)
                                        .withTransition(R.anim.slide_in_right,
                                                R.anim.activity_anim_default)
                                        .navigation(getContext());
                            }
                        }
                    }
                }, 0, markStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                marksSpanStr.append(sp);
                noteDetailViewHolder.flNoteMarks.setMovementMethod(LinkMovementMethod.getInstance
                        ());
                noteDetailViewHolder.flNoteMarks.setLinkTextColor(ContextCompat.getColor
                        (getContext(),
                        isMerchant ? R.color.colorGray : R.color.colorLink));
                //开始响应点击事件
                noteDetailViewHolder.flNoteMarks.setText(marksSpanStr);
            }
        }
    }

    private void initLoad() {
        if (initSubscriber == null || initSubscriber.isUnsubscribed()) {
            initSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                    .setOnNextListener(new SubscriberOnNextListener<ResultZip>() {
                        @Override
                        public void onNext(ResultZip resultZip) {
                            refreshNoteMarks();
                            onGetUserNotes();
                            onRefreshCommentData();
                            if (resultZip.merchants != null) {
                                merchants.clear();
                                merchants.addAll(resultZip.merchants);
                            }
                            if (resultZip.products != null) {
                                products.clear();
                                products.addAll(resultZip.products);
                            }
                            if (resultZip.relativeNotes != null) {
                                relativeNotes.clear();
                                relativeNotes.addAll(resultZip.relativeNotes);
                            }
                            if (relevantHeaderViewHolder != null) {
                                if (CommonUtil.isCollectionEmpty(relativeNotes)) {
                                    relevantHeaderViewHolder.relevantNoteHeaderLayout.setVisibility(
                                            View.GONE);
                                } else {
                                    relevantHeaderViewHolder.relevantNoteHeaderLayout.setVisibility(
                                            View.VISIBLE);
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
                    })
                    .setDataNullable(true)
                    .setProgressBar(progressBar)
                    .setContentView(recyclerView)
                    .build();

            Observable<HljHttpData<List<Merchant>>> mObservable = NoteApi.getNoteMerchantsObb
                    (noteId);
            Observable<HljHttpData<List<ShopProduct>>> pObservable = NoteApi.getNoteProductsObb(
                    noteId);
            Observable<HljHttpData<List<Note>>> rnObservable = NoteApi.getRelativeNotesObb(noteId,
                    1);
            Observable<ResultZip> observable = Observable.zip(mObservable,
                    pObservable,
                    rnObservable,
                    new Func3<HljHttpData<List<Merchant>>, HljHttpData<List<ShopProduct>>,
                            HljHttpData<List<Note>>, ResultZip>() {
                        @Override
                        public ResultZip call(
                                HljHttpData<List<Merchant>> merchantsData,
                                HljHttpData<List<ShopProduct>> productData,
                                HljHttpData<List<Note>> relativeNotesData) {
                            ResultZip resultZip = new ResultZip();
                            if (merchantsData != null) {
                                resultZip.merchants = (ArrayList<Merchant>) merchantsData.getData();
                            }
                            if (productData != null) {
                                resultZip.products = (ArrayList<ShopProduct>) productData.getData();
                            }
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

            observable.subscribe(initSubscriber);
        }
    }

    //大图模式不显示看了又看
    private void initLargerViewLoad() {
        if (initSubscriber == null || initSubscriber.isUnsubscribed()) {
            initSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                    .setOnNextListener(new SubscriberOnNextListener<ResultZip>() {
                        @Override
                        public void onNext(ResultZip resultZip) {
                            refreshNoteMarks();
                            onGetUserNotes();
                            onRefreshCommentData();
                            if (resultZip.merchants != null) {
                                merchants.clear();
                                merchants.addAll(resultZip.merchants);
                            }
                            if (resultZip.products != null) {
                                products.clear();
                                products.addAll(resultZip.products);
                            }
                            if (resultZip.relativeNotes != null) {
                                relativeNotes.clear();
                                relativeNotes.addAll(resultZip.relativeNotes);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    })
                    .setDataNullable(true)
                    .setProgressBar(progressBar)
                    .setContentView(recyclerView)
                    .setEmptyView(emptyView)
                    .build();

            Observable<HljHttpData<List<Merchant>>> mObservable = NoteApi.getNoteMerchantsObb
                    (noteId);
            Observable<HljHttpData<List<ShopProduct>>> pObservable = NoteApi.getNoteProductsObb(
                    noteId);
            Observable<ResultZip> observable = Observable.zip(mObservable,
                    pObservable,
                    new Func2<HljHttpData<List<Merchant>>, HljHttpData<List<ShopProduct>>,
                            ResultZip>() {
                        @Override
                        public ResultZip call(
                                HljHttpData<List<Merchant>> merchantsData,
                                HljHttpData<List<ShopProduct>> productData) {
                            ResultZip resultZip = new ResultZip();
                            if (merchantsData != null) {
                                resultZip.merchants = (ArrayList<Merchant>) merchantsData.getData();
                            }
                            if (productData != null) {
                                resultZip.products = (ArrayList<ShopProduct>) productData.getData();
                            }
                            return resultZip;
                        }
                    });

            observable.subscribe(initSubscriber);
        }
    }

    //商家精简版只需要获取评论
    private void initMerchantLoad() {
        recyclerView.setVisibility(View.VISIBLE);
        onRefreshCommentData();
    }

    @OnClick(R2.id.btn_scroll_top)
    public void onBtnScrollTop() {
        if (recyclerView == null) {
            return;
        }
        recyclerView.scrollToPosition(5);
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                recyclerView.smoothScrollToPosition(0);
            }
        });
    }


    private class ResultZip extends HljHttpResultZip {
        @HljRZField
        ArrayList<Merchant> merchants;
        @HljRZField
        ArrayList<ShopProduct> products;
        @HljRZField
        ArrayList<RepliedComment> comments;
        @HljRZField
        ArrayList<Note> relativeNotes;
    }

    private void onGetUserNotes() {
        if (userNotesSubscriber == null || userNotesSubscriber.isUnsubscribed()) {
            userNotesSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<Note>>>() {
                        @Override
                        public void onNext(HljHttpData<List<Note>> notesData) {
                            userNotes.clear();
                            userNotes.addAll(notesData.getData());
                            adapter.setTotalUserNoteCount(notesData.getTotalCount());
                            adapter.notifyDataSetChanged();
                        }
                    })
                    .build();
            NoteApi.getNoteBookNotesObb(note.getNoteBookId(),
                    note.getAuthor()
                            .getId(),
                    null,
                    1,
                    10)
                    .subscribe(userNotesSubscriber);
        }
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
                        return NoteApi.getRelativeNotesObb(noteId, page);
                    }
                })
                .setLoadView(footerViewHolder.loading)
                .setEndView(footerViewHolder.noMoreHint)
                .build()
                .getPagingObservable()
                .observeOn(AndroidSchedulers.mainThread());

        pageSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
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

    private void onRefreshCommentData() {
        if (refreshCommentSubscriber == null || refreshCommentSubscriber.isUnsubscribed()) {
            refreshCommentSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                    .setOnNextListener(new SubscriberOnNextListener<ResultZip>() {
                        @Override
                        public void onNext(ResultZip resultZip) {
                            if (resultZip.comments != null) {
                                comments.clear();
                                comments.addAll(resultZip.comments);
                            }
                            refreshCommentView();
                            adapter.notifyDataSetChanged();
                        }
                    })
                    .setDataNullable(true)
                    .build();
            Observable<HljHttpData<List<RepliedComment>>> hObservable = CommonApi.getHotCommentsObb(
                    noteId,
                    HljNote.NOTE_TYPE);
            Observable<HljHttpData<List<RepliedComment>>> cObservable = CommonApi
                    .getCommonCommentsObb(
                    noteId,
                    HljNote.NOTE_TYPE,
                    1);
            Observable observable = Observable.zip(cObservable,
                    hObservable,
                    new Func2<HljHttpData<List<RepliedComment>>,
                            HljHttpData<List<RepliedComment>>, ResultZip>() {

                        @Override
                        public ResultZip call(
                                HljHttpData<List<RepliedComment>> hotCommentData,
                                HljHttpData<List<RepliedComment>> commentData) {
                            ResultZip resultZip = new ResultZip();
                            resultZip.comments = new ArrayList<>();
                            if (hotCommentData != null && !CommonUtil.isCollectionEmpty(
                                    hotCommentData.getData())) {
                                resultZip.comments.addAll(hotCommentData.getData());
                            }
                            if (commentData != null && !CommonUtil.isCollectionEmpty(commentData
                                    .getData())) {
                                resultZip.comments.addAll(commentData.getData());
                            }
                            totalCommentCount = note.getCommentCount();
                            return resultZip;
                        }
                    });
            observable.subscribe(refreshCommentSubscriber);
        }
    }

    public void onCollectNoteInspiration() {
        if (note == null || CommonUtil.isCollectionEmpty(note.getInspirations())) {
            return;
        }
        if (note.getCover()
                .getType() == Media.TYPE_VIDEO) {
            onCollectNoteInspiration(note.getInspirations()
                    .get(0), null);
        } else {
            int position = noteDetailViewHolder.viewPager.getCurrentItem();
            onCollectNoteInspiration(note.getInspirations()
                    .get(position), noteDetailViewHolder.imgInspirationCollect);
        }
    }

    /**
     * 取消收藏或收藏灵感
     */
    public void onCollectNoteInspiration(
            final NoteInspiration noteInspiration, final ImageView collectView) {
        if (isMerchant) {
            return;
        }
        if (AuthUtil.loginBindCheck(getContext())) {
            if (collectSubscriber == null || collectSubscriber.isUnsubscribed()) {
                if (getNoteDetailActivity() != null) {
                    getNoteDetailActivity().hideCollectHintView();
                }
                collectSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                        .setOnNextListener(new SubscriberOnNextListener() {
                            @Override
                            public void onNext(Object o) {
                                ToastUtil.showCustomToast(getContext(),
                                        R.string.msg_success_to_un_collect___cm);
                            }
                        })
                        .setOnErrorListener(new SubscriberOnErrorListener() {
                            @Override
                            public void onError(Object o) {
                                setNoteInspirationCollectView(noteInspiration, collectView);
                            }
                        })
                        .build();
                PostCollectBody body = new PostCollectBody();
                body.setId(noteInspiration.getId());
                body.setFollowableType("NoteMedia");
                setNoteInspirationCollectView(noteInspiration, collectView);
                CommonApi.postCollectObb(body, !noteInspiration.isFollowed())
                        .subscribe(collectSubscriber);
            }
        }
    }

    /**
     * 灵感收藏
     */
    private void setNoteInspirationCollectView(
            NoteInspiration noteInspiration, ImageView collectView) {
        if (noteInspiration == null) {
            return;
        }
        // 先变化,再进行网络请求
        if (noteInspiration.isFollowed()) {
            collectCount = note.getCollectCount() - 1;
            note.setCollectCount(collectCount);
            noteInspiration.setFollowed(false);
            if (collectView != null) {
                collectView.setImageResource(R.mipmap.icon_collect_white_72_72);
            }
        } else {
            collectCount = note.getCollectCount() + 1;
            note.setCollectCount(collectCount);
            noteInspiration.setFollowed(true);
            if (collectView != null) {
                collectView.setImageResource(R.mipmap.icon_collect_primary_72_72);
            }
        }
        bottomViewHolder.tvCollect.setText(getString(R.string.fmt_common_count___note,
                "收藏",
                collectCount));
        bottomViewHolder.imgCollect.setImageResource(noteInspiration.isFollowed() ? R.mipmap
                .icon_collect_primary_40_40 : R.mipmap.icon_collect_white_40_40);
        if (HljDialogUtil.isNewFirstNoteCollect(getContext(), HljDialogUtil.NOTE_COLLECT)) {
            showFirstCollectDialog();
        }
    }

    /**
     * 灵感第一次收藏弹窗
     */
    private void showFirstCollectDialog() {
        HljDialogUtil.showFirstCollectNoteNoticeDialog(getContext(),
                HljDialogUtil.NOTE_COLLECT,
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        ARouter.getInstance()
                                .build(RouterPath.IntentPath.Customer.COLLECT)
                                .navigation(getContext());
                    }
                },
                R.mipmap.image_dialog_collect);
    }


    /**
     * 关注用户
     */
    private void setUserFollowView(boolean showToast) {
        if (note.getAuthor()
                .isFollowing()) {
            noteDetailViewHolder.btnUserFollow.setBackgroundResource(R.drawable.sp_r4_stroke1_gray);
            noteDetailViewHolder.btnUserFollow.setTextColor(ContextCompat.getColor(getContext(),
                    R.color.colorGray));
            noteDetailViewHolder.btnUserFollow.setText(getString(R.string.label_followed___cm));
            if (showToast) {
                ToastUtil.showCustomToast(getContext(), R.string.msg_success_to_follow___cm);
            }
        } else {
            noteDetailViewHolder.btnUserFollow.setBackgroundResource(R.drawable
                    .sp_r4_stroke1_primary_solid_white);
            noteDetailViewHolder.btnUserFollow.setTextColor(ContextCompat.getColor(getContext(),
                    R.color.colorPrimary));
            noteDetailViewHolder.btnUserFollow.setText(getString(R.string.label_follow___cv));
            if (showToast) {
                ToastUtil.showCustomToast(getContext(), R.string.msg_success_to_un_follow___cm);
            }
        }
    }

    public float getAlpha() {
        return alpha > 1 ? 1 : alpha;
    }

    public int getCollectCount() {
        return collectCount;
    }

    public View getBottomView() {
        return bottomView;
    }

    @Override
    public void refresh(Object... params) {
        if (params != null && params.length > 0 && params[0] instanceof Note) {
            note = (Note) params[0];
        }
        refreshNoteDetail();
        refreshNoteMarks();
        if (isMerchant) {
            initMerchantLoad();
        } else if (isLargerView) {
            initLargerViewLoad();
        } else {
            initLoad();
        }
    }

    /**
     * 跳转到笔记本或商家主页
     */
    @Override
    public void onUserNoteClick() {
        if (note != null && note.getNoteBookId() != 0) {
            if (note.getNotebookType() == NotebookType.TYPE_WEDDING_PERSON) {
                //进入商家首页，并定位到动态列表中
                Intent intent = new Intent(getContext(), MerchantNoteActivity.class);
                intent.putExtra("id",
                        note.getMerchant()
                                .getId());
                startActivity(intent);
            } else {
                Intent intent = new Intent(getContext(), NotebookActivity.class);
                intent.putExtra("note_book_id", note.getNoteBookId());
                startActivity(intent);
            }
        }
    }

    @Override
    public void onCommentItemClick(RepliedComment repliedComment) {
        if (user == null || repliedComment.getUser() == null) {
            return;
        }
        if (user.getId() != repliedComment.getUser()
                .getId()) {
            onComment(repliedComment);
        } else {
            showMenuDialog(repliedComment);
        }
    }

    /**
     * 收藏灵感
     *
     * @param position
     * @param noteInspiration
     * @param collectView
     */
    @Override
    public void onCollect(
            int position, NoteInspiration noteInspiration, ImageView collectView) {
        onCollectNoteInspiration(noteInspiration, collectView);
    }

    /**
     * 评论笔记
     *
     * @param repliedComment 回复评论
     */
    public void onComment(RepliedComment repliedComment) {
        if (AuthUtil.loginBindCheck(getContext())) {
            if (note != null && note.getId() != 0) {
                Intent intent = new Intent(getContext(), PostNoteCommentActivity.class);
                intent.putExtra("id", note.getId());
                if (repliedComment != null) {
                    intent.putExtra("replied_comment", repliedComment);
                }
                intent.putExtra("entity_type", HljNote.NOTE_TYPE);
                intent.putExtra("hint_content", getCommentHeaderHint(note.getNotebookType()));
                startActivityForResult(intent, COMMENT_REQUEST);
                getActivity().overridePendingTransition(0, 0);
            }
        }
    }

    /**
     * 分享
     */
    private void onShare() {
        if (note == null || note.getShareInfo() == null) {
            return;
        }
        //只有婚纱照类型，并且是自己发的婚纱照才能分享海报
        if (note.getNotebookType() == NotebookType.TYPE_WEDDING_PHOTO && !HljNote.isMerchant(
                getContext()) && user != null && user.getId() == note.getAuthor()
                .getId()) {
            ShareDialogUtil.onCommonWithPoster(getContext(),
                    note.getShareInfo(),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(),
                                    NoteCreateWeddingPosterActivity.class);
                            intent.putExtra("id", note.getId());
                            startActivity(intent);
                            getActivity().overridePendingTransition(R.anim.slide_in_right,
                                    R.anim.activity_anim_default);
                        }
                    });
        } else {
            ShareDialogUtil.onCommonShare(getContext(), note.getShareInfo(), shareCallbackHandler);
        }
    }

    private void goCommentList() {
        if (noteId != 0) {
            Intent intent = new Intent(getContext(), NoteCommentListActivity.class);
            intent.putExtra("id", noteId);
            intent.putExtra("entity_type", HljNote.NOTE_TYPE);
            intent.putExtra("notebook_type", note.getNotebookType());
            startActivity(intent);
        }
    }

    private void goMerchant(Merchant merchant, int locationPosition) {
        if (isMerchant) {
            return;
        }
        if (merchant.getShopType() == Merchant.SHOP_TYPE_PRODUCT) {
            //婚品商家
            ARouter.getInstance()
                    .build(RouterPath.IntentPath.Customer.PRODUCT_MERCHANT_HOME)
                    .withLong("id", merchant.getId())
                    .withTransition(R.anim.slide_in_right, R.anim.activity_anim_default)
                    .navigation(getContext());
        } else {
            ARouter.getInstance()
                    .build(RouterPath.IntentPath.Customer.MERCHANT_HOME)
                    .withLong("id", merchant.getId())
                    .withInt("locationPosition", locationPosition)
                    .withTransition(R.anim.slide_in_right, R.anim.activity_anim_default)
                    .navigation(getContext());
        }
    }

    private void goUserProfile(Author author) {
        ARouter.getInstance()
                .build(RouterPath.IntentPath.Customer.USER_PROFILE)
                .withLong("id", author.getId())
                .withTransition(R.anim.slide_in_right, R.anim.activity_anim_default)
                .navigation(getContext());
    }

    //notebookType 1婚纱照2婚礼筹备3婚品筹备4婚礼人
    private String getCommentHeaderHint(int notebookType) {
        if (isMerchant) {
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

    private int getInspirationPosition(long inspirationId) {
        for (int i = 0; i < note.getInspirations()
                .size(); i++) {
            if (note.getInspirations()
                    .get(i)
                    .getId() == inspirationId) {
                return i;
            }
        }
        return 0;
    }

    private void showMenuDialog(final RepliedComment comment) {
        if (menuDialog != null && menuDialog.isShowing()) {
            return;
        }
        if (menuDialog == null) {
            menuDialog = new Dialog(getContext(), R.style.BubbleDialogTheme);
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
            Window window = menuDialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams params = window.getAttributes();
                params.width = CommonUtil.getDeviceSize(getContext()).x;
                window.setAttributes(params);
                window.setGravity(Gravity.BOTTOM);
                window.setWindowAnimations(R.style.dialog_anim_rise_style);
            }
        }
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                menuDialog.dismiss();
                CommonUtil.unSubscribeSubs(deleteSubscriber);
                if (deleteSubscriber == null || deleteSubscriber.isUnsubscribed()) {
                    deleteSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                            .setOnNextListener(new SubscriberOnNextListener() {
                                @Override
                                public void onNext(Object o) {
                                    ToastUtil.showCustomToast(getContext(),
                                            R.string.msg_delete_success___cm);
                                    onRefreshCommentData();
                                }
                            })
                            .setProgressDialog(DialogUtil.createProgressDialog(getContext()))
                            .build();
                    CommonApi.deleteFuncObb(comment.getId())
                            .subscribe(deleteSubscriber);
                }
            }
        });
        menuDialog.show();
    }

    private class SpacesItemDecoration extends RecyclerView.ItemDecoration {

        private int topSpace;
        private int leftAndRightSpace;

        SpacesItemDecoration() {
            this.topSpace = CommonUtil.dp2px(getContext(), 4);
            this.leftAndRightSpace = CommonUtil.dp2px(getContext(), 6);
        }

        @Override
        public void getItemOffsets(
                Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            StaggeredGridLayoutManager.LayoutParams lp = (StaggeredGridLayoutManager
                    .LayoutParams) view.getLayoutParams();
            int top = 0;
            int left = 0;
            int right = 0;
            int headSize = adapter.getHeaderCount();
            int position = parent.getChildAdapterPosition(view);
            if (position >= headSize && position < parent.getAdapter()
                    .getItemCount() - 1) {
                top = position > headSize + 1 ? -topSpace : topSpace / 2;
                left = lp.getSpanIndex() == 0 ? leftAndRightSpace : 0;
                right = lp.getSpanIndex() == 0 ? 0 / 2 : leftAndRightSpace;
            }
            outRect.set(left, top, right, 0);
        }
    }

    private NoteDetailActivity getNoteDetailActivity() {
        if (getActivity() instanceof NoteDetailActivity) {
            return (NoteDetailActivity) getActivity();
        }
        return null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case COMMENT_REQUEST:
                    if (data != null) {
                        RepliedComment commentResponse = data.getParcelableExtra
                                ("comment_response");
                        if (commentResponse != null) {
                            totalCommentCount = totalCommentCount + 1;
                            onRefreshCommentData();
                            note.setCommentCount(totalCommentCount);
                            bottomViewHolder.tvCommentCount.setText(getString(R.string
                                            .fmt_common_count___note,
                                    "评论",
                                    totalCommentCount));
                        }
                    }
                    break;
                case HljShare.RequestCode.SHARE_TO_WEIBO:
                    shareCallbackHandler.sendEmptyMessage(requestCode);
                    break;
            }
        }
    }

    static class NoteDetailViewHolder {
        @BindView(R2.id.view_pager)
        ViewPager viewPager;
        @BindView(R2.id.flow_indicator)
        NoteCirclePageIndicator flowIndicator;
        @BindView(R2.id.img_inspiration_collect)
        ImageView imgInspirationCollect;
        @BindView(R2.id.photo_view)
        View photoView;
        @BindView(R2.id.img_screen_shot)
        ImageView imgScreenShot;
        @BindView(R2.id.video_layout)
        RelativeLayout videoLayout;
        @BindView(R2.id.toolbar_hint_layout)
        View toolbarHintLayout;
        @BindView(R2.id.riv_auth_avatar)
        RoundedImageView rivAuthAvatar;
        @BindView(R2.id.tv_auth_name)
        TextView tvAuthName;
        @BindView(R2.id.tv_auth_address_and_notes)
        TextView tvAuthAddressAndNotes;
        @BindView(R2.id.btn_user_follow)
        Button btnUserFollow;
        @BindView(R2.id.user_info_layout)
        RelativeLayout userInfoLayout;
        @BindView(R2.id.img_merchant_avatar)
        RoundedImageView imgMerchantAvatar;
        @BindView(R2.id.tv_merchant_name)
        TextView tvMerchantName;
        @BindView(R2.id.img_level)
        ImageView imgLevel;
        @BindView(R2.id.img_bond)
        ImageView imgBond;
        @BindView(R2.id.rating_bar)
        RatingBar ratingBar;
        @BindView(R2.id.tv_comment_count)
        TextView tvCommentCount;
        @BindView(R2.id.btn_merchant_follow)
        Button btnMerchantFollow;
        @BindView(R2.id.merchant_info_layout)
        LinearLayout merchantInfoLayout;
        @BindView(R2.id.tv_note_title)
        TextView tvNoteTitle;
        @BindView(R2.id.tv_note_content)
        TextView tvNoteContent;
        @BindView(R2.id.fl_note_marks)
        TextView flNoteMarks;
        @BindView(R2.id.img_note_address)
        ImageView imgNoteAddress;
        @BindView(R2.id.tv_note_address)
        TextView tvNoteAddress;
        @BindView(R2.id.tv_note_time)
        TextView tvNoteTime;
        @BindView(R2.id.img_vip)
        ImageView imgVip;

        NoteDetailViewHolder(View view) {
            ButterKnife.bind(this, view);
            flowIndicator.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        }
    }

    static class FooterViewHolder {
        @BindView(R2.id.no_more_hint)
        TextView noMoreHint;
        @BindView(R2.id.loading)
        LinearLayout loading;

        FooterViewHolder(View view) {ButterKnife.bind(this, view);}
    }

    class CommentHeaderViewHolder {
        @BindView(R2.id.tv_relevant_header)
        TextView tvRelevantHeader;
        @BindView(R2.id.relevant_header_layout)
        LinearLayout relevantHeaderLayout;
        @BindView(R2.id.tv_comment_tip)
        TextView tvCommentTip;
        @BindView(R2.id.comment_layout)
        LinearLayout commentLayout;
        @BindView(R2.id.comment_empty_layout)
        View commentEmptyLayout;
        @BindView(R2.id.merchant_comment_empty_tip)
        View merchantCommentEmptyTip;
        @BindView(R2.id.customer_comment_empty_tip)
        View customerCommentEmptyTip;
        @BindView(R2.id.comment_tag_view)
        View commentTagView;
        @BindView(R2.id.img_user_avatar)
        ImageView imgUserAvatar;

        CommentHeaderViewHolder(View view) {
            ButterKnife.bind(this, view);
            relevantHeaderLayout.setVisibility(View.VISIBLE);
            commentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onComment(null);
                }
            });
            if (user != null) {
                imgUserAvatar.setVisibility(View.VISIBLE);
                Glide.with(getContext())
                        .load(ImagePath.buildPath(user.getAvatar())
                                .width(CommonUtil.dp2px(getContext(), 30))
                                .height(CommonUtil.dp2px(getContext(), 30))
                                .cropPath())
                        .into(imgUserAvatar);
            } else {
                imgUserAvatar.setVisibility(View.GONE);
            }
        }
    }

    class CommentFooterViewHolder {
        @BindView(R2.id.line_layout)
        View lineLayout;
        @BindView(R2.id.tv_look_all)
        TextView tvLookAll;
        @BindView(R2.id.note_comment_footer_view)
        View noteCommentFooterView;

        CommentFooterViewHolder(View view) {
            ButterKnife.bind(this, view);
            noteCommentFooterView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goCommentList();
                }
            });
        }
    }

    static class RelevantHeaderViewHolder {
        RelevantHeaderViewHolder(View view) {ButterKnife.bind(this, view);}

        @BindView(R2.id.relevant_note_header_layout)
        LinearLayout relevantNoteHeaderLayout;
    }

    class BottomViewHolder {
        @BindView(R2.id.tv_comment_count)
        TextView tvCommentCount;
        @BindView(R2.id.comment_layout)
        LinearLayout commentLayout;
        @BindView(R2.id.img_collect)
        ImageView imgCollect;
        @BindView(R2.id.tv_collect)
        TextView tvCollect;
        @BindView(R2.id.check_collected)
        LinearLayout checkCollected;
        @BindView(R2.id.img_share)
        ImageView imgShare;
        @BindView(R2.id.tv_share)
        TextView tvShare;
        @BindView(R2.id.action_share)
        LinearLayout actionShare;

        BottomViewHolder(View view) {
            ButterKnife.bind(this, view);
            checkCollected.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCollectNoteInspiration();
                }
            });
            commentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onComment(null);
                }
            });
            actionShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onShare();
                }
            });
        }
    }

    private Handler shareCallbackHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case HljShare.RequestCode.SHARE_TO_WEIBO:
                case HljShare.RequestCode.SHARE_TO_QQ:
                case HljShare.RequestCode.SHARE_TO_PENGYOU:
                case HljShare.RequestCode.SHARE_TO_WEIXIN:
                    if (noteId > 0) {
                        TrackerHelper.postShareAction(getContext(), noteId, "note");

                        new HljTracker.Builder(getContext()).eventableId(noteId)
                                .eventableType("Note")
                                .action("share")
                                .additional(HljShare.getShareTypeName(msg.what))
                                .build()
                                .add();
                    }
                    break;
            }
            return false;
        }
    });

    public boolean isVisibleToUser() {
        BaseNoteDetailFragment fragment = (BaseNoteDetailFragment) getParentFragment();
        return fragment == null || fragment.isVisibility();
    }

    private boolean isAnimEnded() {
        return btnScrollTop != null && (btnScrollTop.getAnimation() == null || btnScrollTop
                .getAnimation()
                .hasEnded());
    }

    private void showFiltrateAnimation() {
        if (btnScrollTop == null) {
            return;
        }
        isHide = false;
        if (isAnimEnded()) {
            Animation animation = AnimationUtils.loadAnimation(getActivity(),
                    R.anim.slide_in_bottom2);
            animation.setFillBefore(true);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    btnScrollTop.post(new Runnable() {
                        @Override
                        public void run() {
                            if (isHide) {
                                hideFiltrateAnimation();
                            }
                        }
                    });
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            btnScrollTop.startAnimation(animation);
        }
    }

    private void hideFiltrateAnimation() {
        if (btnScrollTop == null) {
            return;
        }
        isHide = true;
        if (isAnimEnded()) {
            Animation animation = AnimationUtils.loadAnimation(getActivity(),
                    R.anim.slide_out_bottom2);
            animation.setFillAfter(true);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    btnScrollTop.post(new Runnable() {
                        @Override
                        public void run() {
                            if (!isHide) {
                                showFiltrateAnimation();
                            }
                        }
                    });
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            btnScrollTop.startAnimation(animation);
        }
    }
}
