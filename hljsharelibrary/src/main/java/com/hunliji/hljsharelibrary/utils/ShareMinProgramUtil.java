package com.hunliji.hljsharelibrary.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;

import com.hunliji.hljcommonlibrary.models.MinProgramShareInfo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljsharelibrary.HljShare;
import com.hunliji.hljsharelibrary.R;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXMiniProgramObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;


/**
 * 小程序分享
 */
public class ShareMinProgramUtil {
    private Context mContext;
    private Handler handler;
    private MinProgramShareInfo shareInfo;

    public ShareMinProgramUtil(
            Context mContext, MinProgramShareInfo shareInfo, Handler handler) {
        this.mContext = mContext;
        this.handler = handler;
        this.shareInfo = shareInfo;
    }

    public void shareToWeiXin() {
        String imgUrl = shareInfo.getHdImagePath();
        if (TextUtils.isEmpty(imgUrl)) {
            shareWeiXin(null);
            return;
        }
        if (CommonUtil.isHttpUrl(imgUrl)) {
            imgUrl = ImagePath.buildPath(imgUrl)
                    .width(540)
                    .height(432)
                    .ignoreFormat(true)
                    .cropPath();

        }
        ShareUtil.loadThumbBitmapObb(mContext, imgUrl, 540, 432)
                .subscribe(HljHttpSubscriber.buildSubscriber(mContext)
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
        String imgUrl = shareInfo.getHdImagePath();
        if (TextUtils.isEmpty(imgUrl)) {
            sharePengYou(null);
            return;
        }
        if (CommonUtil.isHttpUrl(imgUrl)) {
            imgUrl = ImagePath.buildPath(imgUrl)
                    .width(540)
                    .height(432)
                    .ignoreFormat(true)
                    .cropPath();

        }
        ShareUtil.loadThumbBitmapObb(mContext, imgUrl, 540, 432)
                .subscribe(HljHttpSubscriber.buildSubscriber(mContext)
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
        IWXAPI api = WXAPIFactory.createWXAPI(mContext, HljShare.WEIXINKEY, true);
        api.registerApp(HljShare.WEIXINKEY);
        if (api.getWXAppSupportAPI() >= 0x21020001) {
            WXMiniProgramObject miniProgram = new WXMiniProgramObject();
            miniProgram.webpageUrl = shareInfo.getUrl();
            miniProgram.userName = shareInfo.getProgramUserName();
            miniProgram.path = shareInfo.getProgramPath();
            WXMediaMessage msg = new WXMediaMessage(miniProgram);
            msg.title = shareInfo.getTitle();
            msg.description = shareInfo.getDesc();
            if (bitmap != null && !bitmap.isRecycled()) {
                msg.thumbData = ShareUtil.getThumbDate(bitmap);
            }
            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = String.valueOf(System.currentTimeMillis());
            req.message = msg;
            req.scene = SendMessageToWX.Req.WXSceneTimeline;
            api.sendReq(req);
            WXCallbackUtil.getInstance()
                    .registerShareCallback(new WXShareListener(HljShare.RequestCode
                            .SHARE_TO_PENGYOU,
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

    private void shareWeiXin(Bitmap bitmap) {
        IWXAPI api = WXAPIFactory.createWXAPI(mContext, HljShare.WEIXINKEY, true);
        api.registerApp(HljShare.WEIXINKEY);
        if (api.getWXAppSupportAPI() > 0) {
            WXMiniProgramObject miniProgram = new WXMiniProgramObject();
            miniProgram.webpageUrl = shareInfo.getUrl();
            miniProgram.userName = shareInfo.getProgramUserName();
            miniProgram.path = shareInfo.getProgramPath();
            WXMediaMessage msg = new WXMediaMessage(miniProgram);
            msg.title = shareInfo.getTitle();
            msg.description = shareInfo.getDesc();
            if (bitmap != null && !bitmap.isRecycled()) {
                msg.thumbData = ShareUtil.getThumbDate(bitmap);
            }
            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = String.valueOf(System.currentTimeMillis());
            req.message = msg;
            req.scene = SendMessageToWX.Req.WXSceneSession;
            api.sendReq(req);
            WXCallbackUtil.getInstance()
                    .registerShareCallback(new WXShareListener(HljShare.RequestCode.SHARE_TO_WEIXIN,
                            handler));
        } else {
            Toast.makeText(mContext, R.string.unfind_weixin___share, Toast.LENGTH_LONG)
                    .show();
        }

    }
}
