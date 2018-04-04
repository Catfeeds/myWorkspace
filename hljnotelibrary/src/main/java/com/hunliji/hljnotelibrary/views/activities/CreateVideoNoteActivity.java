package com.hunliji.hljnotelibrary.views.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.interfaces.OnFinishedListener;
import com.hunliji.hljcommonlibrary.models.Media;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.models.Video;
import com.hunliji.hljcommonlibrary.models.note.Note;
import com.hunliji.hljcommonlibrary.models.note.NoteInspiration;
import com.hunliji.hljcommonlibrary.models.note.NoteMark;
import com.hunliji.hljcommonlibrary.models.note.NoteMedia;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljRoundProgressDialog;
import com.hunliji.hljhttplibrary.entities.HljUploadResult;
import com.hunliji.hljhttplibrary.utils.HljFileUploadBuilder;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.adapters.DraggableImgGridAdapter;
import com.hunliji.hljnotelibrary.HljNote;
import com.hunliji.hljnotelibrary.R;
import com.hunliji.hljnotelibrary.R2;
import com.hunliji.hljnotelibrary.api.NoteApi;
import com.hunliji.hljnotelibrary.models.NoteCategoryMark;
import com.hunliji.hljnotelibrary.models.NotebookType;
import com.hunliji.hljnotelibrary.models.wrappers.CreateNoteResponse;
import com.hunliji.hljnotelibrary.utils.NotePrefUtil;
import com.hunliji.hljnotelibrary.utils.VideoUploadUtil;
import com.hunliji.hljvideolibrary.activities.BaseVideoTrimActivity;
import com.hunliji.hljvideolibrary.activities.VideoChooserActivity;
import com.hunliji.hljvideolibrary.activities.VideoPreviewActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;
import rx.Subscription;

/**
 * 商家端带视频的动态
 * Created by chen_bin on 2017/7/10 0010.
 */
@Route(path = RouterPath.IntentPath.Note.CREATE_NOTE)
@RuntimePermissions
public class CreateVideoNoteActivity extends HljBaseNoBarActivity implements
        DraggableImgGridAdapter.OnItemAddListener, DraggableImgGridAdapter.OnItemDeleteListener,
        OnItemClickListener<Photo> {

    @BindView(R2.id.btn_create)
    Button btnCreate;
    @BindView(R2.id.et_title)
    EditText etTitle;
    @BindView(R2.id.et_content)
    EditText etContent;
    @BindView(R2.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R2.id.tv_category_mark_name)
    TextView tvCategoryMarkName;
    @BindView(R2.id.category_mark_layout)
    LinearLayout categoryMarkLayout;

    private DraggableImgGridAdapter adapter;

    private HljRoundProgressDialog progressDialog;
    private DisplayMetrics dm;

    private ArrayList<Photo> photos;
    private Note note;
    private NoteCategoryMark categoryMark;
    private int imageSize;

    private Subscription uploadSub;
    private HljHttpSubscriber createSub;

    private final static int LIMIT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_video_note___note);
        ButterKnife.bind(this);
        setDefaultStatusBarPadding();
        initValues();
        initViews();
    }

    private void initValues() {
        dm = getResources().getDisplayMetrics();
        imageSize = (int) ((dm.widthPixels - 62 * dm.density) / 4);
        photos = new ArrayList<>();
        note = new Note();
        note.setNoteType(Note.TYPE_VIDEO);
        note.setNotebookType(NotebookType.TYPE_WEDDING_PERSON);
        Photo photo = getIntent().getParcelableExtra("photo");
        if (photo != null) {
            convertPhotoToVideo(photo);
        }
    }

    private void initViews() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 4);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new DraggableImgGridAdapter(this, photos, imageSize, LIMIT);
        adapter.setType(DraggableImgGridAdapter.TYPE_VIDEO);
        adapter.setOnItemAddListener(this);
        adapter.setOnItemClickListener(this);
        adapter.setOnItemDeleteListener(this);
        recyclerView.setAdapter(adapter);
        addNewButtonAndRefresh();
    }

    @OnClick(R2.id.category_mark_layout)
    void onSelectCategoryMark() {
        Intent intent = new Intent(this, SelectNoteCategoryMarkActivity.class);
        intent.putExtra("mark_id", categoryMark != null ? categoryMark.getMarkId() : 0);
        startActivityForResult(intent, HljNote.RequestCode.SELECT_NOTE_CATEGORY_MARK);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    @OnTextChanged(R2.id.et_title)
    void afterTitleTextChanged(Editable s) {
        note.setTitle(s.toString());
    }

    @OnTextChanged(R2.id.et_content)
    void afterContentTextChanged(Editable s) {
        note.setContent(s.toString());
        setCreateButtonStatus();
    }

    @OnClick(R2.id.btn_back)
    public void onBackPressed() {
        DialogUtil.createDoubleButtonDialog(this,
                getString(R.string.msg_confirm_exit_edit___note),
                null,
                null,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        overridePendingTransition(R.anim.activity_anim_default,
                                R.anim.slide_out_down);
                    }
                },
                null)
                .show();
    }

    @Override
    public void onItemClick(int position, Photo photo) {
        Intent intent = new Intent(this, VideoPreviewActivity.class);
        intent.putExtra("uri", Uri.parse(photo.getImagePath()));
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_up, R.anim.activity_anim_default);
    }

    @Override
    public void onItemDelete(final int position) {
        DialogUtil.createDoubleButtonDialog(this,
                getString(R.string.msg_confirm_delete_video___note),
                null,
                null,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        photos.remove(position);
                        addNewButtonAndRefresh();
                    }
                },
                null)
                .show();
    }

    @Override
    public void onItemAdd(Object... objects) {
        Intent intent = new Intent(this, VideoChooserActivity.class);
        intent.putExtra(BaseVideoTrimActivity.ARGS_MAX_VIDEO_LENGTH, HljNote.NOTE_MAX_VIDEO_LENGTH);
        startActivityForResult(intent, HljNote.RequestCode.CHOOSE_VIDEO);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    @OnClick(R2.id.btn_create)
    void onCreate() {
        CreateVideoNoteActivityPermissionsDispatcher.isWifiWithCheck(this);
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    void isWifi() {
        if (CommonUtil.isCollectionEmpty(note.getInspirations())) {
            return;
        }
        Video video = note.getInspirations()
                .get(0)
                .getNoteMedia()
                .getVideo();
        if (TextUtils.isEmpty(video.getLocalPath())) {
            ToastUtil.showToast(this, null, R.string.msg_taking_video_error);
            return;
        }
        if (CommonUtil.isWifi(this)) {
            uploadVideo();
        } else {
            DialogUtil.createDoubleButtonDialog(this,
                    getString(R.string.msg_wifi_state),
                    getString(R.string.label_continue_publish),
                    null,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            uploadVideo();
                        }
                    },
                    null)
                    .show();
        }
    }

    private void uploadVideo() {
        if (progressDialog != null && progressDialog.isShowing()) {
            return;
        }
        if (CommonUtil.isCollectionEmpty(note.getInspirations())) {
            return;
        }
        final Video video = note.getInspirations()
                .get(0)
                .getNoteMedia()
                .getVideo();
        if (TextUtils.isEmpty(video.getLocalPath())) {
            return;
        }
        progressDialog = DialogUtil.getRoundProgress(this);
        progressDialog.show();
        if (!TextUtils.isEmpty(video.getOriginPath()) && !TextUtils.isEmpty(video.getPersistentId
                ())) {
            createNote();
        } else {
            uploadSub = new VideoUploadUtil(this,
                    video.getLocalPath(),
                    HljFileUploadBuilder.UploadFrom.NOTE_MEDIA,
                    progressDialog,
                    new OnFinishedListener() {
                        @Override
                        public void onFinished(Object... objects) {
                            if (objects != null && objects.length > 0) {
                                HljUploadResult hljUploadResult = (HljUploadResult) objects[0];
                                video.setOriginPath(hljUploadResult.getUrl());
                                video.setPersistentId(hljUploadResult.getPersistentId());
                                createNote();
                            }
                        }
                    }).startUpload();
        }
    }

    private void createNote() {
        if (isFinishing()) {
            return;
        }
        if (progressDialog == null || !progressDialog.isShowing()) {
            return;
        }
        CommonUtil.unSubscribeSubs(createSub);
        createSub = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<CreateNoteResponse>() {
                    @Override
                    public void onNext(final CreateNoteResponse response) {
                        if (progressDialog == null || !progressDialog.isShowing()) {
                            createSucceed(response);
                            return;
                        }
                        progressDialog.onProgressFinish();
                        progressDialog.setCancelable(false);
                        progressDialog.onComplete(new HljRoundProgressDialog.OnCompleteListener() {
                            @Override
                            public void onCompleted() {
                                createSucceed(response);
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
                .setDataNullable(true)
                .build();
        NoteApi.createNoteObb(note)
                .subscribe(createSub);
    }

    private void createSucceed(final CreateNoteResponse response) {
        RxBus.getDefault()
                .post(new RxEvent(RxEvent.RxEventType.CREATE_NOTE_SUCCESS, null));
        if (NotePrefUtil.getInstance(this)
                .isVideoNoteCreated()) {
            onNoteDetail(response);
        } else {
            Dialog dialog = DialogUtil.createSingleButtonDialog(this,
                    getString(R.string.msg_create_succeed___note),
                    getString(R.string.msg_video_trans_coding),
                    getString(R.string.label_okay___note),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onNoteDetail(response);
                        }
                    });
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
    }

    private void onNoteDetail(CreateNoteResponse response) {
        Intent intent = new Intent(this, NoteDetailActivity.class);
        intent.putExtra("note_id", response.getId());
        startActivity(intent);
        finish();
    }

    private void addNewButtonAndRefresh() {
        setCreateButtonStatus();
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

    private void setCreateButtonStatus() {
        if (etContent.length() > 0 && !CommonUtil.isCollectionEmpty(photos)) {
            btnCreate.setEnabled(true);
            btnCreate.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
        } else {
            btnCreate.setEnabled(false);
            btnCreate.setTextColor(ContextCompat.getColor(this, R.color.colorGray3));
        }
    }

    private void convertPhotoToVideo(Photo photo) {
        photos.clear();
        photos.add(0, photo);
        Video video = new Video();
        video.setWidth(photo.getWidth());
        video.setHeight(photo.getHeight());
        video.setLocalPath(photo.getImagePath());
        NoteInspiration noteInspiration = new NoteInspiration();
        NoteMedia noteMedia = noteInspiration.getNoteMedia();
        noteMedia.setType(Media.TYPE_VIDEO);
        noteMedia.setVideo(video);
        note.getInspirations()
                .clear();
        note.getInspirations()
                .add(noteInspiration);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case HljNote.RequestCode.SELECT_NOTE_CATEGORY_MARK:
                    if (data != null) {
                        categoryMark = data.getParcelableExtra("category_mark");
                        if (categoryMark == null || categoryMark.getMarkId() == 0) {
                            note.getNoteMarks()
                                    .clear();
                            tvCategoryMarkName.setText(R.string.label_add___note);
                        } else {
                            NoteMark noteMark = new NoteMark();
                            noteMark.setId(categoryMark.getMarkId());
                            noteMark.setName(categoryMark.getName());
                            note.getNoteMarks()
                                    .add(noteMark);
                            tvCategoryMarkName.setText(noteMark.getName());
                        }
                    }
                    break;
                case HljNote.RequestCode.CHOOSE_VIDEO:
                    if (data != null) {
                        Photo photo = data.getParcelableExtra("photo");
                        if (photo != null) {
                            convertPhotoToVideo(photo);
                            addNewButtonAndRefresh();
                        }
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(uploadSub, createSub);
    }
}