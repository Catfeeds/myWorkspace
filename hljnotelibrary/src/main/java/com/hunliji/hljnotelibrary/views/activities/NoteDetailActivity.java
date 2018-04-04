package com.hunliji.hljnotelibrary.views.activities;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljcommonlibrary.models.note.Note;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.OncePrefUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.view_tracker.VTMetaData;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.widgets.ChildViewPager;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.hljhttplibrary.utils.AuthUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljnotelibrary.HljNote;
import com.hunliji.hljnotelibrary.R;
import com.hunliji.hljnotelibrary.R2;
import com.hunliji.hljnotelibrary.adapters.NoteFragmentPageAdapter;
import com.hunliji.hljnotelibrary.api.NoteApi;
import com.hunliji.hljnotelibrary.models.NotebookType;
import com.hunliji.hljnotelibrary.utils.NotePrefUtil;
import com.hunliji.hljnotelibrary.views.fragments.BaseNoteDetailFragment;
import com.hunliji.hljnotelibrary.views.widgets.NoteCollectHintView;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by mo_yu on 2017/6/24.笔记详情
 */

public class NoteDetailActivity extends HljBaseNoBarActivity {

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
    @BindView(R2.id.view_pager)
    ChildViewPager viewPager;
    @BindView(R2.id.user_action_footer_layout)
    FrameLayout userActionFooterLayout;
    @BindView(R2.id.merchant_action_footer_layout)
    LinearLayout merchantActionFooterLayout;
    @BindView(R2.id.note_merchant_comment_view)
    LinearLayout noteMerchantCommentView;
    @BindView(R2.id.tv_content)
    TextView tvContent;
    @BindView(R2.id.action_holder_layout)
    LinearLayout actionHolderLayout;
    @BindView(R2.id.action_holder_layout2)
    LinearLayout actionHolderLayout2;
    @BindView(R2.id.collect_hint_view)
    NoteCollectHintView noteCollectHintView;
    private long noteId;
    private long inspirationId;//灵感收藏列表点击进入笔记详情，需要定位到对应的灵感视图
    private int createType;//两个含义，1.从创建页面进入笔记详情，2.笔记类型
    private int notePosition;//大图模式下，笔记列表点击的第n个笔记
    private int itemPosition;//笔记列表中位置，用于回调收藏数和评论数
    private int inspirationPosition;//笔记本点击相应灵感进入笔记详情，需要定位到对应的灵感视图
    private boolean isShowBottom;
    private boolean isShowCollectHint;
    private boolean isLargerView;
    private boolean syncSuccess;//是否同步评价
    private String url;//列表传入运营笔记url
    private User user;
    private long[] noteIds;
    private NoteFragmentPageAdapter fragmentAdapter;
    private Dialog moreSettingDlg;
    private Dialog createPosterDialog;
    private Subscription rxBusEventSub;
    private HljHttpSubscriber deleteSubscriber;

    public final static String ARG_NOTE_ID = "note_id";
    public final static String ARG_ITEM_POSITION = "item_position";
    public final static String ARG_URL = "url";
    public final static String ARG_NOTE_IDS = "note_ids";

    @Override
    public String pageTrackTagName() {
        return "笔记详情页";
    }

    @Override
    public VTMetaData pageTrackData() {
        long id = getIntent().getLongExtra(ARG_NOTE_ID, 0);
        return new VTMetaData(id, "Note");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail___note);
        ButterKnife.bind(this);
        setActionBarPadding(actionHolderLayout, actionHolderLayout2);
        initValue();
        initView();
        registerRxBusEvent();
    }

    private void initValue() {
        //单个笔记只传note_id
        noteId = getIntent().getLongExtra(ARG_NOTE_ID, 0);
        inspirationId = getIntent().getLongExtra("inspiration_id", 0);
        createType = getIntent().getIntExtra("create_type", 0);
        syncSuccess = getIntent().getBooleanExtra("sync_success", false);
        itemPosition = getIntent().getIntExtra(ARG_ITEM_POSITION, -1);
        url = getIntent().getStringExtra(ARG_URL);
        //笔记本大图模式，传入笔记列表
        noteIds = getIntent().getLongArrayExtra(ARG_NOTE_IDS);
        notePosition = getIntent().getIntExtra("note_position", 0);
        inspirationPosition = getIntent().getIntExtra("inspiration_position", 0);
        user = UserSession.getInstance()
                .getUser(this);
        isShowBottom = true;
    }

    private void initView() {
        boolean hasCreated = OncePrefUtil.hasDoneThis(this,
                HljNote.PrefKeys.PREF_HAS_CREATED_WEDDING_PHOTO);
        if (createType == NotebookType.TYPE_WEDDING_PHOTO && !hasCreated && !HljNote.isMerchant
                (this)) {
            OncePrefUtil.doneThis(this, HljNote.PrefKeys.PREF_HAS_CREATED_WEDDING_PHOTO);
            showCreatePoster();
        } else if (syncSuccess) {
            showNoteMerchantCommentView();
        }
        if (HljNote.isMerchant(this)) {
            merchantActionFooterLayout.setVisibility(View.VISIBLE);
        } else {
            merchantActionFooterLayout.setVisibility(View.GONE);
        }
        if (noteIds == null || noteIds.length == 0) {
            if (noteIds == null) {
                noteIds = new long[1];
            }
            noteIds[0] = noteId;
            isLargerView = false;
        } else {
            isLargerView = true;
        }
        fragmentAdapter = new NoteFragmentPageAdapter(getSupportFragmentManager(), noteIds);
        fragmentAdapter.setLargerView(isLargerView);
        fragmentAdapter.setInspirationId(inspirationId);
        fragmentAdapter.setNotePosition(notePosition);
        fragmentAdapter.setInspirationPosition(inspirationPosition);
        fragmentAdapter.setUrl(url);
        viewPager.setAdapter(fragmentAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                BaseNoteDetailFragment baseNoteDetailFragment = fragmentAdapter.getFragment
                        (position);
                if (baseNoteDetailFragment != null) {
                    setToolbarAlpha(baseNoteDetailFragment.getAlpha());
                    refreshBottomView();
                    if (!TextUtils.isEmpty(baseNoteDetailFragment.getNoteUrl())) {
                        hideMoreMenu();
                        hideNativeBottomView();
                    } else {
                        showMoreMenu();
                        showNativeBottomView();
                    }
                }
            }
        });
        viewPager.setCurrentItem(notePosition);
    }

    public void refreshBottomView() {
        BaseNoteDetailFragment baseNoteDetailFragment = fragmentAdapter.getFragment(viewPager
                .getCurrentItem());
        if (baseNoteDetailFragment == null) {
            return;
        }
        View bottomView = baseNoteDetailFragment.getBottomView();
        userActionFooterLayout.removeAllViews();
        if (bottomView != null) {
            userActionFooterLayout.addView(bottomView);
        }
    }

    public void setToolbarAlpha(float alpha) {
        actionHolderLayout2.setAlpha(alpha);
        shadowView.setAlpha(1 - alpha);
    }

    //运营笔记，隐藏原生的底部操作栏
    public void hideNativeBottomView() {
        userActionFooterLayout.setVisibility(View.GONE);
    }

    //普通笔记，显示原生的底部操作栏
    public void showNativeBottomView() {
        userActionFooterLayout.setVisibility(View.VISIBLE);
        initNoteCollectHintView();
    }

    public void hideMoreMenu() {
        btnMenu.setVisibility(View.GONE);
        btnMenu2.setVisibility(View.GONE);
    }

    public void showMoreMenu() {
        btnMenu.setVisibility(View.VISIBLE);
        btnMenu2.setVisibility(View.VISIBLE);
    }

    public void hideBottomView() {
        if (!isShowBottom) {
            return;
        }
        isShowBottom = false;
        ObjectAnimator animator = ObjectAnimator.ofFloat(userActionFooterLayout,
                View.TRANSLATION_Y,
                0,
                userActionFooterLayout.getHeight());
        animator.setDuration(500);
        animator.start();
        if (isShowCollectHint) {
            noteCollectHintView.setVisibility(View.GONE);
        }
    }

    public void showBottomView() {
        if (isShowBottom) {
            return;
        }
        isShowBottom = true;
        ObjectAnimator animator = ObjectAnimator.ofFloat(userActionFooterLayout,
                View.TRANSLATION_Y,
                userActionFooterLayout.getHeight(),
                0);
        animator.setDuration(500);
        animator.start();
        if (isShowCollectHint) {
            noteCollectHintView.setVisibility(View.VISIBLE);
        }
    }

    public void hideCollectHintView() {
        if (isShowCollectHint && noteCollectHintView.getVisibility() == View.VISIBLE) {
            NotePrefUtil.getInstance(NoteDetailActivity.this)
                    .setNoteCollectHintClicked(true);
            isShowCollectHint = false;
            noteCollectHintView.setVisibility(View.GONE);
        }
    }

    public void setTitleText(String title) {
        tvTitle.setText(title);
    }

    public void showNoteMerchantCommentView() {
        noteMerchantCommentView.setVisibility(View.VISIBLE);
        noteMerchantCommentView.postDelayed(new Runnable() {
            @Override
            public void run() {
                hideNoteMerchantCommentView();
            }
        }, 3000);
        ObjectAnimator animator = ObjectAnimator.ofFloat(noteMerchantCommentView,
                View.TRANSLATION_Y,
                0,
                CommonUtil.dp2px(this, 69) + noteMerchantCommentView.getHeight());
        animator.setDuration(500);
        animator.start();
    }

    public void hideNoteMerchantCommentView() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(noteMerchantCommentView,
                View.TRANSLATION_Y,
                CommonUtil.dp2px(this, 69),
                -noteMerchantCommentView.getHeight());
        animator.setDuration(500);
        animator.start();
    }

    public void showCreatePosterByNote(Note note) {
        if (note == null || user == null) {
            return;
        }
        if (user.getId() == note.getAuthor()
                .getId()) {
            boolean hasCreated = OncePrefUtil.hasDoneThis(this,
                    HljNote.PrefKeys.PREF_HAS_CREATED_WEDDING_PHOTO);
            if (note.getNotebookType() == NotebookType.TYPE_WEDDING_PHOTO && !hasCreated &&
                    !HljNote.isMerchant(
                    this)) {
                OncePrefUtil.doneThis(this, HljNote.PrefKeys.PREF_HAS_CREATED_WEDDING_PHOTO);
                showCreatePoster();
            }
        }
    }

    private void showCreatePoster() {
        int width = CommonUtil.getDeviceSize(this).x - CommonUtil.dp2px(this, 60);
        int imgHeight = width * 9 / 16;

        createPosterDialog = new Dialog(this, R.style.BubbleDialogTheme);
        createPosterDialog.setContentView(R.layout.dialog_create_poster___note);
        createPosterDialog.setCancelable(true);
        createPosterDialog.setCanceledOnTouchOutside(true);

        createPosterDialog.findViewById(R.id.img_cover)
                .getLayoutParams().height = imgHeight;
        createPosterDialog.findViewById(R.id.tv_create)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //海报分享
                        Intent intent = new Intent(NoteDetailActivity.this,
                                NoteCreateWeddingPosterActivity.class);
                        intent.putExtra("id", noteId);
                        startActivityForResult(intent, HljNote.RequestCode.NOTE_CREATE_POSTER);
                        overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    }
                });
        createPosterDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                //海报分享弹窗消失后，才显示评价弹窗
                if (syncSuccess) {
                    showNoteMerchantCommentView();
                }
            }
        });

        Window window = createPosterDialog.getWindow();
        if (window != null) {
            window.setGravity(Gravity.CENTER);
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = width;
        }
        createPosterDialog.show();
    }

    public void initNoteCollectHintView() {
        //非商家端，非运营笔记，非笔记本进入笔记详情且未显示过收藏引导才显示
        if (isShowCollectHint()) {
            noteCollectHintView.setVisibility(View.VISIBLE);
            noteCollectHintView.setCenterPoint(CommonUtil.getDeviceSize(this).x / 2,
                    CommonUtil.dp2px(this, 50) / 2)
                    .addRippleView();
            noteCollectHintView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NotePrefUtil.getInstance(NoteDetailActivity.this)
                            .setNoteCollectHintClicked(true);
                    isShowCollectHint = false;
                    noteCollectHintView.setVisibility(View.GONE);
                }
            });
        } else {
            noteCollectHintView.setVisibility(View.GONE);
        }
    }

    //只有商家端动态笔记才有删除功能
    private void deleteNote(Note note) {
        if (deleteSubscriber == null || deleteSubscriber.isUnsubscribed()) {
            deleteSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener() {
                        @Override
                        public void onNext(Object o) {
                            ToastUtil.showToast(NoteDetailActivity.this, "删除成功！", 0);
                            RxBus.getDefault()
                                    .post(new RxEvent(RxEvent.RxEventType.DELETE_NOTE_SUCCESS,
                                            null));
                            finish();
                        }
                    })
                    .build();
            note.setDeleted(true);
            NoteApi.editNoteObb(note)
                    .subscribe(deleteSubscriber);
        }
    }

    @OnClick({R2.id.btn_menu, R2.id.btn_menu2})
    void onMenu() {
        final BaseNoteDetailFragment baseNoteDetailFragment = fragmentAdapter.getFragment
                (viewPager.getCurrentItem());
        if (baseNoteDetailFragment != null) {
            final Note note = baseNoteDetailFragment.getNote();
            if (note == null) {
                return;
            }
            if (moreSettingDlg == null) {
                User user = UserSession.getInstance()
                        .getUser(this);
                LinkedHashMap<String, View.OnClickListener> map = new LinkedHashMap<>();
                if ((user != null && user.getId() == note.getAuthor()
                        .getId())) {
                    if (!HljNote.isMerchant(this)) {
                        map.put("编辑", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //视频无法编辑
                                moreSettingDlg.cancel();
                                if (AuthUtil.loginBindCheck(NoteDetailActivity.this)) {
                                    // 编辑当前话题
                                    Note note = baseNoteDetailFragment.getNote();
                                    Intent intent = new Intent(NoteDetailActivity.this,
                                            CreatePhotoNoteActivity.class);
                                    intent.putExtra(CreatePhotoNoteActivity.ARG_NOTE, note);
                                    startActivity(intent);
                                    overridePendingTransition(0, 0);
                                }
                            }
                        });
                    }
                    if (HljNote.isMerchant(this) && note.getNotebookType() == NotebookType
                            .TYPE_WEDDING_PERSON) {
                        map.put("删除", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                moreSettingDlg.cancel();
                                if (AuthUtil.loginBindCheck(NoteDetailActivity.this)) {
                                    DialogUtil.createDialogWithIcon(NoteDetailActivity.this,
                                            null,
                                            getString(R.string.hint_delete_note___note),
                                            null,
                                            null,
                                            new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    // 删除当前笔记
                                                    deleteNote(note);
                                                }
                                            },
                                            null)
                                            .show();

                                }
                            }
                        });
                    }

                } else {
                    map.put("举报", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            moreSettingDlg.cancel();
                            if (AuthUtil.loginBindCheck(NoteDetailActivity.this)) {
                                Intent intent = new Intent(NoteDetailActivity.this,
                                        ReportActivity.class);
                                intent.putExtra("id", note.getId());
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
    }


    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(rxBusEventSub, deleteSubscriber);
    }

    @OnClick(R2.id.merchant_action_footer_layout)
    public void onComment() {
        BaseNoteDetailFragment baseNoteDetailFragment = fragmentAdapter.getFragment(viewPager
                .getCurrentItem());
        if (baseNoteDetailFragment != null) {
            baseNoteDetailFragment.onComment();
        }
    }

    @OnClick({R2.id.btn_back, R2.id.btn_back_2})
    public void onBack(View view) {
        onBackPressed();
    }

    //注册RxEventBus监听
    private void registerRxBusEvent() {
        if (rxBusEventSub == null || rxBusEventSub.isUnsubscribed()) {
            rxBusEventSub = RxBus.getDefault()
                    .toObservable(RxEvent.class)
                    .subscribe(new RxBusSubscriber<RxEvent>() {
                        @Override
                        protected void onEvent(RxEvent rxEvent) {
                            switch (rxEvent.getType()) {
                                case CREATE_NOTE_SUCCESS:
                                    final BaseNoteDetailFragment baseNoteDetailFragment =
                                            fragmentAdapter.getFragment(
                                            viewPager.getCurrentItem());
                                    if (baseNoteDetailFragment != null) {
                                        baseNoteDetailFragment.refresh();
                                    }
                                    break;
                            }
                        }
                    });
        }
    }

    private boolean isShowCollectHint() {
        isShowCollectHint = !NotePrefUtil.getInstance(this)
                .isNoteCollectHintClicked() && !HljNote.isMerchant(this) && createType == 0 &&
                !isLargerView;
        return isShowCollectHint;
    }

    @Override
    public void onBackPressed() {
        if (itemPosition != -1) {
            BaseNoteDetailFragment baseNoteDetailFragment = fragmentAdapter.getFragment(viewPager
                    .getCurrentItem());
            if (baseNoteDetailFragment != null) {
                int collectCount = baseNoteDetailFragment.getCollectCount();
                if (collectCount != -1) {
                    Intent intent = getIntent();
                    intent.putExtra("collect_count", collectCount);
                    intent.putExtra("position", itemPosition);
                    setResult(Activity.RESULT_OK, intent);
                }
            }
        }
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case HljNote.RequestCode.NOTE_CREATE_POSTER:
                    if (createPosterDialog != null && createPosterDialog.isShowing()) {
                        createPosterDialog.dismiss();
                    }
                    break;
            }
        }
    }
}
