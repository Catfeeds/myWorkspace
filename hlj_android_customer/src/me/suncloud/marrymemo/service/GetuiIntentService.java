package me.suncloud.marrymemo.service;

import android.app.Activity;
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
import com.hunliji.hljcommonlibrary.utils.EmptySubscriber;
import com.hunliji.hljcommonlibrary.utils.SystemNotificationUtil;
import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.PushConsts;
import com.igexin.sdk.message.FeedbackCmdMessage;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTTransmitMessage;
import com.igexin.sdk.message.SetTagCmdMessage;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.HljActivityLifeCycleImpl;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.CustomCommonApi;
import me.suncloud.marrymemo.model.Notify;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.NotificationUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.view.MainActivity;
import me.suncloud.marrymemo.view.notification.CommunityNotificationActivity;
import me.suncloud.marrymemo.view.notification.EventNotificationActivity;
import me.suncloud.marrymemo.view.notification.MessageHomeActivity;
import me.suncloud.marrymemo.view.notification.OrderNotificationActivity;

/**
 * Created by wangtao on 2016/12/23.
 */

public class GetuiIntentService extends GTIntentService {


    public static Notify lastNotify;

    private static int lastNoticeId = 100;

    private static final String TAG = "GetuiSdk";

    public GetuiIntentService() {

    }

    @Override
    public void onReceiveServicePid(Context context, int pid) {
        Log.d(TAG, "onReceiveServicePid -> " + pid);
    }

    @Override
    public void onReceiveClientId(Context context, String clientid) {
        if (CommonUtil.isEmpty(clientid)) {
            return;
        }
        Log.d(TAG, "onReceiveServicePid -> " + clientid);
        context.getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE)
                .edit()
                .putString("clientid", clientid)
                .apply();
        CustomCommonApi.saveClientInfo(context, clientid)
                .subscribe(new EmptySubscriber());
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
                JSONObject object = new JSONObject(data);

                SystemNotificationData notificationData = null;


                // 直接到个推网页发送的推送信息格式是"msg"
                if (!object.isNull("msg")) {
                    int notifyId = ++lastNoticeId;
                    object = object.optJSONObject("msg");
                    lastNotify = new Notify(notifyId, object, taskid, messageid);
                    String title = object.optString("title", "婚礼纪");
                    String content = JSONUtil.getString(object, "content");

                    Intent i = new Intent(context, MainActivity.class);
                    i.putExtra(SystemNotificationUtil.ASG_MSG, object.toString());
                    i.putExtra(SystemNotificationUtil.ASG_TASK_ID, taskid);
                    i.putExtra(SystemNotificationUtil.ASG_MESSAGE_ID, messageid);

                    notificationData = new SystemNotificationData(notifyId,
                            title,
                            content,
                            NotificationCompat.PRIORITY_HIGH,
                            R.drawable.icon_notification_small_icon,
                            R.drawable.icon_app_icon_48_48,
                            i);

                } else if (!object.isNull("message") && !object.isNull("index")) {
                    // 婚礼纪服务器推送的消息的格式是"message"
                    final User user = Session.getInstance()
                            .getCurrentUser(context);
                    if (user == null || user.getId() == 0) {
                        return;
                    }
                    final Activity activity = HljActivityLifeCycleImpl.getInstance()
                            .getCurrentActivity();
                    if (activity!=null&&!(activity instanceof MessageHomeActivity || activity instanceof
                            OrderNotificationActivity || activity instanceof
                            CommunityNotificationActivity || activity instanceof
                            EventNotificationActivity)) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                NotificationUtil.getInstance(context)
                                        .getNewNotifications(user.getId());
                            }
                        });
                    }

                    int index = object.optInt("index", 0);
                    String title = object.optString("title", "婚礼纪");
                    String content = JSONUtil.getString(object, "message");
                    int notifyId = index;

                    Intent i = new Intent(context, MainActivity.class);
                    i.putExtra(SystemNotificationUtil.ASG_INDEX, index);
                    i.putExtra(SystemNotificationUtil.ASG_TASK_ID, taskid);
                    i.putExtra(SystemNotificationUtil.ASG_MESSAGE_ID, messageid);

                    if (index == SystemNotificationUtil.WS_CHAT_INDEX) {
                        try {
                            long userId = object.optJSONObject("ext")
                                    .optLong("id");
                            if (!CommonUtil.isCollectionEmpty(WSRealmHelper.chatIds) &&
                                    WSRealmHelper.chatIds.contains(
                                    userId)) {
                                //当前聊天用户不显示通知
                                return;
                            }
                            String nick = object.optJSONObject("ext")
                                    .optString("nick");
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
                            NotificationCompat.PRIORITY_HIGH,
                            R.drawable.icon_notification_small_icon,
                            R.drawable.icon_app_icon_48_48,
                            i);
                }

                SystemNotificationUtil.INSTANCE.addNotification(context, notificationData);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    public Notification buildNotification(
            Context context,
            int notifyId,
            Intent intent,
            String ticker,
            String title,
            String content) {
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                notifyId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentIntent(
                pendingIntent)
                .setSmallIcon(R.drawable.icon_app_icon_48_48)
                .setTicker(ticker)
                .setContentTitle(title)
                .setContentText(content)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setVisibility(Notification.VISIBILITY_PUBLIC);
        }
        return builder.build();
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

    public static void readNotify(String taskId) {
        if (lastNotify != null && (TextUtils.isEmpty(lastNotify.getTaskid()) || taskId.equals(
                lastNotify.getTaskid()))) {
            lastNotify = null;
        }
    }
}
