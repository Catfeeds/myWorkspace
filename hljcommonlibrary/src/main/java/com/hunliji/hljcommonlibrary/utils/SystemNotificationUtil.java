package com.hunliji.hljcommonlibrary.utils;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;

import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.R;
import com.hunliji.hljcommonlibrary.models.SystemNotificationData;
import com.hunliji.hljcommonlibrary.views.widgets.QueueDialog;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;


/**
 * Created by luohanlin on 2017/10/10.
 */

public enum SystemNotificationUtil {

    INSTANCE;

    public static final int WS_CHAT_INDEX = 4;

    public static final String ASG_NOTIFY_ID = "notify_id";
    public static final String ASG_CHAT_USER_ID = "chat_user_id";
    public static final String ASG_INDEX = "index";
    public static final String ASG_TASK_ID = "task_id";
    public static final String ASG_MESSAGE_ID = "message_id";


    public static final String ASG_MSG = "msg";

    private SparseArray<SystemNotificationData> notificationDataArray;
    private SparseArray<SystemNotificationData> tempNotificationDataArray;
    private long lastNoticeTime;
    private WeakReference<Context> contextWeakReference;
    private Subscription timeSubscription;


    public void readNotification(Context context, int notifyId) {
        try {
            if (notificationDataArray != null) {
                notificationDataArray.remove(notifyId);
            }
            if (tempNotificationDataArray != null) {
                tempNotificationDataArray.remove(notifyId);
            }
            NotificationManagerCompat.from(context)
                    .cancel(notifyId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addNotification(Context context, SystemNotificationData data) {
        if (context == null || data == null) {
            return;
        }
        contextWeakReference = new WeakReference<>(context.getApplicationContext());

        if (CommonUtil.isUnsubscribed(timeSubscription)) {
            showNotification(context, data);
            timeSubscription = Observable.timer(1, TimeUnit.SECONDS)
                    .subscribe(new Subscriber<Long>() {
                        @Override
                        public void onCompleted() {
                            if (getContext() == null) {
                                return;
                            }
                            if (tempNotificationDataArray == null || tempNotificationDataArray
                                    .size() == 0) {
                                return;
                            }
                            Context context = getContext();
                            for (int i = 0, size = tempNotificationDataArray.size(); i < size;
                                 i++) {
                                SystemNotificationData notificationBarData =
                                        tempNotificationDataArray.valueAt(
                                        i);
                                showNotification(context, notificationBarData);
                            }
                            tempNotificationDataArray.clear();
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(Long aLong) {

                        }
                    });
        } else {
            if (tempNotificationDataArray == null) {
                tempNotificationDataArray = new SparseArray<>();
            }
            SystemNotificationData lastData = tempNotificationDataArray.get(data.getNotifyId());
            if (lastData != null) {
                data.setGroupCount(lastData.getGroupCount() + 1);
            }
            tempNotificationDataArray.put(data.getNotifyId(), data);
        }
    }

    private void showNotification(Context context, SystemNotificationData notificationData) {
        try {
            if (notificationDataArray == null) {
                notificationDataArray = new SparseArray<>();
            }
            SystemNotificationData lastData = notificationDataArray.get(notificationData
                    .getNotifyId());
            if (lastData != null) {
                notificationData.setGroupCount(lastData.getGroupCount() + 1);
            }
            notificationDataArray.put(notificationData.getNotifyId(), notificationData);
            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
            if (!managerCompat.areNotificationsEnabled()) {
                return;
            }
            NotificationManagerCompat.from(context)
                    .notify(notificationData.getNotifyId(),
                            buildNotification(context, notificationData));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Context getContext() {
        if (contextWeakReference == null) {
            return null;
        }
        return contextWeakReference.get();
    }

    private Notification buildNotification(
            Context context, SystemNotificationData data) {
        String title = data.getTitle();
        if (data.getGroupCount() > 1) {
            title = title + context.getString(R.string.label_system_notification_count,
                    data.getGroupCount());
        }
        Notification notification;
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                data.getNotifyId(),
                data.getIntent(),
                PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentIntent(
                pendingIntent)
                .setNumber(data.getGroupCount())
                .setAutoCancel(true)
                .setTicker(data.getContent())
                .setContentTitle(title)
                .setContentText(data.getContent())
                .setStyle(new NotificationCompat.BigTextStyle().bigText(data.getContent()))
                .setPriority(data.getmPriority());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setVisibility(Notification.VISIBILITY_PUBLIC);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            builder.setSmallIcon(data.getSmallIconRes())
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                            data.getLargeIconRes()));
        } else {
            builder.setSmallIcon(data.getLargeIconRes());
        }
        notification = builder.build();
        if (System.currentTimeMillis() - lastNoticeTime > 10000) {
            //10秒内不在提示声音震动
            lastNoticeTime = System.currentTimeMillis();
            notification.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE |
                    Notification.DEFAULT_LIGHTS;
        }
        return notification;
    }

    /**
     * 检测是否显示通知开关提示，如果被关闭并且没有显示过，提示打开并跳转
     *
     * @param context
     * @return
     */
    public static boolean isNeedOpenNotificationForPref(Context context, String prefName) {
        boolean enable = NotificationManagerCompat.from(context)
                .areNotificationsEnabled();
        if (!enable) {
            boolean showed;
            if (TextUtils.isEmpty(prefName)) {
                showed = false;
            } else {
                showed = context.getSharedPreferences(HljCommon.FileNames.PREF_FILE,
                        Context.MODE_PRIVATE)
                        .getBoolean(prefName, false);
            }
            // 没有显示过，需要提示，显示过就不提示了
            return !showed;
        } else {
            return false;
        }
    }

    public static void goSystemNotificationSetting(Context context) {
        // 进入设置系统应用权限界面
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        context.startActivity(intent);
    }

    /**
     * 只显示一次的，记录位置的通知开关弹窗
     *
     * @param context
     * @param prefName
     * @param title
     * @param msg
     * @return
     */
    public static QueueDialog getNotificationOpenDlgOfPrefName(
            final Context context, final String prefName, String title, String msg, int iconResId) {
        if (isNeedOpenNotificationForPref(context, prefName)) {
            final QueueDialog notiOpenDlg = DialogUtil.createNoticeOpenDlg(context,
                    title,
                    msg,
                    "立即开启",
                    iconResId,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            goSystemNotificationSetting(context);
                            if (!TextUtils.isEmpty(prefName)) {
                                context.getSharedPreferences(HljCommon.FileNames.PREF_FILE,
                                        Context.MODE_PRIVATE)
                                        .edit()
                                        .putBoolean(prefName, true)
                                        .apply();
                            }
                        }
                    },
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!TextUtils.isEmpty(prefName)) {
                                context.getSharedPreferences(HljCommon.FileNames.PREF_FILE,
                                        Context.MODE_PRIVATE)
                                        .edit()
                                        .putBoolean(prefName, true)
                                        .apply();
                            }
                        }
                    });
            return notiOpenDlg;
        } else {
            return null;
        }
    }
}
