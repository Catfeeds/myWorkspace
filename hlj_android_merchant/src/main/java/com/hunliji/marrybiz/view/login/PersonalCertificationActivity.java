package com.hunliji.marrybiz.view.login;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.IdcardUtils;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.api.login.LoginApi;
import com.hunliji.marrybiz.model.Certify;
import com.hunliji.marrybiz.util.FileUtil;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.MerchantUserSyncUtil;
import com.hunliji.marrybiz.util.Util;
import com.hunliji.marrybiz.view.HomeActivity;
import com.hunliji.marrybiz.view.shop.EditShopActivity;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**
 * Created by mo_yu on 2017/8/17.个人实名认证
 */
@RuntimePermissions
public class PersonalCertificationActivity extends BaseCertificationActivity implements
        TextWatcher {

    @BindView(R.id.label_real_name)
    TextView labelRealName;
    @BindView(R.id.et_real_name)
    EditText etRealName;
    @BindView(R.id.label_certify)
    TextView labelCertify;
    @BindView(R.id.et_certify)
    EditText etCertify;
    @BindView(R.id.img_upload_front)
    ImageView imgUploadFront;
    @BindView(R.id.tv_certify_front)
    TextView tvCertifyFront;
    @BindView(R.id.img_certify_front)
    ImageView imgCertifyFront;
    @BindView(R.id.certify_front_layout)
    RelativeLayout certifyFrontLayout;
    @BindView(R.id.img_upload_back)
    ImageView imgUploadBack;
    @BindView(R.id.tv_certify_back)
    TextView tvCertifyBack;
    @BindView(R.id.img_certify_back)
    ImageView imgCertifyBack;
    @BindView(R.id.certify_back_layout)
    RelativeLayout certifyBackLayout;
    @BindView(R.id.img_certification_correct)
    ImageView imgCertificationCorrect;
    @BindView(R.id.img_certification_error1)
    ImageView imgCertificationError1;
    @BindView(R.id.img_certification_error2)
    ImageView imgCertificationError2;
    @BindView(R.id.img_certification_error3)
    ImageView imgCertificationError3;
    @BindView(R.id.btn_submit)
    Button btnSubmit;
    @BindView(R.id.tv_alert_msg)
    TextView tvAlertMsg;
    @BindView(R.id.alert_layout)
    LinearLayout alertLayout;

    private int sampleHeight;

    private Dialog dialog;
    private HljHttpSubscriber submitSubscriber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_certification);
        ButterKnife.bind(this);
        initView();
    }

    protected void initValue() {
        super.initValue();
        sampleHeight = (CommonUtil.getDeviceSize(this).x - CommonUtil.dp2px(this, 52)) * 7 / 40;
    }

    private void initView() {
        etRealName.addTextChangedListener(this);
        etCertify.addTextChangedListener(this);
        certifyBackLayout.getLayoutParams().width = certifyWidth;
        certifyBackLayout.getLayoutParams().height = certifyHeight;
        certifyFrontLayout.getLayoutParams().width = certifyWidth;
        certifyFrontLayout.getLayoutParams().height = certifyHeight;
        imgCertificationCorrect.getLayoutParams().height = sampleHeight;
        imgCertificationError1.getLayoutParams().height = sampleHeight;
        imgCertificationError2.getLayoutParams().height = sampleHeight;
        imgCertificationError3.getLayoutParams().height = sampleHeight;
        imgCertifyBack.getLayoutParams().width = certifyWidth;
        imgCertifyBack.getLayoutParams().height = certifyHeight;
        imgCertifyFront.getLayoutParams().width = certifyWidth;
        imgCertifyFront.getLayoutParams().height = certifyHeight;
        if (certify != null) {
            initCertifyView();
        }
    }

    private void initCertifyView() {
        if (certify != null) {
            etRealName.setText(certify.getRealname());
            etCertify.setText(certify.getCertify());
            certifyBackPath = certify.getCertifyBack();
            certifyFrontPath = certify.getCertifyFront();
            setThumbnail(certifyBackPath, imgCertifyBack);
            setThumbnail(certifyFrontPath, imgCertifyFront);
            checkSubmitState();
        }
    }

    private void checkSubmitState() {
        if (etRealName.length() > 0 && etCertify.length() > 0 && !TextUtils.isEmpty
                (certifyFrontPath) && !TextUtils.isEmpty(
                certifyBackPath)) {
            btnSubmit.setEnabled(true);
        } else {
            btnSubmit.setEnabled(false);
        }
    }

    private void setThumbnail(String path, ImageView imageView) {
        if (TextUtils.isEmpty(path) || imageView == null) {
            return;
        }
        imageView.setVisibility(View.VISIBLE);
        if (isLocalPath(path)) {
            path = ImagePath.buildPath(path)
                    .width(certifyWidth)
                    .height(certifyHeight)
                    .cropPath();
        }
        Glide.with(this)
                .load(path)
                .into(imageView);
    }

    @OnClick(R.id.btn_submit)
    public void onSubmit() {
        if (!CommonUtil.validIdStr(etCertify.getText()
                .toString())) {
            Util.showToast(this, null, R.string.msg_certify_err);
            etCertify.setSelection(etCertify.length());
            etCertify.requestFocus();
            return;
        }
        if (isLocalPath(certifyFrontPath) || isLocalPath(certifyBackPath)) {
            uploadImage();
            return;
        }
        submitCertifyInfo();
    }

    @Override
    protected void submitCertifyInfo() {
        if (submitSubscriber == null || submitSubscriber.isUnsubscribed()) {
            submitSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener() {
                        @Override
                        public void onNext(Object o) {
                            RxBus.getDefault()
                                    .post(new RxEvent(RxEvent.RxEventType.CERTIFY_SUCCESS, null));
                            MerchantUserSyncUtil.getInstance()
                                    .sync(PersonalCertificationActivity.this, null);
                            if (merchantUser.getExamine() == -1) {
                                Dialog dialog = DialogUtil.createDoubleButtonDialog(
                                        PersonalCertificationActivity
                                                .this,
                                        "提交成功",
                                        "检测到您尚未提交店铺信息\n是否立即提交",
                                        "立即提交",
                                        "稍后提交",
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent = new Intent();
                                                intent.setClass(PersonalCertificationActivity.this,
                                                        EditShopActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        },
                                        null);
                                dialog.setOnDismissListener(new DialogInterface.OnDismissListener
                                        () {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {
                                        PersonalCertificationActivity.super.onBackPressed();
                                    }
                                });
                                dialog.show();
                            } else {
                                Dialog dialog = DialogUtil.createSingleButtonDialog(
                                        PersonalCertificationActivity
                                                .this,
                                        "提交成功",
                                        "审核预计需要1~2个工作日，审核结果将以短信形式通知到您，请注意查收。",
                                        "返回首页",
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent = new Intent();
                                                intent.setClass(PersonalCertificationActivity.this,
                                                        HomeActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        });
                                dialog.setOnDismissListener(new DialogInterface.OnDismissListener
                                        () {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {
                                        PersonalCertificationActivity.super.onBackPressed();
                                    }
                                });
                                dialog.show();
                            }
                        }
                    })
                    .setProgressDialog(DialogUtil.createProgressDialog(this))
                    .build();
            LoginApi.postCertifyInfoObb(etRealName.getText()
                            .toString(),
                    etCertify.getText()
                            .toString(),
                    Certify.PERSONAL_TYPE,
                    certifyFrontPath,
                    certifyBackPath)
                    .subscribe(submitSubscriber);
        }
    }

    @OnClick({R.id.certify_front_layout, R.id.certify_back_layout})
    public void selectPhoto(View view) {
        int type = 0;
        int titleId = R.string.select_photo_title1;
        if (view != null) {
            switch (view.getId()) {
                case R.id.certify_back_layout:
                    titleId = R.string.select_photo_title2;
                    type = 1;
                    break;
                default:
                    break;
            }
        }
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        if (dialog == null) {
            dialog = new Dialog(this, R.style.BubbleDialogTheme);
            dialog.setContentView(R.layout.dialog_select_image);
            dialog.findViewById(R.id.action_cancel)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
            ImageView imageView = (ImageView) dialog.findViewById(R.id.current_photo);
            View imageLine =   dialog.findViewById(R.id.current_photo_line);
            imageView.setVisibility(View.GONE);
            imageLine.setVisibility(View.GONE);
            Window win = dialog.getWindow();
            assert win != null;
            ViewGroup.LayoutParams params = win.getAttributes();
            params.width = CommonUtil.getDeviceSize(this).x;
            win.setGravity(Gravity.BOTTOM);
            win.setWindowAnimations(R.style.dialog_anim_rise_style);
        }
        final int finalType = type;
        TextView title = (TextView) dialog.findViewById(R.id.title);
        title.setText(titleId);
        dialog.findViewById(R.id.action_gallery)
                .setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        PersonalCertificationActivityPermissionsDispatcher.onReadPhotosWithCheck(
                                PersonalCertificationActivity.this,
                                finalType);
                    }
                });
        dialog.findViewById(R.id.action_camera_photo)
                .setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        PersonalCertificationActivityPermissionsDispatcher.onTakePhotosWithCheck(
                                PersonalCertificationActivity.this,
                                finalType);
                    }
                });
        dialog.show();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        checkSubmitState();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            ImageView imageView = null;
            String path = null;
            switch (requestCode) {
                case Constants.RequestCode.CERTIFY_BACK_FOR_CAMERA:
                    path = certifyBackPath = JSONUtil.getImagePathForUri(currentUri, this);
                    imageView = imgCertifyBack;
                    break;
                case Constants.RequestCode.CERTIFY_FRONT_FOR_CAMERA:
                    path = certifyFrontPath = JSONUtil.getImagePathForUri(currentUri, this);
                    imageView = imgCertifyFront;
                    break;
                case Constants.RequestCode.CERTIFY_BACK_FOR_GALLERY:
                    if (data == null) {
                        return;
                    }
                    path = certifyBackPath = JSONUtil.getImagePathForUri(data.getData(), this);
                    imageView = imgCertifyBack;
                    break;
                case Constants.RequestCode.CERTIFY_FRONT_FOR_GALLERY:
                    if (data == null) {
                        return;
                    }
                    path = certifyFrontPath = JSONUtil.getImagePathForUri(data.getData(), this);
                    imageView = imgCertifyFront;
                    break;
            }
            setThumbnail(path, imageView);
            checkSubmitState();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    void onReadPhotos(int finalType) {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (finalType == 1) {
            startActivityForResult(intent, Constants.RequestCode.CERTIFY_BACK_FOR_GALLERY);
        } else {
            startActivityForResult(intent, Constants.RequestCode.CERTIFY_FRONT_FOR_GALLERY);
        }
        dialog.dismiss();
    }


    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void onTakePhotos(int finalType) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = FileUtil.createImageFile();
        currentUri = Uri.fromFile(file);
        Uri imageUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            imageUri = FileProvider.getUriForFile(this, this.getPackageName(), file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        } else {
            imageUri = currentUri;
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        if (finalType == 1) {
            startActivityForResult(intent, Constants.RequestCode.CERTIFY_BACK_FOR_CAMERA);
        } else {
            startActivityForResult(intent, Constants.RequestCode.CERTIFY_FRONT_FOR_CAMERA);
        }
        dialog.dismiss();
    }

    @OnShowRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
    void onRationaleReadExternal(PermissionRequest request) {
        DialogUtil.showRationalePermissionsDialog(this,
                request,
                getString(R.string.permission_r_for_read_external_storage));
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
        PersonalCertificationActivityPermissionsDispatcher.onRequestPermissionsResult(this,
                requestCode,
                grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(submitSubscriber, uploadSubscriptions);
    }
}
