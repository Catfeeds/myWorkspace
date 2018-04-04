package com.hunliji.marrybiz.model;

import com.hunliji.hljkefulibrary.moudles.EMChat;
import com.hunliji.hljkefulibrary.moudles.Support;
import com.hyphenate.chat.Message;

import java.util.Date;

/**
 * Created by wangtao on 2017/10/27.
 */

public class EMLastMessage extends LastMessage  {

    private String userName;

    public EMLastMessage(Message message) {
        EMChat chat = new EMChat(message);
        time = new Date(chat.getTime());
        userName = sessionNick = message.getUserName();
        switch (chat.getType()) {
            case EMChat.VOICE:
                content="[语音]";
                break;
            case EMChat.IMAGE:
                content="[图片]";
                break;
            case EMChat.ROBOT:
                content = chat.getRobotMenuInfo().getTitle();
                break;
            case EMChat.ENQUIRY:
                content = "对客服的服务进行评价";
                break;
            case EMChat.ENQUIRY_RESULT:
                content = "您已评价成功";
                break;
            case EMChat.TRACK:
                content = chat.getTrack().getTitle();
                break;
            case EMChat.MERCHANT:
                content = "婚礼纪给你推荐了一个商家";
                break;
            case EMChat.TRANSFER_HINT:
            case EMChat.TEXT:
                content = chat.getContent();
                break;
            case EMChat.UNKNOWN:
                content = "消息类型不支持";
                break;
            default:
                content = chat.getContent();
                break;
        }
    }


    public EMLastMessage(Message message, Support session) {
        this(message);
        this.sessionAvatar = session != null ? session.getAvatar() : null;
        this.sessionNick = session != null ? session.getNick() : sessionNick;
    }

    public String getUserName() {
        return userName;
    }
}
