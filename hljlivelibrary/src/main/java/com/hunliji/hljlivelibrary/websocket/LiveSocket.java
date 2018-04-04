package com.hunliji.hljlivelibrary.websocket;

import android.content.Context;
import android.net.Uri;
import android.support.v4.util.LongSparseArray;
import android.text.TextUtils;
import android.util.Log;

import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljhttplibrary.entities.HljHttpHeader;
import com.hunliji.hljhttplibrary.entities.HljHttpStatus;
import com.hunliji.hljhttplibrary.utils.GsonUtil;
import com.hunliji.hljlivelibrary.HljLive;
import com.hunliji.hljlivelibrary.R;
import com.hunliji.hljlivelibrary.models.LiveMessage;
import com.hunliji.hljlivelibrary.models.LiveRxEvent;
import com.hunliji.hljlivelibrary.models.wrappers.LiveSocketObject;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpGet;
import com.koushikdutta.async.http.WebSocket;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;

/**
 * Created by Suncloud on 2016/10/25.
 */

public class LiveSocket implements AsyncHttpClient.WebSocketConnectCallback {

    private WebSocket webSocket;

    private int role;
    private long channelId;

    private final String Tag = "WebSocket";

    private static final int NEW_MESSAGE = 2;
    private static final int CALLBACK_MESSAGE = 1;
    private static final int PING = 9;
    private static final int PONG = 10;
    private LongSparseArray<LiveMessage> sendMessages;
    private WeakReference<Context> contextWeakReference;
    private static LiveSocket INSTANCE;
    private int pingCount;
    private Timer timer;
    private boolean isPause;

    public static LiveSocket getInstance(int role, long channelId) {
        if (INSTANCE == null) {
            INSTANCE = new LiveSocket(role, channelId);
        } else {
            INSTANCE.resetSocket(role, channelId);
        }
        return INSTANCE;
    }

    private LiveSocket(int role, long channelId) {
        this.role = role;
        this.channelId = channelId;
    }

    /**
     * 如果 role 或 channelId不同时重连
     *
     * @param role
     * @param channelId
     */
    private void resetSocket(int role, long channelId) {
        if (this.role == role && this.channelId == channelId) {
            return;
        }
        this.role = role;
        this.channelId = channelId;
        disconnect();
    }

    public synchronized void disconnect() {
        if (webSocket != null) {
            Log.e(Tag, "Connect close");
            webSocket.close();
            webSocket = null;
        }
        if (sendMessages != null) {
            sendMessages.clear();
        }
        if (timer != null) {
            timer.cancel();
        }
    }

    public synchronized void connect(Context mContext) {
        contextWeakReference = new WeakReference<>(mContext);
        this.isPause = false;
        try {
            disconnect();
            Log.e(Tag, "Connect");
            AsyncHttpGet asyncHttpGet = new AsyncHttpGet(Uri.parse(HljLive.getLivePath(HljLive
                            .LIVE_CHAT,
                    role,
                    channelId)));
            Map<String, String> mHeaders = new HljHttpHeader(mContext).getHeaderMap();
            if (mHeaders != null && !mHeaders.isEmpty()) {
                for (Map.Entry<String, String> entry : mHeaders.entrySet()) {
                    asyncHttpGet.addHeader(entry.getKey(), entry.getValue());
                }
            }
            AsyncHttpClient.getDefaultInstance()
                    .websocket(asyncHttpGet, null, this);

        } catch (Exception e) {
            Log.e(Tag, "Connect Exception");
            e.printStackTrace();
        }
    }

    @Override
    public void onCompleted(Exception ex, WebSocket webSocket) {
        if (webSocket == null) {
            Log.e(Tag, "webSocket null");
            ex.printStackTrace();
            onClosed();
            return;
        }
        try {
            webSocket.setStringCallback(new WebSocket.StringCallback() {
                @Override
                public void onStringAvailable(String s) {
                    if (TextUtils.isEmpty(s)) {
                        return;
                    }
                    try {
                        LiveSocketObject object = GsonUtil.getGsonInstance()
                                .fromJson(s, LiveSocketObject.class);
                        if (object.getType() == PING) {
                            Log.e(Tag, "ping");
                            sendPong();
                            return;
                        }
                        if (object.getType() == PONG) {
                            Log.e(Tag, "pong");
                            if (pingCount > 0) {
                                pingCount--;
                            }
                            return;
                        }
                        if (object.getData() != null && object.getData()
                                .getChannel() != null) {
                            //更新channel
                            postLiveEvent(new LiveRxEvent(LiveRxEvent.RxEventType.CHANNEL_UPDATE,
                                    channelId,
                                    object.getData()
                                            .getChannel()));
                        }
                        // 更新商家，有可能直接置空
                        postLiveEvent(new LiveRxEvent(LiveRxEvent.RxEventType.MERCHANT_UPDATE,
                                channelId,
                                object.getData()
                                        .getIntroMerchant()));
                        if (object.getData() != null && object.getData()
                                .getIntroProduct() != null) {
                            // 更新婚品
                            postLiveEvent(new LiveRxEvent(LiveRxEvent.RxEventType.PRODUCT_UPDATE,
                                    channelId,
                                    object.getData()
                                            .getIntroProduct()));
                        }
                        if (object.getData() != null && object.getData()
                                .getIntroMerchant() != null) {
                            // 更新套餐
                            postLiveEvent(new LiveRxEvent(LiveRxEvent.RxEventType.WORK_UPDATE,
                                    channelId,
                                    object.getData()
                                            .getIntroWork()));
                        }
                        if (object.getData() != null && object.getData()
                                .getIntroProduct() == null && object.getData()
                                .getIntroWork() == null) {
                            // 清空正在介绍中的商品
                            postLiveEvent(new LiveRxEvent(LiveRxEvent.RxEventType.CLEAR_INTRODUCING,
                                    channelId,
                                    null));
                        }
                        if (object.getType() == CALLBACK_MESSAGE) {
                            //发送消息回执
                            onCallback(object);
                        } else if (object.getType() == NEW_MESSAGE) {
                            //服务器发送消息
                            Log.e(Tag, "new message");
                            if (object.getData() != null && object.getData()
                                    .getMessage() != null) {
                                postLiveEvent(new LiveRxEvent(LiveRxEvent.RxEventType.NEW_MESSAGE,
                                        channelId,
                                        object.getData()));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            webSocket.setClosedCallback(new CompletedCallback() {
                @Override
                public void onCompleted(Exception ex) {
                    //webSocket关闭，本地和服务器关闭都会触发
                    if (ex != null) {
                        ex.printStackTrace();
                    }
                    onClosed();
                }
            });
            this.webSocket = webSocket;
            pingCount = 0;
            Log.e(Tag, "Connected");
        } catch (Exception e) {
            Log.e(Tag, "Callback Exception");
            e.printStackTrace();
            onClosed();
        }

        //30秒计时器
        if (timer != null) {
            timer.cancel();
        }
        this.timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                sendPing();
            }
        }, 30000, 30000);
    }

    private void sendPong() {
        if (!isConnect()) {
            if (getContext() != null && CommonUtil.isNetworkConnected(getContext())) {
                connect(getContext());
            }
            return;
        }
        Log.e(Tag, "send pong");
        webSocket.send("{\"msg_type\":" + PONG + "}");
    }

    /**
     * 连接正常时发送ping请求
     * <p/>
     * 链接断开或ping pong数没对上重连
     */
    private void sendPing() {
        if (isConnect() && pingCount == 0) {
            Log.e(Tag, "send ping");
            webSocket.send("{\"msg_type\":" + PING + "}");
            pingCount++;
        } else {
            onClosed();
            if (!isPause && getContext() != null && CommonUtil.isNetworkConnected(getContext())) {
                connect(getContext());
            }
        }
    }

    public void sendMessage(Context mContext, LiveMessage message) {
        contextWeakReference = new WeakReference<>(mContext);
        if (!isConnect()) {
            if (CommonUtil.isNetworkConnected(mContext)) {
                connect(mContext);
            }
            message.setError(true);
            message.setSending(false);
        } else {
            long id = 1;
            if (sendMessages == null) {
                sendMessages = new LongSparseArray<>();
            }
            if (sendMessages.size() > 0) {
                id = sendMessages.keyAt(sendMessages.size() - 1) + 1;
            }
            message.setClientMessageId(id);
            message.setSending(true);
            sendMessages.put(id, message);
            webSocket.send(message.websocketSerialize());
        }
        postLiveEvent(new LiveRxEvent(LiveRxEvent.RxEventType.SEND_MESSAGE, channelId, message));
    }

    public Context getContext() {
        if (contextWeakReference == null) {
            return null;
        }
        return contextWeakReference.get();
    }

    public boolean isConnect() {
        return webSocket != null && webSocket.isOpen();
    }

    private void onClosed() {
        Log.e(Tag, "closed");
        if (webSocket != null) {
            webSocket.close();
            webSocket = null;
        }
        if (sendMessages != null && sendMessages.size() > 0) {
            for (int i = 0, size = sendMessages.size(); i < size; i++) {
                LiveMessage message = sendMessages.get(i);
                if (message == null) {
                    continue;
                }
                message.setSending(false);
                message.setError(true);
                postLiveEvent(new LiveRxEvent(LiveRxEvent.RxEventType.SEND_MESSAGE,
                        channelId,
                        message));
            }
            sendMessages.clear();
        }
    }

    /**
     * 进入Pause状态
     * isPause=true 时不会重连
     */
    public void onPause() {
        Log.e(Tag, "pause");
        this.isPause = true;
    }

    /**
     * 离开Pause状态
     * isPause=true 时不会重连
     */
    public void onResume() {
        Log.e(Tag, "resume");
        this.isPause = false;
    }

    private void onCallback(LiveSocketObject object) {
        if (sendMessages != null && sendMessages.size() > 0) {
            final LiveMessage message = sendMessages.get(object.getClientMessageId());
            sendMessages.remove(object.getClientMessageId());
            if (message != null) {
                final HljHttpStatus status = object.getStatus();
                if (status != null && status.getRetCode() != 1000) {
                    Log.e(Tag, "send fail");
                    message.setSending(false);
                    message.setError(true);
                    if (getContext() != null) {
                        Observable.empty()
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnCompleted(new Action0() {
                                    @Override
                                    public void call() {
                                        ToastUtil.showToast(getContext(),
                                                status.getMsg(),
                                                R.string.hint_send_message_err___live);
                                    }
                                })
                                .subscribe();
                    }
                } else {
                    Log.e(Tag, "send succeed");
                    if (message.getRoomType() == HljLive.ROOM.CHAT) {
                        message.setId(object.getData()
                                .getMessage()
                                .getChatMessages()
                                .get(0)
                                .getId());
                    } else {
                        message.setId(object.getData()
                                .getMessage()
                                .getLiveMessages()
                                .get(0)
                                .getId());
                    }
                    message.setSending(false);
                    message.setError(false);
                }
                postLiveEvent(new LiveRxEvent(LiveRxEvent.RxEventType.SEND_MESSAGE,
                        channelId,
                        message));
            }
        }
    }

    public void postLiveEvent(LiveRxEvent liveRxEvent) {
        RxBus.getDefault()
                .post(liveRxEvent);
    }
}
