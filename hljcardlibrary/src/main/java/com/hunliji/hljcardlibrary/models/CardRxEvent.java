package com.hunliji.hljcardlibrary.models;

/**
 * Created by wangtao on 2017/6/29.
 */

public class CardRxEvent {

    private RxEventType type;
    private Object object;

    public enum RxEventType {
        CREATE_CARD, //请帖创建 object Card
        CARD_INFO_EDIT, //请帖创建 object Card
        CARD_MUSIC_EDIT, //音乐编辑 object 音乐地址
        CARD_DELETE, //删除请帖 object Card
        CARD_CLOSE_CHANGE, //关闭请帖 object Card
        CARD_COPY, //复制请帖 object Card
        PAGE_EDIT, //请帖页编辑 object PageEditResult
        PAGE_VIDEO_EDIT, //请帖视频页编辑 object PageEditResult
        PAGE_IMAGE_EDIT, //请帖图片页编辑 object PageEditResult
        CARD_APP_SHARE_SUCCESS, //app分享成功
        CARD_THUMB_UPDATE, //请帖封面更新 object cardId
        CARD_OWNER_CHANGE //请帖已经被领取
    }

    public CardRxEvent(RxEventType type, Object object) {
        this.type = type;
        this.object = object;
    }

    public Object getObject() {
        return object;
    }

    public RxEventType getType() {
        return type;
    }
}
