package com.hunliji.hljsharelibrary.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.widget.ProgressBar;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.FileUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljhttplibrary.api.FileApi;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljsharelibrary.HljShare;
import com.hunliji.hljsharelibrary.R;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.share.WbShareCallback;
import com.sina.weibo.sdk.share.WbShareHandler;

import java.io.File;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 微博分享空界面接收分享回调
 * Created by wangtao on 2018/1/9.
 */

public class WeiboShareActivity extends Activity {

    public static final String ASG_TITLE = "title";
    public static final String ASG_CONTENT = "content";
    public static final String ASG_URL = "url";
    public static final String ASG_IMAGE = "image";


    private String title;
    private String content;
    private String url;
    private String imagePath;

    private WbShareHandler shareHandler;
    private Subscription downloadSubscription;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third_empty___share);
        ProgressBar progressBar = findViewById(R.id.progress_bar);
        initData();
        if (TextUtils.isEmpty(imagePath)) {
            onShare(null);
        } else if (!CommonUtil.isHttpUrl(imagePath)) {
            onShare(imagePath);
        } else {
            File imgFile = FileUtil.createImageFile(imagePath);
            if (imgFile.exists() && imgFile.isFile() && imgFile.length() > 0) {
                onShare(imgFile.getAbsolutePath());
            } else {
                downloadSubscription = FileApi.download(ImagePath.buildPath(imagePath)
                        .width(1080)
                        .height(1920)
                        .ignoreFormat(true)
                        .path(), imgFile.getAbsolutePath())
                        .map(new Func1<File, String>() {
                            @Override
                            public String call(File file) {
                                return file.getAbsolutePath();
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(HljHttpSubscriber.buildSubscriber(this)
                                .setProgressBar(progressBar)
                                .setOnNextListener(new SubscriberOnNextListener<String>() {
                                    @Override
                                    public void onNext(String s) {
                                        onShare(s);
                                    }
                                })
                                .setOnErrorListener(new SubscriberOnErrorListener() {
                                    @Override
                                    public void onError(Object o) {
                                        ToastUtil.showToast(WeiboShareActivity.this, "分享失败", 0);
                                        onBackPressed();
                                    }
                                })
                                .build());
            }
        }
    }

    private void initData() {
        title = getIntent().getStringExtra(ASG_TITLE);
        content = getIntent().getStringExtra(ASG_CONTENT);
        url = getIntent().getStringExtra(ASG_URL);
        imagePath = getIntent().getStringExtra(ASG_IMAGE);
        WbSdk.install(this,
                new AuthInfo(this, HljShare.WEIBOKEY, HljShare.WEIBO_CALLBACK, HljShare.SCOPE));
        shareHandler = new WbShareHandler(this);
        shareHandler.registerApp();
        shareHandler.setProgressColor(ContextCompat.getColor(this, R.color.colorPrimary));
    }


    private void onShare(String localPath) {
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        if (!TextUtils.isEmpty(content)) {
            TextObject textObject = new TextObject();
            textObject.text = content + " " + url;
            //            textObject.title = title;
            //            textObject.actionUrl = url;
            weiboMessage.textObject = textObject;
        }
        if (!TextUtils.isEmpty(localPath)) {
            ImageObject imageObject = new ImageObject();
            imageObject.imagePath = localPath;
            weiboMessage.imageObject = imageObject;
        }
        shareHandler.shareMessage(weiboMessage, false);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (shareHandler != null) {
            shareHandler.doResultIntent(intent, new WbShareCallback() {
                @Override
                public void onWbShareSuccess() {
                    ToastUtil.showToast(WeiboShareActivity.this, "分享成功", 0);
                    setResult(RESULT_OK);
                    onBackPressed();
                }

                @Override
                public void onWbShareCancel() {
                    ToastUtil.showToast(WeiboShareActivity.this, "取消分享", 0);
                    onBackPressed();
                }

                @Override
                public void onWbShareFail() {
                    ToastUtil.showToast(WeiboShareActivity.this, "分享失败", 0);
                    onBackPressed();
                }
            });
        } else {
            onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            CommonUtil.unSubscribeSubs(downloadSubscription);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CommonUtil.unSubscribeSubs(downloadSubscription);
    }


    @Override
    public void onBackPressed() {
        CommonUtil.unSubscribeSubs(downloadSubscription);
        finish();
        overridePendingTransition(0, 0);
    }

}
