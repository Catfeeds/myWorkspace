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
import com.google.gson.JsonElement;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.api.login.LoginApi;
import com.hunliji.marrybiz.model.Certify;
import com.hunliji.marrybiz.model.MerchantUser;
import com.hunliji.marrybiz.util.FileUtil;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.MerchantUserSyncUtil;
import com.hunliji.marrybiz.util.Session;
import com.hunliji.marrybiz.util.Util;
import com.hunliji.marrybiz.view.HomeActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**
 * Created by mo_yu on 2017/8/17.企业实名认证
 */
@RuntimePermissions
public class CompanyCertificationActivity extends BaseCertificationActivity implements TextWatcher {

    @BindView(R.id.tv_alert_msg)
    TextView tvAlertMsg;
    @BindView(R.id.alert_layout)
    LinearLayout alertLayout;
    @BindView(R.id.label_real_name)
    TextView labelRealName;
    @BindView(R.id.et_real_name)
    EditText etRealName;
    @BindView(R.id.label_certify)
    TextView labelCertify;
    @BindView(R.id.et_certify)
    EditText etCertify;
    @BindView(R.id.img_certify_front)
    ImageView imgCertifyFront;
    @BindView(R.id.certify_front_layout)
    RelativeLayout certifyFrontLayout;
    @BindView(R.id.img_certify_back)
    ImageView imgCertifyBack;
    @BindView(R.id.certify_back_layout)
    RelativeLayout certifyBackLayout;
    @BindView(R.id.img_company_license)
    ImageView imgCompanyLicense;
    @BindView(R.id.company_license_layout)
    RelativeLayout companyLicenseLayout;
    @BindView(R.id.btn_submit)
    Button btnSubmit;
    private Dialog dialog;
    private HljHttpSubscriber submitSubscriber;
    private HljHttpSubscriber initSubscriber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_certification);
        ButterKnife.bind(this);
        initView();
    }

    protected void initValue() {
        super.initValue();
        certifyWidth = CommonUtil.dp2px(this, 74);
        certifyHeight = CommonUtil.dp2px(this, 46);
        MerchantUser merchantUser = Session.getInstance()
                .getCurrentUser(this);
        if (merchantUser != null) {
            getCertifyInfo(merchantUser.getId());
        }
    }

    private void initView() {
        etRealName.addTextChangedListener(this);
        etCertify.addTextChangedListener(this);
    }

    private void initCertifyView() {
        if (certify != null) {
            etRealName.setText(certify.getRealname());
            etCertify.setText(certify.getCertify());
            certifyBackPath = certify.getCertifyBack();
            certifyFrontPath = certify.getCertifyFront();
            companyLicensePath = certify.getCompanyLicense();
            setThumbnail(certifyBackPath, imgCertifyBack);
            setThumbnail(certifyFrontPath, imgCertifyFront);
            setThumbnail(companyLicensePath, imgCompanyLicense);
            checkSubmitState();
        }
    }

    //获取实名认证信息
    private void getCertifyInfo(long merchantId) {
        if (initSubscriber == null || initSubscriber.isUnsubscribed()) {
            initSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<JsonElement>() {
                        @Override
                        public void onNext(JsonElement jsonElement) {
                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(jsonElement.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            certify = new Certify(jsonObject);
                            initCertifyView();
                        }
                    })
                    .setProgressDialog(DialogUtil.createProgressDialog(this))
                    .build();

            LoginApi.getCertifyInfoObb(merchantId)
                    .subscribe(initSubscriber);
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

    private void checkSubmitState() {
        if (etRealName.length() > 0 && etCertify.length() > 0 && !TextUtils.isEmpty
                (certifyFrontPath) && !TextUtils.isEmpty(
                certifyBackPath) && !TextUtils.isEmpty(companyLicensePath)) {
            btnSubmit.setEnabled(true);
        } else {
            btnSubmit.setEnabled(false);
        }
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
        if (isLocalPath(certifyFrontPath) || isLocalPath(certifyBackPath) || isLocalPath(
                companyLicensePath)) {
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
                                    .sync(CompanyCertificationActivity.this, null);
                            Dialog dialog = DialogUtil.createSingleButtonDialog(
                                    CompanyCertificationActivity
                                            .this,
                                    "提交成功",
                                    "审核预计需要1~2个工作日，审核结果将以短信形式通知到您，请注意查收。",
                                    "返回首页",
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent();
                                            intent.setClass(CompanyCertificationActivity.this,
                                                    HomeActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    CompanyCertificationActivity.super.onBackPressed();
                                }
                            });
                            dialog.show();
                        }
                    })
                    .setProgressDialog(DialogUtil.createProgressDialog(this))
                    .build();
            LoginApi.postCertifyInfoObb(etRealName.getText()
                            .toString(),
                    etCertify.getText()
                            .toString(),
                    Certify.COMPANY_TYPE,
                    certifyFrontPath,
                    certifyBackPath,
                    companyLicensePath)
                    .subscribe(submitSubscriber);
        }
    }

    @OnClick({R.id.certify_front_layout, R.id.certify_back_layout, R.id.company_license_layout})
    public void selectPhoto(View view) {
        int type = 0;
        int titleId = R.string.select_photo_title1;
        int defaultId = R.drawable.image_certify_front_default;
        if (view != null) {
            switch (view.getId()) {
                case R.id.certify_back_layout:
                    titleId = R.string.select_photo_title2;
                    type = 1;
                    defaultId = R.drawable.image_certify_back_default;
                    break;
                case R.id.company_license_layout:
                    titleId = R.string.select_photo_title3;
                    type = 2;
                    defaultId = R.drawable.image_company_license_default;
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
            int width = Math.round(CommonUtil.getDeviceSize(this).x - CommonUtil.dp2px(this, 88));
            imageView.getLayoutParams().height = Math.round(width * 46 / 74);
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
        ImageView imageView = (ImageView) dialog.findViewById(R.id.current_photo);
        imageView.setImageResource(defaultId);
        dialog.findViewById(R.id.action_gallery)
                .setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        CompanyCertificationActivityPermissionsDispatcher.onReadPhotosWithCheck(
                                CompanyCertificationActivity.this,
                                finalType);
                    }
                });
        dialog.findViewById(R.id.action_camera_photo)
                .setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        CompanyCertificationActivityPermissionsDispatcher.onTakePhotosWithCheck(
                                CompanyCertificationActivity.this,
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
                case Constants.RequestCode.COMPANY_LICENSE_FOR_CAMERA:
                    imageView = imgCompanyLicense;
                    path = companyLicensePath = JSONUtil.getImagePathForUri(currentUri, this);
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
                case Constants.RequestCode.COMPANY_LICENSE_FOR_GALLERY:
                    if (data == null) {
                        return;
                    }
                    path = companyLicensePath = JSONUtil.getImagePathForUri(data.getData(), this);
                    imageView = imgCompanyLicense;
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
        if (finalType == 2) {
            startActivityForResult(intent, Constants.RequestCode.COMPANY_LICENSE_FOR_GALLERY);
        } else if (finalType == 1) {
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
        if (finalType == 2) {
            startActivityForResult(intent, Constants.RequestCode.COMPANY_LICENSE_FOR_CAMERA);
        } else if (finalType == 1) {
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
        CompanyCertificationActivityPermissionsDispatcher.onRequestPermissionsResult(this,
                requestCode,
                grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(submitSubscriber, uploadSubscriptions,initSubscriber);
    }
}
