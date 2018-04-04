package me.suncloud.marrymemo.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hunliji.hljhttplibrary.entities.HljApiException;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljsharelibrary.HljShare;
import com.hunliji.hljsharelibrary.activities.WeiboShareActivity;
import com.hunliji.hljsharelibrary.utils.WXCallbackUtil;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.ShareInfo;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class ShareUtil {
    private String webPath;
    private String title;
    private String newTitle;
    private String description;
    private String weiboDescription;
    private String imgUrl;
    private Context mContext;
    private Handler handler;
    private Dialog progressDialog;

    public ShareUtil(
            Context context,
            String webPath,
            String title,
            String description,
            String weiboDescription,
            String imgUrl,
            View progressbar,
            Handler handler) {
        this(context, webPath, title, description, weiboDescription, imgUrl, progressbar);
        this.handler = handler;
    }

    public ShareUtil(
            Context context,
            String webPath,
            String title,
            String description,
            String weiboDescription,
            String imgUrl,
            View progressbar) {
        this.mContext = context;
        this.webPath = webPath;
        this.title = title;
        this.description = description;
        if (!JSONUtil.isEmpty(weiboDescription)) {
            this.weiboDescription = weiboDescription;
        } else {
            this.weiboDescription = description;
        }
        this.imgUrl = imgUrl;
        //        this.progressbar = progressbar;
    }

    public ShareUtil(Context context, ShareInfo shareInfo, View progressbar, Handler handler) {
        this(context,
                shareInfo.getUrl(),
                shareInfo.getTitle(),
                shareInfo.getDesc(),
                shareInfo.getDesc2(),
                shareInfo.getIcon(),
                progressbar,
                handler);
    }

    public Handler getHandler() {
        return handler;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setNewTitle(String newTitle) {
        this.newTitle = newTitle;
    }

    public void shareToPengYou(String path) {
        if (TextUtils.isEmpty(path)) {
            sharePengYou(null);
            return;
        }
        if (path.startsWith("http://") || path.startsWith("https://")) {
            path = ImagePath.buildPath(path)
                    .width(150)
                    .ignoreFormat(true)
                    .cropPath();

        }
        loadBitmapObb(path).subscribe(HljHttpSubscriber.buildSubscriber(mContext)
                .setProgressDialog(DialogUtil.createProgressDialog(mContext))
                .setOnNextListener(new SubscriberOnNextListener<Bitmap>() {
                    @Override
                    public void onNext(Bitmap bitmap) {
                        sharePengYou(bitmap);
                    }
                })
                .build());
    }

    private void sharePengYou(Bitmap bitmap) {
        IWXAPI api = WXAPIFactory.createWXAPI(mContext, Constants.WEIXINKEY, true);
        api.registerApp(Constants.WEIXINKEY);
        if (api.getWXAppSupportAPI() >= 0x21020001) {
            WXWebpageObject web = new WXWebpageObject();
            web.webpageUrl = webPath;
            WXMediaMessage msg = new WXMediaMessage(web);
            if (JSONUtil.isEmpty(newTitle)) {
                msg.title = title;
            } else {
                msg.title = newTitle;
            }

            if (bitmap != null && !bitmap.isRecycled()) {
                msg.thumbData = getThumbDate(bitmap);
            }
            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = String.valueOf(System.currentTimeMillis());
            req.message = msg;
            req.scene = SendMessageToWX.Req.WXSceneTimeline;
            api.sendReq(req);
            WXCallbackUtil.getInstance()
                    .registerShareCallback(new WXShareListener(true));
        } else {
            if (api.getWXAppSupportAPI() > 0) {
                Toast.makeText(mContext, R.string.unfind_pengyou, Toast.LENGTH_LONG)
                        .show();
            } else {
                Toast.makeText(mContext, R.string.unfind_weixin, Toast.LENGTH_LONG)
                        .show();
            }
        }
    }

    private byte[] getThumbDate(Bitmap bitmap) {
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

    private Observable<Bitmap> loadBitmapObb(String path) {
        return Observable.just(path)
                .subscribeOn(Schedulers.io())
                .map(new Func1<String, Bitmap>() {
                    @Override
                    public Bitmap call(String s) {
                        try {
                            return Glide.with(mContext)
                                    .asBitmap()
                                    .load(s)
                                    .submit(150, 150)
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

    public void shareToWeixin(String path) {
        if (TextUtils.isEmpty(path)) {
            shareWeixin(null);
            return;
        }
        if (path.startsWith("http://") || path.startsWith("https://")) {
            path = ImagePath.buildPath(path)
                    .width(150)
                    .ignoreFormat(true)
                    .cropPath();

        }
        loadBitmapObb(path).subscribe(HljHttpSubscriber.buildSubscriber(mContext)
                .setProgressDialog(DialogUtil.createProgressDialog(mContext))
                .setOnNextListener(new SubscriberOnNextListener<Bitmap>() {
                    @Override
                    public void onNext(Bitmap bitmap) {
                        shareWeixin(bitmap);
                    }
                })
                .build());
    }


    private void shareWeixin(Bitmap bitmap) {
        IWXAPI api = WXAPIFactory.createWXAPI(mContext, Constants.WEIXINKEY, true);
        api.registerApp(Constants.WEIXINKEY);
        if (api.getWXAppSupportAPI() > 0) {
            WXWebpageObject web = new WXWebpageObject();
            web.webpageUrl = webPath;
            WXMediaMessage msg = new WXMediaMessage(web);
            msg.description = description;
            msg.title = title;
            if (bitmap != null && !bitmap.isRecycled()) {
                msg.thumbData = getThumbDate(bitmap);
            }
            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = String.valueOf(System.currentTimeMillis());
            req.message = msg;
            req.scene = SendMessageToWX.Req.WXSceneSession;
            api.sendReq(req);
            WXCallbackUtil.getInstance()
                    .registerShareCallback(new WXShareListener(false));
        } else {
            Toast.makeText(mContext, R.string.unfind_weixin, Toast.LENGTH_LONG)
                    .show();
        }

    }

    public void shareMore(String path) {
        try {
            Bitmap bitmap = null;
            if (!JSONUtil.isEmpty(path)) {
                bitmap = JSONUtil.getImageFromPath(mContext.getContentResolver(), path, 100);
            }
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_SUBJECT, title);
            intent.putExtra(Intent.EXTRA_TEXT,
                    description + " " + webPath + " " + mContext.getString(R.string
                            .msg_from_weibo_single));
            if (bitmap != null) {
                try {
                    FileOutputStream out;
                    out = mContext.openFileOutput("Img_share_image.jpg", Context.MODE_PRIVATE);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.close();
                    String filePath = mContext.getFilesDir()
                            .getAbsolutePath() + "/Img_share_image.jpg";
                    File f = new File(filePath);
                    f.setReadable(true, false);
                    Uri uri = Uri.fromFile(f);
                    intent.setType("image/jpeg");
                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                intent.setType("text/plain");
            }
            mContext.startActivity(Intent.createChooser(intent,
                    mContext.getString(R.string.label_share_more)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void downLoad(String path) {
        try {
            Bitmap bitmap = JSONUtil.getImageFromPath(mContext.getContentResolver(), path, 0);
            String string = MediaStore.Images.Media.insertImage(mContext.getContentResolver(),
                    bitmap,
                    null,
                    null);
            if (!JSONUtil.isEmpty(string)) {
                Uri uri = Uri.parse(string);
                path = JSONUtil.getImagePathForUri(uri, mContext);
                Toast.makeText(mContext,
                        mContext.getString(R.string.msg_saved_success2) + path,
                        Toast.LENGTH_SHORT)
                        .show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void shareToWeiXing() {
        shareToWeixin(imgUrl);
    }

    public void shareToPengYou() {
        shareToPengYou(imgUrl);
    }


    public void shareToMore() {
        if (JSONUtil.isEmpty(imgUrl)) {
            shareMore(imgUrl);
        } else {
            new ShareImageDownLoad().execute(imgUrl, 3);
        }
    }

    public void downLoad() {
        new ShareImageDownLoad().execute(imgUrl, 4);
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
        Tencent tencent = Tencent.createInstance(Constants.QQKEY, mContext);
        tencent.shareToQQ((Activity) mContext, params, new IUiListener() {

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(UiError e) {
            }

            @Override
            public void onComplete(Object response) {
                if (handler != null) {
                    Message msg = new Message();
                    msg.what = HljShare.RequestCode.SHARE_TO_QQ;
                    handler.sendMessage(msg);
                }
            }

        });

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
        if (!JSONUtil.isEmpty(imgUrl)) {
            imageUrls.add(imgUrl);
        }
        Tencent tencent = Tencent.createInstance(Constants.QQKEY, mContext);
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);
        tencent.shareToQzone((Activity) mContext, params, new IUiListener() {

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(UiError e) {
            }

            @Override
            public void onComplete(Object response) {
                if (handler != null) {
                    Message msg = new Message();
                    msg.what = HljShare.RequestCode.SHARE_TO_QQZONE;
                    handler.sendMessage(msg);
                }
            }

        });
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    private class ShareImageDownLoad extends AsyncTask<Object, Integer, String> {
        private int type;

        private ShareImageDownLoad() {
            if (progressDialog == null) {
                progressDialog = DialogUtil.createProgressDialog(mContext);
            }
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String result) {
            if (!JSONUtil.isEmpty(result)) {
                if (type == 2) {
                    shareToWeixin(result);
                } else if (type == 1) {
                    shareToPengYou(result);
                } else if (type == 3) {
                    shareMore(result);
                } else if (type == 4) {
                    downLoad(result);
                }
                new File(result).delete();
            }
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            super.onPostExecute(result);
        }

        @Override
        protected void onCancelled() {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            super.onCancelled();
        }

        @Override
        protected String doInBackground(Object... params) {
            try {
                String imgUrl = (String) params[0];
                if (!imgUrl.startsWith("http://") && !imgUrl.startsWith("https://")) {
                    imgUrl = "http://" + imgUrl;
                }
                if (params.length > 1) {
                    type = (Integer) params[1];
                }
                InputStream is = JSONUtil.getContentFromUrl(imgUrl);
                String path = Environment.getExternalStorageDirectory() + File.separator +
                        JSONUtil.removeFileSeparator(
                        "share_image");
                FileOutputStream out = new FileOutputStream(path);
                JSONUtil.saveStreamToFile(is, out);
                return path;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

    }

    private class WXShareListener implements WXCallbackUtil.WXOnCompleteCallbackListener {

        private boolean isTimeLine;

        private WXShareListener(boolean isTimeLine) {
            this.isTimeLine = isTimeLine;
        }

        @Override
        public void OnComplete(String code) {
            if (handler != null) {
                Message msg = new Message();
                if (isTimeLine) {
                    msg.what = HljShare.RequestCode.SHARE_TO_PENGYOU;
                } else {
                    msg.what = HljShare.RequestCode.SHARE_TO_WEIXIN;
                }
                handler.sendMessage(msg);
            }
        }

        @Override
        public void OnError() {

        }

        @Override
        public void OnCancel() {

        }
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
}
