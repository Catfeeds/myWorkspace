package me.suncloud.marrymemo.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ProgressBar;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.widgets.HljHorizontalProgressDialog;
import com.hunliji.hljhttplibrary.entities.HljUploadListener;
import com.hunliji.hljhttplibrary.entities.HljUploadResult;
import com.hunliji.hljhttplibrary.utils.HljFileUploadBuilder;

import java.io.File;
import java.util.ArrayList;

import io.realm.RealmList;
import me.suncloud.marrymemo.model.realm.RealmJsonPic;
import rx.Subscriber;
import rx.Subscription;

/**
 * Created by mo_yu on 2017/5/4.婚纱照多组图片上传工具类
 */

public class WeddingPhotoListUploader {
    private Context context;
    private RealmList<RealmJsonPic> photos;
    private ArrayList<Subscription> uploadSubscriptions;
    private OnFinishedListener onFinishedListener;
    private HljHorizontalProgressDialog progressBar;

    public WeddingPhotoListUploader(
            Context context, ArrayList<Subscription> uploadSubscriptions) {
        this.context = context;
        this.uploadSubscriptions = uploadSubscriptions;
    }

    public void setProgressBar(HljHorizontalProgressDialog progressBar) {
        this.progressBar = progressBar;
    }

    public void setPhotos(RealmList<RealmJsonPic> photos) {
        this.photos = photos;
    }

    public void startUploadPhoto() {
        uploadPhoto(0);
    }

    public void uploadPhoto(final int index) {
        if (CommonUtil.isCollectionEmpty(photos)) {
            return;
        }
        if (index >= photos.size()) {
            if (onFinishedListener != null) {
                onFinishedListener.onFinish(photos, index);
            }
            if (progressBar != null) {
                progressBar.dismiss();
            }
            return;
        }
        final RealmJsonPic photo = photos.get(index);
        if (TextUtils.isEmpty(photo.getPath()) || photo.getPath()
                .startsWith("http://") || photo.getPath()
                .startsWith("https://")) {
            if (progressBar != null) {
                progressBar.setCurrentIndex(index + 1);
            }
            uploadPhoto(index + 1);
        } else {
            Subscription uploadSubscription = new HljFileUploadBuilder(new File(photo.getPath()))
                    .compress(context)
                    .progressListener(
                    new HljUploadListener() {
                        @Override
                        public void transferred(long transBytes) {
                            if (progressBar != null) {
                                progressBar.setTransBytes(transBytes);
                            }
                        }

                        @Override
                        public void setContentLength(long contentLength) {
                            if (progressBar != null) {
                                progressBar.setContentLength(contentLength);
                            }
                        }
                    })
                    .tokenPath(HljFileUploadBuilder.QINIU_IMAGE_TOKEN)
                    .build()
                    .subscribe(new Subscriber<HljUploadResult>() {

                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onNext(HljUploadResult hljUploadResult) {
                            Log.d("===hljUploadResult===",
                                    "===hljUploadResult===" + hljUploadResult.getUrl());
                            photo.setPath(hljUploadResult.getUrl());
                            photo.setWidth(hljUploadResult.getWidth());
                            photo.setHeight(hljUploadResult.getHeight());
                        }

                        @Override
                        public void onError(Throwable e) {
                            if (onFinishedListener != null) {
                                onFinishedListener.onFinish(photos, index);
                            }
                        }

                        @Override
                        public void onCompleted() {
                            if (progressBar != null) {
                                progressBar.setCurrentIndex(index + 1);
                            }
                            uploadPhoto(index + 1);
                        }

                    });
            uploadSubscriptions.add(uploadSubscription);
        }
    }

    public void setOnFinishedListener(OnFinishedListener onFinishedListener) {
        this.onFinishedListener = onFinishedListener;
    }

    public interface OnFinishedListener {
        void onFinish(RealmList<RealmJsonPic> photos, int currentIndex);
    }
}