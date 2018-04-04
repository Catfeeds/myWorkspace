package com.hunliji.hljsharelibrary.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hunliji.hljcommonlibrary.models.ShareInfo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljhttplibrary.entities.HljApiException;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljsharelibrary.HljShare;
import com.hunliji.hljsharelibrary.R;
import com.hunliji.hljsharelibrary.activities.WeiboShareActivity;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.Tencent;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


public class ShareUtil {
    private String webPath;
    private String title;
    private String description;
    private String weiboDescription;
    private String imgUrl;
    private String sms;
    private Context mContext;
    private Handler handler;

    public ShareUtil(Context context, String imgUrl) {
        this(context, null, null, null, null, imgUrl, null, null);
    }

    public ShareUtil(
            Context context,
            String webPath,
            String title,
            String description,
            String weiboDescription,
            String imgUrl) {
        this(context, webPath, title, description, weiboDescription, imgUrl, null, null);
    }

    public ShareUtil(
            Context context,
            String webPath,
            String title,
            String description,
            String weiboDescription,
            String imgUrl,
            String sms) {
        this(context, webPath, title, description, weiboDescription, imgUrl, sms, null);
    }

    public ShareUtil(
            Context context,
            String webPath,
            String title,
            String description,
            String weiboDescription,
            String imgUrl,
            Handler handler) {
        this(context, webPath, title, description, weiboDescription, imgUrl, null, handler);
    }

    public ShareUtil(Context context, ShareInfo shareInfo) {
        this(context,
                shareInfo.getUrl(),
                shareInfo.getTitle(),
                shareInfo.getDesc(),
                shareInfo.getDesc2(),
                shareInfo.getIcon(),
                shareInfo.getSms(),
                null);
    }

    public ShareUtil(Context context, ShareInfo shareInfo, Handler handler) {
        this(context,
                shareInfo.getUrl(),
                shareInfo.getTitle(),
                shareInfo.getDesc(),
                shareInfo.getDesc2(),
                shareInfo.getIcon(),
                shareInfo.getSms(),
                handler);
    }

    public ShareUtil(
            Context context,
            String webPath,
            String title,
            String description,
            String weiboDescription,
            String imgUrl,
            String sms,
            Handler handler) {
        this.mContext = context;
        this.webPath = webPath;
        this.title = title;
        this.description = description;
        if (!TextUtils.isEmpty(weiboDescription)) {
            this.weiboDescription = weiboDescription;
        } else {
            this.weiboDescription = description;
        }
        this.imgUrl = imgUrl;
        this.sms = sms;
        this.handler = handler;
    }

    public void sharePengYou(Bitmap bitmap) {
        IWXAPI api = WXAPIFactory.createWXAPI(mContext, HljShare.WEIXINKEY, true);
        api.registerApp(HljShare.WEIXINKEY);
        if (api.getWXAppSupportAPI() >= 0x21020001) {
            WXMediaMessage msg;
            if (bitmap != null) {
                WXWebpageObject web = new WXWebpageObject();
                web.webpageUrl = webPath;
                msg = new WXMediaMessage(web);
                msg.title = title;
                if (!bitmap.isRecycled()) {
                    msg.thumbData = getThumbDate(bitmap);
                }
            } else {
                WXTextObject text = new WXTextObject();
                text.text = description + webPath;
                msg = new WXMediaMessage(text);
                msg.description = description + webPath;

            }
            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = String.valueOf(System.currentTimeMillis());
            req.message = msg;
            req.scene = SendMessageToWX.Req.WXSceneTimeline;
            api.sendReq(req);
            WXCallbackUtil.getInstance()
                    .registerShareCallback(new WXShareListener(HljShare.RequestCode
                            .SHARE_TO_PENGYOU,
                            imgUrl,
                            handler));
        } else {
            if (api.getWXAppSupportAPI() > 0) {
                Toast.makeText(mContext, R.string.unfind_pengyou___share, Toast.LENGTH_LONG)
                        .show();
            } else {
                Toast.makeText(mContext, R.string.unfind_weixin___share, Toast.LENGTH_LONG)
                        .show();
            }
        }
    }

    public void shareWeiXin(Bitmap bitmap) {
        IWXAPI api = WXAPIFactory.createWXAPI(mContext, HljShare.WEIXINKEY, true);
        api.registerApp(HljShare.WEIXINKEY);
        if (api.getWXAppSupportAPI() > 0) {
            WXMediaMessage msg;
            if (bitmap != null) {
                WXWebpageObject web = new WXWebpageObject();
                web.webpageUrl = webPath;
                msg = new WXMediaMessage(web);
                msg.description = description;
                msg.title = title;
                if (!bitmap.isRecycled()) {
                    msg.thumbData = getThumbDate(bitmap);
                }
            } else {
                WXTextObject text = new WXTextObject();
                text.text = description + webPath;
                msg = new WXMediaMessage(text);
                msg.description = description + webPath;
            }
            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = String.valueOf(System.currentTimeMillis());
            req.message = msg;
            req.scene = SendMessageToWX.Req.WXSceneSession;
            api.sendReq(req);
            WXCallbackUtil.getInstance()
                    .registerShareCallback(new WXShareListener(HljShare.RequestCode.SHARE_TO_WEIXIN,
                            imgUrl,
                            handler));
        } else {
            Toast.makeText(mContext, R.string.unfind_weixin___share, Toast.LENGTH_LONG)
                    .show();
        }

    }

    public void shareToWeiXin() {
        if (TextUtils.isEmpty(imgUrl)) {
            shareWeiXin(null);
            return;
        }
        if (CommonUtil.isHttpUrl(imgUrl)) {
            imgUrl = ImagePath.buildPath(imgUrl)
                    .width(150)
                    .ignoreFormat(true)
                    .cropPath();

        }
        loadThumbBitmapObb(mContext, imgUrl, 150, 150).subscribe(HljHttpSubscriber.buildSubscriber(
                mContext)
                .setProgressDialog(DialogUtil.createProgressDialog(mContext))
                .setOnNextListener(new SubscriberOnNextListener<Bitmap>() {
                    @Override
                    public void onNext(Bitmap bitmap) {
                        shareWeiXin(bitmap);
                    }
                })
                .build());
    }

    public void shareToPengYou() {
        if (TextUtils.isEmpty(imgUrl)) {
            sharePengYou(null);
            return;
        }
        if (CommonUtil.isHttpUrl(imgUrl)) {
            imgUrl = ImagePath.buildPath(imgUrl)
                    .width(150)
                    .ignoreFormat(true)
                    .cropPath();

        }
        loadThumbBitmapObb(mContext, imgUrl, 150, 150).subscribe(HljHttpSubscriber.buildSubscriber(
                mContext)
                .setProgressDialog(DialogUtil.createProgressDialog(mContext))
                .setOnNextListener(new SubscriberOnNextListener<Bitmap>() {
                    @Override
                    public void onNext(Bitmap bitmap) {
                        sharePengYou(bitmap);
                    }
                })
                .build());
    }

    public void shareToQQ() {
        Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, webPath);
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, description);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imgUrl);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, mContext.getString(R.string.app_name));
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, 1);
        Tencent tencent = Tencent.createInstance(HljShare.QQKEY, mContext);
        tencent.shareToQQ((Activity) mContext,
                params,
                new TencentShareListener(HljShare.RequestCode.SHARE_TO_QQ, handler));

    }

    public void shareQQ() {
        Bundle params = new Bundle();
        params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, webPath);
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, description);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, imgUrl);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "婚礼纪");
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, 1);
        Tencent.createInstance(HljShare.QQKEY, mContext)
                .shareToQQ((Activity) mContext,
                        params,
                        new TencentShareListener(HljShare.RequestCode.SHARE_TO_QQ,
                                imgUrl,
                                handler));
    }

    public void shareToWeiBo() {
        Intent intent = new Intent(mContext, WeiboShareActivity.class);
        intent.putExtra(WeiboShareActivity.ASG_IMAGE, imgUrl);
        intent.putExtra(WeiboShareActivity.ASG_TITLE, title);
        intent.putExtra(WeiboShareActivity.ASG_CONTENT, weiboDescription);
        intent.putExtra(WeiboShareActivity.ASG_URL, webPath);
        if (mContext instanceof Activity) {
            ((Activity) mContext).startActivityForResult(intent,
                    HljShare.RequestCode.SHARE_TO_WEIBO);
            ((Activity) mContext).overridePendingTransition(0, 0);
        } else {
            mContext.startActivity(intent);
        }
    }

    public void shareToQQZone() {
        Bundle params = new Bundle();
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE,
                QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, title);
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, description);
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, webPath);
        ArrayList<String> imageUrls = new ArrayList<String>();
        if (!TextUtils.isEmpty(imgUrl)) {
            imageUrls.add(imgUrl);
        }
        Tencent tencent = Tencent.createInstance(HljShare.QQKEY, mContext);
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);
        tencent.shareToQzone((Activity) mContext,
                params,
                new TencentShareListener(HljShare.RequestCode.SHARE_TO_QQZONE, imgUrl, handler));
    }

    public void shareQQZone() {
        Bundle params = new Bundle();
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE,
                QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, title);
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, description);
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, webPath);
        ArrayList<String> imageUrls = new ArrayList<>();
        if (!TextUtils.isEmpty(imgUrl)) {
            imageUrls.add(imgUrl);
        }
        Tencent tencent = Tencent.createInstance(HljShare.QQKEY, mContext);
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, imageUrls);
        tencent.shareToQzone((Activity) mContext,
                params,
                new TencentShareListener(HljShare.RequestCode.SHARE_TO_QQZONE, imgUrl, handler));
    }

    public void shareToSms() {
        Uri smsToUri = Uri.parse("smsto:");
        Intent mIntent = new Intent(Intent.ACTION_SENDTO, smsToUri);
        mIntent.putExtra("sms_body", sms);
        mContext.startActivity(mIntent);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getWebPath() {
        return webPath;
    }


    static byte[] getThumbDate(Bitmap bitmap) {
        byte[] thumbData = null;
        try {
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bao);
            int options = 95;
            while (bao.toByteArray().length / 1024 > 31) {
                bao.reset();
                bitmap.compress(Bitmap.CompressFormat.JPEG, options, bao);
                options -= 5;
            }
            thumbData = bao.toByteArray();
            bao.close();
        } catch (Exception var3) {
            var3.printStackTrace();
        }
        return thumbData;
    }


    static Observable<Bitmap> loadThumbBitmapObb(
            final Context mContext, String path, final int width, final int height) {
        return Observable.just(path)
                .subscribeOn(Schedulers.io())
                .map(new Func1<String, Bitmap>() {
                    @Override
                    public Bitmap call(String s) {
                        try {
                            return Glide.with(mContext)
                                    .asBitmap()
                                    .load(s)
                                    .submit(width, height)
                                    .get();
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                        throw new HljApiException("图片分享失败");
                    }
                })
                .map(new Func1<Bitmap, Bitmap>() {
                    @Override
                    public Bitmap call(Bitmap bitmap) {
                        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(),
                                bitmap.getHeight(),
                                Bitmap.Config.ARGB_4444);
                        Canvas canvas = new Canvas(newBitmap);
                        canvas.drawColor(Color.WHITE);
                        canvas.drawBitmap(bitmap, 0, 0, null);
                        return newBitmap;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

}
