package com.hunliji.hljkefulibrary;

import android.content.Context;
import android.text.TextUtils;

import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljhttplibrary.entities.HljApiException;
import com.hunliji.hljkefulibrary.api.KeFuApi;
import com.hunliji.hljkefulibrary.moudles.HxUser;
import com.hunliji.hljkefulibrary.utils.KeFuSession;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.chat.ChatManager;
import com.hyphenate.chat.Message;
import com.hyphenate.helpdesk.callback.Callback;
import com.hyphenate.util.EasyUtils;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by wangtao on 2017/10/18.
 */

public class HljKeFu {

    public static final String APP_KEY = "hunliji#hunliji";//环信关联的“AppKey”
    public static final String TENANT_ID = "4591"; //环信“租户ID”

    public static synchronized void init(
            final Context context, boolean debug, final KeFuMessageBackgroundCallback callback) {
        ChatClient.Options options = new ChatClient.Options();
        options.setAppkey(APP_KEY);
        options.setTenantId(TENANT_ID);
        options.setConsoleLog(debug);
        // Kefu SDK 初始化
        if (!ChatClient.getInstance()
                .init(context, options)) {
            return;
        }
        ChatClient.getInstance()
                .chatManager()
                .addMessageListener(new ChatManager.MessageListener() {
                    @Override
                    public void onMessage(List<Message> list) {
                        if (CommonUtil.isCollectionEmpty(list)) {
                            return;
                        }
                        if (!EasyUtils.isAppRunningForeground(context)) {
                            callback.onMessageNotifier(list.get(0));
                        } else {
                            RxBus.getDefault()
                                    .post(new RxEvent(RxEvent.RxEventType.EM_MESSAGE, null));
                        }
                    }

                    @Override
                    public void onCmdMessage(List<Message> list) {

                    }

                    @Override
                    public void onMessageStatusUpdate() {

                    }

                    @Override
                    public void onMessageSent() {

                    }
                });
    }

    public static synchronized void logout(Context context) {
        if (ChatClient.getInstance()
                .isLoggedInBefore()) {
            ChatClient.getInstance()
                    .logout(true, null);
        }
        KeFuSession.getInstance()
                .logout(context);
    }


    public static synchronized Observable<Boolean> loginObb(Context context) {
        return Observable.just(context)
                .concatMap(new Func1<Context, Observable<? extends HxUser>>() {
                    @Override
                    public Observable<? extends HxUser> call(Context context) {
                        HxUser user = KeFuSession.getInstance()
                                .getUser(context);
                        if (user == null || TextUtils.isEmpty(user.getUserName()) || TextUtils
                                .isEmpty(
                                user.getUserName())) {
                            return KeFuApi.getHxUserObb(context);
                        }
                        return Observable.just(user);
                    }
                })
                .filter(new Func1<HxUser, Boolean>() {
                    @Override
                    public Boolean call(HxUser hxUser) {
                        if (hxUser == null || TextUtils.isEmpty(hxUser.getUserName()) ||
                                TextUtils.isEmpty(
                                hxUser.getUserName())) {
                            throw new HljApiException("环信用户异常");
                        }
                        if (ChatClient.getInstance()
                                .isLoggedInBefore()) {
                            if (ChatClient.getInstance()
                                    .currentUserName()
                                    .equals(hxUser.getUserName())) {
                                return false;
                            }
                        }
                        return true;
                    }
                })
                .concatMap(new Func1<HxUser, Observable<HxUser>>() {
                    @Override
                    public Observable<HxUser> call(final HxUser hxUser) {
                        if (ChatClient.getInstance()
                                .isLoggedInBefore()) {
                            return Observable.create(new Observable.OnSubscribe<HxUser>() {
                                @Override
                                public void call(final Subscriber<? super HxUser> subscriber) {
                                    ChatClient.getInstance()
                                            .logout(true, new Callback() {
                                                @Override
                                                public void onSuccess() {
                                                    subscriber.onNext(hxUser);
                                                    subscriber.onCompleted();
                                                }

                                                @Override
                                                public void onError(int i, String s) {
                                                    subscriber.onError(new HljApiException
                                                            ("code:" + i + "  msg:" + s));
                                                }

                                                @Override
                                                public void onProgress(int i, String s) {

                                                }
                                            });
                                }
                            });
                        } else {
                            return Observable.just(hxUser);
                        }
                    }
                })
                .concatMap(new Func1<HxUser, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(final HxUser user) {
                        return Observable.create(new Observable.OnSubscribe<Boolean>() {
                            @Override
                            public void call(final Subscriber<? super Boolean> subscriber) {
                                ChatClient.getInstance()
                                        .login(user.getUserName(),
                                                user.getPassWord(),
                                                new Callback() {
                                                    @Override
                                                    public void onSuccess() {
                                                        subscriber.onCompleted();
                                                    }

                                                    @Override
                                                    public void onError(int i, String s) {
                                                        subscriber.onError(new HljApiException(
                                                                "code:" + i + "  msg:" + s));
                                                    }

                                                    @Override
                                                    public void onProgress(int i, String s) {

                                                    }
                                                });
                            }
                        });
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public interface KeFuMessageBackgroundCallback {
        void onMessageNotifier(Message message);
    }

    public static int getUnreadCount() {
        if (ChatClient.getInstance()
                .isLoggedInBefore()) {
            return ChatClient.getInstance()
                    .chatManager()
                    .getUnreadMsgsCount();
        }
        return 0;
    }
}
