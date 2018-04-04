package me.suncloud.marrymemo.view.community;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.h6ah4i.android.widget.advrecyclerview.animator.RefactoredDefaultItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.interfaces.OnFinishedListener;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityChannel;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityThread;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.OncePrefUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljRoundProgressDialog;
import com.hunliji.hljemojilibrary.EmojiUtil;
import com.hunliji.hljemojilibrary.adapters.EmojiPagerAdapter;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.PhotoListUploadUtil;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.adapters.DraggableImgGridAdapter;
import com.hunliji.hljimagelibrary.views.activities.ImageChooserActivity;
import com.hunliji.hljimagelibrary.views.activities.PicsPageViewActivity;
import com.slider.library.Indicators.CirclePageIndicator;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnTouch;
import io.realm.Realm;
import io.realm.RealmList;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.community.CommunityApi;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.model.realm.CommunityDraft;
import me.suncloud.marrymemo.model.realm.RealmJsonPic;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.TextFaceCountWatcher;
import me.suncloud.marrymemo.view.SelectCommunityChannelActivity;
import rx.internal.util.SubscriptionList;

/**
 * 发话题activity
 * Created by chen_bin on 2018/3/13 0013.
 */
public class CreateThreadActivity extends HljBaseNoBarActivity {

    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @BindView(R.id.action_holder_layout)
    RelativeLayout actionHolderLayout;
    @BindView(R.id.et_title)
    EditText etTitle;
    @BindView(R.id.et_content)
    EditText etContent;
    @BindView(R.id.tv_secondary_content_hint)
    TextView tvSecondaryContentHint;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.tv_img_drag_hint)
    TextView tvImgDragHint;
    @BindView(R.id.tv_channel_title)
    TextView tvChannelTitle;
    @BindView(R.id.channel_layout)
    LinearLayout channelLayout;
    @BindView(R.id.btn_add_emoji)
    ImageButton btnAddEmoji;
    @BindView(R.id.tv_img_count)
    TextView tvImgCount;
    @BindView(R.id.tv_text_count)
    TextView tvTextCount;
    @BindView(R.id.imm_layout)
    RelativeLayout immLayout;
    @BindView(R.id.emoji_pager)
    ViewPager emojiPager;
    @BindView(R.id.flow_indicator)
    CirclePageIndicator flowIndicator;
    @BindView(R.id.emoji_layout)
    RelativeLayout emojiLayout;
    @BindView(R.id.bottom_layout)
    LinearLayout bottomLayout;
    @BindView(R.id.btn_select_channel_hint)
    ImageButton btnSelectChannelHint;
    @BindView(R.id.root_layout)
    RelativeLayout rootLayout;

    private DraggableImgGridAdapter adapter;
    private RecyclerView.Adapter wrappedAdapter;

    private HljRoundProgressDialog progressDialog;
    private Dialog exitDialog;

    private DisplayMetrics dm;
    private InputMethodManager imm;
    private Realm realm;
    private RecyclerViewDragDropManager dragDropManager;
    private GridLayoutManager layoutManager;

    private ArrayList<Photo> photos;
    private User user;
    private CommunityThread thread;
    private CommunityChannel channel;
    private CommunityDraft draft;

    private String channelTitle;

    private boolean isShowImm;
    private boolean isShowEmoji;

    private long channelId;
    private long communityActivityId;
    private long pid; //九宫格的图片需要设置不同的id，id需要一直加，不能重复

    private int imageSize;
    private int emojiSize;
    private int emojiImageSize;
    private int emojiPageHeight;

    private HljHttpSubscriber createSub;
    private HljHttpSubscriber getChannelSub;
    public SubscriptionList uploadSubs;

    private final int LIMIT = 9; //图片最多9张
    private final int TITLE_LEN_MIN = 4; //标题最少文字4
    private final int TITLE_LEN_MAX = 32; //标题最多文字32
    private final int CONTENT_LEN_MIN = 5; //内容最少文字5
    private final int CONTENT_LEN_MAX = 2000; //内容最多文字2000

    public final static String ARG_TITLE = "title";
    public final static String ARG_CONTENT = "content";
    public final static String ARG_SECONDARY_CONTENT_HINT = "secondary_content_hint";
    public final static String ARG_CHANNEL = "channel";
    public final static String ARG_THREAD = "thread";
    public final static String ARG_COMMUNITY_ACTIVITY_ID = "community_activity_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_thread);
        ButterKnife.bind(this);
        HljBaseActivity.setActionBarPadding(this, actionHolderLayout);
        initValues();
        initViews();
        initData();
    }

    protected void initValues() {
        photos = new ArrayList<>();
        user = Session.getInstance()
                .getCurrentUser(this);
        realm = Realm.getDefaultInstance();

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        dm = getResources().getDisplayMetrics();
        imageSize = (int) ((dm.widthPixels - 70 * dm.density) / 4);
        emojiSize = Math.round(dm.density * 24);
        emojiImageSize = Math.round((dm.widthPixels - 20 * dm.density) / 7);
        emojiPageHeight = Math.round(emojiImageSize * 3 + 20 * dm.density);

        thread = getIntent().getParcelableExtra(ARG_THREAD);
        channel = getIntent().getParcelableExtra(ARG_CHANNEL);
        communityActivityId = getIntent().getLongExtra(ARG_COMMUNITY_ACTIVITY_ID, 0L);
    }

    private void initViews() {
        initImgsGrid();
        initImmAndEmoji();
        initTextFaceWatcher();
    }

    private void initImgsGrid() {
        dragDropManager = new RecyclerViewDragDropManager();
        dragDropManager.setInitiateOnMove(false);
        dragDropManager.setInitiateOnLongPress(true);
        dragDropManager.setLongPressTimeout(500);
        dragDropManager.setDraggingItemShadowDrawable((NinePatchDrawable) ContextCompat.getDrawable(
                this,
                R.drawable.sp_dragged_shadow));
        dragDropManager.attachRecyclerView(recyclerView);

        layoutManager = new GridLayoutManager(this, 4);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new RefactoredDefaultItemAnimator());

        adapter = new DraggableImgGridAdapter(this, photos, imageSize, LIMIT);
        wrappedAdapter = dragDropManager.createWrappedAdapter(adapter);
        recyclerView.setAdapter(wrappedAdapter);

        adapter.setOnItemAddListener(new DraggableImgGridAdapter.OnItemAddListener() {
            @Override
            public void onItemAdd(Object... objects) {
                onAddImgs();
            }
        });
        adapter.setOnItemClickListener(new OnItemClickListener<Photo>() {
            @Override
            public void onItemClick(int position, Photo photo) {
                Intent intent = new Intent(CreateThreadActivity.this, PicsPageViewActivity.class);
                intent.putExtra(PicsPageViewActivity.ARG_PHOTOS, photos);
                intent.putExtra(PicsPageViewActivity.ARG_POSITION, position);
                startActivity(intent);
            }
        });
        adapter.setOnItemDeleteListener(new DraggableImgGridAdapter.OnItemDeleteListener() {
            @Override
            public void onItemDelete(int position) {
                photos.remove(position);
                addNewButtonAndRefresh();
            }
        });
    }

    private void initImmAndEmoji() {
        final View decorView = getWindow().getDecorView();
        decorView.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Rect rect = new Rect();
                        decorView.getWindowVisibleDisplayFrame(rect);
                        int displayHeight = rect.bottom - rect.top;
                        int height = decorView.getHeight();
                        isShowImm = (double) displayHeight / height < 0.8;
                        if (isShowImm) {
                            emojiLayout.setVisibility(View.GONE);
                            btnAddEmoji.setImageResource(R.mipmap.icon_face_gray);
                            immLayout.setVisibility(View.VISIBLE);
                        } else {
                            immLayout.setVisibility(View.GONE);
                            if (isShowEmoji) {
                                emojiLayout.setVisibility(View.VISIBLE);
                                btnAddEmoji.setImageResource(R.mipmap.icon_keyboard_round_gray);
                                immLayout.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });

        EmojiPagerAdapter pagerAdapter = new EmojiPagerAdapter(this,
                emojiImageSize,
                new EmojiPagerAdapter.OnFaceItemClickListener() {
                    @Override
                    public void onFaceItemClickListener(
                            AdapterView<?> parent, View view, int position, long id) {
                        String tag = (String) parent.getAdapter()
                                .getItem(position);
                        if (CommonUtil.isEmpty(tag)) {
                            return;
                        }
                        EditText currentEt = etTitle.isFocused() ? etTitle : etContent;
                        if ("delete".equals(tag)) {
                            EmojiUtil.deleteTextOrImage(currentEt);
                        } else {
                            StringBuilder sb = new StringBuilder(tag);
                            int start = currentEt.getSelectionStart();
                            int end = currentEt.getSelectionEnd();
                            if (start == end) {
                                currentEt.getText()
                                        .insert(start, sb);
                            } else {
                                currentEt.getText()
                                        .replace(start, end, sb);
                            }
                        }
                    }
                });
        emojiPager.setAdapter(pagerAdapter);
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.addAll(EmojiUtil.getFaceMap(this)
                .keySet());
        pagerAdapter.setTags(arrayList);
        flowIndicator.setViewPager(emojiPager);
        emojiPager.getLayoutParams().height = emojiPageHeight;
    }

    private void initTextFaceWatcher() {
        etTitle.addTextChangedListener(new TextFaceCountWatcher(this,
                etTitle,
                tvTextCount,
                TITLE_LEN_MAX,
                emojiSize));
        etContent.addTextChangedListener(new TextFaceCountWatcher(this,
                etContent,
                tvTextCount,
                CONTENT_LEN_MAX,
                emojiSize));
    }

    private void initData() {
        String title = getIntent().getStringExtra(ARG_TITLE);
        String content = getIntent().getStringExtra(ARG_CONTENT);
        String secondaryContentHint = getIntent().getStringExtra(ARG_SECONDARY_CONTENT_HINT);
        if (thread != null) {
            tvToolbarTitle.setText(R.string.label_edit_thread);
            title = thread.getTitle();
            content = thread.getPost()
                    .getMessage();
            List<Photo> pics = thread.getPost()
                    .getPhotos();
            if (!CommonUtil.isCollectionEmpty(pics)) {
                for (Photo photo : pics) {
                    photo.setId(++pid);
                    photos.add(photo);
                }
            }
        } else {
            draft = getDraft();
            if (draft != null) {
                title = draft.getTitle();
                content = draft.getContent();
                ArrayList<RealmJsonPic> deletedPics = new ArrayList<>();
                for (RealmJsonPic pic : draft.getPics()) {
                    File file = new File(pic.getPath());
                    if (CommonUtil.isHttpUrl(pic.getPath()) || file.exists()) {
                        Photo photo = pic.convertToPhoto();
                        photo.setId(++pid);
                        photos.add(photo);
                    } else {
                        deletedPics.add(pic);
                    }
                }
                realm.beginTransaction();
                for (RealmJsonPic pic : deletedPics) {
                    pic.deleteFromRealm();
                }
                realm.commitTransaction();
            }
            initChannel();
        }
        etTitle.setText(EmojiUtil.parseEmojiByText(this, title, emojiSize));
        etTitle.setSelection(etTitle.length());
        etContent.setText(EmojiUtil.parseEmojiByText(this, content, emojiSize));
        if (CommonUtil.isEmpty(secondaryContentHint)) {
            tvSecondaryContentHint.setVisibility(View.GONE);
        } else {
            tvSecondaryContentHint.setVisibility(View.VISIBLE);
            tvSecondaryContentHint.setText(secondaryContentHint);
        }
        tvTextCount.setText(String.valueOf(TITLE_LEN_MAX - CommonUtil.getTextLength(etTitle
                .getText())));
        addNewButtonAndRefresh();
    }

    @OnTouch({R.id.scroll_view, R.id.recycler_view})
    boolean onTouch(View v, MotionEvent event) {
        if (imm != null && isShowImm && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
        if (isShowEmoji) {
            isShowEmoji = false;
            emojiLayout.setVisibility(View.GONE);
            btnAddEmoji.setImageResource(R.mipmap.icon_face_gray);
        }
        return false;
    }

    @OnFocusChange({R.id.et_title, R.id.et_content})
    void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            return;
        }
        if (v == etTitle) {
            etTitle.setSelection(etTitle.length());
            tvTextCount.setText(String.valueOf(TITLE_LEN_MAX - CommonUtil.getTextLength(etTitle
                    .getText())));
        } else {
            etContent.setSelection(etContent.length());
            tvSecondaryContentHint.setVisibility(View.GONE);
            tvTextCount.setText(String.valueOf(CONTENT_LEN_MAX - CommonUtil.getTextLength
                    (etContent.getText())));
        }
    }

    @OnClick(R.id.add_img_layout)
    void onAddImgs() {
        Intent intent = new Intent(this, ImageChooserActivity.class);
        intent.putExtra(ImageChooserActivity.INTENT_LIMIT, LIMIT - photos.size());
        startActivityForResult(intent, Constants.RequestCode.PHOTO_FROM_GALLERY);
    }

    @OnClick(R.id.btn_add_emoji)
    void onAddEmoji() {
        if (isShowImm) {
            isShowEmoji = true;
            if (imm != null && getCurrentFocus() != null) {
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

    @OnClick({R.id.scroll_holder_layout, R.id.tv_secondary_content_hint})
    void onShowImm() {
        etContent.requestFocus();
        if (imm != null && !isShowImm && getCurrentFocus() != null) {
            imm.toggleSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    0,
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @OnClick(R.id.tv_text_count)
    void onHideImm() {
        isShowEmoji = false;
        emojiLayout.setVisibility(View.GONE);
        btnAddEmoji.setImageResource(R.mipmap.icon_face_gray);
        if (imm != null && isShowImm && getCurrentFocus() != null) {
            imm.toggleSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    0,
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @OnClick(R.id.btn_select_channel_hint)
    void onHideSelectChannelHint() {
        btnSelectChannelHint.setVisibility(View.GONE);
        OncePrefUtil.doneThis(this,
                HljCommon.SharedPreferencesNames.PREF_SELECT_CHANNEL_HINT_CLICKED);
    }

    @OnClick(R.id.channel_layout)
    void onSelectChannel() {
        onHideSelectChannelHint();
        Intent intent = new Intent(this, SelectCommunityChannelActivity.class);
        intent.putExtra(SelectCommunityChannelActivity.ARG_CHANNEL_ID, channelId);
        startActivityForResult(intent, Constants.RequestCode.SELECT_COMMUNITY_CHANNEL);
    }

    @OnClick(R.id.btn_create)
    void onCreate() {
        if (CommonUtil.getTextLength(etTitle.getText()) > TITLE_LEN_MAX || CommonUtil.getTextLength(
                etTitle.getText()) < TITLE_LEN_MIN) {
            ToastUtil.showToast(this, null, R.string.msg_wrong_number_of_thread_title);
            return;
        }
        if (CommonUtil.getTextLength(etContent.getText()) > CONTENT_LEN_MAX || CommonUtil
                .getTextLength(
                etContent.getText()) < CONTENT_LEN_MIN) {
            ToastUtil.showToast(this,
                    getString(R.string.msg_wrong_number_of_thread_content,
                            CONTENT_LEN_MIN,
                            CONTENT_LEN_MAX),
                    0);
            return;
        }
        if (channelId == 0) {
            ToastUtil.showToast(this, null, R.string.msg_empty_channel);
            return;
        }
        if (progressDialog == null || !progressDialog.isShowing()) {
            progressDialog = DialogUtil.getRoundProgress(this);
            progressDialog.show();
        }
        if (CommonUtil.isCollectionEmpty(photos)) {
            createThread();
        } else {
            if (uploadSubs != null) {
                uploadSubs.clear();
            }
            uploadSubs = new SubscriptionList();
            new PhotoListUploadUtil(this,
                    photos,
                    progressDialog,
                    uploadSubs,
                    new OnFinishedListener() {
                        @Override
                        public void onFinished(Object... objects) {
                            createThread();
                        }
                    }).startUpload();
        }
    }

    private void createThread() {
        if (isFinishing()) {
            return;
        }
        if (progressDialog == null || !progressDialog.isShowing()) {
            return;
        }
        CommonUtil.unSubscribeSubs(createSub);
        createSub = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<JsonElement>() {
                    @Override
                    public void onNext(final JsonElement jsonElement) {
                        final long id = CommonUtil.getAsLong(jsonElement, "id");
                        if (progressDialog == null || !progressDialog.isShowing()) {
                            createSucceed(id);
                            return;
                        }
                        progressDialog.onProgressFinish();
                        progressDialog.setCancelable(false);
                        progressDialog.onComplete(new HljRoundProgressDialog.OnCompleteListener() {
                            @Override
                            public void onCompleted() {
                                createSucceed(id);
                            }
                        });
                    }
                })
                .setOnErrorListener(new SubscriberOnErrorListener() {
                    @Override
                    public void onError(Object o) {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                })
                .build();
        Map<String, Object> map = new HashMap<>();
        JsonArray array = new JsonArray();
        for (Photo photo : photos) {
            if (!TextUtils.isEmpty(photo.getImagePath())) {
                JsonObject object = new JsonObject();
                object.addProperty("path", photo.getImagePath());
                object.addProperty("kind", 2);
                object.addProperty("width", photo.getWidth());
                object.addProperty("height", photo.getHeight());
                array.add(object);
            }
        }
        if (array.size() > 0) {
            map.put("pics", array);
        }
        map.put("title",
                etTitle.getText()
                        .toString());
        map.put("message",
                etContent.getText()
                        .toString());
        if (thread != null) {
            map.put("thread_id", thread.getId());
            map.put("post_id",
                    thread.getPost()
                            .getId());
            CommunityApi.modifyThreadObb(map)
                    .subscribe(createSub);
        } else {
            map.put("community_channel_id", channelId);
            map.put("cid",
                    Session.getInstance()
                            .getMyCity(this)
                            .getId());
            //新娘说活动入口进来的 要传communityActivityId
            if (communityActivityId > 0) {
                map.put("community_activity_id", communityActivityId);
            }
            CommunityApi.createThreadObb(map)
                    .subscribe(createSub);
        }
    }

    private void createSucceed(long id) {
        deleteDraft();
        if (thread == null) {
            ToastUtil.showCustomToast(this, R.string.msg_success_to_create_thread);
            onThreadDetail(id);
        } else {
            ToastUtil.showCustomToast(this, R.string.msg_success_to_modify_thread);
            setResult(RESULT_OK, getIntent());
            finish();
            overridePendingTransition(R.anim.activity_anim_default, R.anim.slide_out_down);
        }
    }

    private void onThreadDetail(final long id) {
        Intent intent = new Intent(this, CommunityThreadDetailActivity.class);
        intent.putExtra(CommunityThreadDetailActivity.ARG_ID, id);
        intent.putExtra(CommunityThreadDetailActivity.ARG_IS_AFTER_CREATED, true);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.btn_back)
    public void onBackPressed() {
        if (isShowEmoji) {
            onHideImm();
            return;
        }
        if (exitDialog != null && exitDialog.isShowing()) {
            return;
        }
        if (thread != null && (!TextUtils.equals(etTitle.getText(),
                thread.getTitle()) || !TextUtils.equals(etContent.getText(),
                thread.getPost()
                        .getMessage()) || isPhotosChanged() || channelId != thread.getChannel()
                .getId())) {
            exitDialog = DialogUtil.createDoubleButtonDialog(this,
                    getString(R.string.msg_quit_edit_thread),
                    getString(R.string.label_continue_edit),
                    getString(R.string.label_quit2),
                    null,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                            overridePendingTransition(R.anim.activity_anim_default,
                                    R.anim.slide_out_down);
                        }
                    });
            exitDialog.show();
        } else if ((draft == null && (etTitle.length() > 0 || etContent.length() > 0 || photos
                .size() > 0)) || (draft != null && isDraftChanged())) {
            exitDialog = DialogUtil.createDoubleButtonDialog(this,
                    getString(R.string.msg_save_thread_draft),
                    getString(R.string.label_save),
                    getString(R.string.label_quit2),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            saveDraft();
                            finish();
                            overridePendingTransition(R.anim.activity_anim_default,
                                    R.anim.slide_out_down);
                        }
                    },
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteDraft();
                            finish();
                            overridePendingTransition(R.anim.activity_anim_default,
                                    R.anim.slide_out_down);
                        }
                    });
            exitDialog.show();
        } else {
            finish();
            overridePendingTransition(R.anim.activity_anim_default, R.anim.slide_out_down);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case Constants.RequestCode.PHOTO_FROM_GALLERY:
                    ArrayList<Photo> selectedPhotos = data.getParcelableArrayListExtra(
                            ImageChooserActivity.ARG_SELECTED_PHOTOS);
                    if (!CommonUtil.isCollectionEmpty(selectedPhotos)) {
                        for (Photo photo : selectedPhotos) {
                            photo.setId(++pid);
                            photos.add(photo);
                        }
                        addNewButtonAndRefresh();
                    }
                    break;
                case Constants.RequestCode.SELECT_COMMUNITY_CHANNEL:
                    channelId = data.getLongExtra(SelectCommunityChannelActivity.ARG_CHANNEL_ID, 0);
                    channelTitle = data.getStringExtra(SelectCommunityChannelActivity
                            .ARG_CHANNEL_TITLE);
                    tvChannelTitle.setText(channelTitle);
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void addNewButtonAndRefresh() {
        if (CommonUtil.isCollectionEmpty(photos)) {
            recyclerView.setVisibility(View.GONE);
            tvImgDragHint.setVisibility(View.GONE);
            tvImgCount.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tvImgDragHint.setVisibility(View.VISIBLE);
            tvImgCount.setVisibility(View.VISIBLE);
            tvImgCount.setText(String.valueOf(photos.size()));
        }
        int size = adapter.getItemCount();
        int rows = 1;
        if (size > 8) {
            rows = 3;
        } else if (size > 4) {
            rows = 2;
        }
        recyclerView.getLayoutParams().height = Math.round(imageSize * rows + rows * 10 * dm
                .density);
        adapter.notifyDataSetChanged();
    }

    /**
     * 获取草稿
     *
     * @return
     */
    private CommunityDraft getDraft() {
        CommunityDraft draft;
        if (channel != null) {
            draft = realm.where(CommunityDraft.class)
                    .equalTo("type", CommunityDraft.TYPE_CHANNEL)
                    .equalTo("channelId", channel.getId())
                    .equalTo("userId", user.getId())
                    .findFirst();
        } else {
            draft = realm.where(CommunityDraft.class)
                    .equalTo("type", CommunityDraft.TYPE_OTHERS)
                    .equalTo("userId", user.getId())
                    .findFirst();
        }
        return draft;
    }

    /**
     * 保存草稿到数据库
     */
    private void saveDraft() {
        realm.beginTransaction();
        if (draft == null) {
            draft = realm.createObject(CommunityDraft.class);
            draft.setCreatedAt(Calendar.getInstance()
                    .getTime());
            if (channel != null) {
                //channel!=null代表从频道页进来发帖，跟其它地方的发帖区分类型
                draft.setType(CommunityDraft.TYPE_CHANNEL);
            } else {
                draft.setType(CommunityDraft.TYPE_OTHERS);
            }
        }
        draft.setUserId(user.getId());
        draft.setChannelId(channelId);
        draft.setChannelTitle(channelTitle);
        draft.setTitle(etTitle.getText()
                .toString());
        draft.setContent(etContent.getText()
                .toString());
        draft.setUpdatedAt(Calendar.getInstance()
                .getTime());

        RealmList<RealmJsonPic> pics = new RealmList<>();
        for (Photo photo : photos) {
            RealmJsonPic pic = new RealmJsonPic();
            pic.setId(photo.getId());
            pic.setPath(photo.getImagePath());
            pic.setWidth(photo.getWidth());
            pic.setHeight(photo.getHeight());
            pics.add(pic);
        }
        draft.getPics()
                .deleteAllFromRealm();
        draft.getPics()
                .addAll(pics);
        realm.commitTransaction();
    }

    /**
     * 删除草稿
     */
    private void deleteDraft() {
        if (draft != null) {
            realm.beginTransaction();
            draft.deleteFromRealm();
            realm.commitTransaction();
        }
    }

    /**
     * 检查编辑现有话题的图片时,图片的内容是否有过修改
     *
     * @return
     */
    private boolean isPhotosChanged() {
        if (photos.size() != thread.getPost()
                .getPhotos()
                .size()) {
            return true;
        }
        for (int i = 0; i < thread.getPost()
                .getPhotos()
                .size(); i++) {
            Photo photo = thread.getPost()
                    .getPhotos()
                    .get(i);
            Photo pic = photos.get(i);
            if (!pic.getImagePath()
                    .equals(photo.getImagePath())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 退出时判断草稿是否被修改过,从而决定是否需要再次提示存储草稿
     *
     * @return
     */
    private boolean isDraftChanged() {
        if (!draft.getTitle()
                .equals(etTitle.getText()
                        .toString())) {
            return true;
        }
        if (!draft.getContent()
                .equals(etContent.getText()
                        .toString())) {
            return true;
        }
        if (draft.getChannelId() != channelId) {
            return true;
        }
        if (photos.size() != draft.getPics()
                .size()) {
            return true;
        }
        for (int i = 0; i < draft.getPics()
                .size(); i++) {
            RealmJsonPic draftPic = draft.getPics()
                    .get(i);
            Photo nowPic = photos.get(i);
            if (!draftPic.getPath()
                    .equals(nowPic.getImagePath())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 编辑状态的话题以及传了频道model进来的都不显示“选择频道”布局
     */
    private void initChannel() {
        if (channel != null) {
            channelId = channel.getId();
            channelLayout.setVisibility(View.GONE);
            btnSelectChannelHint.setVisibility(View.GONE);
            return;
        }
        channelLayout.setVisibility(View.VISIBLE);
        btnSelectChannelHint.setVisibility(OncePrefUtil.hasDoneThis(this,
                HljCommon.SharedPreferencesNames.PREF_SELECT_CHANNEL_HINT_CLICKED) ? View.GONE :
                View.VISIBLE);
        if (draft != null) {
            channelId = draft.getChannelId();
            channelTitle = draft.getChannelTitle();
        }
        if (channelId > 0) {
            tvChannelTitle.setText(channelTitle);
            return;
        }
        if (getChannelSub == null || getChannelSub.isUnsubscribed()) {
            getChannelSub = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<CommunityChannel>() {
                        @Override
                        public void onNext(CommunityChannel channel) {
                            channelId = channel.getId();
                            channelTitle = channel.getTitle();
                            tvChannelTitle.setText(channelTitle);
                        }
                    })
                    .build();
            CommunityApi.getOneChannelObb()
                    .subscribe(getChannelSub);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (dragDropManager != null) {
            dragDropManager.cancelDrag();
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(createSub, getChannelSub, uploadSubs);
        if (realm != null) {
            realm.close();
        }
        if (dragDropManager != null) {
            dragDropManager.release();
            dragDropManager = null;
        }
        if (recyclerView != null) {
            recyclerView.setItemAnimator(null);
            recyclerView.setAdapter(null);
            recyclerView = null;
        }
        if (wrappedAdapter != null) {
            WrapperAdapterUtils.releaseAll(wrappedAdapter);
            wrappedAdapter = null;
        }
        layoutManager = null;
    }
}