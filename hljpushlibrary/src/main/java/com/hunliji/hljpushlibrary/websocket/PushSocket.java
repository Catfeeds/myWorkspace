package com.hunliji.hljpushlibrary.websocket;

import android.content.Context;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.hljhttplibrary.entities.HljHttpHeader;
import com.hunliji.hljhttplibrary.utils.GsonUtil;
import com.hunliji.hljpushlibrary.HljPush;
import com.hunliji.hljpushlibrary.models.PushBody;
import com.hunliji.hljpushlibrary.models.activity.ActivityData;
import com.hunliji.hljpushlibrary.models.live.LiveData;
import com.hunliji.hljpushlibrary.models.notify.NotifyData;
import com.hunliji.hljpushlibrary.utils.PushUtil;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpGet;
import com.koushikdutta.async.http.WebSocket;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * <a href=http://doc.hunliji.com/workspace/myWorkspace.do?projectId=54#4106>应用内推送</a>
 * Created by wangtao on 2017/11/28.
 */

public enum PushSocket {

    INSTANCE;

    public static final String PING = "ping";
    public static final String PONG = "pong";
    public static final String SYS_APP_UP = "sys-app-up";
    private static final String LOGIN_FAIL = "login-fail";
    public static final String TASK_NOTIFY_ACK = "task-notify-ack";
    private static final String NOTIFY_TRENDS = "notify-trends"; //新动态数通知
    private static final String TASK_NOTIFY = "task-notify"; //通知消息
    public static final String LOCALE_CHANGE = "locale-change"; //切换城市
    public static final String LIVE_ON = "plugin-live-on"; //新直播通知
    public static final String LIVE_ON_ACK = "plugin-live-on-ack"; //新直播通知反馈
    public static final String ACTIVITY_ON = "plugin-finder-activity-on"; //有活动弹窗
    public static final String ACTIVITY_ON_ACK = "plugin-finder-activity-on-ack"; // 活动弹窗回执

    private WeakReference<Context> contextWeakReference;
    private WebSocket webSocket;
    private Subscription rxNetSubscription;
    private Subscription connectSubscription;
    private Subscription pingCounterSubscription;
    private boolean eventInitialized; // 活动事件标记

    private int pongCount;

    public Context getContext() {
        if (contextWeakReference == null) {
            return null;
        }
        return contextWeakReference.get();
    }

    public synchronized void connect(Context mContext) {
        if (getContext() == null) {
            contextWeakReference = new WeakReference<>(mContext.getApplicationContext());
        }
        if (!CommonUtil.isNetworkConnected(mContext) || isConnected() || !CommonUtil.isUnsubscribed(
                connectSubscription)) {
            return;
        }
        User user = UserSession.getInstance()
                .getUser(mContext);
        if (user == null) {
            return;
        }

        try {
            connectSubscription = getConnectObb(mContext,
                    HljPush.PUSH_HOST).subscribe(new Subscriber<WebSocket>() {

                @Override
                public void onStart() {
                    super.onStart();
                    log("connect");
                    onClosed();
                }

                @Override
                public void onCompleted() {
                    log("connected");
                    CommonUtil.unSubscribeSubs(pingCounterSubscription);
                    pongCount = 0;
                    log("initial event at subscribe");
                    if (eventInitialized) {
                        sendEventInitial();
                        eventInitialized = false;
                    }
                    pingCounterSubscription = Observable.interval(10,
                            30,
                            TimeUnit.SECONDS,
                            Schedulers.io())
                            .subscribe(new Action1<Long>() {
                                @Override
                                public void call(Long pingCount) {
                                    if (pingCount - pongCount > 1) {
                                        onClosed();
                                    }
                                    sendPing();
                                    if (eventInitialized) {
                                        log("initial event at pong");
                                        sendEventInitial();
                                        eventInitialized = false;
                                    }
                                }
                            });
                }

                @Override
                public void onError(Throwable e) {
                    log("connect error");
                    e.printStackTrace();
                }

                @Override
                public void onNext(WebSocket webSocket) {
                    PushSocket.this.webSocket = webSocket;
                    addWebSocketCallback(webSocket);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Observable<WebSocket> getConnectObb(
            final Context mContext, final String path) {
        return Observable.create(new Observable.OnSubscribe<WebSocket>() {
            @Override
            public void call(final Subscriber<? super WebSocket> subscriber) {
                AsyncHttpGet asyncHttpGet = new AsyncHttpGet(path);
                Map<String, String> mHeaders = new HljHttpHeader(mContext).getHeaderMap();
                if (mHeaders != null && !mHeaders.isEmpty()) {
                    for (Map.Entry<String, String> entry : mHeaders.entrySet()) {
                        asyncHttpGet.addHeader(entry.getKey(), entry.getValue());
                    }
                }
                asyncHttpGet.addHeader("Origin", HljPush.getOriginPath(path));
                AsyncHttpClient.getDefaultInstance()
                        .websocket(asyncHttpGet,
                                null,
                                new AsyncHttpClient.WebSocketConnectCallback() {
                                    @Override
                                    public void onCompleted(Exception ex, WebSocket webSocket) {
                                        if (webSocket == null) {
                                            subscriber.onError(ex);
                                        } else {
                                            try {
                                                subscriber.onNext(webSocket);
                                                subscriber.onCompleted();
                                            } catch (Exception e) {
                                                subscriber.onError(ex);
                                            }
                                        }
                                    }
                                });
            }
        });
    }

    private void addWebSocketCallback(WebSocket webSocket) {
        webSocket.setStringCallback(new WebSocket.StringCallback() {
            @Override
            public void onStringAvailable(String msg) {
                if (TextUtils.isEmpty(msg)) {
                    return;
                }
                log("receive:" + msg);
                Gson gson = GsonUtil.getGsonInstance();
                PushBody body = null;
                try {
                    body = gson.fromJson(msg, PushBody.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (body == null) {
                    return;
                }
                switch (body.getType()) {
                    case PING:
                        sendPong();
                        break;
                    case PONG:
                        pongCount++;
                        break;
                    case LOGIN_FAIL:
                        onClosed();
                        break;
                    case TASK_NOTIFY:
                        try {
                            NotifyData data = gson.fromJson(body.getData(), NotifyData.class);
                            sendNotifyAck(data);
                            if (data.getTask() != null) {
                                PushUtil.INSTANCE.newNotifyTask(getContext(), data);
                                RxBus.getDefault()
                                        .post(new RxEvent(RxEvent.RxEventType.HLJ_NOTIFY_PUSH,
                                                data));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case NOTIFY_TRENDS:
                        try {
                            int count = body.getData()
                                    .getAsInt();
                            RxBus.getDefault()
                                    .post(new RxEvent(RxEvent.RxEventType.TRENDS_COUNT_REFRESH,
                                            count));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case LIVE_ON:
                        try {
                            LiveData data = gson.fromJson(body.getData(), LiveData.class);
                            sendLiveAck(data);
                            if (data.getLiveChannel() != null) {
                                PushUtil.INSTANCE.newLive(data);
                                RxBus.getDefault()
                                        .post(new RxEvent(RxEvent.RxEventType.HLJ_LIVE_PUSH, data));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case ACTIVITY_ON:
                        try {
                            ActivityData data = gson.fromJson(body.getData(), ActivityData.class);
                            sendActivityAck(data);
                            if (data.getFinderActivity() != null) {
                                PushUtil.INSTANCE.newActivity(data);
                                RxBus.getDefault()
                                        .post(new RxEvent(RxEvent.RxEventType.HLJ_ACTIVITY_PUSH,
                                                data));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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
    }

    private void sendNotifyAck(NotifyData notifyData) {
        if (notifyData.getLog() == null) {
            return;
        }
        sendMessage(getContext(), new PushBody(notifyData.getLog()));
    }

    private void sendLiveAck(LiveData liveData) {
        if (liveData.getLogId() == 0) {
            return;
        }
        sendMessage(getContext(), new PushBody(liveData));
    }

    private void sendActivityAck(ActivityData activityData) {
        if (activityData.getLogId() == 0) {
            return;
        }
        sendMessage(getContext(), new PushBody(activityData));
    }

    public void sendChangeCity(long cid) {
        if (!isConnected()) {
            return;
        }
        sendMessage(getContext(), new PushBody(cid));
    }

    private void sendPing() {
        sendMessage(getContext(), new PushBody(PING));
    }

    private void sendPong() {
        sendMessage(getContext(), new PushBody(PONG));
    }

    private void sendEventInitial() {
        sendMessage(getContext(), new PushBody(SYS_APP_UP));
    }

    /**
     * 首页启动后发送一个活动弹窗的请求
     */
    public void eventInitial() {
        if (isConnected()) {
            eventInitialized = false;
            sendEventInitial();
        } else {
            eventInitialized = true;
        }
    }


    private void sendMessage(Context context, PushBody pushBody) {
        if (!isConnected()) {
            if (context != null) {
                connect(context);
            }
            return;
        }
        try {
            String msg = GsonUtil.getGsonInstance()
                    .toJson(pushBody);
            log("send:" + msg);
            webSocket.send(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 已连接
     *
     * @return
     */
    public boolean isConnected() {
        return webSocket != null && webSocket.isOpen();
    }

    public void onClosed() {
        CommonUtil.unSubscribeSubs(connectSubscription, pingCounterSubscription);
        if (webSocket != null) {
            webSocket.close();
            webSocket = null;
        }
    }

    public synchronized void onStart(Context mContext) {
        if (rxNetSubscription == null || rxNetSubscription.isUnsubscribed()) {
            rxNetSubscription = RxBus.getDefault()
                    .toObservable(RxEvent.class)
                    .filter(new Func1<RxEvent, Boolean>() {
                        @Override
                        public Boolean call(RxEvent ykRxEvent) {
                            return ykRxEvent.getType() == RxEvent.RxEventType.NETWORK_CHANGE;
                        }
                    })
                    .map(new Func1<RxEvent, Boolean>() {
                        @Override
                        public Boolean call(RxEvent rxEvent) {
                            return rxEvent.getObject() != null && rxEvent.getObject() instanceof
                                    NetworkInfo && ((NetworkInfo) rxEvent.getObject())
                                    .isConnected();
                        }
                    })
                    .distinctUntilChanged()
                    .filter(new Func1<Boolean, Boolean>() {
                        @Override
                        public Boolean call(Boolean aBoolean) {
                            return aBoolean;
                        }
                    })
                    .subscribe(new RxBusSubscriber<Boolean>() {
                        @Override
                        protected void onEvent(Boolean aBoolean) {
                            connect(getContext());
                        }
                    });
        }
        connect(mContext);
    }

    public synchronized void onEnd() {
        CommonUtil.unSubscribeSubs(rxNetSubscription);
        onClosed();
    }


    private void log(String msg) {
        if (HljCommon.debug) {
            Log.e("PushSocket", msg);
        }
    }
}
