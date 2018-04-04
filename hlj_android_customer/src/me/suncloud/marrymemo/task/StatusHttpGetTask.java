package me.suncloud.marrymemo.task;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import me.suncloud.marrymemo.model.ReturnStatus;
import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by Suncloud on 2016/4/15.
 */
public class StatusHttpGetTask extends AsyncTask<String, Object, JSONObject> {

    private Context context;
    private StatusRequestListener requestListener;

    public StatusHttpGetTask(Context context, StatusRequestListener requestListener) {
        this.context=context;
        this.requestListener = requestListener;
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        String url = params[0];
        try {
            String jsonStr;
            jsonStr = JSONUtil.getStringFromUrl(url);
            if (!JSONUtil.isEmpty(jsonStr)) {
                return new JSONObject(jsonStr);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        if (requestListener != null) {
            ReturnStatus returnStatus = null;
            if (jsonObject != null && !jsonObject.isNull("status")) {
                returnStatus = new ReturnStatus(jsonObject.optJSONObject("status"));
            }
            if (returnStatus != null && returnStatus.getRetCode() == 0) {
                requestListener.onRequestCompleted(jsonObject.opt("data"), returnStatus);
            } else {
                requestListener.onRequestFailed(returnStatus, JSONUtil.isNetworkConnected(context));
            }
        }
        super.onPostExecute(jsonObject);
    }
}
