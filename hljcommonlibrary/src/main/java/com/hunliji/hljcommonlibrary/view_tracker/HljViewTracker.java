package com.hunliji.hljcommonlibrary.view_tracker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Looper;
import android.os.MessageQueue;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;

import com.hunliji.hljcommonlibrary.R;
import com.hunliji.hljcommonlibrary.models.realm.Tracker;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.view_tracker.models.MiaoZhenTracker;
import com.hunliji.hljcommonlibrary.views.activities.HljTrackedActivity;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by luohanlin on 17/03/2017.
 * 新版的页面统计，全局单例，
 * 使用enum方式实现单例模式，与传统的单例用法基本一致
 */

public enum HljViewTracker {
    @SuppressLint("StaticFieldLeak")
    INSTANCE;

    public static class EVENT_TYPE {
        public static final String EVENT_TYPE_ELEMENT_VIEW = "element_view";
        public static final String EVENT_TYPE_ELEMENT_CLICK = "element_hit";
        public static final String EVENT_TYPE_ELEMENT_JUMP = "page_jump";
        public static final String EVENT_TYPE_ELEMENT_PAGE_OUT = "page_view";
    }

    public static final String TAG = HljViewTracker.class.getSimpleName();

    private static long currentUserId;
    private static long currentCityId;
    private static String lastActivityName;
    private static String currentActivityName;
    private static String currentActivityViewIDString = null;
    private static LinkedFragment currentLinkedFragment;

    private static VTMetaData currentActivityData;

    private static Map<String, Boolean> lastVisibleMap;
    private static Map<String, IdleHandlerSub> idleHandlerSubMap;

    private static String defaultActivtyTitle;

    MessageQueue.IdleHandler idleHandler = new MessageQueue.IdleHandler() {
        @Override
        public boolean queueIdle() {
            postNewTrackIdleHandlerEvent();
            return true;
        }
    };

    /**
     * 初始化曝光统计系统
     */
    public void initHljViewTracker() {
        Looper.myQueue()
                .addIdleHandler(idleHandler);
    }

    public void postNewTrackIdleHandlerEvent() {
        //        Log.d("POST_NEW_IDLE_EVENT", "------------------------------" + System
        // .currentTimeMillis());
        RxBus.getDefault()
                .post(new IdleHandlerEvent());
    }

    public void setCurrentActivity(
            Activity activity,
            String currentPageName,
            String getLastPageName,
            VTMetaData pageData) {
        Log.i(TAG,
                "currentPageName:" + currentPageName + "\n" + "getLastPageName:" + getLastPageName);
        HljViewTracker.currentActivityViewIDString = viewIdString(activity.getWindow()
                .getDecorView());
        HljViewTracker.lastActivityName = getLastPageName;
        HljViewTracker.currentActivityName = currentPageName;
        HljViewTracker.currentActivityData = pageData;
    }

    public void setCurrentFragment(Fragment fragment, String pageName) {
        LinkedFragment linkedFragment = new LinkedFragment(fragment, pageName);

        Fragment parentFragment = fragment.getParentFragment();
        while (parentFragment != null) {
            if (parentFragment instanceof TrackedFragmentInterface) {
                LinkedFragment parentLinkedFragment = new LinkedFragment(parentFragment,
                        ((TrackedFragmentInterface) parentFragment).getFragmentPageTagName());
                linkedFragment.setLinkedParent(parentLinkedFragment);
            } else {
                LinkedFragment parentLinkedFragment = new LinkedFragment(parentFragment);
                linkedFragment.setLinkedParent(parentLinkedFragment);
            }
            parentFragment = parentFragment.getParentFragment();
        }

        HljViewTracker.currentLinkedFragment = linkedFragment;
    }

    public void clearCurrentFragment() {
        HljViewTracker.currentLinkedFragment = null;
    }

    public void setCurrentUserId(long currentUserId) {
        HljViewTracker.currentUserId = currentUserId;
    }

    public void setCurrentCityId(long currentCityId) {
        HljViewTracker.currentCityId = currentCityId;
    }

    /**
     * 触发了一个视图的可见事件，记录、发送统计数据
     *
     * @param view
     */
    public static void fireViewVisibleEvent(View view) {
        ViewTraceData data = getViewTraceData(view);
        if (data != null) {
            data.setEventType(EVENT_TYPE.EVENT_TYPE_ELEMENT_VIEW);
            String traceString = generateTraceJson(data);
            RxBus.getDefault()
                    .post(new Tracker(traceString, 2));
            if (!TextUtils.isEmpty(data.getMiaoZhenImpUrl())) {
                RxBus.getDefault()
                        .post(new MiaoZhenTracker(data.getMiaoZhenImpUrl()));
            }
            Log.d(TAG, traceString);
        }
    }

    public static String getPageHistoryName() {
        String pageHistory = "currentActivityName:" + currentActivityName + " " +
                "//lastActivityName:" + lastActivityName;
        if (currentLinkedFragment != null && currentLinkedFragment.getFragment() != null) {
            pageHistory += " //currentFragment:" + currentLinkedFragment.getFragment()
                    .getClass()
                    .getSimpleName();
        }
        return pageHistory;
    }

    public static void fireViewClickEvent(View view) {
        if (view.getTag(R.id.hlj_tracker_tag_id) == null) {
            return;
        }
        long l = System.currentTimeMillis();
        if (view.getTag(R.id.hlj_last_track_time) != null && l - (Long) view.getTag(R.id
                .hlj_last_track_time) < 50) {
            return;
        }
        view.setTag(R.id.hlj_last_track_time, l);


        ViewTraceData data = getViewTraceData(view);
        if (data != null) {
            data.setEventType(EVENT_TYPE.EVENT_TYPE_ELEMENT_CLICK);
            String traceString = generateTraceJson(data);
            RxBus.getDefault()
                    .post(new Tracker(traceString, 2));
            if (!TextUtils.isEmpty(data.getMiaoZhenClickUrl())) {
                RxBus.getDefault()
                        .post(new MiaoZhenTracker(data.getMiaoZhenClickUrl()));
            }
            Log.d(TAG, traceString);
        }
    }


    public static void fireFragmentPageJumpEvent(
            Activity activity, RefreshFragment trackedFragment) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("event_type", EVENT_TYPE.EVENT_TYPE_ELEMENT_JUMP);
            jsonObject.put("user_id", currentUserId);
            jsonObject.put("last_page_name", getCurrentPageName(activity));
            jsonObject.put("page_name", trackedFragment.fragmentPageName());
            VTMetaData data = trackedFragment.getPageTrackData();
            if (data != null) {
                jsonObject.put("page_data", data.toJson());
            }
            jsonObject.put("time_stamp", new DateTime().toString("yyyy-MM-dd'T'HH:mm:ssZZ"));
            jsonObject.put("user_city", currentCityId);
            String traceString = jsonObject.toString();
            RxBus.getDefault()
                    .post(new Tracker(jsonObject.toString(), 2));
            Log.d(TAG, traceString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void fireActivityJumpEvent(Activity activity) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("event_type", EVENT_TYPE.EVENT_TYPE_ELEMENT_JUMP);
            jsonObject.put("user_id", currentUserId);
            jsonObject.put("page_name", getCurrentPageName(activity));
            VTMetaData data = getCurrentPageTrackData(activity);
            if (data != null) {
                jsonObject.put("page_data", data.toJson());
            }
            if (activity instanceof TrackedActivityInterface) {
                jsonObject.put("last_page_name",
                        ((TrackedActivityInterface) activity).getLastPageName());
            }
            jsonObject.put("time_stamp", new DateTime().toString("yyyy-MM-dd'T'HH:mm:ssZZ"));
            jsonObject.put("user_city", currentCityId);
            String traceString = jsonObject.toString();
            RxBus.getDefault()
                    .post(new Tracker(jsonObject.toString(), 2));
            Log.d(TAG, traceString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void fireActivityPageOutEvent(Activity activity, long resumeTimeMillis) {
        long duration = System.currentTimeMillis() - resumeTimeMillis;
        if (duration > 300) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("event_type", EVENT_TYPE.EVENT_TYPE_ELEMENT_PAGE_OUT);
                jsonObject.put("user_id", currentUserId);
                jsonObject.put("page_name", getActivityPageName(activity));
                VTMetaData data = getActivityPageTrackData(activity);
                if (data != null) {
                    jsonObject.put("page_data", data.toJson());
                }
                jsonObject.put("duration", duration);
                jsonObject.put("time_stamp", new DateTime().toString("yyyy-MM-dd'T'HH:mm:ssZZ"));
                jsonObject.put("user_city", currentCityId);
                String traceString = jsonObject.toString();
                RxBus.getDefault()
                        .post(new Tracker(jsonObject.toString(), 2));
                Log.d(TAG, traceString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static void fireActivityPageOutEvent(RefreshFragment fragment, long resumeTimeMillis) {
        if (TextUtils.isEmpty(fragment.getFragmentPageTagName())) {
            return;
        }
        long duration = System.currentTimeMillis() - resumeTimeMillis;
        if (duration > 300) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("event_type", EVENT_TYPE.EVENT_TYPE_ELEMENT_PAGE_OUT);
                jsonObject.put("user_id", currentUserId);
                jsonObject.put("page_name", fragment.getFragmentPageTagName());
                VTMetaData data = fragment.getPageTrackData();
                if (data != null) {
                    jsonObject.put("page_data", data.toJson());
                }
                jsonObject.put("duration", duration);
                jsonObject.put("time_stamp", new DateTime().toString("yyyy-MM-dd'T'HH:mm:ssZZ"));
                jsonObject.put("user_city", currentCityId);
                String traceString = jsonObject.toString();
                RxBus.getDefault()
                        .post(new Tracker(jsonObject.toString(), 2));
                Log.d(TAG, traceString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 追踪一个视图，并且给这个视图设置相应的追踪数据，用于曝光统计
     *
     * @param view
     */
    public void trackView(final View view) {
        registerIdleHandlerCallBack(view);
    }

    public void clearView(View view) {
        removeIdleHandlerCallBack(view);
    }

    public void registerIdleHandlerCallBack(View view) {
        if (idleHandlerSubMap == null) {
            idleHandlerSubMap = new HashMap<>();
        }
        if (idleHandlerSubMap.get(viewIdString(view)) == null || idleHandlerSubMap.get(viewIdString(
                view))
                .isUnsubscribed()) {
            IdleHandlerSub sub = new IdleHandlerSub(view);
            idleHandlerSubMap.put(viewIdString(view), sub);
            RxBus.getDefault()
                    .toObservable(IdleHandlerEvent.class)
                    .subscribe(sub);
        }
    }

    public void removeIdleHandlerCallBack(View view) {
        if (idleHandlerSubMap == null) {
            return;
        }
        CommonUtil.unSubscribeSubs(idleHandlerSubMap.get(viewIdString(view)));
    }

    /**
     * 判断view是否在屏幕上可见
     *
     * @param view
     * @return
     */
    private boolean isViewVisible(View view) {
        Rect rect = new Rect();
        view.getGlobalVisibleRect(rect);
        Rect windowRect = new Rect();
        view.getWindowVisibleDisplayFrame(windowRect);

        boolean isCurrentPage;
        if (currentLinkedFragment == null) {
            // 没有fragment，直接判断activity即可
            isCurrentPage = isBelongToCurrentActivity(view);
        } else {
            // 有fragment，必须判断两个都相等
            isCurrentPage = isBelongToCurrentFragment(view);
        }
        boolean visibility = view.getVisibility() == View.VISIBLE;

        return visibility && isCurrentPage && Rect.intersects(rect,
                windowRect) && view.getWindowToken() != null;
    }

    private boolean isBelongToCurrentActivityWithContext(View view) {
        try {
            if (view.getContext() instanceof Activity) {
                View activityView = ((Activity) view.getContext()).getWindow()
                        .getDecorView();
                String currentViewIDString = viewIdString(activityView);
                if (currentViewIDString.equals(currentActivityViewIDString)) {
                    return true;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断view是否位于当前的Activity中
     *
     * @param view
     * @return
     */
    private boolean isBelongToCurrentActivity(View view) {
        boolean isBelongToCurrentActivity = false;
        if (isBelongToCurrentActivityWithContext(view)) {
            return true;
        }
        View current = view;
        while (current != null) {
            String currentViewIDString = viewIdString(current);
            if (currentViewIDString.equals(currentActivityViewIDString)) {
                isBelongToCurrentActivity = true;
            }

            ViewParent parent = current.getParent();
            if (parent == null) {
                break;
            }

            if (parent instanceof View) {
                current = (View) parent;
            } else {
                break;
            }
        }

        return isBelongToCurrentActivity;
    }

    /**
     * 判断View是否同时存在于当前的Activity和Fragment里面
     *
     * @param view
     * @return
     */
    private boolean isBelongToCurrentFragment(View view) {
        boolean isBelongToCurrentActivity = isBelongToCurrentActivityWithContext(view);
        boolean isBelongToCurrentFragment = false;
        boolean isBelongFragment = false;

        View current = view;
        while (current != null) {
            String currentViewIDString = viewIdString(current);
            if (currentViewIDString.equals(currentActivityViewIDString)) {
                isBelongToCurrentActivity = true;
            }
            boolean isFragmentView = current.getTag(R.id.hlj_fragment_root_view) != null;
            if (!isBelongFragment && isFragmentView) {
                isBelongFragment = true;
            }
            if (isFragmentView) {
                LinkedFragment fragment = currentLinkedFragment;
                while (fragment != null) {
                    if (currentViewIDString.equals(fragment.getIdString())) {
                        isBelongToCurrentFragment = true;
                        break;
                    }
                    fragment = fragment.getLinkedParent();
                }
            }
            ViewParent parent = current.getParent();
            if (parent == null) {
                break;
            }

            if (parent instanceof View) {
                current = (View) parent;
            } else {
                break;
            }
        }

        return (!isBelongFragment || isBelongToCurrentFragment) && isBelongToCurrentActivity;
    }

    /**
     * 计算得到View视图对象唯一标识
     *
     * @param view
     * @return
     */
    public static String viewIdString(View view) {
        return Integer.toHexString(System.identityHashCode(view));
    }


    public static ViewTraceData getViewTraceData(View view) {
        if (view.getTag(R.id.hlj_tracker_tag_id) == null) {
            return null;
        }
        ViewTraceData data = null;
        try {
            data = (ViewTraceData) view.getTag(R.id.hlj_tracker_tag_id);
            if (data != null) {
                if (TextUtils.isEmpty(data.getParentTagName())) {
                    data.setParentTagName(getParentTagName(view));
                }
                if (TextUtils.isEmpty(data.getBelongPageName())) {
                    ViewTracePage page = getBelongPage(view);
                    data.setBelongPageName(page.pageName);
                    data.setPageData(page.pageData);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * 获取当前view所属页面名字，包括activity或被设置页面名的fragment
     *
     * @return
     */

    public static ViewTracePage getBelongPage(View view) {
        View current = view;
        while (current != null) {
            String viewPageName = (String) current.getTag(R.id.hlj_tracker_view_page_name);
            VTMetaData viewPageData = (VTMetaData) current.getTag(R.id.hlj_tracker_view_page_data);
            if (!TextUtils.isEmpty(viewPageName)) {
                return new ViewTracePage(viewPageName, viewPageData);
            }
            boolean isFragmentView = current.getTag(R.id.hlj_fragment_root_view) != null;
            if (currentLinkedFragment != null && isFragmentView) {
                LinkedFragment fragment = currentLinkedFragment;
                while (fragment != null) {
                    if (!TextUtils.isEmpty(fragment.getPageName())) {
                        String currentViewIDString = viewIdString(current);
                        if (currentViewIDString.equals(fragment.getIdString())) {
                            return new ViewTracePage(fragment.getPageName(),
                                    fragment.getPageData());
                        }

                    }
                    fragment = fragment.getLinkedParent();
                }
            }
            ViewParent parent = current.getParent();
            if (parent == null) {
                break;
            }
            if (parent instanceof View) {
                current = (View) parent;
            } else {
                break;
            }
        }
        return new ViewTracePage(currentActivityName, currentActivityData);
    }

    private static class ViewTracePage {

        public ViewTracePage(String pageName, VTMetaData pageData) {
            this.pageName = pageName;
            this.pageData = pageData;
        }

        String pageName;
        VTMetaData pageData;
    }

    public static String getParentTagName(View view) {
        View current = view;
        while (current != null) {
            ViewParent parent = current.getParent();
            if (parent == null || !(parent instanceof View)) {
                return null;
            }
            current = (View) parent;
            String parentTag = (String) current.getTag(R.id.hlj_tracker_parent_tag_id);
            if (!TextUtils.isEmpty(parentTag)) {
                return parentTag;
            }
        }
        return null;
    }

    /**
     * 生成提交时候所有的Json String
     *
     * @param data
     * @return
     */
    public static String generateTraceJson(ViewTraceData data) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("event_type", data.getEventType());
            jsonObject.put("user_id", currentUserId);
            if (!TextUtils.isEmpty(data.getBelongPageName())) {
                jsonObject.put("page_name", data.getBelongPageName());
                jsonObject.put("last_page_name", lastActivityName);
                if (data.getPageData() != null) {
                    jsonObject.put("page_data",
                            data.getPageData()
                                    .toJson());
                }
            } else {
                jsonObject.put("page_name", currentActivityName);
                jsonObject.put("last_page_name", lastActivityName);
                if (currentActivityData != null) {
                    jsonObject.put("page_data", currentActivityData.toJson());
                }
            }
            jsonObject.put("element_parent_tag", data.getParentTagName());
            jsonObject.put("time_stamp", new DateTime().toString("yyyy-MM-dd'T'HH:mm:ssZZ"));
            jsonObject.put("element_tag", data.getTagName());
            if (data.getPosition() >= 0) {
                jsonObject.put("element_tag_position", data.getPosition());
            }
            jsonObject.put("user_city", currentCityId);
            jsonObject.put("element_data",
                    data.getMetaData()
                            .toJson());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    /**
     * 用于给view设置一个监听回调，监听程序的Idle事件
     */
    private class IdleHandlerSub extends RxBusSubscriber<IdleHandlerEvent> {
        WeakReference<View> weakReference;

        IdleHandlerSub(View view) {
            this.weakReference = new WeakReference<>(view);
        }

        @Override
        protected void onEvent(IdleHandlerEvent idleHandlerEvent) {
            if (weakReference == null) {
                unsubscribe();
                return;
            }
            View view = weakReference.get();
            if (view == null || view.getContext() == null || (view.getContext() instanceof
                    Activity && ((Activity) view.getContext()).isFinishing())) {
                unsubscribe();
                return;
            }
            if (lastVisibleMap == null) {
                lastVisibleMap = new HashMap<>();
            }
            boolean isVisible = isViewVisible(view);

            if (lastVisibleMap.get(viewIdString(view)) == null) {
                lastVisibleMap.put(viewIdString(view), isVisible);
                if (isVisible) {
                    fireViewVisibleEvent(view);
                }
            } else {
                boolean lastVisibleState = lastVisibleMap.get(viewIdString(view));
                if (isVisible != lastVisibleState) {
                    lastVisibleMap.put(viewIdString(view), isVisible);
                    if (isVisible) {
                        // 可见状态改变，发出track event
                        fireViewVisibleEvent(view);
                    }
                }
            }
        }
    }

    public static String getCurrentPageName(Activity activity) {
        if (activity == null) {
            return null;
        }
        String pageTag = getFragmentPageTrackTagName(activity);
        if (TextUtils.isEmpty(pageTag)) {
            pageTag = getActivityPageName(activity);
        }
        return pageTag;
    }

    protected static String getActivityPageName(Activity activity) {
        if (activity == null) {
            return null;
        }
        String pageTag = null;
        if (activity instanceof HljTrackedActivity) {
            pageTag = ((HljTrackedActivity) activity).pageTrackTagName2();
        }
        if (TextUtils.isEmpty(pageTag)) {
            if (activity instanceof TrackedActivityInterface) {
                pageTag = ((TrackedActivityInterface) activity).pageTrackTagName();
            }
        }
        if (TextUtils.isEmpty(pageTag)) {
            String title = activity.getTitle()
                    .toString();
            if (!TextUtils.isEmpty(title) && !title.equals(getDefaultTitle(activity))) {
                pageTag = title;
            } else {
                pageTag = activity.getClass()
                        .getSimpleName();
            }
        }
        return pageTag;
    }

    private static String getDefaultTitle(Context context) {
        if (TextUtils.isEmpty(defaultActivtyTitle)) {
            try {
                defaultActivtyTitle = context.getPackageManager()
                        .getApplicationLabel(context.getApplicationInfo())
                        .toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return defaultActivtyTitle;
    }

    protected static String getFragmentPageTrackTagName(Activity activity) {
        if (activity != null && activity.getIntent() != null) {
            return activity.getIntent()
                    .getStringExtra(HljTrackedActivity.FRAGMENT_PAGE_EXTRA);
        }
        return null;
    }

    public static VTMetaData getCurrentPageTrackData(Activity activity) {
        if (activity == null) {
            return null;
        }
        VTMetaData vtMetaData = null;
        if (activity instanceof HljTrackedActivity) {
            vtMetaData = ((HljTrackedActivity) activity).pageTrackData2();
        }
        if (vtMetaData == null) {
            if (activity instanceof TrackedActivityInterface) {
                vtMetaData = ((TrackedActivityInterface) activity).pageTrackData();
            }
        }
        return vtMetaData;
    }

    public static VTMetaData getActivityPageTrackData(Activity activity) {
        if (activity == null) {
            return null;
        }
        VTMetaData vtMetaData = null;
        if (activity instanceof HljTrackedActivity) {
            vtMetaData = ((HljTrackedActivity) activity).pageTrackData2();
        }
        if (vtMetaData == null) {
            if (activity instanceof TrackedActivityInterface) {
                vtMetaData = ((TrackedActivityInterface) activity).pageTrackData();
            }
        }
        return vtMetaData;
    }
}
