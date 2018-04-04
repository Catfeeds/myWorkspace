package com.hunliji.hljsharelibrary.models;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.transition.TransitionManager;

import com.hunliji.hljsharelibrary.R;

/**
 * Created by wangtao on 2018/3/20.
 */

public enum ShareAction {

    //朋友圈
    PengYouQuan(R.mipmap.icon_share_pengyou___cm,
            R.string.label_pengyou___share,
            "share_timeline_button"),
    //微信
    WeiXin(R.mipmap.icon_share_wechat___cm, R.string.label_weixin___share, "share_session_button"),
    QQ(R.mipmap.icon_share_qq___cm, R.string.label_qq___share, "share_qq_button"),
    //微博
    WeiBo(R.mipmap.icon_share_weibo___cm, R.string.label_weibo___share, "share_weibo_button"),
    //生成海报
    NotePoster(R.mipmap.icon_share_poster___cm,
            R.string.label_note_poster___share,
            "share_note_poster_button"),
    //宾客桌位二维码
    WeddingTableQRCode(R.mipmap.icon_share_qr_code___cm,
            R.string.label_qr_code___share,null),
    //短信
    SMS(R.mipmap.icon_share_sms___cm,
            R.string.label_sms___share,null);

    private int iconResId;
    private int nameResId;
    private String trackTagName;

    ShareAction(@DrawableRes int iconResId, @StringRes int nameResId, String trackTagName) {
        this.iconResId = iconResId;
        this.nameResId = nameResId;
        this.trackTagName = trackTagName;
    }

    public int getIconResId() {
        return iconResId;
    }

    public int getNameResId() {
        return nameResId;
    }

    public String getTrackTagName() {
        return trackTagName;
    }
}
