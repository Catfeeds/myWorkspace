package com.hunliji.marrybiz.view.login;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.hunliji.hljcommonlibrary.interfaces.OnFinishedListener;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljRoundProgressDialog;
import com.hunliji.hljhttplibrary.utils.PhotoListUploadUtil;
import com.hunliji.marrybiz.model.Certify;
import com.hunliji.marrybiz.model.MerchantUser;
import com.hunliji.marrybiz.util.Session;

import java.util.ArrayList;

import rx.internal.util.SubscriptionList;

/**
 * Created by mo_yu on 2017/8/23.实名认证基类
 */

public abstract class BaseCertificationActivity extends HljBaseActivity{

    public MerchantUser merchantUser;
    public Certify certify;
    public Uri currentUri;
    public String certifyFrontPath;
    public String certifyBackPath;
    public String companyLicensePath;
    public int certifyWidth;
    public int certifyHeight;
    public ArrayList<Photo> photos;
    private HljRoundProgressDialog progressDialog;
    public SubscriptionList uploadSubscriptions;
    public static final String ARG_CERTIFY = "certify";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initValue();
    }

    protected void initValue() {
        certifyWidth = (CommonUtil.getDeviceSize(this).x - CommonUtil.dp2px(this, 44)) / 2;
        certifyHeight = Math.round(certifyWidth * 2.0f / 3.0f);
        photos = new ArrayList<>();
        certify = (Certify) getIntent().getSerializableExtra(ARG_CERTIFY);
        merchantUser = Session.getInstance()
                .getCurrentUser(this);
    }

    protected boolean isLocalPath(String path) {
        return !TextUtils.isEmpty(path) && !path.startsWith("http://") && !path.startsWith(
                "https://");
    }

    //图片上传
    protected void uploadImage() {
        photos.clear();
        if (isLocalPath(certifyFrontPath)) {
            Photo photo = new Photo();
            photo.setImagePath(certifyFrontPath);
            photo.setId(-1);
            photos.add(photo);
        }
        if (isLocalPath(certifyBackPath)) {
            Photo photo = new Photo();
            photo.setId(-2);
            photo.setImagePath(certifyBackPath);
            photos.add(photo);
        }
        if (isLocalPath(companyLicensePath)) {
            Photo photo = new Photo();
            photo.setId(-3);
            photo.setImagePath(companyLicensePath);
            photos.add(photo);
        }
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
                        if (!CommonUtil.isCollectionEmpty(photos)) {
                            for (Photo photo : photos) {
                                if (photo.getId() == -1) {
                                    certifyFrontPath = photo.getImagePath();
                                } else if (photo.getId() == -2) {
                                    certifyBackPath = photo.getImagePath();
                                } else if (photo.getId() == -3) {
                                    companyLicensePath = photo.getImagePath();
                                }
                            }
                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            submitCertifyInfo();
                        }
                    }
                }).startUpload();

    }

    protected abstract void submitCertifyInfo();

}
