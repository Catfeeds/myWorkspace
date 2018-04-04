package me.suncloud.marrymemo.view;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.makeramen.rounded.RoundedImageView;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.task.AsyncBitmapDrawable;
import me.suncloud.marrymemo.task.ImageLoadTask;
import me.suncloud.marrymemo.task.NewHttpPostTask;
import me.suncloud.marrymemo.task.OnHttpRequestListener;
import me.suncloud.marrymemo.task.QiNiuUploadTask;
import me.suncloud.marrymemo.task.UserTask;
import me.suncloud.marrymemo.util.DialogUtil;
import me.suncloud.marrymemo.util.FileUtil;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.ScaleMode;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.widget.RoundProgressDialog;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**
 * modified by mo_yu on 2016/5/10. 完善资料
 */
@Route(path = RouterPath.IntentPath.Customer.COMPLETE_PROFILE)
@RuntimePermissions
public class CompleteProfileActivity extends Activity {


    @BindView(R.id.img_avatar)
    RoundedImageView imgAvatar;
    @BindView(R.id.et_nick)
    EditText etNick;
    @BindView(R.id.complete_profile_save)
    Button completeProfileSave;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.root_view)
    RelativeLayout rootView;
    @BindView(R.id.cancel_view)
    ImageButton cancelView;
    private Dialog photoDlg;
    private Uri cropUri;
    private String cropPath;
    private User user;
    private RoundProgressDialog progressDialog;
    private Dialog dialog;
    private String nick;
    private boolean isKeyBoard;
    private boolean isNeed;
    private String cropAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);
        ButterKnife.bind(this);
        progressBar.setVisibility(View.GONE);
        User user = Session.getInstance()
                .getCurrentUser(this);
        new UserTask(this, new OnHttpRequestListener() {
            @Override
            public void onRequestCompleted(Object obj) {
                progressBar.setVisibility(View.GONE);
                setUserInfo();
            }

            @Override
            public void onRequestFailed(Object obj) {
                progressBar.setVisibility(View.GONE);
                setUserInfo();
            }
        }).execute();

        completeProfileSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveButtonClick();
            }
        });
        cropAvatar = user.getAvatar();
        nick = user.getNick();
        etNick.addTextChangedListener(textWatcher);
        isComplete();

        cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancelPressed();
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {

            // 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
            View v = getCurrentFocus();

            if (isShouldHideInput(v, ev)) {
                hideSoftInput(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
     *
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right && event.getY() > top && event.getY()
                    < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false;
    }

    /**
     * 多种隐藏软件盘方法的其中一种
     *
     * @param token
     */
    private void hideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context
                    .INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void isComplete() {
        if (user != null) {
            if (JSONUtil.isEmpty(user.getAvatar()) || user.getDefaultAvatar()
                    .equals(cropAvatar) || TextUtils.isEmpty(nick) || nick.startsWith("手机用户")) {
                completeProfileSave.setEnabled(false);
                isNeed = true;
            } else {
                completeProfileSave.setEnabled(true);
                isNeed = false;
            }
        }
    }

    private TextWatcher textWatcher = new TextWatcher() {

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable s) {
            nick = s.toString();
            isComplete();
        }
    };

    public void onCancelPressed() {
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        if (dialog == null) {
            dialog = new Dialog(this, R.style.BubbleDialogTheme);
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_confirm, null);
            Button tvConfirm = (Button) dialogView.findViewById(R.id.btn_confirm);
            Button tvCancel = (Button) dialogView.findViewById(R.id.btn_cancel);
            TextView tvMsg = (TextView) dialogView.findViewById(R.id.tv_alert_msg);
            tvMsg.setText(R.string.label_complete_profile_dialog_tip);
            tvConfirm.setText(R.string.action_complete_confirm);
            tvCancel.setText(R.string.action_complete_cancel);
            tvCancel.setTextColor(ContextCompat.getColor(this, R.color.colorGray));
            tvCancel.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    finish();
                    overridePendingTransition(0,0);
                }
            });
            tvConfirm.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.setContentView(dialogView);
            Window window = dialog.getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            Point point = JSONUtil.getDeviceSize(this);
            params.width = Math.round(point.x * 5 / 7);
            window.setAttributes(params);
        }
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        onCancelPressed();
    }

    public void onSaveButtonClick() {

        if (user != null && JSONUtil.isEmpty(user.getAvatar())) {
            if (JSONUtil.isEmpty(cropPath)) {
                Toast.makeText(this, getString(R.string.msg_empty_avatar), Toast.LENGTH_SHORT)
                        .show();
                return;
            }
        }

        if (progressDialog == null) {
            progressDialog = JSONUtil.getRoundProgress(this);
        }
        progressDialog.show();
        if (!JSONUtil.isEmpty(cropPath) && !cropPath.startsWith("http://")) {
            new QiNiuUploadTask(this, new OnHttpRequestListener() {
                @Override
                public void onRequestCompleted(Object obj) {
                    JSONObject json = (JSONObject) obj;
                    if (json != null) {
                        String key = JSONUtil.getString(json, "image_path");
                        String domain = JSONUtil.getString(json, "domain");
                        if (!JSONUtil.isEmpty(key) && !JSONUtil.isEmpty(domain)) {
                            cropPath = domain + key;
                            if (progressDialog != null && progressDialog.isShowing()) {
                                upLoadInfo();
                            }
                        }
                    }
                }

                @Override
                public void onRequestFailed(Object obj) {
                    progressDialog.dismiss();
                }
            }, progressDialog).execute(Constants.getAbsUrl(Constants.HttpPath.QINIU_IMAGE_URL),
                    new File(cropPath));

        } else {
            upLoadInfo();
        }
    }

    private void upLoadInfo() {
        Map<String, Object> data = new HashMap<>();
        data.put("act", "edit");
        data.put("nick",
                etNick.getText()
                        .toString()
                        .trim());
        if (!JSONUtil.isEmpty(cropPath)) {
            data.put("avatar", cropPath);
        }
        progressDialog.onLoadComplate();
        new NewHttpPostTask(this, new OnHttpRequestListener() {
            @Override
            public void onRequestCompleted(Object obj) {
                JSONObject resultObj = (JSONObject) obj;
                if (resultObj != null && resultObj.optJSONObject("status") != null && resultObj
                        .optJSONObject(
                        "status")
                        .optInt("RetCode", -1) == 0) {
                    Toast.makeText(CompleteProfileActivity.this,
                            getString(R.string.msg_success_to_complete_profile),
                            Toast.LENGTH_SHORT)
                            .show();

                    JSONObject dataObj = resultObj.optJSONObject("data");
                    Session.getInstance()
                            .editCurrentUser(CompleteProfileActivity.this, dataObj);

                    Intent intent = getIntent();
                    intent.putExtra("modified", true);
                    setResult(RESULT_OK, intent);
                    finish();
                    overridePendingTransition(0,0);
                } else {
                    Toast.makeText(CompleteProfileActivity.this,
                            getString(R.string.msg_fail_to_complete_profile),
                            Toast.LENGTH_SHORT)
                            .show();
                }

            }

            @Override
            public void onRequestFailed(Object obj) {
                progressDialog.dismiss();
                Toast.makeText(CompleteProfileActivity.this,
                        getString(R.string.msg_fail_to_complete_profile),
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }, progressDialog).execute(Constants.getAbsUrl(Constants.HttpPath.COMPLETE_USER_PROFILE),
                data);
    }

    private void setUserInfo() {
        user = Session.getInstance()
                .getCurrentUser(this);
        if (user != null) {
            String path = user.getAvatar();
            if (!JSONUtil.isEmpty(path)) {
                String defaultAvatar = user.getDefaultAvatar();
                if (!JSONUtil.isEmpty(path) && !path.equals(defaultAvatar)) {
                    Glide.with(this)
                            .load(ImagePath.buildPath(path)
                                    .width(imgAvatar.getLayoutParams().width)
                                    .height(imgAvatar.getLayoutParams().height)
                                    .cropPath())
                            .apply(new RequestOptions().error(R.drawable.icon_avatar_default_gray)
                                    .dontAnimate())
                            .into(imgAvatar);
                }
            }

            if (!TextUtils.isEmpty(user.getNick()) && !user.getNick()
                    .startsWith("手机用户")) {
                etNick.setText(user.getNick());
            }
        }
    }


    @OnClick(R.id.img_avatar)
    void showPopup() {
        if (photoDlg != null && photoDlg.isShowing()) {
            return;
        }
        photoDlg = new Dialog(this, R.style.BubbleDialogTheme);
        View view = View.inflate(this, R.layout.dialog_add_menu, null);
        Button galleryBtn = (Button) view.findViewById(R.id.action_gallery);
        Button videosBtn = (Button) view.findViewById(R.id.action_camera_video);
        Button cameraBtn = (Button) view.findViewById(R.id.action_camera_photo);
        view.findViewById(R.id.action_cancel)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        photoDlg.dismiss();
                    }
                });
        videosBtn.setVisibility(View.GONE);
        galleryBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                CompleteProfileActivityPermissionsDispatcher.onReadPhotosWithCheck(
                        CompleteProfileActivity.this);
            }
        });
        cameraBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                CompleteProfileActivityPermissionsDispatcher.onTakePhotosWithCheck(
                        CompleteProfileActivity.this);
            }
        });

        photoDlg.setContentView(view);
        Window win = photoDlg.getWindow();
        if (win != null) {
            ViewGroup.LayoutParams params = win.getAttributes();
            Point point = JSONUtil.getDeviceSize(this);
            params.width = point.x;
            win.setGravity(Gravity.BOTTOM);
            win.setWindowAnimations(R.style.dialog_anim_rise_style);
            photoDlg.show();
        }
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
        File f = FileUtil.createImageFile();
        cropUri = Uri.fromFile(f);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropUri);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, Source.CROP.ordinal());
    }

    //    private void uploadAvatarThenUploadInfo() {
    //        if (progressDialog == null) {
    //            progressDialog = JSONUtil.getRoundProgress(this);
    //        }
    //        progressDialog.show();
    //        if (!JSONUtil.isEmpty(cropPath) && !cropPath.startsWith("http://")) {
    //            new QiNiuUploadTask(this, new OnHttpRequestListener() {
    //                @Override
    //                public void onRequestCompleted(Object obj) {
    //                    JSONObject json = (JSONObject) obj;
    //                    if (json != null) {
    //                        String key = JSONUtil.getString(json, "image_path");
    //                        String domain = JSONUtil.getString(json, "domain");
    //                        if (!JSONUtil.isEmpty(key) && !JSONUtil.isEmpty(domain)) {
    //                            cropPath = domain + key;
    //                            if (progressDialog != null && progressDialog.isShowing()) {
    //                                avatar = cropPath;
    //                                isComplete();
    ////                                upLoadInfo("avatar", cropPath);
    //                            }
    //                        }
    //                    }
    //                }
    //
    //                @Override
    //                public void onRequestFailed(Object obj) {
    //                    progressDialog.dismiss();
    //                }
    //            }, progressDialog).execute(Constants.getAbsUrl(Constants.HttpPath
    // .QINIU_IMAGE_URL),
    //                    new File(cropPath));
    //
    //        } else {
    //            avatar = cropPath;
    //            isComplete();
    //            //            upLoadInfo("avatar", cropPath);
    //        }
    //    }
    //
    //    private void upLoadInfo(String key, String value) {
    //        if (progressDialog == null) {
    //            progressDialog = JSONUtil.getRoundProgress(this);
    //        }
    //        if (!progressDialog.isShowing()) {
    //            progressDialog.show();
    //        }
    //
    //        final Map<String, Object> data = new HashMap<>();
    //        data.put(key, value);
    //
    //
    //        progressDialog.onLoadComplate();
    //        new NewHttpPostTask(this, new OnHttpRequestListener() {
    //
    //            @Override
    //            public void onRequestFailed(Object obj) {
    //                progressDialog.dismiss();
    //                Toast.makeText(CompleteProfileActivity.this,
    //                        getString(R.string.msg_fail_to_complete_profile),
    //                        Toast.LENGTH_SHORT)
    //                        .show();
    //
    //            }
    //
    //            @Override
    //            public void onRequestCompleted(Object obj) {
    //                JSONObject resultObj = (JSONObject) obj;
    //                if (resultObj != null && resultObj.optJSONObject("status") != null &&
    // resultObj
    //                        .optJSONObject(
    //                        "status")
    //                        .optInt("RetCode", -1) == 0) {
    //                    Toast.makeText(CompleteProfileActivity.this,
    //                            getString(R.string.msg_success_to_complete_profile),
    //                            Toast.LENGTH_SHORT)
    //                            .show();
    //                    JSONObject dataObj = resultObj.optJSONObject("data");
    //                    Session.getInstance()
    //                            .editCurrentUser(CompleteProfileActivity.this, dataObj);
    //                    isComplete(Session.getInstance()
    //                            .getCurrentUser()
    //                            .isNeed());
    //                } else {
    //                    Toast.makeText(CompleteProfileActivity.this,
    //                            getString(R.string.msg_fail_to_complete_profile),
    //                            Toast.LENGTH_SHORT)
    //                            .show();
    //                }
    //
    //                if (progressDialog != null && progressDialog.isShowing()) {
    //                    progressDialog.onLoadComplate();
    //                    progressDialog.setCancelable(false);
    //                    progressDialog.onComplate();
    //                    progressDialog.setOnUpLoadComplate(new RoundProgressDialog
    // .OnUpLoadComplate() {
    //                        @Override
    //                        public void onUpLoadCompleted() {
    //                        }
    //                    });
    //                } else {
    //                }
    //            }
    //        }, progressDialog).execute(Constants.getAbsUrl(Constants.HttpPath
    // .COMPLETE_USER_PROFILE),
    //                data);
    //    }

    private enum Source {
        GALLERY, CAMERA, CROP
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                default:
                    if (requestCode == Source.CROP.ordinal()) {
                        if (cropUri == null) {
                            return;
                        }
                        // 开始上传头像和同步资料
                        String path = JSONUtil.getImagePathForUri(cropUri, this);
                        if (!JSONUtil.isEmpty(path)) {
                            cropPath = path;
                            cropAvatar = cropPath;
                            isComplete();
                        }
                        setAvatar(cropUri);
                    }
                    File file = null;
                    if (requestCode == Source.CAMERA.ordinal()) {
                        String path = Environment.getExternalStorageDirectory() + File.separator
                                + "temp" + ".jpg";
                        file = new File(path);
                    }
                    if (requestCode == Source.GALLERY.ordinal()) {
                        if (data == null) {
                            return;
                        }
                        file = new File(JSONUtil.getImagePathForUri(data.getData(), this));
                    }
                    if (file != null) {
                        showPhotoCrop(file);
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setAvatar(Uri uri) {
        String path = JSONUtil.getImagePathForUri(uri, this);
        if (!JSONUtil.isEmpty(path)) {
            cropPath = path;
            cropAvatar = path;
            ImageLoadTask task = new ImageLoadTask(imgAvatar, 0);
            imgAvatar.setTag(path);
            task.loadImage(cropPath,
                    imgAvatar.getWidth(),
                    ScaleMode.WIDTH,
                    new AsyncBitmapDrawable(getResources(),
                            R.drawable.icon_avatar_default_gray,
                            task));
        }
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    void onReadPhotos() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, Source.GALLERY.ordinal());
        photoDlg.dismiss();
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
        photoDlg.dismiss();
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
        CompleteProfileActivityPermissionsDispatcher.onRequestPermissionsResult(this,
                requestCode,
                grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
