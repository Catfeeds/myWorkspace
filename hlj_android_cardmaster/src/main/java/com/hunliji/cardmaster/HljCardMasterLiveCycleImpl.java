package com.hunliji.cardmaster;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljcommonlibrary.view_tracker.HljViewTracker;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.hljtrackerlibrary.HljTracker;
import com.hunliji.hljtrackerlibrary.TrackerCollection;
import com.tendcloud.tenddata.TCAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luohanlin on 2018/1/25.
 */

public class HljCardMasterLiveCycleImpl implements Application.ActivityLifecycleCallbacks {

    private long startTimeMillis;
    private static List<Activity> activities;
    private static List<String> activityHistories;
    private static HljCardMasterLiveCycleImpl instance;

    private HljCardMasterLiveCycleImpl() {
        if (instance != null) {
            throw new RuntimeException(
                    "Use getInstance() method to get the single instance of this class.");
        }
    }

    public synchronized static HljCardMasterLiveCycleImpl getInstance() {
        if (instance == null) {
            instance = new HljCardMasterLiveCycleImpl();
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
        if (activities == null) {
            activities = new ArrayList<>();
        }
        activities.add(activity);
        try {
            TCAgent.onPageStart(activity, activity.getLocalClassName());
        } catch (Exception e) {
            TCAgent.onError(activity, e);
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        if (activities != null && !activities.isEmpty()) {
            activities.remove(activity);
        }
        try {
            TCAgent.onPageEnd(activity, activity.getLocalClassName());
        } catch (Exception e) {
            TCAgent.onError(activity, e);
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {
        if (!isAppOnForeground()) {
            if (startTimeMillis != 0) {
                User user = UserSession.getInstance()
                        .getUser(activity);
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
