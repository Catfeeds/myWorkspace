package com.hunliji.hljchatlibrary;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.hunliji.hljchatlibrary.api.ChatApi;
import com.hunliji.hljchatlibrary.models.Channel;
import com.hunliji.hljchatlibrary.models.ChatRxEvent;
import com.hunliji.hljchatlibrary.reveiver.NetworkReceiver;
import com.hunliji.hljcommonlibrary.models.City;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljcommonlibrary.models.realm.WSChat;
import com.hunliji.hljcommonlibrary.models.realm.WSChatAuthor;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.utils.ChannelUtil;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.EmptySubscriber;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.hljhttplibrary.entities.HljHttpHeader;
import com.hunliji.hljhttplibrary.utils.GsonUtil;

import org.joda.time.DateTime;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.net.bmobile.websocketrails.WebSocketRailsDataCallback;
import br.net.bmobile.websocketrails.WebSocketRailsDispatcher;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by Suncloud on 2016/10/12.
 */

public class WebSocket implements NetworkReceiver.NetworkInfoChangeListener {

    private WeakReference<Context> contextWeakReference;
    private WebSocketRailsDispatcher dispatcher;
    private NetworkReceiver mNetworkStateReceiver;

    private Handler handler = new Handler();
    private int connectCount;
    private HashMap<String, SendMsgCallbackListener> sendMsgCallbackListenerHashMap;
    private long userId;
    public static String SOCKET_HOST = "http://message.hunliji.com/";
    private static WebSocket webSocket;
    private boolean isSyncFinish = true;
    private ArrayList<Channel> channels;

    private Subscription connectSubscription;

    private Subscription saveCitySubscription;
    private AbstractMap.SimpleEntry<Long, City> cityEntry; //婚车城市记录

    public static void setSocketHost(String socketHost) {
        SOCKET_HOST = socketHost;
        if (webSocket != null) {
            webSocket.disconnect(webSocket.getContext());
            webSocket.socketConnect(webSocket.getContext());
        }
    }

    public Context getContext() {
        return contextWeakReference == null ? null : contextWeakReference.get();
    }

    private WebSocket() {
    }

    public static WebSocket getInstance() {
        if (webSocket == null) {
            webSocket = new WebSocket();
        }
        return webSocket;
    }

    private String getSocketPath(Context mContext) {
        return SOCKET_HOST + String.format("websocket?os=android%s&appver=%s",
                android.os.Build.VERSION.RELEASE,
                ChannelUtil.getVersionCode(mContext));
    }

    public void socketConnect(Context mContext) {
        if (mContext == null) {
            return;
        }
        contextWeakReference = new WeakReference<>(mContext.getApplicationContext());
        if (connectCount == 0 && !isConnect()) {
            connect(mContext);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    connectCount--;
                    if (!isConnect() && connectCount > 0) {
                        connect(getContext());
                        handler.postDelayed(this, 10000);
                    } else {
                        connectCount = 0;
                    }
                }
            }, 10000);
        }
        connectCount++;
    }

    private void connect(Context mContext) {
        if (mContext == null) {
            return;
        }
        User user = UserSession.getInstance()
                .getUser(mContext);
        if (user == null) {
            return;
        }
        userId = user.getId();

        if (isConnect() || !CommonUtil.isUnsubscribed(connectSubscription)) {
            return;
        }

        connectSubscription = WSRealmHelper.chatToChannelObb(user.getId())
                .onErrorReturn(new Func1<Throwable, Boolean>() {
                    @Override
                    public Boolean call(Throwable throwable) {
                        throwable.printStackTrace();
                        return false;
                    }
                })
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (getContext() == null || !CommonUtil.isNetworkConnected(getContext())) {
                            RxBus.getDefault()
                                    .post(new RxEvent(RxEvent.RxEventType.FINISH_SYNC_CHANNELS,
                                            null));
                            return;
                        }
                        if (isConnect()) {
                            return;
                        }
                        try {
                            Map<String, String> headers = new HljHttpHeader(getContext())
                                    .getHeaderMap();
                            try {
                                Date date = WSRealmHelper.getLastMessageDate(userId);
                                if (date != null) {
                                    headers.put("last_msg_time",
                                            new DateTime(date).toString("yyyy-MM-dd HH:mm:ss"));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            isSyncFinish = false;
                            channels = new ArrayList<>();
                            dispatcher = new WebSocketRailsDispatcher(new URL(getSocketPath(
                                    getContext())), headers);
                            dispatcher.bind("connection_error", connectionFailedCallback);
                            dispatcher.bind("connection_closed", connectionFailedCallback);
                            dispatcher.bind("new_message", newMessageCallback);
                            dispatcher.bind("new_channels", channelMessageCallback);
                            dispatcher.bind("writing_message", writingMessageCallback);
                            dispatcher.bind("read_message", readMessageCallback);
                            dispatcher.bind("finish_new_channels", finishSyncChannelCallback);
                            dispatcher.bind("user_update", userUpdateCallback);
                            dispatcher.connect();
                            if (mNetworkStateReceiver == null) {
                                mNetworkStateReceiver = new NetworkReceiver(WebSocket.this);
                                IntentFilter intentFilter = new IntentFilter(ConnectivityManager
                                        .CONNECTIVITY_ACTION);
                                getContext().getApplicationContext()
                                        .registerReceiver(mNetworkStateReceiver, intentFilter);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            mNetworkStateReceiver = null;
                        }
                    }
                });
    }

    private WebSocketRailsDataCallback connectionFailedCallback = new WebSocketRailsDataCallback() {
        @Override
        public void onDataAvailable(Object data) {
            if (!isSyncFinish) {
                isSyncFinish = true;
                channels.clear();
                RxBus.getDefault()
                        .post(new RxEvent(RxEvent.RxEventType.FINISH_SYNC_CHANNELS, null));
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    connectCount = 0;
                    if (sendMsgCallbackListenerHashMap != null && !sendMsgCallbackListenerHashMap
                            .isEmpty()) {
                        for (Map.Entry<String, SendMsgCallbackListener> entry :
                                sendMsgCallbackListenerHashMap.entrySet()) {
                            if (entry.getValue() != null) {
                                entry.getValue()
                                        .onSendFailure();
                            }
                        }
                        sendMsgCallbackListenerHashMap.clear();
                    }
                }
            });
        }
    };

    private WebSocketRailsDataCallback newMessageCallback = new WebSocketRailsDataCallback() {
        @Override
        public void onDataAvailable(Object data) {
            if (data == null) {
                return;
            }
            try {
                Gson gson = GsonUtil.getGsonInstance();
                JsonElement jsonElement = gson.toJsonTree(data);
                if (jsonElement.getAsJsonObject()
                        .get("message") == null) {
                    return;
                }
                final WSChat chat = gson.fromJson(jsonElement.getAsJsonObject()
                        .get("message"), WSChat.class);
                if (TextUtils.isEmpty(chat.getIdStr())) {
                    return;
                }
                WSRealmHelper.newMessage(userId, chat);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (chat.isCurrentUser()) {
                            RxBus.getDefault()
                                    .post(new RxEvent(RxEvent.RxEventType.SEND_MESSAGE, chat));
                        } else {
                            RxBus.getDefault()
                                    .post(new RxEvent(RxEvent.RxEventType.WS_MESSAGE, chat));
                        }
                    }
                }, 500);
                gotMessage(chat);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private WebSocketRailsDataCallback channelMessageCallback = new WebSocketRailsDataCallback() {
        @Override
        public void onDataAvailable(Object data) {
            if (data == null) {
                return;
            }
            try {
                Gson gson = GsonUtil.getGsonInstance();
                JsonElement jsonElement = gson.toJsonTree(data);
                List<Channel> channels = GsonUtil.getGsonInstance()
                        .fromJson(jsonElement, new TypeToken<List<Channel>>() {}.getType());
                if (CommonUtil.isCollectionEmpty(channels)) {
                    return;
                }
                WebSocket.this.channels.addAll(channels);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private WebSocketRailsDataCallback finishSyncChannelCallback = new WebSocketRailsDataCallback
            () {
        @Override
        public void onDataAvailable(Object data) {
            if (!CommonUtil.isCollectionEmpty(channels)) {
                WSRealmHelper.updateChannels(userId, channels);
            }
            if (!isSyncFinish) {
                isSyncFinish = true;
                RxBus.getDefault()
                        .post(new RxEvent(RxEvent.RxEventType.FINISH_SYNC_CHANNELS, null));
            }
        }
    };

    private WebSocketRailsDataCallback userUpdateCallback = new WebSocketRailsDataCallback() {
        @Override
        public void onDataAvailable(Object data) {
            if (data == null) {
                return;
            }
            try {
                Gson gson = GsonUtil.getGsonInstance();
                JsonElement jsonElement = gson.toJsonTree(data);
                if (jsonElement.getAsJsonObject()
                        .get("user") == null) {
                    return;
                }
                final WSChatAuthor user = gson.fromJson(jsonElement.getAsJsonObject()
                        .get("user"), WSChatAuthor.class);
                if (user.getId() > 0) {
                    return;
                }
                WSRealmHelper.updateUser(user);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private WebSocketRailsDataCallback writingMessageCallback = new WebSocketRailsDataCallback() {
        @Override
        public void onDataAvailable(Object data) {
            if (data == null) {
                return;
            }
            try {
                Gson gson = GsonUtil.getGsonInstance();
                JsonObject dataObject = gson.toJsonTree(data)
                        .getAsJsonObject();
                if (dataObject == null) {
                    return;
                }
                long fromId = dataObject.get("from_user_id")
                        .getAsLong();
                long toId = dataObject.get("to_user_id")
                        .getAsLong();
                if (toId != userId || fromId == 0) {
                    return;
                }
                RxBus.getDefault()
                        .post(new ChatRxEvent(ChatRxEvent.RxEventType.WRITING_MESSAGE, fromId));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private WebSocketRailsDataCallback readMessageCallback = new WebSocketRailsDataCallback() {
        @Override
        public void onDataAvailable(Object data) {
            if (data == null) {
                return;
            }
            try {
                Gson gson = GsonUtil.getGsonInstance();
                JsonObject dataObject = gson.toJsonTree(data)
                        .getAsJsonObject();
                if (dataObject == null) {
                    return;
                }
                String channelId = dataObject.get("channel_id")
                        .getAsString();
                if (TextUtils.isEmpty(channelId)) {
                    return;
                }
                WSRealmHelper.readAllMessage(channelId, userId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void gotMessage(WSChat chat) {
        ArrayList<String> ids = new ArrayList<>();
        ids.add(chat.getIdStr());
        ArrayList<Map<String, Object>> speakers = new ArrayList<>();
        Map<String, Object> speaker = new HashMap<>();
        speaker.put("user_id", chat.getFromId());
        speaker.put("messages_id", ids);
        speakers.add(speaker);
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> meta = new HashMap<>();
        meta.put("result", true);
        map.put("meta", meta);
        map.put("speakers", speakers);
        map.put("user_id", chat.getToId());
        data.put("data", map);
        if (dispatcher != null && dispatcher.isconnect()) {
            dispatcher.trigger("got_message", data, null, null);
        }
    }


    @Override
    public void networkInfoChange(NetworkInfo net) {
        if (net != null) {
            switch (net.getType()) {
                case ConnectivityManager.TYPE_WIFI:
                case ConnectivityManager.TYPE_MOBILE:
                    if (getContext() == null) {
                        return;
                    }
                    User user = UserSession.getInstance()
                            .getUser(getContext());
                    if (user != null && !isConnect()) {
                        socketConnect(getContext());
                    }
                    break;
                default:
                    break;
            }
        } else {
            connectCount = 0;
        }
    }


    public void sendReadMessage(String channelId) {
        if (!TextUtils.isEmpty(channelId)) {
            Map<String, Object> map = new HashMap<>();
            Map<String, Object> data = new HashMap<>();
            Map<String, Object> meta = new HashMap<>();
            meta.put("result", true);
            map.put("meta", meta);
            map.put("channel_id", channelId);
            data.put("data", map);
            if (dispatcher != null && dispatcher.isconnect()) {
                dispatcher.trigger("read_message", data);
            }
        }
    }

    public void sendMessage(final WSChat chat, SendMsgCallbackListener sendMsgCallbackListener) {
        User user = UserSession.getInstance()
                .getUser(getContext());
        if (user == null) {
            return;
        }

        Map<String, Object> map = new HashMap<>();
        final Map<String, Object> message = new HashMap<>();
        Map<String, Object> from = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        from.put("user_id",
                chat.getSpeaker()
                        .getId());
        from.put("avatar",
                chat.getSpeaker()
                        .getAvatar());
        from.put("nick",
                chat.getSpeaker()
                        .getName());
        message.put("kind", chat.getKind());
        message.put("source", chat.getSource());
        switch (chat.getKind()) {
            case WSChat.TEXT:
                message.put("content", chat.getContent());
                break;
            case WSChat.IMAGE:
                Map<String, Object> mediaContent = new HashMap<>();
                mediaContent.put("path",
                        chat.getMedia()
                                .getPath());
                mediaContent.put("height",
                        chat.getMedia()
                                .getHeight());
                mediaContent.put("width",
                        chat.getMedia()
                                .getWidth());
                message.put("media_content", mediaContent);
                break;
            case WSChat.VOICE:
                mediaContent = new HashMap<>();
                mediaContent.put("path",
                        chat.getMedia()
                                .getPath());
                mediaContent.put("voice_duration",
                        chat.getMedia()
                                .getVoiceDuration());
                message.put("media_content", mediaContent);
                break;
            case WSChat.WORK_OR_CASE:
            case WSChat.CUSTOM_MEAL:
            case WSChat.PRODUCT:
                Map<String, Object> productContent = new HashMap<>();
                productContent.put("id",
                        chat.getProduct()
                                .getId());
                productContent.put("title",
                        chat.getProduct()
                                .getTitle());
                productContent.put("cover_path",
                        chat.getProduct()
                                .getCoverPath());
                productContent.put("actual_price",
                        chat.getProduct()
                                .getActualPrice());
                productContent.put("cover_height",
                        chat.getProduct()
                                .getHeight());
                productContent.put("cover_width",
                        chat.getProduct()
                                .getWidth());
                message.put("product_content", productContent);
                break;
            case WSChat.TRACK:
            case WSChat.LOCATION:
                message.put("ext", chat.getExtObject(GsonUtil.getGsonInstance()));
                break;
        }
        message.put("created_at", HljTimeUtils.time2UtcString(chat.getCreatedAt()));
        message.put("_id", chat.getIdStr());
        message.put("to", chat.getToId());
        message.put("speaker", from);
        map.put("message", message);
        data.put("data", map);

        chat.setError(true);
        if (!isConnect()) {
            socketConnect(getContext());
            chat.setSending(false);
        } else {
            if (CommonUtil.isUnsubscribed(saveCitySubscription) && cityEntry != null) {
                saveCitySubscription = ChatApi.saveUserCity(cityEntry.getValue()
                        .getCid(), cityEntry.getKey())
                        .subscribe(new EmptySubscriber());
            }
            if (sendMsgCallbackListenerHashMap == null) {
                sendMsgCallbackListenerHashMap = new HashMap<>();
            }
            sendMsgCallbackListenerHashMap.put(chat.getIdStr(), sendMsgCallbackListener);
            dispatcher.trigger("new_message", data, new WebSocketRailsDataCallback() {
                @Override
                public void onDataAvailable(Object data) {
                    chatSendCallback(chat, data);
                }
            }, new WebSocketRailsDataCallback() {
                @Override
                public void onDataAvailable(Object data) {
                    chatSendCallback(chat, data);
                }
            });
        }
        WSRealmHelper.saveLocalMessage(chat);
        RxBus.getDefault()
                .post(new RxEvent(RxEvent.RxEventType.SEND_MESSAGE, chat));

    }

    private void chatSendCallback(WSChat chat, Object data) {
        String hostMsgId = null;
        String channelId = null;
        JsonObject jsonObject = null;
        Date createAt = null;
        String localId = chat.getIdStr();
        chat.setSending(false);
        if (data != null) {
            try {
                jsonObject = GsonUtil.getGsonInstance()
                        .toJsonTree(data)
                        .getAsJsonObject();
                hostMsgId = jsonObject.get("host_msg_id")
                        .getAsString();
                channelId = jsonObject.get("channel_id")
                        .getAsString();
                createAt = GsonUtil.getGsonInstance()
                        .fromJson(jsonObject.get("created_at"), Date.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (!TextUtils.isEmpty(hostMsgId)) {
            chat.setError(false);
            chat.setIdStr(hostMsgId);
            chat.setChannel(channelId);
            if (createAt != null) {
                chat.setCreatedAt(createAt);
            }
        } else {
            chat.setError(true);
        }
        if (sendMsgCallbackListenerHashMap != null && sendMsgCallbackListenerHashMap.get(localId)
                != null) {
            if (!TextUtils.isEmpty(hostMsgId)) {
                sendMsgCallbackListenerHashMap.get(localId)
                        .onSendSuccess();
            } else {
                sendMsgCallbackListenerHashMap.get(localId)
                        .onSendFailure();
                Observable.just(jsonObject)
                        .map(new Func1<JsonObject, String>() {
                            @Override
                            public String call(JsonObject jsonObject) {
                                try {
                                    return jsonObject.get("msg")
                                            .getAsString();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                return null;
                            }
                        })
                        .filter(new Func1<String, Boolean>() {
                            @Override
                            public Boolean call(String s) {
                                return !TextUtils.isEmpty(s);
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<String>() {
                            @Override
                            public void call(String s) {
                                if (getContext() == null) {
                                    return;
                                }
                                ToastUtil.showToast(getContext(), s, 0);
                            }
                        });
            }
            sendMsgCallbackListenerHashMap.remove(localId);
        }
        WSRealmHelper.sendMessageCallback(chat, localId);
    }

    /**
     * 发送正在输入状态，不需要关心是否成功
     *
     * @param fromId 发送人id ，当前用户
     * @param toId   接受人id，聊天对象
     */
    public void sendWriting(long fromId, long toId) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        map.put("from_user_id", fromId);
        map.put("to_user_id", toId);
        data.put("data", map);
        if (!isConnect()) {
            socketConnect(getContext());
        } else {
            dispatcher.trigger("writing_message", data);
        }

    }

    public boolean isConnect() {
        return dispatcher != null && dispatcher.isconnect();
    }

    public void disconnect(Context mContext) {
        if (mContext == null) {
            return;
        }
        cityEntry = null;
        CommonUtil.unSubscribeSubs(connectSubscription, saveCitySubscription);
        contextWeakReference = new WeakReference<>(mContext);
        if (dispatcher != null) {
            dispatcher.disconnect();
        }
        if (mNetworkStateReceiver != null && mContext instanceof Activity) {
            try {
                mContext.getApplicationContext()
                        .unregisterReceiver(mNetworkStateReceiver);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mNetworkStateReceiver = null;
        }
        connectCount = 0;
    }

    public interface SendMsgCallbackListener {
        void onSendSuccess();

        void onSendFailure();
    }

    public boolean isSyncFinish() {
        return isSyncFinish && CommonUtil.isUnsubscribed(connectSubscription);
    }

    public void addCity(long merchantId, City city) {
        if (city == null || merchantId == 0) {
            return;
        }
        this.cityEntry = new AbstractMap.SimpleEntry<>(merchantId, city);
    }

    public void removeCity() {
        CommonUtil.unSubscribeSubs(saveCitySubscription);
        this.cityEntry = null;
    }
}
