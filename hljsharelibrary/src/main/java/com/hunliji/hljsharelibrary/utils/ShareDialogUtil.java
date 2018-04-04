package com.hunliji.hljsharelibrary.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.View;

import com.hunliji.hljcommonlibrary.models.ShareInfo;
import com.hunliji.hljsharelibrary.adapters.viewholders.ShareActionViewHolder;
import com.hunliji.hljsharelibrary.models.ShareAction;
import com.hunliji.hljsharelibrary.widgets.ShareDialog;

import static com.hunliji.hljsharelibrary.models.ShareAction.NotePoster;
import static com.hunliji.hljsharelibrary.models.ShareAction.PengYouQuan;
import static com.hunliji.hljsharelibrary.models.ShareAction.QQ;
import static com.hunliji.hljsharelibrary.models.ShareAction.SMS;
import static com.hunliji.hljsharelibrary.models.ShareAction.WeddingTableQRCode;
import static com.hunliji.hljsharelibrary.models.ShareAction.WeiBo;
import static com.hunliji.hljsharelibrary.models.ShareAction.WeiXin;

/**
 * Created by Suncloud on 2016/8/20.
 */
public class ShareDialogUtil {

    public static void onCommonShare(Context context, ShareInfo shareInfo) {
        ShareUtil shareUtil = new ShareUtil(context, shareInfo, null);
        onCommonShare(context, shareUtil);
    }

    public static void onCommonShare(Context context, ShareInfo shareInfo, Handler handler) {
        ShareUtil shareUtil = new ShareUtil(context, shareInfo, handler);
        onCommonShare(context, shareUtil);
    }

    public static void onCommonShare(Context context, ShareUtil shareUtil) {
        onCommonShare(context, null, shareUtil);
    }


    public static void onCommonShare(Context context, String titleStr, final ShareUtil shareUtil) {
        ShareDialog dialog = new ShareDialog(context);
        dialog.setTitle(titleStr);
        dialog.setActions(PengYouQuan, WeiXin, QQ, WeiBo);
        dialog.setOnShareClickListener(new ShareActionViewHolder.OnShareClickListener() {
            @Override
            public void onShare(View v, ShareAction action) {
                switch (action) {
                    case PengYouQuan:
                        shareUtil.shareToPengYou();
                        break;
                    case WeiXin:
                        shareUtil.shareToWeiXin();
                        break;
                    case QQ:
                        shareUtil.shareToQQ();
                        break;
                    case WeiBo:
                        shareUtil.shareToWeiBo();
                        break;
                }
            }
        });
        dialog.show();
    }

    public static void onLocalImageShare(Context context, final Bitmap bitmap, final String path) {
        final ShareLocalImageUtil shareUtil = new ShareLocalImageUtil(context, path);
        ShareDialog dialog = new ShareDialog(context);
        dialog.setActions(PengYouQuan, WeiXin, QQ, WeiBo);
        dialog.setOnShareClickListener(new ShareActionViewHolder.OnShareClickListener() {
            @Override
            public void onShare(View v, ShareAction action) {
                switch (action) {
                    case PengYouQuan:
                        shareUtil.shareToPengYou(bitmap);
                        break;
                    case WeiXin:
                        shareUtil.shareToWeiXin(bitmap);
                        break;
                    case QQ:
                        shareUtil.shareToQQ();
                        break;
                    case WeiBo:
                        shareUtil.shareToWeiBo();
                        break;
                }
            }
        });
        dialog.show();
    }

    /**
     * 笔记详情 带生成海报弹窗分享
     *
     * @param context
     */
    public static void onCommonWithPoster(
            Context context,
            ShareInfo shareInfo,
            final View.OnClickListener onSharePosterListener) {
        final ShareUtil shareUtil = new ShareUtil(context, shareInfo, null);
        ShareDialog dialog = new ShareDialog(context);
        dialog.setActions(NotePoster, PengYouQuan, WeiXin, QQ, WeiBo);
        dialog.setOnShareClickListener(new ShareActionViewHolder.OnShareClickListener() {
            @Override
            public void onShare(View v, ShareAction action) {
                switch (action) {
                    case NotePoster:
                        if (onSharePosterListener != null) {
                            onSharePosterListener.onClick(v);
                        }
                        break;
                    case PengYouQuan:
                        shareUtil.shareToPengYou();
                        break;
                    case WeiXin:
                        shareUtil.shareToWeiXin();
                        break;
                    case QQ:
                        shareUtil.shareToQQ();
                        break;
                    case WeiBo:
                        shareUtil.shareToWeiBo();
                        break;
                }
            }
        });
        dialog.show();
    }

    /**
     * 宾客分享
     */
    public static Dialog onWeddingTableShare(
            Context context, String titleStr, ShareActionViewHolder.OnShareClickListener shareClickListener) {
        ShareDialog dialog = new ShareDialog(context);
        dialog.setTitle(titleStr);
        dialog.setActions(WeiXin, QQ, WeddingTableQRCode,SMS);
        dialog.setOnShareClickListener(shareClickListener);
        return dialog;
    }
}
