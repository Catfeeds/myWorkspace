package me.suncloud.marrymemo.view.community;

import android.app.Activity;
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
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.h6ah4i.android.widget.advrecyclerview.animator.RefactoredDefaultItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.interfaces.OnFinishedListener;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityPost;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.community.CommunityApi;
import me.suncloud.marrymemo.util.TextFaceCountWatcher;
import me.suncloud.marrymemo.util.Util;
import rx.internal.util.SubscriptionList;

/**
 * 回帖
 * Created by chen_bin on 2018/3/13 0013.
 */
public class CreatePostActivity extends HljBaseNoBarActivity {

    @BindView(R.id.btn_back)
    TextView btnBack;
    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @BindView(R.id.btn_create)
    Button btnCreate;
    @BindView(R.id.action_holder_layout)
    RelativeLayout actionHolderLayout;
    @BindView(R.id.et_content)
    EditText etContent;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.tv_img_drag_hint)
    TextView tvImgDragHint;
    @BindView(R.id.scroll_view)
    ScrollView scrollView;
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

    private DraggableImgGridAdapter adapter;
    private RecyclerView.Adapter wrappedAdapter;

    private HljRoundProgressDialog progressDialog;
    private Dialog exitDialog;

    private DisplayMetrics dm;
    private InputMethodManager imm;
    private RecyclerViewDragDropManager dragDropManager;
    private GridLayoutManager layoutManager;

    private ArrayList<Photo> photos;
    private CommunityPost post;

    private boolean isShowImm;
    private boolean isShowEmoji;
    private boolean isReplyThread;

    private long pid; //九宫格的图片需要设置不同的id，id需要一直加，不能重复

    private int imageSize;
    private int emojiSize;
    private int emojiImageSize;
    private int emojiPageHeight;

    private HljHttpSubscriber createSub;
    public SubscriptionList uploadSubs;

    private final int LIMIT = 9; //图片最多9张

    private final int CONTENT_LEN_MIN = 1; //内容最少文字5
    private final int CONTENT_LEN_MAX = 200; //内容最多文字2000

    public final static String ARG_POST = "post";
    public final static String ARG_POSITION = "position";
    public final static String ARG_REPAY_TITLE = "reply_title";
    public final static String ARG_IS_REPLY_THREAD = "is_reply_thread";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        ButterKnife.bind(this);
        HljBaseActivity.setActionBarPadding(this, actionHolderLayout);
        initValues();
        initViews();
        initData();
    }

    private void initValues() {
        photos = new ArrayList<>();

        dm = getResources().getDisplayMetrics();
        imageSize = (int) ((dm.widthPixels - 70 * dm.density) / 4);
        emojiSize = Math.round(dm.density * 24);
        emojiImageSize = Math.round((dm.widthPixels - 20 * dm.density) / 7);
        emojiPageHeight = Math.round(emojiImageSize * 3 + 20 * dm.density);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        post = getIntent().getParcelableExtra(ARG_POST);
        isReplyThread = getIntent().getBooleanExtra(ARG_IS_REPLY_THREAD, false);
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
            public void onItemClick(int position, Photo object) {
                Intent intent = new Intent(CreatePostActivity.this, PicsPageViewActivity.class);
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
                        if ("delete".equals(tag)) {
                            EmojiUtil.deleteTextOrImage(etContent);
                        } else {
                            StringBuilder sb = new StringBuilder(tag);
                            int start = etContent.getSelectionStart();
                            int end = etContent.getSelectionEnd();
                            if (start == end) {
                                etContent.getText()
                                        .insert(start, sb);
                            } else {
                                etContent.getText()
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
        etContent.addTextChangedListener(new TextFaceCountWatcher(this,
                etContent,
                tvTextCount,
                CONTENT_LEN_MAX,
                emojiSize));
    }

    private void initData() {
        String title = getIntent().getStringExtra(ARG_REPAY_TITLE);
        if (CommonUtil.isEmpty(title)) {
            title = post.getAuthor()
                    .getName();
        }
        tvToolbarTitle.setText(String.format("回复%s", title));
        tvTextCount.setText(String.valueOf(CONTENT_LEN_MAX - CommonUtil.getTextLength(etContent
                .getText())));
    }

    @OnFocusChange(R.id.et_content)
    void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            return;
        }
        etContent.setSelection(etContent.length());
        tvTextCount.setText(String.valueOf(CONTENT_LEN_MAX - CommonUtil.getTextLength(etContent
                .getText())));
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

    @OnClick(R.id.scroll_holder_layout)
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

    @OnClick(R.id.btn_create)
    void onCreate() {
        if (photos.isEmpty() && (CommonUtil.getTextLength(etContent.getText()) > CONTENT_LEN_MAX
                || Util.getTextLength(
                etContent.getText()) < CONTENT_LEN_MIN)) {
            ToastUtil.showToast(this,
                    getString(R.string.msg_wrong_number_of_reply_content,
                            CONTENT_LEN_MIN,
                            CONTENT_LEN_MAX),
                    0);
            return;
        }
        if (progressDialog == null || !progressDialog.isShowing()) {
            progressDialog = DialogUtil.getRoundProgress(this);
            progressDialog.show();
        }
        if (CommonUtil.isCollectionEmpty(photos)) {
            createPost();
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
                            createPost();
                        }
                    }).startUpload();
        }

    }

    private void createPost() {
        if (isFinishing()) {
            return;
        }
        if (progressDialog == null || !progressDialog.isShowing()) {
            return;
        }
        CommonUtil.unSubscribeSubs(createSub);
        createSub = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener() {
                    @Override
                    public void onNext(Object o) {
                        if (progressDialog == null || !progressDialog.isShowing()) {
                            createSucceed();
                            return;
                        }
                        progressDialog.onProgressFinish();
                        progressDialog.setCancelable(false);
                        progressDialog.onComplete(new HljRoundProgressDialog.OnCompleteListener() {
                            @Override
                            public void onCompleted() {
                                createSucceed();
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
        map.put("thread_id", post.getCommunityThreadId());
        map.put("message",
                etContent.getText()
                        .toString());
        if (!isReplyThread) {
            map.put("quote", post.getId());
        }
        CommunityApi.createPostObb(map)
                .subscribe(createSub);
    }

    private void createSucceed() {
        ToastUtil.showCustomToast(this, R.string.msg_success_to_create_post);
        setResult(Activity.RESULT_OK, getIntent());
        finish();
        overridePendingTransition(R.anim.activity_anim_default, R.anim.slide_out_down);
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
        if (etContent.length() == 0 || CommonUtil.isCollectionEmpty(photos)) {
            finish();
            overridePendingTransition(R.anim.activity_anim_default, R.anim.slide_out_down);
            return;
        }
        if (exitDialog == null) {
            exitDialog = DialogUtil.createDoubleButtonDialog(this,
                    getString(R.string.msg_quit_reply_thread),
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
        }
        exitDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.RequestCode.PHOTO_FROM_GALLERY:
                    if (data == null) {
                        return;
                    }
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
        CommonUtil.unSubscribeSubs(createSub, uploadSubs);
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
