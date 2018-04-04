package com.hunliji.marrybiz.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.hunliji.hljsharelibrary.activities.WeiboShareActivity;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.ShareInfo;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ShareUtil {
    private String url;
    private String title;
    private String desc;
    private String desc2;
    private String icon;
    private Context mContext;
    private Handler handler;
    private View progressbar;

    public ShareUtil(
            Context context,
            String url,
            String title,
            String desc,
            String desc2,
            String icon,
            View progressbar,
            Handler handler) {
        this.mContext = context;
        this.url = url;
        this.title = title;
        this.desc = desc;
        if (!JSONUtil.isEmpty(desc2)) {
            this.desc2 = desc2;
        } else {
            this.desc2 = desc;
        }
        this.icon = icon;
        this.progressbar = progressbar;
        this.handler = handler;
    }

    public ShareUtil(
            Context context, ShareInfo shareInfo, View progressbar, Handler handler) {
        this.mContext = context;
        this.url = shareInfo.getUrl();
        this.title = shareInfo.getTitle();
        this.desc = shareInfo.getDesc();
        this.desc2 = shareInfo.getDesc2();
        this.icon = shareInfo.getIcon();
        this.progressbar = progressbar;
        this.handler = handler;
    }

    public void sharePengYou(String path) {
        IWXAPI api = WXAPIFactory.createWXAPI(mContext, Constants.WEIXINKEY, true);
        api.registerApp(Constants.WEIXINKEY);
        if (api.getWXAppSupportAPI() >= 0x21020001) {
            WXWebpageObject web = new WXWebpageObject();
            web.webpageUrl = url;
            WXMediaMessage msg = new WXMediaMessage(web);
            msg.title = desc;
            if (!JSONUtil.isEmpty(path)) {
                try {
                    msg.setThumbImage(JSONUtil.getThumbImageForPath(mContext.getContentResolver(),
                            path,
                            150));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = String.valueOf(System.currentTimeMillis());
            req.message = msg;
            req.scene = SendMessageToWX.Req.WXSceneTimeline;
            api.sendReq(req);
            //            WXCallbackUtil.getInstance(mContext).registerShareCallback(new
            // WXShareListener(true));
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

    public void shareWeixin(String path) {
        IWXAPI api = WXAPIFactory.createWXAPI(mContext, Constants.WEIXINKEY, true);
        api.registerApp(Constants.WEIXINKEY);
        if (api.getWXAppSupportAPI() > 0) {
            WXWebpageObject web = new WXWebpageObject();
            web.webpageUrl = url;
            WXMediaMessage msg = new WXMediaMessage(web);
            msg.description = desc;
            msg.title = title;
            if (!JSONUtil.isEmpty(path)) {
                try {
                    msg.setThumbImage(JSONUtil.getThumbImageForPath(mContext.getContentResolver(),
                            path,
                            150));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = String.valueOf(System.currentTimeMillis());
            req.message = msg;
            req.scene = SendMessageToWX.Req.WXSceneSession;
            api.sendReq(req);
            //            WXCallbackUtil.getInstance(mContext).registerShareCallback(new
            // WXShareListener(false));
        } else {
            Toast.makeText(mContext, R.string.unfind_weixin, Toast.LENGTH_LONG)
                    .show();
        }

    }

    public void shareToWeiXing() {
        if (JSONUtil.isEmpty(icon)) {
            shareWeixin(icon);
        } else {
            new ShareImageDownLoad(progressbar).execute(icon, 2);
        }
    }

    public void shareToPengYou() {
        if (JSONUtil.isEmpty(icon)) {
            sharePengYou(icon);
        } else {
            new ShareImageDownLoad(progressbar).execute(icon, 1);
        }
    }

    public void shareToQQZone() {
        Bundle params = new Bundle();
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE,
                QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, title);
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, desc2);
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, url);
        ArrayList<String> imageUrls = new ArrayList<>();
        if (!JSONUtil.isEmpty(icon)) {
            imageUrls.add(icon);
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
                    //                    Message msg = new Message();
                    //                    msg.what = Constants.RequestCode.SHARE_TO_QQZONE;
                    //                    handler.sendMessage(msg);
                }
            }

        });
    }

    public void shareToQQ() {
        Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, url);
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, desc);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, icon);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, mContext.getString(R.string.app_name));
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, 1);
        Tencent tencent = Tencent.createInstance(Constants.QQKEY, mContext);
        if (tencent == null) {
            return;
        }
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
                    //                    Message msg = new Message();
                    //                    msg.what = Constants.RequestCode.SHARE_TO_QQ;
                    //                    handler.sendMessage(msg);
                }
            }

        });

    }

    public void shareToWeiBo() {
        Intent intent = new Intent(mContext, WeiboShareActivity.class);
        intent.putExtra(WeiboShareActivity.ASG_IMAGE, icon);
        intent.putExtra(WeiboShareActivity.ASG_TITLE, title);
        intent.putExtra(WeiboShareActivity.ASG_CONTENT, desc2);
        intent.putExtra(WeiboShareActivity.ASG_URL, url);
        mContext.startActivity(intent);
        if (mContext instanceof Activity) {
            ((Activity) mContext).overridePendingTransition(0, 0);
        }
    }

    private class ShareImageDownLoad extends AsyncTask<Object, Integer, String> {
        private int type;
        private View progressbar;

        private ShareImageDownLoad(final View progressbar) {
            this.progressbar = progressbar;
            if (progressbar != null) {
                progressbar.post(new Runnable() {
                    @Override
                    public void run() {
                        progressbar.setVisibility(View.VISIBLE);
                    }
                });
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (!JSONUtil.isEmpty(result)) {
                if (type == 2) {
                    shareWeixin(result);
                } else if (type == 1) {
                    sharePengYou(result);
                }
                new File(result).deleteOnExit();
            }
            if (this.progressbar != null) {
                progressbar.post(new Runnable() {
                    @Override
                    public void run() {
                        progressbar.setVisibility(View.GONE);
                    }
                });
            }
            super.onPostExecute(result);
        }

        @Override
        protected void onCancelled() {
            if (this.progressbar != null) {
                progressbar.post(new Runnable() {
                    @Override
                    public void run() {
                        progressbar.setVisibility(View.GONE);
                    }
                });
            }
            super.onCancelled();
        }

        @Override
        protected String doInBackground(Object... params) {
            try {
                String imgUrl = (String) params[0];
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

    //    private class WXShareListener implements WXCallbackUtil.WXOnCompleteCallbackListener {
    //
    //        private boolean isTimeLine;
    //
    //        private WXShareListener(boolean isTimeLine) {
    //            this.isTimeLine = isTimeLine;
    //        }
    //
    //        @Override
    //        public void OnComplete(String code) {
    //            if (handler != null) {
    //                Message msg = new Message();
    //                if (isTimeLine) {
    //                    msg.what = Constants.RequestCode.SHARE_TO_PENGYOU;
    //                } else {
    //                    msg.what = Constants.RequestCode.SHARE_TO_WEIXING;
    //                }
    //                handler.sendMessage(msg);
    //            }
    //        }
    //    }
}
