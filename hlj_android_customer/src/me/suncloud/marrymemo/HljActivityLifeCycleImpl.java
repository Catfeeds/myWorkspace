package me.suncloud.marrymemo;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.hunliji.hljcommonlibrary.utils.SystemNotificationUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljViewTracker;
import com.hunliji.hljpushlibrary.websocket.PushSocket;
import com.hunliji.hljtrackerlibrary.HljTracker;
import com.hunliji.hljtrackerlibrary.TrackerCollection;

import java.util.ArrayList;
import java.util.List;

import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.util.GoogleAnalyticsUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.SplashActivity;
import me.suncloud.marrymemo.view.ad.LaunchAdActivity;
import me.suncloud.marrymemo.view.chat.WSCustomerChatActivity;
import me.suncloud.marrymemo.view.notification.MessageHomeActivity;

/**
 * Created by luohanlin on 2017/12/15.
 */

public class HljActivityLifeCycleImpl implements Application.ActivityLifecycleCallbacks {

    private long startTimeMillis;
    private long resumeTimeMillis;
    private static List<Activity> activities;
    private static List<String> activityHistories;
    private long foregroundTimeMillis;

    private static HljActivityLifeCycleImpl instance;

    private HljActivityLifeCycleImpl() {
        if (instance != null) {
            throw new RuntimeException(
                    "Use getInstance() method to get the single instance of this class.");
        }
    }

    public synchronized static HljActivityLifeCycleImpl getInstance() {
        if (instance == null) {
            instance = new HljActivityLifeCycleImpl();
        }

        return instance;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        if (activityHistories == null) {
            activityHistories = new ArrayList<>();
        }
        if (savedInstanceState == null) {
            activityHistories.add(activity.getClass()
                    .getName() + "#" + activity.getTitle());
        }
        if (savedInstanceState == null) {
            HljViewTracker.fireActivityJumpEvent(activity);
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (startTimeMillis == 0) {
            startTimeMillis = System.currentTimeMillis();
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (resumeTimeMillis == 0) {
            resumeTimeMillis = System.currentTimeMillis();
        }
        if (activities == null) {
            activities = new ArrayList<>();
        }
        activities.add(activity);
        try {
            Util.onResume(activity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        PushSocket.INSTANCE.connect(activity);

        if (activity instanceof SplashActivity) {
            foregroundTimeMillis = 0;
        }
        if (foregroundTimeMillis > 0) {
            if (System.currentTimeMillis() - foregroundTimeMillis > LaunchAdActivity
                    .SHOW_DURATION) {
                activity.startActivity(new Intent(activity, LaunchAdActivity.class));
                activity.overridePendingTransition(0, 0);
            }
            foregroundTimeMillis = 0;
        }
        GoogleAnalyticsUtil.getInstance(activity)
                .sendScreen(activity, HljViewTracker.getCurrentPageName(activity));
        messageNotificationDialog(activity);
    }

    private void messageNotificationDialog(Activity activity) {
        try {
            if (activityHistories == null || activityHistories.isEmpty()) {
                return;
            }
            for (int i = activityHistories.size() - 1; i >= 1; i--) {
                if (activityHistories.get(i)
                        .startsWith(WSCustomerChatActivity.class.getName())) {
                    String resumeActivityName = activityHistories.get(i - 1);
                    if (!TextUtils.isEmpty(resumeActivityName) && !resumeActivityName.startsWith(
                            MessageHomeActivity.class.getName()) && resumeActivityName.startsWith(
                            activity.getClass()
                                    .getName())) {
                        Dialog dialog = SystemNotificationUtil.getNotificationOpenDlgOfPrefName(
                                activity,
                                "",
                                activity.getString(R.string.label_not_open_message_notification),
                                activity.getString(R.string.label_soon_open_message_notification),
                                0);
                        if (dialog != null) {
                            dialog.show();
                        }
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        if (activities != null && !activities.isEmpty()) {
            activities.remove(activity);
        }
        try {
            Util.onPause(activity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (resumeTimeMillis > 0) {
            HljViewTracker.fireActivityPageOutEvent(activity, resumeTimeMillis);
            resumeTimeMillis = 0;
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {
        if (!isAppOnForeground()) {
            foregroundTimeMillis = System.currentTimeMillis();
            if (startTimeMillis != 0) {
                User user = Session.getInstance()
                        .getCurrentUser(activity);
                new HljTracker.Builder(activity).eventableId(user == null ? null : user.getId())
                        .eventableType("User")
                        .action("enter_background")
                        .additional(String.valueOf((System.currentTimeMillis() - startTimeMillis)
                                / 1000))
                        .build()
                        .add();
                startTimeMillis = 0;
            }
            TrackerCollection.INSTANCE.sendTracker(true);
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        if (activityHistories == null || activityHistories.isEmpty()) {
            return;
        }
        for (int i = activityHistories.size() - 1; i >= 0; i--) {
            if (activityHistories.get(i)
                    .startsWith(activity.getClass()
                            .getName())) {
                activityHistories.remove(i);
                break;
            }
        }
    }

    public String getActivityHistoryString() {
        if (activityHistories == null || activityHistories.size() < 2) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        int lastIndex = activityHistories.size() - 2;
        for (int i = 0; i <= Math.min(lastIndex, 4); i++) {
            if (i > 0) {
                stringBuilder.append("#");
            }
            stringBuilder.append(activityHistories.get(lastIndex - i));
        }
        return stringBuilder.toString();
    }

    private boolean isAppOnForeground() {
        return activities != null && !activities.isEmpty();
    }

    public Activity getCurrentActivity() {
        if (activities != null && !activities.isEmpty()) {
            return activities.get(activities.size() - 1);
        }
        return null;
    }
}
