package me.suncloud.marrymemo.view.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljtrackerlibrary.HljTracker;

import org.json.JSONObject;

import de.greenrobot.event.EventBus;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.CustomCommonApi;
import me.suncloud.marrymemo.model.MessageEvent;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.model.login.LoginMessage;
import me.suncloud.marrymemo.task.OnHttpRequestListener;
import me.suncloud.marrymemo.task.UserTask;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.view.MainActivity;
import rx.Subscriber;

/**
 * Created by jinxin on 2016/3/15.
 * 已废弃
 */
@Deprecated
public class BaseLoginActivity extends HljBaseActivity {
    protected Handler handler;
    protected int type;//注册还是LoginCheck
    protected View progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSwipeBackEnable(false);
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == 1) {
                    //账密登录
                    handDone(null);
                } else if (msg.what == 2) {
                    if (progress != null) {
                        progress.setVisibility(View.VISIBLE);
                    }
                } else if (msg.what == 3) {
                    //不知道有什么用  可以去掉?
                    onBackPressed();
                } else if (msg.what == 4) {
                    //第三方登陆
                    if (msg.obj != null) {
                        LoginMessage message = (LoginMessage) msg.obj;
                        saveUserMessage(null, message);
                    }
                } else {
                    if (progress != null) {
                        progress.setVisibility(View.GONE);
                    }
                    String errStr = null;
                    if (msg.obj != null && msg.obj instanceof String) {
                        errStr = (String) msg.obj;
                    }
                    ToastUtil.showToast(BaseLoginActivity.this, errStr, R.string.msg_login_error);
                }
                return false;
            }
        });
    }

    private void handDone(final LoginMessage message) {
        if (progress != null) {
            progress.setVisibility(View.VISIBLE);
        }
        new UserTask(BaseLoginActivity.this, new OnHttpRequestListener() {
            @Override
            public void onRequestCompleted(Object obj) {
                User user = Session.getInstance()
                        .getCurrentUser(BaseLoginActivity.this);
                Session.getInstance()
                        .refreshCartItemsCount(BaseLoginActivity.this);
                saveUserMessage(user, message);
            }

            @Override
            public void onRequestFailed(Object obj) {
                if (progress != null) {
                    progress.setVisibility(View.GONE);
                }
                Toast.makeText(BaseLoginActivity.this,
                        getString(R.string.msg_login_error),
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }).execute();
    }

    /**
     * @param user
     * @param message
     */
    protected void saveUserMessage(User user, LoginMessage message) {
        if (message != null) {
            //第三方登录的时候 message != null 其余的时候都是null
            if (progress != null) {
                progress.setVisibility(View.GONE);
            }
            int retCode = message.getRetCode();
            if (retCode == 0 && message.getUser() != null) {
                //三方登录成功
                saveUserMessage(message.getUser(), message.getType());
                User thirdUser = new User(message.getUser());
                Toast.makeText(this, getString(R.string.msg_login_success), Toast.LENGTH_SHORT)
                        .show();
                switchActivity(thirdUser);
            }
            return;
        }

        //验证码 账密登录
        if (user == null || user.getId() <= 0) {
            return;
        }
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREF_FILE,
                Context.MODE_PRIVATE);
        String cid = sharedPreferences.getString("clientid", "null");
        CustomCommonApi.saveClientInfo(this, cid)
                .subscribe(new Subscriber() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Object o) {

                    }
                });
        switchActivity(user);
    }

    protected void saveUserMessage(JSONObject user, String type) {
        User u = new User(user);
        Session.getInstance()
                .setCurrentUser(BaseLoginActivity.this, user);
        new HljTracker.Builder(this).eventableId(u.getId())
                .eventableType("User")
                .screen("login")
                .action("login")
                .additional(type)
                .build()
                .add();
    }

    protected void switchActivity(User user) {
        //跳转判断  手机号 设置婚期 选择类别
        if (user.getWeddingDay() == null && user.getIsPending() != 1) {
            //设置婚期
            Intent intent = getIntent();
            if (intent == null) {
                intent = new Intent(this, WeddingDateSetActivity.class);
            } else {
                intent.setClass(this, WeddingDateSetActivity.class);
            }
            intent.putExtra("type", type);
            startActivity(intent);
        } else {
            if (type == Constants.Login.LOGINCHECK) {
                sendLoginMessageEvent();
            } else {
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
            }
        }
    }

    private void sendLoginMessageEvent() {
        MessageEvent event = new MessageEvent(MessageEvent.EventType.LOGINCHECK, null);
        EventBus.getDefault()
                .post(event);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
