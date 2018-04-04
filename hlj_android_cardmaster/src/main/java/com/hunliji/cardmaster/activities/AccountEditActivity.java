package com.hunliji.cardmaster.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.cardmaster.R;
import com.hunliji.cardmaster.api.CommonApi;
import com.hunliji.hljcommonlibrary.interfaces.OnFinishedListener;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.models.customer.CustomerUser;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.FileUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljRoundProgressDialog;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.PhotoListUploadUtil;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.makeramen.rounded.RoundedImageView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import rx.internal.util.SubscriptionList;

/**
 * 个人信息
 * Created by jinxin on 2017/11/28 0028.
 */
@RuntimePermissions
public class AccountEditActivity extends HljBaseActivity {

    private final int EDIT_NICK_NAME = 101;
    private final String AVATAR = "avatar";

    @BindView(R.id.avatar_layout)
    LinearLayout avatarLayout;
    @BindView(R.id.tv_nick)
    TextView tvNick;
    @BindView(R.id.nick_layout)
    LinearLayout nickLayout;
    @BindView(R.id.user_avatar)
    RoundedImageView userAvatar;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private Dialog avatarDialog;
    private Uri cropUri;
    private String cropPath;
    private HljRoundProgressDialog roundProgressDialog;
    private SubscriptionList uploadSubscriptionList;
    private HljHttpSubscriber modifySub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_edit);
        ButterKnife.bind(this);

        initConstant();
        setUserInfo();
    }

    private void initConstant() {
        uploadSubscriptionList = new SubscriptionList();
    }

    private void setUserInfo() {
        CustomerUser user = (CustomerUser) UserSession.getInstance()
                .getUser(this);
        if (user == null) {
            return;
        }
        int avatarSize = CommonUtil.dp2px(this, 36);
        Glide.with(this)
                .load(ImagePath.buildPath(user.getAvatar())
                        .width(avatarSize)
                        .height(avatarSize)
                        .cropPath())
                .apply(new RequestOptions().placeholder(R.mipmap.icon_avatar_primary))
                .into(userAvatar);
        tvNick.setText(user.getNick());
    }

    @OnClick(R.id.avatar_layout)
    void onChangeAvatar() {
        showAvatarDialog();
    }

    @OnClick(R.id.nick_layout)
    void onEditNick() {
        Intent intent = new Intent(this, EditNickNameActivity.class);
        startActivityForResult(intent, EDIT_NICK_NAME);
    }

    private void showAvatarDialog() {
        if (avatarDialog == null) {
            avatarDialog = new Dialog(this, R.style.BubbleDialogTheme);
            avatarDialog.setContentView(R.layout.dialog_add_menu);
            avatarDialog.findViewById(R.id.action_camera_video)
                    .setVisibility(View.GONE);
            avatarDialog.findViewById(R.id.action_cancel)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            avatarDialog.dismiss();
                        }
                    });
            avatarDialog.findViewById(R.id.action_gallery)
                    .setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            AccountEditActivityPermissionsDispatcher.onReadPhotosWithCheck(
                                    AccountEditActivity.this);
                        }
                    });
            avatarDialog.findViewById(R.id.action_camera_photo)
                    .setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            AccountEditActivityPermissionsDispatcher.onTakePhotosWithCheck(
                                    AccountEditActivity.this);
                        }
                    });
            Window win = avatarDialog.getWindow();
            ViewGroup.LayoutParams params = win.getAttributes();
            Point point = CommonUtil.getDeviceSize(this);
            params.width = point.x;
            win.setGravity(Gravity.BOTTOM);
            win.setWindowAnimations(R.style.dialog_anim_rise_style);
        }
        avatarDialog.show();
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    void onReadPhotos() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, Source.GALLERY.ordinal());
        avatarDialog.dismiss();
    }

    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void onTakePhotos() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri imageUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            imageUri = FileProvider.getUriForFile(this,
                    getPackageName(),
                    new File(Environment.getExternalStorageDirectory(), "temp.jpg"));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        } else {
            imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                    "temp.jpg"));
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, Source.CAMERA.ordinal());
        avatarDialog.dismiss();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case EDIT_NICK_NAME:
                    if (data != null && data.getBooleanExtra("refresh", false)) {
                        Intent intent = getIntent();
                        intent.putExtra("refresh", true);
                        setResult(RESULT_OK, intent);
                        setUserInfo();
                    }
                    break;
                default:
                    if (requestCode == Source.CAMERA.ordinal() || requestCode == Source.CROP
                            .ordinal() || requestCode == Source.GALLERY.ordinal()) {
                        if (requestCode == Source.CROP.ordinal()) {
                            if (cropUri == null) {
                                return;
                            }
                            // 开始上传头像和同步资料
                            String path = CommonUtil.getImagePathForUri(cropUri, this);
                            if (!TextUtils.isEmpty(path)) {
                                cropPath = path;
                                uploadAvatarThenUploadInfo();
                            }
                            return;
                        }
                        File file = null;
                        if (requestCode == Source.CAMERA.ordinal()) {
                            String path = Environment.getExternalStorageDirectory() + File
                                    .separator + "temp.jpg";
                            file = new File(path);
                        }
                        if (requestCode == Source.GALLERY.ordinal()) {
                            if (data == null) {
                                return;
                            }
                            String path = CommonUtil.getImagePathForUri(data.getData(), this);
                            if (!TextUtils.isEmpty(path)) {
                                file = new File(path);
                            }
                        }
                        if (file != null) {
                            showPhotoCrop(file);
                        }
                    }
                    break;
            }
        }
    }

    private void uploadAvatarThenUploadInfo() {
        if (!TextUtils.isEmpty(cropPath) && !cropPath.startsWith("http://")) {
            uploadAvatar(cropPath);
        } else {
            upLoadInfo(AVATAR, cropPath);
        }
    }

    private void uploadAvatar(String cropPath) {
        if (TextUtils.isEmpty(cropPath)) {
            return;
        }
        if (roundProgressDialog == null) {
            roundProgressDialog = DialogUtil.getRoundProgress(this);
        }
        roundProgressDialog.show();

        ArrayList<Photo> photos = new ArrayList<>();
        final Photo photo = new Photo();
        photo.setImagePath(cropPath);
        photos.add(photo);
        if (uploadSubscriptionList != null) {
            uploadSubscriptionList.clear();
        }

        new PhotoListUploadUtil(this,
                photos,
                roundProgressDialog,
                uploadSubscriptionList,
                new OnFinishedListener() {
                    @Override
                    public void onFinished(Object... result) {
                        if (roundProgressDialog != null && roundProgressDialog.isShowing()) {
                            roundProgressDialog.dismiss();
                        }
                        if (result != null) {
                            ArrayList<Photo> pList = (ArrayList<Photo>) result[0];
                            if (pList != null && !pList.isEmpty()) {
                                upLoadInfo(AVATAR,
                                        pList.get(0)
                                                .getImagePath());
                            }
                        }
                    }
                }).startUpload();
    }

    private void upLoadInfo(String key, String value) {
        CommonUtil.unSubscribeSubs(modifySub);
        Map<String, Object> params = new HashMap<>();
        params.put(key, value);
        modifySub = HljHttpSubscriber.buildSubscriber(this)
                .setProgressBar(progressBar)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpResult<CustomerUser>>() {
                    @Override
                    public void onNext(HljHttpResult<CustomerUser> customerUserHljHttpResult) {
                        setHljHttpResult(customerUserHljHttpResult);
                    }
                })
                .build();
        CommonApi.modifyUserInformation(params)
                .subscribe(modifySub);
    }

    private void setHljHttpResult(HljHttpResult<CustomerUser> hljHttpResult) {
        if (hljHttpResult != null && hljHttpResult.getStatus()
                .getRetCode() == 0) {
            try {
                Toast.makeText(AccountEditActivity.this,
                        getString(R.string.msg_success_to_complete_profile),
                        Toast.LENGTH_SHORT)
                        .show();
                CustomerUser user = hljHttpResult.getData();
                UserSession.getInstance()
                        .setUser(this, user);
                Intent intent = getIntent();
                intent.putExtra("refresh", true);
                setResult(RESULT_OK, intent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(AccountEditActivity.this,
                    getString(R.string.msg_fail_to_complete_profile),
                    Toast.LENGTH_SHORT)
                    .show();
        }
        setUserInfo();
    }

    public void showPhotoCrop(File file) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(this, getPackageName(), file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(file);
        }
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("scale", true);
        //        cropUri = FileProvider.getUriForFile(this,
        //                getString(R.string.file_provider_authorities___cm),
        //                FileUtil.createCropImageFile());
        cropUri = Uri.fromFile(FileUtil.createCropImageFile());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropUri);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, Source.CROP.ordinal());
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(modifySub, uploadSubscriptionList);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        AccountEditActivityPermissionsDispatcher.onRequestPermissionsResult(this,
                requestCode,
                grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private enum Source {
        GALLERY, CAMERA, CROP
    }

}
