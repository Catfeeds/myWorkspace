package com.hunliji.marrybiz.model;

import android.text.TextUtils;

import com.hunliji.hljcommonlibrary.models.chat_ext_object.WSHints;
import com.hunliji.hljcommonlibrary.models.realm.WSChannel;
import com.hunliji.hljcommonlibrary.models.realm.WSChat;
import com.hunliji.hljcommonlibrary.models.realm.WSChatAuthor;
import com.hunliji.hljcommonlibrary.models.realm.WSCity;
import com.hunliji.hljhttplibrary.utils.GsonUtil;

/**
 * Created by Suncloud on 2016/10/19.
 */

public class WSLastMessage extends LastMessage {

    private String channel;
    private WSChannel wsChannel;
    private WSChatAuthor session;
    private WSChat chat;
    private String cityName;
    private String remarkName;

    public WSLastMessage(WSChannel wsChannel) {
        this.wsChannel = wsChannel;
        time = wsChannel.getTime();
        unreadMessageCount = wsChannel.getUnreadMessageCount();
        unreadTrackCount = wsChannel.getUnreadTrackCount();
        if (wsChannel.getChatUser() != null) {
            extend = wsChannel.getChatUser()
                    .getExtend();
        }
        if (wsChannel.getDraft() != null) {
            setDraft(wsChannel.getDraft());
        }
        if (wsChannel.getStick() != null) {
            setStick(wsChannel.getStick());
        }
    }

    public String getCityName() {
        initSession();
        return cityName;
    }

    public void setCityName(WSCity city) {
        if (city != null) {
            this.cityName = city.getName();
        }
    }

    @Override
    public String getSessionNick() {
        initSession();
        if (!TextUtils.isEmpty(remarkName)) {
            return remarkName;
        }
        return super.getSessionNick();
    }

    public void setRemarkName(String remarkName) {
        this.remarkName = remarkName;
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
            setSession(wsChannel.getChatUser());
        }
    }


    public void setSession(WSChatAuthor session) {
        this.session = session;
        sessionNick = session.getNick();
        remarkName = session.getRemarkName();
        sessionAvatar = session.getAvatar();
        sessionId = session.getId();
        extend = session.getExtend();
        if (session.getCity() != null) {
            cityName = session.getCity()
                    .getName();
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
                        case WSChat.IMAGE:
                            content = "[图片]";
                            break;
                        case WSChat.VOICE:
                            content = "[语音]";
                            break;
                        case WSChat.LOCATION:
                            content = "[位置]";
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
                            if (TextUtils.isEmpty(content)) {
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
