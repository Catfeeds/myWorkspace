/**
 *
 */
package me.suncloud.marrymemo.task;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import me.suncloud.marrymemo.model.ReturnStatus;
import me.suncloud.marrymemo.util.JSONUtil;

/**
 * @author iDay
 */
public class StatusHttpDeleteTask extends AsyncTask<String, Integer, JSONObject> {
    private Context context;
    private StatusRequestListener requestListener;

    /**
     * @param context
     * @param requestListener
     */
    public StatusHttpDeleteTask(Context context, StatusRequestListener requestListener) {
        super();
        this.context = context;
        this.requestListener = requestListener;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected JSONObject doInBackground(String... params) {
        String url = params[0];
        try {
            String jsonStr = JSONUtil.delete(url);
            JSONObject json;
            if (!JSONUtil.isEmpty(jsonStr)) {
                json = new JSONObject(jsonStr);
            } else {
                json = new JSONObject();
            }
            return json;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
     */
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
