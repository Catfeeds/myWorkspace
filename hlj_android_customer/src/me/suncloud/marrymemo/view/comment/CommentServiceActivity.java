package me.suncloud.marrymemo.view.comment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.animator.RefactoredDefaultItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.interfaces.OnFinishedListener;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.models.ServiceComment;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearButton;
import com.hunliji.hljcommonlibrary.views.widgets.HljRoundProgressDialog;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.PhotoListUploadUtil;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.adapters.DraggableImgGridAdapter;
import com.hunliji.hljimagelibrary.views.activities.ImageChooserActivity;
import com.hunliji.hljimagelibrary.views.activities.PicsPageViewActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.comment.CommentApi;
import rx.internal.util.SubscriptionList;

/**
 * 本地服务评价页
 * Created by chen_bin on 2018/1/8 0008.
 */
public class CommentServiceActivity extends HljBaseNoBarActivity implements
        DraggableImgGridAdapter.OnItemAddListener, OnItemClickListener<Photo>,
        DraggableImgGridAdapter.OnItemDeleteListener {

    @BindView(R.id.tv_grade)
    TextView tvGrade;
    @BindView(R.id.rating_bar_layout)
    LinearLayout ratingBarLayout;
    @BindView(R.id.et_content)
    EditText etContent;
    @BindView(R.id.tv_count_hint)
    TextView tvCountHint;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.img_add_photos_hint)
    ImageView imgAddPhotosHint;
    @BindView(R.id.tv_comment_hint)
    TextView tvCommentHint;
    @BindView(R.id.img_check)
    ImageView imgCheck;
    @BindView(R.id.check_layout)
    LinearLayout checkLayout;
    @BindView(R.id.scroll_view)
    ScrollView scrollView;

    private RecyclerViewDragDropManager dragDropManager;
    private DraggableImgGridAdapter gridAdapter;
    private RecyclerView.Adapter adapter;

    private DisplayMetrics dm;
    private HljRoundProgressDialog progressDialog;
    private Dialog exitDialog;

    private ServiceComment comment;

    private boolean isShowedAddPhotosHintView; //是否已经显示过添加图片的提示
    private boolean isSyncNoteChecked; //同步笔记是否自动选中过
    private boolean isChecked; //同步笔记当前是否选中

    private long pid;//九宫格的图片需要设置不同的id，id需要一直加，不能重复

    private int updatedRating; //用于评论的修改跟comment.getId()>0配合使用
    private int lastRating; //上一次的评分
    private int imageSize;

    private SubscriptionList uploadSubs;
    private HljHttpSubscriber commentSub;

    private final static int LIMIT = 9;
    private final static int MAX_RATING = 5;

    public final static String ARG_COMMENT = "commentService";
    public final static String ARG_SUB_ORDER_NO = "sub_order_no";
    public final static String ARG_MERCHANT_ID = "merchant_Id";
    public final static String ARG_KNOW_TYPE = "know_type";

    private final static String[] CONTENT_HINTS = {"先消消气，告诉我们遇到了什么问题，我们一定为你解决～",
            "遇到了什么问题吗？我们很乐意为你提供帮助～", "有什么不足之处吗？和我们说说呗～", "说说你的服务感受呗，对新娘们有很多帮助呢～",
            "快告诉新娘们服务有多棒吧！大家都等着你的评价呢～"};
    private String[] COUNT_HINTS = {"至少5个字哦", "加油！再写%s个字就可以得到 20 枚金币", "如果再加%s个字和图片就可以得到 50枚金币哦",
            "不错哦！更详细的服务体验可以获得更多的赞赏哦～", "上传图片就可以得到 50 枚金币哦"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_service);
        ButterKnife.bind(this);
        setDefaultStatusBarPadding();
        initValues();
        initViews();
    }

    public void initValues() {
        dm = getResources().getDisplayMetrics();
        imageSize = (int) ((dm.widthPixels - 62 * dm.density) / 4);
        String subOrderNo = getIntent().getStringExtra(ARG_SUB_ORDER_NO);
        long merchantId = getIntent().getLongExtra(ARG_MERCHANT_ID, 0);
        int knowType = getIntent().getIntExtra(ARG_KNOW_TYPE, 0);
        comment = getIntent().getParcelableExtra(ARG_COMMENT);
        if (comment == null) {
            comment = new ServiceComment();
            comment.getMerchant()
                    .setId(merchantId);
            comment.setSubOrderNo(subOrderNo);
            comment.setKnowType(knowType);
        } else if (!CommonUtil.isCollectionEmpty(comment.getPhotos())) {
            isShowedAddPhotosHintView = true;
            for (Photo photo : comment.getPhotos()) {
                photo.setId(++pid);
            }
        }
        etContent.setText(comment.getContent());
        etContent.setSelection(etContent.length());
        checkLayout.setVisibility(comment.getId() > 0 ? View.GONE : View.VISIBLE);
        updatedRating = comment.getRating() == 0 || comment.getRating() > MAX_RATING ? MAX_RATING
                : comment.getRating();
        onRatingBarChanged(ratingBarLayout.getChildAt(updatedRating - 1));
    }

    private void initViews() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 4);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new RefactoredDefaultItemAnimator());
        dragDropManager = new RecyclerViewDragDropManager();
        dragDropManager.setDraggingItemShadowDrawable((NinePatchDrawable) ContextCompat.getDrawable(
                this,
                R.drawable.sp_dragged_shadow));
        dragDropManager.setInitiateOnMove(false);
        dragDropManager.setInitiateOnLongPress(true);
        dragDropManager.setLongPressTimeout(500);
        dragDropManager.attachRecyclerView(recyclerView);
        gridAdapter = new DraggableImgGridAdapter(this, comment.getPhotos(), imageSize, LIMIT);
        gridAdapter.setOnItemAddListener(this);
        gridAdapter.setOnItemDeleteListener(this);
        gridAdapter.setOnItemClickListener(this);
        adapter = dragDropManager.createWrappedAdapter(gridAdapter);
        recyclerView.setAdapter(adapter);
        addNewButtonAndRefresh();
    }

    @OnClick(R.id.btn_back)
    public void onBackPressed() {
        if (exitDialog != null && exitDialog.isShowing()) {
            return;
        }
        if (exitDialog == null) {
            exitDialog = DialogUtil.createDoubleButtonDialog(this,
                    getString(R.string.label_share_hint),
                    getString(R.string.label_ok),
                    getString(R.string.label_refuse),
                    null,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CommentServiceActivity.super.onBackPressed();
                        }
                    });
        }
        exitDialog.show();
    }

    @OnTextChanged(R.id.et_content)
    void afterTextChanged(Editable s) {
        comment.setContent(s.toString());
        setCountHint();
        setSyncNoteChecked();
    }

    @OnClick({R.id.rating_bar, R.id.rating_bar2, R.id.rating_bar3, R.id.rating_bar4, R.id
            .rating_bar5})
    void onRatingBarChanged(View v) {
        int index = ratingBarLayout.indexOfChild(v);
        if (index < 0) {
            return;
        }
        int rating = index + 1; //当前点击的评分
        if (comment.getRating() == rating && lastRating > 0) {
            return;
        }
        //修改时评分只能往高不能往低
        if (comment.getId() > 0 && updatedRating > rating) {
            ToastUtil.showToast(this, null, R.string.hint_positive_commented);
            return;
        }
        lastRating = rating;
        comment.setRating(rating);
        tvGrade.setText(comment.getGrade(rating));
        etContent.setHint(CONTENT_HINTS[rating - 1]);
        setShowCommentHint();
        setSyncNoteChecked();
        for (int i = 0, size = ratingBarLayout.getChildCount(); i < size; i++) {
            ((CheckableLinearButton) ratingBarLayout.getChildAt(i)).setChecked(i <= index);
        }
    }

    @OnClick(R.id.check_layout)
    void onCheck() {
        if (!isCanCheckSyncNote()) {
            ToastUtil.showToast(this, null, R.string.hint_sync_comment_to_note);
            return;
        }
        isSyncNoteChecked = true;
        check(!isChecked);
    }

    @OnClick(R.id.btn_publish)
    void onPublish() {
        if (etContent.length() < 5) {
            ToastUtil.showToast(this, null, R.string.hint_comment_least_five_words);
            return;
        }
        if (tvCommentHint.getVisibility() == View.VISIBLE) {
            ToastUtil.showToast(this, null, R.string.hint_comment_add_photos);
            return;
        }
        if (progressDialog != null && progressDialog.isShowing()) {
            return;
        }
        progressDialog = DialogUtil.getRoundProgress(this);
        progressDialog.show();
        if (CommonUtil.isCollectionEmpty(comment.getPhotos())) {
            commentService();
        } else {
            if (uploadSubs != null) {
                uploadSubs.clear();
            }
            uploadSubs = new SubscriptionList();
            new PhotoListUploadUtil(this,
                    comment.getPhotos(),
                    progressDialog,
                    uploadSubs,
                    new OnFinishedListener() {
                        @Override
                        public void onFinished(Object... objects) {
                            commentService();
                        }
                    }).startUpload();
        }
    }

    private void commentService() {
        if (isFinishing()) {
            return;
        }
        if (progressDialog == null || !progressDialog.isShowing()) {
            return;
        }
        CommonUtil.unSubscribeSubs(commentSub);
        commentSub = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener() {
                    @Override
                    public void onNext(Object o) {
                        if (progressDialog == null || !progressDialog.isShowing()) {
                            commentSucceed();
                            return;
                        }
                        progressDialog.onProgressFinish();
                        progressDialog.setCancelable(false);
                        progressDialog.onComplete(new HljRoundProgressDialog.OnCompleteListener() {
                            @Override
                            public void onCompleted() {
                                commentSucceed();
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
        CommentApi.commentServiceObb(comment, isChecked)
                .subscribe(commentSub);
    }

    private void commentSucceed() {
        ToastUtil.showCustomToast(this, R.string.msg_comment_success);
        if (comment.getId() > 0) {
            RxBus.getDefault()
                    .post(new RxEvent(RxEvent.RxEventType.EDIT_COMMENT_SUCCESS, comment));
        } else if (TextUtils.isEmpty(comment.getSubOrderNo())) {
            RxBus.getDefault()
                    .post(new RxEvent(RxEvent.RxEventType.COMMENT_SERVICE_SUCCESS, true));
        }
        super.onBackPressed();
    }

    @Override
    public void onItemAdd(Object... objects) {
        Intent intent = new Intent(this, ImageChooserActivity.class);
        intent.putExtra(ImageChooserActivity.INTENT_LIMIT,
                LIMIT - CommonUtil.getCollectionSize(comment.getPhotos()));
        startActivityForResult(intent, Constants.RequestCode.PHOTO_FROM_GALLERY);
    }

    @Override
    public void onItemClick(int position, Photo photo) {
        Intent intent = new Intent(this, PicsPageViewActivity.class);
        intent.putExtra(PicsPageViewActivity.ARG_PHOTOS, comment.getPhotos());
        intent.putExtra(PicsPageViewActivity.ARG_POSITION, position);
        startActivity(intent);
    }

    @Override
    public void onItemDelete(int position) {
        comment.getPhotos()
                .remove(position);
        addNewButtonAndRefresh();
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
                        isShowedAddPhotosHintView = true;
                        for (Photo photo : selectedPhotos) {
                            photo.setId(++pid);
                            comment.getPhotos()
                                    .add(photo);
                        }
                        addNewButtonAndRefresh();
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void addNewButtonAndRefresh() {
        setCountHint();
        setShowAddPhotosHintView();
        setShowCommentHint();
        setSyncNoteChecked();
        int size = gridAdapter.getItemCount();
        int rows = 1;
        if (size > 8) {
            rows = 3;
        } else if (size > 4) {
            rows = 2;
        }
        recyclerView.getLayoutParams().height = Math.round(imageSize * rows + rows * 10 * dm
                .density);
        gridAdapter.notifyDataSetChanged();
    }

    private void setCountHint() {
        String str;
        int len = comment.getContent()
                .length();
        if (len == 0) {
            str = COUNT_HINTS[0];
        } else if (len >= 1 && len < 5) {
            str = String.format(COUNT_HINTS[1], 5 - len);
        } else if (len >= 5 && len < 30) {
            str = String.format(COUNT_HINTS[2], 30 - len);
        } else if (len >= 30 && hasPhotos()) {
            str = COUNT_HINTS[3];
        } else {
            str = COUNT_HINTS[4];
        }
        tvCountHint.setText(str);
    }

    private void setShowAddPhotosHintView() {
        if (isShowedAddPhotosHintView || hasPhotos()) {
            imgAddPhotosHint.setVisibility(View.GONE);
        } else {
            imgAddPhotosHint.setVisibility(View.VISIBLE);
            ((ViewGroup.MarginLayoutParams) imgAddPhotosHint.getLayoutParams()).leftMargin =
                    imageSize + CommonUtil.dp2px(
                    this,
                    17);
        }
    }

    private void setShowCommentHint() {
        tvCommentHint.setText(CommonUtil.fromHtml(this, getString(R.string.html_comment_hint)));
        boolean b = comment.getRating() > 0 && comment.getRating() <= 3 && !hasPhotos() &&
                !isServiceOrder();
        tvCommentHint.setVisibility(b ? View.VISIBLE : View.GONE);
    }

    private void setSyncNoteChecked() {
        boolean b = isCanCheckSyncNote();
        if (!b) {
            check(false);
        } else if (!isSyncNoteChecked) {
            check(true);
        }
    }

    private void check(boolean checked) {
        this.isChecked = checked;
        imgCheck.setImageResource(isChecked ? R.mipmap.icon_check_round_primary_32_32 : R.mipmap
                .icon_check_round_gray_32_32);
    }

    // 1、超过 30 个字，加上图片，自动勾起；
    // 2、如果用户手动去掉勾选，本次评价不再自动勾；
    private boolean isCanCheckSyncNote() {
        return comment.getContent()
                .length() >= 30 && hasPhotos();
    }

    public boolean hasPhotos() {
        return CommonUtil.getCollectionSize(comment.getPhotos()) > 0;
    }

    public boolean isServiceOrder() {
        return !TextUtils.isEmpty(comment.getSubOrderNo()) || comment.getWork()
                .getId() > 0;
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(uploadSubs, commentSub);
    }
}