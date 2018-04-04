package me.suncloud.marrymemo.model;

import android.text.TextUtils;

import com.hunliji.hljcommonlibrary.models.chat_ext_object.WSHints;
import com.hunliji.hljcommonlibrary.models.realm.WSChannel;
import com.hunliji.hljcommonlibrary.models.realm.WSChat;
import com.hunliji.hljcommonlibrary.models.realm.WSChatAuthor;
import com.hunliji.hljhttplibrary.utils.GsonUtil;

/**
 * Created by Suncloud on 2016/10/19.
 * 临时处理私信对象，后面重构
 */

public class WSLastMessage extends LastMessage {

    private String channel;
    private WSChannel wsChannel;
    private WSChatAuthor session;
    private WSChat chat;

    public WSLastMessage(WSChannel wsChannel) {
        this.wsChannel = wsChannel;
        time = wsChannel.getTime();
        unreadSessionCount = wsChannel.getUnreadMessageCount() + wsChannel.getUnreadTrackCount();
        if (wsChannel.getDraft() != null) {
            setDraft(wsChannel.getDraft());
        }
    }

    @Override
    public String getSessionNick() {
        initSession();
        return super.getSessionNick();
    }

    @Override
    public String getSessionAvatar() {
        initSession();
        return super.getSessionAvatar();
    }

    @Override
    public long getSessionId() {
        initSession();
        return super.getSessionId();
    }

    @Override
    public String getContent() {
        initChat();
        return super.getContent();
    }

    public String getChannel() {
        initChat();
        return channel;
    }

    private void initSession() {
        if (session == null) {
            session = wsChannel.getChatUser();
            sessionNick = session.getNick();
            sessionAvatar = session.getAvatar();
            sessionId = session.getId();
        }
    }

    private void initChat() {
        if (chat == null) {
            try {
                chat = wsChannel.getLastMessage();
                channel = chat.getChannel();
                if (!TextUtils.isEmpty(chat.getKind())) {
                    switch (chat.getKind()) {
                        case WSChat.PRODUCT:
                        case WSChat.WORK_OR_CASE:
                        case WSChat.CUSTOM_MEAL:
                            if (chat.getProduct() != null) {
                                content = chat.getProduct()
                                        .getTitle();
                            }
                            break;
                        case WSChat.IMAGE:
                            content = "[图片]";
                            break;
                        case WSChat.VOICE:
                            content = "[语音]";
                            break;
                        case WSChat.LOCATION:
                            content = "[定位]";
                            break;
                        case WSChat.TRACK:
                            try {
                                if (chat.getExtObject(GsonUtil.getGsonInstance())
                                        .getTrack() != null) {
                                    content = chat.getExtObject(GsonUtil.getGsonInstance())
                                            .getTrack()
                                            .getDetail();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                content = "该内容暂不支持，请更新至最新版查看";
                            }
                            break;
                        case WSChat.TIPS:
                            try {
                                if (chat.getExtObject(GsonUtil.getGsonInstance())
                                        .getTips() != null) {
                                    content = chat.getExtObject(GsonUtil.getGsonInstance())
                                            .getTips()
                                            .getTitle() + "：" + chat.getExtObject(GsonUtil
                                            .getGsonInstance())
                                            .getTips()
                                            .getDetail();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                content = "该内容暂不支持，请更新至最新版查看";
                            }
                            break;
                        case WSChat.HINTS:
                            try {
                                switch (chat.getExtObject(GsonUtil.getGsonInstance())
                                        .getHints()
                                        .getAction()) {
                                    case WSHints.ACTION_MERCHANT_SMART_REPLY:
                                        content = chat.getExtObject(GsonUtil.getGsonInstance())
                                                .getHints()
                                                .getTitle();
                                        break;
                                    default:
                                        // hints类型消息没有默认类型的视图，也就是使用Text类型的视图
                                        break;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                content = "该内容暂不支持，请更新至最新版查看";

                            }
                            break;
                        case WSChat.TEXT:
                            content = chat.getContent();
                            break;
                        default:
                            content = chat.getContent();
                            if(TextUtils.isEmpty(content)) {
                                content = "该内容暂不支持，请更新至最新版查看";
                            }
                            break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
