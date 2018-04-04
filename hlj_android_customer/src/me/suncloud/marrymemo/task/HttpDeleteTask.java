/**
 *
 */
package me.suncloud.marrymemo.task;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import me.suncloud.marrymemo.util.JSONUtil;

/**
 * @author iDay
 */
public class HttpDeleteTask extends AsyncTask<Object, Integer, JSONObject> {
    private Context context;
    private OnHttpRequestListener requestListener;

    /**
     * @param context
     * @param onProgressListener
     */
    public HttpDeleteTask(Context context, OnHttpRequestListener requestListener) {
        super();
        this.context = context;
        this.requestListener = requestListener;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected JSONObject doInBackground(Object... params) {
        String url = (String) params[0];
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
    protected void onPostExecute(JSONObject result) {
        if(requestListener!=null) {
            if (result != null) {
                requestListener.onRequestCompleted(result);
            } else {
                requestListener.onRequestFailed(null);
            }
        }
        super.onPostExecute(result);
    }

}
