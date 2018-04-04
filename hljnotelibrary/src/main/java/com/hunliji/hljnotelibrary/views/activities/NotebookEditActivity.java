package com.hunliji.hljnotelibrary.views.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.interfaces.OnFinishedListener;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.FileUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljRoundProgressDialog;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljimagelibrary.views.activities.ImageChooserActivity;
import com.hunliji.hljimagelibrary.views.activities.ImageCropEditActivity;
import com.hunliji.hljnotelibrary.R;
import com.hunliji.hljnotelibrary.R2;
import com.hunliji.hljnotelibrary.api.NoteApi;
import com.hunliji.hljnotelibrary.models.NoteBookEditBody;
import com.hunliji.hljnotelibrary.models.Notebook;
import com.hunliji.hljhttplibrary.utils.PhotoListUploadUtil;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.internal.util.SubscriptionList;

/**
 * 笔记本编辑页面
 * Created by jinxin on 2017/6/27 0027.
 */

public class NotebookEditActivity extends HljBaseActivity {

    private final int CHOSE_REQUEST_CODE = 101;
    private final int CROP_REQUEST_CODE = 102;

    @BindView(R2.id.img_cover)
    ImageView imgCover;
    @BindView(R2.id.modify_photo_layout)
    LinearLayout modifyPhotoLayout;
    @BindView(R2.id.et_photo_title)
    EditText etPhotoTitle;
    @BindView(R2.id.et_photo_des)
    EditText etPhotoDes;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;

    private HljHttpSubscriber editSubscriber;
    private Notebook notebook;
    private Photo cropPhoto;
    private SubscriptionList uploadSubscriptions;
    private HljRoundProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_edit___note);
        ButterKnife.bind(this);

        initConstant();
    }

    private void initConstant() {
        notebook = getIntent().getParcelableExtra("notebook");
        if (notebook != null) {
            initWidget();
        }
    }

    private void initWidget() {
        String title;
        switch (notebook.getNoteBookType()) {
            case 1:
                title = "婚纱照";
                break;
            case 2:
                title = "婚礼筹备";
                break;
            case 3:
                title = "婚品筹备";
                break;
            case 4:
                title = "婚礼人";
                break;
            default:
                title = getString(R.string.label_note_edit___note);
                break;
        }
        setTitle(title);
        int width = CommonUtil.getDeviceSize(this).x - CommonUtil.dp2px(this, 24);
        int height = width * 9 / 16;
        imgCover.getLayoutParams().height = height;
        imgCover.getLayoutParams().width = width;
        if (notebook != null) {
            etPhotoTitle.setText(notebook.getTitle());
            etPhotoDes.setText(notebook.getDesc());
            setImage(imgCover, notebook.getCoverPath());
        }
    }

    private void setImage(ImageView image, String path) {
        Glide.with(this)
                .load(ImagePath.buildPath(path)
                        .width(image.getLayoutParams().width)
                        .height(image.getLayoutParams().height)
                        .cropPath())
                .apply(new RequestOptions().dontAnimate()
                        .placeholder(R.mipmap.icon_empty_image))
                .into(imgCover);
    }


    @OnClick(R2.id.modify_photo_layout)
    void onChose() {
        Intent intent = new Intent(this, ImageChooserActivity.class);
        intent.putExtra("limit", 1);
        startActivityForResult(intent, CHOSE_REQUEST_CODE);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    @OnClick(R2.id.tv_confirm)
    void onPublish() {
        String title = etPhotoTitle.getText()
                .toString();
        if (TextUtils.isEmpty(title) || TextUtils.getTrimmedLength(title) < 4) {
            Toast.makeText(this, "标题不能少于4个字", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (cropPhoto != null) {
            //图片改变 上传七牛
            if (progressDialog != null && progressDialog.isShowing()) {
                return;
            }
            progressDialog = DialogUtil.getRoundProgress(this);
            progressDialog.show();
            if (uploadSubscriptions != null) {
                uploadSubscriptions.clear();
            }
            uploadSubscriptions = new SubscriptionList();
            ArrayList<Photo> photos = new ArrayList<>();
            photos.add(cropPhoto);
            new PhotoListUploadUtil(this,
                    photos,
                    progressDialog,
                    uploadSubscriptions,
                    new OnFinishedListener() {
                        @Override
                        public void onFinished(Object... objects) {
                            ArrayList<Photo> qiNiuPhotos = (ArrayList<Photo>) objects[0];
                            if (qiNiuPhotos != null && !qiNiuPhotos.isEmpty()) {
                                cropPhoto = qiNiuPhotos.get(0);
                                editNoteBook();
                            }
                        }
                    }).startUpload();
        } else {
            editNoteBook();
        }
    }

    private void editNoteBook() {
        if (notebook == null || notebook.getId() == 0) {
            return;
        }
        if (editSubscriber != null && !editSubscriber.isUnsubscribed()) {
            return;
        }
        String title = etPhotoTitle.getText()
                .toString();
        String desc = etPhotoDes.getText()
                .toString();
        editSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setProgressBar(progressBar)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpResult>() {
                    @Override
                    public void onNext(HljHttpResult hljHttpResult) {
                        if (hljHttpResult != null && hljHttpResult.getStatus() != null) {
                            if (hljHttpResult.getStatus()
                                    .getRetCode() == 0) {
                                if(cropPhoto != null){
                                    FileUtil.deleteFile(new File(cropPhoto.getImagePath()));
                                }
                                Toast.makeText(NotebookEditActivity.this,
                                        "保存成功",
                                        Toast.LENGTH_SHORT)
                                        .show();
                                setResult(Activity.RESULT_OK);
                                onBackPressed();
                            } else {
                                Toast.makeText(NotebookEditActivity.this,
                                        hljHttpResult.getStatus()
                                                .getMsg(),
                                        Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }
                    }
                })
                .build();
        NoteBookEditBody editBody = new NoteBookEditBody();
        editBody.setId(notebook.getId());
        editBody.setTitle(title);
        editBody.setDesc(desc);
        editBody.setPhoto(cropPhoto);

        Observable<HljHttpResult> obb = NoteApi.editNoteBook(editBody);
        obb.subscribe(editSubscriber);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CHOSE_REQUEST_CODE) {
                ArrayList<Photo> photos = data.getParcelableArrayListExtra("selectedPhotos");
                if (photos != null && !photos.isEmpty()) {
                    Photo photo = photos.get(0);
                    startCropImage(photo);
                }
            }
            if (requestCode == CROP_REQUEST_CODE) {
                String cropOutPath = data.getStringExtra("output_path");
                if (cropPhoto == null) {
                    cropPhoto = new Photo();
                }
                cropPhoto.setImagePath(cropOutPath);
                setImage(imgCover, cropOutPath);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void startCropImage(Photo photo) {
        if (photo == null || TextUtils.isEmpty(photo.getImagePath())) {
            return;
        }
        int width = CommonUtil.getDeviceSize(this).x;
        int height = width * 9 / 16;
        Intent intent = new Intent(this, ImageCropEditActivity.class);
        intent.putExtra("source_path", photo.getImagePath());
        intent.putExtra("output_width", width);
        intent.putExtra("output_height", height);
        startActivityForResult(intent, CROP_REQUEST_CODE);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    @Override
    protected void onFinish() {
        CommonUtil.unSubscribeSubs(uploadSubscriptions);
        super.onFinish();
    }
}
