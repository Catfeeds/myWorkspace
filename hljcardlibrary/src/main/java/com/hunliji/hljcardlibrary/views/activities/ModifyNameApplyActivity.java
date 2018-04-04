package com.hunliji.hljcardlibrary.views.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hunliji.hljcardlibrary.R;
import com.hunliji.hljcardlibrary.R2;
import com.hunliji.hljcardlibrary.api.CardApi;
import com.hunliji.hljcardlibrary.models.ModifyData;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.interfaces.OnFinishedListener;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.FileUtil;
import com.hunliji.hljcommonlibrary.utils.OncePrefUtil;
import com.hunliji.hljcommonlibrary.utils.TextCountWatcher;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljRoundProgressDialog;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.PhotoListUploadUtil;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.models.Size;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.hunliji.hljimagelibrary.utils.OriginalImageScaleListener;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import rx.internal.util.SubscriptionList;

@RuntimePermissions
public class ModifyNameApplyActivity extends HljBaseActivity {

    @BindView(R2.id.et_groom)
    EditText etGroom;
    @BindView(R2.id.groom_layout)
    View groomLayout;
    @BindView(R2.id.et_bride)
    EditText etBride;
    @BindView(R2.id.bride_layout)
    View brideLayout;
    @BindView(R2.id.tv_type_label)
    TextView tvTypeLabel;
    @BindView(R2.id.tv_card_type)
    TextView tvCardType;
    @BindView(R2.id.card_type_layout)
    RelativeLayout cardTypeLayout;
    @BindView(R2.id.tv_label_groom)
    TextView tvLabelGroom;
    @BindView(R2.id.img_groom_card)
    ImageView imgGroomCard;
    @BindView(R2.id.tv_label_bride)
    TextView tvLabelBride;
    @BindView(R2.id.img_bride_card)
    ImageView imgBrideCard;
    @BindView(R2.id.card_images_layout)
    LinearLayout cardImagesLayout;
    @BindView(R2.id.info_layout)
    LinearLayout infoLayout;
    @BindView(R2.id.scroll_view)
    ScrollView scrollView;

    public static final int PHOTO_OF_BRIDE = 1;
    public static final int PHOTO_OF_GROOM = 2;
    private long cardId;

    private int carType;
    private int photoOf;
    private int imgHeight;
    private int imgWidth;
    private Dialog typeChooseDlg;
    private Dialog choosePhotoDlg;
    private String pathOfBride;
    private String pathOfGroom;
    private HljRoundProgressDialog progressDialog;
    private SubscriptionList uploadSubs;
    private ArrayList<Photo> photos;
    private HljHttpSubscriber postSub;
    private String takePhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_name_apply);
        ButterKnife.bind(this);

        initValues();
        initViews();
        initLoad();
    }

    private void initValues() {
        cardId = getIntent().getLongExtra("id", 0);
        imgWidth = CommonUtil.getDeviceSize(this).x - CommonUtil.dp2px(this, 32);
        imgHeight = imgWidth * 193 / 343;
    }

    private void initViews() {
        setOkText("提交");
        imgBrideCard.getLayoutParams().height = imgHeight;
        imgGroomCard.getLayoutParams().height = imgHeight;

        etGroom.addTextChangedListener(new TextCountWatcher(etGroom, 8));
        etBride.addTextChangedListener(new TextCountWatcher(etBride, 8));
    }

    private void initLoad() {

    }

    private void chooseCardTyp() {
        if (typeChooseDlg == null) {
            typeChooseDlg = DialogUtil.createBottomMenuDialog(this,
                    new LinkedHashMap<String, View.OnClickListener>() {{
                        put("身份证", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                tvCardType.setText("身份证");
                                carType = ModifyData.D_TYPE_ID_CARD;
                                initImagesLayoutView();
                                typeChooseDlg.cancel();
                            }
                        });
                        put("结婚证", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                tvCardType.setText("结婚证");
                                carType = ModifyData.D_TYPE_MARRY_CARD;
                                initImagesLayoutView();
                                typeChooseDlg.cancel();
                            }
                        });
                    }},
                    null);
        }
        typeChooseDlg.show();
    }

    private void initImagesLayoutView() {
        cardImagesLayout.setVisibility(View.VISIBLE);
        pathOfGroom = "";
        pathOfBride = "";
        if (carType == ModifyData.D_TYPE_ID_CARD) {
            imgGroomCard.setImageResource(R.mipmap.image_id_card_example);
            imgBrideCard.setImageResource(R.mipmap.image_id_card_example);
            imgBrideCard.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imgGroomCard.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            tvLabelBride.setText("新娘身份证正面");
            tvLabelGroom.setText("新郎身份证正面");
        } else {
            imgGroomCard.setImageResource(R.mipmap.image_marry_card_example);
            imgBrideCard.setImageResource(R.mipmap.image_marry_card_example);
            imgBrideCard.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imgGroomCard.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            tvLabelBride.setText("新娘结婚证");
            tvLabelGroom.setText("新郎结婚证");
        }
    }


    @OnClick(R2.id.card_type_layout)
    void onChooseCardType() {
        chooseCardTyp();
    }

    @OnClick(R2.id.img_bride_card)
    void onBridePhoto() {
        photoOf = PHOTO_OF_BRIDE;
        chooseOrTakePhoto();
    }

    @OnClick(R2.id.img_groom_card)
    void onGroomPhoto() {
        photoOf = PHOTO_OF_GROOM;
        chooseOrTakePhoto();
    }

    private void chooseOrTakePhoto() {
        if (choosePhotoDlg == null) {
            choosePhotoDlg = DialogUtil.createBottomMenuDialog(this,
                    new LinkedHashMap<String, View.OnClickListener>() {{
                        put("相册", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ModifyNameApplyActivityPermissionsDispatcher.onReadPhotosWithCheck(
                                        ModifyNameApplyActivity.this);
                                choosePhotoDlg.cancel();
                            }
                        });
                        put("拍照", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ModifyNameApplyActivityPermissionsDispatcher.onTakePhotosWithCheck(
                                        ModifyNameApplyActivity.this);
                                choosePhotoDlg.cancel();
                            }
                        });
                    }},
                    null);
        }
        choosePhotoDlg.show();
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    void onReadPhotos() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, Source.GALLERY.ordinal());
    }

    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void onTakePhotos() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File f = FileUtil.createImageFile();
        Uri imageUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            imageUri = FileProvider.getUriForFile(this, getPackageName(), f);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            imageUri = Uri.fromFile(f);
        }
        takePhotoPath = f.getAbsolutePath();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, Source.CAMERA.ordinal());
    }

    @OnShowRationale({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void onRationaleCamera(PermissionRequest request) {
        DialogUtil.showRationalePermissionsDialog(this,
                request,
                getString(R.string.permission_r_for_camera));
    }

    @OnShowRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
    void onRationaleReadExternal(PermissionRequest request) {
        DialogUtil.showRationalePermissionsDialog(this,
                request,
                getString(R.string.permission_r_for_read_external_storage));
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        ModifyNameApplyActivityPermissionsDispatcher.onRequestPermissionsResult(this,
                requestCode,
                grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("currentUrl", takePhotoPath);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        takePhotoPath = savedInstanceState.getString("currentUrl");
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == Source.CAMERA.ordinal()) {
                if (TextUtils.isEmpty(takePhotoPath)) {
                    return;
                }
                setFile(takePhotoPath);
            } else if (requestCode == Source.GALLERY.ordinal()) {
                Uri uri = getUri(data);
                if (uri != null) {
                    setFile(ImageUtil.getImagePathForUri(uri, ModifyNameApplyActivity.this));
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 解决小米手机上获取图片路径为null的情况
     *
     * @param intent
     * @return
     */
    public Uri getUri(android.content.Intent intent) {
        Uri uri = null;
        if (intent != null) {
            uri = intent.getData();
            if (uri.getPath()
                    .equals("/external/images/media")) {
                uri = null;
            }
        }

        return uri;
    }

    private void setFile(String path) {
        if (photoOf == PHOTO_OF_BRIDE) {
            pathOfBride = path;
            Glide.with(this)
                    .load(path)
                    .listener(new OriginalImageScaleListener(imgBrideCard, imgWidth, 0))
                    .into(imgBrideCard);
        } else {
            pathOfGroom = path;
            Glide.with(this)
                    .load(path)
                    .listener(new OriginalImageScaleListener(imgGroomCard, imgWidth, 0))
                    .into(imgGroomCard);
        }
    }

    @Override
    public void onOkButtonClick() {
        if (TextUtils.isEmpty(etBride.getText())) {
            Toast.makeText(this, R.string.hint_groom_name___card, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (TextUtils.isEmpty(etGroom.getText())) {
            Toast.makeText(this, R.string.hint_bride_name___card, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (carType == 0) {
            Toast.makeText(this, "请选择证件类型", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        if (etGroom.length() < 2 || etGroom.length() > 8 || etBride.length() < 2 || etBride
                .length() > 8) {
            ToastUtil.showToast(this, "请输入2~8字的姓名", 0);
            return;
        }
        if (TextUtils.isEmpty(pathOfBride) || TextUtils.isEmpty(pathOfGroom)) {
            String msg;
            if (TextUtils.isEmpty(pathOfGroom)) {
                msg = "请上传" + tvLabelGroom.getText();
            } else {
                msg = "请上传" + tvLabelBride.getText();
            }
            Toast.makeText(this, msg, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (progressDialog == null || !progressDialog.isShowing()) {
            progressDialog = DialogUtil.getRoundProgress(this);
            progressDialog.show();
        }
        if (uploadSubs != null) {
            uploadSubs.clear();
        }
        uploadSubs = new SubscriptionList();
        photos = new ArrayList<>();
        Size size = ImageUtil.getImageSizeFromPath(pathOfGroom);
        Photo photo = new Photo();
        photo.setImagePath(pathOfGroom);
        photo.setWidth(size.getWidth());
        photo.setHeight(size.getHeight());
        photos.add(photo);
        photo = new Photo();
        size = ImageUtil.getImageSizeFromPath(pathOfBride);
        photo.setImagePath(pathOfBride);
        photo.setWidth(size.getWidth());
        photo.setHeight(size.getHeight());
        photos.add(photo);

        new PhotoListUploadUtil(this, photos, progressDialog, uploadSubs, new OnFinishedListener() {
            @Override
            public void onFinished(Object... objects) {
                onPostModify();
            }
        }).startUpload();
    }

    private void onPostModify() {
        CommonUtil.unSubscribeSubs(postSub);
        postSub = HljHttpSubscriber.buildSubscriber(this)
                .setProgressDialog(DialogUtil.createProgressDialog(this))
                .setOnNextListener(new SubscriberOnNextListener() {
                    @Override
                    public void onNext(Object o) {
                        Toast.makeText(ModifyNameApplyActivity.this, "提交成功", Toast.LENGTH_SHORT)
                                .show();
                        OncePrefUtil.resetThisRecord(ModifyNameApplyActivity.this,
                                HljCommon.SharedPreferencesNames.SHOWED_CARD_RENAME_DENIED + "_"
                                        + cardId);
                        ModifyNameApplyActivity.this.onBackPressed();
                    }
                })
                .build();

        CardApi.postModifyName(cardId,
                etBride.getText()
                        .toString(),
                etGroom.getText()
                        .toString(),
                carType,
                photos.get(0)
                        .getImagePath(),
                photos.get(1)
                        .getImagePath())
                .subscribe(postSub);
    }

    private enum Source {
        GALLERY, CAMERA
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(postSub, uploadSubs);
    }
}
