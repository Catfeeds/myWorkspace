/**
 *
 */
package me.suncloud.marrymemo.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.entity.ProgressListener;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.widget.RoundProgressDialog;

/**
 * @author iDay
 */
public class HttpPutTask extends AsyncTask<Object, Long, JSONObject> {
    private Context context;
    private OnHttpRequestListener requestListener;
    private RoundProgressDialog progressDialog;
    private boolean b;

    /**
     * @param context
     * @param onProgressListener
     */
    public HttpPutTask(Context context, OnHttpRequestListener requestListener) {
        super();
        this.context = context;
        this.requestListener = requestListener;
    }

    /**
     * @param context
     * @param requestListener
     * @param progressDialog
     */
    public HttpPutTask(Context context, OnHttpRequestListener requestListener,
                       RoundProgressDialog progressDialog) {
        super();
        this.context = context;
        this.requestListener = requestListener;
        this.progressDialog = progressDialog;
    }

    public HttpPutTask(Context context, OnHttpRequestListener requestListener,
                       RoundProgressDialog progressDialog, boolean b) {
        super();
        this.context = context;
        this.requestListener = requestListener;
        this.progressDialog = progressDialog;
        this.b = b;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected JSONObject doInBackground(Object... params) {
        String url = (String) params[0];
        Map<String, Object> map = null;
        String objectString = null;
        String type = null;
        if (params.length > 1) {
            if (params[1] instanceof Map) {
                map = (Map<String, Object>) params[1];
            } else if (params[1] instanceof String) {
                objectString = (String) params[1];
            }
        }
        if (params.length > 2) {
            type = (String) params[2];
        }
        try {
            String jsonStr;
            if ((map == null || map.isEmpty()) && JSONUtil.isEmpty(objectString)) {
                jsonStr = JSONUtil.put(url);
            } else {
                ProgressListener progressListener = new ProgressListener() {
                    @Override
                    public void transferred(long transferedBytes) {

                    }

                    @Override
                    public void setContentLength(long contentLength) {
                        if (progressDialog != null) {
                            progressDialog.setMax((int) contentLength);
                        }
                    }
                };
                if (!JSONUtil.isEmpty(objectString)) {
                    jsonStr = JSONUtil.putJsonWithAttach(url, objectString,
                            progressListener);
                } else if ("text".equals(type)) {
                    jsonStr = JSONUtil.putFormWithAttach(url, map, progressListener);
                } else {
                    jsonStr = JSONUtil.putMultipartWithAttach(url, map, progressListener, this.context.getContentResolver());
                }
            }
            JSONObject json;
            try {
                json = new JSONObject(jsonStr);
                return json;
            } catch (JSONException e) {
                Log.e("HttpPuTask", jsonStr);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        if (progressDialog != null && !b) {
            progressDialog.setMessage(context.getString(R.string.msg_submitting));
        }
        super.onPreExecute();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
     */
    @Override
    protected void onPostExecute(JSONObject obj) {
        if (requestListener != null) {
            if (obj != null) {
                requestListener.onRequestCompleted(obj);
            } else {
                requestListener.onRequestFailed(obj);
            }
        }
        super.onPostExecute(obj);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.os.AsyncTask#onProgressUpdate(Progress[])
     */
    @Override
    protected void onProgressUpdate(Long... values) {
        if (progressDialog != null) {
            progressDialog.setProgress(values[0]);
        }
        super.onProgressUpdate(values);
    }

}
