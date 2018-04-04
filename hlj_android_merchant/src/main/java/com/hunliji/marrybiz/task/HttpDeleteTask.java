/**
 *
 */
package com.hunliji.marrybiz.task;

import android.content.Context;
import android.os.AsyncTask;

import com.hunliji.marrybiz.util.JSONUtil;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * @author iDay
 */
public class HttpDeleteTask extends AsyncTask<Object, Integer, JSONObject> {
    private OnHttpRequestListener requestListener;
    private Context context;

    /**
     * @param requestListener
     */
    public HttpDeleteTask(Context c, OnHttpRequestListener requestListener) {
        this.requestListener = requestListener;
        this.context = c;
    }

    @Override
    protected JSONObject doInBackground(Object... params) {
        String url = (String) params[0];
        try {
            String jsonStr = JSONUtil.delete(context, url,null);
            JSONObject json = null;
            if (!JSONUtil.isEmpty(jsonStr)) {
                json = new JSONObject(jsonStr);
            } else {
                json = new JSONObject();
            }
            return json;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
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
        super.onPostExecute(result);

        if (result != null) {
            requestListener.onRequestCompleted(result);
        } else {
            requestListener.onRequestFailed(null);
        }
    }

}
