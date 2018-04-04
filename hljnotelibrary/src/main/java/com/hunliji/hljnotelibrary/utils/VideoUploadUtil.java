package com.hunliji.hljnotelibrary.utils;

import android.app.Dialog;
import android.content.Context;

import com.hunliji.hljcommonlibrary.interfaces.OnFinishedListener;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.views.widgets.HljRoundProgressDialog;
import com.hunliji.hljhttplibrary.entities.HljUploadListener;
import com.hunliji.hljhttplibrary.entities.HljUploadResult;
import com.hunliji.hljhttplibrary.utils.HljFileUploadBuilder;
import com.hunliji.hljnotelibrary.R;

import java.io.File;

import rx.Subscriber;
import rx.Subscription;

/**
 * Created by chen_bin on 17/7/12.
 * 通用视频上传工具类
 */
public class VideoUploadUtil {
    private Context context;
    private String filePath;
    private String uploadFrom;
    private OnFinishedListener onFinishedListener;
    private HljRoundProgressDialog progressDialog;

    public VideoUploadUtil(
            Context context,
            String filePath,
            String uploadFrom,
            HljRoundProgressDialog progressDialog,
            OnFinishedListener onFinishedListener) {
        this.context = context;
        this.filePath = filePath;
        this.uploadFrom = uploadFrom;
        this.progressDialog = progressDialog;
        this.onFinishedListener = onFinishedListener;
    }

    public Subscription startUpload() {
        return uploadVideo();
    }

    private Subscription uploadVideo() {
        return new HljFileUploadBuilder(new File(filePath)).progressListener(new HljUploadListener() {
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
                .tokenPath(HljFileUploadBuilder.QINIU_VIDEO_URL, uploadFrom)
                .build()
                .subscribe(new Subscriber<HljUploadResult>() {

                    @Override
                    public void onStart() {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.setMessage("1/1");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                            Dialog dialog = DialogUtil.createSingleButtonDialog(context,
                                    "网络有点不顺畅，再试试~",
                                    null,
                                    null);
                            dialog.setCancelable(false);
                            dialog.setCanceledOnTouchOutside(false);
                            dialog.show();
                        }
                    }

                    @Override
                    public void onNext(HljUploadResult hljUploadResult) {
                        if (onFinishedListener != null) {
                            onFinishedListener.onFinished(hljUploadResult);
                        }
                    }

                    @Override
                    public void onCompleted() {

                    }
                });
    }
}