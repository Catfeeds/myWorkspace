package com.hunliji.hljsharelibrary.utils;

/**
 * Created by Administrator on 2017/12/25 0025.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljsharelibrary.HljShare;
import com.hunliji.hljsharelibrary.R;
import com.hunliji.hljsharelibrary.activities.WeiboShareActivity;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.Tencent;

import static com.hunliji.hljsharelibrary.utils.ShareUtil.getThumbDate;


public class ShareLocalImageUtil {
    private Context context;
    private Handler handler;
    private String imgUrl;

    public ShareLocalImageUtil(Context context, String imgUrl) {
        this(context, imgUrl, null);
    }

    public ShareLocalImageUtil(Context context, String imgUrl, Handler handler) {
        this.context = context;
        this.imgUrl = imgUrl;
        this.handler = handler;
    }

    public void shareToPengYou(Bitmap bitmap) {
        IWXAPI api = WXAPIFactory.createWXAPI(context, HljShare.WEIXINKEY, true);
        api.registerApp(HljShare.WEIXINKEY);
        if (api.getWXAppSupportAPI() >= 0x21020001) {
            WXMediaMessage msg = new WXMediaMessage();
            if (!bitmap.isRecycled()) {
                msg.thumbData = getThumbDate(bitmap);
            }
            msg.mediaObject = new WXImageObject(bitmap);
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
                Toast.makeText(context, R.string.unfind_pengyou___share, Toast.LENGTH_LONG)
                        .show();
            } else {
                Toast.makeText(context, R.string.unfind_weixin___share, Toast.LENGTH_LONG)
                        .show();
            }
        }
    }

    public void shareToWeiXin(Bitmap bitmap) {
        IWXAPI api = WXAPIFactory.createWXAPI(context, HljShare.WEIXINKEY, true);
        api.registerApp(HljShare.WEIXINKEY);
        if (api.getWXAppSupportAPI() > 0) {
            WXMediaMessage msg = new WXMediaMessage();
            if (!bitmap.isRecycled()) {
                msg.thumbData = getThumbDate(bitmap);
            }
            msg.mediaObject = new WXImageObject(bitmap);
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
            Toast.makeText(context, R.string.unfind_weixin___share, Toast.LENGTH_LONG)
                    .show();
        }

    }

    public void shareToQQ() {
        Tencent tencent = Tencent.createInstance(HljShare.QQKEY, context);
        if (!tencent.isSupportSSOLogin((Activity) context)) {
            ToastUtil.showToast(context, null, R.string.unfind_qq___share);
            return;
        }
        Bundle params = new Bundle();
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, imgUrl);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, context.getString(R.string.app_name));
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE);
        tencent.shareToQQ((Activity) context,
                params,
                new TencentShareListener(HljShare.RequestCode.SHARE_TO_QQ, imgUrl, handler));
    }

    public void shareToWeiBo() {
        Intent intent = new Intent(context, WeiboShareActivity.class);
        intent.putExtra(WeiboShareActivity.ASG_IMAGE, imgUrl);
        ((Activity) context).startActivityForResult(intent, HljShare.RequestCode.SHARE_TO_WEIBO);
    }

}
