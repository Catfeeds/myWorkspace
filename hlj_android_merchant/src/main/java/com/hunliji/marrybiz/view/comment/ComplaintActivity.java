package com.hunliji.marrybiz.view.comment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.h6ah4i.android.widget.advrecyclerview.animator.RefactoredDefaultItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.interfaces.OnFinishedListener;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearGroup;
import com.hunliji.hljcommonlibrary.views.widgets.HljRoundProgressDialog;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.PhotoListUploadUtil;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.adapters.DraggableImgGridAdapter;
import com.hunliji.hljimagelibrary.views.activities.ImageChooserActivity;
import com.hunliji.hljimagelibrary.views.activities.PicsPageViewActivity;
import com.hunliji.hljquestionanswer.models.QARxEvent;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.api.comment.CommentApi;
import com.hunliji.marrybiz.model.comment.SubmitAppealBody;
import com.hunliji.marrybiz.util.Session;
import com.hunliji.marrybiz.util.TextFaceCountWatcher;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.internal.util.SubscriptionList;

/**
 * Created by hua_rong on 2017/6/10.
 * 申诉
 */
@Route(path = RouterPath.IntentPath.Merchant.QUESTION_COMPLAIN)
public class ComplaintActivity extends HljBaseActivity implements CheckableLinearGroup
        .OnCheckedChangeListener, DraggableImgGridAdapter.OnItemAddListener,
        OnItemClickListener<Photo>, DraggableImgGridAdapter.OnItemDeleteListener {


    @BindView(R.id.cb_group)
    CheckableLinearGroup cbGroup;
    @BindView(R.id.btn_submit)
    Button btnSubmit;
    @BindView(R.id.et_content)
    EditText etContent;
    @BindView(R.id.tv_text_count)
    TextView tvTextCount;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    private String message;
    private int messagePosition;
    private Dialog dialog;
    public RecyclerView.LayoutManager layoutManager;
    public RecyclerViewDragDropManager dragDropManager;
    public RecyclerView.Adapter adapter;
    public ArrayList<Photo> photos;
    public HljRoundProgressDialog progressDialog;
    private static final int LIMIT_SIZE = 9;
    public int imageSize;
    private HljHttpSubscriber subscriber;
    private Context context;
    private long itemId;
    private SubscriptionList uploadSubscriptions;
    private SubmitAppealBody submitAppealBody;
    private int type;
    private int position = -1;
    private boolean isQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint);
        ButterKnife.bind(this);
        context = this;
        cbGroup.setOnCheckedChangeListener(this);
        setSwipeBackEnable(false);
        initValue();
        initView();
    }

    private void initValue() {
        isQuestion = getIntent().getBooleanExtra("is_question", false);
        long id = getIntent().getLongExtra("id", 0);
        type = getIntent().getIntExtra("type", 0);
        submitAppealBody = new SubmitAppealBody();
        submitAppealBody.setType(type);
        if (isQuestion) {
            submitAppealBody.setMerchantId(Session.getInstance()
                    .getCurrentUser(this)
                    .getMerchantId());
            submitAppealBody.setEntityId(id);
            position = getIntent().getIntExtra("position", -1);
        } else {
            if (type == SubmitAppealBody.TYPE_ORDER) {
                submitAppealBody.setOrderCommentId(id);
            } else {
                submitAppealBody.setCommunityCommentId(id);
                position = getIntent().getIntExtra("position", -1);
            }
        }
        photos = new ArrayList<>();
        imageSize = (CommonUtil.getDeviceSize(context).x - CommonUtil.dp2px(context, 62)) / 4;
    }

    private void initView() {
        layoutManager = new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false);
        dragDropManager = new RecyclerViewDragDropManager();
        dragDropManager.setDraggingItemShadowDrawable((NinePatchDrawable) ContextCompat.getDrawable(
                context,
                R.drawable.sp_dragged_shadow));
        dragDropManager.setInitiateOnMove(false);
        dragDropManager.setInitiateOnLongPress(true);
        dragDropManager.setLongPressTimeout(500);
        DraggableImgGridAdapter gridAdapter = new DraggableImgGridAdapter(context,
                photos,
                imageSize,
                LIMIT_SIZE);
        gridAdapter.setOnItemAddListener(this);
        gridAdapter.setOnItemClickListener(this);
        gridAdapter.setOnItemDeleteListener(this);
        adapter = dragDropManager.createWrappedAdapter(gridAdapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new RefactoredDefaultItemAnimator());
        dragDropManager.attachRecyclerView(recyclerView);
        TextFaceCountWatcher watcher = new TextFaceCountWatcher(this,
                etContent,
                tvTextCount,
                200,
                CommonUtil.dp2px(context, 14));
        watcher.setAfterTextChangedListener(new TextFaceCountWatcher.AfterTextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s != null) {
                    String content = etContent.getText()
                            .toString()
                            .trim();
                    submitAppealBody.setContent(content);
                    tvTextCount.setText(String.format("%s/200", s.length()));
                }
            }
        });
        etContent.addTextChangedListener(watcher);
        addNewButtonAndRefresh();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        hideKeyboard(null);
        String content = etContent.getText()
                .toString()
                .trim();
        if (TextUtils.isEmpty(message) && CommonUtil.isCollectionEmpty(photos) && TextUtils.isEmpty(
                content)) {
            finish();
            overridePendingTransition(0, R.anim.slide_out_right);
        } else {
            if (dialog == null) {
                dialog = DialogUtil.createDoubleButtonDialog(this,
                        getString(R.string.label_contents_not_been_submitted_whether_to_leave),
                        getString(R.string.action_cancel),
                        getString(R.string.label_leave),
                        null,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                ComplaintActivity.super.onBackPressed();
                            }
                        });
            }
            if (!dialog.isShowing()) {
                dialog.show();
            }
        }
    }

    @OnClick(R.id.btn_submit)
    public void onSubmit() {
        if (photos != null && !photos.isEmpty()) {
            if (progressDialog != null && progressDialog.isShowing()) {
                return;
            }
            progressDialog = DialogUtil.getRoundProgress(this);
            progressDialog.show();
            if (uploadSubscriptions != null) {
                uploadSubscriptions.clear();
            }
            uploadSubscriptions = new SubscriptionList();
            new PhotoListUploadUtil(this,
                    photos,
                    progressDialog,
                    uploadSubscriptions,
                    new OnFinishedListener() {
                        @Override
                        public void onFinished(Object... objects) {
                            submitAppealBody.setPhotos(photos);
                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            submitComplain();
                        }
                    }).startUpload();
        } else {
            submitComplain();
        }
    }

    /**
     * 提交申诉
     */
    private void submitComplain() {
        CommonUtil.unSubscribeSubs(subscriber);
        Observable<Object> observable = CommentApi.submitAppeal(submitAppealBody);
        subscriber = HljHttpSubscriber.buildSubscriber(context)
                .setOnNextListener(new SubscriberOnNextListener() {
                    @Override
                    public void onNext(Object o) {
                        Toast.makeText(context,
                                getString(R.string.label_submit_success),
                                Toast.LENGTH_LONG)
                                .show();
                        Intent intent = getIntent();
                        intent.putExtra("type", type);
                        intent.putExtra("position", position);
                        setResult(RESULT_OK, intent);
                        RxBus.getDefault()
                                .post(new QARxEvent(QARxEvent.RxEventType.COMPLAIN_SUCCESS,
                                        position));
                        ComplaintActivity.this.finish();
                    }
                })
                .setProgressBar(progressBar)
                .build();
        observable.subscribe(subscriber);
    }

    @Override
    public void onCheckedChanged(CheckableLinearGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.cb_1:
                message = "恶意诋毁";
                messagePosition = 1;
                break;
            case R.id.cb_2:
                message = "广告";
                messagePosition = 2;
                break;
            case R.id.cb_3:
                message = "色情";
                messagePosition = 3;
                break;
            case R.id.cb_4:
                message = "脏话";
                messagePosition = 4;
                break;
            case R.id.cb_5:
                message = "其他";
                messagePosition = 5;
                break;
        }
        submitAppealBody.setReason(isQuestion ? String.valueOf(messagePosition) : message);
        btnSubmit.setEnabled(!TextUtils.isEmpty(message));
    }


    @Override
    public void onItemAdd(Object... objects) {
        if (photos.size() == LIMIT_SIZE) {
            ToastUtil.showToast(this, null, R.string.hint_choose_photo_limit_out);
            return;
        }
        Intent intent = new Intent(context, ImageChooserActivity.class);
        intent.putExtra("limit", LIMIT_SIZE - photos.size());
        startActivityForResult(intent, Constants.RequestCode.PHOTO_FROM_GALLERY);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case Constants.RequestCode.PHOTO_FROM_GALLERY:
                    ArrayList<Photo> selectedPhotos = data.getParcelableArrayListExtra(
                            "selectedPhotos");
                    if (!CommonUtil.isCollectionEmpty(selectedPhotos)) {
                        for (Photo photo : selectedPhotos) {
                            itemId++;
                            photo.setId(itemId);
                            photos.add(photo);
                        }
                        addNewButtonAndRefresh();
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onItemClick(int position, Photo photo) {
        if (photos != null && !photos.isEmpty()) {
            Intent intent = new Intent(this, PicsPageViewActivity.class);
            intent.putExtra("photos", photos);
            intent.putExtra("position", position);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

    @Override
    public void onItemDelete(int position) {
        if (position < photos.size()) {
            photos.remove(position);
            addNewButtonAndRefresh();
        }
    }

    public void addNewButtonAndRefresh() {
        int size = adapter.getItemCount();
        int rows = 1;
        if (size > 8) {
            rows = 3;
        } else if (size > 4) {
            rows = 2;
        }
        recyclerView.getLayoutParams().height = imageSize * rows + CommonUtil.dp2px(context,
                22 + rows * 10);
        adapter.notifyDataSetChanged();
    }


    @Override
    protected void onFinish() {
        super.onFinish();
        if (dragDropManager != null) {
            dragDropManager.release();
            dragDropManager = null;
        }
        if (recyclerView != null) {
            recyclerView.setItemAnimator(null);
            recyclerView.setAdapter(null);
            recyclerView = null;
        }
        if (adapter != null) {
            WrapperAdapterUtils.releaseAll(adapter);
            adapter = null;
        }
        layoutManager = null;
        CommonUtil.unSubscribeSubs(subscriber, uploadSubscriptions);
    }

}
