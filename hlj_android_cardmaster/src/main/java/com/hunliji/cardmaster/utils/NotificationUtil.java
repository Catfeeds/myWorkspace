package com.hunliji.cardmaster.utils;

import android.content.Context;

import com.hunliji.cardmaster.api.CommonApi;
import com.hunliji.hljcardcustomerlibrary.models.UserGift;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljcommonlibrary.models.realm.Notification;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljhttplibrary.authorization.UserSession;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.Sort;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by wangtao on 2017/11/29.
 */

public enum NotificationUtil {
    INSTANCE;
    private int count;
    private static WeakReference<Context> contextWeakReference;

    private Subscription notificationSubscription;

    private Integer[] notificationTypes = {Notification.NotificationType.SIGN, Notification
            .NotificationType.GIFT, Notification.NotificationType.RECV_INSURANCE};

    private void getNewsCount(long userId) {
        Realm realm = Realm.getDefaultInstance();
        int newCount = (int) realm.where(Notification.class)
                .equalTo("userId", userId)
                .notEqualTo("status", 2)
                .in("notifyType", notificationTypes)
                .count();
        realm.close();
        if (count != newCount) {
            count = newCount;
            RxBus.getDefault()
                    .post(new RxEvent(RxEvent.RxEventType.NOTIFICATION_NEW_COUNT_CHANGE, newCount));
        }
    }

    /**
     * 直接获取当前未读通知条数
     *
     * @return
     */
    public int getCount() {
        return count;
    }


    public void logout() {
        CommonUtil.unSubscribeSubs(notificationSubscription);
        if (count > 0) {
            count = 0;
            RxBus.getDefault()
                    .post(new RxEvent(RxEvent.RxEventType.NOTIFICATION_NEW_COUNT_CHANGE, 0));
        }
    }

    /**
     * 获取最新通知消息列表，可以在任何地方需要更新最新通知条数或者列表的地方调用
     * 最核心被调用的地方是个推推送婚礼纪消息过来后发起通知列表更新，取到对应的通知消息详情
     *
     * @param userId
     */
    public void getNewNotifications(Context context, long userId) {
        getNewsCount(userId);
        getNotifications(context, userId);
    }

    /**
     * 根据通知数据库最后一条通知id获取通知更新
     * <p>
     * 更新成功如果有新消息设置 isNewMsg 标志 和 消息数
     * 并且发送消息通知
     *
     * @param userId 用户id
     */

    private void getNotifications(Context context, final long userId) {
        contextWeakReference = new WeakReference<>(context.getApplicationContext());
        if (notificationSubscription != null && !notificationSubscription.isUnsubscribed()) {
            return;
        }
        notificationSubscription = Observable.create(new Observable.OnSubscribe<Long>() {
            @Override
            public void call(Subscriber<? super Long> subscriber) {
                long lastId = 0;
                Realm realm = Realm.getDefaultInstance();
                Number maxId = realm.where(Notification.class)
                        .equalTo("userId", userId)
                        .max("id");
                if (maxId != null) {
                    lastId = maxId.longValue();
                }
                realm.close();
                subscriber.onNext(lastId);
                subscriber.onCompleted();
            }
        })
                .onErrorReturn(new Func1<Throwable, Long>() {
                    @Override
                    public Long call(Throwable throwable) {
                        return 0L;
                    }
                })
                .subscribeOn(Schedulers.io())
                .concatMap(new Func1<Long, Observable<List<Notification>>>() {
                    @Override
                    public Observable<List<Notification>> call(Long lastId) {
                        //从服务器获取通知
                        return CommonApi.getNotifications(lastId);
                    }
                })
                .concatMap(new Func1<List<Notification>, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(final List<Notification> notifications) {
                        return Observable.create(new Observable.OnSubscribe<Boolean>() {
                            @Override
                            public void call(Subscriber<? super Boolean> subscriber) {
                                //通知为空 没有新通知 结束
                                if (notifications == null || notifications.isEmpty()) {
                                    subscriber.onNext(false);
                                    subscriber.onCompleted();
                                    return;
                                }
                                onNotificationEvent(notifications, userId);
                                //获取到新通知 对数据进行转换 存入数据库
                                for (Notification notification : notifications) {
                                    notification.onRealmChange(userId);
                                }
                                Realm realm = Realm.getDefaultInstance();
                                realm.beginTransaction();
                                realm.copyToRealmOrUpdate(notifications);
                                realm.commitTransaction();
                                realm.close();
                                subscriber.onNext(true);
                                subscriber.onCompleted();

                            }
                        })
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread());
                    }

                })
                .timeout(20, TimeUnit.SECONDS)
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {
                        RxBus.getDefault()
                                .post(new RxEvent(RxEvent.RxEventType.NEW_NOTIFICATION,
                                        getCount()));
                    }

                    @Override
                    public void onError(Throwable e) {
                        RxBus.getDefault()
                                .post(new RxEvent(RxEvent.RxEventType.NEW_NOTIFICATION, -1));
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Boolean b) {
                        //判断用户是否一致
                        User user = null;
                        if (contextWeakReference != null && contextWeakReference.get() != null) {
                            user = UserSession.getInstance()
                                    .getUser(contextWeakReference.get());
                        }
                        if (user == null || user.getId() != userId) {
                            return;
                        }
                        if (b) {
                            // 取完所有通知后，更新未读条数
                            getNewsCount(userId);
                        }
                    }
                });
    }

    /**
     * 收到通知后需要立即进行处理的操作在这里筛选和发送Event事件
     *
     * @param notifications
     */
    private void onNotificationEvent(List<Notification> notifications, long userId) {
        boolean hasCashGiftNotice = false;
        boolean hasCardGiftNotice = false;
        for (int i = notifications.size() - 1; i >= 0; i--) {
            Notification notification = notifications.get(i);
            if (notification.getStatus() == 2) {
                continue;
            }
            if (!hasCashGiftNotice && notification.getAction()
                    .equals(Notification.NotificationAction.RECV_CASH_GIFT)) {
                // 礼金，推送消息，显示红包
                hasCashGiftNotice = true;
                GiftUtil.INSTANCE.showCardCashGiftOpenDlg(UserGift.TYPE_CASH, userId);
            }
            if (!hasCardGiftNotice && notification.getAction()
                    .equals(Notification.NotificationAction.RECV_CARD_GIFT)) {
                // 礼物，推送消息，显示红包
                hasCardGiftNotice = true;
                GiftUtil.INSTANCE.showCardCashGiftOpenDlg(UserGift.TYPE_GIFT, userId);
            }
            if (hasCashGiftNotice &&
                    hasCardGiftNotice) {
                return;
            }
        }

        if (!hasCardGiftNotice && !hasCashGiftNotice) {
            checkOldNoticeForGift(userId);
        }

    }

    /**
     * 如果新消息全部检查完都没有礼物礼金的消息，则去检查旧的消息
     */
    private void checkOldNoticeForGift(final long userId) {
        final Realm realm = Realm.getDefaultInstance();
        Notification notification = realm.where(Notification.class)
                .equalTo("userId", userId)
                .notEqualTo("status", 2)
                .beginGroup()
                .equalTo("action", Notification.NotificationAction.RECV_CASH_GIFT)
                .or()
                .equalTo("action", Notification.NotificationAction.RECV_CARD_GIFT)
                .endGroup()
                .findAllSorted("id", Sort.ASCENDING).first(null);
        if (notification!=null) {
            int type = notification.getAction()
                    .equals(Notification.NotificationAction.RECV_CARD_GIFT) ? UserGift.TYPE_GIFT
                    : UserGift.TYPE_CASH;
            GiftUtil.INSTANCE.showCardCashGiftOpenDlg(type, userId);
        }
        realm.close();
    }


}
