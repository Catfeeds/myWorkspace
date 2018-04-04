package me.suncloud.marrymemo.view.comment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hunliji.hljcommonlibrary.interfaces.OnFinishedListener;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.models.product.ProductComment;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljRoundProgressDialog;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.PhotoListUploadUtil;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.adapters.DraggableImgGridAdapter;
import com.hunliji.hljimagelibrary.views.activities.ImageChooserActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.comment.CommentProductSubOrderListAdapter;
import me.suncloud.marrymemo.adpter.comment.viewholder.CommentProductSubOrderViewHolder;
import me.suncloud.marrymemo.api.comment.CommentApi;
import me.suncloud.marrymemo.model.orders.ProductOrder;
import me.suncloud.marrymemo.model.orders.ProductRefundStatus;
import me.suncloud.marrymemo.model.orders.ProductSubOrder;
import me.suncloud.marrymemo.view.MyOrderListActivity;
import rx.internal.util.SubscriptionList;

/**
 * 婚品订单评价
 * Created by chen_bin on 2018/1/5 0005.
 */
public class CommentProductOrderActivity extends HljBaseNoBarActivity implements
        DraggableImgGridAdapter.OnItemAddListener, CommentProductSubOrderViewHolder
        .OnSyncNoteCheckedChangeListener {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private ImageView imgCheck;
    private LinearLayout checkLayout;

    private CommentProductSubOrderListAdapter adapter;
    private HljRoundProgressDialog progressDialog;
    private Dialog exitDialog;

    private List<ProductComment> comments;
    private ProductOrder productOrder;
    private ProductComment comment;

    private boolean isSyncNoteChecked; //同步笔记是否自动选中过
    private boolean isChecked; //当前同步笔记是否选中

    private long pid; //九宫格的图片需要设置不同的id，id需要一直加，不能重复

    private int position = -1;

    private SubscriptionList uploadSubs;
    private HljHttpSubscriber commentSub;

    public final static String ARG_PRODUCT_ORDER = "product_order";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_product_order);
        ButterKnife.bind(this);
        setDefaultStatusBarPadding();
        initValues();
        initViews();
    }

    private void initValues() {
        comments = new ArrayList<>();
        productOrder = getIntent().getParcelableExtra(ARG_PRODUCT_ORDER);
        for (int i = 0; i < productOrder.getSubOrders()
                .size(); i++) {
            ProductSubOrder subOrder = productOrder.getSubOrders()
                    .get(i);
            //过滤已退款的商品，2为后台退款，11为前端退款
            //过滤已经评价的子订单
            if (subOrder.getRefundStatus() == ProductRefundStatus.OLD_REFUND_SUCCESS || subOrder
                    .getRefundStatus() == ProductRefundStatus.REFUND_COMPLETE || subOrder
                    .isCommented()) {
                productOrder.getSubOrders()
                        .remove(subOrder);
                i--;
                continue;
            }
            ProductComment comment = new ProductComment();
            comment.setSku(subOrder.getSku());
            comment.setProduct(subOrder.getProduct());
            comment.setSubOrderId(subOrder.getId());
            comment.setRating(ProductComment.INIT_RATING);
            comments.add(comment);
        }
    }

    private void initViews() {
        View footerView = View.inflate(this, R.layout.sync_comment_to_note_footer, null);
        imgCheck = footerView.findViewById(R.id.img_check);
        checkLayout = footerView.findViewById(R.id.check_layout);
        checkLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isCanCheckSyncNote()) {
                    ToastUtil.showToast(CommentProductOrderActivity.this,
                            null,
                            R.string.hint_sync_comment_to_note);
                    return;
                }
                isSyncNoteChecked = true;
                check(!isChecked);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new SpacesItemDecoration());
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        adapter = new CommentProductSubOrderListAdapter(this, comments);
        adapter.setFooterView(footerView);
        adapter.setOnItemAddListener(this);
        adapter.setOnSyncNoteCheckedChangeListener(this);
        recyclerView.setAdapter(adapter);
    }

    @OnClick(R.id.btn_back)
    public void onBackPressed() {
        if (exitDialog != null && exitDialog.isShowing()) {
            return;
        }
        if (exitDialog == null) {
            exitDialog = DialogUtil.createDoubleButtonDialog(this,
                    getString(R.string.msg_product_comment_back),
                    getString(R.string.label_comment_cancel),
                    getString(R.string.label_wrong_action),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CommentProductOrderActivity.super.onBackPressed();
                        }
                    },
                    null);
        }
        exitDialog.show();
    }

    @OnClick(R.id.btn_publish)
    void onPublish() {
        boolean isEmpty = true;
        int errorIndex = -1;
        for (int i = 0, size = comments.size(); i < size; i++) {
            ProductComment comment = comments.get(i);
            if (comment.getRating() != ProductComment.INIT_RATING) {
                isEmpty = false;
                if (comment.getContent()
                        .length() < 5) {
                    errorIndex = i;
                    break;
                }
            }
        }
        if (isEmpty || errorIndex > -1) {
            ToastUtil.showToast(this, null, R.string.hint_comment_least_five_words);
            recyclerView.scrollToPosition(Math.max(0, errorIndex));
            return;
        }
        if (progressDialog != null && progressDialog.isShowing()) {
            return;
        }
        progressDialog = DialogUtil.getRoundProgress(this);
        progressDialog.show();
        ArrayList<Photo> photos = new ArrayList<>();
        for (ProductComment comment : comments) {
            photos.addAll(comment.getPhotos());
        }
        if (CommonUtil.isCollectionEmpty(photos)) {
            commentProductOrder();
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
                            commentProductOrder();
                        }
                    }).startUpload();
        }
    }

    private void commentProductOrder() {
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
        CommentApi.commentProductOrderObb(productOrder, comments, isChecked)
                .subscribe(commentSub);
    }

    private void commentSucceed() {
        Dialog dialog = DialogUtil.createSingleButtonDialog(this,
                getString(R.string.title_activity_comment_done),
                getString(R.string.action_ok),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(CommentProductOrderActivity.this,
                                MyOrderListActivity.class));
                        CommentProductOrderActivity.super.onBackPressed();
                    }
                });
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    @Override
    public void onItemAdd(Object... objects) {
        if (objects == null || objects.length < 2) {
            return;
        }
        if (objects[0] instanceof Integer) {
            position = (int) objects[0];
        }
        if (objects[1] instanceof ProductComment) {
            comment = (ProductComment) objects[1];
        }
        if (position < 0 || comment == null) {
            return;
        }
        Intent intent = new Intent(this, ImageChooserActivity.class);
        intent.putExtra(ImageChooserActivity.INTENT_LIMIT,
                CommentProductSubOrderViewHolder.LIMIT - CommonUtil.getCollectionSize(comment
                        .getPhotos()));
        startActivityForResult(intent, Constants.RequestCode.PHOTO_FROM_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.RequestCode.PHOTO_FROM_GALLERY:
                    if (data == null || position < 0 || comment == null) {
                        return;
                    }
                    ArrayList<Photo> selectedPhotos = data.getParcelableArrayListExtra(
                            ImageChooserActivity.ARG_SELECTED_PHOTOS);
                    if (!CommonUtil.isCollectionEmpty(selectedPhotos)) {
                        for (Photo photo : selectedPhotos) {
                            photo.setId(++pid);
                            comment.getPhotos()
                                    .add(photo);
                        }
                        if (!adapter.isShowedAddPhotosHintView()) {
                            adapter.setShowedAddPhotosHintView(true);
                            if (position != 0) {
                                adapter.notifyItemChanged(0);
                            }
                        }
                        if (comment.getRating() == ProductComment.INIT_RATING) {
                            comment.setRating(ProductComment.POSITIVE_RATING);
                        }
                        adapter.notifyItemChanged(position);
                        onSyncNoteCheckedChange();
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSyncNoteCheckedChange() {
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
        boolean b = false;
        for (ProductComment comment : comments) {
            if (comment.getContent()
                    .length() >= 30 && !CommonUtil.isCollectionEmpty(comment.getPhotos())) {
                b = true;
                break;
            }
        }
        return b;
    }

    private class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration() {
            space = CommonUtil.dp2px(CommentProductOrderActivity.this, 10);
        }

        @Override
        public void getItemOffsets(
                Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.set(0, 0, 0, space);
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(uploadSubs, commentSub);
    }
}
