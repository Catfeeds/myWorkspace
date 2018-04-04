package me.suncloud.marrymemo.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.hunliji.hljsharelibrary.HljShare;
import com.hunliji.hljsharelibrary.activities.WeiboShareActivity;
import com.hunliji.hljsharelibrary.utils.WXCallbackUtil;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import java.util.ArrayList;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;

/**
 * Created by Suncloud on 2015/2/15.
 */
public class TextShareUtil {
    private String webPath;
    private String title;
    private String description;
    private String weiboDescription;
    private Context mContext;
    private Handler handler;

    public TextShareUtil(
            Context context,
            String webPath,
            String title,
            String description,
            String weiboDescription,
            Handler handler) {
        this(context, webPath, title, description, weiboDescription);
        this.handler = handler;
    }

    public TextShareUtil(
            Context context,
            String webPath,
            String title,
            String description,
            String weiboDescription) {
        this.mContext = context;
        this.webPath = webPath;
        this.title = title;
        this.description = description;
        this.weiboDescription = weiboDescription;
    }


    public void sharePengYou(String description) {
        IWXAPI api = WXAPIFactory.createWXAPI(mContext, Constants.WEIXINKEY, true);
        api.registerApp(Constants.WEIXINKEY);
        if (api.getWXAppSupportAPI() >= 0x21020001) {
            WXTextObject text = new WXTextObject();
            text.text = description;
            WXMediaMessage msg = new WXMediaMessage(text);
            msg.description = description;
            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = "text" + System.currentTimeMillis();
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

    public void shareWeixin(String description) {
        IWXAPI api = WXAPIFactory.createWXAPI(mContext, Constants.WEIXINKEY, true);
        api.registerApp(Constants.WEIXINKEY);
        if (api.getWXAppSupportAPI() > 0) {
            WXTextObject text = new WXTextObject();
            text.text = description;
            WXMediaMessage msg = new WXMediaMessage(text);
            msg.description = description;
            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = "text" + System.currentTimeMillis();
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

    public void shareToWeiXing() {
        shareWeixin(description + webPath);
    }

    public void shareToPengYou() {
        sharePengYou(description + webPath);
    }

    public void shareToQQ() {
        Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, webPath);
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, description);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, mContext.getString(R.string.app_name));
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,
                "http://qnm.hunliji.com/o_1aikfabnm16ru1sqj1acf1qprs7.png?imageView2/1/w/100/h" +
                        "/100");
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
        ArrayList<String> imageUrls = new ArrayList<>();
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
}
