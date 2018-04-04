/**
 *
 */
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

public class HttpPostTask extends AsyncTask<Object, Integer, JSONObject> {
    private Context context;
    private OnHttpRequestListener requestListener;
    private RoundProgressDialog roundProgressDialog;
    private boolean setString;

    public HttpPostTask(Context context, OnHttpRequestListener requestListener) {
        this.context = context;
        this.requestListener = requestListener;
    }

    public HttpPostTask(Context context, OnHttpRequestListener requestListener,
                        RoundProgressDialog progressDialog) {
        this.context = context;
        this.requestListener = requestListener;
        this.roundProgressDialog = progressDialog;
    }


    public HttpPostTask(Context context, OnHttpRequestListener requestListener,
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
        if (params.length > 1) {
            map = (Map<String, Object>) params[1];
        }
        Map<String, String> headers = null;
        if (params.length > 2) {
            headers = (Map<String, String>) params[2];
        }
        if (!JSONUtil.isNetworkConnected(context)) {
            return null;
        }
        try {
            String jsonStr;
            if (map == null || map.isEmpty()) {
                jsonStr = JSONUtil.post(context, url,headers);
            } else {
                jsonStr = JSONUtil.postFormWithAttach(context, url, map,
                        new ProgressListener() {

                            @Override
                            public void transferred(int transferedBytes) {
                                publishProgress(transferedBytes);
                            }

                            @Override
                            public void setContentLength(long contentLength) {
                                if (roundProgressDialog != null) {
                                    roundProgressDialog.setMax(contentLength);
                                }
                            }
                        },headers);
            }
            JSONObject json;
            try {
                json = new JSONObject(jsonStr);
                return json;
            } catch (JSONException e) {
                Log.e("HttpPostTask", jsonStr);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
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
        super.onPostExecute(obj);

        if (obj != null) {
            requestListener.onRequestCompleted(obj);
        } else {
            requestListener.onRequestFailed(obj);
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        if (roundProgressDialog != null) {
            roundProgressDialog.setProgress(values[0]);
        }
        super.onProgressUpdate(values);
    }

}
