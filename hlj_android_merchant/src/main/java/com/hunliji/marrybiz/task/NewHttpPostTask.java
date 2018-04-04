/**
 *
 */
package com.hunliji.marrybiz.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.entity.ProgressListener;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.widget.RoundProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

public class NewHttpPostTask extends AsyncTask<Object, Integer, JSONObject>{
    private Context context;
    private OnHttpRequestListener requestListener;
    private RoundProgressDialog roundProgressDialog;
    private boolean setString;

    public NewHttpPostTask(Context context, OnHttpRequestListener requestListener) {
        this.context = context;
        this.requestListener = requestListener;
    }

    public NewHttpPostTask(Context context, OnHttpRequestListener requestListener,
                           RoundProgressDialog progressDialog) {
        this.context = context;
        this.requestListener = requestListener;
        this.roundProgressDialog = progressDialog;
    }

    public NewHttpPostTask(Context context, OnHttpRequestListener requestListener,
                           RoundProgressDialog progressDialog, boolean setString) {
        this.context = context;
        this.requestListener = requestListener;
        this.roundProgressDialog = progressDialog;
        this.setString = setString;
    }


    @SuppressWarnings("unchecked")
    @Override
    protected JSONObject doInBackground(Object... params) {
        String url = (String) params[0];
        Map<String, Object> map = null;
        Map<String, String> headers = null;
        String objectString = null;
        if (params.length > 1) {
            if (params[1] instanceof Map) {
                map = (Map<String, Object>) params[1];
            } else {
                objectString = (String) params[1];
            }
        }
        if (params.length > 2) {
            headers = (Map<String, String>) params[2];
        }
        if (!JSONUtil.isNetworkConnected(context)) {
            return null;
        }
        try {
            String jsonStr;
            if ((map == null || map.isEmpty()) && JSONUtil.isEmpty(objectString)) {
                jsonStr = JSONUtil.post(context, url, headers);
            } else {
                ProgressListener progressListener = new ProgressListener() {
                    @Override
                    public void transferred(int transferedBytes) {
                        publishProgress(transferedBytes);
                    }

                    @Override
                    public void setContentLength(long contentLength) {
                        if (roundProgressDialog != null) {
                            roundProgressDialog.setMax((int) contentLength);
                        }
                    }
                };

                if (map == null || map.isEmpty()) {
                    jsonStr = JSONUtil.postJsonWithAttach(context, url, objectString, progressListener,
                            headers);
                } else {
                    jsonStr = JSONUtil.postFormWithAttach(context, url, map, progressListener, headers);
                }
            }
            JSONObject json;
            try {
                json = new JSONObject(jsonStr);
                return json;
            } catch (JSONException e) {
                Log.e("HttpPostTask", jsonStr);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        if (roundProgressDialog != null) {
            if (!setString) {
                roundProgressDialog.setMessage(context.getString(R.string.msg_submitting));
            }
        }
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(JSONObject obj) {
        if (obj != null) {
            requestListener.onRequestCompleted(obj);
        } else {
            requestListener.onRequestFailed(obj);
        }
        super.onPostExecute(obj);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        if (roundProgressDialog != null) {
            roundProgressDialog.setProgress(values[0]);
        }
        super.onProgressUpdate(values);
    }

}
