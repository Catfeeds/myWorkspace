package com.hunliji.marrybiz.view.event;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.animator.RefactoredDefaultItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.interfaces.OnFinishedListener;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljcommonlibrary.views.widgets.HljRoundProgressDialog;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.PhotoListUploadUtil;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.adapters.DraggableImgGridAdapter;
import com.hunliji.hljimagelibrary.views.activities.ImageChooserActivity;
import com.hunliji.hljimagelibrary.views.activities.PicsPageViewActivity;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.api.event.EventApi;
import com.hunliji.marrybiz.model.event.RecordInfo;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import rx.internal.util.SubscriptionList;

/**
 * 申请活动界面
 * Created by chen_bin on 2016/9/5 0005.
 */
@RuntimePermissions
public class ApplyEventActivity extends HljBaseActivity implements DraggableImgGridAdapter
        .OnItemAddListener, OnItemClickListener<Photo>, DraggableImgGridAdapter
        .OnItemDeleteListener {

    @BindView(R.id.scroll_view)
    ScrollView scrollView;
    @BindView(R.id.status_layout)
    LinearLayout statusLayout;
    @BindView(R.id.tv_status)
    TextView tvStatus;
    @BindView(R.id.tv_status_reason)
    TextView tvStatusReason;
    @BindView(R.id.example_layout)
    LinearLayout exampleLayout;
    @BindView(R.id.title_layout)
    LinearLayout titleLayout;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.winner_limit_layout)
    LinearLayout winnerLimitLayout;
    @BindView(R.id.tv_star)
    TextView tvStar;
    @BindView(R.id.tv_winner_limit)
    TextView tvWinnerLimit;
    @BindView(R.id.sign_up_limit_layout)
    LinearLayout signUpLimitLayout;
    @BindView(R.id.tv_star2)
    TextView tvStar2;
    @BindView(R.id.tv_star3)
    TextView tvStar3;
    @BindView(R.id.tv_sign_up_limit)
    TextView tvSignUpLimit;
    @BindView(R.id.et_show_time_title)
    EditText etShowTimeTitle;
    @BindView(R.id.tv_star4)
    TextView tvStar4;
    @BindView(R.id.content_layout)
    LinearLayout contentLayout;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.images_layout)
    LinearLayout imagesLayout;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.tv_star5)
    TextView tvStar5;
    @BindView(R.id.merchant_info_layout)
    LinearLayout merchantInfoLayout;
    @BindView(R.id.tv_merchant_info)
    TextView tvMerchantInfo;
    @BindView(R.id.shop_gift_layout)
    LinearLayout shopGiftLayout;
    @BindView(R.id.tv_shop_gift)
    TextView tvShopGift;
    @BindView(R.id.comment_gift_layout)
    LinearLayout commentGiftLayout;
    @BindView(R.id.tv_comment_gift)
    TextView tvCommentGift;
    @BindView(R.id.order_gift_layout)
    LinearLayout orderGiftLayout;
    @BindView(R.id.tv_order_gift)
    TextView tvOrderGift;
    @BindView(R.id.btn_submit)
    Button btnSubmit;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private TextView currentTV;

    private DisplayMetrics dm;
    private InputMethodManager imm;

    private HljRoundProgressDialog progressDialog;
    private RecyclerViewDragDropManager dragDropManager;
    private GridLayoutManager layoutManager;
    private DraggableImgGridAdapter adapter;
    private RecyclerView.Adapter wrappedAdapter;

    private ArrayList<Photo> photos;
    private RecordInfo recordInfo;

    private long id;
    private long pid;
    private int imageSize;

    private HljHttpSubscriber initSub;
    private HljHttpSubscriber applySub;
    public SubscriptionList uploadSubs;

    public final static int LIMIT = 9;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_event);
        ButterKnife.bind(this);
        photos = new ArrayList<>();
        recordInfo = new RecordInfo();
        dm = getResources().getDisplayMetrics();
        imageSize = (int) ((dm.widthPixels - 54 * dm.density) / 4);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                getRecordDetail();
            }
        });
        id = getIntent().getLongExtra("id", 0);
        exampleLayout.setVisibility(id == 0 ? View.VISIBLE : View.GONE);
        layoutManager = new GridLayoutManager(this, 4);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new RefactoredDefaultItemAnimator());
        dragDropManager = new RecyclerViewDragDropManager();
        dragDropManager.setDraggingItemShadowDrawable((NinePatchDrawable) ContextCompat.getDrawable(
                this,
                R.drawable.sp_dragged_shadow));
        dragDropManager.setInitiateOnMove(false);
        dragDropManager.setInitiateOnLongPress(id == 0);
        dragDropManager.setLongPressTimeout(500);
        dragDropManager.attachRecyclerView(recyclerView);
        adapter = new DraggableImgGridAdapter(this, photos, imageSize, LIMIT);
        adapter.setEdit(id == 0);
        adapter.setOnItemAddListener(this);
        adapter.setOnItemDeleteListener(this);
        adapter.setOnItemClickListener(this);
        wrappedAdapter = dragDropManager.createWrappedAdapter(adapter);
        recyclerView.setAdapter(wrappedAdapter);
        addNewButtonAndRefresh();
        getRecordDetail();
    }

    //获取申请的详情
    private void getRecordDetail() {
        if (id == 0) {
            return;
        }
        if (initSub == null || initSub.isUnsubscribed()) {
            scrollView.setVisibility(View.GONE);
            initSub = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<RecordInfo>() {
                        @Override
                        public void onNext(RecordInfo o) {
                            recordInfo = o;
                            btnSubmit.setVisibility(View.GONE);
                            statusLayout.setVisibility(View.VISIBLE);
                            //审核中，审核不通过等。
                            tvStatus.setText(recordInfo.getStatus() == 0 || recordInfo.getStatus
                                    () == 3 ? R.string.label_reviewing : recordInfo.getStatus()
                                    == 1 ? R.string.label_review_pass : R.string
                                    .label_review_failed);
                            if (recordInfo.getStatus() == 2 && !TextUtils.isEmpty(recordInfo
                                    .getReason())) {
                                tvStatusReason.setVisibility(View.VISIBLE);
                                tvStatusReason.setText(getString(R.string.hint_privilege_refuse2,
                                        recordInfo.getReason()));
                            }
                            //活动标题
                            tvTitle.setHint(null);
                            tvTitle.setText(recordInfo.getTitle());
                            if (TextUtils.isEmpty(recordInfo.getTitle())) {
                                titleLayout.setClickable(false);
                                tvTitle.setCompoundDrawables(null, null, null, null);
                            }
                            //是否抽奖
                            winnerLimitLayout.setClickable(false);
                            tvStar.setVisibility(View.GONE);
                            tvWinnerLimit.setHint(null);
                            tvWinnerLimit.setCompoundDrawables(null, null, null, null);
                            tvWinnerLimit.setText(recordInfo.getHasPrize() == 0 ? R.string
                                    .label_no_prize : R.string.label_prize);
                            //活动名额
                            signUpLimitLayout.setClickable(false);
                            tvStar2.setVisibility(View.GONE);
                            tvSignUpLimit.setHint(null);
                            tvSignUpLimit.setCompoundDrawables(null, null, null, null);
                            tvSignUpLimit.setText(recordInfo.getSignUpLimit());
                            //活动时间
                            tvStar3.setVisibility(View.GONE);
                            etShowTimeTitle.setHint(null);
                            etShowTimeTitle.setEnabled(false);
                            etShowTimeTitle.setText(recordInfo.getShowTimeTitle());
                            //活动内容
                            tvStar4.setVisibility(View.GONE);
                            tvContent.setText(recordInfo.getContent());
                            if (TextUtils.isEmpty(recordInfo.getContent())) {
                                contentLayout.setClickable(false);
                                tvContent.setCompoundDrawables(null, null, null, null);
                            }
                            //9宫格图片
                            adapter.setPhotos(recordInfo.getImgs());
                            if (CommonUtil.isCollectionEmpty(photos)) {
                                imagesLayout.setVisibility(View.GONE);
                            } else {
                                imagesLayout.setVisibility(View.VISIBLE);
                                addNewButtonAndRefresh();
                            }
                            //商家简介
                            tvStar5.setVisibility(View.GONE);
                            tvMerchantInfo.setHint(null);
                            tvMerchantInfo.setText(recordInfo.getMerchantInfo());
                            if (TextUtils.isEmpty(recordInfo.getMerchantInfo())) {
                                merchantInfoLayout.setClickable(false);
                                tvMerchantInfo.setCompoundDrawables(null, null, null, null);
                            }
                            //到店礼
                            tvShopGift.setHint(null);
                            tvShopGift.setText(recordInfo.getShopGift());
                            if (TextUtils.isEmpty(recordInfo.getShopGift())) {
                                shopGiftLayout.setClickable(false);
                                tvShopGift.setCompoundDrawables(null, null, null, null);
                            }
                            //好评礼
                            tvCommentGift.setHint(null);
                            tvCommentGift.setText(recordInfo.getCommentGift());
                            if (TextUtils.isEmpty(recordInfo.getCommentGift())) {
                                commentGiftLayout.setClickable(false);
                                tvCommentGift.setCompoundDrawables(null, null, null, null);
                            }
                            //下单礼
                            tvOrderGift.setHint(null);
                            tvOrderGift.setText(recordInfo.getOrderGift());
                            if (TextUtils.isEmpty(recordInfo.getOrderGift())) {
                                orderGiftLayout.setClickable(false);
                                tvOrderGift.setCompoundDrawables(null, null, null, null);
                            }
                        }
                    })
                    .setProgressBar(progressBar)
                    .setEmptyView(emptyView)
                    .setContentView(scrollView)
                    .build();
            EventApi.getRecordDetailObb(id)
                    .subscribe(initSub);
        }
    }

    @OnClick({R.id.title_layout, R.id.winner_limit_layout, R.id.sign_up_limit_layout, R.id
            .content_layout, R.id.merchant_info_layout, R.id.shop_gift_layout, R.id
            .comment_gift_layout, R.id.order_gift_layout})
    public void onNextPage(View v) {
        switch (v.getId()) {
            case R.id.title_layout:
                currentTV = tvTitle;
                break;
            case R.id.winner_limit_layout:
                currentTV = tvWinnerLimit;
                break;
            case R.id.sign_up_limit_layout:
                currentTV = tvSignUpLimit;
                break;
            case R.id.content_layout:
                currentTV = tvContent;
                break;
            case R.id.merchant_info_layout:
                currentTV = tvMerchantInfo;
                break;
            case R.id.shop_gift_layout:
                currentTV = tvShopGift;
                break;
            case R.id.comment_gift_layout:
                currentTV = tvCommentGift;
                break;
            case R.id.order_gift_layout:
                currentTV = tvOrderGift;
                break;

        }
        Intent intent = new Intent(this, CommonEditActivity.class);
        intent.putExtra("content",
                currentTV.getText()
                        .toString());
        intent.putExtra("has_prize", recordInfo.getHasPrize());
        intent.putExtra("res_id", v.getId());
        intent.putExtra("is_edit", id == 0);
        startActivityForResult(intent, Constants.RequestCode.EDIT_TEXT);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    @OnClick(R.id.btn_submit)
    public void onSubmit() {
        ApplyEventActivityPermissionsDispatcher.publishWithCheck(this);
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    void publish() {
        if (tvWinnerLimit.length() == 0) {
            ToastUtil.showToast(this, null, R.string.hint_enter_winner_limit);
            return;
        }
        if (tvSignUpLimit.length() == 0) {
            ToastUtil.showToast(this, null, R.string.hint_enter_sign_up_limit);
            return;
        }
        if (etShowTimeTitle.length() == 0) {
            ToastUtil.showToast(this, null, R.string.hint_enter_show_time_title);
            return;
        }
        if (tvContent.length() == 0) {
            ToastUtil.showToast(this, null, R.string.hint_enter_event_content);
            return;
        }
        if (tvMerchantInfo.length() == 0) {
            ToastUtil.showToast(this, null, R.string.hint_enter_merchant_info);
            return;
        }
        if (progressDialog != null && progressDialog.isShowing()) {
            return;
        }
        if (progressDialog == null || !progressDialog.isShowing()) {
            progressDialog = DialogUtil.getRoundProgress(this);
            progressDialog.show();
        }
        if (CommonUtil.isCollectionEmpty(photos)) {
            applyEvent();
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
                            recordInfo.setImgs(photos);
                            applyEvent();
                        }
                    }).startUpload();
        }
    }

    //提交活动信息
    private void applyEvent() {
        if (isFinishing()) {
            return;
        }
        if (progressDialog == null || !progressDialog.isShowing()) {
            return;
        }
        recordInfo.setTitle(tvTitle.getText()
                .toString());
        recordInfo.setSignUpLimit(tvSignUpLimit.getText()
                .toString());
        recordInfo.setShowTimeTitle(etShowTimeTitle.getText()
                .toString());
        recordInfo.setContent(tvContent.getText()
                .toString());
        recordInfo.setMerchantInfo(tvMerchantInfo.getText()
                .toString());
        recordInfo.setShopGift(tvShopGift.getText()
                .toString());
        recordInfo.setCommentGift(tvCommentGift.getText()
                .toString());
        recordInfo.setOrderGift(tvOrderGift.getText()
                .toString());
        CommonUtil.unSubscribeSubs(applySub);
        applySub = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener() {
                    @Override
                    public void onNext(Object o) {
                        if (progressDialog == null || !progressDialog.isShowing()) {
                            applySucceed();
                            return;
                        }
                        progressDialog.onProgressFinish();
                        progressDialog.setCancelable(false);
                        progressDialog.onComplete(new HljRoundProgressDialog.OnCompleteListener() {
                            @Override
                            public void onCompleted() {
                                applySucceed();
                            }
                        });
                    }
                })
                .setOnErrorListener(new SubscriberOnErrorListener() {
                    @Override
                    public void onError(Object object) {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                })
                .setDataNullable(true)
                .build();
        EventApi.applyEventObb(recordInfo)
                .subscribe(applySub);
    }

    private void applySucceed() {
        ToastUtil.showCustomToast(this, R.string.label_apply_event_success);
        setResult(RESULT_OK);
        onBackPressed();
    }

    private void addNewButtonAndRefresh() {
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
    public void onItemAdd(Object... objects) {
        Intent intent = new Intent(this, ImageChooserActivity.class);
        intent.putExtra("limit", LIMIT - photos.size());
        startActivityForResult(intent, Constants.RequestCode.PHOTO_FROM_GALLERY);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    @Override
    public void onItemClick(int position, Photo photo) {
        ApplyEventActivityPermissionsDispatcher.onPicsPageWithCheck(this, position);
    }

    @Override
    public void onItemDelete(int position) {
        photos.remove(position);
        addNewButtonAndRefresh();
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    void onPicsPage(int position) {
        Intent intent = new Intent(this, PicsPageViewActivity.class);
        intent.putExtra("photos", photos);
        intent.putExtra("position", position);
        startActivity(intent);
    }

    @OnShowRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
    void onRationale(PermissionRequest request) {
        DialogUtil.showRationalePermissionsDialog(this,
                request,
                getString(R.string.permission_r_for_read_external_storage));
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        ApplyEventActivityPermissionsDispatcher.onRequestPermissionsResult(this,
                requestCode,
                grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            switch (requestCode) {
                case Constants.RequestCode.EDIT_TEXT:
                    //如果当前选择的是“是否抽奖”的话，文本的获取则不一样。
                    if (currentTV == tvWinnerLimit) {
                        recordInfo.setHasPrize(data.getIntExtra("has_prize", 0));
                        currentTV.setText(recordInfo.getHasPrize() == 0 ? R.string.label_no_prize
                                : R.string.label_prize);
                    } else {
                        currentTV.setText(data.getStringExtra("content"));
                    }
                    break;
                case Constants.RequestCode.PHOTO_FROM_GALLERY:
                    ArrayList<Photo> selectedPhotos = data.getParcelableArrayListExtra(
                            "selectedPhotos");
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

    //查看活动范例
    @OnClick(R.id.see_example_layout)
    public void onSeeExample() {
        startActivity(new Intent(this, EventExampleListActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    @Override
    public void onBackPressed() {
        if (imm != null && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(this.getCurrentFocus()
                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        super.onBackPressed();
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
        CommonUtil.unSubscribeSubs(initSub, uploadSubs, applySub);
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