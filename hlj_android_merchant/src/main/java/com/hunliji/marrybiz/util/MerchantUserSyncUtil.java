package com.hunliji.marrybiz.util;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.api.merchant.MerchantApi;
import com.hunliji.marrybiz.model.MerchantUser;
import com.hunliji.marrybiz.view.PreLoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;

import rx.Subscriber;
import rx.Subscription;

/**
 * Created by werther on 16/9/13.
 * 商家用户信息同步Util,任何时候需要同步用户信息都可以调用,同步完成之后将会发送RxBus更新事件通知给任何监听用户信息的地方
 * <p>
 * Add on 16/10/9
 * 检查是否绑定
 */
public class MerchantUserSyncUtil {
    private static MerchantUserSyncUtil INSTANCE;
    private WeakReference<Context> contextWeakReference;
    private OnMerchantUserSyncListener onMerchantUserSyncListener;
    private OnMerchantUserSyncListener onPendingCheckSyncListener;

    private GetMerchantInfoTask getMerchantInfoTask;
    private GetPendingMerchantInfoTask getPendingMerchantInfoTask;

    private boolean isBind;

    private Subscription checkBindSubscription;

    private MerchantUserSyncUtil() {
    }

    private Context getContext() {
        if (contextWeakReference != null) {
            return contextWeakReference.get();
        }
        return null;
    }

    public static MerchantUserSyncUtil getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MerchantUserSyncUtil();
        }
        return INSTANCE;
    }

    public void sync(Context context, OnMerchantUserSyncListener listener) {
        contextWeakReference = new WeakReference<>(context);
        this.onMerchantUserSyncListener = listener;
        if (getMerchantInfoTask == null) {
            getMerchantInfoTask = new GetMerchantInfoTask();
            getMerchantInfoTask.executeOnExecutor(Constants.INFOTHEADPOOL);
        }
    }

    public void pendingCheckSync(Context context, OnMerchantUserSyncListener listener) {
        this.onPendingCheckSyncListener = listener;
        if (getPendingMerchantInfoTask == null) {
            new GetPendingMerchantInfoTask().executeOnExecutor(Constants.INFOTHEADPOOL,
                    Constants.getAbsUrl(Constants.HttpPath.POST_MERCHANTS_INFO) +
                            "?is_pending=true");
        }
    }

    private class GetMerchantInfoTask extends AsyncTask<String, Integer, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            if (getContext() == null) {
                return null;
            }
            String url = Constants.getAbsUrl(Constants.HttpPath.POST_MERCHANTS_INFO);
            try {
                String json = JSONUtil.getStringFromUrl(getContext(), url);
                if (JSONUtil.isEmpty(json)) {
                    return null;
                }
                return new JSONObject(json);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (jsonObject != null && getContext() != null) {
                com.hunliji.marrybiz.model.Status status = new com.hunliji.marrybiz.model.Status(
                        jsonObject.optJSONObject("status"));
                if (status.getRetCode() == 291) {
                    Intent intent = new Intent(getContext(), PreLoginActivity.class);
                    intent.putExtra("logout", true);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent
                            .FLAG_ACTIVITY_NEW_TASK);
                    getContext().startActivity(intent);
                    return;
                }
                JSONObject dataJson = jsonObject.optJSONObject("data");
                if (dataJson != null) {
                    Session.getInstance()
                            .setCurrentUser(getContext(), dataJson);
                    MerchantUser user = Session.getInstance()
                            .getCurrentUser(getContext());
                    // 发送RxBus事件
                    RxBus.getDefault()
                            .post(user);
                    // 回调
                    if (onMerchantUserSyncListener != null) {
                        onMerchantUserSyncListener.onUserSyncFinish(user);
                    }
                }
            }
            getMerchantInfoTask = null;
            super.onPostExecute(jsonObject);
        }
    }


    private class GetPendingMerchantInfoTask extends AsyncTask<String, Integer, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            if (getContext() == null) {
                return null;
            }
            String url = params[0];
            try {
                String json = JSONUtil.getStringFromUrl(getContext(), url);
                if (JSONUtil.isEmpty(json)) {
                    return null;
                }
                return new JSONObject(json);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (jsonObject != null) {
                int statusCode = jsonObject.optJSONObject("status")
                        .optInt("RetCode");
                if (statusCode == 0) {
                    JSONObject merchantJSONObject = jsonObject.optJSONObject("data");
                    MerchantUser pendingUser = new MerchantUser(merchantJSONObject);
                    if (onPendingCheckSyncListener != null) {
                        onPendingCheckSyncListener.onUserSyncFinish(pendingUser);
                    }
                }
            }
            getPendingMerchantInfoTask = null;
            super.onPostExecute(jsonObject);
        }
    }

    public interface OnMerchantUserSyncListener {
        void onUserSyncFinish(MerchantUser user);
    }

    public void onBindWechatCheck() {
        if (checkBindSubscription != null && !checkBindSubscription.isUnsubscribed()) {
            return;
        }
        checkBindSubscription = MerchantApi.checkedBindWechatObb()
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean ^ isBind) {
                            isBind = aBoolean;
                            // 发送RxBus事件
                            RxBus.getDefault()
                                    .post(new RxEvent(RxEvent.RxEventType.WECHAT_BIND_CHANGE,
                                            isBind));
                        }
                    }
                });
    }

    public boolean isBind() {
        return isBind;
    }

    public void setBind(boolean bind) {
        isBind = bind;
    }
}
