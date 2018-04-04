package com.hunliji.cardmaster.service;

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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hunliji.cardmaster.HljCardMasterLiveCycleImpl;
import com.hunliji.cardmaster.R;
import com.hunliji.cardmaster.activities.MainActivity;
import com.hunliji.cardmaster.api.login.LoginApi;
import com.hunliji.cardmaster.models.PushData;
import com.hunliji.cardmaster.utils.NotificationUtil;
import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljcommonlibrary.utils.EmptySubscriber;
import com.hunliji.hljcommonlibrary.utils.SPUtils;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.hljhttplibrary.utils.GsonUtil;
import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.PushConsts;
import com.igexin.sdk.message.FeedbackCmdMessage;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTTransmitMessage;
import com.igexin.sdk.message.SetTagCmdMessage;

import org.joda.time.DateTime;

/**
 * Created by wangtao on 2016/12/23.
 */

public class GetuiIntentService extends GTIntentService {

    public static final String ARG_PUSH_DATA = "push_data";


    private static final String TAG = "GetuiSdk";

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
        Log.d(TAG, "onReceiveServicePid -> " + clientid);
        SPUtils.put(context, "clientid", clientid);
        LoginApi.saveClientInfo(context, clientid)
                .subscribe(new EmptySubscriber());
    }

    @Override
    public void onReceiveMessageData(final Context context, GTTransmitMessage msg) {
        String appid = msg.getAppid();
        String taskid = msg.getTaskId();
        String messageid = msg.getMessageId();
        byte[] payload = msg.getPayload();
        String pkg = msg.getPkgName();
        String cid = msg.getClientId();

        Log.d(TAG,
                "onReceiveMessageData -> " + "appid = " + appid + "\ntaskid = " + taskid +
                        "\nmessageid = " + messageid + "\npkg = " + pkg + "\ncid = " + cid);

        final User user = UserSession.getInstance()
                .getUser(context);
        if (user == null || user.getId() == 0) {
            return;
        }

        if (payload != null) {
            String data = new String(payload);
            try {
                JsonObject jsonObject = new JsonParser().parse(data)
                        .getAsJsonObject();
                PushData pushData;
                if (jsonObject.has("msg") && !jsonObject.get("msg")
                        .isJsonNull()) {
                    pushData = GsonUtil.getGsonInstance()
                            .fromJson(jsonObject.get("msg")
                                    .getAsJsonObject(), PushData.class);
                } else {
                    pushData = GsonUtil.getGsonInstance()
                            .fromJson(jsonObject, PushData.class);
                    if (pushData.getIndex() <= 0) {
                        return;
                    }
                }
                pushData.setMessageId(messageid);
                pushData.setTaskId(taskid);
                String title = pushData.getTitle();
                if (TextUtils.isEmpty(title)) {
                    title = context.getString(R.string.app_name);
                }
                String content = pushData.getContent();

                NotificationManager manager = (NotificationManager) context.getSystemService
                        (Context.NOTIFICATION_SERVICE);
                if (manager == null) {
                    return;
                }
                Intent i = new Intent(context, MainActivity.class);
                i.putExtra(ARG_PUSH_DATA, pushData);
                PendingIntent pendingIntent = PendingIntent.getActivity(context,
                        0,
                        i,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                Notification notification;
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                        .setContentIntent(
                        pendingIntent)
                        .setSmallIcon(R.mipmap.icon_app_icon_48_48)
                        .setTicker(content)
                        .setContentTitle(title)
                        .setContentText(content)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                        .setPriority(NotificationCompat.PRIORITY_HIGH);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder.setVisibility(Notification.VISIBILITY_PUBLIC);
                }
                notification = builder.build();
                notification.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE
                        | Notification.DEFAULT_LIGHTS;
                notification.flags |= Notification.FLAG_AUTO_CANCEL;
                manager.notify(new DateTime().getSecondOfDay(), notification);


                if (pushData.getType() == PushData.INDEX && pushData.getIndex() == 6) {
                    final Activity activity = HljCardMasterLiveCycleImpl.getInstance()
                            .getCurrentActivity();
                    if (activity != null) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                NotificationUtil.INSTANCE.getNewNotifications(context,
                                        user.getId());
                            }
                        });
                    }
                }
            } catch (Exception e) {
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
