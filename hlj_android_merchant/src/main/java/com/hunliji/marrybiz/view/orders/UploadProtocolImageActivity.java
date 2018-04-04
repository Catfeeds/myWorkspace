package com.hunliji.marrybiz.view.orders;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.hunliji.hljcommonlibrary.interfaces.OnFinishedListener;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljRoundProgressDialog;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.models.Size;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.hunliji.hljimagelibrary.views.activities.ImageChooserActivity;
import com.hunliji.hljimagelibrary.views.activities.PicsPageViewActivity;
import com.hunliji.hljhttplibrary.utils.PhotoListUploadUtil;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.api.order.OrderApi;
import com.hunliji.marrybiz.util.FileUtil;
import com.hunliji.marrybiz.util.JSONUtil;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import rx.internal.util.SubscriptionList;

@RuntimePermissions
public class UploadProtocolImageActivity extends HljBaseNoBarActivity implements View
        .OnClickListener {

    @BindView(R.id.btn_back)
    ImageButton btnBack;
    @BindView(R.id.btn_submit)
    Button btnSubmit;
    @BindView(R.id.action_layout)
    RelativeLayout actionLayout;
    @BindView(R.id.protocol_images_layout)
    GridLayout protocolImagesLayout;
    private int protocolImgViewSize;
    private int protocolImgSize;
    private Dialog selectImageDialog;
    private ArrayList<Photo> images;
    private View addView;
    private Uri currentUri;
    private long orderId;
    private int photosLimit;
    private SubscriptionList uploadSubscriptions;
    private HljRoundProgressDialog progressDialog;
    private HljHttpSubscriber postSub;
    private Dialog backDlg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_protocol_image);
        ButterKnife.bind(this);
        setDefaultStatusBarPadding();

        initValues();
        initViews();
    }

    private void initValues() {
        Point point = CommonUtil.getDeviceSize(this);
        protocolImgViewSize = Math.round((point.x - CommonUtil.dp2px(this, 18)) / 3);
        protocolImgSize = Math.round(protocolImgViewSize - CommonUtil.dp2px(this, 14));
        images = new ArrayList<>();
        orderId = getIntent().getLongExtra("order_id", 0);
        photosLimit = getIntent().getIntExtra("photos_limit", 12);
    }

    private void initViews() {
        addView = View.inflate(this, R.layout.protocol_image_item2, null);
        setImageValue(null, addView);
        if (protocolImagesLayout.getChildCount() < photosLimit) {
            protocolImagesLayout.addView(addView, protocolImgViewSize, protocolImgViewSize);
        }
    }

    private void setImageValue(Photo photo, View view) {
        ImageViewHolder holder = (ImageViewHolder) view.getTag();
        if (holder == null) {
            holder = new ImageViewHolder(view);
            view.setTag(holder);
        }
        holder.imgCover.setOnClickListener(new OnItemClickListener(photo));
        if (photo != null) {
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnDelete.setOnClickListener(new OnDeleteClickListener(view, photo));
            holder.imgCover.setBackgroundColor(ContextCompat.getColor(this, R.color.colorLine));
            Glide.with(this)
                    .load(ImagePath.buildPath(photo.getImagePath())
                            .width(protocolImgSize)
                            .height(protocolImgSize)
                            .cropPath())
                    .into(holder.imgCover);
        } else {
            holder.btnDelete.setVisibility(View.GONE);
            holder.imgCover.setBackgroundResource(R.mipmap.icon_cross_add_white_176_176);
            holder.imgCover.setImageBitmap(null);
        }
    }

    @OnClick(R.id.btn_back)
    void onBack() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (!images.isEmpty()) {
            if (backDlg == null) {
                backDlg = DialogUtil.createDoubleButtonDialog(this,
                        "返回后图片将不会保存，确定返回？",
                        "我点错了",
                        "确定返回",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                backDlg.cancel();
                            }
                        },
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                backDlg.cancel();
                                UploadProtocolImageActivity.super.onBackPressed();
                            }
                        });
            }
            backDlg.show();
        } else {
            super.onBackPressed();
        }
    }

    @OnClick(R.id.btn_submit)
    void onSubmit() {
        if (progressDialog == null || !progressDialog.isShowing()) {
            progressDialog = DialogUtil.getRoundProgress(this);
            progressDialog.show();
        }
        if (uploadSubscriptions != null) {
            uploadSubscriptions.clear();
        }
        uploadSubscriptions = new SubscriptionList();
        new PhotoListUploadUtil(this,
                images,
                progressDialog,
                uploadSubscriptions,
                new OnFinishedListener() {
                    @Override
                    public void onFinished(Object... objects) {
                        onPostProtocolImages();
                    }
                }).startUpload();
    }

    void onPostProtocolImages() {
        postSub = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener() {
                    @Override
                    public void onNext(Object o) {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.onProgressFinish();
                            progressDialog.setCancelable(false);
                            progressDialog.onComplete(new HljRoundProgressDialog
                                    .OnCompleteListener() {
                                @Override
                                public void onCompleted() {
                                    // 上传完毕
                                    Intent intent = getIntent();
                                    setResult(RESULT_OK, intent);
                                    UploadProtocolImageActivity.super.onBackPressed();
                                }
                            });
                        }
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
        OrderApi.postProtocolPhotos(orderId, images)
                .subscribe(postSub);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.RequestCode.PROTOCOL_IMAGE_FOR_GALLERY:
                    if (data == null) {
                        return;
                    }
                    ArrayList<Photo> selectedPhotos = data.getParcelableArrayListExtra(
                            "selectedPhotos");
                    int index = protocolImagesLayout.indexOfChild(addView);
                    if (selectedPhotos != null && !selectedPhotos.isEmpty() && index >= 0) {
                        for (Photo image : selectedPhotos) {
                            images.add(image);
                            View view = View.inflate(this, R.layout.protocol_image_item2, null);
                            setImageValue(image, view);
                            protocolImagesLayout.addView(view,
                                    index++,
                                    new ViewGroup.LayoutParams(protocolImgViewSize,
                                            protocolImgViewSize));
                            if (images.size() == photosLimit) {
                                protocolImagesLayout.removeView(addView);
                                break;
                            }
                        }
                        btnSubmit.setEnabled(!images.isEmpty());
                    }
                    break;
                case Constants.RequestCode.PROTOCOL_IMAGE_FOR_CAMERA:
                    String path = ImageUtil.getImagePathForUri(currentUri, this);
                    index = protocolImagesLayout.indexOfChild(addView);
                    if (!TextUtils.isEmpty(path) && index >= 0) {
                        Size size = ImageUtil.getImageSizeFromPath(path);
                        Photo photo = new Photo();
                        photo.setImagePath(path);
                        photo.setWidth(size.getWidth());
                        photo.setHeight(size.getHeight());
                        images.add(photo);
                        View view = View.inflate(this, R.layout.protocol_image_item2, null);
                        setImageValue(photo, view);
                        protocolImagesLayout.addView(view,
                                index,
                                new ViewGroup.LayoutParams(protocolImgViewSize,
                                        protocolImgViewSize));
                        if (images.size() == photosLimit) {
                            protocolImagesLayout.removeView(addView);
                        }
                        btnSubmit.setEnabled(!images.isEmpty());
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void onTakePhotos() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = FileUtil.createImageFile();
        currentUri = Uri.fromFile(file);
        Uri imageUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            imageUri = FileProvider.getUriForFile(this, getPackageName(), file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        } else {
            imageUri = currentUri;
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, Constants.RequestCode.PROTOCOL_IMAGE_FOR_CAMERA);
        selectImageDialog.dismiss();
    }

    @OnShowRationale({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void onRationaleCamera(PermissionRequest request) {
        DialogUtil.showRationalePermissionsDialog(this,
                request,
                getString(R.string.permission_r_for_camera));
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        UploadProtocolImageActivityPermissionsDispatcher.onRequestPermissionsResult(this,
                requestCode,
                grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_cancel:
                selectImageDialog.dismiss();
                break;
            case R.id.action_gallery:
                Intent intent = new Intent(this, ImageChooserActivity.class);
                intent.putExtra("limit", photosLimit - images.size());
                startActivityForResult(intent, Constants.RequestCode.PROTOCOL_IMAGE_FOR_GALLERY);
                overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                selectImageDialog.dismiss();
                break;
            case R.id.action_camera_photo:
                UploadProtocolImageActivityPermissionsDispatcher.onTakePhotosWithCheck(this);
                break;
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(uploadSubscriptions, postSub);
    }

    static class ImageViewHolder {
        @BindView(R.id.img_cover)
        ImageView imgCover;
        @BindView(R.id.btn_delete)
        ImageButton btnDelete;

        ImageViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    private class OnDeleteClickListener implements View.OnClickListener {

        private View view;
        private Photo photo;

        private OnDeleteClickListener(View view, Photo photo) {
            this.view = view;
            this.photo = photo;
        }

        @Override
        public void onClick(View v) {
            protocolImagesLayout.removeView(view);
            if (images.size() == photosLimit) {
                protocolImagesLayout.addView(addView, protocolImgViewSize, protocolImgViewSize);
            }
            images.remove(photo);
            btnSubmit.setEnabled(!images.isEmpty());
        }
    }

    private class OnItemClickListener implements View.OnClickListener {

        private Photo photo;

        private OnItemClickListener(Photo photo) {
            this.photo = photo;
        }

        @Override
        public void onClick(View v) {
            if (photo != null) {
                Intent intent = new Intent(UploadProtocolImageActivity.this,
                        PicsPageViewActivity.class);
                intent.putExtra("photos", images);
                intent.putExtra("position", images.indexOf(photo));
                startActivity(intent);
            } else {
                if (selectImageDialog != null && selectImageDialog.isShowing()) {
                    return;
                }
                if (selectImageDialog == null) {
                    selectImageDialog = new Dialog(UploadProtocolImageActivity.this,
                            R.style.BubbleDialogTheme);
                    selectImageDialog.setContentView(R.layout.dialog_add_menu);
                    selectImageDialog.findViewById(R.id.action_cancel)
                            .setOnClickListener(UploadProtocolImageActivity.this);
                    selectImageDialog.findViewById(R.id.action_gallery)
                            .setOnClickListener(UploadProtocolImageActivity.this);
                    selectImageDialog.findViewById(R.id.action_camera_photo)
                            .setOnClickListener(UploadProtocolImageActivity.this);
                    selectImageDialog.findViewById(R.id.action_camera_video)
                            .setVisibility(View.GONE);
                    Point point = JSONUtil.getDeviceSize(UploadProtocolImageActivity.this);
                    Window win = selectImageDialog.getWindow();
                    ViewGroup.LayoutParams params = win.getAttributes();
                    params.width = point.x;
                    win.setGravity(Gravity.BOTTOM);
                    win.setWindowAnimations(R.style.dialog_anim_rise_style);
                }
                selectImageDialog.show();
            }

        }
    }

}
