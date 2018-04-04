package com.hunliji.hljhttplibrary.utils;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;

import com.hunliji.hljcommonlibrary.interfaces.OnFinishedListener;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.views.widgets.HljRoundProgressDialog;
import com.hunliji.hljhttplibrary.entities.HljUploadListener;
import com.hunliji.hljhttplibrary.entities.HljUploadResult;

import java.io.File;
import java.util.ArrayList;

import rx.Subscriber;
import rx.Subscription;
import rx.internal.util.SubscriptionList;

/**
 * Created by chen_bin on 17/7/06.
 * 批量上传图片列表的通用工具类
 */
public class PhotoListUploadUtil {
    private Context context;
    private String bastHost;
    private ArrayList<Photo> photos;
    private SubscriptionList uploadSubscriptions;
    private OnFinishedListener onFinishedListener;
    private HljRoundProgressDialog progressDialog;

    public PhotoListUploadUtil(
            Context context,
            ArrayList<Photo> photos,
            HljRoundProgressDialog progressDialog,
            SubscriptionList uploadSubscriptions,
            OnFinishedListener onFinishedListener) {
        this(context,null,photos,progressDialog,uploadSubscriptions,onFinishedListener);
    }
    public PhotoListUploadUtil(
            Context context,
            String host,
            ArrayList<Photo> photos,
            HljRoundProgressDialog progressDialog,
            SubscriptionList uploadSubscriptions,
            OnFinishedListener onFinishedListener) {
        this.context = context;
        this.bastHost=host;
        this.photos = photos;
        this.progressDialog = progressDialog;
        this.uploadSubscriptions = uploadSubscriptions;
        this.onFinishedListener = onFinishedListener;
    }

    public void startUpload() {
        uploadPhoto(0);
    }

    private void uploadPhoto(final int index) {
        if (progressDialog == null || !progressDialog.isShowing()) {
            return;
        }
        if (index >= photos.size()) {
            onFinishedListener.onFinished(photos);
            return;
        }
        final Photo photo = photos.get(index);
        if (TextUtils.isEmpty(photo.getImagePath()) || CommonUtil.isHttpUrl(photo.getImagePath())) {
            uploadPhoto(index + 1);
        } else {
            Subscription uploadSubscription = new HljFileUploadBuilder(new File(photo
                    .getImagePath())).progressListener(
                    new HljUploadListener() {
                        @Override
                        public void transferred(long transBytes) {
                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.setProgress(transBytes);
                            }
                        }

                        @Override
                        public void setContentLength(long contentLength) {
                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.setMax(contentLength);
                            }
                        }
                    })
                    .compress(context)
                    .host(bastHost)
                    .tokenPath(HljFileUploadBuilder.QINIU_IMAGE_TOKEN)
                    .build()
                    .subscribe(new Subscriber<HljUploadResult>() {

                        @Override
                        public void onStart() {
                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.setMessage((index + 1) + "/" + photos.size());
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            Dialog dialog = DialogUtil.createSingleButtonDialog(context,
                                    "网络有点不顺畅，再试试~",
                                    null,
                                    null);
                            dialog.setCancelable(false);
                            dialog.setCanceledOnTouchOutside(false);
                            dialog.show();
                        }

                        @Override
                        public void onNext(HljUploadResult hljUploadResult) {
                            photo.setImagePath(hljUploadResult.getUrl());
                            photo.setWidth(hljUploadResult.getWidth());
                            photo.setHeight(hljUploadResult.getHeight());
                        }

                        @Override
                        public void onCompleted() {
                            uploadPhoto(index + 1);
                        }

                    });
            uploadSubscriptions.add(uploadSubscription);
        }
    }
}