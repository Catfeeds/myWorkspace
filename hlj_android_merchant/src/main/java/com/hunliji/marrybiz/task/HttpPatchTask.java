package com.hunliji.marrybiz.task;

import java.io.IOException;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.entity.ProgressListener;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.widget.RoundProgressDialog;

public class HttpPatchTask extends AsyncTask<Object, Integer, JSONObject> {
    private Context context;
    private OnHttpRequestListener requestListener;
    private RoundProgressDialog progressDialog;
    private boolean b;

    public HttpPatchTask(Context context, OnHttpRequestListener requestListener) {
        this.context = context;
        this.requestListener = requestListener;
    }

    public HttpPatchTask(Context context, OnHttpRequestListener requestListener,
                         RoundProgressDialog progressDialog) {
        this.context = context;
        this.requestListener = requestListener;
        this.progressDialog = progressDialog;
    }

    public HttpPatchTask(Context context, OnHttpRequestListener requestListener,
                         RoundProgressDialog progressDialog, boolean b) {
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
        try {
            String jsonStr;
            if ((map == null || map.isEmpty()) && JSONUtil.isEmpty(objectString)) {
                jsonStr = JSONUtil.patch(context, url, headers);
            } else {
                ProgressListener progressListener = new ProgressListener() {
                    @Override
                    public void transferred(int transferedBytes) {
                        publishProgress(transferedBytes);
                    }

                    @Override
                    public void setContentLength(long contentLength) {
                        if (progressDialog != null) {
                            progressDialog.setMax((int) contentLength);
                        }
                    }
                };
                if (map == null || map.isEmpty()) {
                    jsonStr = JSONUtil.patchJsonWithAttach(context, url, objectString, progressListener,
                            headers);
                } else {
                    jsonStr = JSONUtil.patchFormWithAttach(context, url, map, progressListener, headers);
                }
            }
            JSONObject json;
            try {
                json = new JSONObject(jsonStr);
                return json;
            } catch (JSONException e) {
                Log.e("HttpPatchTask", jsonStr);
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

    @Override
    protected void onPostExecute(JSONObject obj) {
        super.onPostExecute(obj);

        if (obj != null) {
            requestListener.onRequestCompleted(obj);
        } else {
            requestListener.onRequestFailed(obj);
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        if (progressDialog != null) {
            progressDialog.setProgress(values[0]);
        }
        super.onProgressUpdate(values);
    }

}
