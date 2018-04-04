package com.hunliji.marrybiz.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.hunliji.hljchatlibrary.WSRealmHelper;
import com.hunliji.hljcommonlibrary.models.SystemNotificationData;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.SystemNotificationUtil;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.HLJMerchantApplication;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.MerchantUser;
import com.hunliji.marrybiz.task.HttpPostTask;
import com.hunliji.marrybiz.task.OnHttpRequestListener;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.NotificationUtil;
import com.hunliji.marrybiz.util.Session;
import com.hunliji.marrybiz.view.HomeActivity;
import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;
import com.igexin.sdk.Tag;
import com.igexin.sdk.message.FeedbackCmdMessage;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTTransmitMessage;
import com.igexin.sdk.message.SetTagCmdMessage;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangtao on 2016/12/23.
 */

public class GetuiIntentService extends GTIntentService {

    private static final String TAG = "GetuiSdk";

    private static int lastNoticeId = 100;

    public GetuiIntentService() {

    }

    @Override
    public void onReceiveServicePid(Context context, int pid) {
        Log.d(TAG, "onReceiveServicePid -> " + pid);
    }

    @Override
    public void onReceiveClientId(Context context, String clientid) {
        if (TextUtils.isEmpty(clientid)) {
            return;
        }
        context.getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE)
                .edit()
                .putString("clientid", clientid)
                .apply();
        MerchantUser user = Session.getInstance()
                .getCurrentUser(context);
        if (user != null && user.getId()
                .intValue() != 0) {
            Tag t = new Tag();
            t.setName("商家类别" + user.getPropertyId());
            Tag t2 = new Tag();
            t2.setName("商家类型" + user.getShopType());
            Tag[] tags = {t, t2};
            PushManager.getInstance()
                    .setTag(context, tags, System.currentTimeMillis() + "");
            Map<String, Object> data = new HashMap<String, Object>();
            com.hunliji.marrybiz.util.DeviceUuidFactory deviceUuidFactory = new com.hunliji
                    .marrybiz.util.DeviceUuidFactory(
                    context);
            data.put("info[cid]", clientid);
            data.put("info[user_id]", String.valueOf(user.getId()));
            data.put("info[from]", Constants.GE_TUI_FROM);
            data.put("info[phone_token]",
                    deviceUuidFactory.getDeviceUuid()
                            .toString());
            data.put("info[apns_token]", String.valueOf(""));
            data.put("info[app_version]", Constants.APP_VERSION);
            data.put("info[phone_type]", String.valueOf(2));
            data.put("info[device]", android.os.Build.MODEL);
            data.put("info[system]", android.os.Build.VERSION.RELEASE);

            new HttpPostTask(context, new OnHttpRequestListener() {
                @Override
                public void onRequestCompleted(Object obj) {

                }

                @Override
                public void onRequestFailed(Object obj) {

                }
            }).execute(Constants.getAbsUrl(Constants.HttpPath.GEXIN_TOKEN_URL), data);
        }
    }

    @Override
    public void onReceiveMessageData(
            final Context context, GTTransmitMessage msg) {
        String appid = msg.getAppid();
        String taskid = msg.getTaskId();
        String messageid = msg.getMessageId();
        byte[] payload = msg.getPayload();
        String pkg = msg.getPkgName();
        String cid = msg.getClientId();

        Log.d(TAG,
                "onReceiveMessageData -> " + "appid = " + appid + "\ntaskid = " + taskid +
                        "\nmessageid = " + messageid + "\npkg = " + pkg + "\ncid = " + cid);
        if (payload != null) {
            String data = new String(payload);
            Log.d(TAG, "onReceiveMessageData -> " + "payload = " + data);
            try {
                JSONObject jsonObject = new JSONObject(data);

                SystemNotificationData notificationData = null;

                if (!jsonObject.isNull("msg")) {
                    jsonObject = jsonObject.optJSONObject("msg");
                    String title = jsonObject.optString("title", "婚礼纪商家版");
                    String content = JSONUtil.getString(jsonObject, "content");

                    Intent i = new Intent(context, HomeActivity.class);
                    i.putExtra(SystemNotificationUtil.ASG_MSG, jsonObject.toString());
                    i.putExtra(SystemNotificationUtil.ASG_TASK_ID, taskid);
                    i.putExtra(SystemNotificationUtil.ASG_MESSAGE_ID, messageid);

                    notificationData = new SystemNotificationData(++lastNoticeId,
                            title,
                            content,
                            NotificationCompat.PRIORITY_DEFAULT,
                            R.drawable.icon_notification_small_icon,
                            R.drawable.icon_app_icon_96_96,
                            i);
                } else if (!jsonObject.isNull("message") && !jsonObject.isNull("index")) {
                    final MerchantUser user = Session.getInstance()
                            .getCurrentUser(context);
                    // 当前用户数据不正确则不做下面的操作
                    if (user == null || user.getId()
                            .intValue() == 0) {
                        return;
                    }

                    // 如果用户停留在在应用的界面上,则直接开始请求最新的通知
                    HLJMerchantApplication application = (HLJMerchantApplication) context
                            .getApplicationContext();
                    if (application != null && application.isAppOnForeground() && application
                            .getCurrentActivity() != null) {
                        application.getCurrentActivity()
                                .runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        NotificationUtil.getInstance(context)
                                                .getNewNotifications(user.getId());
                                    }
                                });
                    }

                    String title = jsonObject.optString("title", "婚礼纪");
                    String content = JSONUtil.getString(jsonObject, "message");
                    int index = jsonObject.optInt("index", 0);
                    int notifyId = index;

                    Intent i = new Intent(context, HomeActivity.class);
                    i.putExtra(SystemNotificationUtil.ASG_INDEX, index);
                    i.putExtra(SystemNotificationUtil.ASG_TASK_ID, taskid);
                    i.putExtra(SystemNotificationUtil.ASG_MESSAGE_ID, messageid);

                    if (index == SystemNotificationUtil.WS_CHAT_INDEX) {
                        try {
                            long userId = jsonObject.optJSONObject("ext")
                                    .optLong("id");
                            String nick = jsonObject.optJSONObject("ext")
                                    .optString("nick");
                            if (!CommonUtil.isCollectionEmpty(WSRealmHelper.chatIds) &&
                                    WSRealmHelper.chatIds.contains(
                                    userId)) {
                                //当前聊天用户不显示通知
                                return;
                            }
                            i.putExtra(SystemNotificationUtil.ASG_CHAT_USER_ID, userId);
                            title = nick;
                            notifyId = (int) userId;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    i.putExtra(SystemNotificationUtil.ASG_NOTIFY_ID, notifyId);

                    notificationData = new SystemNotificationData(notifyId,
                            title,
                            content,
                            NotificationCompat.PRIORITY_DEFAULT,
                            R.drawable.icon_notification_small_icon,
                            R.drawable.icon_app_icon_96_96,
                            i);
                }

                SystemNotificationUtil.INSTANCE.addNotification(context, notificationData);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onReceiveOnlineState(Context context, boolean online) {
        Log.d(TAG, "onReceiveOnlineState -> " + (online ? "online" : "offline"));
    }

    @Override
    public void onReceiveCommandResult(
            Context context, GTCmdMessage cmdMessage) {
        Log.d(TAG, "onReceiveCommandResult -> " + cmdMessage);

        int action = cmdMessage.getAction();

        if (action == PushConsts.SET_TAG_RESULT) {
            setTagResult((SetTagCmdMessage) cmdMessage);
        } else if ((action == PushConsts.THIRDPART_FEEDBACK)) {
            feedbackResult((FeedbackCmdMessage) cmdMessage);
        }

    }


    private void setTagResult(SetTagCmdMessage setTagCmdMsg) {
        String sn = setTagCmdMsg.getSn();
        String code = setTagCmdMsg.getCode();

        String text = "设置标签失败, 未知异常";
        switch (Integer.valueOf(code)) {
            case PushConsts.SETTAG_SUCCESS:
                text = "设置标签成功";
                break;

            case PushConsts.SETTAG_ERROR_COUNT:
                text = "设置标签失败, tag数量过大, 最大不能超过200个";
                break;

            case PushConsts.SETTAG_ERROR_FREQUENCY:
                text = "设置标签失败, 频率过快, 两次间隔应大于1s且一天只能成功调用一次";
                break;

            case PushConsts.SETTAG_ERROR_REPEAT:
                text = "设置标签失败, 标签重复";
                break;

            case PushConsts.SETTAG_ERROR_UNBIND:
                text = "设置标签失败, 服务未初始化成功";
                break;

            case PushConsts.SETTAG_ERROR_EXCEPTION:
                text = "设置标签失败, 未知异常";
                break;

            case PushConsts.SETTAG_ERROR_NULL:
                text = "设置标签失败, tag 为空";
                break;

            case PushConsts.SETTAG_NOTONLINE:
                text = "还未登陆成功";
                break;

            case PushConsts.SETTAG_IN_BLACKLIST:
                text = "该应用已经在黑名单中,请联系售后支持!";
                break;

            case PushConsts.SETTAG_NUM_EXCEED:
                text = "已存 tag 超过限制";
                break;

            default:
                break;
        }

        Log.d(TAG, "settag result sn = " + sn + ", code = " + code + ", text = " + text);
    }


    private void feedbackResult(FeedbackCmdMessage feedbackCmdMsg) {
        String appid = feedbackCmdMsg.getAppid();
        String taskid = feedbackCmdMsg.getTaskId();
        String actionid = feedbackCmdMsg.getActionId();
        String result = feedbackCmdMsg.getResult();
        long timestamp = feedbackCmdMsg.getTimeStamp();
        String cid = feedbackCmdMsg.getClientId();

        Log.d(TAG,
                "onReceiveCommandResult -> " + "appid = " + appid + "\ntaskid = " + taskid +
                        "\nactionid = " + actionid + "\nresult = " + result + "\ncid = " + cid +
                        "\ntimestamp = " + timestamp);
    }

}
