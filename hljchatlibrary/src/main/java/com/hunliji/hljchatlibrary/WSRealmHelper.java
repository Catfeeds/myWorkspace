package com.hunliji.hljchatlibrary;

import android.content.Context;
import android.text.TextUtils;

import com.hunliji.hljchatlibrary.models.Channel;
import com.hunliji.hljchatlibrary.models.ChatRxEvent;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljcommonlibrary.models.chat_ext_object.WSExtObject;
import com.hunliji.hljcommonlibrary.models.realm.ChannelStick;
import com.hunliji.hljcommonlibrary.models.realm.ChatDraft;
import com.hunliji.hljcommonlibrary.models.realm.WSChannel;
import com.hunliji.hljcommonlibrary.models.realm.WSChat;
import com.hunliji.hljcommonlibrary.models.realm.WSChatAuthor;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.SystemNotificationUtil;
import com.hunliji.hljhttplibrary.authorization.UserSession;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * ass
 * Created by wangtao on 2018/1/5.
 */

public class WSRealmHelper {

    public static List<Long> chatIds;

    static synchronized Date getLastMessageDate(long userId) {
        Realm realm = Realm.getDefaultInstance();
        Date date = realm.where(WSChat.class)
                .equalTo("userId", userId)
                .equalTo("isError", false)
                .maximumDate("createdAt");
        realm.close();
        return date;
    }

    public static synchronized void updateChannels(long userId, List<Channel> channels) {
        if (CommonUtil.isCollectionEmpty(channels)) {
            return;
        }
        Realm realm = Realm.getDefaultInstance();
        List<WSChannel> wsChannels = new ArrayList<>();
        List<WSChat> wsChats = new ArrayList<>();
        List<String> removeStickChannelIds = new ArrayList<>();

        for (Channel channel : channels) {
            WSChat chat = channel.getChat(userId);
            if (chat == null) {
                continue;
            }

            //判断消息是否未读
            WSChat realmChat = realm.where(WSChat.class)
                    .equalTo("idStr", chat.getIdStr())
                    .findFirst();
            if (!chat.isCurrentUser()) {
                if (CommonUtil.isCollectionEmpty(chatIds) || !chatIds.contains(chat.getFromId())) {
                    if (realmChat != null) {
                        chat.setNew(realmChat.isNew());
                    } else {
                        chat.setNew(chat.isUnRead());
                    }
                }
            }
            wsChats.add(chat);

            //同步用户城市
            WSChatAuthor chatUser = chat.isCurrentUser() ? chat.getSpeakerTo() : chat.getSpeaker();
            if (chatUser.getCity() == null || chatUser.getCity()
                    .getId() == 0) {
                WSChatAuthor oldUser = realm.where(WSChatAuthor.class)
                        .equalTo("id", chat.getSessionId())
                        .greaterThan("city.id", 0)
                        .findFirst();
                if (oldUser != null) {
                    chatUser.setCity(oldUser.getCity());
                }
            }

            WSChannel wsChannel = realm.where(WSChannel.class)
                    .equalTo("key", WSChannel.getWSChannelKey(userId, chatUser.getId()))
                    .findFirst();
            if (wsChannel != null) {
                wsChannel = realm.copyFromRealm(wsChannel);
                if (wsChannel.getTime()
                        .before(chat.getCreatedAt())) {
                    wsChannel.setLastMessage(chat);
                }
            } else {
                wsChannel = new WSChannel(userId, chatUser, chat);
                int newCount = (int) realm.where(WSChat.class)
                        .equalTo("userId", chat.getUserId())
                        .equalTo("sessionId", chat.getSessionId())
                        .equalTo("isNew", true)
                        .count();
                if (newCount > 0) {
                    int newMessageCount = (int) realm.where(WSChat.class)
                            .equalTo("userId", chat.getUserId())
                            .equalTo("sessionId", chat.getSessionId())
                            .notEqualTo("kind", WSChat.TRACK)
                            .equalTo("isNew", true)
                            .count();
                    wsChannel.setUnreadMessageCount(newMessageCount);
                    wsChannel.setUnreadTrackCount(newCount - newMessageCount);
                }
                ChatDraft draft = realm.where(ChatDraft.class)
                        .equalTo("key",
                                ChatDraft.getWSChatKey(chat.getUserId(), chat.getSessionId()))
                        .findFirst();
                if (draft != null) {
                    wsChannel.setDraft(draft);
                }
            }
            if (realmChat == null && chat.isNew()) {
                wsChannel.setNewsCount(chat);
            }
            if ((channel.getStickAt() == null)) {
                wsChannel.setStick(null);
                removeStickChannelIds.add(channel.getChannelId());
            } else {
                ChannelStick stick = new ChannelStick(userId,
                        channel.getChannelId(),
                        channel.getStickAt());
                wsChannel.setStick(stick);
            }
            wsChannels.add(wsChannel);
        }

        realm.beginTransaction();
        realm.insertOrUpdate(wsChats);
        if (!CommonUtil.isCollectionEmpty(wsChannels)) {
            realm.insertOrUpdate(wsChannels);
        }
        if (!CommonUtil.isCollectionEmpty(removeStickChannelIds)) {
            realm.where(ChannelStick.class)
                    .equalTo("userId", userId)
                    .in("channel",
                            removeStickChannelIds.toArray(new String[removeStickChannelIds.size()]))
                    .findAll()
                    .deleteAllFromRealm();
        }
        realm.commitTransaction();
        realm.close();
    }

    static Observable<Boolean> chatToChannelObb(final long userId) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                Realm realm = Realm.getDefaultInstance();
                List<WSChat> wsChats = realm.where(WSChat.class)
                        .equalTo("userId", userId)
                        .isNull("wsChannelKey")
                        .findAllSorted("createdAt", Sort.DESCENDING, "idStr", Sort.DESCENDING);
                if (CommonUtil.isCollectionEmpty(wsChats)) {
                    realm.close();
                    subscriber.onNext(true);
                    subscriber.onCompleted();
                    return;
                }
                List<WSChat> chats = realm.copyFromRealm(wsChats);
                Set<Long> keyMemory = new HashSet<>();
                List<WSChannel> channels = new ArrayList<>();
                for (WSChat chat : chats) {
                    chat.setWsChannelKey(WSChannel.getWSChannelKey(chat.getUserId(),
                            chat.getSessionId()));
                    if (keyMemory.add(chat.getSessionId())) {
                        WSChannel wsChannel = realm.where(WSChannel.class)
                                .equalTo("key",
                                        WSChannel.getWSChannelKey(userId, chat.getSessionId()))
                                .findFirst();
                        if (wsChannel == null) {
                            WSChatAuthor chatUser = chat.isCurrentUser() ? chat.getSpeakerTo() :
                                    chat.getSpeaker();
                            wsChannel = new WSChannel(chat.getUserId(), chatUser, chat);
                            int newCount = (int) realm.where(WSChat.class)
                                    .equalTo("userId", chat.getUserId())
                                    .equalTo("sessionId", chat.getSessionId())
                                    .equalTo("isNew", true)
                                    .count();
                            if (newCount > 0) {
                                int newMessageCount = (int) realm.where(WSChat.class)
                                        .equalTo("userId", chat.getUserId())
                                        .equalTo("sessionId", chat.getSessionId())
                                        .notEqualTo("kind", WSChat.TRACK)
                                        .equalTo("isNew", true)
                                        .count();
                                wsChannel.setUnreadMessageCount(newMessageCount);
                                wsChannel.setUnreadTrackCount(newCount - newMessageCount);
                            }
                            ChannelStick stick = realm.where(ChannelStick.class)
                                    .equalTo("userId", chat.getUserId())
                                    .equalTo("channel", chat.getChannel())
                                    .findFirst();
                            if (stick != null) {
                                wsChannel.setStick(stick);
                            }
                            ChatDraft draft = realm.where(ChatDraft.class)
                                    .equalTo("key",
                                            ChatDraft.getWSChatKey(chat.getUserId(),
                                                    chat.getSessionId()))
                                    .findFirst();
                            if (draft != null) {
                                wsChannel.setDraft(draft);
                            }
                            channels.add(wsChannel);
                        }
                    }
                }
                realm.beginTransaction();
                if (!CommonUtil.isCollectionEmpty(channels)) {
                    realm.insertOrUpdate(channels);
                }
                realm.insertOrUpdate(chats);
                realm.commitTransaction();
                realm.close();
                subscriber.onNext(true);
                subscriber.onCompleted();
            }
        })
                .onErrorReturn(new Func1<Throwable, Boolean>() {
                    @Override
                    public Boolean call(Throwable throwable) {
                        throwable.printStackTrace();
                        return false;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    static synchronized void newMessage(long userId, WSChat chat) {
        Realm realm = Realm.getDefaultInstance();
        chat.onRealmChange(userId);

        WSChat realmChat = realm.where(WSChat.class)
                .equalTo("idStr", chat.getIdStr())
                .findFirst();
        if (!chat.isCurrentUser()) {
            if (CommonUtil.isCollectionEmpty(chatIds) || !chatIds.contains(chat.getFromId())) {
                chat.setNew(realmChat == null || realmChat.isNew());
            }
        }

        WSChatAuthor chatUser = chat.isCurrentUser() ? chat.getSpeakerTo() : chat.getSpeaker();

        if (chatUser.getCity() == null || chatUser.getCity()
                .getId() == 0) {
            WSChatAuthor oldUser = realm.where(WSChatAuthor.class)
                    .equalTo("id", chat.getSessionId())
                    .greaterThan("city.id", 0)
                    .findFirst();
            if (oldUser != null) {
                chatUser.setCity(realm.copyFromRealm(oldUser.getCity()));
            }
        }

        WSChannel wsChannel = realm.where(WSChannel.class)
                .equalTo("key", WSChannel.getWSChannelKey(userId, chatUser.getId()))
                .findFirst();
        if (wsChannel != null) {
            wsChannel = realm.copyFromRealm(wsChannel);
            if (wsChannel.getTime()
                    .before(chat.getCreatedAt())) {
                wsChannel.setLastMessage(chat);
            }
        } else {
            wsChannel = new WSChannel(userId, chatUser, chat);
        }
        if (realmChat == null && chat.isNew()) {
            wsChannel.setNewsCount(chat);
        }
        realm.beginTransaction();
        realm.insertOrUpdate(chat);
        realm.insertOrUpdate(wsChannel);
        realm.commitTransaction();
        realm.close();
    }


    public static void entryChat(Context context, long userId, long chatUserId) {
        resetUnreadCount(userId, chatUserId);
        if (chatIds == null) {
            chatIds = new ArrayList<>();
        }
        chatIds.add(chatUserId);
        //取消对应用户的私信通知
        SystemNotificationUtil.INSTANCE.readNotification(context, (int) chatUserId);
    }

    public static void exitChat(Context context, long userId, long chatUserId) {
        resetUnreadCount(userId, chatUserId);
        if (chatIds != null) {
            chatIds.remove(chatUserId);
        }
        //取消对应用户的私信通知
        SystemNotificationUtil.INSTANCE.readNotification(context, (int) chatUserId);
    }

    private static synchronized void resetUnreadCount(long userId, long chatUserId) {
        Realm realm = Realm.getDefaultInstance();
        WSChannel lastMessage = realm.where(WSChannel.class)
                .equalTo("key", WSChannel.getWSChannelKey(userId, chatUserId))
                .findFirst();
        if (lastMessage != null && (lastMessage.getUnreadTrackCount() > 0 || lastMessage
                .getUnreadMessageCount() > 0)) {
            RxBus.getDefault()
                    .post(new RxEvent(RxEvent.RxEventType.WS_RESET_UNREAD_MESSAGE, chatUserId));
            realm.beginTransaction();
            lastMessage.setUnreadMessageCount(0);
            lastMessage.setUnreadTrackCount(0);
            realm.commitTransaction();
        }
        realm.close();
    }

    static synchronized void readAllMessage(String channelId, long userId) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<WSChat> chats = realm.where(WSChat.class)
                .equalTo("channel", channelId)
                .equalTo("fromId", userId)
                .equalTo("unRead", true)
                .findAll();
        if (!chats.isEmpty()) {
            realm.beginTransaction();
            for (WSChat wsChat : chats) {
                wsChat.setUnRead(false);
            }
            realm.commitTransaction();
            RxBus.getDefault()
                    .post(new ChatRxEvent(ChatRxEvent.RxEventType.READ_MESSAGE, channelId));
        }
        realm.close();
    }

    public static synchronized void saveLocalMessage(WSChat chat) {
        Realm realm = Realm.getDefaultInstance();

        if (TextUtils.isEmpty(chat.getWsChannelKey())) {
            chat.setWsChannelKey(WSChannel.getWSChannelKey(chat.getUserId(), chat.getSessionId()));
        }
        //同步用户城市
        WSChatAuthor chatUser = chat.isCurrentUser() ? chat.getSpeakerTo() : chat.getSpeaker();

        WSChannel wsChannel = realm.where(WSChannel.class)
                .equalTo("key", chat.getWsChannelKey())
                .findFirst();
        if (wsChannel != null) {
            wsChannel = realm.copyFromRealm(wsChannel);
            wsChannel.setLastMessage(chat);
        } else {
            wsChannel = new WSChannel(chat.getUserId(), chatUser, chat);
        }
        realm.beginTransaction();
        realm.insertOrUpdate(chat);
        realm.insertOrUpdate(wsChannel);
        realm.commitTransaction();
        realm.close();
    }

    static synchronized void sendMessageCallback(WSChat chat, String localId) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        if (TextUtils.isEmpty(chat.getWsChannelKey())) {
            chat.setWsChannelKey(WSChannel.getWSChannelKey(chat.getUserId(), chat.getSessionId()));
        }
        WSChat realmChat = realm.copyToRealmOrUpdate(chat);
        if (!chat.getIdStr()
                .equals(localId)) {
            WSChannel wsChannel = realm.where(WSChannel.class)
                    .equalTo("lastMessage.idStr", localId)
                    .findFirst();
            realm.where(WSChat.class)
                    .equalTo("idStr", localId)
                    .findAll()
                    .deleteAllFromRealm();
            if (wsChannel != null) {
                wsChannel.setLastMessage(realmChat);
            }
        }
        realm.commitTransaction();
        realm.close();
    }

    public static synchronized void saveChatDraft(ChatDraft draft) {
        Realm realm = Realm.getDefaultInstance();
        WSChannel wsChannel = realm.where(WSChannel.class)
                .equalTo("key", WSChannel.getWSChannelKey(draft.getUserId(), draft.getSessionId()))
                .findFirst();
        if (wsChannel != null) {
            realm.beginTransaction();
            ChatDraft realmDraft = realm.copyToRealmOrUpdate(draft);
            wsChannel.setDraft(realmDraft);
            realm.commitTransaction();
        }
        realm.close();
    }

    public static synchronized String readChatDraft(long userId, long chatUserId) {
        String draftContent = null;
        Realm realm = Realm.getDefaultInstance();
        RealmResults<ChatDraft> chatDrafts = realm.where(ChatDraft.class)
                .equalTo("key", ChatDraft.getWSChatKey(userId, chatUserId))
                .findAll();
        if (chatDrafts.size() > 0) {
            draftContent = chatDrafts.get(0)
                    .getContent();
            realm.beginTransaction();
            chatDrafts.deleteAllFromRealm();
            realm.commitTransaction();
        }
        realm.close();
        return draftContent;
    }

    public static synchronized void clearChatMessage(long userId, long chatUserId) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.where(WSChat.class)
                .equalTo("userId", userId)
                .equalTo("sessionId", chatUserId)
                .findAll()
                .deleteAllFromRealm();
        realm.where(WSChannel.class)
                .equalTo("key", WSChannel.getWSChannelKey(userId, chatUserId))
                .findAll()
                .deleteAllFromRealm();
        realm.commitTransaction();
        realm.close();
    }

    public static synchronized void addStick(long userId, String channel, long chatUserId) {
        Realm realm = Realm.getDefaultInstance();
        WSChannel wsChannel = realm.where(WSChannel.class)
                .equalTo("key", WSChannel.getWSChannelKey(userId, chatUserId))
                .findFirst();
        if (wsChannel != null) {
            realm.beginTransaction();
            ChannelStick stick = realm.copyToRealmOrUpdate(new ChannelStick(userId,
                    channel,
                    new Date()));
            wsChannel.setStick(stick);
            realm.commitTransaction();
        }
        realm.close();

    }

    public static synchronized void removeStick(String channel) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.where(ChannelStick.class)
                .equalTo("channel", channel)
                .findAll()
                .deleteAllFromRealm();
        realm.commitTransaction();
        realm.close();

    }

    public static synchronized void updateUser(WSChatAuthor user) {
        Realm realm = Realm.getDefaultInstance();
        WSChatAuthor wsUser = realm.where(WSChatAuthor.class)
                .equalTo("id", user.getId())
                .findFirst();
        if (wsUser == null || !wsUser.realmEquals(user)) {
            RxBus.getDefault()
                    .post(new RxEvent(RxEvent.RxEventType.WS_USER_UPDATE, user));
            realm.beginTransaction();
            realm.insertOrUpdate(user);
            realm.commitTransaction();
        }
        realm.close();
    }

    public static synchronized void saveChats(
            WSChatAuthor user, WSChatAuthor chatUser, String channel, List<WSChat> chats) {
        if (CommonUtil.isCollectionEmpty(chats)) {
            return;
        }
        if (user == null || chatUser == null) {
            return;
        }

        Realm realm = Realm.getDefaultInstance();


        for (WSChat chat : chats) {
            chat.onChannelMessage(user.getId(), user, chatUser, channel);
        }

        realm.beginTransaction();
        realm.insertOrUpdate(chats);
        WSChat lastMessage = chats.get(chats.size() - 1);
        WSChannel wsChannel = realm.where(WSChannel.class)
                .equalTo("key", WSChannel.getWSChannelKey(user.getId(), chatUser.getId()))
                .findFirst();
        if (wsChannel != null) {
            if (wsChannel.getLastMessage()
                    .getCreatedAt()
                    .before(lastMessage.getCreatedAt())) {
                wsChannel = realm.copyFromRealm(wsChannel);
                wsChannel.setLastMessage(lastMessage);
                realm.insertOrUpdate(wsChannel);
            }
        } else {
            wsChannel = new WSChannel(user.getId(), chatUser, lastMessage);
            realm.insertOrUpdate(wsChannel);
        }

        realm.commitTransaction();
        realm.close();
    }

    public static synchronized int getUnreadMessageCountCount(long userId) {
        int count = 0;
        Realm realm = Realm.getDefaultInstance();
        try {
            count = realm.where(WSChannel.class)
                    .equalTo("userId", userId)
                    .sum("unreadMessageCount")
                    .intValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        realm.close();
        return count;
    }

    public static synchronized int getUnreadMessageCountCount(long userId, long chatUserId) {
        int count = 0;
        Realm realm = Realm.getDefaultInstance();
        try {
            count = realm.where(WSChannel.class)
                    .equalTo("key", WSChannel.getWSChannelKey(userId, chatUserId))
                    .sum("unreadMessageCount")
                    .intValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        realm.close();
        return count;
    }

    /**
     * 存入一条附带WSExtObject数据的聊天记录到数据库，发送者为对应商家，接受者为当前用户
     * 比如：预约成功的提示，领券成功的提示消息
     * 这个消息没有地方获取发送者的头像和昵称，所以需要在聊天内容更新的时候更新数据，否则消息列表页面可能显示错误
     *
     * @param context
     * @param kind          消息类型
     * @param sessionUserId 对应商家的user id
     * @param wsExtObject   附带信息，与kind对应
     */
    public static void saveWSChatToLocal(
            Context context, String kind, long sessionUserId, WSExtObject wsExtObject) {
        User selfUser = UserSession.getInstance()
                .getUser(context);

        Realm realm = Realm.getDefaultInstance();
        WSChatAuthor wsChatAuthor = realm.where(WSChatAuthor.class)
                .equalTo("id", sessionUserId)
                .findFirst();
        if (wsChatAuthor != null) {
            wsChatAuthor = realm.copyFromRealm(wsChatAuthor);
        }
        if (wsChatAuthor == null) {
            wsChatAuthor = new WSChatAuthor(sessionUserId);
        }
        realm.close();

        WSChat chat = new WSChat();
        chat.setFromId(sessionUserId);
        chat.setToId(selfUser.getId());
        chat.setSessionId(sessionUserId);
        chat.setSpeaker(wsChatAuthor);
        chat.setSpeakerTo(new WSChatAuthor(selfUser));
        chat.setIdStr(sessionUserId + "" + System.currentTimeMillis());
        chat.setKind(kind);
        chat.setCreatedAt(new Date());
        chat.setUserId(selfUser.getId());
        chat.setSource(1);
        chat.setExtContent(wsExtObject);

        saveLocalMessage(chat);
    }
}
