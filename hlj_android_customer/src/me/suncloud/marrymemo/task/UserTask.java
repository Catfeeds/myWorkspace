package me.suncloud.marrymemo.task;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.hunliji.hljcommonlibrary.rxbus.RxBus;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.Session;

/**
 * Created by Suncloud on 2015/8/1.
 * 更新当前用户信息,更新成功后会发送一个User类型的RxBus更新事件
 * 也可以通过注册回调在更新成功后在回调中更新信息
 */
public class UserTask extends AsyncTask<String, Integer, JSONObject> {
    private Context context;
    private OnHttpRequestListener requestListener;
    private long userId;

    public UserTask(Context context, @Nullable OnHttpRequestListener requestListener) {
        if (Session.getInstance()
                .getCurrentUser(context) != null) {
            this.userId = Session.getInstance()
                    .getCurrentUser(context)
                    .getId();
        } else {
            this.userId = 0;
        }
        this.context = context;
        this.requestListener = requestListener;
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        try {
            String jsonStr = JSONUtil.getStringFromUrl(Constants.getAbsUrl(Constants.HttpPath
                    .GET_USER_INFO));
            if (JSONUtil.isEmpty(jsonStr)) {
                return null;
            }
            return new JSONObject(jsonStr);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        User user = Session.getInstance()
                .getCurrentUser(context);
        if (user == null || userId == 0 || (userId > 0 && user.getId() != userId)) {
            if (requestListener != null) {
                requestListener.onRequestFailed(null);
            }
            return;
        }
        if (result != null) {
            Session.getInstance()
                    .setCurrentUser(context, result);
            if (requestListener != null) {
                requestListener.onRequestCompleted(null);
            }
            user = Session.getInstance()
                    .getCurrentUser(context);
            // RxBus发送UserInfo事件,让所有监听都进行更新
            RxBus.getDefault()
                    .post(user);
        } else {
            if (requestListener != null) {
                requestListener.onRequestFailed(null);
            }
        }
        super.onPostExecute(result);
    }

}